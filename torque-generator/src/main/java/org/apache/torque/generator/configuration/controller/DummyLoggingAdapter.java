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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dummy implementation of the LoggingAdapter interface.
 * Actually does nothing.

 * @version $Id: DummyLoggingAdapter.java 1839288 2018-08-27 09:48:33Z tv $
 *
 */
public class DummyLoggingAdapter implements LoggingAdapter
{
    /** The logger. */
    private static Log log = LogFactory.getLog(DummyLoggingAdapter.class);


    /**
     * Always returns <code>Loglevel.INFO</code>.
     *
     * @return <code>Loglevel.INFO</code>.
     */
    @Override
    public Loglevel getCurrentLoglevel()
    {
        return Loglevel.INFO;
    }

    /**
     * Does nothing except logging the argument.
     *
     * @param loglevel ignored.
     */
    @Override
    public void setLoglevel(Loglevel loglevel)
    {
        log.debug("apply() : Not Setting loglevel to " + loglevel
                + " because this is a dummy implementation");
    }
}
