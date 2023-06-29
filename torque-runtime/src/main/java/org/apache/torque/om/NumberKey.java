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

import java.math.BigDecimal;
import java.sql.Types;

/**
 * This class can be used as an ObjectKey to uniquely identify an
 * object within an application where the id  consists
 * of a single entity such a GUID or the value of a db row's primary key.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @author <a href="mailto:stephenh@chase3000.com">Stephen Haberman</a>
 * @author <a href="mailto:rg@onepercentsoftware.com">Runako Godfrey</a>
 * @version $Id: NumberKey.java 1849379 2018-12-20 12:33:43Z tv $
 */
public class NumberKey extends SimpleKey<BigDecimal>
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -5566819786708264162L;

    /**
     * Initializes the internal key value to <code>null</code>.
     */
    public NumberKey()
    {
        super();
    }

    /**
     * Creates an NumberKey and set its internal representation
     *
     * @param key the key value as String
     */
    public NumberKey(String key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates an NumberKey and set its internal representation
     *
     * @param key the key value
     */
    public NumberKey(BigDecimal key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates a NumberKey that is equivalent to key.
     *
     * @param key the key value
     */
    public NumberKey(NumberKey key)
    {
        super();
        setValue(key);
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     *
     * @param key the key value
     */
    public NumberKey(long key)
    {
        setValue(BigDecimal.valueOf(key));
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     *
     * @param key the key value
     */
    public NumberKey(double key)
    {
        setValue(new BigDecimal(key));
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     * Convenience only.
     *
     * @param key the key value
     */
    public NumberKey(int key)
    {
        this((long) key);
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     * Convenience only.
     *
     * @param key the key value
     */
    public NumberKey(Number key)
    {
        if (key != null)
        {
            setValue(new BigDecimal(key.toString()));
        }
        else
        {
            setValue((BigDecimal)null);
        }
    }

    /**
     * Sets the internal representation using a String representation
     * of a number.
     *
     * @param key the key value
     * @throws NumberFormatException if key is not a valid number
     */
    public void setValue(String key)
    {
        if (key != null)
        {
            setValue(new BigDecimal(key));
        }
        else
        {
            setValue((BigDecimal)null);
        }
    }

    /**
     * Returns the JDBC type of the key
     * as defined in <code>java.sql.Types</code>.
     *
     * @return <code>Types.NUMERIC</code>.
     */
    @Override
    public int getJdbcType()
    {
        return Types.NUMERIC;
    }

    /**
     * @param o the comparison value
     * @return a numeric comparison of the two values
     */
    @Override
    public int compareTo(Object o)
    {
        return getValue().compareTo(((NumberKey) o).getValue());
    }

    /**
     * Returns the value of this NumberKey as a byte. This value is subject
     * to the conversion rules set out in
     * {@link java.math.BigDecimal#byteValue()}
     *
     * @return the NumberKey converted to a byte
     */
    public byte byteValue()
    {
        return getValue().byteValue();
    }

    /**
     * Returns the value of this NumberKey as an int. This value is subject
     * to the conversion rules set out in
     * {@link java.math.BigDecimal#intValue()}, importantly any fractional part
     * will be discarded and if the underlying value is too big to fit in an
     * int, only the low-order 32 bits are returned. Note that this
     * conversion can lose information about the overall magnitude and
     * precision of the NumberKey value as well as return a result with the
     * opposite sign.
     *
     * @return the NumberKey converted to an int
     */
    public int intValue()
    {
        return getValue().intValue();
    }

    /**
     * Returns the value of this NumberKey as a short. This value is subject
     * to the conversion rules set out in
     * {@link java.math.BigDecimal#intValue()}, importantly any fractional part
     *  will be discarded and if the underlying value is too big to fit
     * in a long, only the low-order 64 bits are returned. Note that this
     * conversion can lose information about the overall magnitude and
     * precision of the NumberKey value as well as return a result with the
     * opposite sign.
     *
     * @return the NumberKey converted to a short
     */
    public short shortValue()
    {
        return getValue().shortValue();
    }

    /**
     * Returns the value of this NumberKey as a long. This value is subject
     * to the conversion rules set out in
     * {@link java.math.BigDecimal#intValue()}
     *
     * @return the NumberKey converted to a long
     */
    public long longValue()
    {
        return getValue().longValue();
    }

    /**
     * Returns the value of this NumberKey as a float. This value is subject to
     * the conversion rules set out in
     * {@link java.math.BigDecimal#floatValue()}, most importantly if the
     * underlying value has too great a magnitude to represent as a
     * float, it will be converted to Float.NEGATIVE_INFINITY
     * or Float.POSITIVE_INFINITY as appropriate.
     *
     * @return the NumberKey converted to a float
     */
    public float floatValue()
    {
        return getValue().floatValue();
    }

    /**
     * Returns the value of this NumberKey as a double. This value is subject
     * to the conversion rules set out in
     * {@link java.math.BigDecimal#doubleValue()}, most importantly if the
     * underlying value has too great a magnitude to represent as a
     * double, it will be converted to Double.NEGATIVE_INFINITY
     * or Double.POSITIVE_INFINITY as appropriate.
     *
     * @return the NumberKey converted to a double
     */
    public double doubleValue()
    {
        return getValue().doubleValue();
    }
}
