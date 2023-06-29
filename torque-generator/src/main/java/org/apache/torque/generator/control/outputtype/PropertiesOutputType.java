package org.apache.torque.generator.control.outputtype;

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
 * The output type for properties output.
 *
 * @version $Id: $
 */
public class PropertiesOutputType implements OutputType
{
    /** The unique key of the output type. */
    public static final String KEY = "properties";

    /** The start of a comment. */
    private static final String COMMENT_START = "# ";

    /**
     * Returns the start of a comment.
     * It is assumed that the comment itself does not contain line breaks.
     *
     * @param lineBreak the line break for the current output, not null.
     *
     * @return the String which starts a comment, not null.
     */
    @Override
    public String getCommentStart(String lineBreak)
    {
        return COMMENT_START;
    }

    /**
     * Returns the end of a comment.
     * It is assumed that the comment itself does not contain line breaks.
     *
     * @param lineBreak the line break for the current output, not null.
     *
     * @return the String which starts a comment, not null.
     */
    @Override
    public String getCommentEnd(String lineBreak)
    {
        return lineBreak;
    }
}
