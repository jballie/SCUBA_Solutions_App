
package scuba.solutions.ui.customers.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import scuba.solutions.database.DbConnection;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.DateUtil;

/**
 * Represents a Customer Profile for the SCUBA SCUBA Now company.
 * @author Jonathan Balliet, Samuel Brock
 */
public class Customer 
{
    private final IntegerProperty customerId;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty street;
    private final StringProperty postalCode;
    private final StringProperty city;
    private final StringProperty state;
    private final ObjectProperty<LocalDate> dateOfBirth;
    private final StringProperty phoneNumber;
    private final StringProperty emailAddress;
    private final StringProperty certAgency;
    private final StringProperty certDiveNo;
    private static Connection connection;
    
    // Default Constructor
    public Customer()
    {
    	this(0, null, null);
    }
    
    // Constructor for a new Customer object with a passed in customer id.
    public Customer(int customerId)
    {
    	this.customerId = new SimpleIntegerProperty(customerId);
    	this.firstName = new SimpleStringProperty("");
    	this.lastName = new SimpleStringProperty ("");
    	this.street = new SimpleStringProperty("");
        this.postalCode = new SimpleStringProperty("");
        this.city = new SimpleStringProperty("");
        this.state = new SimpleStringProperty("");
        this.dateOfBirth = new SimpleObjectProperty<>();
        this.phoneNumber = new SimpleStringProperty("");
        this.emailAddress = new SimpleStringProperty("");
        this.certAgency = new SimpleStringProperty("");
        this.certDiveNo = new SimpleStringProperty("");
    }
    
    // Constructor for a customer object with passed in customer id, first name, and last name
    public Customer(int customerId, String firstName, String lastName)
    {
    	this.customerId = new SimpleIntegerProperty(customerId);
    	this.firstName = new SimpleStringProperty(firstName);
    	this.lastName = new SimpleStringProperty (lastName);
    	this.street = new SimpleStringProperty("");
        this.postalCode = new SimpleStringProperty("");
        this.city = new SimpleStringProperty("");
        this.state = new SimpleStringProperty("");
        this.dateOfBirth = new SimpleObjectProperty<>();
        this.phoneNumber = new SimpleStringProperty("");
        this.emailAddress = new SimpleStringProperty("");
        this.certAgency = new SimpleStringProperty("");
        this.certDiveNo = new SimpleStringProperty("");
        
    	
    }
    
    // Copy Constructor for a Customer
    public Customer(Customer cust) 
    {
       
        customerId = cust.customerId;
        this.firstName = new SimpleStringProperty(cust.getFirstName());
    	this.lastName = new SimpleStringProperty (cust.getLastName());
    	this.street = new SimpleStringProperty(cust.getStreet());
        this.postalCode = new SimpleStringProperty(cust.getPostalCode());
        this.city = new SimpleStringProperty(cust.getCity());
        this.state = new SimpleStringProperty(cust.getState());
        this.dateOfBirth = new SimpleObjectProperty<>(cust.getDateOfBirth());
        this.phoneNumber = new SimpleStringProperty(cust.getPhoneNumber());
        this.emailAddress = new SimpleStringProperty(cust.getEmailAddress());
        this.certAgency = new SimpleStringProperty(cust.getCertAgency());
        this.certDiveNo = new SimpleStringProperty(cust.getCertDiveNo());
    }

    public int getCustomerID() 
    {
        return customerId.get();
    }
    
    public String getFirstName() 
    {
        return firstName.get();
    }
    
    public void setFirstName(String firstName) 
    {
        this.firstName.set(firstName);
    }
    
    public StringProperty firstNameProperty() 
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName.get();
    }

    public void setLastName(String lastName) 
    {
        this.lastName.set(lastName);
    }
     
    public StringProperty lastNameProperty() 
    {
        return lastName;
    }

    public String getStreet() 
    {
        return street.get();
    }
    
    public void setStreet(String street) 
    {
        this.street.set(street);
    }

    public String getPostalCode() 
    {
        return postalCode.get();
    }
    
    public void setPostalCode(String postalCode) 
    {
        this.postalCode.set(postalCode);
    }

    public String getCity() 
    {
        return city.get();
    }
    
    public void setCity(String city)
    {
        this.city.set(city);
    }
    
    public String getState() 
    {
    	return state.get();
    }
    
    public void setState(String state)
    {
    	this.state.set(state);
    }

    public String getPhoneNumber() 
    {
        return phoneNumber.get();
    }
    
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber.set(phoneNumber);
    }
    
    public LocalDate getDateOfBirth() 
    {
        return dateOfBirth.get();
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) 
    {
        this.dateOfBirth.set(dateOfBirth);
    }
    
    public StringProperty dateofBirthProperty() 
    {
    	String dobTemp = DateUtil.format(getDateOfBirth());
    	StringProperty dobProp = new SimpleStringProperty(dobTemp);
    	return dobProp;
    }

    public String getEmailAddress() 
    {
        return emailAddress.get();
    }
    
    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress.set(emailAddress);
    }

    public String getCertAgency() 
    {
        return certAgency.get();
    }
    
    public void setCertAgency(String certAgency) 
    {
        this.certAgency.set(certAgency);
    }
    
    public String getCertDiveNo() 
    {
        return certDiveNo.get();
    }
    
    public void setCertDiveNo(String certDiveNo)
    {
        this.certDiveNo.set(certDiveNo);
    }
    
    public String getFullName()
    {
        String fullName = getFirstName() + " " + getLastName();
        return fullName;
    }
    
     public StringProperty fullNameProperty() 
     {
        StringProperty fullNameProp = new SimpleStringProperty(getFullName()); 
        return fullNameProp;
     }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Customer other = (Customer) obj;
        if (this.customerId.get() != other.customerId.get()) {
            return false;
        }
        if (!this.firstName.get().equals(other.firstName.get())) {
            return false;
        }
        if (!this.lastName.get().equals(other.lastName.get())) {
            return false;
        }
        if (!this.street.get().equals(other.street.get())) {
            return false;
        }
        if (!this.postalCode.get().equals(other.postalCode.get())) {
            return false;
        }
        if (!this.city.get().equals(other.city.get())) {
            return false;
        }
        if (!this.state.get().equals(other.state.get())) {
            return false;
        }
        if (!Objects.equals(this.dateOfBirth.get(), other.dateOfBirth.get())) {
            return false;
        }
        if (!Objects.equals(this.phoneNumber.get(), other.phoneNumber.get())) {
            return false;
        }
        if (!Objects.equals(this.emailAddress.get(), other.emailAddress.get())) {
            return false;
        }
        if (!Objects.equals(this.certAgency.get(), other.certAgency.get())) {
            return false;
        }
        if (!Objects.equals(this.certDiveNo.get(), other.certDiveNo.get())) {
            return false;
        }
        return true;
    }

    // Updates the customer profile in the database
    public static void updateCustomer(Customer customer) throws FileNotFoundException, IOException, SQLException
    {
        PreparedStatement preSt = null;
        
        try
        {
            connection = DbConnection.accessDbConnection().getConnection();
            
            int custId = customer.getCustomerID();
            String fName = customer.getFirstName();
            String lName = customer.getLastName();
            String street = customer.getStreet();
            String city = customer.getCity();
            String state = customer.getState();
            String zip = customer.getPostalCode();
            String phone = customer.getPhoneNumber();
            String email = customer.getEmailAddress();
            LocalDate dob = customer.getDateOfBirth();
            String certAgen = customer.getCertAgency();
            String certDiveNo = customer.getCertDiveNo();
                 
            preSt = connection.prepareStatement("UPDATE CUSTOMER SET first_name=?, last_name=?, street=?,"+
                     "city=?, state_of_residence=?, zip=?, phone=?, email=?, dob=?, cert_agency=?, cert_no=?"+
                     "WHERE CUST_ID=?");

            preSt.setString(1, fName);
            preSt.setString(2, lName);
            preSt.setString(3, street);
            preSt.setString(4, city);
            preSt.setString(5, state);
            preSt.setString(6, zip);
            preSt.setString(7, phone);
            preSt.setString(8, email);
            preSt.setDate(9, Date.valueOf(dob));
            preSt.setString(10, certAgen);
            preSt.setString(11, certDiveNo);
            preSt.setInt(12, custId);

            if (preSt.executeUpdate() == 1)
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Saved!");
                alert.setContentText("Customer has successfully been updated in the database.");
                alert.showAndWait();
            } 
            else 
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Error Occured with update of customer.");
                alert.showAndWait();
            }
        }
        catch (SQLException e)
    	{
            AlertUtil.showDbErrorAlert("Error with Database.", e);
    	}
        
    	finally
    	{
            try
            {
            	if (preSt!= null)
            	{
                    preSt.close();
            	}
        
            }
            catch (SQLException e)
            {
            	AlertUtil.showDbErrorAlert("Error with Database Connection.", e);
            }
        
        }
    }
    
    // Adds the new customer profile to the database
    public static void addCustomer(Customer customer)  throws FileNotFoundException, IOException, SQLException
    {
        PreparedStatement preSt = null;
        
        try
        {
            connection = DbConnection.accessDbConnection().getConnection();
            
            String fName = customer.getFirstName();
            String lName = customer.getLastName();
            String street = customer.getStreet();
            String city = customer.getCity();
            String state = customer.getState();
            String zip = customer.getPostalCode();
            String phone = customer.getPhoneNumber();
            String email = customer.getEmailAddress();
            LocalDate dob = customer.getDateOfBirth();
            String certAgen = customer.getCertAgency();
            String certDiveNo = customer.getCertDiveNo();

            preSt = connection.prepareStatement("INSERT INTO CUSTOMER (first_name, last_name, "
                    + "street, city, state_of_residence, zip, phone, email, dob, cert_agency, cert_no) "
                    + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preSt.setString(1, fName);
            preSt.setString(2, lName);
            preSt.setString(3, street);
            preSt.setString(4, city);
            preSt.setString(5, state);
            preSt.setString(6, zip);
            preSt.setString(7, phone);
            preSt.setString(8, email);
            preSt.setDate(9, Date.valueOf(dob));
            preSt.setString(10, certAgen);
            preSt.setString(11, certDiveNo);

            if (preSt.executeUpdate() == 1) 
            {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Saved!");
                alert.setContentText("Customer has successfully been added to the database.");
                alert.showAndWait();

            } 
            else 
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error!");
                alert.setContentText("Error Occured");
                alert.showAndWait();

            }
        }
        catch (SQLException e)
    	{
            AlertUtil.showDbErrorAlert("Error with Database.", e);
    	}        
        finally
        {
            try
            {
            	if (preSt!= null)
            	{
                    preSt.close();
            	}
        
            }
            catch (SQLException e)
            {
            	AlertUtil.showDbErrorAlert("Error with Database Connection.", e);
            }
        
        }    
    }
    
    // Gets the customer id of the passed in customer object.
    public static int getCustId(Customer customer) throws SQLException, IOException
    {
        int custId = 0;
        ResultSet result = null;
        PreparedStatement preSt = null;
        
        try
        {
            connection = DbConnection.accessDbConnection().getConnection();
            preSt = connection.prepareStatement("select cust_id "
                                         + "from customer where first_name = ?" 
                                         + "and last_name = ? and dob = ?");

            preSt.setString(1, customer.getFirstName());
            preSt.setString(2, customer.getLastName());
            preSt.setDate(3, Date.valueOf(customer.getDateOfBirth()));

            result = preSt.executeQuery();
            result.next();
            custId = result.getInt(1);
        }
        catch (SQLException e)
    	{
            AlertUtil.showDbErrorAlert("Error with Database.", e);
    	}        
        finally
        {
            try
            {
            	if (preSt!= null)
            	{
                    preSt.close();
            	}
                if (result != null)
                {
                    result.close();
                }
        
            }
            catch (SQLException e)
            {
            	AlertUtil.showDbErrorAlert("Error with Database Connection.", e);
            }
        }    
        return custId;
    }
}
