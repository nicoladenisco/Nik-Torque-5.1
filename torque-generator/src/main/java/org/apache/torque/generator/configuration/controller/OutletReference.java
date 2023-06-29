package org.apache.torque.generator.configuration.controller;

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

import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Represents a reference to a outlet.
 *
 * @version $Id: OutletReference.java 1331190 2012-04-27 02:41:35Z tfischer $
 */
public class OutletReference
{
    /** The name of the referenced outlet. */
    private QualifiedName name;

    /**
     * The namespace under which the outlet should execute.
     * Defaults to the namespace in the outlet's name.
     */
    private Namespace namespace;

    /**
     * Constructor.
     *
     * @param name the name of the referenced outlet, not null.
     */
    public OutletReference(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name must not be null");
        }
        this.name = new QualifiedName(name, Namespace.ROOT_NAMESPACE);
        namespace = this.name.getNamespace();
    }

    /**
     * Constructor.
     *
     * @param name the name of the referenced outlet, not null.
     */
    public OutletReference(QualifiedName name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name must not be null");
        }
        this.name = name;
        namespace = this.name.getNamespace();
    }

    /**
     * Returns the qualified name of the outlet.
     *
     * @return the qualified name of the outlet, not null.
     */
    public QualifiedName getName()
    {
        return name;
    }

    /**
     * Returns the namespace under which the outlet executes.
     *
     * @return the namespace under which the outlet executes.
     */
    public Namespace getNamespace()
    {
        return namespace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(name=").append(name).append(")");
        return result.toString();
    }
}
