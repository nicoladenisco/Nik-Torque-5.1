#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
FROM postgres:13.1

COPY ./generated-sql-init /tmp-sql
# dependent sql should be name sorted after id-table-schema.sql
# add what you need, avoid duplicate keys
RUN cat /tmp-sql/bookstore-schema-idtable-init.sql /tmp-sql/foreign-key-schema-idtable-init.sql /tmp-sql/test-schema-idtable-init.sql /tmp-sql/types-schema-idtable-init.sql /tmp-sql/default-value-schema-idtable-init.sql > /docker-entrypoint-initdb.d/id-table-sum-init.sql
COPY [ "./generated-sql/*", "/docker-entrypoint-initdb.d/" ]

ENV POSTGRES_DB=${POSTGRES_DB}
ENV POSTGRES_USER=${POSTGRES_USER}
ENV POSTGRES_PASSWORD=${POSTGRES_PASSWORD}