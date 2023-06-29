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

import org.apache.torque.generator.configuration.UnitConfiguration;

/**
 * Helper methods for the controller.
 *
 * @version $Id: ControllerHelper.java 1839288 2018-08-27 09:48:33Z tv $
 */
public final class ControllerHelper
{
    /**
     * Private constructor for helper class.
     */
    private ControllerHelper()
    {
    }

    /**
     * Calculates the location of the target file from the file name and
     * the unit configuration.
     *
     * @param outputDirKey the key for the output directory to use.
     * @param outputPath the output path, not null.
     * @param unitConfiguration the unit configuration, not null.
     *
     * @return the output File, not null.
     */
    public static File getOutputFile(
            String outputDirKey,
            String outputPath,
            UnitConfiguration unitConfiguration)
    {
        File targetDirectory
        = unitConfiguration.getOutputDirectory(outputDirKey);
        File outputFile = new File(
                targetDirectory,
                outputPath);
        return outputFile;
    }
}
