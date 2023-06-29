package org.apache.torque.manager;

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

import java.io.Serializable;
import java.sql.Connection;

import org.apache.torque.TorqueException;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.om.SimpleKey;

/**
 * Dummy Persistent implementation
 *
 */
public class TestPersistent implements Persistent, Serializable
{
    /** Serial ID  */
    private static final long serialVersionUID = 738162496580951932L;

    private ObjectKey<?> primaryKey;
    private boolean isNew = true;
    private boolean modified;

    /**
     * @see org.apache.torque.om.Persistent#getPrimaryKey()
     */
    @Override
    public ObjectKey<?> getPrimaryKey()
    {
        return primaryKey;
    }

    /**
     * @see org.apache.torque.om.Persistent#setPrimaryKey(org.apache.torque.om.ObjectKey)
     */
    @Override
    public void setPrimaryKey(ObjectKey<?> primaryKey) throws TorqueException
    {
        this.primaryKey = primaryKey;
    }

    /**
     * @see org.apache.torque.om.Persistent#setPrimaryKey(java.lang.String)
     */
    @Override
    public void setPrimaryKey(String primaryKey) throws TorqueException
    {
        setPrimaryKey(SimpleKey.keyFor(primaryKey));
    }

    /**
     * @see org.apache.torque.om.Persistent#isModified()
     */
    @Override
    public boolean isModified()
    {
        return modified;
    }

    /**
     * @see org.apache.torque.om.Persistent#isNew()
     */
    @Override
    public boolean isNew()
    {
        return isNew;
    }

    /**
     * @see org.apache.torque.om.Persistent#setNew(boolean)
     */
    @Override
    public void setNew(boolean b)
    {
        this.isNew = b;
    }

    /**
     * @see org.apache.torque.om.Persistent#setModified(boolean)
     */
    @Override
    public void setModified(boolean m)
    {
        this.modified = m;
    }

    /**
     * @see org.apache.torque.om.Persistent#save()
     */
    @Override
    public void save() throws Exception
    {
        // do nothing
        setModified(false);
        setNew(false);
    }

    /**
     * @see org.apache.torque.om.Persistent#save(java.lang.String)
     */
    @Override
    public void save(String dbName) throws Exception
    {
        // do nothing
        setModified(false);
        setNew(false);
    }

    /**
     * @see org.apache.torque.om.Persistent#save(java.sql.Connection)
     */
    @Override
    public void save(Connection con) throws Exception
    {
        // do nothing
        setModified(false);
        setNew(false);
    }
}
