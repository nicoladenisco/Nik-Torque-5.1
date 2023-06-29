package org.apache.torque.ant.task;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.UnitDescriptor.Packaging;
import org.apache.torque.generator.configuration.controller.Loglevel;
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration;
import org.apache.torque.generator.configuration.option.OptionsConfiguration;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.configuration.paths.Maven2JarProjectPaths;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.control.Controller;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.source.stream.FileSourceProvider;

/**
 * Executes a unit of generation within the torque generator.
 * <pre><code>
 *  ant goal generate
 * </code></pre>
 *
 * $Id: TorqueGeneratorTask.java 1850971 2019-01-10 18:14:54Z painter $
 *
 */
public class TorqueGeneratorTask extends Task
{
    /**
     * The packaging type of the generation unit, either "directory" , "jar"
     * or "classpath". Default is "directory".
     */
    private String packaging = "directory";

    /**
     * The root directory of the project.
     * Has no effect if packaging is "classpath".
     * Default is ".".
     */
    private File projectRootDir = new File(".");

    /**
     * The configuration directory of the generation unit.
     * Has no effect if packaging is "classpath".
     */
    private File configDir;

    /**
     * The configuration package of the generation unit.
     * Has only effect if packaging is "classpath".
     */
    private String configPackage;

    /**
     * The directory where the source files reside.
     */
    private File sourceDir;

    /**
     * Include patterns for the source files.
     * If set, the include and exclude patterns from the templates
     * are overridden.
     * If not set, then the include patterns from the templates are used.
     * The patterns are case sensitive, wildcards are * and ?.
     */
    private Set<String> sourceIncludes;

    /**
     * Exclude patterns for the source files.
     * If set, the include and exclude patterns from the templates
     * are overridden.
     * If not set, then the include patterns from the templates are used.
     * The patterns are case sensitive, wildcards are * and ?.
     */
    private Set<String> sourceExcludes;

    /**
     * The target directory for files which are generated each time anew.
     * Default is "target/generated-sources"
     */
    private File defaultOutputDir = new File("target/generated-sources");

    /**
     * The target directories for files which are not generated each time anew.
     * Default is modifiable -> "./src/main/generated-java"
     */
    private Map<String, File> outputDirMap = new HashMap<>();

    /**
     * The filename of the jar file of the generation unit.
     * Has only effect if packaging is "jar".
     */
    private String jarFile;

    /**
     * The config directory of the project overriding the settings.
     * If set, the settings of this directory are used as "child"
     * and the "normal" settings are used as "parent".
     */
    private File overrideConfigDir;

    /**
     * The Loglevel to use in the generation process. Must be one of
     * trace, debug, info, warn or error.
     * If not set, the log level defined in the generation unit is used.
     */
    private String loglevel;

    /**
     * Whether the generator should only run if one of the source files
     * changes.
     */
    private boolean runOnlyOnSourceChange = false;

    /**
     * Whether to add debug information to the output.
     */
    private boolean addDebuggingInfoToOutput = false;

    /**
     * The encoding which should be used for the files which do not have an
     * output encoding set in the templates.
     */
    private String defaultOutputEncoding;

    /**
     * Whether all source files should be combined into one source tree.
     * If false, each source file will be read in its own source tree
     * and start a new generation run.
     * If true, a single source tree with the following structure will be
     * built from all source files:
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
     * If not set, the settings from the templates will be used.
     */
    private Boolean combineFiles;

    /** The list of options for the generation task. */
    private final List<Option> options = new ArrayList<>();

    /**
     * Creates a new option and adds it to the list of options.
     *
     * @return the newly created option.
     */
    public Option createOption()
    {
        Option option = new Option();
        options.add(option);
        return option;
    }

    /**
     * Runs the generation.
     */
    @Override
    public void execute() throws BuildException
    {
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();

        UnitDescriptor.Packaging packaging;
        if ("jar".equals(this.packaging))
        {
            packaging = UnitDescriptor.Packaging.JAR;
        }
        else if ("directory".equals(this.packaging))
        {
            packaging = UnitDescriptor.Packaging.DIRECTORY;
        }
        else if ("classpath".equals(this.packaging))
        {
            packaging = UnitDescriptor.Packaging.CLASSPATH;
        }
        else
        {
            throw new IllegalArgumentException(
                    "Unknown packaging " + this.packaging
                    + ", must be jar, directory or classpath");
        }
        log("Packaging is " + packaging, Project.MSG_DEBUG);

        ProjectPaths defaultProjectPaths;
        if (UnitDescriptor.Packaging.JAR == packaging)
        {
            defaultProjectPaths
                = new Maven2JarProjectPaths(projectRootDir, jarFile);
        }
        else if (UnitDescriptor.Packaging.DIRECTORY == packaging)
        {
            defaultProjectPaths
                = new Maven2DirectoryProjectPaths(projectRootDir);
        }
        else if (UnitDescriptor.Packaging.CLASSPATH == packaging)
        {
            defaultProjectPaths
                = new Maven2DirectoryProjectPaths(projectRootDir);
        }
        else
        {
            throw new IllegalStateException("Unknown packaging" + packaging);
        }

        CustomProjectPaths projectPaths
            = new CustomProjectPaths(defaultProjectPaths);

        if (UnitDescriptor.Packaging.CLASSPATH == packaging)
        {
            if (configPackage == null)
            {
                throw new BuildException(
                        "configPackage must be set for packaging =\"classpath\"");
            }
            projectPaths.setConfigurationPackage(configPackage);
            projectPaths.setConfigurationDir(null);
        }
        else
        {
            if (configDir != null)
            {
                projectPaths.setConfigurationDir(configDir);
                log("Setting config dir to " + configDir.toString(),
                        Project.MSG_DEBUG);
            }
        }

        if (sourceDir != null)
        {
            projectPaths.setSourceDir(sourceDir);
            log("Setting source dir to " + sourceDir.toString(),
                    Project.MSG_DEBUG);
        }

        FileSourceProvider fileSourceProvider = null;
        if (sourceIncludes != null || sourceExcludes != null)
        {
            Fileset sourceFileset
                = new Fileset(
                    projectPaths.getDefaultSourcePath(),
                    sourceIncludes,
                    sourceExcludes);
            log("Setting source includes to " + sourceIncludes,
                    Project.MSG_DEBUG);
            log("Setting source excludes to " + sourceExcludes,
                    Project.MSG_DEBUG);
            try
            {
                fileSourceProvider = new FileSourceProvider(
                        null,
                        sourceFileset,
                        combineFiles);
            }
            catch (ConfigurationException e)
            {
                throw new BuildException(
                        "The source provider cannot be instantiated", e);
            }
        }

        if (defaultOutputDir != null)
        {
            projectPaths.setOutputDirectory(null, defaultOutputDir);
            log("Setting defaultOutputDir to "
                    + defaultOutputDir.getAbsolutePath(),
                    Project.MSG_DEBUG);
        }
        if (outputDirMap != null)
        {
            for (Map.Entry<String, File> dirEntry : outputDirMap.entrySet())
            {
                projectPaths.setOutputDirectory(
                        dirEntry.getKey(),
                        dirEntry.getValue());
                log("Setting output directory with key " + dirEntry.getKey()
                + " to "
                + dirEntry.getValue(),
                Project.MSG_DEBUG);
            }
        }
        log("ProjectPaths = " + projectPaths, Project.MSG_DEBUG);

        OptionsConfiguration optionConfiguration = null;
        if (!options.isEmpty())
        {
            Map<String, String> optionsMap = new HashMap<>();
            for (Option option : options)
            {
                optionsMap.put(option.getKey(), option.getValue());
            }
            optionConfiguration = new MapOptionsConfiguration(optionsMap);
        }
        Loglevel convertedLoglevel = null;
        if (this.loglevel != null)
        {
            convertedLoglevel = Loglevel.getByKey(loglevel);
        }
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                packaging,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideSourceProvider(fileSourceProvider);
        unitDescriptor.setOverrideOptions(optionConfiguration);
        unitDescriptor.setLoglevel(convertedLoglevel);
        unitDescriptor.setRunOnlyOnSourceChange(runOnlyOnSourceChange);
        unitDescriptor.setAddDebuggingInfoToOutput(addDebuggingInfoToOutput);
        unitDescriptor.setDefaultOutputEncoding(defaultOutputEncoding);
        log("unit descriptor created", Project.MSG_DEBUG);
        if (overrideConfigDir != null)
        {
            CustomProjectPaths childProjectPaths
                = new CustomProjectPaths(projectPaths);
            childProjectPaths.setConfigurationDir(overrideConfigDir);

            UnitDescriptor parentUnitDescriptor = new UnitDescriptor(
                    Packaging.DIRECTORY,
                    childProjectPaths,
                    new DefaultTorqueGeneratorPaths());
            parentUnitDescriptor.setInheritsFrom(unitDescriptor);
            parentUnitDescriptor.setOverrideSourceProvider(fileSourceProvider);
            parentUnitDescriptor.setOverrideOptions(optionConfiguration);
            parentUnitDescriptor.setLoglevel(convertedLoglevel);
            parentUnitDescriptor.setDefaultOutputEncoding(
                    defaultOutputEncoding);
            log("child unit descriptor created",Project.MSG_DEBUG);
            unitDescriptor = parentUnitDescriptor;
        }
        unitDescriptors.add(unitDescriptor);
        try
        {
            log("Generation started", Project.MSG_DEBUG);
            controller.run(unitDescriptors);
            log("Generation successful", Project.MSG_INFO);
        }
        catch (Exception e)
        {
            log("Error during generation", e, Project.MSG_ERR);
            throw new BuildException(e.getMessage());
        }
    }

    /**
     * Sets the packaging.
     *
     * @param packaging the packaging, either "jar" or "directory"
     */
    public void setPackaging(final String packaging)
    {
        this.packaging = packaging;
    }

    /**
     * Sets the root directory of the project.
     *
     * @param projectRootDir the project root Directory.
     */
    public void setProjectRootDir(final File projectRootDir)
    {
        this.projectRootDir = projectRootDir;
    }

    public void setConfigDir(final File configDir)
    {
        this.configDir = configDir;
    }

    public void setConfigPackage(final String configPackage)
    {
        this.configPackage = configPackage;
    }

    /**
     * Sets the default output base directory for generated files.
     *
     * @param defaultOutputDir the default output directory,
     *                  or null to use the default.
     */
    public void setDefaultOutputDir(final File defaultOutputDir)
    {
        this.defaultOutputDir = defaultOutputDir;
    }

    /**
     * Sets the mapping from outputDirKey to output directories.
     * The outputDirKeys are defined in the templates you use.
     *
     * @param outputDirMap the new outputDirMap.
     */
    public void setOutputDirMap(final Map<String, File> outputDirMap)
    {
        this.outputDirMap = outputDirMap;
    }

    /**
     * The path to the jar file to use.
     *
     * @param jarFile the jar file, or null.
     */
    public void setJarFile(final String jarFile)
    {
        this.jarFile = jarFile;
    }

    /**
     * Sets the directory in which the source files are located.
     *
     * @param sourceDir the directory in which the source files are located.
     */
    public void setSourceDir(final File sourceDir)
    {
        this.sourceDir = sourceDir;
    }

    /**
     * Sets the pattern which files are included in the generation process.
     *
     * @param sourceIncludes a list containing the include patterns, or null
     *        if no include pattern should be used.
     */
    public void setSourceIncludes(final Set<String> sourceIncludes)
    {
        this.sourceIncludes = sourceIncludes;
    }

    /**
     * Sets the pattern which files are excluded in the generation process.
     *
     * @param sourceExcludes a list containing the exclude patterns, or null
     *        if no exclude pattern should be used.
     */
    public void setSourceExcludes(final Set<String> sourceExcludes)
    {
        this.sourceExcludes = sourceExcludes;
    }

    /**
     * Sets the config directory overriding the template settings.
     * If set, the settings of this directory are used as "child"
     * and the "normal" settings are used as "parent".
     *
     * @param overrideConfigDir the config directory overriding the template settings,
     *            or null if the template settings will not be overridden.
     */
    public void setOverrideConfigDir(final File overrideConfigDir)
    {
        this.overrideConfigDir = overrideConfigDir;
    }

    /**
     * Sets the Loglevel to use in the generation process.
     *
     * @param loglevel the loglevel, must be one of trace, debug, info, warn
     *        or error, or null if the loglevel defined in the templates
     *        should be used.
     */
    public void setLoglevel(final String loglevel)
    {
        this.loglevel = loglevel;
    }

    /**
     * Sets whether debugging information should be added to the output.
     *
     * @param addDebuggingInfoToOutput true if debugging information
     *        should be added to the output, false otherwise.
     */
    public void setAddDebuggingInfoToOutput(final boolean addDebuggingInfoToOutput)
    {
        this.addDebuggingInfoToOutput = addDebuggingInfoToOutput;
    }

    /**
     * Sets whether the generator should only run if one of the source files
     * changes. The default value is false.
     *
     * @param runOnlyOnSourceChange true if the generator should only run
     *        if one of the source files changes, false if it should
     *        always run irrespective of changes in the source files.
     */
    public void setRunOnlyOnSourceChange(final boolean runOnlyOnSourceChange)
    {
        this.runOnlyOnSourceChange = runOnlyOnSourceChange;
    }

    /**
     * Sets the encoding which should be used for the files which do not have
     * an output encoding set in the templates.
     *
     * @param defaultOutputEncoding the default output encoding,
     *        or null to use the generator default
     *        (the platform default encoding).
     */
    public void setDefaultOutputEncoding(final String defaultOutputEncoding)
    {
        this.defaultOutputEncoding = defaultOutputEncoding;
    }

    /**
     * Sets whether all source files should be combined into one source tree.
     * If false, each source file will be read in its own source tree
     * and start a new generation run.
     * If true, a single source tree with the following structure will be
     * built from all source files:
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
     * If not set, the settings from the templates will be used.
     *
     * @param combineFiles whether all sources should be combined.
     */
    public void setCombineFiles(final Boolean combineFiles)
    {
        this.combineFiles = combineFiles;
    }
}
