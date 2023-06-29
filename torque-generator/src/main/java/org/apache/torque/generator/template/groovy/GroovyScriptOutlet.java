package org.apache.torque.generator.template.groovy;

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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.HashMap;
import java.util.Map;

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.qname.QualifiedName;


/**
 * A Outlet which uses a groovy script for generation.
 */
public class GroovyScriptOutlet extends GroovyOutlet
{
    /** The groovy shell to execute groovy scripts. */
    private static GroovyShell groovyShell = new GroovyShell();

    /** The cached compiled scripts, keyed by the detokenized path. */
    private static Map<String, Script> cachedScripts
        = new HashMap<>();

    /**
     * Constructs a new GroovyScriptOutlet.
     *
     * @param name the name of this outlet, not null.
     * @param configurationProvider the provider for reading the templates,
     *        not null.
     * @param path the path to the templates, not null.
     * @param encoding the encoding of the file, or null if the system's
     *        default encoding should be used.
     *
     * @throws NullPointerException if name, path or directories are null.
     * @throws ConfigurationException if an error occurs while reading the
     *         template.
     */
    public GroovyScriptOutlet(
            final QualifiedName name,
            final ConfigurationProvider configurationProvider,
            final String path,
            final String encoding)
                    throws ConfigurationException
    {
        super(name, configurationProvider, path, encoding);
    }

    @Override
    protected String executeGroovy(
            final Map<String, Object> binding,
            final ControllerState controllerState)
                    throws GeneratorException
    {
        try
        {
            final String detokenizedPath = getDetokenizedPath(controllerState);
            Script script = cachedScripts.get(detokenizedPath);
            if (script == null)
            {
                script = groovyShell.parse(getContent(controllerState));
                cachedScripts.put(detokenizedPath, script);
            }
            final Binding scriptBinding = new Binding(binding);
            script.setBinding(scriptBinding);
            final Object result = script.run();
            if (result == null)
            {
                return "";
            }
            return result.toString();
        }
        catch (final Exception e)
        {
            throw new GeneratorException(
                    "Error executing groovy script " + getName(),
                    e);
        }
    }

}
