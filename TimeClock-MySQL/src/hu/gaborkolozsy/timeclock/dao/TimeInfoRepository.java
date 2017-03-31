/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.daos;

import java.sql.SQLException;
import hu.gaborkolozsy.timeclock.model.Job;
import hu.gaborkolozsy.timeclock.model.TimeInfo;

/**
 * The <strong>TimeInfoRepository</strong> interface extends the 
 * {@code Info<T,E>} interface. 
 * <p>
 * The <strong>TimeInfoRepository</strong> interface provide a method 
 * for getting information from database about any <strong>E</strong> 
 * {@code Object} with any returning <strong>T</strong> {@code Object} type.
 * <p>
 * The <strong>TimeInfoRepository</strong> interface provide a method 
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
 * @see hu.gaborkolozsy.timeclock.daos.Info
 * @see hu.gaborkolozsy.timeclock.model.TimeInfo
 * @see timeclock.dao.TimeInfoRepositoryJDBCImpl
 * @see java.sql.SQLException
 */
public interface TimeInfoRepository<T,E> extends Info<T,E> {
    
    /**
     * Make a {@code Object} for displayed the data 
     * members on the specified tab in the program window.
     * 
     * @param e to identification the correct {@code Object}
     * @return a new {@code Object}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see hu.gaborkolozsy.timeclock.model.TimeInfo
     * @see timeclock.dao.TimeInfoRepositoryJDBCImpl
     */
    @Override
    T getInfo(E e) throws SQLException;
    
    /**
     * Make a {@code TimeInfo} object for displayed the data 
     * members on the {@code Time} tab in the program window.
     * 
     * @param job to identification the correct {@code Job}
     * @param status to identification the correct {@code Job}
     * @return a new {@code TimeInfo} object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see hu.gaborkolozsy.timeclock.model.TimeInfo
     * @see timeclock.dao.TimeInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    TimeInfo makeTimeInfo(Job job, String status) throws SQLException;
    
    /**
     * Get the {@code toTime} data member of {@code TimeInfo}
     * object by the specified job if done it.
     * 
     * @param job for identification the correct {@code Job}
     * @return value of {@code To_time} column as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @see timeclock.dao.TimeInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    String getToTime(Job job) throws SQLException;
    
    /**
     * Get the {@code averageTime} data member of {@code TimeInfo}
     * object by the selected {@code Project} if done it.
     * 
     * @param project name of specified {@code Project}
     * @return value of {@code averageTime} as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @see timeclock.dao.TimeInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    String getAverageTime(String project) throws SQLException;
    
    /**
     * Get the {@code totalTime} data member of {@code TimeInfo}
     * object by the selected {@code Project} if done it.
     * 
     * @param project name of specified {@code Project}
     * @param status job {@code Status} in this specified
     * {@code Project} from database
     * @return value of {@code totalTime} as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @see timeclock.dao.TimeInfoRepositoryJDBCImpl
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    String getTotalTime(String project, String status) throws SQLException;
    
    /**
     * Before program quit close all opened {@code PreparedStatement}
     * and database {@code Connection} too.
     * 
     * @throws SQLException 
     * @see timeclock.dao.PayInfoRepositoryJDBCImpl
     */
    @Override
    void close() throws SQLException;
    
}
