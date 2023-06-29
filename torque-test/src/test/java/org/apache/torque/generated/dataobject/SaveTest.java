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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ForeignKeySchemaData;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.MssqlAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.dbobject.Nopk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.NopkPeer;
import org.apache.torque.test.peer.OIntegerPkPeer;
import org.apache.torque.util.CountHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Tests whether the save methods work in the db object classes.
 *
 * @version $Id: SaveTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class SaveTest extends BaseDatabaseTestCase
{
    private static Log log = LogFactory.getLog(SaveTest.class);

    /**
     * Tests the save method for a simple object.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testInsert() throws Exception
    {
        // prepare
        cleanBookstore();
        List<Author> bookstoreContent = insertBookstoreData();
        Author author = new Author();
        author.setName("Author");

        // execute
        author.save();

        // verify
        assertNotNull(author.getAuthorId());
        assertEquals("Author", author.getName());

        bookstoreContent.add(author);
        verifyBookstore(bookstoreContent);
    }

    /**
     * Check that an id can be set manually.
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    public void testInsertWithManualId(Adapter adapter) throws Exception
    {
        if (adapter instanceof MssqlAdapter)
        {
            log.error("manual id insertion is not possible for MSSQL");
            return;
        }
        // prepare
        ForeignKeySchemaData.clearTablesInDatabase();

        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.setId(3001);

        // execute
        oIntegerPk.save();

        // verify
        assertEquals(new Integer(3001), oIntegerPk.getId());

        Criteria criteria = new Criteria().where(OIntegerPkPeer.ID, 3001);
        List<OIntegerPk> integerObjectPkList
        = OIntegerPkPeer.doSelect(criteria);
        assertEquals(1, integerObjectPkList.size());

        assertEquals(
                1,
                new CountHelper().count(OIntegerPkPeer.getTableMap()));
    }

    /**
     * Tests that an insert works for objects without primary key.
     */
    @Test
    public void testInsertWithoutPk() throws TorqueException
    {
        // prepare
        Criteria criteria = new Criteria();
        NopkPeer.doDelete(criteria);

        Nopk nopk = new Nopk();
        nopk.setName("name");
        nopk.save();

        // execute
        nopk.save();

        // verify
        assertEquals("name", nopk.getName());

        criteria = new Criteria().where(NopkPeer.NAME, "name");
        List<Nopk> nopkList = NopkPeer.doSelect(criteria);
        assertEquals(1, nopkList.size());

        assertEquals(1, new CountHelper().count(NopkPeer.getTableMap()));
    }

    /**
     * Tests that save does not throw an exception if save
     * is called on an object without pk which is already saved and
     * nothing is modified.
     */
    @Test
    public void testSaveWithoutPkNoModification() throws TorqueException
    {
        // prepare
        Criteria criteria = new Criteria();
        NopkPeer.doDelete(criteria);

        Nopk nopk = new Nopk();
        nopk.setName("name");
        nopk.save();

        // execute
        nopk.save();

        // verify
        assertEquals("name", nopk.getName());

        criteria = new Criteria().where(NopkPeer.NAME, "name");
        List<Nopk> nopkList = NopkPeer.doSelect(criteria);
        assertEquals(1, nopkList.size());

        assertEquals(1, new CountHelper().count(NopkPeer.getTableMap()));
    }

    /**
     * Tests that save fails if it is called on a modified object without pk
     * which is already saved.
     */
    @Test
    public void testSaveWithoutPkModification() throws TorqueException
    {
        // prepare
        Criteria criteria = new Criteria();
        NopkPeer.doDelete(criteria);

        Nopk nopk = new Nopk();
        nopk.setName("name");
        nopk.save();

        // execute
        try
        {
            nopk.setName("otherName");
            nopk.save();

            //verify
            fail("Exception expected");
        }
        catch (TorqueException e)
        {
            assertEquals(
                    "doUpdate does not work for objects without primary key",
                    e.getMessage());
        }
    }

    /**
     * Tests the save method for a simple object.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testUpdate() throws Exception
    {
        // prepare
        cleanBookstore();
        List<Author> bookstoreContent = insertBookstoreData();
        Author author = new Author();
        author.setName("Author");
        author.save();
        author.setName("nameModified"); // modify after saving

        // execute
        author.save();

        // verify
        assertNotNull(author.getAuthorId());
        assertEquals("nameModified", author.getName());
        bookstoreContent.add(author);
        verifyBookstore(bookstoreContent);
    }

    /**
     * Tests that the save method propagates "down" for a foreign key,
     * i.e. if save is called on an object which contains another
     * referencing object, the referencing object must also be saved.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testInsertPropagationDown() throws Exception
    {
        // prepare
        cleanBookstore();
        List<Author> bookstoreContent = insertBookstoreData();
        Author author = new Author();
        author.setName("Author");
        Book book = new Book();
        author.addBook(book);
        book.setTitle("Book title");
        book.setIsbn("ISBN");

        // execute
        author.save();

        // verify
        assertNotNull(author.getAuthorId());
        assertEquals("Author", author.getName());
        assertNotNull(book.getBookId());
        assertEquals("Book title", book.getTitle());
        assertEquals("ISBN", book.getIsbn());

        bookstoreContent.add(author);
        verifyBookstore(bookstoreContent);
    }

    /**
     * Tests that the save method does not propagate "up" for a foreign key,
     * i.e. if save is called on an object referencing another object,
     * the referenced object must not be saved.
     *
     * @throws Exception if a database error occurs.
     */
    @Test
    public void testNoPropagationUp() throws Exception
    {
        // prepare
        cleanBookstore();
        Author author = new Author();
        author.setName("Author");
        author.save();
        author.setName("nameModified"); // modify after saving

        Book book = new Book();
        author.addBook(book);
        book.setTitle("Book title");
        book.setIsbn("ISBN");

        // execute
        book.save();

        // verify
        // propagation would have been possible, reference is there
        assertNotNull(book.getAuthor());

        // Author in db should still have old name
        Criteria criteria = new Criteria().where(AuthorPeer.NAME, "Author");
        List<Author> authorList = AuthorPeer.doSelect(criteria);
        assertEquals(1, authorList.size());
    }
}
