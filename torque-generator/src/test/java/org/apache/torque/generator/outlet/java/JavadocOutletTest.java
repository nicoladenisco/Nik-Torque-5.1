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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.control.action.MergepointAction;
import org.apache.torque.generator.control.action.OutputAction;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;
import org.junit.Before;
import org.junit.Test;

public class JavadocOutletTest
{
    private JavadocOutlet javadocOutlet;

    @Before
    public void setUp()
    {
        javadocOutlet = new JavadocOutlet(new QualifiedName("jacadocOutlet"));
        javadocOutlet.setMaxLineLength(20);
    }

    /**
     * Tests that the javadoc contains both the body and the attributes
     * and both are properly line wrapped.
     */
    @Test
    public void testCompleteJavadoc() throws Exception
    {
        List<MergepointAction> mergepointActions
            = new ArrayList<>();
        mergepointActions.add(new OutputAction("Test-body for.a,javadoc"));
        javadocOutlet.setMergepointMapping(
                new MergepointMapping(
                        "body",
                        mergepointActions));
        mergepointActions = new ArrayList<>();
        mergepointActions.add(
                new OutputAction("@param param1 description,of param1"));
        javadocOutlet.setMergepointMapping(
                new MergepointMapping(
                        "attributes",
                        mergepointActions));
        OutletResult result = javadocOutlet.execute(new ControllerState());
        assertTrue(result.isStringResult());
        assertEquals(
                "    /**\n"
                        + "     * Test-body\n"
                        + "     * for.a,javadoc\n"
                        + "     *\n"
                        + "     * @param param1\n"
                        + "     *        description,\n"
                        + "     *        of\n"
                        + "     *        param1\n"
                        + "     */\n",
                        result.getStringResult());
    }

    /**
     * Checks that a break-after character can cause a line break , and that
     * a single space after such a break is removed. The boundary cases
     * maxLineLength + 1 and maxLineLength are checked.
     */
    @Test
    public void testWrapAfterNotRemovedCharacters()
    {
        String result = javadocOutlet.wrapLinesAndIndent(
                "Test-body111-breaking.at1.  111111 not;  removed1111,characters");
        assertEquals(
                "     * Test-body111-\n"
                        + "     * breaking.at1.\n"
                        + "     *  111111 not; \n"
                        + "     * removed1111,\n"
                        + "     * characters\n",
                        result);
    }

    /**
     * Checks that a space can cause a line break and the space is removed
     * in the output. The boundary cases maxLineLength + 1 and maxLineLength
     * are checked.
     */
    @Test
    public void testSpaceAtEndRemoved()
    {
        String result = javadocOutlet.wrapLinesAndIndent(
                "Test body1111 breaking at1 space");
        assertEquals(
                "     * Test body1111\n"
                        + "     * breaking at1\n"
                        + "     * space\n",
                        result);
    }

    /**
     * Checks that a new javadoc attribute causes a double line wrap
     */
    @Test
    public void testDoubleWrapAtNewAttribute()
    {
        String result = javadocOutlet.wrapLinesAndIndent(
                "body @since 1 2 3 4 5");
        assertEquals(
                "     * body\n"
                        + "     *\n"
                        + "     * @since 1 2 3\n"
                        + "     *        4 5\n",
                        result);
    }

    /**
     * Checks that between different javadoc attributes,
     * a double line wrap is inserted.
     */
    @Test
    public void testDoubleWrapAtDifferentAttributes()
    {
        String result = javadocOutlet.wrapLinesAndIndent(
                "@since 1 @see y");
        assertEquals(
                "     * @since 1\n"
                        + "     *\n"
                        + "     * @see y\n",
                        result);
    }

    /**
     * Checks that between different javadoc attributes
     * where the first ends with a line break,
     * a double line wrap is inserted.
     */
    @Test
    public void testDoubleWrapAtDifferentAttributesWithLineBreak()
    {
        String result = javadocOutlet.wrapLinesAndIndent(
                "@since 1\n@see y");
        assertEquals(
                "     * @since 1\n"
                        + "     *\n"
                        + "     * @see y\n",
                        result);
    }

    /**
     * Checks that a preceding \n or two same javadoc attribute
     * cause a single line wrap
     */
    @Test
    public void testSingleWrapAtAttribute()
    {
        String result = javadocOutlet.wrapLinesAndIndent(
                "body\n@since 11 2 3 4 5 @since x");
        assertEquals(
                "     * body\n"
                        + "     *\n"
                        + "     * @since 11 2 3\n"
                        + "     *        4 5\n"
                        + "     * @since x\n",
                        result);
    }

    /**
     * Checks that a preceding \n or two same javadoc attribute
     * cause a single line wrap
     */
    @Test
    public void testSingleWrapAtAttributeWithLineBreak()
    {
        String result = javadocOutlet.wrapLinesAndIndent(
                "@since x\n@since y");
        assertEquals(
                "     * @since x\n"
                        + "     * @since y\n",
                        result);
    }

    /**
     * Checks that setWrapAfterCharacters works as expected.
     */
    @Test
    public void testSetWrapAfterCharacters()
    {
        javadocOutlet.setWrapAfterCharacters(".");
        String result = javadocOutlet.wrapLinesAndIndent(
                "123-456,789;012.345");
        assertEquals(
                "     * 123-456,789;012.\n"
                        + "     * 345\n",
                        result);
    }

    /**
     * Checks that setIndent works as expected.
     */
    @Test
    public void testSetIndent()
    {
        javadocOutlet.setIndent("        ");
        String result = javadocOutlet.wrapLinesAndIndent(
                "123-456,789;012");
        assertEquals(
                "         * 123-456,\n"
                        + "         * 789;012\n",
                        result);
    }

    /**
     * Checks that setLineBreak works as expected.
     */
    @Test
    public void testSetLineBreak()
    {
        javadocOutlet.setLineBreak("\r\n");
        String result = javadocOutlet.wrapLinesAndIndent(
                "xxx123-456,789\n012.345");
        assertEquals(
                "     * xxx123-456,\r\n"
                        + "     * 789\r\n"
                        + "     * 012.345\r\n",
                        result);
    }

    /**
     * Checks that setLineBreak works as expected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetLineBreakIllegalInput()
    {
        javadocOutlet.setLineBreak("\n\r");
    }

    /**
     * Tests that removeEnd works for an empty String.
     */
    @Test
    public void testRemoveEndEmptyString()
    {
        StringBuilder builder = new StringBuilder();
        JavadocOutlet.removeEnd(builder, " \r\n");
        assertEquals("", builder.toString());
    }

    /**
     * Tests that removeEnd works if everything is removed.
     */
    @Test
    public void testRemoveEndEverythingRemoved()
    {
        StringBuilder builder = new StringBuilder().append(" \r\r\n   \n");
        JavadocOutlet.removeEnd(builder, " \r\n");
        assertEquals("", builder.toString());
    }

    /**
     * Tests that removeEnd works if nothing is removed.
     */
    @Test
    public void testRemoveEndNothingRemoved()
    {
        StringBuilder builder = new StringBuilder().append(" \r\r\n   \na");
        JavadocOutlet.removeEnd(builder, " \r\n");
        assertEquals(" \r\r\n   \na", builder.toString());
    }

    /**
     * Tests that removeEnd works if a part is removed.
     */
    @Test
    public void testRemoveEndPartRemoved()
    {
        StringBuilder builder = new StringBuilder().append(" \r\r\n x \r \n");
        JavadocOutlet.removeEnd(builder, " \r\n");
        assertEquals(" \r\r\n x", builder.toString());
    }

    /**
     * Tests that removeEnd works if everything but the first character
     * is removed.
     */
    @Test
    public void testRemoveEndAllButFirstCharacterRemoved()
    {
        StringBuilder builder = new StringBuilder().append("x\r\r\n  \r \n");
        JavadocOutlet.removeEnd(builder, " \r\n");
        assertEquals("x", builder.toString());
    }

    /**
     * Tests that removeEnd works if only the last character is removed.
     */
    @Test
    public void testRemoveEndOnlyLastCharacterRemoved()
    {
        StringBuilder builder = new StringBuilder().append("\r\r\n  \rx ");
        JavadocOutlet.removeEnd(builder, " \r\n");
        assertEquals("\r\r\n  \rx", builder.toString());
    }
}
