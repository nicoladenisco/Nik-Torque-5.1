package org.apache.torque.templates.transformer;

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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceException;
import org.apache.torque.generator.source.stream.FileSource;
import org.apache.torque.generator.source.stream.XmlSourceFormat;
import org.apache.torque.generator.source.transform.SourceElementToModelTransformer;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.model.Database;
import org.apache.torque.templates.model.ExternalSchema;
import org.apache.torque.templates.transformer.om.DatabaseAttributeName;
import org.apache.torque.templates.transformer.om.DatabaseChildElementName;
import org.apache.torque.templates.transformer.om.mapInit.DatabaseMapInitTransformer;

/**
 * A SourceTransformer which loads the external schemata tables and views
 * into the current graph.
 *
 * @version $Id: LoadExternalSchemaTransformer.java 1850969 2019-01-10 18:09:47Z painter $
 */
public class LoadExternalSchemaTransformer implements SourceTransformer
{
    /** The class log. */
    private static Log log
    	= LogFactory.getLog(LoadExternalSchemaTransformer.class);

    /**
     * The base dir for the external schema,
     * or null to compute from the current source file.
     */
    private File baseDir;

    /**
     * The transformer creating the typed model from the source graph.
     */
    private final SourceElementToModelTransformer toModelTransformer
        = new SourceElementToModelTransformer(Database.class);

    /**
     * Standard constructor.
     */
    public LoadExternalSchemaTransformer()
    {
    }

    /**
     * Constructor to override base dir.
     *
     * @param baseDir the new base dir.
     */
    public LoadExternalSchemaTransformer(final File baseDir)
    {
        this.baseDir = baseDir;
    }

    /**
     * Loads the external schemata tables into the current graph.
     * The external database element is added as child of the
     * external-schema element.
     * Also, an all-tables child element is added to the root element,
     * which is filled with all tables from the external schema plus its own
     * tables.
     *
     * @param modelRoot the database root element of the source tree, not null.
     * @param controllerState the controller state, not null.
     * @return SourceElement
     * @throws SourceTransformerException if the transformation fails.
     */
    @Override
    public SourceElement transform(
            final Object modelRoot,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        final SourceElement root = (SourceElement) modelRoot;
        final List<SourceElement> externalSchemaElementList
        = root.getChildren(TorqueSchemaElementName.EXTERNAL_SCHEMA);
        SourceElement allTables = root.getChild(
                DatabaseChildElementName.ALL_TABLES);
        if (allTables == null)
        {
            allTables = new SourceElement(DatabaseChildElementName.ALL_TABLES);
            root.getChildren().add(allTables);
        }
        SourceElement allViews = root.getChild(
                DatabaseChildElementName.ALL_VIEWS);
        if (allViews == null)
        {
            allViews = new SourceElement(DatabaseChildElementName.ALL_VIEWS);
            root.getChildren().add(allViews);
        }

        final String rootDatabaseName = (String) root.getAttribute(
                DatabaseAttributeName.ROOT_DATABASE_NAME);
        for (final SourceElement externalSchemaElement : externalSchemaElementList)
        {
            File externalSchemaBaseDir;
            if (this.baseDir == null)
            {
                final File currentSourceFile = controllerState.getSourceFile();
                externalSchemaBaseDir = currentSourceFile.getParentFile();
            }
            else
            {
                externalSchemaBaseDir = baseDir;
            }
            final String relativePath = externalSchemaElement.getAttribute(
                    TorqueSchemaAttributeName.FILENAME)
                    .toString();
            final File externalSchemaPath
                = new File(externalSchemaBaseDir, relativePath);
            try
            {
                final FileSource fileSource = new FileSource(
                        new XmlSourceFormat(),
                        externalSchemaPath,
                        controllerState);
                final SourceElement externalSchemaRootElement
                = fileSource.getRootElement();
                externalSchemaRootElement.setAttribute(
                        DatabaseAttributeName.ROOT_DATABASE_NAME,
                        rootDatabaseName);
                DatabaseMapInitTransformer.setDatabaseMapInitClassNameAttributes(
                        externalSchemaRootElement,
                        rootDatabaseName,
                        controllerState);

                this.transform(externalSchemaRootElement, controllerState);

                externalSchemaElement.getChildren().add(
                        externalSchemaRootElement);

                final SourceElement externalAllTables
                = externalSchemaRootElement.getChild(
                        DatabaseChildElementName.ALL_TABLES);

                // fill root's all-tables with all external tables
                for (final SourceElement externalTable
                        : externalAllTables.getChildren(
                                TorqueSchemaElementName.TABLE))
                {
                    allTables.getChildren().add(externalTable);
                }
                // fill root's all-views with all external views
                for (final SourceElement externalView
                        : externalAllTables.getChildren(
                                TorqueSchemaElementName.VIEW))
                {
                    allViews.getChildren().add(externalView);
                }
            }
            catch (final SourceException e)
            {
                log.error("Could not construct source from schema file "
                        + externalSchemaPath,
                        e);
                throw new SourceTransformerException(e);
            }
        }
        // add own tables to root's all-tables
        for (final SourceElement table
                : root.getChildren(TorqueSchemaElementName.TABLE))
        {
            allTables.getChildren().add(table);
        }
        // add own views to root's all-views
        for (final SourceElement view
                : root.getChildren(TorqueSchemaElementName.VIEW))
        {
            allViews.getChildren().add(view);
        }
        return root;
    }

    /**
     * Loads the external schemata tables into the current graph.
     * The external database element is added as child of the
     * external-schema element.
     * Also, an all-tables child element is added to the root element,
     * which is filled with all tables from the external schema plus its own
     * tables.
     *
     * @param database the database root element of the source tree, not null.
     * @param controllerState the controller state, not null.
     * @return Database object
     * @throws SourceTransformerException if the transformation fails.
     */
    public Database transform(
            final Database database,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        for (final ExternalSchema externalSchema : database.externalSchemaList)
        {
            File externalSchemaBaseDir;
            if (this.baseDir == null)
            {
                final File currentSourceFile = controllerState.getSourceFile();
                externalSchemaBaseDir = currentSourceFile.getParentFile();
            }
            else
            {
                externalSchemaBaseDir = baseDir;
            }
            final File externalSchemaPath
                = new File(externalSchemaBaseDir, externalSchema.filename);
            try
            {
                final FileSource fileSource = new FileSource(
                        new XmlSourceFormat(),
                        externalSchemaPath,
                        controllerState);
                final SourceElement externalSchemaRootElement
                = fileSource.getRootElement();
                final Database externalDatabase
                = (Database) toModelTransformer.transform(
                        externalSchemaRootElement,
                        controllerState);
                externalDatabase.rootDatabaseName = database.rootDatabaseName;
                // TODO fix static access
                DatabaseMapInitTransformer.setDatabaseMapInitClassNameAttributes(
                        externalSchemaRootElement,
                        database.rootDatabaseName,
                        controllerState);

                this.transform(externalDatabase, controllerState);

                externalSchema.database = externalDatabase;

                // fill root's all-tables with all external tables
                database.allTables.addAll(externalDatabase.allTables);
                database.allViews.addAll(externalDatabase.allViews);
            }
            catch (final SourceException e)
            {
                log.error("Could not construct source from schema file "
                        + externalSchemaPath,
                        e);
                throw new SourceTransformerException(e);
            }
        }
        // add own tables to root's all-tables
        database.allTables.addAll(database.tableList);
        // add own views to root's all-views
        database.allViews.addAll(database.viewList);
        return database;
    }
}
