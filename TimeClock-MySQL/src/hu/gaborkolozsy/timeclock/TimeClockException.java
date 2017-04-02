/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock;

/**
 * An exception that a method can away throw as a new exception. 
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 */
public class TimeClockException extends Exception {

    public TimeClockException() {
    }

    public TimeClockException(String message) {
        super(message);
    }

    public TimeClockException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeClockException(Throwable cause) {
        super(cause);
    }
}
