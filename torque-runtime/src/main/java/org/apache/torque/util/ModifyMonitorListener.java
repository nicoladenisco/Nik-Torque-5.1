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
package org.apache.torque.util;

import java.sql.Connection;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.torque.om.ObjectKey;

/**
 * Ascoltatore cambiamenti su tabella.
 *
 * @author Nicola De Nisco
 */
public interface ModifyMonitorListener
{
  /**
   * Notifica inserimento record.
   * Viene chiamata dopo l'inserimento di un nuovo record in tabella.
   * @param dbName nome del database
   * @param tableName nome della tabella
   * @param primaryKey primary key del record appena creato
   * @param insertValues valori inseriti nel record
   * @param con connessione sql
   * @throws TorqueException
   */
  public void doInsert(String dbName, String tableName, ObjectKey primaryKey, ColumnValues insertValues, Connection con)
     throws TorqueException;

  /**
   * Notifica aggiornamento record.
   * Viene chiamata dopo l'aggiornamento di un record in tabella.
   * @param dbName nome del database
   * @param tableName nome della tabella
   * @param selectCriteria valori per selezione record
   * @param updateValues valori aggiornati nel record
   * @param sql
   * @param valori
   * @param con connessione sql
   * @throws TorqueException
   */
  public void doUpdate(String dbName, String tableName, Criteria selectCriteria, ColumnValues updateValues,
     String sql, List<Object> valori, Connection con)
     throws TorqueException;

  /**
   * Notifica cancellazione record.
   * Viene chiamata dopo la cancellazione del record.
   * @param dbName nome del database
   * @param tableName nome della tabella
   * @param selectCriteria valori per selezione record
   * @param sql
   * @param replacements
   * @param con connessione sql
   * @throws TorqueException
   */
  public void doDelete(String dbName, String tableName, Criteria selectCriteria,
     String sql, List<Object> replacements, Connection con)
     throws TorqueException;
}
