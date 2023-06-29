package org.apache.torque.datatypes;

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

import org.apache.torque.test.dbobject.BlobType;
import org.apache.torque.test.peer.BlobTypePeer;

public class BlobTest extends ByteTypeTest<BlobType>
{

    public BlobTest() {
        super(BlobTypePeer.getBlobTypePeerImpl(), new BlobType());
    }

    @Override
    protected void setBytes(byte[] bytes, BlobType toFill)
    {
        toFill.setBlobValue(bytes);
        toFill.setBlobObjectValue(bytes);
    }

    @Override
    protected byte[] getBytes(BlobType toRead)
    {
        return toRead.getBlobValue();
    }

    @Override
    public boolean canWriteNullBytes() {
        // the problem with postgresql is that a setNull(Types.BLOB)
        // is executed by torque which is not accepted for BYTEA columns
        return false;
    }
}
