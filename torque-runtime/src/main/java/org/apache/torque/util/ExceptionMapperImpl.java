package org.apache.torque.util;

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

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.torque.ConstraintViolationException;
import org.apache.torque.DeadlockException;
import org.apache.torque.TorqueException;

/**
 * Default implementation of the ExceptionMapper interface.
 * @version $Id: ExceptionMapperImpl.java 1867515 2019-09-25 15:02:03Z gk $
 */
public class ExceptionMapperImpl extends ExceptionMapper
{
    /** ORA-00060: Deadlock detected while waiting for resource. */
    private static final int ORACLE_DEADLOCK_ERROR_CODE = 60;

    @Override
    public TorqueException toTorqueException(final SQLException sqlException)
    {
        if (StringUtils.startsWith(sqlException.getSQLState(), "23"))
        {
            return new ConstraintViolationException(sqlException);
        }
        if (StringUtils.equals(sqlException.getSQLState(), "40001"))
        {
            // mysql, derby, mssql
            return new DeadlockException(sqlException);
        }
        if (StringUtils.equals(sqlException.getSQLState(), "40P01"))
        {
            // postgresql
            return new DeadlockException(sqlException);
        }
        if (StringUtils.equals(sqlException.getSQLState(), "61000")
                && sqlException.getErrorCode() == ORACLE_DEADLOCK_ERROR_CODE)
        {
            // oracle
            return new DeadlockException(sqlException);
        }
        return new TorqueException(sqlException);
    }
}
