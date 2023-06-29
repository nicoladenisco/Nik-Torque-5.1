package org.apache.torque.templates.transformer.jdbc2schema;

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

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.option.OptionName;
import org.apache.torque.generator.source.transform.SourceTransformerException;

/**
 * The option names which are used in the java code of the jdbc2schema
 * templates.
 *
 * $Id: Jdbc2SchemaOptionName.java 1839288 2018-08-27 09:48:33Z tv $
 */
public enum Jdbc2SchemaOptionName implements OptionName
{
    /** The name of the database to generate.*/
    DATABASE_NAME("torque.database.name", false);

    /**
     * The fully qualified name of the option.
     */
    private String name;

    /**
     * Whether this option must be set or not.
     */
    private boolean required;

    /**
     * Constructor for an option which is not required..
     *
     * @param name the fully qualified name of the option, not null.
     */
    private Jdbc2SchemaOptionName(String name)
    {
        this(name, false);
    }

    /**
     * Constructor.
     *
     * @param name the fully qualified name of the option, not null.
     * @param required whether the option is required.
     */
    private Jdbc2SchemaOptionName(String name, boolean required)
    {
        this.name = name;
        this.required = required;
    }

    /**
     * Returns the name of the option.
     *
     * @return the fully qualified name of the option, not null.
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Returns whether this option must be set.
     *
     * @return true if the option must be set, false if it may be set.
     */
    public boolean isRequired()
    {
        return required;
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Checks whether all required options are set.
     *
     * @param controllerState the current controller state, not null.
     *
     * @throws SourceTransformerException if a required option is not set.
     */
    public static void checkRequiredOptions(ControllerState controllerState)
            throws SourceTransformerException
    {
        for (Jdbc2SchemaOptionName templateOption : values())
        {
            if (templateOption.isRequired())
            {
                Object optionValue
                = controllerState.getOption(templateOption.getName());
                if (optionValue == null)
                {
                    throw new SourceTransformerException(
                            "Option " + templateOption.getName()
                            + " must be set");
                }
            }
        }
    }
}
