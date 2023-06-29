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

import java.util.ArrayList;

import org.apache.torque.Column;

/**
 * List with unique entries. UniqueList does not allow null nor will
 * Columns with the same SQL expression be added twice.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: UniqueColumnList.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class UniqueColumnList extends ArrayList<Column>
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 4467847559423445120L;

    /**
     * Constructs an empty UniqueList.
     */
    public UniqueColumnList()
    {
        // empty
    }

    /**
     * Copy-constructor. Creates a shallow copy of an UniqueList.
     * @param list the uniqueList to copy
     */
    public UniqueColumnList(UniqueColumnList list)
    {
        this.addAll(list);
    }

    /**
     * Adds a Column to the list, if no column with the same SQL Expression
     * is not already contained.
     *
     * @param column the Column to add, not null.
     *
     * @return true if the Object is added.
     *
     * @throws NullPointerException if column is null.
     */
    @Override
    public boolean add(Column column)
    {
        if (column == null)
        {
            throw new NullPointerException("column must not be null");
        }
        if (!containsSqlExpression(column))
        {
            return super.add(column);
        }
        return false;
    }

    /**
     * Checks if this list already contains a column with the same
     * SQL expression.
     *
     * @param column the column to check, not null.
     *
     * @return true if a column with the same Sql Expression is contained,
     *         false otherwise.
     */
    public boolean containsSqlExpression(Column column)
    {
        for (Column candidate : this)
        {
            if (candidate.getSqlExpression().equals(
                    column.getSqlExpression()))
            {
                return true;
            }
        }
        return false;
    }
}
