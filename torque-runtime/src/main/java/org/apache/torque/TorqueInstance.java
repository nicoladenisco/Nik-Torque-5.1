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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.jcs.JCS;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.AdapterFactory;
import org.apache.torque.adapter.IDMethod;
import org.apache.torque.dsfactory.DataSourceFactory;
import org.apache.torque.manager.AbstractBaseManager;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.oid.IDBroker;
import org.apache.torque.oid.IDGeneratorFactory;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.BasePeerImpl;
import org.apache.torque.util.Transaction;
import org.apache.torque.util.TransactionManager;
import org.apache.torque.util.TransactionManagerImpl;

/**
 * The core of Torque's implementation.  Both the classic {@link
 * org.apache.torque.Torque} static wrapper and the {@link
 * org.apache.torque.avalon.TorqueComponent} <a
 * href="http://avalon.apache.org/">Avalon</a> implementation leverage
 * this class.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magn�s ��r Torfason</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:kschrader@karmalab.org">Kurt Schrader</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: TorqueInstance.java 1896642 2022-01-03 14:32:34Z gk $
 */
public class TorqueInstance
{
    /** Logging */
    private static final Logger log = LogManager.getLogger(TorqueInstance.class);

    /** A constant for <code>default</code>. */
    private static final String DEFAULT_NAME = "default";

    /** The db name that is specified as the default in the property file */
    private String defaultDBName = null;

    /**
     * The Map which contains all known databases. All iterations over the map
     * and other operations where the database map needs to stay
     * in a defined state must be synchronized to this map.
     */
    private final ConcurrentMap<String, Database> databases
        = new ConcurrentHashMap<>();

    /** A repository of Manager instances. */
    private final ConcurrentMap<String, AbstractBaseManager<?>> managers
        = new ConcurrentHashMap<>();

    /** A repository of Peer instances. */
    private final ConcurrentMap<Class<?>, BasePeerImpl<?>> peers
        = new ConcurrentHashMap<>();

    /** A repository of idBroker instances. */
    private final Set<IDBroker> idBrokers = new HashSet<>();

    /** Torque-specific configuration. */
    private Configuration conf;

    /** Flag to set to true once this class has been initialized */
    private boolean isInit = false;

    /**
     * A flag which indicates whether the DataSourceFactory in the database
     * named <code>DEFAULT</code> is a reference to another
     * DataSourceFactory. This is important to know when closing the
     * DataSourceFactories on shutdown();
     */
    private boolean defaultDsfIsReference = false;

    /**
     * Creates a new instance with default configuration.
     *
     * @see #resetConfiguration()
     */
    public TorqueInstance()
    {
        resetConfiguration();
    }

    /**
     * Check if this TorqueInstance has been initialized.
     *
     * @throws TorqueException if instance is not initialized
     */
    private void checkInit() throws TorqueException
    {
        if (!isInit())
        {
            throw new TorqueException("Torque is not initialized.");
        }
    }

    /**
     * Initializes this instance of Torque.
     *
     * @throws TorqueException if Torque is already initialized
     *         or an error during initialization occurs.
     */
    private synchronized void initialize() throws TorqueException
    {
        log.debug("initialize()");

        if (isInit)
        {
            throw new TorqueException(
                    "Multiple initializations of Torque attempted");
        }

        if (conf == null || conf.isEmpty())
        {
            throw new TorqueException("Torque cannot be initialized without "
                    + "a valid configuration. Please check the log files "
                    + "for further details.");
        }

        initTransactionManager(conf);
        initDefaultDbName(conf);
        initDataSourceFactories(conf);
        initSchemata(conf);
        initAdapters(conf);

        // As there might be a default database configured
        // to map "default" onto an existing datasource, we
        // must check, whether there _is_ really an entry for
        // the "default" in the dsFactoryMap or not. If it is
        // not, then add a dummy entry for the "default"
        //
        // Without this, you can't actually access the "default"
        // data-source, even if you have an entry like
        //
        // database.default = bookstore
        //
        // in your Torque.properties
        //

        {
            Database defaultDatabase = databases.get(defaultDBName);
            Database databaseInfoForKeyDefault
            = getOrCreateDatabase(DEFAULT_NAME);
            if ((!defaultDBName.equals(DEFAULT_NAME))
                    && databaseInfoForKeyDefault.getDataSourceFactory() == null)
            {
                log.debug("Adding the DatasourceFactory and DatabaseAdapter "
                        + "from database {} onto database {}", () ->
                        defaultDBName, () -> DEFAULT_NAME);
                databaseInfoForKeyDefault.setDataSourceFactory(
                        defaultDatabase.getDataSourceFactory());
                databaseInfoForKeyDefault.setAdapter(
                        defaultDatabase.getAdapter());

                this.defaultDsfIsReference = true;
            }
        }

        // setup manager mappings
        initManagerMappings(conf);

        isInit = true;

        startIdBrokers();
    }


    /**
     * Initializes the transaction manager.
     *
     * @param conf the configuration representing the torque section.
     *        of the properties or xml file.
     *
     * @throws TorqueException if the transaction manger configuration
     *         is invalid.
     */
    private void initTransactionManager(final Configuration conf)
            throws TorqueException
    {
        log.debug("initTransactionManager({})", conf);

        String transactionManagerClassName =
                conf.getString(Torque.TRANSACTION_MANAGER_KEY);
        TransactionManager transactionManager;
        if (StringUtils.isEmpty(transactionManagerClassName))
        {
            if (log.isTraceEnabled())
            {
                log.trace("Configuration key {}.{} not set, using default transaction manager {}",
                        Torque.TORQUE_KEY, Torque.TRANSACTION_MANAGER_KEY, TransactionManagerImpl.class.getName());
            }
            transactionManager = new TransactionManagerImpl();
        }
        else
        {
            try
            {
                Class<?> transactionManagerClass = Class.forName(transactionManagerClassName);
                transactionManager = (TransactionManager)
                        transactionManagerClass.newInstance();
                if (log.isTraceEnabled())
                {
                    log.trace("Using transaction manager {}", transactionManager.getClass().getName());
                }
            }
            catch (Exception e)
            {
                log.error("Error handling transaction manager configuration", e);
                throw new TorqueException(e);
            }
        }
        Transaction.setTransactionManager(transactionManager);
    }


    /**
     * Initializes the name of the default database and
     * associates the database with the name <code>DEFAULT_NAME</code>
     * to the default database.
     *
     * @param conf the configuration representing the torque section.
     *        of the properties or xml file.
     *
     * @throws TorqueException if the appropriate key is not set.
     */
    private void initDefaultDbName(final Configuration conf)
            throws TorqueException
    {
        log.debug("initDefaultDbName({})", conf);

        // Determine default database name.
        defaultDBName =
                conf.getString(
                        Torque.DATABASE_KEY
                        + "."
                        + Torque.DEFAULT_KEY);
        if (defaultDBName == null)
        {
            String error = "Invalid configuration: Key "
                    + Torque.TORQUE_KEY
                    + "."
                    + Torque.DATABASE_KEY
                    + "."
                    + Torque.DEFAULT_KEY
                    + " not set";
            log.error(error);
            throw new TorqueException(error);
        }
    }

    /**
     * Reads the adapter settings from the configuration and
     * assigns the appropriate database adapters and Id Generators
     * to the databases.
     *
     * @param conf the Configuration representing the torque section of the
     *        properties or xml file.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    private void initAdapters(final Configuration conf)
            throws TorqueException
    {
        log.debug("initAdapters({})", conf);

        Configuration c = conf.subset(Torque.DATABASE_KEY);
        if (c == null || c.isEmpty())
        {
            String error = "Invalid configuration : "
                    + "No keys starting with "
                    + Torque.TORQUE_KEY
                    + "."
                    + Torque.DATABASE_KEY
                    + " found in configuration";
            log.error(error);
            throw new TorqueException(error);
        }

        try
        {
            for (Iterator<?> it = c.getKeys(); it.hasNext();)
            {
                String key = (String) it.next();
                if (key.endsWith(Adapter.ADAPTER_KEY) || key.endsWith(Adapter.DRIVER_KEY))
                {
                    String adapterKey = c.getString(key);
                    String handle = key.substring(0, key.indexOf('.'));

                    Database database = getOrCreateDatabase(handle);
                    Adapter adapter = null;

                    if (StringUtils.equals(
                            Adapter.AUTODETECT_ADAPTER,
                            adapterKey))
                    {
                        try (Connection con = database.getDataSourceFactory()
                                .getDataSource().getConnection())
                        {
                            adapter = AdapterFactory.autoDetectAdapter(con);
                        }
                        catch (SQLException e)
                        {
                            log.error("Could not get product information from JDBC", e);
                            throw new InstantiationException(e.getMessage());
                        }
                    }
                    else
                    {
                        adapter = AdapterFactory.create(adapterKey);
                    }

                    // Not supported, try manually defined adapter class
                    if (adapter == null)
                    {
                        String adapterClassName = c.getString(
                                key + "." + adapterKey + ".className", null);
                        adapter = AdapterFactory.create(
                                adapterKey,
                                adapterClassName);
                    }

                    // register the adapter for this name
                    database.setAdapter(adapter);
                    log.debug("Adding {} -> {} as Adapter", adapterKey, handle);

                    // try to get additional meta data from the driver
                    try (Connection con = database.getDataSourceFactory()
                            .getDataSource().getConnection())
                    {
                        AdapterFactory.setCapabilities(con, adapter);
                    }
                    catch (SQLException e)
                    {
                        log.debug("Could not get database meta data from JDBC");
                    }

                    // add Id generators
                    for (IDMethod idMethod : IDGeneratorFactory.ID_GENERATOR_METHODS)
                    {
                        database.addIdGenerator(
                                idMethod,
                                IDGeneratorFactory.create(adapter, handle));
                    }
                }
            }
        }
        catch (InstantiationException e)
        {
            log.error("Error creating a database adapter instance", e);
            throw new TorqueException(e);
        }

        // check that at least the default database has got an adapter.
        Database defaultDatabase = databases.get(getDefaultDB());
        if (defaultDatabase == null || defaultDatabase.getAdapter() == null)
        {
            String error = "Invalid configuration : "
                    + "No adapter definition found for default DB "
                    + "An adapter must be defined under "
                    + Torque.TORQUE_KEY
                    + "."
                    + Torque.DATABASE_KEY
                    + "."
                    + getDefaultDB()
                    + "."
                    + Adapter.ADAPTER_KEY;
            log.error(error);
            throw new TorqueException(error);
        }
    }

    /**
     * Reads the settings for the DataSourceFactories from the
     * configuration and creates and/or configures the DataSourceFactories
     * and the database objects.
     * If no DataSorceFactory is assigned to the database with the name
     * <code>DEFAULT_NAME</code>, a reference to the DataSourceFactory
     * of the default database is made from the database with the name
     * <code>DEFAULT_NAME</code>.
     *
     * @param conf the Configuration representing the properties or xml file.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    private void initDataSourceFactories(final Configuration conf)
            throws TorqueException
    {
        log.debug("initDataSourceFactories({})", conf);

        Configuration c = conf.subset(DataSourceFactory.DSFACTORY_KEY);
        if (c == null || c.isEmpty())
        {
            String error = "Invalid configuration: "
                    + "No keys starting with "
                    + Torque.TORQUE_KEY
                    + "."
                    + DataSourceFactory.DSFACTORY_KEY
                    + " found in configuration";
            log.error(error);
            throw new TorqueException(error);
        }

        // read dsfactory config (may contain schema)
        try
        {
            for (Iterator<?> it = c.getKeys(); it.hasNext();)
            {
                String key = (String) it.next();
                if (key.endsWith(DataSourceFactory.FACTORY_KEY))
                {
                    String classname = c.getString(key);
                    String handle = key.substring(0, key.indexOf('.'));
                    log.debug("handle: {} DataSourceFactory: {}", handle, classname);
                    Class<?> dsfClass = Class.forName(classname);
                    DataSourceFactory dsf = (DataSourceFactory) dsfClass.newInstance();
                    Configuration subConf = c.subset(handle);
                    dsf.initialize(subConf);

                    Database database = getOrCreateDatabase(handle);
                    database.setDataSourceFactory(dsf);
                }
            }
        }
        catch (RuntimeException e)
        {
            log.error("Error reading DataSourceFactory configuration", e);
            throw new TorqueRuntimeException(e);
        }
        catch (Exception e)
        {
            log.error("Error reading DataSourceFactory configuration", e);
            throw new TorqueException(e);
        }

        Database defaultDatabase = databases.get(defaultDBName);
        if (defaultDatabase == null || defaultDatabase.getDataSourceFactory() == null)
        {
            String error = "Invalid configuration : "
                    + "No DataSourceFactory definition for default DB found. "
                    + "A DataSourceFactory must be defined under the key"
                    + Torque.TORQUE_KEY
                    + "."
                    + DataSourceFactory.DSFACTORY_KEY
                    + "."
                    + defaultDBName
                    + "."
                    + DataSourceFactory.FACTORY_KEY;
            log.error(error);
            throw new TorqueException(error);
        }
    }

    /**
     * Reads the schema configuration from the database definitions in
     * the configuration and assigns the defined schemata to the databases.
     *
     * @param conf the Configuration representing the properties file.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    private void initSchemata(final Configuration conf)
            throws TorqueException
    {
        log.debug("initSchemata({})", conf);

        // read schema configuration from database setting
        Configuration c = conf.subset(Torque.DATABASE_KEY);
        if (c == null || c.isEmpty())
        {
            String error = "Invalid configuration: "
                    + "No keys starting with "
                    + Torque.TORQUE_KEY
                    + "."
                    + Torque.DATABASE_KEY
                    + " found in configuration";
            log.error(error);
            throw new TorqueException(error);
        }
        try
        {
            for (Iterator<?> it = c.getKeys(); it.hasNext();)
            {
                String key = (String) it.next();
                int indexOfDot = key.indexOf('.');
                if (indexOfDot == -1)
                {
                    continue;
                }
                String handle = key.substring(0, indexOfDot);

                log.debug("database handle: {}", handle);
                Configuration subConf = c.subset(handle);

                Database database = getOrCreateDatabase(handle);

                String schema = subConf.getString(Torque.SCHEMA_KEY, null);
                // check database schema because schema may have already been
                // set via the dsfactory
                if (StringUtils.isEmpty(schema))
                {
                    schema = database.getSchema();
                }
                if (StringUtils.isEmpty(schema))
                {
                    schema = conf.getString(
                            Torque.DEFAULT_SCHEMA_KEY,
                            null);
                }
                database.setSchema(schema);
            }
        }
        catch (RuntimeException e)
        {
            log.error("Error reading DataSourceFactory configuration", e);
            throw new TorqueRuntimeException(e);
        }
        catch (Exception e)
        {
            log.error("Error reading DataSourceFactory configuration", e);
            throw new TorqueException(e);
        }

    }

    /**
     * Initialization of Torque with a path to a properties or xml file.
     *
     * @param configFile The absolute path to the configuration file.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public void init(final String configFile)
            throws TorqueException
    {
        log.debug("init({})", configFile);
        try
        {
            Configuration configuration;
            if (!configFile.endsWith("xml")) 
            {

                FileBasedConfigurationBuilder<PropertiesConfiguration> propertiesBuilder = new FileBasedConfigurationBuilder<>(
                        PropertiesConfiguration.class)
                                .configure(
                                        new Parameters().properties()
                                        .setFileName(configFile)
                                        //.setThrowExceptionOnMissing(true)
                                        .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                                 );
                configuration = propertiesBuilder.getConfiguration();
            }
            else
            {
                CombinedConfigurationBuilder combinedBuilder = new CombinedConfigurationBuilder()
                        .configure(new Parameters().fileBased()
                                .setFileName(configFile)
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                       );
                configuration = combinedBuilder.getConfiguration();
            }

            log.debug("Config Object is {}", configuration);

            init(configuration);
        }
        catch (ConfigurationException e)
        {
            throw new TorqueException(e);
        }
    }

    /**
     * Initialization of Torque with a Configuration object.
     *
     * @param conf The Torque configuration.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public synchronized void init(final Configuration conf)
            throws TorqueException
    {
        log.debug("init({})", conf);
        setConfiguration(conf);
        initialize();
    }


    /**
     * Creates a mapping between classes and their manager classes.
     *
     * The mapping is built according to settings present in
     * properties file.  The entries should have the
     * following form:
     *
     * <pre>
     * torque.managed_class.com.mycompany.Myclass.manager= \
     *          com.mycompany.MyManagerImpl
     * services.managed_class.com.mycompany.Myotherclass.manager= \
     *          com.mycompany.MyOtherManagerImpl
     * </pre>
     *
     * <br>
     *
     * Generic ServiceBroker provides no Services.
     *
     * @param conf the Configuration representing the properties file
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    protected synchronized void initManagerMappings(final Configuration conf)
            throws TorqueException
    {
        int pref = Torque.MANAGER_PREFIX.length();
        int suff = Torque.MANAGER_SUFFIX.length();

        for (Iterator<?> it = conf.getKeys(); it.hasNext();)
        {
            String key = (String) it.next();

            if (key.startsWith(Torque.MANAGER_PREFIX)
                    && key.endsWith(Torque.MANAGER_SUFFIX))
            {
                String managedClassKey = key.substring(pref,
                        key.length() - suff);
                if (!managers.containsKey(managedClassKey))
                {
                    String managerClass = conf.getString(key);
                    log.info("Added Manager for Class: {} -> {}", 
                            managedClassKey, managerClass);
                    try
                    {
                        initManager(managedClassKey, managerClass);
                    }
                    catch (TorqueException e)
                    {
                        // the exception thrown here seems to disappear.
                        // At least when initialized by Turbine, should find
                        // out why, but for now make sure it is noticed.
                        log.error("", e);
                        e.printStackTrace();
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * Initialize a manager
     *
     * @param name name of the manager
     * @param className name of the manager class
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    private void initManager(final String name, final String className)
            throws TorqueException
    {
        managers.computeIfAbsent(name, key ->
        {
            if (StringUtils.isNotEmpty(className))
            {
                try
                {
                    return (AbstractBaseManager<?>) Class.forName(className).newInstance();
                }
                catch (Exception e)
                {
                    throw new TorqueRuntimeException("Could not instantiate "
                            + "manager associated with class: "
                            + key, e);
                }
            }

            return null;
        });
    }

    /**
     * Starts all registered IdBrokers.
     */
    private void startIdBrokers()
    {
        idBrokers.forEach(IDBroker::start);
    }

    /**
     * Determine whether Torque has already been initialized.
     *
     * @return true if Torque is already initialized
     */
    public boolean isInit()
    {
        return isInit;
    }

    /**
     * Sets the configuration for Torque and all dependencies.
     * The prefix <code>TORQUE_KEY</code> will be removed from the
     * configuration keys for the provided configuration.
     *
     * @param conf the Configuration.
     *
     * @throws TorqueException if the configuration does not contain
     *         any keys starting with <code>Torque.TORQUE_KEY</code>.
     */
    public void setConfiguration(final Configuration conf)
            throws TorqueException
    {
        log.debug("setConfiguration({})", conf);

        Configuration subConf = conf.subset(Torque.TORQUE_KEY);
        if (subConf == null || subConf.isEmpty())
        {
            String error = ("Invalid configuration. No keys starting with "
                    + Torque.TORQUE_KEY
                    + " found in configuration");
            log.error(error);
            throw new TorqueException(error);
        }
        this.conf = subConf;
    }

    /**
     * Get the configuration for this component.
     *
     * @return the Configuration
     */
    public Configuration getConfiguration()
    {
        log.debug("getConfiguration() = {}", conf);
        return conf;
    }

    /**
     * This method returns a Manager for the given name.
     *
     * @param <T> the type of the manager class
     * @param name name of the manager
     * @return a Manager
     */
    public <T extends AbstractBaseManager<? extends Persistent>> T getManager(final String name)
    {
        @SuppressWarnings("unchecked")
        T m = (T) managers.get(name);
        if (m == null)
        {
            log.error("No configured manager for key {}.", name);
        }
        return m;
    }

    /**
     * This methods returns either the Manager from the configuration file,
     * or the default one provided by the generated code.
     *
     * @param <T> the type of the manager class
     * @param name name of the manager.
     * @param defaultClassName the class to use if name has not been configured.
     *
     * @return a Manager
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractBaseManager<? extends Persistent>> T getManager(final String name,
            final String defaultClassName)
    {
        T m = (T) managers.get(name);
        if (m == null)
        {
            log.debug("Added late Manager mapping for Class: {} -> {}", name, defaultClassName);

            try
            {
                initManager(name, defaultClassName);
            }
            catch (TorqueException e)
            {
                log.error(e.getMessage(), e);
            }

            // Try again now that the default manager should be in the map
            m = (T) managers.get(name);
        }

        return m;
    }

    /**
     * This method registers a PeerImpl for a given class.
     *
     * @param <T> the type of the OM class and the type of the peerInstance class, which is of type BasePeerImpl.
     * @param omClass the class of the associated OM object
     * @param peerInstance PeerImpl instance
     */
    public <T> void registerPeerInstance(final Class<T> omClass, BasePeerImpl<T> peerInstance)
    {
        peers.put(omClass, peerInstance);
    }

    /**
     * This method returns a PeerImpl for the given class.
     *
     * @param <T> the type of the OM class
     * @param <P> the type of the peerInstance class
     * @param omClass the class of the associated OM object
     * @return a PeerImpl instance
     */
    public <T, P extends BasePeerImpl<T>> P getPeerInstance(final Class<T> omClass)
    {
        @SuppressWarnings("unchecked")
        P p = (P) peers.get(omClass);
        if (p == null)
        {
            log.error("No registered peer for class {}.", omClass);
        }
        return p;
    }

    /**
     * Shuts down Torque.
     *
     * This method halts the IDBroker's daemon thread in all of
     * the DatabaseMap's. It also closes all SharedPoolDataSourceFactories
     * and PerUserPoolDataSourceFactories initialized by Torque.
     *
     * @throws TorqueException if a DataSourceFactory could not be closed
     *            cleanly. Only the first exception is rethrown, any following
     *            exceptions are logged but ignored.
     */
    public synchronized void shutdown()
            throws TorqueException
    {
        // do not remove idbrokers because they will not be
        // re-registered on a new startup.
        idBrokers.forEach(IDBroker::stop);

        // shut down the cache managers
        managers.values().forEach(AbstractBaseManager::dispose);
        JCS.shutdown();

        // shut down the data source factories
        TorqueException exception = null;
        for (Map.Entry<String, Database> databaseMapEntry
                : databases.entrySet())
        {
            Object databaseKey = databaseMapEntry.getKey();
            Database database = databaseMapEntry.getValue();
            if (DEFAULT_NAME.equals(databaseKey) && defaultDsfIsReference)
            {
                // the DataSourceFactory of the database with the name
                // DEFAULT_NAME is just a reference to another entry.
                // Do not close because this leads to closing
                // the same DataSourceFactory twice.
                database.setDataSourceFactory(null);
                continue;
            }

            try
            {
                DataSourceFactory dataSourceFactory
                = database.getDataSourceFactory();
                if (dataSourceFactory != null)
                {
                    dataSourceFactory.close();
                    database.setDataSourceFactory(null);
                }
            }
            catch (TorqueException e)
            {
                log.error("Error while closing the DataSourceFactory {}",
                        databaseKey,
                        e);
                if (exception == null)
                {
                    exception = e;
                }
            }
        }
        if (exception != null)
        {
            throw exception;
        }
        resetConfiguration();
    }

    /**
     * Resets some internal configuration variables to their defaults.
     */
    private void resetConfiguration()
    {
        managers.clear();
        peers.clear();
        // TODO: database maps should be re-created on restart
        // databases.clear();
        isInit = false;
    }

    /**
     * Returns the database map information for the default db.
     *
     * @return the requested DatabaseMap, not null.
     *
     * @throws TorqueException if Torque is not initialized.
     */
    public DatabaseMap getDatabaseMap()
            throws TorqueException
    {
        String name = getDefaultDB();
        if (name == null)
        {
            throw new TorqueException("Torque is not initialized");
        }
        return getDatabaseMap(name);
    }

    /**
     * Returns the database map information for the given database name.
     *
     * @param name The name of the database corresponding to the
     *        <code>DatabaseMap</code> to retrieve, or null
     *        for the default database.
     *
     * @return The named <code>DatabaseMap</code>, not null.
     *
     * @throws TorqueException if Torque is not initialized and name is null.
     */
    public DatabaseMap getDatabaseMap(String name)
            throws TorqueException
    {
        if (name == null)
        {
            checkInit();
            name = getDefaultDB();
        }
        Database database = getOrCreateDatabase(name);
        return database.getDatabaseMap();
    }

    /**
     * Registers an id broker. If Torque is already initialized,
     * the id broker is started. If Torque is not initialized,
     * the id broker will be started on initialization.
     *
     * @param idBroker the id broker to register, not null.
     *
     * @throws NullPointerException if idBroker is null.
     */
    public void registerIDBroker(final IDBroker idBroker)
    {
        idBrokers.add(idBroker);
        if (isInit())
        {
            idBroker.start();
        }
    }

    /**
     * This method returns a Connection from the default pool.
     *
     * @return The requested connection, never null.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public Connection getConnection()
            throws TorqueException
    {
        return getConnection(getDefaultDB());
    }

    /**
     * Returns a database connection to the database with the key
     * <code>name</code>.
     *
     * @param name The database name.
     *
     * @return a database connection to the named database, never null.
     *
     * @throws TorqueException if Torque is not initialized,
     *         if no DataSourceFactory is configured for the
     *         named database, the connection information is wrong, or the
     *         connection cannot be returned for any other reason.
     */
    public Connection getConnection(final String name)
            throws TorqueException
    {
        checkInit();

        try
        {
            return getDatabase(name)
                    .getDataSourceFactory()
                    .getDataSource()
                    .getConnection();
        }
        catch (SQLException se)
        {
            throw new TorqueException(se);
        }
    }

    /**
     * Returns the DataSourceFactory for the database with the name
     * <code>name</code>.
     *
     * @param name The name of the database to get the DSF for.
     *
     * @return A DataSourceFactory object, never null.
     *
     * @throws TorqueException if Torque is not initialized, or
     *         no DatasourceFactory is configured for the given name.
     */
    public DataSourceFactory getDataSourceFactory(final String name)
            throws TorqueException
    {
        checkInit();

        Database database = getDatabase(name);

        DataSourceFactory dsf = null;
        if (database != null)
        {
            dsf = database.getDataSourceFactory();
        }

        if (dsf == null)
        {
            throw new TorqueException(
                    "There was no DataSourceFactory "
                            + "configured for the connection " + name);
        }

        return dsf;
    }

    /**
     * This method returns a Connection using the given parameters.
     * You should only use this method if you need user based access to the
     * database!
     *
     * @param name The database name.
     * @param username The name of the database user.
     * @param password The password of the database user.
     *
     * @return A Connection to the named database.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public Connection getConnection(final String name, final String username,
            final String password)
                    throws TorqueException
    {
        checkInit();

        try
        {
            return getDataSourceFactory(name)
                    .getDataSource().getConnection(username, password);
        }
        catch (SQLException se)
        {
            throw new TorqueException(se);
        }
    }

    /**
     * Returns the database adapter for a specific database.
     *
     * @param name the database name, or null for the default db.
     *
     * @return The corresponding database adapter, or null if no database
     *         adapter is defined for the given database.
     *
     * @throws TorqueException if Torque is not initialized.
     */
    public Adapter getAdapter(final String name) throws TorqueException
    {
        checkInit();

        Database database = getDatabase(name);
        if (database == null)
        {
            return null;
        }
        return database.getAdapter();
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the name of the default database.
     *
     * @return name of the default DB, or null if Torque is not initialized yet
     */
    public String getDefaultDB()
    {
        return defaultDBName;
    }

    /**
     * Closes a connection.
     *
     * @param con A Connection to close.
     */
    public void closeConnection(final Connection con)
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (SQLException e)
            {
                log.error("Error occured while closing connection.", e);
            }
        }
    }

    /**
     * Sets the current schema for a database connection
     *
     * @param name The database name, not null.
     * @param schema The current schema name.
     *
     * @throws NullPointerException if databaseName is null.
     */
    public void setSchema(final String name, final String schema)
    {
        getOrCreateDatabase(name).setSchema(schema);
    }

    /**
     * This method returns the current schema for a database connection
     *
     * @param name The database name.
     *
     * @return The current schema name. Null means, no schema has been set
     *         or no database with the given name exists.
     *
     * @throws TorqueException if Torque is not yet initialized.
     */
    public String getSchema(final String name)
            throws TorqueException
    {
        checkInit();

        Database database = getDatabase(name);
        if (database == null)
        {
            return null;
        }
        return database.getSchema();
    }

    /**
     * Returns the database for the key <code>databaseName</code>.
     *
     * @param databaseName the key to get the database for,
     *        or null for the default database.
     *
     * @return the database for the specified key, or null if the database
     *         does not exist.
     *
     * @throws TorqueException if Torque is not yet initialized.
     */
    public Database getDatabase(String databaseName) throws TorqueException
    {
        checkInit();

        if (databaseName == null)
        {
            databaseName = getDefaultDB();
        }
        return databases.get(databaseName);
    }

    /**
     * <p>
     * Returns a Map containing all Databases registered to Torque.
     * The key of the Map is the name of the database, and the value is the
     * database instance.
     * </p>
     *
     * <p>
     * Note that in the very special case where a new database which
     * is not configured in Torque's configuration gets known to Torque
     * at a later time, the returned map may change, and there is no way to
     * protect you against this.
     * </p>
     *
     * @return a Map containing all Databases known to Torque, never null.
     *
     * @throws TorqueException if Torque is not yet initialized.
     */
    public Map<String, Database> getDatabases() throws TorqueException
    {
        checkInit();

        return Collections.unmodifiableMap(databases);
    }

    /**
     * Returns the database for the key <code>databaseName</code>.
     * If no database is associated to the specified key,
     * a new database is created, mapped to the specified key, and returned.
     *
     * @param databaseName the key to get the database for, not null.
     *
     * @return the database associated with specified key, or the newly created
     *         database, never null.
     *
     * @throws NullPointerException if databaseName is null.
     */
    public Database getOrCreateDatabase(final String databaseName)
    {
        if (databaseName == null)
        {
            throw new NullPointerException("databaseName is null");
        }

        return databases.computeIfAbsent(databaseName, key -> new Database(key));
    }
}
