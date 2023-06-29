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

import org.apache.torque.test.dbobject.NullableOIntegerFk;
import org.apache.torque.test.dbobject.NullablePIntegerFk;
import org.apache.torque.test.dbobject.OIntegerFkToPPk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.dbobject.PIntegerFkToOPk;
import org.apache.torque.test.dbobject.PIntegerPk;

/**
 * Tests the foreign key adder methods in the generated data object classes.
 *
 * @version $Id: ForeignKeyAdderTest.java 1395238 2012-10-07 07:30:25Z tfischer $
 */
public class ForeignKeyAdderTest extends TestCase
{
    /**
     * Tests that the add method works if referenced and referencing are
     * related by primitive ints and the referenced object has an id of zero.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddPrimitiveIntFkZero() throws Exception
    {
        // prepare
        PIntegerPk pIntegerPk = new PIntegerPk();
        NullablePIntegerFk nullablePIntegerFk = new NullablePIntegerFk();

        // execute
        pIntegerPk.addNullablePIntegerFk(nullablePIntegerFk);

        // verify
        assertEquals(1, pIntegerPk.getNullablePIntegerFks().size());
        assertSame(
                nullablePIntegerFk,
                pIntegerPk.getNullablePIntegerFks().get(0));

        assertEquals(0, nullablePIntegerFk.getFk());
        assertSame(pIntegerPk, nullablePIntegerFk.getPIntegerPk());
    }

    /**
     * Tests that the add method works if referenced and referencing are
     * related by primitive ints and the referenced object has an id
     * which is not equal to zero.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddPrimitiveIntFkNonZero() throws Exception
    {
        // prepare
        PIntegerPk pIntegerPk = new PIntegerPk();
        pIntegerPk.setId(23);
        NullablePIntegerFk nullablePIntegerFk = new NullablePIntegerFk();

        // execute
        pIntegerPk.addNullablePIntegerFk(nullablePIntegerFk);

        // verify
        assertEquals(1, pIntegerPk.getNullablePIntegerFks().size());
        assertSame(
                nullablePIntegerFk,
                pIntegerPk.getNullablePIntegerFks().get(0));

        assertEquals(23, nullablePIntegerFk.getFk());
        assertSame(pIntegerPk, nullablePIntegerFk.getPIntegerPk());
    }

    /**
     * Tests that the add method works if the referenced object has an
     * primitive PK but the referencing object has an object FK,
     * and the referenced object has an id of null.
     *
     * This case is questionable, because 0 has a different meaning in both
     * cases.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddObjectToPrimitiveFkNull() throws Exception
    {
        // prepare
        PIntegerPk pIntegerPk = new PIntegerPk();
        OIntegerFkToPPk oIntegerFkToPPk = new OIntegerFkToPPk();

        // execute
        pIntegerPk.addOIntegerFkToPPk(oIntegerFkToPPk);

        // verify
        assertEquals(1, pIntegerPk.getOIntegerFkToPPks().size());
        assertSame(
                oIntegerFkToPPk,
                pIntegerPk.getOIntegerFkToPPks().get(0));

        assertEquals(new Integer(0), oIntegerFkToPPk.getFk());
        assertSame(pIntegerPk, oIntegerFkToPPk.getPIntegerPk());
    }


    /**
     * Tests that the add method works if the referenced object has an
     * primitive PK but the referencing object has an object FK,
     * which is not equal to zero.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddObjectToPrimitiveFkNonZero() throws Exception
    {
        // prepare
        PIntegerPk pIntegerPk = new PIntegerPk();
        pIntegerPk.setId(4);
        OIntegerFkToPPk oIntegerFkToPPk = new OIntegerFkToPPk();

        // execute
        pIntegerPk.addOIntegerFkToPPk(oIntegerFkToPPk);

        // verify
        assertEquals(1, pIntegerPk.getOIntegerFkToPPks().size());
        assertSame(
                oIntegerFkToPPk,
                pIntegerPk.getOIntegerFkToPPks().get(0));

        assertEquals(new Integer(4), oIntegerFkToPPk.getFk());
        assertSame(pIntegerPk, oIntegerFkToPPk.getPIntegerPk());
    }

    /**
     * Tests that the add method works if referenced and referencing are
     * related by object integers and the referenced object has an id of null.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddObjectIntegerFkNull() throws Exception
    {
        // prepare
        OIntegerPk oIntegerPk = new OIntegerPk();
        NullableOIntegerFk nullableOIntegerFk = new NullableOIntegerFk();

        // execute
        oIntegerPk.addNullableOIntegerFk(nullableOIntegerFk);

        // verify
        assertEquals(1, oIntegerPk.getNullableOIntegerFks().size());
        assertSame(
                nullableOIntegerFk,
                oIntegerPk.getNullableOIntegerFks().get(0));

        assertEquals(null, nullableOIntegerFk.getFk());
        assertSame(oIntegerPk, nullableOIntegerFk.getOIntegerPk());
    }

    /**
     * Tests that the add method works if referenced and referencing are
     * related by object integers and the referenced object has an id of zero.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddObjectIntegerFkZero() throws Exception
    {
        // prepare
        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.setId(0);
        NullableOIntegerFk nullableOIntegerFk = new NullableOIntegerFk();

        // execute
        oIntegerPk.addNullableOIntegerFk(nullableOIntegerFk);

        // verify
        assertEquals(1, oIntegerPk.getNullableOIntegerFks().size());
        assertSame(
                nullableOIntegerFk,
                oIntegerPk.getNullableOIntegerFks().get(0));

        assertEquals(new Integer(0), nullableOIntegerFk.getFk());
        assertSame(oIntegerPk, nullableOIntegerFk.getOIntegerPk());
    }

    /**
     * Tests that the add method works if referenced and referencing are
     * related by object integers and the referenced object has an id
     * which is not equal to zero.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddObjectIntegerFkNonZero() throws Exception
    {
        // prepare
        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.setId(13);
        NullableOIntegerFk nullableOIntegerFk = new NullableOIntegerFk();

        // execute
        oIntegerPk.addNullableOIntegerFk(nullableOIntegerFk);

        // verify
        assertEquals(1, oIntegerPk.getNullableOIntegerFks().size());
        assertSame(
                nullableOIntegerFk,
                oIntegerPk.getNullableOIntegerFks().get(0));

        assertEquals(new Integer(13), nullableOIntegerFk.getFk());
        assertSame(oIntegerPk, nullableOIntegerFk.getOIntegerPk());
    }

    /**
     * Tests that the add method works if the referenced object has an
     * Object PK but the referencing object has a primitive FK,
     * and the referenced object has an id of null.
     *
     * This case is questionable, because the related fields in referencing
     * and referenced object are different.
     * However, both values mean that the final id does not yet exist.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddPrimitiveToObjectFkNull() throws Exception
    {
        // prepare
        OIntegerPk oIntegerPk = new OIntegerPk();
        PIntegerFkToOPk pIntegerFkToOPk = new PIntegerFkToOPk();

        // execute
        oIntegerPk.addPIntegerFkToOPk(pIntegerFkToOPk);

        // verify
        assertEquals(1, oIntegerPk.getPIntegerFkToOPks().size());
        assertSame(
                pIntegerFkToOPk,
                oIntegerPk.getPIntegerFkToOPks().get(0));

        assertEquals(0, pIntegerFkToOPk.getFk());
        assertSame(oIntegerPk, pIntegerFkToOPk.getOIntegerPk());
    }

    /**
     * Tests that the add method works if the referenced object has an
     * Object PK but the referencing object has a primitive FK,
     * and the referenced object has an id of zero.
     *
     * This case is questionable, because 0 means that the id is not yet
     * determined for a primitive key. But in practice ids will start at 1
     * so this should not happen.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddPrimitiveToObjectFkZero() throws Exception
    {
        // prepare
        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.setId(0);
        PIntegerFkToOPk pIntegerFkToOPk = new PIntegerFkToOPk();

        // execute
        oIntegerPk.addPIntegerFkToOPk(pIntegerFkToOPk);

        // verify
        assertEquals(1, oIntegerPk.getPIntegerFkToOPks().size());
        assertSame(
                pIntegerFkToOPk,
                oIntegerPk.getPIntegerFkToOPks().get(0));

        assertEquals(0, pIntegerFkToOPk.getFk());
        assertSame(oIntegerPk, pIntegerFkToOPk.getOIntegerPk());
    }

    /**
     * Tests that the add method works if referenced and referencing are
     * related by primitive ints and the referenced object has an id
     * which is not equal to zero.
     *
     * @throws Exception if an error occurs.
     */
    public void testAddPrimitiveToObjectFkNonZero() throws Exception
    {
        // prepare
        OIntegerPk oIntegerPk = new OIntegerPk();
        oIntegerPk.setId(4);
        PIntegerFkToOPk pIntegerFkToOPk = new PIntegerFkToOPk();

        // execute
        oIntegerPk.addPIntegerFkToOPk(pIntegerFkToOPk);

        // verify
        assertEquals(1, oIntegerPk.getPIntegerFkToOPks().size());
        assertSame(
                pIntegerFkToOPk,
                oIntegerPk.getPIntegerFkToOPks().get(0));

        assertEquals(4, pIntegerFkToOPk.getFk());
        assertSame(oIntegerPk, pIntegerFkToOPk.getOIntegerPk());
    }
}
