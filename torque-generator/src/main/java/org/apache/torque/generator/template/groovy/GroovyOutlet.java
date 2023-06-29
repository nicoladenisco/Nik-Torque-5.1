package org.apache.torque.generator.template.groovy;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.ConfigurationProvider;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.option.Option;
import org.apache.torque.generator.option.Options;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.qname.QualifiedNameMap;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.template.TemplateOutletImpl;
import org.apache.torque.generator.variable.Variable;
import org.apache.torque.generator.variable.VariableStore;


/**
 * A Outlet which uses groovy for generation.
 */
public abstract class GroovyOutlet extends TemplateOutletImpl
{
    /**
     * The name under which the Torque generator interface will be put
     * into the binding.
     */
    public static final String TORQUE_GEN_BINDING_NAME = "torqueGen";

    /**
     * The key under which the null attribute of a source element is put
     * into the binding.
     */
    public static final String NULL_KEY_BINDING_NAME = "value";

    /** The log. */
    private static Log log = LogFactory.getLog(GroovyOutlet.class);

    /**
     * Whether the options should be put into the binding.
     */
    private boolean optionsInBinding = true;

    /**
     * Whether the variables should be put into the binding.
     */
    private boolean variablesInBinding = true;

    /**
     * Whether the attributes of the current source element should be put
     * into the binding.
     */
    private boolean sourceAttributesInBinding = true;

    /**
     * Constructs a new GroovyOutlet.
     *
     * @param name the name of this outlet, not null.
     * @param configurationProvider the provider for reading the templates,
     *        not null.
     * @param path the path to the templates, not null.
     * @param encoding the encoding of the file, or null if the system's
     *        default encoding should be used.
     *
     * @throws NullPointerException if name, path or directories are null.
     * @throws ConfigurationException if an error occurs while reading the
     *         template.
     */
    public GroovyOutlet(
            final QualifiedName name,
            final ConfigurationProvider configurationProvider,
            final String path,
            final String encoding)
                    throws ConfigurationException
    {
        super(name,
                configurationProvider,
                path,
                encoding,
                null);
    }

    /**
     * Executes the generation process; the result is returned.
     *
     * @param controllerState the current controller state.
     *
     * @return the result of the generation, not null.
     *
     * @see org.apache.torque.generator.outlet.Outlet#execute(ControllerState)
     */
    @Override
    public OutletResult execute(final ControllerState controllerState)
            throws GeneratorException

    {
        if (log.isDebugEnabled())
        {
            log.debug("Start executing GroovyOutlet " + getName());
        }

        try
        {
            final Map<String, Object> binding = createBinding(controllerState);

            final String result = executeGroovy(binding, controllerState);
            return new OutletResult(result);
        }
        catch (final Exception e)
        {
            throw new GeneratorException(
                    "Error executing template " + getName(),
                    e);
        }
        finally
        {
            if (log.isDebugEnabled())
            {
                log.debug("End executing GroovyOutlet " + getName());
            }
        }
    }

    /**
     * Executes the Groovy script or template and retuns the reult.
     * @param binding the binding, not null.
     * @param controllerState the controller state, not null.
     *
     * @return the generation result.
     *
     * @throws GeneratorException if generation fails.
     */
    protected abstract String executeGroovy(
            Map<String, Object> binding,
            ControllerState controllerState)
                    throws GeneratorException;

    public Map<String, Object> createBinding(final ControllerState controllerState)
    {
        final Map<String, Object> binding = new HashMap<>();
        binding.put(
                TORQUE_GEN_BINDING_NAME,
                new TorqueGenGroovy(this, controllerState));
        if (optionsInBinding)
        {
            // Only consider options visible from the current namespace.
            final Options visibleOptions
            = controllerState.getVisibleOptions();
            for (final Option option : visibleOptions.values())
            {
                final QualifiedName qualifiedName = option.getQualifiedName();
                binding.put(qualifiedName.getName(), option.getValue());
            }
            log.debug("Put options in context " + visibleOptions.keySet());
        }
        else
        {
            log.debug("options in binding are disabled");
        }

        final Object model = controllerState.getModel();
        if (sourceAttributesInBinding && model instanceof SourceElement)
        {
            final SourceElement sourceElement = (SourceElement) model;
            final Set<String> attributes = sourceElement.getAttributeNames();
            for (String key : attributes)
            {
                final Object value = sourceElement.getAttribute(key);
                if (key == null)
                {
                    // The null key cannot be accessed in the binding.
                    // So if the attribute NULL_KEY_CONTEXT_NAME does not
                    // exist, use this as attribute name.
                    if (sourceElement.getAttributeNames().contains(
                            NULL_KEY_BINDING_NAME))
                    {
                        continue;
                    }
                    key = NULL_KEY_BINDING_NAME;
                }
                binding.put(key, value);
            }
            log.debug("Put attributes in binding " + attributes);
        }
        else
        {
            log.debug("source attributes in binding are disabled");
        }

        if (variablesInBinding)
        {
            // Only consider variables visible from the namespace
            // of this outlet. If a name exists in different
            // namespaces visible from this namespace,
            // only consider the most significant name.
            final Namespace namespace = getName().getNamespace();
            final VariableStore variableStore
            = controllerState.getVariableStore();
            final QualifiedNameMap<Variable> visibleVariables
            = variableStore
            .getContent()
            .getInHierarchy(namespace);
            for (final Variable variable : visibleVariables.values())
            {
                final QualifiedName qualifiedName = variable.getName();
                binding.put(
                        qualifiedName.getName(),
                        variable.getValue());
            }
            log.debug("Put variables in binding "
                    + visibleVariables.keySet());
        }
        else
        {
            log.debug("variables in binding are disabled");
        }
        return binding;
    }

    /**
     * Tells the outlet to put all options which name space is visible to
     * the namespace of this outlet into the binding.
     * Only the variable names are used as keys in the context, the namespaces
     * are stripped.
     *
     * @param optionsInBinding whether to put the options into the context.
     */
    public void setOptionsInBinding(final boolean optionsInBinding)
    {
        this.optionsInBinding = optionsInBinding;
    }

    /**
     * Returns whether all options which namespaces are visible to
     * the name space of this outlet are put into the binding.
     *
     * @return whether the outlet puts the options into the context.
     */
    public boolean isOptionsInBinding()
    {
        return optionsInBinding;
    }

    /**
     * Tells the outlet to put all variables which are visible to this
     * outlet into the binding.
     * Only the variable names are used as keys in the binding, the namespaces
     * are stripped.
     *
     * @param variablesInBinding whether to put the variables into the context.
     */
    public void setVariablesInContext(final boolean variablesInBinding)
    {
        this.variablesInBinding = variablesInBinding;
    }

    /**
     * Returns whether all variables which are visible to this
     * outlet are put into the binding.
     *
     * @return whether the outlet puts the variables into the binding.
     */
    public boolean isVariablesInBinding()
    {
        return variablesInBinding;
    }

    /**
     * Tells the outlet to put the attributes of the current source element
     * into the binding or not.
     *
     * @param sourceAttributesInBinding whether to put the source attributes
     *        into the binding.
     */
    public void setSourceAttributesInBinding(
            final boolean sourceAttributesInBinding)
    {
        this.sourceAttributesInBinding = sourceAttributesInBinding;
    }

    /**
     * Returns whether the attributes of the current source element
     * are put into the binding.
     *
     * @return whether the outlet puts the attributes of the current source
     *         element into the binding.
     */
    public boolean isSourceAttributesInBinding()
    {
        return sourceAttributesInBinding;
    }

}
