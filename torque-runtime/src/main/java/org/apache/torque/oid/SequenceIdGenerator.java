package org.apache.torque.oid;

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

import org.apache.torque.adapter.Adapter;

/**
 * This generator works with databases that have an sql syntax for
 * getting an id prior to inserting a row into the database.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @version $Id: SequenceIdGenerator.java 1850586 2019-01-06 18:46:35Z tv $
 */
public class SequenceIdGenerator extends AbstractIdGenerator
{
    /**
     * Creates an IdGenerator which will work with the specified database.
     *
     * @param adapter the adapter that knows the correct sql syntax.
     * @param databaseName The name of the database to find the
     *        correct schema.
     */
    public SequenceIdGenerator(final Adapter adapter, final String databaseName)
    {
        super(adapter, databaseName);
    }

    /**
     * A flag to determine the timing of the id generation
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean isPriorToInsert()
    {
        return true;
    }

    /**
     * A flag to determine the timing of the id generation
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean isPostInsert()
    {
        return false;
    }

    /**
     * A flag to determine whether a Connection is required to
     * generate an id.
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean isConnectionRequired()
    {
        return true;
    }

    /**
     * A flag to determine whether Statement#getGeneratedKeys()
     * should be used.
     *
     * @return a <code>boolean</code> value
     */
    @Override
    public boolean isGetGeneratedKeysSupported()
    {
        return false;
    }
}
