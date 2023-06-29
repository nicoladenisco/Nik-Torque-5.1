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

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourceElement;
import org.junit.Test;

/**
 * Component tests for the XmlOutlet.
 */
public class XmlOutletTest
{
    /**
     * A basic test for the XML Outlet, where no id attributes are created.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testXmlOutlet() throws Exception
    {
        SourceElement rootElement = new SourceElement("root");
        rootElement.getChildren().add(new SourceElement("child"));
        ControllerState controllerState = new ControllerState();
        controllerState.setModelRoot(rootElement);
        XmlOutlet xmlOutlet = new XmlOutlet(new QualifiedName("test"));
        OutletResult result = xmlOutlet.execute(controllerState);
        assertEquals(
                "<root>\n  <child/>\n</root>\n",
                result.getStringResult());
    }

    /**
     * A basic test for the XML Outlet, where id attributes are created.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testXmlOutletCreateIdAttributes() throws Exception
    {
        SourceElement rootElement = new SourceElement("root");
        rootElement.getChildren().add(new SourceElement("child"));
        ControllerState controllerState = new ControllerState();
        controllerState.setModelRoot(rootElement);
        XmlOutlet xmlOutlet = new XmlOutlet(new QualifiedName("test"));
        xmlOutlet.setCreateIdAttributes(true);
        OutletResult result = xmlOutlet.execute(controllerState);
        assertEquals(
                "<root id=\"1\">\n  <child id=\"2\"/>\n</root>\n",
                result.getStringResult());
    }
}
