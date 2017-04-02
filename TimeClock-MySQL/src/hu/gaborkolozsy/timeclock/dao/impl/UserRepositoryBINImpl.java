/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao.impl;

import hu.gaborkolozsy.timeclock.TimeClockException;
import hu.gaborkolozsy.timeclock.dao.UserRepository;
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
 * This object implementing the {@link hu.gaborkolozsy.timeclock.daos.UserRepository}
 * interface. 
 * <p>
 * It manages the relationship between the {@code Usre} object
 * and the .bin file.
 * 
 * @author Kolozsy Gábor
 * @version 1.0
 * @see hu.gaborkolozsy.timeclock.model.User
 * @see hu.gaborkolozsy.timeclock.daos.UserRepository
 * @see java.io.File
 * @see java.io.FileInputStream
 * @see java.io.FileOutputStream
 * @see java.io.ObjectInputStream
 * @see java.io.ObjectOutputStream
 * @see java.util.List
 * @see java.util.ArrayList
 * @see java.io.IOException
 * @see java.io.FileNotFoundException
 * @see hu.gaborkolozsy.timeclock.TimeClockException
 */
public class UserRepositoryBINImpl implements UserRepository {
    
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.UserRepository}.
     * @param userName for find
     * @return a new {@code User} object
     * @throws TimeClockException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.daos.UserRepository
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.UserRepository}.
     * @param id for identification the correct user
     * @return a new {@code User} object
     * @throws TimeClockException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.daos.UserRepository
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.UserRepository}.
     * @param id for identification the correct user
     * @return {@code true}if the id is valid
     * @throws TimeClockException
     * @throws FileNotFoundException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.daos.UserRepository
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.UserRepository}.
     * @param user for saveing
     * @throws TimeClockException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.daos.UserRepository
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.UserRepository}.
     * @param id for identification the correct user
     * @throws TimeClockException
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.daos.UserRepository
     * @see hu.gaborkolozsy.timeclock.TimeClockException
     * @deprecated
     */
    @Deprecated
    @Override
    public void delete(int id) throws TimeClockException {
        if (new File(fileName).exists()) {
            List<User> list = new ArrayList<>();
            list = inputStreamList();
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
     * <strong>See >>></strong> {@link hu.gaborkolozsy.timeclock.daos.UserRepository}.
     * @param user for updateing
     * @throws TimeClockException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.daos.UserRepository
     * @see hu.gaborkolozsy.timeclock.TimeClockException
     */
    @Override
    public void update(User user) throws TimeClockException {
        List<User> list = new ArrayList<>();
        list = inputStreamList();
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
     * @return a {@code List} of users
     * @throws TimeClockException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.TimeClockException
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
     * @param list for write to file
     * @throws TimeClockException 
     * @see hu.gaborkolozsy.timeclock.model.User
     * @see hu.gaborkolozsy.timeclock.TimeClockException
     */
    private void writeToFile(List<User> list) throws TimeClockException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(list);
        } catch (IOException ex) {
            throw new TimeClockException(ex);
        }
    }
    
}
