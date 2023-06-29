package org.apache.torque.generator.source.transform;

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

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.control.Controller;
import org.junit.jupiter.api.Test;


/**
 * Tests whether the loadAdditionalSourceTransformer works correctly.
 */
public class LoadAdditionalSourceTransformerTest extends BaseTest
{
    private static final String EXPECTED = "<root>\n"
            + "  <element>\n"
            + "    <child/>\n"
            + "    <additional>\n"
            + "      <element/>\n"
            + "    </additional>\n"
            + "    <additional>\n"
            + "      <element/>\n"
            + "    </additional>\n"
            + "  </element>\n"
            + "  <element/>\n"
            + "</root>\n";

    @Test
    public void testLoadAdditionalSourceTransformerGeneration() throws Exception
    {
        File targetDir = new File("target/test/loadAdditionalSourceTransformer");
        FileUtils.deleteDirectory(targetDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/loadAdditionalSourceTransformer")));
        projectPaths.setOutputDirectory(null, targetDir);
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        controller.run(unitDescriptors);

        assertTrue(targetDir.exists());
        File targetFile = new File(targetDir, "output.txt");
        assertTrue(targetFile.exists());

        String content = FileUtils.readFileToString(targetFile);
        assertEquals(EXPECTED, content.toString());
    }
}
