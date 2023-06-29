package org.apache.torque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.MysqlAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.HostCallback;
import org.apache.torque.om.mapper.StringMapper;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.apache.torque.util.BasePeerImpl;
import org.apache.torque.util.CountHelper;


/**
 * Base functionality for Test cases which use standard database setup.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id: BaseDatabaseTestCase.java 1873257 2020-01-28 15:47:06Z gk $
 */
@HostCallback
public abstract class BaseDatabaseTestCase  
{
    /** The system property containing the path to the configuration file. */
    public static final String CONFIG_FILE_SYSTEM_PROPERTY
    = "torque.configuration.file";

    /**
     * Queries mysql for its version.
     * @return the version String mysql returns
     * @throws TorqueException if the database is not mysql or the query fails.
     */
    protected String getMysqlVersion() throws TorqueException
    {
        Adapter adapter
        = Torque.getDatabase(Torque.getDefaultDB()).getAdapter();
        if (!(adapter instanceof MysqlAdapter))
        {
            throw new TorqueException(
                    "getMysqlVersion called but database adapter is "
                            + adapter.getClass().getName());
        }
        List<String> records = new BasePeerImpl<String>().doSelect(
                "show variables like \"version\"",
                new StringMapper(1),
                (String) null);
        return records.get(0);
    }

    /**
     * Queries mysql for its major version. (format is major.minor.release)
     * @return the major version of mysql
     * @throws TorqueException if the database is not mysql or the query fails.
     * @throws NumberFormatException if the mysql major version cannot be
     *         converted to an int
     */
    protected int getMysqlMajorVersion()
            throws TorqueException
    {
        String completeVersion = getMysqlVersion();
        String majorVersion
        = completeVersion.substring(0, completeVersion.indexOf('.'));
        return Integer.parseInt(majorVersion);
    }

    /**
     * Queries mysql for its minor version. (format is major.minor.release)
     * @return the minor version of mysql
     * @throws TorqueException if the database is not mysql or the query fails.
     */
    protected int getMysqlMinorVersion()
            throws TorqueException
    {
        String completeVersion = getMysqlVersion();
        if (completeVersion.indexOf('-') != -1) // handle x.y.z-ubuntuSomething
        {
            completeVersion = completeVersion.substring(
                    0,
                    completeVersion.indexOf('-'));
        }
        String minorVersion
        = completeVersion.substring(
                completeVersion.indexOf('.') + 1,
                completeVersion.lastIndexOf('.'));
        return Integer.parseInt(minorVersion);
    }

    /**
     * Queries the database for its major version.
     *
     * @param connection a connection to the database.
     *
     * @return the version String from the connection metadata
     *
     * @throws TorqueException if the query fails.
     */
    protected int getDatabaseMajorVersion(final Connection connection)
            throws TorqueException
    {
        try
        {
            return connection.getMetaData().getDatabaseMajorVersion();
        }
        catch (SQLException e)
        {
            throw new TorqueException(e);
        }
    }

    /**
     * Deletes all authors and books in the bookstore tables.
     *
     * @throws TorqueException if the bookstore could not be cleaned
     */
    protected void cleanBookstore() throws TorqueException
    {
        Criteria criteria = new Criteria();
        BookPeer.doDelete(criteria);

        criteria = new Criteria();
        AuthorPeer.doDelete(criteria);
    }

    /**
     * Inserts test data into the bookstore tables.
     *
     * @return the list of added authors, which in turn contain the added books.
     *
     * @throws TorqueException if filling data fails.
     */
    protected List<Author> insertBookstoreData() throws TorqueException
    {
        List<Author> result = new ArrayList<>();
        for (int i = 1; i <= 10; i++)
        {
            Author author = new Author();
            author.setName("Author " + i);
            author.save();
            result.add(author);
            assertTrue(author.getAuthorId() != 0, 
            		"authorId should not be 0 after insert");

            for (int j = 1; j <= 10; j++)
            {
                Book book = new Book();
                author.addBook(book);
                book.setTitle("Book " + j + " - Author " + i);
                if (j < 10)
                {
                    book.setIsbn("ISBN " + j + " - " + i);
                }
                else
                {
                    book.setIsbn(null);
                }
                book.save();
            }
        }
        return result;
    }

    /**
     * Checks that the bookstore tables contain exactly the records
     * in the passed list.
     * The books in the authors are also checked.
     *
     * @param toVerify the list of authors to check.
     *
     * @throws TorqueException if reading data fails.
     */
    protected void verifyBookstore(final List<Author> toVerify) throws TorqueException
    {
        int numBooks = 0;

        for (Author author : toVerify)
        {
            Criteria criteria = new Criteria()
                    .where(AuthorPeer.NAME, author.getName())
                    .and(AuthorPeer.AUTHOR_ID, author.getAuthorId());
            criteria.setSingleRecord(true);
            List<Author> selectedAuthorList = AuthorPeer.doSelect(criteria);
            assertEquals(1,selectedAuthorList.size(),
            		"Could not find author with id "
                            + author.getAuthorId()
                            + " and name "
                            + author.getName()
                            );

            numBooks += author.getBooks().size();

            for (Book book : author.getBooks())
            {
                criteria = new Criteria()
                        .where(BookPeer.TITLE, book.getTitle())
                        .and(BookPeer.BOOK_ID, book.getBookId())
                        .and(BookPeer.AUTHOR_ID, book.getAuthorId())
                        .and(BookPeer.ISBN, book.getIsbn());
                criteria.setSingleRecord(true);
                List<Book> selectedBookList = BookPeer.doSelect(criteria);
                assertEquals(1,
                        selectedBookList.size(),
                        "Could not find book with id "
                                + book.getBookId()
                                + " title "
                                + book.getTitle()
                                + " ISBN "
                                + book.getIsbn()
                                + " authorId "
                                + book.getAuthorId());
            }
        }

        assertEquals(
               new CountHelper().count(AuthorPeer.getTableMap()),
               toVerify.size());
        assertEquals(
                new CountHelper().count(BookPeer.getTableMap()),
                numBooks);
    }

}
