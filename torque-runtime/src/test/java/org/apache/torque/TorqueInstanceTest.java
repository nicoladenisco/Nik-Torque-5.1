package org.apache.torque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

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

import java.util.Map;

import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.PostgresAdapter;
import org.apache.torque.dsfactory.DataSourceFactory;


/**
 * Tests the TorqueInstance Class.
 *
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: TorqueInstanceTest.java 1886097 2021-02-01 09:46:04Z gk $
 */
public class TorqueInstanceTest extends BaseTestCase
{
    /** The name of the "default" dataSourceFactory. */
    private static final String DEFAULT_NAME = "default";

    /** The name of the "postgresql" dataSourceFactory */
    private static final String POSTGRESQL_NAME = "postgresql";

    /** The name of the "mysql" dataSourceFactory */
    private static final String MYSQL_NAME = "mysql";

    /** The name of the "oracle" dataSourceFactory */
    private static final String ORACLE_NAME = "oracle";

    /**
     * Tests whether an external adapter is loaded correctly.
     * @throws Exception if an error occurs during the Test.
     */
    @Test
    public void testExternalAdapter() throws Exception
    {
        Adapter adapter = Torque.getDatabase(POSTGRESQL_NAME).getAdapter();
        assertNotNull(adapter);
        assertTrue(adapter instanceof PostgresAdapter);
    }

    /**
     * Checks whether a DataSourceFactory with the name
     * <code>DEFAULT_NAME</code> is defined. (TRQS 322)
     * @throws Exception if an error occurs during the Test.
     */
    @Test
    public void testDefaultDataSourceFactory() throws Exception
    {
        DataSourceFactory defaultDataSourceFactory
        = Torque.getInstance().getDataSourceFactory(DEFAULT_NAME);
        assertNotNull( defaultDataSourceFactory,
                        "The DataSourceFactory for Key "
                                        + DEFAULT_NAME
                                        + " should not be null");
        DataSourceFactory postgresqlDataSourceFactory
        = Torque.getInstance().getDataSourceFactory(POSTGRESQL_NAME);
        assertSame( defaultDataSourceFactory,
                postgresqlDataSourceFactory,
                "The default DataSourceFactory "
                                + "and the postgresql DataSourceFactory "
                                + "are not the same object");
    }

    /**
     * Tests whether the databaseInfo objects are filled correctly.
     * @throws Exception if an error occurs during the Test.
     */
    @Test
    public void testDatabases() throws Exception
    {
        Map<String, Database> databases = Torque.getDatabases();
        // check whether all expected databases are contained in the Map
        assertEquals(4, databases.size(),
                        "Databases should contain 4 Databases, not "
                                        + databases.size());

        // check that the default database and the turbine database
        // refer to the same object
        Database defaultDatabase = Torque.getDatabase(DEFAULT_NAME);
        Database postgresqlDatabase = Torque.getDatabase(POSTGRESQL_NAME);

        assertNotSame( defaultDatabase,
                postgresqlDatabase,
                "The default database and the turbine database "
                                + "are the same object");
    }

    /**
     * Tests whether the schema information in the databases is filled
     * correctly.
     */
    @Test
    public void testSchemata() throws Exception
    {
        Torque.shutdown();
        Torque.init("src/test/resources/torque-schematest.properties");

        Map<String, Database> databases = Torque.getDatabases();

        assertEquals(
                "myschema",
                databases.get(MYSQL_NAME).getSchema());
        assertEquals(
                "defaultschema",
                databases.get(POSTGRESQL_NAME).getSchema());
        assertEquals(
               "orschema",
               databases.get(ORACLE_NAME).getSchema());
    }

    @Test
    public void testShutdown() throws Exception
    {
        // because we have not properly initialized the DataSourceFactory,
        // closing the DatasourceFactory down would result in an error.
        // So we have to remove the reference to the DatasourceFactory.
        Torque.getDatabase(POSTGRESQL_NAME).setDataSourceFactory(null);

        Torque.shutdown();
        assertFalse( Torque.isInit(),
                     "Torque.isInit() should return false after shutdown");
        try
        {
            Torque.getDatabases();
            fail("Torque.getDatabases() should throw an Exception "
                    + "after shutdown");
        }
        catch (TorqueException e)
        {
            assertEquals(e.getMessage(),
                         "Torque is not initialized.");
        }
    }
}
