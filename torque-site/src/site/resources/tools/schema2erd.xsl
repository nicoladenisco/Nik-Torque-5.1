<?xml version="1.0" encoding="UTF-8" ?>

<!--
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
-->
<!--
    Document   : schema2erd.xsl
    Created on : 7. September 2004, 13:12
    Author     : carlptr

-->

<!--
    Document   : schema2erd.xsl
    Created on : 7. September 2004, 13:12
    Author     : carlptr

-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <xsl:template match="database">
        <ERDesignerModel version="0.9.2">
            
            <Entities>
                <xsl:apply-templates select="table"/>
            </Entities>
            <Relations>
                <xsl:for-each select="table/foreign-key">
                    <xsl:call-template name="f-key"/>
                </xsl:for-each>
            </Relations>
        </ERDesignerModel>
    </xsl:template>

    
    <xsl:template match="table">
        <Entity>
            <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:attribute name="comment"><xsl:value-of select="@description"/></xsl:attribute>
            <xsl:apply-templates/>
        </Entity>
    </xsl:template>
    
    <xsl:template match="column">
        <Attribute>
            <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:attribute name="isrequired"><xsl:value-of select="@required"/></xsl:attribute>
            <xsl:attribute name="isprimarykey"><xsl:value-of select="@primaryKey"/></xsl:attribute>
            <xsl:attribute name="defaultvalue"><xsl:value-of select="@default"/></xsl:attribute>
            <xsl:attribute name="comment"><xsl:value-of select="@description"/></xsl:attribute>
        </Attribute>
    </xsl:template>
    
    <xsl:template name="f-key">
        <Relation>
            <xsl:attribute name="type">non-identifying</xsl:attribute>
            <xsl:attribute name="delete-rule">DATABASE_DEFAULT</xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:attribute name="primary"><xsl:value-of select="@foreignTable"/></xsl:attribute>
            <xsl:attribute name="secondary"><xsl:value-of select="parent::table/@name"/></xsl:attribute>
            <Mapping>
                <xsl:attribute name="primary"><xsl:value-of select="reference/@foreign"/></xsl:attribute>
                <xsl:attribute name="secondary"><xsl:value-of select="reference/@local"/></xsl:attribute>                
            </Mapping>
        </Relation>
    </xsl:template>
</xsl:stylesheet>
