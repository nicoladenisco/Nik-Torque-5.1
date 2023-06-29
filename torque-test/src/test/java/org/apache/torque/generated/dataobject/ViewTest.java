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

import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.BookAuthors;
import org.apache.torque.test.peer.BookAuthorsPeer;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNull;

/**
 * Tests whether the save methods work in the db object classes.
 *
 * @version $Id: ViewTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class ViewTest extends BaseDatabaseTestCase
{
    /**
     * Tests that we can select from the view.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testSelect() throws Exception
    {
        // prepare
        cleanBookstore();
        List<Author> bookstoreContent = insertBookstoreData();
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(BookAuthorsPeer.AUTHOR_ID);
        criteria.addAscendingOrderByColumn(BookAuthorsPeer.BOOK_ID);

        // execute
        List<BookAuthors> result = BookAuthorsPeer.doSelect(criteria);

        // verify
        assertEquals(100, result.size());
        assertEquals("Author 1", result.get(0).getAuthorName());
        assertEquals("Book 1 - Author 1" , result.get(0).getBookTitle());
        assertEquals("Author 10", result.get(99).getAuthorName());
        assertEquals("Book 10 - Author 10" , result.get(99).getBookTitle());

        verifyBookstore(bookstoreContent); // db not changed
    }
}
