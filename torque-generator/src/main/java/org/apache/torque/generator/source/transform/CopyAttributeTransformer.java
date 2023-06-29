package org.apache.torque.generator.source.transform;

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

import org.apache.log4j.Logger;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;

/**
 * Fills an attribute with the content of another attribute.
 * It can be configured whether the target attribute is overwritten
 * if it is not set.
 *
 * $Id: CopyAttributeTransformer.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class CopyAttributeTransformer extends SourceTargetAttributeTransformer
{
    /** The logger of the class. */
    private static Logger log
    = Logger.getLogger(CopyAttributeTransformer.class);


    /**
     * Fills the target attribute according to the settings.
     *
     * @param rootObject the root of the source graph, not null.
     * @param controllerState the controller state.
     *
     * @return the modified source element, not null.
     *
     * @throws SourceTransformerException if rootObject is not a SourceElement.
     * @throws IllegalStateException if sourceAttributeName or
     *         targetAttributeName was not set.
     */
    @Override
    public SourceElement transform(
            Object rootObject,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (!(rootObject instanceof SourceElement))
        {
            throw new SourceTransformerException(
                    "rootObject is not a SourceElement but has the class "
                            + rootObject.getClass().getName());
        }
        SourceElement sourceElement = (SourceElement) rootObject;
        String sourceAttributeName = getSourceAttributeName();
        if (sourceAttributeName == null)
        {
            throw new IllegalStateException("sourceAttributeName is not set");
        }

        String targetAttributeName = getTargetAttributeName();
        if (targetAttributeName == null)
        {
            throw new IllegalStateException("targetAttributeName is not set");
        }

        Object attributeValue = sourceElement.getAttribute(sourceAttributeName);
        if (attributeValue == null)
        {
            log.debug("Attribute " + sourceAttributeName
                    + " is not set, no changes made");
            return sourceElement;
        }
        if (isOverwrite()
                || sourceElement.getAttribute(targetAttributeName) == null)
        {
            sourceElement.setAttribute(targetAttributeName, attributeValue);
        }
        return sourceElement;
    }
}
