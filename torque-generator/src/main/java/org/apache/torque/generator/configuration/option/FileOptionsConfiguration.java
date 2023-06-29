package org.apache.torque.generator.configuration.option;

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
 * An options configuration which reads options from a file.
 *
 * $Id: FileOptionsConfiguration.java 1839288 2018-08-27 09:48:33Z tv $
 */
public abstract class FileOptionsConfiguration extends OptionsConfigurationBase
{
    /**
     * The path to the options file.
     */
    private String path;

    /**
     * Sets the path to the options file.
     *
     * @param path the path, not null.
     *
     * @throws NullPointerException if path is null.
     */
    void setPath(String path)
    {
        if (path == null)
        {
            throw new NullPointerException("path must not be null");
        }
        this.path = path;
    }

    /**
     * Returns the path to the options file.
     *
     * @return the path, not null if type is xml or properties.
     */
    public String getPath()
    {
        return path;
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(path=")
        .append(path);
        result.append(")");
        return result.toString();
    }
}
