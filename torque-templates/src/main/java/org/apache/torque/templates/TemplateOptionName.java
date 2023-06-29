package org.apache.torque.templates;

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

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.option.OptionName;
import org.apache.torque.generator.source.transform.SourceTransformerException;

/**
 * The option names which are used in the java code of the templates.
 * Other options may exist which are not used in the java code of the
 * templates.
 *
 * $Id: TemplateOptionName.java 1869103 2019-10-29 09:25:21Z gk $
 */
public enum TemplateOptionName implements OptionName
{
    /** The database vendor. See the schema for possible values.*/
    DATABASE("torque.database"),

    /** The base package of the OM classes.*/
    OM_PACKAGE("torque.om.package"),

    /**
     * Whether getters for boolean fields should use "is" instead of "get"
     * as prefix.
     */
    OM_USE_IS_FOR_BOOLEAN_GETTERS("torque.om.useIsForBooleanGetters"),

    /** Whether to retain schema names in java names. */
    OM_RETAIN_SCHEMA_NAMES_IN_JAVA_NAME(
            "torque.om.retainSchemaNamesInJavaName"),

    /** The default type for a column if no type is given. */
    DEFAULT_TYPE("torque.om.column.defaultType"),

    /** Whether bean objects should be generated. */
    OM_GENERATE_BEANS("torque.om.generateBeans"),

    /** Whether manager classes should be used. */
    OM_USE_MANAGERS("torque.om.useManagers"),

    /**
     * Whether the map init classes which initialize all database maps
     * at once should be generated.
     */
    OM_GENERATE_MAP_INIT("torque.om.generateMapInit"),

    /** The default base class for dbObjects. */
    OM_DB_OBJECT_DEFAULT_BASE_CLASS("torque.om.dbObjectDefaultBaseClass"),

    /** The prefix of the name of the mapInit class. */
    OM_DATABASE_MAP_INIT_CLASS_NAME_PREFIX(
            "torque.om.mapInit.databaseMapInitClassNamePrefix"),

    /** The suffix of the name of the mapInit class. */
    OM_DATABASE_MAP_INIT_CLASS_NAME_SUFFIX(
            "torque.om.mapInit.databaseMapInitClassNameSuffix"),

    /** The additional prefix of the name of the baseMapInit class. */
    OM_BASE_DATABASE_MAP_INIT_CLASS_NAME_PREFIX(
            "torque.om.mapInit.baseDatabaseMapInitClassNamePrefix"),

    /** The prefix of the name of the manager class. */
    OM_MANAGER_CLASS_NAME_PREFIX("torque.om.className.managerClassNamePrefix"),

    /** The suffix of the name of the manager class. */
    OM_MANAGER_CLASS_NAME_SUFFIX("torque.om.className.managerClassNameSuffix"),

    /** The name of the option for the prefix of the adder methods. */
    OM_ADDER_PREFIX("torque.om.complexObjectModel.adderPrefix"),

    /** The name of the option for the suffix of the adder methods. */
    OM_ADDER_SUFFIX("torque.om.complexObjectModel.adderSuffix"),

    /** The name of the option for the prefix of the resetter methods. */
    OM_RESETTER_PREFIX("torque.om.complexObjectModel.resetterPrefix"),

    /** The name of the option for the suffix of the resetter methods. */
    OM_RESETTER_SUFFIX("torque.om.complexObjectModel.resetterSuffix"),

    /** The name of the option for the prefix of the initializer methods. */
    OM_INITIALIZER_PREFIX("torque.om.complexObjectModel.initializerPrefix"),

    /** The name of the option for the suffix of the initializer methods. */
    OM_INITIALIZER_SUFFIX("torque.om.complexObjectModel.initializerSuffix"),

    /** The name of the option for the prefix of the initializer methods. */
    OM_IS_INITIALIZED_PREFIX("torque.om.complexObjectModel.isInitializedPrefix"),

    /** The name of the option for the suffix of the initializer methods. */
    OM_IS_INITIALIZED_SUFFIX("torque.om.complexObjectModel.isInitializedSuffix"),

    /** The name of the option for the prefix of the filler methods. */
    OM_FILLER_PREFIX("torque.om.complexObjectModel.fillerPrefix"),

    /** The name of the option for the suffix of the filler methods. */
    OM_FILLER_SUFFIX("torque.om.complexObjectModel.fillerSuffix"),

    /** The name of the option for the prefix of the setAndSave methods. */
    OM_SET_AND_SAVE_PREFIX("torque.om.complexObjectModel.setAndSavePrefix"),

    /** The name of the option for the suffix of the setAndSave methods. */
    OM_SET_AND_SAVE_SUFFIX("torque.om.complexObjectModel.setAndSaveSuffix"),

    /**
     * The name of the option for the part added to the referencing
     * direction if naming conflicts occur.
     */
    OM_FILLER_REFERENCING_DISTICTION(
            "torque.om.complexObjectModel.fillerReferencingDistinction"),

    /**
     * The name of the option containing the prefix for the local field name
     * of a foreign-key reference.
     */
    OM_LOCAL_FIELD_NAME_PREFIX(
            "torque.om.complexObjectModel.localFieldNamePrefix"),

    /**
     * The name of the option containing the suffix for the local field name
     * of a foreign-key reference.
     */
    OM_LOCAL_FIELD_NAME_SUFFIX(
            "torque.om.complexObjectModel.localFieldNameSuffix"),

    /**
     * The name of the option containing the prefix
     * for the getter of the foreign key.
     */
    OM_FOREIGN_KEY_GETTER_PREFIX("torque.om.foreignKeyGetterPrefix"),

    /**
     * The name of the option containing the suffix
     * for the getter of the foreign key.
     */
    OM_FOREIGN_KEY_GETTER_SUFFIX("torque.om.foreignKeyGetterSuffix"),
    /**
     * The name of the option for the part between referenced table name
     * and referencing column name
     * (only used if the same foreign table is referenced several times)
     * as used in the local field name.
     */
    OM_LOCAL_FIELD_NAME_RELATED_BY(
            "torque.om.complexObjectModel.localFieldNameRelatedBy"),

    /**
     * The name of the option containing the prefix for the foreign field name
     * of a foreign-key reference.
     */
    OM_FOREIGN_FIELD_NAME_PREFIX(
            "torque.om.complexObjectModel.foreignFieldNamePrefix"),

    /**
     * The name of the option containing the suffix for the foreign field name
     * of a foreign-key reference.
     */
    OM_FOREIGN_FIELD_NAME_SUFFIX(
            "torque.om.complexObjectModel.foreignFieldNameSuffix"),

    /**
     * The name of the option for the part between referenced table name
     * and referencing column name
     * (only used if the same local table is referenced several times)
     * as used in the foreign field name.
     */
    OM_FOREIGN_FIELD_NAME_RELATED_BY(
            "torque.om.complexObjectModel.foreignFieldNameRelatedBy"),

    /**
     * The name of the option for the java type for the foreign field
     * (can be an interface),
     */
    OM_FOREIGN_FIELD_TYPE("torque.om.complexObjectModel.foreignFieldType"),

    /**
     * The name of the option for the java type for the initial value
     * of the foreign field (must not be an interface).
     */
    OM_FOREIGN_FIELD_INIT_TYPE(
            "torque.om.complexObjectModel.foreignFieldInitType"),

    /**
     * The name of the option which controls whether save methods are
     * generated at all.
     */
    OM_ADD_SAVE_METHODS("torque.om.addSaveMethods"),

    /**
     * The name of the option which controls whether save methods are
     * generated in the db objects (true) or in the peers (false).
     */
    OM_SAVE_METHODS_IN_DB_OBJECTS("torque.om.saveMethodsInDbObjects"),

    /**
     * The name of the option for the name of the exception thrown
     * by the save methods.
     */
    OM_SAVE_EXCEPTION("torque.om.saveException"),

    /**
     * The name of the option for the default value used in optimistic locking
     * if no explicit default value is set.
     */
    OM_OPTIMISTIC_LOCKING_DEFAULT_VALUE("torque.om.optimisticLocking.defaultValue"),

    /**
     * The name of the option for the mode used in optimistic locking.
     * The value must be one of selectForUpdate, simpleSelect.
     */
    OM_OPTIMISTIC_LOCKING_MODE("torque.om.optimisticLocking.mode"),

    /**
     * Whether joinGetter methods are generated in the data object classes
     * which fetch related objects in one database query.
     */
    OM_GENERATE_JOIN_GETTERS("torque.om.complexObjectModel.generateJoinGetters"),

    /**
     * The separator in the joinGetter Methods between the two name parts.
     */
    OM_JOIN_GETTER_SEPARATOR("torque.om.complexObjectModel.joinGetterSeparator"),

    /**
     * The visibility of the joinGetter methods.
     */
    OM_JOIN_GETTER_VISIBILITY("torque.om.complexObjectModel.joinGetterVisibility"),

    /** The prefix for the name of enum types. */
    OM_ENUM_TYPE_PREFIX("torque.om.enumTypePrefix"),

    /** The suffix for the name of enum types. */
    OM_ENUM_TYPE_SUFFIX("torque.om.enumTypeSuffix"),

    /** The prefix for the constraint name for enum types. */
    SQL_ENUM_CONSTRAINT_NAME_PREFIX("torque.sql.enumConstraintNamePrefix"),

    /** The suffix for the constraint name for enum types. */
    SQL_ENUM_CONSTRAINT_NAME_SUFFIX("torque.sql.enumConstraintNameSuffix");

    /**
     * The fully qualified name of the option.
     */
    private String name;

    /**
     * Constructor.
     *
     * @param name the fully qualified name of the option, not null.
     */
    private TemplateOptionName(final String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the option.
     *
     * @return the fully qualified name of the option, not null.
     */
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Checks whether all required options are set.
     *
     * @param controllerState the current controller state, not null.
     * @param requiredOptions the required options to check, not null.
     *
     * @throws SourceTransformerException if a required option is not set.
     */
    public static void checkRequiredOptions(final ControllerState controllerState,
            final TemplateOptionName... requiredOptions)
                    throws SourceTransformerException
    {
        for (TemplateOptionName templateOption : requiredOptions)
        {
            Object optionValue
            = controllerState.getOption(templateOption.getName());
            if (optionValue == null)
            {
                throw new SourceTransformerException(
                        "Option " + templateOption.getName()
                        + " must be set");
            }
        }
    }
}
