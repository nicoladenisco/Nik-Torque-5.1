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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.processor.string.Camelbacker;
import org.apache.torque.generator.source.SourceElement;

/**
 * Takes a property name as input from a source element attribute
 * and generates a getter and a setter name from it.
 *
 * @version $Id: GetterSetterNameTransformer.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class GetterSetterNameTransformer implements SourceTransformer
{
    /** The logger of the class. */
    private static Logger log
    = Logger.getLogger(GetterSetterNameTransformer.class);

    /** The camelbacker to convert the property name. */
    private static Camelbacker camelbacker = new Camelbacker();

    static
    {
        camelbacker.setDefaultLowerCase(false);
    }

    /** The name of the source element attribute. Default is "name". */
    private String attributeName = "name";

    /**
     * The name of the target attribute for the getter name.
     * Default is "getterName".
     */
    private String getterNameAttribute = "getterName";

    /**
     * The name of the target attribute for the setter name.
     * Default is "setterName".
     */
    private String setterNameAttribute = "setterName";

    /**
     * Returns the name of the source element attribute.
     *
     * @return the name of the source attribute, not null.
     */
    public String getAttributeName()
    {
        return attributeName;
    }

    /**
     * Sets the name of the source element attribute.
     *
     * @param attributeName the name of the source attribute, not null.
     *
     * @throws NullPointerException if attributeName is null.
     */
    public void setAttributeName(String attributeName)
    {
        if (attributeName == null)
        {
            throw new NullPointerException("attributeName must not be null");
        }
        this.attributeName = attributeName;
    }

    /**
     * Returns the name of the target element attribute for the getter name.
     *
     * @return the name of the getter name target attribute, not null.
     */
    public String getGetterNameAttribute()
    {
        return getterNameAttribute;
    }

    /**
     * Sets the name of the target element attribute for the getter name.
     *
     * @param getterNameAttribute the name of the getter name target attribute,
     *        not null.
     *
     * @throws NullPointerException if getterNameAttribute is null.
     */
    public void setGetterNameAttribute(String getterNameAttribute)
    {
        if (getterNameAttribute == null)
        {
            throw new NullPointerException(
                    "getterNameAttribute must not be null");
        }
        this.getterNameAttribute = getterNameAttribute;
    }

    /**
     * Returns the name of the target element attribute for the setter name.
     *
     * @return the name of the setter name target attribute, not null.
     */
    public String getSetterNameAttribute()
    {
        return setterNameAttribute;
    }

    /**
     * Sets the name of the target element attribute for the setter name.
     *
     * @param setterNameAttribute the name of the setter name target attribute,
     *        not null.
     *
     * @throws NullPointerException if setterNameAttribute is null.
     */
    public void setSetterNameAttribute(String setterNameAttribute)
    {
        if (setterNameAttribute == null)
        {
            throw new NullPointerException(
                    "setterNameAttribute must not be null");
        }
        this.setterNameAttribute = setterNameAttribute;
    }

    /**
     * Fills the target attributes according to the settings.
     *
     * @param rootObject the root of the source graph, not null.
     * @param controllerState the controller state.
     *
     * @return the modified source element, not null.
     *
     * @throws IllegalStateException if sourceAttributeName or
     *         targetAttributeName was not set.
     * @throws SourceTransformerException if rootObject is not a SourceElement.
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
        Object attributeValue = sourceElement.getAttribute(attributeName);
        if (attributeValue == null)
        {
            log.debug("Attribute " + attributeName
                    + " is not set, no changes made");
            return sourceElement;
        }
        String attributeValueString = attributeValue.toString();
        if (StringUtils.isBlank(attributeValueString))
        {
            log.debug("Attribute " + attributeName
                    + " is blank, no changes made");
            return sourceElement;
        }
        String getterName = "get" + camelbacker.process(attributeValueString);
        sourceElement.setAttribute(getterNameAttribute, getterName);
        String setterName = "set" + camelbacker.process(attributeValueString);
        sourceElement.setAttribute(setterNameAttribute, setterName);
        return sourceElement;
    }
}
