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
import org.apache.torque.generator.template.groovy.TorqueGenGroovy
import org.apache.torque.templates.model.View

TorqueGenGroovy torqueGenGroovy = (TorqueGenGroovy) torqueGen
View view = torqueGenGroovy.model
if (!view.skipSql)
{
    if (view.createSql != null)
    { 
        return view.createSql + """

"""
    }
    else 
    { 
        String cols = torqueGenGroovy.mergepoint("columns")
        int lastCommaPos = cols.lastIndexOf(",")
        if (lastCommaPos != -1) 
        { 
            cols = cols.substring(0, lastCommaPos) 
        }
        
        return """CREATE VIEW ${view.name} AS
    SELECT
${cols}
    ${view.sqlSuffix};

"""
    }
}
