package org.apache.torque.ant.task;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.configuration.UnitDescriptor;
import org.apache.torque.generator.configuration.controller.Loglevel;
import org.apache.torque.generator.configuration.option.MapOptionsConfiguration;
import org.apache.torque.generator.configuration.paths.CustomProjectPaths;
import org.apache.torque.generator.configuration.paths.DefaultTorqueGeneratorPaths;
import org.apache.torque.generator.configuration.paths.Maven2DirectoryProjectPaths;
import org.apache.torque.generator.configuration.paths.ProjectPaths;
import org.apache.torque.generator.control.Controller;

/**
 * This class generates an XML schema of an existing database from
 * JDBC metadata.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:fedor.karpelevitch@barra.com">Fedor Karpelevitch</a>
 * @version $Id: Torque4JDBCTransformTask.java 1873257 2020-01-28 15:47:06Z gk $
 */
public class Torque4JDBCTransformTask extends Task
{
    
    /**
     * The packaging type of the generation unit, either "directory" 
     * or "classpath". Default is "classpath".
     */
    private String packaging = "classpath";
    
    /**
     * The target directory for files which are generated each time anew.
     * Default is "target/generated-sources"
     */
    private File defaultOutputDir = new File("target/generated-schema");    

    /**
     * The root directory of the project.
     * Has no effect if packaging is "classpath".
     * Default is ".".
     */
    private File projectRootDir = new File(".");
    
    /** Name of XML database schema produced. */
    protected String xmlSchema = "schema.xml";
    
    /**
     * The Loglevel to use in the generation process. Must be one of
     * trace, debug, info, warn or error.
     * If not set, the log level defined in the generation unit is used.
     */
    private String loglevel;

    /** JDBC URL. */
    protected String dbUrl;

    /** JDBC driver. */
    protected String dbDriver;

    /** JDBC user name. */
    protected String dbUser;

    /** JDBC password. */
    protected String dbPassword;
    
    /**
     * The configuration package of the generation unit.
     */
    private String configPackage;
    
    /**
     * The configuration directory of the generation unit.
     * Has no effect if packaging is "classpath".
     */
    private File configDir;


    public void setDbUrl(String v)
    {
        dbUrl = v;
    }

    public void setDbDriver(String v)
    {
        dbDriver = v;
    }


//    public void setOutputFile (String v)
//    {
//        xmlSchema = v;
//    }


    /**
     * Default constructor.
     *
     * @throws BuildException
     */
    @Override
    public void execute() throws BuildException
    {
        log("Torque - JDBCToXMLSchema starting");
        log("Your DB settings are:");
        log("driver : " + dbDriver);
        log("URL : " + dbUrl);
        // log("password : " + dbPassword);

        try
        {
            generateXML();
            log("Target file:" +defaultOutputDir + "/"  + xmlSchema);
        }
        catch (Exception e)
        {
            throw new BuildException(e);
        }
        log("Torque - JDBCToXMLSchema finished");
    }

    /**
     * Generates an XML database schema from JDBC metadata.
     *
     * @throws Exception a generic exception.
     */
    public void generateXML() throws Exception
    {
        // Load the Driver.
        //Class.forName(dbDriver);
        //log("DB driver sucessfuly instantiated", Project.MSG_INFO);

        // Attemtp to connect to a database.
        //Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        //log("DB connection established", Project.MSG_INFO);
     
        Controller controller = new Controller();
        List<UnitDescriptor> unitDescriptors = new ArrayList<>();
      
        Map<String, String> overrideOptions = new HashMap<>();
        overrideOptions.put("torque.jdbc2schema.url", dbUrl);
        overrideOptions.put("torque.jdbc2schema.driver", dbDriver);
        overrideOptions.put("torque.jdbc2schema.user", dbUser);
        overrideOptions.put("torque.jdbc2schema.password", dbPassword);

        
        UnitDescriptor.Packaging packaging;
        if ("directory".equals(this.packaging))
        {
            packaging = UnitDescriptor.Packaging.DIRECTORY;
        }
        else if ("classpath".equals(this.packaging))
        {
            packaging = UnitDescriptor.Packaging.CLASSPATH;
        }
        else
        {
            throw new IllegalArgumentException(
                    "Unknown packaging " + this.packaging
                    + ", must be jar, directory or classpath");
        }
        log("Packaging is " + packaging, Project.MSG_DEBUG);
        
        ProjectPaths defaultProjectPaths;
        if (UnitDescriptor.Packaging.DIRECTORY == packaging)
        {
            defaultProjectPaths
                = new Maven2DirectoryProjectPaths(projectRootDir);
        }
        else if (UnitDescriptor.Packaging.CLASSPATH == packaging)
        {
            defaultProjectPaths
                = new Maven2DirectoryProjectPaths(projectRootDir);
        }
        else
        {
            throw new IllegalStateException("Unknown packaging" + packaging);
        }
        
        CustomProjectPaths projectPaths
        = new CustomProjectPaths(defaultProjectPaths);

        
        if (UnitDescriptor.Packaging.CLASSPATH == packaging)
        {
            if (configPackage == null)
            {
                throw new BuildException(
                        "configPackage must be set for packaging =\"classpath\"");
            }
            projectPaths.setConfigurationPackage(configPackage);
            projectPaths.setConfigurationDir(null);
        }
        else
        {
            if (configDir != null)
            {
                projectPaths.setConfigurationDir(configDir);
                log("Setting config dir to " + configDir.toString(),
                        Project.MSG_DEBUG);
            }
        }
        
        if (defaultOutputDir != null)
        {
            projectPaths.setOutputDirectory(null, defaultOutputDir);
            log("Setting defaultOutputDir to "
                    + defaultOutputDir.getAbsolutePath(),
                    Project.MSG_DEBUG);
        }
        
        log("ProjectPaths = " + projectPaths);
        
        Loglevel convertedLoglevel = null;
        if (this.loglevel != null)
        {
            convertedLoglevel = Loglevel.getByKey(loglevel);
        }
        
        UnitDescriptor unitDescriptor = new UnitDescriptor(
                packaging,
                projectPaths,
                new DefaultTorqueGeneratorPaths());
        unitDescriptor.setOverrideOptions(
                new MapOptionsConfiguration(overrideOptions));
        unitDescriptor.setLoglevel(convertedLoglevel);
        unitDescriptors.add(unitDescriptor);

        try {
            log("Generation started", Project.MSG_DEBUG);
            controller.run(unitDescriptors);          
            log("Generation successful", Project.MSG_INFO);
        } catch (GeneratorException e) {
            log("Error during jdbc3schema generation", e, Project.MSG_ERR);
            throw new BuildException(e.getMessage());
        } 

    }
    
    public void setConfigDir(final File configDir)
    {
        this.configDir = configDir;
    }

    public void setConfigPackage(final String configPackage)
    {
        this.configPackage = configPackage;
    }

    /**
     * Sets the default output base directory for generated files.
     *
     * @param defaultOutputDir the default output directory,
     *                  or null to use the default.
     */
    public void setDefaultOutputDir(final File defaultOutputDir)
    {
        this.defaultOutputDir = defaultOutputDir;
    }

    /**
     * Sets the root directory of the project.
     *
     * @param projectRootDir the project root Directory.
     */
    public void setProjectRootDir(final File projectRootDir)
    {
        this.projectRootDir = projectRootDir;
    }

    
    /**
     * Sets the packaging.
     *
     * @param packaging the packaging or "directory"
     */
    public void setPackaging(final String packaging)
    {
        this.packaging = packaging;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
    
}
