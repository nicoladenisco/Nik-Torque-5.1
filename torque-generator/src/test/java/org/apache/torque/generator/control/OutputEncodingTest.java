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

import org.apache.commons.io.FileUtils;
import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.junit.jupiter.api.Test;

;

/**
 * Tests whether the output encoding handling is correct.
 */
public class OutputEncodingTest extends BaseTest
{
    @Test
    public void testOutputEncoding() throws Exception
    {
        File targetDir = new File("target/test/outputEncoding");
        FileUtils.deleteDirectory(targetDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/outputEncoding")));
        projectPaths.setOutputDirectory(null, targetDir);
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        controller.run(unitDescriptors);

        assertTrue(targetDir.exists());
        // We cannot check the default encoding file content because
        // there are default charsets where the generated characters
        // do not exist (e.G. US 7-bit ASCII)
        // -> this will produce error. So we check only file existence.
        assertTrue(new File(targetDir, "defaultEncoding.txt").exists());
        checkFile(
                new File(targetDir, "iso-8859-1.txt"),
                "iso-8859-1");
        checkFile(
                new File(targetDir, "utf8.txt"),
                "utf-8");
    }

    private void checkFile(File file, String encoding)
            throws IOException
    {
        assertTrue(file.exists());
        String content = FileUtils.readFileToString(file, encoding);
        assertEquals("Test Outlet output; foo=öäü; bar=ÖÄÜ", content);
    }
}
