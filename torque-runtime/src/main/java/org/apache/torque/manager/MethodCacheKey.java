package org.apache.torque.manager;

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

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The key for the MethodResultCache.
 *
 * @version $Id: MethodCacheKey.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class MethodCacheKey implements Serializable
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = -1831486431185021200L;

    /**
     * The Object on which the method is invoked.
     * If the method is static, a String representing the class name is used.
     */
    private Serializable instanceOrClass;

    /** The method name. */
    private String method;

    /** Optional arguments for the method. */
    private Serializable[] args;

    /** The cache group key. */
    private String groupKey;

    public MethodCacheKey()
    {
        // empty
    }

    /**
     * Construct key
     *
     * @param instanceOrClass the Object on which the method is invoked.  if
     * the method is static, a String representing the class name is used.
     * @param method the method name
     * @param arg optional arguments for the method
     */
    public MethodCacheKey(Serializable instanceOrClass, String method, Serializable ... arg)
    {
        init(instanceOrClass, method, arg);
    }

    /**
     * Initialize the key
     *
     * @param instanceOrClass the Object on which the method is invoked.  if
     * the method is static, a String representing the class name is used.
     * @param method the method name
     * @param arg optional arguments for the method
     */
    public void init(Serializable instanceOrClass, String method, Serializable ... arg)
    {
        this.instanceOrClass = instanceOrClass;
        this.method = method;
        groupKey = instanceOrClass.toString() + method;
        this.args = arg;
    }

    /**
     * Return the group key
     *
     * @return the group key
     */
    public String getGroupKey()
    {
        return groupKey;
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
        if (obj.getClass() != this.getClass())
        {
            return false;
        }

        MethodCacheKey methodCacheKey = (MethodCacheKey) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(methodCacheKey.method, method)
        .append(methodCacheKey.instanceOrClass, instanceOrClass)
        .append(methodCacheKey.args, args);
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(method)
        .append(instanceOrClass)
        .append(args);
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(instanceOrClass)
            .append("::")
            .append(method)
            .append('(')
            .append(Arrays.stream(args)
                    .map(arg -> arg.toString())
                    .collect(Collectors.joining(", ")))
            .append(')');
        return sb.toString();
    }
}
