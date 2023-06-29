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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.stream.FileSource;
import org.apache.torque.generator.source.stream.PropertiesSourceFormat;
import org.junit.Before;
import org.junit.Test;

public class RichSourceElementImplTest
{
    private SourceElement rootSourceElement;

    @Before
    public void setUp()
    {
        rootSourceElement = new SourceElement("parent");
        SourceElement child1 = new SourceElement("child1");
        List<SourceElement> children = rootSourceElement.getChildren();
        children.add(child1);
        SourceElement child2 = new SourceElement("child2");
        children.add(child2);
        SourceElement child3 = new SourceElement("child3");
        children.add(child3);
    }

    @Test
    public void testGetParents()
    {
        SourceElement child1 = rootSourceElement.getChildren().get(0);
        List<SourceElement> parentsOfChild1 = child1.getParents();
        assertEquals(1, parentsOfChild1.size());
        assertSame(rootSourceElement, parentsOfChild1.get(0));
    }

    @Test
    public void testAddChild()
    {
        SourceElement child1 = rootSourceElement.getChildren().get(0);
        SourceElement child2 = rootSourceElement.getChildren().get(1);
        SourceElement child3 = rootSourceElement.getChildren().get(2);
        SourceElement child4 = new SourceElement("child4");
        rootSourceElement.getChildren().add(child4);

        assertEquals("parent should have 4 children",
                4,
                rootSourceElement.getChildren().size());
        assertSame(child1, rootSourceElement.getChildren().get(0));
        assertSame(child2, rootSourceElement.getChildren().get(1));
        assertSame(child3, rootSourceElement.getChildren().get(2));
        assertSame(child4, rootSourceElement.getChildren().get(3));
        assertSame(rootSourceElement, child4.getParent());
        assertEquals(1, child4.getParents().size());
    }

    @Test
    public void testAddChildAtIndex()
    {
        SourceElement child1 = rootSourceElement.getChildren().get(0);
        SourceElement child2 = rootSourceElement.getChildren().get(1);
        SourceElement child3 = rootSourceElement.getChildren().get(2);
        SourceElement child4 = new SourceElement("child4");
        List<SourceElement> children = rootSourceElement.getChildren();
        children.add(1, child4);
        assertSame(child1, rootSourceElement.getChildren().get(0));
        assertSame(child4, rootSourceElement.getChildren().get(1));
        assertSame(child2, rootSourceElement.getChildren().get(2));
        assertSame(child3, rootSourceElement.getChildren().get(3));
        assertSame(rootSourceElement, child4.getParent());
        assertEquals(1, child4.getParents().size());
    }

    @Test
    public void testRemoveChild()
    {
        List<SourceElement> children = rootSourceElement.getChildren();
        SourceElement child1 = children.get(0);
        SourceElement child2 = children.get(1);
        SourceElement child3 = children.get(2);
        boolean result = children.remove(child2);
        assertTrue(result);
        assertEquals("parent should have 2 children",
                2,
                rootSourceElement.getChildren().size());
        assertSame(child1, rootSourceElement.getChildren().get(0));
        assertSame(child3, rootSourceElement.getChildren().get(1));
        assertEquals(0, child2.getParents().size());
        assertEquals(null, child2.getParent());
    }

    @Test
    public void testRemoveChildViaIterator()
    {
        Iterator<SourceElement> childIt
        = rootSourceElement.getChildren().iterator();
        List<SourceElement> children = rootSourceElement.getChildren();
        SourceElement child1 = children.get(0);
        SourceElement child2 = children.get(1);
        SourceElement child3 = children.get(2);
        childIt.next(); //child1
        childIt.next(); //child2
        childIt.remove();

        assertEquals(2, children.size());
        assertEquals(child1, children.get(0));
        assertEquals(child3, children.get(1));
        assertEquals(0, child2.getParents().size());
        assertEquals(null, child2.getParent());
    }

    @Test
    public void testRemoveNonExistingChild()
    {
        List<SourceElement> children = rootSourceElement.getChildren();
        SourceElement child1 = children.get(0);
        SourceElement child2 = children.get(1);
        SourceElement child3 = children.get(2);
        SourceElement child4 = new SourceElement("child4");

        boolean result = children.remove(child4);
        assertFalse(result);
        assertEquals("parent should have 3 children",
                3,
                rootSourceElement.getChildren().size());
        assertSame(child1, rootSourceElement.getChildren().get(0));
        assertSame(child2, rootSourceElement.getChildren().get(1));
        assertSame(child3, rootSourceElement.getChildren().get(2));
    }

    @Test
    public void testGetChildIndex()
    {
        SourceElement child2 = rootSourceElement.getChildren().get(1);

        assertEquals("child index should be 1",
                1,
                rootSourceElement.getChildren().indexOf(child2));
    }

    @Test
    public void testGetNonExistingChildIndex()
    {
        SourceElement child4 = new SourceElement("child4");
        assertEquals("child index should be -1",
                -1,
                rootSourceElement.getChildren().indexOf(child4));
    }

    @Test
    public void testAddParent()
    {
        SourceElement child = new SourceElement("child");
        List<SourceElement> parentsOfChild = child.getParents();
        SourceElement parent1 = new SourceElement("parent1");
        parentsOfChild.add(parent1);
        SourceElement parent2 = new SourceElement("parent2");
        parentsOfChild.add(parent2);

        assertEquals("child should have 2 parents",
                2,
                parentsOfChild.size());
        assertSame("child has wrong primary parent",
                parent1,
                child.getParent());
        assertSame("parent1 has wrong child",
                child,
                parent1.getChildren().get(0));
        assertSame("child has wrong second parent",
                parent2,
                child.getParents().get(1));
        assertSame("parent2 has wrong child",
                child,
                parent2.getChildren().get(0));
    }

    @Test
    public void testTreeEquals()
    {
        SourceElement original = new SourceElement("root");
        SourceElement toCompare
            = new SourceElement("wrongName");
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different names must not be equal");
        }

        toCompare = new SourceElement("root");
        if (!original.graphEquals(toCompare))
        {
            fail("empty SourceElements with equal names must be equal");
        }

        original.setAttribute("attributeName", "attributeValue");
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different attribute names"
                    + " must not be equal");
        }

        toCompare.setAttribute("attributeName", "anotherAttributeValue");
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different attribute content"
                    + " must not be equal(1)");
        }

        toCompare.setAttribute("attributeName", null);
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different attribute content"
                    + " must not be equal(2)");
        }

        toCompare.setAttribute("attributeName", "attributeValue");
        if (!original.graphEquals(toCompare))
        {
            fail("SourceElements with equal attribute content"
                    + " must be equal(1)");
        }

        original.setAttribute("attributeName", null);
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different attribute content"
                    + " must not be equal(3)");
        }

        toCompare.setAttribute("attributeName", null);
        if (!original.graphEquals(toCompare))
        {
            fail("SourceElements with equal attribute content"
                    + " must be equal(2)");
        }

        original.getChildren().add(new SourceElement("child"));
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different children"
                    + " must not be equal(1)");
        }

        toCompare.getChildren().add(new SourceElement("wrongChild"));
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different children"
                    + " must not be equal(2)");
        }

        toCompare = new SourceElement("root");
        toCompare.setAttribute("attributeName", null);
        toCompare.getChildren().add(new SourceElement("child"));
        if (!original.graphEquals(toCompare))
        {
            fail("SourceElements with equal children"
                    + " must be equal(1)");
        }

        original.getChildren().add(original);
        if (original.graphEquals(toCompare))
        {
            fail("SourceElements with different children"
                    + " must not be equal(3)");
        }

        toCompare.getChildren().add(toCompare);
        if (!original.graphEquals(toCompare))
        {
            fail("SourceElements with equal children"
                    + " must be equal(2)");
        }
    }

    @Test
    public void testSetAttribute()
    {
        SourceElement sourceElement = new SourceElement("root");
        assertEquals(sourceElement.getAttributeNames().size(), 0);
        assertEquals(null, sourceElement.getAttribute("attributeName"));

        sourceElement.setAttribute("attributeName", "123");
        assertEquals(1, sourceElement.getAttributeNames().size());
        assertEquals("123", sourceElement.getAttribute("attributeName"));
    }

    @Test
    public void testSetAttributeToNull()
    {
        SourceElement sourceElement = new SourceElement("root");
        sourceElement.setAttribute("attributeName", "123");
        assertEquals(1, sourceElement.getAttributeNames().size());

        sourceElement.setAttribute("attributeName", null);
        assertEquals(0, sourceElement.getAttributeNames().size());
    }

    @Test
    public void testToString() throws Exception
    {
        SourceElement sourceElement = new SourceElement("root");
        sourceElement.setAttribute("attributeName", "123");
        sourceElement.getChildren().add(new SourceElement("child"));
        sourceElement.getChildren().add(sourceElement);
        String expected
        = "(name=root,attributes=(attributeName=123),"
                + "children=((name=child,attributes=(),children=()),"
                + "<<loop detected>>))";
        assertEquals(expected, sourceElement.toString());
    }

    @Test
    public void testCopySourceElement() throws Exception
    {
        ProjectPaths projectPaths
            = new Maven2DirectoryProjectPaths(
                new File("src/test/propertyToJava"));
        File propertiesFile
            = new File(
                projectPaths.getDefaultSourcePath(),
                "propertiesData.properties");
        FileSource fileSource
            = new FileSource(
                new PropertiesSourceFormat(),
                propertiesFile,
                new ControllerState());
        SourceElement rootElement
        = fileSource.getRootElement();

        SourceElement copiedRootElement = rootElement.copy();
        if (!rootElement.graphEquals(copiedRootElement))
        {
            fail("copied tree does not equal original tree");
        }
    }
}
