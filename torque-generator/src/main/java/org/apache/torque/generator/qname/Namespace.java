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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * An instance of this class represents a hierarchical namespace. The hierarchy
 * parts are separated by dots.
 *
 * A namespace is in another namespace if it starts with all the components
 * of the the other namespace (it may contain other components afterwards).
 * For example, the namespace &quot;org.apache.torque&quot; is in the namespace
 * &quot;org.apache&quot;.
 *
 * Note that the components and their order need to be equal, it does not
 * suffice if a namespace starts with another namespace. For example,
 * the namespace &quot;org.apache.torque&quot; is not in the namespace
 * &quot;org.ap&quot;.
 *
 * Instances of this class are immutable. To guard against mutable subclasses,
 * this class is final.
 */
public final class Namespace
{
    /**
     * The separator between the hierachical parts of a namespace.
     */
    public static final char SEPARATOR = '.';

    /**
     * The root namespace.
     */
    public static final Namespace ROOT_NAMESPACE
        = new Namespace(StringUtils.EMPTY);

    /**
     * The String representation of the namespace. Is never null.
     */
    private String namespace;

    /**
     * Constructs a namespace from its string representation.
     * @param namespace teh string representation of the namespace.
     *        May not be null, and may not contain colons(:).
     *
     * @throws NullPointerException if namespace is null.
     * @throws IllegalArgumentException if namespace contains colons.
     */
    public Namespace(String namespace)
    {
        if (namespace == null)
        {
            throw new NullPointerException("namespace must not be null");
        }
        this.namespace = namespace;
    }

    /**
     * Copy-Contructor.
     *
     * @param toCopy the namespace to copy, not null.
     * @throws NullPointerException if toCopy is null.
     */
    public Namespace(Namespace toCopy)
    {
        if (toCopy == null)
        {
            throw new NullPointerException("toCopy must not be null");
        }
        this.namespace = toCopy.namespace;
    }

    /**
     * Creates a namespace from a hierarchical List of namespace parts.
     *
     * @param namespaceParts the parts of the namespace.
     */
    public Namespace(List<String> namespaceParts)
    {
        if (namespaceParts == null)
        {
            throw new NullPointerException("namespaceParts must not be null");
        }
        StringBuffer assembledNamespace = new StringBuffer();
        for (Iterator<String> partIt
                = namespaceParts.iterator(); partIt.hasNext();)
        {
            String part = partIt.next();

            assembledNamespace.append(part);
            if (partIt.hasNext())
            {
                assembledNamespace.append(SEPARATOR);
            }
        }
        this.namespace = assembledNamespace.toString();
    }


    /**
     * Returns the parts of the namespace in hierachical order.
     * The most significant part, i.e the leftmost side, is returned first.
     *
     * @return the parts of the namespace, never null. An empty list
     *         is returned for the root namespace.
     */
    public List<String> getParts()
    {
        if (StringUtils.EMPTY.equals(namespace))
        {
            return new ArrayList<>();
        }
        String[] partArray
        = namespace.split("\\" + SEPARATOR);
        return Arrays.asList(partArray);
    }

    /**
     * Returns the parent of the given namespace. If this namespace's
     * parent namespace is the root namespace, or this namespace is the root
     * namespace, the root namespace is returned.
     *
     * @return the parent namespace of the namespace, never null
     */
    public Namespace getParent()
    {
        int separatorPos = namespace.lastIndexOf(SEPARATOR);
        if (separatorPos == -1)
        {
            return ROOT_NAMESPACE;
        }

        String parentNamespace = namespace.substring(0, separatorPos);
        return new Namespace(parentNamespace);
    }

    /**
     * Returns if this namespace is visible to another namespace.
     * This is true if this namespace is a "child" of the other namespace
     * or equal to the other namespace.
     * Note that for being a child, all the parts of the namespace separated
     * by a dot must be equal.
     * For example, &quot;org.apache.torque&quot; is visible to the namespace
     * quot;org.apache&quot; and &quot;org&quot; but not to
     * &quot;org.ap&quot;, as the second parts, &quot;apache&quot; and
     * &quot;ap&quot; are not equal.
     *
     * @param otherNamespace the namespace against this namespace
     *        should be checked, not null.
     * @return true if this namespace is visible to the given namespace,
     *         false otherwise.
     * @throws NullPointerException if otherNamespace is null.
     */
    public boolean isVisibleTo(Namespace otherNamespace)
    {
        if (otherNamespace == null)
        {
            throw new NullPointerException("otherNamespace must not be null");
        }
        // All namespaces are in the root namespace.
        if (ROOT_NAMESPACE.equals(otherNamespace))
        {
            return true;
        }

        // The root namespace is not contained in an non-root namespace.
        if (ROOT_NAMESPACE.equals(this))
        {
            return false;
        }

        if (!this.namespace.startsWith(otherNamespace.namespace))
        {
            return false;
        }

        if (this.namespace.equals(otherNamespace.namespace))
        {
            return true;
        }

        if (this.namespace.charAt(otherNamespace.namespace.length())
                == SEPARATOR)
        {
            return true;
        }

        return false;
    }

    /**
     * Returns if this namespace is visible from another namespace.
     * This is true if the other namespace is a "child" of this namespace.
     * Note that for being a child, all the parts of the namespace separated
     * by a dot must be equal.
     * For example, &quot;org.apache.torque&quot; is visible from the namespace
     * &quot;org.apache.torque.generator&quot;, but not from
     * &quot;org.apache&quot;
     *
     * @param otherNamespace the namespace against this namespace
     *        should be checked, not null.
     * @return true if this namespace is visible from the other namespace,
     *         false otherwise.
     * @throws NullPointerException if otherNamespace is null.
     */
    public boolean isVisibleFrom(Namespace otherNamespace)
    {
        if (otherNamespace == null)
        {
            throw new NullPointerException("otherNamespace is null "
                    + "(this namespace is " + this + ")");
        }
        return otherNamespace.isVisibleTo(this);
    }

    /**
     * Returns whether this namespace is the root namespace.
     *
     * @return true if this namespace is the root namespace, false otherwise.
     */
    public boolean isRoot()
    {
        return ROOT_NAMESPACE.namespace.equals(namespace);
    }

    /**
     * Returns if this object is equal to another object.
     * This is true if and only if the other object is a namespace, and their
     * string representations are equal.
     *
     * @param o the object to check equality.
     * @return true if the object is equal to this namespace, false otherwise.
     *
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Namespace))
        {
            return false;
        }

        Namespace otherNamespace = (Namespace) o;
        return otherNamespace.namespace.equals(this.namespace);
    }

    /**
     * Returns a hash code for this namespace. The hash code is consistent
     * with <code>equals()</code>.
     *
     * @return a hach code for this object.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return namespace.hashCode();
    }

    /**
     * Returns a String representation of this namespace.
     *
     * @return a String representation of this namespace.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return namespace;
    }
}
