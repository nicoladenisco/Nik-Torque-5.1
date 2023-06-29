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
 * object within an application where the id consists
 * of a Boolean.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @version $Id: BooleanKey.java 1849379 2018-12-20 12:33:43Z tv $
 */
public class BooleanKey extends SimpleKey<Boolean>
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 5109588772086713341L;

    /**
     * Initializes the internal key value to <code>null</code>.
     */
    public BooleanKey()
    {
        super();
    }

    /**
     * Creates an BooleanKey and set its internal representation
     *
     * @param key the key value as String
     */
    public BooleanKey(String key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates an BooleanKey and set its internal representation
     *
     * @param key the key value
     */
    public BooleanKey(Boolean key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates a BooleanKey that is equivalent to key.
     *
     * @param key the key value
     */
    public BooleanKey(BooleanKey key)
    {
        super();
        setValue(key);
    }

    /**
     * Sets the internal representation to a String.
     *
     * @param key the key value
     */
    public void setValue(String key)
    {
        if (key == null)
        {
            setValue((Boolean)null);
        }
        else
        {
            setValue(Boolean.parseBoolean(key));
        }
    }

    /**
     * Returns the JDBC type of the key
     * as defined in <code>java.sql.Types</code>.
     *
     * @return <code>Types.BIT</code>.
     */
    @Override
    public int getJdbcType()
    {
        return Types.BIT;
    }
}
