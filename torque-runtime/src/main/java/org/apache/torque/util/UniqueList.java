package org.apache.torque.util;

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

/**
 * List with unique entries. UniqueList does not allow null nor duplicates.
 *
 * @param <T> the type of objects contained in the List.
 *
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @version $Id: UniqueList.java 1839288 2018-08-27 09:48:33Z tv $
 */
public class UniqueList<T> extends ArrayList<T>
{
    /**
     * Serial version
     */
    private static final long serialVersionUID = 4467847559423445120L;

    /**
     * Constructs an empty UniqueList.
     */
    public UniqueList()
    {
        super();
    }

    /**
     * Copy-constructor. Creates a shallow copy of an UniqueList.
     * @param list the uniqueList to copy
     */
    public UniqueList(UniqueList<T> list)
    {
        this();
        this.addAll(list);
    }

    /**
     * Adds an Object to the list.
     *
     * @param o the Object to add
     * @return true if the Object is added
     */
    @Override
    public boolean add(T o)
    {
        if (o != null && !contains(o))
        {
            return super.add(o);
        }
        return false;
    }
}
