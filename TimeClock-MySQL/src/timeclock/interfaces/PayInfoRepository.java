/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package timeclock.interfaces;

import java.sql.SQLException;
import timeclock.query.PayInfo;
import timeclock.job.Job;

/**
 * The <strong>PayInfoRepository</strong> interface extends the 
 * {@code Info<T,E>} interface. 
 * <p>
 * The <strong>PayInfoRepository</strong> interface provide a method 
 * for getting information from database about any <strong>E</strong> 
 * {@code Object} with any returning <strong>T</strong> {@code Object} type.
 * <p>
 * The <strong>PayInfoRepository</strong> interface provide a method 
 * for closing all opened connection.
 * <p>
 * This interface will ensure the correct connection with the MySQL database.
 * <p>
 * This interface provide a few deprecated methods. Please not used these.
 * The generic implemented method is a better alternative.
 * 
 * @author Kolozsy Gábor
 * @version 2.1
 * @param <T> an {@code Object} as a returning type
 * @param <E> an {@code Object} for information searching
 * @see timeclock.interfaces.Info
 * @see timeclock.query.PayInfo
 * @see timeclock.dao.PayInfoRepositoryJDBCImpl
 * @see java.sql.SQLException
 */
public interface PayInfoRepository<T,E> extends Info<T,E> {
 
    /**
     * Make a {@code Object} for displayed the data 
     * members on the specified tab in the program window.
     * 
     * @param e to identification the correct {@code Object}
     * @return a new {@code Object}
     * @throws SQLException 
     * @see timeclock.job.Job
     * @see timeclock.query.PayInfo
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     */
    @Override
    T getInfo(E e) throws SQLException;
    
    /**
     * Make a <code>PayInfo</code> object for displayed the data 
     * members on the <code>Pay</code> tab in the program window.
     * 
     * @param job to identification the correct {@code Job}
     * @param status to identification the correct {@code Job} 
     * @return a new {@code PayInfo} object
     * @throws SQLException 
     * @see timeclock.job.Job
     * @see timeclock.query.PayInfo
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    PayInfo makePayInfo(Job job, String status) throws SQLException;
    
    /**
     * Get the <code>hourlyPay</code> data member of {@code PayInfo}
     * object by the specified job if done it. 
     * <p>
     * Query the <code>Job_id</code> from database with 
     * {@link getJobId(Job job)} method.
     * <p>
     * This value as {@code String} displayed on the <code>Pay</code> tab 
     * in the program window.
     * 
     * @param job a <code>Job</code> object for query the <code>Job_id</code> 
     * from the database 
     * @return the {@code hourlyPay} data member for {@code PayInfo} 
     * object
     * @throws SQLException
     * @see timeclock.job.Job
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    int getHourlyPay(Job job) throws SQLException;
    
    /**
     * Get the <code>averageHourlyPay</code> data member of 
     * {@code PayInfo} object by the specified project. 
     * <p>
     * This value as {@code String} displayed on the <code>Pay</code> tab 
     * in the program window.
     * 
     * @param project name of specified <code>Project</code>
     * @return the {@code averageHourlyPay} data member for 
     * {@code PayInfo} object
     * @throws SQLException 
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    int getAverageHourlyPay(String project) throws SQLException;
    
    /**
     * Get the <code>totalPayment</code> data member of {@code PayInfo} 
     * object by the specified project.
     * <p>
     * Selected job by this {@code status} and specified {@code Project}
     * in the database. If {@code status} <code>Done</code> is, 
     * then calculate with it.
     * <p>
     * This value as {@code String} displayed on the <code>Pay</code> tab 
     * in the program window.
     * 
     * @param project name of specified <code>Project</code>
     * @param status job <code>Status</code> in this specified
     * <code>Project</code> from database
     * @return the {@code totalPayment} data member for {@code PayInfo} 
     * object
     * @throws SQLException 
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    int getTotalPayment(String project, String status) throws SQLException;
    
    /**
     * Returns the {@code Job_id} by specified <code>Job</code> object
     * from database.
     * 
     * @param job a <code>Job</code> object for query the <code>Job_id</code> 
     * from the database
     * @return the <code>Job_id</code> from the database as integer value
     * @throws SQLException
     * @see timeclock.job.Job
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    int getJobId(Job job) throws SQLException;
    
    /**
     * Before program quit close all opened <code>PreparedStatement</code>
     * and database <code>Connection</code> too.
     * 
     * @throws SQLException 
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     */
    @Override
    void close() throws SQLException;
}
