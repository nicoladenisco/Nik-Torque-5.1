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

import java.io.File;

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;
import org.junit.Before;
import org.junit.Test;

/**
 * Component Tests for the ModifySourcenameOutlet.
 */
public class ModifySourcenameOutletTest
{
    /** System under test. */
    private ModifySourcenameOutlet outlet;

    /** A mock controller state. */
    private ControllerState controllerState;

    @Before
    public void setUp()
    {
        this.outlet = new ModifySourcenameOutlet(new QualifiedName(
                "org.apache.torque.generator.modifySourcenameOutlet"));
        controllerState = new ControllerState();
        controllerState.setSourceFile(
                new File("src/test/some-test-file-name.someTestExtension"));
    }

    @Test
    public void testDefault() throws GeneratorException
    {
        OutletResult result = outlet.execute(controllerState);
        assertEquals(
                "some-test-file-name.someTestExtension",
                result.getStringResult());
    }

    @Test
    public void testAll() throws GeneratorException
    {
        outlet.setDiscardFrom("Ext");
        outlet.setDiscardTo("me-");
        outlet.setPrefix("prefix-");
        outlet.setSuffix("-suffix");
        OutletResult result = outlet.execute(controllerState);
        assertEquals(
                "prefix-test-file-name.someTest-suffix",
                result.getStringResult());
    }

    @Test
    public void testDiscardFrom() throws GeneratorException
    {
        outlet.setDiscardFrom("-");
        OutletResult result = outlet.execute(controllerState);
        assertEquals("some", result.getStringResult());
    }

    @Test
    public void testDiscardTo() throws GeneratorException
    {
        outlet.setDiscardTo("-");
        OutletResult result = outlet.execute(controllerState);
        assertEquals("name.someTestExtension", result.getStringResult());
    }

    @Test
    public void testOverlappingFromAndTo() throws GeneratorException
    {
        outlet.setDiscardFrom("-");
        outlet.setDiscardTo("-");
        OutletResult result = outlet.execute(controllerState);
        assertEquals("", result.getStringResult());
    }

    @Test
    public void testSourceFilenameNull() throws GeneratorException
    {
        controllerState.setSourceFile(null);
        OutletResult result = outlet.execute(controllerState);
        assertEquals("", result.getStringResult());
    }
}
