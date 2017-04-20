
package scuba.solutions.ui.customers.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.DateUtil;

/**
 * Controller class for the Customer Pane Interface.
 *
 * @author Jonathan Balliet, Samuel Brock
 * Need to add titles for the Add and Update Customer dialogs to keep front-end consistent
 */
public class CustomerPaneController implements Initializable {
      
    
    private final ObservableList<Customer>  customerData = FXCollections.observableArrayList();
    private FilteredList<Customer> filteredData; 
    private static Connection connection;
    
    private Stage primaryStage;
    private Stage dialogStage;
    
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
    private TableColumn<Customer, String> firstNameColumn;
    @FXML
    private TableColumn<Customer, String> lastNameColumn;
    @FXML
    private TableColumn<Customer, String> dobColumn;
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
    @FXML
    private JFXButton diveScheduleButton;
    @FXML
    private JFXButton customersButton;
    @FXML
    private JFXButton exitButton;
    @FXML
    private AnchorPane rootPane;
  

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    	primaryStage = CustomerPane.getPrimaryStage();
        customersButton.setDisable(true);
        
        initalizeCustomer();
        
        try
        {
            loadCustomers();
        }
        catch (FileNotFoundException e)
        {
            AlertUtil.showErrorAlert("Error with loading customers!\n", e);
        }
        catch (IOException | SQLException e)
        {
            AlertUtil.showErrorAlert("Error with loading customers!\n", e);
        }
         
        // Clear customer details.
        showCustomerDetails(null);

        // Listen for selection changes and show the cutomer details when changed.
        customersTable.getSelectionModel().selectedItemProperty().addListener(
          (observable, oldValue, newValue) -> showCustomerDetails(newValue));
        
        // initalize the search field
        initalizeSearchField(); 
    }
    
   /**
    * Displays the customer Edit Dialog and passes in the wanted customer
    * for either adding or updating.
     * @param customer
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
    	    dialogStage = new Stage();
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
    	    
    	    return controller.isSaveClicked();
    	    
    	}
	catch (IOException e) 
    	{
            AlertUtil.showErrorAlert("Error with opening customer dialog!\n", e);
    	    return false;
    	}
    }
        

    // When the Add Customer button is clicked on - opens the edit customer dialog
    // for adding the information for a new customer profile.
    @FXML
    public void addCustomer() throws SQLException, FileNotFoundException, IOException
    {   
        addCustomerButton.setDisable(true);
        Customer customer = new Customer();
        boolean saveClicked = showCustomerEditDialog(customer);

        if (saveClicked) 
        {   
            Customer.addCustomer(customer);
            customerData.add(customer);
        }
        addCustomerButton.setDisable(false);
    }
    
    // When the Update Customer button is clicked on - opens the edit dialog customer
    // for updating the information for the selected customer.
    @FXML
    public void updateCustomer() throws FileNotFoundException, IOException, SQLException
    {
        updateCustomerButton.setDisable(true);
    	Customer customer = (Customer) customersTable.getSelectionModel().getSelectedItem();
 
         if (customer != null)
         {
            boolean saveClicked = showCustomerEditDialog(customer);
            
            if (saveClicked) 
            {
                Customer.updateCustomer(customer);
                showCustomerDetails(customer);
             }
         } 
         else 
         {
             // If nothing is selected a warning message will pop-up
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(CustomerPane.getPrimaryStage());
             alert.setTitle("No Selection");
             alert.setHeaderText("No Customer Selected");
             alert.setContentText("Please select a customer in the table to update.");

             alert.showAndWait();
         }
         
         updateCustomerButton.setDisable(false);
         searchTextField.setFocusTraversable(false);
    }
    
    // Initializes the columns for the Customer Table
    public void initalizeCustomer()
    {
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        dobColumn.setCellValueFactory(cellData -> cellData.getValue().dateofBirthProperty());
        dobColumn.setComparator(new DateComparator());
    }
    
    // Loads all the Customers from that database into the Customer table
    public void loadCustomers() throws FileNotFoundException, IOException, SQLException
    {
        
    	customerData.clear();
            
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
                customer.setPostalCode(result.getString(7));
                customer.setPhoneNumber(result.getString(8));
                customer.setEmailAddress(result.getString(9));
                customer.setDateOfBirth((result.getDate(10)).toLocalDate());
                customer.setCertAgency(result.getString(11));
                customer.setCertDiveNo(result.getString(12));
						
                customerData.add(customer);
            }
        }
        catch (SQLException e)
        {
            AlertUtil.showErrorAlert("Error with loading customers to the table from the database", e);
        }
        finally
        {
            try
            {
                if (statement != null)
                {
                    statement.close();
                }	
                if (result != null)
                {
                    result.close();
                }
            }
            catch (SQLException e)
            {
                AlertUtil.showErrorAlert("Error with Database", e);
            }
        }
        
        customersTable.setItems(customerData);  
    }
    
    /**
    * Fills all text fields in the Customer Details pane to show details about the customer.
    * If the specified customer is null, all the text fields are cleared.
    */
    private void showCustomerDetails(Customer customer)
    {
        if (customer != null) 
        {
            firstNameLabel.setText(customer.getFirstName());
            lastNameLabel.setText(customer.getLastName());
            streetLabel.setText(customer.getStreet());
            postalCodeLabel.setText(customer.getPostalCode());
            cityLabel.setText(customer.getCity());
            stateLabel.setText(customer.getState());
            phoneNumberLabel.setText(customer.getPhoneNumber());
            emailAddressLabel.setText(customer.getEmailAddress());            
            dobLabel.setText(DateUtil.format(customer.getDateOfBirth()));
            certAgencyLabel.setText(customer.getCertAgency());
            certDiveNoLabel.setText(customer.getCertDiveNo());
        } 
        else 
        {
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
    public void initalizeSearchField()
    {
    	filteredData = new FilteredList<>(customerData, p -> true);

        // Sets the search filter Predicate whenever the search values change.
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> 
        {
            filteredData.setPredicate(customer -> 
            {
                // If the search field text is empty, display all customers in the table
                if (newValue == null || newValue.isEmpty()) 
                {
                    return true;
                }

                // Compares the name, id, and date of birth of every customer with the search text
                String lowerCaseFilter = newValue.toLowerCase();
                String fullName = customer.getFirstName().toLowerCase() + " " + 
                        customer.getLastName().toLowerCase();
       
                if (customer.getFirstName().toLowerCase().contains(lowerCaseFilter)) 
                {
                    return true; // Search matches first name.
                } 
                else if (customer.getLastName().toLowerCase().contains(lowerCaseFilter)) 
                {
                    return true; // Search matches last name.
                } 
                else if (fullName.contains(lowerCaseFilter)) 
                {
                    return true;  // Search matches full name.
                } 
                else if (DateUtil.format(customer.getDateOfBirth()).contains(lowerCaseFilter))
                {
                    return true;   // Search matches date of birth.
                }
                return false; // Search does not match any data.
            });
        });
        
        SortedList<Customer> sortedData = new SortedList<>(filteredData);

        //  Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(customersTable.comparatorProperty());

        //  Add sorted (and filtered) data to the table.
        customersTable.setItems(sortedData);
        // sets the table with the matching filtered search data
        customersTable.setItems(sortedData);
    }
    
    // Clears the search text field - displays all customer data in the table.
    @FXML
    public void clearSearch()
    {
    	searchTextField.clear();
    }
    
    // 
    public class DateComparator implements Comparator<String>
    {
        @Override
        public int compare(String str1, String str2)
        {
            LocalDate dateStr1 = DateUtil.parse(str1);
            LocalDate dateStr2 = DateUtil.parse(str2);
            
            return dateStr1.compareTo(dateStr2);
        }
    }
    
    
    @FXML
    public void transitionToDiveSchedule() throws IOException
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/dive_schedule/view/DiveSchedule.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );     
    }
    
    @FXML
    public void transitionToHome(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/home/view/HomePane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    @FXML
    private void transitionToRecords(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/records/view/RecordsPane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    @FXML
    public void exitProgram(ActionEvent event)
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}