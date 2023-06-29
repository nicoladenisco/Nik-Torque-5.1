package org.apache.torque.testcontainer.junit5.extension;

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

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.TorqueException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * {@link #dbContainer} and  {@link #servicePort} has to be set by implementing class.
 * 
 * 
 * @author gk
 * @version $Id$
 *
 */
public abstract class DockerDatabaseAbstractExtension extends DockerCallbackExtension {

    static Logger log = LogManager.getLogger();
    
    protected static String DATABASE_NAME = "bookstore";
    protected static final String DOCKERFILE = "./target/Dockerfile";
    
    GenericContainer dbContainer;
    
    private int servicePort;
    
    public DockerDatabaseAbstractExtension() {
        super();
    }
    

    /**
     * If database {@link #dbContainer} is not running, start it after port is bound.
     * If it is still not running throw RuntimeException
     */
    private void startDatabaseContainer() {
        if (!getDbContainer().isRunning()) {
            log.info("Waiting for running/ listening port..., binds: {}", getDbContainer().getBinds());
            getDbContainer().waitingFor(Wait.forListeningPort());
            getDbContainer().start();
        }
        if (!getDbContainer().isRunning()) {
            throw new RuntimeException("Could not find RUNNING database container ");
        }
    }

    /**
     * Builds and returns database specific JDBC string <code>jdbc:%s://%s:%d/%s%s</code> with parameters as configured in 
     * <ul>
     * @see #getAdapterProfileFallback(),
     * {@link #getDbContainer} container ip address,
     * {@link #getDbContainer()} mapped port,
     * {@value #DATABASE_NAME} and  param queryContext.
     * 
     * 
     * @param queryContext Optional query string including question mark
     * @return the database specific JDBC string with mapped port binding
     */
    protected String generateJdbcUrl(String queryContext) {
        startDatabaseContainer();
        if (!getDbContainer().isRunning()) {
            throw new RuntimeException("Could not find RUNNING database container");
        }
        // MY_SQL_CONTAINER.withCreateContainerCmdModifier(modifier) //
        String serviceHost = getDbContainer().getContainerIpAddress();
        Integer mappedPort = getDbContainer().getMappedPort(getServicePort());// e.g. 32811
        log.info("generate jdbc url from {}, mapped Port: {}, bounded port: {}", serviceHost, mappedPort,
                getDbContainer().getBoundPortNumbers());
    
        if (queryContext == null) {
            queryContext = "";
        }
        String targetJDBC = // genJDBC;
                String.format("jdbc:%s://%s:%d/%s%s", getAdapterProfileFallback(), serviceHost, mappedPort, DATABASE_NAME, queryContext);
        log.info("used connect url: {}", targetJDBC);
        return targetJDBC;
    }

    protected void dbInit(ExtensionContext context)
            throws TorqueException, UnsupportedOperationException, IOException, InterruptedException {
                log.info("Starting from dockerfile: {} with image name: {}", DOCKERFILE , getDbContainer().getDockerImageName() );
                // before torque init
                getDbContainer().setStartupAttempts(3);
                startDatabaseContainer();
    }

    public DockerDatabaseAbstractExtension(String adapterProfileFallback) {
        super(adapterProfileFallback);
    }

    public GenericContainer getDbContainer() {
        return dbContainer;
    }

    public void setDbContainer(GenericContainer dbContainer) {
        this.dbContainer = dbContainer;
    }


    public int getServicePort() {
        return servicePort;
    }


    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

}