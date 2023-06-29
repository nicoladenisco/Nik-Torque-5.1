package org.apache.torque.generator.source.transform.model;

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

import java.util.List;

import org.apache.torque.generator.source.transform.SourceTransformerException;

/**
 * Indicates that an attempt was made to access a property which does not exist.
 *
 * @version $Id: $
 */
public class NoSuchPropertyException extends SourceTransformerException
{
    /** Serial Version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param target the object on which the property was unsuccessfully
     *        accessed.
     * @param name the name of the property which was unsuccessfully accessed.
     * @param prefixList list of prefixes
     * @param suffixList list of suffixes
     */
    public NoSuchPropertyException(
            Object target,
            String name,
            List<String> prefixList,
            List<String> suffixList)
    {
        super("Neither public field nor public getter/setter"
                + " exists for property "
                + getSuffixedNames(name, suffixList)
                + " and no public field exists for property "
                + getPrefixedNames(name, prefixList)
                + " of class "
                + target.getClass().getName());
    }

    /**
     * Constructor.
     *
     * @param message The exception message.
     */
    protected NoSuchPropertyException(String message)
    {
        super(message);
    }

    private static String getSuffixedNames(
            String name,
            List<String> suffixList)
    {
        StringBuilder result = new StringBuilder();
        for (String suffix: suffixList)
        {
            if (result.length() != 0)
            {
                result.append(" or ");
            }
            result.append(name).append(suffix);
        }
        return result.toString();
    }

    private static String getPrefixedNames(
            String name,
            List<String> prefixList)
    {
        StringBuilder result = new StringBuilder();
        for (String prefix : prefixList)
        {
            if (result.length() != 0)
            {
                result.append(" or ");
            }
            result.append(prefix).append(name);
        }
        return result.toString();
    }
}
