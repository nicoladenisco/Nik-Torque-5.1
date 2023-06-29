package org.apache.torque.util;

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

import java.sql.SQLException;

import org.apache.torque.TorqueException;

/**
 * Translates Database Exceptions into TorqueExceptions.
 * @version $Id: ExceptionMapper.java 1448414 2013-02-20 21:06:35Z tfischer $
 */
public abstract class ExceptionMapper
{
    /** The Exception mapper instance. */
    private static ExceptionMapper instance = new ExceptionMapperImpl();

    /**
     * Returns the current instance of the Exception mapper to use.
     *
     * @return the current Exception mapper instance.
     */
    public static final ExceptionMapper getInstance()
    {
        return instance;
    }

    /**
     * Sets a new instance of an Exception mapper to use.
     *
     * @param newInstance the new Exception mapper instance, not null.
     */
    public static final void setInstance(ExceptionMapper newInstance)
    {
        instance = newInstance;
    }

    /**
     * Maps a SQLException to an appropriate TorqueException.
     *
     * @param sqlException the sqlException to map, not null.
     *
     * @return the maped TorqueException, containing the original
     *         exception as a cause, not null.
     */
    public abstract TorqueException toTorqueException(SQLException sqlException);
}
