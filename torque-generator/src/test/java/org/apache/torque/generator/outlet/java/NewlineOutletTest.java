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

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;
import org.junit.Before;
import org.junit.Test;

/**
 * Component tests for the NewlineOutlet.
 */
public class NewlineOutletTest
{
    /** System under test. */
    private NewlineOutlet newlineOutlet;

    @Before
    public void setUp()
    {
        this.newlineOutlet = new NewlineOutlet(new QualifiedName(
                "org.apache.torque.generator.newlineOutlet"));
    }

    @Test
    public void testDefault() throws GeneratorException
    {
        OutletResult result = newlineOutlet.execute(null);
        assertEquals("\n", result.getStringResult());
    }

    @Test
    public void testWindowsStyle() throws GeneratorException
    {
        newlineOutlet.setWindowsStyle(true);
        OutletResult result = newlineOutlet.execute(null);
        assertEquals("\r\n", result.getStringResult());
    }

    @Test()
    public void testCountZero() throws GeneratorException
    {
        newlineOutlet.setCount(0);
        OutletResult result = newlineOutlet.execute(null);
        assertEquals("", result.getStringResult());
    }

    @Test(expected = GeneratorException.class)
    public void testCountLessThanZero() throws GeneratorException
    {
        newlineOutlet.setCount(-1);
        OutletResult result = newlineOutlet.execute(null);
        assertEquals("\n", result.getStringResult());
    }

    @Test()
    public void testCountFive() throws GeneratorException
    {
        newlineOutlet.setCount(5);
        OutletResult result = newlineOutlet.execute(null);
        assertEquals("\n\n\n\n\n", result.getStringResult());
    }
}
