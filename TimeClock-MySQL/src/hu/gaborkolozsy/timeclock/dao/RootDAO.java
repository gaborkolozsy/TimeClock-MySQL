/*
 * Copyright (c) 2016, by Gábor Kolozsy. All rights reserved.
 * 
 */
package hu.gaborkolozsy.timeclock.dao;

import java.sql.SQLException;

/**
 * The <strong>RootDAO</strong> common interface. 
 * <p>
 * The <strong>RootDAO</strong> interface provide a method for close the MySQL 
 * database connection.
 * 
 * @author Gabor Kolozsy (gabor.kolozsy.development@gmail.com)
 * @since 1.3.1-SNAPSHOT
 * @see SQLException
 */
public interface RootDAO {
    
    /**
     * Before program quit close all opened <code>PreparedStatement</code>.
     * 
     * @throws SQLException
     */
    void close() throws SQLException;

}
