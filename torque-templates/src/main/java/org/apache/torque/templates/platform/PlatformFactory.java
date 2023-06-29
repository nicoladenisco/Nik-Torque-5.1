package org.apache.torque.templates.platform;

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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory class responsible to create Platform objects that
 * define RDBMS platform specific behaviour.
 *
 * @author Thomas Mahler
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: PlatformFactory.java 1855923 2019-03-20 16:19:39Z gk $
 */
public final class PlatformFactory
{
    /** All known platforms. */
    private static HashMap<String, Platform> platforms
        = new HashMap<>();

    /** The class log. */
    private static Log log = LogFactory.getLog(PlatformFactory.class);

    /**
     * private constructor for utility class.
     */
    private PlatformFactory()
    {
    }

    /**
     * Returns the Platform for a platform name.
     *
     * @param dbms name of the platform
     *
     * @return the Platform, not null.
     */
    public static Platform getPlatformFor(String dbms)
    {
        Platform result = null;
        String platformName = null;

        result = getPlatforms().get(dbms);
        if (result == null)
        {
            try
            {
                platformName = getClassnameFor(dbms);
                Class<?> platformClass = Class.forName(platformName);
                result = (Platform) platformClass.newInstance();
            }
            catch (Throwable t)
            {
                log.warn("problems with platform " + platformName
                        + ": " + t.getMessage());
                log.warn("Torque will use PlatformDefaultImpl instead");

                result = new PlatformDefaultImpl();
            }
            getPlatforms().put(dbms, result); // cache the Platform
        }
        if (log.isDebugEnabled()) {
            log.debug("using platform " + result);
        }
        return result;
    }

    /**
     * compute the name of the concrete Class representing the Platform
     * specified by <code>platform</code>
     *
     * @param platform the name of the platform as specified in the repository
     */
    private static String getClassnameFor(String platform)
    {
        String pf = "Default";
        if (platform != null)
        {
            pf = platform;
        }
        String myClassname = PlatformFactory.class.getName();
        String myPackage
        = myClassname.substring(0, myClassname.lastIndexOf("."));
        return myPackage
                + ".Platform"
                + pf.substring(0, 1).toUpperCase()
                + pf.substring(1)
                + "Impl";
    }

    /**
     * Gets the platforms.
     *
     * @return Returns a HashMap
     */
    private static HashMap<String, Platform> getPlatforms()
    {
        return platforms;
    }
}
