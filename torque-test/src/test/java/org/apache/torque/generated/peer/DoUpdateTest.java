package org.apache.torque.generated.peer;

import static org.junit.Assert.assertEquals;

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

import java.sql.Types;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ColumnImpl;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.IntegerType;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.IntegerTypePeer;
import org.apache.torque.util.ColumnValues;
import org.apache.torque.util.JdbcTypedValue;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;
/**
 * Tests the doUpdate Methods in the Peer classes.
 *
 * @version $Id: SaveMethodsInPeerTest.java 1395238 2012-10-07 07:30:25Z tfischer $
 */
public class DoUpdateTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;


    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Tests the doUpdate method for a simple object.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDoUpdate() throws Exception
    {
        Author author = authorList.get(1).copy();
        author.setNew(false);
        author.setAuthorId(authorList.get(1).getAuthorId());

        // execute
        author.setName("newName");
        int numberOfRows = AuthorPeer.doUpdate(author);

        // verify
        assertEquals(1, numberOfRows);
        assertEquals(authorList.get(1).getAuthorId(), author.getAuthorId());
        assertEquals("newName", author.getName());

        authorList.get(1).setName("newName");
        verifyBookstore(authorList);
    }

    /**
     * Tests the doUpdate method with Criteria and ColumnValues.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDoUpdateWithCriteraAndColumnValues() throws Exception
    {
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                AuthorPeer.NAME,
                new JdbcTypedValue("newName", Types.VARCHAR));
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(1).getAuthorId());

        // execute
        int numberOfRows = AuthorPeer.doUpdate(criteria, columnValues);

        // verify
        assertEquals(1, numberOfRows);
        authorList.get(1).setName("newName");
        verifyBookstore(authorList);
    }

    /**
     * Tests the doUpdate method with ColumnValues only.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDoUpdateWithColumnValues() throws Exception
    {
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                AuthorPeer.NAME,
                new JdbcTypedValue("newName", Types.VARCHAR));
        columnValues.put(
                AuthorPeer.AUTHOR_ID,
                new JdbcTypedValue(
                        authorList.get(1).getAuthorId(),
                        Types.INTEGER));

        // execute
        int numberOfRows = AuthorPeer.doUpdate(columnValues);

        // verify
        assertEquals(1, numberOfRows);
        authorList.get(1).setName("newName");
        verifyBookstore(authorList);
    }

    /**
     * Tests the doUpdate method with ColumnValues only where the primary key
     * Column value is a verbatim SQL expression.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDoUpdateWithColumnValuesAndVerbatimSqlValueInPk()
            throws Exception
    {
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                AuthorPeer.NAME,
                new JdbcTypedValue("newName", Types.VARCHAR));
        columnValues.put(
                AuthorPeer.AUTHOR_ID,
                new JdbcTypedValue(new ColumnImpl(
                        Integer.toString(authorList.get(1).getAuthorId()))));

        // execute
        int numberOfRows = AuthorPeer.doUpdate(columnValues);

        // verify
        assertEquals(1, numberOfRows);
        authorList.get(1).setName("newName");
        verifyBookstore(authorList);
    }

    /**
     * Tests the doUpdate method with ColumnValues only where a non-primary-key
     * Column value is a verbatim SQL expression.
     *
     * @throws Exception if a database error occurs.
     */
    public void testDoUpdateWithColumnValuesAndVerbatimSqlValue()
            throws Exception
    {
        IntegerTypePeer.doDelete(new Criteria());

        IntegerType integerType = new IntegerType();
        integerType.save();
        integerType = new IntegerType();
        integerType.save();

        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                IntegerTypePeer.INTEGER_VALUE,
                new JdbcTypedValue(5, Types.INTEGER));
        columnValues.put(
                IntegerTypePeer.INTEGER_OBJECT_VALUE,
                new JdbcTypedValue(new ColumnImpl("3")));
        columnValues.put(
                IntegerTypePeer.ID,
                new JdbcTypedValue(integerType.getId(), Types.INTEGER));

        // execute
        int numberOfRows = IntegerTypePeer.doUpdate(columnValues);

        // verify
        assertEquals(1, numberOfRows);
        List<IntegerType> allIntegerTypes = IntegerTypePeer.doSelect(
                new Criteria().addDescendingOrderByColumn(IntegerTypePeer.ID));
        assertEquals(2, allIntegerTypes.size());
        assertEquals(5, allIntegerTypes.get(0).getIntegerValue());
        assertEquals(new Integer(3), allIntegerTypes.get(0).getIntegerObjectValue());
    }
}
