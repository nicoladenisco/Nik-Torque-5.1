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
import java.util.HashSet;
import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.util.functions.Max;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

/**
 * Tests Group by statements.
 *
 * @version $Id: JoinTest.java 1436782 2013-01-22 07:51:31Z tfischer $
 */
public class GroupByTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;

    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Test a basic group by.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testAddGroupByColumn() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(BookPeer.AUTHOR_ID)
                .addGroupByColumn(BookPeer.AUTHOR_ID)
                .addAscendingOrderByColumn(BookPeer.AUTHOR_ID)
                .setDistinct();

        List<Integer> idList
        = BookPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(10, idList.size());
        assertEquals(getAuthorIds(), idList);
    }

    private List<Integer> getAuthorIds()
    {
        List<Integer> result = new ArrayList<>();
        for (Author author : authorList)
        {
            result.add(author.getAuthorId());
        }
        return result;
    }

    /**
     * Test a group by where the group by column is not selected.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testAddGroupByColumnNotInSelectClause() throws Exception
    {
        Criteria criteria = new Criteria()
                .addSelectColumn(new Max(BookPeer.BOOK_ID))
                .addGroupByColumn(BookPeer.AUTHOR_ID);

        List<Integer> maxList
        = BookPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(10, maxList.size());
        assertEquals(
                new HashSet<>(getMaxBookIdPerAuthor()),
                new HashSet<>(maxList));
    }

    private List<Integer> getMaxBookIdPerAuthor() throws TorqueException
    {
        List<Integer> result = new ArrayList<>();
        for (Author author : authorList)
        {
            int maxBookId = -1;
            for (Book book : author.getBooks())
            {
                if (book.getBookId() > maxBookId)
                {
                    maxBookId = book.getBookId();
                }
            }
            result.add(maxBookId);
        }
        return result;
    }
}
