/*
 * Copyright (c) 2016, Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Store the last valid {@code Job_number}, next action(which tab open
 * {@code Start} or {@code End} by next program start,
 * {@code Status}, auto connection(true, false). 
 * It is possible to add new data to default and store this. 
 * <strong>See ADD tab</strong> in program.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 0.0.1-SNAPSHOT
 * @see File
 * @see FileInputStream
 * @see FileNotFoundException
 * @see FileOutputStream
 * @see IOException
 * @see Properties
 */
public class Config {
    
    /**
     * a new {@code Properties} object
     */
    private final Properties properties = new Properties();
    
    /**
     * the saved file's name as a {@code String}
     */
    private final String fileName = "timeclock.ini";
    
    /**
     * Save a key-value pair in the config file.
     * 
     * @param key the key
     * @param value the value
     * @throws IOException 
     * @see Properties
     */
    public void saveValue(String key, String value) throws IOException {
        properties.setProperty(key, value);
        properties.store(new FileOutputStream(fileName), "Config for TimeClock");
    }
    
    /**
     * Returns the value by the specified key. 
     * 
     * @param key the key
     * @return the value by the specified key
     * @throws IOException 
     * @see Properties
     */
    public String getValue(String key) throws IOException {
        properties.load(new FileInputStream(fileName));
        return properties.getProperty(key);
    }
    
    /**
     * Returns {@code true} if and only if the specified object is a key 
     * in this hashtable.
     * 
     * @param key the key
     * @return return true if the properties list contains this specified key
     * false otherwise
     * @throws FileNotFoundException
     * @throws IOException 
     * @throws NullPointerException if the key is {@code null}
     * @see Properties
     */
    public boolean isKey(String key) throws IOException {
        properties.load(new FileInputStream(fileName));
        return properties.containsKey(key);
    }
    
    /**
     * Return {@code true} if the specified file exists, {@code false}
     * otherwise.
     * 
     * @return {@code true} if the specified file exists
     * @see java.io.File#exists
     */
    public boolean fileExists() {
        return new File(fileName).exists();
    }
    
}
