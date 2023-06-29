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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.apache.torque.generator.template.TemplateFilter;

/**
 * A Filter for velocity templates. It has the effect of beautifying the output
 * of the velocity outlet.
 */
public class VelocityTemplateFilter implements TemplateFilter
{
    /**
     * With which String tabs should be replaced
     */
    private static final String TAB_REPLACEMENT = "    ";

    /**
     * This method filters the template and replaces some
     * unwanted characters. For example it removes leading
     * spaces in front of velocity commands and replaces
     * tabs with spaces to prevent bounces in different
     * code editors with different tab-width-setting.
     *
     * @param resource the input stream to filter.
     * @param encoding the encoding to use, or null for the system encoding.
     *
     * @return the filtered input stream.
     *
     * @throws IOException if creating, reading or writing to a stream fails.
     */
    @Override
    public InputStream filter(InputStream resource, String encoding)
            throws IOException
    {
        InputStreamReader streamReader;
        if (encoding == null)
        {
        	// default to UTF 8 otherwise FindBugs complains of DM_DEFAULT_ENCODING
            streamReader = new InputStreamReader(resource, StandardCharsets.UTF_8);
        }
        else
        {
            streamReader = new InputStreamReader(resource, encoding);
        }
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Writer outputStreamWriter = null;
        if (encoding == null)
        {
        	// default to UTF 8 otherwise FindBugs complains of DM_DEFAULT_ENCODING
            outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        }
        else
        {
            outputStreamWriter = new OutputStreamWriter(outputStream, encoding);
        }

        boolean onlySpacesInLineSoFar = true;
        StringBuilder startLineBuffer = new StringBuilder();
        while (true)
        {
            int readChar = bufferedReader.read();

            if (readChar == -1)
            {
                break;
            }
            if (!onlySpacesInLineSoFar)
            {
                outputStreamWriter.write(readChar);
                if ('\r' == readChar || '\n' == readChar)
                {
                    onlySpacesInLineSoFar = true;
                }
            }
            else
            {
                if (' ' == readChar)
                {
                    startLineBuffer.append((char) readChar);
                }
                else if ('\t' == readChar)
                {
                    startLineBuffer.append(TAB_REPLACEMENT);
                }
                else if ('#' == readChar)
                {
                    if (startLineBuffer.length() != 0)
                    {
                        startLineBuffer = new StringBuilder();
                    }
                    outputStreamWriter.write(readChar);
                    onlySpacesInLineSoFar = false;
                }
                else if ('\r' == readChar || '\n' == readChar)
                {
                    if (startLineBuffer.length() != 0)
                    {
                        outputStreamWriter.write(startLineBuffer.toString());
                        startLineBuffer = new StringBuilder();
                    }
                    outputStreamWriter.write(readChar);
                }
                else
                {
                    if (startLineBuffer.length() != 0)
                    {
                        outputStreamWriter.write(startLineBuffer.toString());
                        startLineBuffer = new StringBuilder();
                    }
                    outputStreamWriter.write(readChar);
                    onlySpacesInLineSoFar = false;

                }
            }
        }
        outputStreamWriter.write(startLineBuffer.toString());

        outputStreamWriter.flush();
        outputStreamWriter.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
