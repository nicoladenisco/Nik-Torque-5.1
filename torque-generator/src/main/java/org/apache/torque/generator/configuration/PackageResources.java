package org.apache.torque.generator.configuration;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Container of resources which contain a specific package.
 *
 * @version $Id: $
 */
public class PackageResources
{
    /** The prefix if an url points to a file. */
    private static final String FILE_URL_PREFIX = "file:";

    /** The logger. */
    private static Log log
    = LogFactory.getLog(PackageResources.class);

    /** The jar files containing the package. */
    private final List<JarFile> jarFiles = new ArrayList<>();

    /** The directories containing the package. */
    private final List<File> directories = new ArrayList<>();

    /** The path to the package, not null. */
    private final String packagePath;

    /**
     * Constructor, finds the resources for a certain package.
     *
     * @param packageToFind the path to the package, using / or \\ as path
     *        separator.
     * @param classLoader the class loader to use.
     *
     * @throws ConfigurationException when accessing the class path fails.
     */
    public PackageResources(
            final String packageToFind,
            final ClassLoader classLoader)
                    throws ConfigurationException
    {
        if (packageToFind == null)
        {
            throw new NullPointerException("packagePath must not be null");
        }
        if (packageToFind.startsWith("/"))
        {
            packagePath = packageToFind.substring(1);
        }
        else
        {
            packagePath = packageToFind;
        }

        final Enumeration<URL> dirUrls;
        try
        {
            dirUrls = classLoader.getResources(packagePath);
        }
        catch (IOException e)
        {
            throw new ConfigurationException("Could not scan class path", e);
        }

        while (dirUrls.hasMoreElements())
        {
            URL dirUrl = dirUrls.nextElement();
            String dirUrlString = dirUrl.toExternalForm();
            if (dirUrlString.startsWith("jar"))
            {
                String jarFilePath = dirUrl.getFile();
                if (jarFilePath.startsWith(FILE_URL_PREFIX))
                {
                    jarFilePath = jarFilePath.substring(
                            FILE_URL_PREFIX.length());
                }
                jarFilePath = jarFilePath.substring(
                        0,
                        jarFilePath.indexOf("!"));
                if (log.isTraceEnabled())
                {
                    log.trace("package " + packagePath + " found in jar file"
                            + jarFilePath);
                }
                JarFile jarFile;
                try
                {
                    jarFile = new JarFile(jarFilePath);
                    jarFiles.add(jarFile);
                }
                catch (IOException e)
                {
                    log.error("Could not open jar File "
                            + jarFilePath);
                    throw new ConfigurationException(e);
                }
                continue;
            }
            File directory = new File(dirUrl.getFile());
            if (!directory.exists())
            {
                throw new ConfigurationException(
                        "Could not read directory "
                                + packagePath
                                + " in classpath; directory URL is "
                                + dirUrl
                                + " file is "
                                + dirUrl.getFile());
            }
            directories.add(directory);
        }
    }

    /**
     * Returns all jar files containing the desired package.
     *
     * @return the jar files, not null.
     */
    public List<JarFile> getJarFiles()
    {
        return jarFiles;
    }

    /**
     * Returns all directories containing the desired package.
     *
     * @return the directories, not null.
     */
    public List<File> getDirectories()
    {
        return directories;
    }

    /**
     * Returns whether the queried package exists in the class path.
     * @return true if package does not exist
     */
    public boolean isEmpty()
    {
        return jarFiles.isEmpty() && directories.isEmpty();
    }

    /**
     * Returns all resource names in the package ending with the defined suffix.
     *
     * @param suffix the suffix which the resource name must have,
     *        or null to match every file name.
     * @param recurse true if subpackages should also be searched,
     *        false if only the specified package should be searched.
     *
     * @return A collection of resource names with the specified suffix,
     *         not null. The contained file names are relative to
     *         the scanned package.
     */
    public Collection<String> getAllResourcesEndingWith(
            final String suffix,
            final boolean recurse)
    {
        List<String> result = new ArrayList<>();
        for (JarFile jarFile : jarFiles)
        {
            String dirNameInJar = packagePath;
            if (dirNameInJar.startsWith("/"))
            {
                dirNameInJar = dirNameInJar.substring(1);
            }
            result.addAll(getFilesInJarDirectoryWithSuffix(
                    dirNameInJar,
                    jarFile,
                    suffix,
                    recurse));
        }
        for (File directory : directories)
        {
            result.addAll(getFilesInDirectoryWithSuffix(
                    directory,
                    suffix,
                    "",
                    recurse));
        }
        return result;
    }

    /**
     * Returns all files in a directory with end with a given suffix.
     *
     * @param directoryToScan the directory to check files in.
     * @param suffix the suffix the files must have,
     *        or null to match every file name.
     * @param prefixToResult a prefix to the path,
     *        to be able to recurse in subdirectories
     * @param recurse whether to scan subdirectories.
     *
     * @return a collection of matching files, not null.
     */
    static Collection<String> getFilesInDirectoryWithSuffix(
            final File directoryToScan,
            final String suffix,
            final String prefixToResult,
            final boolean recurse)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Analyzing directory " + directoryToScan
                    +  " with subdirectory " + prefixToResult
                    + " for files with suffix " + suffix);
        }
        final List<String> result = new ArrayList<>();
        if (!directoryToScan.isDirectory())
        {
            log.debug("Directory "
                    + directoryToScan.getAbsolutePath()
                    + "is not a directory, "
                    + "no outlet definitions will be read "
                    + "(template outlets may still be available "
                    + "through scanning the templates directory)");
            return result;
        }
        String[] filenames = directoryToScan.list();
        if (filenames == null)
        {
            if (log.isDebugEnabled())
            {
                log.debug(directoryToScan
                        + " does not exist, returning the empty list");
            }
            return result;
        }
        for (String filename : filenames)
        {
            File file = new File(directoryToScan, filename);
            if (file.isDirectory())
            {
                if (recurse)
                {
                    result.addAll(getFilesInDirectoryWithSuffix(
                            file,
                            suffix,
                            prefixToResult + filename + "/" ,
                            recurse));
                }
                continue;
            }
            String rawName = file.getName();
            if (suffix != null && !rawName.endsWith(suffix))
            {
                continue;
            }
            result.add(prefixToResult + filename);
        }
        if (log.isDebugEnabled())
        {
            log.debug("Found the following files " + result);
        }
        return result;
    }

    /**
     * Extracts files in a directory from a jar file.
     *
     * @param directory the name of the directory
     *        containing the files. Cannot be
     *        a composite path like parent/child.
     * @param jarFile the jar file to process, not null.
     * @param suffix the suffix the files must have,
     *        or null to match every file name.
     * @param searchSubdirectories if files in subdirectories should
     *        also be considered.
     *
     * @return a collection with the names of all outlet configuration files
     *         contained in the jar file.
     * @throws NullPointerException if jarFile
     *         or outletConfigurationDirectory is null
     */
    static Collection<String> getFilesInJarDirectoryWithSuffix(
            final String directory,
            final JarFile jarFile,
            final String suffix,
            final boolean searchSubdirectories)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Analyzing jar file " + jarFile.getName()
            +  " seeking directory " + directory
            + " for files with suffix " + suffix);
        }
        List<String> result = new ArrayList<>();

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements())
        {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.isDirectory())
            {
                continue;
            }
            String rawName = jarEntry.getName();
            if (!rawName.startsWith(directory + '/'))
            {
                continue;
            }
            String name = rawName.substring(directory.length() + 1);
            if (suffix != null && !name.endsWith(suffix))
            {
                continue;
            }

            if (name.indexOf("/") != -1
                    && !searchSubdirectories)
            {
                continue;
            }
            result.add(name);
        }
        if (log.isDebugEnabled())
        {
            log.debug("Found the following files " + result);
        }
        return result;
    }

}
