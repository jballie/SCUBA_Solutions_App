/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.reservations.model;

import scuba.solutions.ui.dive_schedule.model.*;
import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Samuel
 */
public class Payment {
    
    private final IntegerProperty reservationId;
    private final StringProperty paymentStatus;
    private final IntegerProperty ccConfirmNo;
    private final ObjectProperty<LocalDate> dateProcessed;
    private final IntegerProperty amount;
    
    public Payment() {
        this(0);
    }
    
    public Payment(int reservationId) {
        this.reservationId = new SimpleIntegerProperty(reservationId);
        this.ccConfirmNo = new SimpleIntegerProperty();
        this.dateProcessed = new SimpleObjectProperty();
        this.amount = new SimpleIntegerProperty();
        this.paymentStatus = new SimpleStringProperty(null);
    }
    
    public int getReservationId() {
        return reservationId.get();
    }
   
    public void setReservationId(int reservationId) {
        this.reservationId.set(reservationId);
    }
    
    public LocalDate getDateProcessed() {
        return dateProcessed.get();
    }
    
    public void setDateProcessed(LocalDate dateProcessed) {
        this.dateProcessed.set(dateProcessed);
    }
    
    public String getPaymentStatus(){
        return paymentStatus.get();
    }
    
    public void setPaymentStatus(String paymentStatus){
        this.paymentStatus.set(paymentStatus);
    }
    
    public int getCCConfirmNo(){
        return ccConfirmNo.get();
    }
    
    public void setCCConfirmNo(int ccConfirmNo){
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
    
    public boolean isComplete(){        
        if(getReservationId() != 0 && getDateProcessed() != null && getCCConfirmNo() > 0 && getAmount() != 0)
        {
            return true;
        }        
        return false;
    }
}
