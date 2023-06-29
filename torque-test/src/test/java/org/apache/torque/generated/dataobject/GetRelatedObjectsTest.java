package org.apache.torque.generated.dataobject;

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

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.util.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;


/**
 * Tests whether the getRelatedObjects methods works
 *
 * @version $Id: GetRelatedObjectsTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class GetRelatedObjectsTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;

    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Checks that the test principle for switching silent db fetch works.
     * This test does not check the functionality to test, but rather ensures
     * that we can switch of silent db fetching to check whether objects
     * are already loaded or not.
     */
    @Test
    public void testSwitchSilentFetchingOff() throws Exception
    {
        Author baseObject = authorList.get(0);
        baseObject = AuthorPeer.doSelectSingleRecord(baseObject);
        // switch silent fetching off
        Adapter adapter = Torque.getOrCreateDatabase(Torque.getDefaultDB()).getAdapter();
        try
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(null);
            try
            {
                baseObject.getBooks();
                fail("Exception excpected");
            }
            catch (Exception e)
            {
                // expected
            }
        }
        finally
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(adapter);
        }
    }

    /**
     * Tests getters of related objects.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjects() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);

        // execute loading books
        List<Book> books = author.getBooks();

        // verify
        assertEquals(10, books.size());
        assertTrue(books.get(0).getTitle().endsWith("- Author 1"));
        checkBackreferenceExists(author, books);
    }

    /**
     * Tests getters of related objects, after the collection was initialized.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsAfterInit() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);

        // execute : init, then load books
        author.initBooks();
        List<Book> books = author.getBooks();

        // verify
        // Books should not be loaded from db but cache should be used
        assertEquals(0, books.size());
    }

    /**
     * Tests getters of related objects with a Criteria object
     *
     * @throws Exception if the test fails
     */
    public void testGetRelatedObjectsWithCriteria() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);
        Book book = authorList.get(0).getBooks().get(2);

        // execute : load books
        criteria = new Criteria().where(BookPeer.BOOK_ID, book.getBookId());
        List<Book> books = author.getBooks(criteria);

        // verify
        assertEquals(1, books.size());
        assertEquals(book.getBookId(), books.get(0).getBookId());
        checkBackreferenceExists(author, books);
    }

    /**
     * Tests getters of related objects with a Criteria object
     * after the related objects were loaded with another criteria.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsWithOtherCriteria() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);
        author.getBooks();
        Book book = authorList.get(0).getBooks().get(2);

        // execute : load books
        criteria = new Criteria().where(BookPeer.BOOK_ID, book.getBookId());
        List<Book> books = author.getBooks(criteria);

        // verify
        assertEquals(1, books.size());
        assertEquals(book.getBookId(), books.get(0).getBookId());
        checkBackreferenceExists(author, books);
    }

    /**
     * Tests getters of related objects with a Criteria object
     * after the init method was called on the collection.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsWithCriteriaAfterInit() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);
        author.initBooks();
        Book book = authorList.get(0).getBooks().get(2);

        // execute : load books
        criteria = new Criteria().where(BookPeer.BOOK_ID, book.getBookId());
        List<Book> books = author.getBooks(criteria);

        // verify
        assertEquals(1, books.size());
        assertEquals(book.getBookId(), books.get(0).getBookId());
        checkBackreferenceExists(author, books);
    }

    /**
     * Tests getters of related objects.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsWithConnection() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);

        // execute loading books
        Connection connection = Transaction.begin();
        List<Book> books = author.getBooks(connection);
        Transaction.commit(connection);

        // verify
        assertEquals(10, books.size());
        assertTrue(books.get(0).getTitle().endsWith("- Author 1"));
        checkBackreferenceExists(author, books);
    }

    /**
     * Tests getters of related objects, after the collection was initialized.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsWithConnectionAfterInit() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);

        // execute : init, then load books
        author.initBooks();
        Connection connection = Transaction.begin();
        List<Book> books = author.getBooks(connection);
        Transaction.commit(connection);

        // verify
        // Books should not be loaded from db but cache should be used
        assertEquals(0, books.size());
    }

    /**
     * Tests getters of related objects with a Criteria object
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsWithConnectionAndCriteria() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);
        Book book = authorList.get(0).getBooks().get(2);

        // execute : load books
        criteria = new Criteria().where(BookPeer.BOOK_ID, book.getBookId());
        Connection connection = Transaction.begin();
        List<Book> books = author.getBooks(criteria, connection);
        Transaction.commit(connection);

        // verify
        assertEquals(1, books.size());
        assertEquals(book.getBookId(), books.get(0).getBookId());
        checkBackreferenceExists(author, books);
    }

    /**
     * Tests getters of related objects with a Criteria object
     * after the related objects were loaded with another criteria.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsWithConneectionAndOtherCriteria() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);
        author.getBooks();
        Book book = authorList.get(0).getBooks().get(2);

        // execute : load books
        criteria = new Criteria().where(BookPeer.BOOK_ID, book.getBookId());
        Connection connection = Transaction.begin();
        List<Book> books = author.getBooks(criteria, connection);
        Transaction.commit(connection);

        // verify
        assertEquals(1, books.size());
        assertEquals(book.getBookId(), books.get(0).getBookId());
        checkBackreferenceExists(author, books);
    }

    /**
     * Tests getters of related objects with a Criteria object
     * after the init method was called on the collection.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetRelatedObjectsWithConnectionAndCriteriaAfterInit() throws Exception
    {
        Criteria criteria = new Criteria().where(
                AuthorPeer.AUTHOR_ID,
                authorList.get(0).getAuthorId());
        Author author = AuthorPeer.doSelect(criteria).get(0);
        author.initBooks();
        Book book = authorList.get(0).getBooks().get(2);

        // execute : load books
        criteria = new Criteria().where(BookPeer.BOOK_ID, book.getBookId());
        Connection connection = Transaction.begin();
        List<Book> books = author.getBooks(criteria, connection);
        Transaction.commit(connection);

        // verify
        assertEquals(1, books.size());
        assertEquals(book.getBookId(), books.get(0).getBookId());
        checkBackreferenceExists(author, books);
    }


    /**
     * Switches silent fetching off and checks that a backreference
     * to the author is set on each book in the list.
     *
     * @param author the author to which the backreference should be made
     * @param books the books to check.
     *
     * @throws TorqueException if a database operation fails
     */
    private void checkBackreferenceExists(final Author author, final List<Book> books)
            throws TorqueException
    {
        Adapter adapter = Torque.getOrCreateDatabase(Torque.getDefaultDB()).getAdapter();
        try
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(null);
            for (Book book : books)
            {
                assertEquals(author, book.getAuthor());
                // check that adding the backreference did not lead to changed objects
                assertFalse(book.isModified());
            }
        }
        finally
        {
            Torque.getOrCreateDatabase(Torque.getDefaultDB()).setAdapter(adapter);
        }
    }

}
