/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao;

import hu.gaborkolozsy.timeclock.TimeClockException;
import hu.gaborkolozsy.timeclock.model.User;
import java.io.FileNotFoundException;

/**
 * The {@code UserDAO} interface provide some method 
 * for save/update the {@code User} object to the {@code data.bin} file.
 * This will ensure the correct connection.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @see TimeClockException
 * @see User
 * @see FileNotFoundException
 */
public interface UserDAO {
    
    /**
     * Return a {@code User} object by the specified user name.
     * 
     * @param userName for searching
     * @return a {@code User} object
     * @throws TimeClockException
     * @throws FileNotFoundException 
     */
    User findByUserName(String userName) throws TimeClockException, FileNotFoundException;
    
    /**
     * Return a {@code User} object by the specified id.
     * 
     * @param id for identification the user
     * @return a {@code User} object
     * @throws TimeClockException
     * @throws FileNotFoundException
     */
    User findById(int id) throws TimeClockException, FileNotFoundException;
    
    /**
     * Returns {@code true} if id is valid, otherwise false.
     * 
     * @param id for searching
     * @return {@code true} if id is valid
     * @throws TimeClockException
     * @throws FileNotFoundException
     */
    boolean findId(int id) throws TimeClockException, FileNotFoundException;
    
    /**
     * Save the specified a {@code User} object to a .bin file.
     * 
     * @param user for saveing
     * @throws TimeClockException
     * @throws FileNotFoundException
     */
    void save(User user) throws TimeClockException, FileNotFoundException;
    
    /**
     * Delete the {@code User} by specified id.
     * 
     * @param id for identification
     * @throws TimeClockException
     * @throws FileNotFoundException
     * @deprecated <strong>Don't use this method!</strong>
     */
    @Deprecated
    void delete(int id) throws TimeClockException, FileNotFoundException;
    
    /**
     * Update the {@code User} data with the specified {@code User}.
     * 
     * @param user for updating
     * @throws TimeClockException
     * @throws FileNotFoundException
     */
    void update(User user) throws TimeClockException, FileNotFoundException;
    
}
