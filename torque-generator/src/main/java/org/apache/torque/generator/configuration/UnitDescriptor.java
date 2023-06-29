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

import org.apache.torque.generator.configuration.controller.Loglevel;
import org.apache.torque.generator.configuration.option.OptionsConfiguration;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.configuration.paths.TorqueGeneratorPaths;
import org.apache.torque.generator.source.SourceProvider;

/**
 * Contains all necessary information about a generation unit.
 */
public class UnitDescriptor
{
    /**
     * Possible packaging forms of a unit of generation.
     */
    public enum Packaging
    {
        /** the generation unit is provided as a jar file .*/
        JAR,
        /** the generation unit is provided in a local directory. */
        DIRECTORY,
        /** the generation unit is provided in the classpath. */
        CLASSPATH
    }

    /**
     * The packaging of the generation unit.
     */
    private final Packaging packaging;

    /**
     * The paths the Torque generator must know about the surrounding project.
     */
    private final ProjectPaths projectPaths;

    /**
     * How the Torque generator's configuration is organized internally.
     */
    private final TorqueGeneratorPaths configurationPaths;

    /**
     * The parent of this generation unit, or null if it has no parent.
     */
    private UnitDescriptor inheritsFrom;

    /**  Overrides the source defined in the control file. */
    private SourceProvider overrideSourceProvider;

    /** Options to override the settings in the project Directory. */
    private OptionsConfiguration overrideOptions;

    /**
     * The Loglevel to override the loglevel in the Torque
     * generator configuration.
     */
    private Loglevel loglevel;

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
     * Constructor without inheritance, override options, overrideSourceFileset,
     * loglevel and addDebuggingInfoToOutput.
     *
     * @param packaging The packaging of the generation unit, not null.
     * @param projectPaths The paths the Torque generator must know about
     *        the surrounding project, not null.
     * @param configurationPaths The paths within the configuration
     *        of the configuration unit, not null.
     */
    public UnitDescriptor(
            final Packaging packaging,
            final ProjectPaths projectPaths,
            final TorqueGeneratorPaths configurationPaths)
    {
        if (packaging == null)
        {
            throw new NullPointerException("packaging must not be null");
        }
        if (projectPaths == null)
        {
            throw new NullPointerException(
                    "projectPaths must not be null");
        }
        if (configurationPaths == null)
        {
            throw new NullPointerException(
                    "configurationPaths must not be null");
        }

        this.packaging = packaging;
        this.projectPaths = projectPaths;
        this.configurationPaths = configurationPaths;
    }

    /**
     * Returns the packaging of the unit of generation.
     *
     * @return the packaging of the unit of generation, not null.
     */
    public Packaging getPackaging()
    {
        return packaging;
    }

    /**
     * Returns the paths which the Torque generator must know about the
     * surrounding project.
     *
     * @return the paths of the surrounding project, not null.
     */
    public ProjectPaths getProjectPaths()
    {
        return projectPaths;
    }

    /**
     * Returns the paths in the configuration of this generation unit.
     * @return the paths in the configuration of this generation unit,
     *          not null.
     */
    public TorqueGeneratorPaths getConfigurationPaths()
    {
        return configurationPaths;
    }

    /**
     * Returns the descriptor of the generation unit from which this generation
     * unit inherits, or null if this generation unit does not inherit from
     * another generation unit.
     *
     * @return the parents unit descriptor, or null if no parent exists.
     */
    public UnitDescriptor getInheritsFrom()
    {
        return inheritsFrom;
    }

    /**
     * Sets the descriptor of the generation unit from which this generation
     * unit inherits.
     *
     * @param inheritsFrom the parents unit descriptor,
     *        or null if no parent exists.
     */
    public void setInheritsFrom(final UnitDescriptor inheritsFrom)
    {
        this.inheritsFrom = inheritsFrom;
    }

    /**
     * Returns the source provider overriding the source defined in the
     * control file, or null if the control file definition
     * is not overridden.
     *
     * @return the overriding source provider, or null.
     */
    public SourceProvider getOverrideSourceProvider()
    {
        return overrideSourceProvider;
    }

    /**
     * Sets the source provider overriding the source defined in the
     * control file.
     *
     * @param overrideSourceProvider the overriding source provider,
     *        or null if the control file definition is not overridden.
     */
    public void setOverrideSourceProvider(final SourceProvider overrideSourceProvider)
    {
        this.overrideSourceProvider = overrideSourceProvider;
    }

    /**
     * Returns the configuration of the overriding options, if any.
     *
     * @return the configuration of the overriding options, or null.
     */
    public OptionsConfiguration getOverrideOptions()
    {
        return overrideOptions;
    }

    /**
     * Sets the configuration of the overriding options, if any.
     *
     * @param overrideOptions the configuration of the overriding options,
     *        or null.
     */
    public void setOverrideOptions(final OptionsConfiguration overrideOptions)
    {
        this.overrideOptions = overrideOptions;
    }

    /**
     * Returns the log level overriding the loglevel defined in the
     * configuration unit.
     *
     * @return the log level, or null.
     */
    public Loglevel getLoglevel()
    {
        return loglevel;
    }

    /**
     * Sets the log level overriding the loglevel defined in the
     * configuration unit.
     *
     * @param loglevel the log level, or null.
     */
    public void setLoglevel(final Loglevel loglevel)
    {
        this.loglevel = loglevel;
    }

    /**
     * Returns whether debugging info should be added to the output.
     *
     * @return true if debugging info should be added to the output,
     *         false if not.
     */
    public boolean isAddDebuggingInfoToOutput()
    {
        return addDebuggingInfoToOutput;
    }

    /**
     * Sets whether debugging info should be added to the output.
     *
     * @param addDebuggingInfoToOutput true if debugging info should be added
     *        to the output, false if not.
     */
    public void setAddDebuggingInfoToOutput(final boolean addDebuggingInfoToOutput)
    {
        this.addDebuggingInfoToOutput = addDebuggingInfoToOutput;
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
     * Sets the output encoding which is used if no specific encoding
     * has been set in the output.
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
}
