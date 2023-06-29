package org.apache.torque.generator.configuration;

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
 * Contains commonly used XML Namespaces and other constants.
 */
public final class XMLConstants
{
    /**
     * private constructor for utility class.
     */
    private XMLConstants()
    {
    }

    /**
     * The XML Schema Namespace.
     */
    public static final String XS_NAMESPACE
    = "http://www.w3.org/2001/XMLSchema";

    /**
     * The XML Schema Instance Namespace.
     */
    public static final String XSI_NAMESPACE
    = "http://www.w3.org/2001/XMLSchema-instance";

    /**
     * The Torque generator configuration Namespace.
     */
    public static final String GENERATOR_CONFIGURATION_NAMESPACE
    = "http://db.apache.org/torque/4.0/generator/configuration";

    /**
     * The name of the XSI type attribute.
     */
    public static final String XSI_TYPE_ATTRBUTE_NAME = "type";
}
