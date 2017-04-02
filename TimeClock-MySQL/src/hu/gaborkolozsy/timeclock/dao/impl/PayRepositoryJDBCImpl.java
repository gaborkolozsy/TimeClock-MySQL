/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.dao.PayRepository;
import hu.gaborkolozsy.timeclock.model.Pay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This object implementing the {@link hu.gaborkolozsy.timeclock.daos.PayRepository}
 * interface. 
 * <p>
 * It manages the relationship between the {@code Pay} object
 * and the MySQL database. 
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see hu.gaborkolozsy.timeclock.model.Pay
 * @see hu.gaborkolozsy.timeclock.daos.PayRepository
 * @see java.sql.Connection
 * @see java.sql.PreparedStatement
 * @see java.sql.ResultSet
 * @see java.sql.SQLException
 */
public class PayRepositoryJDBCImpl implements PayRepository<Pay> {

    /**
     * MySQL database connection.
     */
    private final Connection connection;
    
    /**
     * a {@code PreparedStatement} for get maximum id from {@code Pay} 
     * table of {@code TimeClock} schema
     * @see #getMaxPayId
     */
    private final PreparedStatement getMaxPayId;
    
    /**
     * a {@code PreparedStatement} for insert a new {@code Pay} object to the 
     * {@code Pay} table of {@code TimeClock} schema
     * @see #insert
     */
    private final PreparedStatement insert;
    
    /**
     * a {@code PreparedStatement} for delete the last wrong {@code Pay} object
     * from {@code Pay} table of {@code TimeClock} schema
     * @see #deleteLastIncorrectPay
     */
    private final PreparedStatement deleteLastIncorrectPay;
    
    /**
     * a {@code PreparedStatement} for delete the last {@code auto_increment} 
     * value of wrong {@code Pay_id} from {@code Pay} table of 
     * {@code TimeClock} schema
     * @see #autoIncrement
     */
    private final PreparedStatement autoIncrement;

    /**
     * Set the {@code PayRepositoryJDBCImpl} object for connection to MySQL
     * database.
     * 
     * @param connection the MySQL database connection
     * @throws SQLException 
     */
    public PayRepositoryJDBCImpl(Connection connection) throws SQLException {
        this.connection = connection;
        this.getMaxPayId = connection.prepareStatement("SELECT MAX(Pay_id) FROM Pay");
        this.insert = connection.prepareStatement("INSERT INTO Pay (Pay, Currency) VALUES (?, ?)");
        this.deleteLastIncorrectPay = connection.prepareStatement("DELETE FROM Pay WHERE Pay_id = ?");
        this.autoIncrement = connection.prepareStatement("ALTER TABLE Pay AUTO_INCREMENT = 1");
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayRepository}.
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.model.Pay
     * @see hu.gaborkolozsy.timeclock.daos.PayRepository
     */
    @Override
    public int getMaxId() throws SQLException {
        ResultSet rs = getMaxPayId.executeQuery();
        if (rs.first()) {
            return rs.getInt(1);
        }
        return 0;
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayRepository}.
     * @param pay for inserting
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayRepository
     */
    @Override
    public void insert(Pay pay) throws SQLException {
        this.insert.setDouble(1, pay.getPay());
        this.insert.setString(2, pay.getCurrency());
        this.insert.executeUpdate();
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayRepository}.
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayRepository
     */
    @Override
    public void deleteLastIncorrectObject() throws SQLException {
        this.deleteLastIncorrectPay.setInt(1, getMaxId());
        this.deleteLastIncorrectPay.executeUpdate();
    }
    
    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayRepository}.
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayRepository 
     */
    @Override
    public void autoIncrement() throws SQLException {
        this.autoIncrement.executeUpdate();
    }

    /**
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.PayRepository}.
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayRepository
     */
    @Override
    public void close() throws SQLException {
        this.connection.close();
        this.insert.close();
        this.deleteLastIncorrectPay.close();
        this.autoIncrement.close();
    }
    
}
