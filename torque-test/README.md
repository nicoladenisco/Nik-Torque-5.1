# Test Module - Test against Databases

## General 

- A profile exists for each database in folder src/test/profile/<db>.

- Tested with Java 8 and Java 14.

- A Maven test is started like this: 

```sh
mvn clean test -P<data-type>,managers,beans
```

* e.g. for database Hsqldb use `mvn clean test -Phsqldb,managers,beans`. 

Running tests against a non-memory or non-docker-containerized database requires a host-based database to be installed and running.

### Database Profiles
 
Find more details about database and db user settings in the profile. 

* Find profile names in **pom.xml** and in

```sh
src/test/profile/<database>/
```

#### Provided profiles for databases

- derby
- derbyEmbedded
- hsqldb
- mssql
- mysql
- oracle
- postgresql

### MySQL profile

As the Jdbc-URL in Torque.properties contains a serverTimezone:

torque.dsfactory.bookstore.connection.url = jdbc:mysql://localhost:3306/bookstore?serverTimezone=Europe/Berlin&useSSL=false&allowPublicKeyRetrieval=true

Check this with your locale system time or adjust in mysql.


#### Maven profiles

Each database profile is mapped to a Maven profile, but additional maven profiles exist:

- derbyEmbedded-jenkins: derby profile with default settings. 
Can only be run from parent project with `mvn -Ptest,derbyEmbedded-jenkins install` 
because database url is adjusted to this start location

- hsqldb-jenkins: see above.

- docker-testcontainer: Profile activates docker based test environment instead of host based.

- apache-release: profile activates **derbyEmbedded** profile.

- beans: has to be actived for tests; triggers bean generation + test

- managers: has to be actived for tests; triggers use of managers generation + test


## Workflow Internals

- check `src/main/schema` for the source schemas and the results in `target/generated-*` folders.

- find schema xsd in torque-templates module `src/main/resources/torque/xsd`.

### In-memory Databases

- **Hsqldb** and **Derby** adapters are provided

#### Hsqldb

- In memory, no user access restriction as configured is required. 

### Dockerized databases

- This uses Docker Testcontainers. Find more information here: (https://www.testcontainers.org/).

- Tests should be run sequential, not parallel: (https://www.testcontainers.org/test\_framework\_integration/junit_5/).

- Additional Requirements (Junit 5): (https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html)

- Currently only **mysql** and **postgresql** adapter is supported (and selected tests). 

- Run tests either with maven or e.g. in IDE (Eclipse, IntelliJ).

```sh
   mvn clean test -Pmysql,docker-testcontainer,managers,beans
```

or instead of mysql replace with postgresql.

#### Resolving Issues

- Maven environment

Maven 3.3.9 might need to be upgraded to newest version (tested version 3.6.3).

- Docker environment

A successfull connection will result in a message like this 

	org.testcontainers.dockerclient.DockerClientProviderStrategy - Found Docker environment with Docker for Windows \
	(via TCP port ...)
	
or 

	org.testcontainers.dockerclient.DockerClientProviderStrategy - Found Docker environment with Environment variables, \
	system properties and defaults. \
	Resolved dockerHost=unix:///var/run/docker.sock

If you get 

    No <dockerHost> given, no DOCKER_HOST environment variable,     \
    no read/writable '/var/run/docker.sock' or '//./pipe/docker_engine'        \
    and no external provider like Docker machine configured
    
Docker Desktop (Windows) or docker is not running. If the error still shows up after starting, you may configure and check your system. 
Provide in **docker-java.properties** DOCKER_HOST or other environment variables.
Find a templates in *src/test<database>/docker-resources/db*.

If you upgraded a database image, you might get 

    {"message":"Conflict. The container name \"/mysql-1\" is already in use by ...
    
That is the default container is connected to the old image. An option is ti delete the docker container (check with docker ps --all)

    docker rm mysql-1

##### Docker image (Profile docker-textcontainer)

Change into folder torque-test and check configuration files *testcontainers.properties* and *docker-java.properties* 
in folder `src/test/profile/<profile>/docker-resources`.


###### Docker OS System Adjustments

__Docker-maven-plugin and Docker Testcontainers needs appropriate environment settings__:

* **Docker-Maven-Plugin** automatically checks the OS system and which URL to use to communicate with the docker daemon. 
Testcontainers expects [settings][1] in [docker-java.properties][2].
 
Find the file docker-java.properties in: `src/test/profile/mysql/docker-resources`.
 
* Docker daemon: By default setting the environment variable DOCKER_HOST ( URI / daemon ) is required or an exposed daemon port. 

Set it appropriately in `docker-java.properties`. (e.g. activate the daemon without TLS in Windows 10 in Docker Desktop or set it later by using an appropriate (custom) [Docker daemon][3] 
in the default folders by creating a `daemon.json` file. You may want to upgrade to wsl2 (native linux kernel), find more information here: [WSL][4]. 

* Windows system 7 and below without hyperv may require starting docker using `docker-machine start <id>`, environment 
check with `docker-machine env <id>` and setting it in shell and testcontainer-settings.

- Run the tests with profile __docker-testcontainer__ :

```sh
mvn clean test -P mysql,docker-testcontainer,managers,beans
```

[1]: org.testcontainers.dockerclient.EnvironmentAndSystemPropertyClientProviderStrategy 
[2]: https://raw.githubusercontent.com/docker-java/docker-java/master/docker-java-core/src/main/java/com/github/dockerjava/core/DefaultDockerClientConfig.java
[3]: https://docs.microsoft.com/de-de/virtualization/windowscontainers/manage-docker/configure-docker-daemon
[4]: https://docs.docker.com/docker-for-windows/wsl/

#### Run test with Docker-Testcontainer

Change into folder torque-test and check configuration files `testcontainers.properties` and `docker-java.properties` 
in folder `src/test/profile/postgresql/docker-resources`. 

Run the tests with:

```sh
mvn clean test -P postgresql,docker-testcontainer,managers,beans
```

## Tests with hosted database

### Existing host database (default profile)

- User and database must exist, before running test, see profile mysql.

#### Run test 

Provide database profile name and optionally extend with additional build profiles:

```sh
mvn test -P<data-type>, managers,beans
```


### Mssql
...

### Oracle

...

### Derby

...

## IDE integration (Eclipse) Settings

### IDE integration (Eclipse)

- add `src/main/generated-java` and `target/generated-sources` to build path.

- Generate project/classpath by using minimal test database profile hsqldb

```sh
mvn eclipse:eclipse -P hsqldb, managers,beans
```

or with Testcontainers (recommended)

```sh
mvn eclipse:eclipse -P mysql,docker-testcontainer,managers,beans
```

### Maven Database Test with Remote Standard Socket Attachment

Example: Attach example opens port 8000 per default, but forkMode is already deprecated, but change as convenient.

	mvnDebug test -Dtest=DataTest#testLikeClauseEscaping -Pmysql -DforkCount=0

* or set `<forkCount>0</forkCount>` in pom.xml.

Alternatively use [Surefire debugging][https://maven.apache.org/surefire/maven-surefire-plugin/examples/debugging.html]:

	mvn -Dmaven.surefire.debug test

## TODO

- Profiles managers,beans seem to be required always in testss
- Use Java Testcontainers Docker in other database settings ...


