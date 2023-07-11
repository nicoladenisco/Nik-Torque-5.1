<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

# NIK Apache Db Torque Project

## General

It's a generale improvment e bug correction for Torque 5.1.

### Modules

- This project consists of six modules

    - torque-runtime
    - torque-generator
    - torque-templates
    - torque-maven-plugin
    - torque-ant-tasks
    - torque-site
    - torque-test (internal only)


### Changes in torque-maven-plugin

The flag *runOnlyOnSourceChange* has a incorrect behavory when generate SQL script.
Now it is ingored until *defaultOutputDirUsage* is equal to compile or test-compile
(otherways it is used only for java om generation).


### Changes in torque-templates

Re activated by default *retrieveByPKs* for compatibiliti with previous code.
Since there is no name conflict or anything else, it is not clear why not to keep the old name too to simplify compatibility.

New method *retrieveByPKQuiet* . Dont throw exception but return null if is not possible load record.

New method *retriveByAlternateKey* and *retriveByAlternateKeyQuiet*.
For each unique index (i.e. altered key on the table) a method is added which
allows the recovery of the record through the values of the alternating key.

New method *getFirst*.
Useful to select the first record of a Criteria.

Mark as *deprecated* every function access db without an explicit connection.
When complex data saving operations under transaction are implemented,
it is necessary to use a single connection to the db to avoid the dreaded dead-lock.
The nature of peers tends to hide access to the db easily causing the problem.
By marking methods that don't use an explicit connection with deprecated it becomes
easy to identify these situations by getting direct help from the java compiler.
Thus the methods remain usable but it becomes easy to write safe code that does not cause dead-locks.

Change default option *retainSchemaNamesInJavaName* to true.
*torque.om.retainSchemaNamesInJavaName = true*
It's necessary to avoid name conflict when same tablename is multiple schemas.

Change default option *generateFillers* to true.
*torque.om.complexObjectModel.generateFillers = true*
It's very useful when use complex object model.

Change default option *bypk.deprecated* to true.
torque.om.retrieve.bypk.deprecated = true
Since there is no name conflict or anything else, it is not clear why not to keep the old name too to simplify compatibility.
