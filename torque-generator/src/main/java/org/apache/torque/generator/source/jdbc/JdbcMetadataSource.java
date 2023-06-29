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

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.jdbc.SchemaType;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.SourceImpl;

/**
 * A source which reads the data from JDBC Metadata.
 *
 * @version $Id: JdbcMetadataSource.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class JdbcMetadataSource extends SourceImpl
{
    /**
     * The position in table metadata containing the table name.
     */
    private static final int TABLE_NAME_POS_IN_TABLE_METADATA = 3;

    /**
     * The position in column metadata containing the column name.
     */
    private static final int COLUMN_NAME_POS_IN_COLUMN_METADATA = 4;

    /**
     * The position in column metadata containing the data type
     * (as SQL type from java.sql.Types).
     */
    private static final int DATA_TYPE_POS_COLUMN_METADATA = 5;

    /**
     * The position in column metadata containing the column size.
     */
    private static final int COLUMN_SIZE_POS_IN_COLUMN_METADATA = 7;

    /**
     * The position in column metadata containing the number
     * of fractional digits.
     */
    private static final int DECIMAL_DIGITS_POS_IN_COLUMN_METADATA = 9;

    /**
     * The position in column metadata telling whether null is allowed as value
     * for that column.
     */
    private static final int NULLABLE_POS_IN_COLUMN_METADATA = 11;

    /**
     * The position in column metadata containing the column's default value.
     */
    private static final int DEFAULT_VALUE_POS_IN_COLUMN_METADATA = 13;

    /**
     * The position in primary key metadata containing the column name.
     */
    private static final int COLUMN_NAME_POS_IN_PRIMARY_KEY_METADATA = 4;

    /**
     * The position in foreign key metadata containing the column name.
     */
    private static final int TABLE_NAME_POS_IN_FOREIGN_KEY_METADATA = 3;

    /**
     * The position in foreign key metadata containing the foreign column name.
     */
    private static final int FOREIGN_COLUMN_NAME_POS_IN_FOREIGN_KEY_METADATA
    = 4;

    /**
     * The position in foreign key metadata containing the localcolumn name.
     */
    private static final int LOCAL_COLUMN_NAME_POS_IN_FOREIGN_KEY_METADATA = 8;

    /**
     * The position in foreign key metadata containing the foreign key name.
     */
    private static final int FOREIGN_KEY_NAME_POS_IN_FOREIGN_KEY_METADATA = 12;


    /** The class log. */
    private static Log log = LogFactory.getLog(JdbcMetadataSource.class);

    /** The fully qualified class name of the database driver. */
    private final String driver;

    /** The connection url to the database, */
    private final String url;

    /** The username to connect to the database. */
    private final String username;

    /** The password to connect to the database. */
    private final String password;

    /** Which database(mysql) or schema (oracle) should be read. */
    private final String schema;

    /**
     * Constructor.
     *
     * @param driver the database driver class, not null.
     * @param url the connection url, not null.
     * @param username the username of the database user.
     * @param password the password of the database user.
     * @param schema the schema to read.
     */
    public JdbcMetadataSource(
            final String driver,
            final String url,
            final String username,
            final String password,
            final String schema)
    {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.schema = schema;
    }

    @Override
    protected SourceElement createRootElement() throws SourceException
    {
        SourceElement rootElement = new SourceElement("database");
        {
            try
            {
                Class.forName(driver);
            }
            catch (ClassNotFoundException e)
            {
                throw new SourceException(
                        "Could not find database driver class " + driver, e);
            }
            log.debug("DB driver " + driver + " loaded");
        }

        try (Connection con = DriverManager.getConnection(url, username, password))
        {
            log.debug("DB connection to database " + url + " established");

            DatabaseMetaData dbMetaData = con.getMetaData();

            List<String> tableList = getTableNames(dbMetaData, schema);

            for (int i = 0; i < tableList.size(); i++)
            {
                // Add Table.
                String tableName = tableList.get(i);
                log.debug("Processing table: " + tableName);

                SourceElement table = new SourceElement("table");
                rootElement.getChildren().add(table);
                table.setAttribute("name", tableName);

                List<ColumnMetadata> columns
                = getColumns(dbMetaData, tableName, schema);
                Set<String> primaryKeys
                = getPrimaryKeys(dbMetaData, tableName, schema);

                addTableColumns(table, columns, primaryKeys);

                // Foreign keys for this table.
                Collection<ForeignKeyMetadata> forgnKeys
                = getForeignKeys(dbMetaData, tableName, schema);
                addTableForeignKeys(table, forgnKeys);
            }
        }
        catch (SQLException e)
        {
            throw new SourceException(
                    "Could not retrieve JDBC Metadata from url " + url, e);
        }

        return rootElement;
    }

    private void addTableForeignKeys(SourceElement table, Collection<ForeignKeyMetadata> forgnKeys) {
        for (ForeignKeyMetadata foreignKeyMetadata : forgnKeys)
        {
            SourceElement fk = new SourceElement("foreign-key");
            fk.setAttribute(
                    "foreignTable",
                    foreignKeyMetadata.getReferencedTable());
            for (int m = 0; m < foreignKeyMetadata.getLocalColumns().size(); m++)
            {
                SourceElement ref = new SourceElement("reference");
                ref.setAttribute("local", foreignKeyMetadata.getLocalColumns().get(m));
                ref.setAttribute("foreign", foreignKeyMetadata.getForeignColumns().get(m));
                fk.getChildren().add(ref);
            }
            table.getChildren().add(fk);
        }
    }

    private void addTableColumns(SourceElement table, List<ColumnMetadata> columns, Set<String> primaryKeys) {
        for (ColumnMetadata col : columns)
        {
            String name = col.getName();
            Integer type = col.getSqlType();
            int size = col.getSize().intValue();
            int scale = col.getDecimalDigits().intValue();

            Integer nullType = col.getNullType();
            String defValue = col.getDefValue();

            SourceElement column = new SourceElement("column");
            column.setAttribute("name", name);

            SchemaType schemaType = SchemaType.getByJdbcType(type);
            if (schemaType != null)
            {
                column.setAttribute("type", schemaType.toString());
            }

            if (size > 0 && (IntStream.of(Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.DECIMAL,
                    Types.NUMERIC).anyMatch(j -> type.intValue() == j)) )
            {
                column.setAttribute("size", String.valueOf(size));
            }

            if (scale > 0 && (IntStream.of( Types.DECIMAL, Types.NUMERIC))
                    .anyMatch(j -> j == type.intValue()) )
            {
                column.setAttribute("scale", String.valueOf(scale));
            }

            if (primaryKeys.contains(name))
            {
                column.setAttribute("primaryKey", "true");
            }
            else if (nullType.intValue() == 0)
            {
                column.setAttribute("required", "true");
            }

            if (StringUtils.isNotEmpty(defValue))
            {
                // trim out parens & quotes out of def value.
                // makes sense for MSSQL. not sure about others.
                if (defValue.startsWith("(") && defValue.endsWith(")"))
                {
                    defValue = defValue.substring(1, defValue.length() - 1);
                }

                if (defValue.startsWith("'") && defValue.endsWith("'"))
                {
                    defValue = defValue.substring(1, defValue.length() - 1);
                }

                column.setAttribute("default", defValue);
            }
            table.getChildren().add(column);
        }
    }

    @Override
    public String getDescription()
    {
        return "JdbcMetadataSource using url " + url;
    }

    @Override
    public File getSourceFile()
    {
        return null;
    }

    /**
     * Get all the table names in the current database that are not
     * system tables.
     *
     * @param dbMeta JDBC database metadata.
     * @return The list of all the tables in a database.
     * @throws SQLException
     */
    List<String> getTableNames(final DatabaseMetaData dbMeta, final String dbSchema)
            throws SQLException
    {
        log.debug("Getting table list...");
        List<String> tables = new ArrayList<>();
        // these are the entity types we want from the database
        String[] types = {"TABLE", "VIEW"};
        try (ResultSet tableNames = dbMeta.getTables(null, dbSchema, "%", types))
        {
            while (tableNames.next())
            {
                String name = tableNames.getString(
                        TABLE_NAME_POS_IN_TABLE_METADATA);
                tables.add(name);
            }
        }
        return tables;
    }

    /**
     * Retrieves all the column names and types for a given table from
     * JDBC metadata.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve column information.
     *
     * @return The list of columns in <code>tableName</code>.
     *
     * @throws SQLException if an sql error occurs during information retrieval.
     */
    List<ColumnMetadata> getColumns(
            final DatabaseMetaData dbMeta,
            final String tableName,
            final String dbSchema)
                    throws SQLException
    {
        List<ColumnMetadata> columns = new ArrayList<>();
        try (ResultSet columnSet = dbMeta.getColumns(null, dbSchema, tableName, null))
        {
            while (columnSet.next())
            {
                String name = columnSet.getString(
                        COLUMN_NAME_POS_IN_COLUMN_METADATA);
                Integer sqlType = Integer.valueOf(columnSet.getString(
                        DATA_TYPE_POS_COLUMN_METADATA));
                Integer size = Integer.valueOf(columnSet.getInt(
                        COLUMN_SIZE_POS_IN_COLUMN_METADATA));
                Integer decimalDigits = Integer.valueOf(columnSet.getInt(
                        DECIMAL_DIGITS_POS_IN_COLUMN_METADATA));
                Integer nullType = Integer.valueOf(columnSet.getInt(
                        NULLABLE_POS_IN_COLUMN_METADATA));
                String defValue = columnSet.getString(
                        DEFAULT_VALUE_POS_IN_COLUMN_METADATA);

                ColumnMetadata column = new ColumnMetadata(
                        name,
                        sqlType,
                        size,
                        nullType,
                        defValue,
                        decimalDigits);
                columns.add(column);
            }
        }

        return columns;
    }

    /**
     * Retrieves a list of the columns composing the primary key for a given
     * table.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve PK information.
     * @return A list of the primary key parts for <code>tableName</code>.
     * @throws SQLException
     */
    Set<String> getPrimaryKeys(
            final DatabaseMetaData dbMeta,
            final String tableName,
            final String schemaName)
                    throws SQLException
    {
        Set<String> pk = new HashSet<>();
        try (ResultSet parts = dbMeta.getPrimaryKeys(null, schemaName, tableName))
        {
            while (parts.next())
            {
                pk.add(parts.getString(
                        COLUMN_NAME_POS_IN_PRIMARY_KEY_METADATA));
            }
        }

        return pk;
    }

    /**
     * Retrieves a list of foreign key columns for a given table.
     *
     * @param dbMeta JDBC metadata.
     * @param tableName Table from which to retrieve FK information.
     * @return A list of foreign keys in <code>tableName</code>.
     * @throws SQLException
     */
    Collection<ForeignKeyMetadata> getForeignKeys(
            final DatabaseMetaData dbMeta,
            final String tableName,
            final String schemaName)
                    throws SQLException
    {
        Map<String, ForeignKeyMetadata> foreignKeys
            = new HashMap<>();
        try (ResultSet resultSet = dbMeta.getImportedKeys(null, schemaName, tableName))
        {
            while (resultSet.next())
            {
                String refTableName = resultSet.getString(
                        TABLE_NAME_POS_IN_FOREIGN_KEY_METADATA);
                String fkName = resultSet.getString(
                        FOREIGN_KEY_NAME_POS_IN_FOREIGN_KEY_METADATA);
                // if FK has no name - make it up (use tablename instead)
                if (fkName == null)
                {
                    fkName = refTableName;
                }
                ForeignKeyMetadata fk = foreignKeys.get(fkName);
                if (fk == null)
                {
                    fk = new ForeignKeyMetadata();
                    fk.setReferencedTable(refTableName);
                    fk.setForeignKeyName(fkName);
                    foreignKeys.put(fkName, fk);
                }
                fk.getLocalColumns().add(resultSet.getString(
                        LOCAL_COLUMN_NAME_POS_IN_FOREIGN_KEY_METADATA));
                fk.getForeignColumns().add(resultSet.getString(
                        FOREIGN_COLUMN_NAME_POS_IN_FOREIGN_KEY_METADATA));
            }
        }
        catch (SQLException e)
        {
            // this seems to be happening in some db drivers (sybase)
            // when retrieving foreign keys from views.
            log.warn("WARN: Could not read foreign keys for Table "
                    + tableName
                    + " : "
                    + e.getMessage());
        }

        return foreignKeys.values();
    }

    /**
     * Returns the last modification date of the source files.
     *
     * @return always null because no source file exist.
     */
    @Override
    public Date getLastModified()
    {
        return null;
    }

    /**
     * Returns the checksum of the content.
     *
     * @return always null.
     */
    @Override
    public byte[] getContentChecksum()
    {
        // Although we could determine a checksum for the content,
        // doing so makes no sense because we cannot determine
        // a last modified date.
        return null;
    }
}
