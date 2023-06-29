package org.apache.torque.generator.maven;

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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
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
import org.apache.torque.generator.configuration.paths.Maven2ProjectPaths;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.control.Controller;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.source.stream.FileSourceProvider;

/**
 * Executes a unit of generation within the torque generator.
 *
 * $Id: TorqueGeneratorMojo.java 1873218 2020-01-27 14:36:48Z gk $
 *
 */
@Mojo( name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES )
public class TorqueGeneratorMojo extends AbstractMojo
{
    /** Possible usages for generator output directories. */
    private enum OutputDirUsage
    {
        /**
         * The output dir will be added to the maven project's
         * compileSourceRoot.
         */
        COMPILE("compile"),
        /**
         * The output dir will be added to the maven project's
         * testCompileSourceRoot.
         */
        TEST_COMPILE("test-compile"),
        /**
         * The output dir will be added to the maven project's resources.
         */
        RESOURCE("resource"),
        /**
         * The output dir will be added to the maven project's test resources.
         */
        TEST_RESOURCE("test-resource"),
        /**
         * The target dir will not be used in the maven build.
         */
        NONE("none");

        /** The usage key. */
        private final String key;

        /**
         * Constructor.
         *
         * @param key the key for the enum.
         */
        private OutputDirUsage(final String key)
        {
            this.key = key;
        }

        @Override
        public String toString()
        {
            return key;
        }

        /**
         * Returns the key of the Usage.
         *
         * @return the key, not null.
         */
        public String getKey()
        {
            return key;
        }

        public static OutputDirUsage get(final String key)
                throws MojoExecutionException
        {
            for (OutputDirUsage candidate : values())
            {
                if (candidate.getKey().equals(key))
                {
                    return candidate;
                }
            }
            StringBuilder errorMessage = new StringBuilder()
                    .append("targetDirUsage contains illegal value: ")
                    .append(key)
                    .append(". Possible values are :");
            for (OutputDirUsage targetDirUsage : values())
            {
                errorMessage.append(" ").append(targetDirUsage.getKey());
            }
            throw new MojoExecutionException(errorMessage.toString());
        }
    }

    /**
     * The packaging type of the generation unit, either "directory" , "jar"
     * or "classpath".
     */
    @Parameter(defaultValue = "directory", required = true)
    private String packaging;

    /**
     * The root directory of the project.
     * Has no effect if packaging is "classpath".
     */
    @Parameter(defaultValue = "${basedir}", required = true)
    private File projectRootDir;

    /**
     * The configuration directory of the generation unit.
     * Has no effect if packaging is "classpath".
     */
    @Parameter
    private File configDir;

    /**
     * The configuration package of the generation unit.
     * Has only effect if packaging is "classpath".
     */
    @Parameter
    private String configPackage;

    /**
     * The directory where the source files reside.
     */
    @Parameter
    private File sourceDir;

    /**
     * Include patterns for the source files.
     * If set, the include and exclude patterns from the templates
     * are overridden.
     * If not set, then the include patterns from the templates are used.
     * The patterns are case sensitive, wildcards are * and ?.
     */
    @Parameter
    private Set<String> sourceIncludes;

    /**
     * Exclude patterns for the source files.
     * If set, the include and exclude patterns from the templates
     * are overridden.
     * If not set, then the include patterns from the templates are used.
     * The patterns are case sensitive, wildcards are * and ?.
     */
    @Parameter
    private Set<String> sourceExcludes;

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
    @Parameter
    private Boolean combineFiles;

    /**
     * The default base output directory for the generated files.
     * Whether the configured templates use this directory or not,
     * is up to the templates; check the template documentation.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources", required = true)
    private File defaultOutputDir;

    /**
     * The target directories for the generated files, keyed by the
     * output directory keys.
     * If output directory keys are used by the output
     * (and if yes, which output directory keys),
     * is up to the templates; check the template documentation.
     * Default is to map the key "modifiable" to
     * ${project.build.directory}/src/main/generated-java
     */
    @Parameter
    private Map<String, String> outputDirMap = new HashMap<>();

    /**
     * The work directory for e.g. merge sources.
     */
    @Parameter(defaultValue = "${basedir}/src/main/torque-gen/work")
    private File workDir;

    /**
     * The filename of the jar file of the generation unit.
     * Has only effect if packaging is "jar".
     */
    @Parameter
    private String jarFile;

    /**
     * What to do with the generated files in the default output directory.
     * Possible values are:
     * <ul>
     *   <li>
     *     compile: The default value.
     *     The generated files are treated as compileable sources.
     *     In maven-speak, this means the newFileTargetDir will be added as
     *     compileSourceRoot of the maven project.
     *   </li>
     *   <li>
     *     test-compile: The generated files are treated as compileable test sources.
     *     In maven-speak, this means the newFileTargetDir will be added as
     *     testCompileSourceRoot of the maven project.
     *   </li>
     *   <li>
     *     resource: The generated files are treated as resource.
     *     This means the newFileTargetDir will be added to the
     *     resources of the maven project.
     *   </li>
     *   <li>
     *     test-resource: The generated files are treated as test resource.
     *     This means the newFileTargetDir will be added to the
     *     test resources of the maven project.
     *   </li>
     *   <li>
     *     none: The generated files are not used in the maven build.
     *   </li>
     */
    @Parameter
    private String defaultOutputDirUsage = "compile";

    /**
     * What to do with the generated files for the output directories
     * defined in outputDirMap. The map uses the same keys as outputDirMap.
     * Possible values are:
     * <ul>
     *   <li>
     *     compile: The generated files are treated as compileable sources.
     *     In maven-speak, this means the modifiedFileTargetDir will be added
     *     as compileSourceRoot of the maven project.
     *   </li>
     *   <li>
     *     test-compile: The generated files are treated as compileable
     *     test sources.
     *     In maven-speak, this means the modifiedFileTargetDir will be added
     *     as testCompileSourceRoot of the maven project.
     *   </li>
     *   <li>
     *     resource: The generated files are treated as resource.
     *     This means the modifiedFileTargetDir will be added to the
     *     resources of the maven project.
     *   </li>
     *   <li>
     *     test-resource: The generated files are treated as test resource.
     *     This means the modifiedFileTargetDir will be added to the
     *     test resources of the maven project.
     *   </li>
     *   <li>
     *     none: The generated files are not used in the maven build.
     *   </li>
     * Default is to map the key "modifiable" to compile
     */
    @Parameter
    private Map<String, String> outputDirUsageMap = new HashMap<>();

    /**
     * The config directory of the project overriding the settings.
     * If set, the settings of this directory are used as "child"
     * and the "normal" settings are used as "parent".
     */
    @Parameter
    private File overrideConfigDir;

    /**
     * The config package of the project overriding the settings.
     * If set, the settings of this directory are used as "child"
     * and the "normal" settings are used as "parent".
     */
    @Parameter
    private String overrideConfigPackage;

    /**
     * The Loglevel to use in the generation process. Must be one of
     * trace, debug, info, warn or error.
     */
    @Parameter
    private String loglevel;

    /**
     * True if the generator should only run
     * if one of the source files changes,
     * false if it should always run irrespective of changes
     * in the source files.
     */
    @Parameter(defaultValue="false")
    private boolean runOnlyOnSourceChange;

    /**
     * Whether to add debug information to the output.
     */
    @Parameter(defaultValue="false")
    private boolean addDebuggingInfoToOutput;

    /**
     * Additional options which can be added to the generation process.
     * This overrides both the options set in the templates
     * and the options in optionsFile.
     */
    @Parameter
    private Map<String, String> options;

    /**
     * Properties file which contains the options which can be added
     * to the generation process.
     * This overrides the options set in the templates, but not the
     * options set by the parameter <code>options</code>.
     */
    @Parameter
    private File optionsFile;

    /**
     * The encoding which should be used for the files which do not have an
     * output encoding set in the templates.
     * If not set, the property project.build.sourceEncoding from the
     * maven pom is used.
     * If that is also not set, the generator default is used
     * (which is the platform default encoding).
     */
    @Parameter
    private String defaultOutputEncoding;

    /**
     * The Maven project this plugin runs in.
     *
     */
    @Parameter( defaultValue = "${project}", required = true, readonly = true )
    private MavenProject project;
    
    
    /**
     * Specifies whether the execution should be skipped.
     */
    @Parameter( property = "maven.torque.skip", defaultValue = "false" )
    private boolean skip;

    /**
     * Configures and runs the Torque generator.
     */
    @Override
    public void execute() throws MojoExecutionException
    {
        
        if ( skip)
        {
            getLog().info( "Skipping Torque execution" );
            return;
        }
        
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
 
        // Do conversion here so illegal values are discovered before generation
        OutputDirUsage defaultOutputDirUsageConverted
        = OutputDirUsage.get(defaultOutputDirUsage);
        Map<String, OutputDirUsage> outputDirUsageConvertedMap
            = new HashMap<>();
        for (Map.Entry<String, String> outputDirUsageEntry
                : outputDirUsageMap.entrySet())
        {
            outputDirUsageConvertedMap.put(
                    outputDirUsageEntry.getKey(),
                    OutputDirUsage.get(outputDirUsageEntry.getValue()));
        }

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
        getLog().debug("Packaging is " + packaging);

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
                throw new MojoExecutionException(
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
                getLog().debug("Setting config dir to " + configDir.toString());
            }
        }

        if (sourceDir != null)
        {
            projectPaths.setSourceDir(sourceDir);
            getLog().debug("Setting source dir to " + sourceDir.toString());
        }

        FileSourceProvider fileSourceProvider = null;
        if (sourceIncludes != null || sourceExcludes != null)
        {
            Fileset sourceFileset
                = new Fileset(
                    projectPaths.getDefaultSourcePath(),
                    sourceIncludes,
                    sourceExcludes);
            getLog().debug("Setting source includes to "
                    + sourceIncludes);
            getLog().debug("Setting source excludes to "
                    + sourceExcludes);
            try
            {
                fileSourceProvider = new FileSourceProvider(
                        null,
                        sourceFileset,
                        combineFiles);
            }
            catch (ConfigurationException e)
            {
                throw new MojoExecutionException(
                        "The source provider cannot be instantiated", e);
            }
        }

        projectPaths.setOutputDirectory(null, defaultOutputDir);
        getLog().debug("Setting defaultOutputDir to "
                + defaultOutputDir.toString());
        if (outputDirMap != null)
        {
            if (outputDirMap.get(Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY)
                    == null)
            {
                outputDirMap.put(
                        Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                        project.getBasedir()
                        + "/"
                        + Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR);
            }
            for (Map.Entry<String, String> outputDirMapEntry
                    : outputDirMap.entrySet())
            {
                projectPaths.setOutputDirectory(
                        outputDirMapEntry.getKey(),
                        new File(outputDirMapEntry.getValue()));
                getLog().debug("Setting output directory with key "
                        + outputDirMapEntry.getKey()
                        + " to "
                        + outputDirMapEntry.getValue());
            }
        }
        if (workDir != null)
        {
            projectPaths.setWorkDir(workDir);
            getLog().debug("Setting workDir to "
                    + workDir.toString());
        }
        getLog().debug("ProjectPaths = " + projectPaths);

        OptionsConfiguration optionConfiguration = null;
        if (options != null || optionsFile != null)
        {
            if (options == null)
            {
                options = new HashMap<>();
            }
            if (optionsFile != null)
            {
                Properties optionProperties = new Properties();
                try (FileInputStream optionsFileInputStream = new FileInputStream(optionsFile))
                {
                    optionProperties.load(optionsFileInputStream);
                }
                catch (IOException e)
                {
                    getLog().error(e);
                    throw new MojoExecutionException(e.getMessage());
                }
                getLog().debug("loaded options file from "
                        + optionsFile.getAbsolutePath() + ", contents: "
                        + optionProperties);
                for (Map.Entry<Object, Object> propertiesEntry
                        : optionProperties.entrySet())
                {
                    if (!options.containsKey(propertiesEntry.getKey()))
                    {
                        options.put(
                                (String) propertiesEntry.getKey(),
                                (String) propertiesEntry.getValue());
                    }
                }
            }
            getLog().debug("options = " + options);
            optionConfiguration = new MapOptionsConfiguration(options);
        }
        Loglevel convertedLoglevel = null;
        if (this.loglevel != null)
        {
            convertedLoglevel = Loglevel.getByKey(loglevel);
        }
        String encoding = defaultOutputEncoding;
        if (encoding == null)
        {
            encoding = project.getProperties().getProperty(
                    "project.build.sourceEncoding");
        }
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                packaging,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideSourceProvider(fileSourceProvider);
        unitDescriptor.setOverrideOptions(optionConfiguration);
        unitDescriptor.setLoglevel(convertedLoglevel);
        unitDescriptor.setDefaultOutputEncoding(encoding);
        unitDescriptor.setAddDebuggingInfoToOutput(addDebuggingInfoToOutput);
        unitDescriptor.setRunOnlyOnSourceChange(runOnlyOnSourceChange);
        getLog().debug("unit descriptor created");
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
            parentUnitDescriptor.setDefaultOutputEncoding(encoding);
            parentUnitDescriptor.setAddDebuggingInfoToOutput(addDebuggingInfoToOutput);
            parentUnitDescriptor.setRunOnlyOnSourceChange(runOnlyOnSourceChange);
            getLog().debug("child unit descriptor created from directory");
            unitDescriptor = parentUnitDescriptor;
        }
        else if (overrideConfigPackage != null)
        {
            CustomProjectPaths childProjectPaths
                = new CustomProjectPaths(projectPaths);
            childProjectPaths.setConfigurationPackage(overrideConfigPackage);

            UnitDescriptor parentUnitDescriptor = new UnitDescriptor(
                    Packaging.CLASSPATH,
                    childProjectPaths,
                    new DefaultTorqueGeneratorPaths());
            parentUnitDescriptor.setInheritsFrom(unitDescriptor);
            parentUnitDescriptor.setOverrideSourceProvider(fileSourceProvider);
            parentUnitDescriptor.setOverrideOptions(optionConfiguration);
            parentUnitDescriptor.setLoglevel(convertedLoglevel);
            parentUnitDescriptor.setDefaultOutputEncoding(encoding);
            parentUnitDescriptor.setAddDebuggingInfoToOutput(addDebuggingInfoToOutput);
            parentUnitDescriptor.setRunOnlyOnSourceChange(runOnlyOnSourceChange);
            getLog().debug("child unit descriptor created from package");
            unitDescriptor = parentUnitDescriptor;
        }
        unitDescriptors.add(unitDescriptor);
        try
        {
            getLog().debug("Generation started");
            controller.run(unitDescriptors);
            getLog().info("Generation successful");
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new MojoExecutionException(e.getMessage());
        }

        File defaultOutputDirPath = projectPaths.getOutputDirectory(null);
        if (defaultOutputDirPath.exists())
        {
            switch (defaultOutputDirUsageConverted)
            {
            case COMPILE:
                project.addCompileSourceRoot(
                        defaultOutputDirPath.toString());
                getLog().debug("Added "
                        + defaultOutputDirPath.toString()
                        + " as compile source root");
                break;
            case TEST_COMPILE:
                project.addTestCompileSourceRoot(
                        defaultOutputDirPath.toString());
                getLog().debug("Added "
                        + defaultOutputDirPath.toString()
                        + " as test compile source root");
                break;
            case RESOURCE:
                Resource resource = new Resource();
                resource.setDirectory(defaultOutputDirPath.toString());
                project.addResource(resource);
                getLog().debug("Added "
                        + defaultOutputDirPath.toString()
                        + " to the project resources");
                break;
            case TEST_RESOURCE:
                resource = new Resource();
                resource.setDirectory(defaultOutputDirPath.toString());
                project.addTestResource(resource);
                getLog().debug("Added "
                        + defaultOutputDirPath.toString()
                        + " to the project test resources");
                break;
            case NONE:
            default:
            }
        }
        else
        {
            getLog().info("defaultOutputDirPath "
                    + defaultOutputDirPath.getAbsolutePath()
                    + " does not exist, not applying defaultOutputDirUsage");
        }

        if (outputDirUsageConvertedMap.get(
                Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY)
                == null
                && outputDirMap.get(
                        Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY)
                != null)
        {
            outputDirUsageConvertedMap.put(
                    Maven2ProjectPaths.MODIFIABLE_OUTPUT_DIR_KEY,
                    OutputDirUsage.COMPILE);
        }
        for (Map.Entry<String, OutputDirUsage> usageEntry
                : outputDirUsageConvertedMap.entrySet())
        {
            String outputDirPath = outputDirMap.get(usageEntry.getKey());
            if (outputDirPath == null)
            {
                getLog().info("outputDirPath set for key "
                        + usageEntry.getKey()
                        + " ignoring this outputDirUsageMap entry");
                continue;
            }

            File outputDirFile = new File(outputDirPath);
            if (!outputDirFile.exists())
            {
                getLog().info("outputDirPath "
                        + outputDirFile.getAbsolutePath()
                        + " for outputDirUsageMap with key "
                        + usageEntry.getKey()
                        + " does not exist,"
                        + " ignoring this outputDirUsageMap entry");
                continue;
            }
            switch (usageEntry.getValue())
            {
            case COMPILE:
                project.addCompileSourceRoot(outputDirPath.toString());
                getLog().debug("Added "
                        + outputDirPath.toString()
                        + " as compile source root");
                break;
            case TEST_COMPILE:
                project.addTestCompileSourceRoot(
                        outputDirPath.toString());
                getLog().debug("Added "
                        + outputDirPath.toString()
                        + " as test compile source root");
                break;
            case RESOURCE:
                Resource resource = new Resource();
                resource.setDirectory(outputDirPath.toString());
                project.addResource(resource);
                getLog().debug("Added "
                        + outputDirPath.toString()
                        + " to the project resources");
                break;
            case TEST_RESOURCE:
                resource = new Resource();
                resource.setDirectory(outputDirPath.toString());
                project.addTestResource(resource);
                getLog().debug("Added "
                        + outputDirPath.toString()
                        + " to the project test resources");
                break;
            case NONE:
            default:
            }
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
     * Sets the default output directory for generated files.
     *
     * @param outputDir the default output directory, not null.
     */
    public void setDefaultOutputDir(final File outputDir)
    {
        this.defaultOutputDir = outputDir;
    }

    /**
     * Sets the target directory for files which are not generated
     * each time anew.
     *
     * @param outputDirKey the key value
     * @param outputDir the target directory, or null to use the default.
     */
    public void setOutputDir(final String outputDirKey, final String outputDir)
    {
        this.outputDirMap.put(outputDirKey, outputDir);
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
     * Sets the maven project this mojo runs in.
     *
     * @param project the maven project this mojo runs in.
     */
    public void setProject(final MavenProject project)
    {
        this.project = project;
    }

    /**
     * Sets the usage for the default output dir.
     *
     * @param defaultOutputDirUsage the new usage, not null.
     */
    public void setDefaultOutputDirUsage(final String defaultOutputDirUsage)
    {
        if (defaultOutputDirUsage == null)
        {
            throw new NullPointerException(
                    "defaultOutputDirUsage must not be null");
        }
        this.defaultOutputDirUsage = defaultOutputDirUsage;
    }

    /**
     * Sets the usage for an output directory.
     *
     * @param outputDirKey key for the output directory, not null.
     * @param outputDirUsage the new usage, not null.
     */
    public void setOutputDirUsage(
            final String outputDirKey,
            final String outputDirUsage)
    {
        if (outputDirKey == null)
        {
            throw new NullPointerException(
                    "outputDirKey must not be null");
        }
        if (outputDirUsage == null)
        {
            throw new NullPointerException(
                    "modifiedFileTargetDirUsage must not be null");
        }
        this.outputDirUsageMap.put(outputDirKey, outputDirUsage);
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
     * Sets the config package of the project overriding the settings.
     * If set, the settings of this directory are used as "child"
     * and the "normal" settings are used as "parent".
     *
     * @param overrideConfigPackage the config package of the project overriding the settings,
     *            or null if the template settings will not be overridden.
     */
    public void setOverrideConfigPackage(final String overrideConfigPackage)
    {
        this.overrideConfigPackage = overrideConfigPackage;
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
     * Sets additional options which can be added to the generation process.
     * These options overrides existing options in the templates.
     *
     * @param options the overriding options, or null if no options
     *        should be overridden.
     */
    public void setOptions(final Map<String, String> options)
    {
        this.options = options;
    }

    /**
     * Sets whether all source files should be combined into one single graph-
     *
     * @param combineFiles whether the source file should be combined.
     */
    public void setCombineFiles(final Boolean combineFiles)
    {
        this.combineFiles = combineFiles;
    }

    /**
     * Sets the work dir for e.g. merging sources.
     *
     * @param workDir the new workdir.
     */
    public void setWorkDir(final File workDir)
    {
        this.workDir = workDir;
    }

    /**
     * Sets a options file by which generation parameters can be set.
     *
     * @param optionsFile the path to the file containing the generation
     *        options.
     */
    public void setOptionsFile(final File optionsFile)
    {
        this.optionsFile = optionsFile;
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
}
