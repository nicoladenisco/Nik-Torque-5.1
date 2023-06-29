package org.apache.torque.generator.configuration;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarFile;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for the class PackageResources.
 *
 * @version $Id: ClasspathConfigurationProviderTest.java 1456694 2013-03-14 21:54:47Z tfischer $
 */
public class PackageResourcesTest
{

    /**
     * Testet, dass ein Paket in mehreren Jar files und directories
     * gefunden wird, wenn der Pfad nicht mit einem Slash anfängt und endet.
     *
     * @throws Exception wenn der Test Fehlschlägt.
     */
    @Test @Ignore // produces errors on Jenkins
    public void testGetJarFilesAndDirectories() throws Exception
    {
        // Package kommt mindestens in commons-beanutils
        // und in commons-collections vor
        PackageResources packageResources = new PackageResources(
                "org/apache",
                this.getClass().getClassLoader());
        List<JarFile> jarFiles = packageResources.getJarFiles();
        boolean foundCommonsBeanutils = false;
        boolean foundCommonsLang = false;
        for (JarFile jarFile : jarFiles)
        {
            String name = jarFile.getName();
            if (name.contains("commons-lang"))
            {
                foundCommonsLang = true;
            }
            if (name.contains("commons-beanutils"))
            {
                foundCommonsBeanutils = true;
            }
        }
        assertTrue(foundCommonsLang);
        assertTrue(foundCommonsBeanutils);

        List<File> directories = packageResources.getDirectories();
        assertEquals(2, directories.size());
        boolean foundTargetClasses = false;
        boolean foundTargetTestClasses = false;
        for (File directory : directories)
        {
            String name = directory.getAbsolutePath().replace('\\', '/');
            if (name.endsWith("target/classes/org/apache"))
            {
                foundTargetClasses = true;
            }
            if (name.endsWith("target/test-classes/org/apache"))
            {
                foundTargetTestClasses = true;
            }
        }
        assertTrue(foundTargetClasses);
        assertTrue(foundTargetTestClasses);
    }

    /**
     * Testet, dass ein Paket in mehreren Jar files und directories
     * gefunden wird, wenn der Pfad mit einem Slash anfängt und endet.
     *
     * @throws Exception wenn der Test Fehlschlägt.
     */
    @Test @Ignore // produces errors on Jenkins
    public void testGetJarFilesAndDirectoriesSlashes() throws Exception
    {
        // Package kommt mindestens in commons-beanutils
        // und in commons-collections vor
        PackageResources packageResources = new PackageResources(
                "/org/apache/",
                this.getClass().getClassLoader());
        List<JarFile> jarFiles = packageResources.getJarFiles();
        boolean foundCommonsBeanutils = false;
        boolean foundCommonsLang = false;
        for (JarFile jarFile : jarFiles)
        {
            String name = jarFile.getName();
            if (name.contains("commons-lang"))
            {
                foundCommonsLang = true;
            }
            if (name.contains("commons-beanutils"))
            {
                foundCommonsBeanutils = true;
            }
        }
        assertTrue(foundCommonsLang);
        assertTrue(foundCommonsBeanutils);

        List<File> directories = packageResources.getDirectories();
        assertEquals(2, directories.size());
        boolean foundTargetClasses = false;
        boolean foundTargetTestClasses = false;
        for (File directory : directories)
        {
            String name = directory.getAbsolutePath().replace('\\', '/');
            if (name.endsWith("target/classes/org/apache"))
            {
                foundTargetClasses = true;
            }
            if (name.endsWith("target/test-classes/org/apache"))
            {
                foundTargetTestClasses = true;
            }
        }
        assertTrue(foundTargetClasses);
        assertTrue(foundTargetTestClasses);
    }

    public void testFileNamesInDirectory()
    {
        HashSet<String> actual = new HashSet<>();
        actual.addAll(PackageResources.getFilesInDirectoryWithSuffix(
                new File("src/test/file"),
                ".properties",
                "",
                false));
        HashSet<String> expected = new HashSet<>();
        expected.add("1.properties");
        expected.add("11.properties");
        assertEquals(expected, actual);
    }

    public void testFileNamesInDirectorySearchSubdirectories()
    {
        HashSet<String> actual = new HashSet<>();
        actual.addAll(PackageResources.getFilesInDirectoryWithSuffix(
                new File("src/test/file"),
                ".properties",
                "",
                true));
        HashSet<String> expected = new HashSet<>();
        expected.add("1.properties");
        expected.add("11.properties");
        expected.add("subfolder/2.properties");
        expected.add("subfolder/subsubfolder/3.properties");
        assertEquals(expected, actual);
    }

    public void testFileNamesInDirectorySuffixNull()
    {
        HashSet<String> actual = new HashSet<>();
        actual.addAll(PackageResources.getFilesInDirectoryWithSuffix(
                new File("src/test/file"),
                null,
                "",
                true));
        HashSet<String> expected = new HashSet<>();
        assertEquals(expected, actual);
    }

    public void testFileNamesInJarDirectory() throws Exception
    {
        HashSet<String> actual = new HashSet<>();
        actual.addAll(PackageResources.getFilesInJarDirectoryWithSuffix(
                "templates",
                new JarFile("target/test/propertyToJavaJar/src/main/torque-gen/propertyToJava.jar"),
                ".vm",
                false));
        HashSet<String> expected = new HashSet<>();
        expected.add("propertyCopy/propertiesCopy.vm");
        expected.add("propertiesExtendedToJava.vm");
        expected.add("propertiesToJava.vm");
        expected.add("propertyCopy/propertyCopy.vm");
        expected.add("variableAssignment.vm");
        expected.add("variableDefinition.vm");
        assertEquals(expected, actual);
    }

    public void testFileNamesInJarDirectorySearchSubdirectories()
            throws Exception
    {
        HashSet<String> actual = new HashSet<>();
        actual.addAll(PackageResources.getFilesInJarDirectoryWithSuffix(
                "templates",
                new JarFile("target/test/propertyToJavaJar/src/main/torque-gen/propertyToJava.jar"),
                ".xml",
                true));
        assertEquals(8, actual.size());
        assertTrue(actual.contains("conf/control.xml"));
        assertTrue(actual.contains("src/propertiesData.xml"));
    }

    public void testFileNamesInJarDirectorySuffixNull() throws Exception
    {
        HashSet<String> actual = new HashSet<>();
        actual.addAll(PackageResources.getFilesInJarDirectoryWithSuffix(
                "templates",
                new JarFile("target/test/propertyToJavaJar/src/main/torque-gen/propertyToJava.jar"),
                null,
                true));
        HashSet<String> expected = new HashSet<>();
        assertEquals(expected, actual);
    }
}
