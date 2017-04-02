/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.model;

/**
 * This object declared all data members for time calculating.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 */
public class TimeInfo {
    
    /**
     * @see #getToTime() 
     * @see #setToTime(java.lang.String) 
     */
    private String toTime;
    
    /**
     * @see #getAverageTime() 
     * @see #setAverageTime(java.lang.String) 
     */
    private String averageTime;
    
    /**
     * @see #getTotalTime() 
     * @see #setTotalTime(java.lang.String) 
     */
    private String totalTime;

    /**
     * An empty construstor for {@code TimeInfoDAOImpl} object.
     */
    public TimeInfo() {}
    
    /**
     * Set the {@code TimeInfo} object.
     * 
     * @param toTime {@code To_time} by the specified {@code Job}
     * @param averageTime {@code averageTime} by all done {@code Job}
     * @param totalTime {@code totalTime} by all done {@code Job}
     */
    public TimeInfo(String toTime, String averageTime, String totalTime) {
        this.toTime = toTime;
        this.averageTime = averageTime;
        this.totalTime = totalTime;
    }

    /**
     * Get the {@code toTime} by the specified {@code Job}.
     * @return {@code toTime} as a {@code String} value
     */
    public String getToTime() {
        return toTime;
    }

    /**
     * Set the {@code toTime} by the specified {@code Job}.
     * @param toTime period by the specified {@code Job}
     */
    public void setToTime(String toTime) {
        this.toTime = toTime;
    }
    
    /**
     * Get the {@code averageTime} by the specified {@code Project}.
     * @return {@code averageTime} as a {@code String} value
     */
    public String getAverageTime() {
        return averageTime;
    }

    /**
     * Set the {@code averageTime} by the specified {@code Project}.
     * @param averageTime average period by the specified {@code Project}.
     */
    public void setAverageTime(String averageTime) {
        this.averageTime = averageTime;
    }

    /**
     * Get the {@code totalTime} by the specified {@code Project}.
     * @return {@code totalTime} as a {@code String} value
     */
    public String getTotalTime() {
        return totalTime;
    }

    /**
     * Set the {@code totalTime} by the specified {@code Project}.
     * @param totalTime total period by the specified {@code Project}
     */
    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
    
}
