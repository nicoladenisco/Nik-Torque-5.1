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

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Supports the Derby / DB2 / SQL92 standard for defining Binary data fields
 * with either CHAR(#) FOR BIT DATA or VARCHAR(#) FOR BIT DATA. This can be
 * used in Platform implementors initialize() methods, by using lines like:
 * </p>
 * <code>
 *   setSchemaTypeToSqlTypeMapping(
 *              SchemaType.VARBINARY,
 *              new SizedForBitDataSqlType("VARCHAR", "32672"));
 * </code>
 * <p>
 * This will cause the Column.getSqlString() method to produce items similar to:
 * </p>
 * <code>
 * CHAR(#) FOR BIT DATA
 * VARCHAR(#) FOR BIT DATA
 * </code>
 * <p>
 * Where: # is the size= schema attribute or a default size specified in the
 * constructor.
 * </p>
 * <p>
 * Note that this is dependent on the platform implementation correctly defining
 * BINARY and VARBINARY as having a size attribute in the "hasSize()" method.
 * </p>
 *
 * @see org.apache.torque.templates.platform.Platform
 * @author <a href="mailto:Monroe@DukeCE.com">Greg Monroe</a>
 */
public class SizedForBitDataSqlType extends SqlType
{
    /**
     * @param sqlType the sql type
     */
    public SizedForBitDataSqlType(final String sqlType)
    {
        super(sqlType);
    }

    /**
     * @param sqlType the sql type
     * @param size size
     */
    public SizedForBitDataSqlType(final String sqlType, final String size)
    {
        super(sqlType, size);
    }

    /**
     * @param sqlType the sql type
     * @param size of the data
     * @param scale of the data
     * @param defaultValue for the data
     */
    public SizedForBitDataSqlType(
            final SqlType sqlType,
            final String size,
            final String scale,
            final String defaultValue)
    {
        super(sqlType, size, scale, defaultValue);
    }

    /**
     * @see org.apache.torque.templates.typemapping.SqlType#printSize(java.lang.String)

     * Returns the size postfix for the base SQL Column type.
     *
     * @return "(size) FOR BIT DATA" or just " FOR BIT DATA" if size
     * is null.
     */
    @Override
    public String printSize(final String sizeSuffix)
    {
        String result = "";
        if (!StringUtils.isBlank(getSize()))
        {
            result =  "(" + getSize() + sizeSuffix + ")";
        }
        result = result + " FOR BIT DATA";
        return result;
    }

    /* (non-Javadoc)
     * @see org.apache.torque.templates.typemapping.SqlType#getNew(java.lang.String, java.lang.String, java.lang.String)
     * 
     * Returns a new instance with the given sqlTypeName, size, scale
     * and default value.
     *
     * @return a new instance with the given parameters.
     */
    @Override
    public SqlType getNew(
            String size,
            final String scale,
            final String defaultValue)
    {
        if (size == null)
        {
            size = "1";
        }
        return new SizedForBitDataSqlType(
                this,
                size,
                scale,
                defaultValue);
    }

}
