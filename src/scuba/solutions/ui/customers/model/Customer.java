/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.customers.model;

import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a Customer profile for the Scuba Solutions.
 * @author Jon
 */
public class Customer
{
    private final IntegerProperty customerID = null;
    private final StringProperty firstName = null;
    private final StringProperty lastName = null;
    private final StringProperty street = null;
    private final IntegerProperty postalCode = null;
    private final StringProperty city = null;
    private final ObjectProperty<LocalDate> birthday = null;
    private final StringProperty emailAddress = null;
    private final BooleanProperty liabilityStatus = null;
    
    public Customer()
    {
    }

    public int getCustomerID() {
        return customerID.get();
    }
    
    public void setCustomerID(int customerID)
    {
        this.customerID.set(customerID);
    }

    public String getFirstName() {
        return firstName.get();
    }
    
     public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }


    public String getLastName() {
        return lastName.get();
    }

     public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    
    public String getStreet() {
        return street.get();
    }

    public int getPostalCode() {
        return postalCode.get();
    }

    public String getCity() {
        return city.get();
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public String getEmailAddress() {
        return emailAddress.get();
    }

    public Boolean getLiabilityStatus() {
        return liabilityStatus.get();
    }
    
  
}
