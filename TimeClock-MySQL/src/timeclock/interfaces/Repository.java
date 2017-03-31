/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package timeclock.interfaces;

/**
 * The <strong>Repository</strong> interface provide a method for getting 
 * max id from database and a method for insert any 
 * <strong>E</strong> {@code Object}.
 * <p>
 * The <strong>Repository</strong> interface provide a method for deleting
 * last wrong inserted object.
 * <p>
 * The <strong>Repository</strong> interface provide a method for deleting
 * last wrong auto_increment id.
 * <p>
 * The <strong>Repository</strong> interface provide a method for closing all 
 * opened connection.
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @param <E> any {@code Object}
 * @see timeclock.interfaces.JobRepository
 * @see timeclock.interfaces.PayRepository
 * @see timeclock.dao.JobRepositoryJDBCImpl
 * @see timeclock.dao.PayRepositoryJDBCImpl
 * @see java.sql.Exception
 */
public interface Repository<E> {
    
    /**
     * Returns the maximum id from any table of {@code TimeClock}
     * schema from MySQL database. 
     * <p>
     * Use this integer value by {@link deleteLastIncorrectPay(int PayId)} 
     * method for control.
     * 
     * @return maximum <code>id</code> from any table of database
     * @throws Exception 
     * @see timeclock.dao.JobRepository
     * @see timeclock.dao.PayRepository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    int getMaxId() throws Exception;
    
    /**
     * Insert any new object into any table of {@code TimeClock} schema 
     * in the MySQL database.
     * 
     * @param e any object for insert to database
     * 
     * @throws Exception 
     * @see timeclock.job.Job
     * @see timeclock.pay.Pay
     * @see timeclock.dao.JobRepository
     * @see timeclock.dao.PayRepository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    void insert(E e) throws Exception;
    
    /**
     * Delete the last wrong inserted object from {@code TimeClock} schema
     * in the MySQLdatabase.
     * 
     * @throws Exception 
     * @see timeclock.dao.JobRepository
     * @see timeclock.dao.PayRepository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    void deleteLastIncorrectObject() throws Exception;
    
    /**
     * After the deleted last wrong object from database, 
     * delete the last {@code auto-increment} id number too. 
     * <p>
     * By next inserted new object the <code>id</code> will correct again.
     * 
     * @throws Exception 
     * @see timeclock.dao.JobRepository
     * @see timeclock.dao.PayRepository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    void autoIncrement() throws Exception;
    
    /**
     * Before program quit close all opened <code>PreparedStatement</code>
     * and database <code>Connection</code> too.
     * 
     * @throws Exception 
     * @see timeclock.dao.JobRepository
     * @see timeclock.dao.PayRepository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     * @see timeclock.dao.PayRepositoryJDBCImpl
     */
    void close() throws Exception;
}
