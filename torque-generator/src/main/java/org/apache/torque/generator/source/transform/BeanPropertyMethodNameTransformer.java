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
 * Converts a source element Attribute such that it can be used as method name.
 * The base of the method name is the attribute content, with special characters
 * removed and case corrected where necessary. Optionally,
 * a prefix and/or suffix can be added. The result is stored into another
 * attribute of the same source element.
 *
 * @version $Id: BeanPropertyMethodNameTransformer.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class BeanPropertyMethodNameTransformer
extends SourceTargetAttributeTransformer
{
    /** The logger of the class. */
    private static Logger log
    = Logger.getLogger(BeanPropertyMethodNameTransformer.class);

    /** The camelbacker used to convert the input string. */
    private final Camelbacker camelbacker = new Camelbacker();

    /** The prefix which is added in front of the input string. */
    private String prefix = "";

    /** The suffix which is added after the input string. */
    private String suffix = "";

    /**
     * Constructor.
     */
    public BeanPropertyMethodNameTransformer()
    {
        super("name", null);
    }

    /**
     * Returns the prefix which is added in front of the input string.
     *
     * @return the prefix for the result, not null.
     */
    public String getTargetNamePrefix()
    {
        return prefix;
    }

    /**
     * Sets the prefix which is added in front of the input string.
     *
     * @param prefix the prefix for the result, not null.
     *
     * @throws NullPointerException if prefix is null.
     */
    public void setPrefix(String prefix)
    {
        if (prefix == null)
        {
            throw new NullPointerException("prefix must not be null");
        }
        this.prefix = prefix;
        log.debug("prefix set to " + prefix);
    }

    /**
     * Returns the suffix which is added after the input string.
     *
     * @return the suffix for the result, not null.
     */
    public String getSuffix()
    {
        return suffix;
    }

    /**
     * Sets the suffix which is added after of the input string.
     *
     * @param suffix the suffix for the result, not null.
     *
     * @throws NullPointerException if suffix is null.
     */
    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
        log.debug("suffix set to " + suffix);
    }

    /**
     * Fills the target attribute according to the settings.
     *
     * @param rootObject the root of the source graph, not null.
     * @param controllerState the controller state.
     *
     * @return the modified source element, not null.
     *
     * @throws SourceTransformerException if rootObject is not a SourceElement.
     * @throws IllegalStateException if targetAttributeName was not set.
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
        String targetAttributeName = getTargetAttributeName();
        if (targetAttributeName == null)
        {
            throw new IllegalStateException("targetAttributeName is not set");
        }

        String sourceAttributeName = getSourceAttributeName();
        Object attributeValue = sourceElement.getAttribute(sourceAttributeName);
        if (attributeValue == null)
        {
            log.debug("Attribute " + sourceAttributeName
                    + " is not set, no changes made");
            return sourceElement;
        }
        String attributeValueString = attributeValue.toString();
        if (StringUtils.isBlank(attributeValueString))
        {
            log.debug("Attribute " + sourceAttributeName
                    + " is blank, no changes made");
            return sourceElement;
        }

        if (!isOverwrite()
                && sourceElement.getAttribute(targetAttributeName) != null)
        {
            log.trace("Attribute " + targetAttributeName
                    + " is filled and overwrite is false, no changes made");
            return sourceElement;
        }

        camelbacker.setDefaultLowerCase(false);
        if (StringUtils.isEmpty(prefix))
        {
            camelbacker.setFirstCharUppercase(false);
        }
        String result = prefix
                + camelbacker.process(attributeValueString)
                + suffix;
        sourceElement.setAttribute(targetAttributeName, result);
        return sourceElement;
    }
}
