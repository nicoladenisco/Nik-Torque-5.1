package org.apache.torque.generator.variable;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.qname.QualifiedNameMap;
import org.apache.torque.generator.variable.Variable;
import org.apache.torque.generator.variable.VariableStore;
import org.junit.Test;

public class VariableStoreTest extends BaseTest
{
    @Test
    public void testVariableScopePrecedence()
    {
        VariableStore store = new VariableStore();
        store.startOutlet();
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");

        // fill store
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.GENERATOR",
                Variable.Scope.OUTLET));
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.CHILDREN1",
                Variable.Scope.CHILDREN));
        store.startOutlet();
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.CHILDREN2",
                Variable.Scope.CHILDREN));
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.FILE",
                Variable.Scope.FILE));
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.GLOBAL",
                Variable.Scope.GLOBAL));

        assertEquals(
                "org.apache.torque.GENERATOR",
                store.getInHierarchy(qualifiedName).getValue());
        assertEquals(
                "org.apache.torque.GENERATOR",
                store.getContent().get(qualifiedName).getValue());

        store.remove(store.getInHierarchy(qualifiedName));

        assertEquals(
                "org.apache.torque.CHILDREN2",
                store.getInHierarchy(qualifiedName).getValue());
        assertEquals(
                "org.apache.torque.CHILDREN2",
                store.getContent().get(qualifiedName).getValue());

        store.endOutlet();

        assertEquals(
                "org.apache.torque.CHILDREN1",
                store.getInHierarchy(qualifiedName).getValue());
        assertEquals(
                "org.apache.torque.CHILDREN1",
                store.getContent().get(qualifiedName).getValue());

        store.endOutlet();

        assertEquals(
                "org.apache.torque.FILE",
                store.getInHierarchy(qualifiedName).getValue());
        assertEquals(
                "org.apache.torque.FILE",
                store.getContent().get(qualifiedName).getValue());

        store.endFile();

        assertEquals(
                "org.apache.torque.GLOBAL",
                store.getInHierarchy(qualifiedName).getValue());
        assertEquals(
                "org.apache.torque.GLOBAL",
                store.getContent().get(qualifiedName).getValue());

        store.endGeneration();

        assertNull(store.getInHierarchy(qualifiedName));
        assertNull(store.getContent().get(qualifiedName));
    }

    public void testNamespaceVisibility()
    {
        VariableStore store = new VariableStore();
        store.set(new Variable(
                new QualifiedName("org.apache.torque.name"),
                "org.apache.torque.GENERATOR",
                Variable.Scope.OUTLET));
        store.set(new Variable(
                new QualifiedName("org.apache.name"),
                "org.apache.FILE",
                Variable.Scope.FILE));
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");
        assertEquals(
                "org.apache.torque.GENERATOR",
                store.getInHierarchy(qualifiedName).getValue());

        store.clear();
        store.set(new Variable(
                new QualifiedName("org.apache.name"),
                "org.apache.GENERATOR",
                Variable.Scope.OUTLET));
        store.set(new Variable(
                new QualifiedName("org.apache.torque.name"),
                "org.apache.torque.FILE",
                Variable.Scope.FILE));
        assertEquals(
                "org.apache.torque.FILE",
                store.getInHierarchy(qualifiedName).getValue());

    }

    public void testGetInHierarchy()
    {
        VariableStore store = new VariableStore();
        store.set(new Variable(
                new QualifiedName("org.apache.torque.name"),
                "org.apache.torque",
                Variable.Scope.OUTLET));
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");
        assertEquals(
                "org.apache.torque",
                store.getInHierarchy(qualifiedName).getValue());
        qualifiedName
            = new QualifiedName("org.apache.torque.generator.name");
        assertEquals(
                "org.apache.torque",
                store.getInHierarchy(qualifiedName).getValue());
        qualifiedName
            = new QualifiedName("org.apache.name");
        assertNull(store.getInHierarchy(qualifiedName));
    }

    public void testGetContents()
    {
        VariableStore store = new VariableStore();
        store.startOutlet();
        store.set(new Variable(
                new QualifiedName("org.apache.torque.generator"),
                "org.apache.torque.generator",
                Variable.Scope.OUTLET));
        store.set(new Variable(
                new QualifiedName("org.apache.torque.children1"),
                "org.apache.torque.children1",
                Variable.Scope.CHILDREN));
        store.startOutlet();
        store.set(new Variable(
                new QualifiedName("org.apache.torque.children2"),
                "org.apache.torque.children2",
                Variable.Scope.CHILDREN));
        store.set(new Variable(
                new QualifiedName("org.apache.torque.file"),
                "org.apache.torque.file",
                Variable.Scope.FILE));
        store.set(new Variable(
                new QualifiedName("org.apache.torque.global"),
                "org.apache.torque.global",
                Variable.Scope.GLOBAL));

        QualifiedNameMap<Variable> storeContent = store.getContent();
        assertEquals("storeContent should contain 5 entries",
                5,
                storeContent.size());

        {
            Variable variable
            = storeContent.get(
                    new QualifiedName("org.apache.torque.generator"));
            assertEquals(
                    "org.apache.torque.generator",
                    variable.getValue());
        }
        {
            Variable variable
            = storeContent.get(
                    new QualifiedName("org.apache.torque.children1"));
            assertEquals(
                    "org.apache.torque.children1",
                    variable.getValue());
        }
        {
            Variable variable
            = storeContent.get(
                    new QualifiedName("org.apache.torque.children2"));
            assertEquals(
                    "org.apache.torque.children2",
                    variable.getValue());
        }
        {
            Variable variable
            = storeContent.get(
                    new QualifiedName("org.apache.torque.file"));
            assertEquals(
                    "org.apache.torque.file",
                    variable.getValue());
        }
        {
            Variable variable
            = storeContent.get(
                    new QualifiedName("org.apache.torque.global"));
            assertEquals(
                    "org.apache.torque.global",
                    variable.getValue());
        }
    }

    public void testRemove()
    {
        VariableStore store = new VariableStore();
        store.startOutlet();
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");

        // fill store
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.GENERATOR",
                Variable.Scope.OUTLET));
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.CHILDREN",
                Variable.Scope.CHILDREN));
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.FILE",
                Variable.Scope.FILE));
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.GLOBAL",
                Variable.Scope.GLOBAL));

        assertEquals(
                "org.apache.torque.GENERATOR",
                store.getInHierarchy(qualifiedName).getValue());

        store.remove(store.getInHierarchy(qualifiedName));

        assertEquals(
                "org.apache.torque.CHILDREN",
                store.getInHierarchy(qualifiedName).getValue());

        store.remove(store.getInHierarchy(qualifiedName));

        assertEquals(
                "org.apache.torque.FILE",
                store.getInHierarchy(qualifiedName).getValue());

        store.remove(store.getInHierarchy(qualifiedName));

        assertEquals(
                "org.apache.torque.GLOBAL",
                store.getInHierarchy(qualifiedName).getValue());

        store.remove(store.getInHierarchy(qualifiedName));

        assertNull(store.getInHierarchy(qualifiedName));

        // test whether we can remove hidden variables
        Variable childrenVariable = new Variable(
                qualifiedName,
                "org.apache.torque.CHILDREN",
                Variable.Scope.CHILDREN);

        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.GENERATOR",
                Variable.Scope.OUTLET));
        store.set(childrenVariable);
        store.set(new Variable(
                qualifiedName,
                "org.apache.torque.FILE",
                Variable.Scope.FILE));

        assertEquals(
                "org.apache.torque.GENERATOR",
                store.getInHierarchy(qualifiedName).getValue());

        store.remove(childrenVariable);

        assertEquals(
                "org.apache.torque.GENERATOR",
                store.getInHierarchy(qualifiedName).getValue());

        store.remove(store.getInHierarchy(qualifiedName));

        assertEquals(
                "org.apache.torque.FILE",
                store.getInHierarchy(qualifiedName).getValue());

    }
}
