package org.apache.torque.templates.sql.templates.ddl.mssql
// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License. 
import org.apache.torque.generator.template.groovy.TorqueGenGroovy
import org.apache.torque.templates.model.ForeignKey

TorqueGenGroovy torqueGenGroovy = (TorqueGenGroovy) torqueGen
ForeignKey foreignKey = torqueGenGroovy.model

def result = """\
BEGIN
ALTER TABLE $foreignKey.parent.name
    ADD CONSTRAINT $foreignKey.name
    FOREIGN KEY ($foreignKey.localColumnNames)
    REFERENCES $foreignKey.foreignTable ($foreignKey.foreignColumnNames)"""
if (foreignKey.onUpdate == "setnull") 
{ 
    result += """
    ON UPDATE SET NULL"""
} 
else if (foreignKey.onUpdate == "restrict") 
{ 
    result += """
    ON UPDATE NO ACTION"""
}
else if (foreignKey.onUpdate) 
{ 
    result += """
    ON UPDATE ${foreignKey.onUpdate.toUpperCase()}"""
}
if (foreignKey.onDelete == "setnull") 
{ 
    result += """

    ON DELETE SET NULL"""
} 
else if (foreignKey.onDelete == "restrict") 
{ 
    result += """
    ON DELETE NO ACTION"""
}
else if (foreignKey.onDelete) 
{ 
    result += """

    ON DELETE ${foreignKey.onDelete.toUpperCase()}"""
}
result += """

END
;
"""
return result