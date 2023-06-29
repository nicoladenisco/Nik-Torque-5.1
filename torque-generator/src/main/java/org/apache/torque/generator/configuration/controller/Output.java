package org.apache.torque.generator.configuration.controller;

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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.control.existingtargetstrategy.ReplaceTargetFileStrategy;
import org.apache.torque.generator.control.outputtype.UnknownOutputType;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.PostprocessorDefinition;
import org.apache.torque.generator.source.SourceProcessConfiguration;
import org.apache.torque.generator.source.SourceProvider;

/**
 * The configuration for an output (typically one or more files).
 */
public class Output
{
    /** Constant for the Carriage return String. */
    private static final String CARRIAGE_RETURN = "\r";

    /** Constant for the Line feed String. */
    private static final String LINE_FEED = "\n";

    /** Constant for the Carriage return + Line feed String. */
    private static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";

    /**
     * The name by which this output can be identified.
     */
    private final QualifiedName name;

    /**
     * The source provider to access the input for generation.
     */
    private SourceProvider sourceProvider;

    /**
     * The information on how to pre-process the sources before generation.
     */
    private SourceProcessConfiguration sourceProcessConfiguration;

    /**
     * The information on how to post-process the sources after generation.
     */
    private final List<PostprocessorDefinition> postprocessorDefinitions
        = new ArrayList<>();;

    /**
     * The outlet which generates the content.
     */
    private OutletReference contentOutlet;

    /**
     * The outlet which generates the filename, or null if the
     * path to the generated file is explicitly given in <code>path</code>.
     */
    private Outlet filenameOutlet;

    /**
     * The filename of the generated file, or null if the filename must
     * still be generated using a filenameOutlet.
     * This attribute is also used to store the generated filename
     * if it was generated using the filenameOutlet.
     */
    private String filename;

    /**
     * The strategy how existing target files should be handled.
     */
    private String existingTargetStrategy
    = ReplaceTargetFileStrategy.STRATEGY_NAME;

    /**
     * The type of the generated output, e.g. java, xml, html.
     * default is unknown.
     */
    private String type = UnknownOutputType.KEY;

    /**
     * The file's line break character(s), determined from the already
     * generated output.
     */
    private String lineBreak;

    /**
     * The key for the output directory into which the output is written,
     * or null for the default output directory.
     */
    private String outputDirKey;

    /**
     * The character encoding of the generated file, or null for the platform
     * default encoding.
     */
    private String encoding;

    /**
     * Constructor.
     *
     * @param name the name by which this output can be identified.
     */
    public Output(final QualifiedName name)
    {
        this.name = name;
    }

    /**
     * Returns the name by which this output can be identified.
     *
     * @return the name by which this output can be identified, not null.
     */
    public QualifiedName getName()
    {
        return name;
    }

    /**
     * Returns the source provider which provides the input for generating the
     * output file's contents.
     *
     * @return the source provider which provides the input for generation.
     */
    public SourceProvider getSourceProvider()
    {
        return sourceProvider;
    }

    /**
     * Sets the source provider which provides the input for generating the
     * output file's contents.
     *
     * @param sourceProvider the source provider which provides the input for
     *        generation.
     */
    public void setSourceProvider(final SourceProvider sourceProvider)
    {
        this.sourceProvider = sourceProvider;
    }

    /**
     * Returns how the sources should be post-processed before generation.
     *
     * @return the information about post-processing the sources.
     */
    public SourceProcessConfiguration getSourceProcessConfiguration()
    {
        return sourceProcessConfiguration;
    }

    /**
     * Sets how the sources should be post-processed before generation.
     *
     * @param sourceProcessConfiguration the information about
     *      post-processing the sources.
     */
    public void setSourceProcessConfiguration(
            final SourceProcessConfiguration sourceProcessConfiguration)
    {
        this.sourceProcessConfiguration = sourceProcessConfiguration;
    }

    /**
     * Returns the list of postprocessors which should be applied
     * to the generation result.
     *
     * @return the modifiable list of postprocessors, not null.
     *         Changes to the returned list change this object as well.
     */
    public List<PostprocessorDefinition> getPostprocessorDefinitions()
    {
        return postprocessorDefinitions;
    }

    /**
     * Sets the name of the file to generate.
     * This is also used to store the generated filename
     * if it was generated using the filenameOutlet.
     *
     * @param filename the name of the file to generate.
     */
    public void setFilename(final String filename)
    {
        this.filename = filename;
    }

    /**
     * Returns the name of the file to generate. Either this name was
     * given explicitly or it was set using the filenameOutlet.
     *
     * @return the name of the file to generate.
     */
    public String getFilename()
    {
        return filename;
    }

    /**
     * Returns the reference to the outlet which should produce the content.
     *
     * @return the reference to the outlet responsible for producing the
     *         content.
     */
    public OutletReference getContentOutlet()
    {
        return contentOutlet;
    }

    /**
     * Sets the reference to the outlet which should produce the content.
     *
     * @param contentOutlet the reference to the outlet responsible
     *        for producing the content.
     */
    public void setContentOutlet(final OutletReference contentOutlet)
    {
        this.contentOutlet = contentOutlet;
    }

    /**
     * Returns the reference to the outlet which should produce
     * the file name. If this attribute is set, it takes precedence over a
     * set filename.
     *
     * @return the reference to the outlet responsible for producing the
     *         file name, or null if the file name is explicitly given.
     */
    public Outlet getFilenameOutlet()
    {
        return filenameOutlet;
    }

    /**
     * Sets the reference to the outlet which should produce
     * the file name.
     *
     * @param filenameOutlet the reference to the outlet responsible
     *        for producing the file name.
     */
    public void setFilenameOutlet(final Outlet filenameOutlet)
    {
        this.filenameOutlet = filenameOutlet;
    }

    /**
     * Returns the strategy how existing target files should be handled.
     *
     * @return the strategy name.
     */
    public String getExistingTargetStrategy()
    {
        return existingTargetStrategy;
    }

    /**
     * Sets the strategy how existing target files should be handled.
     *
     * @param existingTargetStrategy the strategy name.
     */
    public void setExistingTargetStrategy(final String existingTargetStrategy)
    {
        this.existingTargetStrategy = existingTargetStrategy;
    }

    /**
     * Returns the key for the output directory into which the output is
     * written.
     *
     * @return the key  for the output directory, or null for the default
     *         output directory.
     */
    public String getOutputDirKey()
    {
        return outputDirKey;
    }

    /**
     * Sets the key for the output directory into which the output is
     * written.
     *
     * @param outputKeyDir the key  for the output directory,
     *        or null for the default output directory.
     */
    public void setOutputDirKey(final String outputKeyDir)
    {
        this.outputDirKey = outputKeyDir;
    }

    /**
     * Returns the character encoding of the generated file(s).
     *
     * @return The character encoding of the generated file,
     *         or null for the platform default encoding.
     */
    public String getEncoding()
    {
        return encoding;
    }

    /**
     * Sets the character encoding of the generated file(s).
     *
     * @param encoding The character encoding of the generated file,
     *        or null for the platform default encoding.
     */
    public void setEncoding(final String encoding)
    {
        this.encoding = encoding;
    }

    /**
     * Sets the type of the produced output, e.g. java, xml.
     *
     * @param type the type, not null.
     *
     * @throws NullPointerException if type is null.
     */
    public void setType(final String type)
    {
        if (type == null)
        {
            throw new NullPointerException("type must not be null");
        }
        this.type = type;
    }

    /**
     * Returns the type of the produced output, e.g. java, xml.
     *
     * @return the type, not null.
     */
    public String getType()
    {
        return type;
    }

    /**
     * Returns the line break character(s) for this Output.
     * 
     * <p>
     * If the line break was already determined, the already determined
     * line break character(s) are returned, and content is ignored.
     * 
     * <p>
     * If the line break character was not already determined,
     * the occurrence of the different line break characters is counted
     * and the largest is returned ("\r\n" for equal count)
     * 
     * <p>
     * If one of them is &gt; 0, the result is cached and stored;
     * if all occurrences are 0, the result is not cached and will be determined
     * anew if the method is called another time for the same output.
     *
     * @param content the already produced content.
     * @return the line break character(s), one of "\r", "\n", "\r\n".
     */
    public String getOrDetermineLineBreak(final String content)
    {
        if (lineBreak != null)
        {
            return lineBreak;
        }
        String contentString = content.toString();
        int r = StringUtils.countMatches(contentString, CARRIAGE_RETURN);
        int n = StringUtils.countMatches(contentString, LINE_FEED);
        int rn = StringUtils.countMatches(
                contentString,
                CARRIAGE_RETURN_LINE_FEED);
        // r and n are contained within rn
        if (rn >= r - rn || rn >= n - rn)
        {
            if (rn > 0)
            {
                this.lineBreak = CARRIAGE_RETURN_LINE_FEED;
            }
            return CARRIAGE_RETURN_LINE_FEED;
        }
        else if (n > r)
        {
            this.lineBreak = LINE_FEED;
            return LINE_FEED;
        }
        else
        {
            this.lineBreak = CARRIAGE_RETURN;
            return CARRIAGE_RETURN;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(OutputFile: sourceProvider=").append(sourceProvider)
        .append(",existingTargetStrategy=")
        .append(existingTargetStrategy)
        .append(",encoding=")
        .append(encoding)
        .append(",filenameOutlet=")
        .append(filenameOutlet)
        .append(",contentOutlet=")
        .append(contentOutlet)
        .append(",type=")
        .append(type)
        .append(")");
        return result.toString();
    }
}
