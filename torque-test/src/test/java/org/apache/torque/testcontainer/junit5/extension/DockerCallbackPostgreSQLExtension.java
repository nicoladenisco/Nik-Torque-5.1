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

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Testcontainer configuration for database Postgres
 * 
 * Requires some configuration data found for example in {@link DockerPostGresCallback}.
 * 
 * @author gk
 *
 */
@Testcontainers
public class DockerCallbackPostgreSQLExtension extends DockerDatabaseAbstractExtension implements BeforeAllCallback, BeforeTestExecutionCallback, ExecutionCondition {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(DockerCallbackPostgreSQLExtension.class);

    private static int SERVICE_PORT = 5432;

    private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled(
            "DockerCallbackExtension is enabled by default - checking internal state only");

    @Container
    public static GenericContainer DATABASE_CONTAINER = new GenericContainer<>(
            new ImageFromDockerfile().withDockerfile(new File(DOCKERFILE).toPath())).withExposedPorts(SERVICE_PORT) 
                    .withEnv("POSTGRES_DB", DATABASE_NAME).withEnv("POSTGRES_USER", "torque")
                    .withEnv("POSTGRES_PASSWORD", "torque").withLogConsumer(new Slf4jLogConsumer(log));
    
    public DockerCallbackPostgreSQLExtension() {
        setDbContainer(DATABASE_CONTAINER);
        setServicePort(SERVICE_PORT);
    }
    
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
    public void beforeAll(ExtensionContext context) throws Exception {
        // log.info("starting call context "+ context);
        Class<?> clazz = context.getRequiredTestClass();
        log.warn("found and get annotation : {}", (Object[] )context.getRequiredTestClass().getAnnotations());
        DockerPostGresCallback dockerCallback = clazz.getAnnotation(DockerPostGresCallback.class);
        if (dockerCallback != null) {
            log.warn("using annotation: {}",dockerCallback);
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
            final String logs = DATABASE_CONTAINER.getLogs();
            log.info("fetched container logs: {} ", logs);
            throw e;
        }
        String jdbcConnectionString = generateJdbcUrl("");
        context.getStore(Namespace.GLOBAL).put("jdbcConnectionString", jdbcConnectionString);
        context.getStore(Namespace.GLOBAL).put("container", DATABASE_CONTAINER);
        context.getStore(Namespace.GLOBAL).put("annotatedClass", dockerCallback);
        
        torqueInit(context);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        // log.info("starting call test context "+ context);
    }


}
