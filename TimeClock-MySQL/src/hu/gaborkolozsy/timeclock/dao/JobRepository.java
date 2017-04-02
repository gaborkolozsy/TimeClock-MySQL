/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao;

import hu.gaborkolozsy.timeclock.model.Job;
import java.sql.SQLException;

/**
 * The <strong>JobRepository</strong> interface extends the 
 * {@code Repository<E>} interface. 
 * <p>
 * The <strong>JobRepository</strong> interface provide some method 
 * for connection the {@code Job} object to MySQL database.
 * This will ensure the correct connection.
 * 
 * @author Kolozsy Gábor
 * @version 2.0
 * @param <E> any {@code Object}
 * @see hu.gaborkolozsy.timeclock.model.Job
 * @see hu.gaborkolozsy.timeclock.daos.Repository
 * @see timeclock.dao.JobRepositoryJDBCImpl
 * @see java.sql.SQLException
 */
public interface JobRepository<E> extends Repository<E> {
    
    /**
     * Returns the developer's id for storing in the {@code Job} table of 
     * {@code TimeClock} schema.
     * 
     * @param developerFirstName for identification
     * @return the developer correct id to store in {@code Job} table
     * @throws SQLException 
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    int getDeveloperId(String developerFirstName) throws SQLException;
    
    /**
     * Returns the maximum {@code Job_id} from {@code Job} table of 
     * {@code TimeClock} schema.
     * 
     * @return maximum {@code Job_id} for delete 
     * {@code deleteLastIncorrectJob()} method
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    @Override
    int getMaxId() throws SQLException;
    
    /**
     * Check the {@code Job_number} by the specified {@code Job}
     * is exists or not.
     * 
     * @param job for identification the {@code Job}
     * @return {@code true} if the {@code Job_number} is not exists
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    boolean checkJobNumber(Job job) throws SQLException;
    
    /**
     * Returns true if the {@code Status} column of {@code Job} table is null.
     * 
     * @param job for identification the {@code Job}
     * @return {@code true} if the {@code Status} is null
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    boolean isStatusNull(Job job) throws SQLException;
    
    /**
     * Returns <strong>"WIP"</strong> if the {@code Job} "work in progress"
     * or <strong>"Done"</strong> if done it.
     * 
     * @param job for identification the {@code Job}
     * @return the {@code Job}'s status
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    String checkStatus(Job job) throws SQLException;
    
    /**
     * Insert any object to the MySQL database.
     * 
     * @param e for inserting it
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    @Override
    void insert(E e) throws SQLException;
    
    /**
     * Updateing the {@code Start_at} column in the {@code Job} table if the
     * {@code Status} column <strong>"WIP"</strong> is by the new start.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    void updateStartAt(Job job) throws SQLException;
    
    /**
     * Updateing the {@code End_at} and the {@code Status} column in the 
     * {@code Job} table if the job is end.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    void updateEndAtAndStatus(Job job) throws SQLException;
    
    /**
     * Updateing the {@code To_time} column in {@code Job} table if {@code Job}
     * is done.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    void updateToTime(Job job) throws SQLException;
    
    /**
     * Updateing the {@code To_time} column in {@code Job} table if {@code Job}
     * is "work in progress".
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    void updateToTimeByWIP(Job job) throws SQLException;
    
    /**
     * In how many part will done the specified {@code Job}.
     * 
     * @param job for identification the {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Job
     * @see timeclock.dao.JobRepositoryJDBCImpls 
     */
    void updateInPart(Job job) throws SQLException;
    
    /**
     * Delete last wrong inserted {@code Job} object from {@code Job} table 
     * of {@code TimeClock} schema in MySQL database.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.JobRepositoryJDBCImpls 
     */
    @Override
    void deleteLastIncorrectObject() throws SQLException;
    
    /**
     * After the deleted last wrong {@code Job} object from database, 
     * delete the last {@code auto-increment} id number too. 
     * <p>
     * By next inserted new {@code Job} object the {@code Job_id}
     * will correct again.
     * 
     * @throws SQLException
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.JobRepositoryJDBCImpls
     */
    @Override
    void autoIncrement() throws SQLException;
    
    /**
     * Before program quit close all opened <code>PreparedStatement</code>
     * and database <code>Connection</code> too.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.Repository
     * @see timeclock.dao.JobRepositoryJDBCImpl
     */
    @Override
    void close() throws SQLException;
}
