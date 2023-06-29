package org.apache.torque.generated.peer;

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

import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.QualifiedName;
import org.apache.torque.test.peer.QualifiedNamePeer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests that accessing the database also works if the table name is qualified
 * by a schema name.
 *
 * @version $Id: QualifiedTableNameTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class QualifiedTableNameTest extends BaseDatabaseTestCase
{
    QualifiedName qualifiedName1;
    QualifiedName qualifiedName2;
    QualifiedName qualifiedName3;

    @BeforeEach
    public void setUp() throws Exception
    {
        QualifiedNamePeer.doDelete(new Criteria());
        qualifiedName1 = new QualifiedName();
        qualifiedName1.setPayload("qualifiedName1");
        qualifiedName1.save();
        qualifiedName2 = new QualifiedName();
        qualifiedName2.setPayload("qualifiedName2");
        qualifiedName2.save();
        qualifiedName3 = new QualifiedName();
        qualifiedName3.setPayload("qualifiedName3");
        qualifiedName3.save();
    }

    /**
     * Tests that reading an entry from a table with qualified table name works.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testRead() throws Exception
    {
        // prepare
        Criteria criteria = new Criteria();
        criteria.where(QualifiedNamePeer.ID, qualifiedName1.getId());
        criteria.and(QualifiedNamePeer.PAYLOAD, qualifiedName1.getPayload());

        // execute
        List<QualifiedName> qualifiedNames
        = QualifiedNamePeer.doSelect(criteria);

        // verify
        assertEquals(1, qualifiedNames.size());
        assertEquals(qualifiedName1.getId(), qualifiedNames.get(0).getId());
    }

    /**
     * Tests that sorting entries from a table with qualified table name works.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testReadWithSort() throws Exception
    {
        // prepare
        Criteria criteria = new Criteria();
        criteria.addDescendingOrderByColumn(QualifiedNamePeer.ID);

        // execute
        List<QualifiedName> qualifiedNames
        = QualifiedNamePeer.doSelect(criteria);

        // verify
        assertEquals(3, qualifiedNames.size());
        assertEquals(qualifiedName3.getId(), qualifiedNames.get(0).getId());
        assertEquals(qualifiedName2.getId(), qualifiedNames.get(1).getId());
        assertEquals(qualifiedName1.getId(), qualifiedNames.get(2).getId());
    }

    /**
     * Tests that updating an entry in a table with qualified table name works.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testUpdate() throws Exception
    {
        // prepare
        qualifiedName2.setPayload("qualifiedName2a");

        // execute
        qualifiedName2.save();

        // verify
        Criteria criteria = new Criteria();
        criteria.where(QualifiedNamePeer.ID, qualifiedName2.getId());
        List<QualifiedName> qualifiedNames
        = QualifiedNamePeer.doSelect(criteria);
        assertEquals(1, qualifiedNames.size());
        assertEquals(qualifiedName2.getId(), qualifiedNames.get(0).getId());
        assertEquals(
                qualifiedName2.getPayload(),
                qualifiedNames.get(0).getPayload());
    }

    /**
     * Tests that deleting an entry from a table with qualified table name works.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testDelete() throws Exception
    {
        // execute
        QualifiedNamePeer.doDelete(qualifiedName2);

        // verify
        Criteria criteria = new Criteria();
        List<QualifiedName> qualifiedNames
        = QualifiedNamePeer.doSelect(criteria);
        assertEquals(2, qualifiedNames.size());
        assertFalse(qualifiedNames.contains(qualifiedName2));
    }

    /**
     * Tests that the table name in the peer does not contain the schema name.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testTableNameInPeer() throws Exception
    {
        // verify
        assertEquals("qualified_name", QualifiedNamePeer.TABLE.getName());
        assertEquals("qualified_name", QualifiedNamePeer.TABLE_NAME);
    }
    /**

     * Tests that the schema name returned by the peer is correct.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testSchemaNameInPeer() throws Exception
    {
        // prepare
        String schemaName
        = Torque.getConfiguration().getString("qualifiedNameTest.schema");
        // verify
        assertEquals(schemaName, QualifiedNamePeer.TABLE.getSchemaName());
    }
}
