/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.daos;

/**
 * The <strong>Info</strong> interface provide a method for getting 
 * information from database about any <strong>E</strong> {@code Object} 
 * with any returning <strong>T</strong> {@code Object} type.
 * <p>
 * The <strong>Info</strong> interface provide a method for closing all 
 * opened connection.
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @param <T> an {@code Object} as a returning type
 * @param <E> an {@code Object} for information searching
 * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
 * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
 * @see java.sql.SQLException
 */
public interface Info<T,E> {
    
    /**
     * Make any {@code Object} for displayed data 
     * members on the any tab in the program window.
     * 
     * @param e an {@code Object} to what information and this {@code Object}
     * is in the database
     * @return an info {@code Object}
     * @throws Exception 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     */
    T getInfo(E e) throws Exception;
    
    /**
     * Before program quit close all opened {@code PreparedStatement}
     * and database {@code Connection} too.
     * 
     * @throws Exception 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     */
    void close() throws Exception;
    
}
