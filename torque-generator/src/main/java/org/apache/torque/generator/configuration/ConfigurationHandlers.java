package org.apache.torque.generator.configuration;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.torque.generator.configuration.mergepoint.ActionSaxHandlerFactories;
import org.apache.torque.generator.configuration.mergepoint.OptionsSaxHandlerFactories;
import org.apache.torque.generator.configuration.source.SourceSaxHandlerFactories;
import org.apache.torque.generator.control.outputtype.HtmlOutputType;
import org.apache.torque.generator.control.outputtype.JavaOutputType;
import org.apache.torque.generator.control.outputtype.OutputType;
import org.apache.torque.generator.control.outputtype.PropertiesOutputType;
import org.apache.torque.generator.control.outputtype.UnknownOutputType;
import org.apache.torque.generator.control.outputtype.XmlOutputType;
import org.apache.torque.generator.source.stream.PropertiesSourceFormat;
import org.apache.torque.generator.source.stream.StreamSourceFormat;
import org.apache.torque.generator.source.stream.XmlSourceFormat;

/**
 * Contains the required handlers for reading the configuration.
 *
 * $Id: ConfigurationHandlers.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class ConfigurationHandlers
{
    /**
     * The known types of outlets and how to read their configuration.
     */
    private OutletTypes outletTypes = new OutletTypes();

    /**
     * The known types of outputs.
     */
    private Map<String, OutputType> outputTypes
        = new HashMap<>();

    /**
     * The known (file) formats of stream sources (e.g. XML, properties).
     */
    private Set<StreamSourceFormat> streamSourceFormats
        = new HashSet<>();

    /**
     * The known mergepoint action configuration handlers.
     */
    private ActionSaxHandlerFactories actionSaxHandlerFactories
        = new ActionSaxHandlerFactories();

    /**
     * The known option configuration handlers.
     */
    private OptionsSaxHandlerFactories optionsSaxHandlerFactories
        = new OptionsSaxHandlerFactories();

    /**
     * The known source configuration handlers.
     */
    private SourceSaxHandlerFactories sourceSaxHandlerFactories
        = new SourceSaxHandlerFactories();

    /**
     * Standard constructor.
     */
    public ConfigurationHandlers()
    {
        streamSourceFormats.add(new XmlSourceFormat());
        streamSourceFormats.add(new PropertiesSourceFormat());
        outputTypes.put(UnknownOutputType.KEY, new UnknownOutputType());
        outputTypes.put(JavaOutputType.KEY, new JavaOutputType());
        outputTypes.put(XmlOutputType.KEY, new XmlOutputType());
        outputTypes.put(HtmlOutputType.KEY, new HtmlOutputType());
        outputTypes.put(PropertiesOutputType.KEY, new PropertiesOutputType());
    }

    /**
     * Returns the known outlet types.
     *
     * @return the known outlet types, not null.
     */
    public OutletTypes getOutletTypes()
    {
        return outletTypes;
    }

    /**
     * Returns the known output types.
     *
     * @return the known outlet types, not null.
     */
    public Map<String, OutputType> getOutputTypes()
    {
        return outputTypes;
    }

    /**
     * Returns the known source configuration handlers.
     *
     * @return the known source configuration handlers, not mull.
     */
    public SourceSaxHandlerFactories getSourceSaxHandlerFactories()
    {
        return sourceSaxHandlerFactories;
    }

    /**
     * Returns the known formats of stream (e.g. file) sources.
     *
     * @return the known stream source formats, not null.
     */
    public Set<StreamSourceFormat> getStreamSourceFormats()
    {
        return streamSourceFormats;
    }

    /**
     * Returns the known mergepoint action handlers.
     *
     * @return the known mergepoint action handlers, not null.
     */
    public ActionSaxHandlerFactories getActionSaxHandlerFactories()
    {
        return actionSaxHandlerFactories;
    }

    /**
     * Returns the known options handlers.
     *
     * @return the known options handlers, not null.
     */
    public OptionsSaxHandlerFactories getOptionsSaxHandlerFactories()
    {
        return optionsSaxHandlerFactories;
    }
}
