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

/**
 *
 * @author Samuel
 */
public class Waiver {
    
    private final IntegerProperty reservationId;
    private final ObjectProperty<LocalDate> dateSigned;
    private final StringProperty erFirst;
    private final StringProperty erLast;
    private final StringProperty erPhone;
    private final StringProperty waiverStatus;
    private static Connection connection;
    
    public Waiver()
    {
        this(0);
    }
    
    public Waiver(int reservationId)
    {
        this.reservationId = new SimpleIntegerProperty(reservationId);
        this.dateSigned = new SimpleObjectProperty();
        this.erFirst = new SimpleStringProperty(null);
        this.erLast = new SimpleStringProperty(null);
        this.erPhone = new SimpleStringProperty(null);
        this.waiverStatus = new SimpleStringProperty(null);
    }
    
    public int getReservationId() {
        return reservationId.get();
    }
   
    public void setReservationId(int reservationId) {
        this.reservationId.set(reservationId);
    }
    
    public LocalDate getDateSigned() {
        return dateSigned.get();
    }
    
    public void setDateSigned(LocalDate dateSigned) {
        this.dateSigned.set(dateSigned);
    }
    
    public String getERFirst(){
        return erFirst.get();
    }
    
    public void setERFirst(String erFirst){
        this.erFirst.set(erFirst);
    }
    
    public String getWaiverStatus(){
        return waiverStatus.get();
    }
    
    public void setWaiverStatus(String waiverStatus){
        this.waiverStatus.set(waiverStatus);
    }
    
    public String getERLast(){
        return erLast.get();
    }
    
    public void setERLast(String erLast){
        this.erLast.set(erLast);
    }
    
    public String getERPhone()
    {
        return erPhone.get();
    }
    
    public void setERPhone(String erPhone)
    {
        this.erPhone.set(erPhone);
    }
    
    public boolean isComplete(){
        if(getReservationId() != 0 && getDateSigned() != null && getERFirst() != null && getERLast() != null && getERPhone() != null )
        {
            return true;
        }
        
        return false;
    }
    
    public static void addWaiver(int resId) throws IOException, FileNotFoundException, SQLException
    {
        connection = DbConnection.accessDbConnection().getConnection();
        PreparedStatement preSt = null;
                
        preSt = connection.prepareStatement("INSERT INTO WAIVER "
        + "(reservation_id)"
        + " values(?)");

        preSt.setInt(1, resId);

        preSt.execute();
        
        preSt.close();       
    }
           
}