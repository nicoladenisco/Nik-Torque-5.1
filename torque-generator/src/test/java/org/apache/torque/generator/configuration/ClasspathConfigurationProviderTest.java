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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests for the class ClasspathConfigurationProvider.
 *
 * @version $Id: ClasspathConfigurationProviderTest.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class ClasspathConfigurationProviderTest
{
    /** System under Test. */
    private ClasspathConfigurationProvider classpathConfigurationProvider;

    @BeforeEach
    public void before()
    {
        Map<String, File> outputDirMap = new HashMap<>();
        outputDirMap.put(null, new File("generated-sources"));
        ProjectPaths projectPaths = new CustomProjectPaths(
                null,
                "org.apache.torque.generator.test.readfromclasspath",
                new File("src"),
                outputDirMap,
                new File("work"),
                new File("cache"));
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.CLASSPATH,
                projectPaths,
                new DefaultTorqueGeneratorPaths());

        classpathConfigurationProvider = new ClasspathConfigurationProvider(
                unitDescriptor);
    }

    @Test
    public void testGetTemplateInputStream() throws Exception
    {
        assertArrayEquals(
                FileUtils.readFileToByteArray(new File(
                        "src/test/resources/org/"
                                + "apache/torque/generator/test/readfromclasspath/"
                                + "templates/testTemplate.vm")),
                IOUtils.toByteArray(
                        classpathConfigurationProvider.getTemplateInputStream(
                                "testTemplate.vm")));
    }

    @Test
    public void testGetResourceWithDoubleDots() throws Exception
    {
        assertArrayEquals(
                FileUtils.readFileToByteArray(new File(
                        "src/test/resources/org/"
                                + "apache/torque/generator/test/readfromclasspath/"
                                + "templates/testTemplate.vm")),
                IOUtils.toByteArray(
                        classpathConfigurationProvider.getResourceInputStream(
                                "../templates/testTemplate.vm")));
    }

    @Test
    public void testGetResourceFromJarWithDoubleDots() throws Exception
    {
        assertArrayEquals(
                IOUtils.toByteArray(getClass().getResourceAsStream(
                        "/org/apache/commons/io/CopyUtils.class")),
                IOUtils.toByteArray(
                        classpathConfigurationProvider.getResourceInputStream(
                                "../../../../../commons/io/CopyUtils.class")));
    }

    @Test
    public void testGetOutletConfigurationNames() throws Exception
    {
        Set<String> expected = new HashSet<>();
        expected.add("javaOutlets.xml");
        expected.add("velocityOutlets.xml");
        assertEquals(
                expected,
                new HashSet<>(
                        classpathConfigurationProvider.getOutletConfigurationNames()));
    }

}
