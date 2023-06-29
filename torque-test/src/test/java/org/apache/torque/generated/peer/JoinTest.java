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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ColumnImpl;
import org.apache.torque.ForeignKeySchemaData;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.HsqldbAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.dbobject.MultiRef;
import org.apache.torque.test.dbobject.NullableOIntegerFk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.dbobject.PIntegerPk;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.test.peer.MultiRefPeer;
import org.apache.torque.test.peer.OIntegerPkPeer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests joins.
 *
 * @version $Id: JoinTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class JoinTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(JoinTest.class);

    /**
     * Test left joins.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testLeftJoins() throws Exception
    {
        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        criteria.addJoin(
                AuthorPeer.AUTHOR_ID,
                BookPeer.AUTHOR_ID,
                Criteria.LEFT_JOIN);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 5 authors:
        // the author with one book, the author without books,
        // and three times the author with three books
        if (authorList.size() != 5)
        {
            fail("author left join book : "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 5");
        }
    }

    /**
     * Test inner joins.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInnerJoins() throws Exception
    {
        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        criteria.addJoin(
                AuthorPeer.AUTHOR_ID,
                BookPeer.AUTHOR_ID,
                Criteria.INNER_JOIN);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 4 authors:
        // the author with one book,
        // and three times the author with three books
        if (authorList.size() != 4)
        {
            fail("author left join book : "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 4");
        }
    }

    /**
     * Test an implicit inner join.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testImplicitInnerJoins() throws Exception
    {
        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        criteria.addJoin(
                AuthorPeer.AUTHOR_ID,
                BookPeer.AUTHOR_ID);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 4 authors:
        // the author with one book,
        // and three times the author with three books
        if (authorList.size() != 4)
        {
            fail("author left join book : "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 4");
        }
    }

    /**
     * Test right joins.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testRightJoins(Adapter adapter) throws Exception
    {
        if (!supportsRightJoins(adapter))
        {
            return;
        }

        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        criteria.addJoin(
                BookPeer.AUTHOR_ID,
                AuthorPeer.AUTHOR_ID,
                Criteria.RIGHT_JOIN);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 5 authors:
        // the author with one book, the author without books,
        // and three times the author with three books
        if (authorList.size() != 5)
        {
            fail("book right join author "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 5");
        }
    }

    /**
     * Test a join with an operator which is not equal.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testInnerJoinsOtherComparator() throws Exception
    {
        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        criteria.addJoin(
                BookPeer.TABLE_NAME,
                AuthorPeer.TABLE_NAME,
                new Criterion(
                        BookPeer.AUTHOR_ID,
                        AuthorPeer.AUTHOR_ID,
                        Criteria.NOT_EQUAL),
                null);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 8 authors:
        // three times the author with one book,
        // four times the author without book,
        // and one time the author with three books
        if (authorList.size() != 8)
        {
            fail("book join author on not equals"
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 8");
        }
    }

    /**
     * Test double join with aliases.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testDoubleJoinWithAliases(Adapter adapter) throws Exception
    {
        if (!supportsRightJoins(adapter))
        {
            return;
        }

        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        criteria.addAlias("b", BookPeer.TABLE_NAME);
        criteria.addJoin(
                BookPeer.AUTHOR_ID, AuthorPeer.AUTHOR_ID,
                Criteria.RIGHT_JOIN);
        criteria.addJoin(
                AuthorPeer.AUTHOR_ID,
                new ColumnImpl("b." + BookPeer.AUTHOR_ID.getColumnName()),
                Criteria.LEFT_JOIN);
        List<Author> authorList = AuthorPeer.doSelect(criteria);
        // Here we get 11 authors:
        // the author with one book, the author without books,
        // and nine times the author with three books
        if (authorList.size() != 11)
        {
            fail("book right join author left join book b: "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 11");
        }
    }

    /**
     * Test a reversed join.
     * Reversed means that torque needs to change a right join to a left
     * join and change tables to create a valid sql statement.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testReverseJoin(Adapter adapter) throws Exception
    {
        if (!supportsRightJoins(adapter))
        {
            return;
        }

        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        criteria.addAlias("b", BookPeer.TABLE_NAME);
        criteria.addJoin(BookPeer.AUTHOR_ID, AuthorPeer.AUTHOR_ID,
                Criteria.RIGHT_JOIN);
        criteria.addJoin(
                new ColumnImpl("b." + BookPeer.AUTHOR_ID.getColumnName()),
                AuthorPeer.AUTHOR_ID,
                Criteria.RIGHT_JOIN);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 11 authors:
        // the author with one book, the author without books,
        // and nine times the author with three books
        if (authorList.size() != 11)
        {
            fail("book right join author left join book b (reversed): "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 11");
        }
    }

    /**
     * Test an implicit inner join with a subselect.
     * Reversed means that torque needs to change a right join to a left
     * join and change tables to create a valid sql statement.
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testImplicitInnerJoinWithSubselect(Adapter adapter) throws Exception
    {
        if (!supportsRightJoins(adapter))
        {
            return;
        }

        cleanBookstore();
        insertTestData();

        Criteria subselect = new Criteria();
        BookPeer.addSelectColumns(subselect);
        subselect.where(BookPeer.TITLE, "Book 1");

        Criteria criteria = new Criteria();
        criteria.addAlias("b", subselect);
        criteria.addJoin(
                new ColumnImpl("b." + BookPeer.AUTHOR_ID.getColumnName()),
                AuthorPeer.AUTHOR_ID);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get the one author with ine book:
        // the author with one book, the author without books,
        // and nine times the author with three books
        if (authorList.size() != 1)
        {
            fail("join with subselect: "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 1");
        }
        assertEquals("Author with one book", authorList.get(0).getName());
    }

    /**
     * Test an implicit inner join where the join condition consists of two
     * conditions joined with AND.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testImplicitInnerJoinTwoConditions() throws Exception
    {
        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        Criterion joinCondition = new Criterion(AuthorPeer.AUTHOR_ID,
                BookPeer.AUTHOR_ID);
        joinCondition.and(new Criterion(BookPeer.TITLE, "Book 1"));
        criteria.addJoin(
                AuthorPeer.TABLE_NAME,
                BookPeer.TABLE_NAME,
                joinCondition,
                null);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 1 authors:
        // the author with one book,
        if (authorList.size() != 1)
        {
            fail("author join book : "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 1");
        }
    }

    /**
     * Test an implicit inner join where the join condition consists of two
     * conditions joined with AND.
     *
     * @throws Exception if the test fails.
     */
    @Test
    public void testExplicitInnerJoinTwoConditions() throws Exception
    {
        cleanBookstore();
        insertTestData();
        Criteria criteria = new Criteria();
        Criterion joinCondition = new Criterion(AuthorPeer.AUTHOR_ID,
                BookPeer.AUTHOR_ID);
        joinCondition.and(new Criterion(BookPeer.TITLE, "Book 1"));
        criteria.addJoin(
                AuthorPeer.TABLE_NAME,
                BookPeer.TABLE_NAME,
                joinCondition,
                Criteria.INNER_JOIN);

        List<Author> authorList = AuthorPeer.doSelect(criteria);

        // Here we get 1 authors:
        // the author with one book,
        if (authorList.size() != 1)
        {
            fail("author join book : "
                    + "incorrect numbers of authors found : "
                    + authorList.size()
                    + ", should be 1");
        }
    }

    /**
     * Test joins using the XPeer.DoSelectJoinYYY methods
     * @throws Exception if the Test fails
     */
    @Test
    public void testDoSelectJoinY() throws Exception
    {
        cleanBookstore();
        insertTestData();

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(BookPeer.TITLE);

        List<Book> books = BookPeer.doSelectJoinAuthor(criteria);

        assertTrue("books should contain 4 books but contains "
                + books.size(), books.size() == 4);
        Book bookTwo = books.get(1);
        Book bookThree = books.get(2);
        assertTrue ("the authors of BookTwo and BookThree"
                + " should point to the same instance",
                bookTwo.getAuthor() == bookThree.getAuthor());
    }

    /**
     * Test joins using the XPeer.DoSelectJoinAllExceptYYY methods
     * @throws Exception if the Test fails
     */
    @Test
    public void testDoSelectJoinAllExceptY() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();

        // setup test data
        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.setName("testOIntegerPk");
        oIntegerPk.save();

        PIntegerPk pIntegerPk = new PIntegerPk();
        pIntegerPk.setName("testPIntegerPk");
        pIntegerPk.save();

        NullableOIntegerFk nullableOIntegerFk = new NullableOIntegerFk();
        nullableOIntegerFk.setName("testNullableOIntegerFk");
        nullableOIntegerFk.save();

        MultiRef multiRef = new MultiRef();
        multiRef.setOIntegerPk(oIntegerPk);
        multiRef.setPIntegerPk(pIntegerPk);
        multiRef.setNullableOIntegerFk(nullableOIntegerFk);
        multiRef.save();

        Criteria criteria = new Criteria().where(
                OIntegerPkPeer.ID,
                oIntegerPk.getId());
        List<MultiRef> list
        = MyMultiRefPeer.doSelectJoinAllExceptNullableOIntegerFk(
                criteria);
        assertTrue("list should contain 1 entry but contains "
                + list.size(), list.size() == 1);

        MultiRef MultiRefLoaded = list.get(0);
        // check loaded entities. loading already loaded entities
        // with a null connection does not result in an error
        OIntegerPk relatedBy1
        = MultiRefLoaded.getOIntegerPk(null); // already loaded
        assertEquals(oIntegerPk.getName(), relatedBy1.getName());
        PIntegerPk relatedBy2
        = MultiRefLoaded.getPIntegerPk(null); // already loaded
        assertEquals(pIntegerPk.getName(), relatedBy2.getName());
        try
        {
            MultiRefLoaded.getNullableOIntegerFk(null);
            fail("relatedBy3 should not already be loaded but it is");
        }
        catch (NullPointerException e)
        {
            // expected
        }
    }

    /**
     * Subclass of MultiRefPeer to make the doSelectJoinAllExcept..()
     * visible
     */
    static class MyMultiRefPeer extends MultiRefPeer
    {
        public static List<MultiRef> doSelectJoinAllExceptNullableOIntegerFk(
                Criteria criteria)
                        throws TorqueException
        {
            return MultiRefPeer.doSelectJoinAllExceptNullableOIntegerFk(criteria);
        }
    }

    /**
     * Fills test data into the author and book tables.
     * There is one author without books, one author with one book
     * and one author with three books.
     *
     * @throws TorqueException if saving fails.
     */
    protected void insertTestData() throws TorqueException
    {
        // insert test data
        Author author = new Author();
        author.setName("Author with one book");
        author.save();
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle("Book 1");
        book.setIsbn("unknown");
        book.save();

        author = new Author();
        author.setName("Author without book");
        author.save();

        author = new Author();
        author.setName("Author with three books");
        author.save();
        for (int bookNr = 2; bookNr <=4; bookNr++)
        {
            book = new Book();
            book.setAuthor(author);
            book.setTitle("Book " + bookNr);
            book.setIsbn("unknown");
            book.save();
        }

    }

    /**
     * Returns whether the database supports right joins.
     *
     * @return true if the database supports right joins, false otherwise.
     *
     * @throws TorqueException if an error occurs.
     */
    protected boolean supportsRightJoins(Adapter adapter) throws TorqueException
    {
        if (adapter instanceof HsqldbAdapter)
        {
            log.warn("testRightJoins(): "
                    + "Right joins are not supported by HSQLDB");
            return false;
        }
        return true;
    }
}
