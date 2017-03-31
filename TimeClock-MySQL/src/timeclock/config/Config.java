/*
 * Copyright (c) 2016, Gábor Kolozsy. All rights reserved.
 * 
 */
package timeclock.config;

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
 * @author Kolozsy Gábor
 * @version 1.0
 * @see java.util.Properties
 * @see java.io.File
 * @see java.io.FileInputStream
 * @see java.io.FileOutputStream
 * @see java.io.IOException
 * @see java.io.FileNotFoundException
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
     * @see java.util.Properties#setProperty
     * @see java.util.Properties#store
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
     * @see java.util.Properties#load
     * @see java.util.Properties#getProperty
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
     * @see java.util.Properties#load
     * @see java.util.Properties#containsKey
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
