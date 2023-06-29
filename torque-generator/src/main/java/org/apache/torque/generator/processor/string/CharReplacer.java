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
 * Replaces characters by a String.
 */
public class CharReplacer implements StringProcessor
{
    /**
     * Characters which are not allowed in java class names
     */
    public static final String JAVA_CLASSNAME_SPECIAL_CHARS
    = "-.;,\"'#+*`´~";

    /**
     * The String which is usually used as replacement for not allowed
     * characters in java class names.
     */
    public static final String JAVA_CLASSNAME_REPLACEMENT
    = "_";

    /**
     * Contains the characters which should be replaced.
     */
    private String toReplace = JAVA_CLASSNAME_SPECIAL_CHARS;

    /**
     * Contains the String which should be inserted instead of the replaced
     * characters.
     */
    private String toReplaceWith = JAVA_CLASSNAME_REPLACEMENT;

    /**
     * Returns a String containing all the characters which should be replaced.
     *
     * @return the Characters which should be replaced, not null.
     */
    public String getToReplace()
    {
        return toReplace;
    }

    /**
     * Sets the characters which should be replaced.
     *
     * @param toReplace a String containing all the Characters
     *         which should be replaced, not null.
     */
    public void setToReplace(final String toReplace)
    {
        if (toReplace == null)
        {
            throw new NullPointerException("toReplace is null");
        }
        this.toReplace = toReplace;
    }

    /**
     * Returns the String which are inserted instead of the replaced
     * characters.
     *
     * @return the replacement, not null.
     */
    public String getToReplaceWith()
    {
        return toReplaceWith;
    }

    /**
     * Sets the String which are inserted instead of the replaced
     * characters.
     *
     * @param toReplaceWith the replacement, not null.
     */
    public void setToReplaceWith(final String toReplaceWith)
    {
        if (toReplaceWith == null)
        {
            throw new NullPointerException("toReplaceWith is null");
        }
        this.toReplaceWith = toReplaceWith;
    }

    /**
     * Replaces all characters in <code>toProcess</code> which occur in
     * <code>toReplace</code> with <code>toReplaceWith</code> and returns the
     * new String
     *
     * @param toProcess the String in which replacement should occur, not null.
     *
     * @return the processed String, not null.
     */
    @Override
    public String process(final String toProcess)
    {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < toProcess.length(); ++i)
        {
            char currentChar = toProcess.charAt(i);
            if (toReplace.indexOf(currentChar) != -1)
            {
                result.append(toReplaceWith);
            }
            else
            {
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    @Override
    public String toString()
    {
        return "CharReplacer [toReplace=" + toReplace + ", toReplaceWith="
                + toReplaceWith + "]";
    }
}
