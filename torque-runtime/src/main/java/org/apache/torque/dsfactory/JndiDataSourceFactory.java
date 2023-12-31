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

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.torque.TorqueException;

/**
 * A factory that looks up the DataSource from JNDI.  It is also able
 * to deploy the DataSource based on properties found in the
 * configuration.
 *
 * This factory tries to avoid excessive context lookups to improve speed.
 * The time between two lookups can be configured. The default is 0 (no cache).
 *
 * @author <a href="mailto:jmcnally@apache.org">John McNally</a>
 * @author <a href="mailto:thomas@vandahl.org">Thomas Vandahl</a>
 * @version $Id: JndiDataSourceFactory.java 1870542 2019-11-28 09:32:40Z tv $
 */
public class JndiDataSourceFactory
extends AbstractDataSourceFactory
{
    /**
     * Key for the configuration which contains jndi properties.
     */
    public static final String JNDI_KEY = "jndi";

    /**
     *  Key for the configuration property which contains the jndi path.
     */
    public static final String PATH_KEY = "path";

    /**
     *  Key for the configuration property which contains the
     *  time between two jndi lookups.
     */
    public static final String TIME_BETWEEN_LOOKUPS_KEY = "ttl";

    /**
     * Key for the configuration which contains properties for a DataSource
     * which should be bound into jndi.
     */
    public static final String DATASOURCE_KEY = "datasource";

    /**
     *  Key for the configuration property which contains the class name
     *  of the datasource to be bound into jndi.
     */
    public static final String CLASSNAME_KEY = "classname";

    /** The log. */
    private static final Logger log = LogManager.getLogger(JndiDataSourceFactory.class);

    /** The path to get the resource from. */
    private String path;
    /** The context to get the resource from. */
    private Context ctx;

    /** A locally cached copy of the DataSource */
    private DataSource ds = null;

    /** Time of last actual lookup action */
    private long lastLookup = 0;

    /** Time between two lookups */
    private long ttl = 0; // ms

    /**
     * @see org.apache.torque.dsfactory.DataSourceFactory#getDataSource
     */
    @Override
    public DataSource getDataSource() throws TorqueException
    {
        long time = System.currentTimeMillis();

        if (ds == null || time - lastLookup > ttl)
        {
            try
            {
                synchronized (ctx)
                {
                    ds = ((DataSource) ctx.lookup(path));
                }
                lastLookup = time;
            }
            catch (Exception e)
            {
                throw new TorqueException(e);
            }
        }

        return ds;
    }

    /**
     * @see org.apache.torque.dsfactory.DataSourceFactory#initialize
     */
    @Override
    public void initialize(Configuration configuration) throws TorqueException
    {
        initJNDI(configuration);
        initDataSource(configuration);
    }

    /**
     * Initializes JNDI.
     *
     * @param configuration where to read the settings from
     * @throws TorqueException if a property set fails
     */
    private void initJNDI(Configuration configuration) throws TorqueException
    {
        log.debug("Starting initJNDI");

        Configuration c = configuration.subset(JNDI_KEY);
        if (c == null || c.isEmpty())
        {
            throw new TorqueException(
                    "JndiDataSourceFactory requires a jndi "
                            + "path property to lookup the DataSource in JNDI.");
        }

        try
        {
            Hashtable<String, Object> env = new Hashtable<>();
            for (Iterator<?> i = c.getKeys(); i.hasNext();)
            {
                String key = (String) i.next();
                if (key.equals(PATH_KEY))
                {
                    path = c.getString(key);
                    log.debug("JNDI path: {}", path);
                }
                else if (key.equals(TIME_BETWEEN_LOOKUPS_KEY))
                {
                    ttl = c.getLong(key, ttl);
                    log.debug("Time between context lookups: {} ms", ttl);
                }
                else
                {
                    String value = c.getString(key);
                    env.put(key, value);
                    log.debug("Set jndi property: {} = {}", key, value);
                }
            }

            ctx = new InitialContext(env);
            log.debug("Created new InitialContext");
            debugCtx(ctx);
        }
        catch (Exception e)
        {
            log.error("", e);
            throw new TorqueException(e);
        }
    }

    /**
     * Initializes the DataSource.
     *
     * @param configuration where to read the settings from
     * @throws TorqueException if a property set fails
     */
    private void initDataSource(Configuration configuration)
            throws TorqueException
    {
        log.debug("Starting initDataSource");
        try
        {
            Object dataSource = null;

            Configuration c = configuration.subset(DATASOURCE_KEY);
            if (c != null)
            {
                for (Iterator<?> i = c.getKeys(); i.hasNext();)
                {
                    String key = (String) i.next();
                    if (key.equals(CLASSNAME_KEY))
                    {
                        String classname = c.getString(key);
                        log.debug("Datasource class: {}", classname);

                        Class<?> dsClass = Class.forName(classname);
                        dataSource = dsClass.newInstance();
                    }
                    else
                    {
                        if (dataSource != null)
                        {
                            log.debug("Setting datasource property: {}", key);
                            setProperty(key, c, dataSource);
                        }
                        else
                        {
                            log.error("Tried to set property {} without Datasource definition!", key);
                        }
                    }
                }
            }

            if (dataSource != null)
            {
                synchronized (ctx)
                {
                    bindDStoJndi(ctx, path, dataSource);
                }
            }
        }
        catch (Exception e)
        {
            log.error("", e);
            throw new TorqueException(e);
        }
    }

    /**
     * Does nothing. We do not want to close a dataSource retrieved from Jndi,
     * because other applications might use it as well.
     */
    @Override
    public void close()
    {
        // do nothing
    }

    /**
     *
     * @param ctx the context
     * @throws NamingException
     */
    private void debugCtx(Context ctx) throws NamingException
    {
        log.debug("InitialContext -------------------------------");
        Map<?, ?> env = ctx.getEnvironment();
        log.debug("Environment properties: {}", env.size());
        env.forEach((key, value) -> log.debug("    {}: {}", key, value));
        log.debug("----------------------------------------------");
    }

    /**
     *
     * @param ctx
     * @param path
     * @param ds
     * @throws Exception
     */
    private void bindDStoJndi(Context ctx, String path, Object ds)
            throws Exception
    {
        debugCtx(ctx);

        // add subcontexts, if not added already
        int start = path.indexOf(':') + 1;
        if (start > 0)
        {
            path = path.substring(start);
        }
        StringTokenizer st = new StringTokenizer(path, "/");
        while (st.hasMoreTokens())
        {
            String subctx = st.nextToken();
            if (st.hasMoreTokens())
            {
                try
                {
                    ctx.createSubcontext(subctx);
                    log.debug("Added sub context: {}", subctx);
                }
                catch (NameAlreadyBoundException nabe)
                {
                    // ignore
                    log.debug("Sub context {} already exists", subctx);
                }
                catch (NamingException ne)
                {
                    log.debug("Naming exception caught when creating subcontext {}",
                            subctx,
                            ne);
                    // even though there is a specific exception
                    // for this condition, some implementations
                    // throw the more general one.
                    /*
                     *                      if (ne.getMessage().indexOf("already bound") == -1 )
                     *                      {
                     *                      throw ne;
                     *                      }
                     */
                    // ignore
                }
                ctx = (Context) ctx.lookup(subctx);
            }
            else
            {
                // not really a subctx, it is the ds name
                ctx.bind(subctx, ds);
            }
        }
    }
}
