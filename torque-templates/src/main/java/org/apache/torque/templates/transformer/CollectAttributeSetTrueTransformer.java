package org.apache.torque.templates.transformer;

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

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceAttributeName;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceElementName;

/**
 * Collects the children which have a certain attribute set to true
 * in another child element.
 *
 * So the source elements are
 * rootElement
 *   ${childrenToProcessName}
 *     ...
 *   ${childrenToProcessName}
 *     ...
 *   ...
 *
 * and the outcome is
 *
 * rootElement
 *   ${childrenToProcessName}
 *     ...
 *   ${childrenToProcessName}
 *     ...
 *   ...
 *   ${targetElementName}
 *     ${childrenToProcessName}
 *       ...
 *     ...
 */
public class CollectAttributeSetTrueTransformer
{
    /**
     * Processes the transformation.
     *
     * @param rootElement the root of the transformation.
     * @param controllerState the controller state, not null.
     * @param childrenToProcessName the name of the child elements
     *        to check.
     * @param attributeToCheckName the attribute in the child which is checked
     *        for the value "true"
     * @param targetElementName the child element which collects the hits.
     */
    public void transform(
            SourceElement rootElement,
            ControllerState controllerState,
            SourceElementName childrenToProcessName,
            SourceAttributeName attributeToCheckName,
            SourceElementName targetElementName)
    {
        List<SourceElement> hits
            = new ArrayList<>();
        for (SourceElement column : rootElement.getChildren(
                childrenToProcessName))
        {
            String attributeToCheck
            = (String) column.getAttribute(attributeToCheckName);
            if (Boolean.parseBoolean(attributeToCheck))
            {
                hits.add(column);
            }
        }

        SourceElement targetElement
            = new SourceElement(targetElementName);
        rootElement.getChildren().add(targetElement);

        for (SourceElement hit : hits)
        {
            targetElement.getChildren().add(hit);
        }
    }
}
