package org.apache.torque.generator.processor.string;

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

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.apache.torque.generator.BaseTest;
import org.junit.Test;

/**
 * Unit test for the class OrganizeImportsProcessor.
 *
 * @version $Id: $
 */
public class RemoveUnusedImportsProcessorTest extends BaseTest
{
    /** System under test. */
    private final RemoveUnusedImportsProcessor organizeImportsProcessor
        = new RemoveUnusedImportsProcessor();

    @Test
    public void testProcess()
    {
        String result = organizeImportsProcessor.process(
                "package abc.def\r\n"
                        + "import a.b.c.Class1 ; \n"
                        + "import a.b.c.Class2;\r\n"
                        + "import a.b.c.Class3;\r\n"
                        + "ClaSS1\r\n"
                        + "class2\n"
                        + "Class3");
        assertEquals("package abc.def\r\n"
                + "import a.b.c.Class3;\r\n"
                + "ClaSS1\r\n"
                + "class2\n"
                + "Class3", result);
    }

    @Test
    public void testProcessLong() throws Exception
    {
        String source = IOUtils.toString(getClass().getResourceAsStream("removeUnusedImports.txt"));
        String result = organizeImportsProcessor.process(source);
        String expected = IOUtils.toString(getClass().getResourceAsStream("removeUnusedExpected.txt"));
        assertEquals(expected, result);
    }
}
