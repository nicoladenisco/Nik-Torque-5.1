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


/**
 * Helper for instantiating classes.
 * $Id: ClassHelper.java 1850967 2019-01-10 17:54:13Z painter $
 */
public final class ClassHelper
{
    /**
     * No Instances for utility class.
     */
    private ClassHelper()
    {
    }

    /**
     * Creates an instance of a class and checks whether it can be casted
     * to another class
     *
     * @param className the fully qualified name of the class to instantiate.
     * @param isInstanceOf the Interface or class the instance
     *        must be an instance of, or null to disable the check.
     * @param unitDescriptor The description of the generation unit, not null.
     *
     * @return the instance of the class
     * @throws ConfigurationException if the class cannot be instantiated
     *         or it is not an instance of <code>isInstanceOf</code>
     */
    public static Object getInstance(
            String className,
            Class<?> isInstanceOf,
            UnitDescriptor unitDescriptor)
                    throws ConfigurationException
    {
        if (className == null)
        {
            return null;
        }
        Object result;
        try
        {
            ClassLoader classLoader = unitDescriptor.getClassLoader();
            if (classLoader == null)
            {
                classLoader = ClassHelper.class.getClassLoader();
            }
            Class<?> clazz = Class.forName(className, true, classLoader);
            result = clazz.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            throw new ConfigurationException("The class "
                    + className
                    + " could not be found.",
                    e);
        }
        catch (IllegalAccessException e)
        {
            throw new ConfigurationException("Instantiating "
                    + className
                    + " is not allowed",
                    e);
        }
        catch (InstantiationException e)
        {
            throw new ConfigurationException("The class "
                    + className
                    + " has no standard constructor.",
                    e);
        }
        if (isInstanceOf != null
                && !(isInstanceOf.isInstance(result)))
        {
            throw new ConfigurationException("Classes with class : "
                    + className
                    + " are not instances of "
                    + isInstanceOf.getName());
        }
        return result;
    }
}
