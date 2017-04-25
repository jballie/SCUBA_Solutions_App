/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database Connection for SCUBA Solutions. 
 * Contains options for connection to the remote database
 * or the facility's own local host.
 * @author Jonathan Balliet, Samuel Brock
 */
public final class DbConnection 
{

    private static final String DB_PROPERTIES_FILE = "src\\scuba\\solutions\\database\\database.properties";

    private static DbConnection me = null;

    private Connection dbConn = null;

    private DbConnection(String dbPropertiesFile) throws FileNotFoundException, IOException,
                    SQLException 
    {

        Properties p = new Properties();
        p.load(new FileInputStream(dbPropertiesFile));

        String username = "";
        String password = "";
        String url = "";
        /*
        // Local host connection
        username = p.getProperty("localhost.username");
        password = p.getProperty("localhost.password");
        url = p.getProperty("localhost.url");
        */

        //Remote connection
        username = p.getProperty("remote.username");
        password = p.getProperty("remote.password");
        url = p.getProperty("remote.url");

    dbConn = DriverManager.getConnection(url, username, password);
    }

    public Connection getConnection() {
            return dbConn;
    }

    public static DbConnection accessDbConnection()
                    throws FileNotFoundException, IOException, SQLException {
            return accessDbConnection(DB_PROPERTIES_FILE);
    }

    public static DbConnection accessDbConnection(String propertiesFilename) throws FileNotFoundException, IOException, SQLException {
            if (me == null) {
                    me = new DbConnection(propertiesFilename);
            }
            return me;
    }

    public void disconnect() 
    {
            me = null;
            if (dbConn != null) {
                    try {
                            dbConn.close();
                    } catch (SQLException e) {
                            e.printStackTrace();
                    } finally {
                            dbConn = null;
                    }
            }
    }

}