package org.apache.torque.adapter;

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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class creates different {@link org.apache.torque.adapter.Adapter}
 * objects based on specified JDBC driver name.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:ralf@reswi.ruhr.de">Ralf Stranzenbach</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: AdapterFactory.java 1870542 2019-11-28 09:32:40Z tv $
 */
public final class AdapterFactory
{
    /** Local logger */
    private static final Logger log = LogManager.getLogger(AdapterFactory.class);

    /**
     * JDBC driver to Torque Adapter map.
     */
    private static Map<String, Class<? extends Adapter>> adapters
        = new HashMap<>();

    /**
     * Initialize the JDBC driver to Torque Adapter map.
     */
    static
    {
        adapters.put("org.hsql.jdbcDriver", HsqldbAdapter.class);
        adapters.put("org.hsqldb.jdbcDriver", HsqldbAdapter.class);
        adapters.put("com.microsoft.jdbc.sqlserver.SQLServerDriver",
                MssqlAdapter.class);
        adapters.put("com.jnetdirect.jsql.JSQLDriver", MssqlAdapter.class);
        adapters.put("org.gjt.mm.mysql.Driver", MysqlAdapter.class);
        adapters.put("com.mysql.cj.jdbc.Driver", MysqlAdapter.class);
        adapters.put("oracle.jdbc.driver.OracleDriver", OracleAdapter.class);
        adapters.put("org.postgresql.Driver", PostgresAdapter.class);

        adapters.put("org.apache.derby.jdbc.EmbeddedDriver", DerbyAdapter.class);


        // add some short names to be used when drivers are not used
        adapters.put("hsqldb", HsqldbAdapter.class);
        adapters.put("mssql", MssqlAdapter.class);
        adapters.put("mysql", MysqlAdapter.class);
        adapters.put("oracle", OracleAdapter.class);
        adapters.put("postgresql", PostgresAdapter.class);
        adapters.put("derby", DerbyAdapter.class);

        // add database product names to be used for auto-detection
        adapters.put("HSQL Database Engine", HsqldbAdapter.class);
        adapters.put("Microsoft SQL Server Database", MssqlAdapter.class);
        adapters.put("Microsoft SQL Server", MssqlAdapter.class);
        adapters.put("MySQL", MysqlAdapter.class);
        adapters.put("Oracle", OracleAdapter.class);
        adapters.put("PostgreSQL", PostgresAdapter.class);
        adapters.put("Apache Derby", DerbyAdapter.class);

        adapters.put("", NoneAdapter.class);
    }

    /**
     * Private constructor to prevent instantiation.
     *
     * Class contains only static methods, so no instances are needed.
     */
    private AdapterFactory()
    {
    }

    /**
     * Creates a new instance of the Torque database adapter based on
     * the JDBC meta-data
     *
     * @param con a database connection
     * @return An instance of a Torque database adapter, or null if
     *         no adapter could be detected.
     * @throws InstantiationException if the adapter could not be
     *         instantiated
     * @throws SQLException if there are problems getting the JDBC meta data
     */
    public static Adapter autoDetectAdapter(Connection con)
            throws InstantiationException, SQLException
    {
        DatabaseMetaData dmd = con.getMetaData();
        String dbmsName = dmd.getDatabaseProductName();

        Adapter adapter = create(dbmsName);

        if (adapter == null)
        {
            throw new InstantiationException("Could not detect adapter for database: " + dbmsName);
        }

        log.info("Mapped database product {} to adapter {}", dbmsName, adapter.getClass().getSimpleName());

        return adapter;
    }

    /**
     * Update static capabilities of the Torque database adapter with actual
     * readings based on the JDBC meta-data
     *
     * @param con a database connection
     * @param adapter an adapter
     * @throws SQLException if there are problems getting the JDBC meta data
     */
    public static void setCapabilities(Connection con, Adapter adapter)
            throws SQLException
    {
        DatabaseMetaData dmd = con.getMetaData();
        adapter.setCapabilities(dmd);
    }

    /**
     * Creates a new instance of the Torque database adapter associated
     * with the specified JDBC driver or adapter key.
     *
     * @param key The fully-qualified name of the JDBC driver
     *        or a shorter form adapter key.
     * @return An instance of a Torque database adapter, or null if
     *         no adapter exists for the given key.
     * @throws InstantiationException throws if the adapter could not be
     *         instantiated
     */
    public static Adapter create(String key)
            throws InstantiationException
    {
        Class<? extends Adapter> adapterClass = adapters.get(key);

        if (adapterClass == null)
        {
            return null;
        }

        try
        {
            Adapter adapter = adapterClass.newInstance();
            return adapter;
        }
        catch (IllegalAccessException e)
        {
            throw new InstantiationException(
                    "Could not instantiate adapter for key : "
                            + key
                            + ": Assure that adapter classes are in your classpath");
        }
    }

    /**
     * Creates a new instance of the Torque database adapter associated
     * with the specified JDBC driver or adapter key and the class defined.
     *
     * @param key The fully-qualified name of the JDBC driver
     *        or a shorter form adapter key.
     * @param className The fully qualified name of the adapter class
     * @return An instance of a Torque database adapter.
     * @throws InstantiationException throws if the adapter could not be
     *         instantiated
     */
    @SuppressWarnings("unchecked")
    public static Adapter create(String key, String className)
            throws InstantiationException
    {
        Class<?> adapterClass;

        try
        {
            adapterClass = Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new InstantiationException(
                    "Could not find adapter "
                            + className
                            + " for key "
                            + key
                            + ": Check your configuration file");
        }

        adapters.put(key, (Class<? extends Adapter>) adapterClass);
        Adapter adapter = create(key);

        return adapter;
    }
}
