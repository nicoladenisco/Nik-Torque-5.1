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

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.PerUserPoolDataSource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.TorqueException;

/**
 * A factory that looks up the DataSource using the JDBC2 pool methods.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: PerUserPoolDataSourceFactory.java 1870542 2019-11-28 09:32:40Z tv $
 */
@Deprecated
public class PerUserPoolDataSourceFactory
extends AbstractDataSourceFactory
{

    /** The log. */
    private static final Logger log = LogManager.getLogger(PerUserPoolDataSourceFactory.class);

    /** The wrapped <code>DataSource</code>. */
    private PerUserPoolDataSource ds = null;

    /**
     * @see org.apache.torque.dsfactory.DataSourceFactory#getDataSource
     */
    @Override
    public DataSource getDataSource()
    {
        return ds;
    }

    /**
     * @see org.apache.torque.dsfactory.DataSourceFactory#initialize
     */
    @Override
    public void initialize(final Configuration configuration) throws TorqueException
    {
        ConnectionPoolDataSource cpds = new DriverAdapterCPDS();
        initCPDS(configuration, cpds);
        PerUserPoolDataSource dataSource = new PerUserPoolDataSource();
        initJdbc2Pool(dataSource, configuration);
        dataSource.setConnectionPoolDataSource(cpds);
        this.ds = dataSource;
    }

    /**
     * Closes the pool associated with this factory and releases it.
     * @throws TorqueException if the pool cannot be closed properly
     */
    @Override
    public void close() throws TorqueException
    {
        try
        {
            ds.close();
        }
        catch (Exception e)
        {
            log.error("Exception caught during close()", e);
            throw new TorqueException(e);
        }
        ds = null;
    }

}
