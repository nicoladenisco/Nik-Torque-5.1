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

import org.junit.Before;
import org.junit.Test;

public class ConstantNameCreatorTest
{
    ConstantNameCreator constantNameCreator;

    @Before
    public void setUp()
    {
        constantNameCreator = new ConstantNameCreator();
    }

    @Test
    public void testDefault()
    {
        String result = constantNameCreator.process("prOceSS-*+ing~#._Test");
        assertEquals("PR_OCE_SS_ING_TEST", result);
    }

    @Test
    public void testFirstCharacterUppercase()
    {
        String result = constantNameCreator.process("Process");
        assertEquals("PROCESS", result);
    }

    @Test
    public void testFirstCharacterNumber()
    {
        String result = constantNameCreator.process("1Process");
        assertEquals("_1_PROCESS", result);
    }

    @Test
    public void testEmpty()
    {
        String result = constantNameCreator.process("");
        assertEquals("_", result);
    }
}
