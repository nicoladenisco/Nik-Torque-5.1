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

import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.control.ControllerHelper;

/**
 * A handler which implements the strategy to skip existing target files.
 *
 * @version $Id: SkipExistingTargetFileStrategy.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class SkipExistingTargetFileStrategy extends ReplaceTargetFileStrategy
{
    /** The strategy name "skip". */
    public static final String STRATEGY_NAME = "skip";

    /**
     * Will be called before the generation is started and decides whether
     * the generation process for this file should proceed.
     *
     * @param outputDirKey the key for the output directory
     *        into which the generated file should be written,
     *        null for the default output directory.
     * @param outputPath the path to which the output should be written,
     *        relative to the output base directory.
     * @param unitConfiguration the configuration of the current configuration
     *        unit, not null.
     *
     * @return true if the target file does not exist, false otherwise.
     */
    @Override
    public boolean beforeGeneration(
            String outputDirKey,
            String outputPath,
            String encoding,
            UnitConfiguration unitConfiguration)
    {
        File outputFile = ControllerHelper.getOutputFile(
                outputDirKey,
                outputPath,
                unitConfiguration);
        return !outputFile.exists();
    }

    /**
     * Returns the name of the existing target strategy.
     *
     * @return "skip"
     */
    @Override
    public String getStrategyName()
    {
        return STRATEGY_NAME;
    }
}
