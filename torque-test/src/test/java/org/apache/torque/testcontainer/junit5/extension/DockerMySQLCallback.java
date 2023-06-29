package org.apache.torque.testcontainer.junit5.extension;

import java.lang.annotation.Documented;

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.torque.junit5.extension.AdapterParameterResolver;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * {@link DockerMySQLCallback} is used to signal that the annotated test class or
 * test method is using Docker {@link Testcontainers} with database MySQL and Torque initialized accordingly.
 * 
 * It is currently enabled by overriding the default.
 * 
 * @author gkallidis
 * @version $Id$
 *
 */
// may have ParameterizedTest in test class -> Possible configuration error
@Tag("DockerMySQLCallback")
@Inherited
@Target( { ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE } )
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(AdapterParameterResolver.class)
@ExtendWith(DockerCallbackMySQLExtension.class)
@Documented
public @interface DockerMySQLCallback {
    
    String value() default "";
    
    String adapterProfileFallback() default "mysql";
        
    String customUrlKey() default "torque.dsfactory.bookstore.connection.url";
    
    String targetConfigName() default "torqueuser";
    
    String targetFileName() default "torque.usersettings.properties";
    
    boolean skipConfigurationCheck() default false;
    
}
