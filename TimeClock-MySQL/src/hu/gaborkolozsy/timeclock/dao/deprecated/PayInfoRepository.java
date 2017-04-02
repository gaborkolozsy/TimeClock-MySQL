/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.deprecated;

import hu.gaborkolozsy.timeclock.dao.InfoDAO;
import hu.gaborkolozsy.timeclock.model.Job;
import hu.gaborkolozsy.timeclock.model.PayInfo;
import java.sql.SQLException;

/**
 * This interface provide a few deprecated methods. Please don't used these.
 * The @code InfoDAO} interface's {@code getInfo()} generic method is a 
 * better and simpler alternative.
 * 
 * <p>
 * The {@code PayInfoRepository} interface extends the 
 * {@code InfoDAO<T, E>} interface. 
 * 
 * <p>
 * This interface will ensure the correct connection with the MySQL database.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @param <T> an {@code Object} as a returning type
 * @param <E> an {@code Object} for information searching
 * @see Job
 * @see PayInfo
 * @see SQLException
 * @deprecated <strong>Please use simple the {@code InfoDAO} interface 
 * instead of this.</strong>
 */
@Deprecated
public interface PayInfoRepository<T, E> extends InfoDAO<T, E> {
 
    /**
     * Make a <code>PayInfo</code> object for displayed the data 
     * members on the <code>Pay</code> tab in the program window.
     * 
     * @param job to identification the correct {@code Job}
     * @param status to identification the correct {@code Job} 
     * @return a new {@code PayInfo} object
     * @throws SQLException 
     * @see Job
     * @see PayInfo
     * @deprecated <strong>Don't use this method!</strong>
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
     * @see Job
     * @deprecated <strong>Don't use this method!</strong>
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
     * @deprecated <strong>Don't use this method!</strong>
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
     * @deprecated <strong>Don't use this method!</strong>
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
     * @see Job
     * @deprecated <strong>Don't use this method!</strong>
     */
    @Deprecated
    int getJobId(Job job) throws SQLException;
    
}
