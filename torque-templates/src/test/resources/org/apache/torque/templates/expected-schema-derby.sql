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
-- derby SQL script for schema bookstore
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


drop table a;
drop table b;
drop table b2;
drop table r_ab;
drop table c;
drop table d;
drop table nopk;
drop table MULTIPK_SELF_REFTABLE;
drop table ifc_table;
drop table ifc_table2;
drop table INHERITANCE_TEST;
drop table ext_schema;
drop table extext_schema;
drop table torque.qualified_name;

-- -----------------------------------------------------------------------
-- a
-- -----------------------------------------------------------------------
CREATE TABLE a
(
    a_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(50) default 'a_name' NOT NULL,
    contactdata VARCHAR(500) NOT NULL,
    created_at TIMESTAMP default CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    enum VARCHAR(50) default 'x',
    PRIMARY KEY(a_id)
);

CREATE INDEX NAME_IDX ON a (name);

-- -----------------------------------------------------------------------
-- b
-- -----------------------------------------------------------------------
CREATE TABLE b
(
    b_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY(b_id),
    CONSTRAINT NAME_UNQ UNIQUE(name)
);


-- -----------------------------------------------------------------------
-- b2
-- -----------------------------------------------------------------------
CREATE TABLE b2
(
    b2_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    b2_id_2 INTEGER,
    b2_name_1 VARCHAR(50) NOT NULL,
    b2_name_2 VARCHAR(20) NOT NULL,
    b2_name_3 VARCHAR(10) NOT NULL,
    b2_name_4 VARCHAR(10) NOT NULL,
    PRIMARY KEY(b2_id),
    CONSTRAINT NAME_UNQ UNIQUE(b2_name_1, b2_name_2, b2_name_3, b2_name_4)
);

-- -----------------------------------------------------------------------
-- r_ab
-- -----------------------------------------------------------------------
CREATE TABLE r_ab
(
    a_id INTEGER NOT NULL,
    b_id INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY(a_id, b_id)
);


-- -----------------------------------------------------------------------
-- c
-- -----------------------------------------------------------------------
CREATE TABLE c
(
    c_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    a_id INTEGER NOT NULL,
    b_id INTEGER NOT NULL,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY(c_id)
);


-- -----------------------------------------------------------------------
-- d
-- -----------------------------------------------------------------------
CREATE TABLE d
(
    d_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    b_id INTEGER NOT NULL,
    a_id INTEGER NOT NULL,
    PRIMARY KEY(d_id, b_id, a_id)
);


-- -----------------------------------------------------------------------
-- nopk
-- -----------------------------------------------------------------------
CREATE TABLE nopk
(
    intcol INTEGER,
    name VARCHAR(50)
);


-- -----------------------------------------------------------------------
-- MULTIPK_SELF_REFTABLE
-- -----------------------------------------------------------------------
CREATE TABLE MULTIPK_SELF_REFTABLE
(
    COL1 DECIMAL NOT NULL,
    COL2 DECIMAL NOT NULL,
    PARENT_COL1 DECIMAL NOT NULL,
    PARENT_COL2 DECIMAL NOT NULL,
    PRIMARY KEY(COL1, COL2)
);


-- -----------------------------------------------------------------------
-- ifc_table
-- -----------------------------------------------------------------------
CREATE TABLE ifc_table
(
    id INTEGER NOT NULL,
    name VARCHAR(50),
    PRIMARY KEY(id)
);


-- -----------------------------------------------------------------------
-- ifc_table2
-- -----------------------------------------------------------------------
CREATE TABLE ifc_table2
(
    id INTEGER NOT NULL,
    name VARCHAR(50),
    PRIMARY KEY(id)
);


-- -----------------------------------------------------------------------
-- INHERITANCE_TEST
-- -----------------------------------------------------------------------
CREATE TABLE INHERITANCE_TEST
(
    INHERITANCE_TEST INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    CLASS_NAME CHAR(1),
    PAYLOAD_PARENT VARCHAR(100) NOT NULL,
    PAYLOAD_B VARCHAR(100),
    PAYLOAD_C VARCHAR(100),
    PAYLOAD_D VARCHAR(100),
    PRIMARY KEY(INHERITANCE_TEST)
);


-- -----------------------------------------------------------------------
-- ext_schema
-- -----------------------------------------------------------------------
CREATE TABLE ext_schema
(
    ext_schema_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    ext_id INTEGER,
    test INTEGER NOT NULL,
    PRIMARY KEY(ext_schema_id)
);

CREATE INDEX index_ext_id ON ext_schema (ext_id);

-- -----------------------------------------------------------------------
-- extext_schema
-- -----------------------------------------------------------------------
CREATE TABLE extext_schema
(
    extext_schema_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    extext_id INTEGER,
    test INTEGER NOT NULL,
    PRIMARY KEY(extext_schema_id)
);


-- -----------------------------------------------------------------------
-- torque.qualified_name
-- -----------------------------------------------------------------------
CREATE TABLE torque.qualified_name
(
    id INTEGER,
    payload VARCHAR(100)
);

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

CREATE VIEW a_b AS
    SELECT
    a.a_id AS a_id,
    a.name AS a_name,
    b.b_id AS b_id,
    b.name AS b_name,
    a.enum AS enum
    from a join r_ab on a.a_id=r_ab.a_id join b on r_ab.b_id=b.b_id;


