package org.apache.torque.util.functions;

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

import org.apache.torque.Column;

/**
 * Define the basic methods that classes that support SQL Functions
 * need to implement for Classes that use them.  This is intended to
 * allow code to be written before functions are fully integrated
 * with the DBAdaptors.  As well as allowing for functions to
 * expand as needed.
 *
 * @author <a href="mailto:greg.monroe@dukece.com">Greg Monroe</a>
 * @version $Id: SQLFunction.java 1855244 2019-03-11 15:59:16Z tv $
 */
public interface SQLFunction extends Column
{
    /**
     * Returns the function parameters at index i.
     * Should be null if parameter does not exist.
     *
     * @param i The 0 based parameter to get.
     * @return The parameter.  Null if one does not exist.
     */
    Object getArgument(int i);

    /**
     * Returns the column to which this function is applied.
     *
     * @return the column, not null.
     *
     * @throws IllegalStateException if the column cannot be determined.
     */
    Column getColumn();

    /**
     * Return all the parameters as an object array. This allow for
     * processing of the parameters in their original format rather
     * than just in String format.  E.g. a parameter might be specified
     * as a Date object, or a Column object.
     *
     * @return Should return a valid Object array and not null.  E.g.
     *  implementors should return new Object[0] if there are no parameters.
     */
    Object[] getArguments();

    /**
     * Sets the function specific arguments.  Note, this should generally
     * only be called by FunctionFactory.
     *
     * @param args The function specific arguments.
     */
    void setArguments(Object... args);
}
