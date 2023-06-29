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
 * Allows reading and setting the current loglevel.
 * The implementation depends on the logging framework used.
 *
 * @version $Id: LoggingAdapter.java 1465296 2013-04-06 20:13:45Z tfischer $
 */
public interface LoggingAdapter
{
    /**
     * Returns the current loglevel.
     *
     * @return the current loglevel, or INFO if the current loglevel cannot
     *         be determined.
     */
    Loglevel getCurrentLoglevel();

    /**
     * Sets the loglevel to the given loglevel.
     *
     *  @param loglevel the loglevel to set, not null.
     */
    void setLoglevel(Loglevel loglevel);
}
