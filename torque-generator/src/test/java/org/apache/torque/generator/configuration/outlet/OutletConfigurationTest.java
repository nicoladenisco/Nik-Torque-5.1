package org.apache.torque.generator.configuration.outlet;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.UnitDescriptor.Packaging;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.control.action.ApplyAction;
import org.apache.torque.generator.java.JavaOutlet;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.outlet.java.PackageToPathOutlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.junit.Test;

public class OutletConfigurationTest
{
    /** Unit descriptor for the tests. */
    private UnitDescriptor unitDescriptor = new UnitDescriptor(
            Packaging.JAR,
            new Maven2DirectoryProjectPaths(new File(".")),
            new DefaultTorqueGeneratorPaths());

    /**
     * Tests that getOutlets returns the correct result.
     *
     * @throws ConfigurationException if an error occurs.
     */
    @Test
    public void testGetOutlets() throws ConfigurationException
    {
        List<Outlet> outlets = new ArrayList<>();
        outlets.add(new JavaOutlet(
                new QualifiedName("test.outlet.0")));
        outlets.add(new PackageToPathOutlet(
                new QualifiedName("test.outlet.1")));
        List<MergepointMapping> mergepointMappings
            = new ArrayList<>();

        OutletConfiguration outletConfiguration = new OutletConfiguration(
                outlets,
                mergepointMappings,
                unitDescriptor);
        outletConfiguration.resolveMergepointMappings();

        assertEquals(2, outletConfiguration.getOutlets().size());
        assertEquals(
                outlets.get(0),
                outletConfiguration.getOutlets().get(
                        new QualifiedName("test.outlet.0")));
        assertEquals(
                outlets.get(1),
                outletConfiguration.getOutlets().get(
                        new QualifiedName("test.outlet.1")));
    }

    /**
     * Tests that one cannot add a outlet with the same name twice
     *
     * @throws ConfigurationException if OutletConfiguration cannot be
     *         created,
     */
    @Test(expected = ConfigurationException.class)
    public void testConstructorWithSameOutletName()
            throws ConfigurationException
    {
        List<Outlet> outlets = new ArrayList<>();
        outlets.add(new JavaOutlet(
                new QualifiedName("sameName")));
        outlets.add(new PackageToPathOutlet(
                new QualifiedName("sameName")));
        List<MergepointMapping> mergepointMappings
            = new ArrayList<>();

        new OutletConfiguration(outlets, mergepointMappings, unitDescriptor);
    }

    /**
     * Checks that resolveMergepointMappings adds the mergepoint
     * to the correct outlet.
     */
    @Test
    public void testResolveMergepointMappings() throws ConfigurationException
    {
        List<Outlet> outlets = new ArrayList<>();
        outlets.add(new JavaOutlet(
                new QualifiedName("test.outlet")));
        outlets.add(new PackageToPathOutlet(
                new QualifiedName("test.outlet.2")));
        List<MergepointMapping> mergepointMappings
            = new ArrayList<>();
        mergepointMappings.add(
                new MergepointMapping("test.outlet.testMergepoint"));
        mergepointMappings.get(0).addAction(
                new ApplyAction(".", "test.outlet.2", false));

        OutletConfiguration outletConfiguration = new OutletConfiguration(
                outlets,
                mergepointMappings,
                unitDescriptor);
        outletConfiguration.resolveMergepointMappings();

        Outlet outlet = outletConfiguration.getOutlet(
                new QualifiedName("test.outlet"));
        assertEquals(1, outlet.getMergepointMappings().size());
        assertEquals(
                1,
                outlet.getMergepointMapping("testMergepoint")
                .getActions().size());
    }

    /**
     * Checks that resolveMergepointMappings throws an error if the
     * outlet name cannot be resolved.
     */
    @Test(expected = ConfigurationException.class)
    public void testResolveMergepointMappingsNotExistingOutlet()
            throws ConfigurationException
    {
        List<Outlet> outlets = new ArrayList<>();
        outlets.add(new JavaOutlet(
                new QualifiedName("test.outlet")));
        List<MergepointMapping> mergepointMappings
            = new ArrayList<>();
        mergepointMappings.add(
                new MergepointMapping("not.existing.outlet.testMergepoint"));

        OutletConfiguration outletConfiguration = new OutletConfiguration(
                outlets,
                mergepointMappings,
                unitDescriptor);
        outletConfiguration.resolveMergepointMappings();
    }

    /**
     * Checks that resolveMergepointMappings throws an error if a
     * mergepoint does not contain a namespace.
     */
    @Test(expected = ConfigurationException.class)
    public void testResolveMergepointMappingsNoNamespace()
            throws ConfigurationException
    {
        List<Outlet> outlets = new ArrayList<>();
        outlets.add(new JavaOutlet(
                new QualifiedName("test.outlet")));
        List<MergepointMapping> mergepointMappings
            = new ArrayList<>();
        mergepointMappings.add(
                new MergepointMapping("testMergepoint"));

        OutletConfiguration outletConfiguration = new OutletConfiguration(
                outlets,
                mergepointMappings,
                unitDescriptor);
        outletConfiguration.resolveMergepointMappings();
    }
}
