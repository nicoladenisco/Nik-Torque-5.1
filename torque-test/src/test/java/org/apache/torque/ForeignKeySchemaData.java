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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.mapper.IntegerMapper;
import org.apache.torque.test.dbobject.CompIntegerVarcharFk;
import org.apache.torque.test.dbobject.CompIntegerVarcharPk;
import org.apache.torque.test.dbobject.CompNonpkFk;
import org.apache.torque.test.dbobject.CompPkContainsFk;
import org.apache.torque.test.dbobject.CompPkOtherFk;
import org.apache.torque.test.dbobject.MultiRef;
import org.apache.torque.test.dbobject.MultiRefSameTable;
import org.apache.torque.test.dbobject.NonPkOIntegerFk;
import org.apache.torque.test.dbobject.NonPkPIntegerFk;
import org.apache.torque.test.dbobject.NullableOIntegerFk;
import org.apache.torque.test.dbobject.NullablePIntegerFk;
import org.apache.torque.test.dbobject.OIntegerPk;
import org.apache.torque.test.dbobject.PIntegerPk;
import org.apache.torque.test.dbobject.RequiredOIntegerFk;
import org.apache.torque.test.dbobject.RequiredPIntegerFk;
import org.apache.torque.test.peer.CompIntegerVarcharFkPeer;
import org.apache.torque.test.peer.CompIntegerVarcharPkPeer;
import org.apache.torque.test.peer.CompNonpkFkPeer;
import org.apache.torque.test.peer.CompPkContainsFkPeer;
import org.apache.torque.test.peer.CompPkOtherFkPeer;
import org.apache.torque.test.peer.MultiRefPeer;
import org.apache.torque.test.peer.MultiRefSameTablePeer;
import org.apache.torque.test.peer.NonPkOIntegerFkPeer;
import org.apache.torque.test.peer.NonPkPIntegerFkPeer;
import org.apache.torque.test.peer.NullableOIntegerFkPeer;
import org.apache.torque.test.peer.NullablePIntegerFkPeer;
import org.apache.torque.test.peer.OIntegerPkPeer;
import org.apache.torque.test.peer.PIntegerPkPeer;
import org.apache.torque.test.peer.RequiredOIntegerFkPeer;
import org.apache.torque.test.peer.RequiredPIntegerFkPeer;
import org.apache.torque.util.functions.Count;

/**
 * Contains data (i.e. table records) for the tables in the foreign key schema.
 *
 * @version $Id: ForeignKeySchemaData.java 1855052 2019-03-08 15:52:23Z tv $
 */
public class ForeignKeySchemaData
{
    private final List<PIntegerPk> pIntegerPkList
        = new ArrayList<>();

    private final List<NullablePIntegerFk> nullablePIntegerFkList
        = new ArrayList<>();

    private final List<RequiredPIntegerFk> requiredPIntegerFkList
        = new ArrayList<>();

    private final List<NonPkPIntegerFk> nonPkPIntegerFkList
        = new ArrayList<>();

    private final List<OIntegerPk> oIntegerPkList
        = new ArrayList<>();

    private final List<NullableOIntegerFk> nullableOIntegerFkList
        = new ArrayList<>();

    private final List<RequiredOIntegerFk> requiredOIntegerFkList
        = new ArrayList<>();

    private final List<NonPkOIntegerFk> nonPkOIntegerFkList
        = new ArrayList<>();

    private final List<CompPkOtherFk> compositePkOtherFkList
        = new ArrayList<>();

    private final List<CompPkContainsFk> compositePkContainsFkList
        = new ArrayList<>();

    private final List<CompIntegerVarcharPk> compositeIntegerVarcharPkList
        = new ArrayList<>();

    private final List<CompIntegerVarcharFk> compositeIntegerVarcharFkList
        = new ArrayList<>();

    private final List<CompNonpkFk> compositeNonpkFkList
        = new ArrayList<>();

    private final List<MultiRef> multiRefList
        = new ArrayList<>();

    private final List<MultiRefSameTable> multiRefSameTableList
        = new ArrayList<>();

    /**
     * Creates the default Test data for the foreign key schema.
     * The test data is filled as follows (p=primitive, o=object):
     *
     * pIntegerPk1
     *     - nonPkPIntegerFk1a
     *     - nonPkPIntegerFk1b
     *     - multiRef111a
     *     - multiRef111a
     *     - multiRef010
     *
     * pIntegerPk2
     *     - nonPkPIntegerFk2
     *     - nullablePIntegerFk2
     *     - requiredPIntegerFk2
     *     - multiRef222
     *
     * PIntegerPk3
     *     - nullablePIntegerFk3a
     *     - nullablePIntegerFk3b
     *     - requiredPIntegerFk3a
     *     - requiredPIntegerFk3b
     *
     * oIntegerPk1
     *     - nonPkOIntegerFk1a
     *     - nonPkOIntegerFk1b
     *     - compositePkOtherFk1a
     *     - compositePkOtherFk1b
     *     - multiRef111a
     *     - multiRef111a
     *     - multiRef100
     *
     * oIntegerPk2
     *     - nullableOIntegerFk2
     *     - requiredOIntegerFk2
     *     - nonPkOIntegerFk2
     *     - compositePkOtherFk2
     *     - multiRef222
     *
     * oIntegerPk3
     *     - nullableOIntegerFk3a
     *     - nullableOIntegerFk3b
     *     - requiredOIntegerFk3a
     *     - requiredOIntegerFk3b
     *
     * null
     *     - nullableOIntegerFk4
     *     - nonPkOIntegerFk4
     *
     * compositeIntegerVarcharPk1
     *     - compositeNonpkFk1a
     *     - compositeNonpkFk1b
     *
     * compositeIntegerVarcharPk2
     *     - compositeIntegerVarcharFk2
     *     - compositeNonpkFk2
     *
     * compositeIntegerVarcharPk3
     *     - compositeIntegerVarcharFk3a
     *     - compositeIntegerVarcharFk3b
     *
     * nullableOIntegerFk1
     *     - multiRef111a
     *     - multiRef111a
     *     - multiRef001
     *
     * nullableOIntegerFk2
     *     - multiRef222
     *
     * null
     *     - compositeIntegerVarcharFk4
     *
     * @return a new instance filled with the default test data.
     *
     * @throws TorqueException should not occur.
     */
    public static ForeignKeySchemaData getDefaultTestData()
            throws TorqueException
    {
        ForeignKeySchemaData result = new ForeignKeySchemaData();

        fillPIntegerPks(result);
        fillNullablePIntegerPks(result);
        fillRequiredPIntegerFks(result);
        fillNonPkIntegerFks(result);

        fillOIntegerPks(result);
        fillNullableOIntegerFks(result);
        fillRequiredOIntegerFks(result);
        fillNonPkOIntegerFks(result);
        fillCompositePkOtherFks(result);
        fillCompositePkContainsFks(result);

        fillCompositeIntegerVarcharPks(result);
        fillCompositeIntegerVarcharFks(result);
        fillCompositeNonpkFks(result);

        fillMultiRefs(result);
        fillMultiRefSameTables(result);

        return result;
    }

    private static void fillPIntegerPks(final ForeignKeySchemaData result)
    {
        PIntegerPk pIntegerPk1 = new PIntegerPk();
        pIntegerPk1.setName("pIntegerPk1");
        pIntegerPk1.setIntegerColumn(3);
        result.getPIntegerPkList().add(pIntegerPk1);

        PIntegerPk pIntegerPk2 = new PIntegerPk();
        pIntegerPk2.setName("pIntegerPk2");
        pIntegerPk2.setIntegerColumn(2);
        result.getPIntegerPkList().add(pIntegerPk2);

        PIntegerPk pIntegerPk3 = new PIntegerPk();
        pIntegerPk3.setName("pIntegerPk3");
        pIntegerPk3.setIntegerColumn(1);
        result.getPIntegerPkList().add(pIntegerPk3);
    }

    private static void fillNullablePIntegerPks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        NullablePIntegerFk nullablePIntegerFk2
            = new NullablePIntegerFk();
        nullablePIntegerFk2.setName(
                "nullablePIntegerFk2");
        result.getPIntegerPkList().get(1).addNullablePIntegerFk(
                nullablePIntegerFk2);
        result.getNullablePIntegerFkList().add(
                nullablePIntegerFk2);

        NullablePIntegerFk nullablePIntegerFk3a
            = new NullablePIntegerFk();
        nullablePIntegerFk3a.setName(
                "nullablePIntegerFk3a");
        result.getPIntegerPkList().get(2).addNullablePIntegerFk(
                nullablePIntegerFk3a);
        result.getNullablePIntegerFkList().add(
                nullablePIntegerFk3a);

        NullablePIntegerFk nullablePIntegerFk3b
            = new NullablePIntegerFk();
        nullablePIntegerFk3b.setName(
                "nullablePIntegerFk3b");
        result.getPIntegerPkList().get(2).addNullablePIntegerFk(
                nullablePIntegerFk3b);
        result.getNullablePIntegerFkList().add(
                nullablePIntegerFk3b);
    }

    private static void fillRequiredPIntegerFks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        RequiredPIntegerFk requiredPIntegerFk2
            = new RequiredPIntegerFk();
        requiredPIntegerFk2.setName(
                "requiredPIntegerFk2");
        result.getPIntegerPkList().get(1).addRequiredPIntegerFk(
                requiredPIntegerFk2);
        result.getRequiredPIntegerFkList().add(
                requiredPIntegerFk2);

        RequiredPIntegerFk requiredPIntegerFk3a
            = new RequiredPIntegerFk();
        requiredPIntegerFk3a.setName(
                "requiredPIntegerFk3a");
        result.getPIntegerPkList().get(2).addRequiredPIntegerFk(
                requiredPIntegerFk3a);
        result.getRequiredPIntegerFkList().add(
                requiredPIntegerFk3a);

        RequiredPIntegerFk requiredPIntegerFk3b
            = new RequiredPIntegerFk();
        requiredPIntegerFk3b.setName(
                "requiredPIntegerFk3b");
        result.getPIntegerPkList().get(2).addRequiredPIntegerFk(
                requiredPIntegerFk3b);
        result.getRequiredPIntegerFkList().add(
                requiredPIntegerFk3b);
    }

    private static void fillNonPkIntegerFks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        NonPkPIntegerFk nonPkPIntegerFk1a
            = new NonPkPIntegerFk();
        nonPkPIntegerFk1a.setName(
                "nonPkPIntegerFk1a");
        result.getPIntegerPkList().get(0).addNonPkPIntegerFk(
                nonPkPIntegerFk1a);
        result.getNonPkPIntegerFkList().add(
                nonPkPIntegerFk1a);

        NonPkPIntegerFk nonPkPIntegerFk1b
            = new NonPkPIntegerFk();
        nonPkPIntegerFk1b.setName(
                "nonPkPIntegerFk1b");
        result.getPIntegerPkList().get(0).addNonPkPIntegerFk(
                nonPkPIntegerFk1b);
        result.getNonPkPIntegerFkList().add(
                nonPkPIntegerFk1b);

        NonPkPIntegerFk nonPkPIntegerFk2
            = new NonPkPIntegerFk();
        nonPkPIntegerFk2.setName(
                "nonPkPIntegerFk2");
        result.getPIntegerPkList().get(1).addNonPkPIntegerFk(
                nonPkPIntegerFk2);
        result.getNonPkPIntegerFkList().add(
                nonPkPIntegerFk2);
    }

    private static void fillOIntegerPks(final ForeignKeySchemaData result)
    {
        OIntegerPk oIntegerPk1 = new OIntegerPk();
        oIntegerPk1.setName("oIntegerPk1");
        oIntegerPk1.setIntegerColumn(3);
        result.getOIntegerPkList().add(oIntegerPk1);

        OIntegerPk oIntegerPk2 = new OIntegerPk();
        oIntegerPk2.setName("oIntegerPk2");
        oIntegerPk2.setIntegerColumn(2);
        result.getOIntegerPkList().add(oIntegerPk2);

        OIntegerPk oIntegerPk3 = new OIntegerPk();
        oIntegerPk3.setName("oIntegerPk3");
        oIntegerPk3.setIntegerColumn(1);
        result.getOIntegerPkList().add(oIntegerPk3);
    }

    private static void fillNullableOIntegerFks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        NullableOIntegerFk nullableOIntegerFk2
            = new NullableOIntegerFk();
        nullableOIntegerFk2.setName(
                "nullableOIntegerFk2");
        result.getOIntegerPkList().get(1).addNullableOIntegerFk(
                nullableOIntegerFk2);
        result.getNullableOIntegerFkList().add(
                nullableOIntegerFk2);

        NullableOIntegerFk nullableOIntegerFk3a
            = new NullableOIntegerFk();
        nullableOIntegerFk3a.setName(
                "nullableOIntegerFk3a");
        result.getOIntegerPkList().get(2).addNullableOIntegerFk(
                nullableOIntegerFk3a);
        result.getNullableOIntegerFkList().add(
                nullableOIntegerFk3a);

        NullableOIntegerFk nullableOIntegerFk3b
            = new NullableOIntegerFk();
        nullableOIntegerFk3b.setName(
                "nullableOIntegerFk3b");
        result.getOIntegerPkList().get(2).addNullableOIntegerFk(
                nullableOIntegerFk3b);
        result.getNullableOIntegerFkList().add(
                nullableOIntegerFk3b);

        NullableOIntegerFk nullableOIntegerFk4
            = new NullableOIntegerFk();
        nullableOIntegerFk4.setName(
                "nullableOIntegerFk4");
        result.getNullableOIntegerFkList().add(
                nullableOIntegerFk4);
    }

    private static void fillRequiredOIntegerFks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        RequiredOIntegerFk requiredOIntegerFk2
            = new RequiredOIntegerFk();
        requiredOIntegerFk2.setName(
                "requiredOIntegerFk2");
        result.getOIntegerPkList().get(1).addRequiredOIntegerFk(
                requiredOIntegerFk2);
        result.getRequiredOIntegerFkList().add(
                requiredOIntegerFk2);

        RequiredOIntegerFk requiredOIntegerFk3a
            = new RequiredOIntegerFk();
        requiredOIntegerFk3a.setName(
                "requiredOIntegerFk3a");
        result.getOIntegerPkList().get(2).addRequiredOIntegerFk(
                requiredOIntegerFk3a);
        result.getRequiredOIntegerFkList().add(
                requiredOIntegerFk3a);

        RequiredOIntegerFk requiredOIntegerFk3b
            = new RequiredOIntegerFk();
        requiredOIntegerFk3b.setName(
                "requiredOIntegerFk3b");
        result.getOIntegerPkList().get(2).addRequiredOIntegerFk(
                requiredOIntegerFk3b);
        result.getRequiredOIntegerFkList().add(
                requiredOIntegerFk3b);
    }

    private static void fillNonPkOIntegerFks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        NonPkOIntegerFk nonPkOIntegerFk1a
            = new NonPkOIntegerFk();
        nonPkOIntegerFk1a.setName(
                "nonPkOIntegerFk1a");
        result.getOIntegerPkList().get(0).addNonPkOIntegerFk(
                nonPkOIntegerFk1a);
        result.getNonPkOIntegerFkList().add(
                nonPkOIntegerFk1a);

        NonPkOIntegerFk nonPkOIntegerFk1b
            = new NonPkOIntegerFk();
        nonPkOIntegerFk1b.setName(
                "nonPkOIntegerFk1b");
        result.getOIntegerPkList().get(0).addNonPkOIntegerFk(
                nonPkOIntegerFk1b);
        result.getNonPkOIntegerFkList().add(
                nonPkOIntegerFk1b);

        NonPkOIntegerFk nonPkOIntegerFk2
            = new NonPkOIntegerFk();
        nonPkOIntegerFk2.setName(
                "nonPkOIntegerFk2");
        result.getOIntegerPkList().get(1).addNonPkOIntegerFk(
                nonPkOIntegerFk2);
        result.getNonPkOIntegerFkList().add(
                nonPkOIntegerFk2);

        NonPkOIntegerFk nonPkOIntegerFk4
            = new NonPkOIntegerFk();
        nonPkOIntegerFk4.setName(
                "nonPkOIntegerFk4");
        result.getNonPkOIntegerFkList().add(
                nonPkOIntegerFk4);
    }

    private static void fillCompositePkOtherFks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        CompPkOtherFk compositePkOtherFk1a
            = new CompPkOtherFk();
        compositePkOtherFk1a.setName(
                "compositePkOtherFk1a");
        compositePkOtherFk1a.setId1(1);
        compositePkOtherFk1a.setId2("1a");
        result.getOIntegerPkList().get(0).addCompPkOtherFk(
                compositePkOtherFk1a);
        result.getCompositePkOtherFkList().add(
                compositePkOtherFk1a);

        CompPkOtherFk compositePkOtherFk1b
            = new CompPkOtherFk();
        compositePkOtherFk1b.setName(
                "compositePkOtherFk1b");
        compositePkOtherFk1b.setId1(1);
        compositePkOtherFk1b.setId2("1b");
        result.getOIntegerPkList().get(0).addCompPkOtherFk(
                compositePkOtherFk1b);
        result.getCompositePkOtherFkList().add(
                compositePkOtherFk1b);

        CompPkOtherFk compositePkOtherFk2
            = new CompPkOtherFk();
        compositePkOtherFk2.setName(
                "compositePkOtherFk22");
        compositePkOtherFk2.setId1(2);
        compositePkOtherFk2.setId2("2");
        result.getOIntegerPkList().get(1).addCompPkOtherFk(
                compositePkOtherFk2);
        result.getCompositePkOtherFkList().add(
                compositePkOtherFk2);
    }

    private static void fillCompositePkContainsFks(final ForeignKeySchemaData result)
            throws TorqueException
    {
        CompPkContainsFk compositePkContainsFk1a
            = new CompPkContainsFk();
        compositePkContainsFk1a.setName(
                "compositePkContainsFk1a");
        compositePkContainsFk1a.setId2("1a");
        result.getOIntegerPkList().get(0).addCompPkContainsFk(
                compositePkContainsFk1a);
        result.getCompositePkContainsFkList().add(
                compositePkContainsFk1a);

        CompPkContainsFk compositePkContainsFk1b
            = new CompPkContainsFk();
        compositePkContainsFk1b.setName(
                "compositePkOtherFk1b");
        compositePkContainsFk1b.setId2("1b");
        result.getOIntegerPkList().get(0).addCompPkContainsFk(
                compositePkContainsFk1b);
        result.getCompositePkContainsFkList().add(
                compositePkContainsFk1b);

        CompPkContainsFk compositePkContainsFk2
            = new CompPkContainsFk();
        compositePkContainsFk2.setName(
                "compositePkOtherFk22");
        compositePkContainsFk2.setId1(2);
        compositePkContainsFk2.setId2("2");
        result.getOIntegerPkList().get(1).addCompPkContainsFk(
                compositePkContainsFk2);
        result.getCompositePkContainsFkList().add(
                compositePkContainsFk2);
    }

    private static void fillCompositeIntegerVarcharPks(
            final ForeignKeySchemaData result)
    {
        CompIntegerVarcharPk compositeIntegerVarcharPk1
            = new CompIntegerVarcharPk();
        compositeIntegerVarcharPk1.setName("compositeIntegerVarcharPk1");
        compositeIntegerVarcharPk1.setId1(10);
        compositeIntegerVarcharPk1.setId2("x");
        compositeIntegerVarcharPk1.setIntegerColumn(100);
        compositeIntegerVarcharPk1.setVarcharColumn("a");
        result.getCompositeIntegerVarcharPkList().add(
                compositeIntegerVarcharPk1);

        CompIntegerVarcharPk compositeIntegerVarcharPk2
            = new CompIntegerVarcharPk();
        compositeIntegerVarcharPk2.setName("compositeIntegerVarcharPk2");
        compositeIntegerVarcharPk2.setId1(10);
        compositeIntegerVarcharPk2.setId2("y");
        compositeIntegerVarcharPk2.setIntegerColumn(100);
        compositeIntegerVarcharPk2.setVarcharColumn("b");
        result.getCompositeIntegerVarcharPkList().add(
                compositeIntegerVarcharPk2);

        CompIntegerVarcharPk compositeIntegerVarcharPk3
            = new CompIntegerVarcharPk();
        compositeIntegerVarcharPk3.setName("compositeIntegerVarcharPk3");
        compositeIntegerVarcharPk3.setId1(11);
        compositeIntegerVarcharPk3.setId2("x");
        compositeIntegerVarcharPk3.setIntegerColumn(200);
        compositeIntegerVarcharPk3.setVarcharColumn("a");
        result.getCompositeIntegerVarcharPkList().add(
                compositeIntegerVarcharPk3);
    }

    private static void fillCompositeIntegerVarcharFks(
            final ForeignKeySchemaData result)
                    throws TorqueException
    {
        CompIntegerVarcharFk compositeIntegerVarcharFk2
            = new CompIntegerVarcharFk();
        compositeIntegerVarcharFk2.setName(
                "compositeIntegerVarcharFk2");
        result.getCompositeIntegerVarcharPkList().get(1)
        .addCompIntegerVarcharFk(
                compositeIntegerVarcharFk2);
        result.getCompositeIntegerVarcharFkList().add(
                compositeIntegerVarcharFk2);

        CompIntegerVarcharFk compositeIntegerVarcharFk3a
            = new CompIntegerVarcharFk();
        compositeIntegerVarcharFk3a.setName(
                "compositeIntegerVarcharFk3a");
        result.getCompositeIntegerVarcharPkList().get(2)
        .addCompIntegerVarcharFk(
                compositeIntegerVarcharFk3a);
        result.getCompositeIntegerVarcharFkList().add(
                compositeIntegerVarcharFk3a);

        CompIntegerVarcharFk compositeIntegerVarcharFk3b
            = new CompIntegerVarcharFk();
        compositeIntegerVarcharFk3b.setName(
                "compositeIntegerVarcharFk3b");
        result.getCompositeIntegerVarcharPkList().get(2)
        .addCompIntegerVarcharFk(
                compositeIntegerVarcharFk3b);
        result.getCompositeIntegerVarcharFkList().add(
                compositeIntegerVarcharFk3b);

        CompIntegerVarcharFk compositeIntegerVarcharFk4
            = new CompIntegerVarcharFk();
        compositeIntegerVarcharFk4.setName(
                "compositeIntegerVarcharFk4");
        result.getCompositeIntegerVarcharFkList().add(
                compositeIntegerVarcharFk4);
    }

    private static void fillCompositeNonpkFks(
            final ForeignKeySchemaData result)
                    throws TorqueException
    {
        CompNonpkFk compositeNonpkFk1a
            = new CompNonpkFk();
        compositeNonpkFk1a.setName(
                "compositeNonpkFk1a");
        result.getCompositeIntegerVarcharPkList().get(0)
        .addCompNonpkFk(compositeNonpkFk1a);
        result.getCompositeNonpkFkList().add(compositeNonpkFk1a);

        CompNonpkFk compositeNonpkFk1b
            = new CompNonpkFk();
        compositeNonpkFk1b.setName(
                "compositeNonpkFk1b");
        result.getCompositeIntegerVarcharPkList().get(0)
        .addCompNonpkFk(compositeNonpkFk1b);
        result.getCompositeNonpkFkList().add(compositeNonpkFk1b);

        CompNonpkFk compositeNonpkFk2
            = new CompNonpkFk();
        compositeNonpkFk2.setName(
                "compositeNonpkFk2");
        result.getCompositeIntegerVarcharPkList().get(1)
        .addCompNonpkFk(compositeNonpkFk2);
        result.getCompositeNonpkFkList().add(compositeNonpkFk2);
    }

    private static void fillMultiRefs(
            final ForeignKeySchemaData result)
                    throws TorqueException
    {
        {
            MultiRef multiRef111a = new MultiRef();
            multiRef111a.setName("multiRef111a");
            multiRef111a.setOIntegerPk(result.getOIntegerPkList().get(0));
            multiRef111a.setPIntegerPk(result.getPIntegerPkList().get(0));
            multiRef111a.setNullableOIntegerFk(result.getNullableOIntegerFkList().get(0));
            result.getMultiRefList().add(multiRef111a);
        }

        {
            MultiRef multiRef111b = new MultiRef();
            multiRef111b.setName("multiRef111b");
            multiRef111b.setOIntegerPk(result.getOIntegerPkList().get(0));
            multiRef111b.setPIntegerPk(result.getPIntegerPkList().get(0));
            multiRef111b.setNullableOIntegerFk(result.getNullableOIntegerFkList().get(0));
            result.getMultiRefList().add(multiRef111b);
        }

        {
            MultiRef multiRef100 = new MultiRef();
            multiRef100.setName("multiRef100");
            multiRef100.setOIntegerPk(result.getOIntegerPkList().get(0));
            result.getMultiRefList().add(multiRef100);
        }

        {
            MultiRef multiRef010 = new MultiRef();
            multiRef010.setName("multiRef010");
            multiRef010.setPIntegerPk(result.getPIntegerPkList().get(0));
            result.getMultiRefList().add(multiRef010);
        }

        {
            MultiRef multiRef001 = new MultiRef();
            multiRef001.setName("multiRef001");
            multiRef001.setNullableOIntegerFk(result.getNullableOIntegerFkList().get(0));
            result.getMultiRefList().add(multiRef001);
        }

        {
            MultiRef multiRef120 = new MultiRef();
            multiRef120.setName("multiRef120");
            multiRef120.setOIntegerPk(result.getOIntegerPkList().get(0));
            multiRef120.setPIntegerPk(result.getPIntegerPkList().get(1));
            result.getMultiRefList().add(multiRef120);
        }

        {
            MultiRef multiRef201 = new MultiRef();
            multiRef201.setName("multiRef201");
            multiRef201.setOIntegerPk(result.getOIntegerPkList().get(1));
            multiRef201.setNullableOIntegerFk(result.getNullableOIntegerFkList().get(0));
            result.getMultiRefList().add(multiRef201);
        }

        {
            MultiRef multiRef012 = new MultiRef();
            multiRef012.setName("multiRef012");
            multiRef012.setPIntegerPk(result.getPIntegerPkList().get(0));
            multiRef012.setNullableOIntegerFk(result.getNullableOIntegerFkList().get(1));
            result.getMultiRefList().add(multiRef012);
        }

        {
            MultiRef multiRef222 = new MultiRef();
            multiRef222.setName("multiRef222");
            multiRef222.setOIntegerPk(result.getOIntegerPkList().get(1));
            multiRef222.setPIntegerPk(result.getPIntegerPkList().get(1));
            multiRef222.setNullableOIntegerFk(result.getNullableOIntegerFkList().get(1));
            result.getMultiRefList().add(multiRef222);
        }
    }

    private static void fillMultiRefSameTables(
            final ForeignKeySchemaData result)
                    throws TorqueException
    {
        {
            MultiRefSameTable multiRefSameTable111a = new MultiRefSameTable();
            multiRefSameTable111a.setName("multiRefSameTable111a");
            multiRefSameTable111a.setOIntegerPkRelatedByReference1(result.getOIntegerPkList().get(0));
            multiRefSameTable111a.setOIntegerPkRelatedByReference2(result.getOIntegerPkList().get(0));
            multiRefSameTable111a.setOIntegerPkRelatedByReference3(result.getOIntegerPkList().get(0));
            result.getMultiRefSameTableList().add(multiRefSameTable111a);
        }

        {
            MultiRefSameTable multiRefSameTable111b = new MultiRefSameTable();
            multiRefSameTable111b.setName("multiRefSameTable111b");
            multiRefSameTable111b.setOIntegerPkRelatedByReference1(result.getOIntegerPkList().get(0));
            multiRefSameTable111b.setOIntegerPkRelatedByReference2(result.getOIntegerPkList().get(0));
            multiRefSameTable111b.setOIntegerPkRelatedByReference3(result.getOIntegerPkList().get(0));
            result.getMultiRefSameTableList().add(multiRefSameTable111b);
        }

        {
            MultiRefSameTable multiRefSameTable100 = new MultiRefSameTable();
            multiRefSameTable100.setName("multiRefSameTable100");
            multiRefSameTable100.setOIntegerPkRelatedByReference1(result.getOIntegerPkList().get(0));
            result.getMultiRefSameTableList().add(multiRefSameTable100);
        }

        {
            MultiRefSameTable multiRefSameTable010 = new MultiRefSameTable();
            multiRefSameTable010.setName("multiRefSameTable010");
            multiRefSameTable010.setOIntegerPkRelatedByReference2(result.getOIntegerPkList().get(0));
            result.getMultiRefSameTableList().add(multiRefSameTable010);
        }

        {
            MultiRefSameTable multiRefSameTable001 = new MultiRefSameTable();
            multiRefSameTable001.setName("multiRefSameTable001");
            multiRefSameTable001.setOIntegerPkRelatedByReference3(result.getOIntegerPkList().get(0));
            result.getMultiRefSameTableList().add(multiRefSameTable001);
        }

        {
            MultiRefSameTable multiRefSameTable120 = new MultiRefSameTable();
            multiRefSameTable120.setName("multiRefSameTable120");
            multiRefSameTable120.setOIntegerPkRelatedByReference1(result.getOIntegerPkList().get(0));
            multiRefSameTable120.setOIntegerPkRelatedByReference2(result.getOIntegerPkList().get(1));
            result.getMultiRefSameTableList().add(multiRefSameTable120);
        }

        {
            MultiRefSameTable multiRefSameTable201 = new MultiRefSameTable();
            multiRefSameTable201.setName("multiRefSameTable201");
            multiRefSameTable201.setOIntegerPkRelatedByReference1(result.getOIntegerPkList().get(1));
            multiRefSameTable201.setOIntegerPkRelatedByReference3(result.getOIntegerPkList().get(0));
            result.getMultiRefSameTableList().add(multiRefSameTable201);
        }

        {
            MultiRefSameTable multiRefSameTable012 = new MultiRefSameTable();
            multiRefSameTable012.setName("multiRefSameTable012");
            multiRefSameTable012.setOIntegerPkRelatedByReference2(result.getOIntegerPkList().get(0));
            multiRefSameTable012.setOIntegerPkRelatedByReference3(result.getOIntegerPkList().get(1));
            result.getMultiRefSameTableList().add(multiRefSameTable012);
        }

        {
            MultiRefSameTable multiRefSameTable222 = new MultiRefSameTable();
            multiRefSameTable222.setName("multiRefSameTable222");
            multiRefSameTable222.setOIntegerPkRelatedByReference1(result.getOIntegerPkList().get(1));
            multiRefSameTable222.setOIntegerPkRelatedByReference2(result.getOIntegerPkList().get(1));
            multiRefSameTable222.setOIntegerPkRelatedByReference3(result.getOIntegerPkList().get(1));
            result.getMultiRefSameTableList().add(multiRefSameTable222);
        }
    }

    public List<PIntegerPk> getPIntegerPkList()
    {
        return pIntegerPkList;
    }

    public List<NullablePIntegerFk> getNullablePIntegerFkList()
    {
        return nullablePIntegerFkList;
    }

    public List<RequiredPIntegerFk> getRequiredPIntegerFkList()
    {
        return requiredPIntegerFkList;
    }


    public List<NonPkPIntegerFk> getNonPkPIntegerFkList()
    {
        return nonPkPIntegerFkList;
    }

    public List<OIntegerPk> getOIntegerPkList()
    {
        return oIntegerPkList;
    }

    public List<NullableOIntegerFk> getNullableOIntegerFkList()
    {
        return nullableOIntegerFkList;
    }

    public List<RequiredOIntegerFk> getRequiredOIntegerFkList()
    {
        return requiredOIntegerFkList;
    }

    public List<NonPkOIntegerFk> getNonPkOIntegerFkList()
    {
        return nonPkOIntegerFkList;
    }

    public List<CompPkOtherFk> getCompositePkOtherFkList()
    {
        return compositePkOtherFkList;
    }

    public List<CompPkContainsFk> getCompositePkContainsFkList()
    {
        return compositePkContainsFkList;
    }

    public List<CompIntegerVarcharPk> getCompositeIntegerVarcharPkList()
    {
        return compositeIntegerVarcharPkList;
    }

    public List<CompIntegerVarcharFk> getCompositeIntegerVarcharFkList()
    {
        return compositeIntegerVarcharFkList;
    }

    public List<CompNonpkFk> getCompositeNonpkFkList()
    {
        return compositeNonpkFkList;
    }

    public List<MultiRef> getMultiRefList()
    {
        return multiRefList;
    }

    public List<MultiRefSameTable> getMultiRefSameTableList()
    {
        return multiRefSameTableList;
    }

    /**
     * Saves all contained data if the data is new or was changed
     * after the last save.
     *
     * @throws TorqueException If saving fails.
     */
    public void save() throws TorqueException
    {
        for (PIntegerPk pIntegerPk : pIntegerPkList)
        {
            pIntegerPk.save();
        }
        for (NullablePIntegerFk nullablePIntegerFk : nullablePIntegerFkList)
        {
            nullablePIntegerFk.save();
        }
        for (OIntegerPk oIntegerPk : oIntegerPkList)
        {
            oIntegerPk.save();
        }
        for (NullableOIntegerFk nullableOIntegerFk : nullableOIntegerFkList)
        {
            nullableOIntegerFk.save();
        }
        for (NonPkOIntegerFk nonPkOIntegerFk : nonPkOIntegerFkList)
        {
            nonPkOIntegerFk.save();
        }
        for (CompIntegerVarcharPk compositeIntegerVarcharPk
                : compositeIntegerVarcharPkList)
        {
            compositeIntegerVarcharPk.save();
        }
        for (CompIntegerVarcharFk compositeIntegerVarcharFk
                : compositeIntegerVarcharFkList)
        {
            compositeIntegerVarcharFk.save();
        }
        for (MultiRef multiRef : multiRefList)
        {
            // refresh ids
            multiRef.setOIntegerPk(multiRef.getOIntegerPk());
            multiRef.setPIntegerPk(multiRef.getPIntegerPk());
            multiRef.setNullableOIntegerFk(multiRef.getNullableOIntegerFk());
            multiRef.save();
        }
        for (MultiRefSameTable multiRefSameTable : multiRefSameTableList)
        {
            // refresh ids
            multiRefSameTable.setOIntegerPkRelatedByReference1(
                    multiRefSameTable.getOIntegerPkRelatedByReference1());
            multiRefSameTable.setOIntegerPkRelatedByReference2(
                    multiRefSameTable.getOIntegerPkRelatedByReference2());
            multiRefSameTable.setOIntegerPkRelatedByReference3(
                    multiRefSameTable.getOIntegerPkRelatedByReference3());
            multiRefSameTable.save();
        }
    }

    /**
     * Deletes all records in the foreign-key-schema's tables.
     *
     * @throws TorqueException if the tables could not be cleaned
     */
    public static void clearTablesInDatabase() throws TorqueException
    {
        MultiRefSameTablePeer.doDelete(new Criteria());
        MultiRefPeer.doDelete(new Criteria());
        NullablePIntegerFkPeer.doDelete(new Criteria());
        RequiredPIntegerFkPeer.doDelete(new Criteria());
        NonPkPIntegerFkPeer.doDelete(new Criteria());
        NullableOIntegerFkPeer.doDelete(new Criteria());
        RequiredOIntegerFkPeer.doDelete(new Criteria());
        NonPkOIntegerFkPeer.doDelete(new Criteria());
        CompPkOtherFkPeer.doDelete(new Criteria());
        CompPkContainsFkPeer.doDelete(new Criteria());

        PIntegerPkPeer.doDelete(new Criteria());
        OIntegerPkPeer.doDelete(new Criteria());

        CompIntegerVarcharFkPeer.doDelete(new Criteria());
        CompNonpkFkPeer.doDelete(new Criteria());
        CompIntegerVarcharPkPeer.doDelete(new Criteria());
    }

    /**
     * Prints all records in the foreign-key-schema's tables to stdout.
     *
     * @throws TorqueException if the tables could not be cleaned
     */
    public static void dumpDatabase() throws TorqueException
    {
        {
            System.out.println("contents of table "
                    + NullablePIntegerFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<NullablePIntegerFk> tableContent
            = NullablePIntegerFkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + RequiredPIntegerFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<RequiredPIntegerFk> tableContent
            = RequiredPIntegerFkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + NonPkPIntegerFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<NonPkPIntegerFk> tableContent
            = NonPkPIntegerFkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + NullableOIntegerFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<NullableOIntegerFk> tableContent
            = NullableOIntegerFkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + RequiredOIntegerFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<RequiredOIntegerFk> tableContent
            = RequiredOIntegerFkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + NonPkOIntegerFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<NonPkOIntegerFk> tableContent
            = NonPkOIntegerFkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + PIntegerPkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<PIntegerPk> tableContent
            = PIntegerPkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + OIntegerPkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<OIntegerPk> tableContent
            = OIntegerPkPeer.doSelect(criteria);
            System.out.println(tableContent);
        }

        {
            System.out.println("contents of table "
                    + CompIntegerVarcharFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<CompIntegerVarcharFk> nullablePIntegerFks
            = CompIntegerVarcharFkPeer.doSelect(criteria);
            System.out.println(nullablePIntegerFks);
        }

        {
            System.out.println("contents of table "
                    + CompNonpkFkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<CompNonpkFk> nullablePIntegerFks
            = CompNonpkFkPeer.doSelect(criteria);
            System.out.println(nullablePIntegerFks);
        }

        {
            System.out.println("contents of table "
                    + CompIntegerVarcharPkPeer.TABLE_NAME);
            Criteria criteria = new Criteria();
            List<CompIntegerVarcharPk> nullablePIntegerFks
            = CompIntegerVarcharPkPeer.doSelect(criteria);
            System.out.println(nullablePIntegerFks);
        }
    }

    /**
     * Checks that the table Non_Pk_O_Integer_Fk in the database
     * contains only the passed objects.
     *
     * @param expected the expected content of the table, not null.
     *
     * * @throws TorqueException if accessing the database fails.
     */
    public static void assertNonPkOIntegerFksInDatabaseEquals(
            final List<NonPkOIntegerFk> expected)
                    throws TorqueException
    {
        Criteria criteria = new Criteria().addSelectColumn(new Count("*"));
        assertEquals(Integer.valueOf(expected.size()),
                NonPkOIntegerFkPeer.doSelectSingleRecord(
                        criteria, new IntegerMapper()));
        for (NonPkOIntegerFk nonPkOIntegerFk : expected)
        {
            criteria = NonPkOIntegerFkPeer.buildCriteria(nonPkOIntegerFk);
            criteria.addSelectColumn(new Count("*"));
            assertEquals(
                    "Expected but not found : " + nonPkOIntegerFk,
                    Integer.valueOf(1),
                    NonPkOIntegerFkPeer.doSelectSingleRecord(
                            criteria, new IntegerMapper()));
        }
    }

    /**
     * Checks that the table Comp_O_Integer_Fk in the database
     * contains only the passed objects.
     *
     * @param expected the expected content of the table, not null.
     *
     * * @throws TorqueException if accessing the database fails.
     */
    public static void assertCompositeIntegerVarcharFksInDatabaseEquals(
            final List<CompIntegerVarcharFk> expected)
                    throws TorqueException
    {
        Criteria criteria = new Criteria().addSelectColumn(new Count("*"));
        assertEquals(Integer.valueOf(expected.size()),
                CompIntegerVarcharFkPeer.doSelectSingleRecord(
                        criteria, new IntegerMapper()));
        for (CompIntegerVarcharFk compIntegerVarcharFk : expected)
        {
            criteria = CompIntegerVarcharFkPeer.buildCriteria(
                    compIntegerVarcharFk);
            criteria.addSelectColumn(new Count("*"));
            assertEquals(
                    "Expected but not found : " + compIntegerVarcharFk,
                    Integer.valueOf(1),
                    CompIntegerVarcharFkPeer.doSelectSingleRecord(
                            criteria, new IntegerMapper()));
        }
    }

    /**
     * Checks that the table Nullable_O_Integer_Fk in the database
     * contains only the passed objects.
     *
     * @param expected the expected content of the table, not null.
     *
     * * @throws TorqueException if accessing the database fails.
     */
    public static void assertNullableOIntegerFksInDatabaseEquals(
            final List<NullableOIntegerFk> expected)
                    throws TorqueException
    {
        Criteria criteria = new Criteria().addSelectColumn(new Count("*"));
        assertEquals(Integer.valueOf(expected.size()),
                NullableOIntegerFkPeer.doSelectSingleRecord(
                        criteria, new IntegerMapper()));
        for (NullableOIntegerFk nullableOIntegerFk : expected)
        {
            criteria = NullableOIntegerFkPeer.buildCriteria(nullableOIntegerFk);
            criteria.addSelectColumn(new Count("*"));
            assertEquals(
                    "Expected but not found : " + nullableOIntegerFk,
                    Integer.valueOf(1),
                    NullableOIntegerFkPeer.doSelectSingleRecord(
                            criteria, new IntegerMapper()));
        }
    }

    /**
     * Checks that the table Comp_Pk_Other_Fk in the database
     * contains only the passed objects.
     *
     * @param expected the expected content of the table, not null.
     *
     * * @throws TorqueException if accessing the database fails.
     */
    public static void assertCompositePkOtherFksInDatabaseEquals(
            final List<CompPkOtherFk> expected)
                    throws TorqueException
    {
        Criteria criteria = new Criteria().addSelectColumn(new Count("*"));
        assertEquals(Integer.valueOf(expected.size()),
                CompPkOtherFkPeer.doSelectSingleRecord(
                        criteria, new IntegerMapper()));
        for (CompPkOtherFk compPkOtherFk : expected)
        {
            criteria = CompPkOtherFkPeer.buildCriteria(compPkOtherFk);
            criteria.addSelectColumn(new Count("*"));
            assertEquals(
                    "Expected but not found : " + compPkOtherFk,
                    Integer.valueOf(1),
                    CompPkOtherFkPeer.doSelectSingleRecord(
                            criteria, new IntegerMapper()));
        }
    }

    /**
     * Checks that the table Comp_Pk_Contains_Fk in the database
     * contains only the passed objects.
     *
     * @param expected the expected content of the table, not null.
     *
     * * @throws TorqueException if accessing the database fails.
     */
    public static void assertCompositePkContainsFksInDatabaseEquals(
            final List<CompPkContainsFk> expected)
                    throws TorqueException
    {
        Criteria criteria = new Criteria().addSelectColumn(new Count("*"));
        assertEquals(Integer.valueOf(expected.size()),
                CompPkContainsFkPeer.doSelectSingleRecord(
                        criteria, new IntegerMapper()));
        for (CompPkContainsFk compPkContainsFk : expected)
        {
            criteria = CompPkContainsFkPeer.buildCriteria(compPkContainsFk);
            criteria.addSelectColumn(new Count("*"));
            assertEquals(
                    "Expected but not found : " + compPkContainsFk,
                    Integer.valueOf(1),
                    CompPkContainsFkPeer.doSelectSingleRecord(
                            criteria, new IntegerMapper()));
        }
    }
}
