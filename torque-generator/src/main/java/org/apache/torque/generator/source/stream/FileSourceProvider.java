package org.apache.torque.generator.source.stream;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.source.Source;
import org.apache.torque.generator.source.SourceProvider;

/**
 * Contains sources which are read from files in the file system.
 *
 * $Id: FileSourceProvider.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class FileSourceProvider extends SourceProvider
{
    /** The class log. */
    private static Log log = LogFactory.getLog(FileSourceProvider.class);

    /**
     * The type of the file(s), or null if the type should be determined
     * from the file name extension.
     */
    private StreamSourceFormat sourceFormat;

    /** The fileset defining the source files, not null. */
    private Fileset sourceFileset;

    /**
     * Whether all files should be combined into one source tree.
     * Null to allow overriding this property (but will count as false
     * if not overridden).
     */
    private Boolean combineFiles;

    /** The paths of all contained Files. */
    private List<File> paths;

    /** The iterator over all contained Files. */
    private Iterator<File> pathIt;

    /** The known stream source formats. */
    private Set<StreamSourceFormat> streamSourceFormats;

    /** The Controller state. */
    private ControllerState controllerState;


    /**
     * Constructor.
     *
     * @param sourceFormat the source format, or null if the source format
     *        should be determined from the file extension.
     * @param sourceFileset the fileset defining the source files, not null.
     * @param combineFiles whether all files should be combined into
     *        one source tree.
     *
     * @throws IllegalArgumentException if the source type is unknown.
     * @throws NullPointerException if path is null.
     * @throws ConfigurationException if the source filter cannot be
     *         instantiated.
     */
    public FileSourceProvider(
            StreamSourceFormat sourceFormat,
            Fileset sourceFileset,
            Boolean combineFiles)
                    throws ConfigurationException
    {
        if (sourceFileset == null)
        {
            throw new NullPointerException("sourceFileset must not be null");
        }
        this.sourceFormat = sourceFormat;
        this.sourceFileset = sourceFileset;
        this.combineFiles = combineFiles;
    }

    /**
     * Determines the files which match the source fileset.
     *
     * @param configurationHandlers the configuration handlers, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws ConfigurationException if the sourceFileset has no basedir or
     *         if the files cannot be determined.
     */
    @Override
    protected void initInternal(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
                    throws ConfigurationException
    {
        if (controllerState == null)
        {
            throw new NullPointerException("controllerState must not be null");
        }
        this.controllerState = controllerState;

        if (sourceFileset.getBasedir() == null)
        {
            throw new ConfigurationException(
                    "Basedir of sourceFileset must not be null");
        }
        try
        {
            paths = sourceFileset.getFiles();
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }
        log.debug("initInternal(): " + paths.size() + " matching files found.");
        pathIt = paths.iterator();
        streamSourceFormats = configurationHandlers.getStreamSourceFormats();
    }

    @Override
    public void resetInternal(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
    {
        paths = null;
        pathIt = null;
        streamSourceFormats = null;
        this.controllerState = null;
    }

    @Override
    public boolean hasNext()
    {
        if (!isInit())
        {
            throw new IllegalStateException(
                    "init() must be called on this SourceProvider "
                            + "before hasNext can be called");
        }
        return pathIt.hasNext();
    }

    @Override
    public Source next()
    {
        if (!isInit())
        {
            throw new IllegalStateException(
                    "init() must be called on this SourceProvider "
                            + "before hasNext can be called");
        }
        if (combineFiles == null || !combineFiles)
        {
            return getNextFileSource();
        }
        else
        {
            List<FileSource> fileSources = new ArrayList<>();
            while (pathIt.hasNext())
            {
                fileSources.add(getNextFileSource());
            }
            return new CombinedFileSource(fileSources);
        }
    }

    private FileSource getNextFileSource()
    {
        File currentPath = pathIt.next();
        StreamSourceFormat currentSourceFormat = sourceFormat;
        if (currentSourceFormat == null)
        {
            for (StreamSourceFormat candidate : streamSourceFormats)
            {
                String filenameExtension = candidate.getFilenameExtension();
                if (filenameExtension != null
                        && currentPath.getName().endsWith(
                                "." + filenameExtension))
                {
                    currentSourceFormat = candidate;
                    break;
                }
            }
        }
        if (currentSourceFormat == null)
        {
            throw new RuntimeException(
                    "format is not set and file extension is unknown for file "
                            + currentPath.getAbsolutePath());
        }
        return new FileSource(
                currentSourceFormat,
                currentPath,
                controllerState);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(sourceFileset=").append(sourceFileset)
        .append(",sourceFormat=").append(sourceFormat)
        .append(")");
        return result.toString();
    }

    /**
     * Returns the source format for this provider.
     *
     * @return the source format, or null if the format is determined
     *         from file extensions.
     */
    public StreamSourceFormat getSourceFormat()
    {
        return sourceFormat;
    }

    /**
     * Returns the fileset which determines the source files to read.
     *
     * @return the fileset which determines the source files to read.
     */
    public Fileset getSourceFileset()
    {
        return sourceFileset;
    }

    /**
     * Sets the fileset which determines the source files to read.
     *
     * @param sourceFileset the new source fileset, not null.
     *
     * @throws NullPointerException if sourceFileset is null.
     * @throws IllegalStateException if init() was called before.
     */
    public void setSourceFileset(Fileset sourceFileset)
    {
        if (sourceFileset == null)
        {
            throw new NullPointerException("sourceFileset is null");
        }
        if (isInit())
        {
            throw new IllegalStateException(
                    "Source files are already determined");
        }
        this.sourceFileset = sourceFileset;
    }

    /**
     * Returns all paths in the source Fileset. The method init() must be
     * called beforehand.
     *
     * @return all Paths in the source fileset, not null.
     *
     * @throws IllegalStateException if the init method was not yet called.
     */
    public List<File> getPaths()
    {
        if (!isInit())
        {
            throw new IllegalStateException(
                    "init() must be called on this SourceProvider "
                            + "before hasNext can be called");
        }
        return Collections.unmodifiableList(paths);
    }

    /**
     * Returns whether source files are combined into one source tree.
     *
     * @return true if the source files are combined into one source tree,
     *         false otherwise, null to allow overriding this property
     *         (but will count as false if not overridden).
     */
    public Boolean getCombineFiles()
    {
        return combineFiles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceProvider copy() throws ConfigurationException
    {
        FileSourceProvider result = new FileSourceProvider(
                sourceFormat,
                sourceFileset,
                combineFiles);
        return result;
    }

    /**
     * Copies settings which are not set in this source provider from another
     * source provider. This only works if the type of the other source
     * provider is known to this source provider.
     * Only a subset of all properties are typically used for overwriting.
     * No Properties which are already set are overwritten.
     *
     * @param sourceProvider the source provoder to copy the settings from.
     */
    @Override
    public void copyNotSetSettingsFrom(SourceProvider sourceProvider)
    {
        if (sourceProvider == null)
        {
            return;
        }
        if (!(sourceProvider instanceof FileSourceProvider))
        {
            return;
        }
        FileSourceProvider fileSourceProvider
        = (FileSourceProvider) sourceProvider;
        if (combineFiles == null)
        {
            log.debug("copying combineFiles property"
                    + " from other source provider");
            combineFiles = fileSourceProvider.getCombineFiles();
        }
    }

}
