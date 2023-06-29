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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.torque.generator.GeneratorException;
import org.junit.Before;
import org.junit.Test;

public class SourcePathTest
{
    private SourceElement root;
    private SourceElement firstLevel1;
    private SourceElement firstLevel2;
    private SourceElement firstLevel3;
    private SourceElement secondLevel;
    private SourceElement secondLevela;
    private SourceElement secondLevelb;
    private SourceElement thirdLevel;
    private Root modelRoot;

    @Before
    public void setUp()
    {
        root = new SourceElement("root");
        firstLevel1 = new SourceElement("firstLevel1");
        root.getChildren().add(firstLevel1);
        firstLevel2  = new SourceElement("firstLevel2");
        root.getChildren().add(firstLevel2);
        firstLevel3 = new SourceElement("firstLevel3");
        root.getChildren().add(firstLevel3);
        secondLevel = new SourceElement("secondLevel");
        firstLevel2.getChildren().add(secondLevel);
        thirdLevel = new SourceElement("thirdLevel");
        secondLevel.getChildren().add(thirdLevel);
        secondLevela = new SourceElement("secondLevel");
        firstLevel2.getChildren().add(secondLevela);
        secondLevelb = new SourceElement("secondLevel");
        firstLevel2.getChildren().add(secondLevelb);

        modelRoot = new Root();
        modelRoot.childList.add(new Child("child1", null));
        modelRoot.childList.add(new Child("child2", null));
        modelRoot.stringList.add("string1");
        modelRoot.stringList.add("string2");
        modelRoot.getPropertyList().add("propertyString1");
        modelRoot.getPropertyList().add("propertyString2");
        modelRoot.fieldValue = "fieldValue";
        modelRoot.setPropertyValue("propertyValue");
        modelRoot.setValue("setterValue");
        modelRoot.value = "fieldValue";
        modelRoot.child = new Child("child3", new Child("subchildValue", null));

    }

    @Test
    public void testGetPath() throws GeneratorException
    {
        assertEquals(
                "root/firstLevel2/secondLevel/thirdLevel",
                SourcePath.getPathAsString(thirdLevel));
    }

    @Test(expected = GeneratorException.class)
    public void testGetPathInfiniteLoop() throws GeneratorException
    {
        final SourceElement loopElement1 = new SourceElement("loopElement1");
        final SourceElement loopElement2 = new SourceElement("loopElement2");
        loopElement1.getChildren().add(loopElement2);
        loopElement2.getChildren().add(loopElement1);
        SourcePath.getPathAsString(loopElement1);
    }

    @Test
    public void testGetElementFromRootWithSlash()
    {
        final List<SourceElement> result
        = SourcePath.getElementsFromRoot(root, "/");
        final List<SourceElement> expected = new ArrayList<>();
        expected.add(root);
        assertEquals(expected, result);
    }

    @Test
    public void testGetElementFromRootSingleRelativeElement()
    {
        final List<SourceElement> result
        = SourcePath.getElementsFromRoot(root, "root");
        final List<SourceElement> expected = new ArrayList<>();
        expected.add(root);
        assertEquals(expected, result);
    }

    @Test
    public void testGetElementFromRootSingleRelativeElementNoMatch()
    {
        final List<SourceElement> result
        = SourcePath.getElementsFromRoot(root, "root1");
        assertEquals(0, result.size());
    }

    @Test
    public void testGetElementFromRootSingleAbsoluteElement()
    {
        final List<SourceElement> result
        = SourcePath.getElementsFromRoot(root, "/root");
        final List<SourceElement> expected = new ArrayList<>();
        expected.add(root);
        assertEquals(expected, result);
    }

    @Test
    public void testGetElementFromRootSingleAbsoluteElementNoMatch()
    {
        final List<SourceElement> result
        = SourcePath.getElementsFromRoot(root, "/root1");
        assertEquals(0, result.size());
    }

    @Test
    public void testGetElementFromRootWildcardAtStart()
    {
        final List<SourceElement> result
        = SourcePath.getElementsFromRoot(root, "/*");
        final List<SourceElement> expected = new ArrayList<>();
        expected.add(root);
        assertEquals(expected, result);
    }

    @Test
    public void testGetElementFromRootWildcardInMiddle()
    {
        final List<SourceElement> result
        = SourcePath.getElementsFromRoot(root, "/root/*/secondLevel");
        final List<SourceElement> expected = new ArrayList<>();
        expected.add(secondLevel);
        expected.add(secondLevela);
        expected.add(secondLevelb);
        assertEquals(expected, result);
    }

    // does not yet work
    //    @Test
    //    public void testGetElementFromRootBacktoRootWithParent()
    //    {
    //        List<SourceElement> result
    //                = SourcePath.getElementsFromRoot(root, "/root/../root");
    //        List<SourceElement> expected = new ArrayList<SourceElement>();
    //        expected.add(root);
    //        assertEquals(expected, result);
    //    }

    @Test
    public void testGetElementFromRootParentInMiddle()
    {
        final List<SourceElement> result = SourcePath.getElementsFromRoot(
                root,
                "/root/firstLevel1/../firstLevel2");
        final List<SourceElement> expected = new ArrayList<>();
        expected.add(firstLevel2);
        assertEquals(expected, result);
    }

    @Test
    public void testGetPreceding()
    {
        final List<SourceElement> result
        = SourcePath.getPreceding(firstLevel2, "firstLevel1");
        assertEquals(1, result.size());
        assertSame(firstLevel1, result.get(0));
    }

    @Test
    public void testGetPrecedingNoMatch()
    {
        final List<SourceElement> result
        = SourcePath.getPreceding(firstLevel2, "firstLevel2");
        assertEquals(0, result.size());
    }
    @Test

    public void testGetFollowing()
    {
        final List<SourceElement> result
        = SourcePath.getFollowing(firstLevel2, "firstLevel3");
        assertEquals(1, result.size());
        assertSame(firstLevel3, result.get(0));
    }

    @Test
    public void testGetFollowingNoMatch()
    {
        final List<SourceElement> result
        = SourcePath.getFollowing(firstLevel2, "firstLevel2");
        assertEquals(0, result.size());
    }

    @Test
    public void testIteratePointerGetRoot()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                root,
                "/",
                root,
                "/");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(root, "/root[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerGetRootBaseNull()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                root,
                "/");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(root, "/root[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerGetChildRootSameAsBase()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                root,
                "/",
                root,
                "firstLevel1");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(firstLevel1, "/root[1]/firstLevel1[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerGetChildRootNull()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                root,
                "firstLevel1");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(firstLevel1, "/root[1]/firstLevel1[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerSeveralMatches()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                root,
                "/firstLevel2/secondLevel");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevel,
                        "/root[1]/firstLevel2[1]/secondLevel[1]"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevela,
                        "/root[1]/firstLevel2[1]/secondLevel[2]"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevelb,
                        "/root[1]/firstLevel2[1]/secondLevel[3]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerSeveralMatchesintermediateBase()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                root,
                "/root/firstLevel2",
                firstLevel2,
                "secondLevel");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevel,
                        "/root/firstLevel2[1]/secondLevel[1]"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevela,
                        "/root/firstLevel2[1]/secondLevel[2]"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevelb,
                        "/root/firstLevel2[1]/secondLevel[3]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    /**
     * Test that we can iterate over path where the parent element
     * of the matching elements is not the primary parent element.
     */
    @Test
    public void testIteratePointerNotDefaultParent()
    {
        firstLevel1.getChildren().add(secondLevela);
        firstLevel1.getChildren().add(secondLevel);
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                root,
                "firstLevel1/secondLevel");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevela,
                        "/root[1]/firstLevel1[1]/secondLevel[1]"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        secondLevel,
                        "/root[1]/firstLevel1[1]/secondLevel[2]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    /**
     * Test that we can iterate over the parent element.
     */
    @Test
    public void testIteratePointerGetParent()
    {
        firstLevel1.getChildren().add(secondLevela);
        firstLevel1.getChildren().add(secondLevel);
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                root,
                "firstLevel1/secondLevel[2]",
                secondLevel,
                "..");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(
                        firstLevel1,
                        "/root/firstLevel1[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelStringSelf()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                root,
                ".");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        root,
                        "/root[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelStringField()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                modelRoot,
                "/",
                modelRoot,
                "fieldValue");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "fieldValue",
                        "/fieldValue"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelStringFieldRootNull()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "fieldValue");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "fieldValue",
                        "/fieldValue"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelStringGetter()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "propertyValue");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "propertyValue",
                        "/propertyValue"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelStringFieldAndGetter()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "value");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "setterValue",
                        "/value"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelListField()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "stringList");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "string1",
                        "/stringList[1]"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "string2",
                        "/stringList[2]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelListDotWithBaseField()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                modelRoot,
                "/childList[1]",
                modelRoot.childList.get(0),
                ".");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        modelRoot.childList.get(0),
                        "/childList[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelListProperty()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "propertyList");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "propertyString1",
                        "/propertyList[1]"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "propertyString2",
                        "/propertyList[2]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelListPropertyWithIndex()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "propertyList[2]");
        assertTrue(resultIt.hasNext());
        assertEquals(
                new SourcePathPointer(
                        "propertyString2",
                        "/propertyList[2]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelListChainedAttribute()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "childList/value");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer("child1", "/childList[1]/value"),
                resultIt.next());
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer("child2", "/childList[2]/value"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelPublicListWithIndex()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                null,
                null,
                modelRoot,
                "/childList[1]");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer(modelRoot.childList.get(0), "/childList[1]"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    @Test
    public void testIteratePointerModelListChainedAttributeIntermediateBase()
    {
        final Iterator<SourcePathPointer> resultIt = SourcePath.iteratePointer(
                modelRoot,
                "child",
                modelRoot.child,
                "/child/subchild/value");
        assertTrue(resultIt.hasNext());
        assertSamePointer(
                new SourcePathPointer("subchildValue", "/child/subchild/value"),
                resultIt.next());
        assertFalse(resultIt.hasNext());
    }

    public static class Root
    {
        public List<String> stringList = new ArrayList<>();

        public List<Child> childList = new ArrayList<>();

        private final List<String> propertyList = new ArrayList<>();

        public String fieldValue;

        public String value;

        private String setterValue;

        private String propertyValue;

        private Child child;

        public List<String> getPropertyList()
        {
            return propertyList;
        }

        public String getPropertyValue()
        {
            return propertyValue;
        }

        public void setPropertyValue(final String v)
        {
            this.propertyValue = v;
        }

        public String getValue()
        {
            return setterValue;
        }

        public void setValue(final String v)
        {
            this.setterValue = v;
        }

        public Child getChild()
        {
            return child;
        }

        public void setChild(final Child child)
        {
            this.child = child;
        }
    }

    public static class Child
    {
        public Child(final String value, final Child subchild)
        {
            this.value = value;
            this.subchild = subchild;
        }

        public String value;

        public Child subchild;
    }

    public static void assertSamePointer(
            final SourcePathPointer expected,
            final SourcePathPointer actual)
    {
        if (expected == null)
        {
            assertNull(actual);
        }
        else
        {
            assertNotNull(actual);
            assertSame(expected.getValue(), actual.getValue());
            assertEquals(expected.getPath(), actual.getPath());
        }
    }
}
