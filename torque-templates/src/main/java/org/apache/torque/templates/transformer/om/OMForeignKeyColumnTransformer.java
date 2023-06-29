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

import java.util.List;

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;

/**
 * Sets the referenced and referencing columns for the column.
 * These references are defined by foreign key definitions.
 * The structure before transformation is:
 *
 * database
 *   table
 *     column
 *     column
 *     ...
 *     foreign-key
 *       reference
 *     foreign-key
 *       reference
 *       reference
 *       ...
 *     ...
 *   table
 *   table
 *   ...
 *
 * The structure after transformation is
 * database
 *   table
 *     column
 *       referenced-column (a foreign key exists containing the current column
 *                          as local reference)
 *         column (the foreign reference of the said foreign key)
 *         foreign-key (the foreign key defining the relation)
 *       referencing-column (a foreign key exists containing the current column
 *                           as foreign reference)
 *         column (the local reference of the said foreign key)
 *         foreign-key (the foreign key defining the relation)
 *       referencing-column
 *         column
 *         foreign-key
 *       ...
 *     column
 *     ...
 *     foreign-key
 *       reference
 *     foreign-key
 *       reference
 *       reference
 *       ...
 *     ...
 *   table
 *   table
 *   ...
 *
 * Note that the foreign keys in the foreign-keys and referencing-foreign-keys
 * are the original elements, not just copies.
 */
public class OMForeignKeyColumnTransformer
{
    public void transform(
            SourceElement column,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (!TorqueSchemaElementName.COLUMN.getName().equals(column.getName()))
        {
            throw new IllegalArgumentException("Illegal element Name "
                    + column.getName());
        }
        Object localColumnName
        = column.getAttribute(TorqueSchemaAttributeName.NAME);
        SourceElement localTable = column.getParent();
        Object localTableName
        = localTable.getAttribute(TorqueSchemaAttributeName.NAME);
        SourceElement database = localTable.getParent();

        // create the referencedColumn elements
        {
            List<SourceElement> tableForeignKeys
            = localTable.getChildren(
                    TorqueSchemaElementName.FOREIGN_KEY.getName());

            // find foreign keys with this column as local reference
            // and create a referencedColumn element for each.
            for (SourceElement foreignKey : tableForeignKeys)
            {
                List<SourceElement> references
                = foreignKey.getChildren(
                        TorqueSchemaElementName.REFERENCE.getName());
                for (SourceElement reference : references)
                {
                    if (localColumnName.equals(reference.getAttribute(
                            TorqueSchemaAttributeName.LOCAL)))
                    {
                        SourceElement referencedColumn
                            = new SourceElement(
                                ColumnChildElementName.REFERENCED_COLUMN);
                        referencedColumn.getChildren().add(foreignKey);
                        String foreignTableName
                        = (String) foreignKey.getAttribute(
                                TorqueSchemaAttributeName.FOREIGN_TABLE);
                        String foreignColumnName
                        = (String) reference.getAttribute(
                                TorqueSchemaAttributeName.FOREIGN);
                        SourceElement foreignTable = FindHelper.findTable(
                                database,
                                foreignTableName,
                                true);
                        if (foreignTable == null)
                        {
                            throw new SourceTransformerException(
                                    "Foreign table with name "
                                            + foreignTableName
                                            + " not found for a foreignKey of table "
                                            + localTableName);
                        }
                        SourceElement foreignColumn = FindHelper.findColumn(
                                foreignTable,
                                foreignColumnName);
                        if (foreignColumn == null)
                        {
                            throw new SourceTransformerException(
                                    "Referenced Column with table name "
                                            + foreignTableName
                                            + " and column name "
                                            + foreignColumnName
                                            + " not found for a foreignKey of table "
                                            + localTableName);
                        }
                        referencedColumn.getChildren().add(foreignColumn);
                        column.getChildren().add(referencedColumn);
                        break;
                    }
                }
            }
        }

        //create the referencing-column elements
        List<SourceElement> allTables
        = database.getChildren(TorqueSchemaElementName.TABLE.getName());
        for (SourceElement foreignTable : allTables)
        {
            String foreignTableName
            = (String) foreignTable.getAttribute(
                    TorqueSchemaAttributeName.NAME);
            List<SourceElement> foreignKeys
            = foreignTable.getChildren(
                    TorqueSchemaElementName.FOREIGN_KEY.getName());
            for (SourceElement foreignKey : foreignKeys)
            {
                if (!localTableName.equals(foreignKey.getAttribute(
                        TorqueSchemaAttributeName.FOREIGN_TABLE)))
                {
                    continue;
                }
                List<SourceElement> references
                = foreignKey.getChildren(
                        TorqueSchemaElementName.REFERENCE.getName());
                for (SourceElement reference : references)
                {
                    Object referenceForeignColumnName
                    = reference.getAttribute(
                            TorqueSchemaAttributeName.FOREIGN);
                    String referenceLocalColumnName
                    = (String) reference.getAttribute(
                            TorqueSchemaAttributeName.LOCAL);
                    if (localColumnName.equals(referenceForeignColumnName))
                    {
                        SourceElement referencingColumn
                            = new SourceElement(
                                ColumnChildElementName.REFERENCING_COLUMN);
                        referencingColumn.getChildren().add(foreignKey);
                        referencingColumn.getChildren().add(foreignTable);
                        SourceElement localColumn = FindHelper.findColumn(
                                foreignTable,
                                referenceLocalColumnName);
                        if (localColumn == null)
                        {
                            throw new SourceTransformerException(
                                    "Local Column with column name "
                                            + referenceLocalColumnName
                                            + " not found in one of the foreignKeys"
                                            + " of table "
                                            + foreignTableName);
                        }
                        referencingColumn.getChildren().add(localColumn);
                        column.getChildren().add(referencingColumn);
                        break;
                    }
                }
            }
        }
    }
}
