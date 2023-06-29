package org.apache.torque.generator.source.stream;

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

import static org.apache.torque.generator.file.FileTestUtils.createSetFrom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.file.Fileset;
import org.apache.torque.generator.source.Source;
import org.apache.torque.generator.source.SourceException;
import org.junit.Test;

public class FileSourceProviderTest
{
    private static File TEST_BASE_DIR = new File("src/test/file");

    @Test
    public void testReadFiles()
            throws ConfigurationException, SourceException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("*.properties"),
                null);
        FileSourceProvider fileSourceProvider
            = new FileSourceProvider(null, fileset, false);
        fileSourceProvider.init(
                new ConfigurationHandlers(),
                new ControllerState());

        // We read all sources and add the first property key to a hash set.
        // As all the source files only have one key equal to the base filename
        // we can make sure we have read the expected files correctly.
        Set<String> resultKeys = new HashSet<>();
        {
            assertTrue(fileSourceProvider.hasNext());
            Source source = fileSourceProvider.next();
            Object key = source.getRootElement().getChild("entry")
                    .getAttribute("key");
            resultKeys.add((String) key);
        }
        {
            assertTrue(fileSourceProvider.hasNext());
            Source source = fileSourceProvider.next();
            Object key = source.getRootElement().getChild("entry")
                    .getAttribute("key");
            resultKeys.add((String) key);
        }
        assertFalse(fileSourceProvider.hasNext());
        Set<String> expectedKeys = new HashSet<>();
        expectedKeys.add("1");
        expectedKeys.add("11");
        assertEquals(expectedKeys, resultKeys);
    }

    @Test
    public void testReadFilesCombineFiles()
            throws ConfigurationException, SourceException
    {
        Fileset fileset = new Fileset(
                TEST_BASE_DIR,
                createSetFrom("*.properties"),
                null);
        FileSourceProvider fileSourceProvider
            = new FileSourceProvider(null, fileset, true);
        fileSourceProvider.init(
                new ConfigurationHandlers(),
                new ControllerState());

        // We read all sources and add the first property key to a hash set.
        // As all the source files only have one key equal to the base filename
        // we can make sure we have read the expected files correctly.
        Set<String> resultKeys = new HashSet<>();
        assertTrue(fileSourceProvider.hasNext());
        Source source = fileSourceProvider.next();

        assertEquals(2, source.getRootElement().getChildren("file").size());
        Object key = source.getRootElement()
                .getChildren("file").get(0)
                .getChild("properties")
                .getChild("entry")
                .getAttribute("key");
        resultKeys.add((String) key);
        key = source.getRootElement()
                .getChildren("file").get(1)
                .getChild("properties")
                .getChild("entry")
                .getAttribute("key");
        resultKeys.add((String) key);
        Set<String> expectedKeys = new HashSet<>();
        expectedKeys.add("1");
        expectedKeys.add("11");
        assertEquals(expectedKeys, resultKeys);

        Set<String> resultPaths = new HashSet<>();
        String expectedPath = (String) source.getRootElement()
                .getChildren("file").get(0).getAttribute("path");
        expectedPath = expectedPath.replace('\\', '/');
        resultPaths.add(expectedPath);
        expectedPath = (String) source.getRootElement()
                .getChildren("file").get(1).getAttribute("path");
        expectedPath = expectedPath.replace('\\', '/');
        resultPaths.add(expectedPath);
        Set<String> expectedPaths = new HashSet<>();
        expectedPaths.add("src/test/file/./1.properties");
        expectedPaths.add("src/test/file/./11.properties");
        assertEquals(expectedPaths, resultPaths);

        assertFalse(fileSourceProvider.hasNext());
    }

    @Test(expected = ConfigurationException.class)
    public void testReadFilesBasedirNull()
            throws ConfigurationException, SourceException
    {
        Fileset fileset = new Fileset(
                null,
                createSetFrom("*.properties"),
                null);
        FileSourceProvider fileSourceProvider
            = new FileSourceProvider(null, fileset, false);
        fileSourceProvider.init(
                new ConfigurationHandlers(),
                new ControllerState());
    }
}
