package org.apache.torque.templates.transformer.om;

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
import java.util.ArrayList;
import java.util.List;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformer;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TemplateOptionName;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;
import org.apache.torque.templates.model.Database;
import org.apache.torque.templates.transformer.IncludeSchemaTransformer;
import org.apache.torque.templates.transformer.LoadExternalSchemaTransformer;
import org.apache.torque.templates.transformer.om.mapInit.DatabaseMapInitTransformer;

/**
 * Performs the transformations which are necessary to apply the OM templates
 * to the source tree.
 * This transformer performs the following actions:
 * <ul>
 * <li>adds attributes to source elements</li>
 * <li>links elements to other source elements</li>
 * </ul>
 * No elements or attributes are deleted.
 *
 * This transformer calls the following other transformers on the source tree:
 * <ul>
 * <li>IncludeSchemaTransformer on the root node</li>
 * <li>LoadExternalSchemaTransformer on the root node</li>
 * <li>OMTableAndViewTransformer on all tables and views</li>
 * <li>OMForeignKeyColumnTransformer on all columns</li>
 * <li>OMForeignKeyTransformer on all foreign keys (two passes)</li>
 * </ul>
 *
 * $Id: OMTransformer.java 1850969 2019-01-10 18:09:47Z painter $
 */
public class OMTransformer implements SourceTransformer
{
  /**
   * The transformer which adds references to the foreign keys referenced by
   * this column or referencing this column.
   *
   * @see OMForeignKeyColumnTransformer
   */
  private static final OMForeignKeyColumnTransformer foreignKeyColumnTransformer
     = new OMForeignKeyColumnTransformer();

  /**
   * The transformer which adds references to the referenced and referencing
   * columns, tables and fields to the foreign key.
   *
   * @see OMForeignKeyTransformer
   */
  private static final OMForeignKeyTransformer foreignKeyTransformer
     = new OMForeignKeyTransformer();

  /**
   * The transformer which adds transforms the table/view and column elements
   * of the schema.
   *
   * @see OMTableAndViewTransformer
   */
  private static final OMTableAndViewTransformer tableOrViewTransformer;

  /**
   * The transformer which loads the external schemata.
   *
   * @see LoadExternalSchemaTransformer
   */
  private static final SourceTransformer loadExternalSchemaTransformer
     = new LoadExternalSchemaTransformer();

  /**
   * The transformer which includes the included schemata.
   *
   * @see LoadExternalSchemaTransformer
   */
  private static final SourceTransformer includeSchemaTransformer
     = new IncludeSchemaTransformer();

  /** The transformer which creates the joinGetters. */
  private static OMJoinGetterTransformer joinGetterTransformer
     = new OMJoinGetterTransformer();

  /** Trasformazione per indici univoci */
  private static OMUnicesTransformer uniqueTransformer
     = new OMUnicesTransformer();

  static
  {
    try
    {
      tableOrViewTransformer = new OMTableAndViewTransformer();
    }
    catch(final SourceTransformerException e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Transforms the source tree so it can be used by the om templates.
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
    TemplateOptionName.checkRequiredOptions(
       controllerState,
       TemplateOptionName.OM_PACKAGE);

    setRootDatabaseNameAttribute(root);
    DatabaseMapInitTransformer.setDatabaseMapInitClassNameAttributes(
       root,
       (String) root.getAttribute(TorqueSchemaAttributeName.NAME),
       controllerState);

    // include included schemata
    includeSchemaTransformer.transform(root, controllerState);
    // load referenced external schemata
    loadExternalSchemaTransformer.transform(root, controllerState);

    final List<SourceElement> allTablesAndViews = new ArrayList<>();
    allTablesAndViews.addAll(
       root.getChild(DatabaseChildElementName.ALL_TABLES)
          .getChildren(TorqueSchemaElementName.TABLE));
    allTablesAndViews.addAll(
       root.getChild(DatabaseChildElementName.ALL_VIEWS)
          .getChildren(TorqueSchemaElementName.VIEW));

    for(final SourceElement tableOrViewElement : allTablesAndViews)
    {
      tableOrViewTransformer.transform(
         tableOrViewElement,
         controllerState);
    }

    for(final SourceElement tableOrViewElement : allTablesAndViews)
    {
      for(final SourceElement columnElement : tableOrViewElement.getChildren(
         TorqueSchemaElementName.COLUMN))
      {
        foreignKeyColumnTransformer.transform(
           columnElement,
           controllerState);
      }

      for(final SourceElement foreignKeyElement
         : tableOrViewElement.getChildren(
            TorqueSchemaElementName.FOREIGN_KEY))
      {
        foreignKeyTransformer.transform(
           foreignKeyElement,
           controllerState);
      }

      for(final SourceElement uniqueElement
         : tableOrViewElement.getChildren(
            TorqueSchemaElementName.UNIQUE))
      {
        uniqueTransformer.transform(
           uniqueElement,
           controllerState);
      }
    }

    for(final SourceElement tableOrViewElement : allTablesAndViews)
    {
      for(final SourceElement foreignKeyElement
         : tableOrViewElement.getChildren(
            TorqueSchemaElementName.FOREIGN_KEY))
      {
        foreignKeyTransformer.transformSecondPass(
           foreignKeyElement,
           controllerState);
      }
    }

    for(final SourceElement tableOrViewElement : allTablesAndViews)
    {
      joinGetterTransformer.transform(
         tableOrViewElement,
         controllerState);
    }

    //dumpDebug(root);
    return root;
  }

  /**
   * Sets the rootDatabaseName attribute of the database element
   * to the database's name.
   *
   * @param databaseElement the database element, not null.
   */
  public static void setRootDatabaseNameAttribute(final SourceElement databaseElement)
  {
    final String databaseName = (String) databaseElement.getAttribute(
       TorqueSchemaAttributeName.NAME);
    databaseElement.setAttribute(
       DatabaseAttributeName.ROOT_DATABASE_NAME,
       databaseName);
  }

  /**
   * Sets the rootDatabaseName attribute of the database element
   * to the database's name.
   *
   * @param database the database element, not null.
   */
  public static void setRootDatabaseName(final Database database)
  {
    database.rootDatabaseName = database.name;
  }

  /**
   * Dump di debug.
   * Dump del contenuto in un file in /tmp.
   * @param root
   */
  public static void dumpDebug(SourceElement root)
  {
//    File out = new File(System.getProperty("java.io.tmpdir"), "source-root.txt");
//    System.out.println("Dump SourceElement root in " + out.getAbsolutePath());
//
//    try(FileWriter os = new FileWriter(out))
//    {
//      os.write(root.prettyDump());
//    }
//    catch(Exception ex)
//    {
//      System.err.println("FATAL ERROR: " + ex.getMessage());
//    }
  }
}
