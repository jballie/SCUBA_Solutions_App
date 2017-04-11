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

		private static final String DB_PROPERTIES_FILE = "C:\\Users\\Jon\\Documents\\NetBeansProjects\\GitHub\\SCUBA_Solutions_App\\src\\database.properties";

		private static DbConnection me = null;

		private Connection dbConn = null;

		private DbConnection(String dbPropertiesFile) throws FileNotFoundException, IOException,
				SQLException {

		
                        //Remote Server 
                        /*
			String username = "ScubaNow";
			String password = "capstone";
			String url = "jdbc:oracle:thin:@scubasolutionsdb.ctmcz5bqqxdt.us-west-2.rds.amazonaws.com:1521:ORCL";
			String driver = "oracle.jdbc.driver.OracleDriver";
                        */
                        
                        //LocalHost normal connection - please use this most of the time!
                        
                        String username = "scott";
                        String password = "tiger";
                        String url = "jdbc:oracle:thin:@localhost:1521:xe";
                        

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
    
}