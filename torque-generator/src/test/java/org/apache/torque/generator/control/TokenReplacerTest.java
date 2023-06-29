package org.apache.torque.generator.control;

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

import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.option.Option;
import org.apache.torque.generator.option.OptionImpl;
import org.apache.torque.generator.option.Options;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class TokenReplacerTest extends BaseTest
{
    private TokenReplacer tokenReplacer;

    @BeforeEach
    public void setUp()
    {
        Options options = new Options();
        {
            Option optionWithoutNamespace = new OptionImpl(
                    "optionWithoutNamespace",
                    "ValueWithoutNamespace");
            options.setGlobalOption(optionWithoutNamespace);
        }
        {
            Option optionWithNamespace = new OptionImpl(
                    "org.apache.torque.generator.optionWithNamespace",
                    "ValueWithNamespace");
            options.setGlobalOption(optionWithNamespace);
        }
        {
            Option optionWithSpecialChars = new OptionImpl(
                    "opt${}i\\on",
                    "ValueWith${}Special\\Chars");
            options.setGlobalOption(optionWithSpecialChars);
        }

        UnitConfiguration unitConfiguration = new UnitConfiguration();
        unitConfiguration.setOptions(options);
        ControllerState controllerState = new ControllerState();
        controllerState.setUnitConfiguration(unitConfiguration);
        tokenReplacer = new TokenReplacer(controllerState);
    }

    @Test
    public void testNoToken() throws Exception
    {
        String input = "No Token in this String";
        String output = tokenReplacer.process(input);
        assertEquals(input, output);
    }

    @Test
    public void testTokenWithoutNamespace() throws Exception
    {
        String input = "Prefix${option:optionWithoutNamespace}Postfix";
        String output = tokenReplacer.process(input);
        assertEquals("PrefixValueWithoutNamespacePostfix", output);
    }

    @Test
    public void testTokenWithNamespace() throws Exception
    {
        String input
        = "Prefix${option:org.apache.torque.generator.optionWithNamespace}Postfix";
        String output = tokenReplacer.process(input);
        assertEquals("PrefixValueWithNamespacePostfix", output);
    }

    @Test
    public void testEscapingOutsideToken() throws Exception
    {
        String input = "Pre\\fixXXXPostfix";
        String output = tokenReplacer.process(input);
        assertEquals("PrefixXXXPostfix", output);
    }

    @Test
    public void testEscapingInToken() throws Exception
    {
        String input
        = "Prefix${option:option\\W\\ithoutNamespace}Postfix";
        String output = tokenReplacer.process(input);
        assertEquals("PrefixValueWithoutNamespacePostfix", output);
    }

    @Test
    public void testDoubleEscapingOutsideToken() throws Exception
    {
        String input = "Prefix\\\\Postfix";
        String output = tokenReplacer.process(input);
        assertEquals("Prefix\\Postfix", output);
    }

    @Test
    public void testTokenWithSpecialChars() throws Exception
    {
        String input
        = "Prefix${option:opt${\\}i\\\\on}Postfix";
        String output = tokenReplacer.process(input);
        assertEquals("PrefixValueWith${}Special\\CharsPostfix", output);
    }

    @Test
    public void testEscapingTokenStart1() throws Exception
    {
        String input
        = "Prefix\\${XXX}Postfix";
        String output = tokenReplacer.process(input);
        assertEquals("Prefix${XXX}Postfix", output);
    }

    @Test
    public void testEscapingTokenStart2() throws Exception
    {
        String input
        = "Prefix$\\{XXX}Postfix";
        String output = tokenReplacer.process(input);
        assertEquals("Prefix${XXX}Postfix", output);
    }
}
