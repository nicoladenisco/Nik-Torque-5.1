package org.apache.torque.generator.configuration.mergepoint;

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
import java.util.List;

import org.apache.torque.generator.control.action.MergepointAction;

/**
 * A mapping between the name of an mergepoint and and the action which should
 * be performed at this point.
 */
public class MergepointMapping
{
    /**
     * The name of the mergepoint.
     */
    private String name;

    /**
     * The list of actions whichare executed at the mergepoint.
     */
    private List<MergepointAction> actions = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param name the name of the mergepoint, not null.
     *
     * @throws IllegalArgumentException if name is null.
     */
    public MergepointMapping(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("name must not be null");
        }
        this.name = name;
    }

    /**
     * Constructor.
     *
     * @param name the name of the mergepoint, not null.
     * @param actions the actions in this mergepoint.
     *
     * @throws IllegalArgumentException if name is null.
     */
    public MergepointMapping(String name, List<MergepointAction> actions)
    {
        this(name);
        this.actions.addAll(actions);
    }

    /**
     * Returns the name of the mergepoint.
     *
     * @return the name of the mergepoint, not null.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the list of actions executed at the mergepoint.
     *
     * @return the list of actions. Not null, may be empty.
     */
    public List<MergepointAction> getActions()
    {
        return actions;
    }

    /**
     * Adds an action to this mergepont mapping at the end of the action list.
     *
     * @param action the action to add, not null.
     *
     * @throws NullPointerException if action is null.
     */
    public void addAction(MergepointAction action)
    {
        if (action == null)
        {
            throw new NullPointerException("action is null");
        }
        this.actions.add(action);
    }

    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append("(name=").append(name)
        .append(",actions=").append(actions)
        .append(")");
        return result.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actions == null) ? 0 : actions.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final MergepointMapping other = (MergepointMapping) obj;
        if (actions == null)
        {
            if (other.actions != null)
            {
                return false;
            }
        }
        else if (!actions.equals(other.actions))
        {
            return false;
        }
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
        return true;
    }
}
