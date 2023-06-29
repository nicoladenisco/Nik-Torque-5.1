package org.apache.torque;

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

import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.dsfactory.AbstractDataSourceFactory;
import org.apache.torque.dsfactory.DataSourceFactory;
import org.apache.torque.dsfactory.JndiDataSourceFactory;
import org.apache.torque.test.peer.BookPeer;
import org.junit.jupiter.api.BeforeEach;

import junit.framework.TestCase;

/**
 * Test for configuration using JNDI. The test assumes that we have a
 * non-jndi configuration, and creates the data source configuration from it.
 * It is tested whether we can bind a data source manually into jndi and
 * retrieve it in Torque, and also it is tested whether we can bind a
 * data source using Torque.
 * Note: This test does not extend BaseRuntimeTestCase as we do not want Torque
 * to be initialized.
 *
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id $
 */
public class JndiConfigurationTest extends TestCase
{
    private static Log logger = LogFactory.getLog(JndiConfigurationTest.class);
    /**
     * The subcontext where the data source is bound.
     */
    protected static final String JNDI_SUBCONTEXT = "jdbc";

    /**
     * the full jndi path to the data source.
     */
    protected static final String JNDI_PATH
    = JNDI_SUBCONTEXT + "/" + "jndiTestDataSource";

    @BeforeEach
    public void setUp()
    {
        // super.setUp() initializes torque, but here we want to
        // do that ourselves
    }

    /**
     * Tests whether our home-made Data Source works.
     * @throws Exception if the test fails.
     */
    public void testDataSource() throws Exception
    {
        BasicDataSource dataSource = null;
        try
        {
            dataSource = getDataSource();
            dataSourceConnect(dataSource);
        }
        finally
        {
            if (dataSource != null)
            {
                dataSource.close();
            }
        }
    }

    /**
     * Binds a DataSource to the jndi and checks that we have successfully
     * bound it. Then Torque is configured to lookup the DataSource in jndi,
     * and it is checked if Torque can read from the database. Finally,
     * the DataSource is closed and unbound.
     * @throws Exception if the test fails
     */
    public void testExternalBindTorqueLookup() throws Exception
    {
        // compose the correct configuration
        Configuration torqueConfiguration = getTorqueConfiguraton();
        String defaultDatabase = getDefaultDatabase(torqueConfiguration);

        // remove the dsfactory configuration from the configuration
        {
            Configuration dsfactoryConfiguration = torqueConfiguration.subset(
                    Torque.TORQUE_KEY + "."
                            + DataSourceFactory.DSFACTORY_KEY + "."
                            + defaultDatabase);
            dsfactoryConfiguration.clear();
        }

        // add the jndi configuration to the configuration
        torqueConfiguration.setProperty(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + DataSourceFactory.FACTORY_KEY,
                        JndiDataSourceFactory.class.getName());
        torqueConfiguration.setProperty(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + JndiDataSourceFactory.JNDI_KEY + "."
                        + JndiDataSourceFactory.PATH_KEY,
                        JNDI_PATH);
        torqueConfiguration.setProperty(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + JndiDataSourceFactory.JNDI_KEY + "."
                        + Context.INITIAL_CONTEXT_FACTORY,
                        org.apache.naming.java.javaURLContextFactory.class.getName());

        //System.out.println("Configuration for testExternalBindTorqueLookup:");
        //debugConfiguration(torqueConfiguration);
        //System.out.println();

        try
        {
            // bind datasource and check bind.
            bindDataSource();
            BasicDataSource dataSource = retrieveDataSource();
            dataSourceConnect(dataSource);


            if (Torque.isInit())
            {
                Torque.shutdown();
            }
            // initialize torque with the created configuration
            // and check that we can connect to the database.
            try
            {
                Torque.init(torqueConfiguration);
                torqueConnect();
            }
            finally
            {
                Torque.shutdown();
            }
        }
        finally
        {
            unbindDataSource();
        }
    }

    /**
     * Binds a DataSource to the jndi and checks that we have successfully
     * bound it. Then Torque is configured to lookup the DataSource in jndi,
     * and it is checked if Torque can read from the database. Finally,
     * the DataSource is closed and unbound.
     * @throws Exception if the test fails
     */
    public void testTorqueBindTorqueLookup() throws Exception
    {
        // compose the correct configuration
        Configuration torqueConfiguration = getTorqueConfiguraton();
        String defaultDatabase = getDefaultDatabase(torqueConfiguration);

        // add the jndi configuration to the configuration
        torqueConfiguration.setProperty(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + DataSourceFactory.FACTORY_KEY,
                        JndiDataSourceFactory.class.getName());
        torqueConfiguration.setProperty(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + JndiDataSourceFactory.JNDI_KEY + "."
                        + JndiDataSourceFactory.PATH_KEY,
                        JNDI_PATH);
        torqueConfiguration.setProperty(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + JndiDataSourceFactory.JNDI_KEY + "."
                        + Context.INITIAL_CONTEXT_FACTORY,
                        org.apache.naming.java.javaURLContextFactory.class.getName());

        // add the datasource configuration
        torqueConfiguration.setProperty(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + JndiDataSourceFactory.DATASOURCE_KEY + "."
                        + JndiDataSourceFactory.CLASSNAME_KEY,
                        BasicDataSource.class.getName());
        {
            Map<String, String> tempStore = new HashMap<>();
            Configuration connectionConfiguration
            = torqueConfiguration.subset(
                    Torque.TORQUE_KEY + "."
                            + DataSourceFactory.DSFACTORY_KEY + "."
                            + defaultDatabase + "."
                            + AbstractDataSourceFactory.CONNECTION_KEY);
            for (Iterator<String> keyIt = connectionConfiguration.getKeys();
                    keyIt.hasNext();)
            {
                String key = keyIt.next();
                String value = connectionConfiguration.getString(key);

                if ("user".equals(key))
                {
                    // setUser() in SharedPoolDataSouce corresponds to
                    // setUsername() in BasicDataSourceFactory
                    key = "username";
                }
                else if ("driver".equals(key))
                {
                    // setDriver() in SharedPoolDataSouce corresponds to
                    // setDriverClassName() in BasicDataSourceFactory
                    key = "driverClassName";
                }
                tempStore.put(
                        Torque.TORQUE_KEY + "."
                                + DataSourceFactory.DSFACTORY_KEY + "."
                                + defaultDatabase + "."
                                + JndiDataSourceFactory.DATASOURCE_KEY + "."
                                + key,
                                value);
            }
            // add the new keys
            for (String key : tempStore.keySet())
            {
                String value = tempStore.get(key);
                torqueConfiguration.setProperty(key, value);
            }

            // remove the configuration for the original datasource
            connectionConfiguration.clear();
            Configuration poolConfiguration
            = torqueConfiguration.subset(
                    Torque.TORQUE_KEY + "."
                            + DataSourceFactory.DSFACTORY_KEY + "."
                            + defaultDatabase + "."
                            + AbstractDataSourceFactory.POOL_KEY);
            poolConfiguration.clear();
        }

        //System.out.println("Configuration for testTorqueBindTorqueLookup:");
        //debugConfiguration(torqueConfiguration);
        //System.out.println();

        try
        {
            // initialize torque with the created configuration
            // and check that we can connect to the database.
            try
            {
                Torque.init(torqueConfiguration);
                torqueConnect();
            }
            finally
            {
                Torque.shutdown();
            }
        }
        finally
        {
            unbindDataSource();
        }
    }

    /**
     * creates and binds a BasicDataSource into jndi.
     * @throws Exception if DataSource creation or binding fails.
     */
    protected void bindDataSource() throws Exception
    {
        BasicDataSource dataSource = getDataSource();
        Context context = getInitialContext();
        try {
            context.lookup(JNDI_SUBCONTEXT);
        }
        catch (NameNotFoundException e)
        {
            context.createSubcontext(JNDI_SUBCONTEXT);
        }
        context.bind(JNDI_PATH, dataSource);
    }

    /**
     * Retrieves a BasicDataSource from jndi.
     * @throws Exception if the jndi lookup fails or no DataSource is bound.
     */
    protected BasicDataSource retrieveDataSource() throws Exception
    {
        Context context = getInitialContext();
        BasicDataSource dataSource
        = (BasicDataSource) context.lookup(JNDI_PATH);

        if (dataSource == null)
        {
            fail("DataSource should not be null");
        }
        return dataSource;
    }

    /**
     * unbinds and closes the BasicDataSource in jndi.
     * @throws Exception if creation or binding fails.
     */
    protected void unbindDataSource() throws Exception
    {
        Context context = getInitialContext();
        BasicDataSource dataSource;
        try
        {
            dataSource = (BasicDataSource) context.lookup(JNDI_PATH);
        }
        catch (NameNotFoundException e)
        {
            logger.warn("Name " + JNDI_PATH + " not found, could not unbind DataSource");
            return;
        }

        try
        {
            if (dataSource != null)
            {
                dataSource.close();
            }
        }
        finally
        {
            context.unbind(JNDI_PATH);
        }
    }

    /**
     * Creates a Data Source from the Torque configuration without using Torque.
     * @return a SharedPoolDataSource source.
     * @throws Exception if we cannot create a Data source.
     */
    protected BasicDataSource getDataSource() throws Exception
    {
        Configuration torqueConfiguration = getTorqueConfiguraton();
        String defaultDatabase = getDefaultDatabase(torqueConfiguration);
        Configuration dsfactoryConfiguration = torqueConfiguration.subset(
                Torque.TORQUE_KEY + "."
                        + DataSourceFactory.DSFACTORY_KEY + "."
                        + defaultDatabase + "."
                        + AbstractDataSourceFactory.CONNECTION_KEY);

        BasicDataSource dataSource = new BasicDataSource();
        for (Iterator<String> i = dsfactoryConfiguration.getKeys(); i.hasNext();)
        {
            String key = i.next();
            String stringValue = dsfactoryConfiguration.getString(key);

            if ("user".equals(key))
            {
                // setUser() in SharedPoolDataSouce corresponds to
                // setUsername() in BasicDataSourceFactory
                key = "username";
            }
            else if ("driver".equals(key))
            {
                // setDriver() in SharedPoolDataSouce corresponds to
                // setDriverClassName() in BasicDataSourceFactory
                key = "driverClassName";
            }

            Class<?> propertyType =
                    PropertyUtils.getPropertyType(dataSource, key);
            Object value =
                    ConvertUtils.convert(stringValue, propertyType);
            PropertyUtils.setSimpleProperty(dataSource, key, value);
        }

        return dataSource;
    }

    /**
     * checks whether we can retrieve a connection from a DataSource.
     * @throws Exception if no connection can be established.
     */
    protected void dataSourceConnect(final DataSource dataSource) throws Exception
    {
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            connection.close();
            connection = null;
        }
        finally
        {
            if (connection != null)
            {
                connection.close();
            }
        }
    }

    /**
     * checks whether we can connect to the database via Torque.
     * @throws Exception if no connection can be established.
     */
    protected void torqueConnect() throws Exception
    {
        Connection connection = null;
        try
        {
            connection = Torque.getConnection();
            BookPeer.doSelect(new Criteria(), connection);
            connection.close();
            connection = null;
        }
        finally
        {
            if (connection != null)
            {
                connection.close();
            }
        }
    }

    /**
     * Retrieves (or creates if it does not exist) an InitialContext.
     * @return the InitialContext.
     * @throws NamingException if the InitialContext cannot be retrieved
     *         or created.
     */
    protected InitialContext getInitialContext() throws NamingException
    {
        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(
                Context.INITIAL_CONTEXT_FACTORY,
                org.apache.naming.java.javaURLContextFactory.class.getName());
        InitialContext context = new InitialContext(environment);
        return context;
    }

    /**
     * Retrieves the path for Torque's configuration from the System Properties,
     * loads the configuration and returns it.
     * @return Torque's configuration.
     * @throws ConfigurationException if the configuration cannot be loaded.
     */
    protected static Configuration getTorqueConfiguraton()
            throws ConfigurationException
    {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
            new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
            .configure(params.properties()
            .setFileName(System.getProperty(BaseDatabaseTestCase.CONFIG_FILE_SYSTEM_PROPERTY)));

        return builder.getConfiguration();
    }

    /**
     * extraxts the default Database out of Torque's configuration.
     * @param torqueConfiguration the Torque configuration
     * @return the default Data Source.
     */
    protected static String getDefaultDatabase(
            final Configuration torqueConfiguration)
    {
        String defaultDatabase = torqueConfiguration.getString(
                Torque.TORQUE_KEY + "."
                        + Torque.DATABASE_KEY + "."
                        + Torque.DEFAULT_KEY);
        return defaultDatabase;
    }

    /**
     * Prints the contents of the configuration to System.out
     * @param configuration the configuration to be debugged.
     */
    public static void debugConfiguration(final Configuration configuration)
    {
        for (Iterator<String> dsKeyIt = configuration.getKeys();
                dsKeyIt.hasNext(); )
        {
            String key = dsKeyIt.next();
            System.out.println(key + " = " + configuration.getString(key));
        }
    }
}
