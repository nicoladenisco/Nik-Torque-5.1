package org.apache.torque.templates.transformer.om.mapInit;

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

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.AttributeTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.transformer.om.OMTableAndViewTransformer;

/**
 * A transformer which sets attributes for the table elements
 * for generating the map init classes.
 *
 * @version $Id: DatabaseMapInitTableTransformer.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class DatabaseMapInitTableTransformer extends AttributeTransformer
{
    /**
     * Constructor.
     *
     * @throws SourceTransformerException if the attribute map is malformed.
     */
    public DatabaseMapInitTableTransformer() throws SourceTransformerException
    {
        super(getTransformerProperties());
    }

    /**
     * Returns the Reader to read the transformer properties from.
     *
     * @return the reader, not null.
     */
    private static Reader getTransformerProperties()
    {
        try
        {
            return new InputStreamReader(
                    DatabaseMapInitTableTransformer.class.getResourceAsStream(
                            "DatabaseMapInitTableTransformer.properties"),
                    "ISO-8859-1");
        }
        catch (UnsupportedEncodingException e)
        {
            // will not happen
            throw new RuntimeException(e);
        }
    }

    @Override
    public SourceElement transform(
            Object tableModel,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        SourceElement tableElement = (SourceElement) tableModel;
        if (!TorqueSchemaElementName.TABLE.getName().equals(
                tableElement.getName()))
        {
            throw new IllegalArgumentException("Illegal element Name "
                    + tableElement.getName());
        }
        OMTableAndViewTransformer.setJavaNameAttribute(
                tableElement,
                controllerState);
        super.transform(tableElement, controllerState);
        OMTableAndViewTransformer.setPeerImplGetterAttribute(tableElement);
        return tableElement;
    }
}
