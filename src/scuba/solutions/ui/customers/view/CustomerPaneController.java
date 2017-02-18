/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.customers.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scuba.solutions.ui.customers.model.Customer;

/**
 * FXML Controller class
 *
 * @author Jon
 */
public class CustomerPaneController implements Initializable {

   private ObservableList<Customer> customerData = FXCollections.observableArrayList();
    @FXML
    private Button homeButton;
    @FXML
    private Button diveScheduleButton;
    @FXML
    private Button customersButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private ComboBox<?> searchByButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button addCustomerButton;
    @FXML
    private Button updateCustomerButton;
    @FXML
    private TableView<?> customersTable;
    @FXML
    private TableColumn<?, ?> firstNameColumn;
    @FXML
    private TableColumn<?, ?> lastNameColumn;
    @FXML
    private TableColumn<?, ?> dobColumn;
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
   
   //private DatabaseHandler handler;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // databaseHandler = DatabaseHandler.getInstance();
        // initalizeCustomer();
        // loadCustomer();
        // Clear person details.
        //showCustomerDetails(null);

        // Listen for selection changes and show the person details when changed.
        // customerTable.getSelectionModel().selectedItemProperty().addListener(
        //   (observable, oldValue, newValue) -> showCustomerDetails(newValue));
    }

    // when add button clicked on - goes to edit dialog customer
    public void addCustomer(Customer customer)
    {
        
    }
    
    // when update button clicked on - goes to edit dialog customer
    public void updateCustomer(Customer customer)
    {
        
    }
    
    // initalizes the columns for the TableView
    public void initalizeCustomer()
    {
        /*
        EX:
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        */
        
    }
    
    // Loads all the Customers from that database into the tableView
    // Think of it as a 'refresh' (will be called when transitions to Customer pane
    // and after any customers have been added or updated.
    public void loadCustomers( )
    {
        customerData.clear();  // clears obserbalist list to stert
        
        // queries DB to get all data for each column
        // stores the result set into an equivalent variable
        // creates a Customer object and adds to the list
        //  customerTable.setItem(list)
    }
    
    /**
    * Fills all text fields to show details about the customer.
    * If the specified person is null, all text fields are cleared.

    */
    private void showCustomerDetails(Customer customer)
    {
        
    }
    
    
    
}
