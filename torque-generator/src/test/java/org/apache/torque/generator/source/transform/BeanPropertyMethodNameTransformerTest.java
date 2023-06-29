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

import org.apache.torque.generator.source.SourceElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Converts a source element Attribute such that it can be used as method name.
 * The base of the method name is the attribute content, with special characters
 * removed and case corrected where necessary. Optionally,
 * a prefix and/or suffix can be added. The result is stored into another
 * attribute of the same source element.
 *
 * @version $Id: BeanPropertyMethodNameTransformerTest.java 1855607 2019-03-15 16:41:24Z gk $
 */
public class BeanPropertyMethodNameTransformerTest
{
    private BeanPropertyMethodNameTransformer transformer;

    @BeforeEach
    public void before() throws SourceTransformerException
    {
        transformer = new BeanPropertyMethodNameTransformer();
    }

    @Test
    public void testNoPrefixSuffix() throws SourceTransformerException
    {
        SourceElement sourceElement
            = new SourceElement("elementName");
        sourceElement.setAttribute("name", "vaL_ue");

        transformer.setTargetAttributeName("targetAttribute");
        SourceElement result = transformer.transform(sourceElement, null);
        assertEquals("vaLUe", result.getAttribute("targetAttribute"));
    }

    @Test
    public void testPrefixSuffix() throws SourceTransformerException
    {
        SourceElement sourceElement
            = new SourceElement("elementName");
        sourceElement.setAttribute("sourceAttribute", "someProperty");

        transformer.setSourceAttributeName("sourceAttribute");
        transformer.setTargetAttributeName("targetAttribute");
        transformer.setPrefix("prefix");
        transformer.setSuffix("Suffix");
        SourceElement result = transformer.transform(sourceElement, null);
        assertEquals(
                "prefixSomePropertySuffix",
                result.getAttribute("targetAttribute"));
    }

    @Test
    public void testNoOverwrite() throws SourceTransformerException
    {
        SourceElement sourceElement
            = new SourceElement("elementName");
        sourceElement.setAttribute("name", "modified");
        sourceElement.setAttribute("targetAttribute", "unmodified");

        transformer.setOverwrite(false);
        transformer.setTargetAttributeName("targetAttribute");
        SourceElement result = transformer.transform(sourceElement, null);
        assertEquals(
                "unmodified",
                result.getAttribute("targetAttribute"));
    }

    @Test
    public void testNoOverwriteEmptyTarget() throws SourceTransformerException
    {
        SourceElement sourceElement
            = new SourceElement("elementName");
        sourceElement.setAttribute("name", "source");

        transformer.setOverwrite(false);
        transformer.setTargetAttributeName("targetAttribute");
        SourceElement result = transformer.transform(sourceElement, null);
        assertEquals(
                "source",
                result.getAttribute("targetAttribute"));
    }
}
