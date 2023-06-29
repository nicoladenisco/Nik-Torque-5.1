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

import junit.framework.TestCase;

import org.apache.torque.om.NumberKey;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.test.dbobject.NullableOIntegerFk;
import org.apache.torque.test.dbobject.NullablePIntegerFk;
import org.apache.torque.test.dbobject.OIntegerFkWithDefault;
import org.apache.torque.test.dbobject.PIntegerFkWithDefault;

/**
 * Tests the foreign key getter methods in the generated data object classes.
 *
 * @version $Id: ForeignKeyGetterTest.java 1849386 2018-12-20 13:28:59Z tv $
 */
public class ForeignKeyGetterTest extends TestCase
{
    /**
     * Tests that the foreign key getter returns null if the primitive foreign
     * key is 0.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForPrimitiveIntZero() throws Exception
    {
        NullablePIntegerFk nullablePIntegerFk = new NullablePIntegerFk();
        nullablePIntegerFk.setFk(0);
        ObjectKey<?> foreignKey = nullablePIntegerFk.getForeignKeyForPIntegerPk();
        assertNull(foreignKey.getValue());
    }

    /**
     * Tests that the foreign key getter returns an Integer key
     * if the primitive foreign key is not zero.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForPrimitiveIntNotZero() throws Exception
    {
        NullablePIntegerFk nullablePIntegerFk = new NullablePIntegerFk();
        nullablePIntegerFk.setFk(3);
        ObjectKey<?> foreignKey = nullablePIntegerFk.getForeignKeyForPIntegerPk();
        assertEquals(new NumberKey(3), foreignKey);
    }

    /**
     * Tests that the foreign key getter returns null if the primitive foreign
     * key column has a default value and the column value is 0.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForDefaultedPrimitiveIntZero() throws Exception
    {
        PIntegerFkWithDefault pIntegerFk = new PIntegerFkWithDefault();
        pIntegerFk.setFk(0);
        ObjectKey<?> foreignKey = pIntegerFk.getForeignKeyForPIntegerPk();
        assertNull(foreignKey.getValue());
    }

    /**
     * Tests that the foreign key getter returns null if the primitive foreign
     * key column has a default value and the column value is the default value.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForDefaultedPrimitiveIntDefault()
            throws Exception
    {
        PIntegerFkWithDefault pIntegerFk = new PIntegerFkWithDefault();
        pIntegerFk.setFk(2);
        ObjectKey<?> foreignKey = pIntegerFk.getForeignKeyForPIntegerPk();
        assertEquals(new NumberKey(2), foreignKey);
    }

    /**
     * Tests that the foreign key getter returns an Integer key
     * if the primitive foreign key column has a default value
     * and the column value is not zero.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForDefaultedPrimitiveIntNotZero() throws Exception
    {
        PIntegerFkWithDefault pIntegerFk = new PIntegerFkWithDefault();
        pIntegerFk.setFk(3);
        ObjectKey<?> foreignKey = pIntegerFk.getForeignKeyForPIntegerPk();
        assertEquals(new NumberKey(3), foreignKey);
    }

    /**
     * Tests that the foreign key getter returns null if the object foreign
     * key is null.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForObjectIntNull() throws Exception
    {
        NullableOIntegerFk nullableOIntegerFk = new NullableOIntegerFk();
        nullableOIntegerFk.setFk(null);
        ObjectKey<?> foreignKey = nullableOIntegerFk.getForeignKeyForOIntegerPk();
        assertNull(foreignKey.getValue());
    }

    /**
     * Tests that the foreign key getter returns null if the object foreign
     * key is 0.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForObjectIntZero() throws Exception
    {
        NullableOIntegerFk nullableOIntegerFk = new NullableOIntegerFk();
        nullableOIntegerFk.setFk(0);
        ObjectKey<?> foreignKey = nullableOIntegerFk.getForeignKeyForOIntegerPk();
        assertEquals(new NumberKey(0), foreignKey);
    }

    /**
     * Tests that the foreign key getter returns an Integer key
     * if the object foreign key is not zero.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForObjectIntNotZero() throws Exception
    {
        NullableOIntegerFk nullableOIntegerFk = new NullableOIntegerFk();
        nullableOIntegerFk.setFk(3);
        ObjectKey<?> foreignKey = nullableOIntegerFk.getForeignKeyForOIntegerPk();
        assertEquals(new NumberKey(3), foreignKey);
    }

    /**
     * Tests that the foreign key getter returns null if the object foreign
     * key column has a default value and the column value is null.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForDefaultedObjectIntNull() throws Exception
    {
        OIntegerFkWithDefault oIntegerFk = new OIntegerFkWithDefault();
        oIntegerFk.setFk(null);
        ObjectKey<?> foreignKey = oIntegerFk.getForeignKeyForOIntegerPk();
        assertNull(foreignKey.getValue());
    }

    /**
     * Tests that the foreign key getter returns null if the object foreign
     * key column has a default value and the column value is 0.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForDefaultedObjectIntZero() throws Exception
    {
        OIntegerFkWithDefault oIntegerFk = new OIntegerFkWithDefault();
        oIntegerFk.setFk(0);
        ObjectKey<?> foreignKey = oIntegerFk.getForeignKeyForOIntegerPk();
        assertEquals(new NumberKey(0), foreignKey);
    }

    /**
     * Tests that the foreign key getter returns null if the object foreign
     * key column has a default value and the column value is the default value.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForDefaultedObjectIntDefault() throws Exception
    {
        OIntegerFkWithDefault oIntegerFk = new OIntegerFkWithDefault();
        oIntegerFk.setFk(2);
        ObjectKey<?> foreignKey = oIntegerFk.getForeignKeyForOIntegerPk();
        assertEquals(new NumberKey(2), foreignKey);
    }

    /**
     * Tests that the foreign key getter returns an Integer key
     * if the object foreign key column has a default value
     * and the column value is not zero.
     *
     * @throws Exception if a database error occurs.
     */
    public void testGetForeignKeyForDefaultedObjectIntNotZero() throws Exception
    {
        OIntegerFkWithDefault oIntegerFk = new OIntegerFkWithDefault();
        oIntegerFk.setFk(3);
        ObjectKey<?> foreignKey = oIntegerFk.getForeignKeyForOIntegerPk();
        assertEquals(new NumberKey(3), foreignKey);
    }
}
