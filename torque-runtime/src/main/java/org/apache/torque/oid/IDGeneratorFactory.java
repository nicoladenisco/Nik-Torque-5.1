package org.apache.torque.oid;

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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.IDMethod;

/**
 * A factory which instantiates {@link
 * org.apache.torque.oid.IdGenerator} implementations.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id: IDGeneratorFactory.java 1850965 2019-01-10 17:21:29Z painter $
 */
public final class IDGeneratorFactory
{
    /**
     * The list of ID generation method types which have associated
     * {@link org.apache.torque.oid.IdGenerator} implementations.
     */
    public static final List<IDMethod> ID_GENERATOR_METHODS =
            Collections.unmodifiableList(Stream.of(
                IDMethod.NATIVE,
                IDMethod.AUTO_INCREMENT,
                IDMethod.SEQUENCE
                ).collect(Collectors.toList()));

    /**
     * Private constructor to prevent initialisation.
     *
     * This class contains only static methods and thus should not be
     * instantiated.
     */
    private IDGeneratorFactory()
    {
        // empty
    }

    /**
     * Factory method which instantiates {@link
     * org.apache.torque.oid.IdGenerator} implementations based on the
     * return value of the provided adapter's {@link
     * org.apache.torque.adapter.Adapter#getIDMethodType()} method.
     * Returns <code>null</code> for unknown types.
     *
     * @param adapter The type of adapter to create an ID generator for.
     * @param name name of the adapter
     *
     * @return The appropriate ID generator (possibly <code>null</code>).
     */
    public static IdGenerator create(Adapter adapter, String name)
    {
        switch (adapter.getIDMethodType())
        {
            case AUTO_INCREMENT:
                return new AutoIncrementIdGenerator(adapter, name);

            case SEQUENCE:
                return new SequenceIdGenerator(adapter, name);

            default:
                return null;
        }
    }
}
