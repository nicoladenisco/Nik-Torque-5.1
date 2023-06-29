package org.apache.torque.om;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import java.math.BigDecimal;
import java.util.Date;

import org.apache.torque.BaseDatabaseTestCase;
import org.apache.torque.Column;
import org.apache.torque.TorqueException;
import org.apache.torque.map.ColumnMap;
import org.apache.torque.test.dbobject.TypesObject;
import org.apache.torque.test.dbobject.TypesPrimitive;
import org.apache.torque.test.peer.TypesObjectPeer;
import org.apache.torque.test.peer.TypesPrimitivePeer;
import org.junit.jupiter.api.Test;

/**
 * Test the various setBy and getBy methods that can be used to access field
 * values.
 * <p>
 * Depends on names and ordering in the following tables to match the static
 * fields defined in this class:
 * <P>
 *
 * TypesObject table - which contain column definitions for all (AFAIK) Torque
 * supported column types that use Java Objects for storage. E.g. Integer and
 * not int.
 * <P>
 *
 * TypesPrimitive table - which is the same as TypesObjects except that it uses
 * primitive types for storage.
 * <p>
 *
 * InheritanceTest table - which contains a protected field.
 * <P>
 *
 * @author <a href="mailto:greg.monroe@dukece.com>Greg Monroe</a>
 * @version $Id
 */
public class OMByNameMethodsTest extends BaseDatabaseTestCase
{
    public static final String PROTECTED_COLUMN_NAME = "PayLoadB";

    public static final String PROTECTED_COLUMN_PEER_NAME =
            "INHERITANCE_TEST.PAYLOAD_B";

    public static final String PROTECTED_COLUMN_TABLE = "INHERITANCE_TEST";

    public static final int PROTECTED_COLUMN_POSITION = 3;

    public static final Object[] OBJECT_TEST_VALUES =
        {
                new Boolean(true), // "OBit",
                new Byte((byte) 1), // "OTinyint",
                new Short((short) 1), // "OSmallint",
                new Long((long) 1.0), // "OBigint",
                new Double(1.0), // "OFloat",
                new Float(1.0), // "OReal",
                new BigDecimal(1.0), // "ONumeric",
                new BigDecimal(1.0), // "ODecimal",
                "OChar_TEST_VALUE", // "OChar",
                "OVarchar_TEST_VALUE", // "OVarchar",
                "OLongvarchar_TEST_VALUE", // "OLongvarchar",
                new Date(1000000000l), // "ODate",
                new Date(1000000000l), // "OTime",
                new Integer(1), // "OInteger",
                new Date(1000000000l), // "OTimestamp",
                new byte[]
                        {
                                1, 1, 1
                        }, // "OBinary",
                        new byte[]
                                {
                                        1, 1, 1
                                }, // "OVarbinary",
                                new byte[]
                                        {
                                                1, 1, 1
                                        }, // "OLongvarbinary",
                                        new byte[]
                                                {
                                                        1, 1, 1
                                                }, // "OBlob",
                                                "OClob_TEST_VALUE", // "OClob",
                                                new Boolean(true), // "OBooleanint",
                                                new Boolean(true), // "OBooleanchar",
                                                new Double(1.0)
                                                // "ODouble"
        };

    public static final String[] OBJECT_COLUMN_NAMES =
        {
                "OBit", "OTinyint", "OSmallint", "OBigint", "OFloat", "OReal",
                "ONumeric", "ODecimal", "OChar", "OVarchar", "OLongvarchar",
                "ODate", "OTime", "OInteger", "OTimestamp", "OBinary",
                "OVarbinary", "OLongvarbinary", "OBlob", "OClob", "OBooleanint",
                "OBooleanchar", "ODouble"
        };

    public static final String[] PRIMITIVE_COLUMN_NAMES =
        {
                "PBit", // boolean
                "PTinyint", // byte
                "PSmallint", // short
                "PBigint", // long
                "PFloat", // double
                "PReal", // float
                "PNumeric", //BigDecimal - Skipped because same as Object
                "PDecimal", //BigDecimal - Skipped because same as Object
                "PChar", //String - Skipped because same as Object
                "PVarchar", //String - Skipped because same as Object
                "PLongvarchar", //String - Skipped because same as Object
                "PDate", //Date - Skipped because same as Object
                "PTime", //Date - Skipped because same as Object
                "PInteger", // int
                "PTimestamp", //Date - Skipped because same as Object
                "PBinary", //byte[] - Skipped because same as Object
                "PVarbinary", //byte[] - Skipped because same as Object
                "PLongvarbinary", //byte[] - Skipped because same as Object
                "PBlob", //byte[] - Skipped because same as Object
                "PClob", //String - Skipped because same as Object
                "PBooleanint", // boolean
                "PBooleanchar", // boolean
                "PDouble" // double
        };

    public static final Object[] PRIMITIVE_TEST_VALUES =
        {
                new Boolean(true), // "PBit",
                new Byte((byte) 1), // "PTinyint",
                new Short((short) 1), // "PSmallint",
                new Long((long) 1.0), // "PBigint",
                new Double(1.0), // "PFloat",
                new Float(1.0), // "PReal",
                new BigDecimal(1.0), // "PNumeric",
                new BigDecimal(1.0), // "PDecimal",
                "OChar_TEST_VALUE", // "PChar",
                "OVarchar_TEST_VALUE", // "PVarchar",
                "OLongvarchar_TEST_VALUE", // "PLongvarchar",
                new Date(1000000000l), // "PDate",
                new Date(1000000000l), // "PTime",
                new Integer(1), // "PInteger",
                new Date(1000000000l), // "PTimestamp",
                new byte[]
                        {
                                1, 1, 1
                        }, // "PBinary",
                        new byte[]
                                {
                                        1, 1, 1
                                }, // "PVarbinary",
                                new byte[]
                                        {
                                                1, 1, 1
                                        }, // "PLongvarbinary",
                                        new byte[]
                                                {
                                                        1, 1, 1
                                                }, // "PBlob",
                                                "OClob_TEST_VALUE", // "PClob",
                                                new Boolean(true), // "PBooleanint",
                                                new Boolean(true), // "PBooleanchar",
                                                new Double(1.0)    //" PDouble"
        };

    public static final Column[] OBJECT_PEER_NAMES =
        {
                TypesObjectPeer.O_BIT,
                TypesObjectPeer.O_TINYINT,
                TypesObjectPeer.O_SMALLINT,
                TypesObjectPeer.O_BIGINT,
                TypesObjectPeer.O_FLOAT,
                TypesObjectPeer.O_REAL,
                TypesObjectPeer.O_NUMERIC,
                TypesObjectPeer.O_DECIMAL,
                TypesObjectPeer.O_CHAR,
                TypesObjectPeer.O_VARCHAR,
                TypesObjectPeer.O_LONGVARCHAR,
                TypesObjectPeer.O_DATE,
                TypesObjectPeer.O_TIME,
                TypesObjectPeer.O_INTEGER,
                TypesObjectPeer.O_TIMESTAMP,
                TypesObjectPeer.O_BINARY,
                TypesObjectPeer.O_VARBINARY,
                TypesObjectPeer.O_LONGVARBINARY,
                TypesObjectPeer.O_BLOB,
                TypesObjectPeer.O_CLOB,
                TypesObjectPeer.O_BOOLEANINT,
                TypesObjectPeer.O_BOOLEANCHAR,
                TypesObjectPeer.O_DOUBLE
        };

    public static final ColumnMap[] PRIMITIVE_PEER_NAMES =
        {
                TypesPrimitivePeer.P_BIT,
                TypesPrimitivePeer.P_TINYINT,
                TypesPrimitivePeer.P_SMALLINT,
                TypesPrimitivePeer.P_BIGINT,
                TypesPrimitivePeer.P_FLOAT,
                TypesPrimitivePeer.P_REAL,
                TypesPrimitivePeer.P_NUMERIC,
                TypesPrimitivePeer.P_DECIMAL,
                TypesPrimitivePeer.P_CHAR,
                TypesPrimitivePeer.P_VARCHAR,
                TypesPrimitivePeer.P_LONGVARCHAR,
                TypesPrimitivePeer.P_DATE,
                TypesPrimitivePeer.P_TIME,
                TypesPrimitivePeer.P_INTEGER,
                TypesPrimitivePeer.P_TIMESTAMP,
                TypesPrimitivePeer.P_BINARY,
                TypesPrimitivePeer.P_VARBINARY,
                TypesPrimitivePeer.P_LONGVARBINARY,
                TypesPrimitivePeer.P_BLOB,
                TypesPrimitivePeer.P_CLOB,
                TypesPrimitivePeer.P_BOOLEANINT,
                TypesPrimitivePeer.P_BOOLEANCHAR,
                TypesPrimitivePeer.P_DOUBLE
        };

    /*
     * Validate that the setByName methods work using a BaseObject class type.
     * Checks the setValue against the value returned by the get<FieldName>()
     * methods for all known object and primitive types.
     */
    @Test
    public void testSetByNameMethod() throws Exception
    {

        // Testing SetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_COLUMN_NAMES.length; i++)
            {
                boolean status = objectTypes.setByName(OBJECT_COLUMN_NAMES[i],
                        OBJECT_TEST_VALUES[i]);
                assertTrue("setByName returned false setting column "
                        + OBJECT_COLUMN_NAMES[i], status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesObject) objectTypes);
        // Test Primitive Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_COLUMN_NAMES.length; i++)
            {
                boolean status = primitiveTypes.setByName(
                        PRIMITIVE_COLUMN_NAMES[i], PRIMITIVE_TEST_VALUES[i]);
                assertTrue("setByName returned false setting column "
                        + PRIMITIVE_COLUMN_NAMES[i], status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesPrimitive) primitiveTypes);
    }

    /*
     * Validate that the getBy* methods work using a BaseObject class type.
     * Checks that getValue returns the value set with a setBy call for all
     * known object and primitive types.
     *
     * @throws Exception
     */
    @Test
    public void testGetByNameMethod() throws Exception
    {
        // Testing GetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_COLUMN_NAMES.length; i++)
            {
                objectTypes.setByName(OBJECT_COLUMN_NAMES[i],
                        OBJECT_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        String eMsg = "Did not get expected value for object column: ";
        for (int i = 0; i < OBJECT_COLUMN_NAMES.length; i++)
        {
            assertTrue(eMsg + OBJECT_COLUMN_NAMES[i], OBJECT_TEST_VALUES[i]
                    .equals(objectTypes.getByName(OBJECT_COLUMN_NAMES[i])));
        }

        // Test Primative Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_COLUMN_NAMES.length; i++)
            {
                primitiveTypes.setByName(PRIMITIVE_COLUMN_NAMES[i],
                        PRIMITIVE_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        for (int i = 0; i < PRIMITIVE_COLUMN_NAMES.length; i++)
        {
            assertTrue(eMsg + PRIMITIVE_COLUMN_NAMES[i],
                    PRIMITIVE_TEST_VALUES[i].equals(
                            primitiveTypes.getByName(PRIMITIVE_COLUMN_NAMES[i])));
        }
    }

    /*
     * Validate that the setByPeerName methods work using a BaseObject class type.
     * Checks that getValue returns the value set with a setByPeerName call for all
     * known object and primitive types.
     */
    @Test
    public void testSetByFullyQualifiedPeerName() throws Exception
    {
        // Testing GetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_PEER_NAMES.length; i++)
            {
                boolean status = objectTypes.setByPeerName(
                        OBJECT_PEER_NAMES[i].getSqlExpression(),
                        OBJECT_TEST_VALUES[i]);
                assertTrue("setByPeerName returned false setting column "
                        + OBJECT_PEER_NAMES[i], status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesObject) objectTypes);

        // Test Primitive Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_PEER_NAMES.length; i++)
            {
                boolean status = primitiveTypes.setByPeerName(
                        PRIMITIVE_PEER_NAMES[i].getSqlExpression(),
                        OBJECT_TEST_VALUES[i]);
                assertTrue("setByPeerName returned false setting column "
                        + PRIMITIVE_PEER_NAMES[i], status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesPrimitive) primitiveTypes);
    }

    /*
     * Validate that the setByPeerName methods work using a BaseObject class type.
     * Checks that getValue returns the value set with a setByPeerName call for all
     * known object and primitive types.
     */
    @Test
    public void testSetUnqualifiedPeerName() throws Exception
    {
        // Testing GetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_PEER_NAMES.length; i++)
            {
                boolean status = objectTypes.setByPeerName(
                        OBJECT_PEER_NAMES[i].getColumnName(),
                        OBJECT_TEST_VALUES[i]);
                assertTrue("setByPeerName returned false setting column "
                        + OBJECT_PEER_NAMES[i], status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesObject) objectTypes);

        // Test Primitive Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_PEER_NAMES.length; i++)
            {
                boolean status = primitiveTypes.setByPeerName(
                        PRIMITIVE_PEER_NAMES[i].getColumnName(),
                        OBJECT_TEST_VALUES[i]);
                assertTrue("setByPeerName returned false setting column "
                        + PRIMITIVE_PEER_NAMES[i], status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesPrimitive) primitiveTypes);
    }

    /**
     * Validate that the getByPeerName methods work as expected for all types.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetByFullyQualifiedPeerName() throws Exception
    {
        // Testing GetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_COLUMN_NAMES.length; i++)
            {
                objectTypes.setByName(OBJECT_COLUMN_NAMES[i],
                        OBJECT_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        String eMsg = "Did not get expected value for object column: ";
        for (int i = 0; i < OBJECT_PEER_NAMES.length; i++)
        {
            assertEquals(eMsg + OBJECT_PEER_NAMES[i],
                    OBJECT_TEST_VALUES[i],
                    objectTypes.getByPeerName(
                            OBJECT_PEER_NAMES[i].getSqlExpression()));
        }

        // Test Primative Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_COLUMN_NAMES.length; i++)
            {
                primitiveTypes.setByName(PRIMITIVE_COLUMN_NAMES[i],
                        PRIMITIVE_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        for (int i = 0; i < PRIMITIVE_PEER_NAMES.length; i++)
        {
            assertEquals(eMsg + PRIMITIVE_PEER_NAMES[i],
                    PRIMITIVE_TEST_VALUES[i],
                    primitiveTypes.getByPeerName(
                            PRIMITIVE_PEER_NAMES[i].getSqlExpression()));
        }
    }

    /**
     * Validate that the getByPeerName methods work as expected for all types.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetByUnqualifiedPeerName() throws Exception
    {
        // Testing GetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_COLUMN_NAMES.length; i++)
            {
                objectTypes.setByName(OBJECT_COLUMN_NAMES[i],
                        OBJECT_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        String eMsg = "Did not get expected value for object column: ";
        for (int i = 0; i < OBJECT_PEER_NAMES.length; i++)
        {
            assertEquals(eMsg + OBJECT_PEER_NAMES[i],
                    OBJECT_TEST_VALUES[i],
                    objectTypes.getByPeerName(
                            OBJECT_PEER_NAMES[i].getColumnName()));
        }

        // Test Primative Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_COLUMN_NAMES.length; i++)
            {
                primitiveTypes.setByName(PRIMITIVE_COLUMN_NAMES[i],
                        PRIMITIVE_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        for (int i = 0; i < PRIMITIVE_PEER_NAMES.length; i++)
        {
            assertEquals(eMsg + PRIMITIVE_PEER_NAMES[i],
                    PRIMITIVE_TEST_VALUES[i],
                    primitiveTypes.getByPeerName(
                            PRIMITIVE_PEER_NAMES[i].getColumnName()));
        }
    }

    /*
     * Validate that the setByPostion methods work using a BaseObject class type.
     * Checks that getByPosition returns the value set with a setByPosition call
     * for all known object and primitive types.
     */
    @Test
    public void testSetByPositionMethod() throws Exception
    {
        // Testing GetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_PEER_NAMES.length; i++)
            {
                boolean status = objectTypes.setByPosition(i + 1,
                        OBJECT_TEST_VALUES[i]);
                assertTrue("objectTypes.setByPosition(int, Object ) returned "
                        + "false setting position " + (i + 1), status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesObject) objectTypes);

        // Test Primitive Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_PEER_NAMES.length; i++)
            {
                boolean status = primitiveTypes.setByPosition(i + 1,
                        OBJECT_TEST_VALUES[i]);
                assertTrue("primitiveTypes.setByPosition(int, Object) returned "
                        + "false setting position " + (i + 1), status);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesPrimitive.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        assertValues((TypesPrimitive) primitiveTypes);
    }

    /**
     * Validate that the getByPosition methods work as expected for all types.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testGetByPositionMethod() throws Exception
    {
        // Testing GetByName method for Object Types
        ColumnAccessByName objectTypes = new TypesObject();
        try
        {
            for (int i = 0; i < OBJECT_COLUMN_NAMES.length; i++)
            {
                objectTypes.setByName(OBJECT_COLUMN_NAMES[i],
                        OBJECT_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        String eMsg = "Did not get expected value for object column: ";
        for (int i = 0; i < OBJECT_PEER_NAMES.length; i++)
        {
            assertEquals(eMsg + (i + 1) + " (" + OBJECT_PEER_NAMES[i] + ")",
                    OBJECT_TEST_VALUES[i],
                    objectTypes.getByPosition(i + 1));
        }

        // Test Primative Types
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        try
        {
            for (int i = 0; i < PRIMITIVE_COLUMN_NAMES.length; i++)
            {
                primitiveTypes.setByName(PRIMITIVE_COLUMN_NAMES[i],
                        PRIMITIVE_TEST_VALUES[i]);
            }
        }
        catch (TorqueException e)
        {
            fail("Exception caught trying to call TypesObject.setByName() "
                    + "method!\nWas OM generated with torque.addGetByNameMethod "
                    + "property = true?\nError message was: '" + e.getMessage()
                    + "'");
        }
        for (int i = 0; i < PRIMITIVE_PEER_NAMES.length; i++)
        {
            assertEquals(eMsg + (i + 1) + " (" + PRIMITIVE_PEER_NAMES[i] + ")",
                    PRIMITIVE_TEST_VALUES[i],
                    primitiveTypes.getByPosition(i + 1));
        }
    }

    /*
     * Validate that various an IllegalArgumentException if thrown if the
     * Object type of the value is not correct.
     */
    @Test
    public void testInvalidObjectErrors() throws Exception
    {
        ColumnAccessByName objectTypes = new TypesObject();
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        // Test catching invalid object types
        boolean error = false;
        try
        {
            objectTypes.setByName("OBit", new Integer(1));
        }
        catch (IllegalArgumentException e)
        {
            error = true;
        }
        assertTrue(
                "setByName for OBit column did not catch illegal object type!",
                error);

        error = false;
        try
        {
            primitiveTypes.setByName("PBit", new Integer(99));
        }
        catch (IllegalArgumentException e)
        {
            error = true;
        }
        assertTrue(
                "setByName for PBit column did not catch illegal object type!",
                error);
    }

    /*
     * Validate that a false rc is returned if non-column names passed to methods.
     */
    @Test
    public void testInvalidNameErrors() throws Exception
    {
        ColumnAccessByName objectTypes = new TypesObject();
        // Test that false status is returned for invalid column names.
        boolean status = objectTypes.setByName("xxxOBit", new Integer(1));
        assertFalse("Did not get a false status from setByName with "
                + "invalid column name!", status);

        status = objectTypes.setByPeerName("xxxOBit", new Integer(1));
        assertFalse("Did not get a false status from setByPeerName with "
                + "invalid column name!", status);

        status = objectTypes.setByPosition(1000, new Integer(1));
        assertFalse("Did not get a false status from setByPosition with "
                + "invalid position!", status);
    }

    /**
     *  Verify that null handling (can't use them for primitives) works.
     */
    @Test
    public void testNullHandling() throws Exception
    {
        ColumnAccessByName objectTypes = new TypesObject();
        ColumnAccessByName primitiveTypes = new TypesPrimitive();
        // Object type fields should allow nulls
        boolean error = false;
        try
        {
            objectTypes.setByName("OBit", null);
        }
        catch (IllegalArgumentException e)
        {
            error = true;
        }
        assertFalse("objectTypes.setByName(\"OBit\",null) did not allow "
                +  "a null value!", error);

        // Primitive types should not allow null values
        error = false;
        try
        {
            primitiveTypes.setByName("PBit", null);
        }
        catch (IllegalArgumentException e)
        {
            error = true;
        }
        assertTrue("primitiveTypes.setByName(\"PBit\",null) allowed "
                +  "a null value!", error);
    }

    /**
     * Checks that values were set to the correct value.
     *
     * @param primitiveTypes the object to check the values in.
     */
    private void assertValues(final TypesPrimitive primitiveTypes)
    {
        String eMsg;
        eMsg = "Did not get expected value for primitive column: ";
        // "PBit", true, //boolean
        assertTrue(eMsg + "PBit", primitiveTypes.getPBit() == true);
        // "PTinyint", (byte) 1 //byte
        assertTrue(eMsg + "PTinyint", primitiveTypes.getPTinyint() == (byte) 1);
        // "PSmallint", (short) 1 //short
        assertTrue(eMsg + "PSmallint", primitiveTypes.getPSmallint() == (short) 1);
        // "PBigint", (long) 1.0 //long
        assertTrue(eMsg + "PBigint", primitiveTypes.getPBigint() == (long) 1.0);
        // "PFloat", 1.0 //double
        assertTrue(eMsg + "PFloat", primitiveTypes.getPFloat() == 1.0);
        // "PReal", 1.0 //float
        assertTrue(eMsg + "PReal", primitiveTypes.getPReal() == 1.0);
        // "PInteger", 1 //int
        assertTrue(eMsg + "PInteger", primitiveTypes.getPInteger() == 1);
        // "PBooleanint", true, //boolean
        assertTrue(eMsg + "PBooleanint", primitiveTypes.getPBooleanint() == true);
        // "PBooleanchar", true, //boolean
        assertTrue(eMsg + "PBooleanchar", primitiveTypes.getPBooleanchar() == true);
        // "PDouble" 1.0 //double
        assertTrue(eMsg + "PDouble", primitiveTypes.getPDouble() == 1.0);
    }

    /**
     * Checks that values were set to the correct value.
     *
     * @param objectTypes the object to check the values in.
     */
    private void assertValues(final TypesObject objectTypes)
    {
        String eMsg = "Did not get expected value for object column: ";
        int iValue = 0;
        // "OBit",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOBit()));
        // "OTinyint",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOTinyint()));
        // "OSmallint",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOSmallint()));
        // "OBigint",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOBigint()));
        // "OFloat",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOFloat()));
        // "OReal",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOReal()));
        // "ONumeric",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getONumeric()));
        // "ODecimal",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getODecimal()));
        // "OChar",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOChar()));
        // "OVarchar",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOVarchar()));
        // "OLongvarchar",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOLongvarchar()));
        // "ODate",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getODate()));
        // "OTime",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOTime()));
        // "OInteger",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOInteger()));
        // "OTimestamp",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOTimestamp()));
        // "OBinary",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOBinary()));
        // "OVarbinary",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOVarbinary()));
        // "OLongvarbinary",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOLongvarbinary()));
        // "OBlob",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOBlob()));
        // "OClob",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOClob()));
        // "OBooleanint",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOBooleanint()));
        // "OBooleanchar",
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getOBooleanchar()));
        // "ODouble"
        assertTrue(eMsg + OBJECT_COLUMN_NAMES[iValue],
                OBJECT_TEST_VALUES[iValue++].equals(objectTypes
                        .getODouble()));
    }
}
