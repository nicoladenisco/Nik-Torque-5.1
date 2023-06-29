package org.apache.torque.templates.transformer.om;

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

import org.apache.torque.generator.source.SourceAttributeName;

/**
 * Defines the standard attributes for a source element representing
 * a java field (i.e instance variable of a class).
 */
public enum JavaFieldAttributeName implements SourceAttributeName
{
    /**
     * The name of the field
     **/
    FIELD_NAME("field"),

    /**
     * The type of the field
     */
    FIELD_TYPE("fieldType"),

    /**
     * The access modifier (e.g. "public" ...) for the field.
     * Default is "private"
     **/
    FIELD_ACCESS_MODIFIER("fieldAccessModifier"),

    /**
     * In case the field is a collection:
     * The type of the objects contained in the collection.
     */
    FIELD_CONTAINED_TYPE("fieldContainedType"),

    /**
     * The method name of the getter for the field.
     */
    SETTER_NAME("setter"),

    /**
     * The method name of the setter for the field.
     */
    GETTER_NAME("getter"),

    /**
     * The name of the javaBean property name
     * (corresponding to getter/setter name).
     */
    PROPERTY_NAME("propertyName"),

    /**
     * In case the field must be initialized on first access,
     * this contains the name of the init method.
     */
    INITIALIZER_NAME("initializer"),

    /**
     * In case the type cannot be instantiated (e.g. is an interface like List),
     * this provides the type of the object the field is initialized with.
     */
    INITIALIZER_TYPE("initializerType"),

    /**
     * In case the field must be initialized on first access,
     * this contains the name of the method which returns whether the field
     * has been initialized.
     */
    IS_INITIALIZED_NAME("isInitialized"),

    /**
     * In case the field is a collection:
     * The name of the method used to add one object to the collection.
     */
    ADDER_NAME("adder"),

    /**
     * In case the field is a collection:
     * The name of the method used to reset (null) the collection.
     */
    RESETTER_NAME("resetter"),

    /**
     * In case the field can be filled somehow:
     * The name of the method used to fill the field.
     */
    FILLER_NAME("filler"),

    /**
     * The access modifier (e.g. "public" ...) for the getter method
     **/
    GETTER_ACCESS_MODIFIER("getterAccessModifier"),

    /**
     * The access modifier (e.g. "public" ...) for the setter method
     **/
    SETTER_ACCESS_MODIFIER("setterAccessModifer"),

    /**
     * The throws clause (excluding "throws " itself) for the getter method.
     **/
    GETTER_THROWS("getterThrows"),

    /**
     * The throws clause (excluding "throws " itself) for the setter method.
     **/
    SETTER_THROWS("setterThrows"),

    /**
     * The throws clause (excluding "throws " itself) for the initializer method.
     **/
    INITIALIZER_THROWS("initializerThrows"),

    /**
     * The throws clause (excluding "throws " itself) for the adder method.
     **/
    ADDER_THROWS("adderThrows"),

    /**
     * The default value for the field.
     * This is the value the field is initialized with.
     */
    DEFAULT_VALUE("defaultValue"),

    /**
     * The description of the field.
     */
    DESCRIPTION("description");

    /** The name of the source element attribute, not null. */
    private String name;

    /**
     * Constructor.
     *
     * @param name the name of the source element attribute, not null.
     */
    private JavaFieldAttributeName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the referenced source element attribute.
     *
     * @return the name of the referenced source element attribute.
     */
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
