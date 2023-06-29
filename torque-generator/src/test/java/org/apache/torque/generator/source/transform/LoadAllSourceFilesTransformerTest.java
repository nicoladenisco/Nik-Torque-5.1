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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

/**
 * Tests whether the loadAllSourceTransformer works correctly.
 */
public class LoadAllSourceFilesTransformerTest extends BaseTest
{
    private static final String TEST_RESOURCES_ROOT
    = "src/test/loadAllSourceFilesTransformer";

    @Test
    public void testLoadAllSourceFilesTransformerGeneration() throws Exception
    {
        File targetDir = new File("target/test/loadAllSourceFilesTransformer");
        FileUtils.deleteDirectory(targetDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File(TEST_RESOURCES_ROOT)));
        projectPaths.setOutputDirectory(null, targetDir);
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        controller.run(unitDescriptors);

        assertTrue(targetDir.exists());
        File targetFile = new File(targetDir, "output_for_source1.xml");
        assertTrue(targetFile.exists());
        File expectedFile = new File(TEST_RESOURCES_ROOT, "expected1.xml");

        String result = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
        String reference = FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8);
        Diff myDiffIdentical = DiffBuilder.compare(reference).ignoreComments().withTest(result)
                                   .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                                   .checkForIdentical()
                                   .build();
        assertFalse(myDiffIdentical.hasDifferences(), "XML identical " + myDiffIdentical.toString());

        targetFile = new File(targetDir, "output_for_source2.xml");
        assertTrue(targetFile.exists());
        expectedFile = new File(TEST_RESOURCES_ROOT, "expected2.xml");
        
        result = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
        reference = FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8);
        myDiffIdentical = DiffBuilder.compare(reference).ignoreComments().withTest(result)
                                   .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                                   .checkForIdentical()
                                   .build();
        assertFalse(myDiffIdentical.hasDifferences(), "XML identical " + myDiffIdentical.toString());
    }
}
