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
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaAttributeName;

/**
 * Performs the transformations which are necessary to apply the jdbc2schema
 * templates to the source tree.
 * This transformer performs the following actions:
 * <ul>
 *   <li>sets the name attribute to the database element</li>
 * </ul>
 * No elements or attributes are deleted.
 *
 * $Id: Jdbc2SchemaTransformer.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class Jdbc2SchemaTransformer implements SourceTransformer
{
    /** Default database name if none is set. */
    private static final String DEFAULT_DATABASE_NAME = "default";

    /**
     * Transforms the source tree so it can be used by the jdbc2schema
     * templates.
     *
     * @param modelRoot the database root element of the source tree, not null.
     * @param controllerState the controller state, not null.
     *
     * @throws SourceTransformerException if the transformation fails.
     */
    @Override
    public SourceElement transform(
            Object modelRoot,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        SourceElement root = (SourceElement) modelRoot;
        if (root.getAttribute(TorqueSchemaAttributeName.NAME) == null)
        {
            String name = controllerState.getStringOption(
                    Jdbc2SchemaOptionName.DATABASE_NAME);
            if (name == null)
            {
                name = DEFAULT_DATABASE_NAME;
            }
            root.setAttribute(TorqueSchemaAttributeName.NAME, name);
        }
        return root;
    }

}
