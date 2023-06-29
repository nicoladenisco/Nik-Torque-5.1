package org.apache.torque.generator.source;

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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.torque.generator.processor.string.StringProcessor;

/**
 * All necessary informations for postprocessing the result.
 * Contains the postprocessor instance.
 *
 * $Id$
 */
public final class PostprocessorDefinition
{
    /**
     * The postprocessor instance.
     */
    private final StringProcessor postprocessor;

    /**
     * Constructor.
     *
     * @param postprocessor instance, not null.
     */
    public PostprocessorDefinition(
            final StringProcessor postprocessor)
    {
        if (postprocessor == null)
        {
            throw new NullPointerException(
                    "postprocessor must not be null");
        }
        this.postprocessor = postprocessor;
    }

    /**
     * Returns the postprocessor instance for this definition.
     *
     * @return the postprocessor instance, not null.
     */
    public StringProcessor getPostprocessor()
    {
        return postprocessor;
    }

    @Override
    public String toString()
    {
        return "PostprocessorDefinition [postprocessor="
                + postprocessor + "]";
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder()
                .append(postprocessor);
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(final Object obj)
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
        PostprocessorDefinition other = (PostprocessorDefinition) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder()
                .append(postprocessor, other.postprocessor);
        return equalsBuilder.isEquals();
    }
}
