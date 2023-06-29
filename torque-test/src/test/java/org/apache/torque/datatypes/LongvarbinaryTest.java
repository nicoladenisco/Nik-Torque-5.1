package org.apache.torque.datatypes;

import org.apache.torque.adapter.Adapter;

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

import org.apache.torque.adapter.DerbyAdapter;
import org.apache.torque.test.dbobject.LongvarbinaryType;
import org.apache.torque.test.peer.LongvarbinaryTypePeer;

public class LongvarbinaryTest extends ByteTypeTest<LongvarbinaryType>
{

    public LongvarbinaryTest() {
        super(LongvarbinaryTypePeer.getLongvarbinaryTypePeerImpl(),
                new LongvarbinaryType());
    }

    @Override
    protected void setBytes(byte[] bytes, LongvarbinaryType toFill)
    {
        toFill.setLongvarbinaryValue(bytes);
        toFill.setLongvarbinaryObjectValue(bytes);
    }

    @Override
    protected byte[] getBytes(LongvarbinaryType toRead)
    {
        return toRead.getLongvarbinaryValue();
    }

    @Override
    public int getByteArraySize(Adapter adapter) {
        if (adapter instanceof DerbyAdapter) {
            return 32000; // Maximum 32700 for derby
        }
        return super.getByteArraySize(adapter);
    }

}
