package org.apache.torque.generator.source.transform.model;

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
 * Converts a type to another type.
 *
 * @version $Id: $
 */
public interface TypeConverter
{
    /**
     * Returns true if and only if the converter can convert the passed value
     * to the passed class.
     *
     * @param value the value to convert, may be null.
     * @param targetClass the target class, not null.
     *
     * @return whether the converter can convert the value to the passed
     *         target class.
     */
    boolean accept(Object value, Class<?> targetClass);
    /**
     * Converts the passed value to the passed class.
     * Is only called if accept has returned true.
     *
     * @param value the value to convert, may be null.
     * @param targetClass the target class, not null.
     *
     * @return the converted value.
     */
    Object convert(Object value, Class<?> targetClass);
}
