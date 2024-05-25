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

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;

/**
 * Sets the foreign tables for each of the foreign keys,
 * and define variables, getters and setters for the complexObjectModel.
 *
 * So the source elements are (already processed by OMForeignKeyTransformer)
 * table
 * foreign-key
 * ...
 * table (the foreign referenced table)
 * foreign-key
 * ...
 * table
 * ...
 *
 * and the outcome is
 *
 * table
 * foreign-key
 * ...
 * table (the referenced table in the foreign key)
 * foreign-key
 * ...
 * table
 * ...
 * referencing-foreign-keys
 * foreign-key (a foreign key where the foreignTable is the current table)
 * ...
 * table (the referenced table in the foreign key, i.e this table.)
 * foreign-key
 * ...
 * table
 * ...
 *
 * On running this transformer, the table element in the foreign-key elements
 * must be set properly.
 */
public class OMReferencingForeignKeyTableTransformer
{
  public void transform(SourceElement table, ControllerState controllerState)
     throws SourceTransformerException
  {
    SourceElement database = table.getParent();
    String tableName = (String) table.getAttribute(TorqueSchemaAttributeName.NAME.getName());
    List<SourceElement> referencingForeignKeys = new ArrayList<>();
    for(SourceElement otherTable : database.getChildren(TorqueSchemaElementName.TABLE.getName()))
    {
      List<SourceElement> referencingFromOtherTable
         = FindHelper.findForeignKeyByReferencedTable(otherTable, tableName);
      referencingForeignKeys.addAll(referencingFromOtherTable);
    }

    SourceElement referencingForeignKeysElement = new SourceElement(TableChildElementName.REFERENCING_FOREIGN_KEYS);
    table.getChildren().add(referencingForeignKeysElement);

    for(SourceElement foreignKey : referencingForeignKeys)
    {
      referencingForeignKeysElement.getChildren().add(foreignKey);
    }
  }
}
