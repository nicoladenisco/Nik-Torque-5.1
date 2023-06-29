package org.apache.torque.generator.qname;

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

import org.apache.commons.lang3.StringUtils;


/**
 * This class contains a name which has a namespace. The namespace defines the
 * scope where the name is valid, and is made up of hierarchical bits
 * separated by a dot (<code>SEPARATOR</code>).
 * A qualified name is in the scope of its own namespace and all children
 * namespaces of its own namespace (i.e. the namespaces "deeper down"
 * the hierarchy). For example, the namespace org.apache.torque
 * is in the scope of org.apache but not in the scope of
 * org.apache.torque.generator.
 * As a special case, if the namespace is the empty String,
 * the name is valid in all namespaces.
 *
 * The namespace and the name are also separated by a dot
 * (<code>SEPARATOR</code>).
 *
 * Instances of this class are immutable. To prevent mutable subclasses,
 * this class is final.
 */
public final class QualifiedName
{
    /**
     * The separator between namespace parts and between namespacee
     * and local name in the string representation of a QualifiedName.
     */
    public static final char SEPARATOR = '.';

    /**
     * The local name part of the qualifiedName. Is never null or empty.
     */
    private String name;

    /**
     * The namespace of the qualifiedName. Is never null.
     */
    private Namespace namespace;

    /**
     * Creates a QualifiedName from its string representation, which is
     * namespace:name. The namespace part may be empty (in which case
     * the default namespace is used), but not the name part.
     *
     * @param qualifiedName the string representation of the QualifiedName,
     *        must contain a non-empty name part.
     * @throws NullPointerException if qualifiedName is null
     */
    public QualifiedName(String qualifiedName)
    {
        this(qualifiedName, (Namespace) null);
    }

    /**
     * Creates a QualifiedName from a fully qualified name or the name and the
     * namespace parts of a QualifiedName.
     *
     * @param nameOrQualifiedName either the name part of the QualifiedName,
     *        or a fully qualified name. May not be null and must contain
     *        an non-empty name part.
     * @param defaultNamespace the namespace of the QualifiedName,
     *        may be null if nameOrQualifiedName has a namespace part.
     *
     * @throws IllegalArgumentException if name has an empty name part, or
     *         if no namespace is found (nameOrQualifiedName has no namespace
     *         part and defaultNamespace is null)
     * @throws NullPointerException if name is null.
     */
    public QualifiedName(String nameOrQualifiedName, String defaultNamespace)
    {
        this(nameOrQualifiedName,
                defaultNamespace != null
                ? new Namespace(defaultNamespace)
                        : null);
    }

    /**
     * Creates a QualifiedName from a fully qualified name or the name and the
     * namespace parts of a QualifiedName.
     *
     * @param nameOrQualifiedName either the name part of the QualifiedName,
     *        or a fully qualified name. May not be null and must contain
     *        an non-empty name part.
     * @param defaultNamespace the namespace of the QualifiedName,
     *        may be null if nameOrQualifiedName has a namespace part.
     *
     * @throws IllegalArgumentException if name has an empty name part, or
     *         if no namespace is found (nameOrQualifiedName has no namespace
     *         part and defaultNamespace is null)
     * @throws NullPointerException if name is null.
     */
    public QualifiedName(String nameOrQualifiedName, Namespace defaultNamespace)
    {
        if (nameOrQualifiedName == null)
        {
            throw new NullPointerException(
                    "nameOrQualifiedName must not be null");
        }
        int dotIndex = nameOrQualifiedName.lastIndexOf(SEPARATOR);
        if (dotIndex == -1)
        {
            if (defaultNamespace == null)
            {
                defaultNamespace = Namespace.ROOT_NAMESPACE;
            }
            setName(nameOrQualifiedName);
            this.namespace = defaultNamespace;
        }
        else
        {
            setName(nameOrQualifiedName.substring(dotIndex + 1).trim());
            String namespacePart
            = nameOrQualifiedName.substring(0, dotIndex).trim();
            setNamespace(namespacePart);
        }
    }

    /**
     * Returns the name part of the QualifiedName.
     *
     * @return the name part of the QualifiedName, never null.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name part of this Qualified name.
     * This method is private because this object is immutable.
     *
     * @param name the name, not blank,
     *        must not contain a dot (<code>SEPARATOR</code>).
     */
    private void setName(String name)
    {
        if (name.indexOf(SEPARATOR) != -1)
        {
            throw new IllegalArgumentException(
                    "name \"" + name + "\" must not contain "
                            + SEPARATOR);
        }
        if (StringUtils.isBlank(name))
        {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    /**
     * Sets the namespace part of this Qualified name.
     * This method is private because this object is immutable.
     *
     * @param namespace the name, not null,
     *        must not contain <code>NAMESPACE_NAME_SEPARATOR</code>
     */
    private void setNamespace(String namespace)
    {
        this.namespace = new Namespace(namespace);
    }

    /**
     * Returns the namespace of the QualifiedName.
     *
     * @return the namespace of the QualifiedName, may be null.
     */
    public Namespace getNamespace()
    {
        return namespace;
    }

    /**
     * Returns if this qualified name is visible from another namespace.
     * This is true if the namespace is a "child" of this
     * qualified name's  namespace.
     * Note that for being a child, all the parts of the namespace separated
     * by a dot must be equal.
     * For example, &quot;org.apache.torque:name&quot; is visible from the
     * namespaces &quot;org.apache.torque.generator&quot; and
     * &quot;org.apache.torque&quot;, but not from &quot;org.apache&quot;
     *
     * @param otherNamespace the namespace against this QualifiedName
     *        should be checked, not null.
     *
     * @return true if this QualifiedName is visible from the given namespace,
     *         false otherwise.
     *
     * @throws NullPointerException if otherNamespace is null.
     */
    public boolean isVisibleFrom(Namespace otherNamespace)
    {
        return namespace.isVisibleFrom(otherNamespace);
    }

    /**
     * Returns the parent of the Qualified name's namespace.
     * If this qualified name is in the root namespace, the root namespace
     * is returned.
     *
     * @return the parent namespace of the qualified name's namespace,
     *         never null.
     *
     * @see Namespace#getParent()
     */
    public Namespace getParentNamespace()
    {
        return namespace.getParent();
    }

    /**
     * Returns if the namespace of this qualified name is the root namespace.
     *
     * @return true if the namespace is the root namespace, false otherwise.
     */
    public boolean isInRootNamespace()
    {
        return namespace.isRoot();
    }

    /**
     * Returns the String representation of the QualifiedName,
     * which is namespace.name if namespace is not empty,
     * or name if namespace is empty.
     *
     * @return a String representation of the QualifiedName.
     */
    @Override
    public String toString()
    {
        if (isInRootNamespace())
        {
            return name;
        }
        return namespace.toString() + SEPARATOR + name;
    }

    /**
     * Returns if this object equals another object. This is the case if
     * the other object is also a QualifiedName and its name and namespace
     * are the same as this object's name and namespace.
     *
     * @param o the other object to compare this object to.
     * @return true if this object equals the other object, false otherwise.
     *
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof QualifiedName))
        {
            return false;
        }

        QualifiedName qualifiedName = (QualifiedName) o;

        if (!qualifiedName.name.equals(name))
        {
            return false;
        }

        return qualifiedName.namespace.equals(namespace);
    }

    /**
     * Returns a hashcode for the QualifiedName. The hashcode is consistent
     * with equals.
     *
     * @return a hashcode for the qualified name.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }
}
