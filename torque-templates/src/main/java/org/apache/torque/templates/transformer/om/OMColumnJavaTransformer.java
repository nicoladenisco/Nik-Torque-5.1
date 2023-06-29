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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.processor.string.Camelbacker;
import org.apache.torque.generator.processor.string.ConstantNameCreator;
import org.apache.torque.generator.processor.string.WrapReservedJavaWords;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.TorqueSchemaJavaType;
import org.apache.torque.templates.typemapping.JavaType;
import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.TypeMap;

/**
 * Does java related mappings.
 */
public class OMColumnJavaTransformer
{
    private static Logger log = LogManager.getLogger(OMColumnJavaTransformer.class);
    
    /** The camelbacker to create the java name from the column name. */
    private static Camelbacker javaNameCamelbacker = new Camelbacker();

    /** Creates constant names from values. */
    private static ConstantNameCreator constantNameCreator = new ConstantNameCreator();

    /** The Date format for Dates in Default values. */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

    /** Constant for the CURRENT_DATE default value for Dates. */
    static final String CURRENT_DATE = "CURRENT_DATE";

    /** Constant for the CURRENT_TIME default value for Dates. */
    static final String CURRENT_TIME = "CURRENT_TIME";

    /** Constant for the CURRENT_TIMESTAMP default value for Dates. */
    static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";

    /** Constant for the getDefaultDate method name. */
    static final String GET_DEFAULT_DATE_METHOD_NAME = "getCurrentDate";

    /** Constant for the getDefaultTime method name. */
    static final String GET_DEFAULT_TIME_METHOD_NAME = "getCurrentTime";

    /** Constant for the getDefaultTimestamp method name. */
    static final String GET_DEFAULT_TIMESTAMP_METHOD_NAME
    = "getCurrentTimestamp";

    /** Prevents reserved java words. */
    private static WrapReservedJavaWords reservedJavaWordsWrapper
        = new WrapReservedJavaWords();

    /**
     * Sets the javaType attribute of the column element
     * if it is not already set and a default value is set.
     *
     * @param columnElement the column element, not null.
     */
    protected void setJavaTypeAttribute(final SourceElement columnElement)
    {
        if (columnElement.getAttribute(TorqueSchemaAttributeName.JAVA_TYPE)
                != null)
        {
            return;
        }
        SourceElement databaseElement = columnElement.getParent().getParent();
        String defaultJavaType = (String) databaseElement.getAttribute(
                TorqueSchemaAttributeName.DEFAULT_JAVA_TYPE);
        if (defaultJavaType != null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.JAVA_TYPE,
                    defaultJavaType);
        }
    }


    /**
     * Sets the javaName attribute of the column element if it is not
     * already set.
     *
     * @param columnElement the column element, not null.
     */
    protected void setJavaNameAttribute(final SourceElement columnElement)
    {
        if (columnElement.getAttribute(TorqueSchemaAttributeName.JAVA_NAME)
                != null)
        {
            return;
        }
        String columnName = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.NAME);
        String javaName = javaNameCamelbacker.process(columnName);
        columnElement.setAttribute(
                TorqueSchemaAttributeName.JAVA_NAME,
                javaName);
        //log.debug("javaName: {}", javaName);
    }

    /**
     * Sets the fieldName attribute of the column element if it is not
     * already set.
     * The javaName attribute of the column must be set.
     *
     * @param columnElement the column element, not null.
     */
    protected void setFieldNameAttribute(final SourceElement columnElement)
    {
        if (columnElement.getAttribute(JavaFieldAttributeName.FIELD_NAME)
                != null)
        {
            return;
        }
        String javaName = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.JAVA_NAME);
        String fieldName = StringUtils.uncapitalize(javaName);
        fieldName = reservedJavaWordsWrapper.process(fieldName);
        columnElement.setAttribute(
                JavaFieldAttributeName.FIELD_NAME,
                fieldName);
    }

    /**
     * Calculates the java default value of a column in case a default value
     * is set.
     *
     * @param javaType The java type of the column.
     * @param defaultValue The default value from the schema.
     * @param useDatabaseDefaultValue whether the database default value should
     *        be used.
     * @param columnElement the current column element for which
     *        the default value should be calculated.
     *
     * @return The java default value.
     *
     * @throws SourceTransformerException if an illegal default value is used.
     */
    protected String getDefaultValueWithDefaultSet(
            final JavaType javaType,
            final String defaultValue,
            final boolean useDatabaseDefaultValue,
            final SourceElement columnElement)
                    throws SourceTransformerException
    {
        boolean primitiveFieldType = javaType.isPrimitive();
        String fieldDefaultValue;
        if (JavaType.BOOLEAN_PRIMITIVE == javaType)
        {
            if ("Y".equals(defaultValue)
                    || "1".equals(defaultValue)
                    || "true".equalsIgnoreCase(defaultValue))
            {
                fieldDefaultValue = "true";
            }
            else
            {
                fieldDefaultValue = "false";
            }
        }
        else if (JavaType.BOOLEAN_OBJECT == javaType)
        {
            if ("Y".equals(defaultValue)
                    || "1".equals(defaultValue)
                    || "true".equalsIgnoreCase(defaultValue))
            {
                fieldDefaultValue = "Boolean.TRUE";
            }
            else
            {
                fieldDefaultValue = "Boolean.FALSE";
            }
        }
        else if (JavaType.STRING == javaType)
        {
            fieldDefaultValue = "\"" + defaultValue + "\"";
        }
        else if (JavaType.SHORT_OBJECT == javaType)
        {
            // The following is better than casting with (short)
            // because a range check is performed,
            fieldDefaultValue = "Short.valueOf(\"" + defaultValue + "\")";
        }
        else if (JavaType.BYTE_OBJECT == javaType)
        {
            // The following is better than casting with (byte)
            // because a range check is performed,
            fieldDefaultValue = "Byte.valueOf(\"" + defaultValue + "\")";
        }
        else if (JavaType.INTEGER_OBJECT == javaType)
        {
            fieldDefaultValue = "Integer.valueOf(" + defaultValue + ")";
        }
        else if (JavaType.LONG_OBJECT == javaType)
        {
            fieldDefaultValue = "Long.valueOf(" + defaultValue + "L)";
        }
        else if (JavaType.DATE == javaType)
        {
            if (Stream.of(CURRENT_DATE, CURRENT_TIME, CURRENT_TIMESTAMP)
                    .anyMatch(s -> s.equalsIgnoreCase(defaultValue)) )
            {
                if (useDatabaseDefaultValue)
                {
                    // if the database default value is used do not use
                    // current time in java as it might be different
                    fieldDefaultValue = "null";
                }
                else
                {
                    // the database does not provide a default so use
                    // java current time.
                    if (CURRENT_DATE.equalsIgnoreCase(defaultValue))
                    {
                        String methodName;
                        if (columnElement.getParent().getAttribute(
                                TableAttributeName.GET_CURRENT_DATE_METHOD_NAME)
                                != null)
                        {
                            methodName = columnElement.getParent().getAttribute(
                                    TableAttributeName.GET_CURRENT_DATE_METHOD_NAME)
                                    .toString();
                        }
                        else
                        {
                            methodName = GET_DEFAULT_DATE_METHOD_NAME;
                        }
                        fieldDefaultValue = methodName + "()";
                    }
                    else if (CURRENT_TIME.equalsIgnoreCase(defaultValue))
                    {
                        String methodName;
                        if (columnElement.getParent().getAttribute(
                                TableAttributeName.GET_CURRENT_TIME_METHOD_NAME)
                                != null)
                        {
                            methodName = columnElement.getParent().getAttribute(
                                    TableAttributeName.GET_CURRENT_TIME_METHOD_NAME)
                                    .toString();
                        }
                        else
                        {
                            methodName = GET_DEFAULT_TIME_METHOD_NAME;
                        }
                        fieldDefaultValue = methodName + "()";
                    }
                    else
                    {
                        String methodName;
                        if (columnElement.getParent().getAttribute(
                                TableAttributeName.GET_CURRENT_TIMESTAMP_METHOD_NAME)
                                != null)
                        {
                            methodName = columnElement.getParent().getAttribute(
                                    TableAttributeName.GET_CURRENT_TIMESTAMP_METHOD_NAME)
                                    .toString();
                        }
                        else
                        {
                            methodName = GET_DEFAULT_TIMESTAMP_METHOD_NAME;
                        }
                        fieldDefaultValue = methodName + "()";
                    }
                }
            }
            else
            {
                if (useDatabaseDefaultValue)
                {
                    // if the database default value is used, do not use
                    // current time in java as it might be different
                    // and have a custom format.
                    fieldDefaultValue = "null";
                }
                else
                {
                    fieldDefaultValue = "new Date("
                            + getDefaultValueAsDate(defaultValue).getTime()
                            + "L)";
                }
            }
        }
        else if (primitiveFieldType)
        {
            fieldDefaultValue = defaultValue;
        }
        else
        {
            fieldDefaultValue
            = "new " + javaType.getFullClassName()
            + "(" + defaultValue + ")";
        }
        return fieldDefaultValue;
    }

    /**
     * Parses the default value String as Date.
     *
     * @param defaultValue the String to parse.
     * @return the parsed date.
     *
     * @throws SourceTransformerException if the date cannot be parsed.
     */
    public static Date getDefaultValueAsDate(final String defaultValue)
            throws SourceTransformerException
    {
        try
        {
            SimpleDateFormat dateFormat
                = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return dateFormat.parse(defaultValue);
        }
        catch (ParseException e)
        {
            throw new SourceTransformerException(
                    "The default value "
                            + defaultValue
                            + " does not match the format String "
                            + DEFAULT_DATE_FORMAT
                            + " for date values");
        }
    }

    /**
     * Calculates the java default value of a column in case a default value
     * is not set.
     *
     * @param javaType The java type of the column.
     *
     * @return The java default value.
     * @throws SourceTransformerException if the value cannot be found
     */
    protected String getDefaultValueWithoutDefaultSet(final JavaType javaType)
            throws SourceTransformerException
    {
        String fieldDefaultValue;
        boolean primitiveFieldType = javaType.isPrimitive();
        if (primitiveFieldType)
        {
            if (JavaType.BOOLEAN_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "false";
            }
            else if (JavaType.BYTE_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "(byte) 0";
            }
            else if (JavaType.SHORT_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "(short) 0";
            }
            else if (JavaType.INTEGER_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "0";
            }
            else if (JavaType.LONG_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "0L";
            }
            else if (JavaType.FLOAT_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "0";
            }
            else if (JavaType.DOUBLE_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "0";
            }
            else if (JavaType.CHAR_PRIMITIVE == javaType)
            {
                fieldDefaultValue = "'\0'";
            }
            else
            {
                throw new SourceTransformerException(
                        "unknown primitive type" + javaType);
            }
        }
        else
        {
            fieldDefaultValue = "null";
        }
        return fieldDefaultValue;
    }
    
    /**
     * Returns the java type of the field representing a database column.
     *
     * @param columnElement the column element, not null.
     * @param schemaType the schema type, not null.
     * @param enumClassName the class name of the enum, or null if the column is not an enum.
     *
     * @return the java type of the column
     * @throws SourceTransformerException if error in transform
     */
    protected JavaType setFieldJavaType(
            final SourceElement columnElement,
            final SchemaType schemaType,
            final String enumClassName)
                    throws SourceTransformerException
    {
        JavaType result;
        String javaType = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.JAVA_TYPE);
        if (TorqueSchemaJavaType.OBJECT.getValue().equals(javaType))
        {
            result = TypeMap.getJavaObjectType(schemaType);
        }
        else if (TorqueSchemaJavaType.PRIMITIVE.getValue().equals(javaType)
                || javaType == null)
        {
            result = TypeMap.getJavaPrimitiveType(schemaType);
        }
        else
        {
            String columnName = (String) columnElement.getAttribute(
                    TorqueSchemaAttributeName.NAME);
            throw new SourceTransformerException("Unknown javaType "
                    + javaType
                    + " in column "
                    + columnName);
        }
        if (enumClassName != null)
        {
            // enumclassName is correct for Java class generation, but not for column type mapping in peers -> JavaType.ENUM
            columnElement.setAttribute(JavaFieldAttributeName.FIELD_TYPE, enumClassName);
            //columnElement.setAttribute(JavaFieldAttributeName.FIELD_TYPE, JavaType.ENUM.getClassName());
            
            // this seems to be more correct 
            columnElement.setAttribute(TorqueSchemaAttributeName.JAVA_NAME, enumClassName);
            
            columnElement.setAttribute(ColumnAttributeName.ENUM_VALUE_CLASS_NAME, result.getFullClassName());
        }
        else
        {
            columnElement.setAttribute(JavaFieldAttributeName.FIELD_TYPE, result.getFullClassName());
        }
        return result;
    }

    /**
     * Sets the enumClassName, enumPackage and generateEnum Attributes
     * if either enumValue child elements (xml element <code>enum-value</code>) are present or the enumType
     * attribute is set on the column.
     * Afterwards, the enumClassName attribute contains the unqualified name
     * of the enum, the enumPackage attribute contains the enum package,
     * and the generateEnum attribute contains "true" if the enum needs
     * to be generated.
     *
     * This requires that the javaName attribute is set on the column
     * and that the dbObjectPackage element is set on the table.
     *
     * @param columnElement the column element to set the elements in, not null.
     * @param controllerState the controller state, not null.
     *
     * @return the class name of the enum, or null if the column is not an enum column.
     */
    public static String setEnumAttributes(
            final SourceElement columnElement,
            final ControllerState controllerState)
    {
        String enumClassName = (String) columnElement.getAttribute(TorqueSchemaAttributeName.ENUM_TYPE);
        // whether to generate an enum class. This is not the same as columnIsEnum because
        // we have the case of pre-defined enums (no enum-value attributes given).
        boolean generateEnum = columnElement.getChild(TorqueSchemaElementName.ENUM_VALUE) != null;

        String enumPackage;
        boolean columnIsEnum = false;
        if (enumClassName != null)
        {
            columnIsEnum = true;
            int lastIndexOfDot = enumClassName.lastIndexOf('.');
            if (lastIndexOfDot != -1)
            {
                enumPackage = enumClassName.substring(0, lastIndexOfDot);
                enumClassName = enumClassName.substring(lastIndexOfDot + 1);
            }
            else
            {
                enumPackage = (String) columnElement.getParent().getAttribute(
                        TableAttributeName.DB_OBJECT_PACKAGE);
            }
        }
        else
        {
            if (columnElement.getChild(TorqueSchemaElementName.ENUM_VALUE) != null)
            {
                columnIsEnum = true;
            }
            enumClassName = controllerState.getStringOption(TemplateOptionName.OM_ENUM_TYPE_PREFIX)
                    + columnElement.getAttribute(TorqueSchemaAttributeName.JAVA_NAME)
                    + controllerState.getStringOption(TemplateOptionName.OM_ENUM_TYPE_SUFFIX);
            enumPackage = (String) columnElement.getParent().getAttribute(
                    TableAttributeName.DB_OBJECT_PACKAGE);
        }
        columnElement.setAttribute(
                ColumnAttributeName.ENUM_CLASS_NAME,
                enumClassName);
        columnElement.setAttribute(
                ColumnAttributeName.ENUM_PACKAGE,
                enumPackage);
        columnElement.setAttribute(
                ColumnAttributeName.GENERATE_ENUM,
                Boolean.toString(generateEnum));
        columnElement.setAttribute(
                ColumnAttributeName.IS_ENUM,
                Boolean.toString(columnIsEnum));

        if (columnIsEnum)
        {
            log.debug("set enumClassName to {}", enumClassName);
            return enumClassName;
        }
        else
        {
            return null;
        }
    }

    protected void setEnumValueJavaNameAttribute(
            final SourceElement enumValueElement)
                    throws SourceTransformerException
    {
        if (enumValueElement.getAttribute(TorqueSchemaAttributeName.JAVA_NAME) != null)
        {
            return;
        }
        String value = (String) enumValueElement.getAttribute(TorqueSchemaAttributeName.VALUE);
        if (value == null)
        {
            throw new SourceTransformerException("value attribute must be set on all enum-value elements in enum column "
                    + enumValueElement.getParent());
        }
        String javaName = constantNameCreator.process(value);
        enumValueElement.setAttribute(TorqueSchemaAttributeName.JAVA_NAME, javaName);
    }

    protected void setEnumValueJavaValueAttribute(
            final SourceElement enumValueElement,
            final JavaType columnJavaType)
                    throws SourceTransformerException
    {
        if (enumValueElement.getAttribute(EnumValueAttributeName.JAVA_VALUE) != null)
        {
            return;
        }
        SourceElement columnElement = enumValueElement.getParent();
        String javaValue = getDefaultValueWithDefaultSet(
                columnJavaType,
                (String) enumValueElement.getAttribute(TorqueSchemaAttributeName.VALUE),
                false,
                columnElement);
        enumValueElement.setAttribute(EnumValueAttributeName.JAVA_VALUE, javaValue);
    }
}
