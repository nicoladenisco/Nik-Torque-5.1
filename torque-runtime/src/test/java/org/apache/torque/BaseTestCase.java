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


import org.apache.torque.adapter.IDMethod;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.map.DatabaseMap;
import org.apache.torque.map.TableMap;
import org.junit.jupiter.api.BeforeEach;


/**
 * Base functionality to be extended by all Torque test cases.  Test
 * case implementations are used to automate unit testing via JUnit.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @version $Id: BaseTestCase.java 1867515 2019-09-25 15:02:03Z gk $
 */
public abstract class BaseTestCase
{
    /** The path to the configuration file. */
    public static final String CONFIG_FILE
    = "src/test/resources/Torque2.xml";
    //= "src/test/resources/Torque.properties";

    /** A pre-filled table map. */
    protected TableMap tableMap;

    /** A pre-filled database map. */
    protected DatabaseMap databaseMap;

    /** A Reference to the postgresql (default) database. */
    protected Database database;

    /** A Reference to the postgresql (default) database. */
    protected Database databasePostgresql;

    /** A Reference to the mysql database. */
    protected Database databaseMysql;

    /** A Reference to the oracle database. */
    protected Database databaseOracle;

    /** A pre-filled String column map. */
    protected ColumnMap stringColumnMap;

    /** A pre-filled String column map. */
    protected ColumnMap stringColumnMap2;

    /** A pre-filled String column map. */
    protected ColumnMap stringColumnMap3;

    /** A pre-filled Integer column map. */
    protected ColumnMap integerColumnMap;

    /**
     * Re-Initialize Torque and fill supplied data.  Subclasses which
     * override setUp() must call super.setUp() as their first action.
     *
     * @throws Exception if initialization fails.
     */
    @BeforeEach
    public void setUp() throws Exception
    {
        Torque.setInstance(null);
        Torque.init(CONFIG_FILE);

        databaseMap = Torque.getDatabaseMap(Torque.getDefaultDB());
        database = Torque.getDatabase(Torque.getDefaultDB());
        databasePostgresql = Torque.getDatabase("postgresql");
        databaseMysql = Torque.getDatabase("mysql");
        databaseOracle = Torque.getDatabase("oracle");
        tableMap = databaseMap.addTable("TABLE");
        tableMap.setPrimaryKeyMethod(IDMethod.SEQUENCE);
        {
            stringColumnMap = new ColumnMap("COLUMN1", tableMap);
            stringColumnMap.setType("");
            stringColumnMap.setJavaType("String");
            tableMap.addColumn(stringColumnMap);
        }
        {
            stringColumnMap2 = new ColumnMap("COLUMN2", tableMap);
            stringColumnMap2.setType("");
            stringColumnMap2.setJavaType("String");
            tableMap.addColumn(stringColumnMap2);
        }
        {
            stringColumnMap3 = new ColumnMap("COLUMN3", tableMap);
            stringColumnMap3.setType("");
            stringColumnMap3.setJavaType("String");
            tableMap.addColumn(stringColumnMap3);
        }
        {
            integerColumnMap = new ColumnMap("COLUMN4", tableMap);
            integerColumnMap.setType(Integer.valueOf(0));
            integerColumnMap.setJavaType("Integer");
            integerColumnMap.setPk(true);
            tableMap.addColumn(integerColumnMap);
        }
    }
}
