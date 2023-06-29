package org.apache.torque.datatypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.BasePeerImpl;
import org.junit.jupiter.params.provider.ArgumentsSource;



/**
 * Tests byte data types.
 * @version $Id: ByteTypeTest.java 1879896 2020-07-15 15:03:46Z gk $
 */
public abstract class ByteTypeTest<T extends Persistent> extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(ByteTypeTest.class);

    private BasePeerImpl<T> peer;

    private T dbObject;

    public ByteTypeTest(BasePeerImpl<T> peer, T dbObject) {
        this.peer = peer;
        this.dbObject = dbObject;
    }

    /**
     * Check that byte[] columns can be inserted and read correctly.
     *
     * @throws Exception if the test fails
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testInsertReadByteArray(Adapter adapter) throws Exception
    {
        // prepare
        peer.doDelete(new Criteria());

        // execute write
        T written = fillAndSave(dbObject, getByteArraySize(adapter));
        // execute read
        List<T> readList = peer.doSelect(new Criteria());

        // verify
        assertEquals(1, readList.size());
        T read = readList.get(0);
        assertTrue("read and written byte arrays should be equal. "
                + "Size of read byte array is"
                + getBytes(read).length
                + " size of byte array blob is "
                + getBytes(written).length,
                Arrays.equals(
                        getBytes(written),
                        getBytes(read)));
    }

    /**
     * Check that byte[] columns can be updated and read correctly.
     *
     * @throws Exception if the test fails
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testUpdateReadByteArray(Adapter adapter) throws Exception
    {
        // prepare
        peer.doDelete(new Criteria());
        fillAndSave(dbObject, getByteArraySize(adapter) / 2);

        // execute write
        T written = fillAndSave(dbObject, getByteArraySize(adapter));
        // execute read
        List<T> readList = peer.doSelect(new Criteria());

        // verify
        assertEquals(1, readList.size());
        T read = readList.get(0);
        assertTrue("read and written byte arrays should be equal. "
                + "Size of read byte array is"
                + getBytes(read).length
                + " size of written byte array is "
                + getBytes(written).length,
                Arrays.equals(
                        getBytes(written),
                        getBytes(read)));
    }

    /**
     * Check that byte[] columns can be updated and read correctly.
     *
     * @throws Exception if the test fails
     */
    @ArgumentsSource(AdapterProvider.class)
    public void testInsertReadNullByteArray(Adapter adapter) throws Exception
    {
        if (!canWriteNullBytes()) {
            log.error("database with adapter "
                    + adapter.getClass().getName()
                    + " does not support writing null bate arrays to type "
                    + dbObject.getClass().getName());
            return;
        }
        // prepare
        peer.doDelete(new Criteria());
        setBytes(null, dbObject);

        // execute write
        dbObject.save();
        // execute read
        List<T> readList = peer.doSelect(new Criteria());

        // verify
        assertEquals(1, readList.size());
        T read = readList.get(0);
        assertNull(getBytes(read));
    }


    protected abstract void setBytes(byte[] bytes, T toFill);

    protected abstract byte[] getBytes(T toRead);

    /**
     * Fills the byte value in a dbObject, writes it to to the database and returns it.
     *
     * @param toFill the object to fill and save, not null.
     * @param size the size of the byte array to set.
     *
     * @return the updated dbObject.
     *
     * @throws TorqueException if saving fails
     */
    private T fillAndSave(T toFill, int size)
           // throws Exception
    {
        byte[] bytes = createBytes(size);
        setBytes(bytes, toFill);
        try {
			toFill.save();
		} catch (Exception e) {
			fail(e.getMessage() +" - could not save " + toFill + " with bytes "+ new String(bytes));
			e.printStackTrace();
		}
        return toFill;
    }

    private byte[] createBytes(int size) {
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; ++i)
        {
            bytes[i] = new Integer(i % 256).byteValue();
        }
        return bytes;
    }

    /**
     * Can be overwritten to indicate that the database cannot write null
     * values for the selected type.
     *
     * @return true if null byte arrays can be written, false if not;
     */
    public boolean canWriteNullBytes() {
        return true;
    }

    public int getByteArraySize(Adapter adapter) {
        return 200000;
    }
}
