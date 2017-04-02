/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.dao.TimeInfoRepository;
import hu.gaborkolozsy.timeclock.model.Job;
import hu.gaborkolozsy.timeclock.model.TimeInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This object implementing the {@link hu.gaborkolozsy.timeclock.daos.TimeInfoRepository}
 * generic interface. 
 * <p>
 * It manages the relationship between the {@code TimeInfo} 
 * object and the MySQL database.
 * <p>
 * This class provide a few deprecated methods. Please not used these.
 * Has a better alternative.
 * 
 * @author Kolozsy Gábor
 * @version 2.1
 * @see hu.gaborkolozsy.timeclock.model.TimeInfo
 * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
 * @see java.sql.Connection
 * @see java.sql.PreparedStatement
 * @see java.sql.ResultSet
 * @see java.sql.SQLException
 */
public class TimeInfoRepositoryJDBCImpl implements TimeInfoRepository<TimeInfo,Job> {
    
    /**
     * MySQL database connection
     */
    private final Connection connection;
    
    /**
     * a {@code PreparedStatement} for a new {@code PayInfo} object
     */
    private final PreparedStatement getAllInfo;
    
    /**
     * a {@code PreparedStatement} for {@code toTime} datamember of
     * {@code TimeInfo} object from the {@code Job} table
     * of {@code TimeClock} schema
     */
    @Deprecated
    private final PreparedStatement getTime;
    
    /**
     * a {@code PreparedStatement} for {@code averageTime} datamember of
     * {@code TimeInfo} object from the {@code Job} table
     * of {@code TimeClock} schema
     */
    @Deprecated
    private final PreparedStatement getAverageTime;
    
    /**
     * a {@code PreparedStatement} for {@code totalTime} datamember of
     * {@code TimeInfo} object from the {@code Job} table
     * of {@code TimeClock} schema
     */
    @Deprecated
    private final PreparedStatement getTotalTime;
    
    /**
     * Set the {@code TimeInfoRepositoryJDBCImpl} object for connection
     * to MySQL database
     * 
     * @param connection MySQL database connection
     * @throws SQLException 
     */
    public TimeInfoRepositoryJDBCImpl(Connection connection) throws SQLException {
        this.connection = connection;
        this.getAllInfo = connection.prepareStatement("SELECT \n" +
                                                      "(SELECT CAST(To_time AS CHAR) FROM Job WHERE Project = ? and Package = ? and Class = ? and Job_number = ?) AS To_time,\n" +
                                                      "(SELECT CONVERT(SEC_TO_TIME(ROUND(SUM(TIME_TO_SEC(To_time))/COUNT(To_time))),CHAR) FROM Job WHERE Project = ?) AS averageTime,\n" +
                                                      "(SELECT CONVERT(SEC_TO_TIME(SUM(TIME_TO_SEC(To_time))),CHAR) FROM Job WHERE Project = ? AND Status = 'Done') AS totalTime\n" +
                                                      "FROM Job\n" +
                                                      "GROUP BY Project;");
        this.getTime = connection.prepareStatement("SELECT CAST(To_time AS CHAR) AS To_time FROM Job WHERE Project = ? and Package = ? and Class = ? and Job_number = ?");
        this.getAverageTime = connection.prepareStatement("SELECT CONVERT(SEC_TO_TIME(ROUND(SUM(TIME_TO_SEC(To_time))/COUNT(To_time))),CHAR) AS averageTime FROM Job WHERE Project = ?");
        this.getTotalTime = connection.prepareStatement("SELECT CONVERT(SEC_TO_TIME(SUM(TIME_TO_SEC(To_time))),CHAR) AS totalTime FROM Job WHERE Project = ? AND Status = ?");
    }
    
    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.TimeInfoRepository}.
     * 
     * @param e for identification the correct {@code Job}
     * 
     * @return a new {@code TimeInfo} object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     */
    @Override
    public TimeInfo getInfo(Job e) throws SQLException {
        TimeInfo ret = new TimeInfo();
        this.getAllInfo.setString(1, e.getProject());
        this.getAllInfo.setString(2, e.getPackage());
        this.getAllInfo.setString(3, e.getClazz());
        this.getAllInfo.setInt   (4, e.getJobNumber());
        this.getAllInfo.setString(5, e.getProject());
        this.getAllInfo.setString(6, e.getProject());
        ResultSet rs = getAllInfo.executeQuery();
        if (rs.first()) {
            ret.setToTime(rs.getString("To_time"));
            ret.setAverageTime(rs.getString("averageTime"));
            ret.setTotalTime(rs.getString("totalTime"));
        }
        return ret;
    }
    
    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.TimeInfoRepository}.
     * 
     * @param job for identification the correct {@code Job}
     * @param status for identification the correct {@code Job}
     * 
     * @return a new {@code TimeInfo} object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
    public TimeInfo makeTimeInfo(Job job, String status) throws SQLException {
        TimeInfo ret = new TimeInfo();
        ret.setToTime(getToTime(job));
        ret.setAverageTime(getAverageTime(job.getProject()));
        ret.setTotalTime(getTotalTime(job.getProject(), status));
        return ret;
    }
    
    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.TimeInfoRepository}.
     * 
     * @param job for identification the correct {@code Job}
     * 
     * @return value of {@code To_time} column as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
    public String getToTime(Job job) throws SQLException {
        this.getTime.setString(1, job.getProject());
        this.getTime.setString(2, job.getPackage());
        this.getTime.setString(3, job.getClazz());
        this.getTime.setInt(4, job.getJobNumber());
        ResultSet rs = getTime.executeQuery();
        if (rs.first()) {
            return rs.getString("To_time");
        }
        return null;
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.TimeInfoRepository}.
     * 
     * @param Project for selected the correct {@code Job}
     * 
     * @return value of {@code averageTime} as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
    public String getAverageTime(String Project) throws SQLException {
        this.getAverageTime.setString(1, Project);
        ResultSet rs = getAverageTime.executeQuery();
        if (rs.first()) {
            return rs.getString("averageTime");
        }
        return null;
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.TimeInfoRepository}.
     * 
     * @param Project for selected the correct {@code Job}
     * 
     * @return value of {@code averageTime} as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
    public String getTotalTime(String Project, String status) throws SQLException {
        this.getTotalTime.setString(1, Project);
        this.getTotalTime.setString(2, status);
        ResultSet rs = getTotalTime.executeQuery();
        if (rs.first()) {
            return rs.getString("totalTime");
        }
        return null;
    }
    
    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.TimeInfoRepository}.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.TimeInfoRepository
     */
    @Override
    public void close() throws SQLException {
        this.connection.close();
        this.getTime.close();
        this.getAverageTime.close();
        this.getTotalTime.close();
    }
    
}
