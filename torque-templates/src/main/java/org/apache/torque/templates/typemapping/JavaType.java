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
 * An enum for all java types known to the generator.
 *
 * @version "Id: $
 */
public enum JavaType
{
    /** java.lang.String. */
    STRING("String", false, false, null),
    /** java.math.BigDecimal. */
    BIG_DECIMAL("BigDecimal", false, true, "java.math."),
    /** primitive boolean. */
    BOOLEAN_PRIMITIVE("boolean", true, false, null),
    /** java.lang.Boolean. */
    BOOLEAN_OBJECT("Boolean", false, false, null),
    /** primitive byte. */
    BYTE_PRIMITIVE("byte", true, true, null),
    /** java.lang.Byte. */
    BYTE_OBJECT("Byte", false, true, null),
    /** primitive short. */
    SHORT_PRIMITIVE("short", true, true, null),
    /** java.lang.Short. */
    SHORT_OBJECT("Short", false, true, null),
    /** primitive int. */
    INTEGER_PRIMITIVE("int", true, true, null),
    /** java.lang.Integer. */
    INTEGER_OBJECT("Integer", false, true, null),
    /** primitive long. */
    LONG_PRIMITIVE("long", true, true, null),
    /** java.lang.Long. */
    LONG_OBJECT("Long", false, true, null),
    /** primitive float. */
    FLOAT_PRIMITIVE("float", true, true, null),
    /** java.lang.Float. */
    FLOAT_OBJECT("Float", false, true, null),
    /** primitive double. */
    DOUBLE_PRIMITIVE("double", true, true, null),
    /** java.lang.Double. */
    DOUBLE_OBJECT("Double", false, true, null),
    /** primitive char. */
    CHAR_PRIMITIVE("char", true, false, null),
    /** java.lang.Char. */
    CHAR_OBJECT("Char", false, false, null),
    /** primitive byte array. */
    BYTE_PRIMITIVE_ARRAY("byte[]", false, false, null),
    /** java.util.Date. */
    DATE("Date", false, false, "java.util."),
    /** java.lang.Enum. */
    ENUM("Enum", false, false, null);

    /** The class name without package name. */
    private String className;

    /** Whether the type is a primitive type. */
    private boolean primitive;

    /** Whether the type is a number. */
    private boolean number;

    /** The package prefix for object types, if different from java.lang. */
    private String packagePrefix;

    /**
     * Constructor.
     *
     * @param className the class name, not null.
     * @param primitive Whether the type is a primitive type.
     * @param number Whether the type is a number.
     * @param packagePrefix The package prefix for object types, or null.
     */
    private JavaType(
            final String className,
            final boolean primitive,
            final boolean number,
            final String packagePrefix)
    {
        this.className = className;
        this.primitive = primitive;
        this.number = number;
        this.packagePrefix = packagePrefix;
    }

    /**
     * Returns whether the type is a primitive type.
     *
     * @return true if the type is a primitive type, false otherwise.
     */
    public boolean isPrimitive()
    {
        return primitive;
    }

    /**
     * Returns whether the type is a number.
     *
     * @return true if the type is a number, false otherwise.
     */
    public boolean isNumber()
    {
        return number;
    }

    /**
     * Returns the class name.
     *
     * @return the unqualified class name.
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * Returns the package prefix.
     *
     * @return the package prefix, or null.
     */
    public String getPackagePrefix()
    {
        return packagePrefix;
    }

    /**
     * Returns the class name, if necessary qualified with the package name.
     *
     * @return the full class name.
     */
    public String getFullClassName()
    {
        if (packagePrefix == null)
        {
            return className;
        }
        else
        {
            return packagePrefix + className;
        }
    }
}
