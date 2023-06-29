package org.apache.torque.generator.template.velocity;

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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.StringUtils;

/**
 * A Outlet which uses a velocity template for generation.
 */
public class VelocityOutlet extends TemplateOutletImpl
{
    /**
     * The name under which the Torque generator interface will be put
     * into the context.
     */
    public static final String TORQUE_GEN_CONTEXT_NAME = "torqueGen";

    /**
     * The name under which the velocity StringUtils will be put
     * into the context.
     */
    public static final String STRING_UTILS_CONTEXT_NAME = "stringUtils";

    /**
     * The key under which the null attribute of a source element is put
     * into the context.
     */
    public static final String NULL_KEY_CONTEXT_NAME = "value";

    /** The log. */
    private static Logger log = LogManager.getLogger(VelocityOutlet.class);

    /**
     * Whether the options should be put into the context.
     */
    private boolean optionsInContext = true;

    /**
     * Whether the variables should be put into the context.
     */
    private boolean variablesInContext = true;

    /**
     * Whether the attributes of the current source element should be put
     * into the context.
     */
    private boolean sourceAttributesInContext = true;

    /**
     * Constructs a new VelocityTemplateOutlet.
     *
     * @param name the name of this outlet, not null.
     * @param configurationProvider the provider for reading the templates,
     *        not null.
     * @param path the path to the templates, not null.
     *        May contain tokens of the form ${....}, these are parsed.
     * @param encoding the encoding of the file, or null if the system's
     *        default encoding should be used.
     *
     * @throws NullPointerException if name, path or directories are null.
     * @throws ConfigurationException if an error occurs while reading the
     *         template.
     */
    public VelocityOutlet(
            QualifiedName name,
            ConfigurationProvider configurationProvider,
            String path,
            String encoding)
                    throws ConfigurationException
    {
        super(name,
                configurationProvider,
                path,
                encoding,
                new VelocityTemplateFilter());
    }

    /**
     * Tells the outlet to put all options which name space is visible to
     * the namespace of this outlet into the context.
     * Only the variable names are used as keys in the context, the namespaces
     * are stripped.
     *
     * @param optionsInContext whether to put the options into the context.
     */
    public void setOptionsInContext(boolean optionsInContext)
    {
        this.optionsInContext = optionsInContext;
    }

    /**
     * Returns whether all options which namespaces are visible to
     * the name space of this outlet are put into the context.
     *
     * @return whether the outlet puts the options into the context.
     */
    public boolean isOptionsInContext()
    {
        return optionsInContext;
    }

    /**
     * Tells the outlet to put all variables which are visible to this
     * outlet into the context.
     * Only the variable names are used as keys in the context, the namespaces
     * are stripped.
     *
     * @param variablesInContext whether to put the variables into the context.
     */
    public void setVariablesInContext(boolean variablesInContext)
    {
        this.variablesInContext = variablesInContext;
    }

    /**
     * Returns whether all variables which are visible to this
     * outlet are put into the context.
     *
     * @return whether the outlet puts the variables into the context.
     */
    public boolean isVariablesInContext()
    {
        return variablesInContext;
    }

    /**
     * Tells the outlet to put the attributes of the current source element
     * into the context or not.
     *
     * @param sourceAttributesInContext whether to put the source attributes
     *        into the context.
     */
    public void setSourceAttributesInContext(
            boolean sourceAttributesInContext)
    {
        this.sourceAttributesInContext = sourceAttributesInContext;
    }

    /**
     * Returns whether the attributes of the current source element
     * are put into the context.
     *
     * @return whether the outlet puts the attributes of the current source
     *         element into the context.
     */
    public boolean isSourceAttributesInContext()
    {
        return sourceAttributesInContext;
    }

    /**
     * Executes the generation process; the result is returned.
     * 
     *
     * @param controllerState the current controller state.
     *
     * @return the result of the generation, not null.
     *
     * @see org.apache.torque.generator.outlet.Outlet#execute(ControllerState)
     */
    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException

    {
        if (log.isDebugEnabled())
        {
            log.debug("Start executing VelocityOutlet " + getName());
        }

        try
        {
            try
            {
                Properties properties = new Properties();
                /*properties.put( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                      Log4JLogChute.class.getName());
                   
                // test
                properties.put(RuntimeConstants.EVALUATE_CONTEXT_CLASS, "org.apache.velocity.VelocityContext");*/
                /*
                 * runtime.conversion.handler = none
                 * space.gobbling = bc
                 * directive.if.emptycheck = false
                 * 
                 * cf. http://velocity.apache.org/engine/2.0/upgrading.html
                 */
                properties.put(
                        "runtime.conversion.handler",
                        "none");
                properties.put(
                        RuntimeConstants.SPACE_GOBBLING,
                        "bc");
                properties.put(
                        RuntimeConstants.CHECK_EMPTY_OBJECTS,
                        "false");
                Velocity.init(properties);
            }
            catch (Exception e)
            {
                throw new GeneratorException(
                        "Could not initialize velocity",
                        e);
            }

            Context context = createVelocityContext(controllerState);

            Writer writer = new StringWriter();
            try
            {
                Velocity.evaluate(context, writer,
                        "VelocityTemplateOutlet:" + getName(),
                        getContent(controllerState));
                writer.flush();
                writer.close();
            }
            catch (Exception e)
            {
                log.error("error during execution of outlet "
                        + getName()
                        + " : " + e.getMessage(),
                        e);
                throw new GeneratorException(
                        "Error during execution of outlet "
                                + getName()
                                + " : "
                                + e.getMessage(),
                                e);
            }

            return new OutletResult(writer.toString());
        }
        finally
        {
            if (log.isDebugEnabled())
            {
                log.debug("End executing VelocityOutlet " + getName());
            }
        }
    }

    /**
     * Creates the velocity context for the outlet.
     *
     * @param controllerState the controller state, not null.
     * @return
     */
    private Context createVelocityContext(
            ControllerState controllerState)
    {
        Context context = new VelocityContext();
        context.put(
                TORQUE_GEN_CONTEXT_NAME,
                new TorqueGenVelocity(this, controllerState));
        context.put(
                STRING_UTILS_CONTEXT_NAME,
                new StringUtils());

        if (optionsInContext)
        {
            // Only consider options visible from the current namespace.
            Options visibleOptions
            = controllerState.getVisibleOptions();
            for (Option option : visibleOptions.values())
            {
                QualifiedName qualifiedName = option.getQualifiedName();
                context.put(qualifiedName.getName(), option.getValue());
            }
            log.debug("Put options in context " + visibleOptions.keySet());
        }
        else
        {
            log.debug("options in context are disabled");
        }

        Object model = controllerState.getModel();
        SourceElement sourceElement = null;
        if (model instanceof SourceElement)
        {
            sourceElement = (SourceElement) model;
        }
        if (sourceAttributesInContext && sourceElement != null)
        {
            Set<String> attributes = sourceElement.getAttributeNames();
            for (String key : attributes)
            {
                Object value = sourceElement.getAttribute(key);
                if (key == null)
                {
                    // The null key cannot be accessed in the context.
                    // So if the attribute NULL_KEY_CONTEXT_NAME does not
                    // exist, use this as attribute name.
                    if (sourceElement.getAttributeNames().contains(
                            NULL_KEY_CONTEXT_NAME))
                    {
                        continue;
                    }
                    key = NULL_KEY_CONTEXT_NAME;
                }
                context.put(key, value);
            }
            log.debug("Put attributes in context " + attributes);
        }
        else
        {
            log.debug("source attributes in context are disabled");
        }

        if (variablesInContext)
        {
            // Only consider variables visible from the namespace
            // of this outlet. If a name exists in different
            // namespaces visible from this namespace,
            // only consider the most significant name.
            Namespace namespace = getName().getNamespace();
            VariableStore variableStore
            = controllerState.getVariableStore();
            QualifiedNameMap<Variable> visibleVariables
            = variableStore
            .getContent()
            .getInHierarchy(namespace);
            for (Variable variable : visibleVariables.values())
            {
                QualifiedName qualifiedName = variable.getName();
                context.put(
                        qualifiedName.getName(),
                        variable.getValue());
            }
            log.debug("Put variables in context "
                    + visibleVariables.keySet());
        }
        else
        {
            log.debug("variables in context are disabled");
        }

        return context;
    }
}
