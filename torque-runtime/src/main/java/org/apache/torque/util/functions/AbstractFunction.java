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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.torque.Column;

/**
 * A default framework that implements the core SQLFunction interface
 * requirements that can be used to build specific functions on.
 *
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: AbstractFunction.java 1879896 2020-07-15 15:03:46Z gk $
 */
public abstract class AbstractFunction implements SQLFunction
{
    /** The arguments being used by this function */
    private List<Object> argumentList = new ArrayList<>();

    /**
     * Functions should only be created via the FunctionFactory class.
     */
    protected AbstractFunction()
    {
        super();
    }

    /**
     * This should return the SQL string that can be used
     * when constructing the query.  E.g. "AVG( table.column )" or
     * CONCAT(table.column, " foobar");
     *
     * @return The SQL String.
     */
    @Override
    public abstract String getSqlExpression();

    /**
     * Return all the parameters as an object array. This allow for
     * processing of the parameters in their original format rather
     * than just in String format.  E.g. a parameter might be specified
     * as a Date object, or a Column object.
     *
     * @return Should return a valid Object array and not null.  E.g.
     *  implementors should return new Object[0] if there are no parameters.
     */
    @Override
    public Object[] getArguments()
    {
        Object[] args = getArgumentList().toArray();
        if (args == null)
        {
            args = new Object[0];
        }
        return args;
    }

    /**
     * Sets the function arguments.
     *
     * @param args the function arguments, not null.
     */
    @Override
    public void setArguments(Object... args)
    {
        this.argumentList = new ArrayList<>(Arrays.asList(args));
    }

    /**
     * Returns the column to which this function is applied.
     *
     * @return the column, not null.
     *
     * @throws IllegalStateException if the column cannot be determined.
     */
    @Override
    public Column getColumn()
    {
        for (Object argument : getArgumentList())
        {
            if (argument instanceof Column)
            {
                return (Column) argument;
            }
        }
        throw new IllegalStateException(
                "Column could not be determined from arguments "
                        + getArgumentList());
    }

    /**
     * Return the object representation of the function parameter
     * at the specified index.  Will be null if parameter does not exist.
     *
     * @param index The 0 based index of the parameter to get.
     * @return The parameter object.  Null if one does not
     *         exist.
     */
    @Override
    public Object getArgument(int index)
    {
        List<Object> argList = getArgumentList();
        if (index >= argList.size())
        {
            return null;
        }
        return argList.get(index);
    }

    /**
     * Add an argument to the function argument list
     *
     * @param arg The argument object.
     */
    protected void addArgument(Object arg)
    {
        getArgumentList().add(arg);
    }

    /**
     * Set the full function argument list.
     *
     * @param args The new argument list
     */
    protected void setArgumentList(List<Object> args)
    {
        this.argumentList = args;
    }

    /**
     * Get the full list of function arguments
     *
     * @return The argument list
     */
    protected List<Object> getArgumentList()
    {
        if (this.argumentList == null)
        {
            this.argumentList = new ArrayList<>();
        }
        return this.argumentList;
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


    /**
     * Returns the name of the associated table
     * (not prefixed by the schema name) from the function argument(s).
     * In case that no unique table name can be determined, null is returned.
     *
     * @return the name of the table, may be null but not blank.
     */
    @Override
    public String getTableName()
    {
        String tableName = null;
        boolean columnFound = false;
        for (Object argument : getArgumentList())
        {
            if (argument instanceof Column)
            {
                Column column = (Column) argument;
                if (columnFound
                        && !Objects.equals(tableName, column.getTableName()))
                {
                    // cannot determine unique table name, return null
                    return null;
                }
                tableName = column.getTableName();
                columnFound = true;
            }
        }
        return tableName;
    }

    /**
     * Returns the name of any fixed schema prefix for the column's table
     * (if any) from the function argument(s).
     * In case that no unique schema can be determined, null is returned.
     *
     * @return the schema name, or null if the schema is not known.
     */
    @Override
    public String getSchemaName()
    {
        String schemaName = null;
        boolean columnFound = false;
        for (Object argument : getArgumentList())
        {
            if (argument instanceof Column)
            {
                Column column = (Column) argument;
                if (columnFound
                        && !Objects.equals(schemaName, column.getSchemaName()))
                {
                    // cannot determine unique schema name, return null
                    return null;
                }
                schemaName = column.getSchemaName();
                columnFound = true;
            }
        }
        return schemaName;
    }

    /**
     * Returns the table name prefixed with the schema name if it exists
     * from the function argument(s).
     * I.e. if a schema name exists, the result will be schemaName.tableName,
     * and otherwise it will just be tableName.
     * In case that no unique full table can be determined, null is returned.
     *
     * @return the fully qualified table name may be null but not blank.
     */
    @Override
    public String getFullTableName()
    {
        String fullTableName = null;
        boolean columnFound = false;
        for (Object argument : getArgumentList())
        {
            if (argument instanceof Column)
            {
                Column column = (Column) argument;
                if (columnFound
                        && !Objects.equals(
                                fullTableName,
                                column.getFullTableName()))
                {
                    // cannot determine unique full table name, return null
                    return null;
                }
                fullTableName = column.getFullTableName();
                columnFound = true;
            }
        }
        return fullTableName;
    }
}
