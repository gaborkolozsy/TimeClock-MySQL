/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.dao.InfoDAO;
import hu.gaborkolozsy.timeclock.model.Job;
import hu.gaborkolozsy.timeclock.model.PayInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This object implementing the {@code InfoDAO} generic interface.
 * 
 * <p>
 * It manages the relationship between the {@code PayInfo} 
 * object and the MySQL database.
 * 
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
public class PayInfoDAOImpl implements InfoDAO<PayInfo, Job> {
    
    /**
     * A {@code PreparedStatement} for a new {@code PayInfo} object.
     */
    private final PreparedStatement getAllInfo;
    
    /**
     * A {@code PreparedStatement} for {@code hourlyPay} datamember of
     * {@code PayInfo} object from the {@code Pay} and {@code Job} tables 
     * of {@code TimeClock} schema.
     */
    @Deprecated
    private final PreparedStatement getHourlyPay;
    
    /**
     * A {@code PreparedStatement} for {@code averageHourlyPay} datamember of
     * {@code PayInfo} object from the {@code Pay} and {@code Job} tables 
     * of {@code TimeClock} schema.
     */
    @Deprecated
    private final PreparedStatement getAverageHourlyPay;
    
    /**
     * A {@code PreparedStatement} for {@code totalPayment} datamember of
     * {@code PayInfo} object from the {@code Pay} and {@code Job} tables 
     * of {@code TimeClock} schema.
     */
    @Deprecated
    private final PreparedStatement getTotalPayment;
    
    /**
     * A {@code PreparedStatement} for get {@code JobId} from the 
     * {@code Pay} and {@code Job} tables of {@code TimeClock} schema.
     */
    @Deprecated
    private final PreparedStatement getJobId;
    
    /**
     * Set the {@code PayInfoDAOImpl} object for connection to MySQL database.
     * 
     * @param connection the MySQL database connection
     * @throws SQLException 
     */
    public PayInfoDAOImpl(Connection connection) throws SQLException {
        this.getAllInfo = connection.prepareStatement(
                "SELECT DISTINCT\n" +
                "(SELECT ROUND(Pay/(TIME_TO_SEC(To_time)/3600)) FROM Pay INNER JOIN Job ON Pay_id = Job_id WHERE Project = ? and Package = ? and Class = ? and Job_number = ?) AS hourlyPay,\n" +
                "(SELECT ROUND(SUM(Pay)/(SUM(TIME_TO_SEC(To_time))/3600)) FROM Pay INNER JOIN Job ON Pay_id = Job_id WHERE Project = ?) AS averageHourlyPay,\n" +
                "(SELECT ROUND(SUM(Pay)) FROM Pay INNER JOIN Job ON Pay_id = Job_id WHERE Project = ? AND Status = 'Done') AS totalPayment\n" +
                "FROM Job\n" +
                "GROUP BY Project;");
        this.getHourlyPay = connection.prepareStatement("SELECT ROUND(Pay/(TIME_TO_SEC(To_time)/3600)) AS hourlyPay FROM Pay INNER JOIN Job ON Pay_id = Job_id WHERE Job_id = ?");
        this.getAverageHourlyPay = connection.prepareStatement("SELECT ROUND(SUM(Pay)/(SUM(TIME_TO_SEC(To_time))/3600)) AS averageHourlyPay FROM Pay INNER JOIN Job ON Pay_id = Job_id WHERE Project = ?");
        this.getTotalPayment = connection.prepareStatement("SELECT ROUND(SUM(Pay)) AS totalPayment FROM Pay INNER JOIN Job ON Pay_id = Job_id WHERE Project = ? AND Status = ?");
        this.getJobId = connection.prepareStatement("SELECT Job_id FROM Job WHERE Project = ? AND Package = ? AND Class = ? AND Job_number = ?");
    }
    
    /**
     * Returns a {@code PayInfo} for display it.
     * 
     * @param e for identification the correct {@code Job}
     * @return a new {@code PayInfo} object
     * @throws SQLException
     */
    @Override
    public PayInfo getInfo(Job e) throws SQLException {
        PayInfo ret = new PayInfo();
        this.getAllInfo.setString(1, e.getProject());
        this.getAllInfo.setString(2, e.getPackage());
        this.getAllInfo.setString(3, e.getClazz());
        this.getAllInfo.setInt   (4, e.getJobNumber());
        this.getAllInfo.setString(5, e.getProject());
        this.getAllInfo.setString(6, e.getProject());
        ResultSet rs = getAllInfo.executeQuery();
        if (rs.first()) {
            ret.setHourlyPay(rs.getInt("hourlyPay"));
            ret.setAverageHourlyPay(rs.getInt("averageHourlyPay"));
            ret.setTotalPayment(rs.getInt("totalPayment")); 
        }
        return ret;
    }
    
    /**
     * Make a <code>PayInfo</code> object for display the data 
     * members on the <code>Pay</code> tab in the program window.
     * 
     * @param job for identification the correct {@code Job}
     * @param status for identification the correct {@code Job}
     * @return a new {@code PayInfo} object
     * @throws SQLException
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
    public PayInfo makePayInfo(Job job, String status) throws SQLException {
        PayInfo ret = new PayInfo();
        ret.setHourlyPay(getHourlyPay(job));
        ret.setAverageHourlyPay(getAverageHourlyPay(job.getProject()));
        ret.setTotalPayment(getTotalPayment(job.getProject(), status));
        return ret;
    }
    
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
     * @param job for identification the correct {@code Job}
     * @return {@code hourlyPay} data member of {@code PayInfo} object
     * @throws SQLException
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
    public int getHourlyPay(Job job) throws SQLException {
        this.getHourlyPay.setInt(1, getJobId(job));
        ResultSet rs = getHourlyPay.executeQuery();
        if (rs.first()) {
            return rs.getInt("hourlyPay");
        }
        return 0;
    }

    /**
     * Get the <code>averageHourlyPay</code> data member of 
     * {@code PayInfo} object by the specified project. 
     * <p>
     * This value as {@code String} displayed on the <code>Pay</code> tab 
     * in the program window.
     * 
     * @param project for selected the correct {@code Job}
     * @return {@code averageHourlyPay} data member of {@code PayInfo} 
     * object
     * @throws SQLException
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
    public int getAverageHourlyPay(String project) throws SQLException {
        this.getAverageHourlyPay.setString(1, project);
        ResultSet rs = getAverageHourlyPay.executeQuery();
        if (rs.first()) {
            return rs.getInt("averageHourlyPay");
        }
        return 0;
    }

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
     * @param project for selected the correct {@code Job}
     * @param status for selected the correct {@code Job}
     * @return {@code totalPayment} data member of {@code PayInfo} object
     * @throws SQLException
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
    public int getTotalPayment(String project, String status) throws SQLException {
        this.getTotalPayment.setString(1, project);
        this.getTotalPayment.setString(2, status);
        ResultSet rs = getTotalPayment.executeQuery();
        if (rs.first()) {
            return rs.getInt("totalPayment");
        }
        return 0;
    }
    
    /**
     * Returns the {@code Job_id} by specified <code>Job</code> object
     * from database.
     * 
     * @param job for identification the correct {@code Job}
     * @return the {@code Job_id} for identification the correct {@code Job}
     * @throws SQLException
     * @deprecated <strong>Don't use this method. Please use {@code getInfo()}
     * method instead of this.</strong>
     */
    @Deprecated
    public int getJobId(Job job) throws SQLException {
        this.getJobId.setString(1, job.getProject());
        this.getJobId.setString(2, job.getPackage());
        this.getJobId.setString(3, job.getClazz());
        this.getJobId.setInt(4, job.getJobNumber());
        ResultSet rs = getJobId.executeQuery();
        if (rs.first()) {
            return rs.getInt("Job_id");
        }
        return 0;
    }
    
    /**
     * Before program quit close all opened {@code PreparedStatement}.
     * 
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        this.getAllInfo.close();
        this.getHourlyPay.close();
        this.getAverageHourlyPay.close();
        this.getTotalPayment.close();
    }
}
