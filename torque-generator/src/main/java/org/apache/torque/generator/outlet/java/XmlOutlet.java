package org.apache.torque.generator.outlet.java;

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

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.stream.SourceToXml;

/**
 * Outputs the source tree as XML.
 */
public class XmlOutlet extends OutletWithoutMergepoints
{
    /** Whether id attributes should be automatically created in the output. */
    private boolean createIdAttributes = false;

    /** The XML outputter. */
    private final SourceToXml sourceToXml = new SourceToXml();
    /**
     * Constructor.
     *
     * @param name the qualified name of the outlet.
     */
    public XmlOutlet(QualifiedName name)
    {
        super(name);
    }

    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        String result = sourceToXml.toXml(
                (SourceElement) controllerState.getModelRoot(),
                createIdAttributes);

        return new OutletResult(result.toString());
    }

    /**
     * Returns whether id attributes are automatically created in the output.
     *
     * @return true if id attributes are automatically created, false otherwise.
     */
    public boolean isCreateIdAttributes()
    {
        return createIdAttributes;
    }

    /**
     * Sets whether id attributes should be automatically created in the output.
     *
     * @param createIdAttributes true if id attributes should be automatically
     *        created, false otherwise.
     */
    public void setCreateIdAttributes(boolean createIdAttributes)
    {
        this.createIdAttributes = createIdAttributes;
    }
}
