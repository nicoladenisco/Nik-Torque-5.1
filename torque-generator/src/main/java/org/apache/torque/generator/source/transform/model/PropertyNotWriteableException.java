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

import org.apache.torque.generator.source.transform.SourceTransformerException;

/**
 * Indicates that an attempt was made to write a property which is not
 * writeable.
 *
 * @version $Id: $
 */
public class PropertyNotWriteableException extends SourceTransformerException
{
    /** Serial Version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param target the object on which the property was unsuccessfully
     *        written.
     * @param name the name of the property which was unsuccessfully written.
     */
    public PropertyNotWriteableException(Object target, String name)
    {
        this(target, name, null);
    }

    /**
     * Constructor.
     *
     * @param target the object on which the property was unsuccessfully
     *        written.
     * @param name the name of the property which was unsuccessfully written.
     * @param detail a detail cause which is appended to the error message.
     */
    public PropertyNotWriteableException(
            Object target,
            String name,
            String detail)
    {
        super("The property "
                + name
                + " of class "
                + target.getClass().getName()
                + " is not writeable"
                + (detail == null ? "" : " : " + detail));
    }
}
