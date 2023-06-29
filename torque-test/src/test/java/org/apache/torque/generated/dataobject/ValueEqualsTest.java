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

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.test.dbobject.Book;
import org.junit.jupiter.api.Test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Tests the valueEquals method in the data object classes.
 *
 * @version $Id: ValueEqualsTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class ValueEqualsTest extends BaseDatabaseTestCase
{
    /**
     * Checks the valueEquals method outcome if all colums are not-null
     * and equal in the two objects.
     */
    @Test
    public void testAllSetAndEqual()
    {
        Book book1 = new Book();
        book1.setAuthorId(1);
        book1.setBookId(5);
        book1.setIsbn("abc");
        book1.setTitle("title");
        Book book2 = new Book();
        book2.setAuthorId(1);
        book2.setBookId(5);
        book2.setIsbn("abc");
        book2.setTitle("title");
        assertTrue(book1.valueEquals(book2));
    }

    /**
     * Checks the valueEquals method outcome if all colums are not-null
     * and equal in the two objects except the primary key column.
     */
    public void testAllSetDifferentPk()
    {
        Book book1 = new Book();
        book1.setAuthorId(1);
        book1.setBookId(5);
        book1.setIsbn("abc");
        book1.setTitle("title");
        Book book2 = new Book();
        book2.setAuthorId(1);
        book2.setBookId(7);
        book2.setIsbn("abc");
        book2.setTitle("title");
        assertFalse(book1.valueEquals(book2));
    }

    /**
     * Checks the valueEquals method outcome if all colums are not-null
     * and equal in the two objects except a non-primary key column.
     */
    @Test
    public void testAllSetDifferentNonPk()
    {
        Book book1 = new Book();
        book1.setAuthorId(1);
        book1.setBookId(5);
        book1.setIsbn("abc");
        book1.setTitle("title");
        Book book2 = new Book();
        book2.setAuthorId(1);
        book2.setBookId(5);
        book2.setIsbn("abc");
        book2.setTitle("otherTitle");
        assertFalse(book1.valueEquals(book2));
    }

    /**
     * Checks the valueEquals method outcome if all colums are not-null
     * and equal in the two objects except a non-primary-key foreign-key column.
     */
    @Test
    public void testAllSetDifferentNonPkFk()
    {
        Book book1 = new Book();
        book1.setAuthorId(3);
        book1.setBookId(5);
        book1.setIsbn("abc");
        book1.setTitle("title");
        Book book2 = new Book();
        book2.setAuthorId(1);
        book2.setBookId(5);
        book2.setIsbn("abc");
        book2.setTitle("title");
        assertFalse(book1.valueEquals(book2));
    }

    /**
     * Checks the valueEquals method outcome if a column is null
     * in the two objects and all other fields are set and equal.
     */
    @Test
    public void testFieldNotSetBothNull()
    {
        Book book1 = new Book();
        book1.setAuthorId(1);
        book1.setBookId(5);
        book1.setIsbn(null);
        book1.setTitle("title");
        Book book2 = new Book();
        book2.setAuthorId(1);
        book2.setBookId(5);
        book2.setIsbn(null);
        book2.setTitle("title");
        assertTrue(book1.valueEquals(book2));
    }


    /**
     * Checks the valueEquals method outcome if a column is null
     * in only one the two objects and all other fields are set and equal.
     */
    @Test
    public void testFieldNotSetOneNull()
    {
        Book book1 = new Book();
        book1.setAuthorId(1);
        book1.setBookId(5);
        book1.setIsbn(null);
        book1.setTitle("title");
        Book book2 = new Book();
        book2.setAuthorId(1);
        book2.setBookId(5);
        book2.setIsbn("abc");
        book2.setTitle("Title");
        assertFalse(book1.valueEquals(book2));
    }
}
