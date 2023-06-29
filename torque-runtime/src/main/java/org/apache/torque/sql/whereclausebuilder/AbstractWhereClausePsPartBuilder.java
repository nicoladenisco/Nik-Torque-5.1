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

import org.apache.torque.sql.objectbuilder.ObjectOrColumnPsPartBuilder;
import org.apache.torque.sql.objectbuilder.ObjectPsPartBuilder;

/**
 * Abstract base class for a WhereClausePsPartBuilder.
 *
 * @version $Id: AbstractWhereClausePsPartBuilder.java 1840416 2018-09-09 15:10:22Z tv $
 */
public abstract class AbstractWhereClausePsPartBuilder
implements WhereClausePsPartBuilder
{
    /** The ObjectPsPartBuilder to use for single values or columns. */
    private static ObjectPsPartBuilder objectOrColumnPsPartBuilder
        = new ObjectOrColumnPsPartBuilder();

    /**
     * Returns the ObjectPsPartBuilder to use for single values or columns.
     *
     * @return the ObjectPsPartBuilder to use.
     */
    public static ObjectPsPartBuilder getObjectOrColumnPsPartBuilder()
    {
        return objectOrColumnPsPartBuilder;
    }

    /**
     * Set the ObjectPsPartBuilder to use for single values or columns.
     *
     * @param builder the new ObjectPsPartBuilder to use.
     */
    public static void setObjectOrColumnPsPartBuilder(
            ObjectPsPartBuilder builder)
    {
        AbstractWhereClausePsPartBuilder.objectOrColumnPsPartBuilder = builder;
    }
}
