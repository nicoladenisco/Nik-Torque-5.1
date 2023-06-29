package org.apache.torque.generator.template;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.control.TokenReplacer;
import org.apache.torque.generator.outlet.OutletImpl;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * An implementation of the TemplateOutlet interface.
 */
public abstract class TemplateOutletImpl
extends OutletImpl
implements TemplateOutlet
{
    /**
     * The content of the templates.
     * The key is the resolved template path, the value is the template content.
     * There may be more than one template content per template because
     * the template path can contain tokens.
     */
    private final Map<String, String> contentMap = new HashMap<>();

    /**
     * The path to the template. May contain unresolved tokens.
     */
    private String path = null;

    /**
     * The encoding of the template.
     */
    private String encoding = null;

    /**
     * The configuration provider for accessing the template content.
     */
    private final ConfigurationProvider configurationProvider;

    /**
     * The Filter for filtering the template content.
     */
    private final TemplateFilter templateFilter;

    /** Buffer size for loading the template File. */
    private static final int LOAD_BUFFER_SIZE = 8192;

    /**
     * How many characters of a template content should be output
     * in the toString method.
     */
    private static final int CONTENT_TO_STRING_MAX_OUTPUT_LENGTH = 40;

    /**
     * Constructs a TemplateOutletImpl with the given name.
     *
     * @param name the name of this outlet, not null.
     * @param configurationProvider the provider for reading the templates,
     *        not null.
     * @param path the path to the templates, not null.
     *        May contain tokens of the form ${....}, these are parsed.
     * @param encoding the encoding of the file, or null if the system's
     *        default encoding should be used.
     * @param templateFilter a possible filter for preprocessing the template,
     *        not null.
     *
     * @throws NullPointerException if name or inputStream are null.
     * @throws ConfigurationException if the template cannot be loaded.
     */
    protected TemplateOutletImpl(
            final QualifiedName name,
            final ConfigurationProvider configurationProvider,
            final String path,
            final String encoding,
            final TemplateFilter templateFilter)
                    throws ConfigurationException
    {
        super(name);
        if (configurationProvider == null)
        {
            throw new NullPointerException(
                    "configurationProvider must not be null");
        }
        if (path == null)
        {
            throw new NullPointerException("path must not be null");
        }
        this.configurationProvider = configurationProvider;
        this.path = path;
        this.encoding = encoding;
        this.templateFilter = templateFilter;
    }

    @Override
    public String getContent(final ControllerState controllerState)
            throws ConfigurationException
    {
        String detokenizedPath = getDetokenizedPath(controllerState);

        String result = contentMap.get(detokenizedPath);
        if (result == null)
        {
            try (InputStream templateInputStream
                    = configurationProvider.getTemplateInputStream(
                            detokenizedPath))
            {
                result = load(templateInputStream, encoding, templateFilter);
            }
            catch (IOException e)
            {
                throw new ConfigurationException(e);
            }
            contentMap.put(detokenizedPath, result);
        }

        return result;
    }

    protected String getDetokenizedPath(final ControllerState controllerState)
    {
        TokenReplacer tokenReplacer = new TokenReplacer(controllerState);
        String detokenizedPath = tokenReplacer.process(path);
        return detokenizedPath;
    }

    /**
     * Loads the template, possibly filtering the content..
     *
     * @param inputStream the stream to read from.
     * @param encoding the encoding of the template, or null for auto detection.
     * @param filter a filter for modifying the template,
     *        or null for no filtering.
     *
     * @return the content of the read and filtered template.
     *
     * @throws IOException if an error occurs while reading the template.
     */
    protected String load(
            final InputStream inputStream,
            final String encoding,
            final TemplateFilter filter)
                    throws IOException
    {
        InputStream filteredStream;
        if (filter != null)
        {
            filteredStream = filter.filter(inputStream, encoding);
        }
        else
        {
            filteredStream = inputStream;
        }
        Reader reader;
        if (encoding == null)
        {
        	// default to UTF 8 otherwise FindBugs complains of DM_DEFAULT_ENCODING
        	reader = new InputStreamReader(filteredStream, StandardCharsets.UTF_8);
        }
        else
        {
            reader = new InputStreamReader(filteredStream, encoding);
        }

        StringBuffer contentBuffer = new StringBuffer();
        while (true)
        {
            char[] charBuffer = new char[LOAD_BUFFER_SIZE];
            int filledChars = reader.read(charBuffer);
            if (filledChars == -1)
            {
                break;
            }
            contentBuffer.append(charBuffer, 0, filledChars);
        }

        return contentBuffer.toString();
    }

    /**
     * Returns the path to the template.
     *
     * @return the path to the template, not null.
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Returns a String representation of this outlet for debugging purposes.
     *
     * @return a String representation of this outlet, never null.
     *
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer(super.toString()).append(",");
        result.append("encoding=").append(encoding).append(",");
        result.append("content=");
        if (!contentMap.isEmpty())
        {
            String firstContent = contentMap.values().iterator().next();
            if (firstContent.length() > CONTENT_TO_STRING_MAX_OUTPUT_LENGTH)
            {
                result.append(firstContent.substring(
                        0,
                        CONTENT_TO_STRING_MAX_OUTPUT_LENGTH));
            }
            else
            {
                result.append(firstContent);
            }
        }
        return result.toString();
    }
}
