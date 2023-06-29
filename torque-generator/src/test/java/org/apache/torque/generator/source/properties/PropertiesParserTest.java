package org.apache.torque.generator.source.properties;

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

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.configuration.source.EntityReferences;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.stream.FileSource;
import org.apache.torque.generator.source.stream.PropertiesSourceFormat;
import org.junit.Test;

public class PropertiesParserTest
{
    private ControllerState controllerState = new ControllerState();

    public PropertiesParserTest()
    {
        UnitConfiguration unitConfiguration = new UnitConfiguration();
        unitConfiguration.setEntityReferences(new EntityReferences());
        controllerState.setUnitConfiguration(unitConfiguration);
    }

    @Test
    public void testParsePropertiesFile()
            throws ConfigurationException, SourceException
    {
        File propertiesFile
            = new File("src/test/resources/org/apache/torque/generator/source/properties/propertiesParserTest.properties");
        FileSource fileSource
            = new FileSource(
                new PropertiesSourceFormat(),
                propertiesFile,
                controllerState);

        SourceElement rootElement = fileSource.getRootElement();
        assertEquals("properties", rootElement.getName());
        assertEquals(0, rootElement.getAttributeNames().size());

        assertEquals(2, rootElement.getChildren().size());
        {
            SourceElement child0 = rootElement.getChildren().get(0);
            assertEquals("entry", child0.getName());
            assertEquals(2, child0.getAttributeNames().size());
            assertEquals("propertyName1", child0.getAttribute("key"));
            assertEquals("propertyValue1", child0.getAttribute((String) null));
        }
        {
            SourceElement child1 = rootElement.getChildren().get(1);
            assertEquals("entry", child1.getName());
            assertEquals(2, child1.getAttributeNames().size());
            assertEquals("propertyName2", child1.getAttribute("key"));
            assertEquals("propertyValue2", child1.getAttribute((String) null));
        }
    }

    @Test(expected = NullPointerException.class)
    public void testPathNull() throws ConfigurationException
    {
        new FileSource(
                new PropertiesSourceFormat(),
                null,
                controllerState);
    }
}
