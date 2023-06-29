package org.apache.torque.generator.source.transform;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.apache.torque.generator.BaseTest;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests whether the loadAllSourceTransformer works correctly.
 */
public class SourceElementToModelTransformerTest extends BaseTest
{
    /**
     * The root of the source tree to transform.
     */
    private SourceElement rootSourceElement;

    /**
     * The transformer instance to test.
     */
    private SourceElementToModelTransformer transformer;

    /**
     * A fake controller state which can be used for transformation.
     */
    private ControllerState controllerState;

    @BeforeEach
    public void setUp()
    {
        rootSourceElement = new SourceElement("root");
        rootSourceElement.setAttribute("stringAttribute", "stringAttributeValue");
        rootSourceElement.setAttribute("stringBooleanAttribute", "true");
        SourceElement child0level1 = new SourceElement("child");
        child0level1.setAttribute("level", new Integer(1));
        child0level1.setAttribute("name", "child0level1");
        rootSourceElement.getChildren().add(child0level1);
        SourceElement child1level1 = new SourceElement("child");
        child1level1.setAttribute("level", new Integer(1));
        rootSourceElement.getChildren().add(child1level1);
        SourceElement child0level2 = new SourceElement("child");
        child0level2.setAttribute("level", new Integer(2));
        child0level2.setAttribute("name", "child0level2");
        child0level1.getChildren().add(child0level2);

        transformer = new SourceElementToModelTransformer();
        transformer.setModelRootClass(Root.class.getName());

        controllerState = new ControllerState();
        UnitConfiguration unitConfiguration = new UnitConfiguration();
        controllerState.setUnitConfiguration(unitConfiguration);
        unitConfiguration.setClassLoader(
                SourceElementToModelTransformerTest.class.getClassLoader());
    }

    /**
     * Checks that the SourceElementToModelTransformer can transform
     * a source element to a typed model.
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testTransform() throws Exception
    {
        // prepare

        // execute
        Object result = transformer.transform(
                rootSourceElement,
                controllerState);

        // assert
        assertEquals(Root.class, result.getClass());
        Root root = (Root) result;
        assertEquals("stringAttributeValue", root.stringAttribute);
        assertEquals(Boolean.TRUE, root.stringBooleanAttribute);
        assertEquals(2, root.childList.size());

        Child child0Level1 = root.childList.get(0);
        assertEquals(1, child0Level1.level);
        assertEquals("child0level1", child0Level1.name);
        assertSame(root, child0Level1.parent);
        assertEquals(1, child0Level1.childList.size());

        Child child1Level1 = root.childList.get(1);
        assertEquals(1, child1Level1.level);
        assertEquals(null, child1Level1.name);
        assertSame(root, child1Level1.parent);
        assertEquals(null, child1Level1.childList);

        Child child0Level2 = child0Level1.childList.get(0);
        assertEquals(2, child0Level2.level);
        assertEquals("child0level2", child0Level2.name);
        assertSame(child0Level1, child0Level2.parent);
        assertEquals(null, child0Level2.childList);
    }

    public static class Root
    {
        public String stringAttribute;

        public Boolean stringBooleanAttribute;

        public List<Child> childList;
    }

    public static class Child
    {
        public Object parent;

        public int level;

        public String name;

        public List<Child> childList;
    }
}
