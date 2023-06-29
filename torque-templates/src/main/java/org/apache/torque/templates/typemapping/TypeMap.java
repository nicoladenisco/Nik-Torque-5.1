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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Various mappings for schema types, e.g. to their corresponding
 * Java object types, and Java native types.
 *
 * These are the official SQL type to Java type mappings.
 * These don't quite correspond to the way the peer
 * system works so we'll have to make some adjustments.
 * <pre>
 * -------------------------------------------------------
 * SQL Type      | Java Type            | Peer Type
 * -------------------------------------------------------
 * CHAR          | String               | String
 * VARCHAR       | String               | String
 * LONGVARCHAR   | String               | String
 * NUMERIC       | java.math.BigDecimal | java.math.BigDecimal
 * DECIMAL       | java.math.BigDecimal | java.math.BigDecimal
 * BIT           | boolean OR Boolean   | Boolean
 * TINYINT       | byte OR Byte         | Byte
 * SMALLINT      | short OR Short       | Short
 * INTEGER       | int OR Integer       | Integer
 * BIGINT        | long OR Long         | Long
 * REAL          | float OR Float       | Float
 * FLOAT         | double OR Double     | Double
 * DOUBLE        | double OR Double     | Double
 * BINARY        | byte[]               | ?
 * VARBINARY     | byte[]               | ?
 * LONGVARBINARY | byte[]               | ?
 * DATE          | java.sql.Date        | java.util.Date
 * TIME          | java.sql.Time        | java.util.Date
 * TIMESTAMP     | java.sql.Timestamp   | java.util.Date
 *
 * -------------------------------------------------------
 * A couple variations have been introduced to cover cases
 * that may arise, but are not covered above
 * BOOLEANCHAR   | boolean OR Boolean   | String
 * BOOLEANINT    | boolean OR Boolean   | Integer
 * </pre>
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: TypeMap.java 1850969 2019-01-10 18:09:47Z painter $
 */
public final class TypeMap
{
    /**
     * All text types.
     */
    private static final Set<SchemaType> TEXT_TYPES;

    static
    {
        Set<SchemaType> textTypes = new HashSet<>();
        textTypes.add(SchemaType.CHAR);
        textTypes.add(SchemaType.VARCHAR);
        textTypes.add(SchemaType.LONGVARCHAR);
        textTypes.add(SchemaType.CLOB);
        textTypes.add(SchemaType.DATE);
        textTypes.add(SchemaType.TIME);
        textTypes.add(SchemaType.TIMESTAMP);
        textTypes.add(SchemaType.BOOLEANCHAR);
        TEXT_TYPES = Collections.unmodifiableSet(textTypes);
    }

    /** A sample java object for CHAR columns. */
    public static final String CHAR_OBJECT_TYPE = "\"\"";
    /** A sample java object for VARCHAR columns. */
    public static final String VARCHAR_OBJECT_TYPE = "\"\"";
    /** A sample java object for LONGVARCHAR columns. */
    public static final String LONGVARCHAR_OBJECT_TYPE = "\"\"";
    /** A sample java object for CLOB columns. */
    public static final String CLOB_OBJECT_TYPE = "\"\"";
    /** A sample java object for NUMERIC columns. */
    public static final String NUMERIC_OBJECT_TYPE
    = "new java.math.BigDecimal((double) 0)";
    /** A sample java object for DECIMAL columns. */
    public static final String DECIMAL_OBJECT_TYPE
    = "new java.math.BigDecimal((double) 0)";
    /** A sample java object for BIT columns. */
    public static final String BIT_OBJECT_TYPE = "Boolean.TRUE";
    /** A sample java object for TINYINT columns. */
    public static final String TINYINT_OBJECT_TYPE = "Byte.valueOf((byte) 0)";
    /** A sample java object for SMALLINT columns. */
    public static final String SMALLINT_OBJECT_TYPE = "Short.valueOf((short) 0)";
    /** A sample java object for INTEGER columns. */
    public static final String INTEGER_OBJECT_TYPE = "Integer.valueOf(0)";
    /** A sample java object for BIGINT columns. */
    public static final String BIGINT_OBJECT_TYPE = "Long.valueOf(0)";
    /** A sample java object for REAL columns. */
    public static final String REAL_OBJECT_TYPE = "new Float(0)";
    /** A sample java object for FLOAT columns. */
    public static final String FLOAT_OBJECT_TYPE = "new Double(0)";
    /** A sample java object for DOUBLE columns. */
    public static final String DOUBLE_OBJECT_TYPE = "new Double(0)";
    /** A sample java object for BINARY columns. */
    public static final String BINARY_OBJECT_TYPE = "new Object()"; //?
    /** A sample java object for VARBINARY columns. */
    public static final String VARBINARY_OBJECT_TYPE = "new Object()"; //?
    /** A sample java object for LONGVARBINARY columns. */
    public static final String LONGVARBINARY_OBJECT_TYPE = "new Object()"; //?
    /** A sample java object for BLOB columns. */
    public static final String BLOB_OBJECT_TYPE = "new Object()"; //?
    /** A sample java object for DATE columns. */
    public static final String DATE_OBJECT_TYPE = "new Date()";
    /** A sample java object for TIME columns. */
    public static final String TIME_OBJECT_TYPE = "new Date()";
    /** A sample java object for TIMESTAMP columns. */
    public static final String TIMESTAMP_OBJECT_TYPE = "new Date()";
    /** A sample java object for BOOLEANCHAR columns. */
    public static final String BOOLEANCHAR_OBJECT_TYPE = "\"\"";
    /** A sample java object for BOOLEANINT columns. */
    public static final String BOOLEANINT_OBJECT_TYPE = "Integer.valueOf(0)";

    /**
     * The mapping from schema type to the default initialisation value
     * for that schema type.
     */
    private static final Map<SchemaType, String>
    SCHEMA_TYPE_TO_INIT_VALUE_MAP;

    /**
     * The default Mapping of schema Types to Java Types. Where possible,
     * primitive types are used.
     */
    private static final Map<SchemaType, JavaType>
    SCHEMA_TYPE_TO_JAVA_PRIMITIVE_TYPE_MAP;
    /**
     * The default Mapping of schema Types to Java Types.
     * No primitive types are used.
     */
    private static final Map<SchemaType, JavaType>
    SCHEMA_TYPE_TO_JAVA_OBJECT_TYPE_MAP;


    /**
     * The result set getter methods for a given schema type.
     */
    private static final Map<SchemaType, ResultSetGetter>
    SCHEMA_TYPE_TO_RESULT_SET_GETTER;

    // fill SCHEMA_TYPE_TO_INIT_VALUE_MAP
    static {
        Map<SchemaType, String> schemaToInitValue
            = new HashMap<>();

        schemaToInitValue.put(SchemaType.CHAR, CHAR_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.VARCHAR, VARCHAR_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.LONGVARCHAR, LONGVARCHAR_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.CLOB, CLOB_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.NUMERIC, NUMERIC_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.DECIMAL, DECIMAL_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.BIT, BIT_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.TINYINT, TINYINT_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.SMALLINT, SMALLINT_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.INTEGER, INTEGER_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.BIGINT, BIGINT_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.REAL, REAL_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.FLOAT, FLOAT_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.DOUBLE, DOUBLE_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.BINARY, BINARY_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.VARBINARY, VARBINARY_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.LONGVARBINARY, LONGVARBINARY_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.BLOB, BLOB_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.DATE, DATE_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.TIME, TIME_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.TIMESTAMP, TIMESTAMP_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.BOOLEANCHAR, BOOLEANCHAR_OBJECT_TYPE);
        schemaToInitValue.put(SchemaType.BOOLEANINT, BOOLEANINT_OBJECT_TYPE);
        SCHEMA_TYPE_TO_INIT_VALUE_MAP
        = Collections.unmodifiableMap(schemaToInitValue);
    }

    // Fill SCHEMA_TYPE_TO_JAVA_PRIMITIVE_TYPE_MAP
    static
    {
        Map<SchemaType, JavaType> schemaToJava
            = new HashMap<>();
        schemaToJava.put(SchemaType.CHAR, JavaType.STRING);
        schemaToJava.put(SchemaType.VARCHAR, JavaType.STRING);
        schemaToJava.put(SchemaType.LONGVARCHAR, JavaType.STRING);
        schemaToJava.put(SchemaType.CLOB, JavaType.STRING);
        schemaToJava.put(SchemaType.NUMERIC, JavaType.BIG_DECIMAL);
        schemaToJava.put(SchemaType.DECIMAL, JavaType.BIG_DECIMAL);
        schemaToJava.put(SchemaType.BIT, JavaType.BOOLEAN_PRIMITIVE);
        schemaToJava.put(SchemaType.TINYINT, JavaType.BYTE_PRIMITIVE);
        schemaToJava.put(SchemaType.SMALLINT, JavaType.SHORT_PRIMITIVE);
        schemaToJava.put(SchemaType.INTEGER, JavaType.INTEGER_PRIMITIVE);
        schemaToJava.put(SchemaType.BIGINT, JavaType.LONG_PRIMITIVE);
        schemaToJava.put(SchemaType.REAL, JavaType.FLOAT_PRIMITIVE);
        schemaToJava.put(SchemaType.FLOAT, JavaType.DOUBLE_PRIMITIVE);
        schemaToJava.put(SchemaType.DOUBLE, JavaType.DOUBLE_PRIMITIVE);
        schemaToJava.put(SchemaType.BINARY, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.VARBINARY, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.LONGVARBINARY, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.BLOB, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.DATE, JavaType.DATE);
        schemaToJava.put(SchemaType.TIME, JavaType.DATE);
        schemaToJava.put(SchemaType.TIMESTAMP, JavaType.DATE);
        schemaToJava.put(SchemaType.BOOLEANCHAR, JavaType.BOOLEAN_PRIMITIVE);
        schemaToJava.put(SchemaType.BOOLEANINT, JavaType.BOOLEAN_PRIMITIVE);
        SCHEMA_TYPE_TO_JAVA_PRIMITIVE_TYPE_MAP
        = Collections.unmodifiableMap(schemaToJava);
    }


    // Fill SCHEMA_TYPE_TO_JAVA_OBJECT_TYPE_MAP
    static
    {
        Map<SchemaType, JavaType> schemaToJava
            = new HashMap<>();
        schemaToJava.put(SchemaType.CHAR, JavaType.STRING);
        schemaToJava.put(SchemaType.VARCHAR, JavaType.STRING);
        schemaToJava.put(SchemaType.LONGVARCHAR, JavaType.STRING);
        schemaToJava.put(SchemaType.CLOB, JavaType.STRING);
        schemaToJava.put(SchemaType.NUMERIC, JavaType.BIG_DECIMAL);
        schemaToJava.put(SchemaType.DECIMAL, JavaType.BIG_DECIMAL);
        schemaToJava.put(SchemaType.BIT, JavaType.BOOLEAN_OBJECT);
        schemaToJava.put(SchemaType.TINYINT, JavaType.BYTE_OBJECT);
        schemaToJava.put(SchemaType.SMALLINT, JavaType.SHORT_OBJECT);
        schemaToJava.put(SchemaType.INTEGER, JavaType.INTEGER_OBJECT);
        schemaToJava.put(SchemaType.BIGINT, JavaType.LONG_OBJECT);
        schemaToJava.put(SchemaType.REAL, JavaType.FLOAT_OBJECT);
        schemaToJava.put(SchemaType.FLOAT, JavaType.DOUBLE_OBJECT);
        schemaToJava.put(SchemaType.DOUBLE, JavaType.DOUBLE_OBJECT);
        schemaToJava.put(SchemaType.BINARY, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.VARBINARY, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.LONGVARBINARY, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.BLOB, JavaType.BYTE_PRIMITIVE_ARRAY);
        schemaToJava.put(SchemaType.DATE, JavaType.DATE);
        schemaToJava.put(SchemaType.TIME, JavaType.DATE);
        schemaToJava.put(SchemaType.TIMESTAMP, JavaType.DATE);
        schemaToJava.put(SchemaType.BOOLEANCHAR, JavaType.BOOLEAN_OBJECT);
        schemaToJava.put(SchemaType.BOOLEANINT, JavaType.BOOLEAN_OBJECT);
        SCHEMA_TYPE_TO_JAVA_OBJECT_TYPE_MAP
        = Collections.unmodifiableMap(schemaToJava);
    }

    static
    {
        Map<SchemaType, ResultSetGetter> schemaToResultSetGetter
            = new HashMap<>();

        schemaToResultSetGetter.put(SchemaType.CHAR, ResultSetGetter.STRING);
        schemaToResultSetGetter.put(SchemaType.VARCHAR, ResultSetGetter.STRING);
        schemaToResultSetGetter.put(SchemaType.LONGVARCHAR, ResultSetGetter.STRING);
        schemaToResultSetGetter.put(SchemaType.CLOB, ResultSetGetter.STRING);
        schemaToResultSetGetter.put(SchemaType.NUMERIC, ResultSetGetter.BIG_DECIMAL);
        schemaToResultSetGetter.put(SchemaType.DECIMAL, ResultSetGetter.BIG_DECIMAL);
        schemaToResultSetGetter.put(SchemaType.BIT, ResultSetGetter.BOOLEAN);
        schemaToResultSetGetter.put(SchemaType.TINYINT, ResultSetGetter.BYTE);
        schemaToResultSetGetter.put(SchemaType.SMALLINT, ResultSetGetter.SHORT);
        schemaToResultSetGetter.put(SchemaType.INTEGER, ResultSetGetter.INT);
        schemaToResultSetGetter.put(SchemaType.BIGINT, ResultSetGetter.LONG);
        schemaToResultSetGetter.put(SchemaType.REAL, ResultSetGetter.FLOAT);
        schemaToResultSetGetter.put(SchemaType.FLOAT, ResultSetGetter.DOUBLE);
        schemaToResultSetGetter.put(SchemaType.DOUBLE, ResultSetGetter.DOUBLE);
        schemaToResultSetGetter.put(SchemaType.BINARY, ResultSetGetter.BYTES);
        schemaToResultSetGetter.put(SchemaType.VARBINARY, ResultSetGetter.BYTES);
        schemaToResultSetGetter.put(SchemaType.LONGVARBINARY, ResultSetGetter.BYTES);
        schemaToResultSetGetter.put(SchemaType.BLOB, ResultSetGetter.BYTES);
        schemaToResultSetGetter.put(SchemaType.DATE, ResultSetGetter.DATE);
        schemaToResultSetGetter.put(SchemaType.TIME, ResultSetGetter.TIME);
        schemaToResultSetGetter.put(SchemaType.TIMESTAMP, ResultSetGetter.TIMESTAMP);
        schemaToResultSetGetter.put(SchemaType.BOOLEANCHAR, ResultSetGetter.STRING);
        schemaToResultSetGetter.put(SchemaType.BOOLEANINT, ResultSetGetter.INT);
        SCHEMA_TYPE_TO_RESULT_SET_GETTER
        = Collections.unmodifiableMap(schemaToResultSetGetter);
    }

    /**
     * Private constructor for utility class.
     */
    private TypeMap()
    {
    }

    /**
     * Return a Java object which corresponds to the
     * JDBC type provided. Use in MapBuilder generation.
     *
     * @param jdbcType the JDBC type
     * @return name of the Object
     */
    public static String getJavaObject(final SchemaType jdbcType)
    {
        return SCHEMA_TYPE_TO_INIT_VALUE_MAP.get(jdbcType);
    }

    /**
     * Returns the java type which corresponds to the schema type provided.
     * Where possible, primitive types are used.
     *
     * @param schemaType the schema type.
     * @return name of the native java type
     */
    public static JavaType getJavaPrimitiveType(final SchemaType schemaType)
    {
        return SCHEMA_TYPE_TO_JAVA_PRIMITIVE_TYPE_MAP.get(schemaType);
    }

    /**
     * Returns the java type which corresponds to the schema type provided.
     * Only Object types are used.
     *
     * @param schemaType the schema type.
     * @return the corresponding java Type.
     */
    public static JavaType getJavaObjectType(final SchemaType schemaType)
    {
        return SCHEMA_TYPE_TO_JAVA_OBJECT_TYPE_MAP.get(schemaType);
    }

    /**
     * Returns the result set getter method which corresponds to the
     * Schema type provided.
     *
     * @param schemaType the schema type
     * @return the result set getter method.
     */
    public static ResultSetGetter getResultSetGetter(final SchemaType schemaType)
    {
        return SCHEMA_TYPE_TO_RESULT_SET_GETTER.get(schemaType);
    }

    /**
     * Returns true if the type is boolean in the java
     * object and a numeric (1 or 0) in the db.
     *
     * @param type The type to check.
     * @return true if the type is BOOLEANINT
     */
    public static boolean isBooleanInt(final SchemaType type)
    {
        return SchemaType.BOOLEANINT.equals(type);
    }

    /**
     * Returns true if the type is boolean in the
     * java object and a String "Y" or "N" in the db.
     *
     * @param type The type to check.
     * @return true if the type is BOOLEANCHAR
     */
    public static boolean isBooleanChar(final SchemaType type)
    {
        return SchemaType.BOOLEANCHAR.equals(type);
    }

    /**
     * Returns true if the type is boolean in the
     * java object and a Bit "1" or "0" in the db.
     *
     * @param type The type to check.
     * @return true if the type is BIT
     */
    public static boolean isBit(final SchemaType type)
    {
        return SchemaType.BIT.equals(type);
    }

    /**
     * Returns true if values for the type need to be quoted.
     *
     * @param type The type to check.
     * @return true if values for the type need to be quoted.
     */
    public static boolean isTextType(final SchemaType type)
    {
        return TEXT_TYPES.contains(type);
    }
}
