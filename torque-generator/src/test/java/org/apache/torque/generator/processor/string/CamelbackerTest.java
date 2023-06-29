package org.apache.torque.generator.processor.string;

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


import org.apache.torque.generator.processor.string.Camelbacker;
import org.junit.Before;
import org.junit.Test;

public class CamelbackerTest
{
    Camelbacker camelbacker;

    @Before
    public void setUp()
    {
        camelbacker = new Camelbacker();
    }

    @Test
    public void testDefault()
    {
        String result = camelbacker.process("prOcess-ing_Test");
        assertEquals("ProcessIngTest", result);
    }

    @Test
    public void testRemoveWithUppercaseNull()
    {
        camelbacker.setRemoveWithUppercase(null);
        String result = camelbacker.process("prOcess-ing_Test");
        assertEquals("Process-ing_test", result);
    }

    @Test
    public void testFirstCharacterUppercaseFalse()
    {
        camelbacker.setFirstCharUppercase(false);
        String result = camelbacker.process("prOcess-ing_Test");
        assertEquals("processIngTest", result);

        result = camelbacker.process("PrOcess-ing_Test");
        assertEquals("processIngTest", result);

        camelbacker.setDefaultLowerCase(false);
        result = camelbacker.process("PrOcess-ing_Test");
        assertEquals("PrOcessIngTest", result);
    }

    @Test
    public void testDefaultLowerCaseFalse()
    {
        camelbacker.setDefaultLowerCase(false);
        String result = camelbacker.process("PrOcess-ing_Test");
        assertEquals("PrOcessIngTest", result);
    }

    @Test
    public void testIgnoreBeforeAfter()
    {
        String result = camelbacker.process("pro1cess-ing_te2st");
        assertEquals("Pro1cessIngTe2st", result);

        camelbacker.setIgnorePartBefore("1");
        camelbacker.setIgnorePartAfter("2");

        result = camelbacker.process("pro1cess-ing_te2st");
        assertEquals("1cessIngTe2", result);

        camelbacker.setIgnorePartAfter("1");
        camelbacker.setIgnorePartBefore("2");

        result = camelbacker.process("pro1cess-ing_te2st");
        assertEquals("", result);

        camelbacker.setIgnorePartAfter("1");
        camelbacker.setIgnorePartBefore("1");

        result = camelbacker.process("pro1cess-ing_te2st");
        assertEquals("1", result);

        // make sure IgnoreBeforeAfter plays nice with the other rules

        camelbacker.setRemoveWithoutUppercase("1");
        camelbacker.setIgnorePartAfter(null);

        result = camelbacker.process("pro1cess-ing_te2st");
        assertEquals("cessIngTe2st", result);

        camelbacker.setIgnorePartAfter("1");
        camelbacker.setRemoveWithoutUppercase(null);
        camelbacker.setRemoveWithUppercase("1");

        result = camelbacker.process("pro1cess-ing_te2st");
        assertEquals("", result);
    }
}
