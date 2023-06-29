package org.apache.torque.generator.configuration;

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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A resolver to get the confoguration.xsd file for the XML parser from the jar.
 *
 * @version $Id: ConfigurationEntityResolver.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class ConfigurationEntityResolver implements EntityResolver
{
    /** The SystemId of the configuration schema. */
    public static final String CONFIGURATION_SCHEMA_SYSTEMID
    = XMLConstants.GENERATOR_CONFIGURATION_NAMESPACE + ".xsd";

    /** The SystemId of the configuration schema. */
    public static final String OUTLET_SCHEMA_SYSTEMID
    = "http://db.apache.org/torque/4.0/generator/outlet.xsd";

    /** Logging class from commons.logging */
    private static Log log
    = LogFactory.getLog(ConfigurationEntityResolver.class);

    /** The class object of this class. */
    private static final Class<ConfigurationEntityResolver> CLAZZ
    = ConfigurationEntityResolver.class;

    /**
     * An implementation of the SAX <code>EntityResolver</code>
     * interface to be called by the XML parser.  If the URI is known,
     * the corresponding resource from the jar is returned.
     * In all other cases, null is returned to indicate that the parser
     * should open a regular connection to the systemId URI.
     *
     * @param publicId The public identifier of the external entity
     * @param systemId The system identifier of the external entity
     * @return An <code>InputSource</code> for the entity if the uri is known,
     *         or null otherwise.
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws IOException, SAXException
    {
        if (CONFIGURATION_SCHEMA_SYSTEMID.equals(systemId))
        {
            return readFromClasspath("configuration.xsd");
        }
        else if (OUTLET_SCHEMA_SYSTEMID.equals(systemId))
        {
            return readFromClasspath("outlet.xsd");
        }
        else
        {
            log.debug("Resolver: used default behaviour");
            return null;
        }
    }

    /**
     * Reads the resource with the given name from the classpath.
     *
     * @param resourceName the name of the resource to read
     * @return an <code>InputSource</code> with the content of the resource.
     *
     * @throws SAXException if the resource cannot be read.
     */
    private InputSource readFromClasspath(String resourceName)
            throws SAXException
    {
        try
        {
            InputStream inputStream
            = CLAZZ.getResourceAsStream(resourceName);

            // getResource was buggy on many systems including Linux,
            // OSX, and some versions of windows in jdk1.3.
            // getResourceAsStream works on linux, maybe others?
            if (inputStream != null)
            {
                String pkg = CLAZZ.getName().substring(0,
                        CLAZZ.getName().lastIndexOf('.'));
                log.debug("Resolver: used " + resourceName + " from '"
                        + pkg + "' package");
                return new InputSource(inputStream);
            }
            else
            {
                log.warn("Could not locate resource");
                return null;
            }
        }
        catch (Exception ex)
        {
            throw new SAXException(
                    "Could not get stream for " + resourceName,
                    ex);
        }
    }
}
