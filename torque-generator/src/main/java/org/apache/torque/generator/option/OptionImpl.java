package org.apache.torque.generator.option;

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

import org.apache.torque.generator.qname.Namespace;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * The default implementation of the option Interface. This class is immutable.
 */
public class OptionImpl implements Option
{
    /**
     * The qualified name of the option, including the name and the context.
     */
    private QualifiedName qualifiedName;

    /**
     * The value of the option.
     */
    private Object value;

    /**
     * Constructs an optionImpl from a namespace, a name and a value.
     *
     * @param namespace the namespace of the option, may be null.
     * @param name the name of the option, must not be empty.
     * @param value the value of the option, may be null.
     */
    public OptionImpl(String namespace, String name, Object value)
    {
        this.qualifiedName = new QualifiedName(namespace, name);
        this.value = value;
    }

    /**
     * Constructs an OptionImpl from the String representation of
     * a QualifiedName and a value.
     *
     * @param qualifiedName The String representation of the qualified name
     *        of the option, in the form context:name or name.
     *        (in the latter case, context is null).
     * @param value the value of the option, may be null.
     *
     * @throws IllegalArgumentException if qualifiedName is not a legal
     *         QualifiedName.
     */
    public OptionImpl(String qualifiedName, Object value)
    {
        this(new QualifiedName(qualifiedName, Namespace.ROOT_NAMESPACE), value);
    }

    /**
     * Constructs an OptionImpl from a QualifiedaName and a value.
     * @param qualifiedName A qualifiedName containing the context
     *        and the name of the option.
     * @param value the value of the option, may be null.
     */
    public OptionImpl(QualifiedName qualifiedName, Object value)
    {
        if (qualifiedName == null)
        {
            throw new IllegalArgumentException(
                    "qualifiedName must not be zero");
        }
        this.qualifiedName = qualifiedName;
        this.value = value;
    }

    /**
     * Returns the qualified name of the option.
     *
     * @return the qualified name of the option, not null.
     */
    @Override
    public QualifiedName getQualifiedName()
    {
        return qualifiedName;
    }

    /**
     * Retursn the value of the option.
     *
     * @return the value of the option, may be null.
     */
    @Override
    public Object getValue()
    {
        return value;
    }

    /**
     * Creates as String representation of this Object for
     * debugging purposes.
     *
     * @return A String representation, not null.
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder()
                .append("(qualifiedName=")
                .append(qualifiedName)
                .append(", value=")
                .append(value)
                .append(")");
        return result.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((qualifiedName == null) ? 0 : qualifiedName.hashCode());
        result = prime * result
                + ((value == null) ? 0 : value.hashCode());
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

        final OptionImpl other = (OptionImpl) obj;
        if (qualifiedName == null)
        {
            if (other.qualifiedName != null)
            {
                return false;
            }
        }
        else if (!qualifiedName.equals(other.qualifiedName))
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
}
