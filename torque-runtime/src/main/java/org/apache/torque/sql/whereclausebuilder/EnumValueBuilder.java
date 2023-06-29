package org.apache.torque.sql.whereclausebuilder;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.PreparedStatementPart;
import org.apache.torque.sql.Query;
import org.apache.torque.sql.SqlBuilder;
import org.apache.torque.sql.WhereClauseExpression;

/**
 * Builds a PreparedStatementPart from a WhereClauseExpression containing an enum object.
 *
 * @version $Id: InBuilder.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public class EnumValueBuilder extends AbstractWhereClausePsPartBuilder
{
    /**
     * Takes a WhereClauseExpression containing a enum object and unwraps the enum value.
     *
     * @param whereClausePart the part of the where clause to build.
     *        Can be modified in this method.
     * @param ignoreCase If true and columns represent Strings, the appropriate
     *        function defined for the database will be used to ignore
     *        differences in case.
     * @param query the query which is currently built
     * @param adapter The adapter for the database for which the SQL
     *        should be created, not null.
     */
    @Override
    public PreparedStatementPart buildPs(
            final WhereClauseExpression whereClausePart,
            final boolean ignoreCase,
            final Query query,
            final Adapter adapter)
                    throws TorqueException
    {
        if (whereClausePart.getLValue() instanceof Enum)
        {
            whereClausePart.setLValue(getWrappedValue(whereClausePart.getLValue()));
        }
        if (whereClausePart.getRValue() instanceof Enum)
        {
            whereClausePart.setRValue(getWrappedValue(whereClausePart.getRValue()));
        }
        for (WhereClausePsPartBuilder builder : SqlBuilder.getWhereClausePsPartBuilders())
        {
            if (builder.isApplicable(whereClausePart, adapter))
            {
                return builder.buildPs(
                        whereClausePart,
                        ignoreCase,
                        query,
                        adapter);
            }
        }
        // should not happen as last element in list is standardHandler
        // which takes all
        throw new RuntimeException("No handler found for whereClausePart " + whereClausePart);
    }

    protected Object getWrappedValue(final Object wrapped) throws TorqueException
    {
        Class<?> clazz = wrapped.getClass();
        Method getValueMethod;
        try
        {
            getValueMethod = clazz.getMethod("getValue");
        }
        catch (SecurityException e)
        {
            throw new TorqueException("Could not access the getValue() method of the class of an enum value, " + clazz.getName(),
                    e);
        }
        catch (NoSuchMethodException e)
        {
            throw new TorqueException("An enum is used as Criterion value but its class, " + clazz.getName()
            + ", does not have a parameterless getValue() method");
        }
        if (getValueMethod.getReturnType().equals(Void.class))
        {
            throw new TorqueException("An enum is given as Criterion value but its class, " + clazz.getName()
            + ", has a parameterless getValue() method which retirsns void. It should return the wrapped type instead.");
        }
        try
        {
            return getValueMethod.invoke(wrapped);
        }
        catch (IllegalArgumentException e)
        {
            // should not happen
            throw new TorqueException("Could not invoke the getValue() method of the class of an enum value, " + clazz.getName(),
                    e);
        }
        catch (IllegalAccessException e)
        {
            throw new TorqueException("Could not access the getValue() method of the class of an enum value, " + clazz.getName(),
                    e);
        }
        catch (InvocationTargetException e)
        {
            throw new TorqueException("The getValue() method of the class of an enum value, " + clazz.getName()
            + " threw an exception",
            e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isApplicable(
            final WhereClauseExpression whereClauseExpression,
            final Adapter adapter)
    {
        return (whereClauseExpression.getLValue() instanceof Enum
                || whereClauseExpression.getRValue() instanceof Enum);
    }
}
