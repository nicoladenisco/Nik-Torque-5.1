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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.Database;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.IDMethod;

/**
 * TableMap is used to model a table in a database.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: TableMap.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class TableMap extends OptionSupport implements Serializable
{
    /**
     * Serial version.
     */
    private static final long serialVersionUID = 1L;

    /** The dot String. */
    private static final String DOT = ".";

    /** The columns in the table. XML Order is preserved. */
    private final Map<String, ColumnMap> columns
    = Collections.synchronizedMap(
            new LinkedHashMap<String, ColumnMap>());

    /** The foreign keys in the table. XML Order is preserved. */
    private final List<ForeignKeyMap> foreignKeys = new ArrayList<>();

    /** The database this table belongs to. */
    private DatabaseMap dbMap;

    /** The name of the table. */
    private String tableName;

    /**
     * The name of the schema to which this table belongs,
     * or null for the default schema.
     */
    private String schemaName;

    /** The JavaName of the table as defined in XML */
    private String javaName;

    /** The prefix on the table name. */
    private String prefix;

    /** The primary key generation method. */
    private IDMethod primaryKeyMethod = IDMethod.NO_ID_METHOD;

    /** The table description info. */
    private String description = "";

    /** The Peer Class for this table. */
    private Class<?> peerClass;

    /** The OM Root Class for this table. */
    private Class<?> omClass;

    /** Whether any column uses Inheritance. */
    private boolean useInheritance = false;

    /** Whether cache managers are used. */
    private boolean useManager = false;

    /** The associated cache manager class. */
    private Class<?> managerClass;

    /** Overrides the information stored in the pkInfoMap for all id methods. */
    private Object pkInfoOverride;

    /**
     * Stores information that is needed for generating primary keys.
     * The information is keyed by the idMethodType because it might be
     * different for different id methods.
     */
    private final Map<IDMethod, Object> pkInfoMap
        = new HashMap<>();

    /**
     * Constructor.
     *
     * @param tableName The name of the table, may be prefixed with a
     *        schema name, not null.
     * @param containingDB A DatabaseMap that this table belongs to.
     */
    public TableMap(String tableName, DatabaseMap containingDB)
    {
        setTableName(tableName);
        dbMap = containingDB;
    }

    /**
     * Constructor.
     *
     * @param tableName The name of the table, may be prefixed with a
     *        schema name, not null.
     * @param prefix The prefix for the table name (ie: SCARAB for
     *        SCARAB_PROJECT).
     * @param containingDB A DatabaseMap that this table belongs to.
     */
    public TableMap(String tableName,
            String prefix,
            DatabaseMap containingDB)
    {
        setTableName(tableName);
        this.prefix = prefix;
        dbMap = containingDB;
    }

    private void setTableName(String tableName)
    {
        if (tableName == null)
        {
            throw new NullPointerException("tableName must not be null");
        }
        int dotIndex = tableName.indexOf(DOT);
        if (dotIndex != -1)
        {
            this.schemaName = tableName.substring(0, dotIndex);
            this.tableName = tableName.substring(dotIndex + 1);
        }
        else
        {
            this.tableName = tableName;
        }
    }

    /**
     * Sets the database map this table belongs to.
     * @param databaseMap
     */
    void setDatabaseMap(DatabaseMap databaseMap)
    {
        dbMap = databaseMap;
    }

    /**
     * Does this table contain the specified column?
     *
     * @param column A ColumnMap.
     * @return True if the table contains the column.
     */
    public boolean containsColumn(ColumnMap column)
    {
        return containsColumn(column.getColumnName());
    }

    /**
     * Does this table contain the specified column?
     *
     * @param name A String with the name of the column.
     * @return True if the table contains the column.
     */
    public boolean containsColumn(String name)
    {
        if (name.indexOf('.') > 0)
        {
            name = name.substring(name.indexOf('.') + 1);
        }
        return columns.containsKey(name);
    }

    /**
     * Get the DatabaseMap containing this TableMap.
     *
     * @return A DatabaseMap.
     */
    public DatabaseMap getDatabaseMap()
    {
        return dbMap;
    }

    /**
     * Returns true if this tableMap contains a column with object
     * data.  If the type of the column is not a string, a number or a
     * date, it is assumed that it is object data.
     *
     * @return True if map contains a column with object data.
     */
    public boolean containsObjectColumn()
    {
        synchronized (columns)
        {
            Iterator<ColumnMap> it = columns.values().iterator();
            while (it.hasNext())
            {
                Object theType = it.next().getType();
                if (!(theType instanceof String || theType instanceof Number
                        || theType instanceof java.util.Date))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the name of the Table, not prefixed by a possible schema name
     *
     * @return A String with the name of the table, not null.
     */
    public String getName()
    {
        return tableName;
    }

    /**
     * Get the schema to which the table belongs to.
     *
     * @return the schema name, or null if the default schema should be used.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Returns the fully qualified table name, if available.
     *
     * @return the fully qualified table name, if a schema is set,
     *         or just the table name if no schema is set, not null.
     */
    public String getFullyQualifiedTableName()
    {
        if (!StringUtils.isEmpty(schemaName))
        {
            return schemaName + DOT + tableName;
        }
        return tableName;
    }

    /**
     * Get the Java name of the table as defined in XML.
     *
     * @return A String with the Java name of the table.
     */
    public String getJavaName()
    {
        return javaName;
    }

    /**
     * Set the Java name of the table as defined by generator/XML.
     *
     * @param value A String with the Java name of the table.
     */
    public void setJavaName(String value)
    {
        this.javaName = value;
    }

    /**
     * Get table prefix name.
     *
     * @return A String with the prefix.
     */
    public String getPrefix()
    {
        return this.prefix;
    }

    /**
     * Set table prefix name.
     *
     * @param prefix The prefix for the table name (ie: SCARAB for
     * SCARAB_PROJECT).
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * Get the method used to generate primary keys for this table.
     *
     * @return A String with the method.
     */
    public IDMethod getPrimaryKeyMethod()
    {
        return primaryKeyMethod;
    }

    /**
     * Get the information used to generate a primary key
     *
     * @param idMethod <code>IDMethod</code> used to generate a primary key
     * @return An Object.
     */
    public Object getPrimaryKeyMethodInfo(IDMethod idMethod)
    {
        if (pkInfoOverride != null)
        {
            return pkInfoOverride;
        }
        return pkInfoMap.get(idMethod);
    }

    /**
     * Get a ColumnMap[] of the columns in this table.
     *
     * @return A ColumnMap[].
     */
    public ColumnMap[] getColumns()
    {
        ColumnMap[] tableColumns = columns.values()
                .toArray(new ColumnMap[0]);

        return tableColumns;
    }

    /**
     * Get all foreign keys in the table..
     *
     * @return All foreign keys, not null.
     */
    public List<ForeignKeyMap> getForeignKeys()
    {
        return Collections.unmodifiableList(foreignKeys);
    }

    /**
     * Get a ColumnMap for the named table.
     *
     * @param name A String with the name of the table.
     * @return A ColumnMap.
     */
    public ColumnMap getColumn(String name)
    {
        try
        {
            return columns.get(name);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Add a pre-created column to this table.  It will replace any
     * existing column.
     *
     * @param cmap A ColumnMap.
     */
    public void addColumn(ColumnMap cmap)
    {
        columns.put(cmap.getColumnName(), cmap);
    }

    /**
     * Add a foreign key to this table.
     *
     * @param foreignKey the foreign key map, not null
     */
    public void addForeignKey(ForeignKeyMap foreignKey)
    {
        foreignKeys.add(foreignKey);
    }

    /**
     * Sets the method used to generate a key for this table.  Valid
     * values are as specified in the {@link
     * org.apache.torque.adapter.IDMethod} interface.
     *
     * @param method The ID generation method type, not null.
     */
    public void setPrimaryKeyMethod(IDMethod method)
    {
        if (method == null)
        {
            throw new NullPointerException("method must not be null");
        }
        primaryKeyMethod = method;
        if (IDMethod.ID_BROKER == method)
        {
            Database database = Torque.getOrCreateDatabase(
                    getDatabaseMap().getName());
            database.createAndRegisterIdBroker();
        }
    }

    /**
     * Sets the pk information needed to generate a key.
     * This overrides all information set by
     * <code>setPrimaryKeyMethodInfo(String, Object)</code>.
     *
     * @param pkInfo information needed to generate a key
     */
    public void setPrimaryKeyMethodInfo(Object pkInfo)
    {
        pkInfoOverride = pkInfo;
    }

    /**
     * Sets the pk information needed to generate a key.
     *
     * @param idMethod the id method for which this information is stored.
     * @param pkInfo information needed to generate a key.
     */
    public void setPrimaryKeyMethodInfo(IDMethod idMethod, Object pkInfo)
    {
        pkInfoMap.put(idMethod, pkInfo);
    }

    //---Utility methods for doing intelligent lookup of table names

    /**
     * Tell me if i have PREFIX in my string.
     *
     * @param data A String.
     * @return True if prefix is contained in data.
     */
    private boolean hasPrefix(String data)
    {
        return (data.indexOf(getPrefix()) != -1);
    }

    /**
     * Removes the PREFIX.
     *
     * @param data A String.
     * @return A String with data, but with prefix removed.
     */
    private String removePrefix(String data)
    {
        return data.substring(getPrefix().length());
    }

    /**
     * Removes the PREFIX, removes the underscores and makes
     * first letter caps.
     *
     * SCARAB_FOO_BAR becomes FooBar.
     *
     * @param data A String.
     * @return A String with data processed.
     */
    public final String removeUnderScores(String data)
    {
        String tmp = null;
        StringBuilder out = new StringBuilder();
        if (hasPrefix(data))
        {
            tmp = removePrefix(data);
        }
        else
        {
            tmp = data;
        }

        StringTokenizer st = new StringTokenizer(tmp, "_");
        while (st.hasMoreTokens())
        {
            String element = ((String) st.nextElement()).toLowerCase();
            out.append(StringUtils.capitalize(element));
        }
        return out.toString();
    }

    /**
     * Returns the table description info.
     *
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the table description.
     *
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns the OM class for this table.
     *
     * @return the OM class.
     */
    public Class<?> getOMClass()
    {
        return omClass;
    }

    /**
     * Sets the OM root class for this table.
     *
     * @param omClass The OM root class for this table.
     */
    public void setOMClass(Class<?> omClass)
    {
        this.omClass = omClass;
    }

    /**
     * Returns the Peer Class for this table.
     *
     * @return The peerClass for this table.
     */
    public Class<?> getPeerClass()
    {
        return peerClass;
    }

    /**
     * Sets the Peer class for this table.
     *
     * @param peerClass The peerClass to set.
     */
    public void setPeerClass(Class<?> peerClass)
    {
        this.peerClass = peerClass;
    }

    /**
     * Returns the database map for this table.
     *
     * @return the database map for this table.
     */
    public DatabaseMap getDbMap()
    {
        return dbMap;
    }

    /**
     * Returns whether this table uses inheritance.
     *
     * @return whether inheritance is used.
     */
    public boolean isUseInheritance()
    {
        return useInheritance;
    }

    /**
     * Sets whether this table uses inheritance.
     *
     * @param useInheritance whether this table uses inheritance.
     */
    public void setUseInheritance(boolean useInheritance)
    {
        this.useInheritance = useInheritance;
    }

    /**
     * Returns whether managers are used for this table.
     *
     * @return whether managers are used for this table.
     */
    public boolean isUseManager()
    {
        return useManager;
    }

    /**
     * Sets whether managers are used for this table.
     *
     * @param useManager whether managers are used for this table.
     */
    public void setUseManager(boolean useManager)
    {
        this.useManager = useManager;
    }

    /**
     * Returns the manager class for this table.
     *
     * @return the managerClass.
     */
    public Class<?> getManagerClass()
    {
        return managerClass;
    }

    /**
     * Sets the manager class for this table.
     *
     * @param managerClass the manager class for this table.
     */
    public void setManagerClass(Class<?> managerClass)
    {
        this.managerClass = managerClass;
    }

    /**
     * Returns the single primary key of this table, if it exists
     *
     * @return the single primary key column.
     *
     * @throws TorqueException If the table has no primary key
     *         or if the table has multiple primary keys.
     */
    public ColumnMap getPrimaryKey()
            throws TorqueException
    {
        Set<ColumnMap> result = new HashSet<>();

        columns.values().forEach(column ->
        {
            if (column.isPrimaryKey())
            {
                result.add(column);
            }
        });
        if (result.isEmpty())
        {
            throw new TorqueException("getPrimaryKey(): Table " + tableName
                    + " has no primary key.");
        }
        if (result.size() > 1)
        {
            throw new TorqueException("getPrimaryKey(): Table " + tableName
                    + " has more than one primary key.");
        }
        return result.iterator().next();
    }

    @Override
    public String toString()
    {
        return "TableMap[" + tableName + "]";
    }
}
