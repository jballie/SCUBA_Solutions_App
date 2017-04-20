
package scuba.solutions.ui.reservations.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.util.AlertUtil;

/**
 * Represents a Reservation for a dive trip for a specific customer.
 * @author Samuel Brock, Jonathan Balliet
 */
public class Reservation 
{
    private final IntegerProperty reservationId;
    private final IntegerProperty diveTripId;
    private final IntegerProperty customerId;
    private final StringProperty status;
    private final ObjectProperty<Customer> customer;
    private final ObjectProperty<DiveTrip> diveTrip;
    private final ObjectProperty<Waiver> waiver;
    private final ObjectProperty<Payment> payment;
    private static Connection connection;

    // Default Constructor
    public Reservation() 
    {
        this(0);
    }
	
    /**
    * Constructs a new Reservation object. 
    */
    public Reservation(int id)
    {
        this.reservationId = new SimpleIntegerProperty(id);
        this.diveTripId = new SimpleIntegerProperty();
        this.customerId = new SimpleIntegerProperty();
        this.status = new SimpleStringProperty();
        this.customer = new SimpleObjectProperty();
        this.diveTrip = new SimpleObjectProperty();

        Waiver waiver = new Waiver(id);
        Payment payment = new Payment(id);

        this.waiver = new SimpleObjectProperty(waiver);
        this.payment = new SimpleObjectProperty(payment);

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

    public void setDiveTripId(int id)
    {
        diveTripId.set(id);
    }

     public DiveTrip getDiveTrip()
    {
        return diveTrip.get();
    }

    public void setDriveTrip(DiveTrip diveTrip)
    {
        this.diveTrip.set(diveTrip);
    }

    public Waiver getWaiver()
    {
        return waiver.get();
    }

    public void setWaiver(Waiver waiver)
    {
        this.waiver.set(waiver);
    }

    public Payment getPayment()
    {
        return payment.get();
    }

    public void setPayment(Payment payment)
    {
        this.payment.set(payment);
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
    
    public ObjectProperty reservationIdProperty()
    {
        return reservationId.asObject();
    }
    
    // Adds the reservation based on the customer id and dive trip id.
    public static int addReservation(int custId, int diveId) throws IOException, FileNotFoundException, SQLException
    {
        connection = DbConnection.accessDbConnection().getConnection();
        PreparedStatement preSt = null;
        int result = 0;
        try
        { 
            preSt = connection.prepareStatement("INSERT INTO RESERVATION "
            + "(cust_id, trip_id)"
            + " values(?, ?)");

            preSt.setInt(1, custId);
            preSt.setInt(2, diveId);

            result = preSt.executeUpdate();
           
        }
        catch(SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with adding new Reservation", e);
        }
        finally
        {
            try
            {
                if (preSt != null)
                    preSt.close();
            } 
            catch (SQLException e) 
            {
                AlertUtil.showDbErrorAlert("Error with Database", e);
            }
        }
        
        return result;
    }
    
    // Gets the reservation id for the passed in values.
    public static int getReservationId(int custId, int diveId) throws IOException, FileNotFoundException, SQLException
    {
        PreparedStatement preSt = null;
        int reservationId = 0;
        
        try
        {
            connection = DbConnection.accessDbConnection().getConnection();
            preSt = connection.prepareStatement("SELECT reservation_id FROM RESERVATION WHERE cust_id=? AND trip_id=?");

            preSt.setInt(1, custId);
            preSt.setInt(2, diveId);

            ResultSet resultSet = preSt.executeQuery();
            resultSet.next();

            reservationId = resultSet.getInt(1); //not null?

            preSt.close();
        }
        catch(SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with adding new Reservation", e);
        }
        finally
        {
            try
            {
                if (preSt != null)
                    preSt.close();
            } 
            catch (SQLException e) 
            {
                AlertUtil.showDbErrorAlert("Error with Database", e);
            }
        }
     
        return reservationId;
    }
    
    
    // Determines whether a customer is already reserved for the dive trip.
    public static boolean isCustomerAlreadyReserved(int custId, int diveId) throws FileNotFoundException, IOException, SQLException
    {
        connection = DbConnection.accessDbConnection().getConnection();
        PreparedStatement statement = null;
        ResultSet results = null;
        try
        {
            connection = DbConnection.accessDbConnection().getConnection();
            statement = connection.prepareStatement("SELECT * FROM RESERVATION WHERE CUST_ID=? AND TRIP_ID =?");

            statement.setInt(1, custId);
            statement.setInt(2, diveId);

            results = statement.executeQuery();

            if(results.next())
            {
               return true;
            }
        }
        catch(SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with Database", e);
        }
        finally
        {
            try
            {
                if (statement != null)
                    statement.close();
                if (results != null)
                    results.close();
            } 
            catch (SQLException e) 
            {
                AlertUtil.showDbErrorAlert("Error with Database", e);
            }
        }
        return false;
    }
}
