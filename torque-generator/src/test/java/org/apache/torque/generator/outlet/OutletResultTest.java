package org.apache.torque.generator.outlet;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.torque.generator.GeneratorException;
import org.junit.Before;
import org.junit.Test;

/**
 * Component tests for OutletResult.
 *
 * @version $Id: $
 *
 */
public class OutletResultTest
{
    private static final String STRING_INITIAL_CONTENT = "String!\r\ncontent";

    private static final byte[] BYTE_ARRAY_INITIAL_CONTENT
        = new byte[] {34, -56, 126, -127 };

    /** System under test. */
    private OutletResult stringOutletResult;

    /** System under test. */
    private OutletResult byteArrayOutletResult;

    @Before
    public void setUp()
    {
        byteArrayOutletResult = new OutletResult(BYTE_ARRAY_INITIAL_CONTENT);
        stringOutletResult = new OutletResult(STRING_INITIAL_CONTENT);
    }

    /**
     * Checks that the getStringResult() method returns the correct content.
     */
    @Test
    public void testGetStringResult()
    {
        assertEquals(
                STRING_INITIAL_CONTENT,
                stringOutletResult.getStringResult());
        assertEquals(
                null,
                byteArrayOutletResult.getStringResult());
    }

    /**
     * Checks that the getStringResult() method returns the correct content.
     */
    @Test
    public void testGetByteArrayResult()
    {
        assertEquals(
                null,
                stringOutletResult.getByteArrayResult());
        assertEquals(
                BYTE_ARRAY_INITIAL_CONTENT,
                byteArrayOutletResult.getByteArrayResult());
    }

    /**
     * Checks that the isStringResult() method returns the correct content.
     */
    @Test
    public void testIsStringResult()
    {
        assertTrue(stringOutletResult.isStringResult());
        assertFalse(byteArrayOutletResult.isStringResult());
        assertTrue(new OutletResult((byte[]) null).isStringResult());
        assertTrue(new OutletResult((String) null).isStringResult());
    }

    /**
     * Checks that the isByteArrayResult() method returns the correct content.
     */
    @Test
    public void testIsByteArrayResult()
    {
        assertFalse(stringOutletResult.isByteArrayResult());
        assertTrue(byteArrayOutletResult.isByteArrayResult());
        assertTrue(new OutletResult((byte[]) null).isByteArrayResult());
        assertTrue(new OutletResult((String) null).isByteArrayResult());
    }

    /**
     * Checks that the concatenate() throws an exception if
     * OutletResults of different types are concatenated.
     */
    @Test
    public void testConcatenateWrongTypes()
    {
        try
        {
            OutletResult.concatenate(stringOutletResult, byteArrayOutletResult);
            fail("Exception expected");
        }
        catch (GeneratorException e)
        {
            assertEquals("first OutletResult to concatenate is a "
                    + "String result but a following result is a "
                    + "byte array."
                    + " All concatenated results must be "
                    + "of the same type",
                    e.getMessage());
        }
        try
        {
            OutletResult.concatenate(byteArrayOutletResult, stringOutletResult);
            fail("Exception expected");
        }
        catch (GeneratorException e)
        {
            assertEquals("first OutletResult to concatenate is a "
                    + "byte array result but a following result is a "
                    + "String result."
                    + " All concatenated results must be "
                    + "of the same type",
                    e.getMessage());
        }
    }

    /**
     * Checks that the concatenate() method works for normal String results.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateString() throws GeneratorException
    {
        OutletResult result = OutletResult.concatenate(
                stringOutletResult,
                new OutletResult("bdf"),
                new OutletResult("123"));
        assertEquals(
                STRING_INITIAL_CONTENT + "bdf" + "123",
                result.getStringResult());
        assertEquals(null, result.getByteArrayResult());
        assertTrue(result.isStringResult());
        assertFalse(result.isByteArrayResult());
    }

    /**
     * Checks that the concatenate() method works for null String results.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateNullString() throws GeneratorException
    {
        OutletResult result = OutletResult.concatenate(
                stringOutletResult,
                new OutletResult((String) null));
        assertEquals(STRING_INITIAL_CONTENT, result.getStringResult());
    }

    /**
     * Checks that the concatenate() method works if we start
     * with a null string result.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateStartingWithNullString()
            throws GeneratorException
    {
        OutletResult result = OutletResult.concatenate(
                new OutletResult((String) null),
                stringOutletResult);
        assertEquals(STRING_INITIAL_CONTENT, result.getStringResult());
    }

    /**
     * Checks that the concatenate() method works for normal byte array results.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateByteArray() throws GeneratorException
    {
        OutletResult result = OutletResult.concatenate(
                byteArrayOutletResult,
                new OutletResult(new byte[] {11, -22}),
                new OutletResult(new byte[] {1, 0}));
        assertArrayEquals(
                new byte[] {34, -56, 126, -127, 11, -22, 1, 0},
                result.getByteArrayResult());
        assertEquals(null, result.getStringResult());
        assertFalse(result.isStringResult());
        assertTrue(result.isByteArrayResult());
    }

    /**
     * Checks that the concatenate() method works for null byte array results.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateNullByteArray() throws GeneratorException
    {
        OutletResult result = OutletResult.concatenate(
                byteArrayOutletResult,
                new OutletResult((byte[]) null));
        assertArrayEquals(
                BYTE_ARRAY_INITIAL_CONTENT,
                result.getByteArrayResult());
    }

    /**
     * Checks that the concatenate() method works if we start
     * with a null byte array result.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateStartingWithNullByteArray()
            throws GeneratorException
    {
        OutletResult result = OutletResult.concatenate(
                new OutletResult((byte[]) null),
                byteArrayOutletResult);
        assertArrayEquals(
                BYTE_ARRAY_INITIAL_CONTENT,
                result.getByteArrayResult());
    }

    /**
     * Checks that the concatenate() method throws an exception for a null
     * input.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateNullArray() throws GeneratorException
    {
        try
        {
            OutletResult.concatenate((OutletResult[]) null);
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
            assertEquals("input must not be null", e.getMessage());
        }
    }

    /**
     * Checks that the concatenate() method throws an exception for a null
     * input.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateNullList() throws GeneratorException
    {
        try
        {
            OutletResult.concatenate((Iterable<OutletResult>) null);
            fail("Exception expected");
        }
        catch (NullPointerException e)
        {
            assertEquals("input must not be null", e.getMessage());
        }
    }

    /**
     * Checks that the concatenate() method throws an exception for an empty
     * input.
     *
     * @throws GeneratorException
     */
    @Test
    public void testConcatenateEmpty() throws GeneratorException
    {
        try
        {
            OutletResult.concatenate();
            fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("input must not be empty", e.getMessage());
        }
    }

    /**
     * Checks that the equals() method returns the correct content.
     */
    @Test
    public void testEquals()
    {
        // null
        assertFalse(
                stringOutletResult.equals(null));

        // different class
        assertFalse(
                stringOutletResult.equals("abc"));

        // different content class
        assertFalse(
                stringOutletResult.equals(byteArrayOutletResult));

        // same object
        assertTrue(stringOutletResult.equals(stringOutletResult));

        // same string content
        assertTrue(
                stringOutletResult.equals(
                        new OutletResult(STRING_INITIAL_CONTENT)));

        // different string content
        assertFalse(
                stringOutletResult.equals(
                        new OutletResult("abc")));
        assertFalse(
                stringOutletResult.equals(
                        new OutletResult((String) null)));

        // same byte array content
        assertTrue(
                byteArrayOutletResult.equals(
                        new OutletResult(BYTE_ARRAY_INITIAL_CONTENT)));

        // different byte array content
        assertFalse(
                byteArrayOutletResult.equals(
                        new OutletResult(new byte[] {37, 51})));
        assertFalse(
                byteArrayOutletResult.equals(
                        new OutletResult((byte[]) null)));
    }


}
