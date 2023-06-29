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
import org.apache.torque.generator.source.transform.SourceTransformer;

/**
 * All necessary informations for doing a transformation.
 * Contains the SourceTransformer instance.
 *
 * $Id: SourceTransformerDefinition.java 1855923 2019-03-20 16:19:39Z gk $
 */
public final class SourceTransformerDefinition
{
    /**
     * The transformer instance.
     */
    private final SourceTransformer sourceTransformer;

    /**
     * Constructor.
     *
     * @param sourceTransformer the transformer instance, not null.
     */
    public SourceTransformerDefinition(
            final SourceTransformer sourceTransformer)
    {
        if (sourceTransformer == null)
        {
            throw new NullPointerException(
                    "sourceTransformer must not be null");
        }
        this.sourceTransformer = sourceTransformer;
    }

    /**
     * Returns the source transformer instance for this definition.
     *
     * @return the source transformer instance, not null.
     */
    public SourceTransformer getSourceTransformer()
    {
        return sourceTransformer;
    }

    @Override
    public String toString()
    {
        return "SourceTransformerDefinition [sourceTransformer="
                + sourceTransformer + "]";
    }

    @Override
    public int hashCode()
    {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder()
                .append(sourceTransformer);
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
        SourceTransformerDefinition other = (SourceTransformerDefinition) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder()
                .append(sourceTransformer, other.sourceTransformer);
        return equalsBuilder.isEquals();
    }
}
