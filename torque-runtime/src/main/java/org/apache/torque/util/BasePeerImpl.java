package org.apache.torque.util;

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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.Database;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.TorqueRuntimeException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.IDMethod;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.criteria.FromElement;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.MapHelper;
import org.apache.torque.map.TableMap;
import org.apache.torque.oid.IdGenerator;
import org.apache.torque.oid.SequenceIdGenerator;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.om.StringKey;
import org.apache.torque.om.mapper.RecordMapper;
import org.apache.torque.sql.Query;
import org.apache.torque.sql.SqlBuilder;

/**
 * This is the base class for all Peer classes in the system.  Peer
 * classes are responsible for isolating all of the database access
 * for a specific business object.  They execute all of the SQL
 * against the database.  Over time this class has grown to include
 * utility methods which ease execution of cross-database queries and
 * the implementation of concrete Peers.
 *
 * @param <T> The data object class for this Peer.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:vido@ldh.org">Augustin Vidovic</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: BasePeerImpl.java 1879929 2020-07-16 07:42:57Z gk $
 */
public class BasePeerImpl<T> implements Serializable
{
    /**
     * Serial Version
     */
    private static final long serialVersionUID = -7702123730779032381L;

    /** the log */
    private static final Logger log = LogManager.getLogger(BasePeerImpl.class);

    /** An injected instance of a record mapper to map JDBC result sets to objects */
    private RecordMapper<T> recordMapper = null;

    /** An injected instance of a table map */
    private TableMap tableMap = null;

    /** An injected instance of the database name */
    private String databaseName = null;

    /**
     * Default constructor
     */
    public BasePeerImpl()
    {
        super();
    }

    /**
     * Constructor providing the objects to be injected as parameters.
     *
     * @param recordMapper a record mapper to map JDBC result sets to objects
     * @param tableMap the default table map
     * @param databaseName the name of the database
     */
    public BasePeerImpl(final RecordMapper<T> recordMapper, final TableMap tableMap, final String databaseName)
    {
        this();
        setRecordMapper(recordMapper);
        setTableMap(tableMap);
        setDatabaseName(databaseName);
    }

    /**
     * Set the record mapper for this instance.
     *
     * @param recordMapper the recordMapper to set
     */
    public void setRecordMapper(final RecordMapper<T> recordMapper)
    {
        this.recordMapper = recordMapper;
    }

    /**
     * Get the record mapper for this instance.
     *
     * @return the recordMapper
     */
    public RecordMapper<T> getRecordMapper()
    {
        if (recordMapper == null)
        {
            throw new TorqueRuntimeException("No record mapper injected");
        }

        return recordMapper;
    }

    /**
     * Set the default table map for this instance.
     *
     * @param tableMap the tableMap to set
     */
    public void setTableMap(final TableMap tableMap)
    {
        this.tableMap = tableMap;
    }

    /**
     * Get the default table map for this instance.
     *
     * @return the tableMap
     */
    public TableMap getTableMap()
    {
        if (tableMap == null)
        {
            throw new TorqueRuntimeException("No table map injected");
        }

        return tableMap;
    }

    /**
     * Set the database name for this instance.
     *
     * @param databaseName the databaseName to set
     */
    public void setDatabaseName(final String databaseName)
    {
        this.databaseName = databaseName;
    }

    /**
     * Get the database name for this instance.
     *
     * @return the databaseName
     */
    public String getDatabaseName()
    {
        if (databaseName == null)
        {
            throw new TorqueRuntimeException("No database name injected");
        }

        return databaseName;
    }

    /**
     * Deletes rows from a database table.
     *
     * @param criteria defines the rows to be deleted, not null.
     *
     * @return the number of deleted rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doDelete(final Criteria criteria)
            throws TorqueException
    {
        setDbName(criteria);
        try (TorqueConnection connection = Transaction.begin(criteria.getDbName()))
        {
            int deletedRows = doDelete(criteria, connection);
            Transaction.commit(connection);
            return deletedRows;
        }
    }

    /**
     * Deletes rows from a table.  This method is to be used
     * during a transaction, otherwise use the doDelete(Criteria) method.
     *
     * @param criteria defines the rows to be deleted, not null.
     * @param connection the connection to use, not null.
     *
     * @return the number of deleted rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doDelete(
            final Criteria criteria,
            final Connection connection)
                    throws TorqueException
    {
        correctBooleans(criteria);
        setDbName(criteria);

        Query query = SqlBuilder.buildQuery(criteria);
        query.setType(Query.Type.DELETE);

        String fullTableName;
        if (tableMap == null)
        {
            fullTableName = SqlBuilder.guessFullTableFromCriteria(criteria);
        }
        else
        {
            fullTableName = SqlBuilder.getFullTableName(
                    tableMap.getFullyQualifiedTableName(),
                    criteria.getDbName());
        }
        boolean ownTableAdded = false;
        for (FromElement fromElement : query.getFromClause())
        {
            // Table names are case insensitive in known databases
            // so use case-insensitive compare
            if (fullTableName.equalsIgnoreCase(fromElement.getFromExpression()))
            {
                ownTableAdded = true;
                break;
            }
        }
        if (!ownTableAdded)
        {
            query.getFromClause().add(new FromElement(fullTableName));
        }
        String sql = query.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            List<Object> replacements = setPreparedStatementReplacements(
                    preparedStatement,
                    query.getPreparedStatementReplacements(),
                    0);
            StopWatch stopWatch = new StopWatch();
            log.debug("Executing delete {}, parameters = {}", sql, replacements);

            stopWatch.start();
            int affectedRows = preparedStatement.executeUpdate();
            log.trace("Delete took {} milliseconds", () -> stopWatch.getTime());

            return affectedRows;
        }
        catch (SQLException e)
        {
            throw ExceptionMapper.getInstance().toTorqueException(e);
        }
    }

    /**
     * Inserts a record into a database table.
     * <p>
     * If the primary key is included in Criteria, then that value will
     * be used to insert the row.
     * <p>
     * Otherwise, if the primary key can be generated automatically,
     * the generated key will be used for the insert and will be returned.
     * <p>
     * If no value is given for the primary key is defined and it cannot
     * be generated automatically or the table has no primary key,
     * the values will be inserted as specified and null will be returned.
     *
     * @param insertValues Contains the values to insert, not null.
     *
     * @return the primary key of the inserted row (if the table
     *         has a primary key) or null (if the table does not have
     *         a primary key).
     *
     * @throws TorqueException if a database error occurs.
     */
    public ObjectKey<?> doInsert(final ColumnValues insertValues)
            throws TorqueException
    {
        String databaseNameFromInsertValues = insertValues.getDbName();
        if (databaseNameFromInsertValues == null)
        {
            databaseNameFromInsertValues = getDatabaseName();
        }
        try (TorqueConnection connection = Transaction.begin(databaseNameFromInsertValues))
        {
            ObjectKey<?> id = doInsert(insertValues, connection);
            Transaction.commit(connection);
            return id;
        }
    }

    /**
     * Inserts a record into a database table.
     * <p>
     * If the primary key is included in Criteria, then that value will
     * be used to insert the row.
     * <p>
     * Otherwise, if the primary key can be generated automatically,
     * the generated key will be used for the insert and will be returned.
     * <p>
     * If no value is given for the primary key is defined and it cannot
     * be generated automatically or the table has no primary key,
     * the values will be inserted as specified and null will be returned.
     *
     * @param insertValues Contains the values to insert, not null.
     * @param connection the connection to use for the insert, not null.
     *
     * @return the primary key of the inserted row (if the table
     *         has a primary key) or null (if the table does not have
     *         a primary key).
     *
     * @throws TorqueException if a database error occurs.
     */
    public ObjectKey<?> doInsert(
            final ColumnValues insertValues,
            final Connection connection)
                    throws TorqueException
    {
        if (insertValues == null)
        {
            throw new TorqueException("insertValues is null");
        }
        if (connection == null)
        {
            throw new TorqueException("connection is null");
        }
        String databaseNameFromInsertValues = insertValues.getDbName();
        if (databaseNameFromInsertValues == null)
        {
            databaseNameFromInsertValues = getDatabaseName();
        }
        Database database = Torque.getDatabase(databaseNameFromInsertValues);
        Object keyInfo = getIdMethodInfo();
        IdGenerator keyGen = database.getIdGenerator(
                getTableMap().getPrimaryKeyMethod());

        SimpleKey<?> id = null;
        // can currently generate only single column pks, therefore a single
        // columnMap is ok
        ColumnMap primaryKey = null;
        if (keyGen != null)
        {
            // fail on multiple pks
            primaryKey = getTableMap().getPrimaryKey();

            // primaryKey will be null if there is no primary key
            // defined for the table we're inserting into.
            if (keyGen.isPriorToInsert() && primaryKey != null
                    && !insertValues.containsKey(primaryKey))
            {
                id = getId(primaryKey, keyGen, connection, keyInfo);
                insertValues.put(
                        primaryKey,
                        new JdbcTypedValue(id.getValue(), id.getJdbcType()));
            }
        }

        String columnNamesList = insertValues.keySet().stream()
                .map((column) -> column.getColumnName())
                .collect(Collectors.joining(",", "(", ")"));

        String fullTableName = SqlBuilder.getFullTableName(
                getTableMap().getFullyQualifiedTableName(),
                databaseNameFromInsertValues);
        StringBuilder query = new StringBuilder("INSERT INTO ")
                .append(fullTableName)
                .append(columnNamesList)
                .append(" VALUES (");

        boolean first = true;
        List<JdbcTypedValue> replacementObjects = new ArrayList<>();
        for (Map.Entry<Column, JdbcTypedValue> columnValue
                : insertValues.entrySet())
        {
            if (!first)
            {
                query.append(",");
            }
            if (columnValue.getValue().getSqlExpression() == null)
            {
                query.append("?");
                replacementObjects.add(columnValue.getValue());
            }
            else
            {
                Column sqlExpression = columnValue.getValue().getSqlExpression();
                query.append(sqlExpression.getSqlExpression());
            }
            first = false;
        }
        query.append(")");

        boolean useGetGeneratedKeys = keyGen != null
                && keyGen.isGetGeneratedKeysSupported()
                && primaryKey != null && !insertValues.containsKey(primaryKey);

        try (PreparedStatement preparedStatement = useGetGeneratedKeys ?
                        connection.prepareStatement(query.toString(),
                                Statement.RETURN_GENERATED_KEYS) :
                        connection.prepareStatement(query.toString()))
        {
            int position = 1;
            for (JdbcTypedValue replacementObject : replacementObjects)
            {
                Object value = replacementObject.getValue();
                if (value != null)
                {
                    if (replacementObject.getJdbcType() != Types.BLOB
                            && replacementObject.getJdbcType() != Types.CLOB)
                    {
                        preparedStatement.setObject(
                                position,
                                value,
                                replacementObject.getJdbcType());
                    }
                    else
                    {
                        preparedStatement.setObject(
                                position,
                                value);
                    }
                }
                else
                {
                    preparedStatement.setNull(
                            position,
                            replacementObject.getJdbcType());
                }
                position++;
            }

            StopWatch stopWatch = new StopWatch();
            log.debug("Executing insert {} using parameters {}", 
                    () -> query.toString(), () -> replacementObjects);

            stopWatch.start();
            preparedStatement.executeUpdate();
            log.trace("Insert took {} milliseconds", () -> stopWatch.getTime());

            if (keyGen != null && keyGen.isPostInsert()
                && primaryKey != null
                && !insertValues.containsKey(primaryKey))
            {
                if (keyGen.isGetGeneratedKeysSupported())
                {
                    // If the id-generator supports getGeneratedKeys(), get id
                    // now.
                    id = getId(primaryKey, keyGen, connection, preparedStatement);
                }
                else
                {
                    // If the primary key column is auto-incremented, get the id
                    // now.
                    id = getId(primaryKey, keyGen, connection, keyInfo);
                }
            }
        }
        catch (SQLException e)
        {
            throw ExceptionMapper.getInstance().toTorqueException(e);
        }

        return id;
    }

    /**
     * Executes a insert into...select statement.
     *
     * @param toInsertInto the columns in which to insert, not null.
     * @param criteria the criteria which selects the values to insert,
     *        not null.
     *
     * @return the number of inserted rows, not null.
     *
     * @throws TorqueException if a database error occurs.
     */
    public int doInsert(
            final Column[] toInsertInto,
            final Criteria criteria)
                    throws TorqueException
    {
        return doInsert(toInsertInto, criteria, (String) null);
    }

    /**
     * Executes a insert into...select statement.
     *
     * @param toInsertInto the columns in which to insert, not null.
     * @param criteria the criteria which selects the values to insert,
     *        not null.
     * @param dbName the database name, or null to take the database name
     *        from getDatabaseName().
     *
     * @return the number of inserted rows, not null.
     *
     * @throws TorqueException if a database error occurs.
     */
    public int doInsert(
            final Column[] toInsertInto,
            final Criteria criteria,
            final String dbName)
                    throws TorqueException
    {
        String dbNameToUse = dbName;
        if (dbNameToUse == null)
        {
            dbNameToUse = getDatabaseName();
        }
        try (TorqueConnection connection = Transaction.begin(dbNameToUse))
        {
            int numberOfInsertedRows
            = doInsert(toInsertInto, criteria, dbNameToUse, connection);
            Transaction.commit(connection);
            return numberOfInsertedRows;
        }
    }

    /**
     * Executes a insert into...select statement.
     *
     * @param toInsertInto the columns in which to insert, not null.
     * @param criteria the criteria which selects the values to insert,
     *        not null.
     * @param connection the database connection to use, not null.
     *
     * @return the number of inserted rows, not null.
     *
     * @throws TorqueException if a database error occurs.
     */
    public int doInsert(
            final Column[] toInsertInto,
            final Criteria criteria,
            final Connection connection)
                    throws TorqueException
    {
        return doInsert(toInsertInto, criteria, null, connection);
    }

    /**
     * Executes a insert into...select statement.
     *
     * @param toInsertInto the columns in which to insert, not null,
     *        must not contain null.
     * @param criteria the criteria which selects the values to insert,
     *        not null.
     * @param dbName the database name, or null to take the database name
     *        from getDatabaseName().
     * @param connection the database connection to use, not null.
     *
     * @return the number of inserted rows, not null.
     *
     * @throws TorqueException if a database error occurs.
     */
    public int doInsert(
            final Column[] toInsertInto,
            final Criteria criteria,
            String dbName,
            final Connection connection)
                    throws TorqueException
    {
        if (dbName == null)
        {
            dbName = getDatabaseName();
        }

        ColumnMap pk = getTableMap().getPrimaryKey();
        boolean pkExistsInColumnMap = false;

        List<String> columnNames = new ArrayList<>();
        for (Column column : toInsertInto)
        {
            columnNames.add(column.getColumnName());
            if (pk != null
                    && column.getSqlExpression().equals(pk.getSqlExpression()))
            {
                pkExistsInColumnMap = true;
            }
        }
        if (!pkExistsInColumnMap)
        {
            IDMethod idMethod = getTableMap().getPrimaryKeyMethod();
            Adapter adapter = Torque.getAdapter(dbName);
            if (idMethod == IDMethod.ID_BROKER)
            {
                log.debug("pk does not exist in column map and id method is {}, "
                        + "SQL will fail if column has no default value", () ->
                        idMethod);
                // try SQL, maybe database has a default value set
            }
            else if (idMethod == IDMethod.SEQUENCE
                    || (idMethod == IDMethod.NATIVE
                    && adapter.getIDMethodType() == IDMethod.SEQUENCE))
            {
                IdGenerator keyGen = Torque.getDatabase(dbName).getIdGenerator(
                        getTableMap().getPrimaryKeyMethod());
                if (keyGen instanceof SequenceIdGenerator)
                {
                    SequenceIdGenerator sequenceIdGenerator
                    = (SequenceIdGenerator) keyGen;
                    String idSql = sequenceIdGenerator.getIdSql(
                            getIdMethodInfo());
                    // This is a bit of a hack.
                    // The idSql is usually a stand-alone statement, but we
                    // need the part to be inserted as a column.
                    // Therefore we extract the complete word containing
                    // the term nextval
                    int nextvalPos = idSql.toLowerCase().indexOf("nextval");
                    int spacePos = idSql.lastIndexOf(" ", nextvalPos);
                    if (spacePos != -1)
                    {
                        idSql = idSql.substring(spacePos + 1);
                    }
                    spacePos = idSql.indexOf(" ");
                    if (spacePos != -1)
                    {
                        idSql = idSql.substring(0, idSql.indexOf(" "));
                    }
                    columnNames.add(pk.getColumnName());
                    criteria.addSelectColumn(
                            new ColumnImpl(null, null, null, idSql));
                }
                else
                {
                    log.warn("id method is sequence "
                            + "but keyGen is no Sequence id generator, "
                            + "cannot add sequence generation info");
                }
            }
        }

        String fullTableName = SqlBuilder.getFullTableName(
                getTableMap().getFullyQualifiedTableName(),
                dbName);
        Query selectQuery = SqlBuilder.buildQuery(criteria);
        StringBuilder query = new StringBuilder("INSERT INTO ")
                .append(fullTableName)
                .append("(")
                .append(StringUtils.join(columnNames, ","))
                .append(") ")
                .append(selectQuery);

        int numberOfInsertedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString()))
        {
            List<Object> replacements = setPreparedStatementReplacements(
                    preparedStatement,
                    selectQuery.getPreparedStatementReplacements(),
                    0);

            StopWatch stopWatch = new StopWatch();
            log.debug("Executing insert {} using parameters {}", 
                    () -> query.toString(), () -> replacements);

            stopWatch.start();
            numberOfInsertedRows = preparedStatement.executeUpdate();
            log.trace("Insert took {} milliseconds", () -> stopWatch.getTime());
        }
        catch (SQLException e)
        {
            throw ExceptionMapper.getInstance().toTorqueException(e);
        }

        return numberOfInsertedRows;
    }

    /**
     * Returns the idMethodInfo for the table for this Peer class.
     *
     * @return the idMethodInfo, not null.
     *
     * @throws TorqueException if the database adapter for the table's database
     *         needs to be accessed but is not configured.
     */
    private Object getIdMethodInfo()
            throws TorqueException
    {
        IDMethod idMethod = tableMap.getPrimaryKeyMethod();
        if (IDMethod.NATIVE == idMethod)
        {
            Adapter adapter = Torque.getAdapter(getDatabaseName());
            if (adapter == null)
            {
                throw new TorqueException(
                        "missing adapter configuration for database "
                                + getDatabaseName()
                                + "check the Torque configuration");
            }
            idMethod = adapter.getIDMethodType();
        }
        Object keyInfo = tableMap.getPrimaryKeyMethodInfo(idMethod);
        return keyInfo;
    }

    /**
     * Create an Id for insertion in the Criteria
     *
     * @param pk ColumnMap for the Primary key
     * @param keyGen The Id Generator object
     * @param con The SQL Connection to run the id generation under
     * @param keyInfo KeyInfo Parameter from the Table map
     *
     * @return A simple Key representing the new Id value
     * @throws TorqueException Possible errors get wrapped in here.
     */
    private SimpleKey<?> getId(
            final ColumnMap pk,
            final IdGenerator keyGen,
            final Connection con,
            final Object keyInfo)
                    throws TorqueException
    {
        SimpleKey<?> id = null;

        if (pk != null && keyGen != null)
        {
            if (pk.getType() instanceof Number)
            {
                id = new NumberKey(
                        keyGen.getIdAsBigDecimal(con, keyInfo));
            }
            else
            {
                id = new StringKey(keyGen.getIdAsString(con, keyInfo));
            }
        }

        return id;
    }

    /**
     * Add all the columns needed to create a new object.
     *
     * @param criteria the Criteria to which the select columns should
     *        be added.
     */
    public void addSelectColumns(final Criteria criteria)
    {
        ColumnMap[] columns = this.tableMap.getColumns();

        for (ColumnMap c : columns)
        {
            criteria.addSelectColumn(c);
        }
    }

    /**
     * Selects objects from a database.
     *
     * @param criteria object used to create the SELECT statement.
     *
     * @return the list of selected objects, not null.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public List<T> doSelect(final Criteria criteria)
            throws TorqueException
    {
        if (criteria.getSelectColumns().size() == 0)
        {
            addSelectColumns(criteria);
        }
        setDbName(criteria);

        return doSelect(
                criteria,
                getRecordMapper());
    }

    /**
     * Selects objects from a database
     * within a transaction.
     *
     * @param criteria object used to create the SELECT statement.
     * @param connection the connection to use, not null.
     *
     * @return the list of selected objects, not null.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public List<T> doSelect(
            final Criteria criteria,
            final Connection connection)
                    throws TorqueException
    {
        if (criteria.getSelectColumns().size() == 0)
        {
            addSelectColumns(criteria);
        }
        setDbName(criteria);

        return doSelect(
                criteria,
                getRecordMapper(),
                connection);
    }

    /**
     * Selects objects from a database
     * within a transaction.
     * This method returns a stream that <b>must</b> be closed after use.
     * All resources used by this method will be closed when the stream is
     * closed.
     *
     * @param criteria object used to create the SELECT statement.
     * @param connection the connection to use, not null.
     *
     * @return The results of the query as a Stream, not null.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public Stream<T> doSelectAsStream(
            final Criteria criteria,
            final Connection connection)
                    throws TorqueException
    {
        if (criteria.getSelectColumns().size() == 0)
        {
            addSelectColumns(criteria);
        }
        setDbName(criteria);

        return doSelectAsStream(
                criteria,
                getRecordMapper(),
                connection);
    }

    /**
     * Selects at most one object from a database.
     *
     * @param criteria object used to create the SELECT statement.
     *
     * @return the selected Object, or null if no object was selected.
     *
     * @throws TorqueException If more than one record is selected or if
     *         an error occurs when processing the query.
     */
    public T doSelectSingleRecord(final Criteria criteria)
            throws TorqueException
    {
        List<T> recordList = doSelect(criteria);
        T record = null;
        if (recordList.size() > 1)
        {
            throw new TooManyRowsException("Criteria " + criteria
                    + " matched more than one record");
        }
        if (!recordList.isEmpty())
        {
            record = recordList.get(0);
        }
        return record;
    }

    /**
     * Selects at most one object from a database
     * within a transaction.
     *
     * @param criteria object used to create the SELECT statement.
     * @param connection the connection holding the transaction, not null.
     *
     * @return the selected Object, or null if no object was selected.
     *
     * @throws TorqueException If more than one record is selected or if
     *         an error occurs when processing the query.
     */
    public T doSelectSingleRecord(
            final Criteria criteria,
            final Connection connection)
                    throws TorqueException
    {
        List<T> recordList = doSelect(criteria, connection);
        T record = null;
        if (recordList.size() > 1)
        {
            throw new TooManyRowsException("Criteria " + criteria
                    + " matched more than one record");
        }
        if (!recordList.isEmpty())
        {
            record = recordList.get(0);
        }
        return record;
    }

    /**
     * Selects rows from a database an maps them to objects.
     *
     * @param <TT> Object type class
     * @param criteria A Criteria specifying the records to select, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     *
     * @return The results of the query, not null.
     *
     * @throws TorqueException if querying the database fails.
     */
    public <TT> List<TT> doSelect(
            final Criteria criteria,
            final RecordMapper<TT> mapper)
                    throws TorqueException
    {
        try (TorqueConnection connection = Transaction.begin(criteria.getDbName()))
        {
            List<TT> result = doSelect(
                    criteria,
                    mapper,
                    connection);

            Transaction.commit(connection);
            return result;
        }
    }

    /**
     * Selects rows from a database an maps them to objects.
     *
     * @param query the sql query to execute, not null.
     *
     * @return The results of the query, not null.
     *
     * @throws TorqueException if querying the database fails.
     */
    public List<T> doSelect(final String query)
            throws TorqueException
    {
        return doSelect(
                query,
                getRecordMapper(),
                getDatabaseName());
    }

    /**
     * Selects rows from a database an maps them to objects.
     *
     * @param query the SQL Query to execute, not null.
     * @param connection the database connection, not null.
     *
     * @return The results of the query, not null.
     *
     * @throws TorqueException if querying the database fails.
     */
    public List<T> doSelect(
            final String query,
            final Connection connection)
                    throws TorqueException
    {
        return doSelect(
                query,
                getRecordMapper(),
                connection);
    }

    /**
     * Selects rows from a database an maps them to objects.
     * This method returns a stream that <b>must</b> be closed after use.
     * All resources used by this method will be closed when the stream is
     * closed.
     *
     * @param query the SQL Query to execute, not null.
     * @param connection the database connection, not null.
     *
     * @return The results of the query as a Stream, not null.
     *
     * @throws TorqueException if querying the database fails.
     */
    public Stream<T> doSelectAsStream(
            final String query,
            final Connection connection)
                    throws TorqueException
    {
        return doSelectAsStream(
                query,
                getRecordMapper(),
                connection);
    }

    /**
     * Selects rows from a database an maps them to objects.
     *
     * @param <TT> Object type class
     * @param query the sql query to execute, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     * @param dbName The name of the database to create the connection for,
     *        or null for the default DB.
     *
     * @return The results of the query, not null.
     *
     * @throws TorqueException if querying the database fails.
     */
    public <TT> List<TT> doSelect(
            final String query,
            final RecordMapper<TT> mapper,
            final String dbName)
                    throws TorqueException
    {
        try (TorqueConnection connection = Transaction.begin(
                (dbName == null)
                ? Torque.getDefaultDB()
                        : dbName))
        {
            List<TT> result = doSelect(
                    query,
                    mapper,
                    connection);

            Transaction.commit(connection);
            return result;
        }
    }

    /**
     * Selects rows from a database an maps them to objects.
     *
     * @param <TT> Object type class
     * @param query the SQL Query to execute, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     * @param connection the database connection, not null.
     *
     * @return The results of the query, not null.
     *
     * @throws TorqueException if querying the database fails.
     */
    public <TT> List<TT> doSelect(
            final String query,
            final RecordMapper<TT> mapper,
            final Connection connection)
                    throws TorqueException
    {
        try (Stream<TT> resultStream = doSelectAsStream(query, mapper, connection))
        {
            return resultStream.collect(Collectors.toList());
        }
    }

    /**
     * Selects rows from a database an maps them to objects.
     * This method returns a stream that <b>must</b> be closed after use.
     * All resources used by this method will be closed when the stream is
     * closed.
     *
     * @param <TT> Object type class
     * @param query the SQL Query to execute, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     * @param connection the database connection, not null.
     *
     * @return The results of the query as a Stream, not null.
     *
     * @throws TorqueException if querying the database fails.
     */
    public <TT> Stream<TT> doSelectAsStream(
            final String query,
            final RecordMapper<TT> mapper,
            final Connection connection)
                    throws TorqueException
    {
        if (connection == null)
        {
            throw new NullPointerException("connection is null");
        }

        try
        {
            Statement statement = connection.createStatement();
            StopWatch stopWatch = new StopWatch();
            log.debug("Executing query {}", query);

            stopWatch.start();
            ResultSet resultSet = statement.executeQuery(query.toString());
            ResultsetSpliterator<TT> spliterator =
                    new ResultsetSpliterator<>(mapper, null, statement, resultSet);
            log.trace("Query took {} milliseconds", () -> stopWatch.getTime());

            return StreamSupport.stream(spliterator, false).onClose(spliterator);
        }
        catch (SQLException e)
        {
            throw ExceptionMapper.getInstance().toTorqueException(e);
        }
    }

    /**
     * Performs a SQL <code>select</code> using a PreparedStatement.
     *
     * @param <TT> Object type class
     * @param criteria A Criteria specifying the records to select, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     * @param connection the database connection for selecting records,
     *        not null.
     *
     * @return The results of the query, not null.
     *
     * @throws TorqueException Error performing database query.
     */
    public <TT> List<TT> doSelect(
            final Criteria criteria,
            final RecordMapper<TT> mapper,
            final Connection connection)
                    throws TorqueException
    {
        try (Stream<TT> resultStream = doSelectAsStream(criteria, mapper, connection))
        {
            List<TT> result = resultStream.collect(Collectors.toList());

            if (criteria.isSingleRecord() && result.size() > 1)
            {
                throw new TooManyRowsException(
                        "Criteria expected single Record and "
                                + "Multiple Records were selected");
            }

            return result;
        }
    }

    /**
     * Performs a SQL <code>select</code> using a PreparedStatement.
     * This method returns a stream that <b>must</b> be closed after use.
     * All resources used by this method will be closed when the stream is
     * closed.
     *
     * @param <TT> Object type class
     * @param criteria A Criteria specifying the records to select, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     * @param connection the database connection for selecting records,
     *        not null.
     *
     * @return The results of the query as a Stream, not null.
     *
     * @throws TorqueException Error performing database query.
     */
    public <TT> Stream<TT> doSelectAsStream(
            final Criteria criteria,
            final RecordMapper<TT> mapper,
            final Connection connection)
                    throws TorqueException
    {
        if (connection == null)
        {
            throw new NullPointerException("connection is null");
        }

        correctBooleans(criteria);

        Query query = SqlBuilder.buildQuery(criteria);
        if (query.getFromClause().isEmpty())
        {
            String tableName = SqlBuilder.getFullTableName(
                    getTableMap().getFullyQualifiedTableName(),
                    criteria.getDbName());
            query.getFromClause().add(new FromElement(tableName));
        }

        try
        {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            if (query.getFetchSize() != null)
            {
                statement.setFetchSize(query.getFetchSize());
            }

            List<Object> replacements = setPreparedStatementReplacements(
                    statement,
                    query.getPreparedStatementReplacements(),
                    0);

            StopWatch stopWatch = new StopWatch();
            log.debug("Executing query {}, parameters = {}", query, replacements);

            stopWatch.start();
            ResultSet resultSet = statement.executeQuery();
            ResultsetSpliterator<TT> spliterator =
                    new ResultsetSpliterator<>(mapper, criteria, statement, resultSet);
            log.trace("Query took {} milliseconds", () -> stopWatch.getTime());

            return StreamSupport.stream(spliterator, false).onClose(spliterator);
        }
        catch (SQLException e)
        {
            throw ExceptionMapper.getInstance().toTorqueException(e);
        }
    }

    /**
     * Selects at most a single row from a database an maps them to objects.
     *
     * @param <TT> Object type class
     * @param criteria A Criteria specifying the records to select, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     *
     * @return The selected row, or null if no records was selected.
     *
     * @throws TorqueException if querying the database fails.
     */
    public <TT> TT doSelectSingleRecord(
            final Criteria criteria,
            final RecordMapper<TT> mapper)
                    throws TorqueException
    {
        List<TT> resultList = doSelect(criteria, mapper);
        TT result = null;
        if (resultList.size() > 1)
        {
            throw new TooManyRowsException("Criteria " + criteria
                    + " matched more than one record");
        }
        if (!resultList.isEmpty())
        {
            result = resultList.get(0);
        }
        return result;
    }

    /**
     * Selects at most a single row from a database an maps them to objects.
     *
     * @param <TT> Object type class
     * @param criteria A Criteria specifying the records to select, not null.
     * @param mapper The mapper creating the objects from the resultSet,
     *        not null.
     * @param connection the database connection, not null.
     *
     * @return The selected row, or null if no records was selected.
     *
     * @throws TorqueException if querying the database fails.
     */
    public <TT> TT doSelectSingleRecord(
            final Criteria criteria,
            final RecordMapper<TT> mapper,
            final Connection connection)
                    throws TorqueException
    {
        List<TT> resultList = doSelect(
                criteria,
                mapper,
                connection);
        TT result = null;
        if (resultList.size() > 1)
        {
            throw new TooManyRowsException("Criteria " + criteria
                    + " matched more than one record");
        }
        if (!resultList.isEmpty())
        {
            result = resultList.get(0);
        }
        return result;
    }

    /**
     * Convenience method used to update rows in the DB.  Checks if a
     * <i>single</i> primary key is specified in the Criteria
     * object and uses it to perform the update.  If no primary key is
     * specified or the table has multiple primary keys,
     * an Exception will be thrown.
     * <p>
     * Use this method for performing an update of the kind:
     * <p>
     * "WHERE primary_key_id = someValue"
     * <p>
     * To perform an update on a table with multiple primary keys or
     * an update with non-primary key fields in the WHERE
     * clause, use doUpdate(ColumnValues, Criteria).
     *
     * @param updateValues Which columns to update with which values
     *        for which primary key value, not null.
     *
     * @return the number of affected rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doUpdate(final ColumnValues updateValues)
            throws TorqueException
    {
        String databaseNameFromUpdateValues = updateValues.getDbName();
        if (databaseNameFromUpdateValues == null)
        {
            databaseNameFromUpdateValues = getDatabaseName();
        }
        try (TorqueConnection connection = Transaction.begin(databaseNameFromUpdateValues))
        {
            int result = doUpdate(updateValues, connection);
            Transaction.commit(connection);
            return result;
        }
    }

    /**
     * Convenience method used to update rows in the DB.  Checks if a
     * <i>single</i> primary key is specified in the Criteria
     * object and uses it to perform the update.  If no primary key is
     * specified or the table has multiple primary keys,
     * an Exception will be thrown.
     * <p>
     * Use this method for performing an update of the kind:
     * <p>
     * "WHERE primary_key_id = someValue"
     * <p>
     * To perform an update on a table with multiple primary keys or
     * an update with non-primary key fields in the WHERE
     * clause, use doUpdate(ColumnValues, Criteria, Connection).
     *
     * @param updateValues Which columns to update with which values
     *        for which primary key value, not null.
     * @param connection the database connection to use.
     *
     * @return the number of affected rows.
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    public int doUpdate(
            final ColumnValues updateValues,
            final Connection connection)
                    throws TorqueException
    {
        ColumnMap pk = getTableMap().getPrimaryKey();
        Criteria selectCriteria = null;

        if (pk != null && updateValues.containsKey(pk))
        {
            selectCriteria = new Criteria();
            selectCriteria.where(pk,
                    updateValues.remove(pk).getValue());
        }
        else
        {
            throw new TorqueException("No PK specified for database update");
        }

        return doUpdate(selectCriteria, updateValues, connection);
    }

    /**
     * Executes an update against the database. The rows to be updated
     * are selected using <code>criteria</code> and updated using the values
     * in <code>updateValues</code>.
     *
     * @param selectCriteria selects which rows of which table
     *        should be updated, not null.
     * @param updateValues Which columns to update with which values, not null.
     *
     * @return the number of affected rows.
     *
     * @throws TorqueException if updating fails.
     */
    public int doUpdate(
            final Criteria selectCriteria,
            final ColumnValues updateValues)
                    throws TorqueException
    {
        String databaseNameFromUpdateValues = updateValues.getDbName();
        if (databaseNameFromUpdateValues == null)
        {
            databaseNameFromUpdateValues = getDatabaseName();
        }
        try (TorqueConnection connection = Transaction.begin(databaseNameFromUpdateValues))
        {
            int result = doUpdate(selectCriteria, updateValues, connection);
            Transaction.commit(connection);
            return result;
        }
    }

    /**
     * Executes an update against the database. The rows to be updated
     * are selected using <code>criteria</code> and updated using the values
     * in <code>updateValues</code>.
     *
     * @param criteria selects which rows of which table should be updated.
     * @param updateValues Which columns to update with which values, not null.
     * @param connection the database connection to use, not null.
     *
     * @return the number of affected rows.
     *
     * @throws TorqueException if updating fails.
     */
    public int doUpdate(
            final Criteria criteria,
            final ColumnValues updateValues,
            final Connection connection)
                    throws TorqueException
    {
        Query query = SqlBuilder.buildQuery(criteria);
        query.setType(Query.Type.UPDATE);

        query.getFromClause().clear();
        String fullTableName = SqlBuilder.getFullTableName(
                getTableMap().getFullyQualifiedTableName(),
                criteria.getDbName());
        query.getFromClause().add(new FromElement(fullTableName));

        query.getUpdateValues().putAll(updateValues);

        try (PreparedStatement preparedStatement = connection.prepareStatement(query.toString()))
        {
            int position = 1;
            List<JdbcTypedValue> replacementObjects
                = new ArrayList<>();
            for (Map.Entry<Column, JdbcTypedValue> updateValue
                    : updateValues.entrySet())
            {
                JdbcTypedValue replacementObject = updateValue.getValue();
                if (replacementObject.getSqlExpression() != null)
                {
                    // replacementObject is no real value but contains
                    // a sql snippet which was (hopefully) processed earlier.
                    continue;
                }
                Object value = replacementObject.getValue();
                if (value != null)
                {
                    preparedStatement.setObject(position, value);
                }
                else
                {
                    preparedStatement.setNull(
                            position,
                            replacementObject.getJdbcType());
                }
                replacementObjects.add(replacementObject);
                position++;
            }
            List<Object> replacements = setPreparedStatementReplacements(
                    preparedStatement,
                    query.getPreparedStatementReplacements(),
                    position - 1);
            StopWatch stopWatch = new StopWatch();
            log.debug("Executing update {} using update parameters {} and query parameters {}",
                    () -> query.toString(), () -> replacementObjects, () -> replacements);

            stopWatch.start();
            int affectedRows = preparedStatement.executeUpdate();
            log.trace("Update took {} milliseconds", () -> stopWatch.getTime());

            return affectedRows;
        }
        catch (SQLException e)
        {
            throw ExceptionMapper.getInstance().toTorqueException(e);
        }
    }

    /**
     * Utility method which executes a given sql statement
     * as prepared statement.
     * This method should be used for update, insert, and delete statements.
     * Use executeQuery() for selects.
     *
     * @param statementString A String with the sql statement to execute.
     *
     * @return The number of rows affected.
     *
     * @throws TorqueException if executing the statement fails
     *         or no database connection can be established.
     */
    public int executeStatement(final String statementString) throws TorqueException
    {
        return executeStatement(statementString, Torque.getDefaultDB(), (List<JdbcTypedValue>) null);
    }

    /**
     * Utility method which executes a given sql statement
     * as prepared statement.
     * This method should be used for update, insert, and delete statements.
     * Use executeQuery() for selects.
     *
     * @param statementString A String with the sql statement to execute.
     * @param replacementValues values to use as placeholders in the query.
     *        or null or empty if no placeholders need to be filled.
     *
     * @return The number of rows affected.
     *
     * @throws TorqueException if executing the statement fails
     *         or no database connection can be established.
     */
    public int executeStatement(
            final String statementString,
            final List<JdbcTypedValue> replacementValues)
                    throws TorqueException
    {
        return executeStatement(
                statementString,
                Torque.getDefaultDB(),
                replacementValues);
    }

    /**
     * Utility method which executes a given sql statement
     * as prepared statement.
     * This method should be used for update, insert, and delete statements.
     * Use executeQuery() for selects.
     *
     * @param statementString A String with the sql statement to execute.
     * @param dbName The name of the database to execute the statement against,
     *        or null for the default DB.
     * @param replacementValues values to use as placeholders in the query.
     *        or null or empty if no placeholders need to be filled.
     *
     * @return The number of rows affected.
     *
     * @throws TorqueException if executing the statement fails
     *         or no database connection can be established.
     */
    public int executeStatement(
            final String statementString,
            final String dbName,
            final List<JdbcTypedValue> replacementValues)
                    throws TorqueException
    {
        try (TorqueConnection con = Transaction.begin(dbName))
        {
            int rowCount = executeStatement(
                    statementString,
                    con,
                    replacementValues);
            Transaction.commit(con);
            return rowCount;
        }
    }

    /**
     * Utility method which executes a given sql statement
     * as prepared statement.
     * This method should be used for update, insert, and delete statements.
     * Use executeQuery() for selects.
     *
     * @param statementString A String with the sql statement to execute.
     * @param con The database connection to use.
     * @param replacementValues values to use as placeholders in the query.
     *        or null or empty if no placeholders need to be filled.
     *
     * @return The number of rows affected.
     *
     * @throws TorqueException if executing the statement fails.
     */
    public int executeStatement(
            final String statementString,
            final Connection con,
            final List<JdbcTypedValue> replacementValues)
                    throws TorqueException
    {
        int rowCount = -1;
        try (PreparedStatement statement = con.prepareStatement(statementString))
        {
            if (replacementValues != null)
            {
                int position = 1;
                for (JdbcTypedValue replacementValue : replacementValues)
                {
                    if (replacementValue.getValue() == null)
                    {
                        statement.setNull(
                                position,
                                replacementValue.getJdbcType());
                    }
                    else
                    {
                        statement.setObject(
                                position,
                                replacementValue.getValue(),
                                replacementValue.getJdbcType());
                    }
                    ++position;
                }
            }
            rowCount = statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new TorqueException(e);
        }

        return rowCount;
    }

    /**
     * Utility method which executes a given sql statement
     * as prepared statement.
     * This method should be used for update, insert, and delete statements.
     * Use executeQuery() for selects.
     *
     * @param statementString A String with the sql statement to execute,
     *        containing placeholders of the form ":${placeholderName}".
     *        ${placeholderName} must contain only letters, digits and the underscore
     *        Each placeholder must be followed by a space, except when it is at the end of the statement.
     * @param replacementValues a map mapping the placeholder names to values
     *        to use as placeholders in the query.
     *        Can be null or empty if no placeholders need to be filled.
     *
     * @return The number of rows affected.
     *
     * @throws TorqueException if executing the statement fails.
     */
    public int executeStatement(
            final String statementString,
            final Map<String, JdbcTypedValue> replacementValues)
                    throws TorqueException
    {
        return executeStatement(
                statementString,
                Torque.getDefaultDB(),
                replacementValues);
    }

    /**
     * Utility method which executes a given sql statement
     * as prepared statement.
     * This method should be used for update, insert, and delete statements.
     * Use executeQuery() for selects.
     *
     * @param statementString A String with the sql statement to execute,
     *        containing placeholders of the form ":${placeholderName}".
     *        ${placeholderName} must contain only letters, digits and the underscore
     *        Each placeholder must be followed by a space, except when it is at the end of the statement.
     * @param dbName The name of the database to execute the statement against,
     *        or null for the default DB.
     * @param replacementValues a map mapping the placeholder names to values
     *        to use as placeholders in the query.
     *        Can be null or empty if no placeholders need to be filled.
     *
     * @return The number of rows affected.
     *
     * @throws TorqueException if executing the statement fails.
     */
    public int executeStatement(
            final String statementString,
            final String dbName,
            final Map<String, JdbcTypedValue> replacementValues)
                    throws TorqueException
    {
        try (TorqueConnection con = Transaction.begin(dbName))
        {
            int rowCount = executeStatement(
                    statementString,
                    con,
                    replacementValues);
            Transaction.commit(con);
            return rowCount;
        }
    }

    /**
     * Utility method which executes a given sql statement
     * as prepared statement.
     * This method should be used for update, insert, and delete statements.
     * Use executeQuery() for selects.
     *
     * @param statementString A String with the sql statement to execute,
     *        containing placeholders of the form ":${placeholderName}".
     *        ${placeholderName} must contain only letters, digits and the underscore
     *        Each placeholder must be followed by a space, except when it is at the end of the statement.
     * @param con The database connection to use.
     * @param replacementValues a map mapping the placeholder names (without leading colons)
     *        to values to use as placeholders in the query.
     *        Can be null or empty if no placeholders need to be filled.
     *
     * @return The number of rows affected.
     *
     * @throws TorqueException if executing the statement fails.
     */
    public int executeStatement(
            final String statementString,
            final Connection con,
            final Map<String, JdbcTypedValue> replacementValues)
                    throws TorqueException
    {
        StringBuilder changedStatement = new StringBuilder();
        List<JdbcTypedValue> replacementValueList = new ArrayList<>();
        Pattern pattern = Pattern.compile(":(\\w+)(?: |$)");
        Matcher matcher = pattern.matcher(statementString);
        int statementPosition = 0;
        while (matcher.find())
        {
            int groupStart = matcher.start();
            if (groupStart > statementPosition)
            {
                changedStatement.append(statementString.substring(statementPosition, groupStart));
            }
            String group = matcher.group();
            String key = group.substring(1);
            String replacement = "?";
            if (key.endsWith(" "))
            {
                key = key.substring(0, key.length() - 1);
                replacement += " ";
            }
            if (replacementValues != null && replacementValues.containsKey(key))
            {
                changedStatement.append(replacement);
                replacementValueList.add(replacementValues.get(key));
            }
            else
            {
                changedStatement.append(group);
            }
            statementPosition = matcher.end();
        }
        if (statementPosition != statementString.length() - 1)
        {
            changedStatement.append(statementString.substring(statementPosition));
        }
        return executeStatement(changedStatement.toString(), con, replacementValueList);
    }

    /**
     * Sets the prepared statement replacements into a query, possibly
     * modifying the type if required by DB Drivers.
     *
     * @param statement the statement to set the parameters in, not null.
     * @param replacements the replacements to set, not null.
     * @param offset the offset on the parameters, 0 for no offset.
     *
     * @return the parameters set.
     *
     * @throws SQLException if setting the parameter fails.
     */
    private List<Object> setPreparedStatementReplacements(
            final PreparedStatement statement,
            final List<Object> replacements,
            final int offset)
                    throws SQLException
    {
        List<Object> result = new ArrayList<>(replacements.size());
        int i = 1 + offset;
        for (Object param : replacements)
        {
            if (param instanceof java.sql.Timestamp)
            {
                statement.setTimestamp(i, (java.sql.Timestamp) param);
                result.add(param);
            }
            else if (param instanceof java.sql.Date)
            {
                statement.setDate(i, (java.sql.Date) param);
                result.add(param);
            }
            else if (param instanceof java.sql.Time)
            {
                statement.setTime(i, (java.sql.Time) param);
                result.add(param);
            }
            else if (param instanceof java.util.Date)
            {
                java.sql.Timestamp sqlDate = new java.sql.Timestamp(
                        ((java.util.Date) param).getTime());
                statement.setTimestamp(i, sqlDate);
                result.add(sqlDate);
            }
            else if (param instanceof NumberKey)
            {
                BigDecimal bigDecimal = ((NumberKey) param).getValue();
                statement.setBigDecimal(i, bigDecimal);
                result.add(bigDecimal);
            }
            else if (param instanceof Integer)
            {
                statement.setInt(i, ((Integer) param).intValue());
                result.add(param);
            }
            else if (param instanceof Long)
            {
                statement.setLong(i, ((Long) param).longValue());
                result.add(param);
            }
            else if (param instanceof BigDecimal)
            {
                statement.setBigDecimal(i, (BigDecimal) param);
                result.add(param);
            }
            else if (param instanceof Boolean)
            {
                statement.setBoolean(i, ((Boolean) param).booleanValue());
                result.add(param);
            }
            else if (param instanceof Short)
            {
                statement.setShort(i, ((Short) param).shortValue());
                result.add(param);
            }
            else if (param instanceof Byte)
            {
                statement.setByte(i, ((Byte) param).byteValue());
                result.add(param);
            }
            else if (param instanceof Float)
            {
                statement.setFloat(i, ((Float) param).floatValue());
                result.add(param);
            }
            else if (param instanceof Double)
            {
                statement.setDouble(i, ((Double) param).doubleValue());
                result.add(param);
            }
            else
            {
                statement.setString(i, param.toString());
                result.add(param.toString());
            }
            ++i;
        }
        return result;
    }

    /**
     * Checks all columns in the criteria to see whether
     * booleanchar and booleanint columns are queried with a boolean.
     * If yes, the query values are mapped onto values the database
     * does understand, i.e. 0 and 1 for booleanints and N and Y for
     * booleanchar columns.
     *
     * @param criteria The criteria to be checked for booleanint and booleanchar
     *        columns.
     *
     * @throws TorqueException if the database map for the criteria cannot be
     *         retrieved.
     */
    public void correctBooleans(
            final Criteria criteria)
                    throws TorqueException
    {
        correctBooleans(
                criteria,
                criteria.getTopLevelCriterion());
    }

    private void correctBooleans(
            final Criteria criteria,
            final Criterion criterion)
                    throws TorqueException
    {
        if (criterion == null)
        {
            return;
        }
        if (criterion.isComposite())
        {
            for (Criterion part : criterion.getParts())
            {
                correctBooleans(criteria, part);
            }
            return;
        }

        Object possibleColumn = criterion.getLValue();
        TableMap tableMapForColumn = MapHelper.getTableMap(
                possibleColumn,
                criteria,
                tableMap);
        // if no description of table available, do not modify anything
        if (tableMapForColumn == null)
        {
            return;
        }
        String columnName = ((Column) possibleColumn).getColumnName();
        ColumnMap columnMap = tableMapForColumn.getColumn(columnName);
        if (columnMap != null)
        {
            if ("BOOLEANINT".equals(columnMap.getTorqueType()))
            {
                replaceBooleanValues(
                        criterion,
                        Integer.valueOf(1),
                        Integer.valueOf(0));
            }
            else if ("BOOLEANCHAR".equals(columnMap.getTorqueType()))
            {
                replaceBooleanValues(criterion, "Y", "N");
            }
        }
    }

    /**
     * Replaces any Boolean value in the criterion and its attached Criterions
     * by trueValue if the Boolean equals <code>Boolean.TRUE</code>
     * and falseValue if the Boolean equals <code>Boolean.FALSE</code>.
     *
     * @param criterion the criterion to replace Boolean values in.
     *        May not be a composite criterion.
     * @param trueValue the value by which Boolean.TRUE should be replaced.
     * @param falseValue the value by which Boolean.FALSE should be replaced.
     */
    private void replaceBooleanValues(
            final Criterion criterion,
            final Object trueValue,
            final Object falseValue)
    {
        Object rValue = criterion.getRValue();
        if (rValue instanceof Boolean)
        {
            Boolean booleanValue = (Boolean) rValue;
            criterion.setRValue(
                    Boolean.TRUE.equals(booleanValue)
                    ? trueValue
                            : falseValue);
        }
        Object lValue = criterion.getLValue();
        if (lValue instanceof Boolean)
        {
            Boolean booleanValue = (Boolean) lValue;
            criterion.setLValue(
                    Boolean.TRUE.equals(booleanValue)
                    ? trueValue
                            : falseValue);
        }
    }

    /**
     * Checks all columns in the criteria to see whether
     * booleanchar and booleanint columns are queried with a boolean.
     * If yes, the query values are mapped onto values the database
     * does understand, i.e. 0 and 1 for booleanints and N and Y for
     * booleanchar columns.
     *
     * @param columnValues The value to be checked for booleanint
     *        and booleanchar columns.
     * @throws TorqueException if the database map for the criteria cannot be
     *         retrieved.
     */
    public void correctBooleans(
            final ColumnValues columnValues)
                    throws TorqueException
    {
        for (Map.Entry<Column, JdbcTypedValue> entry : columnValues.entrySet())
        {
            String columnName = entry.getKey().getColumnName();
            ColumnMap column = getTableMap().getColumn(columnName);
            if (column != null)
            {
                JdbcTypedValue columnValue = entry.getValue();
                if ("BOOLEANINT".equals(column.getTorqueType()))
                {
                    if (Boolean.TRUE.equals(columnValue.getValue()))
                    {
                        entry.setValue(new JdbcTypedValue(1, Types.INTEGER));
                    }
                    else if (Boolean.FALSE.equals(columnValue.getValue()))
                    {
                        entry.setValue(new JdbcTypedValue(0, Types.INTEGER));
                    }
                    else if (columnValue.getValue() == null)
                    {
                        entry.setValue(new JdbcTypedValue(null, Types.INTEGER));
                    }
                }
                else if ("BOOLEANCHAR".equals(column.getTorqueType()))
                {
                    if (Boolean.TRUE.equals(columnValue.getValue()))
                    {
                        entry.setValue(new JdbcTypedValue("Y", Types.CHAR));
                    }
                    else if (Boolean.FALSE.equals(columnValue.getValue()))
                    {
                        entry.setValue(new JdbcTypedValue("N", Types.CHAR));
                    }
                    else if (columnValue.getValue() == null)
                    {
                        entry.setValue(new JdbcTypedValue(null, Types.CHAR));
                    }
                }
            }
        }
    }

    /**
     * Sets the database name in the passed criteria to the table's default,
     * if it is not already set.
     *
     * @param crit the criteria to set the database name in, not null.
     * @throws TorqueException if unable to set the database name
     */
    protected void setDbName(final Criteria crit)
            throws TorqueException
    {
        if (crit.getDbName() == null)
        {
            crit.setDbName(getDatabaseName());
        }
    }
}
