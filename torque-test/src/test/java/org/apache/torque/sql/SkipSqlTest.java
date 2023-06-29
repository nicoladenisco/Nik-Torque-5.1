package org.apache.torque.sql;

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

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Torque;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Checks whether the SkipSql flag works on a table.
 *
 * @version $Id: SkipSqlTest.java 1870836 2019-12-04 15:51:06Z gk $
 */
public class SkipSqlTest extends BaseDatabaseTestCase
{
    /**
     * Check that the skip_table (which has skipSql set to true)
     * is not present in the database.
     *
     * @throws Exception if an error occurs.
     */
    @Test
    public void testSkipTableNotPresent() throws Exception
    {
        Connection connection = null;
        try
        {
            connection = Torque.getConnection();
            assertFalse(DatabaseTools.tableExists("SKIP_SQL", connection));
        }
        finally
        {
            if (connection != null)
            {
                connection.close();
            }
        }
    }
}
