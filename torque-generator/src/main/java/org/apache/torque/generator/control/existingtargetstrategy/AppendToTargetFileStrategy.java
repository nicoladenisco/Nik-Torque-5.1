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
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.control.ControllerException;
import org.apache.torque.generator.control.ControllerHelper;
import org.apache.torque.generator.outlet.OutletResult;

/**
 * A handler which implements the strategy to append the generation result
 * to the existing target files.
 *
 * @version $Id: AppendToTargetFileStrategy.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class AppendToTargetFileStrategy implements ExistingTargetStrategy
{
    /** The strategy name "append". */
    public static final String STRATEGY_NAME = "append";

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
            String outputDirKey,
            String outputPath,
            String encoding,
            UnitConfiguration unitConfiguration)
    {
        return true;
    }

    /**
     * Processes the results of the generation.
     *
     * @param outputDirKey the key for the output directory
     *        into which the generated file should be written,
     *        null for the default output directory.
     * @param outputPath the path to which the output should be written,
     *        relative to the output base directory.
     * @param encoding The character encoding of the generated file,
     *        or null for the platform default encoding.
     * @param generationResult the result of the generation, not null.
     * @param unitConfiguration the configuration of the current configuration
     *        unit, not null.
     * @throws GeneratorException on an error.
     */
    @Override
    public void afterGeneration(
            String outputDirKey,
            String outputPath,
            String encoding,
            OutletResult generationResult,
            UnitConfiguration unitConfiguration)
                    throws GeneratorException
    {
        File outputFile = ControllerHelper.getOutputFile(
                outputDirKey,
                outputPath,
                unitConfiguration);
        try
        {
            if (generationResult.isStringResult())
            {
                String originalContent = "";
                if (outputFile.exists())
                {
                    originalContent = FileUtils.readFileToString(
                            outputFile,
                            encoding);
                }
                FileUtils.writeStringToFile(
                        outputFile,
                        originalContent + generationResult.getStringResult(),
                        encoding);
            }
            else
            {
                byte[] originalContent = new byte[] {};
                if (outputFile.exists())
                {
                    originalContent = FileUtils.readFileToByteArray(
                            outputFile);
                }
                byte[] result = new byte[originalContent.length
                                         + generationResult.getByteArrayResult().length];
                System.arraycopy(
                        originalContent,
                        0,
                        result,
                        0,
                        originalContent.length);
                System.arraycopy(generationResult.getByteArrayResult(),
                        0,
                        result,
                        originalContent.length,
                        generationResult.getByteArrayResult().length);

                FileUtils.writeByteArrayToFile(
                        outputFile,
                        result);
            }
        }
        catch (IOException e)
        {
            throw new ControllerException(
                    "Could not write file \""
                            + outputFile.getAbsolutePath()
                            + "\"",
                            e);
        }
    }

    /**
     * Returns the name of the existing target strategy.
     *
     * @return "replace"
     */
    @Override
    public String getStrategyName()
    {
        return STRATEGY_NAME;
    }
}
