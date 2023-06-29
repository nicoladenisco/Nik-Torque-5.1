<?xml version="1.0" encoding="ISO-8859-1"?>

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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" doctype-system="http://db.apache.org/torque/dtd/database_3_2.dtd"/>

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="DBMODEL">
		<database>
			<xsl:attribute name="name">
				<xsl:value-of select="SETTINGS/GLOBALSETTINGS/@ModelName"/>
			</xsl:attribute>
			<xsl:apply-templates/>
		</database>
	</xsl:template>

	<xsl:template match="METADATA">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="TABLES">
		<xsl:for-each select="TABLE[@Tablename]">
			<table>
				<xsl:attribute name="name">
					<xsl:value-of select="@Tablename"/>
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="@nmTable = 1">
						<xsl:attribute name="idMethod">none</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="idMethod">native</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates/>
			</table>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="COLUMNS">
		<xsl:for-each select="COLUMN[@ColName]">
			<column>
				<xsl:attribute name="name">
					<xsl:value-of select="@ColName"/>
				</xsl:attribute>
				<xsl:if test="@PrimaryKey = 1">
					<xsl:attribute name="primaryKey">true</xsl:attribute>
				</xsl:if>
				<xsl:if test="@NotNull = 1">
					<xsl:attribute name="required">true</xsl:attribute>
				</xsl:if>
				<xsl:if test="@AutoInc = 1">
					<xsl:attribute name="autoIncrement">true</xsl:attribute>
				</xsl:if>
				<xsl:if test="@idDatatype = 20">
					<!-- VARCHAR -->
					<xsl:attribute name="size">
						<xsl:value-of select="substring-after(substring-before(@DatatypeParams, ')'),'(')"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="type">
					<xsl:variable name="d" select="@idDatatype"/>
					<xsl:for-each select="//DATATYPE[@ID=$d]">
						<xsl:choose>
							<xsl:when test="@PhysicalMapping = 1">
								<xsl:choose>
									<xsl:when test="contains(@PhysicalTypeName, 'BOOL')">BOOLEANINT</xsl:when>
									<xsl:when test="contains(@PhysicalTypeName, 'DATETIME')">TIMESTAMP</xsl:when>
									<xsl:when test="contains(@PhysicalTypeName, 'MEDIUMTEXT')">LONGVARCHAR</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@PhysicalTypeName"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="contains(@TypeName, 'BOOL')">BOOLEANINT</xsl:when>
									<xsl:when test="contains(@TypeName, 'DATETIME')">TIMESTAMP</xsl:when>
									<xsl:when test="contains(@TypeName, 'MEDIUMTEXT')">LONGVARCHAR</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@TypeName"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</xsl:attribute>
				<xsl:if test="string-length(@DefaultValue) &gt; 0">
					<xsl:attribute name="default">
						<xsl:value-of select="@DefaultValue"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates/>
			</column>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="RELATIONS_END">
		<xsl:for-each select="RELATION_END[@ID]">
			<foreign-key>
				<xsl:variable name="id" select="@ID"/>
				<xsl:for-each select="//RELATION[@ID=$id]">
					<xsl:variable name="t" select="@SrcTable"/>
					<xsl:for-each select="//TABLE[@ID=$t]">
						<xsl:attribute name="foreignTable">
							<xsl:value-of select="@Tablename"/>
						</xsl:attribute>
					</xsl:for-each>
					<xsl:if test="@CreateRefDef = 1">
						<xsl:attribute name="onUpdate">
							<xsl:choose>
								<xsl:when test="contains(@RefDef, 'OnUpdate=0')">restrict</xsl:when>
								<xsl:when test="contains(@RefDef, 'OnUpdate=1')">cascade</xsl:when>
								<xsl:when test="contains(@RefDef, 'OnUpdate=2')">setnull</xsl:when>
								<xsl:otherwise>none</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="onDelete">
							<xsl:choose>
								<xsl:when test="contains(@RefDef, 'OnDelete=0')">restrict</xsl:when>
								<xsl:when test="contains(@RefDef, 'OnDelete=1')">cascade</xsl:when>
								<xsl:when test="contains(@RefDef, 'OnDelete=2')">setnull</xsl:when>
								<xsl:otherwise>none</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
					</xsl:if>
					<reference>
						<xsl:attribute name="local">
							<xsl:value-of select="substring-after(substring-before(@FKFields, '\'), '=')"/>
						</xsl:attribute>
						<xsl:attribute name="foreign">
							<xsl:value-of select="substring-before(@FKFields,'=')"/>
						</xsl:attribute>
					</reference>
				</xsl:for-each>
				<xsl:apply-templates/>
			</foreign-key>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="INDICES">
		<xsl:for-each select="INDEX[@ID]">
			<xsl:choose>
				<xsl:when test="@IndexKind = 1">
					<index>
						<xsl:attribute name="name">
							<xsl:value-of select="@IndexName"/>
						</xsl:attribute>
						<xsl:for-each select="INDEXCOLUMNS/INDEXCOLUMN[@idColumn]">
						<index-column>
							<xsl:variable name="c" select="@idColumn"/>
							<xsl:for-each select="//COLUMN[@ID=$c]">
								<xsl:attribute name="name">
									<xsl:value-of select="@ColName"/>
								</xsl:attribute>
							</xsl:for-each>
						</index-column>
						</xsl:for-each>
					</index>
				</xsl:when>
				<xsl:when test="@IndexKind = 2">
					<unique>
						<xsl:attribute name="name">
							<xsl:value-of select="@IndexName"/>
						</xsl:attribute>
						<xsl:for-each select="INDEXCOLUMNS/INDEXCOLUMN[@idColumn]">
						<unique-column>
							<xsl:variable name="c" select="@idColumn"/>
							<xsl:for-each select="//COLUMN[@ID=$c]">
								<xsl:attribute name="name">
									<xsl:value-of select="@ColName"/>
								</xsl:attribute>
							</xsl:for-each>
						</unique-column>
						</xsl:for-each>
					</unique>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>