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

import org.apache.torque.generator.qname.QualifiedName;

/**
 * A variable which can be used to transport data in and between outlets.
 * A variable has a name (possibly with a name space),
 * a value (which may be null),
 * and a scope (determining in which outlets this variable will be visible).
 * if the scope is <code>CHILDREN</code>, the outlet in which this
 * variable was set is also stored in the variable.
 */
public class Variable
{
    /**
     * The scope of a variable.
     */
    public enum Scope
    {
        /**
         * The scope of the variable is limited to the current outlet.
         */
        OUTLET,
        /**
         * The scope of the variable is the current outlet and
         * recursively all the outlets in the merge points accessed
         * in the generation process.
         */
        CHILDREN,
        /**
         * The variable is visible throughout the generation process of this
         * file.
         */
        FILE,
        /**
         * The variable is visible throughout the whole generation process.
         */
        GLOBAL
    }

    /**
     * The name of the variable.
     */
    private QualifiedName name;

    /**
     * The value fo the variable. May be null.
     */
    private Object value;

    /**
     * The scope of this variable. Is never null.
     */
    private Scope scope;

    /**
     * Constructor.
     *
     * @param name the name of the variable, not null.
     * @param value the value of the variable.
     * @param scope the scope of the variable, not null.
     */
    public Variable(
            QualifiedName name,
            Object value,
            Scope scope)
    {
        if (name == null)
        {
            throw new NullPointerException("name must not be null");
        }
        if (scope == null)
        {
            throw new NullPointerException("scope must not be null");
        }
        this.name = name;
        this.value = value;
        this.scope = scope;
    }

    /**
     * Returns the name of the variable.
     *
     * @return the name of the variable, not null.
     */
    public QualifiedName getName()
    {
        return name;
    }

    /**
     * Returns the value of the variable,
     * @return the value of the variable, may be null.
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Sets the value of the variable.
     *
     * @param value the value to set in the variable.
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * Returns the scope of the variable.
     *
     * @return the scope of the variable, not null.
     */
    public Scope getScope()
    {
        return scope;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }

        final Variable other = (Variable) obj;

        if (name == null)
        {
            if (other.name != null)
            {
                return false;
            }
        }
        else if (!name.equals(other.name))
        {
            return false;
        }

        if (scope == null)
        {
            if (other.scope != null)
            {
                return false;
            }
        }
        else if (!scope.equals(other.scope))
        {
            return false;
        }

        if (value == null)
        {
            if (other.value != null)
            {
                return false;
            }
        }
        else if (!value.equals(other.value))
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("(name=").append(name)
        .append(", value=").append(value)
        .append(", scope=").append(scope)
        .append(")");
        return result.toString();
    }
}
