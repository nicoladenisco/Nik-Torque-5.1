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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.UnitDescriptor.Packaging;
import org.apache.torque.generator.configuration.controller.ControlConfiguration;
import org.apache.torque.generator.configuration.controller.ControlConfigurationXmlParser;
import org.apache.torque.generator.configuration.controller.Output;
import org.apache.torque.generator.configuration.option.OptionsConfiguration;
import org.apache.torque.generator.configuration.outlet.OutletConfiguration;
import org.apache.torque.generator.configuration.outlet.OutletConfigurationXmlParser;
import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.option.Option;
import org.apache.torque.generator.option.Options;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Reads the configuration of a unit of generation.
 */
class UnitConfigurationReader
{
    /**
     * The logger.
     */
    private static Log log = LogFactory.getLog(UnitConfigurationReader.class);

    /**
     * Reads the configuration for a unit of generation.
     * @param unitDescriptor the descriptor of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @return the configuration of the unit of generation.
     * @throws ConfigurationException if the configuration is incorrect
     *         or cannot be read.
     * @throws NullPointerException if the unitDescriptor or
     *         configurationHandlers is null.
     * @throws IllegalArgumentException if a reader is the wrong type of reader
     *         for the given configuration type.
     */
    public UnitConfiguration read(
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers)
                    throws ConfigurationException
    {
        if (unitDescriptor == null)
        {
            throw new NullPointerException("unitDescriptor must not be null");
        }
        if (configurationHandlers == null)
        {
            throw new NullPointerException(
                    "configurationHandlers must not be null");
        }

        if (log.isDebugEnabled())
        {
            log.debug("Start reading unitConfiguration for unit "
                    + getUnitDisplayName(unitDescriptor));
        }

        UnitConfiguration unitConfiguration = new UnitConfiguration();
        unitConfiguration.setConfigurationHandlers(
                configurationHandlers);
        unitConfiguration.setOutputDirectoryMap(
                unitDescriptor.getProjectPaths().getOutputDirectoryMap());
        unitConfiguration.setWorkDirectory(
                unitDescriptor.getProjectPaths().getWorkDirectory());
        unitConfiguration.setCacheDirectory(
                unitDescriptor.getProjectPaths().getCacheDirectory());
        unitConfiguration.setOverrideSourceProvider(
                unitDescriptor.getOverrideSourceProvider());
        unitConfiguration.setClassLoader(unitDescriptor.getClassLoader());
        unitConfiguration.setRunOnlyOnSourceChange(
                unitDescriptor.isRunOnlyOnSourceChange());
        StringBuilder name = new StringBuilder()
                .append(unitDescriptor.getPackaging().toString())
                .append(":");
        if (unitDescriptor.getProjectPaths().getConfigurationPackage() != null)
        {
            name.append(
                    unitDescriptor.getProjectPaths().getConfigurationPackage());
        }
        else
        {
            name.append(
                    unitDescriptor.getProjectPaths().getConfigurationPath());
        }
        unitConfiguration.setTemplateSetName(name.toString());

        ConfigurationProvider configurationProvider
        = createConfigurationProvider(unitDescriptor);
        readControlConfiguration(
                unitConfiguration,
                unitDescriptor,
                configurationHandlers,
                configurationProvider);

        {
            if (log.isDebugEnabled())
            {
                log.debug("Start reading outlet configuration");
            }
            OutletConfiguration outletConfiguration
                = new OutletConfigurationXmlParser()
            .readOutletConfiguration(
                    configurationProvider,
                    configurationHandlers,
                    unitDescriptor);
            unitConfiguration.setOutletConfiguration(outletConfiguration);
        }

        if (log.isDebugEnabled())
        {
            log.debug("Sucessfully read unitConfiguration for unit "
                    + getUnitDisplayName(unitDescriptor));
        }


        if (unitDescriptor.getInheritsFrom() == null)
        {
            OutletConfiguration outletConfiguration
            = unitConfiguration.getOutletConfiguration();
            outletConfiguration.resolveMergepointMappings();
            return unitConfiguration;
        }
        UnitConfiguration inherited = read(
                unitDescriptor.getInheritsFrom(),
                configurationHandlers);

        // Loglevel of inherited may have changed during reading.
        // Restore this loglevel.
        unitConfiguration.getLoglevel().apply();

        mergeInheritedOutletConfiguration(
                unitDescriptor,
                unitConfiguration,
                inherited);
        mergeInheritedOptionConfiguration(unitConfiguration, inherited);
        mergeInheritedOutputFiles(unitConfiguration, inherited);
        mergeInheritedEntityRefernces(unitConfiguration, inherited);
        // target directory cannot be null and thus the current target directory
        // always overrides the inherited target directory.

        return unitConfiguration;
    }

    /**
     * Merges the inherited output files with the output files
     * of the current unit configuration.
     *
     * @param unitConfiguration the current unit configuration
     * @param inheritedConfiguration the inherited unit configuration
     *        which option configuration should be merged.
     */
    private void mergeInheritedOutputFiles(
            final UnitConfiguration unitConfiguration,
            final UnitConfiguration inheritedConfiguration)
    {
        List<Output> outputFiles = new ArrayList<>();
        // inherited Files are generated first.
        Set<QualifiedName> qualifiedNames = new HashSet<>();
        for (Output output : inheritedConfiguration.getOutputList())
        {
            outputFiles.add(output);
            qualifiedNames.add(output.getName());
        }
        for (Output output : unitConfiguration.getOutputList())
        {
            if (qualifiedNames.contains(output.getName()))
            {
                Iterator<Output> addedOutputIt = outputFiles.iterator();
                while (addedOutputIt.hasNext())
                {
                    if (addedOutputIt.next().getName().equals(output.getName()))
                    {
                        addedOutputIt.remove();
                        log.info("Output with name " + output.getName()
                        + " is overridden in child and is replaced.");
                        break;
                    }
                }
            }
            outputFiles.add(output);
        }
        unitConfiguration.setOutputList(outputFiles);
    }

    /**
     * Merges the inherited entity references with the entity references
     * of the current unit configuration.
     *
     * @param unitConfiguration the current unit configuration
     * @param inheritedConfiguration the inherited unit configuration
     *        which entity references should be merged.
     */
    private void mergeInheritedEntityRefernces(
            final UnitConfiguration unitConfiguration,
            final UnitConfiguration inheritedConfiguration)
    {
        EntityReferences entityReferences
        = unitConfiguration.getEntityReferences();
        Map<String, byte[]> inheritedReferences
        = inheritedConfiguration.getEntityReferences()
        .getEntityReferences();
        for (Map.Entry<String, byte[]> inheritedReference
                : inheritedReferences.entrySet())
        {
            String systemId = inheritedReference.getKey();
            if (!entityReferences.containsSystemId(systemId))
            {
                entityReferences.addEntityReference(
                        systemId,
                        inheritedReference.getValue());
                log.debug("entityReferences with system id "
                        + systemId
                        + " is inherited from the parent.");
            }
            else
            {
                log.debug("entityReferences with system id "
                        + systemId
                        + " is overidden in the child.");
            }
        }
    }

    /**
     * Merges an inherited option configuration into the option
     * configuration of the current unit configuration.
     *
     * @param unitConfiguration the current unit configuration
     * @param inheritedConfiguration the inherited unit configuration
     *        which option configuration should be merged.
     */
    private void mergeInheritedOptionConfiguration(
            final UnitConfiguration unitConfiguration,
            final UnitConfiguration inheritedConfiguration)
    {
        Options options = unitConfiguration.getOptions();
        Options inheritedOptions = inheritedConfiguration.getOptions();
        for (Map.Entry<QualifiedName, Option> entry
                : inheritedOptions.getGlobalScope().entrySet())
        {
            QualifiedName optionName = entry.getKey();
            Option option = entry.getValue();
            if (!options.getGlobalScope().containsKey(optionName))
            {
                options.setGlobalOption(option);
            }
        }
    }

    /**
     * Merges an inherited outlet configuration into the outlet
     * configuration of the current unit configuration.
     *
     *@param unitDescriptor the descriptor of the current generation unit.
     * @param unitConfiguration the current unit configuration.
     * @param inheritedConfiguration the inherited unit configuration
     *        which outlets should be merged.
     *
     * @throws ConfigurationException will not normally happen.
     */
    private void mergeInheritedOutletConfiguration(
            final UnitDescriptor unitDescriptor,
            final UnitConfiguration unitConfiguration,
            final UnitConfiguration inheritedConfiguration)
                    throws ConfigurationException
    {
        OutletConfiguration outletConfiguration
        = unitConfiguration.getOutletConfiguration();

        OutletConfiguration inheritedOutletConfiguration
        = inheritedConfiguration.getOutletConfiguration();
        Map<QualifiedName, Outlet> inheritedOutlets
        = inheritedOutletConfiguration.getOutlets();

        for (Map.Entry<QualifiedName, Outlet> entry
                : inheritedOutlets.entrySet())
        {
            if (!outletConfiguration.outletExists(entry.getKey()))
            {
                outletConfiguration.addOutlet(
                        entry.getValue(),
                        unitDescriptor);
            }
        }
        outletConfiguration.resolveMergepointMappings();
    }

    /**
     * Reads the control configuration and stores it in the unitConfiguration.
     *
     * @param unitConfiguration the configuration into which the control
     *        configuration should be read.
     * @param unitDescriptor The unit descriptor for the generation unit to
     *        use.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     * @param configurationProvider The provider for accessing the
     *        configuration files.
     *
     * @throws ConfigurationException if an error occurs while reading
     *         the configuration.
     */
    private void readControlConfiguration(
            final UnitConfiguration unitConfiguration,
            final UnitDescriptor unitDescriptor,
            final ConfigurationHandlers configurationHandlers,
            final ConfigurationProvider configurationProvider)
                    throws ConfigurationException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Start reading control configuration");
        }
        ControlConfiguration controlConfiguration
            = new ControlConfigurationXmlParser()
        .readControllerConfiguration(
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
        if (unitDescriptor.getLoglevel() == null)
        {
            unitConfiguration.setLoglevel(controlConfiguration.getLoglevel());
            controlConfiguration.getLoglevel().apply();
        }
        unitConfiguration.setOutputList(
                controlConfiguration.getOutputFiles());

        {
            if (log.isDebugEnabled())
            {
                log.debug("Start reading options");
            }
            List<OptionsConfiguration> optionConfigurations
                = new ArrayList<>();
            optionConfigurations.addAll(
                    controlConfiguration.getOptionsConfigurations());
            if (unitDescriptor.getOverrideOptions() != null)
            {
                optionConfigurations.add(unitDescriptor.getOverrideOptions());
            }

            Options options = new Options();
            for (OptionsConfiguration optionConfiguration
                    : optionConfigurations)
            {
                options.addGlobalOptions(
                        optionConfiguration.getOptions(
                                configurationProvider));
            }
            unitConfiguration.setOptions(options);
            {
                log.debug("End reading options");
            }
        }
        unitConfiguration.setEntityReferences(
                controlConfiguration.getEntityReferences());
        unitConfiguration.setDefaultOutputEncoding(
                unitDescriptor.getDefaultOutputEncoding());
        if (log.isDebugEnabled())
        {
            log.debug("Control configuration successfully read.");
        }
    }

    /**
     * Creates the matching configuration provider for the packaging type
     * of the unit descriptor.
     *
     * @param unitDescriptor the unit descriptor to create the
     *        configuration provider for, not null.
     *
     * @return a configuration provider for the unit descriptor, not null.
     *
     * @throws ConfigurationException if an unknown packaging is encountered
     *         or if a jar file cannot be accessed.
     */
    private ConfigurationProvider createConfigurationProvider(
            final UnitDescriptor unitDescriptor)
                    throws ConfigurationException
    {
        ConfigurationProvider configurationProvider;
        if (UnitDescriptor.Packaging.DIRECTORY == unitDescriptor.getPackaging())
        {
            configurationProvider = new DirectoryConfigurationProvider(
                    unitDescriptor.getProjectPaths(),
                    unitDescriptor.getConfigurationPaths());
        }
        else if (UnitDescriptor.Packaging.JAR == unitDescriptor.getPackaging())
        {
            configurationProvider = new JarConfigurationProvider(
                    unitDescriptor.getProjectPaths(),
                    unitDescriptor.getConfigurationPaths());
        }
        else if (UnitDescriptor.Packaging.CLASSPATH
                == unitDescriptor.getPackaging())
        {
            configurationProvider = new ClasspathConfigurationProvider(
                    unitDescriptor);
        }
        else
        {
            throw new ConfigurationException("Unknown Unit type "
                    + unitDescriptor.getPackaging());
        }
        return configurationProvider;
    }

    private String getUnitDisplayName(final UnitDescriptor unitDescriptor)
    {
        if (Packaging.CLASSPATH == unitDescriptor.getPackaging())
        {
            return unitDescriptor.getProjectPaths().getConfigurationPackage();
        }
        else
        {
            return unitDescriptor.getProjectPaths().getConfigurationPath()
                    .toString();
        }
    }
}
