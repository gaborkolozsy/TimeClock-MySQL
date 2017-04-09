/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.dao.JobDAO;
import hu.gaborkolozsy.timeclock.model.Job;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * This object implementing the {@code JobDAO} interface.
 * 
 * <p>
 * It manages the relationship between the {@code Job} object
 * and the MySQL database. 
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @see Connection
 * @see PreparedStatement
 * @see ResultSet
 * @see SQLException 
 * @see Timestamp
 */
public class JobDAOImpl implements JobDAO<Job> {

    /**
     * A {@code PreparedStatement} for get {@code Developer_id} 
     * from {@code Job} table of {@code TimeClock} schema.
     * @see #getDeveloperId
     */
    private final PreparedStatement getDeveloperId;
    
    /**
     * A {@code PreparedStatement} for get maximum id from {@code Job} 
     * table of {@code TimeClock} schema.
     * @see #getMaxJobId
     */
    private final PreparedStatement getMaxJobId;
    
    /**
     * A {@code PreparedStatement} for check {@code Job_number} 
     * from {@code Job} table of {@code TimeClock} schema.
     * @see #checkJobNumber 
     */
    private final PreparedStatement checkJobNumber;
    
    /**
     * A {@code PreparedStatement} for check {@code Status} 
     * from {@code Job} table of {@code TimeClock} schema.
     * @see #isStatusNull 
     */
    private final PreparedStatement isStatusNull;
    
    /**
     * A {@code PreparedStatement} for check {@code Status} 
     * from {@code Job} table of {@code TimeClock} schema.
     * @see #checkStatus
     */
    private final PreparedStatement checkStatus;
    
    /**
     * A {@code PreparedStatement} for insert a new {@code Job} object to the 
     * {@code Job} table of {@code TimeClock} schema.
     * @see #insert 
     */
    private final PreparedStatement insert;
    
    /**
     * A {@code PreparedStatement} for update the {@code Start_at} column 
     * to the {@code Job} table of {@code TimeClock} schema.
     * @see #updateStartAt
     */
    private final PreparedStatement updateStartAt;
    
    /**
     * A {@code PreparedStatement} for update the {@code End_at} column and 
     * the {@code Status}to the {@code Job} table of {@code TimeClock} schema.
     * @see #updateEndAtAndStatus
     */
    private final PreparedStatement updateEndAtAndStatus;
    
    /**
     * A {@code PreparedStatement} for update the {@code To_time} column 
     * to the {@code Job} table of {@code TimeClock} schema.
     * @see #updateToTime
     */
    private final PreparedStatement updateToTime;
    
    /**
     * A {@code PreparedStatement} for update the {@code To_time} column 
     * to the {@code Job} table of {@code TimeClock} schema by "WIP".
     * @see #updateToTimeByWIP
     */
    private final PreparedStatement updateToTimeByWIP;
    
    /**
     * A {@code PreparedStatement} for update the {@code In_part} column 
     * to the {@code Job} table of {@code TimeClock} schema.
     * @see #updateInPart
     */
    private final PreparedStatement updateInPart;
    
    /**
     * A {@code PreparedStatement} for delete the last wrong {@code Job} object
     * from {@code Job} table of {@code TimeClock} schema.
     * @see #deleteLastIncorrectJob
     */
    private final PreparedStatement deleteLastIncorrectJob;
    
    /**
     * A {@code PreparedStatement} for delete the last {@code auto_increment} 
     * value of wrong {@code Job_id} from {@code Job} table of 
     * {@code TimeClock} schema.
     * @see #autoIncrement
     */
    private final PreparedStatement autoIncrement;
    
    /**
     * Set the {@code JobDAOImpl} object for connection to MySQL database.
     * 
     * @param connection the MySQL database connection
     * @throws SQLException 
     */
    public JobDAOImpl(Connection connection) throws SQLException {
        this.getDeveloperId = connection.prepareStatement("SELECT Developer_id FROM Developer WHERE First_name = ?");
        this.getMaxJobId = connection.prepareStatement("SELECT MAX(Job_id) FROM Job");
        this.checkJobNumber = connection.prepareStatement("SELECT Job_id FROM Job WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.isStatusNull = connection.prepareStatement("SELECT ISNULL(Status) FROM Job WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.checkStatus = connection.prepareStatement("SELECT Status FROM Job WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.insert = connection.prepareStatement("INSERT INTO Job (Branch, Project, Package, Class, Job_number, Start_at, Comment, Developer_id, In_part) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        this.updateStartAt = connection.prepareStatement("UPDATE Job SET Start_at = ? WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.updateEndAtAndStatus = connection.prepareStatement("UPDATE Job SET End_at = ?, Status = ? WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.updateToTime = connection.prepareStatement("UPDATE Job SET To_time = TIMEDIFF(End_at, Start_at) WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.updateToTimeByWIP = connection.prepareStatement("UPDATE Job SET To_time = SEC_TO_TIME(TIME_TO_SEC(TIMEDIFF(End_at, Start_at)) + TIME_TO_SEC(To_time)) WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.updateInPart = connection.prepareStatement("UPDATE Job SET In_part = In_part + 1 WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.deleteLastIncorrectJob = connection.prepareStatement("DELETE FROM Job WHERE Job_id = ?");
        this.autoIncrement = connection.prepareStatement("ALTER TABLE Job AUTO_INCREMENT = 1");
    }
    
    /**
     * Returns the developer's id for storing in the {@code Job} table of 
     * {@code TimeClock} schema.
     * 
     * @param developerFirstName for getting id
     * @return the developer id for insert
     * @throws SQLException
     */
    @Override
    public int getDeveloperId(String developerFirstName) throws SQLException {
        this.getDeveloperId.setString(1, developerFirstName);
        ResultSet rs = getDeveloperId.executeQuery();
        if (rs.first()) {
            return rs.getInt("Developer_id");
        }
        return 0;
    }
    
    /**
     * Returns the maximum id from {@code Job} table of {@code TimeClock}
     * schema from MySQL database.
     * 
     * @return maximum {@code Job} id from {@code Job} table
     * @throws SQLException
     */
    @Override
    public int getMaxId() throws SQLException {
        ResultSet rs = getMaxJobId.executeQuery();
        if (rs.first()) {
            return rs.getInt(1);
        }
        return 0;
    }
    
    /**
     * Check the {@code Job_number} by the specified {@code Job}
     * is exists or not.
     * 
     * @param job for identification the correct {@code Job}
     * @return {@code true} is if the {@code Job_number} not used
     * @throws SQLException
     */
    @Override
    public boolean checkJobNumber(Job job) throws SQLException {
        this.checkJobNumber.setString(1, job.getProject());
        this.checkJobNumber.setString(2, job.getPackage());
        this.checkJobNumber.setString(3, job.getClazz());
        this.checkJobNumber.setInt(4, job.getJobNumber());
        ResultSet rs = checkJobNumber.executeQuery();
        return !rs.first();
    }
    
    /**
     * Returns true if the {@code Status} column of {@code Job} table is null.
     * 
     * @param job for identification the correct {@code Job}
     * @return {@code true} is the {@code Status} column is null
     * @throws SQLException
     */
    @Override
    public boolean isStatusNull(Job job) throws SQLException {
        this.isStatusNull.setString(1, job.getProject());
        this.isStatusNull.setString(2, job.getPackage());
        this.isStatusNull.setString(3, job.getClazz());
        this.isStatusNull.setInt(4, job.getJobNumber());
        ResultSet rs = isStatusNull.executeQuery();
        boolean statusNull = false;
        if (rs.first()) {
             statusNull = rs.getBoolean(1);
        }
        return statusNull;
    }
    
    /**
     * Returns <strong>"WIP"</strong> if the {@code Job} "work in progress"
     * or <strong>"Done"</strong> if it's done.
     * 
     * @param job for identification the correct {@code Job}
     * @return the {@code Job}'s status("WIP" or "Done")
     * @throws SQLException
     */
    @Override
    public String checkStatus(Job job) throws SQLException {
        this.checkStatus.setString(1, job.getProject());
        this.checkStatus.setString(2, job.getPackage());
        this.checkStatus.setString(3, job.getClazz());
        this.checkStatus.setInt(4, job.getJobNumber());
        ResultSet rs = checkStatus.executeQuery();
        if (rs.first()) {
            return rs.getString("Status");
        }
        return null;
    }
    
    /**
     * Insert {@code Job} into the {@code Job} table of {@code TimeClock} schema 
     * in the MySQL database.
     * 
     * @param job for inserting
     * @throws SQLException
     */
    @Override
    public void insert(Job job) throws SQLException {
        this.insert.setString(1, job.getBranch());
        this.insert.setString(2, job.getProject());
        this.insert.setString(3, job.getPackage());
        this.insert.setString(4, job.getClazz());
        this.insert.setInt(5, job.getJobNumber());
        this.insert.setTimestamp(6, Timestamp.valueOf(job.getStartAt()));
        this.insert.setString(7, job.getComment());
        this.insert.setInt(8, job.getDeveloperId());
        this.insert.setInt(9, 1);
        this.insert.executeUpdate();
    }
    
    /**
     * Updateing the {@code Start_at} column in the {@code Job} table if the
     * {@code Status} column <strong>"WIP"</strong> is by the new start.
     * 
     * @param job for identification the correct {@code Job}
     * @throws SQLException
     */
    @Override
    public void updateStartAt(Job job) throws SQLException {
        this.updateStartAt.setTimestamp(1, Timestamp.valueOf(job.getStartAt()));
        this.updateStartAt.setString(2, job.getProject());
        this.updateStartAt.setString(3, job.getPackage());
        this.updateStartAt.setString(4, job.getClazz());
        this.updateStartAt.setInt(5, job.getJobNumber());
        this.updateStartAt.executeUpdate();
    }

    /**
     * Updateing the {@code End_at} and the {@code Status} column in the 
     * {@code Job} table if the job is end.
     * 
     * @param job for identification the correct {@code Job}
     * @throws SQLException
     */
    @Override
    public void updateEndAtAndStatus(Job job) throws SQLException {
        this.updateEndAtAndStatus.setTimestamp(1, Timestamp.valueOf(job.getEndAt()));
        this.updateEndAtAndStatus.setString(2, job.getStatus());
        this.updateEndAtAndStatus.setString(3, job.getProject());
        this.updateEndAtAndStatus.setString(4, job.getPackage());
        this.updateEndAtAndStatus.setString(5, job.getClazz());
        this.updateEndAtAndStatus.setInt(6, job.getJobNumber());
        this.updateEndAtAndStatus.executeUpdate();
    }

    /**
     * Updateing the {@code To_time} column in {@code Job} table if {@code Job}
     * is done.
     * 
     * @param job for identification the correct {@code Job}
     * @throws SQLException
     */
    @Override
    public void updateToTime(Job job) throws SQLException {
        this.updateToTime.setString(1, job.getProject());
        this.updateToTime.setString(2, job.getPackage());
        this.updateToTime.setString(3, job.getClazz());
        this.updateToTime.setInt(4, job.getJobNumber());
        this.updateToTime.executeUpdate();
    }
    
    /**
     * Updateing the {@code To_time} column in {@code Job} table if {@code Job}
     * is "work in progress".
     * 
     * @param job for identification the correct {@code Job}
     * @throws SQLException
     */
    @Override
    public void updateToTimeByWIP(Job job) throws SQLException {
        this.updateToTimeByWIP.setString(1, job.getProject());
        this.updateToTimeByWIP.setString(2, job.getPackage());
        this.updateToTimeByWIP.setString(3, job.getClazz());
        this.updateToTimeByWIP.setInt(4, job.getJobNumber());
        this.updateToTimeByWIP.executeUpdate();
    }
    
    /**
     * In how many part will done the specified {@code Job}.
     * 
     * @param job for identification the correct {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
     */
    @Override
    public void updateInPart(Job job) throws SQLException {
        this.updateInPart.setString(1, job.getProject());
        this.updateInPart.setString(2, job.getPackage());
        this.updateInPart.setString(3, job.getClazz());
        this.updateInPart.setInt(4, job.getJobNumber());
        this.updateInPart.executeUpdate();
    }
    
    /**
     * Delete the last wrong inserted object from {@code TimeClock} schema
     * in the MySQL database.
     * 
     * @throws SQLException
     */
    @Override
    public void deleteLastIncorrectObject() throws SQLException {
        this.deleteLastIncorrectJob.setInt(1, getMaxId());
        this.deleteLastIncorrectJob.executeUpdate();
    }
    
    /**
     * After the deleted last wrong object from database, 
     * delete the last {@code auto-increment} id number too. 
     * <p>
     * By next inserted new object the <code>id</code> will correct again.
     * 
     * @throws SQLException
     */
    @Override
    public void autoIncrement() throws SQLException {
        this.autoIncrement.executeUpdate();
    }

    /**
     * Before program quit close all opened <code>PreparedStatement</code>
     * and database <code>Connection</code> too.
     * 
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        this.getDeveloperId.close();
        this.checkJobNumber.close();
        this.isStatusNull.close();
        this.checkStatus.close();
        this.insert.close();
        this.updateStartAt.close();
        this.updateEndAtAndStatus.close();
        this.updateToTime.close();
        this.updateToTimeByWIP.close();
        this.updateInPart.close();
        this.deleteLastIncorrectJob.close();
        this.autoIncrement.close();
    }

}
