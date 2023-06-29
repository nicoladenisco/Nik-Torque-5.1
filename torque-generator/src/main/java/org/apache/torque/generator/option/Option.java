package org.apache.torque.generator.option;

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

import org.apache.torque.generator.qname.QualifiedName;

/**
 * An option used in the code generation process.
 * It has got a name, a value, and a context where it belongs to.
 */
public interface Option
{
    /**
     * Returns the qualified name of the option.
     *
     * @return the qualified name of the option, not null.
     */
    QualifiedName getQualifiedName();

    /**
     * Returns the value of the option.
     *
     * @return the value of the option, may be null.
     */
    Object getValue();
}
