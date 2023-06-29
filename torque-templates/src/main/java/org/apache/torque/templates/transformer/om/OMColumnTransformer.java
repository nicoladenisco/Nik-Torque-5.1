package org.apache.torque.templates.transformer.om;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.processor.string.WrapReservedJavaWords;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.TorqueSchemaInheritance;
import org.apache.torque.templates.transformer.SchemaTypeHelper;
import org.apache.torque.templates.typemapping.JavaType;
import org.apache.torque.templates.typemapping.ResultSetGetter;
import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;
import org.apache.torque.templates.typemapping.TypeMap;

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
 * Sets the class names and packages for the OM model.
 * The id method attribute must already be set on the parent table element
 * when this transformer is called.
 */
public class OMColumnTransformer
{
    private static Logger log = LogManager.getLogger(OMColumnTransformer.class);

    /** The transformer for inheritance elements. */
    private static OMInheritanceTransformer inheritanceTransformer
        = new OMInheritanceTransformer();

    /** Names which cannot be used as constants for column names. */
    private static final Set<String> RESERVED_CONSTANT_NAMES;

    /** Prevents reserved java words. */
    private static WrapReservedJavaWords reservedJavaWordsWrapper
        = new WrapReservedJavaWords();
    
    protected OMColumnJavaTransformer javaTransformer = new OMColumnJavaTransformer();

    static
    {
        Set<String> reservedConstantNames = new HashSet<>();
        reservedConstantNames.add("DATABASE");
        reservedConstantNames.add("TABLE_NAME");
        reservedConstantNames.add("TABLE");
        RESERVED_CONSTANT_NAMES
        = Collections.unmodifiableSet(reservedConstantNames);
    }

    /**
     * @param columnElement column to transform
     * @param controllerState the controller state object
     * @param columnPosition column position
     * @throws SourceTransformerException if the column cannot be found
     */
    public void transform(
            final SourceElement columnElement,
            final ControllerState controllerState,
            final int columnPosition)
                    throws SourceTransformerException
    {
        checkElementName(columnElement);
        checkColumnNameExists(columnElement);
        javaTransformer.setJavaTypeAttribute(columnElement);
        javaTransformer.setJavaNameAttribute(columnElement);

        columnElement.setAttribute(
                ColumnAttributeName.POSITION,
                columnPosition);
        setAttributeDefaultValues(columnElement);

        SchemaType schemaType = SchemaTypeHelper.getSchemaType(
                columnElement,
                controllerState);
        columnElement.setAttribute("schemaType", schemaType);
        setDomainAttributes(columnElement, controllerState);

        String enumClassName = OMColumnJavaTransformer.setEnumAttributes(columnElement, controllerState);
        JavaType fieldJavaType = javaTransformer.setFieldJavaType(columnElement, schemaType, enumClassName);
        JavaType fieldJavaObjectType = TypeMap.getJavaObjectType(schemaType);
        columnElement.setAttribute(
                ColumnAttributeName.FIELD_OBJECT_TYPE,
                fieldJavaObjectType.getFullClassName());

        setPrimitiveTypeAttribute(columnElement, fieldJavaType);
        setNumberTypeAttribute(columnElement, fieldJavaType);
        setFieldNameAttribute(columnElement);
        setPeerColumnNameAttribute(columnElement);
        setQualifiedColumnNameAttribute(columnElement);
        setGetterNameAttribute(columnElement, fieldJavaType, controllerState);
        setSetterNameAttribute(columnElement);
        setAccessModifierAttributes(columnElement);
        setDefaultValueAttribute(columnElement, fieldJavaType, controllerState);
        setUseDatabaseDefaultValueAttribute(columnElement);
        setResultSetGetterAttribute(columnElement, schemaType);
        setSampleObjectAttribute(columnElement, schemaType);

        for (SourceElement inheritanceElement : columnElement.getChildren(
                TorqueSchemaElementName.INHERITANCE.getName()))
        {
            inheritanceTransformer.transform(
                    inheritanceElement,
                    controllerState);
        }
        for (SourceElement enumValueElement : columnElement.getChildren(
                TorqueSchemaElementName.ENUM_VALUE.getName()))
        {
            javaTransformer.setEnumValueJavaNameAttribute(enumValueElement);
            javaTransformer.setEnumValueJavaValueAttribute(enumValueElement, fieldJavaType);
        }
    }

    /**
     * Sets default values for attributes of the column element
     * if the attribute is not set.
     * The following attributes are checked:
     * autoIncrement, protected, inheritance, required, primaryKey
     *
     * @param columnElement the column element, not null.
     */
    protected void setAttributeDefaultValues(final SourceElement columnElement)
    {
        // set autoincrement attribute
        if (columnElement.getAttribute(TorqueSchemaAttributeName.AUTO_INCREMENT)
                == null)
        {
            String idMethod
            = (String) columnElement.getParent().getAttribute(
                    TorqueSchemaAttributeName.ID_METHOD);
            // idMethod can not be null because it is already set by the
            // table transformer
            if (!"none".equals(idMethod))
            {
                columnElement.setAttribute(
                        TorqueSchemaAttributeName.AUTO_INCREMENT,
                        Boolean.TRUE.toString());
            }
            else
            {
                columnElement.setAttribute(
                        TorqueSchemaAttributeName.AUTO_INCREMENT,
                        Boolean.FALSE.toString());
            }
        }

        // set protected attribute
        if (columnElement.getAttribute(
                TorqueSchemaAttributeName.PROTECTED)
                == null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.PROTECTED,
                    Boolean.FALSE.toString());
        }

        // set inheritance attribute
        if (columnElement.getAttribute(
                TorqueSchemaAttributeName.INHERITANCE)
                == null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.INHERITANCE,
                    Boolean.FALSE.toString());
        }

        // set required attribute
        Object required = columnElement.getAttribute(
                TorqueSchemaAttributeName.REQUIRED);
        if (required == null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.REQUIRED,
                    Boolean.FALSE.toString());
        }

        // set primaryKey attribute
        Object primaryKey = columnElement.getAttribute(
                TorqueSchemaAttributeName.PRIMARY_KEY);
        if (primaryKey == null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.PRIMARY_KEY,
                    Boolean.FALSE.toString());
        }
    }

    protected void setDomainAttributes(
            final SourceElement columnElement,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        SqlType domain = SchemaTypeHelper.getDomain(
                columnElement,
                controllerState);
        if (domain == null)
        {
            return;
        }
        if (columnElement.getAttribute(TorqueSchemaAttributeName.TYPE)
                == null
                && domain.getSqlTypeName() != null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.TYPE,
                    domain.getSqlTypeName());
        }
        if (columnElement.getAttribute(TorqueSchemaAttributeName.DEFAULT)
                == null
                && domain.getDefaultValue() != null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.DEFAULT,
                    domain.getDefaultValue());
        }
        if (columnElement.getAttribute(TorqueSchemaAttributeName.SIZE) == null
                && domain.getSize() != null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.SIZE,
                    domain.getSize());
        }
        if (columnElement.getAttribute(TorqueSchemaAttributeName.SCALE) == null
                && domain.getScale() != null)
        {
            columnElement.setAttribute(
                    TorqueSchemaAttributeName.SCALE,
                    domain.getScale());
        }
    }

    /**
     * Sets the attributes getterAccessModifier and setterAccessModifer
     * on the column element.
     *
     * @param columnElement the column element, not null.
     */
    protected void setAccessModifierAttributes(final SourceElement columnElement)
    {
        boolean isProtected = "true".equals(
                columnElement.getAttribute(
                        TorqueSchemaAttributeName.PROTECTED));

        String accessModifier;
        if (isProtected)
        {
            accessModifier = "protected";
        }
        else
        {
            accessModifier = "public";
        }
        columnElement.setAttribute(
                JavaFieldAttributeName.GETTER_ACCESS_MODIFIER,
                accessModifier);
        columnElement.setAttribute(
                JavaFieldAttributeName.SETTER_ACCESS_MODIFIER,
                accessModifier);
    }

    /**
     * Checks that the name of the column element is correct.
     *
     * @param columnElement the column element, not null.
     *
     * @throws IllegalArgumentException if the element name is wrong.
     */
    protected void checkElementName(final SourceElement columnElement)
    {
        if (!TorqueSchemaElementName.COLUMN.getName().equals(
                columnElement.getName()))
        {
            throw new IllegalArgumentException("Illegal element Name "
                    + columnElement.getName());
        }
    }

    /**
     * Checks that the name attribute exists on the column element.
     *
     * @param columnElement the column element, not null.
     *
     * @throws SourceTransformerException if the name attribute does not exist.
     */
    protected void checkColumnNameExists(final SourceElement columnElement)
            throws SourceTransformerException
    {
        String columnName
        = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.NAME);
        if (columnName == null)
        {
            throw new SourceTransformerException("The attribute "
                    + TorqueSchemaAttributeName.NAME.getName()
                    + " on element "
                    + columnElement.getName()
                    + " is null");
        }
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
     * Sets the peerColumnName attribute of the column element if it is not
     * already set.
     *
     * @param columnElement the column element, not null.
     */
    protected void setPeerColumnNameAttribute(final SourceElement columnElement)
    {
        if (columnElement.getAttribute(ColumnAttributeName.PEER_COLUMN_NAME)
                != null)
        {
            return;
        }
        String columnName = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.NAME);
        String peerColumnName = columnName.toUpperCase();
        if (RESERVED_CONSTANT_NAMES.contains(peerColumnName))
        {
            peerColumnName = "_" +  peerColumnName;
        }
        columnElement.setAttribute(
                ColumnAttributeName.PEER_COLUMN_NAME,
                peerColumnName);
    }

    /**
     * Sets the qualifiedColumnName attribute of the column element
     * if it is not already set.
     *
     * @param columnElement the column element, not null.
     */
    protected void setQualifiedColumnNameAttribute(final SourceElement columnElement)
    {
        if (columnElement.getAttribute(ColumnAttributeName.QUALIFIED_COLUMN_NAME)
                != null)
        {
            return;
        }
        String tableName = (String) columnElement.getParent().getAttribute(
                TorqueSchemaAttributeName.NAME);
        String columnName = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.NAME);
        String qualifiedColumnName
        = tableName + "." + columnName;
        columnElement.setAttribute(
                ColumnAttributeName.QUALIFIED_COLUMN_NAME,
                qualifiedColumnName);
    }

    /**
     * Sets the getterName attribute of the column element
     * if it is not already set.
     * The fieldName attribute of the column element must already be set.
     *
     * @param columnElement the column element, not null.
     * @param javaType the java type of the column, not null.
     * @param controllerState the controller state, not null.
     */
    protected void setGetterNameAttribute(
            final SourceElement columnElement,
            final JavaType javaType,
            final ControllerState controllerState)
    {
        if (columnElement.getAttribute(JavaFieldAttributeName.GETTER_NAME)
                != null)
        {
            return;
        }
        String fieldName = (String) columnElement.getAttribute(
                JavaFieldAttributeName.FIELD_NAME);
        String getterName = FieldHelper.getGetterName(
                fieldName,
                javaType.getFullClassName(),
                controllerState);
        columnElement.setAttribute(
                JavaFieldAttributeName.GETTER_NAME,
                getterName);
    }

    /**
     * Sets the setterName attribute of the column element
     * if it is not already set.
     * The fieldName attribute of the column element must already be set.
     *
     * @param columnElement the column element, not null.
     */
    protected void setSetterNameAttribute(final SourceElement columnElement)
    {
        if (columnElement.getAttribute(JavaFieldAttributeName.SETTER_NAME)
                != null)
        {
            return;
        }
        String fieldName = (String) columnElement.getAttribute(
                JavaFieldAttributeName.FIELD_NAME);
        String setterName = FieldHelper.getSetterName(fieldName);
        columnElement.setAttribute(
                JavaFieldAttributeName.SETTER_NAME,
                setterName);
    }

    /**
     * Sets the primitiveType attribute of the column element
     * if it is not already set.
     *
     * @param columnElement the column element, not null.
     * @param javaType the type of the java field corresponding to the
     *        column, not null.
     */
    protected void setPrimitiveTypeAttribute(
            final SourceElement columnElement,
            final JavaType javaType)
    {
        if (columnElement.getAttribute(ColumnAttributeName.PRIMITIVE_TYPE)
                != null)
        {
            return;
        }
        boolean primitiveFieldType = javaType.isPrimitive();
        columnElement.setAttribute(
                ColumnAttributeName.PRIMITIVE_TYPE,
                Boolean.toString(primitiveFieldType));
    }

    /**
     * Sets the numberType attribute of the column element
     * if it is not already set.
     *
     * @param columnElement the column element, not null.
     * @param javaType the type of the java field corresponding to the
     *        column, not null.
     */
    protected void setNumberTypeAttribute(
            final SourceElement columnElement,
            final JavaType javaType)
    {
        if (columnElement.getAttribute(ColumnAttributeName.NUMBER_TYPE)
                != null)
        {
            return;
        }
        boolean numberFieldType = javaType.isNumber();
        columnElement.setAttribute(
                ColumnAttributeName.NUMBER_TYPE,
                Boolean.toString(numberFieldType));
    }


    /**
     * Sets the defaultValue attribute of the column element
     * if it is not already set.
     *
     * @param columnElement the column element, not null.
     * @param javaType the type of the java field corresponding to the
     *        column, not null.
     * @param controllerState the ControllerState, not null.
     *
     * @throws SourceTransformerException if an unknown primitive type
     *         is encountered
     */
    protected void setDefaultValueAttribute(
            final SourceElement columnElement,
            final JavaType javaType,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        // special case inheritance by class name
        // In this case, the class name of the object must be written into the
        // column as default, overriding any SQL default values.
        if (TorqueSchemaInheritance.SINGLE.getValue().equals(
                columnElement.getAttribute(
                        TorqueSchemaAttributeName.INHERITANCE))
                && columnElement.getChildren(TorqueSchemaElementName.INHERITANCE)
                .isEmpty())
        {
            columnElement.setAttribute(
                    JavaFieldAttributeName.DEFAULT_VALUE,
                    "getClass().getName()");
            return;
        }

        if (columnElement.getAttribute(JavaFieldAttributeName.DEFAULT_VALUE)
                != null)
        {
            return;
        }
        String defaultAttributeValue = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.DEFAULT);

        String fieldDefaultValue;
        if (defaultAttributeValue != null)
        {
            boolean useDatabaseDefaultValue = "true".equals(
                    columnElement.getAttribute(
                            TorqueSchemaAttributeName.USE_DATABASE_DEFAULT_VALUE));
            fieldDefaultValue = javaTransformer.getDefaultValueWithDefaultSet(
                    javaType,
                    defaultAttributeValue,
                    useDatabaseDefaultValue,
                    columnElement);
        }
        else if ("true".equals(
                columnElement.getAttribute(TorqueSchemaAttributeName.VERSION)))
        {
            fieldDefaultValue = controllerState.getStringOption(
                    TemplateOptionName.OM_OPTIMISTIC_LOCKING_DEFAULT_VALUE);
        }
        else
        {
            fieldDefaultValue = javaTransformer.getDefaultValueWithoutDefaultSet(javaType);
        }
        if (!"null".equals(fieldDefaultValue)
                && Boolean.parseBoolean((String) columnElement.getAttribute(ColumnAttributeName.IS_ENUM)))
        {
            fieldDefaultValue = (String) columnElement.getAttribute(ColumnAttributeName.ENUM_CLASS_NAME)
                    + ".getByValue(" + fieldDefaultValue + ")";
        }
        columnElement.setAttribute(
                JavaFieldAttributeName.DEFAULT_VALUE,
                fieldDefaultValue);
    }


    /**
     * Sets the useDatabaseDefaultValue attribute of the column element to its
     * default "false" if it is not already set.
     *
     * @param columnElement the column element, not null.
     */
    protected void setUseDatabaseDefaultValueAttribute(
            final SourceElement columnElement)
    {
        if (columnElement.getAttribute(
                TorqueSchemaAttributeName.USE_DATABASE_DEFAULT_VALUE)
                != null)
        {
            return;
        }
        columnElement.setAttribute(
                TorqueSchemaAttributeName.USE_DATABASE_DEFAULT_VALUE,
                Boolean.toString(false));
    }

    /**
     * Sets the resultSetGetter attribute of the column element
     * if it is not already set.
     * If the resultSetGetter is a string value, it is converted to
     * a ResultSetGetter value.
     *
     * @param columnElement the column element, not null.
     * @param schemaType the schema type of the column, not null.
     */
    protected void setResultSetGetterAttribute(
            final SourceElement columnElement,
            final SchemaType schemaType)
    {
        ResultSetGetter resultSetGetter = null;
        Object originalValue = columnElement.getAttribute(
                ColumnAttributeName.RESULT_SET_GETTER);

        if (originalValue != null)
        {
            if (originalValue instanceof String)
            {
                resultSetGetter = ResultSetGetter.getByMethodName(
                        (String) originalValue);
            }
        }
        else
        {
            resultSetGetter = TypeMap.getResultSetGetter(schemaType);
        }

        if (resultSetGetter != null)
        {
            columnElement.setAttribute(
                    ColumnAttributeName.RESULT_SET_GETTER,
                    resultSetGetter);
        }
    }

    /**
     * Sets the sampleObject attribute of the column element
     * if it is not already set.
     *
     * @param columnElement the column element, not null.
     * @param schemaType the schema type of the column, not null.
     */
    protected void setSampleObjectAttribute(
            final SourceElement columnElement,
            final SchemaType schemaType)
    {
        if (columnElement.getAttribute(ColumnAttributeName.SAMPLE_OBJECT)
                != null)
        {
            return;
        }

        String sampleObject = TypeMap.getJavaObject(schemaType);
        columnElement.setAttribute(
                ColumnAttributeName.SAMPLE_OBJECT,
                sampleObject);
    }
    
}
