package org.apache.torque.generator.source.transform.model;

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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the PropertyAccess class.
 *
 * @version $Id: $
 */
public class PropertyAccessTest
{
    /** the class to set properties on. */
    private TestClass testClass;

    @Before
    public void setUp()
    {
        testClass = new TestClass();
    }

    @Test
    public void testPropertyAccessTargetNull() throws Exception
    {
        try
        {
            new PropertyAccess(null, "publicIntField");
            fail("Exception expected");
        }
        catch (final NullPointerException e)
        {
            assertEquals("target must not be null", e.getMessage());
        }
    }

    @Test
    public void testPropertyAccessPropertyNameNull() throws Exception
    {
        try
        {
            new PropertyAccess(testClass, null);
            fail("Exception expected");
        }
        catch (final NullPointerException e)
        {
            assertEquals("propertyName must not be null", e.getMessage());
        }
    }

    @Test
    public void testSetPropertyNotExistentField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "fieldDoesNotExist");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final NoSuchPropertyException e)
        {
            assertEquals("Neither public field nor public getter/setter exists "
                    + "for property fieldDoesNotExist or fieldDoesNotExists "
                    + "or fieldDoesNotExistArray or fieldDoesNotExistList "
                    + "and no public field exists for property "
                    + "_fieldDoesNotExist of class "
                    + "org.apache.torque.generator.source.transform.model"
                    + ".PropertyAccessTest$TestClass",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPrivateField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "privateIntField");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final NoSuchPropertyException e)
        {
            assertEquals("Neither public field nor public getter/setter exists "
                    + "for property privateIntField or privateIntFields "
                    + "or privateIntFieldArray or privateIntFieldList "
                    + "and no public field exists for property "
                    + "_privateIntField of class "
                    + "org.apache.torque.generator.source.transform.model"
                    + ".PropertyAccessTest$TestClass",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyProtectedField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "protectedIntField");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final NoSuchPropertyException e)
        {
            assertEquals("Neither public field nor public getter/setter exists "
                    + "for property protectedIntField or protectedIntFields "
                    + "or protectedIntFieldArray or protectedIntFieldList "
                    + "and no public field exists for property "
                    + "_protectedIntField of class "
                    + "org.apache.torque.generator.source.transform.model"
                    + ".PropertyAccessTest$TestClass",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "intField");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final NoSuchPropertyException e)
        {
            assertEquals("Neither public field nor public getter/setter exists "
                    + "for property intField or intFields "
                    + "or intFieldArray or intFieldList "
                    + "and no public field exists for property "
                    + "_intField of class "
                    + "org.apache.torque.generator.source.transform.model"
                    + ".PropertyAccessTest$TestClass",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPublicField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntField");
        propertyAccess.setProperty(2);
        assertEquals(2, testClass.publicIntField);
    }

    /**
     * Check whether a field ending with s can be accessed without
     * the ending s.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSetPropertyPublicFieldEndingWithS() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicFieldEndingWith");
        propertyAccess.setProperty(2);
        assertEquals(2, testClass.publicFieldEndingWiths);
    }

    /**
     * Check whether a field starting with an underscore can be accessed
     * without the underscore.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSetPropertyPublicFieldStartingWithUnderscore()
            throws Exception
    {
        final PropertyAccess propertyAccess = new PropertyAccess(
                testClass,
                "publicFieldStartingWithUnderscore");
        propertyAccess.setProperty(2);
        assertEquals(2, testClass._publicFieldStartingWithUnderscore);
    }

    @Test
    public void testAccessPublicFieldFromBaseClass() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntFieldFromBaseClass");
        propertyAccess.setProperty(2);
        assertEquals(2, testClass.publicIntFieldFromBaseClass);
    }

    @Test
    public void testSetPropertyPublicFieldWrongClass() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntField");
        try
        {
            propertyAccess.setProperty("abc");
            fail("Exception expected");
        }
        catch (final SourceTransformerException e)
        {
            assertEquals("The field publicIntField of class "
                    + "org.apache.torque.generator.source.transform.model"
                    + ".PropertyAccessTest$TestClass cannot be set to abc "
                    + "because the argument has the wrong type "
                    + "java.lang.String",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyIntFieldNull() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntField");
        try
        {
            propertyAccess.setProperty(null);
            fail("Exception expected");
        }
        catch (final SourceTransformerException e)
        {
            assertEquals("The field publicIntField of class "
                    + "org.apache.torque.generator.source.transform.model"
                    + ".PropertyAccessTest$TestClass because the value is null"
                    + " which is not allowed",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPublicStringField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringField");
        propertyAccess.setProperty("abc");
        assertEquals("abc", testClass.publicStringField);
    }

    @Test
    public void testSetPropertyPublicStringFieldNull() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringField");
        testClass.publicStringField = "abc";
        propertyAccess.setProperty(null);
        assertEquals(null, testClass.publicStringField);
    }

    @Test
    public void testSetPropertyPublicStringArrayField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArrayField");
        propertyAccess.setProperty("abc");
        assertArrayEquals(new Object[] {"abc"}, testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArrayFieldAlreadyFilled()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArrayField");
        testClass.publicStringArrayField = new String[] {"def", "XYZ"};
        propertyAccess.setProperty("abc");
        assertArrayEquals(
                new String[] {"def", "XYZ", "abc"},
                testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArrayFieldNull() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArrayField");
        testClass.publicStringArrayField = new String[] {"abc"};
        propertyAccess.setProperty(null);
        assertArrayEquals(
                new String[] {"abc", null},
                testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArrayFieldArray() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArrayField");
        testClass.publicStringArrayField = new String[] {"XYZ", "def"};
        propertyAccess.setProperty(new String[] {"abc"});
        assertArrayEquals(new String[] {"abc"}, testClass.publicStringArrayField);
    }


    @Test
    public void testSetPropertyPublicStringCollectionField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionField");
        propertyAccess.setProperty("abc");
        final List<String> expected = new ArrayList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    @Test
    public void testSetPropertyPublicStringCollectionFieldAlreadyFilled()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionField");
        final List<String> initial = new ArrayList<>();
        initial.add("def");
        initial.add("XYZ");
        testClass.publicStringCollectionField = initial;
        propertyAccess.setProperty("abc");
        final List<String> expected = new ArrayList<>();
        expected.add("def");
        expected.add("XYZ");
        expected.add("abc");
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    @Test
    public void testSetPropertyPublicStringCollectionFieldNull() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionField");
        testClass.publicStringField = "abc";
        propertyAccess.setProperty(null);
        final List<String> expected = new ArrayList<>();
        expected.add(null);
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    /**
     * Checks that a collection field gets overwritten if it is set
     * using a collection.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSetPropertyPublicStringCollectionFieldCollection()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionField");
        final List<String> initial = new ArrayList<>();
        initial.add("def");
        initial.add("XYZ");
        testClass.publicStringCollectionField = initial;
        final List<String> setValue = new ArrayList<>();
        setValue.add("abc");
        propertyAccess.setProperty(setValue);
        final List<String> expected = new ArrayList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    @Test
    public void testSetPropertyPublicStringListField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringListField");
        propertyAccess.setProperty("abc");
        final List<String> expected = new ArrayList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringListField);
    }

    @Test
    public void testSetPropertyPublicStringSetField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringSetField");
        propertyAccess.setProperty("abc");
        final Set<String> expected = new HashSet<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringSetField);
    }

    @Test
    public void testSetPropertyPublicStringQueueField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringQueueField");
        propertyAccess.setProperty("abc");
        final LinkedList<String> expected = new LinkedList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringQueueField);
    }

    @Test
    public void testSetPropertyPublicStringVectorField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringVectorField");
        propertyAccess.setProperty("abc");
        final Vector<String> expected = new Vector<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringVectorField);
    }

    @Test
    public void testSetPropertyPublicBooleanFieldUsingConverter()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicBooleanField");
        propertyAccess.setProperty("true");
        assertEquals(Boolean.TRUE, testClass.publicBooleanField);
    }

    @Test
    public void testSetPropertyOnlyGetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "onlyGetter");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final PropertyNotWriteableException e)
        {
            assertEquals("The property onlyGetter of class "
                    + testClass.getClass().getName() + " is not writeable",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPrivateSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "privateIntSetter");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final NoSuchPropertyException e)
        {
            assertEquals("Neither public field nor public getter/setter exists "
                    + "for property privateIntSetter or privateIntSetters "
                    +  "or privateIntSetterArray or privateIntSetterList "
                    + "and no public field exists for property "
                    + "_privateIntSetter of class "
                    + testClass.getClass().getName(),
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyProtectedSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "protectedIntSetter");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final NoSuchPropertyException e)
        {
            assertEquals("Neither public field nor public getter/setter exists "
                    + "for property protectedIntSetter or protectedIntSetters "
                    +  "or protectedIntSetterArray or protectedIntSetterList "
                    + "and no public field exists for property "
                    + "_protectedIntSetter of class "
                    + testClass.getClass().getName(),
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertySetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "intSetter");
        try
        {
            propertyAccess.setProperty(2);
            fail("Exception expected");
        }
        catch (final NoSuchPropertyException e)
        {
            assertEquals("Neither public field nor public getter/setter exists "
                    + "for property intSetter or intSetters "
                    +  "or intSetterArray or intSetterList "
                    + "and no public field exists for property "
                    + "_intSetter of class "
                    + testClass.getClass().getName(),
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPublicSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntSetter");
        propertyAccess.setProperty(2);
        assertEquals(2, testClass.publicIntField);
    }

    @Test
    public void testSetPropertyPublicSetterEndingWithS() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicSetterEndingWith");
        propertyAccess.setProperty(2);
        assertEquals(2, testClass.publicFieldEndingWiths);
    }

    @Test
    public void testSetPropertyPublicSetterFromBaseClass() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntSetterFromBaseClass");
        propertyAccess.setProperty(2);
        assertEquals(2, testClass.publicIntFieldFromBaseClass);
    }

    @Test
    public void testSetPropertyPublicSetterWrongClass() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntSetter");
        try
        {
            propertyAccess.setProperty("abc");
            fail("Exception expected");
        }
        catch (final SourceTransformerException e)
        {
            assertEquals("The field publicIntSetter of class "
                    + testClass.getClass().getName()
                    + " cannot be set to abc "
                    + "because the argument has the wrong type "
                    + "java.lang.String",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyIntSetterNull() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntSetter");
        try
        {
            propertyAccess.setProperty(null);
            fail("Exception expected");
        }
        catch (final SourceTransformerException e)
        {
            assertEquals("The field publicIntSetter of class "
                    + testClass.getClass().getName()
                    + " because the value is null"
                    + " which is not allowed",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPublicStringSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringSetter");
        propertyAccess.setProperty("abc");
        assertEquals("abc", testClass.publicStringField);
    }

    @Test
    public void testSetPropertyPublicStringSetterNull() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringSetter");
        testClass.publicStringField = "abc";
        propertyAccess.setProperty(null);
        assertArrayEquals(null, testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArraySetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArraySetter");
        propertyAccess.setProperty("abc");
        assertArrayEquals(new Object[] {"abc"}, testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArraySetterAlreadyFilled()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArraySetter");
        testClass.publicStringArrayField = new String[] {"def", "XYZ"};
        propertyAccess.setProperty("abc");
        assertArrayEquals(
                new Object[] {"def", "XYZ", "abc"},
                testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArraySetterNull() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArraySetter");
        testClass.publicStringArrayField = new String[] {"abc"};
        propertyAccess.setProperty(null);
        assertArrayEquals(new Object[] {"abc", null}, testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArraySetterArray() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArraySetter");
        propertyAccess.setProperty(new String[] {"abc"});
        assertArrayEquals(new String[] {"abc"}, testClass.publicStringArrayField);
    }

    @Test
    public void testSetPropertyPublicStringArraySetterWithoutGetter()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArraySetterWithoutGetter");
        try
        {
            propertyAccess.setProperty("abc");
            fail("Exception expected");
        }
        catch (final PropertyNotReadableException e)
        {
            assertEquals("The property publicStringArraySetterWithoutGetter "
                    + "of class "
                    + testClass.getClass().getName()
                    + " is not readable",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPublicStringArrayGetterWithoutSetter()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringArrayGetterWithoutSetter");
        try
        {
            propertyAccess.setProperty("abc");
            fail("Exception expected");
        }
        catch (final PropertyNotWriteableException e)
        {
            assertEquals("The property publicStringArrayGetterWithoutSetter "
                    + "of class "
                    + testClass.getClass().getName()
                    + " is not writeable",
                    e.getMessage());
        }
    }

    @Test
    public void testSetPropertyPublicStringCollectionSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionSetter");
        propertyAccess.setProperty("abc");
        final List<String> expected = new ArrayList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    @Test
    public void testSetPropertyPublicStringCollectionSetterAlreadyFilled()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionSetter");
        final List<String> initial = new ArrayList<>();
        initial.add("def");
        initial.add("XYZ");
        testClass.publicStringCollectionField = initial;
        propertyAccess.setProperty("abc");
        final List<String> expected = new ArrayList<>();
        expected.add("def");
        expected.add("XYZ");
        expected.add("abc");
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    @Test
    public void testSetPropertyPublicStringCollectionSetterNull()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionSetter");
        testClass.publicStringField = "abc";
        propertyAccess.setProperty(null);
        final List<String> expected = new ArrayList<>();
        expected.add(null);
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    /**
     * Checks that a collection setter field gets overwritten if it is set
     * using a collection.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSetPropertyPublicStringCollectionSetterCollection()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionSetter");
        final List<String> initial = new ArrayList<>();
        initial.add("def");
        initial.add("XYZ");
        testClass.publicStringCollectionField = initial;
        final List<String> setValue = new ArrayList<>();
        setValue.add("abc");
        propertyAccess.setProperty(setValue);
        final List<String> expected = new ArrayList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    @Test
    public void testSetPropertyPublicStringListSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringListSetter");
        propertyAccess.setProperty("abc");
        final List<String> expected = new ArrayList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringListField);
    }

    @Test
    public void testSetPropertyPublicStringSetSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringSetSetter");
        propertyAccess.setProperty("abc");
        final Set<String> expected = new HashSet<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringSetField);
    }

    @Test
    public void testSetPropertyPublicStringQueueSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringQueueSetter");
        propertyAccess.setProperty("abc");
        final LinkedList<String> expected = new LinkedList<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringQueueField);
    }

    @Test
    public void testSetPropertyPublicStringVectorSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringVectorSetter");
        propertyAccess.setProperty("abc");
        final Vector<String> expected = new Vector<>();
        expected.add("abc");
        assertEquals(expected, testClass.publicStringVectorField);
    }

    /**
     * Checks that a collection setter field gets overwritten if it is set
     * using a collection.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSetPropertyPublicStringCollectionOnlyGetter()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionGetterWithoutSetter");
        try
        {
            propertyAccess.setProperty("abc");
            fail("Exception expected");
        }
        catch (final PropertyNotWriteableException e)
        {
            assertEquals("The property "
                    + "publicStringCollectionGetterWithoutSetter of class "
                    + testClass.getClass().getName() + " is not writeable",
                    e.getMessage());
        }
    }

    /**
     * Checks that a collection setter field gets overwritten if it is set
     * using a collection.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSetPropertyPublicStringCollectionOnlyGetterAlreadyFilled()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionGetterWithoutSetter");
        final List<String> initial = new ArrayList<>();
        initial.add("def");
        initial.add("XYZ");
        testClass.publicStringCollectionField = initial;
        propertyAccess.setProperty("abc");
        final List<String> expected = new ArrayList<>();
        expected.add("def");
        expected.add("XYZ");
        expected.add("abc");
        assertEquals(expected, testClass.publicStringCollectionField);
    }

    @Test
    public void testSetPropertyPublicBooleanSetterWithConverter()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicBooleanSetter");
        propertyAccess.setProperty("true");
        assertEquals(Boolean.TRUE, testClass.publicBooleanField);
    }

    // start tests of getPropertyType method

    @Test
    public void testGetPropertyTypeNotExistentField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "fieldDoesNotExist");
        assertEquals(null, propertyAccess.getPropertyType());
    }

    @Test
    public void testGetPropertyTypePrivateField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "privateIntField");
        assertEquals(null, propertyAccess.getPropertyType());
    }

    @Test
    public void testGetPropertyTypePublicIntField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntField");
        assertEquals(int.class, propertyAccess.getPropertyType());
    }

    @Test
    public void testGetPropertyTypePublicStringField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringField");
        assertEquals(String.class, propertyAccess.getPropertyType());
    }

    @Test
    public void testGetPropertyTypePublicIntSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntSetter");
        assertEquals(int.class, propertyAccess.getPropertyType());
    }

    @Test
    public void testGetPropertyTypePublicStringSetter() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringSetter");
        assertEquals(String.class, propertyAccess.getPropertyType());
    }

    // start tests of getFirstGenericTypeArgument method

    @Test
    public void testGetFirstGenericTypeArgumentNotExistentField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "fieldDoesNotExist");
        assertEquals(null, propertyAccess.getFirstGenericTypeArgument());
    }

    @Test
    public void testGetFirstGenericTypeArgumentPrivateField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "privateIntField");
        assertEquals(null, propertyAccess.getFirstGenericTypeArgument());
    }

    @Test
    public void testGetFirstGenericTypeArgumentPublicIntField() throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicIntField");
        assertEquals(null, propertyAccess.getFirstGenericTypeArgument());
    }

    @Test
    public void testGetFirstGenericTypeArgumentPublicCollectionField()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionField");
        assertEquals(String.class, propertyAccess.getFirstGenericTypeArgument());
    }

    @Test
    public void testGetFirstGenericTypeArgumentPublicCollectionSetter()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(testClass, "publicStringCollectionSetter");
        assertEquals(String.class, propertyAccess.getFirstGenericTypeArgument());
    }

    @Test
    public void testGetFirstGenericTypeArgumentPublicCollectionGetterWithoutSetter()
            throws Exception
    {
        final PropertyAccess propertyAccess
            = new PropertyAccess(
                testClass,
                "publicStringCollectionGetterWithoutSetter");
        assertEquals(String.class, propertyAccess.getFirstGenericTypeArgument());
    }

    public static class TestClass extends TestBaseClass
    {
        protected int protectedIntField;

        int intField;

        public int publicIntField;

        public String publicStringField;

        public Boolean publicBooleanField;

        public String[] publicStringArrayField;

        public Collection<String> publicStringCollectionField;

        public List<String> publicStringListField;

        public Set<String> publicStringSetField;

        public Queue<String> publicStringQueueField;

        public Vector<String> publicStringVectorField;

        public int publicFieldEndingWiths;

        public int _publicFieldStartingWithUnderscore;

        public int getOnlyGetter()
        {
            return 0;
        }

        protected void setProtectedIntSetter(final int value)
        {
            protectedIntField = value;
        }

        void setIntSetter(final int value)
        {
            intField = value;
        }

        @Override
        public void setPublicIntSetter(final int value)
        {
            publicIntField = value;
        }

        public void setPublicStringSetter(final String value)
        {
            publicStringField = value;
        }

        public void setPublicBooleanSetter(final Boolean value)
        {
            publicBooleanField = value;
        }

        public void setPublicStringArraySetter(final String[] value)
        {
            publicStringArrayField = value;
        }

        public String[] getPublicStringArraySetter()
        {
            return publicStringArrayField;
        }

        public void setPublicStringArraySetterWithoutGetter(final String[] value)
        {
            publicStringArrayField = value;
        }

        public String[] getPublicStringArrayGetterWithoutSetter()
        {
            return publicStringArrayField;
        }

        public void setPublicStringCollectionSetter(final Collection<String> value)
        {
            publicStringCollectionField = value;
        }

        public Collection<String> getPublicStringCollectionSetter()
        {
            return publicStringCollectionField;
        }

        public void setPublicStringCollectionSetterWithoutGetter(final Collection<String> value)
        {
            publicStringCollectionField = value;
        }

        public Collection<String> getPublicStringCollectionGetterWithoutSetter()
        {
            return publicStringCollectionField;
        }

        public void setPublicStringListSetter(final List<String> value)
        {
            publicStringListField = value;
        }

        public List<String> getPublicStringListSetter()
        {
            return publicStringListField;
        }

        public void setPublicStringSetSetter(final Set<String> value)
        {
            publicStringSetField = value;
        }

        public Set<String> getPublicStringSetSetter()
        {
            return publicStringSetField;
        }

        public void setPublicStringQueueSetter(final Queue<String> value)
        {
            publicStringQueueField = value;
        }

        public Queue<String> getPublicStringQueueSetter()
        {
            return publicStringQueueField;
        }

        public void setPublicStringVectorSetter(final Vector<String> value)
        {
            publicStringVectorField = value;
        }

        public Vector<String> getPublicStringVectorSetter()
        {
            return publicStringVectorField;
        }

        public void setPublicSetterEndingWiths(final int value)
        {
            publicFieldEndingWiths = value;
        }

    }

    public static class TestBaseClass
    {
        public int publicIntFieldFromBaseClass;

        public int publicIntField; // to be overridden

        public void setPublicIntSetterFromBaseClass(final int value)
        {
            publicIntFieldFromBaseClass = value;
        }

        public void setPublicIntSetter(final int value) // to be overridden
        {
            publicIntField = value;
        }
    }
}
