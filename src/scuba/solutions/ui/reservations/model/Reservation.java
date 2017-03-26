/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.reservations.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;

/**
 *
 * @author Samuel
 */
public class Reservation 
{
    private final IntegerProperty reservationId;
    private final IntegerProperty diveTripId;
    private final IntegerProperty customerId;
    private final StringProperty status;
    private final ObjectProperty<Customer> customer;
    private static Connection connection;

    public Reservation() {
        this(0);
    }
	
    
    /**
	 * Constructs a new Reservation object. (Insert any further description that is needed)
	 * @param i
	 */
	public Reservation(int id)
	{
		reservationId = new SimpleIntegerProperty(id);
		diveTripId = new SimpleIntegerProperty();
		customerId = new SimpleIntegerProperty();
		status = new SimpleStringProperty();
		customer = new SimpleObjectProperty<Customer>(null);
	}
	
	public int getReservationId()
	{
		return reservationId.get();
	}
	
	public void setReservationId(int id)
	{
		reservationId.set(id);
	}
	
	public int getDiveTripId()
	{
		return diveTripId.get();
	}
	
	public void setDiveTrip(int id)
	{
		diveTripId.set(id);
	}
	
	public int getCustomerId()
	{
		return customerId.get();
	}
	
	public void setCustomerId(int id)
	{
		customerId.set(id);
	}
	
	public String getStatus()
	{
		return status.get();
	}
	
	public void setStatus(String stat)
	{
		status.set(stat);
	}
	
	public Customer getCustomer()
	{
		return customer.get();
	}
	
	public void setCustomer(Customer cust)
	{
		customer.set(cust);
	}

    public StringProperty statusProperty()
    {
        return status;
    }
    
    public static void addReservation(int custId, int diveId) throws IOException, FileNotFoundException, SQLException
    {
        connection = DbConnection.accessDbConnection().getConnection();
        PreparedStatement preSt = null;
                
        preSt = connection.prepareStatement("INSERT INTO RESERVATION "
        + "(cust_id, trip_id)"
        + " values(?, ?)");

        preSt.setInt(1, custId);
        preSt.setInt(2, diveId);

        preSt.execute();
        
        preSt.close();
     
        
    }
    
}
