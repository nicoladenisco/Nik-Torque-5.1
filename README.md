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

# Apache Db Torque Project 

## General 

- Apache Torque is an object-relational mapper using java.
- It generates the necessary classes (including the Data Objects) from an XML schema describing the database layout.
- it uses velocity / groovy templates during the code generation process.

## Requirements
- Version 5 requires Java 8. 
- Build and Tests run with Java 8 and 14. Building with Java 11 may require some fixes.

- IDE integration

To run with eclipse skip torque Maven plugin, hich tries to generate classes from unconfigured 

    mvn eclipse:eclipse -Dmaven.torque.skip=true
    
or try it with default database and activated beans and managers profile (some Java classes depend on this):

    mvn eclipse:eclipse -PderbyEmbedded,beans,managers
   

### Modules

- This project consists of six modules

    - torque-runtime 
    - torque-generator
    - torque-templates
    - torque-maven-plugin 
    - torque-ant-tasks
    - torque-site 
    - torque-test (internal only)


### Core modules

*Torque-generator* is the core module providing the mechanism to generate the mappings, 
which are provided by *Torque-templates* module. You need the ant tool installed.

*Torque-runtime* is the only module needed, if building / generation step was done.


#### Test module

Two kinds of tests exist. Module "internal" tests without database dependency or explicitely inlined 
dependency as in torque-templates (ddl templates) database testing in the module "Torque-test".

*Torque-test* allows to test against databases and provides a [README](torque-test/README.md) of its own. 
You need the ant tool installed.

You may start Torque-Test module in the **torque-test folder** by this command (with default database derbyEmbedded) 

    mvn clean install -PderbyEmbedded,beans,managers
    
or in **root folder** (with default database **derbyEmbedded-jenkins** and activated module test

    mvn clean install -PderbyEmbedded-jenkins,beans,managers,test


#### Site module

*Torque-site* is used to generate the site (add post-site to merge the documentation of dependent modules. 
You need the ant tool installed.

    mvn site post-site

N.B. This has to be run from root folder, as torque-site expects existing target/site folders for each module (torque-test is not included).
Required minimal memory 1GB, set Maven_OPTS=2G.

The ant build tool does not (yet) include pmd/xref reports in the final site building, 
although they are build for inspection in each sub module (except test). 
    
#### Build modules

- *Torque-maven-plugin* provides a Maven plugin (generator).

- *Torque-ant-tasks* is to provide some ant tasks to generate the Torque artefacts (xml, java, sql).

To build all with checksums run in root 

    mvn clean install -Papache-release,derbyEmbedded-jenkins,beans,managers,owasp


- one "database" profile (setting torque.driver) is required to be active
- managers and beans profile currently are required to be activated

#### Provided  database profiles

- derby
- derbyEmbedded
- hsqldb
- mssql
- mysql
- oracle
- postgresql
- derbyEmbedded-jenkins: derby profile with default settings. Can only be run from parent project with `mvn -Ptest,derbyEmbedded-jenkins install`
- hsqldb-jenkins: see above, run from parent project with `mvn -Ptest,hsqldb-jenkins install`
- apache-release: sets as database derbyEmbedded

#### Deploy Artifacts and Release modules

Find more information here: [Developer Guide][https://db.apache.org/torque/torque-5.0/developer-info/developer-guide.html]

Check versions with Maven Versions Plugin:

    - mvn versions:display-plugin-updates
    - mvn versions:display-dependency-updates
    - mvn versions:display-property-updates

- Requirements

Public Key is added to KEYS. 

##### Deploy artifacts

You will be asked for your gpg passphrase during the build process, if not provided otherwise.

    mvn clean package -Papache-release,derbyEmbedded-jenkins,beans,managers,owasp
    mvn deploy -DskipTests=true
    

##### Release modules

First test the release locally until the build is successfull, 
then clean again and run and perform release (from the parent root):

    mvn release:prepare -DdryRun=true -DautoVersionSubmodules=true -Ptest,managers,beans,apache-release,derbyEmbedded-jenkins

    mvn release:clean ..
    
You have to add profiles test,derbyEmbedded-jenkins to allow for running the test module (find more information below in Note)
and profile apache-release to create the release checksums.
    
    mvn release:prepare -DautoVersionSubmodules=true -Ptest,managers,beans,apache-release,derbyEmbedded-jenkins
    
    mvn release:perform  -Pmanagers,beans,derbyEmbedded-jenkins
    
Note: We need to add profiles managers, beans as release perform does a checkout 
of the tagged version (which is then uploaded to nexus) and is running the build process again. 
In module torque-test the beans and manager java is generated only, if the beforementioned profiles are active.

###### Important information for version < 5.1

From the developer-guide: Running release:prepare "will change the version in the poms 
to the release version and then run a clean verify cycle on the new poms. 
This will fail because **the test project** needs the *maven plugin* which is not installed
in the new version yet. 
To fix this, you might have to run **rm derby.log**, 
then run **mvn install** to install the torque artifacts in the release version. 
Then run again **mvn -Ptest release:prepare..** to finish building the release".
This requires **activated profile test** to also apply the release prepare step to module torque-test, 
which is by default not in modules list.


Now close the staging repository after Login in Nexus Repo
  https://repository.apache.org/index.html#stagingRepositories
  
Then prepare voting information and voting. 

If voting was successfull, proceed by publishing in Nexus Repo and distribute binaries and sources to d. Otherwise drop the staged repo in Nexus repo.
