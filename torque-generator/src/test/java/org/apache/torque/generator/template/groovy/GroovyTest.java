package org.apache.torque.generator.template.groovy;

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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.control.Controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests that another template language can easily integrated.
 *
 * @version $Id: GroovyTest.java 1855607 2019-03-15 16:41:24Z gk $
 */
public class GroovyTest
{
    private static final File TARGET_DIR
        = new File("target/test/groovy");

    private static final File SCRIPT_OUTPUT_FILE
        = new File(TARGET_DIR, "scriptOutput.txt");

    private static final File TEMPLATE_OUTPUT_FILE
        = new File(TARGET_DIR, "templateOutput.txt");

    @BeforeEach
    public void setUp() throws Exception
    {
        FileUtils.deleteDirectory(TARGET_DIR);
    }

    /**
     * Tests that Groovy outlets can be used.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testGroovy() throws Exception
    {
        final Controller controller = new Controller();
        final List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        final CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(
                        new File("src/test/groovyTemplates")));
        projectPaths.setOutputDirectory(null, TARGET_DIR);
        unitDescriptors.add(new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths()));
        controller.run(unitDescriptors);
        assertTrue(SCRIPT_OUTPUT_FILE.exists());
        assertEquals(
                "groovy script output: root",
                FileUtils.readFileToString(SCRIPT_OUTPUT_FILE));
        assertTrue(TEMPLATE_OUTPUT_FILE.exists());
        assertEquals(
                "groovy template output: root",
                FileUtils.readFileToString(TEMPLATE_OUTPUT_FILE));
    }
}
