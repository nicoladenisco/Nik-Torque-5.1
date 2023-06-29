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

/**
 * A transformer which has a source attribute and a target attribute.
 *
 * @version $Id: SourceTargetAttributeTransformer.java 1839288 2018-08-27 09:48:33Z tv $
 */
public abstract class SourceTargetAttributeTransformer
implements SourceTransformer
{
    /** The logger of the class. */
    private static final Logger log
    = Logger.getLogger(SourceTargetAttributeTransformer.class);

    /** The name of the attribute which is read as Input. */
    private String sourceAttributeName;

    /** The name of the attribute into which the result is stored. */
    private String targetAttributeName;

    /**
     * Whether the content of the target attribute should be overwritten
     * if it exists. Default is true.
     */
    private boolean overwrite = true;

    /**
     * Standard constructor.
     */
    public SourceTargetAttributeTransformer()
    {
    }

    /**
     * Constructor used to set default attribute names.
     *
     * @param sourceAttributeName the default sourceAttributeName.
     * @param targetAttributeName the default targetAttributeName.
     */
    public SourceTargetAttributeTransformer(
            String sourceAttributeName,
            String targetAttributeName)
    {
        this.sourceAttributeName = sourceAttributeName;
        this.targetAttributeName = targetAttributeName;
    }

    /**
     * Returns the name of the attribute which is read as Input.
     *
     * @return the name of the attribute which is read as Input.
     */
    public String getSourceAttributeName()
    {
        return sourceAttributeName;
    }

    /**
     * Sets the name of the attribute which is read as Input.
     *
     * @param sourceAttributeName the name of the attribute which is read
     *        as Input, not null.
     *
     * @throws NullPointerException if sourceAttributeName is null.
     */
    public void setSourceAttributeName(String sourceAttributeName)
    {
        if (sourceAttributeName == null)
        {
            throw new NullPointerException(
                    "sourceAttributeName must not be null");
        }
        this.sourceAttributeName = sourceAttributeName;
        log.debug("sourceAttributeName set to " + sourceAttributeName);
    }

    /**
     * Returns the name of the attribute into which the result is stored.
     *
     * @return the name of the attribute into which the result is stored.
     */
    public String getTargetAttributeName()
    {
        return targetAttributeName;
    }

    /**
     * Sets the name of the attribute into which the result is stored.
     *
     * @param targetAttributeName the name of the attribute into which
     *        the result is stored, not null.
     *
     * @throws NullPointerException if targetAttributeName is null.
     */
    public void setTargetAttributeName(String targetAttributeName)
    {
        if (targetAttributeName == null)
        {
            throw new NullPointerException(
                    "targetAttributeName must not be null");
        }
        this.targetAttributeName = targetAttributeName;
        log.debug("targetAttributeName set to " + targetAttributeName);
    }

    /**
     * Returns whether the content of the target attribute is overwritten
     * if it exists.
     *
     * @return true if the target is overwritten, false if it is only filled
     *         when not set.
     */
    public boolean isOverwrite()
    {
        return overwrite;
    }

    /**
     * Sets whether the content of the target attribute should be overwritten
     * if it exists.
     *
     * @param overwrite true if the target is overwritten, false if it is
     *        only filled when not set.
     */
    public void setOverwrite(boolean overwrite)
    {
        this.overwrite = overwrite;
        log.debug("overwrite set to " + overwrite);
    }

    /**
     * Performs the transformation.
     *
     * @param toTransformRoot the root object of the source tree
     *        to transform, not null.
     * @param controllerState the state of the controller, not null.
     *
     * @return the root element of the transformed source tree, not null.
     *
     * @throws SourceTransformerException if the source cannot be transformed.
     *
     * @see SourceTransformer#transform(Object, ControllerState)
     */
    @Override
    public abstract Object transform(
            Object toTransformRoot,
            ControllerState controllerState)
                    throws SourceTransformerException;
}
