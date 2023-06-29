package org.apache.torque.templates.sql.templates.ddl.oracle
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
import org.apache.torque.templates.model.Table

TorqueGenGroovy torqueGenGroovy = torqueGen
Table table = torqueGenGroovy.model
String cols = torqueGenGroovy.mergepoint("columns")
String pk = torqueGenGroovy.mergepoint("primaryKey")
String unique = torqueGenGroovy.mergepoint("unique")
String index = torqueGenGroovy.mergepoint("index")
String sequence = torqueGenGroovy.mergepoint("sequence")
String createTableSql = "${cols}${unique}"
int lastCommaPos = createTableSql.lastIndexOf(",")
if (lastCommaPos != -1) 
{
    createTableSql = createTableSql.substring(0, lastCommaPos)
}
 
def result = """
-- -----------------------------------------------------------------------
-- $table.name
-- -----------------------------------------------------------------------
CREATE TABLE $table.name
(
${createTableSql}${torqueGenGroovy.mergepoint("createOptions")}
);

${pk}
${index}
${sequence}
"""
