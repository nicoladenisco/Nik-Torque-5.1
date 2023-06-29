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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.configuration.paths.Maven2ProjectPaths;
import org.apache.torque.generator.control.Controller;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.source.SourceProvider;
import org.apache.torque.generator.source.stream.FileSourceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


public class TestProcessing
{
    private static final String SCHEMA_DIR = "src/test/schema";

    @BeforeEach
    public void setUp() throws Exception
    {
        final String testRoot = "target";
//        {
//            final File generatedDocDir = new File(testRoot, "generated-docs");
//            FileUtils.deleteDirectory(generatedDocDir);
//        }
        {
            final File generatedSourcesDir = new File(testRoot, "generated-sources");
            FileUtils.deleteDirectory(generatedSourcesDir);
        }
        {
            final File generatedSources2Dir
                = new File(testRoot, "generated-sources-2");
            FileUtils.deleteDirectory(generatedSources2Dir);
        }
        {
            final File generatedSqlDir = new File(testRoot, "generated-sql");
            deleteFilesInDirectory(generatedSqlDir);
        }
//        {
//            final File generatedXdocsDir = new File(testRoot, "generated-xdocs");
//            deleteDirectory(generatedXdocsDir);
//        }
    }

    @Test
    public void testOmTemplates() throws Exception
    {
        final Controller controller = new Controller();
        final List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        final Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put(TemplateOptionName.OM_PACKAGE.getName(), "org.apache.torque.test");
        overrideOptions.put(TemplateOptionName.OM_GENERATE_MAP_INIT.getName(), "true");
            
        overrideOptions.put(TemplateOptionName.OM_GENERATE_BEANS.getName(), "true");
        //overrideOptions.put(TemplateOptionName.OM_USE_MANAGERS.getName(), "true");

        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(new File(".")));
        projectPaths.setConfigurationDir(
                new File("src/main/resources/org/apache/torque/templates/om"));
        projectPaths.setSourceDir(
                new File(SCHEMA_DIR));
        projectPaths.setOutputDirectory(
                Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                new File("target/generated-sources-2"));
        final Fileset sourceFileset = new Fileset();
        final Set<String> sourceIncludes = new HashSet<>();
        sourceIncludes.add("schema.xml");
        sourceFileset.setIncludes(sourceIncludes);
        sourceFileset.setBasedir(projectPaths.getDefaultSourcePath());
        final SourceProvider sourceProvider
            = new FileSourceProvider(
                null,
                sourceFileset,
                null);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideSourceProvider(sourceProvider);
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));
        unitDescriptors.add(unitDescriptor);
        
//      Options options = controllerState.getUnitConfiguration().getOptions();
//      log.trace( "uc.options:"+ options );
//      
//      Option omGenerateBeans = new OptionImpl(
//              new QualifiedName(TemplateOptionName.OM_GENERATE_BEANS.getName()),
//              true);
//      options.setGlobalOption( omGenerateBeans );

        // generate
        controller.run(unitDescriptors);

        // TODO assert output
    }

    @Test
    public void testSqlCreatedbTemplates() throws Exception
    {
        File generationFolder = new File("target/generated-sql");
        File generationFolder2 = new File("target/generated-sql-2");
        deleteFilesInDirectory(generationFolder);
        deleteFilesInDirectory(generationFolder2);

        final Controller controller = new Controller();
        final List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        final Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("torque.database", "mysql");
        overrideOptions.put("torque.om.package", "org.apache.torque.test");
        overrideOptions.put("torque.om.generateMapInit", "true");

        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(new File(".")));
        projectPaths.setConfigurationDir(
                new File("src/main/resources/org/apache/torque/templates/sql/createdb"));
        projectPaths.setSourceDir(new File(SCHEMA_DIR));
        projectPaths.setOutputDirectory(null, generationFolder);
        projectPaths.setOutputDirectory(
                Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                generationFolder2);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));
        unitDescriptors.add(unitDescriptor);

        // generate
        controller.run(unitDescriptors);

        assertOutput(
                "expected-schema-create.sql",
                "target/generated-sql/schema-create.sql");
        assertOutput(
                "expected-ext-schema-create.sql",
                "target/generated-sql/ext-schema-create.sql");
        assertOutput(
                "expected-extext-schema-create.sql",
                "target/generated-sql/extext-schema-create.sql");
        assertFalse(generationFolder2.exists());
    }
    

    @Test
    public void testIdtableTemplates() throws Exception
    {
        File generationFolder = new File("target/generated-sql");
        File generationFolder2 = new File("target/generated-sql-2");
        deleteFilesInDirectory(generationFolder);
        deleteFilesInDirectory(generationFolder2);

        final Controller controller = new Controller();
        final List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        final Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("torque.database", "mysql");

        // idbroker-init-sql templates
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(new File(".")));
        projectPaths.setConfigurationDir(
                new File("src/main/resources/org/apache/torque/templates/idtable"));
        projectPaths.setSourceDir(new File(SCHEMA_DIR));
        projectPaths.setOutputDirectory(null, generationFolder);
        projectPaths.setOutputDirectory(
                Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                generationFolder2);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));
        unitDescriptors.add(unitDescriptor);

        // generate
        controller.run(unitDescriptors);

        assertOutput(
                "expected-schema-idtable-init.sql",
                "target/generated-sql/schema-idtable-init.sql");
        assertOutput(
                "expected-ext-schema-idtable-init.sql",
                "target/generated-sql/ext-schema-idtable-init.sql");
        assertOutput(
                "expected-extext-schema-idtable-init.sql",
                "target/generated-sql/extext-schema-idtable-init.sql");
        assertFalse(generationFolder2.exists());
    }

    /**
     * Ignore until
     *
     * https://issues.apache.org/jira/projects/TORQUE/issues/TORQUE-358?filter=allopenissues
     * is resolved
     *
     * @throws Exception
     */
//    @Disabled
    @Test
    public void testHtmlDocTemplates() throws Exception
    {
        File generationFolder = new File("target/generated-docs");
        File generationFolder2 = new File("target/generated-docs-2");

        deleteDirectory(generationFolder);
        deleteDirectory(generationFolder2);

        final Controller controller = new Controller();
        final List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        final Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("torque.om.package", "org.apache.torque.test");
        overrideOptions.put("torque.om.generateMapInit", "true");

        // html doc templates
        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(new File(".")));
        projectPaths.setConfigurationDir(
                new File("src/main/resources/org/apache/torque/templates/doc/html"));
        projectPaths.setSourceDir(new File(SCHEMA_DIR));
        projectPaths.setOutputDirectory(null, generationFolder);
        projectPaths.setOutputDirectory(
                Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                generationFolder2);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));
        unitDescriptors.add(unitDescriptor);

        // generate
        controller.run(unitDescriptors);

        assertOutput(
                "expected-datamodel.css",
                "target/generated-docs/datamodel.css");
        assertOutput(
                "expected-schema.html",
                "target/generated-docs/schema.html");
        assertOutput(
                "expected-ext-schema.html",
                "target/generated-docs/ext-schema.html");
        assertOutput(
                "expected-extext-schema.html",
                "target/generated-docs/extext-schema.html");
        assertFalse(generationFolder2.exists());
    }

    /**
     * Ignore until
     *
     * https://issues.apache.org/jira/projects/TORQUE/issues/TORQUE-358?filter=allopenissues
     * is resolved
     *
     * @throws Exception
     */
    @Test
    public void testXdocTemplates() throws Exception
    {
        File generationFolder = new File("target/generated-xdocs");
        File generationFolder2 = new File("target/generated-xdocs-2");
        
        deleteDirectory(generationFolder);
        deleteDirectory(generationFolder2);

        final Controller controller = new Controller();
        final List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        final Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("torque.om.package", "org.apache.torque.test");

        CustomProjectPaths projectPaths = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(new File(".")));
        projectPaths.setConfigurationDir(
                new File("src/main/resources/org/apache/torque/templates/doc/xdoc"));
        projectPaths.setSourceDir(new File(SCHEMA_DIR));
        projectPaths.setOutputDirectory(null, generationFolder);
        projectPaths.setOutputDirectory(
                Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                generationFolder2);
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptors.add(unitDescriptor);
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));

        // generate
        controller.run(unitDescriptors);

        assertOutput(
                "expected-datamodel.xml",
                "target/generated-xdocs/datamodel.xml");
        assertFalse(generationFolder2.exists());
    }

    @ParameterizedTest //  "mysql", "oracle", "postgresql", "hsqldb", "derby",
    //@ValueSource(strings = { "mysql" } ) 
    @ValueSource(strings = { "mysql", "oracle", "postgresql", "hsqldb", "derby", "mssql" } )
    public void testSqlDdlTemplates(String dbType) throws Exception
    {
        File generationFolder = new File("target/generated-sql");
        File generationFolder2 = new File("target/generated-sql-2");
        
        deleteFilesInDirectory(generationFolder);
        deleteFilesInDirectory(generationFolder2);

        final Controller controller = new Controller();
        final List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        final Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("torque.database", dbType);

        final CustomProjectPaths projectPaths
            = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(new File(".")));
        projectPaths.setConfigurationDir(
                new File("src/main/resources/org/apache/torque/templates/sql"));
        projectPaths.setSourceDir(new File(SCHEMA_DIR));
        projectPaths.setOutputDirectory(null, generationFolder);
        projectPaths.setOutputDirectory(
                Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                generationFolder2);
        final UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));
        unitDescriptors.add(unitDescriptor);

        // generate
        controller.run(unitDescriptors);

        assertOutput("expected-schema-"+dbType+".sql", "target/generated-sql/schema.sql");
        assertFalse(generationFolder2.exists());
    }

    private void assertOutput(
            final String expectedFilename,
            final String actualFilename)
                    throws IOException
    {
        Charset charset = StandardCharsets.ISO_8859_1;
        String expected = IOUtils.toString(
                getClass().getResourceAsStream(expectedFilename),
                charset);
        // remove license header
        int licenseHeaderEnd = expected.lastIndexOf("##\n");
        if (licenseHeaderEnd != -1)
        {
            expected = expected.substring(licenseHeaderEnd + 3);
        }
        else
        {
            expected = expected.substring(expected.lastIndexOf("##\r\n") + 4);
        }

//        final String actual = FileUtils.readFileToString(
//                new File(actualFilename),
//                charset);
        // auto close file input stream
        boolean isEqual = false; // delay assert
        try (Reader actualReader  = new InputStreamReader(new FileInputStream(actualFilename), charset) ) {
            isEqual = 
                IOUtils.contentEqualsIgnoreEOL(new InputStreamReader(IOUtils.toInputStream(expected, charset)),
                        actualReader) ;
        }
        // read again to get details
        if (!isEqual) {
            try (Reader actualReader  = new InputStreamReader(new FileInputStream(actualFilename), charset) ) {
                //check lines
                List<String> genSchema = IOUtils.readLines( actualReader );
                int lastLineIndex = 0, lineNr = 0;
                for (String line : genSchema) {
                    lineNr++;
                    assertTrue(expected.contains(line), actualFilename + "(line: "+ lineNr + "):\r\n'" + line + "'\r\nis not in expected file: "+ expectedFilename + " after:\r\n" +  expected.substring(lastLineIndex));
                    lastLineIndex =  expected.indexOf(line);// is found
                }
            }  
        }
    }
    
    private void deleteDirectory(final File generatedDir) throws IOException {
        try {
            FileUtils.deleteDirectory(generatedDir);
        } catch (Exception e) {
            Files.walk(generatedDir.toPath())
            .filter(f -> f.toFile().isFile())
            .forEach(file  -> {
               try {
                Files.delete(file);
               } catch (IOException e1) {
                   throw new RuntimeException(e1);
               } 
            });
            //Files.delete();
        }
    }
    
    private void deleteFilesInDirectory(final File generatedDir) throws IOException {
        if (!generatedDir.exists()) {
            return;
        }
        Files.walk(generatedDir.toPath())
        .filter(f -> f.toFile().isFile())
        .forEach(file  -> {
           try {
            Files.delete(file);
           } catch (IOException e1) {
               throw new RuntimeException(e1);
           } 
        });
    }
}
