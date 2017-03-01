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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scuba.solutions.util.DateUtil;

/**
 * Represents a Customer profile for the Scuba Solutions app.
 * @author Jon
 */
public class Customer
{
    private final IntegerProperty customerID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty street;
    private final IntegerProperty postalCode;
    private final StringProperty city;
    private final StringProperty state;
    private final ObjectProperty<LocalDate> dateOfBirth;
    private final StringProperty phoneNumber;
    private final StringProperty emailAddress;
    private final StringProperty certAgency;
    private final IntegerProperty certDiveNo;
    
    public Customer()
    {
    	this(0, null, null);
    }
    
    
    public Customer(int customerID)
    {
    	this.customerID = new SimpleIntegerProperty(customerID);
    	this.firstName = new SimpleStringProperty("");
    	this.lastName = new SimpleStringProperty ("");
    	this.street = new SimpleStringProperty("some street");
        this.postalCode = new SimpleIntegerProperty(123456);
        this.city = new SimpleStringProperty("some city");
        this.state = new SimpleStringProperty("NC");
        this.dateOfBirth = new SimpleObjectProperty<LocalDate>(LocalDate.of(1999, 2, 21));
        this.phoneNumber = new SimpleStringProperty("677");
        this.emailAddress = new SimpleStringProperty("blah@gmail.com");
        this.certAgency = new SimpleStringProperty("scuba");
        this.certDiveNo = new SimpleIntegerProperty(67);
    }
    
    public Customer(int customerID, String firstName, String lastName)
    {
    	this.customerID = new SimpleIntegerProperty(customerID);
    	this.firstName = new SimpleStringProperty(firstName);
    	this.lastName = new SimpleStringProperty (lastName);
    	this.street = new SimpleStringProperty("some street");
        this.postalCode = new SimpleIntegerProperty(123456);
        this.city = new SimpleStringProperty("some city");
        this.state = new SimpleStringProperty("NC");
        this.dateOfBirth = new SimpleObjectProperty<LocalDate>(LocalDate.of(1999, 2, 21));
        this.phoneNumber = new SimpleStringProperty("677");
        this.emailAddress = new SimpleStringProperty("blah@gmail.com");
        this.certAgency = new SimpleStringProperty("scuba");
        this.certDiveNo = new SimpleIntegerProperty(67);
        
    	
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((customerID == null) ? 0 : customerID.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		if (customerID == null)
		{
			if (other.customerID != null)
				return false;
		}
		else if (!customerID.equals(other.customerID))
			return false;
		return true;
	}


	public int getCustomerID() {
        return customerID.get();
    }
    
    /*
    Question: Not sure if Customer ID is automatically generated from Database
    or if we will have to give an individual one ourselves. There is a way to do
    this that is pretty simple if needed
    */
    
    public void setCustomerID(int customerID)
   
    {
        this.customerID.set(customerID);
    }
    public StringProperty customerIdProperty() {
      String cusIdTemp = Integer.toString(getCustomerID());
      StringProperty cusIdProp = new  SimpleStringProperty(cusIdTemp);
      return cusIdProp;
      
    }
    
    public String getFirstName() {
        return firstName.get();
    }
    
     public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }
    
     public StringProperty firstNameProperty() {
         return firstName;
     }

    public String getLastName() {
        return lastName.get();
    }

     public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }
     
    public StringProperty lastNameProperty() {
         return lastName;
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
    	return state.get();
    }
    
    public void setState(String state){
    	this.state.set(state);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }
    
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber.set(phoneNumber);
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth.get();
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth.set(dateOfBirth);
    }
    
    public ObjectProperty<LocalDate> dateofBirthProperty() {
    	String dobTemp = DateUtil.format(getDateOfBirth());
    	StringProperty dobProp = new SimpleStringProperty(dobTemp);
    	return dateOfBirth;
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
