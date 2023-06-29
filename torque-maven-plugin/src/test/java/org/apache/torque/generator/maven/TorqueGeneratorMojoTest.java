package org.apache.torque.generator.maven;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

public class TorqueGeneratorMojoTest
{
	@Test
    public void testRun() throws Exception
    {
        File target = new File("target/tests/gettingStarted");
        FileUtils.deleteDirectory(target);
        TorqueGeneratorMojo mojo = new TorqueGeneratorMojo();
        mojo.setPackaging("directory");
        mojo.setProjectRootDir(new File("src/test/gettingStarted"));
        mojo.setDefaultOutputDir(target);
        mojo.setDefaultOutputDirUsage("compile");
        mojo.setProject(new MavenProject());
        mojo.execute();

        assertTrue(target.exists());
        File generatedJavaFile = new File(
                target,
                "org/apache/torque/generator/maven/PropertyKeys.java");
        assertTrue(generatedJavaFile.exists());
        File expectedJavaFile = new File(
                "src/test/resources/org/apache/torque/generator/maven/PropertyKeys.java");

        assertTrue(FileUtils.contentEquals(generatedJavaFile, expectedJavaFile), "The files differ!");
    }
}
