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

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.DerbyAdapter;
import org.apache.torque.adapter.HsqldbAdapter;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.criteria.Criterion;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.om.mapper.CompositeMapper;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.om.mapper.RecordMapper;
import org.apache.torque.test.InheritanceClassnameTestChild1;
import org.apache.torque.test.InheritanceClassnameTestChild2;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.BigintType;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.dbobject.CompPkContainsFk;
import org.apache.torque.test.dbobject.IfcTable;
import org.apache.torque.test.dbobject.InheritanceChildB;
import org.apache.torque.test.dbobject.InheritanceChildC;
import org.apache.torque.test.dbobject.InheritanceChildD;
import org.apache.torque.test.dbobject.InheritanceClassnameTest;
import org.apache.torque.test.dbobject.InheritanceTest;
import org.apache.torque.test.dbobject.IntegerType;
import org.apache.torque.test.dbobject.LocalIfcTable;
import org.apache.torque.test.dbobject.LocalTestInterface;
import org.apache.torque.test.dbobject.MultiPk;
import org.apache.torque.test.dbobject.Nopk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.dbobject.VarcharType;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BigintTypePeer;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.test.peer.CompPkContainsFkPeer;
import org.apache.torque.test.peer.IfcTablePeer;
import org.apache.torque.test.peer.IfcTablePeerImpl;
import org.apache.torque.test.peer.InheritanceClassnameTestPeer;
import org.apache.torque.test.peer.InheritanceTestPeer;
import org.apache.torque.test.peer.IntegerTypePeer;
import org.apache.torque.test.peer.LocalIfcTablePeer;
import org.apache.torque.test.peer.LocalIfcTablePeerImpl;
import org.apache.torque.test.peer.LocalTestPeerInterface;
import org.apache.torque.test.peer.MultiPkPeer;
import org.apache.torque.test.peer.NopkPeer;
import org.apache.torque.test.peer.VarcharTypePeer;
import org.apache.torque.test.recordmapper.AuthorRecordMapper;
import org.apache.torque.test.recordmapper.BookRecordMapper;
import org.apache.torque.util.BasePeerImpl;
import org.apache.torque.util.CountHelper;
import org.apache.torque.util.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.opentest4j.AssertionFailedError;

/**
 * Runtime tests.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @author <a href="mailto:patrick.carl@web.de">Patrick Carl</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: DataTest.java 1871755 2019-12-18 15:31:27Z gk $
 */
public class DataTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(DataTest.class);

    /**
     * test whether we can connect to the database at all
     * @throws Exception if no connection can be established
     */
    @Test
    public void testConnect() throws Exception
    {
        Connection connection = null;
        try
        {
            connection = Torque.getConnection();
            connection.close();
            connection = null;
        }
        finally
        {
            if (connection != null)
            {
                connection.close();
            }
        }
    }

    /**
     * multiple pk test (TRQ12)
     * @throws Exception if the test fails
     */
    @Test
    public void testMultiplePk() throws Exception
    {
        // clean table
        Criteria criteria = new Criteria();
        criteria.where(MultiPkPeer.PK1, (Object) null, Criteria.NOT_EQUAL);
        MultiPkPeer.doDelete(criteria);

        // do test
        MultiPk mpk = new MultiPk();
        mpk.setPrimaryKey("Svarchar:N5:Schar:N3:N-42:N3:N4:N5:N6:D9999999999:");
        mpk.save();
        // TODO assert saved values
    }

    private static final String[] validTitles = {
            "Book 6 - Author 4", "Book 6 - Author 5", "Book 6 - Author 6",
            "Book 6 - Author 7", "Book 6 - Author 8",
            "Book 7 - Author 4", "Book 7 - Author 5", "Book 7 - Author 6",
            "Book 7 - Author 7", "Book 7 - Author 8"
    };

    /**
     * test limit/offset
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testLimitOffset(Adapter adapter) throws Exception
    {
        cleanBookstore();
        insertBookstoreData();
        Set<String> titleSet = new HashSet<>();
        for (int j = 0; j < validTitles.length; j++)
        {
            titleSet.add(validTitles[j]);
        }

        Criteria crit = new Criteria();
        Criterion c = new Criterion(BookPeer.TITLE,
                "Book 6 - Author 1", Criteria.GREATER_EQUAL);
        c.and(new Criterion(BookPeer.TITLE,
                "Book 8 - Author 3", Criteria.LESS_EQUAL));
        crit.where(c);
        crit.addDescendingOrderByColumn(BookPeer.BOOK_ID);
        crit.setLimit(10);
        crit.setOffset(5);
        List<Book> books = BookPeer.doSelect(crit);
        assertEquals(10, books.size(), "List should have 10 books");
        for (Book book : books)
        {
            String title = book.getTitle();
            assertTrue( titleSet.contains(title), "Incorrect title: " + title);
        }


        // Test limit of zero works
        if (adapter instanceof DerbyAdapter || adapter instanceof HsqldbAdapter)
        {
            log.info("testLimitOffset(): "
                    + "A limit of 0 is not supported for Derby or Hsqldb");
        }
        else
        {
            crit = new Criteria();
            crit.setLimit(0);
            books = BookPeer.doSelect(crit);
            assertEquals(0, books.size(), "List should have 0 books");
        }

        // check that Offset also works without limit
        crit = new Criteria();
        crit.setOffset(5);
        books = BookPeer.doSelect(crit);
        assertEquals(95, books.size(), "List should have 95 books");

        // Check that limiting also works if a table with an equal column name
        // is joined. This is problematic for oracle, see TORQUE-10.

        crit = new Criteria();
        crit.setLimit(10);
        crit.setOffset(5);
        books = BookPeer.doSelectJoinAuthor(crit);
        assertEquals(10, books.size(), "List should have 10 books");
    }

    /**
     * Checks whether the setSingleRecord() method in criteria works
     */
    @Test
    public void testSingleRecord() throws Exception
    {
        cleanBookstore();
        insertBookstoreData();
        Criteria criteria = new Criteria();
        criteria.setSingleRecord(true);
        criteria.setLimit(1);
        criteria.setOffset(5);
        List<Book> books = BookPeer.doSelect(criteria);
        assertTrue(books.size() == 1, 
                "List should have 1 books, not " + books.size());

        criteria = new Criteria();
        criteria.setSingleRecord(true);
        criteria.setLimit(2);
        try
        {
            books = BookPeer.doSelect(criteria);
            fail("doSelect should have failed "
                    + "because two records were selected "
                    + " and one was expected");
        }
        catch (TorqueException e)
        {
        }
    }

    /**
     * Tests whether selects work correctly if the value <code>null</code>
     * is used.
     * @throws Exception if the test fails
     */
    @Test
    public void testNullSelects() throws Exception
    {
        // clean table
        VarcharTypePeer.doDelete(new Criteria());
        IntegerTypePeer.doDelete(new Criteria());

        // add test data
        VarcharType varcharType = new VarcharType();
        varcharType.setId("text2");
        varcharType.setVarcharValue("text2");
        varcharType.save();
        varcharType = new VarcharType();
        varcharType.setId("text");
        varcharType.save();

        IntegerType integerTypeNotNull = new IntegerType();
        integerTypeNotNull.setIntegerObjectValue(1);
        integerTypeNotNull.save();
        IntegerType integerTypeNull = new IntegerType();
        integerTypeNull.save();

        // check for comparison NOT_EQUAL and value null
        Criteria criteria = new Criteria();
        criteria.where(VarcharTypePeer.ID, null, Criteria.NOT_EQUAL)
        .and(VarcharTypePeer.VARCHAR_VALUE, null, Criteria.NOT_EQUAL);
        List<VarcharType> varcharResult = VarcharTypePeer.doSelect(criteria);
        assertEquals(1, varcharResult.size());
        assertEquals("text2", varcharResult.get(0).getId());

        criteria = new Criteria();
        criteria.where(IntegerTypePeer.ID, null, Criteria.NOT_EQUAL)
        .and(IntegerTypePeer.INTEGER_OBJECT_VALUE, null, Criteria.NOT_EQUAL);
        List<IntegerType> integerResult = IntegerTypePeer.doSelect(criteria);
        assertEquals(1, integerResult.size());
        assertEquals(integerTypeNotNull.getId(), integerResult.get(0).getId());

        // check for comparison EQUAL and value null
        criteria = new Criteria();
        criteria.where(VarcharTypePeer.VARCHAR_VALUE, null, Criteria.EQUAL);
        varcharResult = VarcharTypePeer.doSelect(criteria);
        assertEquals(1, varcharResult.size());
        assertEquals("text", varcharResult.get(0).getId());

        criteria = new Criteria();
        criteria.where(IntegerTypePeer.INTEGER_OBJECT_VALUE, null, Criteria.EQUAL);
        integerResult = IntegerTypePeer.doSelect(criteria);
        assertEquals(1, integerResult.size());
        assertEquals(integerTypeNull.getId(), integerResult.get(0).getId());
    }

    /**
     * Test whether an update works and whether it only affects the
     * specified record.
     * @throws Exception if anything in the test goes wrong.
     */
    @Test
    public void testUpdate() throws Exception
    {
        cleanBookstore();

        Author otherAuthor = new Author();
        otherAuthor.setName("OtherName");
        otherAuthor.save();

        Author author = new Author();
        author.setName("Name");
        author.save();


        // Test doUpdate methods in Peer explicitly
        Connection connection = Transaction.begin(AuthorPeer.DATABASE_NAME);
        author.setName("NewName2");
        AuthorPeer.doUpdate(author);
        Transaction.commit(connection);

        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(AuthorPeer.NAME);

        List<Author> authors = AuthorPeer.doSelect(criteria);
        assertEquals(2, authors.size(), "List should contain 2 authors");
        assertEquals(
                "NewName2",
                authors.get(0).getName(),
                "First Author's name should be \"NewName2\"");
        assertEquals(
                "OtherName",
                authors.get(1).getName(),
                "Second Author's name should be \"OtherName\"");

        author.setName("NewName3");
        AuthorPeer.doUpdate(author);

        criteria = new Criteria();
        criteria.addAscendingOrderByColumn(AuthorPeer.NAME);

        authors = AuthorPeer.doSelect(criteria);
        assertEquals(2, authors.size(), "List should contain 2 authors");
        assertEquals("NewName3",
                authors.get(0).getName(),
                "First Author's name should be \"NewName3\"");
        assertEquals(
                "OtherName",
                authors.get(1).getName(),
                "Second Author's name should be \"OtherName\"");

        Nopk nopk = new Nopk();
        nopk.setName("name");
        nopk.save();

        // check the doPupdate Peer methods throw exceptions on a modified
        // object without primary keys
        try
        {
            NopkPeer.doUpdate(new Nopk());
            fail("A Torque exception should be thrown (2)");
        }
        catch (TorqueException e)
        {
        }

        connection = Transaction.begin(NopkPeer.DATABASE_NAME);
        try
        {
            NopkPeer.doUpdate(new Nopk(),connection);
            fail("A Torque exception should be thrown (3)");
        }
        catch (TorqueException e)
        {
        }
        Transaction.safeRollback(connection);

    }

    /**
     * test special cases in the select clause
     * @throws Exception if the test fails
     */
    @Test
    public void testSelectClause() throws Exception
    {
        // test double functions in select columns
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(
                new ColumnImpl("count(distinct(" + BookPeer.BOOK_ID + "))"));
        new BasePeerImpl<>().doSelect(criteria, new IntegerMapper());

        // test qualifiers in function in select columns
        criteria = new Criteria();
        criteria.addSelectColumn(
                new ColumnImpl("count(distinct " + BookPeer.BOOK_ID + ")"));
        new BasePeerImpl<>().doSelect(criteria, new IntegerMapper());
    }

    /**
     * test if a select from the "default" database works
     * @throws Exception (NPE) if the test fails
     */
    @Test
    public void testSelectFromDefault() throws Exception
    {
        Criteria criteria = new Criteria("default");

        criteria.addSelectColumn(BookPeer.BOOK_ID);

        new BasePeerImpl<>().doSelect(criteria, new IntegerMapper());
    }

    /**
     * Test the behaviour if a connection is supplied to access the database,
     * but it is null. All methods on the user level should be fail
     * because these methods will be only needed if a method should be executed
     * in a transaction context. If one assumes that a transaction is open
     * (connection is not null), but it is not (connection == null),
     * it is a bad idea to silently start one as this behaviour is very
     * difficult to tell from the correct one. A clean failure is much easier
     * to test for.
     */
    @Test
    public void testNullConnection() throws Exception
    {
        try
        {
            Criteria criteria = new Criteria();
            AuthorPeer.doSelect(criteria, new IntegerMapper(), null);
            fail("NullPointerException expected");
        }
        catch (NullPointerException e)
        {
            //expected
        }

        try
        {
            Criteria criteria = new Criteria();
            criteria.where(BookPeer.BOOK_ID, (Long) null, Criteria.NOT_EQUAL);
            BookPeer.doDelete(criteria, (Connection) null);
            fail("NullPointerException expected");
        }
        catch (NullPointerException e)
        {
            //expected
        }

        try
        {
            Author author = new Author();
            author.setName("name");
            author.save((Connection) null);
            fail("TorqueException expected");
        }
        catch (TorqueException e)
        {
            //expected
            assertEquals("connection is null", e.getMessage());
        }
    }

    /**
     * test the order by, especially in joins and with aliases
     * @throws Exception if the test fails
     */
    @Test
    public void testOrderBy() throws Exception
    {
        cleanBookstore();

        // insert test data
        Author firstAuthor = new Author();
        firstAuthor.setName("Author 1");
        firstAuthor.save();
        Book book = new Book();
        book.setAuthor(firstAuthor);
        book.setTitle("Book 1");
        book.setIsbn("unknown");
        book.save();

        Author secondAuthor = new Author();
        secondAuthor.setName("Author 2");
        secondAuthor.save();
        for (int bookNr = 2; bookNr <=4; bookNr++)
        {
            book = new Book();
            book.setAuthor(secondAuthor);
            book.setTitle("Book " + bookNr);
            book.setIsbn("unknown");
            book.save();
        }

        // test simple ascending order by
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(BookPeer.TITLE);
        List<Book> bookList = BookPeer.doSelect(criteria);
        if (bookList.size() != 4)
        {
            fail("Ascending Order By: "
                    + "incorrect numbers of books found : "
                    + bookList.size()
                    + ", should be 4");
        }
        if (! "Book 1".equals(bookList.get(0).getTitle()))
        {
            fail("Ascending Order By: "
                    + "Title of first Book is "
                    + bookList.get(0).getTitle()
                    + ", should be \"Book 1\"");
        }
        if (! "Book 4".equals(bookList.get(3).getTitle()))
        {
            fail("Ascending Order By: "
                    + "Title of fourth Book is "
                    + bookList.get(3).getTitle()
                    + ", should be \"Book 4\"");
        }

        // test simple descending order by
        criteria = new Criteria();
        criteria.addDescendingOrderByColumn(BookPeer.TITLE);
        bookList = BookPeer.doSelect(criteria);
        if (bookList.size() != 4)
        {
            fail("Descending Order By: "
                    + "incorrect numbers of books found : "
                    + bookList.size()
                    + ", should be 4");
        }
        if (! "Book 1".equals(bookList.get(3).getTitle()))
        {
            fail("Descending Order By: "
                    + "Title of fourth Book is "
                    + bookList.get(3).getTitle()
                    + ", should be \"Book 1\"");
        }
        if (! "Book 4".equals((bookList.get(0)).getTitle()))
        {
            fail("Descending Order By: "
                    + "Title of first Book is "
                    + bookList.get(0).getTitle()
                    + ", should be \"Book 4\"");
        }

        criteria = new Criteria();
        criteria.addAlias("b", BookPeer.TABLE_NAME);
        criteria.addJoin(BookPeer.AUTHOR_ID, AuthorPeer.AUTHOR_ID);
        criteria.addJoin(
                AuthorPeer.AUTHOR_ID,
                new ColumnImpl("b." + BookPeer.AUTHOR_ID.getColumnName()));
        criteria.addAscendingOrderByColumn(
                new ColumnImpl("b." + BookPeer.TITLE.getColumnName()));
        criteria.addDescendingOrderByColumn(BookPeer.TITLE);
        // the retrieved columns are
        // author    book   b
        // author1  book1   book1
        // author2  book4   book2
        // author2  book3   book2
        // author2  book2   book2
        // author2  book4   book3
        // ...
        bookList = BookPeer.doSelect(criteria);
        if (bookList.size() != 10)
        {
            fail("ordering by Aliases: "
                    + "incorrect numbers of books found : "
                    + bookList.size()
                    + ", should be 10");
        }
        if (!"Book 4".equals(bookList.get(1).getTitle()))
        {
            fail("ordering by Aliases: "
                    + "Title of second Book is "
                    + bookList.get(1).getTitle()
                    + ", should be \"Book 4\"");
        }
        if (!"Book 3".equals(bookList.get(2).getTitle()))
        {
            fail("ordering by Aliases: "
                    + "Title of third Book is "
                    + bookList.get(2).getTitle()
                    + ", should be \"Book 3\"");
        }

        criteria = new Criteria();
        criteria.addAlias("b", BookPeer.TABLE_NAME);
        criteria.addJoin(BookPeer.AUTHOR_ID, AuthorPeer.AUTHOR_ID);
        criteria.addJoin(
                AuthorPeer.AUTHOR_ID,
                new ColumnImpl("b." + BookPeer.AUTHOR_ID.getColumnName()));
        criteria.addAscendingOrderByColumn(BookPeer.TITLE);
        criteria.addDescendingOrderByColumn(
                new ColumnImpl("b." + BookPeer.TITLE.getColumnName()));
        // the retrieved columns are
        // author    book   b
        // author1  book1   book1
        // author2  book2   book4
        // author2  book2   book3
        // author2  book2   book2
        // author2  book3   book4
        // ...
        bookList = BookPeer.doSelect(criteria);
        if (bookList.size() != 10)
        {
            fail("ordering by Aliases (2): "
                    + "incorrect numbers of books found : "
                    + bookList.size()
                    + ", should be 10");
        }
        if (!"Book 2".equals(bookList.get(1).getTitle()))
        {
            fail("ordering by Aliases (2, PS): "
                    + "Title of second Book is "
                    + bookList.get(1).getTitle()
                    + ", should be \"Book 2\"");
        }
        if (!"Book 2".equals(bookList.get(2).getTitle()))
        {
            fail("ordering by Aliases (2, PS): "
                    + "Title of third Book is "
                    + bookList.get(2).getTitle()
                    + ", should be \"Book 2\"");
        }

        // test usage of Expressions in order by
        criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                new ColumnImpl("UPPER(" + BookPeer.TITLE + ")"));
        criteria.setIgnoreCase(true);
        BookPeer.doSelect(criteria);
    }


    /**
     * Tests whether ignoreCase works correctly
     * @throws Exception if the test fails
     */
    @Test
    public void testIgnoreCase() throws Exception
    {
        cleanBookstore();

        // check ignore case in selects
        Author author = new Author();
        author.setName("AuTHor");
        author.save();

        Criteria criteria = new Criteria();
        criteria.where(AuthorPeer.NAME, author.getName().toLowerCase());
        criteria.setIgnoreCase(true);
        List<Author> result = AuthorPeer.doSelect(criteria);
        assertTrue( result.size() == 1,
                "Size of result is not 1, but " + result.size());

        // LIKE treatment might be different (e.g. postgres), so check extra
        criteria = new Criteria();
        criteria.where(
                AuthorPeer.NAME,
                author.getName().toLowerCase().replace('r', '%'),
                Criteria.LIKE);
        criteria.setIgnoreCase(true);
        result = AuthorPeer.doSelect(criteria);
        assertTrue(   result.size() == 1,
                "Size of result is not 1, but " + result.size());

        // Test ignore case in criterion
        criteria = new Criteria();
        Criterion criterion1 = new Criterion(
                AuthorPeer.NAME,
                author.getName().toLowerCase(),
                Criteria.EQUAL);
        criterion1.setIgnoreCase(true);
        Criterion criterion2 = new Criterion(
                AuthorPeer.AUTHOR_ID, null, Criteria.NOT_EQUAL);
        criterion1.and(criterion2);

        result = AuthorPeer.doSelect(criteria);

        // ignore case should not be set either in Criteria
        // nor in other criterions
        assertFalse(criteria.isIgnoreCase());
        assertFalse(criterion2.isIgnoreCase());
        assertTrue( result.size() == 1,
                "Size of result is not 1, but " + result.size());


        // Test ignore case in attached criterion
        criteria = new Criteria();
        criterion1 = new Criterion(
                AuthorPeer.AUTHOR_ID, null, Criteria.NOT_EQUAL);
        criterion2 = new Criterion(
                AuthorPeer.NAME,
                author.getName().toLowerCase(),
                Criteria.EQUAL);
        criterion2.setIgnoreCase(true);
        criterion1.and(criterion2);

        result = AuthorPeer.doSelect(criteria);

        // ignore case should not be set either in Criteria
        // nor in other criterions
        assertFalse(criteria.isIgnoreCase());
        assertFalse(criterion1.isIgnoreCase());

        assertTrue( result.size() == 1,
                "Size of result is not 1, but " + result.size());

        // ignore case in "in" query
        {
            criteria = new Criteria();
            Set<String> names = new HashSet<>();
            names.add(author.getName().toLowerCase());
            criteria.where(AuthorPeer.NAME, names, Criteria.IN);
            criteria.setIgnoreCase(true);

            result = AuthorPeer.doSelect(criteria);
            assertEquals(  1,
                    result.size(),
                    "Expected result of size 1 but got " + result.size());
        }

        // Check that case is not ignored if ignoreCase is not set
        // This is known not to work for mysql
        author = new Author();
        author.setName("author");
        author.save();

        Adapter adapter = Torque.getAdapter(Torque.getDefaultDB());
        if (adapter instanceof MysqlAdapter
                || adapter instanceof MssqlAdapter)
        {
            log.error("testIgnoreCase(): "
                    + "Case sensitive comparisons are known not to work"
                    + " with Mysql and MSSQL");
            // failing is "expected", so bypass without error
        }
        else
        {
            criteria = new Criteria();
            criteria.where(AuthorPeer.NAME, author.getName());
            result = AuthorPeer.doSelect(criteria);
            assertTrue( result.size() == 1,
                    "Size of result is not 1, but " + result.size());

            // again check LIKE treatment
            criteria = new Criteria();
            criteria.where(
                    AuthorPeer.NAME,
                    author.getName().replace('r', '%'),
                    Criteria.LIKE);
            result = AuthorPeer.doSelect(criteria);
            assertTrue( result.size() == 1,
                    "Size of result is not 1, but " + result.size());

            // Test different ignore cases in criterions
            criteria = new Criteria();
            criterion1 = new Criterion(
                    AuthorPeer.NAME,
                    author.getName().toLowerCase(),
                    Criteria.NOT_EQUAL);
            criterion2 = new Criterion(
                    AuthorPeer.NAME,
                    author.getName().toLowerCase(),
                    Criteria.EQUAL);
            criterion2.setIgnoreCase(true);
            criterion1.and(criterion2);
            criteria.where(criterion1);

            result = AuthorPeer.doSelect(criteria);
            assertTrue( result.size() == 1,
                    "Size of result is not 1, but " + result.size());

            // ignore case in "in" query
            {
                criteria = new Criteria();
                Set<String> names = new HashSet<>();
                names.add(author.getName());
                criteria.where(AuthorPeer.NAME, names, Criteria.IN);

                result = AuthorPeer.doSelect(criteria);
                assertEquals( 1,
                        result.size(),
                        "Expected result of size 1 but got " + result.size());
            }
        }

        cleanBookstore();
        author = new Author();
        author.setName("AA");
        author.save();
        author = new Author();
        author.setName("BB");
        author.save();
        author = new Author();
        author.setName("ba");
        author.save();
        author = new Author();
        author.setName("ab");
        author.save();

        // check ignoreCase in Criteria
        criteria = new Criteria();
        criteria.setIgnoreCase(true);
        criteria.addAscendingOrderByColumn(AuthorPeer.NAME);
        result = AuthorPeer.doSelect(criteria);
        assertTrue(  result.size() == 4,
                "Size of result is not 4, but " + result.size());
        assertEquals("AA", result.get(0).getName());
        assertEquals("ab", result.get(1).getName());
        assertEquals("ba", result.get(2).getName());
        assertEquals("BB", result.get(3).getName());

        // check ignoreCase in orderBy
        criteria = new Criteria();
        criteria.addAscendingOrderByColumn(AuthorPeer.NAME, true);
        result = AuthorPeer.doSelect(criteria);
        assertTrue( result.size() == 4,
                "Size of result is not 4, but " + result.size());
        assertEquals(result.get(0).getName(), "AA");
        assertEquals(result.get(1).getName(), "ab");
        assertEquals(result.get(2).getName(), "ba");
        assertEquals(result.get(3).getName(), "BB");
    }

    /**
     * tests whether AsColumns produce valid SQL code
     * @throws Exception if the test fails
     */
    @Test
    public void testAsColumn() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAsColumn("ALIASNAME", AuthorPeer.NAME);
        // we need an additional column to select from,
        // to indicate the table we want use
        criteria.addSelectColumn(AuthorPeer.AUTHOR_ID);
        new BasePeerImpl<>().doSelect(criteria, new DoNothingMapper());
    }

    /**
     * Test whether same column name in different tables
     * are handled correctly
     * @throws Exception if the test fails
     */
    @Test
    public void testSameColumnName() throws Exception
    {
        cleanBookstore();
        Author author = new Author();
        author.setName("Name");
        author.save();

        author = new Author();
        author.setName("NotCorrespondingName");
        author.save();

        Book book = new Book();
        book.setTitle("Name");
        book.setAuthor(author);
        book.setIsbn("unknown");
        book.save();

        Criteria criteria = new Criteria();
        criteria.addJoin(BookPeer.TITLE, AuthorPeer.NAME);
        BookPeer.addSelectColumns(criteria);
        AuthorPeer.addSelectColumns(criteria);
        // basically a BaseBookPeer.setDbName(criteria);
        // and BasePeer.doSelect(criteria);
        CompositeMapper mapper = new CompositeMapper();
        mapper.addMapper(new BookRecordMapper(), 0);
        mapper.addMapper(
                new AuthorRecordMapper(),
                BookPeer.numColumns);

        List<List<Object>> queryResult
        = BookPeer.doSelect(criteria, mapper);
        List<Object> mappedRow = queryResult.get(0);
        book = (Book) mappedRow.get(0);
        author = (Author) mappedRow.get(1);

        if (book.getAuthorId() == author.getAuthorId())
        {
            fail("wrong Ids read");
        }
    }

    /**
     * tests whether large primary keys are inserted and read correctly
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testLargePk(Adapter adapter) throws Exception
    {
        if (adapter instanceof MssqlAdapter) {
            log.error("testLargePk(): "
                    + "MSSQL does not support inserting defined PK values");
            return;
        }
        BigintTypePeer.doDelete(new Criteria());

        long longId = 8771507845873286l;
        BigintType bigintType = new BigintType();
        bigintType.setId(longId);
        bigintType.save();

        List<BigintType> bigintTypeList = BigintTypePeer.doSelect(new Criteria());
        BigintType readBigintType = bigintTypeList.get(0);
        assertEquals(bigintType.getId(), readBigintType.getId());
        assertEquals(longId, readBigintType.getId());
    }

    /**
     * tests whether large bigint values are inserted and read correctly
     * @throws Exception if the test fails
     */
    @Test
    public void testLargeValue() throws Exception
    {
        BigintTypePeer.doDelete(new Criteria());

        long longValue = 8771507845873286l;
        BigintType bigintType = new BigintType();
        bigintType.setBigintValue(longValue);
        bigintType.save();

        List<BigintType> bigintTypeList = BigintTypePeer.doSelect(new Criteria());
        BigintType readBigintType = bigintTypeList.get(0);
        assertEquals(bigintType.getId(), readBigintType.getId());
        assertEquals(longValue, readBigintType.getBigintValue());
    }

    /**
     * Tests the CountHelper class
     * @throws Exception if the test fails
     */
    @Test
    public void testCountHelper() throws Exception
    {
        cleanBookstore();
        Author author = new Author();
        author.setName("Name");
        author.save();

        author = new Author();
        author.setName("Name2");
        author.save();

        author = new Author();
        author.setName("Name");
        author.save();

        Criteria criteria = new Criteria();
        int count = new CountHelper().count(
                criteria,
                null,
                AuthorPeer.AUTHOR_ID);

        if (count != 3) {
            fail("counted " + count + " datasets, should be 3 ");
        }

        criteria = new Criteria();
        criteria.setDistinct();
        count = new CountHelper().count(criteria, null, AuthorPeer.NAME);

        if (count != 2) {
            fail("counted " + count + " distinct datasets, should be 2 ");
        }

        criteria = new Criteria();
        criteria.where(AuthorPeer.NAME, "Name2");
        count = new CountHelper().count(criteria);

        if (count != 1) {
            fail("counted " + count + " datasets with name Name2,"
                    + " should be 1 ");
        }
    }


    /**
     * Tests whether we can handle multiple primary keys some of which are
     * also foreign keys
     * @throws Exception if the test fails
     */
    @Test
    public void testMultiplePrimaryForeignKey() throws Exception
    {
        ForeignKeySchemaData.clearTablesInDatabase();

        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.save();
        CompPkContainsFk compPkContainsFk = new CompPkContainsFk();
        compPkContainsFk.setId1(oIntegerPk.getId());
        compPkContainsFk.setId2("test");
        compPkContainsFk.save();

        List<CompPkContainsFk> selectedList
        = CompPkContainsFkPeer.doSelect(new Criteria());
        assertEquals(1, selectedList.size());
        CompPkContainsFk selected = selectedList.get(0);
        assertEquals(oIntegerPk.getId(), selected.getId1());
        assertEquals("test", selected.getId2());
    }

    /**
     * Tests inserting single quotes in Strings.
     * This may not crash now, but in a later task like datasql,
     * so the data has to be inserted in a table which does not get cleaned
     * during the runtime test.
     * @throws Exception if inserting the test data fails
     */
    @Test
    public void testSingleQuotes() throws Exception
    {
        cleanBookstore();

        Author author = new Author();
        author.setName("has Single ' Quote");
        author.save();
    }

    /**
     * Test whether equals() is working correctly
     * @throws Exception
     */
    @Test
    public void testEquals() throws Exception
    {
        Author author = new Author();
        author.setAuthorId(1000);

        Book book = new Book();
        book.setBookId(1000);

        Book bookNotEqual = new Book();
        bookNotEqual.setBookId(2000);

        Book bookEqual = new Book();
        bookEqual.setBookId(1000);

        assertFalse(author.equals(book),
                "Author and Book should not be equal"
                );
        assertTrue( book.equals(book),
                "Book compared with itself should be equal");
        assertTrue(book.equals(bookEqual),
                "Book compared with book with same id should be equal");
        assertFalse(book.equals(bookNotEqual),
                "Book compared with book with different id "
                + "should not be equal"
                );
    }

    /**
     * Tests whether a table implementing an interface actually
     * returns an instance of this interface
     * @throws Exception if the test fails
     */
    @Test
    public void testInterface() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.where(IfcTablePeer.ID, -1, Criteria.NOT_EQUAL);
        IfcTablePeer.doDelete(criteria);

        IfcTable ifc = new IfcTable();

        assertTrue( ifc instanceof TestInterface,
                "IfcTable should be an instance of TestInterface");

        ifc.setID(1);
        ifc.setName("John Doe");
        ifc.save();

        List<IfcTable> results = IfcTablePeer.doSelect(new Criteria());

        for (IfcTable ifcTable : results)
        {
            assertTrue(
                    ifcTable instanceof TestInterface,
                    "IfcTablePeer.doSelect should return"
                            + " instances of TestInterface");
        }

        IfcTablePeerImpl peerImpl = IfcTablePeer.getIfcTablePeerImpl();
        assertTrue(
                peerImpl instanceof TestPeerInterface,
                "IfcTablePeerImpl should be an instance of "
                        + "TestPeerInterface");

        LocalIfcTable localIfc = new LocalIfcTable();

        assertTrue( localIfc instanceof LocalTestInterface,
                "LocalIfcTable should be an instance of LocalTestInterface");

        List<LocalIfcTable> results2 = LocalIfcTablePeer.doSelect(new Criteria());

        for (LocalIfcTable readLocalIfcTable : results2)
        {
            assertTrue(
                    readLocalIfcTable instanceof LocalTestInterface,
                    "IfcTable2Peer.doSelect should return"
                            + " instances of LocalTestInterface");
        }

        LocalIfcTablePeerImpl localPeerImpl = LocalIfcTablePeer.getLocalIfcTablePeerImpl();
        assertTrue(
                localPeerImpl instanceof LocalTestPeerInterface,
                "LocalIfcTablePeerImpl should be an instance of "
                        + "LocalTestPeerInterface");
    }

    @Test
    public void testInheritanceWithKeys() throws Exception
    {
        // make sure that the InheritanceTest table is empty before the test
        Criteria criteria = new Criteria();
        criteria.where(
                InheritanceTestPeer.INHERITANCE_TEST,
                (Object) null,
                Criteria.ISNOTNULL);
        InheritanceTestPeer.doDelete(criteria);
        criteria = new Criteria();
        criteria.where(
                InheritanceTestPeer.INHERITANCE_TEST,
                (Object) null,
                Criteria.ISNOTNULL);
        assertEquals(0,
                new CountHelper().count(criteria));

        // create & save test data
        InheritanceTest inheritanceTest = new InheritanceTest();
        inheritanceTest.setPayload("payload1");
        inheritanceTest.save();
        InheritanceChildB inheritanceChildB = new InheritanceChildB();
        inheritanceChildB.setPayload("payload 2");
        inheritanceChildB.save();
        InheritanceChildC inheritanceChildC = new InheritanceChildC();
        inheritanceChildC.setPayload("payload 3");
        inheritanceChildC.save();
        InheritanceChildD inheritanceChildD = new InheritanceChildD();
        inheritanceChildD.setPayload("payload 4");
        inheritanceChildD.save();

        // Check that all objects are saved into the InheritanceTest table
        criteria = new Criteria();
        criteria.where(
                InheritanceTestPeer.INHERITANCE_TEST,
                null,
                Criteria.ISNOTNULL);
        assertEquals( 4,
                new CountHelper().count(criteria),
                "InheritanceTestTable should contain 4 rows");
        criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                InheritanceTestPeer.INHERITANCE_TEST);

        // Check that the class of the object is retained when loading
        List<InheritanceTest> inheritanceObjects
        = InheritanceTestPeer.doSelect(criteria);
        assertEquals(
                InheritanceTest.class,
                inheritanceObjects.get(0).getClass());
        assertEquals(
                InheritanceChildB.class,
                inheritanceObjects.get(1).getClass());
        assertEquals(
                InheritanceChildC.class,
                inheritanceObjects.get(2).getClass());
        assertEquals(
                InheritanceChildD.class,
                inheritanceObjects.get(3).getClass());
    }

    @Test
    public void testInheritanceWithClassname() throws Exception
    {
        // make sure that the InheritanceTest table is empty before the test
        Criteria criteria = new Criteria();
        InheritanceClassnameTestPeer.doDelete(criteria);
        criteria = new Criteria();
        criteria.where(
                InheritanceClassnameTestPeer.INHERITANCE_TEST,
                null,
                Criteria.ISNOTNULL);
        assertEquals(0,
                new CountHelper().count(criteria));

        // create & save test data
        InheritanceClassnameTest inheritanceClassnameTest
            = new InheritanceClassnameTest();
        inheritanceClassnameTest.setPayload("0 parent");
        inheritanceClassnameTest.save();
        InheritanceClassnameTestChild1 inheritanceClassnameChild1
            = new InheritanceClassnameTestChild1();
        inheritanceClassnameChild1.setPayload("1 child");
        inheritanceClassnameChild1.save();
        InheritanceClassnameTestChild2 inheritanceClassnameChild2
            = new InheritanceClassnameTestChild2();
        inheritanceClassnameChild2.setPayload("2 child");
        inheritanceClassnameChild2.save();

        // Check that all objects are saved into the InheritanceTest table
        criteria = new Criteria();
        criteria.where(
                InheritanceClassnameTestPeer.INHERITANCE_TEST,
                null,
                Criteria.ISNOTNULL);
        assertEquals(
                3,
                new CountHelper().count(criteria),
                "InheritanceClassnameTest table should contain 3 rows");
        criteria = new Criteria();
        criteria.addAscendingOrderByColumn(
                InheritanceClassnameTestPeer.PAYLOAD);

        // Check that the class of the object is retained when loading
        List<InheritanceClassnameTest> inheritanceObjects
        = InheritanceClassnameTestPeer.doSelect(criteria);
        assertEquals(
                InheritanceClassnameTest.class,
                inheritanceObjects.get(0).getClass());
        assertEquals("0 parent", inheritanceObjects.get(0).getPayload());
        assertEquals(
                InheritanceClassnameTestChild1.class,
                inheritanceObjects.get(1).getClass());
        assertEquals("1 child", inheritanceObjects.get(1).getPayload());
        assertEquals(
                InheritanceClassnameTestChild2.class,
                inheritanceObjects.get(2).getClass());
        assertEquals("2 child", inheritanceObjects.get(2).getPayload());
    }

    /**
     * Checks whether selects with unqualified column names work.
     *
     * @throws Exception if a problem occurs.
     */
    @Test
    public void testUnqualifiedColumnNames() throws Exception
    {
        cleanBookstore();
        Author author = new Author();
        author.setName("Joshua Bloch");
        author.save();

        Criteria criteria = new Criteria();
        criteria.where(AuthorPeer.AUTHOR_ID, (Object) null, Criteria.NOT_EQUAL);
        criteria.and(new ColumnImpl("name"), "Joshua Bloch", Criteria.EQUAL);
        List<Author> authors = AuthorPeer.doSelect(criteria);
        assertEquals(1, authors.size());
    }

    
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testLikeClauseEscaping(Adapter adapter) throws Exception
    {
        String[] authorNames
        = {"abc", "bbc", "a_c", "a%c", "a\\c",
                "a\"c", "a'c", "a?c", "a*c" };

        Map<String, String> likeResults = new LinkedHashMap<>();

        likeResults.put("a\\_c", "a_c");
        likeResults.put("a\\_%", "a_c");
        likeResults.put("%\\_c", "a_c");

        likeResults.put("a\\%c", "a%c");
        likeResults.put("a\\%%", "a%c");
        likeResults.put("%\\%c", "a%c"); // escaped second %

        likeResults.put("a\\\\c", "a\\c"); // escaped \ three times
        likeResults.put("a\\\\%", "a\\c");
        
        // mysql: like '%\\c' ESCAPE '|' succeeds, but
        // %\\\\c fails, see https://dev.mysql.com/doc/refman/8.0/en/string-comparison-functions.html, 
        // MySQL uses C escape syntax in strings..  you must double any \ that you use in LIKE strings.
        // That is platform specific mysql:
        
        // succeeded only in mysql 5.x:
        if (adapter instanceof MysqlAdapter && 
                ( getMysqlMajorVersion()  < 8 || getMysqlVersion().matches("10\\.[12]\\..*\\-MariaDB") )) {  
            //    likeResults.put("%\\\\\\\\c", "a\\c");
            log.warn("If using MySQL < 8 this test may fail, skipping special escape, "
                    + "see https://dev.mysql.com/doc/refman/8.0/en/string-comparison-functions.html and issue TORQUE-353");
        } else {
            likeResults.put("%\\\\c", "a\\c");     
        }
        
        likeResults.put("a\\*c", "a*c");
        likeResults.put("a\\*%", "a*c");
        //likeResults.put("%\\*c", "a*c"); // mysql: %\\*c fails, only underscore (_) is wild card
        // this matches multiple users
        //likeResults.put("%*c", "a%c"); 
        likeResults.put("_\\*c", "a*c"); 

        likeResults.put("a\\?c", "a?c");
        likeResults.put("a\\?%", "a?c");
        likeResults.put("%\\?c", "a?c");

        likeResults.put("a\"c", "a\"c");
        likeResults.put("a\"%", "a\"c");
        likeResults.put("%\"c", "a\"c");

        likeResults.put("a'c", "a'c");
        likeResults.put("a'%", "a'c");
        likeResults.put("%'c", "a'c");
        cleanBookstore();

        // Save authors
        for (int i = 0; i < authorNames.length; ++i)
        {
            Author author = new Author();
            author.setName(authorNames[i]);
            author.save();
        }

        // Check authors are in the database
        for (int i = 0; i < authorNames.length; ++i)
        {
            Criteria criteria = new Criteria();
            criteria.where(AuthorPeer.NAME, authorNames[i]);
            List<Author> authorList = AuthorPeer.doSelect(criteria);
            assertEquals( 1,
                          authorList.size(),
                          "AuthorList should contain one author"
                                    + " when querying for " + authorNames[i]);
            Author author = authorList.get(0);
            assertEquals(
                    authorNames[i],
                    author.getName(),
                    "Name of author should be " + authorNames[i]);
        }

        boolean bulkOk = true;
        StringBuffer errors = new StringBuffer();
        for (Map.Entry<String, String> likeResult : likeResults.entrySet())
        {
            // System.out.println("Key: " + likeResult.getKey() + " - Value: " + likeResult.getValue());
            Criteria criteria = new Criteria();
            criteria.where(
                    AuthorPeer.NAME,
                    likeResult.getKey(),
                    Criteria.LIKE);
            List<Author> authorList;
            try
            {
                authorList = AuthorPeer.doSelect(criteria);
            }
            catch (Exception e)
            {
                throw new Exception(
                        "error executing select using like content "
                                + likeResult.getKey(),
                                e);
            }
            try {
                assertEquals( 1,
                              authorList.size(),
                              "AuthorList contained " + authorList.size() + ", but should contain one author"
                                        + " when querying for " + likeResult.getKey());
                Author author = authorList.get(0);
                assertEquals(
                        likeResult.getValue(),
                        author.getName(),
                        "Name of author should be "
                                + likeResult.getValue()
                                + " when querying for "
                                + likeResult.getKey());
            } catch (AssertionFailedError e) {
                log.error("assert failed, but proceeding", e);
                bulkOk = false;
                errors.append(e.getMessage()).append("expected:")
                .append(e.getExpected().getValue()).append('|').append("actual:")
                .append(e.getActual().getValue()).append("\r\n");
            }
        }
        if (!bulkOk) {
            fail(errors.toString());
        }

        // check that case insensitivity is maintained if
        // a like is replaced with an equals (no wildcard present)
        // This might be a problem for databases which use ILIKE
        Criteria criteria = new Criteria();
        criteria.where(AuthorPeer.NAME, "AbC", Criteria.LIKE);
        criteria.setIgnoreCase(true);
        List<Author> authorList = AuthorPeer.doSelect(criteria);
        assertEquals(
                1,
                authorList.size(),
                "AuthorList should contain one author");
        Author author = authorList.get(0);
        assertEquals(
                "abc",
                author.getName(),
                "Name of author should be abc");

        // check that the escape clause (where needed) also works
        // with limit, offset and order by
        criteria = new Criteria();
        Criterion criterion1 = new Criterion(
                AuthorPeer.NAME,
                "b%",
                Criteria.LIKE);
        Criterion criterion2 = new Criterion(
                AuthorPeer.NAME,
                "a\\%%",
                Criteria.LIKE);
        Criterion criterion3 = new Criterion(
                AuthorPeer.NAME,
                "cbc",
                Criteria.LIKE);
        criteria.where(criterion1.or(criterion2).or(criterion3));
        criteria.addAscendingOrderByColumn(AuthorPeer.NAME);
        criteria.setOffset(1);
        criteria.setLimit(1);
        authorList = AuthorPeer.doSelect(criteria);
        assertEquals(1,
                authorList.size(),
                "AuthorList should contain one author");
        author = authorList.get(0);
        assertEquals( "bbc",
                author.getName(),
                "Name of author should be bbc");
    }


    /**
     * Strips the schema and table name from a fully qualified colum name
     * This is useful for creating Query with aliases, as the constants
     * for the colum names in the data objects are fully qualified.
     * @param fullyQualifiedColumnName the fully qualified column name, not null
     * @return the column name stripped from the table (and schema) prefixes
     */
    
    public static String getRawColumnName(final String fullyQualifiedColumnName)
    {
        int dotPosition = fullyQualifiedColumnName.lastIndexOf(".");
        if (dotPosition == -1)
        {
            return fullyQualifiedColumnName;
        }
        String result = fullyQualifiedColumnName.substring(
                dotPosition + 1,
                fullyQualifiedColumnName.length());
        return result;
    }

    static class DoNothingMapper implements RecordMapper<Object>
    {

        /** Serial version */
        private static final long serialVersionUID = 1L;

        @Override
        public Object processRow(
                final ResultSet resultSet,
                final int rowOffset,
                final Criteria criteria)
                        throws TorqueException
        {
            return null;
        }
    }
}
