package org.apache.torque.generator.source.stream;

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

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.stream.FileSource;
import org.apache.torque.generator.source.stream.SourceToXml;
import org.apache.torque.generator.source.stream.XmlSourceFormat;
import org.junit.Test;

/**
 * Tests the SourceToXml functionality.
 *
 * $Id: SourceToXmlTest.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class SourceToXmlTest
{
    private ControllerState controllerState = new ControllerState();

    public SourceToXmlTest()
    {
        UnitConfiguration unitConfiguration = new UnitConfiguration();
        unitConfiguration.setEntityReferences(new EntityReferences());
        controllerState.setUnitConfiguration(unitConfiguration);
    }

    @Test
    public void testSourceToXml() throws Exception
    {
        File xmlFile
            = new File("src/test/resources/org/apache/torque/generator/source/xml/source.xml");
        FileSource fileSource = new FileSource(
                new XmlSourceFormat(),
                xmlFile,
                controllerState);
        SourceElement rootElement = fileSource.getRootElement();

        String result = new SourceToXml().toXml(rootElement, true);
        String expected = FileUtils.readFileToString(new File(
                "src/test/resources/org/apache/torque/generator/source/xml/sourceToXmlResult.xml"));
        // remove license from expected file
        expected = StringUtils.substringAfterLast(expected, "-->\n\n");
        assertEquals(expected, result);
    }

    @Test
    public void testSourceToXmlWithReferences() throws Exception
    {
        File xmlFile = new File(
                "src/test/resources/org/apache/torque/generator/source/xml/source.xml");
        FileSource fileSource = new FileSource(
                new XmlSourceFormat(),
                xmlFile,
                controllerState);
        SourceElement rootElement = fileSource.getRootElement();
        rootElement.getChildren().get(2).getChildren().add(
                rootElement.getChildren().get(0).getChildren().get(0));

        String result = new SourceToXml().toXml(rootElement, true);
        String expected = FileUtils.readFileToString(new File(
                "src/test/resources/org/apache/torque/generator/source/xml/sourceToXmlWithReferenceResult.xml"));
        // remove license from expected file
        expected = StringUtils.substringAfterLast(expected, "-->\n\n");
        assertEquals(expected, result);
    }

    @Test
    public void testSourceToXmlTextEscaping() throws Exception
    {
        SourceElement rootElement = new SourceElement("root");
        rootElement.setAttribute((String) null, "X&<>Y'\"Z");
        String result = new SourceToXml().toXml(rootElement, true);
        assertEquals(
                "<root id=\"1\">X&amp;&lt;&gt;Y&apo;&quot;Z</root>\n",
                result);
    }

    @Test
    public void testSourceToXmlAttributeEscaping() throws Exception
    {
        SourceElement rootElement = new SourceElement("root");
        rootElement.setAttribute("attribute", "&<>'\"");
        String result = new SourceToXml().toXml(rootElement, true);
        assertEquals(
                "<root attribute=\"&amp;&lt;&gt;&apo;&quot;\" id=\"1\"/>\n",
                result);
    }

    @Test
    public void testSourceToXmlNoAutomaticIds() throws Exception
    {
        SourceElement rootElement = new SourceElement("root");
        rootElement.getChildren().add(new SourceElement("child"));
        String result = new SourceToXml().toXml(rootElement, false);
        assertEquals(
                "<root>\n  <child/>\n</root>\n",
                result);
    }
}
