package org.apache.torque.generator.configuration.outlet;

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
 * Tag names for the outlet configuration files.
 */
public final class OutletConfigurationTags
{
    /**
     * private constructor for utility class.
     */
    private OutletConfigurationTags()
    {
    }

    /** Tag name for the "outlets" tag. */
    public static final String OUTLETS_TAG = "outlets";

    /** Tag name for the "outlet" tag. */
    public static final String OUTLET_TAG = "outlet";

    /** Attribute name for the attribute "name" of the "outlet" tag. */
    public static final String OUTLET_NAME_ATTRIBUTE = "name";

    /** Attribute name for the attribute "type" of the "outlet" tag. */
    public static final String OUTLET_TYPE_ATTRIBUTE = "type";

    /** Attribute name for the attribute "path" of the "outlet" tag. */
    public static final String OUTLET_PATH_ATTRIBUTE = "path";

    /** Attribute name for the attribute "encoding" of the "outlet" tag. */
    public static final String OUTLET_ENCODING_ATTRIBUTE = "encoding";

    /** Attribute name for the attribute "class" of the "outlet" tag. */
    public static final String OUTLET_CLASS_ATTRIBUTE = "class";

    /** Attribute name for the attribute "template" of the "outlet" tag. */
    public static final String OUTLET_TEMPLATE_ATTRIBUTE = "template";

    /**
     * Attribute name for the attribute "optionsInContext"
     * of the "outlet" tag.
     */
    public static final String OUTLET_OPTIONS_IN_CONTEXT_ATTRIBUTE
    = "optionsInContext";

    /**
     * Attribute name for the attribute "sourceAttributesInContext"
     * of the "outlet" tag.
     */
    public static final String
    OUTLET_SOURCE_ATTRIBUTES_IN_CONTEXT_ATTRIBUTE
    = "sourceAttributesInContext";

    /**
     * Attribute name for the attribute "variablesInContext"
     * of the "outlet" tag.
     */
    public static final String OUTLET_VARIABLES_IN_CONTEXT_ATTRIBUTE
    = "variablesInContext";

    /**
     * Attribute name for the attribute "optionsInBinding"
     * of the "outlet" tag.
     */
    public static final String OUTLET_OPTIONS_IN_BINDING_ATTRIBUTE
    = "optionsInBinding";

    /**
     * Attribute name for the attribute "sourceAttributesInBinding"
     * of the "outlet" tag.
     */
    public static final String
    OUTLET_SOURCE_ATTRIBUTES_IN_BINDING_ATTRIBUTE
    = "sourceAttributesInBinding";

    /**
     * Attribute name for the attribute "variablesInBinding"
     * of the "outlet" tag.
     */
    public static final String OUTLET_VARIABLES_IN_BINDING_ATTRIBUTE
    = "variablesInBinding";

    /** Tag name for the "input" tag. */
    public static final String INPUT_TAG = "input";

    /** Attribute name for the attribute "elementName" of the "input" tag. */
    public static final String INPUT_ELEMENT_NAME_ATTRIBUTE = "elementName";

    /** Attribute name for the attribute "class" of the "input" tag. */
    public static final String INPUT_CLASS_ATTRIBUTE = "class";
}
