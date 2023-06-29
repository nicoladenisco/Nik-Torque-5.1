package org.apache.torque.generator.control;

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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.torque.generator.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests the class LastChangesContent.
 *
 * @version $Id: $
 */
public class ChecksumsTest extends BaseTest
{
    private static final File CHECKSUM_DIR
        = new File("target/test/lastChangesContent");

    private static final File CHECKSUM_FILE
        = new File(CHECKSUM_DIR, "checksum");

    @BeforeEach
    public void cleanUp()
    {
        if (CHECKSUM_FILE.exists())
        {
            CHECKSUM_FILE.delete();
        }
        if (CHECKSUM_DIR.exists())
        {
            CHECKSUM_DIR.delete();
        }
    }

    @Test
    public void testWriteToFile() throws Exception
    {
        // prepare
        Checksums lastChangesContent = new Checksums();
        lastChangesContent.setChecksum(
                "name",
                new byte[] {0, 100, -100, 1, -1});
        lastChangesContent.setChecksum(
                "nameChecksumOnly",
                new byte[] {127});
        lastChangesContent.setModificationDate(
                "name",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(
                        "2000-01-01 17:13:16 GMT"));
        lastChangesContent.setModificationDate(
                "name - ModDateOnly",
                new Date(12345L));
        assertFalse(CHECKSUM_DIR.exists());
        assertFalse(CHECKSUM_FILE.exists());

        // execute
        lastChangesContent.writeToFile(CHECKSUM_FILE);

        // verify
        assertTrue(CHECKSUM_FILE.exists());
        String fileContent = FileUtils.readFileToString(
                CHECKSUM_FILE,
                "ISO-8859-1");
        // order of lines not fixed, therefore check substrings.
        // expected is
        // "12345--nameModDateOnly\n"
        // + "946743196000-00649C01FF-name\n"
        // + "-7F-nameChecksumOnly"
        // where line order may be changed.
        String expectedLine1 = "946746796000-00649C01FF-name\n";
        String expectedLine2 = "-7F-nameChecksumOnly\n";
        String expectedLine3 = "12345--name - ModDateOnly\n";
        assertEquals(expectedLine1.length()
                + expectedLine2.length()
                + expectedLine3.length(),
                fileContent.length());
        assertTrue(
                fileContent.contains(expectedLine1),
                "Content " + fileContent + " should contain " + expectedLine1);
        assertTrue(
                fileContent.contains(expectedLine2),
                "Content " + fileContent + " should contain " + expectedLine2);
        assertTrue(
                fileContent.contains(expectedLine3),
                "Content " + fileContent + " should contain " + expectedLine3);
    }

    @Test
    public void testReadFromFile() throws Exception
    {
        // prepare
        Checksums lastChangesContent = new Checksums();
        FileUtils.writeStringToFile(
                CHECKSUM_FILE,
                "946743196000-00649C01FF-name\n"
                        + "-7F-nameChecksumOnly\n"
                        +"12345--name - ModDateOnly\n",
                "ISO-8859-1");
        // check that existing entries are cleared
        lastChangesContent.setChecksum("a", new byte[] {1});
        lastChangesContent.setModificationDate("b", new Date());

        // execute
        lastChangesContent.readFromFile(CHECKSUM_FILE);

        // verify
        assertEquals(2, lastChangesContent.getChecksums().size());
        assertArrayEquals(
                new byte[] {0, 100, -100, 1, -1},
                lastChangesContent.getChecksum("name"));
        assertArrayEquals(
                new byte[] {127},
                lastChangesContent.getChecksum("nameChecksumOnly"));
        assertEquals(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(
                        "2000-01-01 16:13:16 GMT"),
                lastChangesContent.getModificationDate("name"));
        assertEquals(
                new Date(12345L),
                lastChangesContent.getModificationDate("name - ModDateOnly"));
    }
}
