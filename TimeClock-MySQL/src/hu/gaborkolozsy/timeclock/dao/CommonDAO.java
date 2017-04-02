/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao;

import java.sql.SQLException;

/**
 * The <strong>CommonDAO</strong> interface provide a method for getting 
 * max id from database, a method for insert any <strong>E</strong> {@code Object},
 * for deleting last wrong inserted object, a methode for deleting last wrong 
 * auto_increment id and a methode for closing all opened connection.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @param <E> a {@TimeClock} model
 * @see SQLException
 */
public interface CommonDAO<E> extends RootDAO {
    
    /**
     * Returns the maximum id from any table of {@code TimeClock}
     * schema from MySQL database.
     * 
     * @return maximum <code>id</code> from any table of database
     * @throws SQLException
     */
    int getMaxId() throws SQLException;
    
    /**
     * Insert E {@code Object} into any table of {@code TimeClock} schema 
     * in the MySQL database.
     * 
     * @param e any object for insert to database
     * @throws SQLException
     */
    void insert(E e) throws SQLException;
    
    /**
     * Delete the last wrong inserted object from {@code TimeClock} schema
     * in the MySQL database.
     * 
     * @throws SQLException
     */
    void deleteLastIncorrectObject() throws SQLException;
    
    /**
     * After the deleted last wrong object from database, 
     * delete the last {@code auto-increment} id number too. 
     * <p>
     * By next inserted new object the <code>id</code> will correct again.
     * 
     * @throws SQLException
     */
    void autoIncrement() throws SQLException;
    
}
