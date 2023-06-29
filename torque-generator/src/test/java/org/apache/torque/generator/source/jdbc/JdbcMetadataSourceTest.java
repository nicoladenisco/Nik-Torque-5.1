package org.apache.torque.generator.source.jdbc;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.torque.generator.source.SourceElement;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JdbcMetadataSourceTest
{
    private static Logger logger
    = Logger.getLogger(JdbcMetadataSourceTest.class);

    private static String SQL_FILENAME = "jdbcMetadataSourceTest.sql";

    private static String SQL_CHARSET = "iso-8859-1";

    private static String URL = "jdbc:derby:memory:myDb;create=true";

    private static String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        Class.forName(DRIVER).newInstance();
    }

    @Before
    public void setUp() throws Exception
    {
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
                logger.info(e.getMessage());
            }
            statement.close();
        }
        connection.close();
    }

    @Test
    public void testExecute() throws Exception
    {
        JdbcMetadataSource source
            = new JdbcMetadataSource(DRIVER, URL, null, null, null);
        SourceElement rootElement = source.createRootElement();
        assertEquals("database", rootElement.getName());
        List<SourceElement> tableElements = rootElement.getChildren();
        assertEquals(2, tableElements.size());
        for (SourceElement tableElement : tableElements)
        {
            assertEquals("table", tableElement.getName());
            Object tableName = tableElement.getAttribute("name");
            if ("AUTHOR".equals(tableName))
            {
                List<SourceElement> columnElements
                = tableElement.getChildren();
                assertEquals(2, columnElements.size());
                for (SourceElement columnElement : columnElements)
                {
                    assertEquals("column", columnElement.getName());
                    Object columnName = columnElement.getAttribute("name");
                    if ("AUTHOR_ID".equals(columnName))
                    {
                        assertEquals(
                                "true",
                                columnElement.getAttribute("primaryKey"));
                        assertNull(columnElement.getAttribute("required"));
                        assertEquals(
                                "INTEGER",
                                columnElement.getAttribute("type"));
                        assertNull(columnElement.getAttribute("size"));
                        assertNull(columnElement.getAttribute("scale"));
                        // do not check default value as it is currently
                        // not generated correctly
                        // assertNull(columnElement.getAttribute("default"));
                    }
                    else if ("NAME".equals(columnName))
                    {
                        assertNull(columnElement.getAttribute("primaryKey"));
                        assertEquals(
                                "true",
                                columnElement.getAttribute("required"));
                        assertEquals(
                                "VARCHAR",
                                columnElement.getAttribute("type"));
                        assertEquals(
                                "50",
                                columnElement.getAttribute("size"));
                        assertNull(columnElement.getAttribute("scale"));
                        assertNull(columnElement.getAttribute("default"));
                    }
                    else
                    {
                        fail("Unknown column : " + columnName);
                    }
                }
            }
            else if ("BOOK".equals(tableName))
            {
                assertEquals(5, tableElement.getChildren().size());
                List<SourceElement> columnElements
                = tableElement.getChildren("column");
                assertEquals(4, columnElements.size());
                for (SourceElement columnElement : columnElements)
                {
                    assertEquals("column", columnElement.getName());
                    Object columnName = columnElement.getAttribute("name");
                    if ("BOOK_ID".equals(columnName))
                    {
                        assertEquals(
                                "true",
                                columnElement.getAttribute("primaryKey"));
                        assertNull(columnElement.getAttribute("required"));
                        assertEquals(
                                "INTEGER",
                                columnElement.getAttribute("type"));
                        assertNull(columnElement.getAttribute("size"));
                        assertNull(columnElement.getAttribute("scale"));
                        // do not check default value as it is currently
                        // not generated correctly
                        // assertNull(columnElement.getAttribute("default"));
                    }
                    else if ("ISBN".equals(columnName))
                    {
                        assertNull(columnElement.getAttribute("primaryKey"));
                        assertNull(columnElement.getAttribute("required"));
                        assertEquals(
                                "VARCHAR",
                                columnElement.getAttribute("type"));
                        assertEquals(
                                "15",
                                columnElement.getAttribute("size"));
                        assertNull(columnElement.getAttribute("scale"));
                        assertNull(columnElement.getAttribute("default"));
                    }
                    else if ("AUTHOR_ID".equals(columnName))
                    {
                        assertNull(columnElement.getAttribute("primaryKey"));
                        assertEquals(
                                "true",
                                columnElement.getAttribute("required"));
                        assertEquals(
                                "INTEGER",
                                columnElement.getAttribute("type"));
                        assertNull(columnElement.getAttribute("size"));
                        assertNull(columnElement.getAttribute("scale"));
                        assertNull(columnElement.getAttribute("default"));
                    }
                    else if ("TITLE".equals(columnName))
                    {
                        assertNull(columnElement.getAttribute("primaryKey"));
                        assertEquals(
                                "true",
                                columnElement.getAttribute("required"));
                        assertEquals(
                                "VARCHAR",
                                columnElement.getAttribute("type"));
                        assertEquals(
                                "255",
                                columnElement.getAttribute("size"));
                        assertNull(columnElement.getAttribute("scale"));
                        assertEquals(
                                "no title",
                                columnElement.getAttribute("default"));
                    }
                    else
                    {
                        fail("Unknown column : " + columnName);
                    }
                }
                List<SourceElement> foreignKeyElements
                = tableElement.getChildren("foreign-key");
                assertEquals(1, foreignKeyElements.size());
                SourceElement foreignKeyElement = foreignKeyElements.get(0);
                assertEquals(
                        "AUTHOR",
                        foreignKeyElement.getAttribute("foreignTable"));
            }
            else
            {
                fail("Unknown table : " + tableName);
            }
        }
        //
        //        String xml = new SourceToXml().toXml(rootElement, false);
        //        System.out.println(xml);
    }
}
