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

import static org.apache.torque.templates.transformer.om.OMColumnJavaTransformer.CURRENT_DATE;
import static org.apache.torque.templates.transformer.om.OMColumnJavaTransformer.CURRENT_TIME;
import static org.apache.torque.templates.transformer.om.OMColumnJavaTransformer.CURRENT_TIMESTAMP;
import static org.apache.torque.templates.transformer.om.OMColumnJavaTransformer.GET_DEFAULT_DATE_METHOD_NAME;
import static org.apache.torque.templates.transformer.om.OMColumnJavaTransformer.GET_DEFAULT_TIMESTAMP_METHOD_NAME;
import static org.apache.torque.templates.transformer.om.OMColumnJavaTransformer.GET_DEFAULT_TIME_METHOD_NAME;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.processor.string.Camelbacker;
import org.apache.torque.generator.processor.string.WrapReservedJavaWords;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.AttributeTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.TorqueSchemaIdMethod;
import org.apache.torque.templates.TorqueSchemaInheritance;
import org.apache.torque.templates.transformer.CollectAttributeSetTrueTransformer;

/**
 * Transforms the tables and views in the OM model.
 */
public class OMTableAndViewTransformer extends AttributeTransformer
{
    
    private static Logger log = LogManager.getLogger(OMTableAndViewTransformer.class);
    /** Constant for the dot. */
    private static final String DOT = ".";

    /** The transformer for the column child elements. */
    private static OMColumnTransformer columnTransformer
        = new OMColumnTransformer();;

    /** The transformer for the foreign keys referencing this table. */
    private static OMReferencingForeignKeyTableTransformer
    referencingForeignKeyTableTransformer
        = new OMReferencingForeignKeyTableTransformer();

    /** The transformer collecting the primary key columns in this table. */
    private static CollectAttributeSetTrueTransformer primaryKeyTransformer
        = new CollectAttributeSetTrueTransformer();

    /** Prevents reserved java words. */
    private static WrapReservedJavaWords reservedJavaWordsWrapper
        = new WrapReservedJavaWords();

    public OMTableAndViewTransformer() throws SourceTransformerException
    {
        super(getTransformerProperties());
    }

    /**
     * Returns the Reader to read the transformer properties from.
     *
     * @return the reader, not null.
     */
    private static Reader getTransformerProperties()
    {
        try
        {
            return new InputStreamReader(
                    OMTableAndViewTransformer.class.getResourceAsStream(
                            "OMTableTransformer.properties"),
                    "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e)
        {
            // will not happen
            throw new RuntimeException(e);
        }
    }

    @Override
    public SourceElement transform(
            Object tableModel,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        SourceElement tableElement = (SourceElement) tableModel;
        checkElementName(tableElement);
        setUnqualifiedNameAttribute(tableElement);
        setJavaNameAttribute(tableElement, controllerState);
        setFieldNameAttribute(tableElement);
        setIdMethodAttribute(tableElement);
        setSequenceAttributeIfNecessary(tableElement);
        setBaseClass(tableElement, controllerState);

        // sets all the attributes defined in OMTableTransformer.properties
        super.transform(tableElement, controllerState);

        setManagerClassNameAttributes(tableElement, controllerState);
        setPeerImplFieldNameAttribute(tableElement);
        setPeerImplGetterAttribute(tableElement);
        setPeerImplSetterAttribute(tableElement);

        setSaveAttributes(tableElement, controllerState);

        createInheritanceColumnChildIfNecessary(tableElement);
        primaryKeyTransformer.transform(tableElement, controllerState,
                TorqueSchemaElementName.COLUMN,
                TorqueSchemaAttributeName.PRIMARY_KEY,
                TableChildElementName.PRIMARY_KEYS);

        // position is one based
        AtomicInteger columnPosition = new AtomicInteger(1);
        for (SourceElement element : tableElement.getChildren(
                TorqueSchemaElementName.COLUMN))
        {
            columnTransformer.transform(
                    element,
                    controllerState,
                    columnPosition.get());
            columnPosition.incrementAndGet();
        }

        setCreateDefaultDateMethodsAttributes(tableElement);

        referencingForeignKeyTableTransformer.transform(
                tableElement,
                controllerState);
        return tableElement;
    }

    /**
     * Checks that the table element has the correct element name.
     *
     * @param tableElement the table element to check, not null.
     *
     * @throws IllegalArgumentException if the name of the element is wrong.
     */
    private void checkElementName(SourceElement tableElement)
    {
        if (!TorqueSchemaElementName.TABLE.getName().equals(
                tableElement.getName())
                && !TorqueSchemaElementName.VIEW.getName().equals(
                        tableElement.getName()))
        {
            throw new IllegalArgumentException("Illegal element Name "
                    + tableElement.getName());
        }
    }

    /**
     * Sets the javaName attribute of the table element, if not
     * already set.
     *
     * @param tableElement the table element, not null.
     * @param controllerState the controller state, not null.
     *
     * @throws SourceTransformerException if both attributes javaName and name
     *         are not set on the tableElement.
     */
    public static void setJavaNameAttribute(
            SourceElement tableElement,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        // set javaName attribute
        Object javaName = tableElement.getAttribute(
                TorqueSchemaAttributeName.JAVA_NAME);
        if (javaName == null)
        {
            Object inputName = tableElement.getAttribute(
                    TorqueSchemaAttributeName.NAME);
            if (inputName == null)
            {
                throw new SourceTransformerException("The required attribute "
                        + TorqueSchemaAttributeName.NAME
                        + " on element "
                        + tableElement.getName()
                        + " is null");
            }
            Camelbacker javaNameCamelbacker = new Camelbacker();
            javaNameCamelbacker.setRemoveWithoutUppercase("");
            javaNameCamelbacker.setRemoveWithUppercase(
                    javaNameCamelbacker.getRemoveWithUppercase() + DOT);
            if (!controllerState.getBooleanOption(
                    TemplateOptionName.OM_RETAIN_SCHEMA_NAMES_IN_JAVA_NAME))
            {
                javaNameCamelbacker.setIgnorePartBefore(DOT);
            }
            javaName = javaNameCamelbacker.process(inputName.toString());
            tableElement.setAttribute(
                    TorqueSchemaAttributeName.JAVA_NAME,
                    javaName);
        }
        log.debug("javaName: {}", javaName);
    }

    protected void setUnqualifiedNameAttribute(SourceElement tableElement)
    {
        String name = (String)
                tableElement.getAttribute(TorqueSchemaAttributeName.NAME);
        String unqualifiedName = name;
        if (StringUtils.contains(name, DOT))
        {
            unqualifiedName = name.substring(name.indexOf(DOT) + 1);
        }
        tableElement.setAttribute(
                TableAttributeName.UNQUALIFIED_NAME,
                unqualifiedName);
    }

    protected void setIdMethodAttribute(SourceElement tableElement)
            throws SourceTransformerException
    {
        Object idMethod = tableElement.getAttribute(
                TorqueSchemaAttributeName.ID_METHOD);
        if (idMethod == null)
        {
            idMethod = tableElement.getParent().getAttribute(
                    TorqueSchemaAttributeName.DEFAULT_ID_METHOD);
            if (idMethod == null)
            {
                throw new SourceTransformerException("The attribute "
                        + TorqueSchemaAttributeName.DEFAULT_ID_METHOD
                        + " is not set on the database element "
                        + " and no id method is given on table "
                        + tableElement.getName());
            }
            tableElement.setAttribute(
                    TorqueSchemaAttributeName.ID_METHOD,
                    idMethod);
        }
    }

    protected void setSequenceAttributeIfNecessary(SourceElement tableElement)
    {
        // set sequence attribute
        if (!TorqueSchemaIdMethod.NATIVE.getName().equals(
                tableElement.getAttribute(TorqueSchemaAttributeName.ID_METHOD)))
        {
            return;
        }

        List<SourceElement> idMethodParams = tableElement.getChildren(
                TorqueSchemaElementName.ID_METHOD_PARAMETER);
        if (idMethodParams.isEmpty()
                && tableElement.getAttribute(TableAttributeName.SEQUENCE_NAME) == null)
        {
            tableElement.setAttribute(
                    TableAttributeName.SEQUENCE_NAME,
                    (String) tableElement.getAttribute(
                            TorqueSchemaAttributeName.NAME)
                    + "_SEQ");
        }
        else
        {
            tableElement.setAttribute(
                    TableAttributeName.SEQUENCE_NAME,
                    idMethodParams.get(0).getAttribute(
                            TorqueSchemaAttributeName.VALUE.getName()));
        }
    }


    protected void setManagerClassNameAttributes(
            SourceElement tableElement,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        // Managers must be named after an interface if there is one.
        String interfaceName = (String) tableElement.getAttribute(
                TorqueSchemaAttributeName.INTERFACE);
        if (interfaceName == null)
        {
            String managerClassName = controllerState.getStringOption(
                    TemplateOptionName.OM_MANAGER_CLASS_NAME_PREFIX)
                    + tableElement.getAttribute(TorqueSchemaAttributeName.JAVA_NAME)
                    + controllerState.getStringOption(
                            TemplateOptionName.OM_MANAGER_CLASS_NAME_SUFFIX);
            tableElement.setAttribute(
                    TableAttributeName.MANAGER_CLASS_NAME,
                    managerClassName);
            String baseManagerClassName = controllerState.getStringOption(
                    "torque.om.className.baseManagerClassNamePrefix")
                    + tableElement.getAttribute(TorqueSchemaAttributeName.JAVA_NAME)
                    + controllerState.getStringOption(
                            "torque.om.className.baseManagerClassNameSuffix");
            tableElement.setAttribute(
                    TableAttributeName.BASE_MANAGER_CLASS_NAME,
                    baseManagerClassName);
        }
        else
        {
            if (StringUtils.isBlank(interfaceName))
            {
                throw new SourceTransformerException("The attribute "
                        + TorqueSchemaAttributeName.INTERFACE
                        + " is blank on table "
                        + tableElement.getName());
            }
            int dotPosition = interfaceName.lastIndexOf(".");
            if (dotPosition != -1)
            {
                interfaceName = interfaceName.substring(dotPosition + 1);
            }
            if (StringUtils.isBlank(interfaceName))
            {
                throw new SourceTransformerException("The attribute "
                        + TorqueSchemaAttributeName.INTERFACE
                        + " ends with a dot on table "
                        + tableElement.getName());
            }

            // first character upper case
            if (interfaceName.length() == 1)
            {
                interfaceName = interfaceName.toUpperCase();
            }
            else
            {
                interfaceName = interfaceName.substring(0, 1).toUpperCase()
                        + interfaceName.substring(1);
            }

            if (tableElement.getAttribute(TableAttributeName.MANAGER_CLASS_NAME)
                    == null)
            {
                String managerClassName = controllerState.getOption(
                        TemplateOptionName.OM_MANAGER_CLASS_NAME_PREFIX)
                        + interfaceName
                        + controllerState.getOption(
                                TemplateOptionName.OM_MANAGER_CLASS_NAME_SUFFIX);
                tableElement.setAttribute(
                        TableAttributeName.MANAGER_CLASS_NAME,
                        managerClassName);
            }
            if (tableElement.getAttribute(
                    TableAttributeName.BASE_MANAGER_CLASS_NAME) == null)
            {
                String baseManagerClassName = controllerState.getOption(
                        "torque.om.className.baseManagerClassNamePrefix")
                        + interfaceName
                        + controllerState.getOption(
                                "torque.om.className.baseManagerClassNameSuffix");
                tableElement.setAttribute(
                        TableAttributeName.BASE_MANAGER_CLASS_NAME,
                        baseManagerClassName);
            }
        }
    }

    public void createInheritanceColumnChildIfNecessary(
            SourceElement tableElement)
                    throws SourceTransformerException
    {
        boolean inheritanceFound = false;
        for (SourceElement columnElement : tableElement.getChildren(
                TorqueSchemaElementName.COLUMN))
        {
            if (TorqueSchemaInheritance.SINGLE.getValue().equals(
                    columnElement.getAttribute(
                            TorqueSchemaAttributeName.INHERITANCE)))
            {
                if (inheritanceFound)
                {
                    throw new SourceTransformerException(
                            "more than one column with "
                                    + TorqueSchemaAttributeName.INHERITANCE
                                    + " set to \"single\" found in table "
                                    + tableElement.getAttribute(
                                            tableElement.getName()));
                }
                SourceElement inheritanceColumnElement
                    = new SourceElement(
                        TableChildElementName.INHERITANCE_COLUMN);
                inheritanceColumnElement.getChildren().add(columnElement);
                tableElement.getChildren().add(inheritanceColumnElement);
                inheritanceFound = true;
            }
        }
    }

    /**
     * Sets the base class attribute on the table element if it is not
     * already set.
     *
     * @param tableElement the table attribute to process, not null.
     * @param controllerState the controller state, not null.
     */
    private void setBaseClass(
            SourceElement tableElement,
            ControllerState controllerState)
    {
        Object baseClass = tableElement.getAttribute(
                TorqueSchemaAttributeName.BASE_CLASS);
        if (baseClass == null)
        {
            baseClass = tableElement.getParent().getAttribute(
                    TorqueSchemaAttributeName.BASE_CLASS);
        }
        if (baseClass == null)
        {
            baseClass = controllerState.getOption(
                    TemplateOptionName.OM_DB_OBJECT_DEFAULT_BASE_CLASS);
        }
        if (baseClass == null)
        {
            baseClass = "";
        }
        tableElement.setAttribute(
                TorqueSchemaAttributeName.BASE_CLASS,
                baseClass);
    }

    /**
     * Sets the fieldName attribute of the table element if it is not
     * already set. The field name can be used to contain a database object
     * corresponding to the table.
     * The javaName attribute of the column must be set.
     *
     * @param tableElement the table element, not null.
     */
    protected void setFieldNameAttribute(SourceElement tableElement)
    {
        if (tableElement.getAttribute(JavaFieldAttributeName.FIELD_NAME)
                != null)
        {
            return;
        }
        String javaName = (String) tableElement.getAttribute(
                TorqueSchemaAttributeName.JAVA_NAME);
        String fieldName = StringUtils.uncapitalize(javaName);
        fieldName = reservedJavaWordsWrapper.process(fieldName);
        tableElement.setAttribute(
                JavaFieldAttributeName.FIELD_NAME,
                fieldName);
    }

    /**
     * Sets the peerImplFieldName attribute of the table element if it is not
     * already set. The field name can be used to contain a peer object
     * corresponding to the table.
     * The peerImplClassName attribute of the column must be already set
     * when this method is called.
     *
     * @param tableElement the table element, not null.
     */
    protected void setPeerImplFieldNameAttribute(SourceElement tableElement)
    {
        if (tableElement.getAttribute(TableAttributeName.PEER_IMPL_FIELD_NAME)
                != null)
        {
            return;
        }
        String peerImplClassName = (String) tableElement.getAttribute(
                TableAttributeName.PEER_IMPL_CLASS_NAME);
        String fieldName = StringUtils.uncapitalize(peerImplClassName);
        fieldName = reservedJavaWordsWrapper.process(fieldName);
        tableElement.setAttribute(
                TableAttributeName.PEER_IMPL_FIELD_NAME,
                fieldName);
    }

    /**
     * Sets the peerImplGetter attribute of the table element if it is not
     * already set.
     * The peerImplClassName attribute of the column must be already set
     * when this method is called.
     *
     * @param tableElement the table element, not null.
     */
    public static void setPeerImplGetterAttribute(SourceElement tableElement)
    {
        if (tableElement.getAttribute(TableAttributeName.PEER_IMPL_GETTER)
                != null)
        {
            return;
        }
        String peerImplClassName = (String) tableElement.getAttribute(
                TableAttributeName.PEER_IMPL_CLASS_NAME);
        String getter = "get" + peerImplClassName;
        getter = reservedJavaWordsWrapper.process(getter);
        tableElement.setAttribute(
                TableAttributeName.PEER_IMPL_GETTER,
                getter);
    }

    /**
     * Sets the peerImplSetter attribute of the table element if it is not
     * already set.
     * The peerImplClassName attribute of the column must be already set
     * when this method is called.
     *
     * @param tableElement the table element, not null.
     */
    protected void setPeerImplSetterAttribute(SourceElement tableElement)
    {
        if (tableElement.getAttribute(TableAttributeName.PEER_IMPL_SETTER)
                != null)
        {
            return;
        }
        String peerImplClassName = (String) tableElement.getAttribute(
                TableAttributeName.PEER_IMPL_CLASS_NAME);
        String setter = "set" + peerImplClassName;
        setter = reservedJavaWordsWrapper.process(setter);
        tableElement.setAttribute(
                TableAttributeName.PEER_IMPL_SETTER,
                setter);
    }

    protected void setSaveAttributes(
            SourceElement tableElement,
            ControllerState controllerState)
    {
        // check save method location definitions (prio descending):
        //   - Attribute saveMethodsInDbObjects in table
        //   - option TemplateOptionName.OM_SAVE_METHODS_IN_DB_OBJECTS
        String saveMethodLocationOptionName
        = TemplateOptionName.OM_SAVE_METHODS_IN_DB_OBJECTS.getName();
        String saveMethodLocationAttributeName
        = saveMethodLocationOptionName.substring(
                saveMethodLocationOptionName.lastIndexOf('.') + 1);
        boolean saveMethodsInDbObjects;
        if (tableElement.getAttribute(saveMethodLocationAttributeName) != null)
        {
            saveMethodsInDbObjects = Boolean.parseBoolean(
                    tableElement.getAttribute(saveMethodLocationAttributeName)
                    .toString());
        }
        else
        {
            saveMethodsInDbObjects = controllerState.getBooleanOption(
                    TemplateOptionName.OM_SAVE_METHODS_IN_DB_OBJECTS);
        }

        if (!saveMethodsInDbObjects)
        {
            tableElement.setAttribute(
                    TableAttributeName.SAVE_METHOD_INPUT_TYPE,
                    tableElement.getAttribute(
                            TableAttributeName.DB_OBJECT_CLASS_NAME));
            tableElement.setAttribute(
                    TableAttributeName.SAVE_METHOD_TO_SAVE_VARIABLE,
                    "toSave");
        }
    }

    protected void setCreateDefaultDateMethodsAttributes(
            SourceElement tableElement)
    {
        for (SourceElement column : tableElement.getChildren(
                TorqueSchemaElementName.COLUMN))
        {
            log.trace("column.getAttribute(JavaFieldAttributeName.FIELD_TYPE): {}",column.getAttribute(JavaFieldAttributeName.FIELD_TYPE));
            log.debug("column: {}",column);
            if ("java.util.Date".equals(
                    column.getAttribute(JavaFieldAttributeName.FIELD_TYPE)))
            {
                String defaultValue = Objects.toString(column.getAttribute(
                        TorqueSchemaAttributeName.DEFAULT));
                if (CURRENT_DATE.equalsIgnoreCase(defaultValue))
                {
                    if (tableElement.getAttribute(
                            TableAttributeName.GET_CURRENT_DATE_METHOD_NAME)
                            == null)
                    {
                        tableElement.setAttribute(
                                TableAttributeName.GET_CURRENT_DATE_METHOD_NAME,
                                GET_DEFAULT_DATE_METHOD_NAME);
                    }
                }
                else if (CURRENT_TIME.equalsIgnoreCase(defaultValue))
                {
                    if (tableElement.getAttribute(
                            TableAttributeName.GET_CURRENT_TIME_METHOD_NAME)
                            == null)
                    {
                        tableElement.setAttribute(
                                TableAttributeName.GET_CURRENT_TIME_METHOD_NAME,
                                GET_DEFAULT_TIME_METHOD_NAME);
                    }
                }
                else if (CURRENT_TIMESTAMP.equalsIgnoreCase(defaultValue)
                        && tableElement.getAttribute(
                                TableAttributeName.GET_CURRENT_TIMESTAMP_METHOD_NAME)
                        == null)
                {
                    tableElement.setAttribute(
                            TableAttributeName.GET_CURRENT_TIMESTAMP_METHOD_NAME,
                            GET_DEFAULT_TIMESTAMP_METHOD_NAME);
                }
            }
        }
    }
}
