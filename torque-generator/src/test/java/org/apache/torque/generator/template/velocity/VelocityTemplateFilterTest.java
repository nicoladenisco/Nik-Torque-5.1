package org.apache.torque.generator.template.velocity;


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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class VelocityTemplateFilterTest
{
    private VelocityTemplateFilter velocityTemplateFilter;

    @BeforeEach
    public void setUp()
    {
        velocityTemplateFilter = new VelocityTemplateFilter();
    }

    @Test
    public void testEmptyInput() throws IOException
    {
        assertFilteredEquals("", "");
    }

    @Test
    public void testEmptyLines() throws IOException
    {
        assertFilteredEquals("\n", "\n");
        assertFilteredEquals("\r\n", "\r\n");
    }

    @Test
    public void testCommentWithoutSpaces() throws IOException
    {
        assertFilteredEquals("#Comment", "#Comment");
        assertFilteredEquals("#Comment\n", "#Comment\n");
        assertFilteredEquals("#Comment\r\n", "#Comment\r\n");
    }

    @Test
    public void testCommentWithSpaces() throws IOException
    {
        assertFilteredEquals("# Com ment", "# Com ment");
        assertFilteredEquals("# Com ment\n", "# Com ment\n");
        assertFilteredEquals("# Com ment\r\n", "# Com ment\r\n");
    }

    @Test
    public void testWhitespaceLine() throws IOException
    {
        assertFilteredEquals("        ", "    \t");
        assertFilteredEquals("        \n", "    \t\n");
        assertFilteredEquals("        \r\n", "    \t\r\n");
    }

    @Test
    public void testLineStartingWithWhitespaceNoComment() throws IOException
    {
        assertFilteredEquals("     ab    cd", "\t ab\tcd");
        assertFilteredEquals("     ab    cd\n", "\t ab\tcd\n");
        assertFilteredEquals("      ab    cd\r\n", "\t ab\tcd\r\n");
    }

    @Test
    public void testLineStartingWithWhitespaceComment() throws IOException
    {
        assertFilteredEquals("# ab    cd", "\t # ab\tcd");
        assertFilteredEquals("# ab    cd\n", "\t # ab\tcd\n");
        assertFilteredEquals("# ab    cd\r\n", "\t # ab\tcd\r\n");
    }

    @Test
    public void testCommentAfterWhitespaceLineSpaces() throws IOException
    {
        assertFilteredEquals(" \n#Comment", " \n #Comment");
        assertFilteredEquals(" \n#Comment\n", " \n #Comment\n");
        assertFilteredEquals(" \n#Comment\r\n", " \n #Comment\r\n");
    }

    /**
     * Filters the input <code>input</code> and checks whether the output
     * of the filter is equal to <code>expected</code>.
     *
     * @param expected The expected outcome.
     * @param input the input for the filter.
     *
     * @throws IOException If an IOException occurs during Streaming.
     */
    private void assertFilteredEquals(String expected, String input)
            throws IOException
    {
        InputStream inputStream = velocityTemplateFilter.filter(
                new ByteArrayInputStream(expected.getBytes("ISO-8859-1")),
                "ISO-8859-1");
        String result = readAndCloseStream(inputStream, "ISO-8859-1");
        assertEquals(expected, result);
    }

    /**
     * Reads the content of a Stream into a String.
     *
     * @param stream the stream to read, not null.
     * @param encoding The encoding to use.
     *
     * @return the Stream as String.
     *
     * @throws IOException If an IO Error occurs during reading.
     */
    private String readAndCloseStream(InputStream stream, String encoding)
            throws IOException
    {
        Reader reader;
        if (encoding == null)
        {
            reader = new InputStreamReader(stream);
        }
        else
        {
            reader = new InputStreamReader(stream, encoding);
        }

        StringBuffer contentBuffer = new StringBuffer();
        while (true)
        {
            char[] charBuffer = new char[8192];
            int filledChars = reader.read(charBuffer);
            if (filledChars == -1)
            {
                break;
            }
            contentBuffer.append(charBuffer, 0, filledChars);
        }
        stream.close();
        return contentBuffer.toString();
    }
}
