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
import java.util.ArrayList;
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
import org.apache.torque.templates.model.IncludeSchema;

/**
 * A SourceTransformer which includes other schemata into the current graph.
 *
 * @version $Id: IncludeSchemaTransformer.java 1850969 2019-01-10 18:09:47Z painter $
 */
public class IncludeSchemaTransformer implements SourceTransformer
{
    /** The class log. */
    private static Log log
    = LogFactory.getLog(IncludeSchemaTransformer.class);

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
    public IncludeSchemaTransformer()
    {
    }

    /**
     * Constructor to override base dir.
     *
     * @param baseDir the new base dir.
     */
    public IncludeSchemaTransformer(final File baseDir)
    {
        this.baseDir = baseDir;
    }

    /**
     * Loads the included schema objects into the current source graph.
     *
     * @param modelRoot the database root element of the source tree, not null.
     * @param controllerState the controller state, not null.
     *
     * @throws SourceTransformerException if the transformation fails.
     */
    @Override
    public SourceElement transform(
            final Object modelRoot,
            final ControllerState controllerState)
                    throws SourceTransformerException
    {
        final SourceElement root = (SourceElement) modelRoot;
        final List<SourceElement> includeSchemaList
        = root.getChildren(TorqueSchemaElementName.INCLUDE_SCHEMA);
        final List<SourceElement> childrenList = root.getChildren();

        for (final SourceElement includeSchemaElement : includeSchemaList)
        {
            File includeSchemaBaseDir;
            if (this.baseDir == null)
            {
                final File currentSourceFile = controllerState.getSourceFile();
                includeSchemaBaseDir = currentSourceFile.getParentFile();
            }
            else
            {
                includeSchemaBaseDir = baseDir;
            }
            final String relativePath = includeSchemaElement.getAttribute(
                    TorqueSchemaAttributeName.FILENAME)
                    .toString();
            final File includeSchemaPath
                = new File(includeSchemaBaseDir, relativePath);
            log.trace("Trying to read included file " + includeSchemaPath);
            try
            {
                final FileSource fileSource = new FileSource(
                        new XmlSourceFormat(),
                        includeSchemaPath,
                        controllerState);
                final SourceElement includeSchemaRootElement
                = fileSource.getRootElement();
                log.trace("successfully read included file "
                        + includeSchemaPath);

                this.transform(includeSchemaRootElement, controllerState);

                // disattach children from their current parent
                // so that the new parent is the primary parent
                final List<SourceElement> toIncludeList
                    = new ArrayList<>(
                        includeSchemaRootElement.getChildren());
                for (final SourceElement childToInclude : toIncludeList)
                {
                    childToInclude.getParents().clear();
                }

                childrenList.addAll(toIncludeList);

                log.trace("finished processing included file "
                        + includeSchemaPath);
            }
            catch (final SourceException e)
            {
                log.error("Could not construct source from schema file "
                        + includeSchemaPath,
                        e);
                throw new SourceTransformerException(e);
            }
        }
        return root;
    }

    /**
     * Loads the included schema content into the current model.
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
        // copy original list to iterate so we can modify the model list
        // during iteration
        final List<IncludeSchema> originalIncludeSchemaList
            = new ArrayList<>(database.includeSchemaList);
        for (final IncludeSchema includeSchema : originalIncludeSchemaList)
        {
            File includeSchemaBaseDir;
            if (this.baseDir == null)
            {
                final File currentSourceFile = controllerState.getSourceFile();
                includeSchemaBaseDir = currentSourceFile.getParentFile();
            }
            else
            {
                includeSchemaBaseDir = baseDir;
            }
            final File includeSchemaPath
                = new File(includeSchemaBaseDir, includeSchema.filename);
            log.trace("Trying to read included file " + includeSchemaPath);
            try
            {
                final FileSource fileSource = new FileSource(
                        new XmlSourceFormat(),
                        includeSchemaPath,
                        controllerState);
                final SourceElement includeSchemaRootElement
                = fileSource.getRootElement();
                log.trace("successfully read included file "
                        + includeSchemaPath);
                final Database includedDatabase
                = (Database) toModelTransformer.transform(
                        includeSchemaRootElement,
                        controllerState);

                this.transform(includedDatabase, controllerState);

                // add child elements from loaded schema
                database.optionList.addAll(includedDatabase.optionList);
                database.includeSchemaList.addAll(
                        includedDatabase.includeSchemaList);
                database.externalSchemaList.addAll(
                        includedDatabase.externalSchemaList);
                database.domainList.addAll(includedDatabase.domainList);
                database.tableList.addAll(includedDatabase.tableList);
                database.viewList.addAll(includedDatabase.viewList);

                log.trace("finished processing included file "
                        + includeSchemaPath);
            }
            catch (final SourceException e)
            {
                log.error("Could not construct source from schema file "
                        + includeSchemaPath,
                        e);
                throw new SourceTransformerException(e);
            }
        }
        return database;
    }
}
