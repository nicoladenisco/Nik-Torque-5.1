package org.apache.torque.generator.source;

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

/**
 * An interface for a class respresenting the name of a source attribute.
 * This is useful e.g. if enums are defined for attribute names.
 *
 * $Id: SourceAttributeName.java 1331190 2012-04-27 02:41:35Z tfischer $
 */
public interface SourceAttributeName
{
    /**
     * returns the name of the attribute.
     *
     * @return the name of the attribute, not null.
     */
    String getName();
}
