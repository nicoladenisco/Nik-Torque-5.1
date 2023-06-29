package org.apache.torque.generator.control;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.generator.configuration.UnitConfiguration;
import org.apache.torque.generator.configuration.controller.OutletReference;
import org.apache.torque.generator.configuration.controller.Output;
import org.apache.torque.generator.option.Option;
import org.apache.torque.generator.option.OptionName;
import org.apache.torque.generator.option.Options;
import org.apache.torque.generator.outlet.Outlet;
import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.SourceProvider;
import org.apache.torque.generator.variable.VariableStore;

/**
 * The state of the controller.  Contains all stuff the controller needs to
 * track.
 * 
 * Initialization steps in {@link Controller#run(List)}:
 * <ol> 
 * <li>First {@link #unitConfiguration} {@link UnitConfiguration} is set 
 *      in {@link Controller#processGenerationUnit(ControllerState, UnitConfiguration)}
 * <li>Second {@link #output} {@link Output} and {@link #sourceProvider} {@link SourceProvider} is set and reset 
 *      in private method processOutput of {@link Controller}.
 * <li>Third {@link #sourceFile}, {@link #modelRoot} and {@link #model} is set 
 *      in private method processSourceInOutput of {@link Controller}. 
 * <li>Fourth {@link #outputFile}, {@link #outletNamespace}, {@link #rootOutletReference} is set 
 *      in private method processModel of {@link Controller} .
 * 
 */
public class ControllerState
{
    /**
     * The Source provider which is currently used.
     */
    private SourceProvider sourceProvider;

    /**
     * The output which currently processed .
     */
    private Output output;

    /**
     * The current stack of outlets being executed.
     */
    private final List<Outlet> outlets = new ArrayList<>();

    /**
     * The root object of the source model.
     */
    private Object modelRoot;

    /**
     * The current model object within the source.
     */
    private Object model;

    /**
     * The path from the model root to the current model.
     */
    private String pathToModel = "/";

    /**
     * The generation unit which is currently processed.
     */
    private UnitConfiguration unitConfiguration;

    /**
     * The reference in the controller to the root outlet.
     * May override mergepoints in the outlets.
     */
    private OutletReference rootOutletReference;

    /**
     * The variable store.
     */
    private final VariableStore variableStore = new VariableStore();

    /**
     * The currently generated output file. May be null if
     * no file is currently generated (e.g. if the filename is currently
     * generated).
     */
    private File outputFile;

    /**
     * The name of the currently processed source file. May be null if
     * no source file is used (e.g. if the input is created by other means
     * than reading a file).
     */
    private File sourceFile;

    /**
     * The name space in which the current outlet operates.
     */
    private Namespace outletNamespace;

    /**
     * The checksums of the source files for the last generation run,
     * not null.
     */
    private final Checksums lastGeneratedSourceChecksums
        = new Checksums();

    /**
     * The checksums of the source files for this generation run,
     * not null.
     */
    private final Checksums thisGenerationSourceChecksums
        = new Checksums();

    /**
     * Returns the source provider which is currently in use.
     *
     * @return the current source provider.
     */
    public SourceProvider getSourceProvider()
    {
        return sourceProvider;
    }

    /**
     * Sets the source provider which is currently in use.
     *
     * @param sourceProvider the current source provider.
     */
    public void setSourceProvider(final SourceProvider sourceProvider)
    {
        this.sourceProvider = sourceProvider;
    }

    /**
     * Returns the output declaration which is currently processed.
     *
     * @return the output declaration which is currently processed, may be null
     *         only if no output is processed at the moment.
     */
    public Output getOutput()
    {
        return output;
    }

    /**
     * Sets the output declaration which is currently processed.
     *
     * @param output the output which is currently processed.
     */
    void setOutput(final Output output)
    {
        this.output = output;
    }

    /**
     * Returns the topmost outlet in the stack of outlets.
     *
     * @return the topmost outlet in the stack of outlets, or null
     *         if the stack is empty.
     */
    public Outlet getOutlet()
    {
        if (outlets.isEmpty())
        {
            return null;
        }
        return outlets.get(outlets.size() - 1);
    }

    /**
     * Pushes a outlet onto the stack of outlets.
     *
     * @param outlet the outlet to be added to the stack of outlets,
     *        not null.
     */
    public void pushOutlet(final Outlet outlet)
    {
        if (outlet == null)
        {
            throw new NullPointerException("outlet must not be null");
        }
        this.outlets.add(outlet);
    }

    /**
     * Pops the topmost outlets from the stack of outlets.
     *
     * @return the removed outlet, not null.
     *
     * @throws IndexOutOfBoundsException if the stack is empty.
     */
    public Outlet popOutlet()
    {
        return outlets.remove(outlets.size() - 1);
    }

    /**
     * Returns the current model object within the source.
     * Does not return null during generation.
     *
     * @return the current model object.
     */
    public Object getModel()
    {
        return model;
    }

    /**
     * Sets the current source model object.
     *
     * @param model the new current source model object, or null
     *        to remove the current source model object.
     * @param newPathToModel the path from root
     *        to the new model, or null to leave the path unchanged.
     */
    public void setModel(final Object model, String newPathToModel)
    {
        this.model = model;
        if (newPathToModel != null)
        {
            // In source element, the root node precedes the path.
            // For a correct relative path, the root node
            // of the relative path must be removed.
            if (model instanceof SourceElement && newPathToModel.startsWith("/"))
            {
                int slashIndex = newPathToModel.indexOf('/', 1);
                if (slashIndex != -1)
                {
                    newPathToModel = newPathToModel.substring(slashIndex + 1);
                }
                else
                {
                    newPathToModel = "/";
                }
            }

            if (StringUtils.isEmpty(newPathToModel))
            {
                this.pathToModel = "/";
            }
            else
            {
                this.pathToModel = newPathToModel;
            }
        }
    }

    /**
     * Returns the path from the model root to the current model.
     *
     * @return the path from the model root to the current model, not null.
     */
    public String getPathToModel()
    {
        return pathToModel;
    }

    /**
     * Sets the path from the model root to the current model.
     *
     * @param pathToModel the path from the model root to the current model,
     *        not null.
     */
    public void setPathToModel(final String pathToModel)
    {
        if (pathToModel == null)
        {
            throw new NullPointerException("pathToModel must not be null");
        }
        this.pathToModel = pathToModel;
    }

    /**
     * Returns the root object of the current source.
     *
     * @return The the root object of the current source;
     *         may be null only if no source is currently processed.
     */
    public Object getModelRoot()
    {
        return modelRoot;
    }

    /**
     * Sets the root object of the current source.
     *
     * @param modelRoot the the root object of the current source,
     *        or null to remove the current root object.
     */
    public void setModelRoot(final Object modelRoot)
    {
        this.modelRoot = modelRoot;
    }

    /**
     * Returns the reference to the current outlet.
     *
     * @return the reference to the current outlet, or null if no
     *         outlet is currently active.
     */
    public OutletReference getRootOutletReference()
    {
        return rootOutletReference;
    }

    /**
     * Sets the reference to the root outlet.
     *
     * @param rootOutletReference the reference to the root outlet
     *        (i.e. the outlet which produces the whole content),
     *        or null to remove the reference.
     */
    void setRootOutletReference(final OutletReference rootOutletReference)
    {
        this.rootOutletReference = rootOutletReference;
    }


    /**
     * Sets the name space of the outlet which is currently active.
     *
     * @param namespace the namespace of the outlet which is currently
     *        active, or null to remove the name space.
     */
    void setOutletNamespace(final Namespace namespace)
    {
        outletNamespace = namespace;
    }

    /**
     * Returns the namespace of the outlet which is currently active.
     *
     * @return the name space of the active outlet. May be null only
     *         if no generation is in progress.
     */
    public Namespace getOutletNamespace()
    {
        return outletNamespace;
    }

    /**
     * Calculates the value of an option in the current outlet's context.
     * The default namespace which is used when no namespace is given in
     * <code>name</code> is the namespace of the currently used outlet.
     *
     * @param name the name of the option, can contain a namespace.
     *
     * @return The value of the option, or null if no option with that name
     *         is visible from the given namespace.
     */
    public Object getOption(final String name)
    {
        Options options = unitConfiguration.getOptions();
        QualifiedName qualifiedName = getQualifiedName(name);
        Option option = options.getInHierarchy(qualifiedName);
        Object result = null;
        if (option != null)
        {
            result = option.getValue();
        }
        return result;
    }

    /**
     * Calculates the value of an option in the current outlet's context.
     * The default namespace which is used when no namespace is given in
     * <code>name</code> is the namespace of the currently used outlet.
     *
     * @param optionName the object containing the name of the option,
     *        which can contain a namespace, not null.
     *
     * @return The value of the option, or null if no option with that name
     *         is visible from the given namespace.
     *
     * @throws NullPointerException if optionName is null.
     */
    public Object getOption(final OptionName optionName)
    {
        return getOption(optionName.getName());
    }

    /**
     * Convenience method to return the value of an option as boolean.
     * The option is evaluated in the current outlet's context, see
     * getOption(String). 
     * 
     * <p>
     * Uses Boolean.paseBoolean() for String -&gt; Boolean conversion.
     * 
     * @param name the name of the option, can contain a namespace.
     *
     * @return The value of the option as boolean, or false if no option
     *         with that name is visible from the given namespace,
     */
    public boolean getBooleanOption(final String name)
    {
        Object option = getOption(name);
        if (option == null)
        {
            return false;
        }
        return Boolean.parseBoolean(option.toString().trim());
    }

    /**
     * Convenience method to return the value of an option as boolean.
     * The option is evaluated in the current outlet's context, see
     * getOption(String).
     * 
     * <p>
     * Uses Boolean.paseBoolean() for String -&gt; Boolean conversion.
     *
     * @param optionName the object containing the name of the option,
     *        which can contain a namespace.
     *
     * @return The value of the option as boolean, or false if no option
     *         with that name is visible from the given namespace.
     *
     * @throws NullPointerException if optionName is null.
     */
    public boolean getBooleanOption(final OptionName optionName)
    {
        return getBooleanOption(optionName.getName());
    }

    /**
     * Convenience method to return the value of an option as String.
     * The option is evaluated in the current outlet's context, see
     * getOption(String).
     *
     * @param name the name of the option, can contain a namespace.
     *
     * @return The value of the option as boolean, or false if no option
     *         with that name is visible from the given namespace,
     */
    public String getStringOption(final String name)
    {
        Object option = getOption(name);
        if (option == null)
        {
            return null;
        }
        return option.toString();
    }

    /**
     * Convenience method to return the value of an option as String.
     * The option is evaluated in the current outlet's context, see
     * getOption(String).
     *
     * @param optionName the object containing the name of the option,
     *        which can contain a namespace.
     *
     * @return The value of the option as String, or null if no option
     *         with that name is visible from the given namespace,
     *
     * @throws NullPointerException if optionName is null.
     */
    public String getStringOption(final OptionName optionName)
    {
        return getStringOption(optionName.getName());
    }

    /**
     * Returns all options which are visible from the current outlet's
     * namespace.
     *
     * @return all visible options, not null.
     */
    public Options getVisibleOptions()
    {
        return unitConfiguration.getOptions().getInHierarchy(
                outletNamespace);
    }

    /**
     * Returns the VariableStore where generation variables can be set.
     *
     * @return the variableStore, never null.
     */
    public VariableStore getVariableStore()
    {
        return variableStore;
    }

    /**
     * Converts a name to a QualifiedName, using the outlet namespace as
     * default namespace is none is given.
     *
     * @param name the name to convert to a qualifiedName, not null.
     * @return the corresponding qualifiedName.
     *
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is no valid qualifiedName.
     */
    public QualifiedName getQualifiedName(final String name)
    {
        QualifiedName qualifiedName = new QualifiedName(
                name,
                outletNamespace);
        return qualifiedName;
    }

    /**
     * Returns the currently generated file.
     *
     * @return the current output file. May only be null if no
     *         output file is currently generated (e.g. if the file name
     *         is currently generated).
     */
    public File getOutputFile()
    {
        return outputFile;
    }

    /**
     * Sets the currently generated file.
     *
     * @param outputFilePath the currently generated file, or null to remove
     *        the current output file.
     */
    void setOutputFile(final File outputFilePath)
    {
        this.outputFile = outputFilePath;
    }


    /**
     * Returns the currently used source file.
     *
     * @return the current source file. May be null if no
     *         source file is currently used (e.g. if the source is created
     *         by other means than reading a file).
     */
    public File getSourceFile()
    {
        return sourceFile;
    }

    /**
     * Sets the currently used source file.
     *
     * @param sourceFile the current source file, or null to remove the
     *        source file.
     */
    public void setSourceFile(final File sourceFile)
    {
        this.sourceFile = sourceFile;
    }


    /**
     * Returns the configuration of the currently processed generation unit.
     *
     * @return the configuration of the currently processed generation unit.
     */
    public UnitConfiguration getUnitConfiguration()
    {
        return unitConfiguration;
    }

    /**
     * Sets the configuration of the currently processed generation unit.
     *
     * @param unitConfiguration the configuration of the currently processed
     *        generation unit.
     */
    public void setUnitConfiguration(final UnitConfiguration unitConfiguration)
    {
        this.unitConfiguration = unitConfiguration;
    }

    /**
     * Returns the checksums of the source files for the last generation run.
     *
     * @return the checksums, not null.
     */
    public Checksums getLastGeneratedSourceChecksums()
    {
        return lastGeneratedSourceChecksums;
    }

    /**
     * Returns the checksums of the source files for this generation run.
     *
     * @return the checksums, not null.
     */
    public Checksums getThisGenerationSourceChecksums()
    {
        return thisGenerationSourceChecksums;
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("sourceProvider=").append(sourceProvider)
        .append("output=").append(output)
        .append("outputFilePath=").append(outputFile)
        .append("\noutletNamespace=").append(outletNamespace)
        .append("\noutlets=").append(outlets)
        .append("\nmodelRoot=").append(modelRoot)
        .append("\nmodel").append(model)
        .append("\nrootOutletReference=")
        .append(rootOutletReference)
        .append("\nunitConfiguration=").append(unitConfiguration)
        .append("\nvariableStore=").append(variableStore);
        return result.toString();
    }
}
