package org.apache.torque.util;

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
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.peer.BookPeer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests the methods in BasePeer directly, without any generated classes.
 *
 * @version $Id: BasePeerTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class BasePeerTest extends BaseDatabaseTestCase
{
    private final CountHelper countHelper = new CountHelper();

    public void testDeleteGuessTableNameFromCriterion() throws Exception
    {
        // prepare
        cleanBookstore();
        insertBookstoreData();
        Criteria criteria = new Criteria(BookPeer.DATABASE_NAME);
        criteria.where(BookPeer.TITLE, "Book 1 - Author 1");

        // execute
        BasePeerImpl<?> basePeerImpl = new BasePeerImpl<>();
        int deleted = basePeerImpl.doDelete(criteria);

        // verify
        assertEquals(1, deleted);
        assertEquals(99, countHelper.count(BookPeer.getTableMap()));
        criteria = new Criteria();
        criteria.where(BookPeer.TITLE, "Book 1 - Author 1");
        assertEquals(0, countHelper.count(criteria));
    }

    public void testExecuteStatement() throws Exception
    {
        // prepare
        cleanBookstore();
        List<Author> authors = insertBookstoreData();
        String statement = "DELETE FROM " + BookPeer.TABLE_NAME
                + " WHERE " + BookPeer.BOOK_ID + "=?"
                + " AND "+ BookPeer.AUTHOR_ID + "=?";
        List<JdbcTypedValue> replacements = new ArrayList<>();
        replacements.add(new JdbcTypedValue(
                authors.get(2).getBooks().get(4).getBookId(),
                Types.INTEGER));
        replacements.add(
                new JdbcTypedValue(
                        authors.get(2).getAuthorId(),
                        Types.INTEGER));

        // execute
        BasePeerImpl<?> basePeerImpl = new BasePeerImpl<>();
        int deleted = basePeerImpl.executeStatement(statement, replacements);

        // verify
        assertEquals(1, deleted);
        assertEquals(99, countHelper.count(BookPeer.getTableMap()));
        Criteria criteria = new Criteria();
        criteria.and(
                BookPeer.BOOK_ID,
                authors.get(2).getBooks().get(4).getBookId());
        assertEquals(0, countHelper.count(criteria));
    }
}
