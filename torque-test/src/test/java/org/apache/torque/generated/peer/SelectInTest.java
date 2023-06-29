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

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests selects using the IN operator.
 *
 * @version $Id: SelectInTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class SelectInTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;

    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Tests "in" query with String list and ignoreCase = false.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInWithStringListNoIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        List<String> nameList = new ArrayList<>();
        nameList.add("Author 1");
        nameList.add("Author 2");
        criteria.where(AuthorPeer.NAME, nameList, Criteria.IN);
        criteria.addDescendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 2 but got " + result.size(),
                2,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + authorList.get(1).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(1).getAuthorId(),
                author.getAuthorId());
        author = result.get(1);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " at second position but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests "in" query with String list and ignoreCase = true.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInWithIntegerListNoIgnoreCase() throws Exception
    {
        Criteria criteria = new Criteria();
        List<Integer> idList = new ArrayList<>();
        idList.add(authorList.get(0).getAuthorId());
        idList.add(authorList.get(1).getAuthorId());
        criteria.where(AuthorPeer.AUTHOR_ID, idList, Criteria.IN);
        criteria.addDescendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 2 but got " + result.size(),
                2,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + authorList.get(1).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(1).getAuthorId(),
                author.getAuthorId());
        author = result.get(1);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " at second position but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests "in" query with String list containing null value.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInWithStringListAndNullValue() throws Exception
    {
        Criteria criteria = new Criteria();
        List<String> isbnList = new ArrayList<>();

        isbnList.add("ISBN 1 - 1");
        isbnList.add(null);
        criteria.where(BookPeer.ISBN, isbnList, Criteria.IN);
        criteria.addAscendingOrderByColumn(BookPeer.BOOK_ID);

        List<Book> result = BookPeer.doSelect(criteria);
        assertEquals("Expected result of size 11 but got " + result.size(),
                11,
                result.size());
        Book book = result.get(0);
        assertEquals("Expected book with Id "
                + authorList.get(0).getBooks().get(0).getBookId()
                + " at first position but got "
                + book.getBookId(),
                authorList.get(0).getBooks().get(0).getBookId(),
                book.getBookId());
        book = result.get(1);
        assertEquals("Expected book with ISBN null "
                + " at second position but got "
                + book.getIsbn(),
                null,
                book.getIsbn());
    }

    /**
     * Tests "in" query with String array and ignoreCase = false.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInWithStringArrayIgnoreCaseFalse() throws Exception
    {
        Criteria criteria = new Criteria();
        String[] nameArray = new String[] {"Author 1", "Author 3"};
        criteria.where(AuthorPeer.NAME, nameArray, Criteria.IN);
        criteria.addDescendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 2 but got " + result.size(),
                result.size(),
                2);
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + authorList.get(2).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(2).getAuthorId(),
                author.getAuthorId());
        author = result.get(1);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " at second position but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests "in" query with String list and ignoreCase = true.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInWithStringListIgnoreCaseTrue() throws Exception
    {
        Criteria criteria = new Criteria();
        List<String> nameList = new ArrayList<>();
        nameList.add("AuTHor 1");
        nameList.add("AuTHor 2");
        criteria.where(AuthorPeer.NAME, nameList, Criteria.IN);
        criteria.setIgnoreCase(true);
        criteria.addDescendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 2 but got " + result.size(),
                2,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + authorList.get(1).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(1).getAuthorId(),
                author.getAuthorId());
        author = result.get(1);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " at second position but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests "in" query with Integer list and ignoreCase = true.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInWithIntegerListIgnoreCaseTrue() throws Exception
    {
        Criteria criteria = new Criteria();
        List<Integer> idList = new ArrayList<>();
        idList.add(authorList.get(0).getAuthorId());
        idList.add(authorList.get(1).getAuthorId());
        criteria.where(AuthorPeer.AUTHOR_ID, idList, Criteria.IN);
        criteria.setIgnoreCase(true);
        criteria.addDescendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 2 but got " + result.size(),
                2,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + authorList.get(1).getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                authorList.get(1).getAuthorId(),
                author.getAuthorId());
        author = result.get(1);
        assertEquals("Expected author with Id "
                + authorList.get(0).getAuthorId()
                + " at second position but got "
                + author.getAuthorId(),
                authorList.get(0).getAuthorId(),
                author.getAuthorId());
    }
}
