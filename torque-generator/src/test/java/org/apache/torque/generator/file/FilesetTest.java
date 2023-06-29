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
import static org.apache.torque.generator.file.FileTestUtils.assertFileListEquals;
import static org.apache.torque.generator.file.FileTestUtils.createSetFrom;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.torque.generator.file.Fileset;
import org.junit.Test;

/**
 * Tests for the class Fileset.
 *
 * @version $Id: FilesetTest.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class FilesetTest
{
    private static File TEST_BASE_DIR = new File("src/test/file");

    @Test
    public void testAnalyzePathNoWildcards()
    {
        String path = "/tmp/schema.xml";
        int baseDirSeparatorPos = Fileset.getWildcardFreeSeparatorPos(path);
        assertEquals(4, baseDirSeparatorPos);
        String wildcardFree
        = Fileset.getPathPartBefore(path, baseDirSeparatorPos);
        assertEquals("/tmp", wildcardFree);
        String wildcardPattern
        = Fileset.getPathPartAfter(path, baseDirSeparatorPos);
        assertEquals("schema.xml", wildcardPattern);
    }

    @Test
    public void testAnalyzePathOnlyFilename()
    {
        String path = "schema.xml";
        int baseDirSeparatorPos = Fileset.getWildcardFreeSeparatorPos(path);
        assertEquals(-1, baseDirSeparatorPos);
        String wildcardFree
        = Fileset.getPathPartBefore(path, baseDirSeparatorPos);
        assertEquals(".", wildcardFree);
        String wildcardPattern
        = Fileset.getPathPartAfter(path, baseDirSeparatorPos);
        assertEquals("schema.xml", wildcardPattern);
    }

    @Test
    public void testAnalyzePathQuestionMark()
    {
        String path = "C:\\schema\\?\\schema.xml";
        int baseDirSeparatorPos = Fileset.getWildcardFreeSeparatorPos(path);
        assertEquals(9, baseDirSeparatorPos);
        String wildcardFree
        = Fileset.getPathPartBefore(path, baseDirSeparatorPos);
        assertEquals("C:\\schema", wildcardFree);
        String wildcardPattern
        = Fileset.getPathPartAfter(path, baseDirSeparatorPos);
        assertEquals("?\\schema.xml", wildcardPattern);
    }

    @Test
    public void testAnalyzePathAsterisk()
    {
        String path = "C:\\schema\\*\\schema.xml";
        int baseDirSeparatorPos = Fileset.getWildcardFreeSeparatorPos(path);
        assertEquals(9, baseDirSeparatorPos);
        String wildcardFree
        = Fileset.getPathPartBefore(path, baseDirSeparatorPos);
        assertEquals("C:\\schema", wildcardFree);
        String wildcardPattern
        = Fileset.getPathPartAfter(path, baseDirSeparatorPos);
        assertEquals("*\\schema.xml", wildcardPattern);
    }

    @Test
    public void testAnalyzePathMultipleWildcards()
    {
        String path = "/tmp/*/???/schema.xml";
        int baseDirSeparatorPos = Fileset.getWildcardFreeSeparatorPos(path);
        assertEquals(4, baseDirSeparatorPos);
        String wildcardFree
        = Fileset.getPathPartBefore(path, baseDirSeparatorPos);
        assertEquals("/tmp", wildcardFree);
        String wildcardPattern
        = Fileset.getPathPartAfter(path, baseDirSeparatorPos);
        assertEquals("*/???/schema.xml", wildcardPattern);
    }

    @Test
    public void testAnalyzePathWildcardFirst()
    {
        String path = "*/???/schema.xml";
        int baseDirSeparatorPos = Fileset.getWildcardFreeSeparatorPos(path);
        assertEquals(-1, baseDirSeparatorPos);
        String wildcardFree
        = Fileset.getPathPartBefore(path, baseDirSeparatorPos);
        assertEquals(".", wildcardFree);
        String wildcardPattern
        = Fileset.getPathPartAfter(path, baseDirSeparatorPos);
        assertEquals("*/???/schema.xml", wildcardPattern);
    }

    @Test
    public void testFilelistFixedNameBasedir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("1.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "./1.properties"));
    }

    @Test
    public void testFilelistFixedNameSubdir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/2.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "subfolder/2.properties"));
    }

    @Test
    public void testFilelistMixedSlashBackslash() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/subsubfolder\\3.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "subfolder/subsubfolder/3.properties"));
    }

    @Test
    public void testFilelistWildcardDir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("*//2.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "./subfolder/2.properties"));
    }

    @Test
    public void testFilelistQuestionmarkFilename() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("?.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "./1.properties"));
    }

    @Test
    public void testFilelistWildcardFilename() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("*.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "./1.properties"),
                new File(TEST_BASE_DIR, "./11.properties"));
    }

    @Test
    public void testFilelistDoubleDotsStayingInBaseDir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/../1.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "subfolder/../1.properties"));
    }

    @Test
    public void testFilelistDoubleDotsLeavingBaseDir() throws IOException
    {
        Fileset fileset = new Fileset(
                new File(TEST_BASE_DIR, "subfolder"),
                createSetFrom("../1.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "subfolder/../1.properties"));
    }

    @Test
    public void testFilelistAllNull() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                null,
                null);
        List<File> fileList = fileset.getFiles();
        Iterator<File> fileIt = fileList.iterator();
        while (fileIt.hasNext())
        {
            File file = fileIt.next();
            if (file.getPath().indexOf(".svn") != -1)
            {
                fileIt.remove();
            }
        }
        assertFileListEquals(
                fileList,
                new File(TEST_BASE_DIR, "1.properties"),
                new File(TEST_BASE_DIR, "11.properties"),
                new File(TEST_BASE_DIR, "package.html"),
                new File(TEST_BASE_DIR, "subfolder/2.properties"),
                new File(TEST_BASE_DIR, "subfolder/subsubfolder/3.properties"));
    }

    @Test
    public void testFilelistAllEmpty() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                new HashSet<String>(),
                new HashSet<String>());
        List<File> fileList = fileset.getFiles();
        Iterator<File> fileIt = fileList.iterator();
        while (fileIt.hasNext())
        {
            File file = fileIt.next();
            if (file.getPath().indexOf(".svn") != -1)
            {
                fileIt.remove();
            }
        }
        assertFileListEquals(
                fileList,
                new File(TEST_BASE_DIR, "1.properties"),
                new File(TEST_BASE_DIR, "11.properties"),
                new File(TEST_BASE_DIR, "package.html"),
                new File(TEST_BASE_DIR, "subfolder/2.properties"),
                new File(TEST_BASE_DIR, "subfolder/subsubfolder/3.properties"));
    }

    @Test
    public void testFilelistExcludeInBasedir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("*"),
                createSetFrom("11.properties"));
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "./package.html"),
                new File(TEST_BASE_DIR, "./1.properties"));
    }

    @Test
    public void testFilelistExcludeInSubdir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/*"),
                createSetFrom("subfolder/2.properties"));
        assertFileListEquals(
                fileset,
                new File[] {});
    }

    @Test
    public void testFilelistExcludeWithBackslashes() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/*"),
                createSetFrom("subfolder\\2.properties"));
        assertFileListEquals(
                fileset,
                new File[] {});
    }

    @Test
    public void testFilelistExcludeAsteriskSubdir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/*"),
                createSetFrom("*/2.properties"));
        assertFileListEquals(
                fileset,
                new File[] {});
    }

    @Test
    public void testFilelistExcludeQuestionMarkSubdir() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/*"),
                createSetFrom("su??old?r/2.properties"));
        assertFileListEquals(
                fileset,
                new File[] {});
    }

    @Test
    public void testFilelistExcludeDot() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("subfolder/*"),
                createSetFrom("su??old?r/./2.properties"));
        assertFileListEquals(
                fileset,
                new File[] {});
    }

    @Test
    public void testFilelistExcludeDoubleDot() throws IOException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("*"),
                createSetFrom("subfolder/../11.properties"));
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "./package.html"),
                new File(TEST_BASE_DIR, "./1.properties"));
    }

    @Test
    public void testFilelistExcludeDoubleDotLeavingBaseDir() throws IOException
    {
        Fileset fileset = new Fileset(
                new File(TEST_BASE_DIR, "subfolder"),
                createSetFrom("../*"),
                createSetFrom("../1.properties", ".././package.html"));
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "subfolder/../11.properties"));
    }

    @Test
    public void testFilelistBasedirNul() throws IOException
    {
        Fileset fileset = new Fileset(
                null,
                createSetFrom(TEST_BASE_DIR + "/1.properties"),
                null);
        assertFileListEquals(
                fileset,
                new File(TEST_BASE_DIR, "/1.properties"));
    }

}
