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

import java.io.Serializable;
import java.util.Objects;

/**
 * This class can be used to uniquely identify an object within
 * an application.  There are four subclasses: StringKey, NumberKey,
 * and DateKey, and ComboKey which is a Key made up of a combination
 * of the first three.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @version $Id: ObjectKey.java 1850965 2019-01-10 17:21:29Z painter $
 */
public abstract class ObjectKey<T> implements Serializable, Comparable<Object>
{
    /** Version id for serializing. */
    private static final long serialVersionUID = 1L;

    /**
     * The underlying key value.
     */
    private T key;

    /**
     * Initializes the internal key value to <code>null</code>.
     */
    public ObjectKey()
    {
        key = null;
    }

    /**
     * Returns the hashcode of the underlying value (key), if key is
     * not null. Otherwise calls Object.hashCode()
     *
     * @return an <code>int</code> value
     */
    @Override
    public int hashCode()
    {
        if (key == null)
        {
            return super.hashCode();
        }
        return key.hashCode();
    }

    /**
     * Returns whether this ObjekctKey is equal to another Object.
     * obj is equal to this ObjectKey if obj has the same class
     * as this ObjectKey and contains the same information
     * this key contains.
     * Two ObjectKeys that both contain null values are not considered equal.
     *
     * @param obj the comparison value.
     *
     * @return whether the two objects are equal.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (obj.getClass() != this.getClass())
        {
            return false;
        }

        ObjectKey<?> objectKey = (ObjectKey<?>) obj;
        if (key == null)
        {
            return false;
        }
        return key.equals(objectKey.getValue());
    }

    /**
     * Get the underlying object.
     *
     * @return the underlying object
     */
    public T getValue()
    {
        return key;
    }

    /**
     * Returns the JDBC type of the key
     * as defined in <code>java.sql.Types</code>.
     *
     * @return the JDBC type of the key.
     */
    public abstract int getJdbcType();

//    /**
//     * Returns the JDBC type of the key
//     * as defined in <code>java.sql.Types</code>.
//     *
//     * @return the JDBC type of the key.
//     */
//    public abstract JDBCType getJdbcType();

    /**
     * Appends a String representation of the key to a buffer.
     *
     * @param sb a <code>StringBuilder</code>
     */
    public void appendTo(StringBuilder sb)
    {
        sb.append(this.toString());
    }

    /**
     * Implements the compareTo method.
     *
     * @param obj the object to compare to this object
     * @return a numeric comparison of the two values
     */
    @Override
    public int compareTo(Object obj)
    {
        return toString().compareTo(obj.toString());
    }

    /**
     * Sets the internal representation.
     *
     * @param key the key value
     */
    public void setValue(T key)
    {
        this.key = key;
    }

    /**
     * Sets the internal representation to the same object used by key.
     *
     * @param <O> the key type
     * @param key the key value
     */
    public <O extends ObjectKey<T>> void setValue(O key)
    {
        if (key != null)
        {
            this.key = key.getValue();
        }
        else
        {
            this.key = null;
        }
    }

    /**
     * Get a String representation of this key.
     *
     * @return a String representation of this key,
     *         or an empty String if the value is null.
     */
    @Override
    public String toString()
    {
        return Objects.toString(key, "");
    }
}
