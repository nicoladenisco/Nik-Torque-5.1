package org.apache.torque.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.ConstraintViolationException;
import org.apache.torque.DeadlockException;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.HsqldbAdapter;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.dbobject.Book;
import org.apache.torque.test.dbobject.SingleNamedUnique;
import org.apache.torque.test.peer.SingleNamedUniquePeer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;


public class ExceptionMapperTest extends BaseDatabaseTestCase
{
    /** Sleep time for thread polling, in miliseconds. */
    private static int SLEEP_TIME = 10;

    /** Timeout for waiting for started threads and saves, in milliseconds. */
    private static int TIMEOUT = 50000;

    private static Log log = LogFactory.getLog(ExceptionMapperTest.class);

    /**
     * Check that a ConstraintViolationException is thrown if a record
     * with an already-existing primary key is inserted.
     */
    @Test
    public void testInsertWithPkViolation() throws TorqueException
    {
        // prepare
        cleanBookstore();
        List<Author> bookstoreContent = insertBookstoreData();
        Author author = new Author();
        author.setName("Author");
        author.setAuthorId(bookstoreContent.get(0).getAuthorId());

        // execute & verify
        try
        {
            author.save();
            fail("Exception expected");
        }
        catch (ConstraintViolationException e)
        {
            assertTrue(e.getCause() instanceof SQLException);
        }
    }

    /**
     * Check that a ConstraintViolationException is thrown if a record
     * with an already-existing unique key is inserted.
     */
    @Test
    public void testUpdateWithUniqueConstraintViolation() throws TorqueException
    {
        // prepare
        SingleNamedUniquePeer.doDelete(new Criteria());
        SingleNamedUnique unique = new SingleNamedUnique();
        unique.setValue(1);
        unique.save();

        SingleNamedUnique duplicateUnique = new SingleNamedUnique();
        duplicateUnique.setValue(1);

        // execute & verify
        try
        {
            duplicateUnique.save();
            fail("Exception expected");
        }
        catch (ConstraintViolationException e)
        {
            assertTrue(e.getCause() instanceof SQLException);
        }
    }

    /**
     * Check that a ConstraintViolationException is thrown if a record
     * is saved which contains null in a non-nullable column
     */
    @Test
    public void testInsertWithNonNullableColumnViolation()
            throws TorqueException
    {
        // prepare
        cleanBookstore();
        Author author = new Author();
        author.setName(null); // is required

        // execute & verify
        try
        {
            author.save();
            fail("Exception expected");
        }
        catch (ConstraintViolationException e)
        {
            assertTrue(e.getCause() instanceof SQLException);
        }
    }

    /**
     * Check that a ConstraintViolationException is thrown if a record
     * is saved which has a foreign key to a non-existing object
     */
    @Test
    public void testInsertWithForeignKeyConstraintViolation()
            throws TorqueException
    {
        // prepare
        cleanBookstore();
        Book book = new Book();
        book.setTitle("title");
        book.setIsbn("isbn");
        book.setAuthorId(1); // does not exist

        // execute & verify
        try
        {
            book.save();
            fail("Exception expected");
        }
        catch (ConstraintViolationException e)
        {
            assertTrue(e.getCause() instanceof SQLException);
        }
    }

    /**
     * Check that a deadlockException is thrown if two transaction want to access
     * a resource locked by the other thread.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testTransactionDeadlock() throws Exception
    {
        Adapter adapter
        = Torque.getDatabase(Torque.getDefaultDB()).getAdapter();
        if (adapter instanceof HsqldbAdapter)
        {
            log.warn("hsqldb (2.2.6- 2.5.0) fails to detect the deadlock in this test, " 
            		+ "v 2.5.1 already has a timeout or Database lock acquisition failure, probably already in insertBookstore"
                    + " therefore this test is skipped");
            return;
        }

        // prepare
        cleanBookstore();
        List<Author> bookstoreContent = insertBookstoreData();
        Connection transaction1 = null;
        Connection transaction2 = null;
        try
        {
            // lock author 1 in transaction 1
            transaction1 = Transaction.begin();
            if (transaction1.getAutoCommit())
            {
                fail("autocommit must be set to false for this test");
            }
            Author author1 = bookstoreContent.get(0);
            author1.setName("new Author1");
            author1.save(transaction1);

            // lock author 2 in transaction 2
            transaction2 = Transaction.begin();
            if (transaction2.getAutoCommit())
            {
                fail("autocommit must be set to false for this test(2)");
            }
            Author author2 = bookstoreContent.get(1);
            author2.setName("new Author2");
            author2.save(transaction2);

            // lock author 2 in transaction 1 (must wait for lock)
            author2.setName("newer Author2");
            SaveAndRollbackThread saveThreadTransaction1
                = new SaveAndRollbackThread(
                    author2,
                    transaction1,
                    "saveThreadAuthor2Con1");
            saveThreadTransaction1.start();

            long startTime = System.currentTimeMillis();
            while (!author2.isSaving() && author2.isModified()
                    && saveThreadTransaction1.isAlive())
            {
                Thread.sleep(SLEEP_TIME);
                if (System.currentTimeMillis() > startTime + TIMEOUT)
                {
                    fail("Waited too long for saving to start");
                }
            }

            // Try to lock author1 in transaction 2 (deadlock)
            author1.setName("newer Author1");
            SaveAndRollbackThread saveThreadTransaction2
                = new SaveAndRollbackThread(
                    author1,
                    transaction2,
                    "saveThreadAuthor1Con2");
            saveThreadTransaction2.start();

            startTime = System.currentTimeMillis();
            while (!author1.isSaving() && author1.isModified()
                    && saveThreadTransaction2.isAlive())
            {
                Thread.sleep(SLEEP_TIME);
                if (System.currentTimeMillis() > startTime + TIMEOUT)
                {
                    fail("Waited too long for saving to start (2)");
                }
            }

            // wait till save on transaction 1 has finished
            startTime = System.currentTimeMillis();
            while (saveThreadTransaction1.isAlive())
            {
                Thread.sleep(10);
                if (System.currentTimeMillis() > startTime + TIMEOUT)
                {
                    fail("Waited too long for saving to finish");
                }
            }

            // wait till save on transaction 2 has finished
            startTime = System.currentTimeMillis();
            while (saveThreadTransaction2.isAlive())
            {
                Thread.sleep(10);
                if (System.currentTimeMillis() > startTime + TIMEOUT)
                {
                    fail("Waited too long for saving to finish (2)");
                }
            }

            // verify. Either in transaction 1 or in transaction 2
            // a deadlock exception must have occurred
            if (saveThreadTransaction1.getCaughtException() != null)
            {
                if (!(saveThreadTransaction1.getCaughtException()
                        instanceof DeadlockException))
                {
                    throw saveThreadTransaction1.getCaughtException();
                }
            }
            else if (saveThreadTransaction2.getCaughtException() == null)
            {
                fail("No Deadlock occured");
                if (!(saveThreadTransaction2.getCaughtException()
                        instanceof DeadlockException))
                {
                    throw saveThreadTransaction2.getCaughtException();
                }
            }
        }
        finally
        {
        	// already called safeRollback in SaveAndRollbackThread
            Transaction.safeRollback(transaction1);
            Transaction.safeRollback(transaction2);
        }
    }

    private static class SaveAndRollbackThread extends Thread
    {
        private final Author toSave;

        private final Connection transaction;

        private TorqueException caughtException;

        public SaveAndRollbackThread(
                Author toSave,
                Connection transaction,
                String name)
        {
            this.toSave = toSave;
            this.transaction = transaction;
            this.setName(name);
        }

        @Override
        public void run()
        {
            try
            {
                log.debug(getName() + "Starting to save author");
                toSave.save(transaction);
                log.debug(getName() + "Finished saving author");
            }
            catch (TorqueException e)
            {
                log.info(getName() + "caught deadlock exception (as expected) "
                        + e.getClass().getName());
                caughtException = e;
            }
            finally
            {
                log.debug(getName() + "starting rollback");
                Transaction.safeRollback(transaction);
                log.debug(getName() + "rollback finished");
            }
        }

        public TorqueException getCaughtException()
        {
            return caughtException;
        }
    }

}
