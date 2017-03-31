/*
 * Copyright (c) 2016, Gábor Kolozsy. All rights reserved.
 * 
 */
package timeclock.user;

import java.io.Serializable;

/**
 * This object declared all datamembers for user. Implements 
 * {@code Serializable}.
 * 
 * @author Kolozsy Gábor
 * @see java.io.Serializable
 */
public class User implements Serializable {
    
    /**
     * an id for identification an user
     * 
     * @see #getId() 
     * @see #setId(int) 
     */
    private int id;
    
    /**
     * @see #getUserName() 
     * @see #setUserName(java.lang.String) 
     */
    private String userName;
    
    /**
     * @see #getPassword() 
     * @see #setPassword(java.lang.String) 
     */
    private String password;
    
    /**
     * 
     * @param id identification an user
     * @param userName a possible username
     * @param password a possible password
     */
    public User(int id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * Returns the id.
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the id.
     * @param id the new id as an integer value
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Get the username.
     * @return the valid username as a {@code String} value
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * Set the new username.
     * @param userName the new username as a {@code String} value
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    /**
     * Get the password.
     * @return the valid password as a {@code String} value
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Set the new password.
     * @param password the new password as a {@code String} value
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
