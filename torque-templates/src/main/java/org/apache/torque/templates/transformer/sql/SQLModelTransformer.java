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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaIdMethod;
import org.apache.torque.templates.model.Column;
import org.apache.torque.templates.model.Database;
import org.apache.torque.templates.model.EnumValue;
import org.apache.torque.templates.model.ForeignKey;
import org.apache.torque.templates.model.Index;
import org.apache.torque.templates.model.IndexColumn;
import org.apache.torque.templates.model.Reference;
import org.apache.torque.templates.model.Table;
import org.apache.torque.templates.model.Unique;
import org.apache.torque.templates.model.UniqueColumn;
import org.apache.torque.templates.platform.Platform;
import org.apache.torque.templates.platform.PlatformFactory;
import org.apache.torque.templates.transformer.IncludeSchemaTransformer;
import org.apache.torque.templates.transformer.LoadExternalSchemaTransformer;
import org.apache.torque.templates.transformer.SchemaTypeHelper;
import org.apache.torque.templates.transformer.om.OMColumnJavaTransformer;
import org.apache.torque.templates.transformer.om.OMTransformer;
import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;
import org.apache.torque.templates.typemapping.TypeMap;

/**
 * Transforms the tables in the OM model for sql generation.
 */
public class SQLModelTransformer implements SourceTransformer
{
    /**
     * The transformer which loads the external schemata.
     *
     * @see LoadExternalSchemaTransformer
     */
    private static final LoadExternalSchemaTransformer loadExternalSchemaTransformer
        = new LoadExternalSchemaTransformer();

    /**
     * The transformer which includes the included schemata.
     *
     * @see LoadExternalSchemaTransformer
     */
    private static final IncludeSchemaTransformer includeSchemaTransformer
        = new IncludeSchemaTransformer();

    @Override
    public Database transform(
            final Object databaseModel,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        final Database database = (Database) databaseModel;
        OMTransformer.setRootDatabaseName(database);
        // include included schemata
        includeSchemaTransformer.transform(database, controllerState);
        // load referenced external schemata
        loadExternalSchemaTransformer.transform(database, controllerState);

        TemplateOptionName.checkRequiredOptions(
                controllerState,
                TemplateOptionName.DATABASE);

        for (final Table table : database.allTables)
        {
            transformTable(table, database, controllerState);
        }
        addDatabaseSchemaElements(database, controllerState);
        return database;
    }

    public void transformTable(
            final Table table,
            final Database database,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        table.unqualifiedName = table.name;
        if (StringUtils.contains(table.name, "."))
        {
            table.unqualifiedName
            = table.name.substring(table.name.indexOf(".") + 1);
        }

        if (table.idMethod == null)
        {
            if (database.defaultIdMethod == null)
            {
                throw new SourceTransformerException("idMethod is not set"
                        + " on table "
                        + table.name
                        + " and defaultIdMethod is not set on database");
            }
            table.idMethod = database.defaultIdMethod;
        }
        if (table.primaryKeyConstraintName == null)
        {
            table.primaryKeyConstraintName = table.unqualifiedName + "_PK";
        }
        if (StringUtils.isBlank(table.sequenceName))
        {
            if (!table.idMethodParameterList.isEmpty())
            {
                table.sequenceName = table.idMethodParameterList.get(0).value;
            }
            if (StringUtils.isBlank(table.sequenceName))
            {
                table.sequenceName = table.name + "_SEQ";
            }
        }

        // primary keys
        for (final Column column : table.columnList)
        {
            if (Boolean.TRUE.equals(column.primaryKey))
            {
                table.primaryKeyList.add(column);
            }
        }

        final StringBuilder primaryKeyColumnNames = new StringBuilder();
        for (final Column primaryKeyColumn : table.primaryKeyList)
        {
            if (primaryKeyColumnNames.length() != 0)
            {
                primaryKeyColumnNames.append(", ");
            }
            primaryKeyColumnNames.append(primaryKeyColumn.name);
        }
        table.primaryKeyColumnNames = primaryKeyColumnNames.toString();

        Platform platform = getPlatform(controllerState);
        boolean hasUniqueSize = platform.hasUniqueConstraintSize();
        
        // unique
        int uniqueIndex = 1;
        for (final Unique unique : table.uniqueList)
        {
            if (unique.name == null)
            {
                unique.name = table.name + "_UQ_" + uniqueIndex;
            }
            final StringBuilder uniqueColumnNames = new StringBuilder();
            
            final StringBuilder uniqueColumnSizes = new StringBuilder();
            
            for (final UniqueColumn uniqueColumn : unique.uniqueColumnList)
            {
                if (uniqueColumnNames.length() != 0)
                {
                    uniqueColumnNames.append(", ");
                }
                uniqueColumnNames.append(uniqueColumn.name);
                
                if (hasUniqueSize) {
                    if (uniqueColumnSizes.length() != 0)
                    {
                        uniqueColumnSizes.append(", ");
                    }
                    if (uniqueColumn.size != null) {
                        uniqueColumnNames.append("(" + uniqueColumn.size + ")");
                    }   
                }
            }
            unique.uniqueColumnNames = uniqueColumnNames.toString();
            
            ++uniqueIndex;
        }

        // index
        int indexIndex = 1;
        for (final Index index : table.indexList)
        {
            if (index.name == null)
            {
                index.name = table.name + "_IDX_" + indexIndex;
            }
            final StringBuilder indexColumnNames = new StringBuilder();
            for (final IndexColumn indexColumn : index.indexColumnList)
            {
                if (indexColumnNames.length() != 0)
                {
                    indexColumnNames.append(", ");
                }
                indexColumnNames.append(indexColumn.name);
            }
            index.indexColumnNames = indexColumnNames.toString();
            indexIndex++;
        }

        for (final Column column : table.columnList)
        {
            if (column.ddlSql == null)
            {
                column.ddlSql = getDdlSql(
                        column,
                        controllerState);
            }
            if (column.enumConstraintName == null)
            {
                column.enumConstraintName = controllerState.getStringOption(TemplateOptionName.SQL_ENUM_CONSTRAINT_NAME_PREFIX)
                        + column.name
                        + controllerState.getStringOption(TemplateOptionName.SQL_ENUM_CONSTRAINT_NAME_SUFFIX);
            }
            column.generateEnum = !column.enumValueList.isEmpty();
            Iterator<EnumValue> enumValueIt = column.enumValueList.iterator();
            while (enumValueIt.hasNext())
            {
                EnumValue enumValue = enumValueIt.next();
                transformEnumValue(enumValue, controllerState);
                enumValue.hasNext = enumValueIt.hasNext();
            }
        }

        int fkIndex = 1;
        for (final ForeignKey foreignKey : table.foreignKeyList)
        {
            transformForeignKey(
                    foreignKey,
                    controllerState,
                    fkIndex);
            ++fkIndex;
        }
    }

    /**
     * Creates the SQL for defining a column.
     *
     * @param column the column element for which the SQL
     *        should be created, not null.
     * @param controllerState the current controller state, not null.
     *
     * @return the SQL for defining the column, not null.
     *
     * @throws SourceTransformerException
     */
    private String getDdlSql(
            final Column column,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        final SchemaType schemaType = SchemaTypeHelper.getSchemaType(
                column,
                controllerState);
        final SqlType domainType = SchemaTypeHelper.getDomain(
                column,
                controllerState);
        final SqlType sqlType = SchemaTypeHelper.getSqlType(
                schemaType,
                domainType,
                controllerState,
                column.size,
                column.scale,
                column._default);
        final Platform platform = getPlatform(controllerState);

        final List<String> resultList = new ArrayList<>();

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
                    final Date defaultDate
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

        final boolean primaryKey = Boolean.TRUE.equals(column.primaryKey);
        final boolean isNotNull = primaryKey
                || Boolean.TRUE.equals(column.required);
        final String isNotNullString = platform.getNullString(isNotNull);

        if (platform.createNotNullBeforeAutoincrement()
                && StringUtils.isNotEmpty(isNotNullString))
        {
            resultList.add(isNotNullString);
        }
        // if idMethod was not set explicitly by the user,
        // the transformTable() method sets the attribute from the
        // defaultIdMethod of the database, so this always returns
        // the id method
        if (primaryKey
                && TorqueSchemaIdMethod.NATIVE.getName().equals(
                        column.parent.idMethod))
        {
            final String autoIncrement = platform.getAutoIncrement();
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

    private Platform getPlatform(final ControllerState controllerState)
    {
        final Platform platform = PlatformFactory.getPlatformFor(
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
            final ForeignKey foreignKey,
            final ControllerState controllerState,
            final int fkIndex)
    {
        if (foreignKey.name == null)
        {
            foreignKey.name = foreignKey.parent.name + "_FK_" + fkIndex;
        }
        final StringBuilder localColumnNames = new StringBuilder();
        final StringBuilder foreignColumnNames = new StringBuilder();
        for (final Reference reference : foreignKey.referenceList)
        {
            if (localColumnNames.length() != 0)
            {
                localColumnNames.append(", ");
            }
            localColumnNames.append(reference.local);
            if (foreignColumnNames.length() != 0)
            {
                foreignColumnNames.append(", ");
            }
            foreignColumnNames.append(reference.foreign);
        }
        foreignKey.localColumnNames = localColumnNames.toString();
        foreignKey.foreignColumnNames = foreignColumnNames.toString();
    }

    private void addDatabaseSchemaElements(
            final Database database,
            final ControllerState controllerState)
    {
        final Platform platform = getPlatform(controllerState);
        if (!platform.usesStandaloneSchema())
        {
            return;
        }
        for (final Table table : database.allTables)
        {
            if (StringUtils.contains(table.name, '.'))
            {
                final String databaseSchema
                = table.name.substring(0, table.name.indexOf('.'));
                if (!database.schemaNameList.contains(databaseSchema))
                {
                    database.schemaNameList.add(databaseSchema);
                }
            }
        }
    }

    /**
     * Enriches the enum-value elements with additional attributes
     * needed for SQL generation.
     *
     * @param enumValueElement the element to enrich, not null.
     * @param controllerState the current controller state, not null.
     *
     * @throws SourceTransformerException if the name or type attributes
     *         are not set or if the type is unknown.
     */
    private void transformEnumValue(
            final EnumValue enumValue,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (enumValue.sqlValue != null)
        {
            return;
        }
        Column column = enumValue.parent;
        SchemaType schemaType = SchemaTypeHelper.getSchemaType(
                column,
                controllerState);
        Platform platform = getPlatform(controllerState);

        if ((SchemaType.DATE == schemaType
                || SchemaType.TIME == schemaType
                || SchemaType.TIMESTAMP == schemaType))
        {
            Date defaultDate
            = OMColumnJavaTransformer.getDefaultValueAsDate(enumValue.value);
            if (SchemaType.DATE == schemaType)
            {
                enumValue.sqlValue = platform.getDateString(defaultDate);
            }
            else if (SchemaType.TIME == schemaType)
            {
                enumValue.sqlValue = platform.getTimeString(defaultDate);
            }
            else
            {
                enumValue.sqlValue = platform.getTimestampString(defaultDate);
            }
        }
        else if (TypeMap.isTextType(schemaType))
        {
            enumValue.sqlValue = platform.quoteAndEscape(enumValue.value);
        }
        else
        {
            enumValue.sqlValue = enumValue.value;
        }
    }

}
