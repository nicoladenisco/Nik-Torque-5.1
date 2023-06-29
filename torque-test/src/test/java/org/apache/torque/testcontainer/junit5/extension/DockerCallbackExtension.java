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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.adapter.Adapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

/**
 * Docker Test configuration, updates Torque configuration files in {@link #getTargetFileName()}, 
 * which is set by database or default annotation {@link DockerCallback#targetFileName()}.
 * 
 * Uses by default a base configuration found in <code>src/test/profile/%s/Torque4Test.xml</code>.
 * 
 * @author gk
 *
 */

public class DockerCallbackExtension implements BeforeAllCallback, ExecutionCondition {

    protected static final String DEFAULT_TEST_PROFILE_TORQUE4_TEST_XML = "src/test/profile/%s/Torque4Test.xml";

    protected static final String TORQUE_WRAPPER_CONFIG_SYSTEM_PROPERTY = "torque.wrapper.configuration.file";

    private static Logger log = LogManager.getLogger();

    protected static Adapter defaultAdapter;
    
    private String adapterProfileFallback;
    
    private String customUrlKey;
    
    private String targetConfigName;
    
    private String targetFileName;
    
    boolean skipConfigurationCheck = false;
    
    protected static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled(
            "DockerCallbackExtension is enabled by default - checking internal state only");
    
    public DockerCallbackExtension() {
     
    }
    
    public DockerCallbackExtension(String adapterProfileFallback)  {
        this.adapterProfileFallback =adapterProfileFallback;
    }
    
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        String actual = System.getProperty("torque.callback");
        if (actual == null) {
            actual = System.getenv("torque.callback");
        }
        if (actual != null && actual.matches("docker")) {
        }
        return ENABLED_BY_DEFAULT;
    }
 
    /**
     * calls first super {@link #beforeAll(ExtensionContext)}
     * 
     * @see BeforeAll
     */
    public void beforeAll(ExtensionContext context) throws Exception {
        Annotation[] annots = context.getTestClass().get().getAnnotations();
        log.info("found and get context.getElement(): {}", context.getElement());
        log.info("found and get context.getTestClass(): {}", context.getTestClass());
        for (Annotation annot : annots) {
            if (annot instanceof DockerCallback) {
                log.warn("using annotation: {}",annot);
                setAdapterProfileFallback(getDockerCallback(annot).adapterProfileFallback());
                setSkipConfigurationCheck(getDockerCallback(annot).skipConfigurationCheck());
                setCustomUrlKey(getDockerCallback(annot).customUrlKey());
                setTargetConfigName(getDockerCallback(annot).targetConfigName());
                setTargetFileName(getDockerCallback(annot).targetFileName());
                //torqueInit(context);
            }
        }   
    }
    
    public DockerCallback getDockerCallback(Annotation annot) {
        return (DockerCallback)annot;
    }
    
    
    protected void torqueInit(ExtensionContext context)
            throws TorqueException, UnsupportedOperationException, IOException, InterruptedException {

        // TODO do resource filtering and read from properties
        String filePath = System.getProperty(TORQUE_WRAPPER_CONFIG_SYSTEM_PROPERTY);
        if (filePath == null) {
            log.warn("Could not resolve system property: {}", TORQUE_WRAPPER_CONFIG_SYSTEM_PROPERTY);
            filePath = System.getenv(TORQUE_WRAPPER_CONFIG_SYSTEM_PROPERTY);
        }
        if (filePath == null) {
            filePath = getDefaultTestProfileFallback(context);
            log.warn("Could not resolve system env/property: {}, using default: {}",
                    TORQUE_WRAPPER_CONFIG_SYSTEM_PROPERTY, filePath);
        }
        log.info("resolved filePath: {}", filePath);
        File targetFile = new File(filePath);
        Path torqueConfBase = Paths.get(targetFile.toURI()).getParent();
        
        if (!skipConfigurationCheck ) {
            CombinedConfigurationBuilder combinedBuilder = new CombinedConfigurationBuilder()
                    .configure(new Parameters().fileBased()
                            .setFileName(filePath)
                            .setListDelimiterHandler(new DefaultListDelimiterHandler(','))
                   );
            try {
                CombinedConfiguration cc = combinedBuilder.getConfiguration();
                PropertiesConfiguration innerConfRef = (PropertiesConfiguration) cc.getConfiguration(targetConfigName);
                assertTrue(innerConfRef != null, "Could not locate target configuration with config-namen: " + targetConfigName);
            } catch (ConfigurationException e) {
               log.error(e.getMessage(), e);
            }            
        }
        String jdbcConnectionString = (String) context.getStore(Namespace.GLOBAL).get("jdbcConnectionString");
        updateTorque(torqueConfBase, targetFileName, customUrlKey, jdbcConnectionString);

        synchronized (DockerCallbackExtension.class) {
            if (!Torque.isInit()) {
                Torque.init(filePath);
            }
        }
        defaultAdapter = Torque.getDatabase(Torque.getDefaultDB()).getAdapter();
        log.info("using adapter: {}", defaultAdapter);
        context.getStore(Namespace.GLOBAL).put("adapter", defaultAdapter);

    }

    private String getDefaultTestProfileFallback( ExtensionContext context ) {
        if (adapterProfileFallback == null) {
            throw new AnnotationFormatError("No profile is set. Set it in annotation DockerCallback with adapterProfileFallback" );
        }
        return String.format(DEFAULT_TEST_PROFILE_TORQUE4_TEST_XML, adapterProfileFallback);
    }
    
  
    private void updateTorque(Path torqueConfBase, String fileName, String customUrlKey, String jdbcConnectionString) {
        try {
            String customUrl = customUrlKey+"=" + jdbcConnectionString;
            // override and set mapped port in url, which is known only at runtime.
            File file = torqueConfBase.resolve(fileName).toFile();
            try (FileOutputStream fop = new FileOutputStream(file)) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                fop.write(customUrl.getBytes());
                fop.flush();
            }
        } catch (Exception e) {
            fail();
        }
    }

    public String getAdapterProfileFallback() {
        return adapterProfileFallback;
    }

    public void setAdapterProfileFallback(String adapterProfileFallback) {
        this.adapterProfileFallback = adapterProfileFallback;
    }

    public String getCustomUrlKey() {
        return customUrlKey;
    }

    public void setCustomUrlKey(String customUrlKey) {
        this.customUrlKey = customUrlKey;
    }

    public String getTargetConfigName() {
        return targetConfigName;
    }

    public void setTargetConfigName(String targetConfigName) {
        this.targetConfigName = targetConfigName;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public boolean isSkipConfigurationCheck() {
        return skipConfigurationCheck;
    }

    public void setSkipConfigurationCheck(boolean skipConfigurationCheck) {
        this.skipConfigurationCheck = skipConfigurationCheck;
    }

}
