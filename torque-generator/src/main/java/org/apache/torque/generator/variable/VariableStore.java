package org.apache.torque.generator.variable;

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

import java.util.LinkedList;

import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;
import org.apache.torque.generator.qname.QualifiedNameMap;

/**
 * Stores all variables which are currently accessible.
 */
public class VariableStore
{
    /**
     * contains all variables with the scope
     * <code>Variable.Scope.OUTLET<code>.
     */
    private QualifiedNameMap<Variable> outletScope
        = new QualifiedNameMap<>();

    /**
     * contains all variables with the scope
     * <code>Variable.Scope.CHILDREN<code>.
     * The QualifiedNameMap with the largest index contains the variables added
     * in the current outlet, and with descending index the variables added
     * to the respective calling outlets.
     */
    private LinkedList<QualifiedNameMap<Variable>> childrenScopeList
        = new LinkedList<>();

    /**
     * contains all variables with the scope
     * <code>Variable.Scope.FILE<code>.
     */
    private QualifiedNameMap<Variable> fileScope
        = new QualifiedNameMap<>();

    /**
     * contains all variables with the scope
     * <code>Variable.Scope.GLOBAL<code>.
     */
    private QualifiedNameMap<Variable> globalScope
        = new QualifiedNameMap<>();

    /**
     * Sets a variable.
     *
     * @param variable the variable to set.
     *
     * @throws NullPointerException if variable is null.
     */
    public void set(Variable variable)
    {
        if (Variable.Scope.OUTLET.equals(variable.getScope()))
        {
            outletScope.put(variable.getName(), variable);
        }
        else if (Variable.Scope.CHILDREN.equals(variable.getScope()))
        {
            QualifiedNameMap<Variable> qualifiedNameMap
            = childrenScopeList.getLast();
            qualifiedNameMap.put(variable.getName(), variable);
        }
        else if (Variable.Scope.FILE.equals(variable.getScope()))
        {
            fileScope.put(variable.getName(), variable);
        }
        else if (Variable.Scope.GLOBAL.equals(variable.getScope()))
        {
            globalScope.put(variable.getName(), variable);
        }
    }

    /**
     * removes a variable from the store.
     *
     * @param variable the variable to remove, not null.
     *
     * @throws NullPointerException if variable is null.
     */
    public void remove(Variable variable)
    {
        if (Variable.Scope.OUTLET.equals(variable.getScope()))
        {
            outletScope.remove(variable.getName());
        }
        else if (Variable.Scope.CHILDREN.equals(variable.getScope()))
        {
            QualifiedNameMap<Variable> qualifiedNameMap
            = childrenScopeList.getLast();
            qualifiedNameMap.remove(variable.getName());
        }
        else if (Variable.Scope.FILE.equals(variable.getScope()))
        {
            fileScope.remove(variable.getName());
        }
        else if (Variable.Scope.GLOBAL.equals(variable.getScope()))
        {
            globalScope.remove(variable.getName());
        }
    }

    /**
     * Signals this store that the processing of a outlet has started.
     */
    public void startOutlet()
    {
        childrenScopeList.addLast(new QualifiedNameMap<Variable>());
    }

    /**
     * Signals this store that the processing of a outlet has ended.
     * Removes all variables with the scope
     * <code>Variable.Scope.OUTLET</code>,
     * and all variables with the scope <code>Variable.Scope.CHILDREN</code>
     * which were set by the outlet which processing ended.
     */
    public void endOutlet()
    {
        outletScope.clear();
        childrenScopeList.removeLast();
    }

    /**
     * Signals this store that the processing of one file has ended.
     * Removes all variables with the scope <code>FILE</code>.
     */
    public void endFile()
    {
        fileScope.clear();
    }

    /**
     * Signals this store that generation has ended. Removes the variables
     * from all scopes.
     */
    public void endGeneration()
    {
        clear();
    }

    /**
     * Removes all variables from this store.
     */
    public void clear()
    {
        outletScope.clear();
        childrenScopeList.clear();
        fileScope.clear();
        globalScope.clear();
    }

    /**
     * Returns a map with the variables contained in this store.
     * Note that if one variable is hidden by another with a more specific
     * scope, only the variable with the more specific scope is returned.
     * <p>
     * The Map is not backed by this stores, i.e. adding and removing
     * variables to the returned nmap has no effect on this store.
     * However, the variables in the returned map are the variables in this
     * store, i.e. setting the value of the variable will change the variable
     * in this store.
     * <p>
     * Note: The current store content is rebuilt each time this method is
     * called. For this reason, <code>store.getInHierarchy(qualifiedName)</code>
     * is much faster than <code>store.getContent().get(qualifiedName)</code>.
     *
     * @return a map with the variables contained ins this store, never null.
     */
    public QualifiedNameMap<Variable> getContent()
    {
        QualifiedNameMap<Variable> result = new QualifiedNameMap<>();
        result.putAll(globalScope);
        result.putAll(fileScope);

        for (QualifiedNameMap<Variable> childrenScope : childrenScopeList)
        {
            result.putAll(childrenScope);
        }

        result.putAll(outletScope);

        return result;
    }

    /**
     * Returns the most specific variable which is visible from the key's
     * namespace and has the same name as the key's name.
     *
     * if several most specific variables are found in the different scopes,
     * the one with the most specific scope is returned.
     *
     * @param key The key for the variable.
     * @return the most specific matching variable, or null if no variable
     *         matches.
     */
    public Variable getInHierarchy(QualifiedName key)
    {
        Variable inOutletScope = outletScope.getInHierarchy(key);
        Variable inFileScope = fileScope.getInHierarchy(key);
        Variable inGenerationScope = globalScope.getInHierarchy(key);

        // look through children, with precedence to the outlets which
        // started later
        Variable result = null;
        for (QualifiedNameMap<Variable> childrenScope : childrenScopeList)
        {
            Variable inChildrenScope = childrenScope.getInHierarchy(key);
            result = getMoreSpecific(inChildrenScope, result);
        }
        result = getMoreSpecific(inOutletScope, result);
        result = getMoreSpecific(result, inFileScope);
        result = getMoreSpecific(result, inGenerationScope);
        return result;
    }

    /**
     * Returns the more specific variable out of two variables (the variable
     * which hides the other variable).
     *
     * If one of the two variables is null, the other is returned.
     * If both variables is null, null is returned.
     * If both variables are in the same namespace, variable1 is returned.
     *
     * It is assumed that variable1 is in an ancestor namespace of
     * variable2 or that variable 2 is in an ancestor namespace of
     * variable1 or both variables are in the same namespace.
     *
     * @param variable1 the first variable to compare.
     * @param variable2 the second variable to compare.
     * @return the more specific variable, or null if both variable1 and
     *         variable2 are null.
     *
     */
    private Variable getMoreSpecific(Variable variable1, Variable variable2)
    {
        if (variable1 == null)
        {
            return variable2;
        }
        if (variable2 == null)
        {
            return variable1;
        }
        Namespace variable1Namespace
        = variable1.getName().getNamespace();
        Namespace variable2Namespace
        = variable2.getName().getNamespace();
        if (variable2Namespace.isVisibleFrom(variable1Namespace))
        {
            // variable1 is more specific or in the same naemspace
            // as variable2, so return variable1
            return variable1;
        }
        else
        {
            // variable2 is more specific than variable1
            return variable2;
        }
    }
}
