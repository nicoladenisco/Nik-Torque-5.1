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

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.Torque;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.SimpleKey;
import org.apache.torque.om.mapper.CompositeMapper;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.test.recordmapper.AuthorRecordMapper;
import org.apache.torque.test.recordmapper.BookRecordMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests simple selects.
 *
 * @version $Id: SelectTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class SelectTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;

    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Tests a select using an Integer comparison.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSelectInteger() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 1 but got " + result.size(),
                1,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests a select where the column is on the right hand side
     * of the comparison.
     *
     * @throws Exception if the test fails
     */
    public void testSelectIntegerAsLvalue() throws Exception
    {
        Criteria criteria = new Criteria().where(
                authorList.get(0).getAuthorId(),
                AuthorPeer.AUTHOR_ID);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 1 but got " + result.size(),
                1,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests a select by the doSelectSingleRecord method.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSelectSingleRecord() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());

        Author author = AuthorPeer.doSelectSingleRecord(criteria);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests a select by the doSelectSingleRecord method with a connection.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSelectSingleRecordWithConnection() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());

        Connection connection = Torque.getConnection();
        Author author = AuthorPeer.doSelectSingleRecord(criteria, connection);
        Torque.closeConnection(connection);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests a select by the doSelectSingleRecord method by passing
     * a database object.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSelectSingleRecordWithObject() throws Exception
    {
        Author author = AuthorPeer.doSelectSingleRecord(authorList.get(0));
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests a select by the doSelectSingleRecord method if no result is found.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSelectSingleRecordResultNull() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                -1);

        Author author = AuthorPeer.doSelectSingleRecord(criteria);
        assertEquals(null, author);
    }

    /**
     * Tests a select by the doSelectSingleRecord method if too many records
     * are found.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testSelectSingleRecordTooManyRecords() throws Exception
    {
        Criteria criteria = new Criteria();

        try
        {
            AuthorPeer.doSelectSingleRecord(criteria);
            fail("Exception expected");
        }
        catch (TooManyRowsException e)
        {
            // expected
        }
    }

    /**
     * Tests a select with the operator IsNull using a null comparison.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSelectOperatorIsNull() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.where(BookPeer.ISBN, null, Criteria.ISNULL);

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(10, books.size());
    }

    /**
     * Tests a select with the operator IsNull using not null
     * as comparison value.
     *
     * @throws Exception if the test fails.
     */
    public void testSelectOperatorIsNullOtherComparison() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.where(BookPeer.ISBN, "xy", Criteria.ISNULL);

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(10, books.size());
    }

    /**
     * Tests a select with the operator IsNotNull
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSelectOperatorIsNotNull() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.where(BookPeer.ISBN, null, Criteria.ISNOTNULL);

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(90, books.size());
    }

    /**
     * Tests a select with the operator IsNotNull using not null
     * as comparison value.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSelectOperatorIsNotNullOtherComparison() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.where(BookPeer.ISBN, "xy", Criteria.ISNOTNULL);

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(90, books.size());
    }

    /**
     * Tests a partial select where only a part of the object's attributes
     * are filled and no other columns are read.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPartialSelectOnlyOwnColumns() throws Exception
    {
        Book bookToSelect = authorList.get(0).getBooks().get(0);
        Criteria criteria = new Criteria()
                .where(BookPeer.BOOK_ID, bookToSelect.getBookId())
                .addSelectColumn(BookPeer.BOOK_ID)
                .addSelectColumn(BookPeer.TITLE);

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(1, books.size());
        Book selectedBook = books.get(0);
        assertEquals(bookToSelect.getBookId(), selectedBook.getBookId());
        assertEquals(bookToSelect.getTitle(), selectedBook.getTitle());
        assertEquals(null, selectedBook.getIsbn());
        assertEquals(0, selectedBook.getAuthorId());
    }

    /**
     * Tests a partial select where only a part of the object's attributes
     * are filled and an offset is used.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPartialSelectOffset() throws Exception
    {
        Criteria criteria = new Criteria();
        Book bookToSelect = authorList.get(0).getBooks().get(0);
        criteria.where(BookPeer.BOOK_ID, bookToSelect.getBookId());
        criteria.addSelectColumn(BookPeer.BOOK_ID);
        criteria.addSelectColumn(BookPeer.TITLE);
        // use CompositeMapper to enforce offset
        CompositeMapper recordMapper = new CompositeMapper();
        recordMapper.addMapper(new BookRecordMapper(), 1);

        List<List<Object>> books = BookPeer.doSelect(criteria, recordMapper);
        assertEquals(1, books.size());
        Book selectedBook = (Book) books.get(0).get(0);
        assertEquals(0, selectedBook.getBookId());
        assertEquals(bookToSelect.getTitle(), selectedBook.getTitle());
        assertEquals(null, selectedBook.getIsbn());
        assertEquals(0, selectedBook.getAuthorId());
    }

    /**
     * Tests a select where an offset and limit is used.
     * The test uses doSelectAsStream()
     *
     * @throws Exception if the test fails.
     */
    public void testSelectStreamOffset() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(BookPeer.TITLE);
        criteria.setOffset(3);
        criteria.setLimit(5);
        BookPeer.addSelectColumns(criteria);

        Connection connection = Torque.getConnection();
        List<Book> books = BookPeer
                .doSelectAsStream(criteria, new BookRecordMapper(), connection)
                .collect(Collectors.toList());
        Torque.closeConnection(connection);

        assertEquals(5, books.size());
        Book selectedBook = books.get(0);
        assertEquals("Book 1 - Author 3", selectedBook.getTitle());
        selectedBook = books.get(4);
        assertEquals("Book 1 - Author 7", selectedBook.getTitle());
    }

    /**
     * Tests a partial select where only a part of the object's attributes
     * are filled and foreign columns are also read.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPartialSelectForeignColumns() throws Exception
    {
        Criteria criteria = new Criteria();
        Book bookToSelect = authorList.get(0).getBooks().get(0);
        criteria.where(BookPeer.BOOK_ID, bookToSelect.getBookId());
        criteria.addSelectColumn(AuthorPeer.AUTHOR_ID);
        criteria.addSelectColumn(BookPeer.BOOK_ID);
        criteria.addSelectColumn(AuthorPeer.NAME);
        criteria.addSelectColumn(BookPeer.TITLE);
        criteria.addJoin(BookPeer.AUTHOR_ID, AuthorPeer.AUTHOR_ID);

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(1, books.size());
        Book selectedBook = books.get(0);
        assertEquals(bookToSelect.getBookId(), selectedBook.getBookId());
        assertEquals(bookToSelect.getTitle(), selectedBook.getTitle());
        assertEquals(null, selectedBook.getIsbn());
        assertEquals(0, selectedBook.getAuthorId());
    }

    /**
     * Tests a partial select where only a part of the object's attributes
     * are filled and no other columns are read. It is expected that the
     * record mapper returns null.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testPartialSelectNoOwnColumns() throws Exception
    {
        Book bookToSelect = authorList.get(0).getBooks().get(0);
        Criteria criteria = new Criteria()
                .where(BookPeer.BOOK_ID, bookToSelect.getBookId())
                .addSelectColumn(BookPeer.BOOK_ID);

        List<Author> authors = BookPeer.doSelect(
                criteria, new AuthorRecordMapper());
        assertEquals(1, authors.size());
        assertEquals(null, authors.get(0));
    }

    /**
     * Tests a select for a SimpleKeyValue.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSelectSimpleKey() throws Exception
    {
        Book bookToSelect = authorList.get(0).getBooks().get(0);
        Criteria criteria = new Criteria()
                .where(BookPeer.BOOK_ID, bookToSelect.getPrimaryKey());

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(1, books.size());
        assertEquals(bookToSelect.getBookId(), books.get(0).getBookId());
    }

    /**
     * Tests a select for a SimpleKeyValue with a null value.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testSelectSimpleKeyNullValue() throws Exception
    {
        SimpleKey<?> keyToSelect = SimpleKey.keyFor((Integer) null);
        Criteria criteria = new Criteria()
                .where(BookPeer.ISBN, keyToSelect);

        List<Book> books = BookPeer.doSelect(criteria);
        assertEquals(10, books.size());
    }
}
