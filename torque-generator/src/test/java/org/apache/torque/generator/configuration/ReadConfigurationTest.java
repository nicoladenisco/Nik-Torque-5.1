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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.controller.Log4j2LoggingAdapter;
import org.apache.torque.generator.configuration.controller.Log4jLoggingAdapter;
import org.apache.torque.generator.configuration.controller.Loglevel;
import org.apache.torque.generator.configuration.controller.OutletReference;
import org.apache.torque.generator.configuration.controller.Output;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration;
import org.apache.torque.generator.configuration.option.OptionsConfiguration;
import org.apache.torque.generator.configuration.outlet.OutletConfiguration;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.control.action.ApplyAction;
import org.apache.torque.generator.control.action.MergepointAction;
import org.apache.torque.generator.control.action.TraverseAllAction;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.java.JavaOutlet;
import org.apache.torque.generator.option.Option;
import org.apache.torque.generator.option.OptionImpl;
import org.apache.torque.generator.option.Options;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.processor.string.CharReplacer;
import org.apache.torque.generator.processor.string.UnixLinefeedProcessor;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourceProcessConfiguration;
import org.apache.torque.generator.source.SourceTransformerDefinition;
import org.apache.torque.generator.source.jdbc.JdbcMetadataSourceProvider;
import org.apache.torque.generator.source.stream.FileSourceProvider;
import org.apache.torque.generator.source.stream.PropertiesSourceFormat;
import org.apache.torque.generator.source.stream.XmlSourceFormat;
import org.apache.torque.generator.template.velocity.VelocityOutlet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.TestInfo;



/**
 * Tests whether the configuration is read correctly.
 */
public class ReadConfigurationTest extends BaseTest
{
    private ControllerState controllerState;
    
    private Logger logger = LogManager.getLogger(getClass());
    

    @BeforeEach
    public void setUp(TestInfo testInfo, RepetitionInfo repetitionInfo)
    {
        controllerState = new ControllerState();

        int currentRepetition = repetitionInfo.getCurrentRepetition();
        if (currentRepetition % 2 == 0) {
            Loglevel.setLoggingAdapter(new Log4j2LoggingAdapter());  
        } else {
           // no implicit Loglevel.setLoggingAdapter(null);
            Loglevel.setLoggingAdapter(new Log4jLoggingAdapter());  
        }
        UnitConfiguration unitConfiguration = new UnitConfiguration();
        // unit configuration
        logger.info(currentRepetition + " loglevel type:" + Loglevel.getLoggingAdapter().getClass());
        controllerState.setUnitConfiguration(unitConfiguration);
        Options options = new Options();
        unitConfiguration.setOptions(options);
    }

    @RepeatedTest(value = 4, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("test read configuration")
    public void testReadConfiguration() throws Exception
    {
        ProjectPaths projectPaths = new Maven2DirectoryProjectPaths(
                new File("src/test/configuration"));
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        UnitConfigurationReader configurationReader
            = new UnitConfigurationReader();
        ConfigurationHandlers configurationHandlers
            = new ConfigurationHandlers();
        UnitConfiguration unitConfiguration
        = configurationReader.read(
                unitDescriptor,
                configurationHandlers);
        {
            File defaultTargetDirectory
            = unitConfiguration.getOutputDirectory(null);
            File expected = new File(
                    "src/test/configuration/target/generated-sources");
            assertEquals(expected, defaultTargetDirectory);
        }
        {
            File modifiableTargetDirectory
            = unitConfiguration.getOutputDirectory("modifiable");
            File expected = new File(
                    "src/test/configuration/src/main/generated-java");
            assertEquals(expected, modifiableTargetDirectory);
        }
        try
        {
            unitConfiguration.getOutputDirectory("notExistingKey");
            fail("Exception expected");
        }
        catch (IllegalStateException e)
        {
            // expected
        }

        assertEquals(Loglevel.DEBUG, unitConfiguration.getLoglevel());

        {
            Options expectedOptions = new Options();
            Set<Option> expectedOptionSet = new HashSet<>();
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("org.apache.torque.generator.optionWithNamespace"),
                    "optionWithNamespaceValue"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("org.apache.optionWithNamespace"),
                    "org.apache.optionWithNamespaceValue"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("optionWithoutNamespace", ""),
                    "optionWithoutNamespaceValue"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("optionPrecedenceLastFile"),
                    "value from xml"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("jdbcUrl"),
                    "jdbc.url.option.value"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("jdbcDriver"),
                    "jdbc.driver.option.value"));
            expectedOptions.addGlobalOptions(expectedOptionSet);
            assertOptionsEquals(
                    expectedOptions,
                    unitConfiguration.getOptions());
        }

        {
            EntityReferences entityReferences
            = unitConfiguration.getEntityReferences();
            Map<String, byte[]> actual = entityReferences.getEntityReferences();
            assertEquals(2, actual.size());
            assertArrayEquals(
                    FileUtils.readFileToByteArray(
                            new File("src/test/configuration/src/main/torque-gen/resources/override.xsd")),
                    actual.get("http://db.apache.org/torque/some.xsd"));
            assertArrayEquals(
                    FileUtils.readFileToByteArray(
                            new File("src/test/configuration/src/main/torque-gen/resources/new.xsd")),
                    actual.get("http://db.apache.org/torque/new.xsd"));
        }

        {
            List<Output> outputFiles = unitConfiguration.getOutputList();
            assertEquals(3, outputFiles.size());

            {
                Output output = outputFiles.get(0);
                assertEquals(
                        new QualifiedName("org.apache.torque.generator.firstOutput"),
                        output.getName());
                assertEquals(
                        "skip",
                        output.getExistingTargetStrategy());
                assertEquals(
                        null,
                        output.getOutputDirKey());
                assertNull(output.getFilename());

                {
                    OutletReference outletConfiguration
                    = output.getContentOutlet();
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.test.readConfiguration.javaOutlet"),
                            outletConfiguration.getName());
                }

                {
                    JavaOutlet filenameOutlet
                    = (JavaOutlet) output.getFilenameOutlet();
                    assertEquals("Foo", filenameOutlet.getFoo());
                    assertEquals("Bar", filenameOutlet.getBar());
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.configuration.filenameOutlet"),
                            filenameOutlet.getName());
                }

                {
                    Fileset sourceFileset = new Fileset(
                            projectPaths.getDefaultSourcePath(),
                            new HashSet<String>(),
                            new HashSet<String>());
                    FileSourceProvider sourceProvider = new FileSourceProvider(
                            new XmlSourceFormat(),
                            sourceFileset,
                            null);
                    sourceProvider.init(
                            configurationHandlers,
                            controllerState);
                    output.getSourceProvider().init(
                            configurationHandlers,
                            controllerState);
                    assertFileSourceProviderEquals(
                            sourceProvider,
                            (FileSourceProvider) output.getSourceProvider());
                }
                assertSourceProcessConfigurationEquals(
                        new SourceProcessConfiguration(),
                        output.getSourceProcessConfiguration());
            }

            {
                Output output = outputFiles.get(1);
                assertEquals(
                        new QualifiedName("secondOutput"),
                        output.getName());
                assertEquals(
                        "replace",
                        output.getExistingTargetStrategy());
                assertEquals(
                        "secondOutputDirKey",
                        output.getOutputDirKey());
                assertNull(output.getFilename());

                {
                    OutletReference outletConfiguration
                    = output.getContentOutlet();
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.test.readConfiguration.anotherOutlet"),
                            outletConfiguration.getName());
                }

                {
                    VelocityOutlet filenameOutlet
                    = (VelocityOutlet) output.getFilenameOutlet();
                    String templateContent
                    = filenameOutlet.getContent(controllerState);
                    // remove License from template by comparing only
                    // the last line
                    String templateContentLicenseRemoved
                    = StringUtils.substringAfterLast(templateContent, "\r\n");
                    assertEquals(
                            "test template output",
                            templateContentLicenseRemoved);
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.configuration.filenameOutlet"),
                            filenameOutlet.getName());
                    assertTrue(filenameOutlet.isOptionsInContext());
                    assertTrue(filenameOutlet.isSourceAttributesInContext());
                    assertTrue(filenameOutlet.isVariablesInContext());
                }

                {
                    Fileset sourceFileset = new Fileset(
                            projectPaths.getDefaultSourcePath(),
                            createSetFrom("second.source.path.properties"),
                            createSetFrom("second.excluded.properties"));
                    FileSourceProvider sourceProvider = new FileSourceProvider(
                            new PropertiesSourceFormat(),
                            sourceFileset,
                            true);
                    sourceProvider.init(
                            configurationHandlers,
                            controllerState);
                    output.getSourceProvider().init(
                            configurationHandlers,
                            controllerState);
                    assertFileSourceProviderEquals(
                            sourceProvider,
                            (FileSourceProvider) output.getSourceProvider());
                }
                {
                    SourceProcessConfiguration sourceProcessConfiguration
                        = new SourceProcessConfiguration();
                    sourceProcessConfiguration.setStartElementsPath(
                            "properties/entry");
                    List<SourceTransformerDefinition> transformerDefinitions
                        = new ArrayList<>();

                    transformerDefinitions.add(
                            new SourceTransformerDefinition(
                                    new ConfigurationTestTransformer()));
                    transformerDefinitions.add(
                            new SourceTransformerDefinition(
                                    new OtherConfigurationTestTransformer()));
                    sourceProcessConfiguration.setSourceTransformerDefinitions(
                            transformerDefinitions);
                    sourceProcessConfiguration.setSkipDecider(
                            "org.apache.torque.generator.configuration.ConfigurationTestSkipDecider",
                            unitDescriptor);
                    assertSourceProcessConfigurationEquals(
                            sourceProcessConfiguration,
                            output.getSourceProcessConfiguration());
                }
                {
                    assertEquals(2, output.getPostprocessorDefinitions().size());
                    assertEquals(
                            CharReplacer.class,
                            output.getPostprocessorDefinitions().get(0).getPostprocessor().getClass());
                    assertEquals("x", ((CharReplacer) output.getPostprocessorDefinitions().get(0).getPostprocessor()).getToReplaceWith());
                    assertEquals(
                            UnixLinefeedProcessor.class,
                            output.getPostprocessorDefinitions().get(1).getPostprocessor().getClass());
                }
            }
            {
                Output output = outputFiles.get(2);
                assertEquals(
                        new QualifiedName("thirdOutput"),
                        output.getName());
                assertEquals(
                        "append",
                        output.getExistingTargetStrategy());
                assertEquals(
                        "thirdOutputDirKey",
                        output.getOutputDirKey());
                assertEquals("outputFileName", output.getFilename());
                assertNull(output.getFilenameOutlet());

                {
                    OutletReference outletConfiguration
                    = output.getContentOutlet();
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.test.readConfiguration.thirdOutlet"),
                            outletConfiguration.getName());
                }

                {
                    JdbcMetadataSourceProvider sourceProvider
                        = new JdbcMetadataSourceProvider(
                            "jdbcUrl",
                            "jdbcDriver",
                            "jdbcUsername",
                            "jdbcPassword",
                            "jdbcSchema");
                    sourceProvider.init(
                            configurationHandlers,
                            controllerState);
                    output.getSourceProvider().init(
                            configurationHandlers,
                            controllerState);
                    assertJdbcMetadataSourceProviderEquals(
                            sourceProvider,
                            (JdbcMetadataSourceProvider) output.getSourceProvider());
                }
                assertSourceProcessConfigurationEquals(
                        new SourceProcessConfiguration(),
                        output.getSourceProcessConfiguration());
            }
        }

        {
            OutletConfiguration outletConfiguration
            = unitConfiguration.getOutletConfiguration();
            Map<QualifiedName, Outlet> outletMap
            = outletConfiguration.getOutlets();
            assertEquals(3, outletMap.size());
            {
                JavaOutlet outlet
                = (JavaOutlet) outletMap.get(new QualifiedName(
                        "org.apache.torque.generator.test.readConfiguration.javaOutlet"));
                assertEquals("Foo2", outlet.getFoo());
                assertEquals("Bar2", outlet.getBar());
                Map<String, MergepointMapping> mergepointMappings
                = outlet.getMergepointMappings();
                assertEquals(3, mergepointMappings.size());
                {
                    MergepointMapping mergepointMapping
                    = mergepointMappings.get("properties");
                    assertEquals(1, mergepointMapping.getActions().size());
                    MergepointAction action
                    = mergepointMapping.getActions().get(0);
                    assertEquals(
                            new TraverseAllAction(
                                    "entry",
                                    "org.apache.torque.generator.velocity.propertyCopy",
                                    true),
                            action);
                }
                {
                    // mergepoint from the separate mapping
                    MergepointMapping mergepointMapping
                    = mergepointMappings.get("mergepointName");
                    assertEquals(1, mergepointMapping.getActions().size());
                    MergepointAction action
                    = mergepointMapping.getActions().get(0);
                    assertEquals(
                            new ApplyAction(
                                    null,
                                    "someOutletAction",
                                    false),
                            action);
                }
                {
                    // other mergepoint from the separate mapping
                    MergepointMapping mergepointMapping
                    = mergepointMappings.get("mergepointFromParent");
                    assertEquals(1, mergepointMapping.getActions().size());
                    MergepointAction action
                    = mergepointMapping.getActions().get(0);
                    assertEquals(
                            new ApplyAction(
                                    null,
                                    "newOutletAction",
                                    true),
                            action);
                }
            }

            {
                VelocityOutlet outlet
                = (VelocityOutlet) outletMap.get(new QualifiedName(
                        "org.apache.torque.generator.test.readConfiguration.anotherOutlet"));
                String templateContent
                = outlet.getContent(controllerState);
                // remove License from template by comparing only
                // the last line
                String templateContentLicenseRemoved
                = StringUtils.substringAfterLast(templateContent, "\r\n");
                assertEquals(
                        "test template output",
                        templateContentLicenseRemoved);
                assertEquals(0, outlet.getMergepointMappings().size());
                assertFalse(outlet.isOptionsInContext());
                assertFalse(outlet.isSourceAttributesInContext());
                assertFalse(outlet.isVariablesInContext());
            }

            {
                VelocityOutlet outlet
                = (VelocityOutlet) outletMap.get(new QualifiedName(
                        "testTemplate"));
                String templateContent
                = outlet.getContent(controllerState);
                // remove License from template by comparing only
                // the last line
                String templateContentLicenseRemoved
                = StringUtils.substringAfterLast(templateContent, "\r\n");
                assertEquals(
                        "test template output",
                        templateContentLicenseRemoved);
                assertEquals(0, outlet.getMergepointMappings().size());
                assertTrue(outlet.isOptionsInContext());
                assertTrue(outlet.isSourceAttributesInContext());
                assertTrue(outlet.isVariablesInContext());
            }
        }
    }

    @RepeatedTest(value = 2, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("test read from classpath configuration")
    public void testReadConfigFromClasspath() throws Exception
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
        ConfigurationHandlers configurationHandlers
            = new ConfigurationHandlers();
        UnitConfigurationReader configurationReader
            = new UnitConfigurationReader();
        UnitConfiguration unitConfiguration
        = configurationReader.read(
                unitDescriptor,
                configurationHandlers);

        // check that we have read the correct configuration
        {
            Options options = unitConfiguration.getOptions();
            Option option  = options.getInHierarchy(
                    new QualifiedName("configuration"));
            assertNotNull(option, "option configuration should be set");
            assertEquals("fromClasspath", option.getValue());
        }

        // check that the outlets are read
        // two from explicit configuration and one from the templates
        {
            OutletConfiguration outletConfiguration
            = unitConfiguration.getOutletConfiguration();
            Map<QualifiedName, Outlet> outletMap
            = outletConfiguration.getOutlets();
            assertEquals(3, outletMap.size());
        }
    }

    @RepeatedTest(value = 2, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("test override configuration")
    public void testOverrideOptions() throws Exception
    {
        ProjectPaths projectPaths = new Maven2DirectoryProjectPaths(
                new File("src/test/configuration"));
        Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("optionWithoutNamespace", "overriddenValue");
        overrideOptions.put("newOption", "newValue");
        OptionsConfiguration optionConfiguration
            = new MapOptionsConfiguration(overrideOptions);

        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(optionConfiguration);
        ConfigurationHandlers configurationHandlers
            = new ConfigurationHandlers();
        UnitConfigurationReader configurationReader
            = new UnitConfigurationReader();
        UnitConfiguration unitConfiguration
        = configurationReader.read(
                unitDescriptor,
                configurationHandlers);
        Options options = unitConfiguration.getOptions();
        {
            Option option = options.getInHierarchy(
                    new QualifiedName("optionWithoutNamespace"));
            assertEquals("overriddenValue", option.getValue());
        }
        {
            Option option = options.getInHierarchy(
                    new QualifiedName("newOption"));
            assertEquals("newValue", option.getValue());
        }
    }


    @RepeatedTest(value = 2, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("test inheritance configuration")
    public void testInheritance() throws Exception
    {
        CustomProjectPaths projectPaths;
        UnitConfiguration unitConfiguration;
        ConfigurationHandlers configurationHandlers
            = new ConfigurationHandlers();
        UnitDescriptor unitDescriptor;
        {
            CustomProjectPaths parentProjectPaths
                = new CustomProjectPaths(
                    new Maven2DirectoryProjectPaths(
                            new File("src/test/configuration")));
            parentProjectPaths.setConfigurationDir(
                    new File("src/test/configuration/src/main/torque-gen-parent"));
            parentProjectPaths.setOutputDirectory(
                    null,
                    new File("src/test/configuration/target/parentCustom"));
            parentProjectPaths.setOutputDirectory(
                    "modifiable",
                    new File("src/test/configuration/src/main/parentCustom"));
            UnitDescriptor parentUnitDescriptor = new UnitDescriptor(
                    UnitDescriptor.Packaging.DIRECTORY,
                    parentProjectPaths,
                    new DefaultTorqueGeneratorPaths());

            projectPaths = new CustomProjectPaths(
                    new Maven2DirectoryProjectPaths(
                            new File("src/test/configuration")));
            projectPaths.setOutputDirectory(
                    null,
                    new File("src/test/configuration/target/custom"));
            projectPaths.setOutputDirectory(
                    "modifiable",
                    new File("src/test/configuration/src/main/custom"));
            unitDescriptor = new UnitDescriptor(
                    UnitDescriptor.Packaging.DIRECTORY,
                    projectPaths,
                    new DefaultTorqueGeneratorPaths());
            unitDescriptor.setInheritsFrom(parentUnitDescriptor);
            UnitConfigurationReader configurationReader
                = new UnitConfigurationReader();
            unitConfiguration
            = configurationReader.read(
                    unitDescriptor,
                    configurationHandlers);
        }

        assertEquals(Loglevel.DEBUG, unitConfiguration.getLoglevel());

        {
            File newFileTargetDirectory
            = unitConfiguration.getOutputDirectory(null);
            File expected = new File("src/test/configuration/target/custom");
            assertEquals(expected, newFileTargetDirectory);
        }
        {
            File modifiableFileTargetDirectory
            = unitConfiguration.getOutputDirectory("modifiable");
            File expected = new File("src/test/configuration/src/main/custom");
            assertEquals(expected, modifiableFileTargetDirectory);
        }

        {
            Options expectedOptions = new Options();
            Set<Option> expectedOptionSet = new HashSet<>();
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("org.apache.torque.generator.optionWithNamespace"),
                    "optionWithNamespaceValue"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("org.apache.optionWithNamespace"),
                    "org.apache.optionWithNamespaceValue"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("optionWithoutNamespace", ""),
                    "optionWithoutNamespaceValue"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("optionPrecedenceLastFile"),
                    "value from xml"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("jdbcUrl"),
                    "jdbc.url.option.value"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("jdbcDriver"),
                    "jdbc.driver.option.value"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("parentOptionWithoutNamespace", ""),
                    "parentOptionWithoutNamespaceParentValue"));
            expectedOptionSet.add(new OptionImpl(
                    new QualifiedName("org.apache.torque.generator.parentOptionWithNamespace"),
                    "parentOptionWithNamespaceParentValue"));
            expectedOptions.addGlobalOptions(expectedOptionSet);
            assertOptionsEquals(
                    expectedOptions,
                    unitConfiguration.getOptions());
        }

        {
            EntityReferences entityReferences
            = unitConfiguration.getEntityReferences();
            Map<String, byte[]> actual = entityReferences.getEntityReferences();
            assertEquals(3, actual.size());
            assertArrayEquals(
                    FileUtils.readFileToByteArray(
                            new File("src/test/configuration/src/main/torque-gen-parent/resources/parent.xsd")),
                    actual.get("http://db.apache.org/torque/parent.xsd"));
            assertArrayEquals(
                    FileUtils.readFileToByteArray(
                            new File("src/test/configuration/src/main/torque-gen/resources/override.xsd")),
                    actual.get("http://db.apache.org/torque/some.xsd"));
            assertArrayEquals(
                    FileUtils.readFileToByteArray(
                            new File("src/test/configuration/src/main/torque-gen/resources/new.xsd")),
                    actual.get("http://db.apache.org/torque/new.xsd"));
        }

        {
            List<Output> outputList = unitConfiguration.getOutputList();
            assertEquals(4, outputList.size());

            {
                Output output = outputList.get(0);
                assertEquals(
                        new QualifiedName("parentOutput"),
                        output.getName());
                assertEquals(
                        "replace",
                        output.getExistingTargetStrategy());
                assertEquals(
                        "parentOutputDirKeyFromParent",
                        output.getOutputDirKey());
                assertNull(output.getFilename());
                {
                    OutletReference outletConfiguration
                    = output.getContentOutlet();
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.test.readConfiguration.testParentOutlet"),
                            outletConfiguration.getName());
                }
                {
                    JavaOutlet filenameOutlet
                    = (JavaOutlet) output.getFilenameOutlet();
                    assertEquals("ParentFoo", filenameOutlet.getFoo());
                    assertEquals("ParentBar", filenameOutlet.getBar());
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.configuration.filenameOutlet"),
                            filenameOutlet.getName());
                }

                {
                    Fileset sourceFileset = new Fileset(
                            projectPaths.getDefaultSourcePath(),
                            createSetFrom("parentSource"),
                            new HashSet<String>());
                    FileSourceProvider sourceProvider = new FileSourceProvider(
                            new XmlSourceFormat(),
                            sourceFileset,
                            false);
                    output.getSourceProvider().init(
                            configurationHandlers,
                            controllerState);
                    sourceProvider.init(
                            configurationHandlers,
                            controllerState);
                    assertFileSourceProviderEquals(
                            sourceProvider,
                            (FileSourceProvider) output.getSourceProvider());
                }
                {
                    SourceProcessConfiguration sourceProcessConfiguration
                        = new SourceProcessConfiguration();
                    sourceProcessConfiguration.setStartElementsPath("parentSourceElement");
                    List<SourceTransformerDefinition> transformerDefinitions
                        = new ArrayList<>();

                    transformerDefinitions.add(
                            new SourceTransformerDefinition(
                                    new OtherConfigurationTestTransformer()));
                    sourceProcessConfiguration.setSourceTransformerDefinitions(
                            transformerDefinitions);
                    sourceProcessConfiguration.setSkipDecider(
                            "org.apache.torque.generator.configuration.OtherConfigurationTestSkipDecider",
                            unitDescriptor);
                    assertSourceProcessConfigurationEquals(
                            sourceProcessConfiguration,
                            output.getSourceProcessConfiguration());
                }
            }
            {
                Output output = outputList.get(1);
                assertEquals(
                        new QualifiedName("org.apache.torque.generator.firstOutput"),
                        output.getName());
                assertEquals(
                        "skip",
                        output.getExistingTargetStrategy());
                assertEquals(
                        null,
                        output.getOutputDirKey());
                assertNull(output.getFilename());

                {
                    OutletReference outletConfiguration
                    = output.getContentOutlet();
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.test.readConfiguration.javaOutlet"),
                            outletConfiguration.getName());
                }
                {
                    JavaOutlet filenameOutlet
                    = (JavaOutlet) output.getFilenameOutlet();
                    assertEquals("Foo", filenameOutlet.getFoo());
                    assertEquals("Bar", filenameOutlet.getBar());
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.configuration.filenameOutlet"),
                            filenameOutlet.getName());
                }

                {
                    Fileset sourceFileset = new Fileset(
                            projectPaths.getDefaultSourcePath(),
                            new HashSet<String>(),
                            new HashSet<String>());
                    FileSourceProvider sourceProvider = new FileSourceProvider(
                            new XmlSourceFormat(),
                            sourceFileset,
                            null);
                    sourceProvider.init(
                            configurationHandlers,
                            controllerState);
                    output.getSourceProvider().init(
                            configurationHandlers,
                            controllerState);
                    assertFileSourceProviderEquals(
                            sourceProvider,
                            (FileSourceProvider) output.getSourceProvider());
                }
                assertSourceProcessConfigurationEquals(
                        new SourceProcessConfiguration(),
                        output.getSourceProcessConfiguration());
            }

            {
                Output output = outputList.get(2);
                assertEquals(
                        new QualifiedName("secondOutput"),
                        output.getName());
                assertEquals(
                        "replace",
                        output.getExistingTargetStrategy());
                assertEquals(
                        output.getOutputDirKey(),
                        "secondOutputDirKey");
                assertNull(output.getFilename());

                {
                    OutletReference outletConfiguration
                    = output.getContentOutlet();
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.test.readConfiguration.anotherOutlet"),
                            outletConfiguration.getName());
                }
                {
                    VelocityOutlet filenameOutlet
                    = (VelocityOutlet) output.getFilenameOutlet();
                    String templateContent
                    = filenameOutlet.getContent(controllerState);
                    // remove License from template by comparing only
                    // the last line
                    String templateContentLicenseRemoved
                    = StringUtils.substringAfterLast(templateContent, "\r\n");
                    assertEquals(
                            "test template output",
                            templateContentLicenseRemoved);
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.configuration.filenameOutlet"),
                            filenameOutlet.getName());
                }

                {
                    Fileset sourceFileset = new Fileset(
                            projectPaths.getDefaultSourcePath(),
                            createSetFrom("second.source.path.properties"),
                            createSetFrom("second.excluded.properties"));
                    FileSourceProvider sourceProvider = new FileSourceProvider(
                            new PropertiesSourceFormat(),
                            sourceFileset,
                            true);
                    sourceProvider.init(
                            configurationHandlers,
                            controllerState);
                    output.getSourceProvider().init(
                            configurationHandlers,
                            controllerState);
                    assertFileSourceProviderEquals(
                            sourceProvider,
                            (FileSourceProvider) output.getSourceProvider());
                }
                {
                    SourceProcessConfiguration sourceProcessConfiguration
                        = new SourceProcessConfiguration();
                    sourceProcessConfiguration.setStartElementsPath("properties/entry");
                    List<SourceTransformerDefinition> transformerDefinitions
                        = new ArrayList<>();
                    transformerDefinitions.add(
                            new SourceTransformerDefinition(
                                    new ConfigurationTestTransformer()));
                    transformerDefinitions.add(
                            new SourceTransformerDefinition(
                                    new OtherConfigurationTestTransformer()));
                    sourceProcessConfiguration.setSourceTransformerDefinitions(
                            transformerDefinitions);
                    sourceProcessConfiguration.setSkipDecider(
                            "org.apache.torque.generator.configuration.ConfigurationTestSkipDecider",
                            unitDescriptor);
                    assertSourceProcessConfigurationEquals(
                            sourceProcessConfiguration,
                            output.getSourceProcessConfiguration());
                }
            }
            {
                Output output = outputList.get(3);
                assertEquals(
                        new QualifiedName("thirdOutput"),
                        output.getName());
                assertEquals(
                        "append",
                        output.getExistingTargetStrategy());
                assertEquals(
                        "thirdOutputDirKey",
                        output.getOutputDirKey());
                assertEquals("outputFileName", output.getFilename());
                assertNull(output.getFilenameOutlet());

                {
                    OutletReference outletConfiguration
                    = output.getContentOutlet();
                    assertEquals(
                            new QualifiedName(
                                    "org.apache.torque.generator.test.readConfiguration.thirdOutlet"),
                            outletConfiguration.getName());
                }

                {
                    JdbcMetadataSourceProvider sourceProvider
                        = new JdbcMetadataSourceProvider(
                            "jdbcUrl",
                            "jdbcDriver",
                            "jdbcUsername",
                            "jdbcPassword",
                            "jdbcSchema");
                    sourceProvider.init(
                            configurationHandlers,
                            controllerState);
                    output.getSourceProvider().init(
                            configurationHandlers,
                            controllerState);
                    assertJdbcMetadataSourceProviderEquals(
                            sourceProvider,
                            (JdbcMetadataSourceProvider) output.getSourceProvider());
                }
                assertSourceProcessConfigurationEquals(
                        new SourceProcessConfiguration(),
                        output.getSourceProcessConfiguration());
            }
        }

        {
            OutletConfiguration outletConfiguration
            = unitConfiguration.getOutletConfiguration();
            Map<QualifiedName, Outlet> outletMap
            = outletConfiguration.getOutlets();
            assertEquals(4, outletMap.size());
            {
                JavaOutlet outlet
                = (JavaOutlet) outletMap.get(new QualifiedName(
                        "org.apache.torque.generator.test.readConfiguration.javaOutlet"));
                assertEquals("Foo2", outlet.getFoo());
                assertEquals("Bar2", outlet.getBar());
                Map<String, MergepointMapping> mergepointMappings
                = outlet.getMergepointMappings();
                assertEquals(3, mergepointMappings.size());
                {
                    MergepointMapping mergepointMapping
                    = mergepointMappings.get("properties");
                    assertEquals(1, mergepointMapping.getActions().size());
                    MergepointAction action
                    = mergepointMapping.getActions().get(0);
                    assertEquals(
                            new TraverseAllAction(
                                    "entry",
                                    "org.apache.torque.generator.velocity.propertyCopy",
                                    true),
                            action);
                }
                {
                    // mergepoint from the separate mapping in child
                    MergepointMapping mergepointMapping
                    = mergepointMappings.get("mergepointName");
                    assertEquals(1, mergepointMapping.getActions().size());
                    MergepointAction action
                    = mergepointMapping.getActions().get(0);
                    assertEquals(
                            new ApplyAction(
                                    null,
                                    "someOutletAction",
                                    false),
                            action);
                }
                {
                    // other mergepoint from the separate mapping in child
                    MergepointMapping mergepointMapping
                    = mergepointMappings.get("mergepointFromParent");
                    assertEquals(1, mergepointMapping.getActions().size());
                    MergepointAction action
                    = mergepointMapping.getActions().get(0);
                    assertEquals(
                            new ApplyAction(
                                    null,
                                    "newOutletAction",
                                    true),
                            action);
                }
            }

            {
                VelocityOutlet outlet
                = (VelocityOutlet) outletMap.get(new QualifiedName(
                        "org.apache.torque.generator.test.readConfiguration.anotherOutlet"));
                String templateContent
                = outlet.getContent(controllerState);
                // remove License from template by comparing only
                // the last line
                String templateContentLicenseRemoved
                = StringUtils.substringAfterLast(templateContent, "\r\n");
                assertEquals(
                        "test template output",
                        templateContentLicenseRemoved);
                assertEquals(0, outlet.getMergepointMappings().size());
            }
            {
                VelocityOutlet outlet
                = (VelocityOutlet) outletMap.get(new QualifiedName(
                        "org.apache.torque.generator.test.readConfiguration.parentOutlet"));
                String templateContent
                = outlet.getContent(controllerState);
                // remove License from template by comparing only
                // the last line
                String templateContentLicenseRemoved
                = StringUtils.substringAfterLast(templateContent, "\r\n");
                assertEquals(
                        "parent test template output",
                        templateContentLicenseRemoved);
                assertEquals(0, outlet.getMergepointMappings().size());
            }
            {
                VelocityOutlet outlet
                = (VelocityOutlet) outletMap.get(new QualifiedName(
                        "testTemplate"));
                String templateContent
                = outlet.getContent(controllerState);
                // remove License from template by comparing only
                // the last line
                String templateContentLicenseRemoved
                = StringUtils.substringAfterLast(templateContent, "\r\n");
                assertEquals(
                        "test template output",
                        templateContentLicenseRemoved);
                assertEquals(0, outlet.getMergepointMappings().size());
            }
        }
    }

    private static void assertFileSourceProviderEquals(
            final FileSourceProvider expected,
            final FileSourceProvider actual)
    {
        assertEquals(
                expected.getSourceFormat(),
                actual.getSourceFormat());
        assertEquals(
                expected.getSourceFileset().getIncludes(),
                actual.getSourceFileset().getIncludes());
        assertEquals(
                expected.getSourceFileset().getExcludes(),
                actual.getSourceFileset().getExcludes());
        assertEquals(
                expected.getSourceFileset().getBasedir(),
                actual.getSourceFileset().getBasedir());
        assertEquals(
                expected.getPaths(),
                actual.getPaths());
        assertEquals(
                expected.getCombineFiles(),
                actual.getCombineFiles());
    }

    private static void assertJdbcMetadataSourceProviderEquals(
            final JdbcMetadataSourceProvider expected,
            final JdbcMetadataSourceProvider actual)
    {
        assertEquals(
                expected.getDriverOption(),
                actual.getDriverOption());
        assertEquals(
                expected.getPasswordOption(),
                actual.getPasswordOption());
        assertEquals(
                expected.getSchemaOption(),
                actual.getSchemaOption());
        assertEquals(
                expected.getUrlOption(),
                actual.getUrlOption());
        assertEquals(
                expected.getUsernameOption(),
                actual.getUsernameOption());
        assertEquals(
                expected.getDriver(),
                actual.getDriver());
        assertEquals(
                expected.getPassword(),
                actual.getPassword());
        assertEquals(
                expected.getSchema(),
                actual.getSchema());
        assertEquals(
                expected.getUrl(),
                actual.getUrl());
        assertEquals(
                expected.getUsername(),
                actual.getUsername());
    }

    private static void assertOptionsEquals(final Options expected, final Options actual)
    {
        Map<QualifiedName, Option> expectedMap
        = expected.getGlobalScope();
        Map<QualifiedName, Option> actualMap
        = actual.getGlobalScope();
        assertEquals(expectedMap.size(), actualMap.size());

        for (QualifiedName expectedKey : expectedMap.keySet())
        {
            assertTrue(expectedMap.containsKey(expectedKey));
            Object expectedValue = expectedMap.get(expectedKey).getValue();
            Object actualValue = actualMap.get(expectedKey).getValue();
            assertEquals(expectedValue, actualValue);
        }
    }

    private void assertSourceProcessConfigurationEquals(
            final SourceProcessConfiguration expected,
            final SourceProcessConfiguration actual)
    {
        if (expected.getSkipDecider() != null)
        {
            assertEquals(
                    expected.getSkipDecider().getClass(),
                    actual.getSkipDecider().getClass());
        }
        else
        {
            assertNull(actual.getSkipDecider());
        }
        assertEquals(
                expected.getStartElementsPath(),
                actual.getStartElementsPath());
        assertEquals(
                expected.getTransformerDefinitions(),
                actual.getTransformerDefinitions());
    }

    /**
     * Creates as set containing the Strings in content.
     *
     * @param content The Strings which should be in the set.
     *
     * @return the Set containing all the strings.
     */
    private static Set<String> createSetFrom(final String... content)
    {
        Set<String> result = new HashSet<>();
        for (String part : content)
        {
            result.add(part);
        }
        return result;
    }
}
