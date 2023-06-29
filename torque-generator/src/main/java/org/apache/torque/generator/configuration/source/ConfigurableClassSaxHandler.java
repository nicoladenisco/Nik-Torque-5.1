package org.apache.torque.generator.configuration.source;

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

import static org.apache.torque.generator.configuration.source.SourceConfigurationTags.CLASS_ATTRIBUTE;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads a configurable class from the controller configuration file.
 *
 * @param <T> the class to create.
 */
public class ConfigurableClassSaxHandler<T> extends DefaultHandler
{
    /** The logger for this class. */
    private static Log log = LogFactory.getLog(DefaultHandler.class);

    /** The description of the generation unit, not null. */
    private final UnitDescriptor unitDescriptor;

    /** The class instance which is currently configured. */
    private T configuredClass;

    /** the name of the XML Tag to process, not null. */
    private final String tagNameToProcess;

    /** The current nesting level inside the processed element. */
    private int level = 0;

    /**
     * The name of the currently read bean property of the transformer,
     * or null if no property is currently read.
     */
    private String propertyName;

    /**
     * The value of the currently read bean property of the transformer,
     * or null if no property is currently read or the property is a
     * list property.
     */
    private StringBuilder simplePropertyValue;

    /**
     * The value of the currently read bean property of the transformer,
     * or null if no property is currently read or the property is a
     * simple property.
     */
    private ArrayList<String> listPropertyValue;

    /**
     * The value of the currently read list entry of the currently read
     * property of the transformer,
     * or null if no list entry is currently read or the property is a
     * simple property.
     */
    private StringBuilder listPropertyEntry;

    /**
     * Constructor.
     *
     * @param configurationProvider The access object for the configuration
     *        files, not null.
     * @param unitDescriptor The description of the generation unit, not null.
     * @param tagNameToProcess the name of the XML Tag to process, not null.
     *
     * @throws NullPointerException if an argument is null.
     */
    public ConfigurableClassSaxHandler(
            final ConfigurationProvider configurationProvider,
            final UnitDescriptor unitDescriptor,
            final String tagNameToProcess)
    {
        if (configurationProvider == null)
        {
            throw new NullPointerException("configurationProvider must not be null");
        }
        if (unitDescriptor == null)
        {
            throw new NullPointerException("unitDescriptor must not be null");
        }
        if (tagNameToProcess == null)
        {
            throw new NullPointerException("tagNameToProcess must not be null");
        }
        this.unitDescriptor = unitDescriptor;
        this.tagNameToProcess = tagNameToProcess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName, final String rawName,
            final Attributes attributes)
                    throws SAXException
    {
        if (level == 0)
        {
            level++;
            if (rawName.equals(tagNameToProcess))
            {
                String className = attributes.getValue(
                        CLASS_ATTRIBUTE);
                if (className == null)
                {
                    throw new SAXException(
                            "The attribute " + CLASS_ATTRIBUTE
                            + " must not be null for the element "
                            + tagNameToProcess);
                }
                configuredClass = createConfiguredClass(
                        className, unitDescriptor);
            }
            else
            {
                throw new SAXException("Unknown element " + rawName);
            }
        }
        else if (level == 1)
        {
            level++;
            propertyName = rawName;
            simplePropertyValue = new StringBuilder();
        }
        else if (level == 2)
        {
            level++;
            if (simplePropertyValue.length() > 0
                    && !StringUtils.isWhitespace(simplePropertyValue.toString()))
            {
                throw new SAXException(
                        "Cannot parse both text content and child elements "
                                + " in element " + propertyName);
            }
            simplePropertyValue = null;
            if (listPropertyValue == null)
            {
                listPropertyValue = new ArrayList<>();
            }
            listPropertyEntry = new StringBuilder();
        }
        else
        {
            throw new SAXException("unknown Element " + rawName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName, final String rawName)
            throws SAXException
    {
        level--;
        if (level == 2)
        {
            listPropertyValue.add(listPropertyEntry.toString());
            listPropertyEntry = null;
        }
        else if (level == 1)
        {
            if (!PropertyUtils.isWriteable(configuredClass, propertyName))
            {
                throw new SAXException("No setter found for property "
                        + propertyName
                        + " in class "
                        + configuredClass.getClass().getName());
            }
            Object propertyValue;
            if (simplePropertyValue != null)
            {
                propertyValue = simplePropertyValue.toString();
            }
            else
            {
                propertyValue = listPropertyValue;
            }
            try
            {
                BeanUtils.copyProperty(
                        configuredClass,
                        propertyName,
                        propertyValue);
            }
            catch (InvocationTargetException | IllegalAccessException e)
            {
                throw new SAXException("error while setting Property "
                        + propertyName
                        + " for java transformer "
                        + configuredClass.getClass().getName()
                        + " with value "
                        + propertyValue.toString(),
                        e);
            }
            propertyName = null;
            propertyValue = null;
        }
        else if (level != 0)
        {
            throw new SAXException("endElemend reached Level " + level);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(final char[] ch, final int start, final int length)
            throws SAXException
    {
        if (simplePropertyValue != null)
        {
            for (int i = start; i < start + length; ++i)
            {
                simplePropertyValue.append(ch[i]);
            }
            return;
        }
        if (listPropertyEntry != null)
        {
            for (int i = start; i < start + length; ++i)
            {
                listPropertyEntry.append(ch[i]);
            }
            return;
        }
    }

    /**
     * Returns the configured class.
     *
     * @return the configured class, not null if a
     *         matching snippet was processed.
     */
    public T getConfiguredClass()
    {
        return configuredClass;
    }

    /**
     * Returns the configuration filled with the contents of the parsed snippet.
     *
     * @return the configuration which was filled, not null if a
     *         matching snippet was processed.
     */
    public boolean isFinished()
    {
        return configuredClass != null && level == 0;
    }

    /**
     * Creates an instance of the configured class.
     *
     * @param className the name of the configured class to be created.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @return the instance of the configured class, not null.
     *
     * @throws ExceptionInInitializerError if an error occurs in the
     *        initializer of the configured class.
     * @throws LinkageError if the linkage fails.
     * @throws SAXException if any other  error occurs during instantiation
     *         of the class.
     */
    @SuppressWarnings("unchecked")
    private T createConfiguredClass(
            final String className,
            final UnitDescriptor unitDescriptor)
                    throws SAXException
    {
        Class<?> transformerClass;
        try
        {
            ClassLoader classLoader = unitDescriptor.getClassLoader();
            if (classLoader == null)
            {
                classLoader
                = ConfigurableClassSaxHandler.class.getClassLoader();
            }
            transformerClass = Class.forName(className, true, classLoader);
        }
        catch (ClassNotFoundException e)
        {
            throw new SAXException(
                    "Error  while initializing a source transformer :"
                            + " Could not load class " + className, e);
        }
        catch (ExceptionInInitializerError e)
        {
            log.error(
                    "Error  while initializing a source transformer :"
                            + " Could not initialize class " + className
                            + " : " + e.getMessage());
            throw e;
        }
        catch (LinkageError e)
        {
            log.error(
                    "Error  while initializing a source transformer :"
                            + " Could not link class " + className
                            + " : " + e.getMessage());
            throw e;
        }

        T result;
        try
        {
            Constructor<?> constructor
            = transformerClass.getConstructor();
            result = (T) constructor.newInstance();
        }
        catch (NoSuchMethodException e)
        {
            throw new SAXException(
                    "Error  while instantiating a source transformer :"
                            + " The class " + className
                            + " has no default constructor",
                            e);
        }
        catch (ClassCastException e)
        {
            throw new SAXException(
                    "Error  while instantiating a source transformer :"
                            + " The class " + className
                            + " is not an instance of "
                            + SourceTransformer.class.getName(),
                            e);
        }
        catch (IllegalAccessException e)
        {
            throw new SAXException(
                    "Error  while instantiating a source transformer :"
                            + " The constructor of class "
                            + className + " could not be accessed",
                            e);
        }
        catch (InvocationTargetException e)
        {
            throw new SAXException(
                    "Error  while instantiating a source transformer :"
                            + " The constructor of class "
                            + className + " could not be called",
                            e);
        }
        catch (InstantiationException e)
        {
            throw new SAXException(
                    "Error  while instantiating a source transformer :"
                            + " The class " + className
                            + " represents an abstract class, "
                            + "an interface, an array class, a primitive type, "
                            + "or void, or the class has no parameterless constructor, "
                            + "or the instantiation fails for some other reason.",
                            e);
        }
        catch (SecurityException e)
        {
            throw new SAXException(
                    "Error  while instantiating a source transformer :"
                            + " The security manager denies instantiation of the class "
                            + className,
                            e);
        }

        return result;
    }
}
