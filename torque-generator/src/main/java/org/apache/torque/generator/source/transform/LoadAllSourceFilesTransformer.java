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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.configuration.controller.Output;
import org.apache.torque.generator.control.Controller;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.Source;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.SourcePath;
import org.apache.torque.generator.source.SourceProcessConfiguration;
import org.apache.torque.generator.source.SourceProvider;
import org.apache.torque.generator.source.SourceTransformerDefinition;

/**
 * A SourceTransformer which loads all sources which are defined in the current
 * output into the source graph.
 * The sources are added into new created element. If such elements
 * already exists in the defined, generation is skipped
 * (preventing an infinite loop).
 *
 * @version $Id: LoadAllSourceFilesTransformer.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class LoadAllSourceFilesTransformer implements SourceTransformer
{
    /** The class log. */
    private static Log log
    = LogFactory.getLog(LoadAllSourceFilesTransformer.class);

    /**
     * The element to where the new elements are added.
     * This element must already exist.
     */
    private String addTo;

    /**
     * The name of the new element(s) into which a loaded source is added /
     * the loaded sources are added.
     */
    private String newElement;

    /**
     * Whether all sources are loaded into one new Element.
     * If false, a new element is created for every source.
     */
    private boolean combineSources;

    /**
     * Loads the additional source into the current source graph.
     *
     * @param rootObject the root of the source graph, not null.
     * @param controllerState the controller state, not null.
     *
     * @throws SourceTransformerException if the additional source
     *         cannot be loaded or the element to add to does not exist.
     */
    @Override
    public Object transform(
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
        SourceElement root = (SourceElement) rootObject;
        Output output = controllerState.getOutput();
        log.debug("adding all sources of output " + output.getName()
        + " to current source tree at element " + addTo);
        // the element where the additional source should be anchored.
        SourceElement addToSourceElement;
        List<SourceElement> sourceElementList
        = SourcePath.getElementsFromRoot(root, addTo);
        if (sourceElementList.isEmpty())
        {
            throw new SourceTransformerException(
                    "Source element " + addTo + " does not exist");
        }
        addToSourceElement = sourceElementList.get(0);


        UnitConfiguration unitConfiguration
        = controllerState.getUnitConfiguration();
        ConfigurationHandlers configurationHandlers
        = unitConfiguration.getConfigurationHandlers();
        Controller helperController = new Controller();

        SourceElement newSourceElement = new SourceElement(newElement);
        boolean newSourceElementAdded = false;
        try
        {
            // do not change state of original source provider,
            // instead make a copy.
            SourceProvider sourceProvider
            = controllerState.getSourceProvider().copy();
            sourceProvider.init(configurationHandlers, controllerState);

            while (sourceProvider.hasNext())
            {
                Source source = sourceProvider.next();
                SourceElement rootElement = source.getRootElement();
                SourceProcessConfiguration sourceProcessConfiguration
                = output.getSourceProcessConfiguration();
                List<SourceTransformerDefinition> transformerDefinitions
                = sourceProcessConfiguration.getTransformerDefinitions();
                transformerDefinitions
                    = new ArrayList<>
                (transformerDefinitions);
                Iterator<SourceTransformerDefinition> transformerDefinitionIt
                = transformerDefinitions.iterator();
                while (transformerDefinitionIt.hasNext())
                {
                    SourceTransformerDefinition transformerDefinition
                    = transformerDefinitionIt.next();
                    if (this.equals(
                            transformerDefinition.getSourceTransformer()))
                    {
                        transformerDefinitionIt.remove();
                    }
                }

                rootElement = (SourceElement) helperController.transformSource(
                        rootElement,
                        transformerDefinitions,
                        controllerState);

                newSourceElement.getChildren().add(rootElement);
                if (!newSourceElementAdded)
                {
                    addToSourceElement.getChildren().add(newSourceElement);
                    newSourceElementAdded = true;
                }

                if (!combineSources)
                {
                    newSourceElement = new SourceElement(newElement);
                    newSourceElementAdded = false;
                }
            }
        }
        catch (ConfigurationException e)
        {
            throw new SourceTransformerException(e);
        }
        catch (SourceException e)
        {
            throw new SourceTransformerException(e);
        }

        log.debug("additional sources loaded.");
        return root;
    }

    /**
     * Returns the path to the source element to where the new elements
     * are added. This element must already exist.
     *
     * @return the path to the anchor element.
     */
    public String getAddTo()
    {
        return addTo;
    }

    /**
     * Sets the path to the source element to where the new elements
     * are added. This element must already exist.
     *
     * @param addTo the path to the anchor element.
     */
    public void setAddTo(String addTo)
    {
        this.addTo = addTo;
    }

    /**
     * Returns the name of the new element(s) into which a loaded source
     * is added / the loaded sources are added.
     *
     * @return the name of the new element.
     */
    public String getNewElement()
    {
        return newElement;
    }

    /**
     * Sets the name of the new element(s) into which a loaded source
     * is added / the loaded sources are added.
     *
     * @param newElement the name of the new element.
     */
    public void setNewElement(String newElement)
    {
        this.newElement = newElement;
    }

    /**
     * Returns whether all sources are be loaded into one new Element.
     * If false, a new element is created for every source.
     *
     * @return whether all sources are be loaded into one new Element.
     */
    public boolean isCombineSources()
    {
        return combineSources;
    }

    /**
     * Sets whether all sources are be loaded into one new Element.
     * If false, a new element is created for every source.
     *
     * @param combineSources whether all sources are be loaded
     *        into one new Element.
     */
    public void setCombineSources(boolean combineSources)
    {
        this.combineSources = combineSources;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(addTo)
                .append(combineSources)
                .append(newElement)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }
        if (obj.getClass() != getClass())
        {
            return false;
        }
        LoadAllSourceFilesTransformer other
        = (LoadAllSourceFilesTransformer) obj;
        return new EqualsBuilder()
                .append(other.addTo, this.addTo)
                .append(other.combineSources, this.combineSources)
                .append(other.newElement, this.newElement)
                .isEquals();
    }
}
