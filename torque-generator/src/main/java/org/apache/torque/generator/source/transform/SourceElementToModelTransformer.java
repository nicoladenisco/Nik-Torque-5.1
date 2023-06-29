package org.apache.torque.generator.source.transform;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.torque.generator.configuration.ClassHelper;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.processor.string.Camelbacker;
import org.apache.torque.generator.processor.string.WrapReservedJavaWords;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.model.NoSuchPropertyException;
import org.apache.torque.generator.source.transform.model.PropertyAccess;

/**
 * A Source transformer transforming a SourceElement graph to a typed model.
 *
 * @version $Id: $
 */
public class SourceElementToModelTransformer implements SourceTransformer
{
    /**
     * The field name in the model which is filled if the attribute name
     * <code>null</code> appears in the source graph.
     */
    public static final String NULL_ATTRIBUTE_FIELD_NAME = "value";

    /** The class logger. */
    private static final Logger log = Logger.getLogger(SourceTransformer.class);

    /** The processor which does the camelback processing. */
    private final Camelbacker camelbacker = new Camelbacker();

    /** The processor which wraps reserved java words. */
    private final WrapReservedJavaWords reservedWordsWrapper
        = new WrapReservedJavaWords();

    /** The class the model root must have. */
    private String modelRootClass;

    /** Whether to ignore unknown attributes in the source tree. */
    private boolean ignoreUnknownAttributes = true;

    /** Whether to ignore unknown elements in the source tree. */
    private boolean ignoreUnknownElements = true;

    /**
     * Standard constructor.
     */
    public SourceElementToModelTransformer()
    {
        camelbacker.setDefaultLowerCase(false);
        camelbacker.setFirstCharUppercase(false);
    }

    /**
     * Constructor defining the model root class.
     *
     * @param modelRootClass the model root class, not null.
     */
    public SourceElementToModelTransformer(final Class<?> modelRootClass)
    {
        this();
        this.modelRootClass = modelRootClass.getName();
    }

    /**
     * Sets the class which the model root must have.
     *
     * @param modelRootClass the class the model root must have,
     *        or null for any class
     */
    public void setModelRootClass(final String modelRootClass)
    {
        this.modelRootClass = modelRootClass;
    }

    /**
     * Sets whether to ignore unknown attributes in the source tree.
     *
     * @param ignoreUnknownAttributes true if unknown attributes
     *        should be ignored, false otherwise.
     */
    public void setIgnoreUnknownAttributes(
            final boolean ignoreUnknownAttributes)
    {
        this.ignoreUnknownAttributes = ignoreUnknownAttributes;
    }

    /**
     * Sets whether to ignore unknown elements in the source tree.
     *
     * @param ignoreUnknownElements true if unknown elements
     *        should be ignored, false otherwise.
     */
    public void setIgnoreUnknownElements(
            final boolean ignoreUnknownElements)
    {
        this.ignoreUnknownElements = ignoreUnknownElements;
    }

    /**
     * @see org.apache.torque.generator.source.transform.SourceTransformer#transform(java.lang.Object, org.apache.torque.generator.control.ControllerState)
     * 
     * Transforms the source graph into the model. This is done by recursively
     * walking through the source graph and setting the fields corresponding
     * to the names of the attributes resp. child elements.     
     * 
     * @param modelRoot the root object to fill, not null.
     * @param controllerState the controller state object
     * 
     * @throws SourceTransformerException if filling the source graph fails,
     *         e.g. if an attribute name or element name has no corresponding
     *         field in a model element.
     */
    @Override
    public Object transform(
            final Object modelRoot,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (modelRootClass == null)
        {
            throw new SourceTransformerException(
                    "modelRootClass must not be null "
                            + "for the transformer of type "
                            + getClass().getName());
        }
        if (!(modelRoot instanceof SourceElement))
        {
            throw new SourceTransformerException(
                    "modelRoot must be of type "
                            + SourceElement.class.getName());
        }
        final Object targetModelRoot = getInstance(
                modelRootClass,
                controllerState.getUnitConfiguration());
        fillModelElement(
                targetModelRoot,
                (SourceElement) modelRoot,
                controllerState.getUnitConfiguration(),
                new HashMap<SourceElement, Object>());
        return targetModelRoot;
    }

    /**
     * Constructs a part of the model from a part of the source graph.
     * This is done by recursively walking through the source graph
     * and setting the fields corresponding to the names of the attributes
     * resp. child elements.
     *
     * @param model the model object to fill, not null.
     * @param sourceElement the source element corresponding to the
     *        model object, not null.
     * @param unitConfiguration the unit configuration, not null.
     * @param alreadyMapped a map of already mapped source elements,
     *        mapped to the corresponding model elements.
     *
     * @throws SourceTransformerException if filling the target object graph
     *         fails, e.g. if an attribute name or element name
     *         has no corresponding field in a model element.
     */
    private void fillModelElement(
            final Object model,
            final SourceElement sourceElement,
            final UnitConfiguration unitConfiguration,
            final Map<SourceElement, Object> alreadyMapped)
                    throws SourceTransformerException
    {
        for (String attributeName : sourceElement.getAttributeNames())
        {
            final Object attributeValue = sourceElement.getAttribute(attributeName);
            attributeName = camelbacker.process(attributeName);
            attributeName = reservedWordsWrapper.process(attributeName);
            if (attributeName == null)
            {
                attributeName = NULL_ATTRIBUTE_FIELD_NAME;
            }
            final PropertyAccess propertyAccess = new PropertyAccess(
                    model,
                    attributeName);
            if (!propertyAccess.isPropertyAccessible())
            {
                if (!ignoreUnknownAttributes)
                {
                    throw new NoSuchPropertyException(
                            model,
                            attributeName,
                            propertyAccess.getPrefixList(),
                            propertyAccess.getSuffixList());
                }
                else
                {
                    log.debug("Cannot set property " + attributeName
                            + " on class " + model.getClass()
                            + ", skipping this property.");
                    continue;
                }
            }
            propertyAccess.setProperty(attributeValue);
        }
        for (final SourceElement child : sourceElement.getChildren())
        {
            String propertyName = child.getName();
            propertyName = camelbacker.process(propertyName);
            propertyName = reservedWordsWrapper.process(propertyName);
            final PropertyAccess propertyAccess = new PropertyAccess(
                    model,
                    propertyName);
            if (!propertyAccess.isPropertyAccessible())
            {
                if (!ignoreUnknownElements)
                {
                    throw new NoSuchPropertyException(
                            model,
                            propertyName,
                            propertyAccess.getPrefixList(),
                            propertyAccess.getSuffixList());
                }
                else
                {
                    log.debug("Cannot set property " + propertyName
                            + " on class " + model.getClass()
                            + ", skipping this property.");
                    continue;
                }
            }
            Object childModelElement = alreadyMapped.get(child);
            if (childModelElement != null)
            {
                propertyAccess.setProperty(childModelElement);
                continue;
            }
            if (Collection.class.isAssignableFrom(
                    propertyAccess.getPropertyType()))
            {
                childModelElement = getInstance(
                        propertyAccess.getFirstGenericTypeArgument().getName(),
                        unitConfiguration);
            }
            else if (propertyAccess.getPropertyType().isArray())
            {
                childModelElement = getInstance(
                        propertyAccess.getPropertyType().getComponentType().getName(),
                        unitConfiguration);
            }
            else
            {
                childModelElement = getInstance(
                        propertyAccess.getPropertyType().getName(),
                        unitConfiguration);
            }
            propertyAccess.setProperty(childModelElement);
            alreadyMapped.put(child, childModelElement);
            PropertyAccess parentPropertyAccess
                = new PropertyAccess(childModelElement, "parent");
            if (!parentPropertyAccess.isPropertyAccessible())
            {
                final String modelClassName  = model.getClass().getSimpleName();
                parentPropertyAccess = new PropertyAccess(
                        childModelElement,
                        "parent" + modelClassName);
            }
            if (parentPropertyAccess.isPropertyAccessible())
            {
                parentPropertyAccess.setProperty(model);
            }
            fillModelElement(
                    childModelElement,
                    child,
                    unitConfiguration,
                    alreadyMapped);
        }
    }

    /**
     * Creates an instance of a class.
     *
     * @param className the fully qualified name of the class to instantiate.
     * @param unitConfiguration The configuration of the generation unit, not null.
     * @return the instance of the class
     * @throws SourceTransformerException if the class cannot be instantiated.
     */
    protected static Object getInstance(
            final String className,
            final UnitConfiguration unitConfiguration)
                    throws SourceTransformerException
    {
        if (className == null)
        {
            return null;
        }
        Object result;
        try
        {
            ClassLoader classLoader = unitConfiguration.getClassLoader();
            if (classLoader == null)
            {
                classLoader = ClassHelper.class.getClassLoader();
            }
            final Class<?> clazz = Class.forName(className, true, classLoader);
            result = clazz.newInstance();
        }
        catch (final ClassNotFoundException e)
        {
            throw new SourceTransformerException("The class "
                    + className
                    + " could not be found.",
                    e);
        }
        catch (final IllegalAccessException e)
        {
            throw new SourceTransformerException("Instantiating "
                    + className
                    + " is not allowed",
                    e);
        }
        catch (final InstantiationException e)
        {
            throw new SourceTransformerException("The class "
                    + className
                    + " has no standard constructor.",
                    e);
        }
        return result;
    }

}
