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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.SAXException;

/**
 * Creates outlet SAX handlers using reflection and a naming convention.
 */
public class ReflectionOutletSaxHandlerFactory
implements UntypedOutletSaxHandlerFactory
{
    /**
     * The suffix for SAX handler class names.
     */
    private static final String SAX_HANDLER_CLASSNAME_SUFFIX
    = "SaxHandler";

    /**
     * The default package for SAX handler class names.
     */
    private static final String DEFAULT_PACKAGE
    = "org.apache.torque.generator.configuration.outlet";

    /** The class log. */
    private static Log log = LogFactory.getLog(
            ReflectionOutletSaxHandlerFactory.class);

    /**
     * The package for the handler class.
     */
    private String saxHandlerPackage = DEFAULT_PACKAGE;

    /**
     * Creates a ReflectionOutletSaxHandlerFactory with the default
     * sax handler package.
     */
    public ReflectionOutletSaxHandlerFactory()
    {
    }

    /**
     * Creates a ReflectionOutletSaxHandlerFactory with the default
     * sax handler package.
     *
     * @param saxHandlerPackage the package where the Sax handlers reside.
     */
    public ReflectionOutletSaxHandlerFactory(String saxHandlerPackage)
    {
        this.saxHandlerPackage = saxHandlerPackage;
    }

    /**
     * Returns the outlet type which can be handled by the
     * OutletSaxHandlers created by this factory.
     *
     * @return null.
     */
    public String getType()
    {
        return null;
    }

    /**
     * Returns a OutletSaxHandler for reading the configuration of
     * the outlet. This implementation uses the provided name
     * as outlet name.
     *
     * @param outletType the type of the outlet, not null.
     * @param outletName the name for the outlet which configuration
     *        will be read in by the generated SaxHandlerFactory,
     *        or null if the name of the outlet should be determined from
     *        the parsed XML.
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @return a new VelocityOutletSaxHandler.
     */
    @Override
    public final OutletSaxHandler getOutletSaxHandler(
            String outletType,
            QualifiedName outletName,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
                    throws SAXException
    {
        String fullyQualifiedSaxHandlerName
        = getFullyQualifiedHandlerClassName(outletType);

        Class<?> handlerClass;
        try
        {
            ClassLoader classLoader = unitDescriptor.getClassLoader();
            if (classLoader == null)
            {
                classLoader = getClass().getClassLoader();
            }
            handlerClass = Class.forName(
                    fullyQualifiedSaxHandlerName, true, classLoader);
        }
        catch (ClassNotFoundException e)
        {
            // should not happen, because otherwise canHandle() would not
            // have returned true
            throw new RuntimeException(e);
        }

        Constructor<?> constructor;
        try
        {
            constructor = handlerClass.getConstructor(
                    QualifiedName.class,
                    ConfigurationProvider.class,
                    UnitDescriptor.class,
                    ConfigurationHandlers.class);
        }
        catch (NoSuchMethodException e)
        {
            throw new SAXException("Class " + handlerClass.getName()
            + " has no constructor for types "
            + QualifiedName.class.getName() + ","
            + ConfigurationProvider.class.getName() + ","
            + UnitDescriptor.class.getName() + ","
            + ConfigurationHandlers.class.getName());
        }

        OutletSaxHandler outletSaxHandler;
        try
        {
            outletSaxHandler = (OutletSaxHandler) constructor.newInstance(
                    outletName,
                    configurationProvider,
                    unitDescriptor,
                    configurationHandlers);
        }
        catch (IllegalArgumentException e)
        {
            // should not happen, we have checked arguments before
            throw new RuntimeException(e);
        }
        catch (InstantiationException e)
        {
            throw new SAXException("The class "
                    + handlerClass.getName() + " is abstract",
                    e);
        }
        catch (IllegalAccessException e)
        {
            throw new SAXException("Constructor of class "
                    + handlerClass.getName() + " is inaccessible",
                    e);
        }
        catch (InvocationTargetException e)
        {
            throw new SAXException("Constructor of class "
                    + handlerClass.getName() + " has thrown an exception",
                    e);
        }
        return outletSaxHandler;
    }

    @Override
    public boolean canHandle(String type, UnitDescriptor unitDescriptor)
    {
        String fullyQualifiedSaxHandlerName
        = getFullyQualifiedHandlerClassName(type);
        log.debug("canHandle: Using class name "
                + fullyQualifiedSaxHandlerName);

        Class<?> handlerClass;
        try
        {
            ClassLoader classLoader = unitDescriptor.getClassLoader();
            if (classLoader == null)
            {
                classLoader = getClass().getClassLoader();
            }
            handlerClass = Class.forName(
                    fullyQualifiedSaxHandlerName, true, classLoader);
        }
        catch (ClassNotFoundException e)
        {
            log.debug("canHandle: class does not exist, returning false");
            return false;
        }
        if (!OutletSaxHandler.class.isAssignableFrom(handlerClass))
        {
            log.debug("canHandle: class is no OutletSaxHandler"
                    + "returning false");
            return false;
        }
        return true;
    }

    /**
     * Returns the fully qualified class name of the handler class
     * for a given type.
     *
     * @param type the type to determine the handler for.
     *
     * @return the fully qualified class name, not null.
     */
    protected String getFullyQualifiedHandlerClassName(String type)
    {
        String saxHandlerClassName = StringUtils.capitalize(type)
                + SAX_HANDLER_CLASSNAME_SUFFIX;
        String fullyQualifiedSaxHandlerName = saxHandlerPackage + "."
                + saxHandlerClassName;
        return fullyQualifiedSaxHandlerName;
    }
}
