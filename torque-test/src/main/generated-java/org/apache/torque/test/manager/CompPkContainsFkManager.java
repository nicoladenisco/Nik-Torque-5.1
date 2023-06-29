package org.apache.torque.test.manager;

import org.apache.torque.TorqueException;

/**
 * This class manages CompPkContainsFk objects.
 *
 * table with a composite primary key a part of which           is a foreign key column which references the non-composite            primary key of another table
 *
 * This class was autogenerated by Torque on:
 *
 * [Thu Jan 27 17:44:01 CET 2022]
 *
 * You should not use this class directly.  It should not even be
 * extended; all references should be to CompPkContainsFkBean
 */
 
public class CompPkContainsFkManager
    extends org.apache.torque.test.manager.base.BaseCompPkContainsFkManager
{
    /** Serial version */
    private static final long serialVersionUID = 1643301841757L;


    /**
     * Creates a new <code>CompPkContainsFkManager</code> instance.
     *
     * @exception TorqueException if an error occurs
     */
    public CompPkContainsFkManager()
        throws TorqueException
    {
        super();
    }

}
