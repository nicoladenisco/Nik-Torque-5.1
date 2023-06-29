package org.apache.torque.ant.task;

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
 * An option for the generator task.
 *
 * @version $Id: Option.java 1402619 2012-10-26 19:23:11Z tfischer $
 */
public class Option
{
    /** The key of the option. */
    private String key;

    /** The value of the option. */
    private String value;

    /**
     * Returns the key of the option.
     *
     * @return the key of the option.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the key of the option.
     *
     * @param key the key of the option.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Returns the value of the option.
     *
     * @return the value of the option.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value of the option.
     *
     * @param value the value of the option.
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}
