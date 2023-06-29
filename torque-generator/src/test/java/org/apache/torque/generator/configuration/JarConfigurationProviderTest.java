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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the class JarConfigurationProvider.
 *
 * @version $Id: ClasspathConfigurationProviderTest.java 1456694 2013-03-14 21:54:47Z tfischer $
 */
public class JarConfigurationProviderTest
{

    private JarConfigurationProvider jarConfigurationProvider;

    @Before
    public void before() throws ConfigurationException
    {
        Map<String, File> outputDirMap = new HashMap<>();
        outputDirMap.put(null, new File("generated-sources"));
        ProjectPaths projectPaths = new CustomProjectPaths(
                new File("target/test/propertyToJavaJar/src/main/torque-gen/propertyToJava.jar"),
                null,
                new File("src"),
                outputDirMap,
                new File("work"),
                new File("cache"));

        jarConfigurationProvider = new JarConfigurationProvider(
                projectPaths,
                new DefaultTorqueGeneratorPaths());
    }

    @Test
    public void testGetTemplateInputStream() throws Exception
    {
        assertArrayEquals(
                FileUtils.readFileToByteArray(new File(
                        "src/test/propertyToJava/src/main/torque-gen/templates/"
                                + "propertiesToJava.vm")),
                IOUtils.toByteArray(
                        jarConfigurationProvider.getTemplateInputStream(
                                "propertiesToJava.vm")));
    }

    @Test
    public void testGetOutletConfigurationNames() throws Exception
    {
        Set<String> expected = new HashSet<>();
        expected.add("javaTestOutlet.xml");
        expected.add("logoCopyOutlet.xml");
        expected.add("velocityExtendedPropertiesOutlet.xml");
        expected.add("velocityPropertiesCopy.xml");
        expected.add("velocityPropertiesOutlet.xml");
        expected.add("velocityVariableAssignment.xml");
        expected.add("velocityVariableDefinition.xml");
        assertEquals(
                expected,
                new HashSet<>(
                        jarConfigurationProvider.getOutletConfigurationNames()));
    }
}
