package org.apache.torque.generator.configuration.controller;

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
 * The tag and attribute names used for configuring an Output unit.
 */
public final class OutputConfigurationTags
{
    /**
     * private constructor for utility class.
     */
    private OutputConfigurationTags()
    {
    }

    /** Tag name for the "output" tag. */
    public static final String OUTPUT_TAG = "output";

    /** Attribute name for the "name" attribute. */
    public static final String NAME_ATTRIBUTE = "name";

    /** Attribute name for the "existingTargetStrategy" attribute. */
    public static final String EXISTING_TARGET_STRATEGY_ATTRIBUTE
    = "existingTargetStrategy";

    /** Attribute name for the "existingTargetStrategy" attribute. */
    public static final String OUTPUT_DIR_KEY_ATTRIBUTE
    = "outputDirKey";

    /** Attribute name for the "file" attribute. */
    public static final String FILE_ATTRIBUTE = "file";

    /** Attribute name for the "encoding" attribute. */
    public static final String ENCODING_ATTRIBUTE = "encoding";

    /** Attribute name for the "type" attribute. */
    public static final String TYPE_ATTRIBUTE = "type";

    /** Tag name for the "outlet" tag. */
    public static final String OUTLET_TAG = "outlet";

    /** Tag name for the "filenameOutlet" tag. */
    public static final String FILENAME_GENERATOR_TAG = "filenameOutlet";
}
