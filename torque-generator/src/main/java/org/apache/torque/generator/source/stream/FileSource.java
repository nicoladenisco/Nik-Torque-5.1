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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.SourceImpl;

/**
 * A Source which uses a file as input.
 */
public class FileSource extends SourceImpl
{
    /**
     * The log of the class.
     */
    private static Log log = LogFactory.getLog(FileSource.class);

    /**
     * The format of the file, e.g. properties or XML, not null.
     */
    private final StreamSourceFormat format;

    /**
     * The path of the file, not null.
     */
    private final File path;

    /**
     * The controller state, not null.
     */
    private final ControllerState controllerState;

    /**
     * The md5sum of the file content.
     */
    private byte[] contentMd5Sum;

    /**
     * Constructor.
     *
     * @param format the source format, not null.
     * @param path the path to the file to read, not null.
     * @param controllerState the controller state, not null.
     *
     * @throws NullPointerException if path or format is null.
     */
    public FileSource(
            final StreamSourceFormat format,
            final File path,
            final ControllerState controllerState)
    {
        if (path == null)
        {
            throw new NullPointerException("path must not be null");
        }
        if (format == null)
        {
            throw new NullPointerException("format must not be null");
        }
        if (controllerState == null)
        {
            throw new NullPointerException("controllerState must not be null");
        }
        this.format = format;
        this.path = path;
        this.controllerState = controllerState;
    }

    /**
     * Returns the format of the source.
     *
     * @return the source format, not null.
     */
    public StreamSourceFormat getSourceFormat()
    {
        return format;
    }

    /**
     * Returns the path to the source file.
     *
     * @return the path to the source file, not null.
     */
    public File getPath()
    {
        return path;
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
        SourceElement result;
        try (InputStream inputStream = new FileInputStream(path))
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            DigestInputStream digestInputStream
                = new DigestInputStream(inputStream, messageDigest);
            log.debug("Reading file "
                    + path.getAbsolutePath()
                    + " of type "
                    + format.getKey());
            result = format.parse(digestInputStream, controllerState);
            contentMd5Sum = messageDigest.digest();
        }
        catch (FileNotFoundException e)
        {
            throw new SourceException(
                    "File not found: " + path.getAbsolutePath(),
                    e);
        }
        catch (IOException e)
        {
            throw new SourceException(
                    "Could not close: " + path.getAbsolutePath(),
                    e);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new SourceException(
                    "MD5 message Digest not implemented",
                    e);
        }

        if (log.isDebugEnabled())
        {
            log.debug("finished creating root Element, source is\n"
                    + new SourceToXml().toXml(result, true));
        }
        return result;
    }

    /**
     * Returns the path of the file as a description.
     *
     * @return path of the file, not null.
     *
     * @see org.apache.torque.generator.source.Source#getDescription()
     */
    @Override
    public String getDescription()
    {
        return path.getAbsolutePath();
    }

    /**
     * Returns the source file, if it exists.
     *
     * @return the source file, or null if the source is not read from a file.
     */
    @Override
    public File getSourceFile()
    {
        return path;
    }

    /**
     * Returns the date when the source was last modified.
     *
     * @return the last modification date, or null when unknown.
     */
    @Override
    public Date getLastModified()
    {
        long lastModified = path.lastModified();
        if (lastModified == 0)
        {
            return null;
        }
        return new Date(lastModified);
    }

    /**
     * Returns the checksum of the content.
     *
     * @return the md5 sum of the content,
     *         or null if the implementation does not support
     *         creating the checksum, or if the source is unread
     *         and reading the source fails.
     */
    @Override
    public byte[] getContentChecksum()
    {
        if (contentMd5Sum == null)
        {
            try
            {
                getRootElement();
            }
            catch (SourceException e)
            {
                // contentMd5Sum remains as set by getRootElement()
            }
        }
        if (contentMd5Sum == null)
        {
            return null;
        }
        return Arrays.copyOf(contentMd5Sum, contentMd5Sum.length);
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(path=").append(path)
        .append(",type=").append(format);
        return result.toString();
    }
}
