package org.apache.torque.criteria;

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
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.Column;
import org.apache.torque.TorqueRuntimeException;
import org.apache.torque.sql.OrderBy;
import org.apache.torque.sql.Query;
import org.apache.torque.sql.SqlBuilder;
import org.apache.torque.util.UniqueColumnList;
import org.apache.torque.util.UniqueList;

/**
 * Encapsulates conditions to access rows in database tables.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:eric@dobbse.net">Eric Dobbs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:sam@neurogrid.com">Sam Joseph</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: Criteria.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class Criteria implements Serializable, Cloneable
{
  /** Serial version. */
  private static final long serialVersionUID = -9001666575933085601L;

  /** Comparison type. */
  public static final SqlEnum EQUAL = SqlEnum.EQUAL;

  /** Comparison type. */
  public static final SqlEnum NOT_EQUAL = SqlEnum.NOT_EQUAL;

  /** Comparison type. */
  public static final SqlEnum ALT_NOT_EQUAL = SqlEnum.ALT_NOT_EQUAL;

  /** Comparison type. */
  public static final SqlEnum GREATER_THAN = SqlEnum.GREATER_THAN;

  /** Comparison type. */
  public static final SqlEnum LESS_THAN = SqlEnum.LESS_THAN;

  /** Comparison type. */
  public static final SqlEnum GREATER_EQUAL = SqlEnum.GREATER_EQUAL;

  /** Comparison type. */
  public static final SqlEnum LESS_EQUAL = SqlEnum.LESS_EQUAL;

  /** Comparison type. */
  public static final SqlEnum LIKE = SqlEnum.LIKE;

  /** Comparison type. */
  public static final SqlEnum NOT_LIKE = SqlEnum.NOT_LIKE;

  /** Comparison type. */
  public static final SqlEnum ILIKE = SqlEnum.ILIKE;

  /** Comparison type. */
  public static final SqlEnum NOT_ILIKE = SqlEnum.NOT_ILIKE;

  /** Comparison type. */
  public static final SqlEnum DISTINCT = SqlEnum.DISTINCT;

  /** Comparison type. */
  public static final SqlEnum IN = SqlEnum.IN;

  /** Comparison type. */
  public static final SqlEnum NOT_IN = SqlEnum.NOT_IN;

  /** Comparison type. */
  public static final SqlEnum ALL = SqlEnum.ALL;

  /** Comparison type. */
  public static final SqlEnum JOIN = SqlEnum.JOIN;

  /** &quot;IS NULL&quot; null comparison */
  public static final SqlEnum ISNULL = SqlEnum.ISNULL;

  /** &quot;IS NOT NULL&quot; null comparison */
  public static final SqlEnum ISNOTNULL = SqlEnum.ISNOTNULL;

  /** &quot;CURRENT_DATE&quot; ANSI SQL function */
  public static final SqlEnum CURRENT_DATE = SqlEnum.CURRENT_DATE;

  /** &quot;CURRENT_TIME&quot; ANSI SQL function */
  public static final SqlEnum CURRENT_TIME = SqlEnum.CURRENT_TIME;

  /** &quot;CURRENT_TIMESTAMP&quot; ANSI SQL function */
  public static final SqlEnum CURRENT_TIMESTAMP = SqlEnum.CURRENT_TIMESTAMP;

  /** &quot;LEFT JOIN&quot; SQL statement */
  public static final JoinType LEFT_JOIN = JoinType.LEFT_JOIN;

  /** &quot;RIGHT JOIN&quot; SQL statement */
  public static final JoinType RIGHT_JOIN = JoinType.RIGHT_JOIN;

  /** &quot;INNER JOIN&quot; SQL statement */
  public static final JoinType INNER_JOIN = JoinType.INNER_JOIN;

  /** Whether to ignore the case in all String conditions in the criteria. */
  private boolean ignoreCase = false;

  /** Whether the result must be a single record. */
  private boolean singleRecord = false;

  /** List of modifiers like DISTICT. */
  private final UniqueList<String> selectModifiers;

  /** List of all columns to select. */
  private final UniqueColumnList selectColumns;

  /** All "order by" clauses, containing the order ASC or DESC. */
  private final UniqueList<OrderBy> orderByColumns;

  /** The names of columns to add to a groupBy clause */
  private final UniqueColumnList groupByColumns;

  /**
   * All "from" clauses. Empty if the from clause should be computed
   * automatically.
   */
  private final UniqueList<FromElement> fromElements;

  /** The having clause in a query. */
  private Criterion having = null;

  /** Whether a FOR UPDATE clause should be added. */
  private boolean forUpdate = false;

  /** The criterion objects, keyed by the column. */
  private Criterion topLevelCriterion;

  /**
   * Maps column alias names to the real column names.
   * The key of the map is the alias and the value is the real column.
   */
  private final Map<String, Column> asColumns;

  /** Contains all joins. */
  private final List<Join> joins;

  /** The name of the database in which this criteria should execute. */
  private String dbName;

  /**
   * To limit the number of rows to return.  <code>-1</code> means return all
   * rows.
   */
  private int limit = -1;

  /** To start the results at a row other than the first one. */
  private long offset = 0;

  /**
   * Aliases for table names. The key of the map is the alias,
   * and the value is either the real name of the table
   * or a corresponding subselect.
   */
  private final Map<String, Object> aliases;

  /** The JDBC statement fetch size, if any. */
  private Integer fetchSize;

  /** The operator used to connect the crizeria parts for set operations. */
  private SqlEnum setOperator;

  /** The parts of a Criteria which consists of several criteria
   * connected by a set operation.
   * Set operations are e.g. UNION, INTERSECT, EXCEPT...
   */
  private final List<Criteria> setCriteriaParts;

  /**
   * Constructor.
   */
  public Criteria()
  {
    selectModifiers = new UniqueList<>();
    selectColumns = new UniqueColumnList();
    orderByColumns = new UniqueList<>();
    groupByColumns = new UniqueColumnList();
    fromElements = new UniqueList<>();
    asColumns = new LinkedHashMap<>();
    joins = new ArrayList<>();
    aliases = new HashMap<>();
    setCriteriaParts = new ArrayList<>();
  }

  /**
   * Constructor with the database name as parameter..
   *
   * @param dbName The database name.
   */
  public Criteria(final String dbName)
  {
    this();
    this.dbName = dbName;
  }

  /**
   * Copy-constructor.
   * The copy is deep insofar as all contained lists are copied,
   * however the elements contained in the list are not copied.
   *
   * @param toCopy the criteria to copy.
   */
  public Criteria(final Criteria toCopy)
  {
    ignoreCase = toCopy.ignoreCase;
    singleRecord = toCopy.singleRecord;
    selectModifiers = new UniqueList<>(toCopy.selectModifiers);
    selectColumns = new UniqueColumnList(toCopy.selectColumns);
    orderByColumns = new UniqueList<>(toCopy.orderByColumns);
    groupByColumns = new UniqueColumnList(toCopy.groupByColumns);
    fromElements = new UniqueList<>(toCopy.fromElements);
    if(toCopy.having != null)
    {
      having = new Criterion(toCopy.having);
    }
    forUpdate = toCopy.forUpdate;
    if(toCopy.topLevelCriterion != null)
    {
      topLevelCriterion = new Criterion(toCopy.topLevelCriterion);
    }
    asColumns = new HashMap<>(toCopy.asColumns);
    joins = new ArrayList<>(toCopy.joins);
    dbName = toCopy.dbName;
    limit = toCopy.limit;
    offset = toCopy.offset;
    aliases = new HashMap<>(toCopy.aliases);
    fetchSize = toCopy.fetchSize;
    setOperator = toCopy.setOperator;
    setCriteriaParts = new ArrayList<>(toCopy.setCriteriaParts.size());
    toCopy.setCriteriaParts.forEach(setCriteriaPart
       -> setCriteriaParts.add(new Criteria(setCriteriaPart)));
  }

  /**
   * Resets this Criteria to its original state.
   */
  protected void clear()
  {
    ignoreCase = false;
    singleRecord = false;
    selectModifiers.clear();
    selectColumns.clear();
    orderByColumns.clear();
    groupByColumns.clear();
    fromElements.clear();
    having = null;
    forUpdate = false;
    topLevelCriterion = null;
    asColumns.clear();
    joins.clear();
    dbName = null;
    limit = -1;
    offset = 0;
    aliases.clear();
    fetchSize = null;
    setOperator = null;
    setCriteriaParts.clear();
  }

  /**
   * Add an AS clause to the select columns. Usage:
   * <p>
   * <code>
   * Criteria myCrit = new Criteria();
   * myCrit.addAsColumn(
   *     &quot;alias&quot;,
   *     &quot;ALIAS(&quot;+MyPeer.ID+&quot;)&quot;);
   * </code>
   * If the name already exists, it is replaced by the new clause.
   *
   * @param name wanted Name of the column
   * @param clause SQL clause to select from the table
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addAsColumn(final String name, final Column clause)
  {
    assertNoComposite();
    asColumns.put(name, clause);
    return this;
  }

  /**
   * Get the column aliases.
   *
   * @return A Map which map the column alias names
   * to the alias clauses.
   */
  public Map<String, Column> getAsColumns()
  {
    return asColumns;
  }

  /**
   * Get the table aliases.
   *
   * @return A Map which maps the table alias names to either the actual
   * table names (String) or to a subselect (Criteria).
   */
  public Map<String, Object> getAliases()
  {
    return aliases;
  }

  /**
   * Allows one to specify an alias for a table.
   *
   * @param alias the alias for the table name.
   * @param table the table name as known in the database.
   * @return the Criteria object
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addAlias(final String alias, final String table)
  {
    assertNoComposite();
    aliases.put(alias, table);
    return this;
  }

  /**
   * Allows one to specify an alias for a subselect.
   *
   * @param alias the alias for the subselect.
   * @param subselect the Criteria for the subselect.
   * @return the Criteria object
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addAlias(final String alias, final Criteria subselect)
  {
    assertNoComposite();
    aliases.put(alias, subselect);
    return this;
  }

  /**
   * Returns the database table name associated with an alias.
   *
   * @param alias a <code>String</code> value.
   *
   * @return a <code>String</code> value, or null if the alias is not defined.
   *
   * @throws IllegalArgumentException if the alias with the name
   * <code>alias</code> is defined but is no alias for a table name
   * (e.g. it is an alias for a subselect).
   */
  public String getTableForAlias(final String alias)
  {
    Object aliasResolved = aliases.get(alias);
    if(aliasResolved == null)
    {
      return null;
    }
    if(aliasResolved instanceof String)
    {
      return (String) aliasResolved;
    }
    throw new IllegalArgumentException("The alias " + alias
       + " is not associated to a table but to an object of type "
       + alias.getClass().getName());
  }

  /**
   * Returns the subselect associated with an alias.
   *
   * @param alias a <code>String</code> value.
   *
   * @return a <code>String</code> value, or null if the alias is not defined.
   *
   * @throws IllegalArgumentException if the alias with the name
   * <code>alias</code> is defined but is not an alias for a subselect
   * (e.g. it is an alias for a table).
   */
  public Criteria getSubselectForAlias(final String alias)
  {
    Object aliasResolved = aliases.get(alias);
    if(aliasResolved == null)
    {
      return null;
    }
    if(aliasResolved instanceof Criteria)
    {
      return (Criteria) aliasResolved;
    }
    throw new IllegalArgumentException("The alias " + alias
       + " is not associated to a subselect but to an object of type "
       + alias.getClass().getName());
  }

  /**
   * Returns the top level Criterion.
   *
   * @return the top level Criterion, or null if no Criterion is contained.
   */
  public Criterion getTopLevelCriterion()
  {
    return topLevelCriterion;
  }

  /**
   * Get the Database name to be used for this criterion.
   *
   * @return The database name, may be null.
   */
  public String getDbName()
  {
    return dbName;
  }

  /**
   * Set the Database name. The value <code>null</code> denotes the
   * database name provided by <code>Torque.getDefaultDB()</code>
   * (but this is not resolved here).
   *
   * @param dbName The Database(Map) name.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria setDbName(final String dbName)
  {
    assertNoComposite();
    this.dbName = dbName;
    return this;
  }

  /**
   * This method adds a prepared Criterion object to the Criteria as a having
   * clause. Usage:
   * <p>
   * <code>
   * Criteria crit = new Criteria();
   * Criterion c =new Criterion(MyPeer.ID, 5, Criteria.LESS_THAN);
   * crit.addHaving(c);
   * </code>
   *
   * @param having A Criterion object
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addHaving(final Criterion having)
  {
    assertNoComposite();
    this.having = having;
    return this;
  }

  /**
   * Get Having Criterion.
   *
   * @return A Criterion that is the having clause.
   */
  public Criterion getHaving()
  {
    return having;
  }

  /**
   * Sets that FOR UPDATE clause should be added to the query.
   *
   * @return this object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria forUpdate()
  {
    assertNoComposite();
    forUpdate = true;
    return this;
  }

  /**
   * Sets whether FOR UPDATE clause should be added to the query.
   *
   * @param forUpdate true if a FOR UPDATE clause should be added,
   * false if not.
   *
   * @return this object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria setForUpdate(final boolean forUpdate)
  {
    assertNoComposite();
    this.forUpdate = forUpdate;
    return this;
  }

  /**
   * Returns whether a FOR UPDATE clause is added.
   *
   * @return true if a FOR UPDATE clause is added, false otherwise.
   */
  public boolean isForUpdate()
  {
    return forUpdate;
  }

  /**
   * Adds a join to the criteria, E.g. to create the condition
   * <p>
   * AND PROJECT.PROJECT_ID=FOO.PROJECT_ID
   * <p>
   * use
   * <p>
   * <code>
   * criteria.addJoin(ProjectPeer.PROJECT_ID, FooPeer.PROJECT_ID)
   * </code>
   *
   * @param left A String with the left side of the join.
   * @param right A String with the right side of the join.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addJoin(final Column left, final Column right)
  {
    assertNoComposite();
    return addJoin(left, right, null);
  }

  /**
   * Adds a join to the criteria, E.g. to create the condition
   * <p>
   * PROJECT LEFT JOIN FOO ON PROJECT.PROJECT_ID=FOO.PROJECT_ID
   * <p>
   * use
   * <p>
   * <code>
   * criteria.addJoin(ProjectPeer.PROJECT_ID, FooPeer.PROJECT_ID,
   *     Criteria.LEFT_JOIN);
   * </code>
   *
   * @param left A String with the left side of the join.
   * @param right A String with the right side of the join.
   * @param joinType The operator used for the join: must be one of null,
   * Criteria.LEFT_JOIN, Criteria.RIGHT_JOIN, Criteria.INNER_JOIN
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addJoin(
     final Column left,
     final Column right,
     final JoinType joinType)
  {
    assertNoComposite();
    joins.add(new Join(left, right, Criteria.EQUAL, joinType));
    return this;
  }

  /**
   * Adds a join to the criteria, E.g. to create the condition
   * <p>
   * PROJECT LEFT JOIN FOO ON PROJECT.PROJECT_ID &lt;&gt; FOO.PROJECT_ID
   * <p>
   * use
   * <p>
   * <code>
   * criteria.addJoin(
   *     ProjectPeer.PROJECT_ID,
   *     Criteria.NOT_EQUAL,
   *     FooPeer.PROJECT_ID,
   *     Criteria.LEFT_JOIN);
   * </code>
   *
   * @param left A String with the left side of the join condition.
   * @param right A String with the right side of the join condition.
   * @param comparison the comparison operator, not null.
   * The operator CUSTOM is not supported.
   * @param joinType The operator used for the join. Must be one of null,
   * Criteria.LEFT_JOIN, Criteria.RIGHT_JOIN, Criteria.INNER_JOIN
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addJoin(
     final Column left,
     final Column right,
     final SqlEnum comparison,
     final JoinType joinType)
  {
    assertNoComposite();
    joins.add(new Join(left, right, comparison, joinType));
    return this;
  }

  /**
   * Adds a join to the criteria.
   * For creating standard joins, please use addJoin(Column, Column, (SqlEnum, ) JoinType)
   * <p>
   * PROJECT LEFT JOIN FOO ON (PROJECT.PROJECT_ID=FOO.PROJECT_ID OR PROJECT.PROJECT_ID==FOO.PROJECT2_ID)
   * <p>
   * use
   * <p>
   * <code>
   * criteria.addJoin(
   *     ProjectPeer.TABLE_NAME,
   *     FooPeer.TABLE_NAME,
   *     new Criterion(ProjectPeer.PROJECT_ID,
   *         FooPeer.PROJECT_ID)
   *       .or(Criterion(ProjectPeer.PROJECT_ID,
   *         FooPeer.PROJECT"_ID))
   *     Criteria.LEFT_JOIN);
   * </code>
   * If a default schema name is set for the used database
   * and leftTable or rightTable are a simple unqualified table names,
   * the default schema name is prepended to the table name.
   * For more complicated "table names", no schema resolution is done.
   *
   * @param leftTable the left table of the join, or null to determine
   * the left table from the join condition.
   * @param rightTable the left table of the join, or null to determine
   * the left table from the join condition.
   * @param joinCondition the join condition, not null.
   * @param joinType The operator used for the join. Must be one of null,
   * Criteria.LEFT_JOIN, Criteria.RIGHT_JOIN, Criteria.INNER_JOIN
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addJoin(
     final String leftTable,
     final String rightTable,
     final Criterion joinCondition,
     final JoinType joinType)
  {
    assertNoComposite();
    joins.add(new Join(
       new PreparedStatementPartImpl(leftTable),
       new PreparedStatementPartImpl(rightTable),
       joinCondition,
       joinType));
    return this;
  }

  /**
   * Adds a join to the criteria, E.g. to create the condition
   * <p>
   * PROJECT LEFT JOIN FOO ON PROJECT.PROJECT_ID=FOO.PROJECT_ID
   * <p>
   * use
   * <p>
   * <code>
   * criteria.addJoin(
   *     new PreparedStatementPart(ProjectPeer.TABLE_NAME),
   *     new PreparedStatementPart(FooPeer.TABLE_NAME),
   *     new Criterion(ProjectPeer.PROJECT_ID,
   *         FooPeer.PROJECT_ID,
   *         Criteria.NOT_EQUAL)
   *     Criteria.LEFT_JOIN);
   * </code>.
   * If a default schema name is set for the used database
   * and leftTable or rightTable are a simple unqualified table names,
   * the default schema name is prepended to the table name.
   * For more complicated "table names", no schema resolution is done.
   *
   * @param leftTable the left table of the join, might contain an alias name,
   * or null to be determined from the join clause.
   * @param rightTable the right table of the join, might contain an alias
   * name, or null to be determined from the join clause.
   * @param joinCondition the join condition, not null.
   * @param joinType The operator used for the join. Must be one of null,
   * Criteria.LEFT_JOIN, Criteria.RIGHT_JOIN, Criteria.INNER_JOIN
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addJoin(
     final PreparedStatementPart leftTable,
     final PreparedStatementPart rightTable,
     final Criterion joinCondition,
     final JoinType joinType)
  {
    assertNoComposite();
    joins.add(new Join(
       leftTable,
       rightTable,
       joinCondition,
       joinType));
    return this;
  }

  /**
   * Get the List of Joins.
   *
   * @return a List which contains objects of type Join, not null.
   */
  public List<Join> getJoins()
  {
    return joins;
  }

  /**
   * Adds &quot;ALL &quot; to the SQL statement.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   *
   * @return A modified Criteria object.
   */
  public Criteria setAll()
  {
    assertNoComposite();
    selectModifiers.add(ALL.toString());
    return this;
  }

  /**
   * Adds &quot;DISTINCT &quot; to the SQL statement.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   *
   * @return A modified Criteria object.
   */
  public Criteria setDistinct()
  {
    assertNoComposite();
    selectModifiers.add(DISTINCT.toString());
    return this;
  }

  /**
   * Sets whether case should be ignored in where clauses and order by
   * whenever String columns are encountered.
   *
   * @param ignoreCase True if case should be ignored.
   *
   * @return A modified Criteria object.
   */
  public Criteria setIgnoreCase(final boolean ignoreCase)
  {
    if(isComposite())
    {
      setCriteriaParts.forEach(criteria -> criteria.setIgnoreCase(true));
    }
    else
    {
      this.ignoreCase = ignoreCase;
    }
    return this;
  }

  /**
   * Returns whether case should be ignored in where clauses and order by
   * whenever String columns are encountered.
   *
   * @return True if case is ignored.
   */
  public boolean isIgnoreCase()
  {
    return ignoreCase;
  }

  /**
   * Switch the check on or off that a query returns exactly one record.
   * Set this to <code>true</code> if you want a TorqueException to be thrown
   * if none or multiple records are returned when the query is executed.
   * This should be used in situations where returning multiple
   * rows would indicate an error of some sort. If your query might return
   * multiple records but you are only interested in the first one then you
   * should be using setLimit(1).
   *
   * @param b set to <code>true</code> if you expect the query to select
   * exactly one record.
   * @return A modified Criteria object.
   */
  public Criteria setSingleRecord(final boolean b)
  {
    singleRecord = b;
    return this;
  }

  /**
   * Returns whether the check that a query returns exactly one record
   * is active.
   *
   * @return True if the check for exactly one record is active.
   */
  public boolean isSingleRecord()
  {
    return singleRecord;
  }

  /**
   * Set a limit for the query
   *
   * @param limit The upper limit for the number of records returned
   * by a query.
   * @return A modified Criteria object.
   */
  public Criteria setLimit(final int limit)
  {
    this.limit = limit;
    return this;
  }

  /**
   * Get the upper limit for the number of records returned by a query.
   *
   * @return The value for limit.
   */
  public int getLimit()
  {
    return limit;
  }

  /**
   * Set the offset.
   *
   * @param offset how many records should be skipped at the start of the
   * result.
   * @return A modified Criteria object.
   */
  public Criteria setOffset(final long offset)
  {
    this.offset = offset;
    return this;
  }

  /**
   * Get how many records should be skipped at the start of the result.
   *
   * @return The value for offset.
   */
  public long getOffset()
  {
    return offset;
  }

  /**
   * Returns the JDBC statement fetch size to use for queries.
   *
   * @return the fetch size, or null if none is set.
   */
  public Integer getFetchSize()
  {
    return fetchSize;
  }

  /**
   * Sets the JDBC statement fetch size to use for queries.
   *
   * @param fetchSize the fetch size, or null for not set.
   *
   * @return A modified Criteria object.
   */
  public Criteria setFetchSize(final Integer fetchSize)
  {
    this.fetchSize = fetchSize;
    return this;
  }

  /**
   * Adds a select column to the Criteria.
   *
   * @param column The select column to add.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addSelectColumn(final Column column)
  {
    assertNoComposite();
    selectColumns.add(column);
    return this;
  }

  /**
   * Return all select columns.
   *
   * @return An List with the names of the select columns, not null
   */
  public UniqueColumnList getSelectColumns()
  {
    return selectColumns;
  }

  /**
   * Return all select modifiers.
   *
   * @return An UniqueList with the select modifiers.
   */
  public UniqueList<String> getSelectModifiers()
  {
    return selectModifiers;
  }

  /**
   * Add a group by clause.
   *
   * @param groupBy The column to group by.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addGroupByColumn(final Column groupBy)
  {
    assertNoComposite();
    groupByColumns.add(groupBy);
    return this;
  }

  /**
   * Get all group by columns.
   *
   * @return An UniqueList with the name of the groupBy clause, not null.
   */
  public UniqueColumnList getGroupByColumns()
  {
    return groupByColumns;
  }

  /**
   * Adds an order by clause, explicitly specifying ascending.
   *
   * @param column The column to order by.
   *
   * @return A modified Criteria object.
   */
  public Criteria addAscendingOrderByColumn(final Column column)
  {
    orderByColumns.add(new OrderBy(column, SqlEnum.ASC, false));
    return this;
  }

  /**
   * Add an order by clause, explicitly specifying ascending.
   *
   * @param column The column to order by.
   * @param ignoreCase whether to ignore case on String columns.
   *
   * @return A modified Criteria object.
   */
  public Criteria addAscendingOrderByColumn(
     final Column column,
     final boolean ignoreCase)
  {
    orderByColumns.add(new OrderBy(column, SqlEnum.ASC, ignoreCase));
    return this;
  }

  /**
   * Add order by column name, explicitly specifying descending.
   *
   * @param column The column to order by.
   *
   * @return A modified Criteria object.
   */
  public Criteria addDescendingOrderByColumn(final Column column)
  {
    orderByColumns.add(new OrderBy(column, SqlEnum.DESC, false));
    return this;
  }

  /**
   * Add order by column name, explicitly specifying descending.
   *
   * @param column The column to order by.
   *
   * @param ignoreCase whether to ignore case on String columns.
   *
   * @return A modified Criteria object.
   */
  public Criteria addDescendingOrderByColumn(
     final Column column,
     final boolean ignoreCase)
  {
    orderByColumns.add(new OrderBy(column, SqlEnum.DESC, ignoreCase));
    return this;
  }

  /**
   * Get all order by columns.
   *
   * @return An UniqueList with the name of the order columns, not null.
   */
  public UniqueList<OrderBy> getOrderByColumns()
  {
    return orderByColumns;
  }

  /**
   * Get all elements in the from clause of the query.
   *
   * @return An UniqueList with all from elements, not null.
   * Empty if the from elements should be computed automatically.
   */
  public UniqueList<FromElement> getFromElements()
  {
    return fromElements;
  }

  /**
   * Adds a table to the from clause, not using a joinType or joinCondition.
   *
   * @param tableName the table name
   * @return the modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addFrom(final String tableName)
  {
    assertNoComposite();
    fromElements.add(new FromElement(tableName));
    return this;
  }

  /**
   * Adds a new Element to the from clause.
   *
   * @param fromElement the element to add from
   * @return the modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria addFrom(final FromElement fromElement)
  {
    assertNoComposite();
    fromElements.add(fromElement);
    return this;
  }

  /**
   * Build a string representation of the Criteria for debugging purposes.
   *
   * @return A String with the representation of the Criteria.
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("Criteria: ");
    try
    {
      Query query = SqlBuilder.buildQuery(this);
      sb.append("Current Query SQL (may not be complete or applicable): ")
         .append(query.getDisplayString());
    }
    catch(Exception exc)
    {
      sb.append("Error ").append(exc.getMessage());
    }

    return sb.toString();
  }

  /**
   * Checks whether an object is equal to this Criteria.
   * This is the case if the other object is also a Criteria and has
   * the same attributes and criterions.
   *
   * @param object the other object to check, can be null.
   *
   * @return true if the object is equal to this Criteria, false otherwise.
   */
  @Override
  public boolean equals(final Object object)
  {
    if(object == null)
    {
      return false;
    }
    if(this == object)
    {
      return true;
    }
    if(object.getClass() != this.getClass())
    {
      return false;
    }

    Criteria criteria = (Criteria) object;
    EqualsBuilder equalsBuilder = new EqualsBuilder();
    equalsBuilder.append(criteria.topLevelCriterion, this.topLevelCriterion);
    equalsBuilder.append(criteria.offset, this.offset);
    equalsBuilder.append(criteria.limit, this.limit);
    equalsBuilder.append(criteria.ignoreCase, this.ignoreCase);
    equalsBuilder.append(criteria.singleRecord, this.singleRecord);
    equalsBuilder.append(criteria.dbName, this.dbName);
    equalsBuilder.append(criteria.selectModifiers, this.selectModifiers);
    equalsBuilder.append(criteria.selectColumns, this.selectColumns);
    equalsBuilder.append(criteria.orderByColumns, this.orderByColumns);
    equalsBuilder.append(criteria.fromElements, this.fromElements);
    equalsBuilder.append(criteria.aliases, this.aliases);
    equalsBuilder.append(criteria.asColumns, this.asColumns);
    equalsBuilder.append(criteria.joins, this.joins);
    equalsBuilder.append(criteria.fetchSize, this.fetchSize);
    equalsBuilder.append(criteria.setOperator, this.setOperator);
    equalsBuilder.append(criteria.setCriteriaParts, this.setCriteriaParts);
    return equalsBuilder.isEquals();
  }

  /**
   * Returns the hash code value for this Criteria.
   *
   * @return a hash code value for this object.
   */
  @Override
  public int hashCode()
  {
    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    hashCodeBuilder.append(topLevelCriterion);
    hashCodeBuilder.append(offset);
    hashCodeBuilder.append(limit);
    hashCodeBuilder.append(ignoreCase);
    hashCodeBuilder.append(singleRecord);
    hashCodeBuilder.append(dbName);
    hashCodeBuilder.append(selectModifiers);
    hashCodeBuilder.append(selectColumns);
    hashCodeBuilder.append(orderByColumns);
    hashCodeBuilder.append(fromElements);
    hashCodeBuilder.append(aliases);
    hashCodeBuilder.append(asColumns);
    hashCodeBuilder.append(joins);
    hashCodeBuilder.append(fetchSize);
    hashCodeBuilder.append(setOperator);
    hashCodeBuilder.append(setCriteriaParts);
    return hashCodeBuilder.toHashCode();
  }

  /**
   * Returns a shallow copy of this object.
   *
   * @return the cloned criteria.
   */
  @Override
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch(CloneNotSupportedException e)
    {
      // should not happen as we implement Cloneable
      throw new RuntimeException(e);
    }
  }

  /*
     * ------------------------------------------------------------------------
     *
     * Start of the "and" methods
     *
     * ------------------------------------------------------------------------
   */
  /**
   * "AND"s Criterion object with the conditions in this Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria crit = new Criteria();
   * Criterion c = new Criterion(XXXPeer.ID, Integer.valueOf(5),
   *         Criteria.LESS_THAN);
   * crit.and(c);
   * </code>
   *
   * @param criterion A Criterion object.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria and(final Criterion criterion)
  {
    assertNoComposite();
    if(topLevelCriterion == null)
    {
      topLevelCriterion = new Criterion(criterion);
    }
    else
    {
      topLevelCriterion.and(new Criterion(criterion));
    }
    return this;
  }

  /**
   * "AND"s a new condition with the conditions in this Criteria.
   * Depending on rValue, the condition is constructed differently:
   * Either rValue is a unary comparison operator (i.e. is a SqlEnum and
   * getNumberOfCompareOperands() == 1) (e.g. Criteria.ISNULL),
   * then lValue is taken as single operand of the operator
   * ant the passed operator is used for comparison.
   * Otherwise, an EQUAL comparison is used for comparing rValue and lValue.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.g. string object), it is interpreted as
   * literal value.
   * @param rValue The right hand side of the comparison, may be null.
   * If this object is a unary comparison operator, it is taken as
   * comparison operator of the condition to add.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.g. string object), it is interpreted as
   * literal value.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria and(final Object lValue, final Object rValue)
  {
    if(rValue instanceof SqlEnum)
    {
      SqlEnum sqlEnum = (SqlEnum) rValue;
      if(sqlEnum.getNumberOfCompareOperands() == 1)
      {
        return and(lValue, null, sqlEnum);
      }
    }
    return and(lValue, rValue, EQUAL);
  }

  /**
   * "AND"s a new condition with the conditions in this Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValue The right hand side of the comparison, may be null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param comparison the comparison, or <code>Criteria.CUSTOM</code>
   * to specify the expression manually in the rValue parameter.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria and(
     final Object lValue,
     final Object rValue,
     final SqlEnum comparison)
  {
    assertNoComposite();
    Criterion newCriterion = new Criterion(lValue, rValue, comparison);
    if(topLevelCriterion == null)
    {
      topLevelCriterion = newCriterion;
    }
    else
    {
      topLevelCriterion.and(newCriterion);
    }
    return this;
  }

  /**
   * Convenience method to AND a new date comparison with the conditions
   * in this Criteria. Equal to
   * <p>
   * <code>
   * and(column, new GregorianCalendar(year, month,date), EQUAL);
   * </code>
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param year The year to compare to.
   * @param month The month to compare to.
   * @param day The day to compare to.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andDate(
     final Object lValue,
     final int year,
     final int month,
     final int day)
  {
    return and(lValue,
       new GregorianCalendar(year, month, day).getTime(),
       Criteria.EQUAL);
  }

  /**
   * Convenience method to AND a new date comparison with the conditions
   * in this Criteria. Equal to
   * <p>
   * <code>
   * and(column, new GregorianCalendar(year, month,date), comparison);
   * </code>
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param year The year to compare to.
   * @param month The month to compare to.
   * @param day The day to compare to.
   * @param comparison The comparison operator.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andDate(
     final Object lValue,
     final int year,
     final int month,
     final int day,
     final SqlEnum comparison)
  {
    return and(lValue,
       new GregorianCalendar(year, month, day).getTime(),
       comparison);
  }

  /**
   * Convenience method to AND a "in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andIn(final Object lValue, final Object[] rValues)
  {
    return and(lValue, rValues, Criteria.IN);
  }

  /**
   * Convenience method to AND a "in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andIn(final Object lValue, final Collection<?> rValues)
  {
    return and(lValue, rValues, Criteria.IN);
  }

  /**
   * Convenience method to AND a "not in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME NOT IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andNotIn(final Object lValue, final Object[] rValues)
  {
    return and(lValue, rValues, Criteria.NOT_IN);
  }

  /**
   * Convenience method to AND a "not in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME NOT IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andNotIn(final Object lValue, final Collection<?> rValues)
  {
    return and(lValue, rValues, Criteria.NOT_IN);
  }

  /**
   * Ands a verbatim sql condition to this Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria criteria = ...;
   * criteria.andVerbatimSql("count(foo.x) = ?", new Object[] {0});
   * </code>
   *
   * @param sql the verbatim SQL to use.
   * @param replacements the replacements for the "?" placeholders in SQL.
   *
   * @return the modified Criteria.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andVerbatimSql(
     final String sql,
     final Object[] replacements)
  {
    Criterion criterion
       = new Criterion(null, null, null, sql, replacements);
    and(criterion);
    return this;
  }

  /**
   * ANDs a verbatim sql condition to this Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria criteria = new Criteria();
   * criteria.andVerbatimSql(
   *     "count(foo.x) = ?",
   *     new Object[] {0},
   *     FooPeer.X,
   *     null);
   * </code>
   *
   * @param sql the verbatim SQL to use.
   * @param replacements the replacements for the "?" placeholders in SQL.
   * @param toAddToFromClause1 a column to add to from clause, may be null.
   * @param toAddToFromClause2 a column to add to from clause, may be null.
   *
   * @return the modified Criteria.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria andVerbatimSql(
     final String sql,
     final Object[] replacements,
     final Column toAddToFromClause1,
     final Column toAddToFromClause2)
  {
    Criterion criterion = new Criterion(
       toAddToFromClause1,
       toAddToFromClause2,
       null,
       sql,
       replacements);
    and(criterion);
    return this;
  }

  /*
     * ------------------------------------------------------------------------
     *
     * Start of the "or" methods
     *
     * ------------------------------------------------------------------------
   */
  /**
   * "OR"s a Criterion object with the conditions in this Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria crit = new Criteria();
   * Criterion c = new Criterion(XXXPeer.ID, Integer.valueOf(5),
   *         Criteria.LESS_THAN);
   * crit.or(c);
   * </code>
   *
   * @param criterion A Criterion object.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria or(final Criterion criterion)
  {
    assertNoComposite();
    if(topLevelCriterion == null)
    {
      topLevelCriterion = new Criterion(criterion);
    }
    else
    {
      topLevelCriterion.or(new Criterion(criterion));
    }
    return this;
  }

  /**
   * "OR"s a new condition with the conditions in this Criteria.
   * Depending on rValue, the condition is constructed differently:
   * Either rValue is a unary comparison operator (i.e. is a SqlEnum and
   * getNumberOfCompareOperands() == 1) (e.g. Criteria.ISNULL),
   * then lValue is taken as single operand of the operator
   * ant the passed operator is used for comparison.
   * Otherwise, an EQUAL comparison is used for comparing rValue and lValue.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.g. string object), it is interpreted as
   * literal value.
   * @param rValue The right hand side of the comparison, may be null.
   * If this object is a unary comparison operator, it is taken as
   * comparison operator of the condition to add.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.g. string object), it is interpreted as
   * literal value.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria or(final Object lValue, final Object rValue)
  {
    if(rValue instanceof SqlEnum)
    {
      SqlEnum sqlEnum = (SqlEnum) rValue;
      if(sqlEnum.getNumberOfCompareOperands() == 1)
      {
        return or(lValue, null, sqlEnum);
      }
    }
    return or(lValue, rValue, EQUAL);
  }

  /**
   * "OR"s a new condition with the conditions in this Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValue The right hand side of the comparison, may be null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param comparison the comparison, or <code>Criteria.CUSTOM</code>
   * to specify the expression manually in the value parameter.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria or(
     final Object lValue,
     final Object rValue,
     final SqlEnum comparison)
  {
    assertNoComposite();
    Criterion newCriterion = new Criterion(lValue, rValue, comparison);
    if(topLevelCriterion == null)
    {
      topLevelCriterion = newCriterion;
    }
    else
    {
      topLevelCriterion.or(newCriterion);
    }
    return this;
  }

  /**
   * Convenience method to OR a new date comparison with the conditions
   * in this Criteria. Equal to
   * <p>
   * <code>
   * or(column, new GregorianCalendar(year, month,date), EQUAL);
   * </code>
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param year The year to compare to.
   * @param month The month to compare to.
   * @param day The day to compare to.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orDate(
     final Object lValue,
     final int year,
     final int month,
     final int day)
  {
    return or(lValue,
       new GregorianCalendar(year, month, day).getTime(),
       Criteria.EQUAL);
  }

  /**
   * Convenience method to OR a new date comparison with the conditions
   * in this Criteria. Equal to
   * <p>
   * <code>
   * or(column, new GregorianCalendar(year, month,date), comparison);
   * </code>
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param year The year to compare to.
   * @param month The month to compare to.
   * @param day The day to compare to.
   * @param comparison The comparison operator.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orDate(
     final Object lValue,
     final int year,
     final int month,
     final int day,
     final SqlEnum comparison)
  {
    return or(lValue,
       new GregorianCalendar(year, month, day).getTime(),
       comparison);
  }

  /**
   * Convenience method to OR a "in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orIn(final Object lValue, final Object[] rValues)
  {
    return or(lValue, rValues, Criteria.IN);
  }

  /**
   * Convenience method to OR a "in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orIn(final Object lValue, final Collection<?> rValues)
  {
    return or(lValue, rValues, Criteria.IN);
  }

  /**
   * Convenience method to OR a "not in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME NOT IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orNotIn(final Object lValue, final Object[] rValues)
  {
    return or(lValue, rValues, Criteria.NOT_IN);
  }

  /**
   * Convenience method to OR a "not in" comparison with the conditions
   * in this Criteria. Creates the condition
   * <p>
   * FOO.NAME NOT IN (${values})
   * <p>
   * where ${values} contains the values to compare against.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orNotIn(final Object lValue, final Collection<?> rValues)
  {
    return or(lValue, rValues, Criteria.NOT_IN);
  }

  /**
   * ORs a verbatim sql condition to this Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria criteria = ...;
   * criteria.orVerbatimSql("count(foo.x) = ?", new Object[] {0});
   * </code>
   *
   * @param sql the verbatim SQL to use.
   * @param replacements the replacements for the "?" placeholders in SQL.
   *
   * @return the modified Criteria.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orVerbatimSql(final String sql, final Object[] replacements)
  {
    Criterion criterion
       = new Criterion(null, null, null, sql, replacements);
    or(criterion);
    return this;
  }

  /**
   * ORs a verbatim sql condition to this Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria criteria = new Criteria();
   * criteria.orVerbatimSql(
   *     "count(foo.x) = ?",
   *     new Object[] {0},
   *     FooPeer.X,
   *     null);
   * </code>
   *
   * @param sql the verbatim SQL to use.
   * @param replacements the replacements for the "?" placeholders in SQL.
   * @param toAddToFromClause1 a column to add to from clause, may be null.
   * @param toAddToFromClause2 a column to add to from clause, may be null.
   *
   * @return the modified Criteria.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria orVerbatimSql(
     final String sql,
     final Object[] replacements,
     final Column toAddToFromClause1,
     final Column toAddToFromClause2)
  {
    Criterion criterion = new Criterion(
       toAddToFromClause1,
       toAddToFromClause2,
       null,
       sql,
       replacements);
    or(criterion);
    return this;
  }

  /*
     * ------------------------------------------------------------------------
     *
     * Start of the "where" methods
     *
     * ------------------------------------------------------------------------
   */
  /**
   * "AND"s Criterion object with the conditions in this Criteria.
   * Equivalent to <code>#and(Criterion)</code> but better to read if this is
   * the first condition to be added to the Criteria.
   *
   * @param criterion A Criterion object.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria where(final Criterion criterion)
  {
    return and(criterion);
  }

  /**
   * "AND"s a new condition with the conditions in this Criteria.
   * Equivalent to <code>#and(Object, Object)</code> but better to read
   * if this is the first condition to be added to the Criteria.
   * Depending on rValue, the condition is constructed differently:
   * Either rValue is a unary comparison operator (i.e. is a SqlEnum and
   * getNumberOfCompareOperands() == 1) (e.g. Criteria.ISNULL),
   * then lValue is taken as single operand of the operator
   * ant the passed operator is used for comparison.
   * Otherwise, an EQUAL comparison is used for comparing rValue and lValue.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.g. string object), it is interpreted as
   * literal value.
   * @param rValue The right hand side of the comparison, may be null.
   * If this object is a unary comparison operator, it is taken as
   * comparison operator of the condition to add.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.g. string object), it is interpreted as
   * literal value.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria where(final Object lValue, final Object rValue)
  {
    return and(lValue, rValue);
  }

  /**
   * "AND"s a new condition with the conditions in this Criteria.
   * Equivalent to <code>#and(Column, Object, SqlEnum)</code> but better to
   * read if this is the first condition to be added to the Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValue The right hand side of the comparison, may be null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param comparison the comparison, or <code>Criteria.CUSTOM</code>
   * to specify the expression manually in the value parameter.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria where(
     final Object lValue,
     final Object rValue,
     final SqlEnum comparison)
  {
    return and(lValue, rValue, comparison);
  }

  /**
   * Convenience method to AND a new date comparison with the conditions
   * in this Criteria. Equivalent to
   * <code>#andDate(Column, int, int, int)</code> but better to read
   * if this is the first condition to be added to the Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param year The year to compare to.
   * @param month The month to compare to.
   * @param day The day to compare to.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereDate(
     final Object lValue,
     final int year,
     final int month,
     final int day)
  {
    return andDate(lValue, year, month, day);
  }

  /**
   * Convenience method to AND a new date comparison with the conditions
   * in this Criteria. Equivalent to
   * <code>#andDate(Column, int, int, int, SqlEnum)</code> but better to read
   * if this is the first condition to be added to the Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param year The year to compare to.
   * @param month The month to compare to.
   * @param day The day to compare to.
   * @param comparison The comparison operator.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereDate(
     final Object lValue,
     final int year,
     final int month,
     final int day,
     final SqlEnum comparison)
  {
    return andDate(lValue, year, month, day, comparison);
  }

  /**
   * Convenience method to AND a "in" comparison with the conditions
   * in this Criteria. Equivalent to <code>#andIn(Column, Object[])</code>
   * but better to read if this is the first condition to be added
   * to the Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereIn(final Object lValue, final Object[] rValues)
  {
    return andIn(lValue, rValues);
  }

  /**
   * Convenience method to AND a "in" comparison with the conditions
   * in this Criteria. Equivalent to <code>#andIn(Column, Collection)</code>
   * but better to read if this is the first condition to be added
   * to the Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereIn(final Object lValue, final Collection<?> rValues)
  {
    return andIn(lValue, rValues);
  }

  /**
   * Convenience method to AND a "not in" comparison with the conditions
   * in this Criteria. Equivalent to
   * <code>#andNotIn(Column, Object[])</code> but better to read
   * if this is the first condition to be added to the Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereNotIn(final Object lValue, final Object[] rValues)
  {
    return andNotIn(lValue, rValues);
  }

  /**
   * Convenience method to AND a "not in" comparison with the conditions
   * in this Criteria. Equivalent to
   * <code>#andNotIn(Column, Collection)</code> but better to read
   * if this is the first condition to be added to the Criteria.
   *
   * @param lValue The left hand side of the comparison, not null.
   * If this object implements the Column interface, it is interpreted
   * as a (pseudo)column.
   * If this object is a Criteria, it is interpreted as a subselect.
   * In all other cases, (e.G. string object), it is interpreted as
   * literal value.
   * @param rValues The values to compare against.
   *
   * @return A modified Criteria object.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereNotIn(final Object lValue, final Collection<?> rValues)
  {
    return andNotIn(lValue, rValues);
  }

  /**
   * Convenience method to AND a verbatim sql condition to this Criteria.
   * Equivalent to
   * <code>#andNotIn(String, String[])</code> but better to read
   * if this is the first condition to be added to the Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria criteria = new Criteria();
   * criteria.whereVerbatimSql("count(foo.x) = ?", new Object[] {0});
   * </code>
   *
   * @param sql the verbatim SQL to use.
   * @param replacements the replacements for the "?" placeholders in SQL.
   *
   * @return the modified Criteria.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereVerbatimSql(
     final String sql,
     final Object[] replacements)
  {
    Criterion criterion
       = new Criterion(null, null, null, sql, replacements);
    and(criterion);
    return this;
  }

  /**
   * Convenience method to AND a verbatim sql condition to this Criteria.
   * Equivalent to
   * <code>#andNotIn(String, String[], Column, Column)</code> but better
   * to read if this is the first condition to be added to the Criteria.
   * This is used as follows:
   * <p>
   * <code>
   * Criteria criteria = new Criteria();
   * criteria.whereVerbatimSql(
   *     "count(foo.x) = ?",
   *     new Object[] {0},
   *     FooPeer.X,
   *     null);
   * </code>
   *
   * @param sql the verbatim SQL to use.
   * @param replacements the replacements for the "?" placeholders in SQL.
   * @param toAddToFromClause1 a column to add to from clause, may be null.
   * @param toAddToFromClause2 a column to add to from clause, may be null.
   *
   * @return the modified Criteria.
   *
   * @throws TorqueRuntimeException if this operation is performed on a
   * Criteria composed of set parts (e.g. union, intersect, except).
   */
  public Criteria whereVerbatimSql(
     final String sql,
     final Object[] replacements,
     final Column toAddToFromClause1,
     final Column toAddToFromClause2)
  {
    Criterion criterion = new Criterion(
       toAddToFromClause1,
       toAddToFromClause2,
       null,
       sql,
       replacements);
    and(criterion);
    return this;
  }

  /**
   * Creates a SQL UNION between this Criteria and the passed other criteria.
   *
   * @param other the other part of the union.
   *
   * @return the modified Criteria.
   */
  public Criteria union(final Criteria other)
  {
    appendSetOperation(other, SqlEnum.UNION);
    return this;
  }

  /**
   * Creates a SQL UNION ALL between this Criteria
   * and the passed other criteria.
   *
   * @param other the other part of the union.
   *
   * @return the modified Criteria.
   */
  public Criteria unionAll(final Criteria other)
  {
    appendSetOperation(other, SqlEnum.UNION_ALL);
    return this;
  }

  /**
   * Creates a SQL INTERSECT between this Criteria
   * and the passed other criteria.
   *
   * @param other the other part of the union.
   *
   * @return the modified Criteria.
   */
  public Criteria intersect(final Criteria other)
  {
    appendSetOperation(other, SqlEnum.INTERSECT);
    return this;
  }

  /**
   * Creates a SQL INTERSECT ALL between this Criteria
   * and the passed other criteria.
   *
   * @param other the other part of the union.
   *
   * @return the modified Criteria.
   */
  public Criteria intersectAll(final Criteria other)
  {
    appendSetOperation(other, SqlEnum.INTERSECT_ALL);
    return this;
  }

  /**
   * Creates a SQL EXCEPT between this Criteria
   * and the passed other criteria.
   *
   * @param other the other part of the union.
   *
   * @return the modified Criteria.
   */
  public Criteria except(final Criteria other)
  {
    appendSetOperation(other, SqlEnum.EXCEPT);
    return this;
  }

  /**
   * Creates a SQL EXCEPT between this Criteria
   * and the passed other criteria.
   *
   * @param other the other part of the union.
   *
   * @return the modified Criteria.
   */
  public Criteria exceptAll(final Criteria other)
  {
    appendSetOperation(other, SqlEnum.EXCEPT_ALL);
    return this;
  }

  /**
   * Appends a set operation (union, except, intersect) to this Criteria.
   * If not already done, this criteria is converted to a composite criteria.
   *
   * @param other the other criteria, not null.
   * @param setOperator the set operator, not null.
   *
   * @throws NullPointerException if other or setOperator are null.
   */
  protected void appendSetOperation(
     final Criteria other,
     final SqlEnum setOperator)
  {
    if(other == null)
    {
      throw new NullPointerException("other must not be null");
    }
    if(setOperator == null)
    {
      throw new NullPointerException("setOperator must not be null");
    }
    if(isComposite() && this.setOperator.equals(setOperator))
    {
      setCriteriaParts.add(other);
    }
    else
    {
      Criteria copy = new Criteria(this);
      this.clear();
      setCriteriaParts.add(copy);
      setCriteriaParts.add(other);
    }
    this.setOperator = setOperator;
  }

  /**
   * Return the parts of the criteria which compose a query using
   * set operations (union, except, intersect).
   *
   * @return the parts, not null, empty if this query does not contain set
   * operations
   */
  public List<Criteria> getSetCriteriaParts()
  {
    return setCriteriaParts;
  }

  /**
   * Returns the operator between the set operations.
   *
   * @return the operator, or null if this is not a composite criteria.
   */
  public SqlEnum getSetOperator()
  {
    return setOperator;
  }

  /**
   * Returns whether this Criteria is a composite criteria, i.e. is composed
   * from more than one Criteria related by set operations
   * (e.g. union, except, intersect)..
   *
   * @return true if the criteria consists of several parts connected
   * by set operations, false otherwise.
   */
  public boolean isComposite()
  {
    return !setCriteriaParts.isEmpty();
  }

  /**
   * Checks that this Criteria is no composite Criteria, and throws a
   * TorqueRuntimeException otherwise.
   *
   * @throws TorqueRuntimeException if this Criteria is a composite Criteria.
   */
  protected void assertNoComposite()
  {
    if(isComposite())
    {
      throw new TorqueRuntimeException(
         "This operation cannot be performed "
         + "on a composite Criteria, which is composed "
         + "of several set operations."
         + " Try to perform the operation on the single parts"
         + " of this Criteria");
    }
  }
}
