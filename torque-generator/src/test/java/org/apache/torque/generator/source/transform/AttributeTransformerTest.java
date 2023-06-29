package org.apache.torque.generator.source.transform;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class AttributeTransformerTest
{
    private AttributeTransformer emptyTransformer;

    @BeforeEach
    public void before() throws SourceTransformerException
    {
        StringReader stringReader = new StringReader("#");
        emptyTransformer = new AttributeTransformer(stringReader);
    }

    @Test
    public void testParsePlainText() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse("abcxy", true);
        assertEquals(1, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.PLAIN,
                result.get(0).getType());
        assertEquals("abcxy", result.get(0).getContent());
    }

    @Test
    public void testParseOption() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse("${option:torque:someOption}", true);
        assertEquals(1, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.OPTION,
                result.get(0).getType());
        assertEquals("torque:someOption", result.get(0).getContent());
    }

    @Test
    public void testParseAttribute() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse(
                "${attribute:org.apache.torque:someAttribute}",
                true);
        assertEquals(1, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.ATTRIBUTE,
                result.get(0).getType());
        assertEquals(
                "org.apache.torque:someAttribute",
                result.get(0).getContent());
    }

    @Test
    public void testParseOverrideAttribute() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse(
                "${attribute(override):torque:someAttribute}",
                false);
        assertEquals(1, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.ATTRIBUTE_OVERRIDE,
                result.get(0).getType());
        assertEquals(
                "torque:someAttribute",
                result.get(0).getContent());
    }

    @Test
    public void testParseNoOverrideAttribute() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse(
                "${attribute(noOverride):torque:someAttribute}",
                false);
        assertEquals(1, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.ATTRIBUTE_NO_OVERRIDE,
                result.get(0).getType());
        assertEquals(
                "torque:someAttribute",
                result.get(0).getContent());
    }

    @Test
    public void testParseEscapingInPlain() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse("\\:\\$\\}\\{\\\\l", true);
        assertEquals(1, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.PLAIN,
                result.get(0).getType());
        assertEquals(
                ":$}{\\l",
                result.get(0).getContent());
    }

    @Test
    public void testParseEscapingInDefinition() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse(
                "${attribute:\\:\\$\\}\\{l\\\\}",
                true);
        assertEquals(1, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.ATTRIBUTE,
                result.get(0).getType());
        assertEquals(
                ":$}{l\\",
                result.get(0).getContent());
    }

    @Test
    public void testParseSeveralDefinitions() throws SourceTransformerException
    {
        List<AttributeTransformer.Definition> result
        = emptyTransformer.parse(
                "${attribute:someAttribute}"
                        + "${option:someOption}"
                        + "plainText"
                        + "${option:someOption1}"
                        + "${attribute:someAttribute1}",
                        true);
        assertEquals(5, result.size());
        assertEquals(
                AttributeTransformer.Definition.Type.ATTRIBUTE,
                result.get(0).getType());
        assertEquals("someAttribute", result.get(0).getContent());
        assertEquals(
                AttributeTransformer.Definition.Type.OPTION,
                result.get(1).getType());
        assertEquals("someOption", result.get(1).getContent());
        assertEquals(
                AttributeTransformer.Definition.Type.PLAIN,
                result.get(2).getType());
        assertEquals("plainText", result.get(2).getContent());
        assertEquals(
                AttributeTransformer.Definition.Type.OPTION,
                result.get(3).getType());
        assertEquals("someOption1", result.get(3).getContent());
        assertEquals(
                AttributeTransformer.Definition.Type.ATTRIBUTE,
                result.get(4).getType());
        assertEquals("someAttribute1", result.get(4).getContent());
    }

    @Test
    public void testParseNotEndedDefinition()
    {
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                "${option:torque.endlessAttribute",
                false);
        });
    }

    @Test
    public void testParseDefinitionInsideType()
    {
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                    "${option:torque${option:torque.otherAttr}",
                    false);
        });
    }

    @Test
    public void testParseDefinitionInsideValue()
    {      
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                    "${option:torque.attribute${option:torque.otherAttr}",
                    false);
        });
    }

    @Test
    public void testUnknownDefinition()
    {
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                    "${unknownDefinition:torque.unknownDefinition}",
                    false);
        });
    }

    @Test
    public void testParsePlainInTarget()
    {
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                    "xy",
                    false);
        });
    }

    @Test
    public void testParseOptionInTarget()
    {
        assertThrows(SourceTransformerException.class, ()  -> {         
            emptyTransformer.parse(
                    "${option:torque.optionValue}",
                    false);
        });
    }

    @Test
    public void testParseAttributeInTarget()
    {
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                    "${attribute:torque.attributeValue}",
                    false);
        });
    }

    @Test
    public void testParseNoOverrideAttributeInSource()
    {
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                    "${attribute(noOverride):torque.attributeValue}",
                    true);
            });
    }

    @Test
    public void testParseOverrideAttributeInSource()
    {
        assertThrows(SourceTransformerException.class, ()  -> {
            emptyTransformer.parse(
                    "${attribute(override):torque.attributeValue}",
                    true);
        });
    }
}
