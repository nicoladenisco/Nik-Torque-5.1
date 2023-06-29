package org.apache.torque.generator.source.skipDecider;

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

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceException;

/**
 * A class deciding whether the generation should be skipped or not.
 * The class is asked for every source which is processed.
 *
 * @version $Id: SkipDecider.java 1331190 2012-04-27 02:41:35Z tfischer $
 */
public interface SkipDecider
{
    /**
     * Decides whether the source file should be skipped and not used for
     *        generation.
     * @param controllerState the current controller state, containing e.g.
     *        the current source element.
     * @return true if the current generation should proceed,
     *         false if the generation should be skipped.
     *
     * @throws SourceException if an error occurs
     */
    boolean proceed(ControllerState controllerState) throws SourceException;
}
