package org.apache.torque.templates.transformer.sql;

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
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourcePath;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.TorqueSchemaIdMethod;
import org.apache.torque.templates.platform.Platform;
import org.apache.torque.templates.platform.PlatformFactory;
import org.apache.torque.templates.transformer.CollectAttributeSetTrueTransformer;
import org.apache.torque.templates.transformer.IncludeSchemaTransformer;
import org.apache.torque.templates.transformer.LoadExternalSchemaTransformer;
import org.apache.torque.templates.transformer.SchemaTypeHelper;
import org.apache.torque.templates.transformer.om.DatabaseChildElementName;
import org.apache.torque.templates.transformer.om.OMColumnJavaTransformer;
import org.apache.torque.templates.transformer.om.OMTransformer;
import org.apache.torque.templates.transformer.om.TableChildElementName;
import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;
import org.apache.torque.templates.typemapping.TypeMap;

/**
 * Transforms the tables in the OM model for sql generation.
 */
public class SQLTransformer implements SourceTransformer
{
    /** A CollectAttributeSetTrueTransformer instance. */
    private static final CollectAttributeSetTrueTransformer collectAttributeSetTrueTransformer
        = new CollectAttributeSetTrueTransformer();

    /**
     * The transformer which loads the external schemata.
     *
     * @see LoadExternalSchemaTransformer
     */
    private static final SourceTransformer loadExternalSchemaTransformer
        = new LoadExternalSchemaTransformer();

    /**
     * The transformer which includes the included schemata.
     *
     * @see LoadExternalSchemaTransformer
     */
    private static final SourceTransformer includeSchemaTransformer
        = new IncludeSchemaTransformer();

    @Override
    public SourceElement transform(
            Object databaseModel,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        SourceElement databaseElement = (SourceElement) databaseModel;
        OMTransformer.setRootDatabaseNameAttribute(databaseElement);
        // include included schemata
        includeSchemaTransformer.transform(databaseElement, controllerState);
        // load referenced external schemata
        loadExternalSchemaTransformer.transform(databaseElement, controllerState);

        TemplateOptionName.checkRequiredOptions(
                controllerState,
                TemplateOptionName.DATABASE);

        List<SourceElement> allTables
        = databaseElement.getChild(DatabaseChildElementName.ALL_TABLES)
        .getChildren(TorqueSchemaElementName.TABLE);
        for (SourceElement tableElement : allTables)
        {
            transformTable(tableElement, controllerState);
        }
        addDatabaseSchemaElements(databaseElement, controllerState);
        return databaseElement;
    }

    public void transformTable(
            SourceElement tableElement,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        String tableName = (String) tableElement.getAttribute(
                TorqueSchemaAttributeName.NAME);
        String unqualifiedTableName = tableName;
        if (StringUtils.contains(tableName, "."))
        {
            unqualifiedTableName = tableName.substring(tableName.indexOf(".") + 1);
        }
        tableElement.setAttribute(
                SqlAttributeName.UNQUALIFIED_NAME,
                unqualifiedTableName);

        Object idMethod = tableElement.getAttribute(
                TorqueSchemaAttributeName.ID_METHOD.getName());
        if (idMethod == null)
        {
            Object defaultIdMethod = tableElement.getParent().getAttribute(
                    TorqueSchemaAttributeName.DEFAULT_ID_METHOD.getName());
            if (defaultIdMethod == null)
            {
                throw new SourceTransformerException("id Method is not set"
                        + " on table "
                        + tableElement.getAttribute(
                                TorqueSchemaAttributeName.NAME.getName())
                        + " and defaultIdMethod is not set on database");
            }
            tableElement.setAttribute(
                    TorqueSchemaAttributeName.ID_METHOD.getName(),
                    defaultIdMethod);
        }
        if (tableElement.getAttribute(
                SqlAttributeName.PRIMARY_KEY_CONSTRAINT_NAME)
                == null)
        {
            String primaryKeyConstraintName = unqualifiedTableName + "_PK";
            tableElement.setAttribute(
                    SqlAttributeName.PRIMARY_KEY_CONSTRAINT_NAME,
                    primaryKeyConstraintName);
        }
        if (tableElement.getAttribute(SqlAttributeName.SEQUENCE_NAME)
                == null)
        {
            String sequenceName = null;
            SourceElement idMethodParameterElement = tableElement.getChild(
                    TorqueSchemaElementName.ID_METHOD_PARAMETER);
            if (idMethodParameterElement != null)
            {
                sequenceName = (String) idMethodParameterElement.getAttribute(
                        TorqueSchemaAttributeName.VALUE);
            }
            if (StringUtils.isBlank(sequenceName))
            {
                sequenceName = tableName + "_SEQ";
            }
            tableElement.setAttribute(
                    SqlAttributeName.SEQUENCE_NAME,
                    sequenceName);
        }

        // primary keys
        collectAttributeSetTrueTransformer.transform(
                tableElement,
                controllerState,
                TorqueSchemaElementName.COLUMN,
                TorqueSchemaAttributeName.PRIMARY_KEY,
                TableChildElementName.PRIMARY_KEYS);

        StringBuilder primaryKeyColumnNames = new StringBuilder();
        SourceElement primaryKeysElement = tableElement.getChild(
                TableChildElementName.PRIMARY_KEYS);
        boolean firstPk = true;
        for (SourceElement primaryKeyColumn : primaryKeysElement.getChildren())
        {
            if (!firstPk)
            {
                primaryKeyColumnNames.append(", ");
            }
            primaryKeyColumnNames.append(primaryKeyColumn.getAttribute(
                    TorqueSchemaAttributeName.NAME.getName()));
            firstPk = false;
        }
        tableElement.setAttribute(
                SqlAttributeName.PRIMARY_KEY_COLUMN_NAMES,
                primaryKeyColumnNames.toString());

        // unique
        int uniqueIndex = 1;
        for (SourceElement uniqueElement : tableElement.getChildren(
                TorqueSchemaElementName.UNIQUE.getName()))
        {
            if (uniqueElement.getAttribute(
                    TorqueSchemaAttributeName.NAME.getName())
                    == null)
            {
                uniqueElement.setAttribute(
                        TorqueSchemaAttributeName.NAME.getName(),
                        uniqueElement.getParent().getAttribute(
                                TorqueSchemaAttributeName.NAME.getName())
                        + "_UQ_" + uniqueIndex);
            }
            String uniqueColumnNames = collectAttributes(
                    uniqueElement,
                    TorqueSchemaElementName.UNIQUE_COLUMN.getName(),
                    TorqueSchemaAttributeName.NAME.getName());
            uniqueElement.setAttribute(
                    SqlAttributeName.UNIQUE_COLUMN_NAMES,
                    uniqueColumnNames);
            String uniqueColumnSizes = collectAttributes(
                    uniqueElement,
                    TorqueSchemaElementName.UNIQUE_COLUMN.getName(),
                    TorqueSchemaAttributeName.SIZE.getName());
            uniqueElement.setAttribute(
                    SqlAttributeName.UNIQUE_COLUMN_SIZES,
                    uniqueColumnSizes);
            ++uniqueIndex;
        }

        // index
        int indexIndex = 1;
        for (SourceElement indexElement : tableElement.getChildren(
                TorqueSchemaElementName.INDEX.getName()))
        {
            if (indexElement.getAttribute(
                    TorqueSchemaAttributeName.NAME.getName())
                    == null)
            {
                indexElement.setAttribute(
                        TorqueSchemaAttributeName.NAME.getName(),
                        indexElement.getParent().getAttribute(
                                TorqueSchemaAttributeName.NAME.getName())
                        + "_IDX_" + indexIndex);
            }
            String indexColumnNames = collectAttributes(
                    indexElement,
                    TorqueSchemaElementName.INDEX_COLUMN.getName(),
                    TorqueSchemaAttributeName.NAME.getName());
            indexElement.setAttribute(
                    SqlAttributeName.INDEX_COLUMN_NAMES,
                    indexColumnNames);
            indexIndex++;
        }

        List<SourceElement> columnElements = tableElement.getChildren(
                TorqueSchemaElementName.COLUMN.getName());
        for (SourceElement columnElement : columnElements)
        {
            transformColumn(columnElement, controllerState);
        }
        List<SourceElement> foreignKeyElements = tableElement.getChildren(
                TorqueSchemaElementName.FOREIGN_KEY.getName());

        int fkIndex = 1;
        for (SourceElement foreignKeyElemenElement : foreignKeyElements)
        {
            transformForeignKey(
                    foreignKeyElemenElement,
                    controllerState,
                    fkIndex);
            ++fkIndex;
        }
    }

    /**
     * Enriches the column elements with additional attributes
     * needed for SQL generation.
     *
     * @param columnElement the element to enrich, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws SourceTransformerException if the name or type attributes
     *         are not set or if the type is unknown.
     */
    private void transformColumn(
            SourceElement columnElement,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        String sql = (String) columnElement.getAttribute(
                SqlAttributeName.DDL_SQL);
        if (sql == null)
        {
            sql = getDdlSql(columnElement, controllerState);
            columnElement.setAttribute(SqlAttributeName.DDL_SQL, sql);
        }
    }

    /**
     * Creates the SQL for defining a column.
     *
     * @param columnElement the column element for which the SQL
     *        should be created, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the SQL for defining the column, not null.
     *
     * @throws SourceTransformerException
     */
    private String getDdlSql(
            SourceElement columnElement,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        SchemaType schemaType = SchemaTypeHelper.getSchemaType(
                columnElement,
                controllerState);
        SqlType domainType = SchemaTypeHelper.getDomain(
                columnElement,
                controllerState);
        Object size = columnElement.getAttribute(
                TorqueSchemaAttributeName.SIZE);
        Object scale = columnElement.getAttribute(
                TorqueSchemaAttributeName.SCALE);
        Object defaultValue = columnElement.getAttribute(
                TorqueSchemaAttributeName.DEFAULT);
        SqlType sqlType = SchemaTypeHelper.getSqlType(
                schemaType,
                domainType,
                controllerState,
                Objects.toString(size, null),
                Objects.toString(scale, null),
                Objects.toString(defaultValue, null));
        Platform platform = getPlatform(controllerState);

        List<String> resultList = new ArrayList<>();

        String sqlTypeName = sqlType.getSqlTypeName();

        if (platform.hasSize(sqlTypeName))
        {
            sqlTypeName += sqlType.printSize(
                    platform.getSizeSuffix(sqlTypeName));
        }
        
        if (platform.hasScale(sqlTypeName))
        {
            sqlTypeName += sqlType.printScale();
        }

        resultList.add(sqlTypeName);

        if (StringUtils.isNotEmpty(sqlType.getDefaultValue()))
        {
            resultList.add("default");

            if ((SchemaType.DATE == schemaType
                    || SchemaType.TIME == schemaType
                    || SchemaType.TIMESTAMP == schemaType))
            {
                if (sqlType.getDefaultValue().startsWith("CURRENT_"))
                {
                    resultList.add(sqlType.getDefaultValue());
                }
                else
                {
                    Date defaultDate
                    = OMColumnJavaTransformer.getDefaultValueAsDate(
                            sqlType.getDefaultValue());
                    if (SchemaType.DATE == schemaType)
                    {
                        resultList.add(platform.getDateString(defaultDate));
                    }
                    else if (SchemaType.TIME == schemaType)
                    {
                        resultList.add(platform.getTimeString(defaultDate));
                    }
                    else
                    {
                        resultList.add(platform.getTimestampString(
                                defaultDate));
                    }
                }
            }
            else if (TypeMap.isTextType(schemaType))
            {
                resultList.add(platform.quoteAndEscape(
                        sqlType.getDefaultValue()));
            }
            else
            {
                resultList.add(sqlType.getDefaultValue());
            }
        }

        boolean primaryKey;
        {
            String primaryKeyString = (String) columnElement.getAttribute(
                    TorqueSchemaAttributeName.PRIMARY_KEY);
            primaryKey = Boolean.parseBoolean(primaryKeyString);
        }
        boolean required;
        {
            String requiredString = (String) columnElement.getAttribute(
                    TorqueSchemaAttributeName.REQUIRED);
            required = Boolean.parseBoolean(requiredString);
        }
        boolean isNotNull = primaryKey || required;
        String isNotNullString = platform.getNullString(isNotNull);

        if (platform.createNotNullBeforeAutoincrement()
                && StringUtils.isNotEmpty(isNotNullString))
        {
            resultList.add(isNotNullString);
        }
        // if idMethod was not set explicitly by the user,
        // the transformTable() method sets the attribute from the
        // defaultIdMethod of the database, so this always returns
        // the id method
        Object idMethod = columnElement.getParent().getAttribute(
                TorqueSchemaAttributeName.ID_METHOD);
        if (primaryKey
                && TorqueSchemaIdMethod.NATIVE.getName().equals(idMethod))
        {
            String autoIncrement = platform.getAutoIncrement();
            if (StringUtils.isNotEmpty(autoIncrement))
            {
                resultList.add(autoIncrement);
            }
        }
        if (!platform.createNotNullBeforeAutoincrement()
                && StringUtils.isNotEmpty(isNotNullString))
        {
            resultList.add(isNotNullString);
        }
        return StringUtils.join(resultList.iterator(), ' ');
    }

    private Platform getPlatform(ControllerState controllerState)
    {
        Platform platform = PlatformFactory.getPlatformFor(
                controllerState.getStringOption(
                        TemplateOptionName.DATABASE));
        return platform;
    }

    /**
     * Sets additional attributes on foreign key elements.
     *
     * @param foreignKeyElement the foreign key element to enrich, not null.
     * @param controllerState the current controller state, not null.
     * @param fkIndex the number of the foreign-key element in the table.
     */
    private void transformForeignKey(
            SourceElement foreignKeyElement,
            ControllerState controllerState,
            int fkIndex)
    {
        if (foreignKeyElement.getAttribute(
                TorqueSchemaAttributeName.NAME.getName())
                == null)
        {
            foreignKeyElement.setAttribute(
                    TorqueSchemaAttributeName.NAME.getName(),
                    foreignKeyElement.getParent().getAttribute(
                            TorqueSchemaAttributeName.NAME.getName())
                    + "_FK_" + fkIndex);
        }
        String localColumnsNames = collectAttributes(
                foreignKeyElement,
                TorqueSchemaElementName.REFERENCE.getName(),
                TorqueSchemaAttributeName.LOCAL.getName());
        foreignKeyElement.setAttribute(
                SqlAttributeName.LOCAL_COLUMN_NAMES,
                localColumnsNames);
        String foreignColumnsNames = collectAttributes(
                foreignKeyElement,
                TorqueSchemaElementName.REFERENCE.getName(),
                TorqueSchemaAttributeName.FOREIGN.getName());
        foreignKeyElement.setAttribute(
                SqlAttributeName.FOREIGN_COLUMN_NAMES,
                foreignColumnsNames);
    }

    private void addDatabaseSchemaElements(
            SourceElement databaseElement,
            ControllerState controllerState)
    {
        Platform platform = getPlatform(controllerState);
        if (!platform.usesStandaloneSchema())
        {
            return;
        }
        List<String> databaseSchemaNames = new ArrayList<>();
        List<SourceElement> allTables
        = databaseElement.getChild(DatabaseChildElementName.ALL_TABLES)
        .getChildren(TorqueSchemaElementName.TABLE);
        for (SourceElement tableElement : allTables)
        {
            String name = (String)
                    tableElement.getAttribute(TorqueSchemaAttributeName.NAME);
            if (StringUtils.contains(name, '.'))
            {
                String databaseSchema = name.substring(0, name.indexOf('.'));
                if (!databaseSchemaNames.contains(databaseSchema))
                {
                    databaseSchemaNames.add(databaseSchema);
                }
            }
        }
        for (String databaseSchemaName : databaseSchemaNames)
        {
            SourceElement databaseSchemaElement
                = new SourceElement("databaseSchema");
            databaseSchemaElement.setAttribute("name", databaseSchemaName);
            databaseElement.getChildren().add(databaseSchemaElement);
        }
    }

    /**
     * Collects attribute values in a comma-separated string.
     * The elements on which the attribute values are read can be reached
     * from <code>rootElement</code> via the path <code>sourcePath</code>;
     * and on these elements, the attributes with name
     * <code>attributeName</code> are collected.
     *
     * @param rootElement the base element from where to start, not null.
     * @param sourcePath the path from <code>rootElement</code> to the elements
     *        on which to collect the attributes.
     * @param attributeName the name of the attributes to collect.
     *
     * @return a comma-separated (", ") String containing all the matching
     *         attribute values.
     */
    private String collectAttributes(
            SourceElement rootElement,
            String sourcePath,
            String attributeName)
    {
        StringBuilder result = new StringBuilder();
        List<SourceElement> toCollectFrom = SourcePath.getElements(
                rootElement, sourcePath);
        for (SourceElement sourceElement : toCollectFrom)
        {
            Object attributeValue = sourceElement.getAttribute(attributeName);
            if (attributeValue != null)
            {
                if (result.length() > 0)
                {
                    result.append(", ");
                }
                result.append(sourceElement.getAttribute(attributeName));
            }
        }
        return result.toString();
    }
}
