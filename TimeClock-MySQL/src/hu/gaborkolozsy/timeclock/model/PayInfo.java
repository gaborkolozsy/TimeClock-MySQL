/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.model;

/**
 * This object declared all data members for payment calculating.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 */
public class PayInfo {
    
    /**
     * @see #getHourlyPay() 
     * @see #setHourlyPay(int) 
     */
    private int hourlyPay;
    
    /**
     * @see #getAverageHourlyPay() 
     * @see #setAverageHourlyPay(int) 
     */
    private int averageHourlyPay;
    
    /**
     * @see #getTotalPayment() 
     * @see #setTotalPayment(int) 
     */
    private int totalPayment;

    /**
     * An empty construstor for {@code PayInfoDAOImpl} object.
     */
    public PayInfo() {}
    
    /**
     * Set the {@code PayInfo} object.
     * 
     * @param hourlyPay the hourly pay by the specified {@code Job}
     * @param averageHourlyPay the average hourly pay by the all done 
     * {@code Job} and specified {@code Project}
     * @param totalPay total payment by the specified {@code Project}
     */
    public PayInfo(int hourlyPay, int averageHourlyPay, int totalPay) {
        this.hourlyPay = hourlyPay;
        this.averageHourlyPay = averageHourlyPay;
        this.totalPayment = totalPay;
    }

    /**
     * Get the {@code hourlyPay} by the specified {@code Job}.
     * @return {@code hourlyPay} as an integer value
     */
    public int getHourlyPay() {
        return hourlyPay;
    }

    /**
     * Set the {@code hourlyPay} data member for the specified {@code Job}.
     * @param hourlyPay hourly pay by the specified {@code Job}
     */
    public void setHourlyPay(int hourlyPay) {
        this.hourlyPay = hourlyPay;
    }

    /**
     * Get the {@code averageHourlyPay} by the specified {@code Project}.
     * @return {@code averageHourlyPay} by the specified {@code Project}
     */
    public int getAverageHourlyPay() {
        return averageHourlyPay;
    }

    /**
     * Set the {@code averageHourlyPay} by the specified {@code Project}.
     * @param averageHourlyPay average hourly pay by the specified {@code Project}
     */
    public void setAverageHourlyPay(int averageHourlyPay) {
        this.averageHourlyPay = averageHourlyPay;
    }

    /**
     * Get the {@code totalPayment} by the specified {@code Project}.
     * @return {@code totalPayment} by the specified {@code Project}
     */
    public int getTotalPayment() {
        return totalPayment;
    }

    /**
     * Set the {@code totalPayment} by the specified {@code Project}.
     * @param totalPayment total payment by the specified {@code Project}
     */
    public void setTotalPayment(int totalPayment) {
        this.totalPayment = totalPayment;
    }
    
}
