package com.interview.cache.integration.redis.util;

import java.util.concurrent.locks.Lock;

/**
 * Strategy for maintaining a registry of shared locks
 *
 * @author Oleg Zhurakousky
 * @author Gary Russell
 * @since 2.1.1
 */
public interface LockRegistry {

    /**
     * Obtains the lock associated with the parameter object.
     * @param lockKey The object with which the lock is associated.
     * @return The associated lock.
     */
    Lock obtain(Object lockKey);

}