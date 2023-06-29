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
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ExistingTargetStrategyTest extends BaseTest
{
    private final File confRootDir = new File("src/test/existingTargetStrategy");
    private final File targetDir1 = new File("target/test/existingTargetStrategy/target1");
    private final File targetDir2 = new File("target/test/existingTargetStrategy/target2");
    private final File workDir
        = new File("target/test/existingTargetStrategy/work");
    private final File targetFile1 = new File(targetDir1, "output1.txt");
    private final File targetFile2 = new File(targetDir2, "output2.txt");
    private final File workFile1 = new File(workDir, "raw-generated/default/output1.txt");
    private final File workFile2 = new File(workDir, "raw-generated/other/outputDirKey2/output2.txt");

    private final String srcPath1 = "src/main/torque-gen/src/source1.properties";
    private final String srcPath2 = "src/main/torque-gen/src/source2.properties";

    @BeforeEach
    public void setUp() throws IOException
    {
        FileUtils.deleteDirectory(targetDir1);
        FileUtils.deleteDirectory(targetDir2);
        FileUtils.deleteDirectory(workDir);
    }

    @Test
    public void testReplaceStrategy() throws Exception
    {
        File configurationDirectory = new File(confRootDir, "initial");
        runGeneration(configurationDirectory, null);
        assertFile(new File(configurationDirectory, srcPath1), targetFile1);
        assertFile(new File(configurationDirectory, srcPath2), targetFile2);
        configurationDirectory = new File(confRootDir, "replaceStrategy");
        runGeneration(configurationDirectory, null);
        assertFile(new File(configurationDirectory, srcPath1), targetFile1);
        assertFile(new File(configurationDirectory, srcPath2), targetFile2);
    }

    @Test
    public void testMergeStrategyChangeGenerationResult() throws Exception
    {
        File configurationDirectory = new File(confRootDir, "mergeStrategy");
        File initialSourceDir
            = new File(confRootDir, "/initial/src/main/torque-gen/src");
        runGeneration(configurationDirectory, initialSourceDir);
        File initialSourceDirectory = new File(confRootDir, "initial");
        assertFile(new File(initialSourceDirectory, srcPath1), targetFile1);
        assertFile(new File(initialSourceDirectory, srcPath2), targetFile2);
        assertFile(new File(initialSourceDirectory, srcPath1), workFile1);
        assertFile(new File(initialSourceDirectory, srcPath2), workFile2);

        // change the generation result but do not change the target file
        // -> new target must look like new generation result
        File mergeSourceDir  = new File(
                confRootDir,
                "/mergeStrategy/src/main/torque-gen/src");
        runGeneration(configurationDirectory, mergeSourceDir);
        assertFile(new File(configurationDirectory, srcPath1), targetFile1);
        assertFile(new File(configurationDirectory, srcPath2), targetFile2);
        assertFile(new File(configurationDirectory, srcPath1), workFile1);
        assertFile(new File(configurationDirectory, srcPath2), workFile2);
    }

    @Test
    public void testMergeStrategyChangeTarget() throws Exception
    {
        File configurationDirectory = new File(confRootDir, "mergeStrategy");
        File initialSourceDir
            = new File(confRootDir, "/initial/src/main/torque-gen/src");
        runGeneration(configurationDirectory, initialSourceDir);
        File initialSourceDirectory = new File(confRootDir, "initial");
        assertFile(new File(initialSourceDirectory, srcPath1), targetFile1);
        assertFile(new File(initialSourceDirectory, srcPath2), targetFile2);
        assertFile(new File(initialSourceDirectory, srcPath1), workFile1);
        assertFile(new File(initialSourceDirectory, srcPath2), workFile2);

        // change the target file result but do not change the generation result
        // -> new target must not change
        File mergeStrategyDir  = new File(
                confRootDir,
                "/mergeStrategy");
        String intermediateTargetContent1 = FileUtils.readFileToString(
                new File(mergeStrategyDir, srcPath1),
                "ISO-8859-1");
        intermediateTargetContent1 = Pattern.compile("#.*\\r?\\n{1}?")
                .matcher(intermediateTargetContent1)
                .replaceAll("");
        FileUtils.writeStringToFile(
                targetFile1,
                intermediateTargetContent1,
                "ISO-8859-1");
        String intermediateTargetContent2 = FileUtils.readFileToString(
                new File(mergeStrategyDir, srcPath2),
                "ISO-8859-1");
        intermediateTargetContent2 = Pattern.compile("#.*\\r?\\n{1}?")
                .matcher(intermediateTargetContent2)
                .replaceAll("");
        FileUtils.writeStringToFile(
                targetFile2,
                intermediateTargetContent2,
                "ISO-8859-1");
        runGeneration(configurationDirectory, initialSourceDir);
        String endGenerationContent1
        = FileUtils.readFileToString(targetFile1, "ISO-8859-1");
        assertEquals(intermediateTargetContent1, endGenerationContent1);
        String endGenerationContent2
        = FileUtils.readFileToString(targetFile2, "ISO-8859-1");
        assertEquals(intermediateTargetContent2, endGenerationContent2);
        assertFile(new File(initialSourceDirectory, srcPath1), workFile1);
        assertFile(new File(initialSourceDirectory, srcPath2), workFile2);
    }

    @Test
    public void testMergeStrategyChangeGenerationResultAndTarget()
            throws Exception
    {
        File configurationDirectory = new File(confRootDir, "mergeStrategy");
        File initialSourceDir
            = new File(confRootDir, "/initial/src/main/torque-gen/src");
        runGeneration(configurationDirectory, initialSourceDir);
        File initialSourceDirectory = new File(confRootDir, "initial");
        assertFile(new File(initialSourceDirectory, srcPath1), targetFile1);
        assertFile(new File(initialSourceDirectory, srcPath2), targetFile2);
        assertFile(new File(initialSourceDirectory, srcPath1), workFile1);
        assertFile(new File(initialSourceDirectory, srcPath2), workFile2);

        // change the target file result and the generation result
        File newTargetContentsFile1 = new File(
                configurationDirectory,
                "src/main/torque-gen/src/source1.properties");
        String newTargetContent1 = FileUtils.readFileToString(
                newTargetContentsFile1,
                "ISO-8859-1");
        newTargetContent1 = Pattern.compile("#.*\\r?\\n{1}?")
                .matcher(newTargetContent1)
                .replaceAll("");
        FileUtils.writeStringToFile(targetFile1, newTargetContent1, "ISO-8859-1");
        File newTargetContentsFile2 = new File(
                configurationDirectory,
                "src/main/torque-gen/src/source2.properties");
        String newTargetContent2 = FileUtils.readFileToString(
                newTargetContentsFile2,
                "ISO-8859-1");
        newTargetContent2 = Pattern.compile("#.*\\r?\\n{1}?")
                .matcher(newTargetContent2)
                .replaceAll("");
        FileUtils.writeStringToFile(targetFile2, newTargetContent2, "ISO-8859-1");
        File conflictSourceDir = new File(configurationDirectory, "conflict");
        runGeneration(configurationDirectory, conflictSourceDir);
        assertFile(
                new File(conflictSourceDir, "expected1.properties"),
                targetFile1);
        assertFile(
                new File(conflictSourceDir, "expected2.properties"),
                targetFile2);
        assertFile(new File(conflictSourceDir, "source1.properties"), workFile1);
        assertFile(new File(conflictSourceDir, "source2.properties"), workFile2);
    }

    // TORQUE-226
    @Test
    public void testMergeStrategyNoNewlineAtEndOfFile() throws Exception
    {
        File configurationDirectory = new File(confRootDir, "mergeStrategyNoNewlinesAtEndOfFile");
        File initialSourceDir
            = new File(confRootDir, "/initial/src/main/torque-gen/src");
        runGeneration(configurationDirectory, initialSourceDir);
        assertFile(new File(configurationDirectory, "src/main/torque-gen/templates/simpleOutput.vm"), targetFile1);
        assertFile(new File(configurationDirectory, "src/main/torque-gen/templates/simpleOutput.vm"), workFile1);

        // rerun
        runGeneration(configurationDirectory, initialSourceDir);
        assertFile(new File(configurationDirectory, "src/main/torque-gen/templates/simpleOutput.vm"), targetFile1);
        assertFile(new File(configurationDirectory, "src/main/torque-gen/templates/simpleOutput.vm"), workFile1);
    }

    @Test
    public void testSkipStrategy() throws Exception
    {
        File initialConfigurationDirectory = new File(confRootDir, "initial");
        runGeneration(initialConfigurationDirectory, null);
        assertFile(new File(initialConfigurationDirectory, srcPath1), targetFile1);
        assertFile(new File(initialConfigurationDirectory, srcPath2), targetFile2);
        File newConfigurationDirectory = new File(confRootDir, "skipStrategy");
        runGeneration(newConfigurationDirectory, null);
        assertFile(new File(initialConfigurationDirectory, srcPath1), targetFile1);
        assertFile(new File(initialConfigurationDirectory, srcPath2), targetFile2);
    }

    @Test
    public void testAppendStrategy() throws Exception
    {
        File configurationDirectoryInitial = new File(confRootDir, "initial");
        runGeneration(configurationDirectoryInitial, null);
        assertFile(
                new File(configurationDirectoryInitial, srcPath1),
                targetFile1);
        assertFile(
                new File(configurationDirectoryInitial, srcPath2),
                targetFile2);
        File configurationDirectoryAppend
            = new File(confRootDir, "appendStrategy");
        runGeneration(configurationDirectoryAppend, null);
        File expectedTargetFile1 = new File(targetDir1, "expectedOutput1.txt");
        String initial1 = FileUtils.readFileToString(
                new File(configurationDirectoryInitial, srcPath1),
                "ISO-8859-1");
        String appended1 = FileUtils.readFileToString(
                new File(configurationDirectoryAppend, srcPath1),
                "ISO-8859-1");
        FileUtils.writeStringToFile(expectedTargetFile1, initial1 + appended1);
        File expectedTargetFile2 = new File(targetDir1, "expectedOutput2.txt");
        String initial2 = FileUtils.readFileToString(
                new File(configurationDirectoryInitial, srcPath2),
                "ISO-8859-1");
        String appended2 = FileUtils.readFileToString(
                new File(configurationDirectoryAppend, srcPath2),
                "ISO-8859-1");
        FileUtils.writeStringToFile(expectedTargetFile2, initial2 + appended2);
        assertFile(expectedTargetFile1, targetFile1);
        assertFile(expectedTargetFile2, targetFile2);
    }

    private File runGeneration(final File configurationDirectory, final File sourceDir)
            throws GeneratorException
    {
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(configurationDirectory));
        projectPaths.setOutputDirectory(null, targetDir1);
        projectPaths.setOutputDirectory("outputDirKey2", targetDir2);
        projectPaths.setWorkDir(workDir);
        if (sourceDir != null)
        {
            projectPaths.setSourceDir(sourceDir);
        }
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        controller.run(unitDescriptors);
        return configurationDirectory;
    }

    private void assertFile(final File expectedFile, final File actualFile)
            throws IOException
    {
        assertTrue(actualFile.exists());
        String expected = FileUtils.readFileToString(expectedFile);
        // remove Apache license header
        expected = Pattern.compile("#.*\\r?\\n{1}?")
                .matcher(expected)
                .replaceAll("");
        String actual = FileUtils.readFileToString(actualFile);
        assertEquals(expected, actual);
    }
}
