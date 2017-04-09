/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.dao.CommonDAO;
import hu.gaborkolozsy.timeclock.model.Pay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This object implementing the {@code PayDAO} interface. 
 * 
 * <p>
 * It manages the relationship between the {@code Pay} object
 * and the MySQL database. 
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @see Connection
 * @see PreparedStatement
 * @see ResultSet
 * @see SQLException
 */
public class PayCommonDAOImpl implements CommonDAO<Pay> {

    /**
     * A {@code PreparedStatement} for get maximum id from {@code Pay} 
     * table of {@code TimeClock} schema.
     * @see #getMaxPayId
     */
    private final PreparedStatement getMaxPayId;
    
    /**
     * A {@code PreparedStatement} for insert a new {@code Pay} object to the 
     * {@code Pay} table of {@code TimeClock} schema.
     * @see #insert
     */
    private final PreparedStatement insert;
    
    /**
     * A {@code PreparedStatement} for delete the last wrong {@code Pay} object
     * from {@code Pay} table of {@code TimeClock} schema.
     * @see #deleteLastIncorrectPay
     */
    private final PreparedStatement deleteLastIncorrectPay;
    
    /**
     * A {@code PreparedStatement} for delete the last {@code auto_increment} 
     * value of wrong {@code Pay_id} from {@code Pay} table of 
     * {@code TimeClock} schema.
     * @see #autoIncrement
     */
    private final PreparedStatement autoIncrement;

    /**
     * Set the {@code PayCommonDAOImpl} object for connection to MySQL database.
     * 
     * @param connection the MySQL database connection
     * @throws SQLException 
     */
    public PayCommonDAOImpl(Connection connection) throws SQLException {
        this.getMaxPayId = connection.prepareStatement("SELECT MAX(Pay_id) FROM Pay");
        this.insert = connection.prepareStatement("INSERT INTO Pay (Pay, Currency) VALUES (?, ?)");
        this.deleteLastIncorrectPay = connection.prepareStatement("DELETE FROM Pay WHERE Pay_id = ?");
        this.autoIncrement = connection.prepareStatement("ALTER TABLE Pay AUTO_INCREMENT = 1");
    }

    /**
     * Returns the maximum id from the {@code Pay} table of {@code TimeClock}
     * schema from MySQL database.
     * 
     * @throws SQLException
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
     * Insert E {@code Object} into any table of {@code TimeClock} schema 
     * in the MySQL database.
     * 
     * @param pay for inserting
     * @throws SQLException
     */
    @Override
    public void insert(Pay pay) throws SQLException {
        this.insert.setDouble(1, pay.getPay());
        this.insert.setString(2, pay.getCurrency());
        this.insert.executeUpdate();
    }

    /**
     * Delete the last wrong inserted object from {@code TimeClock} schema
     * in the MySQL database.
     * 
     * @throws SQLException
     */
    @Override
    public void deleteLastIncorrectObject() throws SQLException {
        this.deleteLastIncorrectPay.setInt(1, getMaxId());
        this.deleteLastIncorrectPay.executeUpdate();
    }
    
    /**
     * After the deleted last wrong object from database, 
     * delete the last {@code auto-increment} id number too.
     * 
     * @throws SQLException 
     * @see hu.gaborkolozsy.timeclock.daos.PayRepository 
     */
    @Override
    public void autoIncrement() throws SQLException {
        this.autoIncrement.executeUpdate();
    }

    /**
     * Before program quit close all opened <code>PreparedStatement</code>.
     * 
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        this.insert.close();
        this.deleteLastIncorrectPay.close();
        this.autoIncrement.close();
    }
    
}
