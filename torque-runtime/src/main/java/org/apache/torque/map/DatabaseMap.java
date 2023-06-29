package org.apache.torque.map;

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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.Database;
import org.apache.torque.TorqueException;

/**
 * DatabaseMap is used to model a database.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: DatabaseMap.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class DatabaseMap extends OptionSupport implements Serializable
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The character used by most implementations as the separator
     * between name elements.
     */
    public static final char STD_SEPARATOR_CHAR = '_';

    /**
     * The character which separates the schema name from the table name.
     */
    public static final char SCHEMA_SEPARATOR_CHAR = '.';

    /**
     * Format used to create create the class name for initializing a DB
     * specific map
     */
    public static final String INIT_CLASS_NAME_FORMAT =
            "org.apache.torque.linkage.{0}DatabaseMapInit";

    /** Error Message for class not found. */
    private static final String ERROR_MESSAGES_CLASS_NOT_FOUND
    	= "Invalid Torque OM setup for Database \"{0}\".\n"
            + "Database Map initialization class, \"{1}\"," + " "
            + "could not be found in your classpath.";

    /** Error Message for dependent class not found. */
    private static final String ERROR_MESSAGES_DEPENDENT_CLASS_NOT_FOUND
    	= "Invalid Torque OM setup for Database \"{0}\".\n"
            + "A class that the Database Map initialization class, \"{1}\", "
            + "depends on could not be found.";

    /** Error Message for class for name error. */
    private static final String ERROR_MESSAGES_CLASS_FOR_NAME =
            "Invalid Torque OM setup for Database \"{0}\".\n"
                    + "Something unexpected happened doing Class.forName(\"{1}\").  "
                    + "See the nested exception for details.";

    /** Error Message for class for init error. */
    private static final String ERROR_MESSAGES_INIT =
            "Invalid Torque OM setup for Database \"{0}\".\n"
                    + "An error occured invoking the init() method in class, \"{1}\"";

    /**
     * The name of the database where this databaseMap belongs to,
     * for internal purposes only.
     */
    private final String name;

    /** Name of the tables in the database. */
    private final Map<String, TableMap> tables
    	= Collections.synchronizedMap(new LinkedHashMap<String, TableMap>());

    /** The id Table. */
    private TableMap idTable = null;

    /** Flag indicating that all tables have been loaded via initialize() */
    private boolean isInitialized = false;

    /**
     * Constructs a new DatabaseMap.
     * @param database the databsae to map
     * @throws NullPointerException if the database is not provided
     */
    public DatabaseMap(final Database database)
    {
        if (database == null)
        {
            throw new NullPointerException("database must not be null");
        }
        this.name = database.getName();
    }

    /**
     * The name of the database to which this database map belongs.
     *
     * @return the database name, not null.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Does this database contain this specific table?
     *
     * @param table The TableMap representation of the table.
     * @return True if the database contains the table.
     */
    public boolean containsTable(final TableMap table)
    {
        return containsTable(table.getName());
    }

    /**
     * Does this database contain this specific table?
     *
     * @param name The String representation of the table.
     * @return True if the database contains the table.
     */
    public boolean containsTable(String name)
    {
        if (name.indexOf('.') > 0)
        {
            name = name.substring(0, name.indexOf('.'));
        }
        boolean found = tables.containsKey(name);
        if (!found && idTable != null)
        {
            return idTable.getName().equals(name);
        }
        return found;
    }

    /**
     * Get a TableMap for the table by name. <p>
     *
     * Note that by default Torque uses lazy initialization to minimize
     * memory usage and startup time.  However, if an OM or PEER class
     * has not called the table's MapBuilder class, it will not be here.
     * See the optional initialize method if you need full OM Mapping.<p>
     *
     * @param name Name of the table.
     * @return A TableMap, null if the table was not found.
     */
    public TableMap getTable(final String name)
    {
        TableMap result = tables.get(name);
        if (result != null)
        {
            return result;
        }
        if (idTable != null && idTable.getName().equals(name))
        {
            return idTable;
        }
        return null;
    }

    /**
     * Get a TableMap[] of all of the tables in the database.<P>
     *
     * Note that by default Torque uses lazy initialization to minimize
     * memory usage and startup time.  However, if an OM or PEER class
     * has not called the table's MapBuilder class, it will not be here.
     * See the optional initialize method if you need full OM Mapping.<p>
     *
     * @return A TableMap[].
     */
    public TableMap[] getTables()
    {
        int size = tables.size();
        if (idTable != null)
        {
            size++;
        }
        TableMap[] dbTables = tables.values()
                .toArray(new TableMap[size]);
        if (idTable != null)
        {
            dbTables[size - 1] = idTable;
        }
        return dbTables;
    }

    /**
     * Add a new table to the database by name.  It creates an empty
     * TableMap that you need to populate.
     *
     * @param tableName The name of the table.
     *
     * @return the new table map.
     */
    public TableMap addTable(final String tableName)
    {
        TableMap tmap = new TableMap(tableName, this);
        tables.put(tableName, tmap);
        return tmap;
    }

    /**
     * Add a new TableMap to the database.
     *
     * @param idTableMap The TableMap representation.
     */
    public void setIdTable(final TableMap idTableMap)
    {
        this.idTable = idTableMap;
    }

    /**
     * Fully populate this DatabaseMap with all the TablesMaps.  This
     * is only needed if the application needs to use the complete OM
     * mapping information.  Otherwise, the OM Mapping information
     * will be populated as needed by OM and Peer classes.  An example
     * of how to initialize the map info from the application:<p>
     *
     *   <code>
     *   DatabaseMap dbMap = Torque.getDatabaseMap( dbName );
     *   try {
     *      dbMap.initialize();
     *   } catch ( TorqueException e ) {
     *      ... error handling
     *   }
     *   </code>
     *
     * Note that Torque database names are case sensitive and this DB
     * map must be retrieved with the exact name used in the XML schema.<p>
     *
     * This uses Java reflection methods to locate and run the
     * init() method of a class generated in the org.apache.torque.linkage
     * package with a name based on the XML Database name value, e.g.
     * org.apache.torque.linkage.DefaultMapInit<p>
     *
     * Some misconfiguration situations that could cause this method to fail
     * are:<p>
     *
     * The class(es) in the org.apache.torque.linkage package were not included
     * with the other generated class files (e.g. the jar file creation process
     * only included com.* and not org.* files).<p>
     *
     * @throws TorqueException If an error is encountered locating and calling
     *                          the init method.
     */
    public synchronized void initialize() throws TorqueException
    {
        if (isInitialized)
        {
            return;
        }
        String initClassName = MessageFormat.format(INIT_CLASS_NAME_FORMAT,
                new Object[] {
                        javanameMethod(name)
        });

        Class<?> initClass = null;
        try
        {
            initClass = Class.forName(initClassName);
        }
        catch (ClassNotFoundException e)
        {
            throw new TorqueException(MessageFormat.format(
                    ERROR_MESSAGES_CLASS_NOT_FOUND,
                    new Object[] {
                            name,
                            initClassName
                    }),
                    e);
        }
        catch (LinkageError e)
        {
            throw new TorqueException(MessageFormat.format(
                    ERROR_MESSAGES_DEPENDENT_CLASS_NOT_FOUND,
                    new Object[] {
                            name,
                            initClassName
                    }),
                    e);
        }
        catch (Throwable e)
        {
            throw new TorqueException(MessageFormat.format(
                    ERROR_MESSAGES_CLASS_FOR_NAME,
                    new Object[] {
                            name,
                            initClassName
                    }),
                    e);
        }
        try
        {
            Method initMethod = initClass.getMethod("init", (Class []) null);
            initMethod.invoke(null, (Object []) null);
        }
        catch (Exception e)
        {
            throw new TorqueException(MessageFormat.format(
                    ERROR_MESSAGES_INIT,
                    new Object[] {
                            name,
                            initClassName
                    }),
                    e);
        }
        isInitialized = true;
    }

    /**
     * Converts a database schema name to java object name.  Operates
     * same as underscoreMethod but does not convert anything to
     * lowercase.  This must match the javaNameMethod in the
     * JavaNameGenerator class in Generator code.
     *
     * @param schemaName name to be converted.
     *
     * @return converted name.
     */
    protected String javanameMethod(String schemaName)
    {
        StringBuilder result = new StringBuilder();
        StringTokenizer tok = new StringTokenizer
                (schemaName, String.valueOf(STD_SEPARATOR_CHAR));
        while (tok.hasMoreTokens())
        {
            String namePart = (String) tok.nextElement();
            result.append(StringUtils.capitalize(namePart));
        }

        // remove the SCHEMA_SEPARATOR_CHARs and capitalize
        // the tokens
        schemaName = result.toString();
        result = new StringBuilder();

        tok = new StringTokenizer
                (schemaName, String.valueOf(SCHEMA_SEPARATOR_CHAR));
        while (tok.hasMoreTokens())
        {
            String namePart = (String) tok.nextElement();
            result.append(StringUtils.capitalize(namePart));
        }
        return result.toString();
    }

    /**
     * Copy all settings except the database from another database map.
     *
     * @param databaseMap the database map to copy from, not null.
     */
    public void copyFrom(final DatabaseMap databaseMap)
    {
        this.isInitialized = databaseMap.isInitialized;

        this.clearOptions();
        databaseMap.getOptions().forEach((key, value) -> this.setOption(key, value));

        this.tables.clear();
        this.tables.putAll(databaseMap.tables);
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("DatabaseMap[name=")
        .append(name)
        .append(", tables=(")
        .append(tables.values().stream()
                .map(table -> table.getName())
                .collect(Collectors.joining(",")))
        .append(")]");
        return result.toString();
    }
}
