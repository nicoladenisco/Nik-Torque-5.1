package org.apache.torque.templates;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.configuration.paths.Maven2ProjectPaths;
import org.apache.torque.generator.control.Controller;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests how to access the templates from java.
 *
 * This does not really test the templates,
 * however in the reference documentation it is mentioned how to use
 * the generator/templates from java, and this test makes sure it works
 * as documented.
 *
 * @version $Id: GenerateOmClassesFromJavaTest.java 1856067 2019-03-22 15:32:47Z gk $
 */
public class GenerateOmClassesFromJavaTest
{
  @Test
  @DisplayName("Generate OM Classes From Java")
  public void generateOMClassesFromJava()
     throws Exception
  {
    Logger.getRootLogger().setLevel(Level.DEBUG);

    Controller controller = new Controller();
    List<UnitDescriptor> unitDescriptors = new ArrayList<>();

    Map<String, String> overrideOptions = new HashMap<>();
    overrideOptions.put("torque.om.package", "org.apache.torque.templates.test");

    CustomProjectPaths projectPaths
       = new CustomProjectPaths(
          new Maven2DirectoryProjectPaths(new File(".")));
    projectPaths.setConfigurationPackage("org.apache.torque.templates.om");
    projectPaths.setConfigurationDir(null);
    projectPaths.setSourceDir(new File("src/test/simple-schema"));
    projectPaths.setOutputDirectory(
       null,
       new File("target/generateOmClassesFromJava/default"));
    projectPaths.setOutputDirectory(
       Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
       new File("target/generateOmClassesFromJava/modifiable"));
    UnitDescriptor unitDescriptor = new UnitDescriptor(
       UnitDescriptor.Packaging.CLASSPATH,
       projectPaths,
       new DefaultTorqueGeneratorPaths());
    unitDescriptor.setOverrideOptions(
       new MapOptionsConfiguration(overrideOptions));
    unitDescriptors.add(unitDescriptor);

    controller.run(unitDescriptors);
    assertTrue(
       new File("target/generateOmClassesFromJava/default/org/apache/torque/templates/test/BaseAPeer.java")
          .exists());
    assertTrue(
       new File("target/generateOmClassesFromJava/modifiable/org/apache/torque/templates/test/APeer.java")
          .exists());
  }
}
