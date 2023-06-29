package org.apache.torque.generator.source.transform;

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

/**
 * Transforms one source tree into another source tree.
 */
public interface SourceTransformer
{
    /**
     * Transforms one source root into another source root.
     * It is allowed to modify the toTransformRoot tree and
     * return the same tree.
     *
     * @param modelRoot the root of the model to transform, not null.
     * @param controllerState the state of the controller, not null.
     *
     * @return the transformed source root, not null.
     *
     * @throws SourceTransformerException if the source cannot be transformed.
     */
    Object transform(
            Object modelRoot,
            ControllerState controllerState)
                    throws SourceTransformerException;
}
