package org.apache.torque.generator.maven;

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
 * Contains all keys in the property file.
 */
public enum PropertyKeys 
{
    /** Key for torque.sample.property */
    TORQUE_SAMPLE_PROPERTY("torque.sample.property"),

    /** Key for torque.some.other.property */
    TORQUE_SOME_OTHER_PROPERTY("torque.some.other.property");

    /** The property key. */
    private String key;

    /**
     * Constructor.
     *
     * @param key the key of the property. 
     */
    private PropertyKeys(String key)
    {
        this.key = key;
    }

    /**
     * Returns the property key.
     *
     * @return the property key.
     */
    public String getKey() 
    {
        return key;
    }

    @Override
    public String toString()
    {
        return key;
    }
}
