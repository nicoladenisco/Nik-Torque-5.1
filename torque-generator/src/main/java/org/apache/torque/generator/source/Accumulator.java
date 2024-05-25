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
package org.apache.torque.generator.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Accumulatore di stringhe per semplificare la fusione in velocity.
 *
 * @author Nicola De Nisco
 */
public class Accumulator extends ArrayList<Object>
   implements Serializable
{
  /**
   * Fonde tutti gli oggetti restituiti dall'iterator in una unica stringa
   * utilizzando il separatore specificato.
   * Di ogni oggetto restituito viene chiamato il metodo toString().
   * ES: join(",", "\"") restituisce "una","due","tre"
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di stringa (può essere null)
   * @return
   */
  public String join(String separator, String delimiter)
  {
    return join(iterator(), separator, delimiter);
  }

  /**
   * Fonde tutti gli oggetti restituiti dall'iterator in una unica stringa
   * utilizzando il separatore specificato.
   * Di ogni oggetto restituito viene chiamato il metodo toString().
   * ES: join(",") restituisce una,due,tre
   * @param separator carattere separatore fra le stringhe
   * @return
   */
  public String join(String separator)
  {
    return join(separator, null);
  }

  public String join(String separator, String delimiter, int min, int max)
  {
    return join(subList(min, max).iterator(), separator, delimiter);
  }

  public String join(String separator, int min, int max)
  {
    return join(subList(min, max).iterator(), separator, null);
  }

  /**
   * Fonde tutti gli oggetti restituiti dall'iterator in una unica stringa
   * utilizzando il separatore specificato.
   * Di ogni oggetto restituito viene chiamato il metodo toString().
   * ES: join(itrStringhe, ',') restituisce una,due,tre
   * @param itr iteratore generico
   * @param separator carattere separatore fra le stringhe
   * @param delimiter delimitatore di stringa (può essere null)
   * @return
   */
  public static String join(Iterator itr, String separator, String delimiter)
  {
    StringBuilder rv = new StringBuilder(512);
    for(int i = 0; itr.hasNext(); i++)
    {
      Object obj = itr.next();

      if(i > 0)
        rv.append(separator);

      if(delimiter != null)
        rv.append(delimiter);

      rv.append(obj.toString());

      if(delimiter != null)
        rv.append(delimiter);
    }

    return rv.toString();
  }
}
