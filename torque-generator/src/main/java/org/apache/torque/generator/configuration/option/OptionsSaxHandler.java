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

import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX handler for reading the options tag in the control configuration.
 *
 * $Id: OptionsSaxHandler.java 1331190 2012-04-27 02:41:35Z tfischer $
 */
public class OptionsSaxHandler extends DefaultHandler
{
    /** The OptionsConfiguration to be filled by this SAX handler. */
    private OptionsConfiguration optionsConfiguration;

    /**
     * Whether the SAX handler has finished reading the options configuration.
     */
    private boolean finished = false;

    /**
     * Constructor.
     *
     * @param optionsConfiguration the OptionsConfiguration to be filled,
     *        not null.
     */
    public OptionsSaxHandler(OptionsConfiguration optionsConfiguration)
    {
        if (optionsConfiguration == null)
        {
            throw new NullPointerException("optionsConfiguration is null");
        }
        this.optionsConfiguration = optionsConfiguration;
    }

    /**
     * Returns the options configuration read by this SAX handler.
     *
     * @return the options configuration, not null.
     *
     * @throws IllegalStateException if the SAX handler has not yet finished
     *         reading the configuration.
     */
    public OptionsConfiguration getOptionsConfiguration()
    {
        if (!finished)
        {
            throw new IllegalStateException(
                    "Not finished reading the options configuration");
        }
        return optionsConfiguration;
    }

    /**
     * Returns whether the SAX handler has finished reading the options
     * configuration.
     *
     * @return true if whether the SAX handler has finished, false otherwise.
     */
    public boolean isFinished()
    {
        return finished;
    }

    /**
     * Marks that the SAX handler has finished reading the options
     * configuration.
     */
    void finished()
    {
        this.finished = true;
    }
}
