package org.apache.torque.generator.outlet.java;

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

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.outlet.OutletImpl;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * An outlet without mergepoints.
 */
public abstract class OutletWithoutMergepoints extends OutletImpl
{
    /**
     * Constructs a OutletWithoutMergepoints with the given name.
     *
     * @param name the name of this outlet, not null.
     *
     * @throws NullPointerException if name is null.
     */
    public OutletWithoutMergepoints(QualifiedName name)
    {
        super(name);
    }

    @Override
    public final void addMergepointMapping(MergepointMapping mergepointMapping)
            throws ConfigurationException
    {
        throw new UnsupportedOperationException(
                "The outlet " + getClass().getName()
                + " does not support mergepoints");
    }

    @Override
    public final MergepointMapping getMergepointMapping(String name)
    {
        return null;
    }
}
