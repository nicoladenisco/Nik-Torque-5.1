package org.apache.torque.test.manager;

import org.apache.torque.TorqueException;

/**
 * This class manages OptimisticLocking objects.
 *
 * this table implements optimistic Locking with the selectForUpdate mode
 *
 * This class was autogenerated by Torque on:
 *
 * [Thu Jan 27 17:44:01 CET 2022]
 *
 * You should not use this class directly.  It should not even be
 * extended; all references should be to OptimisticLockingBean
 */
 
public class OptimisticLockingManager
    extends org.apache.torque.test.manager.base.BaseOptimisticLockingManager
{
    /** Serial version */
    private static final long serialVersionUID = 1643301841859L;


    /**
     * Creates a new <code>OptimisticLockingManager</code> instance.
     *
     * @exception TorqueException if an error occurs
     */
    public OptimisticLockingManager()
        throws TorqueException
    {
        super();
    }

}
