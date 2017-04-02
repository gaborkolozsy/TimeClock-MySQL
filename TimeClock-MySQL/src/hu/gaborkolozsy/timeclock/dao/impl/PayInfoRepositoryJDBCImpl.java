/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.dao.PayInfoRepository;
import hu.gaborkolozsy.timeclock.model.Job;
import hu.gaborkolozsy.timeclock.model.PayInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This object implementing the {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}
 * generic interface. 
 * <p>
 * It manages the relationship between the {@code PayInfo} 
 * object and the MySQL database.
 * <p>
 * This class provide a few deprecated methods. Please not used these.
 * Has a better alternative.
 * 
 * @author Kolozsy Gábor
 * @version 2.1
 * @see hu.gaborkolozsy.timeclock.model.Job
 * @see hu.gaborkolozsy.timeclock.model.PayInfo
 * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
 * @see java.sql.Connection
 * @see java.sql.PreparedStatement
 * @see java.sql.ResultSet
 * @see java.sql.SQLException
 */
public class PayInfoRepositoryJDBCImpl implements PayInfoRepository<PayInfo,Job> {
    
    /**
     * MySQL database connection
     */
    private final Connection connection;
    
    /**
     * a {@code PreparedStatement} for a new {@code PayInfo} object
     */
    private final PreparedStatement getAllInfo;
    
    /**
     * a {@code PreparedStatement} for {@code hourlyPay} datamember of
     * {@code PayInfo} object from the {@code Pay} and {@code Job} tables 
     * of {@code TimeClock} schema
     */
    @Deprecated
    private final PreparedStatement getHourlyPay;
    
    /**
     * a {@code PreparedStatement} for {@code averageHourlyPay} datamember of
     * {@code PayInfo} object from the {@code Pay} and {@code Job} tables 
     * of {@code TimeClock} schema
     */
    @Deprecated
    private final PreparedStatement getAverageHourlyPay;
    
    /**
     * a {@code PreparedStatement} for {@code totalPayment} datamember of
     * {@code PayInfo} object from the {@code Pay} and {@code Job} tables 
     * of {@code TimeClock} schema
     */
    @Deprecated
    private final PreparedStatement getTotalPayment;
    
    /**
     * a {@code PreparedStatement} for get {@code JobId} from the 
     * {@code Pay} and {@code Job} tables of {@code TimeClock} schema
     */
    @Deprecated
    private final PreparedStatement getJobId;
    
    /**
     * Set the {@code PayInfoRepositoryJDBCImpl} object for connection
     * to MySQL database
     * 
     * @param connection the MySQL database connection
     * @throws SQLException 
     */
    public PayInfoRepositoryJDBCImpl(Connection connection) throws SQLException {
        this.connection = connection;
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}.
     * 
     * @param e for identification the correct {@code Job}
     * 
     * @return a new {@code PayInfo} object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}.
     * 
     * @param job for identification the correct {@code Job}
     * @param status for identification the correct {@code Job}
     * 
     * @return a new {@code PayInfo} object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
    public PayInfo makePayInfo(Job job, String status) throws SQLException {
        PayInfo ret = new PayInfo();
        ret.setHourlyPay(getHourlyPay(job));
        ret.setAverageHourlyPay(getAverageHourlyPay(job.getProject()));
        ret.setTotalPayment(getTotalPayment(job.getProject(), status));
        return ret;
    }
    
    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}.
     * 
     * @param job for identification the correct {@code Job}
     * 
     * @return {@code hourlyPay} data member of {@code PayInfo} object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
    public int getHourlyPay(Job job) throws SQLException {
        this.getHourlyPay.setInt(1, getJobId(job));
        ResultSet rs = getHourlyPay.executeQuery();
        if (rs.first()) {
            return rs.getInt("hourlyPay");
        }
        return 0;
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}.
     * 
     * @param project for selected the correct {@code Job}
     * 
     * @return {@code averageHourlyPay} data member of {@code PayInfo} 
     * object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
    public int getAverageHourlyPay(String project) throws SQLException {
        this.getAverageHourlyPay.setString(1, project);
        ResultSet rs = getAverageHourlyPay.executeQuery();
        if (rs.first()) {
            return rs.getInt("averageHourlyPay");
        }
        return 0;
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}.
     * 
     * @param project for selected the correct {@code Job}
     * @param status for selected the correct {@code Job}
     * 
     * @return {@code totalPayment} data member of {@code PayInfo} object
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}.
     * 
     * @param job for identification the correct {@code Job}
     * 
     * @return the {@code Job_id} for identification the correct {@code Job}
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    @Override
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayInfoRepository}.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayInfoRepository
     */
    @Override
    public void close() throws SQLException {
        this.connection.close();
        this.getHourlyPay.close();
        this.getAverageHourlyPay.close();
        this.getTotalPayment.close();
    }
}
