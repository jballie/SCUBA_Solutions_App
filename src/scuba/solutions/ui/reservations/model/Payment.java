
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

/**
 * Represents the Payment to be completed by the Customer for each Reservation.
 * @author Samuel Brock, Jonathan Balliet
 */
public class Payment 
{
    private final IntegerProperty reservationId;
    private final StringProperty paymentStatus;
    private final IntegerProperty ccConfirmNo;
    private final ObjectProperty<LocalDate> dateProcessed;
    private final IntegerProperty amount;
    private static Connection connection;
    
    public Payment() 
    {
        this(0);
    }
    
    public Payment(int reservationId) 
    {
        this.reservationId = new SimpleIntegerProperty(reservationId);
        this.ccConfirmNo = new SimpleIntegerProperty(0);
        this.dateProcessed = new SimpleObjectProperty(null);
        this.amount = new SimpleIntegerProperty(0);
        this.paymentStatus = new SimpleStringProperty("UNPAID");
    }
    
    public int getReservationId() 
    {
        return reservationId.get();
    }
   
    public void setReservationId(int reservationId) 
    {
        this.reservationId.set(reservationId);
    }
    
    public LocalDate getDateProcessed() 
    {
        return dateProcessed.get();
    }
    
    public void setDateProcessed(LocalDate dateProcessed)
    {
        this.dateProcessed.set(dateProcessed);
    }
    
    public String getPaymentStatus()
    {
        return paymentStatus.get();
    }
    
    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus.set(paymentStatus);
    }
    
    public int getCCConfirmNo()
    {
        return ccConfirmNo.get();
    }
    
    public void setCCConfirmNo(int ccConfirmNo)
    {
        this.ccConfirmNo.set(ccConfirmNo);
    }
    
    public int getAmount()
    {
        return amount.get();
    }
    
    public void setAmount(int amount)
    {
        this.amount.set(amount);
    }
    
    // Determines whether the Payment is complete or not.
    public boolean isComplete()
    {        
        return getReservationId() != 0 && getDateProcessed() != null && getCCConfirmNo() > 0 && getAmount() > 0;
    }
    
    // Add the Payment to its corresponding reservation.
    public static void addPayment(int resId) throws IOException, FileNotFoundException, SQLException
    {
        connection = DbConnection.accessDbConnection().getConnection();
        PreparedStatement preSt = null;
                
        preSt = connection.prepareStatement("INSERT INTO PAYMENT "
        + "(reservation_id)"
        + " values(?)");

        preSt.setInt(1, resId);

        preSt.execute();
        
        preSt.close();       
    }
}
