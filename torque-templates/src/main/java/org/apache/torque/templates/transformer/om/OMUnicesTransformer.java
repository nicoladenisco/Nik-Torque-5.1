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
package org.apache.torque.templates.transformer.om;

import java.util.List;
import java.util.Set;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.source.SourceElement;
import org.apache.torque.generator.source.transform.SourceTransformerException;
import org.apache.torque.templates.TorqueSchemaElementName;

/**
 * Trasformazione per indici univoci.
 * Copia gli attributi di colonna dalla tabella alle colonne dell'indice.
 *
 * @author Nicola De Nisco
 */
public class OMUnicesTransformer
{
  public void transform(final SourceElement uniqueKey, final ControllerState controllerState)
     throws SourceTransformerException
  {
    if(!TorqueSchemaElementName.UNIQUE.getName().equals(uniqueKey.getName()))
      throw new IllegalArgumentException("Illegal element Name " + uniqueKey.getName());

    SourceElement tabella = uniqueKey.getParent();
    List<SourceElement> lsColonne = tabella.getChildren("column");
    List<SourceElement> lsUniqueCol = uniqueKey.getChildren("unique-column");

    StringBuilder sb1 = new StringBuilder();
    StringBuilder sb2 = new StringBuilder();

    for(SourceElement colonna : lsColonne)
    {
      String nomeColonna = (String) colonna.getAttribute("name");
      if(nomeColonna == null)
        continue;

      for(SourceElement ucol : lsUniqueCol)
      {
        if(nomeColonna.equals(ucol.getAttribute("name")))
        {
          Set<String> attributeNames = colonna.getAttributeNames();
          for(String attrname : attributeNames)
            ucol.setAttribute(attrname, colonna.getAttribute(attrname));

          if(sb1.length() != 0)
            sb1.append(',');
          sb1.append(colonna.getAttribute("fieldType")).append(' ');
          sb1.append("val_").append(nomeColonna);

          if(sb2.length() != 0)
            sb2.append(',');
          sb2.append("val_").append(nomeColonna);

          break;
        }
      }
    }

    uniqueKey.setAttribute("func-params", sb1.toString());
    uniqueKey.setAttribute("func-passtr", sb2.toString());
  }
}
