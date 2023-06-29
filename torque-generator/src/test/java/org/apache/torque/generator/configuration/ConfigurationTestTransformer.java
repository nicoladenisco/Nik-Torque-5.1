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

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;

/**
 * A source transformer for testing the configuration class. Does nothing.
 *
 * @version $Id: ConfigurationTestTransformer.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class ConfigurationTestTransformer implements SourceTransformer
{
    @Override
    public Object transform(Object toTransformRoot,
            ControllerState controllerState) throws SourceTransformerException
    {
        return toTransformRoot;
    }

    @Override
    public int hashCode()
    {
        // All instances of this class are equal to each other so always
        // the same number is returned
        return 1;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        // All instances of this class are equal to each other
        return true;
    }
}
