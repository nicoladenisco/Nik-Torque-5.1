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

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.adapter.Adapter;
import org.apache.torque.adapter.PostgresAdapter;
import org.apache.torque.junit5.extension.AdapterProvider;
import org.apache.torque.test.dbobject.Author;
import org.apache.torque.test.peer.AuthorPeer;
import org.apache.torque.util.JdbcTypedValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;

/**
 * Tests executeStatement calls.
 *
 * @version $Id: $
 */
public class ExecuteStatementsTest extends BaseDatabaseTestCase
{
    private List<Author> authorList;

    @BeforeEach
    public void setUp() throws Exception
    {
        cleanBookstore();
        authorList = insertBookstoreData();
    }

    /**
     * Test execution of a statement with string replacements
     *
     * @throws Exception if the test fails
     */
    @Test
    @ArgumentsSource(AdapterProvider.class)
    @Execution(ExecutionMode.SAME_THREAD)
    public void testExecuteStatementWithStringReplacements(Adapter adapter) throws Exception
    {
        Map<String, JdbcTypedValue> replacements = new HashMap<>();
        replacements.put("name", new JdbcTypedValue("Author 1", Types.VARCHAR));
        replacements.put("newname", new JdbcTypedValue("Author 1 changed", Types.VARCHAR));
        
        boolean unqualifiedSetColumn = false;
        if (adapter instanceof PostgresAdapter) {
        	// needs unqualified column in set
        	unqualifiedSetColumn = true;
        }
        
        int updateCount = AuthorPeer.executeStatement(
                "update " + AuthorPeer.TABLE_NAME + " set " + ((unqualifiedSetColumn)?  AuthorPeer.NAME.getColumnName():AuthorPeer.NAME) + "=:newname where " + AuthorPeer.NAME + "=:name",
                replacements);
        

        assertEquals(1,updateCount);
        authorList.get(0).setName("Author 1 changed");
        verifyBookstore(authorList);
    }
}
