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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.junit.jupiter.api.Test;

/**
 * A test case for a more complex generation.
 * Checks that we can use different generator types in one generation,
 * that debugging output works
 * and that the runOnlyOnSchemaChange detection works.
 *
 * @version $Id: PropertyToJavaGenerationTest.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class PropertyToJavaGenerationTest extends BaseTest
{
    /**
     * Tests that the property to java generation works.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPropertyToJavaGeneration() throws Exception
    {
        // prepare
        File targetDir = new File("target/test/propertyToJava");
        FileUtils.deleteDirectory(targetDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/propertyToJava")));
        projectPaths.setOutputDirectory(null, targetDir);
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));

        // execute
        controller.run(unitDescriptors);

        // verify
        assertTrue(targetDir.exists());
        File logoResultFile = new File(targetDir, "logo.png");
        assertTrue(logoResultFile.exists());
        byte[] expectedContent = FileUtils.readFileToByteArray(
                new File("src/test/propertyToJava/src/main/torque-gen/resources/torque-logo-new.png"));
        assertArrayEquals(
                expectedContent,
                FileUtils.readFileToByteArray(logoResultFile));
        File propertiesResultFile
            = new File(targetDir, "Properties.properties");
        assertTrue(propertiesResultFile.exists());
        File propertiesExpectedFile
            = new File("src/test/propertyToJava/expectedProperties.properties");
        assertEquals(FileUtils.readFileToString(propertiesExpectedFile, StandardCharsets.ISO_8859_1),
                FileUtils.readFileToString(propertiesResultFile, StandardCharsets.ISO_8859_1));
    }

    /**
     * Tests that the property to java generation works with debug output
     * switched on.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPropertyToJavaGenerationDebugOutput() throws Exception
    {
        // prepare
        File targetDir = new File("target/test/propertyToJava");
        FileUtils.deleteDirectory(targetDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/propertyToJava")));
        projectPaths.setOutputDirectory(null, targetDir);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setAddDebuggingInfoToOutput(true);
        unitDescriptors.add(unitDescriptor);

        // execute
        controller.run(unitDescriptors);

        // verify
        assertTrue(targetDir.exists());
        File logoResultFile = new File(targetDir, "logo.png");
        assertTrue(logoResultFile.exists());
        byte[] expectedContent = FileUtils.readFileToByteArray(
                new File("src/test/propertyToJava/src/main/torque-gen/resources/torque-logo-new.png"));
        assertArrayEquals(
                expectedContent,
                FileUtils.readFileToByteArray(logoResultFile));
        File propertiesResultFile
            = new File(targetDir, "Properties.properties");
        assertTrue(propertiesResultFile.exists());
        File propertiesExpectedFile
            = new File("src/test/propertyToJava/expectedPropertiesDebugOutput.properties");
        assertEquals(FileUtils.readFileToString(propertiesExpectedFile, StandardCharsets.ISO_8859_1),
                FileUtils.readFileToString(propertiesResultFile, StandardCharsets.ISO_8859_1));
    }

    /**
     * Tests that runOnlyOnSchemaChange set true and an unchanged source
     * results in not generating a second time.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPropertyToJavaRunOnlyOnSchemChangeNoChange()
            throws Exception
    {
        // prepare
        File targetDir = new File("target/test/propertyToJava");
        File cacheDir = new File("target/cache/propertyToJava");
        File sourceDir = new File("target/source/propertyToJava");
        FileUtils.cleanDirectory(targetDir);
        FileUtils.cleanDirectory(cacheDir);
        FileUtils.cleanDirectory(sourceDir);
        FileUtils.deleteDirectory(targetDir);
        FileUtils.deleteDirectory(cacheDir);
        FileUtils.deleteDirectory(sourceDir);
        FileUtils.copyDirectory(
                new File("src/test/propertyToJava/src/main/torque-gen/src"),
                sourceDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/propertyToJava")));
        projectPaths.setOutputDirectory(null, targetDir);
        projectPaths.setCacheDir(cacheDir);
        projectPaths.setSourceDir(sourceDir);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setRunOnlyOnSourceChange(true);
        unitDescriptors.add(unitDescriptor);

        // run first time
        controller.run(unitDescriptors);
        File propertiesResultFile
            = new File(targetDir, "Properties.properties");
        assertTrue(propertiesResultFile.exists());

        // delete target so we can see if generation does not run again
        assertTrue(propertiesResultFile.delete());

        // execute
        controller.run(unitDescriptors);

        // verify
        assertFalse(propertiesResultFile.exists());
    }

    /**
     * Tests that runOnlyOnSchemaChange set true and a different checksum
     * results in generating a second time.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPropertyToJavaRunOnlyOnSchemChangeChecksumChange()
            throws Exception
    {
        // prepare
        File targetDir = new File("target/test/propertyToJava");
        File cacheDir = new File("target/cache/propertyToJava");
        File sourceDir = new File("target/source/propertyToJava");
        FileUtils.deleteDirectory(targetDir);
        FileUtils.deleteDirectory(cacheDir);
        FileUtils.deleteDirectory(sourceDir);
        FileUtils.copyDirectory(
                new File("src/test/propertyToJava/src/main/torque-gen/src"),
                sourceDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/propertyToJava")));
        projectPaths.setOutputDirectory(null, targetDir);
        projectPaths.setCacheDir(cacheDir);
        projectPaths.setSourceDir(sourceDir);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setRunOnlyOnSourceChange(true);
        unitDescriptors.add(unitDescriptor);

        // run first time
        controller.run(unitDescriptors);
        File propertiesResultFile
            = new File(targetDir, "Properties.properties");
        assertTrue(propertiesResultFile.exists());

        // change checksum in checksums file
        File checksumFile = new File(
                cacheDir,
                "last-source-changes.checksums");
        String checksumContent = FileUtils.readFileToString(checksumFile, StandardCharsets.ISO_8859_1);
        // check contains only one line
        assertEquals(1, StringUtils.countMatches(checksumContent, "\n"));
        int firstMinusPos = checksumContent.indexOf("-");
        int secondMinusPos = checksumContent.indexOf("-", firstMinusPos + 1);
        String checksum = checksumContent.substring(firstMinusPos, secondMinusPos + 1);
        String changedChecksumContent = checksumContent.replace(checksum, "-AA-");
        FileUtils.writeStringToFile(checksumFile, changedChecksumContent, StandardCharsets.ISO_8859_1);

        // delete target so we can see if generation runs again
        assertTrue(propertiesResultFile.delete());

        // execute
        controller.run(unitDescriptors);

        // verify
        assertTrue(propertiesResultFile.exists());
    }

    /**
     * Tests that runOnlyOnSchemaChange set true and a different modification
     * date results in generating a second time.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPropertyToJavaRunOnlyOnSchemChangeModificationDateChange()
            throws Exception
    {
        // prepare
        File targetDir = new File("target/test/propertyToJava");
        File cacheDir = new File("target/cache/propertyToJava");
        File sourceDir = new File("target/source/propertyToJava");
        FileUtils.deleteDirectory(targetDir);
        FileUtils.deleteDirectory(cacheDir);
        FileUtils.deleteDirectory(sourceDir);
        FileUtils.copyDirectory(
                new File("src/test/propertyToJava/src/main/torque-gen/src"),
                sourceDir);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/propertyToJava")));
        projectPaths.setOutputDirectory(null, targetDir);
        projectPaths.setCacheDir(cacheDir);
        projectPaths.setSourceDir(sourceDir);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setRunOnlyOnSourceChange(true);
        unitDescriptors.add(unitDescriptor);

        // run first time
        controller.run(unitDescriptors);
        File propertiesResultFile
            = new File(targetDir, "Properties.properties");
        assertTrue(propertiesResultFile.exists());

        // change modification date of source file
        File sourceFile = new File(
                sourceDir,
                "propertiesData.properties");
        assertTrue(sourceFile.setLastModified(
                System.currentTimeMillis() + 1000L));

        // delete target so we can see if generation runs again
        assertTrue(propertiesResultFile.delete());

        // execute
        controller.run(unitDescriptors);

        // verify
        assertTrue(propertiesResultFile.exists());
    }
}
