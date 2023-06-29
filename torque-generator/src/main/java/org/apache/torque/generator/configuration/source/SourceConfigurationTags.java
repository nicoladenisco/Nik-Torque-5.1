package org.apache.torque.generator.configuration.source;

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
 * Contains tag and attribute names for the Source configuration.
 */
public final class SourceConfigurationTags
{
    /**
     * private constructor for utility class.
     */
    private SourceConfigurationTags()
    {
    }

    /** Tag name for the "source" tag. */
    public static final String SOURCE_TAG = "source";

    /** Attribute name for the "elements" attribute. */
    public static final String ELEMENTS_ATTRIBUTE = "elements";

    /** Attribute name for the "skipDecider" attribute. */
    public static final String SKIP_DECIDER_ARRTIBUTE = "skipDecider";

    /** Attribute name for the "format" attribute. */
    public static final String FORMAT_ATTRIBUTE = "format";

    /** Attribute name for the "combineFiles" attribute. */
    public static final String COMBINE_FILES_ATTRIBUTE = "combineFiles";

    /** Tag name for the "transformer" tag. */
    public static final String TRANSFORMER_TAG = "transformer";

    /**
     * Tag name for the "class" attribute of the "transformer"
     * and "postprocessor tags.
     */
    public static final String CLASS_ATTRIBUTE = "class";

    /** Tag name for the "postprocessor" tag. */
    public static final String POSTPROCESSOR_TAG = "postprocessor";

    /** Tag name for the "include" tag. */
    public static final String INCLUDE_TAG = "include";

    /** Tag name for the "exclude" tag. */
    public static final String EXCLUDE_TAG = "exclude";

    /** Tag name for the "entityReference" tag. */
    public static final String ENTITY_REFERENCE = "entityReference";

    /** Attribute name for the "urlOption" attribute. */
    public static final String URL_OPTION_ATTRIBUTE = "urlOption";

    /** Attribute name for the "driverOption" attribute. */
    public static final String DRIVER_OPTION_ATTRIBUTE = "driverOption";

    /** Attribute name for the "usernameOption" attribute. */
    public static final String USERNAME_OPTION_ATTRIBUTE = "usernameOption";

    /** Attribute name for the "passwordOption" attribute. */
    public static final String PASSWORD_OPTION_ATTRIBUTE = "passwordOption";

    /** Attribute name for the "schemaOption" attribute. */
    public static final String SCHEMA_OPTION_ATTRIBUTE = "schemaOption";

    /** Attribute name for the "systemId" attribute. */
    public static final String SYSTEM_ID_ATTRIBUTE = "systemId";

    /** Attribute name for the "resource" attribute. */
    public static final String RESOURCE_ATTRIBUTE = "resource";

}
