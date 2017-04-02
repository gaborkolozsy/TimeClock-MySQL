/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.deprecated;

import hu.gaborkolozsy.timeclock.dao.InfoDAO;
import hu.gaborkolozsy.timeclock.model.Job;
import hu.gaborkolozsy.timeclock.model.TimeInfo;
import java.sql.SQLException;

/**
 * This interface provide a few deprecated methods. Please don't used these.
 * The @code InfoDAO} interface's {@code getInfo()} generic method is a 
 * better and simpler alternative.
 * 
 * <p>
 * The {@code TimeInfoRepository} interface extends the 
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
 * @see TimeInfo
 * @see SQLException
 * @deprecated <strong>Please use simple the {@code InfoDAO} interface 
 * instead of this.</strong>
 */
@Deprecated
public interface TimeInfoRepository<T, E> extends InfoDAO<T, E> {
    
    /**
     * Make a {@code TimeInfo} object for displayed the data 
     * members on the {@code Time} tab in the program window.
     * 
     * @param job to identification the correct {@code Job}
     * @param status to identification the correct {@code Job}
     * @return a new {@code TimeInfo} object
     * @throws SQLException 
     * @see Job
     * @see TimeInfo
     * @deprecated <strong>Don't use this method!</strong>
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
     * @deprecated <strong>Don't use this method!</strong>
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
     * @deprecated <strong>Don't use this method!</strong>
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
     * @deprecated <strong>Don't use this method!</strong>
     */
    @Deprecated
    String getTotalTime(String project, String status) throws SQLException;
    
}
