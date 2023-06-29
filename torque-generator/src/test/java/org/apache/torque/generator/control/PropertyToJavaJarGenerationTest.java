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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2JarProjectPaths;
import org.junit.jupiter.api.Test;


/**
 * Tests generation where the templates are read from a jar file.
 */
public class PropertyToJavaJarGenerationTest extends BaseTest
{
    @Test
    public void testPropertyToJavaJarGeneration() throws Exception
    {
        File target = new File("target/test/propertyToJavaJar/target");
        FileUtils.deleteDirectory(target);
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.JAR,
                new Maven2JarProjectPaths(
                        new File("target/test/propertyToJavaJar"),
                        "propertyToJava.jar"),
                new DefaultTorqueGeneratorPaths()));
        controller.run(unitDescriptors);
        assertTrue(target.exists());
    }
}
