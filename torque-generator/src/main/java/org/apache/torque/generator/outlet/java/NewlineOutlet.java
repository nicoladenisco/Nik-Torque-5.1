package org.apache.torque.generator.outlet.java;

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

import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;

/**
 * Creates new lines (\n or \r\n).
 */
public class NewlineOutlet extends OutletWithoutMergepoints
{
    /** The carriage return char '\r'. */
    public static final char CARRIAGE_RETURN = '\r';

    /** The newline char '\r'. */
    public static final char NEWLINE = '\n';

    /** How many new lines should be added; */
    private int count = 1;

    /** Whether windows Style ("\r\n") should be used instead of '\n'. */
    private boolean windowsStyle = false;

    /**
     * Constructor.
     *
     * @param name the qualified name of the outlet.
     */
    public NewlineOutlet(QualifiedName name)
    {
        super(name);
    }

    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        if (count < 0)
        {
            throw new GeneratorException("count must not be < 0");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; ++i)
        {
            if (windowsStyle)
            {
                result.append(CARRIAGE_RETURN);
            }
            result.append(NEWLINE);
        }
        return new OutletResult(result.toString());
    }

    /**
     * Returns how many newlines should be created.
     *
     * @return how many newlines should be created.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Sets how many newlines should be created.
     *
     * @param count how many newlines should be created.
     */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * Returns whether windows newlines (\r\n) are used.
     *
     * @return whether windows newlines are used.
     */
    public boolean isWindowsStyle()
    {
        return windowsStyle;
    }

    /**
     * Sets whether windows newlines (\r\n) are used.
     *
     * @param windowsStyle whether windows newlines should be used.
     */
    public void setWindowsStyle(boolean windowsStyle)
    {
        this.windowsStyle = windowsStyle;
    }
}
