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
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.manager.AbstractBaseManager;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.oid.IDBroker;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.BasePeerImpl;

/**
 * <p>
 * A static facade wrapper around the Torque implementation (which is in
 * {@link org.apache.torque.TorqueInstance}).
 * </p>
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magn�s ��r Torfason</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:kschrader@karmalab.org">Kurt Schrader</a>
 * @version $Id: Torque.java 1867515 2019-09-25 15:02:03Z gk $
 */
public final class Torque
{
    /** The prefix for all configuration keys used by Torque. */
    public static final String TORQUE_KEY = "torque";

    /**
     * The prefix for configuring the database adapters
     * and the default database.
     */
    public static final String DATABASE_KEY = "database";

    /** The key used to configure the name of the default database. */
    public static final String DEFAULT_KEY = "default";

    /** "schema" Key for the configuration */
    public static final String SCHEMA_KEY = "schema";

    /** "defaults" Key for the configuration */
    public static final String DEFAULTS_KEY = "defaults";

    /** default schema name for the configuration */
    public static final String DEFAULT_SCHEMA_KEY
    = DEFAULTS_KEY + "." + SCHEMA_KEY;

    /** A prefix for <code>Manager</code> properties in the configuration. */
    public static final String MANAGER_PREFIX = "managed_class.";

    /**
     * A <code>Service</code> property determining its implementing
     * class name .
     */
    public static final String MANAGER_SUFFIX = ".manager";

    /** Name of config property to determine whether caching is used. */
    public static final String CACHE_KEY = "manager.useCache";

    /** The prefix for configuring the transaction manger. */
    public static final String TRANSACTION_MANAGER_KEY = "transactionManager";

    /**
     * The single instance of {@link TorqueInstance} used by the
     * static API presented by this class.
     */
    private static TorqueInstance torqueSingleton = null;

    /**
     * Class contains only static methods, thus instantiation makes no sense.
     */
    private Torque()
    {
        // not used
    }

    /**
     * Retrieves the single {@link org.apache.torque.TorqueInstance}
     * used by this class.
     *
     * @return Our singleton.
     */
    public static TorqueInstance getInstance()
    {
        if (torqueSingleton == null)
        {
            torqueSingleton = new TorqueInstance();
        }
        return torqueSingleton;
    }

    /**
     * Sets the single {@link org.apache.torque.TorqueInstance}
     * used by this class. This is used by the Avalon component
     * to make sure that only one instance of Torque exists.
     *
     * @param instance Our singleton.
     */
    public static void setInstance(final TorqueInstance instance)
    {
        torqueSingleton = instance;
    }

    /**
     * Initialization of Torque with a path to a properties file.
     *
     * @param configFile The absolute path to the configuration file.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static void init(final String configFile)
            throws TorqueException
    {
        getInstance().init(configFile);
    }

    /**
     * Initialization of Torque with a configuration.
     *
     * @param conf The Torque configuration.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static void init(final Configuration conf)
            throws TorqueException
    {
        getInstance().init(conf);
    }

    /**
     * Determine whether Torque has already been initialized.
     *
     * @return true if Torque is already initialized
     */
    public static boolean isInit()
    {
        return getInstance().isInit();
    }

    /**
     * Sets the configuration for Torque and all dependencies.
     *
     * @param conf the Configuration
     *
     * @throws TorqueException if the configuration does not contain
     *         any keys starting with <code>Torque.TORQUE_KEY</code>.
     */
    public static void setConfiguration(final Configuration conf)
            throws TorqueException
    {
        getInstance().setConfiguration(conf);
    }

    /**
     * Get the configuration for this component.
     *
     * @return the Configuration
     */
    public static Configuration getConfiguration()
    {
        return getInstance().getConfiguration();
    }

    /**
     * This method returns a Manager for the given name.
     *
     * @param <T> the type of the manager class
     * @param name name of the manager.
     *
     * @return The requested Manager.
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractBaseManager<? extends Persistent>> T getManager(final String name)
    {
        /*
         *  The cast is necessary to work around bug
         *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
         *  in JDK 5.0
         */
        return (T) getInstance().getManager(name);
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
    public static <T extends AbstractBaseManager<? extends Persistent>> T getManager(
            final String name,
            final String defaultClassName)
    {
        /*
         *  The cast is necessary to work around bug
         *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
         *  in JDK 5.0
         */
        return (T) getInstance().getManager(name, defaultClassName);
    }

    /**
     * This method registers a PeerImpl for a given class.
     *
     * @param <T> the type of the OM class
     * @param omClass the class of the associated OM object
     * @param peerInstance PeerImpl instance
     */
    public static <T> void registerPeerInstance(final Class<T> omClass, BasePeerImpl<T> peerInstance)
    {
        getInstance().registerPeerInstance(omClass, peerInstance);
    }

    /**
     * This method returns a PeerImpl for the given class.
     *
     * @param <T> the type of the OM class
     * @param <P> the type of the peer instance class
     * @param omClass the class of the associated OM object
     * @return a PeerImpl instance
     */
    @SuppressWarnings("unchecked")
    public static <T, P extends BasePeerImpl<T>> P getPeerInstance(final Class<T> omClass)
    {
        /*
         *  The cast is necessary to work around bug
         *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
         *  in JDK 5.0
         */
        return (P) getInstance().getPeerInstance(omClass);
    }

    /**
     * Shuts down the service.
     *
     * This method halts the IDBroker's daemon thread in all of
     * the DatabaseMap's. It also closes all SharedPoolDataSourceFactories
     * and PerUserPoolDataSourceFactories initialized by Torque.
     *
     * @throws TorqueException if a DataSourceFactory could not be closed
     *            cleanly. Only the first exception is rethrown, any following
     *            exceptions are logged but ignored.
     */
    public static void shutdown()
            throws TorqueException
    {
        getInstance().shutdown();
    }

    /**
     * Returns the default database map information.
     *
     * @return A DatabaseMap.
     *
     * @throws TorqueException if Torque is not initialized.
     */
    public static DatabaseMap getDatabaseMap()
            throws TorqueException
    {
        return getInstance().getDatabaseMap();
    }

    /**
     * Returns the database map information for a given database.
     *
     * @param name The name of the database corresponding to the
     *        <code>DatabaseMap</code> to retrieve, or null
     *        for the default database.
     *
     * @return The named <code>DatabaseMap</code>, not null.
     *
     * @throws TorqueException if Torque is not initialized and name is null.
     */
    public static DatabaseMap getDatabaseMap(final String name)
            throws TorqueException
    {
        return getInstance().getDatabaseMap(name);
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
    public static void registerIDBroker(final IDBroker idBroker)
    {
        getInstance().registerIDBroker(idBroker);
    }

    /**
     * This method returns a Connection from the default pool.
     *
     * @return The requested connection.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static Connection getConnection()
            throws TorqueException
    {
        return getInstance().getConnection();
    }

    /**
     * This method returns a Connecton using the given database name.
     *
     * @param name The database name.
     *
     * @return a database connection to the database with the given name.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static Connection getConnection(final String name)
            throws TorqueException
    {
        return getInstance().getConnection(name);
    }

    /**
     * This method returns a Connecton using the given parameters.
     * You should only use this method if you need user based access to the
     * database!
     *
     * @param name The database name.
     * @param username The name of the database user.
     * @param password The password of the database user.
     *
     * @return A Connection to the database with the given name.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static Connection getConnection(final String name, final String username,
            final String password)
                    throws TorqueException
    {
        return getInstance().getConnection(name, username, password);
    }

    /**
     * Returns the database adapter for a specific database name.
     *
     * @param name the database name, or null for the default db.
     *
     * @return The corresponding database adapter, or null if no database
     *         adapter is defined for the given database.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public static Adapter getAdapter(final String name) throws TorqueException
    {
        return getInstance().getAdapter(name);
    }

    /**
     * Returns the name of the default database.
     *
     * @return name of the default DB, or null if Torque is not initialized yet
     */
    public static String getDefaultDB()
    {
        return getInstance().getDefaultDB();
    }

    /**
     * Closes a connection.
     *
     * @param con A Connection to close.
     */
    public static void closeConnection(final Connection con)
    {
        getInstance().closeConnection(con);
    }

    /**
     * Sets the current schema for a database connection
     *
     * @param name The database name.
     * @param schema The current schema name.
     *
     * @throws NullPointerException if databaseName is null.
     */
    public static void setSchema(final String name, final String schema)
    {
        getInstance().setSchema(name, schema);
    }

    /**
     * This method returns the current schema for a database connection
     *
     * @param name The database name.
     *
     * @return The current schema name. Null means, no schema has been set.
     *
     * @throws TorqueException if Torque is not yet initialized.
     */
    public static String getSchema(final String name)
            throws TorqueException
    {
        return getInstance().getSchema(name);
    }

    /**
     * Returns the database for the given key.
     *
     * @param name the key to get the database for,
     *        or null for the default database.
     *
     * @return the Database for the given name, or null if no database exists
     *         for the given name.
     *
     * @throws TorqueException if Torque is not yet initialized.
     */
    public static Database getDatabase(final String name) throws TorqueException
    {
        return getInstance().getDatabase(name);
    }

    /**
     * Returns the database for the key <code>databaseName</code>.
     * If no database is associated to the specified key,
     * a new database is created, mapped to the specified key, and returned.
     *
     * @param name the key to get the database for, not null.
     *
     * @return the database associated with specified key, or the newly created
     *         database, never null.
     *
     * @throws IllegalArgumentException if databaseName is null.
     */
    public static Database getOrCreateDatabase(final String name)
    {
        return getInstance().getOrCreateDatabase(name);
    }

    /**
     * Returns a Map containing all Databases registered to Torque.
     * The key of the Map is the name of the database, and the value is the
     * database instance. 
     * 
     * <p>
     * Note that in the very special case where a new database which
     * is not configured in Torque's configuration gets known to Torque
     * at a later time, the returned map may change, and there is no way to
     * protect you against this. However, Databases should be initialized
     * in the init() method, so this will not happen if Torque is used
     * properly.
     * </p>
     *
     * @return a Map containing all Databases known to Torque, never null.
     *
     * @throws TorqueException if Torque is not yet initialized.
     */
    public static Map<String, Database> getDatabases() throws TorqueException
    {
        return getInstance().getDatabases();
    }
}
