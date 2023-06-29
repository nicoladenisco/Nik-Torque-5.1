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

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.typemapping.JavaType;

/**
 * Generates method names from field names.
 *
 * $Id: FieldHelper.java 1855923 2019-03-20 16:19:39Z gk $
 */
final class FieldHelper
{
    /** Constant for get. */
    private static final String GET = "get";
    /** Constant for set. */
    private static final String SET = "set";
    /** Constant for is. */
    private static final String IS = "is";

    /**
     * Private constructor for utility class.
     */
    private FieldHelper()
    {
    }

    /**
     * Returns the name of the getter method for a field.
     * "get" is used as prefix, except if the option
     * <code>USE_IS_FOR_GETTERS_OPTION_NAME</code> is set to true and
     * the field type is "boolean", in which case "is" is used as prefix.
     *
     * @param fieldName the name of the field, not null.
     * @param fieldType the type of the field, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the getter method name, not null.
     */
    public static String getGetterName(
            final String fieldName,
            final String fieldType,
            final ControllerState controllerState)
    {
        String getterName;
        if (controllerState.getBooleanOption(
                TemplateOptionName.OM_USE_IS_FOR_BOOLEAN_GETTERS)
                && JavaType.BOOLEAN_PRIMITIVE.getClassName().equals(fieldType))
        {
            getterName = IS + StringUtils.capitalize(fieldName);
        }
        else
        {
            getterName = GET + StringUtils.capitalize(fieldName);
        }
        return getterName;
    }

    /**
     * Returns the name of the setter method for a field.
     * Uses "set" as prefix.
     *
     * @param fieldName the name of the field, not null.
     *
     * @return the setter method name, not null.
     */
    public static String getSetterName(final String fieldName)
    {
        String setterName = SET + StringUtils.capitalize(fieldName);
        return setterName;
    }

    /**
     * Returns the name of the field from a setter name.
     * Removes the "set" prefix and decapitalizes the remainder.
     *
     * @param setterName the name of the setterName, not null, length > 3.
     *
     * @return the field name, not null.
     */
    public static String getFieldNameFromSetterName(final String setterName)
    {
        String fieldName = StringUtils.uncapitalize(
                setterName.substring(SET.length()));
        return fieldName;
    }

    /**
     * Returns the name of the adder method for a field.
     *
     * @param fieldName the name of the field, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the adder method name, not null.
     */
    public static String getAdderName(
            final String fieldName,
            final ControllerState controllerState)
    {
        String adderName
        = controllerState.getOption(
                TemplateOptionName.OM_ADDER_PREFIX)
        + StringUtils.capitalize(fieldName)
        + controllerState.getOption(
                TemplateOptionName.OM_ADDER_SUFFIX);
        return adderName;
    }

    /**
     * Returns the name of the resetter method for a field.
     *
     * @param fieldName the name of the field, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the resetter method name, not null.
     */
    public static String getResetterName(
            final String fieldName,
            final ControllerState controllerState)
    {
        String adderName
        = controllerState.getOption(
                TemplateOptionName.OM_RESETTER_PREFIX)
        + StringUtils.capitalize(fieldName)
        + controllerState.getOption(
                TemplateOptionName.OM_RESETTER_SUFFIX);
        return adderName;
    }

    /**
     * Returns the name of the initializer method for a field.
     *
     * @param fieldName the name of the field, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the initializer method name, not null.
     */
    public static String getInitializerName(
            final String fieldName,
            final ControllerState controllerState)
    {
        String initializerName
        = controllerState.getOption(
                TemplateOptionName.OM_INITIALIZER_PREFIX)
        + StringUtils.capitalize(fieldName)
        + controllerState.getOption(
                TemplateOptionName.OM_INITIALIZER_SUFFIX);
        return initializerName;
    }

    /**
     * Returns the name of the isInitialized method for a field.
     *
     * @param fieldName the name of the field, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the isInitialized method name, not null.
     */
    public static String getIsInitializedName(
            final String fieldName,
            final ControllerState controllerState)
    {
        String initializerName
        = controllerState.getOption(
                TemplateOptionName.OM_IS_INITIALIZED_PREFIX)
        + StringUtils.capitalize(fieldName)
        + controllerState.getOption(
                TemplateOptionName.OM_IS_INITIALIZED_SUFFIX);
        return initializerName;
    }

    /**
     * Returns the name of the filler method for a field.
     *
     * @param fieldName the name of the field, not null.
     * @param distinctionPart a possible distinction addition for resolving
     *        naming conflicts, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the filler method name, not null.
     */
    public static String getFillerName(
            final String fieldName,
            final String distinctionPart,
            final ControllerState controllerState)
    {
        String fillerName
        = controllerState.getOption(
                TemplateOptionName.OM_FILLER_PREFIX)
        + distinctionPart
        + StringUtils.capitalize(fieldName)
        + controllerState.getOption(
                TemplateOptionName.OM_FILLER_SUFFIX);
        return fillerName;
    }

    /**
     * Returns the name of the setAndSave method for a field.
     *
     * @param fieldName the name of the field, not null.
     * @param distinctionPart a possible distinction addition for resolving
     *        naming conflicts, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the setAndSave method name, not null.
     */
    public static String getSetAndSaveMethodName(
            final String fieldName,
            final String distinctionPart,
            final ControllerState controllerState)
    {
        String fillerName
        = controllerState.getOption(
                TemplateOptionName.OM_SET_AND_SAVE_PREFIX)
        + distinctionPart
        + StringUtils.capitalize(fieldName)
        + controllerState.getOption(
                TemplateOptionName.OM_SET_AND_SAVE_SUFFIX);
        return fillerName;
    }
}
