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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Selects Files in a directory and the subdirectories of the directory.
 * From these files, all that match an include pattern and do not
 * match an exclude pattern are selected.
 *
 * @version $Id: Fileset.java 1855923 2019-03-20 16:19:39Z gk $
 */
public class Fileset
{
    /** The class logger. */
    private static Log log = LogFactory.getLog(Fileset.class);

    /** The base directory of the fileset. */
    private File basedir;

    /**
     * The patterns for the files to include.
     * If null or empty, all Files are included.
     */
    private Collection<String> includes;

    /**
     * The patterns for the files to exclude.
     * If null or empty, no Files are excluded.
     */
    private Collection<String> excludes;

    /**
     * Default constructor.
     */
    public Fileset()
    {
    }

    /**
     * All-Args constructor.
     *
     * @param basedir the basedir, or null to use the current basedir.
     * @param includes The patterns for the files to include.
     *        If null or empty, all Files are included.
     * @param excludes The patterns for the files to exclude.
     *        If null or empty, no Files are excluded.
     */
    public Fileset(File basedir,
            Collection<String> includes,
            Collection<String> excludes)
    {
        this.basedir = basedir;
        this.includes = includes;
        this.excludes = excludes;
    }

    /**
     * Returns the base directory of the fileset.
     *
     * @return the base directory, or null if no basedir is specified.
     */
    public File getBasedir()
    {
        return basedir;
    }

    /**
     * Sets the base directory of the fileset.
     *
     * @param basedir the base directory, or null.
     */
    public void setBasedir(File basedir)
    {
        this.basedir = basedir;
    }

    /**
     * Returns the include patterns for the fileset.
     *
     * @return the include patterns, or null if all files should be included.
     */
    public Collection<String> getIncludes()
    {
        return includes;
    }

    /**
     * Sets the include patterns for the fileset.
     *
     * @param includes the include patterns, or null if all files
     *        should be included.
     */
    public void setIncludes(Collection<String> includes)
    {
        this.includes = includes;
    }

    /**
     * Returns the exclude patterns for the fileset.
     *
     * @return the exclude patterns, or null if all files should be excluded.
     */
    public Collection<String> getExcludes()
    {
        return excludes;
    }

    /**
     * Sets the exclude patterns for the fileset.
     *
     * @param excludes the exclude patterns, or null if all files
     *        should be excluded.
     */
    public void setExcludes(Collection<String> excludes)
    {
        this.excludes = excludes;
    }

    /**
     * Returns the names of all files matching this fileset.
     *
     * @return the names of all matching files, not null.
     *
     * @throws IOException if an error occurs reading the file names.
     */
    public List<File> getFiles() throws IOException
    {
        List<File> result = new ArrayList<>();
        if (includes == null || includes.isEmpty())
        {
            getAllFiles(basedir, result);
        }
        else
        {
            // process includes
            for (String includePattern : includes)
            {
                int wildcardFreeSeparatorPos
                = getWildcardFreeSeparatorPos(includePattern);
                String wildcardFreeIncludePart = getPathPartBefore(
                        includePattern,
                        wildcardFreeSeparatorPos);
                if (log.isTraceEnabled())
                {
                    log.trace("getFiles() : traversing directory "
                            + wildcardFreeIncludePart
                            + " in base dir "
                            + basedir);
                }
                File wildcardFreeBaseDir = new File(
                        basedir,
                        wildcardFreeIncludePart);
                String wildcardPattern
                = getPathPartAfter(includePattern, wildcardFreeSeparatorPos);
                String[] wildcardParts = StringUtils.split(wildcardPattern, "\\/");
                List<String> wildcardPartList = Arrays.asList(wildcardParts);

                List<File> includeFiles = getFiles(
                        wildcardFreeBaseDir,
                        wildcardPartList);
                result.addAll(includeFiles);
            }
        }
        // process excludes
        if (excludes == null)
        {
            if (log.isTraceEnabled())
            {
                log.trace("getFiles() : no excludes are defined.");
            }
            return result;
        }
        Iterator<File> fileIt = result.iterator();
        while (fileIt.hasNext())
        {
            File file = fileIt.next();
            if (log.isTraceEnabled())
            {
                log.trace("getFiles() : checking excludes for file "
                        + file.getPath());
            }
            boolean excluded = false;
            for (String excludePattern : excludes)
            {
                File excludePatternFile = new File(basedir, excludePattern);
                if (matchesPattern(file, excludePatternFile.getPath()))
                {
                    if (log.isTraceEnabled())
                    {
                        log.trace("getFiles() : exclude pattern "
                                + excludePatternFile.getPath()
                                + " matches, file is excluded");
                    }
                    excluded = true;
                    break;
                }
                else if (log.isTraceEnabled())
                {
                    log.trace("getFiles() : exclude pattern "
                            + excludePatternFile.getPath()
                            + " does not match");
                }
            }
            if (excluded)
            {
                fileIt.remove();
            }
        }

        // make file order reproducable
        Collections.sort(result);

        return result;
    }

    /**
     * Reads the names of all files in a directory and its subdirectories.
     *
     * @param currentBaseDir the base directory from which the files are read,
     *        not null.
     * @param toAddTo the list of files where the found file should be added to,
     *        not null.
     *
     * @throws IOException if an error occurs during reading the directory.
     */
    static void getAllFiles(File currentBaseDir, List<File> toAddTo)
            throws IOException
    {
        if (currentBaseDir == null)
        {
            currentBaseDir = new File(".");
        }
        if (log.isTraceEnabled())
        {
            log.trace("getAllFiles() : traversing directory "
                    +  currentBaseDir.getAbsolutePath());
        }
        File[] filesInDir = currentBaseDir.listFiles(
                new WildcardFilter("*", false, true));
        if (filesInDir == null)
        {
            throw new IOException(
                    "Could not list files in the following Directory "
                            + "while reading the sources: "
                            + currentBaseDir.getAbsolutePath());
        }
        if (log.isTraceEnabled())
        {
            log.trace("getAllFiles() : Adding files "
                    + Arrays.toString(filesInDir)
                    + " to candidate list");
        }
        toAddTo.addAll(Arrays.asList(filesInDir));

        File[] dirsInDir = currentBaseDir.listFiles(
                new WildcardFilter("*", true, false));

        if ( dirsInDir != null )
        {
	        for (File dir : dirsInDir)
	        {
	            getAllFiles(dir, toAddTo);
	        }
        }
    }

    /**
     * Reads the name of a set of files matching a path pattern.
     *
     * @param currentBaseDir the base directory from which the files are read,
     *        not null.
     * @param pathPartList the split path to the files (split where the path
     *        separator appears). E.g. to access resources/x.properties,
     *        the list would be ["resources", "x.properties"]
     * @return the set of all files which match the pathPartList.
     *
     * @throws IOException if an error occurs during reading the directory.
     */
    static List<File> getFiles(
            File currentBaseDir, List<String> pathPartList)
                    throws IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("getFiles(File, List) : traversing directory "
                    +  currentBaseDir.getAbsolutePath()
                    + ", current path parts: "
                    + pathPartList);
        }
        List<String> partsCopy = new ArrayList<>(pathPartList);
        String includeToProcess = partsCopy.remove(0);
        if (partsCopy.size() == 0)
        {
            File[] matches = currentBaseDir.listFiles(
                    new WildcardFilter(includeToProcess, false, true));
            if (matches == null)
            {
                throw new IOException(
                        "Could not list files in the following Directory "
                                + "while reading the sources: "
                                + currentBaseDir.getAbsolutePath());
            }
            List<File> result = Arrays.asList(matches);
            if (log.isTraceEnabled())
            {
                log.trace("getFiles(File, List) : Returning files "
                        + result);
            }
            return result;
        }
        if ("..".equals(includeToProcess))
        {
            return getFiles(currentBaseDir.getParentFile(), partsCopy);
        }
        File[] matchingDirs = currentBaseDir.listFiles(
                new WildcardFilter(includeToProcess, true, false));
        List<File> result = new ArrayList<>();
        if ( matchingDirs != null )
        {
	        for (File dir : matchingDirs)
	        {
	            result.addAll(getFiles(dir, partsCopy));
	        }
        }
        return result;
    }

    /**
     * Returns the position of the separator which separates the base part
     * of the path which does not contain any wildcards from the rest.
     * Example:
     * <ul>
     *   <li>*.txt returns -1(no separator)</li>
     *   <li>schema*.xml returns -1(no separator)</li>
     *   <li>xml\schema*.xml returns 3 (backslash position)</li>
     *   <li>/xml/???/schema*.xml returns 4 (middle slash position)</li>
     * </ul>
     * @param path the path to compute the position from, not null.
     * @return the separator position, -1 if no base part exists.
     */
    static int getWildcardFreeSeparatorPos(String path)
    {
        int asteriskIndex = path.indexOf("*");
        int questionMarkIndex = path.indexOf("?");
        if (asteriskIndex != -1)
        {
            if (questionMarkIndex != -1)
            {
                int min = Math.min(asteriskIndex, questionMarkIndex);
                return getLargestSeparatorPos(path, min);
            }
            else
            {
                return getLargestSeparatorPos(path, asteriskIndex);
            }
        }
        return getLargestSeparatorPos(path, questionMarkIndex);
    }

    /**
     * Returns the largest position of a path separator within the path
     * which is smaller than endIndex. An endIndex of -1 means that
     * the largest separator pos should be given. -1 is returned if no
     * separator is present in the region.
     * <ul>
     *   <li>getBaseDir("/xml/xxxx", 5) returns 4</li>
     *   <li>getBaseDir("/xml/x/y", -1) returns 6</li>
     *   <li>getBaseDir("/xml/x/y/", -1) returns 8</li>
     * </ul>
     */
    static int getLargestSeparatorPos(String path, int maxIndex)
    {
        String baseString;
        if (maxIndex == -1)
        {
            baseString = path;
        }
        else
        {
            baseString = path.substring(0, maxIndex);
        }
        return FilenameUtils.indexOfLastSeparator(baseString);
    }

    /**
     * Returns the part of the path before the cutPosition.
     * If this part is empty or separatorPos is -1, "." is returned.
     * The character at cutPosition is not included in the result.
     *
     * @param path the path to get the part from.
     * @param cutPosition the position where to cut.
     *
     * @return the part of the path before cutPosition, or "." if this part
     *         does not exist.
     */
    static String getPathPartBefore(String path, int cutPosition)
    {
        if (cutPosition == -1)
        {
            return ".";
        }
        else
        {
            String resultString = path.substring(0, cutPosition);
            if (StringUtils.EMPTY.equals(resultString))
            {
                resultString = ".";
            }
            return resultString;
        }
    }


    /**
     * Returns the part of the path after the separatorPos.
     * The character at cutPosition is not included in the result.
     *
     * @param path the path to get the part from.
     * @param cutPosition the position where to cut.
     *
     * @return the part of the path before cutPosition, or "." if this part
     *         does not exist.
     */
    static String getPathPartAfter(String path, int cutPosition)
    {
        String resultString = path.substring(cutPosition + 1);
        return resultString;
    }

    static boolean matchesPattern(File file, String pattern)
    {
        String filePath = file.getPath();
        List<String> fileParts = splitAndNormalize(filePath);
        List<String> patternParts = splitAndNormalize(pattern);
        if (fileParts.size() != patternParts.size())
        {
            return false;
        }
        Iterator<String> patternPartIt = patternParts.iterator();
        for (String filePart : fileParts)
        {
            String patternPart = patternPartIt.next();
            if (!FilenameUtils.wildcardMatch(
                    filePart,
                    patternPart,
                    IOCase.SENSITIVE))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Splits a path in its parts and normalizes the path
     * (i.e. removes . and ..), if possible.
     *
     * @param path the path to normalize
     *
     * @return the normalized path in its parts.
     */
    static List<String> splitAndNormalize(String path)
    {
        String[] parts = StringUtils.split(path, "\\/");
        List<String> normalizedParts = new ArrayList<>();
        for (String part : parts)
        {
            if (".".equals(part))
            {
                continue;
            }
            if ("..".equals(part) && !normalizedParts.isEmpty())
            {
                normalizedParts.remove(normalizedParts.size() - 1);
                continue;
            }
            normalizedParts.add(part);
        }
        return normalizedParts;
    }

    @Override
    public String toString()
    {
        return "Fileset [basedir=" + basedir
                + ", excludes=" + excludes
                + ", includes=" + includes + "]";
    }
}
