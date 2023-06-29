package org.apache.torque.generator.source.jdbc;

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

import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.Source;
import org.apache.torque.generator.source.SourceProvider;

/**
 * Creates a source from JDBC metadata.
 *
 * @version $Id: JdbcMetadataSourceProvider.java 1850967 2019-01-10 17:54:13Z painter $
 */
public class JdbcMetadataSourceProvider extends SourceProvider
{
    /** The name of the option to retrieve the JDBC URL from. */
    private String urlOption;

    /** The name of the option to retrieve the JDBC driver from. */
    private String driverOption;

    /** The name of the option to retrieve the JDBC user name from. */
    private String usernameOption;

    /** The name of the option to retrieve the JDBC password from. */
    private String passwordOption;

    /** The name of the option to retrieve the JDBC schema from. */
    private String schemaOption;

    /** JDBC URL. */
    private String url;

    /** JDBC driver. */
    private String driver;

    /** JDBC user name. */
    private String username;

    /** JDBC password. */
    private String password;

    /** JDBC schema. */
    private String schema;

    /** Whether next() was already called. */
    private boolean nextCalled = false;

    public JdbcMetadataSourceProvider(
            String urlOption,
            String driverOption,
            String usernameOption,
            String passwordOption,
            String schemaOption)
                    throws ConfigurationException
    {
        if (urlOption == null)
        {
            throw new ConfigurationException(
                    "JdbcMetadataSourceProvider: urlOption must not be null");
        }
        if (driverOption == null)
        {
            throw new ConfigurationException(
                    "JdbcMetadataSourceProvider:"
                            + " driverOption must not be null");
        }
        this.urlOption = urlOption;
        this.driverOption = driverOption;
        this.usernameOption = usernameOption;
        this.passwordOption = passwordOption;
        this.schemaOption = schemaOption;
    }


    /* (non-Javadoc)
     * @see org.apache.torque.generator.source.SourceProvider#initInternal(org.apache.torque.generator.configuration.ConfigurationHandlers, org.apache.torque.generator.control.ControllerState)
     * 
     * Generates an XML database schema from JDBC metadata.
     */
    @Override
    public void initInternal(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
                    throws ConfigurationException
    {
        driver = controllerState.getStringOption(driverOption);
        url = controllerState.getStringOption(urlOption);
        if (usernameOption != null)
        {
            username = controllerState.getStringOption(usernameOption);
        }
        if (passwordOption != null)
        {
            password = controllerState.getStringOption(passwordOption);
        }
        if (schemaOption != null)
        {
            schema = controllerState.getStringOption(schemaOption);
        }
    }

    @Override
    public boolean hasNext()
    {
        return !nextCalled;
    }

    @Override
    public Source next()
    {
        if (nextCalled)
        {
            throw new IndexOutOfBoundsException();
        }
        nextCalled = true;
        return new JdbcMetadataSource(driver, url, username, password, schema);
    }


    @Override
    protected void resetInternal(
            ConfigurationHandlers configurationHandlers,
            ControllerState controllerState)
                    throws ConfigurationException
    {
        driver = null;
        url = null;
        username = null;
        password = null;
        schema = null;
        nextCalled = false;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }


    public String getUrlOption()
    {
        return urlOption;
    }

    public String getDriverOption()
    {
        return driverOption;
    }

    public String getUsernameOption()
    {
        return usernameOption;
    }

    public String getPasswordOption()
    {
        return passwordOption;
    }

    public String getSchemaOption()
    {
        return schemaOption;
    }

    public String getUrl()
    {
        return url;
    }

    public String getDriver()
    {
        return driver;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getSchema()
    {
        return schema;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceProvider copy() throws ConfigurationException
    {
        JdbcMetadataSourceProvider result = new JdbcMetadataSourceProvider(
                urlOption,
                driverOption,
                usernameOption,
                passwordOption,
                schemaOption);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copyNotSetSettingsFrom(SourceProvider sourceProvider)
    {
        // do nothing.
    }

}
