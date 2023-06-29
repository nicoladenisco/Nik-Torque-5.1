package org.apache.torque.generator.variable;

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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.junit.Test;

/**
 * Unit test for the Variable Class.
 */
public class VariableTest extends BaseTest
{
    /**
     * Tests the constructor.
     */
    @Test
    public void testConstructor()
    {
        Variable variable = new Variable(
                new QualifiedName("generator", "org.apache.torque"),
                "value",
                Variable.Scope.FILE);
        assertEquals(
                new Namespace("org.apache.torque"),
                variable.getName().getNamespace());
        assertEquals("generator", variable.getName().getName());
        assertEquals("value", variable.getValue());
        assertEquals(Variable.Scope.FILE, variable.getScope());

        try
        {
            variable = new Variable(
                    null,
                    "value",
                    Variable.Scope.CHILDREN);
            fail("NullPointerException expected");
        }
        catch (NullPointerException e)
        {
            // expected
        }

        try
        {
            variable = new Variable(
                    new QualifiedName("org.apache.torque", "generator"),
                    new Object(),
                    null);
            fail("NullPointerException expected");
        }
        catch (NullPointerException e)
        {
        }

        variable = new Variable(
                new QualifiedName("org.apache.torque", "generator"),
                null,
                Variable.Scope.GLOBAL);
        assertNull(variable.getValue());
    }
}
