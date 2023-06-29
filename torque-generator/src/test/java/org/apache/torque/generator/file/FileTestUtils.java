package org.apache.torque.generator.file;

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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileTestUtils
{
    /**
     * Checks that the file list of the Fileset contains all the
     * expected paths and no other paths.
     *
     * @param fileset The Fileset which file list should be checked.
     * @param expectedPaths the paths which should be in the fileset.
     *
     * @throws IOException if Fileset#getFiles() throws it.
     */
    public static void assertFileListEquals(
            final Fileset fileset,
            final File... expectedPaths)
                    throws IOException
    {
        List<File> fileList = fileset.getFiles();
        assertFileListEquals(fileList, expectedPaths);
    }

    /**
     * Checks that a file list contains all the
     * expected paths and no other paths.
     *
     * @param fileList The list of files which should be checked.
     * @param expected the paths which should be in the file list.
     */
    public static void assertFileListEquals(
            final List<File> fileList,
            final File... expected)
    {
        Set<File> actualFiles = new HashSet<>();
        for (File file : fileList)
        {
            actualFiles.add(file);
        }
        Set<File> expectedFiles = new HashSet<>();
        for (File file : expected)
        {
            expectedFiles.add(file);
        }
        assertEquals(expectedFiles, actualFiles);
    }

    /**
     * Creates as set containing the Strings in content.
     *
     * @param content The Strings which should be in the set.
     *
     * @return the Set containing all the strings.
     */
    public static Set<String> createSetFrom(final String... content)
    {
        Set<String> result = new HashSet<>();
        for (String part : content)
        {
            result.add(part);
        }
        return result;
    }
}
