package org.apache.torque.templates.jdbc2schema;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.control.Controller;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

public class Jdbc2SchemaTest /* implements TestExecutionExceptionHandler */
{
    private static Logger log
    = LogManager.getLogger(Jdbc2SchemaTest.class);

    private static String SQL_FILENAME = "jdbc2schemaTest.sql";

    private static String SQL_CHARSET = "iso-8859-1";

    private static String URL = "jdbc:derby:memory:myDb;create=true";

    private static String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    @BeforeAll
    public static void beforeClass() throws Exception
    {
        Class.forName(DRIVER).newInstance();
    }

    @BeforeEach
    public void setUp() 
    {
        try {
            BasicConfigurator.configure();
            Connection connection = DriverManager.getConnection(URL);
            InputStream inputStream = getClass().getResourceAsStream(SQL_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, SQL_CHARSET));
            String line;
            StringBuilder queryBuffer = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null)
            {
                if (line.trim().length() == 0 || line.trim().startsWith("--"))
                {
                    continue;
                }
                queryBuffer.append(line);
            }
            StringTokenizer tokenizer
                = new StringTokenizer(queryBuffer.toString(), ";", false);
            while (tokenizer.hasMoreTokens())
            {
                String sqlQuery = tokenizer.nextToken();
                Statement statement = connection.createStatement();
                try
                {
                    statement.execute(sqlQuery);
                }
                catch (SQLException e)
                {
                    log.info(e.getMessage(),e);
                    fail();
                }
                statement.close();
            }
            connection.close();

            String testRoot = "target";
            {
                File generatedSchemaDir = new File(testRoot, "generated-schema");
                FileUtils.deleteDirectory(generatedSchemaDir);
            }
        } catch (SQLException | IOException e) {
            fail();
        }
    }

    @Test
    public void testExecute()
    {
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
        Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("torque.jdbc2schema.url", URL);
        overrideOptions.put("torque.jdbc2schema.driver", DRIVER);

        CustomProjectPaths projectPaths
            = new CustomProjectPaths(
                new Maven2DirectoryProjectPaths(new File(".")));
        projectPaths.setConfigurationDir(
                new File("src/main/resources/org/apache/torque/templates/jdbc2schema"));
        projectPaths.setOutputDirectory(
                null,
                new File("target/generated-schema"));
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                UnitDescriptor.Packaging.DIRECTORY,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));
        unitDescriptors.add(unitDescriptor);

        try {
            controller.run(unitDescriptors);

            File generatedFile = new File("target/generated-schema/schema.xml");
            assertTrue(generatedFile.exists());
            String result = FileUtils.readFileToString(generatedFile, StandardCharsets.UTF_8);
            File referenceFile = new File(
                    "src/test/resources/org/apache/torque/templates/jdbc2schema/expected-schema.xml");
            String reference = FileUtils.readFileToString(referenceFile, StandardCharsets.UTF_8);
            
            Diff myDiffSimilar = DiffBuilder.compare(reference).ignoreComments().withTest(result)
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                    .checkForSimilar()
                    .build();
            assertFalse(myDiffSimilar.hasDifferences(), "XML similar " + myDiffSimilar.toString());

            
            Diff myDiffIdentical = DiffBuilder.compare(reference).ignoreComments().withTest(result)
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                    .checkForIdentical()
                    .build();
            assertFalse(myDiffIdentical.hasDifferences(), "XML identical " + myDiffIdentical.toString());
            
            //XMLAssert.assertXMLEqual(reference, result);
        } catch (GeneratorException e) {
            fail();
        } catch (IOException e) {
            fail();
        } 
        
    }

//    @Override
//    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
//        throw throwable;
//    }
}
