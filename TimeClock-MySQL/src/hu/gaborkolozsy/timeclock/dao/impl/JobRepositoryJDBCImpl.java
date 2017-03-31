/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import hu.gaborkolozsy.timeclock.daos.JobRepository;
import hu.gaborkolozsy.timeclock.model.Job;

/**
 * This object implementing the {@link hu.gaborkolozsy.timeclock.daos.JobRepository}
 * interface. 
 * <p>
 * It manages the relationship between the {@code Job} object
 * and the MySQL database. 
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see hu.gaborkolozsy.timeclock.model.Job
 * @see hu.gaborkolozsy.timeclock.daos.JobRepository
 * @see java.sql.Connection
 * @see java.sql.PreparedStatement
 * @see java.sql.ResultSet
 * @see java.sql.SQLException 
 */
public class JobRepositoryJDBCImpl implements JobRepository<Job> {

    /**
     * MySQL database connection.
     */
    private final Connection connection;
    
    /**
     * a {@code PreparedStatement} for get {@code Developer_id} 
     * from {@code Job} table of {@code TimeClock} schema
     * @see #getDeveloperId
     */
    private final PreparedStatement getDeveloperId;
    
    /**
     * a {@code PreparedStatement} for get maximum id from {@code Job} 
     * table of {@code TimeClock} schema
     * @see #getMaxJobId
     */
    private final PreparedStatement getMaxJobId;
    
    /**
     * a {@code PreparedStatement} for check {@code Job_number} 
     * from {@code Job} table of {@code TimeClock} schema
     * @see #checkJobNumber 
     */
    private final PreparedStatement checkJobNumber;
    
    /**
     * a {@code PreparedStatement} for check {@code Status} 
     * from {@code Job} table of {@code TimeClock} schema
     * @see #isStatusNull 
     */
    private final PreparedStatement isStatusNull;
    
    /**
     * a {@code PreparedStatement} for check {@code Status} 
     * from {@code Job} table of {@code TimeClock} schema
     * @see #checkStatus
     */
    private final PreparedStatement checkStatus;
    
    /**
     * a {@code PreparedStatement} for insert a new {@code Job} object to the 
     * {@code Job} table of {@code TimeClock} schema
     * @see #insert 
     */
    private final PreparedStatement insert;
    
    /**
     * a {@code PreparedStatement} for update the {@code Start_at} column 
     * to the {@code Job} table of {@code TimeClock} schema
     * @see #updateStartAt
     */
    private final PreparedStatement updateStartAt;
    
    /**
     * a {@code PreparedStatement} for update the {@code End_at} column and 
     * the {@code Status}to the {@code Job} table of {@code TimeClock} schema
     * @see #updateEndAtAndStatus
     */
    private final PreparedStatement updateEndAtAndStatus;
    
    /**
     * a {@code PreparedStatement} for update the {@code To_time} column 
     * to the {@code Job} table of {@code TimeClock} schema
     * @see #updateToTime
     */
    private final PreparedStatement updateToTime;
    
    /**
     * a {@code PreparedStatement} for update the {@code To_time} column 
     * to the {@code Job} table of {@code TimeClock} schema by "WIP"
     * @see #updateToTimeByWIP
     */
    private final PreparedStatement updateToTimeByWIP;
    
    /**
     * a {@code PreparedStatement} for update the {@code In_part} column 
     * to the {@code Job} table of {@code TimeClock} schema
     * @see #updateInPart
     */
    private final PreparedStatement updateInPart;
    
    /**
     * a {@code PreparedStatement} for delete the last wrong {@code Job} object
     * from {@code Job} table of {@code TimeClock} schema
     * @see #deleteLastIncorrectJob
     */
    private final PreparedStatement deleteLastIncorrectJob;
    
    /**
     * a {@code PreparedStatement} for delete the last {@code auto_increment} 
     * value of wrong {@code Job_id} from {@code Job} table of 
     * {@code TimeClock} schema
     * @see #autoIncrement
     */
    private final PreparedStatement autoIncrement;
    
    /**
     * Set the {@code JpbRepositoryJDBCImpl} object for connection to MySQL
     * database.
     * @param connection the MySQL database connection
     * @throws SQLException 
     */
    public JobRepositoryJDBCImpl(Connection connection) throws SQLException {
        this.connection = connection;
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param developerFirstName for getting id
     * @return the developer id for insert
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @return maximum {@code Job} id from {@code Job} table
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for identification the correct {@code Job}
     * @return {@code true} is if the {@code Job_number} not used
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for identification the correct {@code Job}
     * @return {@code true} is the {@code Status} column is null
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for identification the correct {@code Job}
     * @return the {@code Job}'s status("WIP" or "Done")
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for inserting
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for identification the correct {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for identification the correct {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for identification the correct {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @param job for identification the correct {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
     */
    @Override
    public void deleteLastIncorrectObject() throws SQLException {
        this.deleteLastIncorrectJob.setInt(1, getMaxId());
        this.deleteLastIncorrectJob.executeUpdate();
    }
    
    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository 
     */
    @Override
    public void autoIncrement() throws SQLException {
        this.autoIncrement.executeUpdate();
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.JobRepository}.
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.JobRepository
     */
    @Override
    public void close() throws SQLException {
        this.connection.close();
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
