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

import java.util.Iterator;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.TorqueRuntimeException;

/**
 * A class that contains common functionality of the factories in this
 * package.
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id: AbstractDataSourceFactory.java 1870542 2019-11-28 09:32:40Z tv $
 */
public abstract class AbstractDataSourceFactory
implements DataSourceFactory
{
    /** "pool" Key for the configuration */
    public static final String POOL_KEY = "pool";

    /** "connection" Key for the configuration */
    public static final String CONNECTION_KEY = "connection";

    /** "defaults" Key for the configuration */
    public static final String DEFAULTS_KEY = "defaults";

    /** "defaults.pool" Key for the configuration */
    public static final String DEFAULT_POOL_KEY
    = DEFAULTS_KEY + "." + POOL_KEY;

    /** "defaults.connection" Key for the configuration */
    public static final String DEFAULT_CONNECTION_KEY
    = DEFAULTS_KEY + "." + CONNECTION_KEY;

    /** The log */
    private static final Logger log = LogManager.getLogger(AbstractDataSourceFactory.class);

    /**
     * Encapsulates setting configuration properties on
     * <code>DataSource</code> objects.
     *
     * @param property the property to read from the configuration
     * @param c the configuration to read the property from
     * @param ds the <code>DataSource</code> instance to write the property to
     * @throws Exception if anything goes wrong
     */
    protected void setProperty(String property, final Configuration c, final Object ds)
            throws Exception
    {
        if (c == null || c.isEmpty())
        {
            return;
        }

        String key = property;
        Class<?> dsClass = ds.getClass();
        int dot = property.indexOf('.');
        try
        {
            if (dot > 0)
            {
                property = property.substring(0, dot);

                MappedPropertyDescriptor mappedPD =
                        new MappedPropertyDescriptor(property, dsClass);
                Class<?> propertyType = mappedPD.getMappedPropertyType();
                Configuration subProps = c.subset(property);
                // use reflection to set properties
                Iterator<?> j = subProps.getKeys();
                while (j.hasNext())
                {
                    String subProp = (String) j.next();
                    String propVal = subProps.getString(subProp);
                    Object value = ConvertUtils.convert(propVal, propertyType);
                    PropertyUtils.setMappedProperty(ds, property, subProp, value);

                    log.debug("setMappedProperty({}, {}, {}, {})", ds, property, subProp, value);
                }
            }
            else
            {
                if ("password".equals(key))
                {
                    // do not log value of password
                    // for this, ConvertUtils.convert cannot be used
                    // as it also logs the value of the converted property
                    // so it is assumed here that the password is a String
                    String value = c.getString(property);
                    PropertyUtils.setSimpleProperty(ds, property, value);

                    log.debug("setSimpleProperty({}, {}, (value not logged))", ds, property);
                }
                else
                {
                    Class<?> propertyType =
                            PropertyUtils.getPropertyType(ds, property);
                    Object value =
                            ConvertUtils.convert(c.getString(property), propertyType);
                    PropertyUtils.setSimpleProperty(ds, property, value);

                    log.debug("setSimpleProperty({}, {}, {})", ds, property, value);
                }
            }
        }
        catch (RuntimeException e)
        {
            throw new TorqueRuntimeException(
                    "Runtime error setting property " + property, e);
        }
        catch (Exception e)
        {
            log.error("Property: {}  value: {}  is not supported by DataSource: {}",
                    property, c.getString(key), ds.getClass().getName());
        }
    }

    /**
     * Iterate over a Configuration subset and apply all
     * properties to a passed object which must contain Bean
     * setter and getter
     *
     * @param c The configuration subset
     * @param o The object to apply the properties to
     * @throws TorqueException if a property set fails
     */
    protected void applyConfiguration(final Configuration c, final Object o)
            throws TorqueException
    {
        log.debug("applyConfiguration({}, {})", c, o);

        if (c != null)
        {
            try
            {
                for (Iterator<?> i = c.getKeys(); i.hasNext();)
                {
                    String key = (String) i.next();
                    setProperty(key, c, o);
                }
            }
            catch (Exception e)
            {
                log.error(e);
                throw new TorqueException(e);
            }
        }
    }

    /**
     * Initializes the ConnectionPoolDataSource.
     *
     * @param configuration where to read the settings from
     * @param cpds data source to configure
     * @throws TorqueException if a property set fails
     */
    protected void initCPDS(final Configuration configuration, final ConnectionPoolDataSource cpds)
            throws TorqueException
    {
        log.debug("Starting initCPDS");
        Configuration c = Torque.getConfiguration();

        if (c == null || c.isEmpty())
        {
            log.warn("Global Configuration not set, no Default connection pool data source configured!");
        }
        else
        {
            Configuration conf = c.subset(DEFAULT_CONNECTION_KEY);
            applyConfiguration(conf, cpds);
        }

        Configuration conf = configuration.subset(CONNECTION_KEY);
        applyConfiguration(conf, cpds);
    }

    /**
     * Initializes the Jdbc2PoolDataSource.
     *
     * @param dataSource the dataSource to initialize, not null.
     * @param configuration where to read the settings from, not null.
     *
     * @throws TorqueException if a property set fails.
     */
    protected void initJdbc2Pool(
            final DataSource dataSource,
            final Configuration configuration)
                    throws TorqueException
    {
        log.debug("Starting initJdbc2Pool");
        Configuration c = Torque.getConfiguration();

        if (c == null || c.isEmpty())
        {
            log.warn("Global Configuration not set, no Default pool data source configured!");
        }
        else
        {
            Configuration conf = c.subset(DEFAULT_POOL_KEY);
            applyConfiguration(conf, dataSource);
        }

        Configuration conf = configuration.subset(POOL_KEY);
        applyConfiguration(conf, dataSource);
    }


    /**
     * @return the <code>DataSource</code> configured by the factory.
     * @throws TorqueException if the source can't be returned
     */
    @Override
    public abstract DataSource getDataSource()
            throws TorqueException;

    /**
     * Initialize the factory.
     *
     * @param configuration where to load the factory settings from
     * @throws TorqueException Any exceptions caught during processing will be
     *         rethrown wrapped into a TorqueException.
     */
    @Override
    public abstract void initialize(Configuration configuration)
            throws TorqueException;
}
