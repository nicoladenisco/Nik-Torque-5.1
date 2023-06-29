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

import static org.apache.torque.generator.configuration.mergepoint.MergepointConfigurationTags.MERGEPOINT_TAG;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.INPUT_TAG;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_CLASS_ATTRIBUTE;
import static org.apache.torque.generator.configuration.outlet.OutletConfigurationTags.OUTLET_NAME_ATTRIBUTE;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationHandlers;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.QualifiedName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Handles a declaration of a java outlet within a outlet configuration
 * file.
 */
class JavaOutletSaxHandler extends OutletSaxHandler
{
    /** The logger of the class. */
    private static Log log = LogFactory.getLog(JavaOutletSaxHandler.class);

    /** the name of the property which is currently processed. */
    private String propertyName;

    /** The value of the property which is currently processed. */
    private StringBuffer propertyValue;

    /** The current nesting level inside the processed element. */
    private int level = 0;

    /**
     * Constructor.
     *
     * @param outletName the name for the outlet which configuration
     *        will be read in by the generated SaxHandlerFactory,
     *        or null if the name of the outlet should be determined from
     *        the parsed xml.
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param configurationHandlers the available configuration handlers,
     *        not null.
     *
     * @throws SAXException if an error occurs during creation of the outlet.
     */
    public JavaOutletSaxHandler(
            QualifiedName outletName,
            ConfigurationProvider configurationProvider,
            UnitDescriptor unitDescriptor,
            ConfigurationHandlers configurationHandlers)
                    throws SAXException
    {
        super(outletName,
                configurationProvider,
                unitDescriptor,
                configurationHandlers);
    }

    /**
     * Instantiates a java outlet.
     *
     * @param outletName the name for the outlet which configuration
     *        will be read in by the generated SaxHandlerFactory,
     *        or null if the name of the outlet should be determined from
     *        the parsed xml.
     * @param uri - The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace processing is not
     *        being performed.
     * @param localName - The local name (without prefix), or
     *        the empty string if Namespace processing is not being performed.
     * @param rawName - The qualified name (with prefix), or the empty string if
     *        qualified names are not available.
     * @param attributes - The attributes attached to the element.
     *          If there are no attributes, it shall be an empty Attributes
     *          object.
     *
     * @return the created outlet, not null.
     *
     * @throws SAXException if an error occurs during creation.
     */
    @Override
    protected Outlet createOutlet(
            QualifiedName outletName,
            String uri,
            String localName,
            String rawName,
            Attributes attributes)
                    throws SAXException
    {
        if (outletName == null)
        {
            String nameAttribute
            = attributes.getValue(OUTLET_NAME_ATTRIBUTE);
            if (nameAttribute == null)
            {
                throw new SAXException("The attribute "
                        + OUTLET_NAME_ATTRIBUTE
                        + " must be set on the element "
                        + rawName
                        + " for Java Outlets");
            }
            outletName = new QualifiedName(nameAttribute);
        }


        String className;
        {
            className = attributes.getValue(OUTLET_CLASS_ATTRIBUTE);
            if (className == null)
            {
                throw new SAXException("The attribute "
                        + OUTLET_CLASS_ATTRIBUTE
                        + " must be set on the element "
                        + rawName
                        + " for java Outlets");
            }
        }

        Class<?> outletClass;
        try
        {
            ClassLoader classLoader = getUnitDescriptor().getClassLoader();
            if (classLoader == null)
            {
                classLoader = getClass().getClassLoader();
            }
            outletClass = Class.forName(className, true, classLoader);
        }
        catch (ClassNotFoundException e)
        {
            throw new SAXException(
                    "Error  while initializing the java outlet "
                            + outletName
                            + " : Could not load class " + className, e);
        }
        catch (ExceptionInInitializerError e)
        {
            log.error(
                    "Error  while initializing the java outlet "
                            + outletName
                            + " : Could not initialize class " + className
                            + " : " + e.getMessage());
            throw e;
        }
        catch (LinkageError e)
        {
            log.error(
                    "Error  while initializing the java outlet "
                            + outletName
                            + " : Could not link class " + className
                            + " : " + e.getMessage());
            throw e;
        }

        Outlet result;
        try
        {
            Constructor<?> constructor
            = outletClass.getConstructor(QualifiedName.class);
            result = (Outlet) constructor.newInstance(outletName);
        }
        catch (NoSuchMethodException e)
        {
            throw new SAXException(
                    "Error  while instantiating the java outlet "
                            + outletName
                            + " : The class " + className
                            + " has no constructor which takes a qualified name",
                            e);
        }
        catch (ClassCastException e)
        {
            throw new SAXException(
                    "Error  while instantiating the java outlet "
                            + outletName
                            + " : The class " + className
                            + " is not an instance of "
                            + Outlet.class.getName(),
                            e);
        }
        catch (IllegalAccessException e)
        {
            throw new SAXException(
                    "Error  while instantiating the java outlet "
                            + outletName
                            + " : The constructor of class "
                            + className + " could not be accessed",
                            e);
        }
        catch (InvocationTargetException e)
        {
            throw new SAXException(
                    "Error  while instantiating the java outlet "
                            + outletName
                            + " : The constructor of class "
                            + className + " could not be called",
                            e);
        }
        catch (InstantiationException e)
        {
            throw new SAXException(
                    "Error  while instantiating the java outlet "
                            + outletName
                            + " : The class " + className
                            + " represents an abstract class, "
                            + "an interface, an array class, a primitive type, "
                            + "or void, or the class has no parameterless constructor, "
                            + "or the instantiation fails for some other reason.",
                            e);
        }
        catch (SecurityException e)
        {
            throw new SAXException(
                    "Error  while instantiating the java outlet "
                            + outletName
                            + " : The security manager denies instantiation",
                            e);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(
            String uri,
            String localName,
            String rawName,
            Attributes attributes)
                    throws SAXException
    {
        level++;
        if (level == 2 && MERGEPOINT_TAG.equals(rawName)
                || super.isProcessingMergepointTag())
        {
            super.startElement(uri, localName, rawName, attributes);
        }
        else if (level == 2 && INPUT_TAG.equals(rawName))
        {
            super.startElement(uri, localName, rawName, attributes);
        }
        else if (level > 1)
        {
            propertyName = rawName;
            propertyValue = new StringBuffer();
        }
        else
        {
            super.startElement(uri, localName, rawName, attributes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String rawName)
            throws SAXException
    {
        level--;
        if (propertyName != null)
        {
            if (!PropertyUtils.isWriteable(getOutlet(), propertyName))
            {
                throw new SAXException("No setter found for property "
                        + propertyName
                        + " in class "
                        + getOutlet().getClass().getName());
            }
            try
            {
                BeanUtils.copyProperty(
                        getOutlet(), propertyName, propertyValue.toString());
            }
            catch (InvocationTargetException | IllegalAccessException e)
            {
                throw new SAXException("error while setting Property "
                        + propertyName
                        + " for java outlet "
                        + getOutlet().getName()
                        + " with value "
                        + propertyValue.toString(),
                        e);
            }
            propertyName = null;
            propertyValue = null;
        }
        else
        {
            super.endElement(uri, localName, rawName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        if (propertyName == null)
        {
            return;
        }
        for (int i = start; i < start + length; ++i)
        {
            propertyValue.append(ch[i]);
        }
    }
}
