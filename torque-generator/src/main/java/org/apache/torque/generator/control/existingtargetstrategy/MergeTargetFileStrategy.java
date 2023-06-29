package org.apache.torque.generator.control.existingtargetstrategy;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.control.ControllerException;
import org.apache.torque.generator.control.ControllerHelper;
import org.apache.torque.generator.merge.ThreeWayMerger;
import org.apache.torque.generator.outlet.OutletResult;

/**
 * A handler which implements the strategy to replace existing target files.
 *
 * @version $Id: MergeTargetFileStrategy.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class MergeTargetFileStrategy implements ExistingTargetStrategy
{
    /** The strategy name "replace". */
    public static final String STRATEGY_NAME = "merge";

    /**
     * The subdirectory in the work directory where the results
     * of the last generation will be stored.
     */
    public static final String WORK_SUBDIR = "raw-generated";

    /** The classlogger. */
    private static Log log = LogFactory.getLog(MergeTargetFileStrategy.class);

    /** The merger. */
    private final ThreeWayMerger merger = new ThreeWayMerger();

    /**
     * Will be called before the generation is started and decides whether
     * the generation process for this file should proceed.
     *
     * @param outputDirKey the key for the output directory
     *        into which the generated file should be written,
     *        null for the default output directory.
     * @param outputPath the path to which the output should be written,
     *        relative to the output base directory.
     * @param encoding The character encoding of the generated file,
     *        or null for the platform default encoding.
     * @param unitConfiguration the configuration of the current configuration
     *        unit, not null.
     *
     * @return true always.
     */
    @Override
    public boolean beforeGeneration(
            final String outputDirKey,
            final String outputPath,
            final String encoding,
            final UnitConfiguration unitConfiguration)
    {
        return true;
    }

    /**
     * Processes the results of the generation.
     *
     * @param outputDirKey the key for the output directory
     *        into which the generated file should be written,
     *        null for the default output directory.
     * @param outputPath the location to which the output should be written.
     * @param encoding The character encoding of the generated file,
     *        or null for the platform default encoding.
     * @param generationResult the result of the generation, not null.
     * @param unitConfiguration the configuration of the current configuration
     *        unit, not null.
     *
     * @throws GeneratorException on an error.
     */
    @Override
    public void afterGeneration(
            final String outputDirKey,
            final String outputPath,
            final String encoding,
            final OutletResult generationResult,
            final UnitConfiguration unitConfiguration)
                    throws GeneratorException
    {
        File generationStorageDir
            = new File(unitConfiguration.getWorkDirectory(), WORK_SUBDIR);
        File generationStorageFile;
        if (outputDirKey == null)
        {
            generationStorageFile
                = new File(generationStorageDir,
                    FilenameUtils.concat("default", outputPath));
        }
        else
        {
            generationStorageFile
                = new File(generationStorageDir,
                    FilenameUtils.concat("other",
                            FilenameUtils.concat(outputDirKey, outputPath)));
        }
        String oldGenerationContent = readFileToString(
                generationStorageFile,
                encoding);

        File targetFile = ControllerHelper.getOutputFile(
                outputDirKey,
                outputPath,
                unitConfiguration);
        String oldTargetContent = readFileToString(
                targetFile,
                encoding);

        if (!generationResult.isStringResult())
        {
            throw new GeneratorException(
                    "The merge target file strategy onlys works"
                            + " for String generation results (target file="
                            + targetFile.getAbsolutePath() + ")");
        }

        String newTargetContent = null;
        if (oldTargetContent == null)
        {
            log.debug("no old target content found, using generation result");
            newTargetContent = generationResult.getStringResult();
        }
        else if (oldGenerationContent == null)
        {
            log.info("no old generation content found,"
                    + "using old target content."
                    + " This is a bit unusual, but may be ok"
                    + " depending on the circumstances");
            newTargetContent = generationResult.getStringResult();
        }
        else
        {
            log.debug("merging generation result and old target content");
            newTargetContent = merger.merge(
                    oldGenerationContent,
                    generationResult.getStringResult(),
                    oldTargetContent,
                    encoding);
        }
        writeStringToFile(targetFile, newTargetContent, encoding);
        writeStringToFile(
                generationStorageFile,
                generationResult.getStringResult(),
                encoding);
    }

    /**
     * Returns the name of the existing target strategy.
     *
     * @return "merge"
     */
    @Override
    public String getStrategyName()
    {
        return STRATEGY_NAME;
    }

    /**
     * Reads a String to a file.
     *
     * @param file the file to read, not null.
     * @param charset the character set to use for reading the file,
     *        or null for platform default.
     * @return the file's contents, or null if the file does not exist.
     *
     * @throws ControllerException if an error occurs while reading the file.
     */
    private String readFileToString(
            final File file,
            final String charset)
                    throws ControllerException
    {
        String result = null;
        if (file.exists())
        {
            try
            {
                result = FileUtils.readFileToString(
                        file,
                        charset);
            }
            catch (IOException e)
            {
                throw new ControllerException(
                        "Could not read file \""
                                + file.getAbsolutePath()
                                + "\"",
                                e);
            }
        }
        return result;
    }

    /**
     * Writes a String to a file.
     *
     * @param file the location to write to, not null.
     * @param content the content of the file, not null.
     * @param charset the character set to use for writing the file,
     *        or null for platform default.
     *
     * @throws ControllerException if writing the file fails.
     */
    private void writeStringToFile(
            final File file,
            final String content,
            final String charset)
                    throws ControllerException
    {
        try
        {
            FileUtils.writeStringToFile(file, content, charset);
        }
        catch (IOException e)
        {
            throw new ControllerException(
                    "Could not write file \""
                            + file.getAbsolutePath()
                            + "\"",
                            e);
        }
    }
}
