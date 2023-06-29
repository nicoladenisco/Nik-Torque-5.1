package org.apache.torque.templates.typemapping;

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

/**
 * All available result set getter methods.
 *
 * $Id: ResultSetGetter.java 1839288 2018-08-27 09:48:33Z tv $
 */
public enum ResultSetGetter
{
    /** getArray Method. */
    ARRAY("getArray"),
    /** getAsciiStream Method. */
    ASCII_STREAM("getAsciiStream"),
    /** getBigDecimal Method. */
    BIG_DECIMAL("getBigDecimal"),
    /** getBinaryStream Method. */
    BINARY_STREAM("getBinaryStream"),
    /** getBlob Method. */
    BLOB("getBlob"),
    /** getBoolean Method. */
    BOOLEAN("getBoolean"),
    /** getByte Method. */
    BYTE("getByte"),
    /** getBytes Method. */
    BYTES("getBytes"),
    /** getCharacterStream Method. */
    CHARACTER_STREAM("getCharacterStream"),
    /** getClob Method. */
    CLOB("getClob"),
    /** getDate Method. */
    DATE("getDate"),
    /** getDouble Method. */
    DOUBLE("getDouble"),
    /** getFloat Method. */
    FLOAT("getFloat"),
    /** getInt Method. */
    INT("getInt"),
    /** getLong Method. */
    LONG("getLong"),
    /** getObject Method. */
    OBJECT("getObject"),
    /** getRef Method. */
    REF("getRef"),
    /** getShort Method. */
    SHORT("getShort"),
    /** getString Method. */
    STRING("getString"),
    /** getTime Method. */
    TIME("getTime"),
    /** getTimestamp Method. */
    TIMESTAMP("getTimestamp"),
    /** getUnicodeStream Method. */
    UNICODE_STREAM("getUnicodeStream"),
    /** getURL Method. */
    URL("getURL");

    /** The method name. */
    private String method;

    /**
     * Constructor.
     *
     * @param method the method name, not null.
     */
    private ResultSetGetter(String method)
    {
        this.method = method;
    }

    /**
     * Returns the method name.
     *
     * @return the method name, not null.
     */
    public String getMethod()
    {
        return method;
    }

    /**
     * Returns the method name.
     *
     * @return the method name, not null.
     */
    @Override
    public String toString()
    {
        return method;
    }

    /**
     * Retursn the ResultSetGetter with the given method name.
     *
     * @param methodName the method name.
     *
     * @return the matching ResultSetGetter, not null.
     *
     * @throws IllegalArgumentException if no matching ResultSetGetter
     *         exists.
     */
    public static ResultSetGetter getByMethodName(String methodName)
    {
        for (ResultSetGetter resultSetGetter : values())
        {
            if (resultSetGetter.getMethod().equals(methodName))
            {
                return resultSetGetter;
            }
        }
        throw new IllegalArgumentException("Unknown value " + methodName);
    }
}
