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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletImpl;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * An outlet for creating correctly formatted javadoc.
 *
 * @version $Id: JavadocOutlet.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class JavadocOutlet extends OutletImpl
{
    /** The name of the mergepoint which output is used as javadoc body. */
    public static final String BODY_MERGEPOINT_NAME = "body";

    /** The name of the mergepoint which contains the javadoc attributes. */
    public static final String ATTRIBUTES_MERGEPOINT_NAME = "attributes";

    /** The first line of the javadoc. */
    private static final String START_LINE = "/**";

    /** The mid line start of the javadoc. */
    private static final String MID_LINE_START = " * ";

    /** The last line of the javadoc. */
    private static final String END_LINE = " */";

    /** The character which starts a javadoc attribute. */
    private static final String JAVADOC_ATTRIBUTE_START = "@";

    /** The default maximum line width. */
    private static final int DEFAULT_MAX_LINEWIDTH = 80;

    /** The line break string. */
    private String lineBreak = "\n";

    /** How long a line can be before it will be wrapped, -1 for no wrapping */
    private int maxLineLength = DEFAULT_MAX_LINEWIDTH;

    /** The indent to use in front of each line. */
    private String indent = "    ";

    /**
     * At which characters a line can be wrapped
     * with the character removed.
     */
    private String removableWrapCharacters = " ";

    /**
     * After which characters a line can be wrapped
     * without the character removed.
     */
    private String wrapAfterCharacters = ".;,-";

    /**
     * Constructor.
     *
     * @param qualifiedName the qualified name of the outlet, not null.
     */
    public JavadocOutlet(QualifiedName qualifiedName)
    {
        super(qualifiedName);
    }

    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        StringBuilder result = new StringBuilder();
        result.append(indent).append(START_LINE).append(lineBreak);
        String body = mergepoint(BODY_MERGEPOINT_NAME, controllerState);
        result.append(wrapLinesAndIndent(body));
        {
            String attributes = mergepoint(
                    ATTRIBUTES_MERGEPOINT_NAME,
                    controllerState);
            if (!StringUtils.isEmpty(attributes)
                    && !StringUtils.isEmpty(body))
            {
                result.append(indent).append(" *").append(lineBreak);
            }
            if (!StringUtils.isEmpty(attributes))
            {
                result.append(wrapLinesAndIndent(attributes));
            }
        }

        result.append(indent).append(END_LINE).append(lineBreak);
        return new OutletResult(result.toString());
    }

    /**
     * Wraps the content string such that the line length is not longer than
     * maxLineLength characters, if that can be achieved by wrapping at the
     * wrap characters. All resulting lines are also indented.
     *
     * @param content The content to wrap, may be null.
     *
     * @return the wrapped and indented content, not null.
     */
    String wrapLinesAndIndent(String content)
    {
        if (StringUtils.isBlank(content))
        {
            return "";
        }
        StringTokenizer tokenizer
            = new StringTokenizer(
                content.trim(),
                removableWrapCharacters
                + wrapAfterCharacters
                + "\r\n"
                + JAVADOC_ATTRIBUTE_START,
                true);
        StringBuilder result = new StringBuilder();
        result.append(indent).append(MID_LINE_START);
        int currentLineLength = indent.length() + MID_LINE_START.length();
        boolean lineBreakPossible = false;
        // last char is space so it can be removed
        boolean lastCharRemovable = true;
        String token = null;
        String currentIndent = indent + MID_LINE_START;
        String lastJavadocAttribute = null;
        while (tokenizer.hasMoreTokens() || token != null)
        {
            if (token == null)
            {
                // token has not been prefetched
                token = tokenizer.nextToken();
            }
            if ("\r".equals(token))
            {
                // Assumption: no \r without line breaks
                // always remove, will be added again if linebreak is \r\n
                token = null;
            }
            else if ("\n".equals(token))
            {
                // line break does not count towards line length
                result.append(lineBreak);
                lineBreakPossible = false;
                // because currentIndent ends with a space
                // we can remove the last char
                lastCharRemovable = true;
                if (tokenizer.hasMoreTokens())
                {
                    result.append(currentIndent);
                    currentLineLength = currentIndent.length();
                }
                token = null;
            }
            else if (JAVADOC_ATTRIBUTE_START.equals(token))
            {
                if (tokenizer.hasMoreTokens())
                {

                    token = tokenizer.nextToken();
                    // + 2 because of "@" + space after attribute
                    currentIndent = StringUtils.rightPad(
                            indent + MID_LINE_START,
                            indent.length() + MID_LINE_START.length()
                            + 2 + token.length());
                    if (result.length()
                            > indent.length() + MID_LINE_START.length())
                    {
                        // we could already be indented by a line break
                        // in a previous param
                        removeEnd(result, " \r\n");
                        boolean alreadyIndented = false;
                        if (result.toString().endsWith(indent + " *"))
                        {
                            alreadyIndented = true;
                        }
                        boolean doubleLineBreak = false;
                        if (!token.equals(lastJavadocAttribute))
                        {
                            doubleLineBreak = true;
                        }
                        if (!alreadyIndented)
                        {
                            result.append(lineBreak).append(indent).append(" *");
                        }
                        if (doubleLineBreak)
                        {
                            result.append(lineBreak).append(indent).append(" *");
                        }
                        result.append(" ");
                    }
                    //+ 3 because " * "
                    currentLineLength
                    = indent.length() + MID_LINE_START.length();
                    lastJavadocAttribute = token;
                }
                result.append(JAVADOC_ATTRIBUTE_START);
                ++currentLineLength;
                lineBreakPossible = false;
                lastCharRemovable = false;
            }
            else if (maxLineLength == -1)
            {
                result.append(token);
                token = null;
            }
            else if (removableWrapCharacters.indexOf(token) != -1)
            {
                if (currentLineLength + 1 > maxLineLength)
                {
                    result.append(lineBreak);
                    if (tokenizer.hasMoreTokens())
                    {
                        result.append(currentIndent);
                        currentLineLength = currentIndent.length();
                    }
                    lineBreakPossible = false;
                    lastCharRemovable = false;
                }
                else
                {
                    result.append(token);
                    currentLineLength++;
                    lineBreakPossible = true;
                    lastCharRemovable = true;
                }
                token = null;
            }
            else if (lineBreakPossible)
            {
                // we must check next token
                String nextToken = null;
                if (tokenizer.hasMoreTokens())
                {
                    nextToken = tokenizer.nextToken();
                }
                int unbreakableChunkSize;
                if (nextToken == null
                        || "\r".equals(nextToken)
                        || "\n".equals(nextToken)
                        || wrapAfterCharacters.contains(token)
                        || JAVADOC_ATTRIBUTE_START.equals(nextToken)
                        || removableWrapCharacters.contains(nextToken))
                {
                    unbreakableChunkSize = token.length();
                }
                else
                {
                    unbreakableChunkSize = token.length() + nextToken.length();
                }
                if (currentLineLength + unbreakableChunkSize > maxLineLength)
                {
                    // break now
                    if (lastCharRemovable)
                    {
                        result.replace(
                                result.length() - 1,
                                result.length(),
                                "");
                    }
                    result.append(lineBreak)
                    .append(currentIndent)
                    .append(token);
                    currentLineLength
                    = currentIndent.length() + token.length();
                }
                else
                {
                    // no break necessary
                    result.append(token);
                    currentLineLength += token.length();
                }
                lastCharRemovable = false;
                lineBreakPossible = wrapAfterCharacters.contains(token);
                token = nextToken;
            }
            else
            {
                result.append(token);
                currentLineLength += token.length();
                lastCharRemovable = false;
                lineBreakPossible = wrapAfterCharacters.contains(token);
                token = null;
            }
        }
        if (!result.toString().endsWith(lineBreak))
        {
            result.append(lineBreak);
        }
        return result.toString();
    }

    public String getLineBreak()
    {
        return lineBreak;
    }

    public void setLineBreak(String lineBreak)
    {
        if (!("\r".equals(lineBreak)) && !("\r\n".equals(lineBreak)))
        {
            throw new IllegalArgumentException(
                    "lineBreak must be either \\r or \\r\\n");
        }
        this.lineBreak = lineBreak;
    }

    public int getMaxLineLength()
    {
        return maxLineLength;
    }

    public void setMaxLineLength(int maxLineLength)
    {
        this.maxLineLength = maxLineLength;
    }

    public String getIndent()
    {
        return indent;
    }

    public void setIndent(String indent)
    {
        this.indent = indent;
    }

    public String getRemovableWrapCharacters()
    {
        return removableWrapCharacters;
    }

    public void setRemovableWrapCharacters(String removableWrapCharacters)
    {
        this.removableWrapCharacters = removableWrapCharacters;
    }

    public String getWrapAfterCharacters()
    {
        return wrapAfterCharacters;
    }

    public void setWrapAfterCharacters(String wrapAfterCharacters)
    {
        this.wrapAfterCharacters = wrapAfterCharacters;
    }

    /**
     * Removes the trailing characters from a string builder.
     * The characters to be removed are passed in as parameter.
     *
     * @param stringBuilder the string builder to remove the end from, not null.
     * @param removeChars The characters to remove if they appear at the end,
     *        not null.
     */
    static void removeEnd(StringBuilder stringBuilder, String removeChars)
    {
        Set<Character> removeCharSet = new HashSet<>();
        for (char character : removeChars.toCharArray())
        {
            removeCharSet.add(character);
        }
        int index = stringBuilder.length();
        while (index > 0)
        {
            if (!removeCharSet.contains(stringBuilder.charAt(index - 1)))
            {
                break;
            }
            index--;
        }
        // index is now last char in String which does not match pattern
        // maybe -1 if all the string matches or the input is empty
        stringBuilder.replace(index, stringBuilder.length(), "");
    }
}
