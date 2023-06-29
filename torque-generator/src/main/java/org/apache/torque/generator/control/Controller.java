package org.apache.torque.generator.control;

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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.Configuration;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.controller.OutletReference;
import org.apache.torque.generator.configuration.controller.Output;
import org.apache.torque.generator.configuration.outlet.OutletConfiguration;
import org.apache.torque.generator.control.existingtargetstrategy.AppendToTargetFileStrategy;
import org.apache.torque.generator.control.existingtargetstrategy.ExistingTargetStrategy;
import org.apache.torque.generator.control.existingtargetstrategy.MergeTargetFileStrategy;
import org.apache.torque.generator.control.existingtargetstrategy.ReplaceTargetFileStrategy;
import org.apache.torque.generator.control.existingtargetstrategy.SkipExistingTargetFileStrategy;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.processor.string.StringProcessor;
import org.apache.torque.generator.source.PostprocessorDefinition;
import org.apache.torque.generator.source.Source;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.SourcePath;
import org.apache.torque.generator.source.SourcePathPointer;
import org.apache.torque.generator.source.SourceProcessConfiguration;
import org.apache.torque.generator.source.SourceProvider;
import org.apache.torque.generator.source.SourceTransformerDefinition;
import org.apache.torque.generator.source.skipDecider.SkipDecider;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;

/**
 * Reads the configuration and generates the output accordingly.
 */
public class Controller
{
    /** The logger. */
    private static Log log = LogFactory.getLog(Controller.class);

    /**
     * The file in the work directory where last source changes
     * are stored.
     */
    public static final String LAST_SOURCE_CHANGE_CACHE_FILE
    = "last-source-changes.checksums";

    /** The content of checksum files if no checksum can be computed. */
    public static final String NO_CHECKSUM_CONTENT
    = "[checksum could not be determined]";

    /**
     * All known ExistingTargetStrategies.
     * TODO: move to a better place.
     */
    private static final List<ExistingTargetStrategy>
    EXISTING_TARGET_STRATEGIES;

    static
    {
        final List<ExistingTargetStrategy> existingTargetStrategies
            = new ArrayList<>();
        existingTargetStrategies.add(new ReplaceTargetFileStrategy());
        existingTargetStrategies.add(new SkipExistingTargetFileStrategy());
        existingTargetStrategies.add(new MergeTargetFileStrategy());
        existingTargetStrategies.add(new AppendToTargetFileStrategy());
        EXISTING_TARGET_STRATEGIES = Collections.unmodifiableList(
                existingTargetStrategies);
    }

    /**
     * Caches the result whether the source was modified since last generation
     * (relevant if the runOnlyOnSchemaCahne Flag is set).
     * Caching is necessary because a source file can be read several times
     * during one generation, and only the first check against the checksum file
     * is meaningful.
     * The key is the absolute path to the source file, the value the result
     * of the sourceModified check.
     */
    private final Map<String, Boolean> sourceModifiedCache
        = new HashMap<>();

    /**
     * Executes the controller action.
     *
     * @param unitDescriptors the units of generation to execute.
     *
     * @throws ControllerException if a ControllerException occurs during
     *         processing.
     * @throws ConfigurationException if a ConfigurationException occurs during
     *         processing.
     * @throws GeneratorException if a OutletException occurs during
     *         processing.
     */
    public void run(final List<UnitDescriptor> unitDescriptors)
            throws GeneratorException
    {
        checkLogging();
        sourceModifiedCache.clear();
        final Configuration configuration = readConfiguration(unitDescriptors);

        final List<UnitConfiguration> unitConfigurations
        = configuration.getUnitConfigurations();
        final ControllerState controllerState = new ControllerState();
        unitConfigurations.stream().forEach( unitConfiguration -> {
            try {
                processGenerationUnit(
                        controllerState,
                        unitConfiguration);
            } catch (GeneratorException e) {
               log.error(e.getMessage());
               throw new RuntimeException(e);
            }
        });
        controllerState.getVariableStore().endGeneration();
    }

    /**
     * Initializes the Logging.
     * 
     */
    protected void checkLogging()
    {
        String log4jConfiguration = System.getProperty("log4j2.configuration");
        if (log4jConfiguration != null)
        {
            log.info("Using external log4j2 configuration from " + log4jConfiguration);
            return;
        }
        /*final InputStream log4jStream
        = Controller.class.getClassLoader().getResourceAsStream(
                "org/apache/torque/generator/log4j2.xml");
        ConfigurationSource source = null;
        if (log4jStream != null) {
            try {
                source = new ConfigurationSource(log4jStream);
                LoggerContext lc = (LoggerContext) LogManager.getContext(false);
                lc.start( ConfigurationFactory.getInstance().getConfiguration(lc, source));
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }*/
    }

    /**
     * Reads the configuration.
     *
     * @param unitDescriptors the unit descriptors for which the configuration
     *        should be read, not null, not empty.
     *
     * @return the configuration.
     *
     * @throws ConfigurationException if the configuration is faulty.
     */
    private Configuration readConfiguration(
            final List<UnitDescriptor> unitDescriptors)
                    throws ConfigurationException
    {
        log.info("readConfiguration() : Starting to read configuration files");
        final Configuration configuration = new Configuration();
        configuration.addUnits(unitDescriptors);
        configuration.read();
        log.info("readConfiguration() : Configuration read.");
        return configuration;
    }

    /**
     * Processes a unit of generation.
     *
     * @param controllerState the controller state, not null.
     * @param unitConfiguration the configuration of the generation unit
     *        to process, not null.
     *
     * @throws GeneratorException if a generation error occurs.
     */
    protected void processGenerationUnit(
            final ControllerState controllerState,
            final UnitConfiguration unitConfiguration)
                    throws GeneratorException
    {
        log.debug("processGenerationUnit() : start");
        unitConfiguration.getLoglevel().apply();
        log.debug("processGenerationUnit() : Loglevel applied.");
        controllerState.setUnitConfiguration(unitConfiguration);

        File sourceChecksumsFile = new File(
                unitConfiguration.getCacheDirectory(),
                LAST_SOURCE_CHANGE_CACHE_FILE);
        if (unitConfiguration.isRunOnlyOnSourceChange())
        {
            try
            {
                controllerState.getLastGeneratedSourceChecksums().readFromFile(
                        sourceChecksumsFile);
            }
            catch (IOException e)
            {
                throw new GeneratorException("could not read "
                        + "LastSourceChange checksum file"
                        + sourceChecksumsFile.getAbsolutePath(),
                        e);
            }
        }
        final List<Output> outputList = unitConfiguration.getOutputList();
        outputList.stream().forEach( output -> {
            try {
                processOutput(
                        output,
                        controllerState,
                        unitConfiguration);
            } catch (GeneratorException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });

        if (unitConfiguration.isRunOnlyOnSourceChange())
        {
            try
            {
                controllerState.getThisGenerationSourceChecksums().writeToFile(
                        sourceChecksumsFile);
            }
            catch (IOException e)
            {
                throw new GeneratorException("could not write "
                        + "LastSourceChange checksum file"
                        + sourceChecksumsFile.getAbsolutePath(),
                        e);
            }
        }
    }

    /**
     * Processes an output definition.
     *
     * @param output the output definition to process, not null.
     * @param controllerState the controller state, not null.
     * @param unitConfiguration the configuration of the generation unit
     *        to process, not null.
     *
     * @throws GeneratorException if a generation error occurs.
     */
    private void processOutput(
            final Output output,
            final ControllerState controllerState,
            final UnitConfiguration unitConfiguration)
                    throws GeneratorException
    {
        log.info("Processing output " + output.getName());
        controllerState.setOutput(output);

        SourceProvider sourceProvider = output.getSourceProvider();
        SourceProvider overrideSourceProvider
        = unitConfiguration.getOverrideSourceProvider();
        if (overrideSourceProvider != null)
        {
            overrideSourceProvider = overrideSourceProvider.copy();
            overrideSourceProvider.copyNotSetSettingsFrom(sourceProvider);
            sourceProvider = overrideSourceProvider;
        }
        controllerState.setSourceProvider(sourceProvider);
        sourceProvider.init(
                unitConfiguration.getConfigurationHandlers(),
                controllerState);
        if (!sourceProvider.hasNext())
        {
            log.info("No sources found, skipping output");
        }

        while (sourceProvider.hasNext())
        {
            final Source source = sourceProvider.next();
            if (!unitConfiguration.isRunOnlyOnSourceChange()
                    || checkSourceModified(source, controllerState, unitConfiguration))
            {
                processSourceInOutput(
                        source,
                        output,
                        controllerState,
                        unitConfiguration);
            }
        }
        controllerState.setSourceProvider(null);
    }

    /**
     * Processes a single source in an output definition.
     *
     * @param source the source to process, not null.
     * @param output the output to which the source belongs, not null.
     * @param controllerState the controller state, not null.
     * @param unitConfiguration the configuration of the current generation
     *        unit, not null.
     *
     * @throws GeneratorException if a generation error occurs.
     */
    private void processSourceInOutput(
            final Source source,
            final Output output,
            final ControllerState controllerState,
            final UnitConfiguration unitConfiguration)
                    throws GeneratorException
    {
        log.info("Processing source " + source.getDescription());
        Object modelRoot = source.getRootElement();
        controllerState.setSourceFile(source.getSourceFile());
        final SourceProcessConfiguration sourceProcessConfiguration
        	= output.getSourceProcessConfiguration();
        modelRoot = transformSource(
                modelRoot,
                sourceProcessConfiguration.getTransformerDefinitions(),
                controllerState);
        controllerState.setModelRoot(modelRoot);

        final String startElementsPath
        	= sourceProcessConfiguration.getStartElementsPath();

        final Iterator<SourcePathPointer> iterator = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                startElementsPath);
        if (!iterator.hasNext())
        {
            log.info("No start Elements found for path "
                    + startElementsPath);
        }
        while (iterator.hasNext())
        {
            final SourcePathPointer pointer = iterator.next();
            final Object model = pointer.getValue();
            String path = pointer.getPath();

            // remove root node from path because the root node
            // is no part of the xpath for sourceElements
            if (model instanceof SourceElement && path.startsWith("/"))
            {
                int slashIndex = path.indexOf('/', 1);
                if (slashIndex != -1)
                {
                    path = path.substring(slashIndex + 1);
                }
                else
                {
                    path = "/";
                }
            }
            controllerState.setModel(model, path);
            processModel(
                    model,
                    output,
                    source,
                    unitConfiguration,
                    controllerState);
        }
    }

    /**
     * Creates the output file name and sets it in the output.
     * The filename is calculated either by the filenameConfigurator in
     * <code>output</code> or is given explicitly (in the latter case
     * nothing needs to be done).
     *
     * @param controllerState the controller state, not null.
     * @param output The output to  process, not null.
     *
     * @throws ConfigurationException if an incorrect configuration is
     *         encountered, e.g. if neither filename nor filenameOutlet is
     *         set in output.
     * @throws GeneratorException if an error occurs during generation of
     *         the output filename.
     */
    protected void createOutputFilename(
            final Output output,
            final ControllerState controllerState)
                    throws GeneratorException
    {
        if (output.getFilenameOutlet() == null)
        {
            if (output.getFilename() == null)
            {
                throw new ConfigurationException(
                        "neither filename nor filenameOutlet are set"
                                + " on output" + output);
            }
        }
        else
        {
            if (log.isDebugEnabled())
            {
                log.debug("Start generation of Output File path");
            }
            controllerState.setOutputFile(null);

            final Outlet filenameOutlet = output.getFilenameOutlet();
            final OutletReference contentOutletReference
                = new OutletReference(
                    filenameOutlet.getName());
            controllerState.setRootOutletReference(
                    contentOutletReference);
            // use the namespace not of the filenameOutlet
            // but of the real outlet
            // TODO: is this a good idea ? make configurable ?
            controllerState.setOutletNamespace(
                    output.getContentOutlet().getNamespace());
            filenameOutlet.beforeExecute(controllerState);
            final OutletResult filenameResult
            = filenameOutlet.execute(controllerState);
            if (!filenameResult.isStringResult())
            {
                throw new GeneratorException(
                        "The result of a filename generation must be a String,"
                                + " not a byte array");
            }
            final String filename = filenameResult.getStringResult();
            filenameOutlet.afterExecute(controllerState);
            if (log.isDebugEnabled())
            {
                log.debug("End generation of Output File path, result is "
                        + filename);
            }
            output.setFilename(filename);
        }
    }

    /**
     * Processes the generation for a single model object in a source.
     *
     * @param model the source model object to process.
     * @param output the current output, not null.
     * @param source the current source, not null.
     * @param unitConfiguration the current unit configuration, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws ControllerException if startElement is null or the configured
     *         outlet does not exist or the output directory cannot be created
     *         or the output file cannot be written..
     * @throws GeneratorException if the outlet throws an exception
     *         during execution.
     */
    private void processModel(
            final Object model,
            final Output output,
            final Source source,
            final UnitConfiguration unitConfiguration,
            final ControllerState controllerState)
                    throws GeneratorException
    {
        if (model == null)
        {
            throw new ControllerException(
                    "Null start element found in source "
                            + "for generating the filename "
                            + "of output file "
                            + output);
        }
        log.debug("Processing new model " + model);

        ExistingTargetStrategy existingTargetStrategy = EXISTING_TARGET_STRATEGIES.stream()
                .filter(candidate -> candidate.getStrategyName().equals(
                output.getExistingTargetStrategy()))
                .findFirst().orElse(null);
        if (existingTargetStrategy == null)
        {
            throw new ControllerException("existingTargetStrategy "
                    + output.getExistingTargetStrategy()
                    + " not found");
        }

        createOutputFilename(output, controllerState);
        final File outputFile = ControllerHelper.getOutputFile(
                output.getOutputDirKey(),
                output.getFilename(),
                unitConfiguration);
        controllerState.setOutputFile(outputFile);

        if (!existingTargetStrategy.beforeGeneration(
                output.getOutputDirKey(),
                output.getFilename(),
                getOutputEncoding(output, unitConfiguration),
                unitConfiguration))
        {
            log.info("Skipping generation of File "
                    + outputFile.getAbsolutePath()
                    + " because of existingTargetStrategy "
                    + existingTargetStrategy.getStrategyName());
            return;
        }
        if (log.isInfoEnabled())
        {
            log.info("Start generation of File "
                    + outputFile.getAbsolutePath());
        }

        final OutletReference contentOutletConfiguration
        = output.getContentOutlet();
        controllerState.setOutletNamespace(
                contentOutletConfiguration.getNamespace());
        controllerState.setRootOutletReference(
                contentOutletConfiguration);

        final OutletConfiguration outletConfiguration
        = unitConfiguration.getOutletConfiguration();

        final Outlet outlet = outletConfiguration.getOutlet(
                contentOutletConfiguration.getName());
        if (outlet == null)
        {
            throw new ControllerException(
                    "No outlet configured for outlet name \""
                            + contentOutletConfiguration.getName()
                            + "\"");
        }

        final SkipDecider skipDecider
        = output.getSourceProcessConfiguration().getSkipDecider();
        if (skipDecider != null)
        {
            if (!skipDecider.proceed(controllerState))
            {
                log.debug("SkipDecider " + skipDecider.getClass().getName()
                        + " decided to skip "
                        + "generation of file "
                        + controllerState.getOutputFile());
                return;
            }
            else
            {
                log.debug("SkipDecider " + skipDecider.getClass().getName()
                        + " decided to proceed");
            }
        }

        {
            final File parentOutputDir
            = controllerState.getOutputFile().getParentFile();
            if (parentOutputDir != null
                    && !parentOutputDir.isDirectory())
            {
                final boolean success = parentOutputDir.mkdirs();
                if (!success)
                {
                    throw new ControllerException(
                            "Could not create directory \""
                                    + parentOutputDir.getAbsolutePath()
                                    + "\"");
                }
            }
        }

        outlet.beforeExecute(controllerState);
        OutletResult result = outlet.execute(controllerState);
        outlet.afterExecute(controllerState);

        if (result.isStringResult())
        {
            for (PostprocessorDefinition postprocessorDefinition
                    : output.getPostprocessorDefinitions())
            {
                StringProcessor postprocessor
                = postprocessorDefinition.getPostprocessor();
                String postprocessedResult
                = postprocessor.process(result.getStringResult());
                result = new OutletResult(postprocessedResult);
            }
        }
        existingTargetStrategy.afterGeneration(
                output.getOutputDirKey(),
                output.getFilename(),
                getOutputEncoding(output, unitConfiguration),
                result,
                unitConfiguration);

        controllerState.getVariableStore().endFile();
        if (log.isDebugEnabled())
        {
            log.debug("End generation of Output File "
                    + controllerState.getOutputFile());
        }
    }

    /**
     * Applies all transformer definitions to the current source.
     *
     * @param sourceRoot the root element of the source to transform,
     *        not null.
     * @param transformerDefinitions the transformer definitions to apply,
     *        not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the transformed root element, not null.
     * @throws SourceTransformerException if error in transform
     * @throws SourceException if source not found
     */
    public Object transformSource(
            final Object sourceRoot,
            final List<SourceTransformerDefinition> transformerDefinitions,
            final ControllerState controllerState)
                    throws SourceTransformerException, SourceException
    {
        Object toTransform = sourceRoot;
        Object result = sourceRoot;
        for (final SourceTransformerDefinition transformerDefinition
                : transformerDefinitions)
        {
            final SourceTransformer sourceTransformer
            = transformerDefinition.getSourceTransformer();
            log.debug("Applying source transformer "
                    + sourceTransformer.getClass().getName()
                    + " to source root object " + toTransform);

            result = sourceTransformer.transform(
                    result,
                    controllerState);
            if (result == null)
            {
                throw new SourceTransformerException("Transformer "
                        + sourceTransformer.getClass().getName()
                        + " returned null for element "
                        + toTransform);
            }
            log.debug("Transformation ended");
            toTransform = result;
        }
        return result;
    }

    /**
     * Calculates the output encoding for an output.
     *
     * @param output The output, not null.
     * @param unitConfiguration the configuration of the unit of generation
     *        to which the output belongs.
     *
     * @return the encoding, not null.
     */
    private String getOutputEncoding(
            final Output output,
            final UnitConfiguration unitConfiguration)
    {
        if (output.getEncoding() != null)
        {
            return output.getEncoding();
        }
        if (unitConfiguration.getDefaultOutputEncoding() != null)
        {
            return unitConfiguration.getDefaultOutputEncoding();
        }
        return Charset.defaultCharset().displayName();
    }

    /**
     * Checks whether a source file was modified since the last generation.
     *
     * @param source the source file to check, not null.
     * @param unitConfiguration the configuration of the unit of generation,
     *        not null.
     *
     * @return true if the source file was modified since last generation,
     *         if the time of the last generation cannot be determined,
     *         or if the source file does not exist,
     *         false otherwise.
     */
    private boolean checkSourceModified(
            final Source source,
            final ControllerState controllerState,
            final UnitConfiguration unitConfiguration)
    {
        File sourceFile = source.getSourceFile();
        if (sourceFile == null)
        {
            log.debug("checkSourceModified(): "
                    + "source file cannot be determined, return true");
            return true;
        }
        String sourceChangeKey = getSourceChangeKey(
                unitConfiguration,
                sourceFile);
        if (sourceModifiedCache.get(sourceChangeKey) != null)
        {
            return sourceModifiedCache.get(sourceChangeKey);
        }

        Date sourceLastModified = source.getLastModified();
        if (sourceLastModified == null)
        {
            log.debug("checkSourceModified(): "
                    + "lastModified date of source cannot be determined, "
                    + "return true");
            sourceModifiedCache.put(sourceChangeKey, true);
            return true;
        }
        controllerState.getThisGenerationSourceChecksums().setModificationDate(
                sourceChangeKey, sourceLastModified);

        byte[] sourceChecksum = source.getContentChecksum();
        controllerState.getThisGenerationSourceChecksums().setChecksum(
                sourceChangeKey, sourceChecksum);

        Date lastGenerationTime
        = controllerState.getLastGeneratedSourceChecksums()
        .getModificationDate(sourceChangeKey);
        if (lastGenerationTime == null)
        {
            log.debug("checkSourceModified(): "
                    + "lastGenerationTime does not exist, return true");
            sourceModifiedCache.put(sourceChangeKey, true);
            return true;
        }
        if (lastGenerationTime.before(sourceLastModified))
        {
            log.debug("checkSourceModified(): "
                    + "lastGenerationTime was before source was modified ("
                    + lastGenerationTime
                    + " < "
                    + sourceLastModified
                    + "), return true");
            sourceModifiedCache.put(sourceChangeKey, true);
            return true;
        }
        byte[] lastGeneratedChecksum
        = controllerState.getLastGeneratedSourceChecksums()
        .getChecksum(sourceChangeKey);
        if (!Arrays.equals(lastGeneratedChecksum, sourceChecksum))
        {
            log.debug("checkSourceModified(): "
                    + " different checksum, return true");
            sourceModifiedCache.put(sourceChangeKey, true);
            return true;
        }
        log.debug("checkSourceModified() : returning false");
        sourceModifiedCache.put(sourceChangeKey, false);
        return false;
    }

    private String getSourceChangeKey(
            final UnitConfiguration unitConfiguration, final File sourceFile)
    {
        String sourceChangeCacheKey
        = unitConfiguration.getTemplateSetName()
        + ":"
        + sourceFile.getAbsolutePath();
        return sourceChangeCacheKey;
    }

}
