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
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 *
 * @author Jon
 */


public final class DbConnection {

		private static final String DB_PROPERTIES_FILE = "database.properties";

		private static DbConnection me = null;

		private Connection dbConn = null;

		private DbConnection(String dbPropertiesFile) throws FileNotFoundException, IOException,
				SQLException {

			//Properties p = new Properties();
			//p.load(new FileInputStream(dbPropertiesFile));

			String username = "";
			String password = "";
			String url = "jdbc:derby:ScubaSolutions;create=true";
			String driver = "org.apache.derby.jdbc.EmbeddedDriver";
			//url = p.getProperty("jdbc.url");
			//driver = p.getProperty("jdbc.driver");
			// Class.forName(driver);

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

		public void disconnect() {
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

    
    /**
    void setupCustomerTable() {
        String TABLE_NAME = "CUSTOMER";
        try {
            stmt = dbConn.createStatement();

            DatabaseMetaData dbm = dbConn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + "already exists. Ready for go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "	id varchar(200) primary key,\n"
                        + "	title varchar(200),\n"
                        + "	author varchar(200),\n"
                        + "	publisher varchar(100),\n"
                        + "	isAvail boolean default true"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + " --- setupDatabase");
        } finally {
        }
    }
    */
    
    
    

    
}