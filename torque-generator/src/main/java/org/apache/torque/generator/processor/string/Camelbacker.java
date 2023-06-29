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
 * Creates a String in CaMelBaCk case from a String with special characters
 */
public class Camelbacker implements StringProcessor
{
    /**
     * Whether the first character of the processed String should always
     * be upper case.
     */
    private boolean firstCharUppercase = true;

    /**
     * Separation characters. If one of the characters in this String
     * is encountered in the processed String, the  part of the processed
     * String before it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     */
    private String ignorePartBefore = null;

    /**
     * Separation characters. If one of the characters in this String
     * is encountered in the processed String, the part of the processed
     * String after it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     */
    private String ignorePartAfter = null;

    /**
     * All the characters in this String are removed from the processed String.
     */
    private String removeWithoutUppercase = ".";

    /**
     * All the characters in this String are removed from the processed String;
     * the following character is converted to upper case.
     */
    private String removeWithUppercase = "_-";

    /**
     * Whether all characters in the processed String should be made
     * lower Case by default (i.e if none of the camelback rules is applicable).
     */
    private boolean defaultLowerCase = true;

    /**
     * Does the camelback processing according to the settings.
     */
    @Override
    public String process(String toProcess)
    {
        StringBuilder result = new StringBuilder();
        boolean ignoreAfterHit = false;
        boolean nextCharUpperCase = firstCharUppercase;
        for (int i = 0; i < toProcess.length(); ++i)
        {
            char currentChar = toProcess.charAt(i);
            if (ignorePartBefore != null
                    && ignorePartBefore.indexOf(currentChar) != -1)
            {
                result = new StringBuilder();
            }
            if (ignoreAfterHit)
            {
                // we cannot do a break here, because one of the characters in
                // ignorePartBefore may still be encountered, and then we have
                // to clean the result
                continue;
            }
            if (ignorePartAfter != null
                    && ignorePartAfter.indexOf(currentChar) != -1)
            {
                ignoreAfterHit = true;
            }
            if (removeWithoutUppercase != null
                    && removeWithoutUppercase.indexOf(currentChar) != -1)
            {
                continue;
            }
            if (removeWithUppercase != null
                    && removeWithUppercase.indexOf(currentChar) != -1)
            {

                nextCharUpperCase = true;
                continue;
            }
            if (nextCharUpperCase)
            {
                result.append(Character.toUpperCase(currentChar));
                nextCharUpperCase = false;
            }
            else
            {
                if (defaultLowerCase)
                {
                    result.append(Character.toLowerCase(currentChar));
                }
                else
                {
                    result.append(currentChar);
                }
            }
        }
        return result.toString();
    }

    /**
     * Returns whether the first character is always converted to upper case.
     *
     * @return true if the first character is always converted to upper case,
     *         false if not.
     */
    public boolean isFirstCharUppercase()
    {
        return firstCharUppercase;
    }

    /**
     * Sets whether the first character should always be upper case.
     * Default is true.
     *
     * @param firstCharUppercase true if the first character should always
     *        be converted to upper case, false if not.
     */
    public void setFirstCharUppercase(boolean firstCharUppercase)
    {
        this.firstCharUppercase = firstCharUppercase;
    }

    /**
     * Returns the separation chars which define the tail to be removed.
     * If one of the characters in this String
     * is encountered in the processed String, the part of the processed
     * String after it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     *
     * @return the separation chars for removing the tail.
     */
    public String getIgnorePartAfter()
    {
        return ignorePartAfter;
    }

    /**
     * Sets the separation chars which define the suffix to be removed.
     * If one of the characters in this String
     * is encountered in the processed String, the part of the processed
     * String after it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     *
     * @param ignorePartAfter the separation chars for removing the tail.
     */
    public void setIgnorePartAfter(String ignorePartAfter)
    {
        this.ignorePartAfter = ignorePartAfter;
    }

    /**
     * Returns the separation characters which defile the prefix to be removed.
     * If one of the characters in this String
     * is encountered in the processed String, the  part of the processed
     * String before it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     *
     * @return the separation chars which define the suffix to be removed.
     */
    public String getIgnorePartBefore()
    {
        return ignorePartBefore;
    }

    /**
     * Sets the separation characters which define the prefix to be removed.
     * If one of the characters in this String
     * is encountered in the processed String, the  part of the processed
     * String before it are ignored. The character itself is retained,
     * unless it is removed by another rule.
     *
     * @param ignorePartBefore the separation chars which define the suffix
     *        to be removed.
     */
    public void setIgnorePartBefore(String ignorePartBefore)
    {
        this.ignorePartBefore = ignorePartBefore;
    }

    /**
     * Returns which characters are removed from the processed String.
     *
     * @return a String containing all characters which are simply removed
     *         from the input String.
     */
    public String getRemoveWithoutUppercase()
    {
        return removeWithoutUppercase;
    }

    /**
     * Sets which characters are removed from the processed String.
     * Default is "."
     *
     * @param removeWithoutUppercase a String containing all characters
     *        which are simply removed from the input String.
     */
    public void setRemoveWithoutUppercase(String removeWithoutUppercase)
    {
        this.removeWithoutUppercase = removeWithoutUppercase;
    }

    /**
     * Returns the characters which are removed from the processed String
     * and cause the following character to be converted to upper case.
     *
     * @return a String containing all characters which are removed
     *         from the input String and which cause the following character
     *         to be converted to upper case.
     */
    public String getRemoveWithUppercase()
    {
        return removeWithUppercase;
    }

    /**
     * Sets the characters which are removed from the processed String
     * and cause the following character to be converted to upper case.
     * Default is "_-"
     *
     * @param removeWithUppercase a String containing all characters which are
     *        removed from the input String and which cause the following
     *        character to be converted to upper case.
     */
    public void setRemoveWithUppercase(String removeWithUppercase)
    {
        this.removeWithUppercase = removeWithUppercase;
    }

    /**
     * Returns whether all characters in the processed String should be made
     * lower Case by default (i.e if none of the camelback rules is applicable).
     *
     * @return true if all characters are converted to lower case by default,
     *         false if not.
     */
    public boolean isDefaultLowerCase()
    {
        return defaultLowerCase;
    }

    /**
     * Sets whether all characters in the processed String should be made
     * lower Case by default (i.e if none of the camelback rules is applicable).
     * Default is true.
     *
     * @param defaultLowerCase true if all characters are converted to lower
     *        case by default, false if not.
     */
    public void setDefaultLowerCase(boolean defaultLowerCase)
    {
        this.defaultLowerCase = defaultLowerCase;
    }
}
