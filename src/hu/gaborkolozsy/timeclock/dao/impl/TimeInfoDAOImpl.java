/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.dao.InfoDAO;
import hu.gaborkolozsy.timeclock.model.Job;
import hu.gaborkolozsy.timeclock.model.TimeInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This object implementing the {@code InfoDAO generic interface. 
 * <p>
 * It manages the relationship between the {@code TimeInfo} 
 * object and the MySQL database.
 * <p>
 * This class provide a few deprecated methods. Please use {@code getInfo()}
 * method instead of these.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @see Connection
 * @see PreparedStatement
 * @see ResultSet
 * @see SQLException
 */
public class TimeInfoDAOImpl implements InfoDAO<TimeInfo, Job> {
    
    /**
     * A {@code PreparedStatement} for a new {@code PayInfo} object.
     */
    private final PreparedStatement getAllInfo;
    
    /**
     * A {@code PreparedStatement} for {@code toTime} datamember of
     * {@code TimeInfo} object from the {@code Job} table
     * of {@code TimeClock} schema.
     */
    @Deprecated
    private final PreparedStatement getTime;
    
    /**
     * A {@code PreparedStatement} for {@code averageTime} datamember of
     * {@code TimeInfo} object from the {@code Job} table
     * of {@code TimeClock} schema.
     */
    @Deprecated
    private final PreparedStatement getAverageTime;
    
    /**
     * A {@code PreparedStatement} for {@code totalTime} datamember of
     * {@code TimeInfo} object from the {@code Job} table
     * of {@code TimeClock} schema.
     */
    @Deprecated
    private final PreparedStatement getTotalTime;
    
    /**
     * Set the {@code TimeInfoAOImpl} object for connection to MySQL database.
     * 
     * @param connection MySQL database connection
     * @throws SQLException 
     */
    public TimeInfoDAOImpl(Connection connection) throws SQLException {
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
     * Returns a {@code TimeInfo} for display it.
     * 
     * @param e for identification the correct {@code Job}
     * @return a new {@code TimeInfo} object
     * @throws SQLException
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
     * Make a {@code TimeInfo} object for displayed the data 
     * members on the {@code Time} tab in the program window.
     * 
     * @param job for identification the correct {@code Job}
     * @param status for identification the correct {@code Job}
     * @return a new {@code TimeInfo} object
     * @throws SQLException
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
    public TimeInfo makeTimeInfo(Job job, String status) throws SQLException {
        TimeInfo ret = new TimeInfo();
        ret.setToTime(getToTime(job));
        ret.setAverageTime(getAverageTime(job.getProject()));
        ret.setTotalTime(getTotalTime(job.getProject(), status));
        return ret;
    }
    
    /**
     * Get the {@code toTime} data member of {@code TimeInfo}
     * object by the specified job if done it.
     * 
     * @param job for identification the correct {@code Job}
     * @return value of {@code To_time} column as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
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
     * Get the {@code averageTime} data member of {@code TimeInfo}
     * object by the selected {@code Project} if done it.
     * 
     * @param Project for selected the correct {@code Job}
     * @return value of {@code averageTime} as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
    public String getAverageTime(String Project) throws SQLException {
        this.getAverageTime.setString(1, Project);
        ResultSet rs = getAverageTime.executeQuery();
        if (rs.first()) {
            return rs.getString("averageTime");
        }
        return null;
    }

    /**
     * Get the {@code totalTime} data member of {@code TimeInfo}
     * object by the selected {@code Project} if done it.
     * 
     * @param Project for selected the correct {@code Job}
     * @param status the job status
     * @return value of {@code averageTime} as a {@code String} from 
     * {@code Job} table of {@code TimeClock} schema
     * @throws SQLException 
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
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
     * Before program quit close all opened {@code PreparedStatement}.
     * 
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        this.getAllInfo.close();
        this.getTime.close();
        this.getAverageTime.close();
        this.getTotalTime.close();
    }
    
}
