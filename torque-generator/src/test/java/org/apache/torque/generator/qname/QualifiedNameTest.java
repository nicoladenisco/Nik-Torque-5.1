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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.junit.Test;

public class QualifiedNameTest
{
    @Test
    public void testConstructors()
    {
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");
        assertEquals("name", qualifiedName.getName());
        assertEquals(
                new Namespace("org.apache.torque"),
                qualifiedName.getNamespace());

        qualifiedName = new QualifiedName("name");
        assertEquals("name", qualifiedName.getName());
        assertEquals(Namespace.ROOT_NAMESPACE, qualifiedName.getNamespace());

        qualifiedName = new QualifiedName(".name");
        assertEquals("name", qualifiedName.getName());
        assertEquals(Namespace.ROOT_NAMESPACE, qualifiedName.getNamespace());

        qualifiedName
            = new QualifiedName("name", new Namespace("org.apache.torque"));
        assertEquals("name", qualifiedName.getName());
        assertEquals(
                new Namespace("org.apache.torque"),
                qualifiedName.getNamespace());

        qualifiedName = new QualifiedName("name", (Namespace) null);
        assertEquals("name", qualifiedName.getName());
        assertEquals(Namespace.ROOT_NAMESPACE, qualifiedName.getNamespace());

        qualifiedName = new QualifiedName("name", new Namespace(""));
        assertEquals("name", qualifiedName.getName());
        assertEquals(Namespace.ROOT_NAMESPACE, qualifiedName.getNamespace());
    }

    @Test
    public void testEmptyNames()
    {
        try
        {
            new QualifiedName("org.apache.torque.");
            fail("Could generate QualifiedName from String "
                    + "with empty name part");
        }
        catch (IllegalArgumentException e)
        {
        }

        try
        {
            new QualifiedName("", "org.apache.torque");
            fail("Could generate QualifiedName with empty name");
        }
        catch (IllegalArgumentException e)
        {
        }
    }

    @Test
    public void testNamespaceFromParts()
    {
        List<String> parts = new ArrayList<>(3);
        parts.add("org");
        parts.add("apache");
        parts.add("torque");
        Namespace namespace = new Namespace(parts);
        assertEquals(namespace, new Namespace("org.apache.torque"));

        parts = new ArrayList<>();
        namespace = new Namespace(parts);
        assertEquals(Namespace.ROOT_NAMESPACE, namespace);

        parts = null;
        try
        {
            namespace = new Namespace(parts);
            fail("should not be able to construct namespace "
                    + "from null list");
        }
        catch (NullPointerException e)
        {
        }
    }

    @Test
    public void testGetNamespaceParts()
    {
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");
        List<String> parts = qualifiedName.getNamespace().getParts();
        assertEquals(parts.size(), 3);
        assertEquals(parts.get(0), "org");
        assertEquals(parts.get(1), "apache");
        assertEquals(parts.get(2), "torque");
    }

    @Test
    public void testGetParentNamespace()
    {
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");
        Namespace parentNamespace = qualifiedName.getParentNamespace();
        assertEquals(parentNamespace, new Namespace("org.apache"));

        qualifiedName = new QualifiedName("org.name");
        parentNamespace = qualifiedName.getParentNamespace();
        assertEquals(Namespace.ROOT_NAMESPACE, parentNamespace);

        qualifiedName = new QualifiedName(".name");
        parentNamespace = qualifiedName.getParentNamespace();
        assertEquals(Namespace.ROOT_NAMESPACE, parentNamespace);
    }

    @Test
    public void testEquals()
    {
        QualifiedName qualifiedName
            = new QualifiedName("org.apache.torque.name");
        assertEquals(
                qualifiedName,
                new QualifiedName("org.apache.torque.name"));
        assertFalse(
                qualifiedName.equals(
                        new QualifiedName("org.apache.name")));
        assertFalse(
                qualifiedName.equals(
                        new QualifiedName(".name")));
        assertFalse(
                qualifiedName.equals(
                        new QualifiedName("org.apache.torque.otherName")));

        qualifiedName = new QualifiedName(".name");
        assertEquals(
                qualifiedName,
                new QualifiedName(".name"));
        assertEquals(
                qualifiedName,
                new QualifiedName("name"));
        assertFalse(
                qualifiedName.equals(
                        new QualifiedName("org.apache.name")));
        assertFalse(
                qualifiedName.equals(
                        new QualifiedName(".otherName")));
    }
}
