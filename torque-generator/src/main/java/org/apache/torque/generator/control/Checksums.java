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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Stores Checksums and dates of the checksums, keyed by a String.
 * @version $Id$
 *
 */
public class Checksums
{
    /** Array containing the 16 hex characters. */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /** The file encoding for the lastChanges file. */
    private static final String FILE_ENCODING = "ISO-8859-1";

    /** The '0' character as byte. */
    private static final byte ZERO_LITERAL_AS_BYTE = (byte) '0';

    /** The '9' character as byte. */
    private static final byte NINE_LITERAL_AS_BYTE = (byte) '9';

    /** The base for characters > 9 in a hex string. */
    private static final byte CHAR_BASE = (byte) 55;

    /** Bits per character of a hexadecimal number. */
    private static final int HEX_BITS_PER_CHAR = 4;

    /** The largest hex two character number. */
    private static final int HEX_SMALLEST_TWOCHAR_NUMBER = 0x10;

    /** The smallest hex one character number. */
    private static final int HEX_LARGEST_ONECHAR_NUMBER = 0x0F;

    /** The largest hex two character number. */
    private static final int HEX_LARGEST_TWOCHAR_NUMBER = 0xFF;

    /** The separator between tokens in the written file. */
    private static final String SEPARATOR = "-";

    /** The checksums, keyed by the name of the checked entity. */
    private final Map<String, byte[]> checksums
        = new HashMap<>();

    /** The modification dates, keyed by the name of the checked entity. */
    private final Map<String, Date> modificationDates
        = new HashMap<>();

    public byte[] getChecksum(final String name)
    {
        return checksums.get(name);
    }

    public Checksums setChecksum(
            final String name,
            final byte[] checksum)
    {
        if (checksum == null)
        {
            throw new NullPointerException("checksum must not be null");
        }
        checksums.put(name, checksum);
        return this;
    }

    public Map<String, byte[]> getChecksums()
    {
        return Collections.unmodifiableMap(checksums);
    }

    public Date getModificationDate(final String name)
    {
        return modificationDates.get(name);
    }

    public Checksums setModificationDate(
            final String name,
            final Date modificationDate)
    {
        if (modificationDate == null)
        {
            throw new NullPointerException("modificationDate must not be null");
        }
        modificationDates.put(name, modificationDate);
        return this;
    }

    public Map<String, Date> getModificationDates()
    {
        return Collections.unmodifiableMap(modificationDates);
    }

    public Checksums writeToFile(final File toWriteTo)
            throws IOException
    {
        Set<String> keys = new HashSet<>();
        keys.addAll(checksums.keySet());
        keys.addAll(modificationDates.keySet());

        Checksums existing = new Checksums();
        existing.readFromFile(toWriteTo);
        for (String key : existing.getChecksums().keySet())
        {
            if (keys.add(key))
            {
                checksums.put(key, existing.getChecksum(key));
                if (existing.getModificationDate(key) != null)
                {
                    modificationDates.put(key, existing.getModificationDate(key));
                }
            }
        }

        for (String key : existing.getModificationDates().keySet())
        {
            if (keys.add(key))
            {
                modificationDates.put(key, existing.getModificationDate(key));
            }
        }

        StringBuilder content = new StringBuilder();
        for (String key : keys)
        {
            Date modificationDate = modificationDates.get(key);
            if (modificationDate != null)
            {
                content.append(modificationDate.getTime());
            }
            content.append(SEPARATOR);
            byte[] checksum = checksums.get(key);
            if (checksum != null)
            {
                for (int j = 0; j < checksum.length; j++)
                {
                    int v = checksum[j] & HEX_LARGEST_TWOCHAR_NUMBER;
                    content.append(HEX_ARRAY[v >>> HEX_BITS_PER_CHAR])
                    .append(HEX_ARRAY[v & HEX_LARGEST_ONECHAR_NUMBER]);
                }
            }
            content.append(SEPARATOR).append(key).append("\n");
        }
        FileUtils.writeStringToFile(
                toWriteTo,
                content.toString(),
                FILE_ENCODING);
        return this;
    }

    public Checksums readFromFile(final File toReadFrom)
            throws IOException
    {
        checksums.clear();
        modificationDates.clear();

        if (!toReadFrom.exists())
        {
            // nothing to read
            return this;
        }

        String content = FileUtils.readFileToString(
                toReadFrom,
                FILE_ENCODING);
        for (String line : StringUtils.split(content, "\n"))
        {
            if (StringUtils.isBlank(line))
            {
                continue;
            }
            StringTokenizer tokenizer = new StringTokenizer(
                    line,
                    SEPARATOR,
                    true);

            String datestring;
            String checksumString;
            String name;
            try
            {
                datestring = tokenizer.nextToken();
                if (SEPARATOR.equals(datestring))
                {
                    datestring = null;
                }
                else if (!(SEPARATOR.equals(tokenizer.nextToken())))
                {
                    throw new IOException("invalid line (no separator after date)"
                            + line);
                }

                checksumString = tokenizer.nextToken();
                if (SEPARATOR.equals(checksumString))
                {
                    checksumString = null;
                }
                else if (!(SEPARATOR.equals(tokenizer.nextToken())))
                {
                    throw new IOException("invalid line (no separator after checksum)"
                            + line);
                }
                name = tokenizer.nextToken("");
            }
            catch (NoSuchElementException e)
            {
                throw new IOException("invalid line (not enough separators)"
                        + line);
            }

            if (datestring != null)
            {
                Date modificationDate = new Date(Long.parseLong(datestring));
                modificationDates.put(name, modificationDate);
            }
            if (checksumString != null)
            {
                byte[] checksum = new byte[checksumString.length() / 2];
                boolean last = false;
                int currentByte = 0;
                int i = 0;
                for (char hexValue : checksumString.toCharArray())
                {
                    byte read;
                    if (hexValue > NINE_LITERAL_AS_BYTE)
                    {
                        read = (byte) (hexValue - CHAR_BASE);
                    }
                    else
                    {
                        read = (byte) (hexValue - ZERO_LITERAL_AS_BYTE);
                    }
                    if (!last)
                    {
                        currentByte = HEX_SMALLEST_TWOCHAR_NUMBER * read;
                        last = true;
                    }
                    else
                    {
                        currentByte = currentByte + read;
                        checksum[i] = (byte) currentByte;
                        i++;
                        last = false;
                    }
                }
                checksums.put(name, checksum);
            }
        }
        return this;
    }
}
