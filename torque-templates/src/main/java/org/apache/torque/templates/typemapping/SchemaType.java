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

import java.sql.Types;

/**
 * Enum for known SQL types.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: SchemaType.java 1850969 2019-01-10 18:09:47Z painter $
 */
public enum SchemaType
{
    /** JDBC Datatype BIT. */
    BIT(Types.BIT),
    /** JDBC Datatype TINYINT. */
    TINYINT(Types.TINYINT),
    /** JDBC Datatype SMALLINT. */
    SMALLINT(Types.SMALLINT),
    /** JDBC Datatype INTEGER. */
    INTEGER(Types.INTEGER),
    /** JDBC Datatype BIGINT. */
    BIGINT(Types.BIGINT),
    /** JDBC Datatype FLOAT. */
    FLOAT(Types.FLOAT),
    /** JDBC Datatype REAL. */
    REAL(Types.REAL),
    /** JDBC Datatype NUMERIC. */
    NUMERIC(Types.NUMERIC),
    /** JDBC Datatype DECIMAL. */
    DECIMAL(Types.DECIMAL),
    /** JDBC Datatype CHAR. */
    CHAR(Types.CHAR),
    /** JDBC Datatype VARCHAR. */
    VARCHAR(Types.VARCHAR),
    /** JDBC Datatype LONGVARCHAR. */
    LONGVARCHAR(Types.LONGVARCHAR),
    /** JDBC Datatype DATE. */
    DATE(Types.DATE),
    /** JDBC Datatype TIME. */
    TIME(Types.TIME),
    /** JDBC Datatype TIMESTAMP. */
    TIMESTAMP(Types.TIMESTAMP),
    /** JDBC Datatype BINARY. */
    BINARY(Types.BINARY),
    /** JDBC Datatype VARBINARY. */
    VARBINARY(Types.VARBINARY),
    /** JDBC Datatype LONGVARBINARY. */
    LONGVARBINARY(Types.LONGVARBINARY),
    /** JDBC Datatype NULL. */
    NULL(Types.NULL),
    /** JDBC Datatype OTHER. */
    OTHER(Types.OTHER),
    /** JDBC Datatype JAVA_OBJECT. */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    /** JDBC Datatype DISTINCT. */
    DISTINCT(Types.DISTINCT),
    /** JDBC Datatype STRUCT. */
    STRUCT(Types.STRUCT),
    /** JDBC Datatype ARRAY. */
    ARRAY(Types.ARRAY),
    /** JDBC Datatype BLOB. */
    BLOB(Types.BLOB),
    /** JDBC Datatype CLOB. */
    CLOB(Types.CLOB),
    /** JDBC Datatype REF. */
    REF(Types.REF),
    /** JDBC Datatype INTEGER interpreted as Boolean. */
    BOOLEANINT(Types.INTEGER),
    /** JDBC Datatype CHAR interpreted as Boolean. */
    BOOLEANCHAR(Types.CHAR),
    /** JDBC Datatype DOUBLE. */
    DOUBLE(Types.DOUBLE);

    /**
     * The corresponding jdbc type,
     * may be null if no corresponding type exists.
     */
    private Integer jdbcType;

    private SchemaType(Integer jdbcType)
    {
        this.jdbcType = jdbcType;
    }

    /**
     * Returns the corresponding jdbc type.
     *
     * @return the corresponding jdbc type, or null if no corresponding
     *         type exists.
     */
    public Integer getJdbcType()
    {
        return jdbcType;
    }
}
