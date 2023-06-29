package org.apache.torque.templates.transformer.om;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaAttributeName;
import org.apache.torque.templates.TorqueSchemaElementName;

/**
 * Transforms an Inheritance element.
 */
public class OMInheritanceTransformer
{
    /** The logger. */
    private static Logger logger = LogManager.getLogger(OMInheritanceTransformer.class);
    
    public void transform(
            SourceElement inheritanceElement,
            ControllerState controllerState)
                    throws SourceTransformerException
    {
        if (!TorqueSchemaElementName.INHERITANCE.getName().equals(
                inheritanceElement.getName()))
        {
            throw new IllegalArgumentException("Illegal element Name "
                    + inheritanceElement.getName());
        }
        String key = (String)
                inheritanceElement.getAttribute(TorqueSchemaAttributeName.KEY);
        if (key == null)
        {
            throw new IllegalArgumentException(
                    "Attribute \""
                            + TorqueSchemaAttributeName.KEY
                            + "\" not set in Element "
                            + TorqueSchemaElementName.INHERITANCE);
        }
        inheritanceElement.setAttribute(
                InheritanceAttributeName.CLASSKEY_CONSTANT,
                "CLASSKEY_" + key.toUpperCase());

        SourceElement tableElement
        = inheritanceElement.getParent().getParent();
        String className = (String) inheritanceElement.getAttribute(
                TorqueSchemaAttributeName.CLASS.getName());
        if (className == null)
        {
            className = (String) tableElement.getAttribute(
                    TableAttributeName.DB_OBJECT_CLASS_NAME);
        }
        inheritanceElement.setAttribute(
                InheritanceAttributeName.CLASS_NAME, className);
        
        String tablePackage = (String) tableElement.getAttribute(
                TableAttributeName.DB_OBJECT_PACKAGE);
          
        String thisElementPackage = (String) inheritanceElement.getAttribute(
                TorqueSchemaAttributeName.PACKAGE);

        String packageName = updatePackage( thisElementPackage, tablePackage );
        logger.debug( "key: {}: package: {} from package: {}, table package: {}",
                key,
                packageName,thisElementPackage, tablePackage );
        
        inheritanceElement.setAttribute(
                TorqueSchemaAttributeName.PACKAGE,
                packageName);
        {
            String extendsAttribute = (String) inheritanceElement.getAttribute(
                    TorqueSchemaAttributeName.EXTENDS.getName());
            String beanExtends = (String) inheritanceElement.getAttribute(
                    InheritanceAttributeName.BEAN_EXTENDS);
            if (extendsAttribute == null)
            { 
                extendsAttribute = (String) tableElement.getAttribute(
                        TableAttributeName.DB_OBJECT_CLASS_NAME);
                inheritanceElement.setAttribute(
                        TorqueSchemaAttributeName.EXTENDS.getName(), 
                                extendsAttribute);
                
                String beanClassName = tableElement.getAttribute(
                        TableAttributeName.BEAN_CLASS_NAME).toString();
                logger.debug( "key: {}: extends from default {} (not set in element) and beanClassName:{}..",
                        key, extendsAttribute, beanClassName );
                
                inheritanceElement.setAttribute(
                        InheritanceAttributeName.BEAN_EXTENDS,
                        beanClassName);
            }
            else if (beanExtends == null)
            {
                
                // we try to guess the extendsBean from extends
                // and the bean package if the element has to set its own package
                int lastDot = extendsAttribute.lastIndexOf(".");
                String unqualifiedClassname;

                // we  try to guess if the package is within the default table bean package
                String tableBeanPackage = (String) tableElement.getAttribute(
                        TableAttributeName.BEAN_PACKAGE);
                String extendPackage = extendsAttribute.substring(0, lastDot);
                
                String beanClassNameSuffix = controllerState.getOption(
                        "torque.om.className.beanClassNameSuffix").toString();
                
                if (extendPackage.equals( packageName ) && lastDot != -1 )
                {
                    unqualifiedClassname
                    = extendsAttribute.substring(lastDot + 1);
                }
                else if (lastDot == -1 ) // no package at all
                {
                    unqualifiedClassname = extendsAttribute;
                } else {
                    
                    unqualifiedClassname = extendsAttribute.substring(lastDot + 1);
                    
                    int withBeanPackage = extendPackage.lastIndexOf( tablePackage );
                    if (withBeanPackage != -1 && !extendPackage.equals( tablePackage )) {
                        extendPackage =  extendPackage.substring(tablePackage.length());
                        logger.debug( "key: {}: filtered extendPackage {} from table package: {} ",
                                key, extendPackage, tablePackage);
                        extendPackage = updatePackage( extendPackage, tableBeanPackage ) ;
                    } else {
                        extendPackage = tableBeanPackage ;   
                    }
                    extendPackage += ".";   
                
                    logger.debug( "key: {}: extendPackage: {} from table bean package: {} ",
                            key, 
                            extendPackage,
                            tableBeanPackage);
                    
                    String qualifiedClassname = 
                            extendPackage + unqualifiedClassname + beanClassNameSuffix;
                    
                    logger.info("key: {}: override qualifiedClassname (ignoring beanClassNamePrefix): {}", 
                            key, qualifiedClassname);
                    beanExtends = qualifiedClassname;
                }
   
                if (beanExtends == null) 
                {
                    String beanClassNamePrefix = controllerState.getOption(
                            "torque.om.className.beanClassNamePrefix").toString();   
                      
                    beanExtends = beanClassNamePrefix
                            + unqualifiedClassname
                            + beanClassNameSuffix;
                    logger.debug( "using extendsAttribute: {} with beanextends {}",
                            extendsAttribute, beanExtends);
                }
                inheritanceElement.setAttribute(
                        InheritanceAttributeName.BEAN_EXTENDS,
                        beanExtends);
            }
        }
        {
            int lastDot = className.lastIndexOf(".");
            String unqualifiedClassname;
            if (lastDot != -1)
            {
                unqualifiedClassname
                = className.substring(lastDot + 1);
            }
            else
            {
                unqualifiedClassname = className;
            }
            String beanClassName = controllerState.getOption(
                    "torque.om.className.beanClassNamePrefix")
                    + unqualifiedClassname
                    + controllerState.getOption(
                            "torque.om.className.beanClassNameSuffix");
            inheritanceElement.setAttribute(
                    InheritanceAttributeName.BEAN_CLASS_NAME,
                    beanClassName);

        }
        
        String tableBeanPackage = (String) tableElement.getAttribute(
                TableAttributeName.BEAN_PACKAGE);

        String beanPackage = updatePackage( thisElementPackage, tableBeanPackage, ".bean" );
        
        logger.debug( "key: {}: beanPackage {} from {}, {}", 
                key,beanPackage, thisElementPackage, tableBeanPackage );
        
        inheritanceElement.setAttribute(
                InheritanceAttributeName.BEAN_PACKAGE,
                beanPackage);
    }
    
    private String updatePackage(String thisBeanPackageName, String rootPackage)
    {
        return updatePackage(thisBeanPackageName, rootPackage, "");
    }

    private String updatePackage(String thisBeanPackageName, String rootPackage, String suffix)
    {
        String beanPackage =  rootPackage;
        
        if (thisBeanPackageName != null) 
        { 
            if (thisBeanPackageName.startsWith( "." ))
            {   // relative 
                beanPackage += thisBeanPackageName;
            } else {
                beanPackage = thisBeanPackageName + suffix;
            }
        }
        return beanPackage;
    }
}
