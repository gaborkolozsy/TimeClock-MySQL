/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao;

import java.sql.SQLException;

/**
 * The <strong>InfoDAO</strong> interface provide a method for getting 
 * information from database about any <strong>E</strong> {@code Object} 
 * with any returning <strong>T</strong> {@code Object} type.
 * <p>
 * The <strong>InfoDAO</strong> interface provide a method for closing all 
 * opened connection.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @param <T> an {@code Object} as a returning type
 * @param <E> an {@code Object} for information searching
 * @see SQLException
 */
public interface InfoDAO<T, E> extends RootDAO {
    
    /**
     * Make any {@code Object} for displayed data 
     * members on the any tab in the program window.
     * 
     * @param e an {@code Object} to what information and this {@code Object}
     * is in the database
     * @return an info {@code Object}
     * @throws SQLException
     */
    T getInfo(E e) throws SQLException;
    
}
