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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.generator.configuration.controller.Loglevel;
import org.apache.torque.generator.configuration.controller.Output;
import org.apache.torque.generator.configuration.outlet.OutletConfiguration;
import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.option.Options;
import org.apache.torque.generator.source.SourceProvider;

/**
 * Contains all information to run a generation unit.
 * Provides state checking, i.e. getters can only be called
 * after all setters has been called.
 */
public class UnitConfiguration
{
    /** The list of all output activities of the generation unit. */
    private List<Output> outputList = new ArrayList<>();

    /** All options for the generation unit. */
    private Options options;

    /** Overrides the source provider defined in the control file. */
    private SourceProvider overrideSourceProvider;

    /** Whether the overrideSourceProviderInitialized was initialized. */
    private boolean overrideSourceProviderInitialized = false;

    /** The configuration for the available outlets. */
    private OutletConfiguration outletConfiguration;

    /** The entity references for resolving schema files for XML sources. */
    private EntityReferences entityReferences;

    /** The configuration handlers which are used to read this configuration. */
    private ConfigurationHandlers configurationHandlers;

    /**
     * The base output directories for the generated files,
     * keyed by the output directory key.
     */
    private final Map<String, File> outputDirectoryMap = new HashMap<>();

    /**
     * A work directory where the Torque generator can store internal files.
     * These files should remain intact between generation runs.
     */
    private File workDirectory;

    /**
     * A cache directory where the Torque generator can store internal files.
     * These files can be removed between generation runs, however removal
     * may lead to a longer generation.
     */
    private File cacheDirectory;

    /** The Loglevel for generation. */
    private Loglevel loglevel = Loglevel.INFO;

    /** Whether to add debug information to the output. */
    private boolean addDebuggingInfoToOutput = false;

    /**
     * The output encoding if no specific encoding has been set in the output;
     * null for the default platform encoding.
     */
    private String defaultOutputEncoding = null;

    /**
     * The class loader to use by the generator when accessing the templates
     * or classes defined within the templates.
     * If null, the class loader of the torque generator classes is used.
     */
    private ClassLoader classLoader;

    /**
     * Set to true if only the sources should be processed which have
     * changed since last generation.
     */
    private boolean runOnlyOnSourceChange = false;

    /**
     * The name of the template set. Is determined from the packaging
     * and the location of the control file.
     */
    private String templateSetName;

    /**
     * Returns the configuration of the outlets in this generation unit.
     *
     * @return the outlet configuration, not null.
     *
     * @throws IllegalStateException if outletConfiguration was not set.
     */
    public OutletConfiguration getOutletConfiguration()
    {
        if (outletConfiguration == null)
        {
            throw new IllegalStateException(
                    "outletConfiguration is not initialized");
        }
        return outletConfiguration;
    }

    /**
     * Sets the outlet configuration of the associated configuration unit.
     *
     * @param outletConfiguration the outlet configuration, not null.
     *
     * @throws NullPointerException if outletConfiguration is null.
     */
    public void setOutletConfiguration(
            final OutletConfiguration outletConfiguration)
    {
        if (outletConfiguration == null)
        {
            throw new NullPointerException(
                    "outletConfiguration cannot be null");
        }
        this.outletConfiguration = outletConfiguration;
    }

    /**
     * Returns the options of the associated configuration unit.
     *
     * @return the options, not null.
     *
     * @throws IllegalStateException if options were not yet set.
     */
    public Options getOptions()
    {
        if (options == null)
        {
            throw new IllegalStateException(
                    "options are not initialized");
        }
        return options;
    }

    /**
     * Sets the options of the associated configuration unit.
     *
     * @param options the options, not null.
     *
     * @throws NullPointerException if options is null.
     */
    public void setOptions(final Options options)
    {
        if (options == null)
        {
            throw new NullPointerException(
                    "options cannot be null");
        }
        this.options = options;
    }

    /**
     * Returns the output directory for a given output directory key.
     *
     * @param outputDirKey the key which output directory should be returned.
     *        Null represents the default output directory and is always
     *        mapped to a non-null value.
     *
     * @return the output directory for the key, not null.
     *
     * @throws IllegalStateException if the default output directory
     *         was not yet set.
     */
    public File getOutputDirectory(final String outputDirKey)
    {
        File result = outputDirectoryMap.get(outputDirKey);
        if (result == null)
        {
            throw new IllegalStateException(
                    "The output directory for the key " + outputDirKey
                    + "is not set");
        }
        return result;
    }

    /**
     * Returns the output directory map which contains the mapping
     * from output directory key to output directory.
     *
     * @return the immutable output directory map, not null, contains at least
     *         a mapping for the key null.
     *
     * @throws IllegalStateException if no mapping is contained
     *         for the key null.
     */
    public Map<String, File> getOutputDirectoryMap()
    {
        if (outputDirectoryMap.get(null) == null)
        {
            throw new IllegalStateException(
                    "outputDirectoryMap does not contain a mapping"
                            + " for the key null");
        }
        return Collections.unmodifiableMap(outputDirectoryMap);
    }

    /**
     * Sets the output directory map which contains the mapping
     * from output directory key to output directory.
     *
     * @param outputDirectoryMap the new output directory map,
     *        must contain at least a mapping for the key null.
     *
     * @throws NullPointerException if outputDirectoryMap is null.
     * @throws IllegalStateException if the target directory was not yet set.
     */
    public void setOutputDirectoryMap(final Map<String, File> outputDirectoryMap)
    {
        if (outputDirectoryMap == null)
        {
            throw new NullPointerException(
                    "outputDirectoryMap may not be null");
        }
        if (outputDirectoryMap.get(null) == null)
        {
            throw new IllegalArgumentException(
                    "outputDirectoryMap(null) may not be null");
        }
        this.outputDirectoryMap.clear();
        this.outputDirectoryMap.putAll(outputDirectoryMap);
    }

    /**
     * Returns the work directory where the generator can store internal files.
     *
     * @return the directory where the generator can store internal files,
     *         not null.
     *
     * @throws IllegalStateException if the target directory was not yet set.
     */
    public File getWorkDirectory()
    {
        if (workDirectory == null)
        {
            throw new IllegalStateException(
                    "workDirectory is not initialized");
        }
        return workDirectory;
    }

    /**
     * Sets the work directory where the generator can store internal files.
     *
     * @param workDirectory the work directory, not null.
     *
     * @throws NullPointerException if workDirectory is null.
     */
    public void setWorkDirectory(final File workDirectory)
    {
        if (workDirectory == null)
        {
            throw new NullPointerException("workDirectory cannot be null");
        }
        this.workDirectory = workDirectory;
    }

    /**
     * Returns the cache directory where the generator can store internal files.
     *
     * @return the directory where the generator can store internal files,
     *         not null.
     *
     * @throws IllegalStateException if the target directory was not yet set.
     */
    public File getCacheDirectory()
    {
        if (cacheDirectory == null)
        {
            throw new IllegalStateException(
                    "cacheDirectory is not initialized");
        }
        return cacheDirectory;
    }

    /**
     * Sets the cache directory where the generator can store internal files.
     *
     * @param cacheDirectory the work directory, not null.
     *
     * @throws NullPointerException if workDirectory is null.
     */
    public void setCacheDirectory(final File cacheDirectory)
    {
        if (cacheDirectory == null)
        {
            throw new NullPointerException("cacheDirectory cannot be null");
        }
        this.cacheDirectory = cacheDirectory;
    }

    /**
     * Sets the output activities of the associated configuration unit.
     *
     * @param outputList the output activities, not null.
     *
     * @throws NullPointerException if outputFiles is null.
     */
    public void setOutputList(final List<Output> outputList)
    {
        if (outputList == null)
        {
            throw new NullPointerException(
                    "outputList cannot be null");
        }
        this.outputList = outputList;
    }

    /**
     * Returns the list of output definitions of the associated configuration
     * unit.
     *
     * @return the output definitions, not null.
     *
     * @throws IllegalStateException if the output definitions were not yet set.
     */
    public List<Output> getOutputList()
    {
        if (outputList == null)
        {
            throw new IllegalStateException(
                    "outputFiles are not initialized");
        }
        return Collections.unmodifiableList(outputList);
    }

    /**
     * Returns the Loglevel during generation.
     *
     * @return the Loglevel, not null.
     *
     * @throws IllegalStateException if the loglevel is not yet set.
     */
    public Loglevel getLoglevel()
    {
        if (loglevel == null)
        {
            throw new IllegalStateException(
                    "loglevel is not initialized");
        }
        return loglevel;
    }

    /**
     * Sets the Loglevel during generation.
     *
     * @param loglevel the Loglevel, not null.
     *
     * @throws NullPointerException if loglevel is set to null.
     */
    public void setLoglevel(final Loglevel loglevel)
    {
        if (loglevel == null)
        {
            throw new NullPointerException("loglevel is null");
        }
        this.loglevel = loglevel;
    }

    /**
     * Returns whether to add debug information to the output.
     *
     * @return true if debug information should be added, false otherwise.
     */
    public boolean isAddDebuggingInfoToOutput()
    {
        return addDebuggingInfoToOutput;
    }

    /**
     * Sets whether to add debug information to the output.
     *
     * @param addDebuggingInfoToOutput true if debug information should be
     *        added, false otherwise.
     */
    public void setAddDebuggingInfoToOutput(final boolean addDebuggingInfoToOutput)
    {
        this.addDebuggingInfoToOutput = addDebuggingInfoToOutput;
    }

    /**
     * Returns the configuration handlers used to parse the configuration
     * of this generation unit.
     *
     * @return the configuration handlers, not null.
     *
     * @throws IllegalStateException if configurationHandlers was not set.
     */
    public ConfigurationHandlers getConfigurationHandlers()
    {
        if (configurationHandlers == null)
        {
            throw new IllegalStateException(
                    "configurationHandlers is not initialized");
        }
        return configurationHandlers;
    }

    /**
     * Sets the configuration handlers used to parse the configuration
     * of this generation unit.
     *
     * @param configurationHandlers the configuration handlers, not null.
     *
     * @throws NullPointerException if configurationHandlers is null.
     */
    public void setConfigurationHandlers(
            final ConfigurationHandlers configurationHandlers)
    {
        if (configurationHandlers == null)
        {
            throw new NullPointerException(
                    "configurationHandlers cannot be null");
        }
        this.configurationHandlers = configurationHandlers;
    }

    /**
     * Returns the source provider which overrides the source provider defined
     * in the control file.
     *
     * @return the overriding source provider, or null to use the
     *         source provider defined in the control file.
     *
     * @throws NullPointerException if overrideSourceFileset was not yet set.
     */
    public SourceProvider getOverrideSourceProvider()
    {
        if (!overrideSourceProviderInitialized)
        {
            throw new IllegalStateException(
                    "overrideSourceProvider is not initialized");
        }
        return overrideSourceProvider;
    }

    /**
     * Sets the source provider which overrides the source provider defined
     * in the control file.
     *
     * @param overrideSourceProvider the override source provider, or null
     *        to use the source provider defined in the control file.
     */
    public void setOverrideSourceProvider(
            final SourceProvider overrideSourceProvider)
    {
        this.overrideSourceProvider = overrideSourceProvider;
        overrideSourceProviderInitialized = true;
    }

    /**
     * Returns the entityReferences of the associated configuration unit.
     *
     * @return the entityReferences, not null.
     *
     * @throws IllegalStateException if entityReferences were not yet set.
     */
    public EntityReferences getEntityReferences()
    {
        if (entityReferences == null)
        {
            throw new IllegalStateException(
                    "entityReferences are not initialized");
        }
        return entityReferences;
    }

    /**
     * Sets the entityReferences of the associated configuration unit.
     *
     * @param entityReferences the entityReferences, not null.
     *
     * @throws NullPointerException if entityReferences is null.
     */
    public void setEntityReferences(final EntityReferences entityReferences)
    {
        if (entityReferences == null)
        {
            throw new NullPointerException(
                    "entityReferences cannot be null");
        }
        this.entityReferences = entityReferences;
    }

    /**
     * Returns the output encoding if no specific encoding has been set
     * in the output.
     *
     * @return the default output encoding, null for the default
     *         platform encoding.
     */
    public String getDefaultOutputEncoding()
    {
        return defaultOutputEncoding;
    }

    /**
     * Sets the output encoding if no specific encoding has been set
     * in the output.
     *
     * @param defaultOutputEncoding the default output encoding,
     *        null for the default platform encoding.
     */
    public void setDefaultOutputEncoding(final String defaultOutputEncoding)
    {
        this.defaultOutputEncoding = defaultOutputEncoding;
    }

    /**
     * Returns the class loader to use by the generator when accessing
     * the templates or classes defined within the templates.
     *
     * @return the class loader for the templates, or null if the standard
     *         class loader of the torque generator classes is used.
     */
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * Sets the class loader to use by the generator when accessing
     * the templates or classes defined within the templates.
     *
     * @param classLoader the class loader for the templates,
     *        or null if the standard class loader
     *        of the torque generator classes should be used.
     */
    public void setClassLoader(final ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     * Returns true if only the sources should be processed which have
     * changed since last generation.
     *
     * @return false if the controller should be run irrespective of changes
     *         in the source files, true if the controller should be run for
     *         source files which have changed during last generation.
     */
    public boolean isRunOnlyOnSourceChange()
    {
        return runOnlyOnSourceChange;
    }

    /**
     * Sets whether only the sources should be processed which have
     * changed since last generation.
     *
     * @param runOnlyOnSourceChange false if the controller should be run
     *        irrespective of changes in the source files,
     *        true if the controller should be run for source files
     *        which have changed during last generation.
     */
    public void setRunOnlyOnSourceChange(final boolean runOnlyOnSourceChange)
    {
        this.runOnlyOnSourceChange = runOnlyOnSourceChange;
    }

    /**
     * Returns the name of the template set.
     *
     * @return the name of the template set.
     */
    public String getTemplateSetName()
    {
        return templateSetName;
    }

    /**
     * Sets the name of the template set.
     *
     * @param name the name of the template set.
     */
    public void setTemplateSetName(final String name)
    {
        this.templateSetName = name;
    }

    /**
     * Checks whether the unit configuration is fully initialized.
     *
     * @return true if the unit configuration is fully initialized,
     *          false otherwise.
     */
    public boolean isInit()
    {
        if (outputList == null
                || options == null
                || outletConfiguration == null
                || outputDirectoryMap.get(null) == null
                || loglevel == null
                || configurationHandlers == null
                || entityReferences == null
                || !overrideSourceProviderInitialized
                || templateSetName == null)
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(outputFiles=")
        .append(outputList.toString())
        .append(", options=")
        .append(options)
        .append(", outletConfiguration=")
        .append(outletConfiguration)
        .append(", outputDirectoryMap=")
        .append(outputDirectoryMap)
        .append(", loglevel=")
        .append(loglevel)
        .append(", name=")
        .append(templateSetName)
        .append(")");
        return result.toString();
    }
}
