package org.apache.torque.om;

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

import java.sql.Types;

/**
 * This class can be used as an ObjectKey to uniquely identify an
 * object within an application where the id  consists
 * of a single entity such a GUID or the value of a db row's primary key.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @version $Id: StringKey.java 1849379 2018-12-20 12:33:43Z tv $
 */
public class StringKey extends SimpleKey<String>
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 5109588772086713341L;

    /**
     * Initializes the internal key value to <code>null</code>.
     */
    public StringKey()
    {
        super();
    }

    /**
     * Creates an StringKey and set its internal representation
     *
     * @param key the key value as String
     */
    public StringKey(String key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates a StringKey that is equivalent to key.
     *
     * @param key the key value
     */
    public StringKey(StringKey key)
    {
        super();
        setValue(key);
    }

    /**
     * Returns the JDBC type of the key
     * as defined in <code>java.sql.Types</code>.
     *
     * @return <code>Types.VARCHAR</code>.
     */
    @Override
    public int getJdbcType()
    {
        return Types.VARCHAR;
    }
}
