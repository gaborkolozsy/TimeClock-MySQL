/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.TimeClockException;
import hu.gaborkolozsy.timeclock.dao.UserDAO;
import hu.gaborkolozsy.timeclock.model.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This object implementing the {@code UserDAO} interface. 
 * 
 * <p>
 * It manages the relationship between the {@code Usre} object
 * and the .bin file.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @see TimeClockException
 * @see User
 * @see File
 * @see FileInputStream
 * @see FileNotFoundException
 * @see FileOutputStream
 * @see IOException
 * @see ObjectInputStream
 * @see ObjectOutputStream
 * @see ArrayList
 * @see List
 */
public class UserDAOImpl implements UserDAO {
    
    /**
     * The file name.
     */
    private final String fileName = "user.bin";
    
    /**
     * @see #save
     * @see #delete
     */
    private boolean validID = true;
    
    /**
     * Return a {@code User} object by the specified user name.
     * 
     * @param userName for find
     * @return a new {@code User} object
     * @throws TimeClockException
     */
    @Override
    public User findByUserName(String userName) throws TimeClockException {
        for (User user : inputStreamList()) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Return a {@code User} object by the specified id.
     * 
     * @param id for identification the correct user
     * @return a new {@code User} object
     * @throws TimeClockException
     */
    @Override
    public User findById(int id) throws TimeClockException {
        for (User user : inputStreamList()) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Returns {@code true} if id is valid, otherwise false.
     * 
     * @param id for identification the correct user
     * @return {@code true}if the id is valid
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see User
     */
    @Override
    public boolean findId(int id) throws TimeClockException, FileNotFoundException {
        for (User user : inputStreamList()) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Save the specified a {@code User} object to the data.bin file.
     * 
     * @param user for saveing
     * @throws TimeClockException
     */
    @Override
    public void save(User user) throws TimeClockException {
        List<User> list = new ArrayList<>();
        if (new File(fileName).exists()) {
            list = inputStreamList();
            validID = true;
            for (User u : list) {
                if (u.getId() == user.getId()) {
                    validID = false;
                    throw new TimeClockException("Valid ID: " + user.getId());
                }
            }
        } 
        
        if (validID) { 
            list.add(user);
            writeToFile(list);
        }
    }

    /**
     * Delete the {@code User} by specified id.
     * 
     * @param id for identification the correct user
     * @throws TimeClockException
     * @see User
     * @deprecated <strong>Please don't use this method!</strong>
     */
    @Deprecated
    @Override
    public void delete(int id) throws TimeClockException {
        if (new File(fileName).exists()) {
            List<User> list = inputStreamList();
            User delete = null;
            validID = false;
            for (User user : list) {
                if (user.getId() == id) {
                    delete = user;
                    validID = true;
                    break;
                }
            }
            
            if (validID && delete != null) {
                list.remove(delete);
                writeToFile(list);
            }
        }
    }

    /**
     * Update the {@code User} data with the specified {@code User}.
     * 
     * @param user for updateing
     * @throws TimeClockException
     */
    @Override
    public void update(User user) throws TimeClockException {
        List<User> list = inputStreamList();
        for (User u : list) {
            if (u.getId() == user.getId()) {
                u.setUserName(user.getUserName());
                u.setPassword(user.getPassword());
                writeToFile(list);
                break;
            }
        }
    }
    
    /**
     * Returns a {@code List} of users for some method.
     * 
     * @return a {@code List} of users
     * @throws TimeClockException
     */
    private List<User> inputStreamList() throws TimeClockException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return new ArrayList<>((List<User>) in.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            throw new TimeClockException(ex);
        }
    }
    
    /**
     * Users write to file.
     * 
     * @param list for write to file
     * @throws TimeClockException
     */
    private void writeToFile(List<User> list) throws TimeClockException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(list);
        } catch (IOException ex) {
            throw new TimeClockException(ex);
        }
    }
    
}
