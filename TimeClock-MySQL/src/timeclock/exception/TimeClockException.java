/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package timeclock.exception;

/**
 * An exception that a method can away throw as a new exception. 
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see Exception
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
