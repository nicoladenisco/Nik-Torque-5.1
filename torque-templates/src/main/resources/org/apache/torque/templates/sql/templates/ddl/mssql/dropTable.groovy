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
import org.apache.torque.templates.model.Table

TorqueGenGroovy torqueGenGroovy = (TorqueGenGroovy) torqueGen
Table table = torqueGenGroovy.model
int counter = torqueGenGroovy.counter;

return """\
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = '$table.unqualifiedName')
BEGIN
     DECLARE @reftable_${counter} nvarchar(60), @constraintname_${counter} nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = '$table.name'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_${counter}, @constraintname_${counter}
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_${counter}+' drop constraint '+@constraintname_${counter})
       FETCH NEXT from refcursor into @reftable_${counter}, @constraintname_${counter}
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE $table.name
END
;
"""