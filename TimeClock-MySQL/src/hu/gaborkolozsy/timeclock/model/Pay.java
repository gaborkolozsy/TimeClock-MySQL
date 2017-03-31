/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.model;

/**
 * This object declared all datamembers for payment. Store in the database
 * and use this by calculating the {@code Hourly_pay}, {@code averageHourlyPay} 
 * and {@code totalPay}.
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see timeclock.interfaces.PayRepository
 * @see timeclock.dao.PayRepositoryJDBCImpl
 */
public class Pay {
    
    /**
     * @see #getPay()
     */
    private final double pay;
    
    /**
     * @see #getCurrency() 
     */
    private final String currency;
    
    /**
     * Set the {@code Pay} object.
     * 
     * @param pay payment for the specified {@code Job}
     * @param currency the actual {@code currency} code
     */
    public Pay(double pay, String currency) {
        this.pay = pay;
        this.currency = currency;
    }

    /**
     * Get the pay by specified {@code Job}.
     * 
     * @return pay for job as double value
     */
    public double getPay() {
        return pay;
    }

    /**
     * Get the {@code currency}'s short code by the specified {@code Job}.
     * 
     * @return {@code currency} short code
     */
    public String getCurrency() {
        return currency;
    }
    
}
