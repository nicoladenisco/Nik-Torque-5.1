package org.apache.torque.generator.configuration.outlet;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * Parses outlet configuration files and creates a OutletConfiguration.
 */
public class OutletConfigurationXmlParser
{
    /**
     * The SaxParserFactory to create the sax parsers for reading in the
     * outlet configuration files.
     */
    private static SAXParserFactory saxFactory;

    /** The logger. */
    private static Log log
    = LogFactory.getLog(OutletConfigurationXmlParser.class);

    static
    {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        try
        {
            saxFactory.setFeature(
                    "http://xml.org/sax/features/validation",
                    true);
            saxFactory.setFeature(
                    "http://apache.org/xml/features/validation/schema", true);
        }
        catch (SAXNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        catch (SAXNotRecognizedException e)
        {
            throw new RuntimeException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads all outlet configuration files and creates the outlet
     * configuration from them.
     * All the outlet configuration files known to the provide are parsed.
     * These are typically all XML files in the outletDefintiton configuration
     * directory and its subdirectories.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param configurationHandlers the handlers for reading the configuration,
     *        not null.
     * @param unitDescriptor the unit descriptor, not null.
     *
     * @return the outlet configuration.
     *
     * @throws ConfigurationException if the Configuration cannot be read
     *         or errors exists in the outlet configuration files.
     */
    public OutletConfiguration readOutletConfiguration(
            final ConfigurationProvider configurationProvider,
            final ConfigurationHandlers configurationHandlers,
            final UnitDescriptor unitDescriptor)
                    throws ConfigurationException
    {
        if (configurationHandlers == null)
        {
            log.error("OutletConfiguration: "
                    + " configurationHandlers is null");
            throw new NullPointerException("configurationHandlers is null");
        }
        if (configurationProvider == null)
        {
            log.error("OutletConfiguration: "
                    + " configurationProvider is null");
            throw new NullPointerException("configurationProvider is null");
        }

        List<Outlet> allOutlets = new ArrayList<>();
        List<MergepointMapping> allMergepointMappings
            = new ArrayList<>();

        // Outlets from all files
        Collection<String> outletConfigNames
        = configurationProvider.getOutletConfigurationNames();

        for (String outletConfigName : outletConfigNames)
        {
            try (InputStream inputStream = configurationProvider.getOutletConfigurationInputStream(
                    outletConfigName))
            {
                OutletConfigFileContent fileContent
                = readOutletConfig(
                        inputStream,
                        configurationProvider,
                        unitDescriptor,
                        configurationHandlers);
                allOutlets.addAll(fileContent.getOutlets());
                allMergepointMappings.addAll(
                        fileContent.getMergepointMappings());
            }
            catch (SAXParseException e)
            {
                throw new ConfigurationException(
                        "Error parsing outlet configuration "
                                + outletConfigName
                                + " at line "
                                + e.getLineNumber()
                                + " column "
                                + e.getColumnNumber()
                                + " : "
                                + e.getMessage(),
                                e);

            }
            catch (Exception e)
            {
                throw new ConfigurationException(
                        "Error parsing outlet configuration "
                                + outletConfigName,
                                e);
            }
        }
        // add outlets defined implicitly by templates
        scanTemplatesForOutlets(
                allOutlets,
                configurationProvider,
                configurationHandlers);

        return new OutletConfiguration(
                allOutlets,
                allMergepointMappings,
                unitDescriptor);
    }


    /**
     * Reads a outlet configuration file and returns the outlets
     * and isolated mergepoint mappings which are configured in this file.
     *
     * @param outletConfigurationInputStream the stream containing the
     *        outlet configuration.
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the handlers for reading the configuration,
     *        not null.
     *
     * @return All the outlets and isolated mergepoint mappings
     *         configured in the file.
     * @throws SAXException if an error occurs while parsing the configuration
     *         file.
     * @throws IOException if the file cannot be read.
     * @throws ParserConfigurationException if a serious parser configuration
     *         error occurs.
     * @throws ConfigurationException if the templates names cannot be scanned.
     */
    private OutletConfigFileContent readOutletConfig(
            final InputStream outletConfigurationInputStream,
            final ConfigurationProvider configurationProvider,
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers)
                    throws SAXException, IOException, ParserConfigurationException,
                    ConfigurationException
    {
        SAXParser parser = saxFactory.newSAXParser();
        OutletConfigurationSaxHandler saxHandler
            = new OutletConfigurationSaxHandler(
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
        InputSource is = new InputSource(outletConfigurationInputStream);
        parser.parse(is, saxHandler);

        List<Outlet> outlets = new ArrayList<>();
        outlets.addAll(saxHandler.getOutlets());

        return new OutletConfigFileContent(
                outlets,
                saxHandler.getMergepointMappings());
    }

    private void scanTemplatesForOutlets(
            final List<Outlet> outlets,
            final ConfigurationProvider configurationProvider,
            final ConfigurationHandlers configurationHandlers)
                    throws ConfigurationException
    {
        Set<QualifiedName> outletNames = new HashSet<>();
        for (Outlet outlet : outlets)
        {
            outletNames.add(outlet.getName());
        }
        Collection<String> templateFileNames
        = configurationProvider.getTemplateNames();
        for (String templateFileName : templateFileNames)
        {
            Collection<TypedOutletSaxHandlerFactory> outletSaxHandlerFactories
            = configurationHandlers.getOutletTypes()
            .getTypedOutletHandlerFactories().values();
            for (TypedOutletSaxHandlerFactory outletSaxHandlerFactory
                    : outletSaxHandlerFactories)
            {
                for (String suffix : outletSaxHandlerFactory
                        .getTemplatesFilenameExtensionsForScan())
                {
                    if (templateFileName.endsWith(suffix))
                    {
                        Outlet outlet
                        = outletSaxHandlerFactory.createOutletForTemplate(
                                templateFileName,
                                configurationProvider);
                        QualifiedName outletName = outlet.getName();
                        if (!outletNames.contains(outletName))
                        {
                            outlets.add(outlet);
                            outletNames.add(outletName);
                            continue;
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates an outlet name from a file path to a template.
     *
     * @param path the path to the file, relative to the templates directory,
     *        not null.
     *
     * @return the outlet name, not null.
     */
    static QualifiedName getOutletNameForFilename(final String path)
    {
        String resultName = path;
        int dotIndex = resultName.lastIndexOf('.');
        if (dotIndex != -1)
        {
            resultName = resultName.substring(0, dotIndex);
        }
        resultName = resultName.replace('/', '.');
        resultName = resultName.replace('\\', '.');
        QualifiedName result = new QualifiedName(resultName);
        return result;
    }

    /**
     * The parsed content of a outlet definition file.
     * Contains the parsed outlets and the isolated
     * (i.e. outside outlet context) mergepoints.
     */
    private static final class OutletConfigFileContent
    {
        /** The parsed outlets. */
        private final List<Outlet> outlets;

        /** The parsed isolated mergepoint mappings. */
        private final List<MergepointMapping> mergepointMappings;

        /**
         * Constructor.
         *
         * @param outlets the parsed outlets.
         * @param mergepointMappings the isolated mergepoint mappings.
         */
        public OutletConfigFileContent(
                final List<Outlet> outlets,
                final List<MergepointMapping> mergepointMappings)
        {
            this.outlets = outlets;
            this.mergepointMappings = mergepointMappings;
        }

        /**
         * Returns the parsed outlets.
         *
         * @return the parsed outlets.
         */
        public List<Outlet> getOutlets()
        {
            return outlets;
        }

        /**
         * Returns the parsed isolated mergepoint mappings.
         *
         * @return the isolated mergepoint mappings.
         */
        public List<MergepointMapping> getMergepointMappings()
        {
            return mergepointMappings;
        }
    }
}
