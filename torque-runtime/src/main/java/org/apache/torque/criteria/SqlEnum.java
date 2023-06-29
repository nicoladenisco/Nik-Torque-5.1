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

/**
 * A typesafe enum of SQL string fragments.  Used by Criteria and SqlExpression
 * to build queries.  Criteria also makes most of the constants available
 * in order to specify a criterion.
 *
 * @author <a href="mailto:jmcnally@collab.net"></a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: SqlEnum.java 1850965 2019-01-10 17:21:29Z painter $
 * @since 3.0
 */
public final class SqlEnum implements java.io.Serializable
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 5963149836513364800L;

    /** The SQL expression. */
    private final String s;

    /**
     * The number of operands, if the SqlEnum is a comparison operator.
     * -1 if the SqlEnum is no comparison operator.
     * A Set operand (e.g. in IN) is counted as one operand.
     */
    private final int numberOfCompareOperands;

    private SqlEnum(final String s, final int numberOfCompareOperands)
    {
        this.s = s;
        this.numberOfCompareOperands = numberOfCompareOperands;
    }

    @Override
    public String toString()
    {
        return s;
    }

    /**
     * Returns the number of operands, if the SqlEnum is a comparison operator.
     * A Set operand (e.g. in IN) is counted as one operand.
     *
     * @return the number of compare operands, or -1 if the SqlEnum
     *         is no comparison operator.
     */
    public int getNumberOfCompareOperands()
    {
        return numberOfCompareOperands;
    }

    /** SQL Expression "=". */
    public static final SqlEnum EQUAL =
            new SqlEnum("=", 2);
    /** SQL Expression "&lt;&gt;". */
    public static final SqlEnum NOT_EQUAL =
            new SqlEnum("<>", 2);
    /** SQL Expression "!=". */
    public static final SqlEnum ALT_NOT_EQUAL =
            new SqlEnum("!=", 2);
    /** SQL Expression "&gt;". */
    public static final SqlEnum GREATER_THAN =
            new SqlEnum(">", 2);
    /** SQL Expression "&lt;". */
    public static final SqlEnum LESS_THAN =
            new SqlEnum("<", 2);
    /** SQL Expression "&gt;=". */
    public static final SqlEnum GREATER_EQUAL =
            new SqlEnum(">=", 2);
    /** SQL Expression "&lt;=". */
    public static final SqlEnum LESS_EQUAL =
            new SqlEnum("<=", 2);
    /** SQL Expression " LIKE ". */
    public static final SqlEnum LIKE =
            new SqlEnum(" LIKE ", 2);
    /** SQL Expression " NOT LIKE ". */
    public static final SqlEnum NOT_LIKE =
            new SqlEnum(" NOT LIKE ", 2);
    /** SQL Expression " ILIKE ". */
    public static final SqlEnum ILIKE =
            new SqlEnum(" ILIKE ", 2);
    /** SQL Expression " NOT ILIKE ". */
    public static final SqlEnum NOT_ILIKE =
            new SqlEnum(" NOT ILIKE ", 2);
    /** SQL Expression " IN ". */
    public static final SqlEnum IN =
            new SqlEnum(" IN ", 2);
    /** SQL Expression " NOT IN ". */
    public static final SqlEnum NOT_IN =
            new SqlEnum(" NOT IN ", 2);
    /** SQL Expression "JOIN". */
    public static final SqlEnum JOIN =
            new SqlEnum("JOIN", -1);
    /** SQL Expression "DISTINCT ". */
    public static final SqlEnum DISTINCT =
            new SqlEnum("DISTINCT ", -1);
    /** SQL Expression "ALL ". */
    public static final SqlEnum ALL =
            new SqlEnum("ALL ", -1);
    /** SQL Expression "ASC". */
    public static final SqlEnum ASC =
            new SqlEnum("ASC", -1);
    /** SQL Expression "DESC". */
    public static final SqlEnum DESC =
            new SqlEnum("DESC", -1);
    /** SQL Expression " IS NULL". */
    public static final SqlEnum ISNULL =
            new SqlEnum(" IS NULL", 1);
    /** SQL Expression " IS NOT NULL". */
    public static final SqlEnum ISNOTNULL =
            new SqlEnum(" IS NOT NULL", 1);
    /** SQL Expression "CURRENT_DATE". */
    public static final SqlEnum CURRENT_DATE =
            new SqlEnum("CURRENT_DATE", -1);
    /** SQL Expression "CURRENT_TIME". */
    public static final SqlEnum CURRENT_TIME =
            new SqlEnum("CURRENT_TIME", -1);
    /** SQL Expression "CURRENT_TIMESTAMP". */
    public static final SqlEnum CURRENT_TIMESTAMP =
            new SqlEnum("CURRENT_TIMESTAMP", -1);
    /** SQL Expression " ON ". */
    public static final SqlEnum ON =
            new SqlEnum(" ON ", -1);
    /** SQL Expression " AS ". */
    public static final SqlEnum AS =
            new SqlEnum(" AS ", -1);
    /** SQL Expression " ESCAPE ". */
    public static final SqlEnum ESCAPE =
            new SqlEnum(" ESCAPE ", -1);
    /** SQL Expression " UNION ". */
    public static final SqlEnum UNION =
            new SqlEnum(" UNION ", -1);
    /** SQL Expression " UNION ALL ". */
    public static final SqlEnum UNION_ALL =
            new SqlEnum(" UNION ALL ", -1);
    /** SQL Expression " INTERSECT ". */
    public static final SqlEnum INTERSECT =
            new SqlEnum(" INTERSECT ", -1);
    /** SQL Expression " INTERSECT ALL ". */
    public static final SqlEnum INTERSECT_ALL =
            new SqlEnum(" INTERSECT ALL ", -1);
    /** SQL Expression " EXCEPT ". */
    public static final SqlEnum EXCEPT =
            new SqlEnum(" EXCEPT ", -1);
    /** SQL Expression " EXCEPT ALL ". */
    public static final SqlEnum EXCEPT_ALL =
            new SqlEnum(" EXCEPT ALL ", -1);
    /** SQL Expression " MINUS ". */
    public static final SqlEnum MINUS =
            new SqlEnum(" MINUS ", -1);
    /** SQL Expression " MINUS ALL ". */
    public static final SqlEnum MINUS_ALL =
            new SqlEnum(" MINUS ALL ", -1);

    /**
     * returns whether o is the same SqlEnum as this object.
     * Two SqlEnums are considered equal if they contain the same String.
     * @param o the object to compare the SqlEnum with.
     */
    @Override
    public boolean equals(final Object o)
    {
        if (o == null)
        {
            return false;
        }

        if (!(o instanceof SqlEnum))
        {
            return false;
        }

        SqlEnum otherEnum = (SqlEnum) o;


        // both null: true
        // other null, this not null: false
        // else compare
        return (otherEnum.s == null)
                ? (s == null)
                        : otherEnum.s.equals(s);
    }

    /**
     * returns a hashcode for this object which is consistent with equals()
     */
    @Override
    public int hashCode()
    {
        return (s == null)
                ? 0
                        : s.hashCode();
    }
}
