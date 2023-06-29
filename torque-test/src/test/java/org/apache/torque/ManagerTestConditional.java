package org.apache.torque;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.torque.om.ObjectKey;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.dbobject.IfcTable;
import org.apache.torque.test.manager.AuthorManager;
import org.apache.torque.test.manager.BookManager;
import org.apache.torque.test.manager.TestInterfaceManager;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Runtime tests for managers and caching.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: ManagerTestConditional.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class ManagerTestConditional extends BaseDatabaseTestCase
{
    /**
     * Tests whether managers and caching work
     * @throws Exception if the test fails
     */
    @Test
    public void testManagers() throws Exception
    {
        cleanBookstore();
        AuthorManager.clear();
        BookManager.clear();
        AuthorManager.getManager().setRegion("om_Author");
        BookManager.getManager().setRegion("om_Book");

        Author author1 = new Author();
        author1.setName("author1");
        author1.save();
        Author author2 = new Author();
        author2.setName("author2");
        author2.save();

        Author myauthor = AuthorManager.getCachedInstance(author1.getPrimaryKey());
        assertNotNull("Primary key of Author1 should not be null", author1.getPrimaryKey());
        assertNotNull("MyAuthor should not be null", myauthor);
        assertTrue("Author1 and MyAuthor should point to the same cache instance", author1 == myauthor);

        Book book = new Book();
        book.setAuthor(author1);
        book.setTitle("Book 1");
        book.setIsbn("unknown");
        book.save();

        Book mybook = BookManager.getInstance(book.getPrimaryKey());
        assertTrue("Author1 and the author of MyBook should point to the same cache instance", author1 == mybook.getAuthor());
    }

    /**
     * Tests whether the getInstances method works.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetInstances() throws Exception
    {
        cleanBookstore();
        AuthorManager.clear();
        AuthorManager.getManager().setRegion("om_Author");

        Author author1 = new Author();
        author1.setName("author1");
        author1.save();
        Author author2 = new Author();
        author2.setName("author2");
        author2.save();

        List<ObjectKey<?>> authorKeys = 
                Stream.of(author1.getPrimaryKey(), author2.getPrimaryKey())
                    .collect(Collectors.toList()); 
        List<Author> authors = AuthorManager.getInstances(authorKeys);
        assertSame(author1, authors.get(0));
        assertSame(author2, authors.get(1));
    }

    /**
     * Tests whether the getInstances method returns null
     * if null is supplied as argument.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetInstancesNull() throws Exception
    {
        List<Author> authors = AuthorManager.getInstances(null);
        assertEquals(new ArrayList<Author>(), authors);
    }

    /**
     * Tests whether the getInstances method returns the empty list
     * if the empty list is supplied as argument.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetInstancesEmpty() throws Exception
    {
        List<Author> authors
                = AuthorManager.getInstances(new ArrayList<ObjectKey<?>>());
        assertEquals(new ArrayList<Author>(), authors);
    }

    /**
     * Tests whether managers return the right interface
     * @throws Exception if the test fails
     */
    @Test
    public void testInterfaces() throws Exception
    {
        TestInterface ifc = TestInterfaceManager.getInstance();

        assertTrue("TestInterfaceManager should create instances of TestInterface", ifc instanceof TestInterface);
        assertTrue("TestInterfaceManager should also create instances of IfcTable", ifc instanceof IfcTable);
    }
}
