package org.apache.torque.generator.outlet;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.ConfigurationException;
import org.apache.torque.generator.configuration.mergepoint.MergepointMapping;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.control.action.MergepointAction;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.variable.Variable;
import org.apache.torque.generator.variable.VariableStore;

/**
 * Implementation of the features in the <code>Outlet</code> interface
 * which do not depend on type of Outlet.
 */
public abstract class OutletImpl implements Outlet
{
  /**
   * The log.
   */
  private static Log log = LogFactory.getLog(OutletImpl.class);

  /**
   * The merge point mappings configured for this outlet.
   */
  private final Map<String, MergepointMapping> mergepointMappings
     = new HashMap<>();

  /**
   * The name of the outlet. Is immutable.
   */
  private final QualifiedName name;

  /**
   * The name of the input elements to process. If null, the input
   * elements are not checked. If not null, the input element must be
   * a SourceElement.
   */
  private String inputElementName;

  /**
   * The class of the input models to process. If null, the class of the
   * input objects are not checked, except if inputElementName is not null.
   */
  private String inputClass;

  /**
   * Constructs a OutletImpl with the given name.
   *
   * @param name the name of this outlet, not null.
   *
   * @throws NullPointerException if name is null.
   */
  public OutletImpl(QualifiedName name)
  {
    if(name == null)
    {
      throw new NullPointerException("name must not be null");
    }
    this.name = name;
  }

  @Override
  public QualifiedName getName()
  {
    return name;
  }

  @Override
  public String getInputElementName()
  {
    return inputElementName;
  }

  @Override
  public void setInputElementName(String inputElementName)
  {
    this.inputElementName = inputElementName;
  }

  @Override
  public String getInputClass()
  {
    return inputClass;
  }

  @Override
  public void setInputClass(String inputClass)
  {
    this.inputClass = inputClass;
  }

  /**
   * Adds an mergepoint mapping to the outlet. No mergepoint
   * mappings must exist with the given name.
   *
   * @param mergepointMapping the mergepointMapping to add, not null.
   *
   * @throws NullPointerException if mergepointMapping is null.
   * @throws ConfigurationException if an mergepointMapping
   * for the given name already exists.
   */
  @Override
  public void addMergepointMapping(MergepointMapping mergepointMapping)
     throws ConfigurationException
  {
    final String namePoint = mergepointMapping.getName();
    MergepointMapping oldMapping = mergepointMappings.get(namePoint);
    if(oldMapping != null)
    {
      throw new ConfigurationException(
         "Attempted to add another mergepoint mapping for the name "
         + namePoint
         + " : New mapping mapped to Actions "
         + mergepointMapping.getActions()
         + ", old mapping mapped to Actions "
         + oldMapping.getActions());
    }
    log.debug("adding mergepointMapping: " + mergepointMapping);
    mergepointMappings.put(namePoint, mergepointMapping);
  }

  /**
   * Sets an mergepoint mapping in the outlet. If a mergepoint
   * mapping with the given name already exists, it is replaced.
   *
   * @param mergepointMapping the mergepointMapping to add, not null.
   *
   * @return the replaced mergepoint mapping, not null.
   *
   * @throws NullPointerException if mergepointMapping is null.
   */
  @Override
  public MergepointMapping setMergepointMapping(
     MergepointMapping mergepointMapping)
  {
    return mergepointMappings.put(
       mergepointMapping.getName(),
       mergepointMapping);
  }

  /**
   * Returns the mergepoint mapping for the given mergepoint name.
   * @return the mergepoint mapping for the given name, or null if no
   * mergepoint mapping exists for this name.
   */
  @Override
  public MergepointMapping getMergepointMapping(String name)
  {
    return mergepointMappings.get(name);
  }

  @Override
  public Map<String, MergepointMapping> getMergepointMappings()
  {
    return Collections.unmodifiableMap(mergepointMappings);
  }

  @Override
  public void beforeExecute(ControllerState controllerState)
     throws GeneratorException
  {
    if(inputElementName != null || inputClass != null)
    {
      Object model = controllerState.getModel();
      if(inputClass != null
         && !inputClass.equals(model.getClass().getName()))
      {
        throw new GeneratorException("The input model of outlet "
           + getName()
           + " must be of class " + inputClass
           + " (because of the value of the attribute inputClass"
           + " on the outlet in the generator config) "
           + "but is of class " + model.getClass().getName());
      }
      if(inputElementName != null
         && !SourceElement.class.isAssignableFrom(model.getClass()))
      {
        throw new GeneratorException("The input model of outlet "
           + getName()
           + " must be "
           + SourceElement.class.getName()
           + " or a subclass thereof "
           + "(because the attribute elementName is set "
           + "on the outlet in the generator config) "
           + " but is of class "
           + model.getClass().getName());
      }
      if(inputElementName != null
         && !inputElementName.equals(((SourceElement) model).getName()))
      {
        throw new GeneratorException("Input element name, "
           + ((SourceElement) model).getName()
           + ", is not the expected name, "
           + getInputElementName()
           + ", for outlet "
           + getName());
      }
    }

    controllerState.pushOutlet(this);
    controllerState.getVariableStore().startOutlet();
    if(log.isDebugEnabled())
    {
      log.debug("Executing outlet "
         + getName()
         + " on element "
         + controllerState.getModel());
    }
  }

  @Override
  public void afterExecute(ControllerState controllerState)
  {
    controllerState.getVariableStore().endOutlet();
    controllerState.popOutlet();
  }

  @Override
  public abstract OutletResult execute(ControllerState controllerState)
     throws GeneratorException;

  @Override
  public String toString()
  {
    StringBuffer result = new StringBuffer();
    result.append("(name=").append(name).append(",");
    result.append("inputElementName=").append(inputElementName);
    result.append(")");
    return result.toString();
  }

  /**
   * Sets a variable. The key can be given with or without namespace;
   * in the latter case, the variable is set in the namespace of this
   * outlet. The scope of the variable is this outlet and its children.
   *
   * @param key the name of the variable, not null
   * @param value the value of the variable, may be null.
   * @param controllerState the context of the controller, not null.
   *
   * @throws NullPointerException if key, scope or controllerState is null.
   * @throws IllegalArgumentException if the key is no valid QualifiedName.
   */
  public void setVariable(
     String key,
     Object value,
     ControllerState controllerState)
  {
    setVariable(key, value, Variable.Scope.CHILDREN, controllerState);
  }

  /**
   * Sets a variable. The key can be given with or without namespace;
   * in the latter case, the variable is set in the namespace of this
   * outlet.
   *
   * @param key the name of the variable, not null.
   * @param value the value of the variable, may be null.
   * @param scope the scope of the variable, not null.
   * @param controllerState the context of the controller, not null.
   *
   * @throws NullPointerException if key or scope is null.
   * @throws IllegalArgumentException if the key is no valid QualifiedName.
   */
  public void setVariable(
     String key,
     Object value,
     Variable.Scope scope,
     ControllerState controllerState)
  {
    QualifiedName qualifiedName = controllerState.getQualifiedName(key);
    Variable variable = new Variable(qualifiedName, value, scope);
    VariableStore variableStore = controllerState.getVariableStore();
    variableStore.set(variable);
  }

  /**
   * Returns the variable with the given key. The key can either be a name
   * prefixed with a namespace, or a name without namespace, in which case
   * the namespace of the outlet is used.
   *
   * In the case that the variable is not set in this namespace, the parent
   * namespaces are searched recursively. If the variable is not set in any
   * of the parent namespaces, null is returned.
   *
   * @param key the key for the variable to retrieve.
   * @param controllerState the context of the controller, not null.
   *
   * @return the variable for the given key, or null if the variable is not
   * set or explicitly set to null.
   */
  public Object getVariable(String key, ControllerState controllerState)
  {
    QualifiedName qualifiedName
       = controllerState.getQualifiedName(key);
    VariableStore variableStore = controllerState.getVariableStore();
    Variable variable = variableStore.getInHierarchy(qualifiedName);

    Object value = null;
    if(variable != null)
    {
      value = variable.getValue();
    }

    return value;
  }

  /**
   * Processes the mergepoint with the given name.
   *
   * @param mergepointName the name of the mergepoint.
   * @param controllerState the context of the controller, not null.
   *
   * @return the output generated by the mergepoint.
   *
   * @throws GeneratorException if the mergepoint could not be processed
   * completely.
   */
  public String mergepoint(String mergepointName, ControllerState controllerState)
     throws GeneratorException
  {
    if(log.isDebugEnabled())
    {
      log.debug("mergepoint() : Start for mergepoint " + mergepointName);
    }

    MergepointMapping mergepointMapping = getMergepointMapping(mergepointName);
    if(mergepointMapping == null)
    {
      if(log.isInfoEnabled())
      {
        log.info("mergepoint() : End. Mapping "
           + mergepointName
           + " not found in outlet "
           + getName()
           + ", returning the empty String");
      }
      return "";
    }

    List<MergepointAction> actions = mergepointMapping.getActions();
    if(actions.isEmpty())
    {
      log.debug("No actions specified for action mapping with name "
         + mergepointName
         + " in outlet "
         + controllerState.getOutlet().getName()
         + " while generating to "
         + controllerState.getOutputFile()
         + " Returning the empty String.");
      return "";
    }

    StringBuffer result = new StringBuffer();
    for(MergepointAction action : actions)
    {
      if(log.isDebugEnabled())
      {
        log.debug("mergepoint() : Executing action " + action);
      }

      // some engines (e.g. velocity) does not chain exceptions,
      // so catch and log exceptions here
      try
      {
        OutletResult actionResult = action.execute(controllerState);
        if(!actionResult.isStringResult())
        {
          throw new GeneratorException(
             "mergepoint actions "
             + "must return a String result! Mergepoint name: "
             + mergepointName
             + ", outlet name: "
             + controllerState.getOutlet().getName().toString());
        }
        result.append(actionResult.getStringResult());
      }
      catch(GeneratorException e)
      {
        log.error("mergepoint() : Error executing action " + action, e);
        throw e;
      }
      catch(RuntimeException e)
      {
        log.error("mergepoint() : Error executing action " + action, e);
        throw e;
      }

      if(log.isDebugEnabled())
      {
        log.debug("mergepoint() : End for mergepoint "
           + mergepointName);
      }
    }

    return result.toString();
  }
}
