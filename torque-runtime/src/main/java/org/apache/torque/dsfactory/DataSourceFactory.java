package org.apache.torque.dsfactory;

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

import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.apache.torque.TorqueException;

/**
 * A factory that returns a DataSource.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @author <a href="mailto:fischer@seitenbau.de">Thomas Fischer</a>
 * @version $Id: DataSourceFactory.java 1867515 2019-09-25 15:02:03Z gk $
 */
public interface DataSourceFactory
{
    /**
     * Key for the configuration which contains DataSourceFactories
     */
    String DSFACTORY_KEY = "dsfactory";

    /**
     *  Key for the configuration which contains the fully qualified name
     *  of the factory implementation class
     */
    String FACTORY_KEY = "factory";

    /**
     * @return the <code>DataSource</code> configured by the factory.
     * @throws TorqueException if the source can't be returned
     */
    DataSource getDataSource() throws TorqueException;

    /**
     * Initialize the factory.
     *
     * @param configuration where to load the factory settings from
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    void initialize(Configuration configuration)
            throws TorqueException;

    /**
     * A hook which is called when the resources of the associated DataSource
     * can be released.
     * After close() is called, the other methods may not work any more
     * (e.g. getDataSource() might return null).
     * It is not guaranteed that this method does anything. For example,
     * we do not want to close connections retrieved via JNDI, so the
     * JndiDataSouurceFactory does not close these connections
     *
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    void close()
            throws TorqueException;
}
