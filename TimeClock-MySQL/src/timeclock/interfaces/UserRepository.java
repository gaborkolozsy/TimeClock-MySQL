/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package timeclock.interfaces;

import java.io.FileNotFoundException;
import timeclock.exception.TimeClockException;
import timeclock.user.User;

/**
 * The <strong>UserRepository</strong> interface provide some method 
 * for save/update the {@code User} object to a .bin file.
 * This will ensure the correct connection.
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see timeclock.user.User
 * @see timeclock.dao.UserRepositoryBINImpl
 * @see java.io.FileNotFoundException
 * @see timeclock.exception.TimeClockException
 */
public interface UserRepository {
    
    /**
     * Return a {@code User} object by the specified user name.
     * 
     * @param userName for searching
     * @return a {@code User} object
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see timeclock.user.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see timeclock.exception.TimeClockException
     */
    User findByUserName(String userName) throws TimeClockException, FileNotFoundException;
    
    /**
     * Return a {@code User} object by the specified id.
     * 
     * @param id for identification the user
     * @return a {@code User} object
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see timeclock.user.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see timeclock.exception.TimeClockException
     */
    User findById(int id) throws TimeClockException, FileNotFoundException;
    
    /**
     * Returns {@code true} if id is valid, otherwise false.
     * 
     * @param id for searching
     * @return {@code true} if id is valid
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see timeclock.exception.TimeClockException
     */
    boolean findId(int id) throws TimeClockException, FileNotFoundException;
    
    /**
     * Save the specified a {@code User} object to a .bin file.
     * 
     * @param user for saveing
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see timeclock.user.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see timeclock.exception.TimeClockException
     */
    void save(User user) throws TimeClockException, FileNotFoundException;
    
    /**
     * Delete the {@code User} by specified id.
     * 
     * @param id for identification
     * @throws TimeClockException
     * @throws FileNotFoundException
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see timeclock.exception.TimeClockException
     * @deprecated <strong>not use this method</strong>
     */
    @Deprecated
    void delete(int id) throws TimeClockException, FileNotFoundException;
    
    /**
     * Update the {@code User} data with the specified {@code User}.
     * 
     * @param user for updateing
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see timeclock.user.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see timeclock.exception.TimeClockException
     */
    void update(User user) throws TimeClockException, FileNotFoundException;
}
