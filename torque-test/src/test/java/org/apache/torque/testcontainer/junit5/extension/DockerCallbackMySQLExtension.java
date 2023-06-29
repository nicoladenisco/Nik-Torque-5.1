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

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

import java.io.File;
import java.time.ZoneId;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Testcontainer configuration for database Mysql
 * 
 * Requires some configuration data found for example in {@link DockerMySQLCallback}.
 * 
 * @author gk
 *
 */
@Testcontainers
public class DockerCallbackMySQLExtension extends DockerDatabaseAbstractExtension implements BeforeAllCallback, BeforeTestExecutionCallback, ExecutionCondition {

    static Logger log = LogManager.getLogger();
    
    private static int SERVICE_PORT = 3306;

    @Container
    public static GenericContainer DATABASE_CONTAINER = new GenericContainer<>(
                new ImageFromDockerfile().withDockerfile(new File(DOCKERFILE).toPath())).withExposedPorts(SERVICE_PORT) 
                        .withEnv("MYSQL_DATABASE", DATABASE_NAME).withEnv("MYSQL_USER", "torque")
                        .withEnv("MYSQL_PASSWORD", "torque").withEnv("MYSQL_ROOT_PASSWORD", "torque");

    
    public DockerCallbackMySQLExtension() {
       setDbContainer(DATABASE_CONTAINER);
       setServicePort(SERVICE_PORT);
    }
    
    protected static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled(
            "DockerCallbackExtension is enabled by default - checking internal state only");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        String actual = System.getProperty("torque.callback");
        if (actual == null) {
            actual = System.getenv("torque.callback");
        }
        if (actual != null && actual.matches("docker")) {
        }
        return ENABLED_BY_DEFAULT;
    }
    
    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        // log.info("starting call test context "+ context);
    }
    
    /**
     * Should be called first 
     * @see BeforeAll
     */
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class<?> clazz = context.getRequiredTestClass();
        log.warn("found and get annotation : {}", (Object[] )context.getRequiredTestClass().getAnnotations());
        DockerMySQLCallback dockerCallback = clazz.getAnnotation(DockerMySQLCallback.class);
        if (dockerCallback != null) {
            setSkipConfigurationCheck(dockerCallback.skipConfigurationCheck());
            setCustomUrlKey(dockerCallback.customUrlKey());
            setTargetConfigName(dockerCallback.targetConfigName());
            setTargetFileName(dockerCallback.targetFileName());
            setAdapterProfileFallback(dockerCallback.adapterProfileFallback());
        } else {
            super.beforeAll(context);
        }
        
        try {
            dbInit(context);
        } catch (Exception e) {
            final String logs = getDbContainer().getLogs();
            log.info("fetched container logs: {} ", logs);
            throw e;
        }
        String jdbcConnectionString = generateJdbcUrl("?loggerLevel=OFF&serverTimezone="+ ZoneId.systemDefault());
        context.getStore(Namespace.GLOBAL).put("jdbcConnectionString", jdbcConnectionString);
        context.getStore(Namespace.GLOBAL).put("container", getDbContainer());
        context.getStore(Namespace.GLOBAL).put("annotatedClass", dockerCallback);
        
        torqueInit(context);
    }

}
