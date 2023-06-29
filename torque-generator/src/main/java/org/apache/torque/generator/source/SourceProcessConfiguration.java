package org.apache.torque.generator.source;

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
import java.util.Collections;
import java.util.List;

import org.apache.torque.generator.configuration.ClassHelper;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.source.skipDecider.SkipDecider;

/**
 * Contains the information how a source should be processed between
 * obtaining the original source and feeding it to the outlets.
 *
 * @version $Id: SourceProcessConfiguration.java 1840416 2018-09-09 15:10:22Z tv $
 *
 */
public class SourceProcessConfiguration
{
    /** The elements which should be used as starting points for generation. */
    private String startElementsPath;

    /** The source transformers which are executed before transformation. */
    private List<SourceTransformerDefinition> transformerDefinitions
        = new ArrayList<>();

    /** The skip decider, or null if none exists. */
    private SkipDecider skipDecider;

    /**
     * Sets the start element path.
     *
     * @param startElementsPath the path to the elements which are used
     *        as starting points for generation,
     *        or null if the root element should be used.
     */
    public void setStartElementsPath(String startElementsPath)
    {
        this.startElementsPath = startElementsPath;
    }

    /**
     * Sets and instantiates the source filter class.
     *
     * @param skipDecider the fully qualified name of a class
     *        which determines whether a particular source is skipped,
     *        or null if every source should be used.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @throws ConfigurationException if the class cannot be
     *         instantiated or does not implement the
     *         <code>SkipDecider</code> interface.
     */
    public void setSkipDecider(
            String skipDecider,
            UnitDescriptor unitDescriptor)
                    throws ConfigurationException
    {
        if (skipDecider != null)
        {
            this.skipDecider = (SkipDecider) ClassHelper.getInstance(
                    skipDecider,
                    SkipDecider.class,
                    unitDescriptor);
        }
        else
        {
            this.skipDecider = null;
        }
    }

    /**
     * Sets the transformer definitions.
     *
     * @param transformerDefinitions the transformer definitions, or null
     *        if the input should not be transformed before generation.
     */
    public void setSourceTransformerDefinitions(
            List<SourceTransformerDefinition> transformerDefinitions)
    {
        if (transformerDefinitions == null)
        {
            this.transformerDefinitions
                = new ArrayList<>();
        }
        else
        {
            this.transformerDefinitions
                = new ArrayList<>(
                    transformerDefinitions);
        }
    }

    /**
     * Returns the path to elements which should be used as starting points for
     * generation.
     *
     * @return the elements to use, or null, in which case the root element
     *         is used.
     */
    public String getStartElementsPath()
    {
        return startElementsPath;
    }

    /**
     * Return all currently registered source transformer definitions.
     *
     * @return the source transformer definitions, not null.
     */
    public List<SourceTransformerDefinition> getTransformerDefinitions()
    {
        return Collections.unmodifiableList(transformerDefinitions);
    }

    /**
     * Returns the current SkipDecider.
     *
     * @return the current SkipDecider, or null if the generation should
     *         always be started for all source files.
     */
    public SkipDecider getSkipDecider()
    {
        return skipDecider;
    }
}
