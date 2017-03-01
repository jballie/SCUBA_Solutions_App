/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.customers.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.util.DateUtil;

/**
 * Controller class for the Customer Pane Interface.
 *
 * @author Jonathan Balliet, Samuel Brock
 * Bugs/Todo:
 * some values are not updating (phone number)
 * Adding and updating is giving an error but can't figure out why - does not shut off program
 * or effect the added/updated info in table or DB.
 */
public class CustomerPaneController implements Initializable {
      
    
    private ObservableList<Customer> customerData = FXCollections.observableArrayList();
    private FilteredList<Customer> filteredData; 
    private static Connection connection;
    
    public Stage primaryStage;
    
    @FXML
    private JFXButton homeButton;
    @FXML
    private JFXTextField searchTextField;
    @FXML
    private JFXButton clearSearchButton;
    @FXML
    private JFXButton addCustomerButton;
    @FXML
    private JFXButton updateCustomerButton;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, String> customerIdColumn;
    @FXML
    private TableColumn<Customer, String> firstNameColumn;
    @FXML
    private TableColumn<Customer, String> lastNameColumn;
    @FXML
    private TableColumn<Customer, LocalDate> dobColumn;
    @FXML
    private Label customerIdLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label emailAddressLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label certAgencyLabel;
    @FXML
    private Label certDiveNoLabel;
  
   
   
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    	primaryStage = CustomerPane.getPrimaryStage();
        // databaseHandler = DatabaseHandler.getInstance();
         initalizeCustomer();
         
         searchTextField.setAlignment(Pos.CENTER);
     
         try
		{
			loadCustomers();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
        // Clear person details.
        showCustomerDetails(null);

        // Listen for selection changes and show the person details when changed.
         customersTable.getSelectionModel().selectedItemProperty().addListener(
          (observable, oldValue, newValue) -> showCustomerDetails(newValue));
         
         initalizeSearchField();
    }
    
   /**
    * Displays the customer Edit Dialog and passes in the wanted customer
 	* for either adding or updating.
    */
	public boolean showCustomerEditDialog(Customer customer) 
	{
		try
    	{
			// Load the fxml file and create a new stage for the popup dialog.
    	    FXMLLoader loader = new FXMLLoader();
    	    loader.setLocation(CustomerPaneController.class.getResource("CustomerEditDialog.fxml"));
    	    Parent root = loader.load();

    	    // Create the dialog Stage.
    	    Stage dialogStage = new Stage();
    	    dialogStage.setTitle("Edit Person");
    	    dialogStage.initModality(Modality.WINDOW_MODAL);
    	    dialogStage.initOwner(primaryStage);
    	    Scene scene = new Scene(root);
    	    dialogStage.setScene(scene);

    	    // Set the customer into the controller.
    	    CustomerEditDialogController controller = loader.getController();
    	    controller.setDialogStage(dialogStage);
    	    controller.setCustomer(customer);

    	    // Show the dialog and wait until the user closes it
    	    dialogStage.showAndWait();

    	    return controller.isOkClicked();
    	    
    	}
		catch (IOException e) 
    	{
			e.printStackTrace();
    	    return false;
    	}
    }
        

    // When the add button is clicked on - goes to edit dialog customer
	// for adding the information for a new customer.
    @FXML
    public void addCustomer() throws SQLException, FileNotFoundException, IOException
    {
        Customer tempPerson = new Customer();
        boolean okClicked = showCustomerEditDialog(tempPerson);
        if (okClicked) 
        {
        	
        	 int custId = getCurrentSequence();
        	 String fName = tempPerson.getFirstName();
        	 String lName = tempPerson.getLastName();
        	 String street = tempPerson.getStreet();
        	 String city = tempPerson.getCity();
        	 String state = tempPerson.getState();
        	 int zip = tempPerson.getPostalCode();
        	 String phone = tempPerson.getPhoneNumber();
        	 String email = tempPerson.getEmailAddress();
        	 LocalDate dob = tempPerson.getDateOfBirth();
        	 String certAgen = tempPerson.getCertAgency();
        	 int certDiveNo = tempPerson.getCertDiveNo();
             
        	PreparedStatement preSt = connection.prepareStatement("INSERT INTO customer(CUSTOMER_ID, FNAME, LNAME, STREET, CITY," +
        			 "STATE, ZIP, PHONE, EMAIL, DOB, CERTAGEN, CERTDIVENO) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        	preSt.setInt(1, custId);
        	preSt.setString(2, fName);
			preSt.setString(3, lName);
			preSt.setString(4, street);
			preSt.setString(5, city);
			preSt.setString(6, state);
			preSt.setInt(7, zip);
			preSt.setString(8, phone);
			preSt.setString(9, email);
			preSt.setDate(10, Date.valueOf(dob));
			preSt.setString(11, certAgen);
			preSt.setInt(12, certDiveNo);
			
		
        	
        	
            
            if (preSt.executeUpdate() == 1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Saved!");
                alert.setContentText("Customer has successfully been added to the database.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Error Occured");
                alert.showAndWait();
            }
        	
        	
            
           
        }
        loadCustomers();
    }
    
    // When the update button is clicked on - goes to edit dialog customer
    // for updating the information for the selected customer.
    @FXML
    public void updateCustomer() throws FileNotFoundException, IOException, SQLException
    {
        
    	Customer selectedPerson = (Customer) customersTable.getSelectionModel().getSelectedItem();
         if (selectedPerson != null)
         {
             boolean okClicked = showCustomerEditDialog(selectedPerson);
             
             if (okClicked) 
             {
            	 
            	 int custId = selectedPerson.getCustomerID();
            	 String fName = selectedPerson.getFirstName();
            	 String lName = selectedPerson.getLastName();
            	 String street = selectedPerson.getStreet();
            	 String city = selectedPerson.getCity();
            	 String state = selectedPerson.getState();
            	 int zip = selectedPerson.getPostalCode();
            	 String phone = selectedPerson.getPhoneNumber();
            	 String email = selectedPerson.getEmailAddress();
            	 LocalDate dob = selectedPerson.getDateOfBirth();
            	 String certAgen = selectedPerson.getCertAgency();
            	 int certDiveNo = selectedPerson.getCertDiveNo();
                 
            	PreparedStatement preSt = 
            			connection.prepareStatement("UPDATE CUSTOMER SET FNAME=?, LNAME=?, STREET=?,"+
            			 "CITY=?, STATE=?, ZIP=?, PHONE=?, EMAIL=?, DOB=?, CERTAGEN=?, CERTDIVENO=?"+
            			 "WHERE CUSTOMER_ID=?");
          
            	preSt.setString(1, fName);
    			preSt.setString(2, lName);
    			preSt.setString(3, street);
    			preSt.setString(4, city);
    			preSt.setString(5, state);
    			preSt.setInt(6, zip);
    			preSt.setString(7, phone);
    			preSt.setString(8, email);
    			preSt.setDate(9, Date.valueOf(dob));
    			preSt.setString(10, certAgen);
    			preSt.setInt(11, certDiveNo);
    			preSt.setInt(12, custId);
            	 

                if (preSt.executeUpdate() == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Saved!");
                    alert.setContentText("Customer has successfully been updated in the database.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Error Occured with update of customer.");
                    alert.showAndWait();
                }
            	 
                 showCustomerDetails(selectedPerson);
                 
                 loadCustomers();
             }

         } 
         else 
         {
             // If nothing is selected an warning message will pop-up
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(CustomerPane.getPrimaryStage());
             alert.setTitle("No Selection");
             alert.setHeaderText("No Customer Selected");
             alert.setContentText("Please select a customer in the table to update.");

             alert.showAndWait();
         }
    }
    
    // Initializes the columns for the TableView
    public void initalizeCustomer()
    {
    	customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        dobColumn.setCellValueFactory(cellData -> cellData.getValue().dateofBirthProperty());

     
    }
    
    // Loads all the Customers from that database into the tableView
    // Think of it as a 'refresh' (will be called when transitions to Customer pane
    // and after any customers have been added or updated.
    public void loadCustomers() throws FileNotFoundException, IOException, SQLException
    {
       // customerData.clear();  // clears list to start
    	//if (!customerData.isEmpty())
    	//{
    		customerData.clear();
    	//}
    	
        // queries DB to get all data for each column
        // stores the result set into an equivalent variable
        // creates a Customer object and adds to the list
    	
    	connection = DbConnection.accessDbConnection().getConnection();
		
		String query = "SELECT * FROM customer";
		
		Statement statement = null;
		ResultSet result = null;
    
		try
		{
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			while(result.next())
			{
				int customerID = result.getInt(1);
				Customer customer = new Customer(customerID);
				customer.setFirstName(result.getString(2));
				customer.setLastName(result.getString(3));
				customer.setStreet(result.getString(4));
				customer.setCity(result.getString(5));
				customer.setState(result.getString(6));
				customer.setPostalCode(result.getInt(7));
				customer.setPhoneNumber(result.getString(8));
				customer.setEmailAddress(result.getString(9));
				customer.setDateOfBirth((result.getDate(10)).toLocalDate());
				customer.setCertAgency(result.getString(11));
				customer.setCertDiveNo(result.getInt(12));
				
				customerData.add(customer);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (statement != null)
				{
					statement.close();
				}
				
				result.close();
			}
			catch (SQLException e)
			{
				Logger.getLogger(CustomerPaneController.class.getName()).log(Level.SEVERE, null, e);
			}
		}
        customersTable.getItems().setAll(customerData);
    }
    
    /**
    * Fills all text fields to show details about the customer.
    * If the specified customer is null, all the text fields are cleared.
    */
    private void showCustomerDetails(Customer customer)
    {
            if (customer != null) 
            {
            	customerIdLabel.setText(Integer.toString(customer.getCustomerID()));
            	firstNameLabel.setText(customer.getFirstName());
            	lastNameLabel.setText(customer.getLastName());
            	streetLabel.setText(customer.getStreet());
            	postalCodeLabel.setText(Integer.toString(customer.getPostalCode()));
            	cityLabel.setText(customer.getCity());
            	stateLabel.setText(customer.getState());
            	phoneNumberLabel.setText(customer.getPhoneNumber());
            	emailAddressLabel.setText(customer.getEmailAddress());            
            	dobLabel.setText(DateUtil.format(customer.getDateOfBirth()));
            	certAgencyLabel.setText(customer.getCertAgency());
            	certDiveNoLabel.setText(Integer.toString(customer.getCertDiveNo()));
            } 
            else 
            {
            	customerIdLabel.setText("");
            	firstNameLabel.setText("");
            	lastNameLabel.setText("");
            	streetLabel.setText("");
            	postalCodeLabel.setText("");
            	cityLabel.setText("");
            	stateLabel.setText("");
            	phoneNumberLabel.setText("");
            	emailAddressLabel.setText("");
            	dobLabel.setText("");
            	certAgencyLabel.setText("");
            	certDiveNoLabel.setText("");
            }
    }
    
   /**
    * Initalizes the search field. Any typed in text in the search text field will
    * show the matching entries in the customer table.
    */
    private void initalizeSearchField()
    {
    	filteredData = new FilteredList<>(customerData, p -> true);

        // Sets the search filter Predicate whenever the search values change.
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                // If the search field text is empty, display all customers in the table
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compares the name, id, and date of birth of every customer with the search text
                String lowerCaseFilter = newValue.toLowerCase();
                String fullName = customer.getFirstName().toLowerCase() + " " + customer.getLastName().toLowerCase();
       
                if (customer.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Search matches first name.
                } else if (customer.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Search matches last name.
                } else if (fullName.contains(lowerCaseFilter)) {
                	return true;  // Search matches full name.
                } else if (Integer.toString(customer.getCustomerID()).contains(lowerCaseFilter)){
                	return true; // Search matches customer id.
            	} else if (DateUtil.format(customer.getDateOfBirth()).contains(lowerCaseFilter)){
            		return true;   // Search matches date of birth.
            	}
                return false; // Search does not match any data.
            });
        });
        
        // sets the table with the matching filtered search data
        customersTable.setItems(filteredData);
    	
    }
    
    // clears the search text field - displays all customer data in table
    @FXML
    public void clearSearch()
    {
    	searchTextField.clear();

    }
    
    
    public int getCurrentSequence() throws FileNotFoundException, IOException, SQLException
    {
    	connection = DbConnection.accessDbConnection().getConnection();
		
    	Random rand = new Random(System.currentTimeMillis());
    	int currentId = rand.nextInt(1000);
		Statement statement = null;
		ResultSet result = null;
		/**
    	String query = "SELECT CURRENTVALUE FROM SYS.SYSSEQUENCES WHERE SEQUENCE='CUSTOMER_SEQ'";
    	try
		{
			statement = connection.createStatement();
			result = statement.executeQuery(query);
			if(result.next())
			{
				currentId = result.getInt(1);
			}
			System.out.println(currentId);
			
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		*/
    	return currentId;
    }

}