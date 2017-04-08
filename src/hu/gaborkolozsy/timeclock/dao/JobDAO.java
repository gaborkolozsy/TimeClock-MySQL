/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao;

import hu.gaborkolozsy.timeclock.model.Job;
import java.sql.SQLException;

/**
 * The extended <strong>JobDAO</strong> interface. 
 * <p>
 * The <strong>JobDAO</strong> interface provide some method 
 * for connection the {@code Job} object to MySQL database.
 * This will ensure the correct connection.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @param <E> {@code Job} model
 * @see Job
 * @see SQLException
 */
public interface JobDAO<E> extends CommonDAO<E> {
    
    /**
     * Returns the developer's id for storing in the {@code Job} table of 
     * {@code TimeClock} schema.
     * 
     * @param developerFirstName for identification
     * @return the developer correct id to store in {@code Job} table
     * @throws SQLException
     */
    int getDeveloperId(String developerFirstName) throws SQLException;
    
    /**
     * Check the {@code Job_number} by the specified {@code Job}
     * is exists or not.
     * 
     * @param job for identification the {@code Job}
     * @return {@code true} if the {@code Job_number} is not exists
     * @throws SQLException
     */
    boolean checkJobNumber(Job job) throws SQLException;
    
    /**
     * Returns true if the {@code Status} column of {@code Job} table is null.
     * 
     * @param job for identification the {@code Job}
     * @return {@code true} if the {@code Status} is null
     * @throws SQLException
     */
    boolean isStatusNull(Job job) throws SQLException;
    
    /**
     * Returns <strong>"WIP"</strong> if the {@code Job} "work in progress"
     * or <strong>"Done"</strong> if it's done.
     * 
     * @param job for identification the {@code Job}
     * @return the {@code Job}'s status
     * @throws SQLException
     */
    String checkStatus(Job job) throws SQLException;
    
    /**
     * Updateing the {@code Start_at} column in the {@code Job} table if the
     * {@code Status} column <strong>"WIP"</strong> is by the new start.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException
     */
    void updateStartAt(Job job) throws SQLException;
    
    /**
     * Updateing the {@code End_at} and the {@code Status} column in the 
     * {@code Job} table if the job is end.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException
     */
    void updateEndAtAndStatus(Job job) throws SQLException;
    
    /**
     * Updateing the {@code To_time} column in {@code Job} table if {@code Job}
     * is done.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException
     */
    void updateToTime(Job job) throws SQLException;
    
    /**
     * Updateing the {@code To_time} column in {@code Job} table if {@code Job}
     * is "work in progress".
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException
     */
    void updateToTimeByWIP(Job job) throws SQLException;
    
    /**
     * In how many part will done the specified {@code Job}.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException
     */
    void updateInPart(Job job) throws SQLException;
    
}
