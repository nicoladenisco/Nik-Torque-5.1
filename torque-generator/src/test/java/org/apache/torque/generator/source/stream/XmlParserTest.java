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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.junit.Test;

public class XmlParserTest
{
    private ControllerState controllerState = new ControllerState();

    public XmlParserTest()
    {
        UnitConfiguration unitConfiguration = new UnitConfiguration();
        EntityReferences entityReferences = new EntityReferences();
        File schemaFile = new File("src/test/resources/"
                + "org/apache/torque/generator/source/xml/schema.xsd");
        byte[] schemaContent;
        try
        {
            schemaContent = FileUtils.readFileToByteArray(schemaFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        entityReferences.addEntityReference(
                "http://db.apache.org/torque/4.0/generator/test/namespace.xsd",
                schemaContent);
        unitConfiguration.setEntityReferences(entityReferences);
        controllerState.setUnitConfiguration(unitConfiguration);
    }

    @Test
    public void testReadXmlSource() throws Exception
    {
        File xmlFile = new File(
                "src/test/resources/org/apache/torque/generator/source/xml/source.xml");
        FileSource fileSource = new FileSource(
                new XmlSourceFormat(),
                xmlFile,
                controllerState);
        SourceElement rootElement = fileSource.getRootElement();

        assertEquals("root", rootElement.getName());
        assertEquals(1, rootElement.getAttributeNames().size());
        assertEquals(
                "rootElementAttributeValue",
                rootElement.getAttribute("rootElementAttribute"));
        assertEquals(4, rootElement.getChildren().size());
        SourceElement child1 = rootElement.getChildren().get(0);
        SourceElement child2 = rootElement.getChildren().get(1);
        SourceElement child3 = rootElement.getChildren().get(2);
        SourceElement child4 = rootElement.getChildren().get(3);

        assertEquals(1, child1.getChildren().size());
        assertEquals(0, child2.getChildren().size());
        assertEquals(1, child3.getChildren().size());
        assertEquals(0, child4.getChildren().size());

        assertEquals("secondLevelElement1", child1.getName());
        assertEquals("secondLevelElement2", child2.getName());
        assertEquals("secondLevelElement3", child3.getName());
        assertEquals("secondLevelElement4", child4.getName());

        assertEquals(0, child1.getAttributeNames().size());
        SourceElement tlElement1 = child1.getChildren().get(0);
        assertEquals("thirdLevelElement1", tlElement1.getName());
        assertEquals(1, tlElement1.getAttributeNames().size());
        assertEquals("tlaValue", tlElement1.getAttribute("tla"));

        assertEquals(0, child2.getAttributeNames().size());

        assertEquals(2, child3.getAttributeNames().size());
        assertEquals("slaValue", child3.getAttribute("sla"));
        assertEquals(
                " text For Second Level Element 3 ",
                child3.getAttribute((String) null));
        SourceElement tlElement2 = child3.getChildren().get(0);
        assertEquals("thirdLevelElement2", tlElement2.getName());
        assertEquals(1, tlElement2.getAttributeNames().size());
        assertEquals("tla2Value", tlElement2.getAttribute("tla2"));

        assertEquals(1, child4.getAttributeNames().size());
        assertEquals(
                "  text For Second Level Element 4  ",
                child4.getAttribute((String) null));
    }

    @Test
    public void testReadXmlSourceWithSchema() throws Exception
    {
        File xmlFile = new File(
                "src/test/resources/org/apache/torque/generator/source/xml/"
                        + "sourceConformingWithSchema.xml");
        FileSource fileSource = new FileSource(
                new XmlSourceFormat(),
                xmlFile,
                controllerState);
        SourceElement rootElement = fileSource.getRootElement();
        assertEquals("value", rootElement.getAttribute("attribute"));
    }

    @Test(expected = SourceException.class)
    public void testReadXmlSourceNotConformWithSchema() throws Exception
    {
        File xmlFile = new File(
                "src/test/resources/org/apache/torque/generator/source/xml/"
                        + "sourceNotConformingWithSchema.xml");
        FileSource fileSource = new FileSource(
                new XmlSourceFormat(),
                xmlFile,
                controllerState);
        fileSource.getRootElement();
    }
}
