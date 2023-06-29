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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tries to remove unused imports
 */
public class RemoveUnusedImportsProcessor implements StringProcessor
{
    /** Regex for import lines. */
    private static final Pattern IMPORT_PATTERN
    = Pattern.compile("^import .*\\.[^\r\n\\.]*$", Pattern.MULTILINE);

    /**
     * Converts Windows CR/LF character sequences to Unix LF sequences.
     *
     * @param toProcess the String to process, not null.
     *
     * @return the processed String, not null.
     */
    @Override
    public String process(final String toProcess)
    {
        Matcher matcher = IMPORT_PATTERN.matcher(toProcess);
        Map<String, String> importsForClasses = new HashMap<>();
        while (matcher.find())
        {
            String importLine = matcher.group();
            String className = importLine.substring(importLine.lastIndexOf(".") + 1);
            int indexOfSemicolon = className.lastIndexOf(';');
            if (indexOfSemicolon != -1)
            {
                int endPos = matcher.end();
                if (toProcess.length() > endPos)
                {
                    char followingChar = toProcess.charAt(endPos);
                    if (followingChar == '\r'
                            && toProcess.charAt(endPos + 1) == '\n')
                    {
                        importLine = importLine + followingChar + '\n';
                    }
                    else
                    {
                        importLine = importLine + followingChar;
                    }
                }
                className = className.substring(0, indexOfSemicolon).trim();
                importsForClasses.put(className, importLine);
            }
        }
        String result = toProcess;
        // do repeated runs to catch cases where e.g. a SimpleDateFormat
        // and Date are both unused imports.
        boolean doNextRun = true;
        while (doNextRun)
        {
            doNextRun = false;
            Iterator<Map.Entry<String, String>> entryIt
            = importsForClasses.entrySet().iterator();
            while (entryIt.hasNext())
            {
                Map.Entry<String, String> importForClassEntry = entryIt.next();
                String className = importForClassEntry.getKey();
                if (result.indexOf(className) == result.lastIndexOf(className))
                {
                    result = result.replace(importForClassEntry.getValue(), "");
                    doNextRun = true;
                    entryIt.remove();
                }
            }
        }
        return result;
    }
}
