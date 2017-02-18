/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.database;

/**
 *
 * @author Jon
 */
public class DatabaseHandler
{
     private static DatabaseHandler handler=null;

    // private static final String DB_URL = 
    // private static Connection conn = null;
    // private static Statement stmt = null;
    
    private DatabaseHandler() {
//        createConnection();
     

    }
    
     public static DatabaseHandler getInstance()
    {
        if(handler==null)
        {
            handler = new DatabaseHandler();
        }
        return handler;
    }
     
     
    void createConnection()
    {
    }
    
    
  
    
}
