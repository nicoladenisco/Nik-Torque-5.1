package org.apache.torque.generator.control;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.junit.jupiter.api.Test;

/**
 * Tests nested mergepoints in a depth of 10 calls, and checks that the
 * outcome is correct.
 */
public class RunOnlyOnSourceChangeTest extends BaseTest
{
    /** The logger. */
    private static Log log = LogFactory.getLog(Controller.class);

    @Test
    @SuppressWarnings("unchecked")
    public void testRunOnlyOnSourceChange() throws Exception
    {
        File targetDir = new File("target/test/runOnlyOnSourceChange");
        FileUtils.deleteDirectory(targetDir);
        File srcDir1 = new File(targetDir, "src/1");
        File srcDir2 = new File(targetDir, "src/2");
        FileUtils.copyDirectory(new File("src/test/runOnlyOnSourceChange/src/main/torque-gen/src"), srcDir1);
        FileUtils.copyDirectory(new File("src/test/runOnlyOnSourceChange/src/main/torque-gen/src"), srcDir2);

        // Fix TORQUE-351, timestamps of files in src and secondSource must be identical
        Collection<File> srcFiles = FileUtils.listFiles(new File(targetDir, "src"), null, true);
        Map<String, Long> srcFileStamps = new HashMap<>();
        for (File file : srcFiles)
        {
            srcFileStamps.put(file.getAbsolutePath(), Long.valueOf(file.lastModified()));
        }

        Controller controller = new Controller();

        // run first generation with two unit descriptors to reproduce TORQUE-338
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/runOnlyOnSourceChange")));
        File targetDir1 = new File(targetDir, "1");
        projectPaths.setOutputDirectory(null, targetDir1);
        projectPaths.setSourceDir(srcDir1);
        projectPaths.setCacheDir(new File("target/test/runOnlyOnSourceChange/cache"));
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        unitDescriptors.get(0).setRunOnlyOnSourceChange(true);

        projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/runOnlyOnSourceChange")));
        File targetDir2 = new File(targetDir, "2");
        projectPaths.setOutputDirectory(null, targetDir2);
        projectPaths.setSourceDir(srcDir2);
        projectPaths.setCacheDir(new File("target/test/runOnlyOnSourceChange/cache"));
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        unitDescriptors.get(1).setRunOnlyOnSourceChange(true);

        controller.run(unitDescriptors);

        // check outcome of first generation
        assertTrue(targetDir.exists());

        long unchangedTargetFile1LastModified = assertFile(targetDir1, "unchangedOutput.txt", "unchangedValue");
        log.debug("sample modified time:" + unchangedTargetFile1LastModified);
        long changedTargetFile11LastModified = assertFile(targetDir1, "changedOutput1.txt", "valueToBeChanged");
        long changedTargetFile12LastModified = assertFile(targetDir1, "changedOutput2.txt", "valueToBeChanged");
        long unchangedTargetFile2LastModified = assertFile(targetDir2, "unchangedOutput.txt", "unchangedValue");
        long changedTargetFile21LastModified = assertFile(targetDir2, "changedOutput1.txt", "valueToBeChanged");
        long changedTargetFile22LastModified = assertFile(targetDir2, "changedOutput2.txt", "valueToBeChanged");
        Thread.sleep(1000); // allow time for Date checks

        // second run
        log.debug("Start time of generation " + System.currentTimeMillis());
        FileUtils.deleteDirectory(srcDir1);
        FileUtils.deleteDirectory(srcDir2);
        FileUtils.copyDirectory(new File("src/test/runOnlyOnSourceChange/src/main/torque-gen/secondSource"), srcDir1);
        FileUtils.copyDirectory(new File("src/test/runOnlyOnSourceChange/src/main/torque-gen/secondSource"), srcDir2);

        srcFiles = FileUtils.listFiles(new File(targetDir, "src"), null, true);
        for (File file : srcFiles)
        {
            Long ts = srcFileStamps.get(file.getAbsolutePath());
            if (ts != null && ts.longValue() != file.lastModified())
            {
                log.debug("Fixing timestamp of " + file);
                file.setLastModified(ts.longValue());
            }
        }

        controller.run(unitDescriptors);
        log.debug("End time of generation " + System.currentTimeMillis());

        // check outcome of second generation
        // fails in Ubuntu wsl1, Java 11, but not Ubuntu, wsl2, java 14
        long lastModified = assertFile(targetDir1, "unchangedOutput.txt", "unchangedValue");
        assertTrue(unchangedTargetFile1LastModified == lastModified, "sample last modified time: "+ lastModified + ", but expected time was:" + unchangedTargetFile1LastModified );
        assertTrue(changedTargetFile11LastModified < assertFile(targetDir1, "changedOutput1.txt", "changedValue"));
        assertTrue(changedTargetFile12LastModified < assertFile(targetDir1, "changedOutput2.txt", "changedValue"));
        assertTrue(unchangedTargetFile2LastModified == assertFile(targetDir2, "unchangedOutput.txt", "unchangedValue"));
        assertTrue(changedTargetFile21LastModified < assertFile(targetDir2, "changedOutput1.txt", "changedValue"));
        assertTrue(changedTargetFile22LastModified < assertFile(targetDir2, "changedOutput2.txt", "changedValue"));
    }

    /**
     * Tests that file exists and content equals to supllied expectedContent.
     * @param targetDir where to find the file
     * @param filename
     * @param expectedContent
     * @return last modified time of file in milliseconds
     * @throws IOException
     */
    private long assertFile(final File targetDir, final String filename, final String expectedContent) throws IOException
    {
        File targetFile = new File(targetDir, filename);
        assertTrue(targetFile.exists());
        assertEquals(expectedContent, FileUtils.readFileToString(targetFile, "ISO-8859-1"));

        return targetFile.lastModified();
    }
}
