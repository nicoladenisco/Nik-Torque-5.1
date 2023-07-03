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


### Changes in torque-templates

Re activated by default *retrieveByPKs* for compatibiliti with previous code.
Since there is no name conflict or anything else, it is not clear why not to keep the old name too to simplify compatibility.

New method *retrieveByPKQuiet* . Dont throw exception but return null if is not possible load record.

New method *retriveByAlternateKey* and *retriveByAlternateKeyQuiet*.
For each unique index (i.e. altered key on the table) a method is added which
allows the recovery of the record through the values of the alternating key.

New method *getFirst*.
Useful to select the first record of a Criteria.

