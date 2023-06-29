package org.apache.torque.ant.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.ElementSelectors;

public class TorqueGeneratorTaskTest 
{
    
    @Test
    public void testExecute() throws Exception
    {
        File target = new File("target/tests/torqueGeneratorTaskTest");
        FileUtils.deleteDirectory(target);
        TorqueGeneratorTask task = new TorqueGeneratorTask();
        task.setPackaging("directory");
        task.setProjectRootDir(new File("src/test/torqueGeneratorTaskTest"));
        task.setDefaultOutputDir(target);
        task.execute();

        assertTrue(target.exists());
        File generatedJavaFile = new File(
                target,
                "org/apache/torque/ant/PropertyKeys.java");
        assertTrue(generatedJavaFile.exists());
        File expectedJavaFile = new File(
                "src/test/resources/org/apache/torque/ant/PropertyKeys.java");
        
        assertTrue(FileUtils.contentEquals(generatedJavaFile, expectedJavaFile), "The files differ!");

    }
    
    /**
     * compare org.apache.torque.templates.jdbc2schema.Jdbc2SchemaTest.java in torque-templates
     * 
     * @throws Exception
     */
    @Test
    public void testJDBC2SchemaExecute() throws Exception
    {
        //
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            BasicConfigurator.configure();
            Connection connection = DriverManager.getConnection("jdbc:derby:memory:myDb;create=true");
            InputStream inputStream = getClass().getResourceAsStream(
                    "jdbc2schemaTest.sql");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "iso-8859-1"));
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
        
        File target = new File("target/generated-schema");
        FileUtils.deleteDirectory(target);
        Torque4JDBCTransformTask task = new Torque4JDBCTransformTask();
        task.setDbUrl("jdbc:derby:memory:myDb;create=true");
        task.setDbDriver("org.apache.derby.jdbc.EmbeddedDriver");
        
        task.setPackaging("directory");
        //task.setProjectRootDir(new File("src/test/torqueJDBC2SchemaTaskTest/src/main/jdbc2schema"));        
        //task.setPackaging("classpath");
        //task.setConfigPackage("org.apache.torque.templates.jdbc2schema");
        
        task.setProjectRootDir(new File("src/test/torqueJDBC2SchemaTaskTest"));
        task.setDefaultOutputDir(target);
        try {
            task.execute();
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }

        assertTrue(target.exists());
        File generatedSchemaFile = new File(
                target,
                "schema.xml");
        assertTrue(generatedSchemaFile.exists());
        File expectedSchemaFile = new File(
               // "src/test/schema/schema.xml"
                "src/test/schema/expected-schema.xml"
                );
        
        Diff myDiffSimilar = DiffBuilder.compare(expectedSchemaFile).ignoreComments().withTest(generatedSchemaFile)
                .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName))
                .checkForSimilar()
                .build();
        assertFalse(myDiffSimilar.hasDifferences(), "The files differ! " + myDiffSimilar.toString());

    }
}
