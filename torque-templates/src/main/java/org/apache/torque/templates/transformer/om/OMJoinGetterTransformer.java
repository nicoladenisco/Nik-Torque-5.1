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

import static org.apache.torque.templates.TemplateOptionName.OM_GENERATE_JOIN_GETTERS;
import static org.apache.torque.templates.TemplateOptionName.OM_JOIN_GETTER_SEPARATOR;
import static org.apache.torque.templates.TemplateOptionName.OM_JOIN_GETTER_VISIBILITY;

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaElementName;

/**
 * Creates the joinGetter Elements on a table.
 * The OMReferencingForeignKeyTableTransformer needs to be run
 * before this transformation
 * The structure before transformation is:
 * <pre>
 * table
 *   foreign-key
 *     ...
 *     table (the referenced table in the foreign key)
 *   foreign-key
 *     ...
 *     table
 *   ...
 *   referencing-foreign-keys
 *     foreign-key (a foreign key where the foreignTable is the current table)
 *       ...
 *       table (the referenced table in the foreign key, i.e this table.)
 *     foreign-key
 *       ...
 *       table
 *   ...
 * </pre>
 * 
 * The structure after transformation is
 * <pre>
 * table
 *   foreign-key
 *     ...
 *     table (the referenced table in the foreign key)
 *   foreign-key
 *     ...
 *     table
 *   ...
 *   referencing-foreign-keys
 *     foreign-key (a foreign key where the foreignTable is the current table)
 *       ...
 *       table (the referenced table in the foreign key, i.e this table.)
 *     foreign-key
 *       ...
 *       table
 *   ...
 *   joinGetter name=".."
 *     local
 *       foreign-key (a foreign key referencing the table)
 *     remote
 *       foreign-key (a foreign key of the referenced table)
 *
 *   ...
 * </pre>
 * 
 */
public class OMJoinGetterTransformer
{
    /**
     * @param tableElement the table 
     * @param controllerState the controller state
     * @throws SourceTransformerException if the table cannot be joined
     */
    public void transform(
            final SourceElement tableElement,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (!controllerState.getBooleanOption(OM_GENERATE_JOIN_GETTERS))
        {
            return;
        }

        checkElementName(tableElement);
        SourceElement referencingForeignKeys = tableElement.getChild(
                TableChildElementName.REFERENCING_FOREIGN_KEYS);
        for (SourceElement foreignKey
                : referencingForeignKeys.getChildren(
                        TorqueSchemaElementName.FOREIGN_KEY))
        {
            SourceElement foreignTable = foreignKey.getParent();
            for (SourceElement remoteForeignKey
                    : foreignTable.getChildren(
                            TorqueSchemaElementName.FOREIGN_KEY))
            {
                if (remoteForeignKey == foreignKey)
                {
                    continue;
                }
                SourceElement remoteForeignTable = remoteForeignKey.getChild(
                        TorqueSchemaElementName.TABLE);

                String localReferencedBySuffix
                = OMForeignKeyTransformer.getForeignReferencedBySuffix(
                        foreignKey, controllerState);
                String localGetterSetterFieldName
                = (String) foreignTable.getAttribute(
                        TableAttributeName.DB_OBJECT_CLASS_NAME)
                + localReferencedBySuffix;
                String foreignReferencedBySuffix
                = OMForeignKeyTransformer.getForeignReferencedBySuffix(
                        remoteForeignKey, controllerState);
                String foreignGetterSetterFieldName
                = (String) remoteForeignTable.getAttribute(
                        TableAttributeName.DB_OBJECT_CLASS_NAME)
                + foreignReferencedBySuffix;
                String joinGetterFieldName = localGetterSetterFieldName
                        + controllerState.getStringOption(OM_JOIN_GETTER_SEPARATOR)
                        + foreignGetterSetterFieldName;
                String joinGetterName = FieldHelper.getGetterName(
                        joinGetterFieldName,
                        null,
                        controllerState);
                SourceElement joinGetterElement = new SourceElement(
                        TableChildElementName.JOIN_GETTER);
                tableElement.getChildren().add(joinGetterElement);
                joinGetterElement.setAttribute(
                        JavaFieldAttributeName.GETTER_NAME, joinGetterName);
                joinGetterElement.setAttribute(
                        JavaFieldAttributeName.GETTER_ACCESS_MODIFIER,
                        controllerState.getStringOption(
                                OM_JOIN_GETTER_VISIBILITY));

                SourceElement localElement = new SourceElement(
                        TableChildElementName.LOCAL);
                joinGetterElement.getChildren().add(localElement);
                localElement.getChildren().add(foreignKey);


                SourceElement foreignElement = new SourceElement(
                        TableChildElementName.FOREIGN);
                joinGetterElement.getChildren().add(foreignElement);
                foreignElement.getChildren().add(remoteForeignKey);
            }
        }
    }


    /**
     * Checks that the name of the table element is correct.
     *
     * @param tableElement the table element, not null.
     *
     * @throws IllegalArgumentException if the element name is wrong.
     */
    protected void checkElementName(final SourceElement tableElement)
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
}
