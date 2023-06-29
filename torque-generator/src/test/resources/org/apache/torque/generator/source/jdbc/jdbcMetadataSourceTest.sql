-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.
--
-- -----------------------------------------------------------------------
-- derby SQL script for schema bookstore
-- -----------------------------------------------------------------------



-- -----------------------------------------------------------------------
-- book
-- -----------------------------------------------------------------------
CREATE TABLE book
(
    book_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    isbn VARCHAR(15),
    author_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL default 'no title',
    PRIMARY KEY(book_id));




-- -----------------------------------------------------------------------
-- author
-- -----------------------------------------------------------------------
CREATE TABLE author
(
    author_id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY,
    name VARCHAR(50) NOT NULL,
    PRIMARY KEY(author_id));




ALTER TABLE book
    ADD CONSTRAINT book_FK_1 
    FOREIGN KEY (author_id)
    REFERENCES author (author_id);


