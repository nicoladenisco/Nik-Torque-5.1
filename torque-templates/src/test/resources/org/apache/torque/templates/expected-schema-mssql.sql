## Licensed to the Apache Software Foundation (ASF) under one
## or more contributor license agreements.  See the NOTICE file
## distributed with this work for additional information
## regarding copyright ownership.  The ASF licenses this file
## to you under the Apache License, Version 2.0 (the
## "License"); you may not use this file except in compliance
## with the License.  You may obtain a copy of the License at
##
##   http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing,
## software distributed under the License is distributed on an
## "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
## KIND, either express or implied.  See the License for the
## specific language governing permissions and limitations
## under the License.
##
-- -----------------------------------------------------------------------
-- mssql SQL script for schema bookstore
-- -----------------------------------------------------------------------


DROP VIEW a_b;

ALTER TABLE r_ab
    DROP CONSTRAINT r_ab_FK_1;


ALTER TABLE r_ab
    DROP CONSTRAINT r_ab_FK_2;


ALTER TABLE c
    DROP CONSTRAINT c_FK_1;


ALTER TABLE c
    DROP CONSTRAINT c_FK_2;


ALTER TABLE c
    DROP CONSTRAINT c_FK_3;


ALTER TABLE d
    DROP CONSTRAINT d_FK_1;


ALTER TABLE d
    DROP CONSTRAINT d_FK_2;


ALTER TABLE MULTIPK_SELF_REFTABLE
    DROP CONSTRAINT MULTIPK_SELF_REFTABLE_FK_1;


ALTER TABLE ext_schema
    DROP CONSTRAINT ext_schema_FK_1;


ALTER TABLE extext_schema
    DROP CONSTRAINT extext_schema_FK_1;


IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'a')
BEGIN
     DECLARE @reftable_3 nvarchar(60), @constraintname_3 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'a'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_3+' drop constraint '+@constraintname_3)
       FETCH NEXT from refcursor into @reftable_3, @constraintname_3
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE a
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'b')
BEGIN
     DECLARE @reftable_4 nvarchar(60), @constraintname_4 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'b'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_4+' drop constraint '+@constraintname_4)
       FETCH NEXT from refcursor into @reftable_4, @constraintname_4
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE b
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'b2')
BEGIN
     DECLARE @reftable_5 nvarchar(60), @constraintname_5 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'b2'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_5+' drop constraint '+@constraintname_5)
       FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE b2
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'r_ab')
BEGIN
     DECLARE @reftable_5 nvarchar(60), @constraintname_5 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'r_ab'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_5+' drop constraint '+@constraintname_5)
       FETCH NEXT from refcursor into @reftable_5, @constraintname_5
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE r_ab
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'c')
BEGIN
     DECLARE @reftable_6 nvarchar(60), @constraintname_6 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'c'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_6+' drop constraint '+@constraintname_6)
       FETCH NEXT from refcursor into @reftable_6, @constraintname_6
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE c
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'd')
BEGIN
     DECLARE @reftable_7 nvarchar(60), @constraintname_7 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'd'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_7+' drop constraint '+@constraintname_7)
       FETCH NEXT from refcursor into @reftable_7, @constraintname_7
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE d
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'nopk')
BEGIN
     DECLARE @reftable_8 nvarchar(60), @constraintname_8 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'nopk'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_8+' drop constraint '+@constraintname_8)
       FETCH NEXT from refcursor into @reftable_8, @constraintname_8
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE nopk
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'MULTIPK_SELF_REFTABLE')
BEGIN
     DECLARE @reftable_9 nvarchar(60), @constraintname_9 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'MULTIPK_SELF_REFTABLE'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_9+' drop constraint '+@constraintname_9)
       FETCH NEXT from refcursor into @reftable_9, @constraintname_9
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE MULTIPK_SELF_REFTABLE
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'ifc_table')
BEGIN
     DECLARE @reftable_10 nvarchar(60), @constraintname_10 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'ifc_table'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_10+' drop constraint '+@constraintname_10)
       FETCH NEXT from refcursor into @reftable_10, @constraintname_10
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE ifc_table
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'ifc_table2')
BEGIN
     DECLARE @reftable_11 nvarchar(60), @constraintname_11 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'ifc_table2'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_11+' drop constraint '+@constraintname_11)
       FETCH NEXT from refcursor into @reftable_11, @constraintname_11
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE ifc_table2
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'INHERITANCE_TEST')
BEGIN
     DECLARE @reftable_12 nvarchar(60), @constraintname_12 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'INHERITANCE_TEST'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_12+' drop constraint '+@constraintname_12)
       FETCH NEXT from refcursor into @reftable_12, @constraintname_12
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE INHERITANCE_TEST
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'ext_schema')
BEGIN
     DECLARE @reftable_13 nvarchar(60), @constraintname_13 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'ext_schema'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_13+' drop constraint '+@constraintname_13)
       FETCH NEXT from refcursor into @reftable_13, @constraintname_13
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE ext_schema
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'extext_schema')
BEGIN
     DECLARE @reftable_14 nvarchar(60), @constraintname_14 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'extext_schema'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_14, @constraintname_14
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_14+' drop constraint '+@constraintname_14)
       FETCH NEXT from refcursor into @reftable_14, @constraintname_14
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE extext_schema
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'qualified_name')
BEGIN
     DECLARE @reftable_15 nvarchar(60), @constraintname_15 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'torque.qualified_name'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_15, @constraintname_15
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_15+' drop constraint '+@constraintname_15)
       FETCH NEXT from refcursor into @reftable_15, @constraintname_15
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE torque.qualified_name
END
;
IF EXISTS (SELECT 1 FROM sysobjects WHERE type = 'U' AND name = 'qualified_name')
BEGIN
     DECLARE @reftable_16 nvarchar(60), @constraintname_16 nvarchar(60)
     DECLARE refcursor CURSOR FOR
     select reftables.name tablename, cons.name constraintname
      from sysobjects tables,
           sysobjects reftables,
           sysobjects cons,
           sysreferences ref
       where tables.id = ref.rkeyid
         and cons.id = ref.constid
         and reftables.id = ref.fkeyid
         and tables.name = 'torque.qualified_name'
     OPEN refcursor
     FETCH NEXT from refcursor into @reftable_16, @constraintname_16
     while @@FETCH_STATUS = 0
     BEGIN
       exec ('alter table '+@reftable_16+' drop constraint '+@constraintname_16)
       FETCH NEXT from refcursor into @reftable_16, @constraintname_16
     END
     CLOSE refcursor
     DEALLOCATE refcursor
     DROP TABLE torque.qualified_name
END
;

DROP SCHEMA torque;

CREATE SCHEMA torque;

/* ----------------------------------------------------------------------- */
/* a                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE a
(
    a_id INT NOT NULL IDENTITY,
    name VARCHAR(50) default 'a_name' NOT NULL,
    contactdata VARCHAR(500) NOT NULL,
    created_at DATETIME(6) default CURRENT_TIMESTAMP NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    enum VARCHAR(50) default 'x' NULL,
    CONSTRAINT a_PK PRIMARY KEY(a_id)
)

;
CREATE INDEX NAME_IDX ON a (name);


/* ----------------------------------------------------------------------- */
/* b                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE b
(
    b_id INT NOT NULL IDENTITY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT b_PK PRIMARY KEY(b_id),
    CONSTRAINT NAME_UNQ UNIQUE (name)
)

;
/* ----------------------------------------------------------------------- */
/* b2                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE b2
(
    b2_id INT NOT NULL IDENTITY,
    b2_id_2 INT NULL,
    b2_name_1 VARCHAR(50) NOT NULL,
    b2_name_2 VARCHAR(20) NOT NULL,
    b2_name_3 VARCHAR(10) NOT NULL,
    b2_name_4 VARCHAR(10) NOT NULL,
    CONSTRAINT b2_PK PRIMARY KEY(b2_id),
    CONSTRAINT NAME_UNQ UNIQUE (b2_name_1, b2_name_2, b2_name_3, b2_name_4)
)

;


/* ----------------------------------------------------------------------- */
/* r_ab                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE r_ab
(
    a_id INT NOT NULL,
    b_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT r_ab_PK PRIMARY KEY(a_id, b_id)
)

;


/* ----------------------------------------------------------------------- */
/* c                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE c
(
    c_id INT NOT NULL IDENTITY,
    a_id INT NOT NULL,
    b_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT c_PK PRIMARY KEY(c_id)
)

;


/* ----------------------------------------------------------------------- */
/* d                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE d
(
    d_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    b_id INT NOT NULL,
    a_id INT NOT NULL,
    CONSTRAINT d_PK PRIMARY KEY(d_id, b_id, a_id)
)

;


/* ----------------------------------------------------------------------- */
/* nopk                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE nopk
(
    intcol INT NULL,
    name VARCHAR(50) NULL
)

;


/* ----------------------------------------------------------------------- */
/* MULTIPK_SELF_REFTABLE                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE MULTIPK_SELF_REFTABLE
(
    COL1 DECIMAL NOT NULL,
    COL2 DECIMAL NOT NULL,
    PARENT_COL1 DECIMAL NOT NULL,
    PARENT_COL2 DECIMAL NOT NULL,
    CONSTRAINT MULTIPK_SELF_REFTABLE_PK PRIMARY KEY(COL1, COL2)
)

;


/* ----------------------------------------------------------------------- */
/* ifc_table                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE ifc_table
(
    id INT NOT NULL,
    name VARCHAR(50) NULL,
    CONSTRAINT ifc_table_PK PRIMARY KEY(id)
)

;


/* ----------------------------------------------------------------------- */
/* ifc_table2                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE ifc_table2
(
    id INT NOT NULL,
    name VARCHAR(50) NULL,
    CONSTRAINT ifc_table2_PK PRIMARY KEY(id)
)

;


/* ----------------------------------------------------------------------- */
/* INHERITANCE_TEST                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE INHERITANCE_TEST
(
    INHERITANCE_TEST INT NOT NULL IDENTITY,
    CLASS_NAME CHAR(1) NULL,
    PAYLOAD_PARENT VARCHAR(100) NOT NULL,
    PAYLOAD_B VARCHAR(100) NULL,
    PAYLOAD_C VARCHAR(100) NULL,
    PAYLOAD_D VARCHAR(100) NULL,
    CONSTRAINT INHERITANCE_TEST_PK PRIMARY KEY(INHERITANCE_TEST)
)

;


/* ----------------------------------------------------------------------- */
/* ext_schema                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE ext_schema
(
    ext_schema_id INT NOT NULL IDENTITY,
    ext_id INT NULL,
    test INT NOT NULL,
    CONSTRAINT ext_schema_PK PRIMARY KEY(ext_schema_id)
)

;
CREATE INDEX index_ext_id ON ext_schema (ext_id);


/* ----------------------------------------------------------------------- */
/* extext_schema                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE extext_schema
(
    extext_schema_id INT NOT NULL IDENTITY,
    extext_id INT NULL,
    test INT NOT NULL,
    CONSTRAINT extext_schema_PK PRIMARY KEY(extext_schema_id)
)

;


/* ----------------------------------------------------------------------- */
/* torque.qualified_name                                                */
/* ----------------------------------------------------------------------- */
CREATE TABLE torque.qualified_name
(
    id INT NULL,
    payload VARCHAR(100) NULL
)

;

BEGIN
ALTER TABLE r_ab
    ADD CONSTRAINT r_ab_FK_1
    FOREIGN KEY (a_id)
    REFERENCES a (a_id)

END
;
BEGIN
ALTER TABLE r_ab
    ADD CONSTRAINT r_ab_FK_2
    FOREIGN KEY (b_id)
    REFERENCES b (b_id)

END
;
BEGIN
ALTER TABLE c
    ADD CONSTRAINT c_FK_1
    FOREIGN KEY (a_id, b_id)
    REFERENCES r_ab (a_id, b_id)

END
;
BEGIN
ALTER TABLE c
    ADD CONSTRAINT c_FK_2
    FOREIGN KEY (a_id)
    REFERENCES a (a_id)

END
;
BEGIN
ALTER TABLE c
    ADD CONSTRAINT c_FK_3
    FOREIGN KEY (b_id)
    REFERENCES b (b_id)

END
;
BEGIN
ALTER TABLE d
    ADD CONSTRAINT d_FK_1
    FOREIGN KEY (a_id)
    REFERENCES a (a_id)

END
;
BEGIN
ALTER TABLE d
    ADD CONSTRAINT d_FK_2
    FOREIGN KEY (a_id, b_id)
    REFERENCES r_ab (a_id, b_id)

END
;
BEGIN
ALTER TABLE MULTIPK_SELF_REFTABLE
    ADD CONSTRAINT MULTIPK_SELF_REFTABLE_FK_1
    FOREIGN KEY (PARENT_COL1, PARENT_COL2)
    REFERENCES MULTIPK_SELF_REFTABLE (COL1, COL2)

END
;
BEGIN
ALTER TABLE ext_schema
    ADD CONSTRAINT ext_schema_FK_1
    FOREIGN KEY (ext_id)
    REFERENCES ext (ext_id)

END
;
BEGIN
ALTER TABLE extext_schema
    ADD CONSTRAINT extext_schema_FK_1
    FOREIGN KEY (extext_id)
    REFERENCES extext (extext_id)

END
;
ALTER TABLE a
    ADD CONSTRAINT enum_CHECK
    CHECK
    (enum in ('x', 'y', 'z'));

CREATE VIEW a_b AS
    SELECT
    a.a_id AS a_id,
    a.name AS a_name,
    b.b_id AS b_id,
    b.name AS b_name,
    a.enum AS enum
    from a join r_ab on a.a_id=r_ab.a_id join b on r_ab.b_id=b.b_id;


