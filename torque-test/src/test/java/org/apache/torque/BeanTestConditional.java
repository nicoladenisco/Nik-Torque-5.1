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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.bean.AuthorBean;
import org.apache.torque.test.bean.BookBean;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Test generated bean classes.
 *
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: BeanTestConditional.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class BeanTestConditional extends BaseDatabaseTestCase
{
    public static final String AUTHOR_1_NAME = "Joshua Bloch";

    public static final int AUTHOR_1_ID = 123;

    public static final String BOOK_1_TITLE = "Effective Java";

    public static final String BOOK_1_ISBN = "0-618-12902-2";

    public static final int BOOK_1_ID = 456;

    public static final String AUTHOR_2_NAME = "W. Stevens";

    /**
     * tests the creation of beans from objects and vice versa
     */
    @Test
    public void testCreateBeans() throws Exception
    {
        Author author = new Author();
        author.setName(AUTHOR_1_NAME);
        author.setAuthorId(AUTHOR_1_ID);

        AuthorBean authorBean = author.getBean();
        assertTrue("bean.getName() is " + authorBean.getName()
                + " should be " + author.getName(),
                author.getName().equals(authorBean.getName()));
        assertTrue("bean.getId() is " + authorBean.getAuthorId()
                + " should be " + AUTHOR_1_ID,
                author.getAuthorId() == authorBean.getAuthorId());

        Author authorFromBean = Author.createAuthor(authorBean);
        assertTrue("author from bean has name " + authorFromBean.getName()
                + " should be " + author.getName(),
                author.getName().equals(authorFromBean.getName()));
        assertTrue("author from bean has Id " + authorFromBean.getAuthorId()
                + " should be " + author.getAuthorId(),
                author.getAuthorId() == authorBean.getAuthorId());
    }

    /**
     * tests whether it is possible to serialize/deserialize beans
     * @throws Exception
     */
    @Test
    public void testSerializeBeans() throws Exception
    {
        Author author = new Author();
        author.setName(AUTHOR_1_NAME);
        author.setAuthorId(AUTHOR_1_ID);

        AuthorBean authorBean = author.getBean();

        // serialize the AuthorBean
        byte[] serializedAuthorBean;
        {
            ObjectOutputStream objectOutputStream = null;
            ByteArrayOutputStream byteArrayOutputStream;
            try
            {
                byteArrayOutputStream
                        = new ByteArrayOutputStream();
                objectOutputStream
                        = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(authorBean);
                serializedAuthorBean
                        = byteArrayOutputStream.toByteArray();
            }
            finally
            {
                if (objectOutputStream != null)
                {
                    objectOutputStream.close();
                }
            }
        }
        // deserialize the AuthorBean again
        AuthorBean deserializedAuthorBean;
        {
            ObjectInputStream objectInputStream = null;
            ByteArrayInputStream byteArrayInputStream = null;
            try
            {
                byteArrayInputStream
                        = new ByteArrayInputStream(serializedAuthorBean);
                objectInputStream
                        = new ObjectInputStream(byteArrayInputStream);
                deserializedAuthorBean
                        = (AuthorBean) objectInputStream.readObject();
            }
            finally
            {
                if (byteArrayInputStream != null)
                {
                    byteArrayInputStream.close();
                }
            }
        }
        assertEquals("The Name of the deserialized AuthorBean, "
                + " should equal the Name of AuthorBean",
                deserializedAuthorBean.getName(), authorBean.getName());
        assertEquals("The Id of the deserialized AuthorBean, "
                + " should equal the Id of AuthorBean",
                deserializedAuthorBean.getAuthorId(), authorBean.getAuthorId());
    }

    /**
     * tests whether object relations are transferred correctly,
     * if two objects refer to each other
     */
    @Test
    public void testSameObjectRelations() throws Exception
    {
        Author author = new Author();
        author.setAuthorId(AUTHOR_1_ID);

        Book book = new Book();
        book.setBookId(BOOK_1_ID);

        author.addBook(book);
        book.setAuthor(author);

        // check one roundtrip from author
        assertTrue("author from book should be the same object as author",
                author == book.getAuthor());

        AuthorBean authorBean = author.getBean();
        BookBean bookBean = authorBean.getBookBeans().get(0);
        assertTrue("authorBean from BookBean should be the same "
                + "object as authorBean",
                bookBean.getAuthorBean() == authorBean);

        author = Author.createAuthor(authorBean);
        book = author.getBooks().get(0);

        assertTrue("author from book should be the same object as author "
                + "after creating from bean",
                author == book.getAuthor());

        // check one roundtrip from book
        assertTrue("book from author should be the same object as book",
                book == author.getBooks().get(0));

        bookBean = book.getBean();
        authorBean = bookBean.getAuthorBean();
        assertTrue("bookBean from authorBean should be the same "
                + "object as bookBean",
                authorBean.getBookBeans().get(0) == bookBean);

        book = Book.createBook(bookBean);
        author = book.getAuthor();

        assertTrue("book from author should be the same object as book "
                + "after creating from bean",
                author.getBooks().get(0) == book);
    }

    /**
     * tests whether object relations are transferred correctly,
     * if there is no mutual reference between objects
     * @throws Exception
     */
    @Test
    public void testDifferentObjectRelations() throws Exception
    {
        // create a relation chain:
        //
        //      getBooks()  getAuthor()          getBooks()
        //         |            |                    |
        // author ----> book -----> differentAuthor ---> differentBook
        Author author = new Author();
        author.setAuthorId(AUTHOR_1_ID);

        Book book = new Book();
        book.setBookId(BOOK_1_ID);

        Author differentAuthor = new Author();
        author.setAuthorId(AUTHOR_1_ID);

        author.addBook(book);
        book.setAuthor(differentAuthor);

        Book differentBook = new Book();
        book.setBookId(BOOK_1_ID);

        differentAuthor.addBook(differentBook);

        // check one roundtrip from author
        assertTrue("author from book should not be the same object as author",
                author != book.getAuthor());

        AuthorBean authorBean = author.getBean();
        BookBean bookBean = authorBean.getBookBeans().get(0);
        assertTrue("authorBean from BookBean should not be the same "
                + "object as authorBean",
                bookBean.getAuthorBean() != authorBean);

        author = Author.createAuthor(authorBean);
        book = author.getBooks().get(0);

        assertTrue("author from book should not be the same object as author "
                + "after creating from bean",
                author != book.getAuthor());

        // check one roundtrip from book
        assertTrue("book from differentAuthor should not be "
                + "the same object as book",
                book != differentAuthor.getBooks().get(0));

        bookBean = book.getBean();
        AuthorBean differentAuthorBean = bookBean.getAuthorBean();
        assertTrue("bookBean from differentAuthorBean should not be the same "
                + "object as bookBean",
                differentAuthorBean.getBookBeans().get(0) != bookBean);

        book = Book.createBook(bookBean);
        differentAuthor = book.getAuthor();

        assertTrue("book from differentAuthor should not be "
                + "the same object as book "
                + "after creating from bean",
                differentAuthor.getBooks().get(0) != book);
    }

    @Test
    public void testSaves() throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.where(BookPeer.BOOK_ID, (Long) null, Criteria.NOT_EQUAL);
        BookPeer.doDelete(criteria);

        criteria = new Criteria();
        criteria.where(AuthorPeer.AUTHOR_ID, (Long) null, Criteria.NOT_EQUAL);
        AuthorPeer.doDelete(criteria);

        Author author = new Author();
        author.setName(AUTHOR_1_NAME);
        author.save();

        assertFalse("isModified() should return false after save",
                author.isModified());
        assertFalse("isNew() should return false after save",
                author.isNew());

        AuthorBean authorBean = author.getBean();

        assertFalse("bean.isModified() should return false after save "
                + "and bean creation",
                authorBean.isModified());
        assertFalse("bean.isNew() should return false after save "
                + "and bean creation",
                authorBean.isNew());

        author = Author.createAuthor(authorBean);

        assertFalse("isModified() should return false after save "
                + "and bean roundtrip",
                author.isModified());
        assertFalse("isNew() should return false after save "
                + "and bean rounddtrip",
                author.isNew());

        authorBean.setName(AUTHOR_2_NAME);
        assertTrue("bean.isModified() should return true after it was modified",
                authorBean.isModified());
        assertFalse("bean.isNew() should still return false "
                + "after bean creation and modification",
                authorBean.isNew());

        author = Author.createAuthor(authorBean);
        assertTrue("isModified() should return true after creation of object "
                + "from modified bean",
                author.isModified());

        author.save();

        List<Author> authorList = AuthorPeer.doSelect(new Criteria());
        Author readAuthor = authorList.get(0);
        assertEquals("name from read Author is " + readAuthor.getName()
                +" but should be " + authorBean.getName(),
                readAuthor.getName(),
                authorBean.getName());

        BookBean bookBean = new BookBean();
        bookBean.setTitle(BOOK_1_TITLE);
        bookBean.setIsbn(BOOK_1_ISBN);

        Book book = Book.createBook(bookBean);
        assertTrue("isModified() should return true after creation of object "
                + " from new bean",
                book.isModified());
        assertTrue("isNew() should return true after creation of object "
                + " from new bean",
                book.isNew());
        book.setAuthor(author);
        book.save();

        List<Book> bookList = BookPeer.doSelect(new Criteria());
        assertTrue("Ther should be one book in DB but there are "
                + bookList.size(),
                bookList.size() == 1);
    }
}
