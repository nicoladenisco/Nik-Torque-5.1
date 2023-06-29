package org.apache.torque.generator.configuration.source;

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

import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.COMBINE_FILES_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.EXCLUDE_TAG;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.FORMAT_ATTRIBUTE;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.INCLUDE_TAG;
import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.SOURCE_TAG;

import java.util.HashSet;
import java.util.Set;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.SaxHelper;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.source.SourceProvider;
import org.apache.torque.generator.source.stream.FileSourceProvider;
import org.apache.torque.generator.source.stream.StreamSourceFormat;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reads file source definitions from the controller configuration file.
 */
public class FileSourceSaxHandler extends SourceSaxHandler
{
    /** The file format of the source element which is currently parsed. */
    private String format;

    /** The source file names which should be included in generation. */
    private Set<String> includes = new HashSet<>();

    /** The source file names which should be excluded from generation. */
    private Set<String> excludes = new HashSet<>();

    /** Whether to combine all source files. */
    private Boolean combineFiles;

    /**
     * The string builder for an include definition.
     * Not null if an include tag is currently parsed.
     */
    private StringBuilder currentInclude = null;

    /**
     * The string builder for an exclude definition.
     * Not null if an exclude tag is currently parsed.
     */
    private StringBuilder currentExclude = null;

    /**
     * The source provider which is configured by this SAX handler.
     */
    private SourceProvider sourceProvider;

    /**
     * Constructor.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers All known configuration handlers, not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public FileSourceSaxHandler(
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
    {
        super(configurationProvider, unitDescriptor, configurationHandlers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String rawName,
            Attributes attributes)
                    throws SAXException
    {
        switch (rawName)
        {
            case INCLUDE_TAG:
                currentInclude = new StringBuilder();
                break;
            case EXCLUDE_TAG:
                currentExclude = new StringBuilder();
                break;
            case SOURCE_TAG:
                super.startElement(uri, localName, rawName, attributes);
                format = attributes.getValue(FORMAT_ATTRIBUTE);
                if (attributes.getValue(COMBINE_FILES_ATTRIBUTE) != null)
                {
                    combineFiles = SaxHelper.getBooleanAttribute(
                            COMBINE_FILES_ATTRIBUTE,
                            attributes,
                            "the element " + SOURCE_TAG);
                }
                break;
            default:
                super.startElement(uri, localName, rawName, attributes);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String rawName)
            throws SAXException
    {
        switch (rawName)
        {
            case INCLUDE_TAG:
                includes.add(currentInclude.toString());
                currentInclude = null;
                break;
            case EXCLUDE_TAG:
                excludes.add(currentExclude.toString());
                currentExclude = null;
                break;
            case SOURCE_TAG:
                try
                {
                    StreamSourceFormat sourceFormat = null;
                    if (format != null)
                    {
                        Set<StreamSourceFormat> sourceFormats
                                = getConfigurationHandlers().getStreamSourceFormats();
                        for (StreamSourceFormat candidate : sourceFormats)
                        {
                            if (format.equals(candidate.getKey()))
                            {
                                sourceFormat = candidate;
                                break;
                            }
                        }
                        if (sourceFormat == null)
                        {
                            throw new SAXException("Unknown source format : "
                                    + format
                                    + " Known types are: " + sourceFormats);
                        }
                    }
                    Fileset sourceFileset = new Fileset(
                            getUnitDescriptor().getProjectPaths()
                                    .getDefaultSourcePath(),
                            includes,
                            excludes);

                    sourceProvider = new FileSourceProvider(
                            sourceFormat,
                            sourceFileset,
                            combineFiles);
                }
                catch (ConfigurationException e)
                {
                    throw new SAXException("Could not create source: "
                            + e.getMessage(),
                            e);
                }
                super.endElement(uri, localName, rawName);
                finished();
                break;
            default:
                super.endElement(uri, localName, rawName);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        if (currentInclude != null)
        {
            for (int i = start; i < start + length; ++i)
            {
                currentInclude.append(ch[i]);
            }
        }
        else if (currentExclude != null)
        {
            for (int i = start; i < start + length; ++i)
            {
                currentExclude.append(ch[i]);
            }
        }
        else
        {
            super.characters(ch, start, length);
        }
    }

    /**
     * Returns the information how to read the sources.
     *
     * @return the source Provider, not null if the
     *         source snippet was processed.
     */
    @Override
    public SourceProvider getSourceProvider()
    {
        return sourceProvider;
    }
}
