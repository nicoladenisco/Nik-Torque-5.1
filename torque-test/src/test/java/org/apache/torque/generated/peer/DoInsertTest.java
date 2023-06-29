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

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Column;
import org.apache.torque.ColumnImpl;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.IDMethod;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.dbobject.IntegerType;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.test.peer.IntegerTypePeer;
import org.apache.torque.util.ColumnValues;
import org.apache.torque.util.JdbcTypedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests the doInsert Methods in the Peer classes.
 *
 * @version $Id: SaveMethodsInPeerTest.java 1395238 2012-10-07 07:30:25Z tfischer $
 */
public class DoInsertTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;

    private static Log log =LogFactory.getLog(DoInsertTest.class);

    @BeforeEach
    public void setUp() throws Exception
    {
       
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Tests the doInsert method for a simple object.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testDoInsert() throws Exception
    {
        Author author = new Author();
        author.setName("newName");

        // execute
        AuthorPeer.doInsert(author);

        // verify
        assertNotNull(author.getAuthorId());
        assertEquals("newName", author.getName());

        authorList.add(author);
        verifyBookstore(authorList);
    }

    /**
     * Tests the doInsert method with ColumnValues.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testDoInsertWithColumnValues() throws Exception
    {
        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                AuthorPeer.NAME,
                new JdbcTypedValue("newName", Types.VARCHAR));

        // execute
        ObjectKey<?> result = AuthorPeer.doInsert(columnValues);

        // verify
        Author author = new Author();
        author.setName("newName");
        author.setAuthorId(((BigDecimal) result.getValue()).intValue());

        authorList.add(author);
        verifyBookstore(authorList);
    }

    /**
     * Tests the doInsert method with ColumnValues only where a non-primary-key
     * Column value is a verbatim SQL expression.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testDoInsertWithColumnValuesAndVerbatimSqlValue()
            throws Exception
    {
        IntegerTypePeer.doDelete(new Criteria());

        IntegerType integerType = new IntegerType();
        integerType.save();

        ColumnValues columnValues = new ColumnValues();
        columnValues.put(
                IntegerTypePeer.INTEGER_VALUE,
                new JdbcTypedValue(5, Types.INTEGER));
        columnValues.put(
                IntegerTypePeer.INTEGER_OBJECT_VALUE,
                new JdbcTypedValue(new ColumnImpl("3")));

        // execute
        ObjectKey<?> objectKey = IntegerTypePeer.doInsert(columnValues);

        // verify
        List<IntegerType> allIntegerTypes = IntegerTypePeer.doSelect(
                new Criteria().addDescendingOrderByColumn(IntegerTypePeer.ID));
        assertEquals(2, allIntegerTypes.size());
        assertEquals(5, allIntegerTypes.get(0).getIntegerValue());
        assertEquals(new Integer(3), allIntegerTypes.get(0).getIntegerObjectValue());
        assertEquals(
                ((BigDecimal) objectKey.getValue()).intValue(),
                allIntegerTypes.get(0).getId());
    }

    /**
     * Tests the doInsert method where the inserted values are selected
     * from the table.
     *
     * @throws Exception if a database error occurs.
     */
    @ArgumentsSource(AdapterProvider.class)
    @Test
    public void testDoInsertWithSelectSingleRecordIdSet(Adapter adapter)
            throws Exception
    {
        if (adapter instanceof MssqlAdapter)
        {
            log.warn("testDoInsertWithSelectSingleRecordIdSet(): "
                    + "Setting explicit Ids is not (easily) supported by MSSQL");
            return;
        }
        Criteria selectCriteria = new Criteria()
                .where(AuthorPeer.AUTHOR_ID, authorList.get(0).getAuthorId())
                .addSelectColumn(new ColumnImpl(
                        AuthorPeer.AUTHOR_ID.getSchemaName(),
                        AuthorPeer.AUTHOR_ID.getTableName(),
                        AuthorPeer.AUTHOR_ID.getColumnName(),
                        AuthorPeer.AUTHOR_ID.getSqlExpression() + " + 100"))
                .addSelectColumn(AuthorPeer.NAME);

        // execute
        int numberOfInsertedRows = AuthorPeer.doInsert(
                new Column[] {AuthorPeer.AUTHOR_ID, AuthorPeer.NAME},
                selectCriteria);

        // verify
        assertEquals(1, numberOfInsertedRows);
        Author author = new Author();
        author.setAuthorId(authorList.get(0).getAuthorId() + 100);
        author.setName(authorList.get(0).getName());
        authorList.add(author);
        verifyBookstore(authorList);
    }

    /**
     * Tests the doInsert method where the inserted values are selected
     * from the table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testDoInsertWithSelectSingleRecord()
            throws Exception
    {
        if ( IDMethod.ID_BROKER == AuthorPeer.getTableMap().getPrimaryKeyMethod())
        {
            log.warn("Cannot test insert.. select statement with autoincrement"
                    + " id method");
            return;
        }
        Criteria selectCriteria = new Criteria()
                .where(AuthorPeer.AUTHOR_ID, authorList.get(0).getAuthorId())
                .addSelectColumn(AuthorPeer.NAME);

        // execute
        int numberOfInsertedRows = AuthorPeer.doInsert(
                new Column[] {AuthorPeer.NAME},
                selectCriteria);

        // verify
        assertEquals(1, numberOfInsertedRows);
        Author author = new Author();
        author.setAuthorId(authorList.get(9).getAuthorId() + 1);
        author.setName(authorList.get(0).getName());
        authorList.add(author);
        verifyBookstore(authorList);
    }

    /**
     * Tests the doInsert method where the inserted values are selected
     * from the table.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testDoInsertWithSelectMultipleRecords()
            throws Exception
    {
        if ( IDMethod.ID_BROKER == AuthorPeer.getTableMap().getPrimaryKeyMethod())
        {
            log.warn("Cannot test insert.. select statement with autoincrement"
                    + " id method");
            return;
        }
        Criteria selectCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID, authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.TITLE);

        // execute
        int numberOfInsertedRows = AuthorPeer.doInsert(
                new Column[] {AuthorPeer.NAME},
                selectCriteria);

        // verify
        assertEquals(10, numberOfInsertedRows);
        List<Author> selectedAuthorList = AuthorPeer.doSelect(new Criteria());
        // check size of List
        assertEquals(20, selectedAuthorList.size());
        for (Author author : selectedAuthorList)
        {
            if (author.getAuthorId() > authorList.get(9).getAuthorId())
            {
                boolean found = false;
                for (Book book : authorList.get(0).getBooks())
                {
                    if (author.getName().equals(book.getTitle()))
                    {
                        found = true;
                        authorList.get(0).getBooks().remove(book);
                        break;
                    }
                }
                if (!found)
                {
                    fail("unexpected author with name " + author.getName());
                }
            }
        }
    }
}
