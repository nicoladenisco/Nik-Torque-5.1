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



/**
 * Creates the name of a constant from a string. All letters in the String are
 * capitalized, and underscores (_) are used as separators per default.
 * If the string is empty or starts with a number, an underscore(_) is prefixed to the constant name.
 */
public class ConstantNameCreator extends CharReplacer
{
    /**
     * The String which is usually used as prefix in front of upper case
     * characters in java constants.
     */
    public static final String UPPER_CASE_SEPPARATION_PREFIX = "_";

    /**
     * The String which is used as prefix in front of upper case
     * characters in java constants.
     */
    private String upperCaseSeparationPrefix = UPPER_CASE_SEPPARATION_PREFIX;

    /**
     * Constructor.
     */
    public ConstantNameCreator()
    {
        setToReplace(JAVA_CLASSNAME_SPECIAL_CHARS + JAVA_CLASSNAME_REPLACEMENT);
    }

    /**
     * Returns the prefix which is used as Separator when an upper
     * case character is encountered after a lower case character.
     *
     * @return the separator which is inserted between a lower case character
     *         and an upper case character.
     */
    public String getUpperCaseSeparationPrefix()
    {
        return upperCaseSeparationPrefix;
    }

    /**
     * Sets the prefix which is used as Separator when an upper
     * case character is encountered after a lower case character.
     *
     * @param upperCaseSeparationPrefix the separator which is inserted
     *        between a lower case character and an upper case character.
     */
    public void setUpperCaseSeparationPrefix(final String upperCaseSeparationPrefix)
    {
        this.upperCaseSeparationPrefix = upperCaseSeparationPrefix;
    }

    /**
     * Replaces all characters in <code>toProcess</code> which occur in
     * <code>toReplace</code> with <code>toReplaceWith</code>; groups of
     * special characters are treated as one.
     * Inserts <code>UPPER_CASE_SEPPARATION_PREFIX</code> if an upper case
     * character follows a lower case character, and converts all charcters
     * to upper case.
     * If the string starts is empty or start with a number, the character <code>toReplaceWith</code>
     * is prefixed to the constant name.
     * Finally, the new String is returned.
     * 
     * <p>
     * Example: "prOceSS-*+ing~#._Test" is converted to "PR_OCE_SS_ING_TEST"
     *
     * @param toProcess the String in which replacement should occur, not null.
     *
     * @return the processed String, not null.
     */
    @Override
    public String process(final String toProcess)
    {
        StringBuilder result = new StringBuilder();
        String toReplace = getToReplace();
        String toReplaceWith = getToReplaceWith();
        boolean lastCharWasSpecial = true;
        boolean lastCharWasLowerCase = false;
        for (int i = 0; i < toProcess.length(); ++i)
        {
            char currentChar = toProcess.charAt(i);
            boolean specialChar = toReplace.indexOf(currentChar) != -1;
            if (specialChar)
            {
                if (lastCharWasSpecial)
                {
                    continue;
                }
                else
                {
                    result.append(toReplaceWith);
                }
                lastCharWasLowerCase = false;
            }
            else if (Character.isUpperCase(currentChar))
            {
                if (lastCharWasSpecial || !lastCharWasLowerCase)
                {
                    result.append(currentChar);
                }
                else
                {
                    result.append(upperCaseSeparationPrefix)
                    .append(currentChar);
                }
                lastCharWasLowerCase = false;
            }
            else
            {
                result.append(Character.toUpperCase(currentChar));
                lastCharWasLowerCase = true;
            }
            lastCharWasSpecial = specialChar;
        }
        if (result.length() == 0 || result.charAt(0) >= '0' && result.charAt(0) <= '9')
        {
            result.insert(0, toReplaceWith);
        }
        return result.toString();
    }
}
