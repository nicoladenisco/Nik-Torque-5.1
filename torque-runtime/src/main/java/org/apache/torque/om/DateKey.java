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
import java.util.Date;

/**
 * This class can be used as an ObjectKey to uniquely identify an
 * object within an application where the id is a Date.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @version $Id: DateKey.java 1849379 2018-12-20 12:33:43Z tv $
 */
public class DateKey extends SimpleKey<Date>
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 3102583536685348517L;

    /**
     * Initializes the internal key value to <code>null</code>.
     */
    public DateKey()
    {
        super();
    }

    /**
     * Creates an DateKey and set its internal representation
     *
     * @param key the key value as String
     */
    public DateKey(String key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates an DateKey and set its internal representation
     *
     * @param key the key value
     */
    public DateKey(Date key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates a DateKey that is equivalent to key.
     *
     * @param key the key value
     */
    public DateKey(DateKey key)
    {
        super();
        setValue(key);
    }

    /**
     * Sets the internal representation to a String
     *
     * @param key the key value
     */
    public void setValue(String key)
    {
        if (key != null)
        {
            setValue(new Date(Long.parseLong(key)));
        }
        else
        {
            setValue((Date)null);
        }
    }

    /**
     * Returns the JDBC type of the key
     * as defined in <code>java.sql.Types</code>.
     *
     * @return <code>Types.TIMESTAMP</code>.
     */
    @Override
    public int getJdbcType()
    {
        return Types.TIMESTAMP;
    }

    /**
     * Get a String representation for this key.
     *
     * @return a String representation of the Date or an empty String if the
     *          Date is null
     */
    @Override
    public String toString()
    {
        Date dt = getValue();
        return (dt == null ? "" : Long.toString(dt.getTime()));
    }
}
