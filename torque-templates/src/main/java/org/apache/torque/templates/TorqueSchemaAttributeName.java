package org.apache.torque.templates;

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

import org.apache.torque.generator.source.SourceAttributeName;

/**
 * Contains all attribute names from the Torque schema.
 *
 * $Id: TorqueSchemaAttributeName.java 1895112 2021-11-17 15:48:40Z gk $
 */
public enum TorqueSchemaAttributeName implements SourceAttributeName
{
    /** attribute name. */
    NAME("name"),
    /** attribute javaName. */
    JAVA_NAME("javaName"),
    /** attribute javaType. */
    JAVA_TYPE("javaType"),
    /** attribute foreignTable. */
    FOREIGN_TABLE("foreignTable"),
    /** attribute local. */
    LOCAL("local"),
    /** attribute foreign. */
    FOREIGN("foreign"),
    /** attribute onUpdate. */
    ON_UPDATE("onUpdate"),
    /** attribute onDelete. */
    ON_DELETE("onDelete"),
    /** attribute primaryKey. */
    PRIMARY_KEY("primaryKey"),
    /** attribute idMethod. */
    ID_METHOD("idMethod"),
    /** attribute defaultIdMethod. */
    DEFAULT_ID_METHOD("defaultIdMethod"),
    /** attribute defaultJavaType. */
    DEFAULT_JAVA_TYPE("defaultJavaType"),
    /** attribute value. */
    VALUE("value"),
    /** attribute required. */
    REQUIRED("required"),
    /** attribute autoIncrement. */
    AUTO_INCREMENT("autoIncrement"),
    /** attribute inheritance. */
    INHERITANCE("inheritance"),
    /** attribute interface. */
    INTERFACE("interface"),
    /** attribute peerInterface. */
    PEER_INTERFACE("peerInterface"),
    /** attribute protected. */
    PROTECTED("protected"),
    /** attribute default. */
    DEFAULT("default"),
    /** attribute useDatabaseDefaultValue. */
    USE_DATABASE_DEFAULT_VALUE("useDatabaseDefaultValue"),
    /** attribute class. */
    CLASS("class"),
    /** attribute baseClass. */
    BASE_CLASS("baseClass"),
    /** attribute extends. */
    EXTENDS("extends"),
    /** attribute domain. */
    DOMAIN("domain"),
    /** attribute type. */
    TYPE("type"),
    /** attribute size. */
    SIZE("size"),
    /** attribute scale. */
    SCALE("scale"),
    /** attribute filename. */
    FILENAME("filename"),
    /** attribute key */
    KEY("key"),
    /** attribute skipSql */
    SKIP_SQL("skipSql"),
    /** attribute version */
    VERSION ("version"),
    /** attribute version */
    ENUM_TYPE("enumType"),
    /** package relative or absolute, only inheritance type */
    PACKAGE("package");

    /** The name of the attribute, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the attribute, not null.
     */
    private TorqueSchemaAttributeName(final String name)
    {
        this.name = name;
    }

    /**
     * returns the name of the attribute.
     *
     * @return the name of the attribute, not null.
     */
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
