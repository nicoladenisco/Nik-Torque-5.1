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

import java.util.List;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.test.peer.BookPeer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Test correct behavior if Transaction is used with the try-with-resources pattern
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class TorqueConnectionImplTest extends BaseDatabaseTestCase
{
    
    public void testTryWithResources() throws TorqueException
    {
        // prepare
        cleanBookstore();
        insertBookstoreData();

        try(TorqueConnection con = Transaction.begin())
        {
            BookPeer.doSelect(new Criteria(), con);
            Transaction.commit(con);

            assertTrue(con.isCommitted());
        }
        catch (TorqueException e)
        {
            // expected
        }

        Author author = new Author();
        author.setName("AuthorProxy");

        try(TorqueConnection con = Transaction.begin())
        {
            author.save(con);
            throw new TorqueException("Forcing rollback");
        }
        catch (TorqueException e)
        {
            // expected
        }

        Criteria criteria = new Criteria();
        criteria.where(AuthorPeer.NAME, "AuthorProxy");
        List<Author> authors = AuthorPeer.doSelect(criteria);

        // Record should not be there
        assertEquals(0, authors.size());
    }
}
