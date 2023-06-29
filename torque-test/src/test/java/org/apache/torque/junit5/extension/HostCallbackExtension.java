package org.apache.torque.junit5.extension;

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
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.Torque;
import org.apache.torque.adapter.Adapter;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/**
 * Legacy host based testing requires a database environment settings available
 * and started.
 * 
 * @author gk
 *
 */
public class HostCallbackExtension implements BeforeAllCallback, ExecutionCondition {

    private static final String DEFAULT_TEST_PROFILE_TORQUE_PROPERTIES = "src/test/profile/%s/Torque.properties";

    private static final Logger log = LogManager.getLogger(HostCallbackExtension.class);

    /** The system property containing the path to the configuration file. */
    public static final String CONFIG_FILE_SYSTEM_PROPERTY = "torque.configuration.file";

    protected static Adapter defaultAdapter;

    public boolean skip = true;

    private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled(
            "HostCallbackExtension does not enable test - checking internal state only");

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        String actual = System.getProperty("torque.callback");
        if (actual == null) {
            actual = System.getenv("torque.callback");
        }
        if (actual != null) {
            log.debug("checking host based environment call context, torque.callback: {} ",  actual);
            if (actual.matches("host")) {
                skip = false;
            } 
        }
        return ENABLED_BY_DEFAULT;
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (!isHostCallback(context)) {
            throw new Exception("Use @HostCallback annotation to use HostCallbackExtension. Class: "
                    + context.getRequiredTestClass());
        }
        if (skip) {
            log.info("skipping host based environment call context, torque.callback: {} ",
                    System.getenv("torque.callback"));
            return;
        } else {
            // log.info("starting call context "+ context);
            setUp(context);
            context.getStore(Namespace.GLOBAL).put("adapter", defaultAdapter);
        }
    }

    /**
     * Initialize Torque on the first setUp(). Subclasses which override setUp()
     * must call super.setUp() as their first action.
     */
    public void setUp(ExtensionContext context) throws Exception {
        String filePath = System.getProperty(CONFIG_FILE_SYSTEM_PROPERTY);
        if (filePath == null) {
            log.warn("Could not resolve system property: {}", CONFIG_FILE_SYSTEM_PROPERTY);
            filePath = System.getenv(CONFIG_FILE_SYSTEM_PROPERTY);
        }
        if (filePath == null) {
            filePath = getDefaultTestProfileFallback(context);
            log.warn("Could not resolve system env/property: {}, using default: {}", CONFIG_FILE_SYSTEM_PROPERTY,
                    filePath);
        }

        synchronized (HostCallbackExtension.class) {
            if (!Torque.isInit()) {
                Torque.init(filePath);
            }
        }
        defaultAdapter = Torque.getDatabase(Torque.getDefaultDB()).getAdapter();
    }

    private String getDefaultTestProfileFallback(ExtensionContext context) {
        Class<?> clazz = context.getRequiredTestClass();
        HostCallback hostCallback = clazz.getAnnotation(HostCallback.class);
        String adapterProfile = hostCallback.adapterProfileFallback();
        if (adapterProfile == null) {
            adapterProfile = "mysql";
        }
        return String.format(DEFAULT_TEST_PROFILE_TORQUE_PROPERTIES, adapterProfile);
    }

    private static boolean isHostCallback(ExtensionContext context) {
        return isAnnotated(context.getRequiredTestClass(), HostCallback.class);
    }

}
