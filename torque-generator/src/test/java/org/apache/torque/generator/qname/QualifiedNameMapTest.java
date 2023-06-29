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
import static org.junit.Assert.assertNull;

import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.qname.QualifiedNameMap;
import org.junit.Test;

public class QualifiedNameMapTest
{
    private static final QualifiedName qualifiedNameChild
        = new QualifiedName("org.apache.torque.generator.name");
    private static final QualifiedName qualifiedName
        = new QualifiedName("org.apache.torque.name");
    private static final QualifiedName qualifiedNameParent
        = new QualifiedName("org.apache.name");
    private static final QualifiedName qualifiedNameAncestor
        = new QualifiedName("org.name");
    private static final QualifiedName qualifiedNameDefault
        = new QualifiedName("name");
    private static final QualifiedName qualifiedNameUnrelated
        = new QualifiedName("org.apa.name");
    private static final QualifiedName qualifiedNameOtherName
        = new QualifiedName("org.apache.torque.otherName");

    @Test
    public void testGetKeyInHierarchyByQualifiedName()
    {
        QualifiedNameMap<String> qualifiedNameMap
            = new QualifiedNameMap<>();

        QualifiedName result
        = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertNull(result);

        qualifiedNameMap.put(qualifiedNameUnrelated, "org.apa");
        result = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertNull(result);

        qualifiedNameMap.put(qualifiedNameChild, "org.apache.torque.generator");
        result = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertNull(result);

        qualifiedNameMap.put(qualifiedNameDefault, "default");
        result = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertEquals(result, qualifiedNameDefault);

        qualifiedNameMap.put(qualifiedNameAncestor, "org");
        result = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertEquals(result, qualifiedNameAncestor);

        qualifiedNameMap.put(qualifiedNameParent, "org.apache");
        result = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertEquals(result, qualifiedNameParent);

        qualifiedNameMap.put(qualifiedName, "org.apache.torque");
        result = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertEquals(result, qualifiedName);

        qualifiedNameMap.remove(qualifiedName);
        result = qualifiedNameMap.getKeyInHierarchy(qualifiedName);
        assertEquals(result, qualifiedNameParent);
    }

    @Test
    public void testGetInHierarchyByQualifiedName()
    {
        QualifiedNameMap<String> qualifiedNameMap
            = new QualifiedNameMap<>();

        String result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertNull(result);

        qualifiedNameMap.put(qualifiedNameUnrelated, "org.apa");
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertNull(result);

        qualifiedNameMap.put(qualifiedNameChild, "org.apache.torque.generator");
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertNull(result);

        qualifiedNameMap.put(qualifiedNameDefault, "default");
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertEquals(result, "default");

        qualifiedNameMap.put(qualifiedNameAncestor, "org");
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertEquals(result, "org");

        qualifiedNameMap.put(qualifiedNameParent, "org.apache");
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertEquals(result, "org.apache");

        qualifiedNameMap.put(qualifiedName, "org.apache.torque");
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertEquals(result, "org.apache.torque");

        qualifiedNameMap.put(qualifiedName, null);
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertEquals(result, null);

        qualifiedNameMap.remove(qualifiedName);
        result = qualifiedNameMap.getInHierarchy(qualifiedName);
        assertEquals(result, "org.apache");
    }

    @Test
    public void testGetInHierarchyByNamespace()
    {
        QualifiedNameMap<String> qualifiedNameMap
            = new QualifiedNameMap<>();

        qualifiedNameMap.put(qualifiedNameUnrelated, "org.apa");
        qualifiedNameMap.put(qualifiedNameChild, "org.apache.torque.generator");
        qualifiedNameMap.put(qualifiedNameDefault, "default");
        qualifiedNameMap.put(qualifiedNameParent, "org.apache");
        qualifiedNameMap.put(qualifiedName, "org.apache.torque");
        qualifiedNameMap.put(
                qualifiedNameOtherName,
                "org.apache.torque.otherName");

        QualifiedNameMap<String> expected
            = new QualifiedNameMap<>();

        expected.put(qualifiedName, "org.apache.torque");
        expected.put(
                qualifiedNameOtherName,
                "org.apache.torque.otherName");

        QualifiedNameMap<String> result
        = qualifiedNameMap.getInHierarchy(
                qualifiedName.getNamespace());

        assertEquals(expected, result);
    }

    @Test
    public void testGetAllInHierarchy()
    {
        QualifiedNameMap<String> qualifiedNameMap
            = new QualifiedNameMap<>();

        qualifiedNameMap.put(qualifiedNameUnrelated, "org.apa");
        qualifiedNameMap.put(qualifiedNameChild, "org.apache.torque.generator");
        qualifiedNameMap.put(qualifiedNameDefault, "default");
        qualifiedNameMap.put(qualifiedNameParent, "org.apache");
        qualifiedNameMap.put(qualifiedName, "org.apache.torque");
        qualifiedNameMap.put(
                qualifiedNameOtherName,
                "org.apache.torque.otherName");

        QualifiedNameMap<String> expected
            = new QualifiedNameMap<>();

        expected.put(qualifiedNameDefault, "default");
        expected.put(qualifiedNameParent, "org.apache");
        expected.put(qualifiedName, "org.apache.torque");
        expected.put(
                qualifiedNameOtherName,
                "org.apache.torque.otherName");

        QualifiedNameMap<String> result
        = qualifiedNameMap.getAllInHierarchy(
                qualifiedName.getNamespace());

        assertEquals(expected, result);
    }

    public void testGetMoreSpecific()
    {
        // TODO: implement !
    }
}
