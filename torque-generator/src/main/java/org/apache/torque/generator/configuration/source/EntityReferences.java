package org.apache.torque.generator.configuration.source;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resolves system Ids for schema files to the schema file content.
 */
public class EntityReferences implements EntityResolver
{
    /** All known entity references. */
    private Map<String, byte[]> entityReferences
        = new HashMap<>();

    /**
     * Adds a new entity reference.
     *
     * @param systemId the systemId of the entity, not null.
     * @param content the content of the entity, not null.
     *
     * @throws NullPointerException if systemId or content are null.
     * @throws IllegalArgumentException if the systemId is already defined.
     */
    public void addEntityReference(String systemId, byte[] content)
    {
        if (systemId == null)
        {
            throw new NullPointerException("systemId must not be null");
        }
        if (content == null)
        {
            throw new NullPointerException("content must not be null");
        }
        if (entityReferences.containsKey(systemId))
        {
            throw new IllegalArgumentException("systemId is already defined");
        }
        entityReferences.put(systemId, content);
    }

    /**
     * Returns whether the given system id is known.
     *
     * @param systemId the system id to check.
     *
     * @return true if the system id can be resolved, false otherwise.
     */
    public boolean containsSystemId(String systemId)
    {
        return entityReferences.containsKey(systemId);
    }

    /**
     * Returns a copy of the entity reference map.
     *
     * @return a copy of the entity reference map.
     */
    public Map<String, byte[]> getEntityReferences()
    {
        return new HashMap<>(entityReferences);
    }

    /**
     * An implementation of the SAX <code>EntityResolver</code>
     * interface to be called by the XML parser.  If the systemId is known,
     * the corresponding resource from the jar is returned.
     * In all other cases, null is returned to indicate that the parser
     * should open a regular connection to the systemId URI.
     *
     * @param publicId The public identifier of the external entity
     * @param systemId The system identifier of the external entity
     * @return An <code>InputSource</code> for the entity if the
     *         systemId is known, or null otherwise.
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
            throws IOException, SAXException
    {

        byte[] content = entityReferences.get(systemId);
        if (content == null)
        {
            return null;
        }
        return new InputSource(new ByteArrayInputStream(content));
    }
}
