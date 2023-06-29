package org.apache.torque.generator.outlet.java;

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


import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletImpl;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Creates a target filename from a source filename.
 */
public class ModifySourcenameOutlet extends OutletImpl
{
    /**
     * The prefix which should be added at the beginning of the target filename.
     */
    private String prefix = "";

    /**
     * The suffix which should be added at the end of the target filename.
     */
    private String suffix = "";

    /**
     * Character sequence after which the source file should be discarded.
     * The character sequence itself is also discarded.
     */
    private String discardFrom = null;

    /**
     * Character sequence up to which the source file should be discarded.
     * The character sequence itself is also discarded.
     */
    private String discardTo = null;

    /** The logger of the class. */
    private static Logger logger
    = Logger.getLogger(ModifySourcenameOutlet.class);

    /**
     * Constructor.
     *
     * @param qualifiedName the qualified name of the outlet, not null.
     */
    public ModifySourcenameOutlet(QualifiedName qualifiedName)
    {
        super(qualifiedName);
    }

    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {

        final String sourceName;
        {
            File sourceFile = controllerState.getSourceFile();
            if (sourceFile == null)
            {
                logger.warn("execute(): sourceFile is null, "
                        + "returning the empty String");
                return new OutletResult("");
            }
            sourceName = sourceFile.getName();
        }

        int startFrom = 0;
        int endAt = sourceName.length();
        if (!StringUtils.isEmpty(discardTo))
        {
            int position = sourceName.lastIndexOf(discardTo);
            if (position != -1)
            {
                startFrom = position + discardTo.length();
            }
        }
        if (!StringUtils.isEmpty(discardFrom))
        {
            int position = sourceName.indexOf(discardFrom);
            if (position != -1)
            {
                endAt = position;
            }
        }
        String result;
        if (startFrom <= endAt)
        {
            result = sourceName.substring(startFrom, endAt);
        }
        else
        {
            logger.debug("execute(): startFrom is later than endAt, "
                    + "returning the empty String");
            result = "";
        }
        result = prefix + result + suffix;
        return new OutletResult(result);
    }

    /**
     * Returns the prefix which is added in front of the modified source
     * filename.
     *
     * @return the prefix, not null
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Sets the prefix which is added in front of the modified source
     * filename.
     *
     * @param prefix the prefix, not null.
     *
     * @throws NullPointerException if prefix is null.
     */
    public void setPrefix(String prefix)
    {
        if (prefix == null)
        {
            throw new NullPointerException("prefix is null");
        }
        this.prefix = prefix;
    }

    /**
     * Returns the suffix which is added after the modified source
     * filename.
     *
     * @return the suffix, not null.
     */
    public String getSuffix()
    {
        return suffix;
    }

    /**
     * Sets the suffix which is added after the modified source
     * filename.
     *
     * @param suffix the suffix, not null
     *
     * @throws NullPointerException if suffix is null.
     */
    public void setSuffix(String suffix)
    {
        if (suffix == null)
        {
            throw new NullPointerException("suffix is null");
        }
        this.suffix = suffix;
    }

    /**
     * Returns the character sequence which separates the discarded beginning
     * of the source filename from the returned end.
     * <p>
     * Example: if the source filename is "xyz.a.b.c", and discardFrom is ".",
     * then "xyz" will be returned.
     *
     * @return the character sequence from whose first occurrence the
     *         source filename is discarded.
     */
    public String getDiscardFrom()
    {
        return discardFrom;
    }

    /**
     * Sets the character sequence which separates the discarded beginning
     * of the source filename from the returned end.
     * <p>
     * Example: if the source filename is "xyz.a.b.c", and discardFrom is ".",
     * then "xyz" will be returned.
     *
     * @param discardFrom the character sequence from whose first occurrence the
     *         source filename is discarded.
     */
    public void setDiscardFrom(String discardFrom)
    {
        this.discardFrom = discardFrom;
    }

    /**
     * The character sequence from the beginning of the source filename
     * to and including the last occurrence of the returned string is discarded
     * for the result.
     * <p>
     * Example: if the source filename is "xyz.a.b.c", and discardTo is ".",
     * then "c" will be returned.
     *
     * @return the character sequence up to whose last occurrence the
     *         source filename is discarded.
     */
    public String getDiscardTo()
    {
        return discardTo;
    }

    /**
     * Sets the character sequence after which last occurrence the source
     * filename is returned.
     * <p>
     * Example: if the source filename is "xyz.a.b.c", and discardTo is ".",
     * then "c" will be returned.
     *
     * @param discardTo the character sequence up to whose last occurrence the
     *         source filename is discarded.
     */
    public void setDiscardTo(String discardTo)
    {
        this.discardTo = discardTo;
    }
}
