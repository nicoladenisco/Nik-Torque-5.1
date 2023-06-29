package org.apache.torque.testcontainer.postgresql;

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

import org.apache.torque.DataTest;
import org.apache.torque.testcontainer.junit5.extension.DockerCallback;
import org.apache.torque.testcontainer.junit5.extension.DockerCallbackPostgreSQLExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

/**
 * Docker adapter tests.
 *
 * @author <a href="mailto:gk@apache.org">Georg Kallidis</a>
 * @version $Id: DataTest.java 1869081 2019-10-28 16:17:11Z gk $
 */
@DockerCallback(adapterProfileFallback="postgresql")
public class DataContainerTest extends DataTest
{
    /**
     * statically registered extensions are registered after extendwith extensions
     */
    @RegisterExtension
    static DockerCallbackPostgreSQLExtension dce = new DockerCallbackPostgreSQLExtension();
   
}
