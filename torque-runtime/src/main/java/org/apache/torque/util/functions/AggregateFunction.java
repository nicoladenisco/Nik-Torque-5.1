package org.apache.torque.util.functions;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.Column;

/**
 * <p>A container for classes that will generate SQL for the SQL99 Standard
 * Aggregate functions. These can be used via the Criteria.addSelectColumn
 * method to produce SQL statements that can be called via the
 * BasePeerImpl.doSelect methods.</p>
 * <p>
 * Note database servers that use non-standard function names
 * can be supported by setting the function name in the constructor accordingly.
 * </p>
 * <p>
 * E.g., older MySQL servers use LEAST instead of MIN. This can be
 * supported by supplying "LEAST" as function name.</p>
 *
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: AggregateFunction.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class AggregateFunction implements SQLFunction
{
    /** Should the column have DISTINCT added before it */
    private boolean distinct;

    /** The function to use */
    private String function;

    /** The column to apply the function to, not null. */
    private Column column;

    /**
     * Constructor for aggregate functions.
     *
     * @param function the function name, not null or blank.
     * @param column the column to apply the function to, not null.
     * @param distinct whether to apply DISTINCT to the column.
     */
    protected AggregateFunction(
            String function,
            Column column,
            boolean distinct)
    {
        if (StringUtils.isBlank(function))
        {
            throw new IllegalArgumentException(
                    "function must not be null or blank");
        }
        this.function = function;
        setColumn(column);
        this.distinct = distinct;
    }

    /**
     * Returns the column the function is applied to.
     *
     * @return the column, not null.
     */
    @Override
    public Column getColumn()
    {
        return column;
    }

    /**
     * Sets the column the function is applied to.
     *
     * @param column the column, not null.
     */
    public void setColumn(Column column)
    {
        if (column == null)
        {
            throw new IllegalArgumentException(
                    "column must not be null");
        }
        this.column = column;
    }

    /**
     * Should the column have DISTINCT added in front of it?
     *
     * @return True if DISTINCT is needed.
     */
    public boolean isDistinct()
    {
        return this.distinct;
    }

    /**
     * Get the function name to use, e.g. AVG, MIN, LEAST.
     *
     * @return The function name.
     */
    protected String getFunction()
    {
        return this.function;
    }

    /**
     * Set the function name to use, e.g. AVG, MIN, LEAST.
     *
     * @param function The function name to use, not null or blank.
     *
     * @throws UnsupportedOperationException if a subclass does not support
     *         changing the function name; never thrown by this implementation.
     */
    public void setFunction(String function)
    {
        if (StringUtils.isBlank(function))
        {
            throw new IllegalArgumentException(
                    "function must not be null or blank");
        }
        this.function = function;
    }

    /**
     * Generate the SQL for this function.
     *
     * @throws IllegalStateException if the arguments are not set
     */
    @Override
    public String getSqlExpression()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getFunction()).append("(");
        if (isDistinct())
        {
            sb.append("DISTINCT ");
        }
        sb.append(column.getSqlExpression())
        .append(")");
        return sb.toString();
    }

    @Override
    public Object getArgument(int i)
    {
        switch (i)
        {
        case 0:
            return column;
        case 1:
            return distinct;
        default:
            return null;
        }
    }


    @Override
    public Object[] getArguments()
    {
        return new Object[] {column, distinct};
    }

    /**
     * Assumes that there are one or two arguments being specified.  The
     * first being a column identifier,
     * and the second being an optional boolean indicating if DISTINCT
     * needs to be added.
     *
     * @param args The column to apply the function to.
     * @throws IllegalArgumentException If at least one argument has not
     *                              been supplied or the second argument
     *                              object is not Boolean.
     */
    @Override
    public void setArguments(Object... args)
    {

        if (args.length < 1)
        {
            throw new IllegalArgumentException(
                    "There must be at least one argument object specified!");
        }
        if (args.length < 2)
        {
            this.distinct = false;
        }
        else
        {
            if (!(args[1] instanceof Boolean))
            {
                throw new IllegalArgumentException(
                        "Second argument object is not type Boolean!");
            }
            this.distinct = ((Boolean) args[1]).booleanValue();
        }
        if (!(args[0] instanceof Column))
        {
            throw new IllegalArgumentException(
                    "First argument object is not type Column!");
        }
        this.column = (Column) args[0];
    }

    /**
     * Returns the column name.
     * This implementation always return null because we do not reference
     * a real column.
     *
     * @return the column name, always null.
     */
    @Override
    public String getColumnName()
    {
        return null;
    }

    @Override
    public String getTableName()
    {
        return column.getTableName();
    }

    @Override
    public String getSchemaName()
    {
        return column.getSchemaName();
    }

    @Override
    public String getFullTableName()
    {
        return column.getFullTableName();
    }
}
