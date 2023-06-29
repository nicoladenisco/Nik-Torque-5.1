package org.apache.torque.generator.java;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.generator.GeneratorException;
import org.apache.torque.generator.control.ControllerState;
import org.apache.torque.generator.outlet.OutletImpl;
import org.apache.torque.generator.outlet.OutletResult;
import org.apache.torque.generator.qname.QualifiedName;
/**
 * A test java outlet.
 */
public class JavaOutlet extends OutletImpl
{
    /** The class log. */
    private static Log log = LogFactory.getLog(JavaOutlet.class);

    /** A generator configuration option. */
    private String foo;

    /** Another generator configuration option. */
    private String bar;

    public JavaOutlet(QualifiedName name)
    {
        super(name);
    }

    @Override
    public OutletResult execute(ControllerState controllerState)
            throws GeneratorException
    {
        return new OutletResult(
                "Test Outlet output; foo=" + foo + "; bar=" + bar);
    }

    public void setFoo(String foo)
    {
        log.info("foo set to " + foo);
        this.foo = foo;
    }

    public void setBar(String bar)
    {
        log.info("bar set to " + bar);
        this.bar = bar;
    }

    public String getBar()
    {
        return bar;
    }

    public String getFoo()
    {
        return foo;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bar == null) ? 0 : bar.hashCode());
        result = prime * result + ((foo == null) ? 0 : foo.hashCode());
        result = prime * result + getName().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        JavaOutlet other = (JavaOutlet) obj;
        if (bar == null)
        {
            if (other.bar != null)
            {
                return false;
            }
        }
        else if (!bar.equals(other.bar))
        {
            return false;
        }
        if (foo == null)
        {
            if (other.foo != null)
            {
                return false;
            }
        }
        else if (!foo.equals(other.foo))
        {
            return false;
        }
        if (!getName().equals(other.getName()))
        {
            return false;
        }
        return true;
    }
}
