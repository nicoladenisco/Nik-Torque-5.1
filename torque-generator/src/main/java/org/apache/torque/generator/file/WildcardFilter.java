package org.apache.torque.generator.file;

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
import java.io.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;

/**
 * A filter evaluating a file name against a wildcard expression.
 */
public class WildcardFilter implements FileFilter
{
    /**
     * The wildcard expression against which the file names are
     * checked.
     */
    private String expression;

    /**
     * Whether directories are accepted at all.
     */
    private boolean acceptDir;

    /**
     * Whether files are accepted at all.
     */
    private boolean acceptFile;

    /**
     * Constructor.
     *
     * @param expression The wildcard expression against which
     *        the file names are checked.
     * @param acceptDir Whether directories are accepted at all.
     * @param acceptFile Whether files are accepted at all.
     */
    public WildcardFilter(
            String expression,
            boolean acceptDir,
            boolean acceptFile)
    {
        this.expression = expression;
        this.acceptDir = acceptDir;
        this.acceptFile = acceptFile;
    }

    /**
     * Returns whether a file matches the criteria of this filter.
     * If the file is a directory and <code>acceptDir</code> is false,
     * the file is rejected.
     * If the file is regular file and <code>acceptFile</code> is false,
     * the file is rejected.
     * If the filename does not match the wildcard filter, the file is
     * rejected.
     * If none of the above applies, the file is accepted.
     *
     * @return false if the file is rejected, true if it is accepted.
     */
    @Override
    public boolean accept(File file)
    {
        if (!acceptDir && file.isDirectory())
        {
            return false;
        }
        if (!acceptFile && file.isFile())
        {
            return false;
        }
        if (FilenameUtils.wildcardMatch(
                file.getName(),
                expression,
                IOCase.SENSITIVE))
        {
            return true;
        }
        return false;
    }
}
