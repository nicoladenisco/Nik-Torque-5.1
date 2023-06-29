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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for CombinedFileSource.
 * @version $Id: $
 */
public class CombinedFileSourceTest
{
    @Mock
    private FileSource fileSource1;

    @Mock
    private FileSource fileSource2;

    @Mock
    private FileSource fileSource3;

    /** System under test. */
    private CombinedFileSource combinedFileSource;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        List<FileSource> fileSources = new ArrayList<>();
        fileSources.add(fileSource1);
        fileSources.add(fileSource2);
        fileSources.add(fileSource3);
        combinedFileSource = new CombinedFileSource(fileSources);
    }

    @Test
    public void testGetLastModifiedAllDatesSet()
    {
        when(fileSource1.getLastModified()).thenReturn(new Date(100000));
        when(fileSource2.getLastModified()).thenReturn(new Date(99999));
        when(fileSource3.getLastModified()).thenReturn(new Date(100001));
        assertEquals(new Date(99999), combinedFileSource.getLastModified());
    }

    @Test
    public void testGetLastModifiedNoDateSet()
    {
        when(fileSource1.getLastModified()).thenReturn(null);
        when(fileSource2.getLastModified()).thenReturn(null);
        when(fileSource3.getLastModified()).thenReturn(null);
        assertEquals(null, combinedFileSource.getLastModified());
    }

    @Test
    public void testGetLastModifiedOneDateNotSet()
    {
        when(fileSource1.getLastModified()).thenReturn(new Date(100000));
        when(fileSource2.getLastModified()).thenReturn(null);
        when(fileSource3.getLastModified()).thenReturn(new Date(100001));
        assertEquals(null, combinedFileSource.getLastModified());
    }

    @Test
    public void testGetContentChecksum()
    {
        when(fileSource1.getContentChecksum()).thenReturn(new byte[] {1, 2, 100, -100, 100});
        when(fileSource2.getContentChecksum()).thenReturn(new byte[] {2, 4});
        when(fileSource3.getContentChecksum()).thenReturn(new byte[] {4, 8, 100, -100, -100});
        assertArrayEquals(new byte[] {7, 14, -56, 56, 0}, combinedFileSource.getContentChecksum());
    }

    @Test
    public void testGetContentChecksumOneNull()
    {
        when(fileSource1.getContentChecksum()).thenReturn(new byte[] {1, 2, 100, -100});
        when(fileSource2.getContentChecksum()).thenReturn(null);
        when(fileSource3.getContentChecksum()).thenReturn(new byte[] {4, 8, 100, -100});
        assertEquals(null, combinedFileSource.getContentChecksum());
    }

}
