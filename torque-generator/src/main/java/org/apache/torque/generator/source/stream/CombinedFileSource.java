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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.SourceImpl;

/**
 * A source which uses several files as input and combines them into one file.
 * The source tree will look as follows:
 * &lt;source&gt;
 *   &lt;file path="path/to/file1"&gt;
 *      &lt;rootOfFile1&gt;
 *        ...
 *      &lt;/rootOfFile1&gt;
 *   &lt;/file&gt;
 *   &lt;file path="path/to/file2"&gt;
 *      &lt;rootOfFile2&gt;
 *        ...
 *      &lt;/rootOfFile2&gt;
 *   &lt;/file&gt;
 *   ...
 * &lt;/source&gt;
 */
public class CombinedFileSource extends SourceImpl
{
    /** The name of the root element of the produced source tree. */
    public static final String ROOT_ELEMENT_NAME = "source";

    /** The name of the root element's children of the produced source tree. */
    public static final String FILE_ELEMENT_NAME = "file";

    /** The name of the path attribute of the file elements. */
    public static final String PATH_ATTRIBUTE_NAME = "path";

    /**
     * The log of the class.
     */
    private static final Log log = LogFactory.getLog(CombinedFileSource.class);

    /**
     * The list of contained file sources, not null..
     */
    private final List<FileSource> fileSources;

    /**
     * Constructor.
     *
     * @param fileSources the file sources, not null.
     *
     * @throws NullPointerException if path or format is null.
     */
    public CombinedFileSource(
            final Collection<FileSource> fileSources)
    {
        if (fileSources == null)
        {
            throw new NullPointerException("path must not be null");
        }
        this.fileSources = new ArrayList<>(fileSources);
    }

    /**
     * Reads and parses the input file and creates the element tree from it.
     *
     * @throws SourceException if the input file cannot be read or parsed.
     *
     * @return the root element of the element tree.
     */
    @Override
    public SourceElement createRootElement() throws SourceException
    {
        if (log.isDebugEnabled())
        {
            log.debug("start creating root Element");
        }

        SourceElement result = new SourceElement(ROOT_ELEMENT_NAME);
        for (FileSource fileSource : fileSources)
        {
            SourceElement fileElement = new SourceElement(FILE_ELEMENT_NAME);
            fileElement.setAttribute(
                    PATH_ATTRIBUTE_NAME,
                    fileSource.getPath().getPath());
            fileElement.getChildren().add(fileSource.getRootElement());
            result.getChildren().add(fileElement);
        }

        if (log.isDebugEnabled())
        {
            log.debug("finished creating root Element, source is\n"
                    + new SourceToXml().toXml(result, true));
        }
        return result;
    }

    /**
     * Returns the path of the files as a description.
     *
     * @return path of the files,separated by a semicolon, not null.
     *
     * @see org.apache.torque.generator.source.Source#getDescription()
     */
    @Override
    public String getDescription()
    {
        String result =
                fileSources.stream()
                        .map(fileSource ->
                                String.valueOf(fileSource.getPath()))
                        .collect(Collectors.joining(";"));
        return result;
    }

    /**
     * Returns the source file, if applicable. As no single source file
     * exists, the method returns always null.
     *
     * @return null.
     */
    @Override
    public File getSourceFile()
    {
        return null;
    }

    /**
     * Returns the earliest date when any of the source files was last modified.
     *
     * @return the last modification date,
     *         or null when unknown for at least one of the files.
     */
    @Override
    public Date getLastModified()
    {
        Date result = null;
        for (FileSource fileSource : fileSources)
        {
            Date fileLastModified = fileSource.getLastModified();
            if (fileLastModified == null)
            {
                return null;
            }
            if (result == null || fileLastModified.before(result))
            {
                result = fileLastModified;
            }
        }
        return result;
    }

    /**
     * Returns the checksum of all files.
     * All bytes are added so order of the files does not matter.
     *
     * @return a checksum for all files, or null if one of the checksums
     *         of the file sources is null.
     */
    @Override
    public byte[] getContentChecksum()
    {
        byte[] result = new byte[] {};

        for (FileSource fileSource : fileSources)
        {
            byte[] fileChecksum = fileSource.getContentChecksum();
            if (fileChecksum == null)
            {
                return null;
            }
            byte[] lastResult = result;
            result = new byte[Math.max(lastResult.length, fileChecksum.length)];
            for (int i = 0; i < result.length; ++i)
            {
                if (i < lastResult.length && i < fileChecksum.length)
                {
                    result[i] = (byte) (lastResult[i] + fileChecksum[i]);
                }
                else if (i < lastResult.length)
                {
                    result[i] = lastResult[i];
                }
                else
                {
                    result[i] = fileChecksum[i];
                }
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return getDescription();
    }

}
