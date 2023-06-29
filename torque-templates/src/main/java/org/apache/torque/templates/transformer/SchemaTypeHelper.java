package org.apache.torque.templates.transformer;

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

import java.util.List;
import java.util.Objects;

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.model.Column;
import org.apache.torque.templates.model.Domain;
import org.apache.torque.templates.platform.Platform;
import org.apache.torque.templates.platform.PlatformFactory;
import org.apache.torque.templates.typemapping.SchemaType;
import org.apache.torque.templates.typemapping.SqlType;

/**
 * Helper class for retrieving the schema type of a column.
 *
 * $Id: SchemaTypeHelper.java 1856067 2019-03-22 15:32:47Z gk $
 */
public final class SchemaTypeHelper
{
    /**
     * Private constructor for utility class.
     */
    private SchemaTypeHelper()
    {
    }

    /**
     * Determines the schema type of a column.
     *
     * @param columnElement the source element which defines the column
     *        for which the schema type should be determined; not null.
     * @param controllerState the controller state, not null.
     *
     * @return the schema type of the column, not null.
     *
     * @throws SourceTransformerException if the name attribute is not set
     *         in the column or if the type refers to an unknown type.
     */
    public static SchemaType getSchemaType(
            final SourceElement columnElement,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        final String columnNameFromSchema
        = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.NAME);
        if (columnNameFromSchema == null)
        {
            throw new SourceTransformerException("The attribute "
                    + TorqueSchemaAttributeName.NAME.getName()
                    + " on element "
                    + columnElement.getName()
                    + " is null");
        }

        SchemaType schemaType = null;
        final SqlType domain = getDomain(columnElement, controllerState);
        if (domain != null && domain.getSqlTypeName() != null)
        {
            try
            {
                schemaType = SchemaType.valueOf(domain.getSqlTypeName());
            }
            catch (final IllegalArgumentException e)
            {
                throw new SourceTransformerException("Unknown type "
                        + domain.getSqlTypeName()
                        + " in Domain definition");
            }
        }
        else
        {
            final String schemaTypeString = (String) columnElement.getAttribute(
                    TorqueSchemaAttributeName.TYPE.getName());
            if (schemaTypeString == null)
            {
                throw new SourceTransformerException("type attribute not set"
                        + " in Column "
                        + columnNameFromSchema);
            }
            try
            {
                schemaType = SchemaType.valueOf(schemaTypeString);
            }
            catch (final IllegalArgumentException e)
            {
                throw new SourceTransformerException("Unknown type "
                        + schemaTypeString
                        + " in Column "
                        + columnNameFromSchema);
            }
        }
        return schemaType;
    }

    /**
     * Determines the schema type of a column.
     *
     * @param column the column for which the schema type should be determined;
     *        not null.
     * @param controllerState the controller state, not null.
     *
     * @return the schema type of the column, not null.
     *
     * @throws SourceTransformerException if the name attribute is not set
     *         in the column or if the type refers to an unknown type.
     */
    public static SchemaType getSchemaType(
            final Column column,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (column.name == null)
        {
            throw new SourceTransformerException("The attribute "
                    + TorqueSchemaAttributeName.NAME.getName()
                    + " on element "
                    + TorqueSchemaElementName.COLUMN
                    + " is null");
        }

        SchemaType schemaType = null;
        final SqlType domain = getDomain(
                column,
                controllerState);
        if (domain != null && domain.getSqlTypeName() != null)
        {
            try
            {
                schemaType = SchemaType.valueOf(domain.getSqlTypeName());
            }
            catch (final IllegalArgumentException e)
            {
                throw new SourceTransformerException("Unknown type "
                        + domain.getSqlTypeName()
                        + " in Domain definition");
            }
        }
        else
        {
            if (column.type == null)
            {
                throw new SourceTransformerException("type attribute not set"
                        + " in Column "
                        + column.name);
            }
            try
            {
                schemaType = SchemaType.valueOf(column.type);
            }
            catch (final IllegalArgumentException e)
            {
                throw new SourceTransformerException("Unknown type "
                        + column.type
                        + " in Column "
                        + column.name);
            }
        }
        return schemaType;
    }

    /**
     * Returns the SQL type for a schema type and the specified target database.
     *
     * @param schemaType the schema type for which the SQL type should be
     *        determined, not null.
     * @param domainType the domain type which overrides the schema type,
     *        or null if no domain is defined.
     * @param controllerState the controller state, not null.
     * @param size overrides the size from schemaType and/or domainType,
     *        or null to use the default from domainType or schemaType.
     * @param scale overrides the scale from schemaType and/or domainType,
     *        or null to use the default from domainType or schemaType.
     * @param defaultValue overrides the defaultValue from schemaType
     *        and/or domainType, or null to use the default from domainType
     *        or schemaType.
     *
     * @return the the SQL type for the schema type, or null if no SQL type
     *         exists for the schema type.
     */
    public static SqlType getSqlType(
            final SchemaType schemaType,
            final SqlType domainType,
            final ControllerState controllerState,
            String size,
            String scale,
            String defaultValue)
    {
        final Platform platform = PlatformFactory.getPlatformFor(
                controllerState.getStringOption(TemplateOptionName.DATABASE));
        final SqlType platformSqlType = platform.getSqlTypeForSchemaType(schemaType);
        if (domainType != null)
        {
            if (size == null)
            {
                size = domainType.getSize();
            }
            if (scale == null)
            {
                scale = domainType.getScale();
            }
            if (defaultValue == null)
            {
                defaultValue = domainType.getDefaultValue();
            }
        }
        final SqlType result = platformSqlType.getNew(
                size,
                scale,
                defaultValue);
        return result;
    }

    public static SqlType getDomain(
            final SourceElement columnElement,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        final String domainNameFromSchema
        = (String) columnElement.getAttribute(
                TorqueSchemaAttributeName.DOMAIN);
        if (domainNameFromSchema == null)
        {
            // no domain specified
            return null;
        }
        SourceElement domainElement = null;
        {
            final SourceElement databaseElement
            = columnElement.getParent().getParent();
            final List<SourceElement> domainElementList
            = databaseElement.getChildren(
                    TorqueSchemaElementName.DOMAIN);
            for (final SourceElement candidate : domainElementList)
            {
                if (domainNameFromSchema.equals(candidate.getAttribute(
                        TorqueSchemaAttributeName.NAME)))
                {
                    domainElement = candidate;
                    break;
                }
            }
        }
        if (domainElement == null)
        {
            throw new SourceTransformerException("The domain named "
                    + domainNameFromSchema
                    + " referenced by the column "
                    + columnElement.getParent().getAttribute(
                            TorqueSchemaAttributeName.NAME)
                    + " in the table "
                    + columnElement.getAttribute(TorqueSchemaAttributeName.NAME)
                    + " was not found in this schema");
        }
        final String sqlType = Objects.toString(
                domainElement.getAttribute(TorqueSchemaAttributeName.TYPE),
                null);
        final String defaultValue = Objects.toString(
                domainElement.getAttribute(TorqueSchemaAttributeName.DEFAULT),
                null);
        final String size = Objects.toString(
                domainElement.getAttribute(TorqueSchemaAttributeName.SIZE),
                null);
        final String scale = Objects.toString(
                domainElement.getAttribute(TorqueSchemaAttributeName.SCALE),
                null);
        return new SqlType(sqlType, size, scale, defaultValue);
    }

    public static SqlType getDomain(
            final Column column,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (column.domain == null)
        {
            // no domain specified
            return null;
        }
        Domain domain = null;
        {
            for (final Domain candidate : column.parent.parent.domainList)
            {
                if (column.domain.equals(candidate.name))
                {
                    domain = candidate;
                    break;
                }
            }
        }
        if (domain == null)
        {
            throw new SourceTransformerException("The domain named "
                    + column.domain
                    + " referenced by the column "
                    + column.name
                    + " in the table "
                    + column.parent.name
                    + " was not found in this schema");
        }
        return new SqlType(
                domain.type,
                domain.size,
                domain.scale,
                domain._default);
    }
}
