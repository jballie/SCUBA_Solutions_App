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
 * Represents a Customer profile for the Scuba Solutions app.
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
    private final StringProperty state = null;
    private final StringProperty phoneNumber = null;
    private final StringProperty emailAddress = null;
    private final ObjectProperty<LocalDate> dateOfBirth = null;
    private final StringProperty certAgency = null;
    private final IntegerProperty certDiveNo = null;
    
    public Customer()
    {
    }

    public int getCustomerID() {
        return customerID.get();
    }
    
    /*
    Question: Not sure if Customer ID is automatically generated from Database
    or if we will have to give an individual one ourselves. There is a way to do
    this that is pretty simple if needed
    public void setCustomerID(int customerID)
    {
        this.customerID.set(customerID);
    }
    */
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
    
     public void setStreet(String street) {
        this.street.set(street);
    }

    public int getPostalCode() {
        return postalCode.get();
    }
    
    public void setPostalCode(int postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getCity() {
        return city.get();
    }
    
    public void setCity(String city){
        this.city.set(city);
    }
    
    public String getState() {
        return city.get();
    }
    
    public void setState(String state){
        this.city.set(state);
    }
    
    public String getPhoneNumber() {
        return phoneNumber.get();
    }
    
    public void setPhoneNumber(String phoneNumber){
        this.city.set(phoneNumber);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth.get();
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }

    public String getEmailAddress() {
        return emailAddress.get();
    }
    
    public void setEmailAddress(String emailAddress){
        this.emailAddress.set(emailAddress);
    }

    public String getCertAgency() {
        return certAgency.get();
    }
    
    public void setCertAgency(String certAgency) {
        this.certAgency.set(certAgency);
    }
    
    public int getCertDiveNo() {
        return certDiveNo.get();
    }
    
    public void setCertDiveNo(int certDiveNo)
    {
        this.certDiveNo.set(certDiveNo);
    }
  
}
