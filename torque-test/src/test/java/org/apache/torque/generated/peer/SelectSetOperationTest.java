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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ColumnImpl;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.adapter.OracleAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests simple selects.
 *
 * @version $Id: SelectTest.java 1448403 2013-02-20 20:55:15Z tfischer $
 */
public class SelectSetOperationTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(SelectSetOperationTest.class);

    private List<Author> authorList;

    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Tests a select with UNION
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUnion() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.union(otherCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(20, result.size());

        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(0), expected);
        addBookIds(authorList.get(1), expected);
        assertEquals(expected, new HashSet<>(result));
    }

    /**
     * Tests a select with UNION ALL
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUnionAll() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(30, result.size());

        // check unique contents of list
        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(0), expected);
        addBookIds(authorList.get(1), expected);
        assertEquals(expected, new HashSet<>(result));

        // check that books of first author occur multiple times (in fact twice)
        // in the list
        for (int i = 0; i < 10; ++i)
        {
            assertFalse(result.indexOf(authorList.get(0).getBooks().get(i).getBookId())
                    == result.lastIndexOf(authorList.get(0).getBooks().get(i).getBookId()));
        }
    }

    /**
     * Tests a select with EXCEPT
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testExcept(Adapter adapter) throws Exception
    {
        if (adapter instanceof MysqlAdapter)
        {
            log.error("testExcept(): "
                    + "MySQL does not support "
                    + "the EXCEPT ALL operator");
            return;
        }
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria);
        otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.except(otherCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(10, result.size());

        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(1), expected);
        assertEquals(expected, new HashSet<>(result));
    }

    /**
     * Tests a select with EXCEPT ALL.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testExceptAll(Adapter adapter) throws Exception
    {
        if (adapter instanceof OracleAdapter
                || adapter instanceof MysqlAdapter
                || adapter instanceof MssqlAdapter)
        {
            log.error("testExceptAll(): "
                    + "Oracle, MySQL and MSSQL do not support "
                    + "the EXCEPT ALL operator");
            return;
        }
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria);
        otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.exceptAll(otherCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(20, result.size());

        // check unique result
        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(1), expected);
        assertEquals(expected, new HashSet<>(result));

        // check that books of the author occur multiple times (in fact twice)
        // in the list
        for (int i = 0; i < 10; ++i)
        {
            assertFalse(result.indexOf(authorList.get(1).getBooks().get(i).getBookId())
                    == result.lastIndexOf(authorList.get(1).getBooks().get(i).getBookId()));
        }
    }

    /**
     * Tests a select with EXCEPT ALL. The Set from which the Except statement
     * is executed contains the removable numbers twice,
     * Except all only removes one occurance so the other remains.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testExceptAllMultipleOccurances(Adapter adapter) throws Exception
    {
        if (adapter instanceof OracleAdapter
                || adapter instanceof MysqlAdapter
                || adapter instanceof MssqlAdapter)
        {
            log.error("testExceptAllMultipleOccurances(): "
                    + "Oracle, MySQL and MSSQL do not support "
                    + "the EXCEPT ALL operator");
            return;
        }
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria);
        otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.exceptAll(otherCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(30, result.size());

        // check unique result
        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(0), expected);
        addBookIds(authorList.get(1), expected);
        assertEquals(expected, new HashSet<>(result));

        // check that books of the second author occur multiple times (in fact twice)
        // in the list
        for (int i = 0; i < 10; ++i)
        {
            assertFalse(result.indexOf(authorList.get(1).getBooks().get(i).getBookId())
                    == result.lastIndexOf(authorList.get(1).getBooks().get(i).getBookId()));
        }
    }

    /**
     * Tests a select with INTERSECT
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testIntersect(Adapter adapter) throws Exception
    {
        if (adapter instanceof MysqlAdapter)
        {
            log.error("testIntersect(): "
                    + "MySQL does not support "
                    + "the EXCEPT ALL operator");
            return;
        }
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria);
        Criteria intersectCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        intersectCriteria.unionAll(otherCriteria);
        criteria.intersect(intersectCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(10, result.size());

        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(1), expected);

        assertEquals(expected, new HashSet<>(result));
    }

    /**
     * Tests a select with INTERSECT ALL. The common set is contained twice
     * in the intersected sets, so the intersection contains the numbers twice.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testIntersectAllMultipleOccurances(Adapter adapter) throws Exception
    {
        if (adapter instanceof OracleAdapter
                || adapter instanceof MysqlAdapter
                || adapter instanceof MssqlAdapter)
        {
            log.error("testIntersectAllMultipleOccurances(): "
                    + "Oracle, MySQL and MSSQL do not support "
                    + "the EXCEPT ALL operator");
            return;
        }
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria);
        Criteria intersectCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        intersectCriteria.unionAll(otherCriteria);
        criteria.intersectAll(intersectCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(20, result.size());

        // check unique result
        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(1), expected);
        assertEquals(expected, new HashSet<>(result));

        // check that books of the author occur multiple times (in fact twice)
        // in the list
        for (int i = 0; i < 10; ++i)
        {
            assertFalse(result.indexOf(authorList.get(1).getBooks().get(i).getBookId())
                    == result.lastIndexOf(authorList.get(1).getBooks().get(i).getBookId()));
        }
    }

    /**
     * Tests a select with INTERSECT ALL.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testIntersectAll(Adapter adapter) throws Exception
    {
        if (adapter instanceof OracleAdapter
                || adapter instanceof MysqlAdapter
                || adapter instanceof MssqlAdapter)
        {
            log.error("testIntersectAll(): "
                    + "Oracle, MySQL and MSSQL do not support "
                    + "the EXCEPT ALL operator");
            return;
        }
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId(),
                        Criteria.LESS_EQUAL)
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria);
        otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(1).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.intersectAll(otherCriteria);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(10, result.size());

        // check unique result
        Set<Integer> expected = new HashSet<>();
        addBookIds(authorList.get(1), expected);
        assertEquals(expected, new HashSet<>(result));
    }

    private void addBookIds(
            final Author author,
            final Collection<Integer> expected)
                    throws TorqueException
    {
        for (Book book : author.getBooks())
        {
            expected.add(book.getBookId());
        }
    }

    /**
     * Tests a select with UNION ALL, order by, limit and offset
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUnionAllOrderByLimitOffset() throws Exception
    {
        Criteria criteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        Criteria otherCriteria = new Criteria()
                .where(BookPeer.AUTHOR_ID,
                        authorList.get(0).getAuthorId())
                .addSelectColumn(BookPeer.BOOK_ID);
        criteria.unionAll(otherCriteria)
        .addAscendingOrderByColumn(new ColumnImpl("1"))
        .setOffset(2)
        .setLimit(4);

        List<Integer> result
        = AuthorPeer.doSelect(criteria, new IntegerMapper());

        assertEquals(4, result.size());

        // check unique contents of list
        List<Integer> expected = new ArrayList<>();
        expected.add(authorList.get(0).getBooks().get(1).getBookId());
        expected.add(authorList.get(0).getBooks().get(1).getBookId());
        expected.add(authorList.get(0).getBooks().get(2).getBookId());
        expected.add(authorList.get(0).getBooks().get(2).getBookId());
        assertEquals(expected, result);
    }
}
