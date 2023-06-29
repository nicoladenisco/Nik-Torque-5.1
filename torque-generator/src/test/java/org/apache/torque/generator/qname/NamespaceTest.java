package org.apache.torque.generator.qname;

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.torque.generator.qname.Namespace;
import org.junit.Test;

public class NamespaceTest
{
    @Test
    public void testIsVisibleToe()
    {
        Namespace namespace
            = new Namespace("org.apache.torque");
        assertTrue(namespace.isVisibleTo(Namespace.ROOT_NAMESPACE));
        assertTrue(namespace.isVisibleTo(
                new Namespace("org")));
        assertTrue(namespace.isVisibleTo(
                new Namespace("org.apache")));
        assertTrue(namespace.isVisibleTo(
                new Namespace("org.apache.torque")));

        assertFalse(namespace.isVisibleTo(
                new Namespace("or")));
        assertFalse(namespace.isVisibleTo(
                new Namespace("org.apache.torque.generator")));

        namespace = Namespace.ROOT_NAMESPACE;
        assertTrue(namespace.isVisibleTo(Namespace.ROOT_NAMESPACE));
        assertFalse(namespace.isVisibleTo(new Namespace("org.apache")));
    }

    @Test
    public void testIsVisibleFrom()
    {
        Namespace namespace
            = new Namespace("org.apache.torque");
        assertFalse(namespace.isVisibleFrom(Namespace.ROOT_NAMESPACE));
        assertFalse(namespace.isVisibleFrom(new Namespace("org.apache")));
        assertTrue(namespace.isVisibleFrom(new Namespace("org.apache.torque")));

        assertFalse(namespace.isVisibleFrom(new Namespace("or")));
        assertTrue(namespace.isVisibleFrom(
                new Namespace("org.apache.torque.generator")));

        namespace = Namespace.ROOT_NAMESPACE;
        assertTrue(namespace.isVisibleFrom(Namespace.ROOT_NAMESPACE));
        assertTrue(namespace.isVisibleFrom(new Namespace("org.apache")));
    }
}
