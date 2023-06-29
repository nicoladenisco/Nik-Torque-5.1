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

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.outlet.OutletResult;

/**
 * A handler which implements a strategy on how to deal with existing targets.
 *
 * @version $Id: ExistingTargetStrategy.java 1839288 2018-08-27 09:48:33Z tv $
 */
public interface ExistingTargetStrategy
{
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
     * @return true if generation should proceed,
     *         false if generation should be aborted.
     *
     * @throws GeneratorException on an error.
     */
    boolean beforeGeneration(
            String outputDirKey,
            String outputPath,
            String encoding,
            UnitConfiguration unitConfiguration)
                    throws GeneratorException;

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
     *
     * @throws GeneratorException on an error.
     */
    void afterGeneration(
            String outputDirKey,
            String outputPath,
            String encoding,
            OutletResult generationResult,
            UnitConfiguration unitConfiguration)
                    throws GeneratorException;

    /**
     * Returns the name of the existing target strategy.
     *
     * @return the strategy name, not null, must be different from the names of
     *         other strategies.
     */
    String getStrategyName();
}
