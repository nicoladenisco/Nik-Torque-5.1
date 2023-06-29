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
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
public class DeeplyNestedMergepointsTest extends BaseTest
{
    @Test
    public void testDeeplyNestedMergepointsGeneration() throws Exception
    {
        File targetDir = new File("target/test/deeplyNestedMergepoints");
        FileUtils.deleteDirectory(targetDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/deeplyNestedMergepoints")));
        projectPaths.setOutputDirectory(null, targetDir);
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        controller.run(unitDescriptors);

        assertTrue(targetDir.exists());
        File targetFile = new File(targetDir, "output.txt");
        assertTrue(targetFile.exists());

        FileReader fileReader = new FileReader(targetFile);
        StringBuilder content = new StringBuilder();
        int readChars;
        char[] buffer = new char[50];
        do
        {
            readChars = fileReader.read(buffer);
            if (readChars != -1)
            {
                content.append(buffer, 0, readChars);
            }
        }
        while (readChars != -1);
        fileReader.close();
        assertEquals("content", content.toString());
    }
}
