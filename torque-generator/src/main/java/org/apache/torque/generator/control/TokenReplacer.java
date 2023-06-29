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

import org.apache.torque.generator.processor.string.StringProcessor;

/**
 * Replaces placeholder tokens like ${option:optionName} in a string by the
 * resolved values. The escape character is the backslash (\).
 *
 * $Id: TokenReplacer.java 1856207 2019-03-25 15:40:01Z painter $
 */
public class TokenReplacer implements StringProcessor
{
    /** First character of a Token start. */
    public static final char TOKEN_START_1 = '$';

    /** Second character of a Token start. */
    public static final char TOKEN_START_2 = '{';

    /** The character ending a token. */
    public static final char TOKEN_END = '}';

    /** The escape character. */
    public static final char ESCAPE = '\\';

    /** The prefix for an option token. */
    public static final String OPTION_PREFIX = "option";

    /** The separator between prefix and key. */
    public static final char PREFIX_SEPARATOR = ':';

    /**
     * The controller state for resolving options.
     */
    private ControllerState controllerState;

    /**
     * @param controllerState to init the token replacer
     */
    public TokenReplacer(ControllerState controllerState)
    {
        if (controllerState == null)
        {
            throw new NullPointerException("controllerState must not be null");
        }
        this.controllerState = controllerState;
    }

    /**
     * Resolves all Tokens ${option:optionName} and replaces them with the
     * appropriate value.
     *
     * @param toProcess the String to remove tokens from, or null.
     */
    @Override
    public String process(String toProcess)
    {
        if (toProcess == null)
        {
            return null;
        }
        StringBuilder result = new StringBuilder();
        StringBuilder tokenName = new StringBuilder();
        boolean escape = false;
        boolean inTokenStart = false;
        boolean inToken = false;
        for (char currentChar : toProcess.toCharArray())
        {
            if (currentChar == ESCAPE && !escape)
            {
                escape = true;
                if (inTokenStart)
                {
                    result.append(TOKEN_START_1);
                    inTokenStart = false; // $\ is not token start
                }
                continue;
            }
            if (escape)
            {
                if (inToken)
                {
                    tokenName.append(currentChar);
                }
                else
                {
                    result.append(currentChar);
                }
                escape = false;
                continue;
            }
            escape = false;
            if (currentChar == TOKEN_START_1 && !inTokenStart && !inToken)
            {
                inTokenStart = true;
                continue;
            }
            if (inTokenStart)
            {
                if (currentChar == TOKEN_START_2)
                {
                    inTokenStart = false;
                    inToken = true;
                    continue;
                }
                else
                {
                    result.append(TOKEN_START_1); // did not copy that before
                    result.append(currentChar);
                    inTokenStart = false;
                    continue;
                }
            }
            if (currentChar == TOKEN_END && inToken)
            {
                result.append(resolveToken(tokenName.toString()));
                tokenName = new StringBuilder();
                inToken = false;
                continue;
            }
            if (inToken)
            {
                tokenName.append(currentChar);
            }
            else
            {
                result.append(currentChar);
            }
        }
        if (escape)
        {
            throw new IllegalArgumentException("Single escape character "
                    + ESCAPE
                    + " encountered at end of String "
                    + toProcess);
        }
        if (inTokenStart)
        {
            result.append(TOKEN_START_1);
        }
        if (inToken)
        {
            throw new IllegalArgumentException("Token end "
                    + TOKEN_END
                    + " missing at end of String "
                    + toProcess);
        }
        return result.toString();
    }

    
    /**
     * @param tokenName the token name to resolve
     * @return resolved token
     */
    private String resolveToken(String tokenName)
    {
    	String optionPrefixWithSep = OPTION_PREFIX + PREFIX_SEPARATOR;
        if (!tokenName.startsWith(optionPrefixWithSep))
        {
            throw new IllegalArgumentException("Token name must start with" + optionPrefixWithSep );
        }
        String optionName = tokenName.substring( optionPrefixWithSep.length() );
        Object optionValue = controllerState.getOption(optionName);
        if (optionValue == null)
        {
            return "";
        }
        return optionValue.toString();
    }
}
