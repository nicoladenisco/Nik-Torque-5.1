package org.apache.torque.generator.merge;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.torque.generator.GeneratorException;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.merge.MergeAlgorithm;
import org.eclipse.jgit.merge.MergeFormatter;
import org.eclipse.jgit.merge.MergeResult;

/**
 * Can execute a three-way merge. This class is thread safe.
 *
 * @version $Id: ThreeWayMerger.java 1840416 2018-09-09 15:10:22Z tv $
 */
public class ThreeWayMerger
{
    /**
     * Performs a three-way merge.
     *
     * @param base the base from which the other two versions are derived,
     *        not null.
     * @param generated the newly generated text, not null.
     * @param edited the possibly edited text, not null.
     * @param charsetName the name of the character set, not null.
     * @return the merge result, not null.
     *
     * @throws GeneratorException if merging fails.
     */
    public String merge(
            String base,
            String generated,
            String edited,
            String charsetName)
                    throws GeneratorException
    {
        MergeAlgorithm mergeAlgorithm = new MergeAlgorithm();
        MergeResult<RawText> mergeResult;
        try
        {
            mergeResult = mergeAlgorithm.merge(
                    RawTextComparator.DEFAULT,
                    new RawText(base.getBytes(charsetName)),
                    new RawText(generated.getBytes(charsetName)),
                    new RawText(edited.getBytes(charsetName)));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new GeneratorException(
                    "unknown character set " + charsetName,
                    e);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try
        {
            new MergeFormatter().formatMerge(
                    outputStream,
                    mergeResult,
                    "base",
                    "generated",
                    "edited",
                    charsetName);
        }
        catch (IOException e)
        {
            throw new GeneratorException("could nor render merge result", e);
        }
        try
        {
            String result
                = new String(outputStream.toByteArray(), charsetName);
            return result;
        }
        catch (UnsupportedEncodingException e)
        {
            throw new GeneratorException(
                    "unknown character set " + charsetName,
                    e);
        }
    }
}
