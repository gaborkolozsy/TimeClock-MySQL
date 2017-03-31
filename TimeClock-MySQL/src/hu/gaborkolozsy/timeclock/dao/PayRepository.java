/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.daos;

import java.sql.SQLException;

/**
 * The <strong>PayRepository</strong> interface extends the 
 * {@code Repository<E>} interface. 
 * <p>
 * The <strong>PayRepository</strong> interface provide some method 
 * for connection the {@code Pay} object to MySQL database.
 * This will ensure the correct connection.
 * 
 * @author Kolozsy Gábor
 * @version 2.0
 * @param <E>
 * @see hu.gaborkolozsy.timeclock.daos.Repository
 * @see timeclock.dao.PayRepositoryJDBCImpl
 * @see java.sql.SQLException
 */
public interface PayRepository<E> extends Repository<E> {
    
    /**
     * Returns the maximum {@code Pay_id} from <code>Pay</code> table of 
     * {@code TimeClock} schema from MySQL database. 
     * <p>
     * Use this integer value before and after by 
     * {@link deleteLastIncorrectPay(int PayId)} method for control.
     * 
     * @return maximum <code>Pay_id</code> from <code>Pay</code> table
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    @Override
    int getMaxId() throws SQLException;
    
    /**
     * Insert a new <code>Pay</code> object into a <code>Pay</code> table 
     * of {@code TimeClock} schema in the MySQL database.
     * 
     * @param e any object for insert to database
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    @Override
    void insert(E e) throws SQLException;
    
    /**
     * Delete the last wrong inserted object from {@code TimeClock} schema
     * in the MySQLdatabase.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    @Override
    void deleteLastIncorrectObject() throws SQLException;
    
    /**
     * After the deleted last wrong <code>Pay</code> object from database, 
     * delete the last {@code auto-increment} id number too. 
     * <p>
     * By next inserted new <code>Pay</code> object the <code>Pay_id</code>
     * will correct again.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    @Override
    void autoIncrement() throws SQLException;
    
    /**
     * Before program quit close all opened <code>PreparedStatement</code>
     * and database <code>Connection</code> too.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    @Override
    void close() throws SQLException;

}
