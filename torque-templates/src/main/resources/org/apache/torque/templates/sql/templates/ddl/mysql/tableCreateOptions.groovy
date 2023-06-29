// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License. 
//
//////////
//
// version $Id: tableCreateOptions.vm 1437184 2013-01-22 21:25:57Z tfischer $
//
// Creates the options for table creation.
//
org.apache.torque.templates.model.Table table = torqueGen.model
for (org.apache.torque.templates.model.Option option : table.optionList)
{
  if (option.key == "ENGINE" 
        || option.key == "AVG_ROW_LENGTH"
        || option.key == "CHARACTER SET"
        || option.key == "DEFAULT CHARACTER SET"
        || option.key == "CHECKSUM"
        || option.key == "COLLATE"
        || option.key == "DEFAULT COLLATE"
        || option.key == "CONNECTION"
        || option.key == "DATA DIRECTORY"
        || option.key == "DELAY_KEY_WRITE"
        || option.key == "INDEX DIRECTORY"
        || option.key == "INSERT_METHOD"
        || option.key == "KEY_BLOCK_SIZE"
        || option.key == "MAX_ROWS"
        || option.key == "MIN_ROWS"
        || option.key == "PACK_KEYS"
        || option.key == "PASSWORD"
        || option.key == "ROW_FORMAT"
        || option.key == "TABLESPACE"
        || option.key == "UNION") 
  {
    return " $option.key=$option.value"
  }
  else if (option.key == "PARTITION BY") 
  { 
    return " $option.key $option.value"
  }
}
