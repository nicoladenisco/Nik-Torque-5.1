package org.apache.torque.avalon;

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

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.commons.lang3.StringUtils;
import org.apache.torque.Database;
import org.apache.torque.TorqueInstance;

/**
 * Avalon component for Torque.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: TorqueComponent.java 1874175 2020-02-18 21:08:40Z tv $
 */
public class TorqueComponent
extends TorqueInstance
implements Torque,
    LogEnabled,
    Configurable,
    Initializable,
    Contextualizable,
    Disposable,
    ThreadSafe
{
    /** The Avalon Application Root */
    private String appRoot = null;

    /** The Avalon Logger */
    private Logger logger = null;

    /** The configuration file name. */
    private String configFile = null;

    /*
     * ========================================================================
     *
     * Avalon Component Interfaces
     *
     * ========================================================================
     */

    /**
     * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    @Override
    public void enableLogging(Logger aLogger)
    {
        this.logger = aLogger;
    }

    /**
     * Convenience method to provide the Avalon logger the way AbstractLogEnabled does.
     * @return Logger instance
     */
    public Logger getLogger()
    {
        return logger;
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    @Override
    public void configure(Configuration configuration)
            throws ConfigurationException
    {
        getLogger().debug("configure(" + configuration + ")");

        String configurationFile
        = configuration.getChild("configfile").getValue();

        if (StringUtils.isNotEmpty(appRoot))
        {
            if (configurationFile.startsWith("/"))
            {
                configurationFile = configurationFile.substring(1);
                getLogger().debug("Config File changes to "
                        + configurationFile);
            }

            Path configurationPath = new File(appRoot).toPath().resolve(configurationFile).normalize();
            getLogger().debug("Config Path normalized  to "
                    + configurationPath);
            configurationFile = configurationPath.toString();
        }

        getLogger().debug("Config File is " + configurationFile);

        this.configFile = configurationFile;
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.context.Contextualizable#contextualize(org.apache.avalon.framework.context.Context)
     */
    @Override
    public void contextualize(Context context)
            throws ContextException
    {
        // check context Merlin and YAAFI style
        try
        {
            appRoot = ((File) context.get("urn:avalon:home")).getAbsolutePath();
        }
        catch (ContextException ce)
        {
            appRoot = null;
        }

        if (appRoot == null)
        {
            // check context old ECM style, let exception flow if not available
            appRoot = (String) context.get("componentAppRoot");
        }

        if (StringUtils.isNotEmpty(appRoot) && appRoot.endsWith("/"))
        {
            appRoot = appRoot.substring(0, appRoot.length() - 1);
            getLogger().debug("Application Root changed to " + appRoot);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    @Override
    public void initialize()
            throws Exception
    {
        getLogger().debug("initialize()");

        TorqueInstance instance = org.apache.torque.Torque.getInstance();

        // Copy the database maps
        Map<String, Database> databases = null;
        if (instance.isInit())
        {
            databases = instance.getDatabases();
            for (Database otherDatabase : databases.values())
            {
                getDatabaseMap(otherDatabase.getName()).copyFrom(
                        otherDatabase.getDatabaseMap());
            }
        }

        // Provide the singleton instance to the static accessor
        org.apache.torque.Torque.setInstance(this);

        init(configFile);

        // start the id brokers
        if (instance.isInit())
        {
            for (Database otherDatabase : databases.values())
            {
                Database database = getDatabase(otherDatabase.getName());
                if (otherDatabase.getIdBroker() != null)
                {
                    database.createAndRegisterIdBroker();
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    @Override
    public void dispose()
    {
        getLogger().debug("dispose()");
        try
        {
            shutdown();
        }
        catch (Exception e)
        {
            getLogger().error("Error while stopping Torque", e);
        }
    }
}
