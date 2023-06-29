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
-- oracle SQL script for schema bookstore
-- -----------------------------------------------------------------------


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


DROP TABLE a CASCADE CONSTRAINTS;
DROP SEQUENCE a_SEQ;
DROP TABLE b CASCADE CONSTRAINTS;
DROP SEQUENCE b_SEQ;
DROP TABLE b2 CASCADE CONSTRAINTS;
DROP SEQUENCE b2_SEQ;
DROP TABLE r_ab CASCADE CONSTRAINTS;
DROP TABLE c CASCADE CONSTRAINTS;
DROP SEQUENCE c_SEQ;
DROP TABLE d CASCADE CONSTRAINTS;
DROP TABLE nopk CASCADE CONSTRAINTS;
DROP TABLE MULTIPK_SELF_REFTABLE CASCADE CONSTRAINTS;
DROP TABLE ifc_table CASCADE CONSTRAINTS;
DROP TABLE ifc_table2 CASCADE CONSTRAINTS;
DROP TABLE INHERITANCE_TEST CASCADE CONSTRAINTS;
DROP SEQUENCE INHERITANCE_TEST_SEQ;
DROP TABLE ext_schema CASCADE CONSTRAINTS;
DROP SEQUENCE ext_schema_SEQ;
DROP TABLE extext_schema CASCADE CONSTRAINTS;
DROP SEQUENCE extext_schema_SEQ;
DROP TABLE torque.qualified_name CASCADE CONSTRAINTS;
DROP SEQUENCE torque.qualified_name_SEQ;

-- -----------------------------------------------------------------------
-- a
-- -----------------------------------------------------------------------
CREATE TABLE a
(
    a_id NUMBER(10,0) NOT NULL,
    name VARCHAR2(50 CHAR) default 'a_name' NOT NULL,
    contactdata VARCHAR2(500 CHAR) NOT NULL,
    created_at TIMESTAMP(6) default CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    enum VARCHAR2(50 CHAR) default 'x'
);

ALTER TABLE a
    ADD CONSTRAINT a_PK
    PRIMARY KEY(a_id);

CREATE  INDEX NAME_IDX ON a (name);

CREATE SEQUENCE a_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;


-- -----------------------------------------------------------------------
-- b
-- -----------------------------------------------------------------------
CREATE TABLE b
(
    b_id NUMBER(10,0) NOT NULL,
    name VARCHAR2(50 CHAR) NOT NULL,
    CONSTRAINT NAME_UNQ UNIQUE (name)
);

ALTER TABLE b
    ADD CONSTRAINT b_PK
    PRIMARY KEY(b_id);


CREATE SEQUENCE b_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;


-- -----------------------------------------------------------------------
-- b2
-- -----------------------------------------------------------------------
CREATE TABLE b2
(
    b2_id NUMBER(10,0) NOT NULL,
    b2_id_2 NUMBER(10,0),
    b2_name_1 VARCHAR2(50 CHAR) NOT NULL,
    b2_name_2 VARCHAR2(20 CHAR) NOT NULL,
    b2_name_3 VARCHAR2(10 CHAR) NOT NULL,
    b2_name_4 VARCHAR2(10 CHAR) NOT NULL,
    CONSTRAINT NAME_UNQ UNIQUE (b2_name_1, b2_name_2, b2_name_3, b2_name_4)
);

ALTER TABLE b2
    ADD CONSTRAINT b2_PK
    PRIMARY KEY(b2_id);


CREATE SEQUENCE b2_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;


-- -----------------------------------------------------------------------
-- r_ab
-- -----------------------------------------------------------------------
CREATE TABLE r_ab
(
    a_id NUMBER(10,0) NOT NULL,
    b_id NUMBER(10,0) NOT NULL,
    name VARCHAR2(50 CHAR) NOT NULL
);

ALTER TABLE r_ab
    ADD CONSTRAINT r_ab_PK
    PRIMARY KEY(a_id, b_id);




-- -----------------------------------------------------------------------
-- c
-- -----------------------------------------------------------------------
CREATE TABLE c
(
    c_id NUMBER(10,0) NOT NULL,
    a_id NUMBER(10,0) NOT NULL,
    b_id NUMBER(10,0) NOT NULL,
    name VARCHAR2(50 CHAR) NOT NULL
);

ALTER TABLE c
    ADD CONSTRAINT c_PK
    PRIMARY KEY(c_id);


CREATE SEQUENCE c_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;


-- -----------------------------------------------------------------------
-- d
-- -----------------------------------------------------------------------
CREATE TABLE d
(
    d_id NUMBER(10,0) NOT NULL,
    name VARCHAR2(255 CHAR) NOT NULL,
    b_id NUMBER(10,0) NOT NULL,
    a_id NUMBER(10,0) NOT NULL
);

ALTER TABLE d
    ADD CONSTRAINT d_PK
    PRIMARY KEY(d_id, b_id, a_id);




-- -----------------------------------------------------------------------
-- nopk
-- -----------------------------------------------------------------------
CREATE TABLE nopk
(
    intcol NUMBER(10,0),
    name VARCHAR2(50 CHAR)
);





-- -----------------------------------------------------------------------
-- MULTIPK_SELF_REFTABLE
-- -----------------------------------------------------------------------
CREATE TABLE MULTIPK_SELF_REFTABLE
(
    COL1 NUMBER NOT NULL,
    COL2 NUMBER NOT NULL,
    PARENT_COL1 NUMBER NOT NULL,
    PARENT_COL2 NUMBER NOT NULL
);

ALTER TABLE MULTIPK_SELF_REFTABLE
    ADD CONSTRAINT MULTIPK_SELF_REFTABLE_PK
    PRIMARY KEY(COL1, COL2);




-- -----------------------------------------------------------------------
-- ifc_table
-- -----------------------------------------------------------------------
CREATE TABLE ifc_table
(
    id NUMBER(10,0) NOT NULL,
    name VARCHAR2(50 CHAR)
);

ALTER TABLE ifc_table
    ADD CONSTRAINT ifc_table_PK
    PRIMARY KEY(id);




-- -----------------------------------------------------------------------
-- ifc_table2
-- -----------------------------------------------------------------------
CREATE TABLE ifc_table2
(
    id NUMBER(10,0) NOT NULL,
    name VARCHAR2(50 CHAR)
);

ALTER TABLE ifc_table2
    ADD CONSTRAINT ifc_table2_PK
    PRIMARY KEY(id);




-- -----------------------------------------------------------------------
-- INHERITANCE_TEST
-- -----------------------------------------------------------------------
CREATE TABLE INHERITANCE_TEST
(
    INHERITANCE_TEST NUMBER(10,0) NOT NULL,
    CLASS_NAME CHAR(1),
    PAYLOAD_PARENT VARCHAR2(100 CHAR) NOT NULL,
    PAYLOAD_B VARCHAR2(100 CHAR),
    PAYLOAD_C VARCHAR2(100 CHAR),
    PAYLOAD_D VARCHAR2(100 CHAR)
);

ALTER TABLE INHERITANCE_TEST
    ADD CONSTRAINT INHERITANCE_TEST_PK
    PRIMARY KEY(INHERITANCE_TEST);


CREATE SEQUENCE INHERITANCE_TEST_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;


-- -----------------------------------------------------------------------
-- ext_schema
-- -----------------------------------------------------------------------
CREATE TABLE ext_schema
(
    ext_schema_id NUMBER(10,0) NOT NULL,
    ext_id NUMBER(10,0),
    test NUMBER(10,0) NOT NULL
);

ALTER TABLE ext_schema
    ADD CONSTRAINT ext_schema_PK
    PRIMARY KEY(ext_schema_id);

CREATE  INDEX index_ext_id ON ext_schema (ext_id);

CREATE SEQUENCE ext_schema_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;


-- -----------------------------------------------------------------------
-- extext_schema
-- -----------------------------------------------------------------------
CREATE TABLE extext_schema
(
    extext_schema_id NUMBER(10,0) NOT NULL,
    extext_id NUMBER(10,0),
    test NUMBER(10,0) NOT NULL
);

ALTER TABLE extext_schema
    ADD CONSTRAINT extext_schema_PK
    PRIMARY KEY(extext_schema_id);


CREATE SEQUENCE extext_schema_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;


-- -----------------------------------------------------------------------
-- torque.qualified_name
-- -----------------------------------------------------------------------
CREATE TABLE torque.qualified_name
(
    id NUMBER(10,0),
    payload VARCHAR2(100 CHAR)
);



CREATE SEQUENCE torque.qualified_name_SEQ INCREMENT BY 1 START WITH 1 NOMAXVALUE NOCYCLE NOCACHE ORDER;

ALTER TABLE r_ab
    ADD CONSTRAINT r_ab_FK_1
    FOREIGN KEY (a_id)
    REFERENCES a (a_id);

ALTER TABLE r_ab
    ADD CONSTRAINT r_ab_FK_2
    FOREIGN KEY (b_id)
    REFERENCES b (b_id);

ALTER TABLE c
    ADD CONSTRAINT c_FK_1
    FOREIGN KEY (a_id, b_id)
    REFERENCES r_ab (a_id, b_id);

ALTER TABLE c
    ADD CONSTRAINT c_FK_2
    FOREIGN KEY (a_id)
    REFERENCES a (a_id);

ALTER TABLE c
    ADD CONSTRAINT c_FK_3
    FOREIGN KEY (b_id)
    REFERENCES b (b_id);

ALTER TABLE d
    ADD CONSTRAINT d_FK_1
    FOREIGN KEY (a_id)
    REFERENCES a (a_id);

ALTER TABLE d
    ADD CONSTRAINT d_FK_2
    FOREIGN KEY (a_id, b_id)
    REFERENCES r_ab (a_id, b_id);

ALTER TABLE MULTIPK_SELF_REFTABLE
    ADD CONSTRAINT MULTIPK_SELF_REFTABLE_FK_1
    FOREIGN KEY (PARENT_COL1, PARENT_COL2)
    REFERENCES MULTIPK_SELF_REFTABLE (COL1, COL2);

ALTER TABLE ext_schema
    ADD CONSTRAINT ext_schema_FK_1
    FOREIGN KEY (ext_id)
    REFERENCES ext (ext_id);

ALTER TABLE extext_schema
    ADD CONSTRAINT extext_schema_FK_1
    FOREIGN KEY (extext_id)
    REFERENCES extext (extext_id);

ALTER TABLE a
    ADD CONSTRAINT enum_CHECK
    CHECK
    (enum in ('x', 'y', 'z'));

CREATE OR REPLACE VIEW a_b AS
    SELECT
    a.a_id AS a_id,
    a.name AS a_name,
    b.b_id AS b_id,
    b.name AS b_name,
    a.enum AS enum
    from a join r_ab on a.a_id=r_ab.a_id join b on r_ab.b_id=b.b_id;

COMMENT ON TABLE a IS 'A table';


COMMENT ON TABLE b IS 'B table';


COMMENT ON TABLE b2 IS 'B2 table';


COMMENT ON TABLE r_ab IS 'r_ab table';


COMMENT ON TABLE c IS 'c table contains fk to ar_ab, a and b';


COMMENT ON TABLE nopk IS 'this table has no pk';


COMMENT ON TABLE ifc_table IS 'this table implements an interface';


COMMENT ON TABLE ifc_table2 IS 'this table implements a local interface';


COMMENT ON TABLE INHERITANCE_TEST IS 'Table to test inheritance';



