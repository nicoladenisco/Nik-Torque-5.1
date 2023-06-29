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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ColumnImpl;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.util.functions.Count;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests subselects in the where clause.
 *
 * @version $Id: WhereClauseSubselectTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class WhereClauseSubselectTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(WhereClauseSubselectTest.class);

    Author author1;
    Author author2;
    Author author2b;
    Author author3;
    Book book1;
    Book book3;

    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        author1 = new Author();
        author1.setName("author1");
        author1.save();
        author2 = new Author();
        author2.setName("author2");
        author2.save();
        author2b = new Author();
        author2b.setName("author2");
        author2b.save();
        author3 = new Author();
        author3.setName("author3");
        author3.save();
        book1 = new Book();
        book1.setTitle("Book from author 1");
        book1.setAuthor(author1);
        book1.save();
        book3 = new Book();
        book3.setTitle("Book from author 3");
        book3.setAuthor(author3);
        book3.save();
    }

    /**
     * Tests whether we can execute subselects using an in clause with
     * integer values.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSubselectUsingInWithInteger(Adapter adapter) throws Exception
    {
        if (!supportsSubselects(adapter))
        {
            return;
        }

        Criteria subquery = new Criteria();
        subquery.addSelectColumn(AuthorPeer.AUTHOR_ID);
        List<String> authorIds = new ArrayList<>();
        authorIds.add(author1.getName());
        authorIds.add(author2.getName());
        subquery.where(AuthorPeer.NAME, authorIds, Criteria.IN);
        Criteria criteria = new Criteria();
        criteria.where(AuthorPeer.AUTHOR_ID, subquery, Criteria.IN);
        criteria.addDescendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<?> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 2 but got " + result.size(),
                result.size(),
                3);
        Author author = (Author) result.get(0);
        assertEquals("Expected author with Id "
                + author2b.getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                author2b.getAuthorId(),
                author.getAuthorId());
        author = (Author) result.get(1);
        assertEquals("Expected author with Id "
                + author2.getAuthorId()
                + " at second position but got "
                + author.getAuthorId(),
                author2.getAuthorId(),
                author.getAuthorId());
        author = (Author) result.get(2);
        assertEquals("Expected author with Id "
                + author1.getAuthorId()
                + " at second position but got "
                + author.getAuthorId(),
                author1.getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests whether we can execute subselects using an equals comparison with
     * integer values, with the subselects as left hand side of the comparison.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSubselectAsLvalue(Adapter adapter) throws Exception
    {
        if (!supportsSubselects(adapter))
        {
            return;
        }

        Criteria subquery = new Criteria();
        subquery.addSelectColumn(AuthorPeer.AUTHOR_ID);
        subquery.where(author1.getName(), AuthorPeer.NAME);
        Criteria criteria = new Criteria();
        criteria.where(subquery, AuthorPeer.AUTHOR_ID);
        criteria.addDescendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<?> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 1 but got " + result.size(),
                1,
                result.size());
        Author author = (Author) result.get(0);
        assertEquals("Expected author with Id "
                + author1.getAuthorId()
                + " at first position but got "
                + author.getAuthorId(),
                author1.getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests whether we can execute subselects using an equals clause.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSubselectUsingEquals(Adapter adapter) throws Exception
    {
        if (!supportsSubselects(adapter))
        {
            return;
        }

        Criteria subquery = new Criteria();
        subquery.addSelectColumn(AuthorPeer.AUTHOR_ID);
        subquery.where(AuthorPeer.NAME, author1.getName());
        Criteria criteria = new Criteria();
        criteria.where(AuthorPeer.AUTHOR_ID, subquery);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 1 but got " + result.size(),
                result.size(),
                1);
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + author1.getAuthorId()
                + " but got "
                + author.getAuthorId(),
                author1.getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests whether we can execute subqueries using in with Strings.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSubselectUsingInWithStrings(Adapter adapter) throws Exception
    {
        if (!supportsSubselects(adapter))
        {
            return;
        }

        Criteria subquery = new Criteria();
        subquery.addSelectColumn(AuthorPeer.AUTHOR_ID);
        List<String> nameList = new ArrayList<>();
        nameList.add(author1.getName());
        nameList.add(author2.getName());
        subquery.where(AuthorPeer.NAME, nameList, Criteria.IN);
        Criteria criteria = new Criteria();
        criteria.where(AuthorPeer.AUTHOR_ID, subquery, Criteria.IN);
        criteria.addAscendingOrderByColumn(AuthorPeer.AUTHOR_ID);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 2 but got " + result.size(),
                result.size(),
                3);
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + author1.getAuthorId()
                + " but got "
                + author.getAuthorId(),
                author1.getAuthorId(),
                author.getAuthorId());
        author = result.get(1);
        assertEquals("Expected author with Id "
                + author2.getAuthorId()
                + " but got "
                + author.getAuthorId(),
                author2.getAuthorId(),
                author.getAuthorId());
        author = result.get(2);
        assertEquals("Expected author with Id "
                + author2b.getAuthorId()
                + " but got "
                + author.getAuthorId(),
                author2b.getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests whether we can execute subselects which reference the outer select
     * in the subselect.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSubselectReferencingOuterSelectWithManuallyAddedFromClause(Adapter adapter) throws Exception
    {
        if (!supportsSubselects(adapter))
        {
            return;
        }

        Criteria subquery = new Criteria();
        subquery.addSelectColumn(new ColumnImpl("count(*)"));
        subquery.where(BookPeer.AUTHOR_ID, AuthorPeer.AUTHOR_ID);
        subquery.and(BookPeer.TITLE, book3.getTitle());
        subquery.addFrom(BookPeer.TABLE_NAME);
        Criteria criteria = new Criteria();
        criteria.where(subquery, 1);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 1 but got " + result.size(),
                1,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + author3.getAuthorId()
                + " but got "
                + author.getAuthorId(),
                author3.getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Tests whether we can execute subselects which reference the outer select
     * in the subselect.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testSubselectReferencingOuterSelect(Adapter adapter) throws Exception
    {
        if (!supportsSubselects(adapter))
        {
            return;
        }

        Criteria subquery = new Criteria();
        subquery.where(BookPeer.AUTHOR_ID, AuthorPeer.AUTHOR_ID);
        subquery.and(BookPeer.TITLE, book3.getTitle());
        subquery.addSelectColumn(new Count("*"));

        Criteria criteria = new Criteria();
        criteria.where(subquery, 1);

        List<Author> result = AuthorPeer.doSelect(criteria);
        assertEquals("Expected result of size 1 but got " + result.size(),
                1,
                result.size());
        Author author = result.get(0);
        assertEquals("Expected author with Id "
                + author3.getAuthorId()
                + " but got "
                + author.getAuthorId(),
                author3.getAuthorId(),
                author.getAuthorId());
    }

    /**
     * Returns whether the database supports subselects.
     * If not a warning is written to the logs.
     *
     * @return true if the database supports subselects, false otherwise.
     *
     * @throws TorqueException If an error occurs.
     */
    
    private boolean supportsSubselects(Adapter adapter) throws TorqueException
    {
        if (!(adapter instanceof MysqlAdapter))
        {
            return true;
        }
        int majorVersion = getMysqlMajorVersion();
        int minorVersion = getMysqlMinorVersion();
        if (majorVersion < 4 || (majorVersion == 4 && minorVersion == 0))
        {
            log.warn("supportsSubselects(): "
                    + "Subselects are not supported by Mysql < 4.1");
            return false;
        }
        return true;
    }
}
