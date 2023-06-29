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

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.processor.string.Camelbacker;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.stream.CombinedFileSource;
import org.apache.torque.generator.source.transform.AttributeTransformer;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.transformer.LoadExternalSchemaTransformer;
import org.apache.torque.templates.transformer.om.DatabaseAttributeName;
import org.apache.torque.templates.transformer.om.DatabaseChildElementName;
import org.apache.torque.templates.transformer.om.OMTransformer;

/**
 * A transformer providing extra attributes for the generation of the
 * mapInit classes. It collects all tables for a database with the same name
 * and also sets attributes needed for java generation.
 *
 * @version $Id: DatabaseMapInitTransformer.java 1896195 2021-12-20 17:41:20Z gk $
 */
public class DatabaseMapInitTransformer extends AttributeTransformer
{
    /** The camelbacker for creating the MapInit class name. */
    private static Camelbacker camelbacker = new Camelbacker();

    static
    {
        camelbacker.setDefaultLowerCase(false);
    }

    /**
     * The element under which all databases with same names are collected.
     */
    private static final String DATABASE_SETS_ELEMENT = "databaseSets";

    /**
     * The element under which the tables of all databases with the same name
     * are collected.
     */
    private static final String DATABASE_SET_ELEMENT = "databaseSet";

    /** The transformer for table elements. */
    private final DatabaseMapInitTableTransformer tableTransformer
        = new DatabaseMapInitTableTransformer();

    /**
     * Constructor.
     *
     * @throws SourceTransformerException if the attribute map is malformed.
     */
    public DatabaseMapInitTransformer() throws SourceTransformerException
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
                    DatabaseMapInitTransformer.class.getResourceAsStream(
                            "DatabaseMapInitTransformer.properties"),
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
            Object modelRoot,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        SourceElement rootElement = (SourceElement) modelRoot;
        if (!CombinedFileSource.ROOT_ELEMENT_NAME.equals(
                rootElement.getName()))
        {
            throw new IllegalArgumentException("Illegal element Name "
                    + rootElement.getName());
        }
        SourceElement databaseSetsElement
            = new SourceElement(DATABASE_SETS_ELEMENT);
        rootElement.getChildren().add(databaseSetsElement);

        for (SourceElement fileElement
                : rootElement.getChildren(CombinedFileSource.FILE_ELEMENT_NAME))
        {
            SourceElement databaseElement
            = fileElement.getChild(TorqueSchemaElementName.DATABASE);
            if (databaseElement == null)
            {
                throw new IllegalArgumentException("The root element of file "
                        + fileElement.getAttribute(
                                CombinedFileSource.PATH_ATTRIBUTE_NAME)
                        + " is unknown, should be "
                        + TorqueSchemaElementName.DATABASE.getName());
            }
            String name = (String) databaseElement.getAttribute(
                    TorqueSchemaAttributeName.NAME.getName());
            if (name == null)
            {
                throw new SourceTransformerException("The attribute "
                        + TorqueSchemaAttributeName.NAME.getName()
                        + " on element "
                        + databaseElement.getName()
                        + " is null");
            }
            OMTransformer.setRootDatabaseNameAttribute(databaseElement);
            // load referenced external schemata
            String path = (String) fileElement.getAttribute(
                    CombinedFileSource.PATH_ATTRIBUTE_NAME);
            File pathFile = new File(path);
            SourceTransformer loadExternalSchemaTransformer
                = new LoadExternalSchemaTransformer(
                    pathFile.getParentFile());
            loadExternalSchemaTransformer.transform(
                    databaseElement,
                    controllerState);

            SourceElement allTablesRoot
            = databaseElement.getChild(DatabaseChildElementName.ALL_TABLES);

            SourceElement databaseSetElement = getDatabaseSetWithDatabaseName(
                    databaseSetsElement,
                    name);
            if (databaseSetElement == null)
            {
                databaseSetElement = new SourceElement(DATABASE_SET_ELEMENT);
                databaseSetElement.setAttribute(
                        TorqueSchemaAttributeName.NAME,
                        name);
                databaseSetsElement.getChildren().add(databaseSetElement);
            }
            for (SourceElement tableElement : allTablesRoot.getChildren(
                    TorqueSchemaElementName.TABLE))
            {
                databaseSetElement.getChildren().add(tableElement);
            }
        }

        for (SourceElement databaseSetElement
                : databaseSetsElement.getChildren(DATABASE_SET_ELEMENT))
        {
            String name = (String) databaseSetElement.getAttribute(
                    TorqueSchemaAttributeName.NAME);
            setDatabaseMapInitClassNameAttributes(
                    databaseSetElement,
                    name,
                    controllerState);
            super.transform(databaseSetElement, controllerState);
            for (SourceElement tableElement
                    : databaseSetElement.getChildren(TorqueSchemaElementName.TABLE))
            {
                tableTransformer.transform(tableElement, controllerState);
            }
        }
        return rootElement;
    }

    /**
     * Sets the databaseMapInitClassName and baseDatabaseMapInitClassName
     * attributes on a source element.
     *
     * @param sourceElement the element where the attribute should be set,
     *        not null.
     * @param databaseName the name of the database element, not null.
     * @param controllerState the controller state, not null.
     */
    public static void setDatabaseMapInitClassNameAttributes(
            SourceElement sourceElement,
            String databaseName,
            ControllerState controllerState)
    {
        String databaseMapInitClassName
        = controllerState.getOption(
                TemplateOptionName.OM_DATABASE_MAP_INIT_CLASS_NAME_PREFIX)
        + camelbacker.process(databaseName)
        + controllerState.getOption(
                TemplateOptionName.OM_DATABASE_MAP_INIT_CLASS_NAME_SUFFIX);
        sourceElement.setAttribute(
                DatabaseAttributeName.DATABASE_MAP_INIT_CLASS_NAME,
                databaseMapInitClassName);
        String baseMapInitClassName
        = controllerState.getOption(
                TemplateOptionName.OM_BASE_DATABASE_MAP_INIT_CLASS_NAME_PREFIX)
        + databaseMapInitClassName;
        sourceElement.setAttribute(
                DatabaseAttributeName.BASE_DATABASE_MAP_INIT_CLASS_NAME,
                baseMapInitClassName);
    }

    private SourceElement getDatabaseSetWithDatabaseName(
            SourceElement databaseSetsElement, String name)
    {
        return databaseSetsElement.getChildren(DATABASE_SET_ELEMENT).stream()
                .filter(databaseSetElement -> databaseSetElement.getAttribute(TorqueSchemaAttributeName.NAME)
                .equals(name)).findFirst().orElse(null);
    }
}
