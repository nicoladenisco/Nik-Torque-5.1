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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;

/**
 * Sets the foreign tables for each of the foreign keys,
 * and define variables, getters and setters for the complexObjectModel.
 *
 * So the source elements are (attributes not shown)
 * foreign-key
 *   reference
 *   reference
 *   ...
 *
 * and the result is
 * foreign-key
 *   reference
 *     local-column
 *       column
 *     foreign-column
 *       column
 *   reference
 *     local-column
 *       column
 *     foreign-column
 *       column
 *   ...
 *   local-field (properties for the field on the local table's database object
 *                referencing the foreign database object)
 *   foreign-field (properties for the field on the foreign table's
 *                  database object referencing the local database objects)
 *   table (the foreign referenced table)
 *
 * On running this transformer, the javaName Attribute on the columns
 * must be set properly.
 */
public class OMForeignKeyTransformer
{
    public void transform(
            final SourceElement foreignKey,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (!TorqueSchemaElementName.FOREIGN_KEY.getName().equals(
                foreignKey.getName()))
        {
            throw new IllegalArgumentException("Illegal element Name "
                    + foreignKey.getName());
        }

        SourceElement localTable = foreignKey.getParent();
        SourceElement database = localTable.getParent();

        String foreignTableName = (String) foreignKey.getAttribute(
                TorqueSchemaAttributeName.FOREIGN_TABLE);
        SourceElement foreignTable
        = FindHelper.findTable(database, foreignTableName, true);
        foreignKey.getChildren().add(foreignTable);

        for (SourceElement reference : foreignKey.getChildren(
                TorqueSchemaElementName.REFERENCE))
        {
            createLocalElementForReference(localTable, reference);
            createForeignElementForReference(foreignTable, reference);
        }

        StringBuilder localParentPath = new StringBuilder();
        getParentPath(localTable, localParentPath);

        StringBuilder foreignParentPath = new StringBuilder();
        getParentPath(foreignTable, foreignParentPath);
        // create reference only if this table is not in a external-schema
        // element of the foreign table's database
        if (foreignParentPath.toString().startsWith(localParentPath.toString()))
        {
            addLocalField(foreignKey, controllerState);
            addLocalFieldInBean(foreignKey, controllerState);
        }

        // create backreference only if the foreign table is not
        // in a external-schema element of this table's database
        if (localParentPath.toString().startsWith(foreignParentPath.toString()))
        {
            addForeignField(foreignKey, controllerState);
            addForeignFieldInBean(foreignKey, controllerState);
        }

        setForeignKeyAttributes(foreignKey, controllerState);
    }

    /**
     * Second pass of the transformation. Performs all steps which require that
     * the first pass is complete.
     *
     * @param foreignKey the element to transform, not null.
     * @param controllerState the controller state, not null.
     *
     * @throws SourceTransformerException if the transformation fails
     */
    public void transformSecondPass(
            final SourceElement foreignKey,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        modifyForeignFieldSecondPass(foreignKey, controllerState);
    }

    /**
     * Create a foreign-field element which describes a field
     * in the foreign table referencing the local table.
     * As more than one foreign object can point
     * to this object, the filed needs to be a collection.
     *
     * @param foreignKey the foreign-key element to which the foreign-field
     *        element should be added.
     * @param controllerState the current controller state.
     */
    private void addForeignField(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement localTable = foreignKey.getParent();
        SourceElement foreignTable = foreignKey.getChild(
                TorqueSchemaElementName.TABLE);
        String referencedBySuffix = getForeignReferencedBySuffix(
                foreignKey, controllerState);
        {
            // the field name for the variable used
            String foreignFieldName = (String) controllerState.getOption(
                    TemplateOptionName.OM_FOREIGN_FIELD_NAME_PREFIX)
                    + localTable.getAttribute(
                            TableAttributeName.DB_OBJECT_CLASS_NAME)
                    + controllerState.getOption(
                            TemplateOptionName.OM_FOREIGN_FIELD_NAME_SUFFIX)
                    + referencedBySuffix;
            // the field name to create the name of the getter and setter
            String getterSetterFieldName
            = (String) localTable.getAttribute(
                    TableAttributeName.DB_OBJECT_CLASS_NAME)
            + referencedBySuffix;
            SourceElement foreignFieldElement
                = new SourceElement(
                    ForeignKeyChildElementName.FOREIGN_FIELD);

            foreignFieldElement.setAttribute(
                    JavaFieldAttributeName.FIELD_NAME,
                    foreignFieldName);
            foreignFieldElement.setAttribute(
                    JavaFieldAttributeName.FIELD_ACCESS_MODIFIER,
                    "protected");
            {
                // the field name to cache the used Criteria
                String criteriaFieldName = "last"
                        + StringUtils.capitalize(getterSetterFieldName)
                        + "Criteria";
                foreignFieldElement.setAttribute(
                        ForeignKeyChildAttributeName
                        .FOREIGN_COLUMN_CRITERIA_CACHE_FIELD,
                        criteriaFieldName);
            }

            String fieldContainedType = (String) localTable.getAttribute(
                    TorqueSchemaAttributeName.JAVA_NAME);
            foreignFieldElement.setAttribute(
                    JavaFieldAttributeName.FIELD_CONTAINED_TYPE,
                    fieldContainedType);

            String fieldType = (String) controllerState.getOption(
                    TemplateOptionName.OM_FOREIGN_FIELD_TYPE)
                    + "<" + fieldContainedType + ">";
            foreignFieldElement.setAttribute(
                    JavaFieldAttributeName.FIELD_TYPE,
                    fieldType);

            foreignFieldElement.setAttribute(
                    JavaFieldAttributeName.DEFAULT_VALUE,
                    "null");
            {
                String getterName = FieldHelper.getGetterName(
                        getterSetterFieldName,
                        fieldType,
                        controllerState)
                        + "s";
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.GETTER_NAME,
                        getterName);
            }
            {
                String setterName = FieldHelper.getSetterName(
                        getterSetterFieldName)
                        + "s";
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.SETTER_NAME,
                        setterName);
            }
            {
                String adderName = FieldHelper.getAdderName(
                        getterSetterFieldName,
                        controllerState);
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.ADDER_NAME,
                        adderName);
            }
            {
                String adderName = FieldHelper.getResetterName(
                        getterSetterFieldName,
                        controllerState);
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.RESETTER_NAME,
                        adderName);
            }
            {
                String initializerName = FieldHelper.getInitializerName(
                        getterSetterFieldName,
                        controllerState);
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.INITIALIZER_NAME,
                        initializerName);
            }
            {
                String isInitializedName = FieldHelper.getIsInitializedName(
                        getterSetterFieldName,
                        controllerState);
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.IS_INITIALIZED_NAME,
                        isInitializedName);
            }
            {
                String initType = (String) controllerState.getOption(
                        TemplateOptionName.OM_FOREIGN_FIELD_INIT_TYPE)
                        + "<" + fieldContainedType + ">";
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.INITIALIZER_TYPE,
                        initType);

            }
            {
                String fillerName = FieldHelper.getFillerName(
                        getterSetterFieldName,
                        "",
                        controllerState);
                foreignFieldElement.setAttribute(
                        JavaFieldAttributeName.FILLER_NAME,
                        fillerName);
            }
            {
                String setAndSaveMethodName
                = FieldHelper.getSetAndSaveMethodName(
                        getterSetterFieldName,
                        "",
                        controllerState);
                foreignFieldElement.setAttribute(
                        "setAndSaveMethodName",
                        setAndSaveMethodName);
            }
            {
                // Name for the doSelectJoinXXX method in the Peer Class
                // of the foreign table.
                String peerJoinSelectMethodName
                = "doSelectJoin"
                        + foreignTable.getAttribute(
                                TorqueSchemaAttributeName.JAVA_NAME)
                        + referencedBySuffix;
                foreignFieldElement.setAttribute(
                        ForeignKeyChildAttributeName
                        .PEER_JOIN_SELECT_METHOD,
                        peerJoinSelectMethodName);
            }
            {
                // Name for the doSelectJoinAllExceptXXX method
                // in the Peer Class of the foreign table.
                String peerJoinAllExceptSelectMethodName
                = "doSelectJoinAllExcept"
                        + foreignTable.getAttribute(
                                TorqueSchemaAttributeName.JAVA_NAME)
                        + referencedBySuffix;
                foreignFieldElement.setAttribute(
                        ForeignKeyChildAttributeName
                        .PEER_JOIN_ALL_EXCEPT_SELECT_METHOD,
                        peerJoinAllExceptSelectMethodName);
            }
            foreignKey.getChildren().add(foreignFieldElement);
        }
    }

    /**
     * Adds the the filler attribute to a foreign-field element.
     * This method checks for naming conflicts with the local fields of the
     * referenced table and modifies the filler name accordingly if a conflict
     * is found.
     * The name of all filler attributes of the local-field elements
     * must be set when this method is called.
     *
     * @param foreignKey the foreign-key element which foreign-field
     *        element should be modified.
     * @param controllerState the current controller state.
     */
    private void modifyForeignFieldSecondPass(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement foreignFieldElement
        = foreignKey.getChild(ForeignKeyChildElementName.FOREIGN_FIELD);
        if (foreignFieldElement == null)
        {
            return;
        }
        String setterName = (String) foreignFieldElement.getAttribute(
                JavaFieldAttributeName.SETTER_NAME);
        // setter gets a "s" appended, remove that
        String regularSetterName
        = setterName.substring(0, setterName.length() - 1);
        String fieldName
        = FieldHelper.getFieldNameFromSetterName(regularSetterName);
        String fillerName = FieldHelper.getFillerName(
                fieldName,
                "",
                controllerState);
        // check whether there is a local-field in the referenced table
        // which has the same filler name
        SourceElement referencedTable = foreignKey.getChild(
                TorqueSchemaElementName.TABLE);
        boolean fillerNamingConflictFound = false;
        for (SourceElement referencedTableForeignKey
                : referencedTable.getChildren(
                        TorqueSchemaElementName.FOREIGN_KEY))
        {
            SourceElement referencedTableLocalField
            = referencedTableForeignKey.getChild(
                    ForeignKeyChildElementName.LOCAL_FIELD);
            if (referencedTableLocalField == null)
            {
                continue;
            }
            String referencedTableFiller
            = (String) referencedTableLocalField.getAttribute(
                    JavaFieldAttributeName.FILLER_NAME);
            if (fillerName.equals(referencedTableFiller))
            {
                fillerNamingConflictFound = true;
                break;
            }
        }
        if (fillerNamingConflictFound)
        {
            fillerName = FieldHelper.getFillerName(
                    fieldName,
                    (String) controllerState.getOption(
                            TemplateOptionName.OM_FILLER_REFERENCING_DISTICTION),
                    controllerState);
        }
        foreignFieldElement.setAttribute(
                JavaFieldAttributeName.FILLER_NAME,
                fillerName);
    }

    /**
     * Create a foreign-field-in-bean element which describes the referenced
     * instances of the local table bean object in the foreign table bean class.
     * As more than one foreign bean can point to this bean,
     * the field needs to be a collection.
     *
     * @param foreignKey the foreign-key element
     *        to which the foreign-field-in-bean element should be added.
     * @param controllerState the current controller state.
     */
    private void addForeignFieldInBean(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement localTable = foreignKey.getParent();
        String referencedBySuffix = getForeignReferencedBySuffix(
                foreignKey, controllerState);
        // the field name to create the name of the bean getter and setter
        String beanGetterSetterFieldName
        = (String) localTable.getAttribute(
                TableAttributeName.BEAN_CLASS_NAME)
        + referencedBySuffix;

        // the field name for the variable used
        String foreignFieldInBeanName
        = (String) controllerState.getOption(
                TemplateOptionName.OM_FOREIGN_FIELD_NAME_PREFIX)
        + localTable.getAttribute(
                TableAttributeName.BEAN_CLASS_NAME)
        + controllerState.getOption(
                TemplateOptionName.OM_FOREIGN_FIELD_NAME_SUFFIX)
        + referencedBySuffix;
        SourceElement foreignFieldInBeanElement
            = new SourceElement(
                ForeignKeyChildElementName.FOREIGN_FIELD_IN_BEAN);

        foreignFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.FIELD_NAME,
                foreignFieldInBeanName);
        foreignFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.FIELD_ACCESS_MODIFIER,
                "protected");

        String fieldContainedType = (String) localTable.getAttribute(
                TableAttributeName.BEAN_CLASS_NAME);
        foreignFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.FIELD_CONTAINED_TYPE,
                fieldContainedType);

        String fieldType = (String) controllerState.getOption(
                TemplateOptionName.OM_FOREIGN_FIELD_TYPE)
                + "<" + fieldContainedType + ">";
        foreignFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.FIELD_TYPE,
                fieldType);

        {
            String initType = (String) controllerState.getOption(
                    TemplateOptionName.OM_FOREIGN_FIELD_INIT_TYPE)
                    + "<" + fieldContainedType + ">";
            foreignFieldInBeanElement.setAttribute(
                    JavaFieldAttributeName.INITIALIZER_TYPE,
                    initType);
        }

        foreignFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.DEFAULT_VALUE,
                "null");
        {
            String getterName = FieldHelper.getGetterName(
                    beanGetterSetterFieldName,
                    fieldType,
                    controllerState)
                    + "s";
            foreignFieldInBeanElement.setAttribute(
                    JavaFieldAttributeName.GETTER_NAME,
                    getterName);
        }
        {
            String setterName = FieldHelper.getSetterName(
                    beanGetterSetterFieldName)
                    + "s";
            foreignFieldInBeanElement.setAttribute(
                    JavaFieldAttributeName.SETTER_NAME,
                    setterName);
        }
        foreignKey.getChildren().add(foreignFieldInBeanElement);
    }

    /**
     * Create a local-field-in-bean element which describes the referenced
     * instance of the foreign table bean object in the local table bean class.
     * This field will store one instance of the foreign table's bean object.
     *
     * @param foreignKey the foreign-key element
     *        to which the local-field-in-bean element should be added.
     * @param controllerState the current controller state.
     */
    private void addLocalFieldInBean(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement foreignTable = foreignKey.getChild(
                TorqueSchemaElementName.TABLE);
        String referencedBySuffix = getLocalReferencedBySuffix(
                foreignKey, controllerState);
        String beanGetterSetterFieldName
        = (String) foreignTable.getAttribute(
                TableAttributeName.BEAN_CLASS_NAME)
        + referencedBySuffix;
        // the field name for the variable used
        String localBeanFieldName = (String) controllerState.getOption(
                TemplateOptionName.OM_LOCAL_FIELD_NAME_PREFIX)
                + foreignTable.getAttribute(
                        TableAttributeName.BEAN_CLASS_NAME)
                + controllerState.getOption(
                        TemplateOptionName.OM_LOCAL_FIELD_NAME_SUFFIX)
                + referencedBySuffix;
        SourceElement localFieldInBeanElement
            = new SourceElement(
                ForeignKeyChildElementName.LOCAL_FIELD_IN_BEAN);

        localFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.FIELD_NAME,
                localBeanFieldName.toString());

        String fieldType = (String) foreignTable.getAttribute(
                TableAttributeName.BEAN_CLASS_NAME);
        localFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.FIELD_TYPE,
                fieldType);
        localFieldInBeanElement.setAttribute(
                JavaFieldAttributeName.DEFAULT_VALUE,
                "null");
        {
            String getterName = FieldHelper.getGetterName(
                    beanGetterSetterFieldName,
                    fieldType,
                    controllerState);
            localFieldInBeanElement.setAttribute(
                    JavaFieldAttributeName.GETTER_NAME,
                    getterName);
        }
        {
            String setterName = FieldHelper.getSetterName(
                    beanGetterSetterFieldName);
            localFieldInBeanElement.setAttribute(
                    JavaFieldAttributeName.SETTER_NAME,
                    setterName);
        }
        foreignKey.getChildren().add(localFieldInBeanElement);
    }

    /**
     * Create a local-field element which describes a field in the local table
     * referencing the foreign table.
     * This field will store one instance of the foreign table's java object.
     *
     * @param foreignKey the foreign-key element to which the local-field
     *        element should be added.
     * @param controllerState the current controller state.
     */
    private void addLocalField(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement foreignTable = foreignKey.getChild(
                TorqueSchemaElementName.TABLE);
        String referencedBySuffix = getLocalReferencedBySuffix(
                foreignKey, controllerState);
        // the field name for the variable used
        String localFieldName = (String) controllerState.getOption(
                TemplateOptionName.OM_LOCAL_FIELD_NAME_PREFIX)
                + foreignTable.getAttribute(
                        TableAttributeName.DB_OBJECT_CLASS_NAME)
                + controllerState.getOption(
                        TemplateOptionName.OM_LOCAL_FIELD_NAME_SUFFIX)
                + referencedBySuffix;
        // the field name to create the getter and setter names
        String getterSetterFieldName
        = (String) foreignTable.getAttribute(
                TableAttributeName.DB_OBJECT_CLASS_NAME)
        + referencedBySuffix;

        SourceElement localFieldElement
            = new SourceElement(
                ForeignKeyChildElementName.LOCAL_FIELD);

        localFieldElement.setAttribute(
                JavaFieldAttributeName.FIELD_NAME,
                localFieldName.toString());
        localFieldElement.setAttribute(
                JavaFieldAttributeName.PROPERTY_NAME,
                getterSetterFieldName.toString());

        String fieldType = (String) foreignTable.getAttribute(
                TableAttributeName.DB_OBJECT_CLASS_NAME);
        localFieldElement.setAttribute(
                JavaFieldAttributeName.FIELD_TYPE,
                fieldType);
        localFieldElement.setAttribute(
                JavaFieldAttributeName.DEFAULT_VALUE,
                "null");
        {
            String getterName = FieldHelper.getGetterName(
                    getterSetterFieldName,
                    fieldType,
                    controllerState);
            localFieldElement.setAttribute(
                    JavaFieldAttributeName.GETTER_NAME,
                    getterName);
        }
        {
            String setterName = FieldHelper.getSetterName(
                    getterSetterFieldName);
            localFieldElement.setAttribute(
                    JavaFieldAttributeName.SETTER_NAME,
                    setterName);
        }
        {
            String fillerName = FieldHelper.getFillerName(
                    getterSetterFieldName,
                    "",
                    controllerState);
            localFieldElement.setAttribute(
                    JavaFieldAttributeName.FILLER_NAME,
                    fillerName);
        }
        foreignKey.getChildren().add(localFieldElement);
    }

    /**
     * If a foreign table is referenced more than once from this table,
     * the local field for the foreign key must be qualified by which
     * field(s) the foreign table is referenced. This method calculates
     * this qualifying suffix and returns it.
     *
     * @param foreignKey the foreign key element for which the suffix
     *        is calculated, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the qualifying suffix, or the empty String if none is needed.
     *         Not null.
     */
    private String getLocalReferencedBySuffix(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement localTable = foreignKey.getParent();
        String localTableName = (String) localTable.getAttribute(
                TorqueSchemaAttributeName.NAME);
        String foreignTableName = (String) foreignKey.getAttribute(
                TorqueSchemaAttributeName.FOREIGN_TABLE);

        StringBuilder result = new StringBuilder();
        List<SourceElement> referencesToSameTable
        = FindHelper.findForeignKeyByReferencedTable(
                localTable,
                foreignTableName);
        if (referencesToSameTable.size() > 1
                || foreignKey.getAttribute(
                        TorqueSchemaAttributeName.FOREIGN_TABLE)
                .equals(localTableName))
        {
            result.append((String) controllerState.getOption(
                    TemplateOptionName.OM_LOCAL_FIELD_NAME_RELATED_BY));
            for (SourceElement reference : foreignKey.getChildren(
                    TorqueSchemaElementName.REFERENCE))
            {
                SourceElement localColumnElement
                = reference.getChildren(
                        ReferenceChildElementName.LOCAL_COLUMN)
                .get(0);
                SourceElement localColumn
                = localColumnElement.getChildren(
                        TorqueSchemaElementName.COLUMN)
                .get(0);
                String fieldName = (String) localColumn.getAttribute(
                        JavaFieldAttributeName.FIELD_NAME);
                result.append(
                        StringUtils.capitalize(fieldName));
            }
        }
        return result.toString();
    }

    /**
     * If a foreign table is referenced more than once from this table,
     * the foreign field for the foreign key must be qualified by which
     * field(s) the foreign table is referenced. This method calculates
     * this qualifying suffix and returns it.
     *
     * @param foreignKey the foreign key element for which the suffix
     *        is calculated, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the qualifying suffix, or the empty String if none is needed.
     *         Not null.
     */
    static String getForeignReferencedBySuffix(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement localTable = foreignKey.getParent();
        String foreignTableName = (String) foreignKey.getAttribute(
                TorqueSchemaAttributeName.FOREIGN_TABLE);

        StringBuilder result = new StringBuilder();
        List<SourceElement> referencingSameTable
        = FindHelper.findForeignKeyByReferencedTable(
                localTable,
                foreignTableName);
        if (referencingSameTable.size() > 1)
        {
            result.append((String) controllerState.getOption(
                    TemplateOptionName.OM_FOREIGN_FIELD_NAME_RELATED_BY));
            for (SourceElement reference : foreignKey.getChildren(
                    TorqueSchemaElementName.REFERENCE))
            {
                SourceElement localColumnElement
                = reference.getChildren(
                        ReferenceChildElementName.LOCAL_COLUMN)
                .get(0);
                SourceElement localColumn
                = localColumnElement.getChildren(
                        TorqueSchemaElementName.COLUMN)
                .get(0);
                String fieldName = (String) localColumn.getAttribute(
                        JavaFieldAttributeName.FIELD_NAME);
                result.append(
                        StringUtils.capitalize(fieldName));
            }
        }
        return result.toString();
    }

    /**
     * Creates the child element "foreign-colum" for the reference element
     * and adds the foreign column as a child of it.
     *
     * @param foreignTable the foreign table element.
     * @param reference the refenced element to enrich.
     */
    private void createForeignElementForReference(
            final SourceElement foreignTable,
            final SourceElement reference)
    {
        {
            String foreignColumnName = (String)
                    reference.getAttribute(
                            TorqueSchemaAttributeName.FOREIGN);
            SourceElement foreignColumnElement
                = new SourceElement(
                    ReferenceChildElementName.FOREIGN_COLUMN);
            SourceElement column
            = FindHelper.findColumn(foreignTable, foreignColumnName);
            foreignColumnElement.getChildren().add(column);
            reference.getChildren().add(foreignColumnElement);
        }
    }

    /**
     * Creates the child element "local-column" for the reference element
     * and adds the local column as a child of it.
     *
     * @param localTable the local table element.
     * @param reference the referenced element to enrich.
     *
     * @throws SourceTransformerException if the referenced column
     *         cannot be found.
     */
    protected void createLocalElementForReference(
            final SourceElement localTable,
            final SourceElement reference)
                    throws SourceTransformerException
    {
        {
            String localColumnName = (String)
                    reference.getAttribute(
                            TorqueSchemaAttributeName.LOCAL);
            SourceElement localColumnElement
                = new SourceElement(
                    ReferenceChildElementName.LOCAL_COLUMN);
            SourceElement column
            = FindHelper.findColumn(localTable, localColumnName);
            if (column == null)
            {
                Object localTableName = localTable.getAttribute(
                        TorqueSchemaAttributeName.NAME);
                throw new SourceTransformerException(
                        "Error defining foreign key in table " + localTableName
                        + " : Could not find local column " + localColumnName
                        + " in table " + localTableName);
            }
            localColumnElement.getChildren().add(column);
            reference.getChildren().add(localColumnElement);
        }
    }

    private void setForeignKeyAttributes(
            final SourceElement foreignKey,
            final ControllerState controllerState)
    {
        SourceElement foreignTable = foreignKey.getChild(
                TorqueSchemaElementName.TABLE);
        String referencedBySuffix = getLocalReferencedBySuffix(
                foreignKey, controllerState);
        String foreignKeyGetterName = (String) controllerState.getOption(
                TemplateOptionName.OM_FOREIGN_KEY_GETTER_PREFIX)
                + foreignTable.getAttribute(
                        TableAttributeName.DB_OBJECT_CLASS_NAME)
                + controllerState.getOption(
                        TemplateOptionName.OM_FOREIGN_KEY_GETTER_SUFFIX)
                + referencedBySuffix;
        foreignKey.setAttribute(
                ForeignKeyAttributeName.FOREIGN_KEY_GETTER,
                foreignKeyGetterName);

        boolean referencesPrimaryKey = false;
        List<SourceElement> foreignTablePrimaryKeys
        = foreignTable.getChild(TableChildElementName.PRIMARY_KEYS)
        .getChildren(TorqueSchemaElementName.COLUMN);
        List<SourceElement> foreignTableForeignKeyColumns
            = new ArrayList<>();
        for (SourceElement reference
                : foreignKey.getChildren(TorqueSchemaElementName.REFERENCE))
        {
            SourceElement column = reference.getChild(
                    ReferenceChildElementName.FOREIGN_COLUMN)
                    .getChild(TorqueSchemaElementName.COLUMN);
            foreignTableForeignKeyColumns.add(column);
        }
        if (foreignTablePrimaryKeys.size()
                == foreignTableForeignKeyColumns.size())
        {
            referencesPrimaryKey = true;
            for (int i = 0; i < foreignTablePrimaryKeys.size(); ++i)
            {
                if (foreignTablePrimaryKeys.get(i)
                        != foreignTableForeignKeyColumns.get(i))
                {
                    referencesPrimaryKey = false;
                    break;
                }
            }
        }
        foreignKey.setAttribute(
                ForeignKeyAttributeName.REFERENCES_PRIMARY_KEY,
                referencesPrimaryKey);
    }

    private void getParentPath(
            final SourceElement sourceElement,
            final StringBuilder result)
    {
        SourceElement parent = sourceElement.getParent();
        if (parent == null)
        {
            return;
        }
        result.append(parent.getName());
        if (TorqueSchemaElementName.EXTERNAL_SCHEMA.getName().equals(
                parent.getName()))
        {
            result.append("[")
            .append(parent.getAttribute(TorqueSchemaAttributeName.FILENAME))
            .append("]");
        }
        result.append("/");
        getParentPath(parent, result);
    }
}
