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

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.source.Source;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.SourcePath;
import org.apache.torque.generator.source.stream.FileSourceProvider;
import org.apache.torque.generator.source.stream.StreamSourceFormat;

/**
 * A SourceTransformer which loads other sources into the current source graph.
 *
 * @version $Id: LoadAdditionalSourceTransformer.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class LoadAdditionalSourceTransformer implements SourceTransformer
{
    /** The class log. */
    private static Log log
    = LogFactory.getLog(LoadAdditionalSourceTransformer.class);

    /**
     * The element where the newly loaded source should be added.
     */
    private String element;

    /**
     * The include path for the files to load,
     * relative to the current source file.
     */
    private List<String> fileIncludes;

    /**
     * The exclude path for the files to load,
     * relative to the current source file.
     */
    private List<String> fileExcludes;

    /**
     * The format of the source.
     */
    private String sourceFormat;

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
        SourceElement root = (SourceElement) rootObject;

        // the element where the additional source should be anchored.
        SourceElement sourceElement;
        List<SourceElement> sourceElementList
        = SourcePath.getElementsFromRoot(root, element);
        if (sourceElementList.isEmpty())
        {
            throw new SourceTransformerException(
                    "Source element " + element + " does not exist");
        }
        sourceElement = sourceElementList.get(0);

        ConfigurationHandlers configurationHandlers
        = controllerState.getUnitConfiguration()
        .getConfigurationHandlers();
        Set<StreamSourceFormat> streamSourceFormats
        = configurationHandlers.getStreamSourceFormats();
        StreamSourceFormat streamSourceFormat = null;
        for (StreamSourceFormat candidate : streamSourceFormats)
        {
            if (sourceFormat.equals(candidate.getKey()))
            {
                streamSourceFormat = candidate;
                break;
            }
        }
        if (streamSourceFormat == null)
        {
            throw new SourceTransformerException(
                    "Unknown source format" + sourceFormat);
        }

        FileSourceProvider fileSourceProvider;
        try
        {
            fileSourceProvider = new FileSourceProvider(
                    streamSourceFormat,
                    new Fileset(
                            controllerState.getSourceFile().getParentFile(),
                            fileIncludes,
                            fileExcludes),
                    false);
            fileSourceProvider.init(configurationHandlers, controllerState);
        }
        catch (ConfigurationException e)
        {
            throw new SourceTransformerException(
                    "Could initialize file source provider", e);
        }
        while (fileSourceProvider.hasNext())
        {
            Source additionalSource = fileSourceProvider.next();
            SourceElement additionalSourceRoot;
            try
            {
                additionalSourceRoot = additionalSource.getRootElement();
            }
            catch (SourceException e)
            {
                log.error("Could not construct source from schema file "
                        + additionalSource.getDescription(),
                        e);
                throw new SourceTransformerException(
                        "Could not construct source from schema file "
                                + additionalSource.getDescription(),
                                e);
            }
            sourceElement.getChildren().add(additionalSourceRoot);
        }

        return root;
    }

    /**
     * Returns the path to the source element to which the additional sources
     * should be added.
     *
     * @return the path to the anchor element.
     */
    public String getElement()
    {
        return element;
    }

    /**
     * Sets the path to the source element to which the additional sources
     * should be added.
     *
     * @param element the path to the anchor element.
     */
    public void setElement(String element)
    {
        this.element = element;
    }

    /**
     * Returns the file patterns to include to the sources to read.
     *
     * @return the file patterns to include.
     */
    public List<String> getFileIncludes()
    {
        return fileIncludes;
    }

    /**
     * Sets the file patterns to include to the sources to read.
     *
     * @param fileIncludes the file patterns to include.
     */
    public void setFileIncludes(List<String> fileIncludes)
    {
        this.fileIncludes = fileIncludes;
    }

    /**
     * Returns the file patterns to exclude from the sources to read.
     *
     * @return the file patterns to exclude.
     */
    public List<String> getFileExcludes()
    {
        return fileExcludes;
    }

    /**
     * Sets the file patterns to exclude from the sources to read.
     *
     * @param fileExcludes the file patterns to exclude.
     */
    public void setFileExcludes(List<String> fileExcludes)
    {
        this.fileExcludes = fileExcludes;
    }

    /**
     * Returns the key of the format of the source file(s).
     *
     * @return the key for the source format.
     */
    public String getSourceFormat()
    {
        return sourceFormat;
    }

    /**
     * Sets the key of the source format of the source file(s).
     *
     * @param sourceFormat the key for the source format.
     */
    public void setSourceFormat(String sourceFormat)
    {
        this.sourceFormat = sourceFormat;
    }
}
