/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.daos;

import java.io.FileNotFoundException;
import hu.gaborkolozsy.timeclock.TimeClockException;
import hu.gaborkolozsy.timeclock.model.User;

/**
 * The <strong>UserRepository</strong> interface provide some method 
 * for save/update the {@code User} object to a .bin file.
 * This will ensure the correct connection.
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see hu.gaborkolozsy.timeclock.model.User
 * @see timeclock.dao.UserRepositoryBINImpl
 * @see java.io.FileNotFoundException
 * @see hu.gaborkolozsy.timeclock.TimeClockException
 */
public interface UserRepository {
    
    /**
     * Return a {@code User} object by the specified user name.
     * 
     * @param userName for searching
     * @return a {@code User} object
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see hu.gaborkolozsy.timeclock.TimeClockException
     */
    User findByUserName(String userName) throws TimeClockException, FileNotFoundException;
    
    /**
     * Return a {@code User} object by the specified id.
     * 
     * @param id for identification the user
     * @return a {@code User} object
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * @see hu.gaborkolozsy.timeclock.TimeClockException
     */
    boolean findId(int id) throws TimeClockException, FileNotFoundException;
    
    /**
     * Save the specified a {@code User} object to a .bin file.
     * 
     * @param user for saveing
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see timeclock.dao.UserRepositoryBINImpl
     * @see java.io.FileNotFoundException
     * @see hu.gaborkolozsy.timeclock.TimeClockException
     */
    void update(User user) throws TimeClockException, FileNotFoundException;
}
