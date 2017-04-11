/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.reservations.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.customers.view.CustomerPaneController;
import scuba.solutions.util.DateUtil;

/**
 * FXML Controller class
 *
 * @author Jon
 */
public class ReservationAddDialog_SearchCustomerController extends CustomerPaneController  implements Initializable  {
    private ObservableList<Customer>  customerData = FXCollections.observableArrayList();
    private FilteredList<Customer> filteredData; 
    private static Connection connection;
    private static boolean isCustomerFound = false;
    private boolean isProceedClicked = false;
    private Stage dialogStage;
    private static Customer customer;

    
    @FXML
    private JFXButton proceedButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXTextField searchTextField;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, String> firstNameColumn;
    @FXML
    private TableColumn<Customer, String> lastNameColumn;
    @FXML
    private TableColumn<Customer, String> dobColumn;
    @FXML
    private Label customerLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
         initalizeCustomer();
         
          try
		{
	    	loadCustomers();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
	    }
		catch (SQLException e)
		{
			e.printStackTrace();
		}
        
        showCustomer(null);
        
        customersTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showCustomer(newValue));
        
        initalizeSearchField(); 
        searchTextField.setFocusTraversable(false);

    }    
    
    
    public void showCustomer(Customer customer)
    {
        if (customer != null)
        {
            customerLabel.setText(customer.getFullName());
            this.customer = customer;
        }
        else
        {
            customerLabel.setText("Customer Not Found");
        }
        
    }
  
    @FXML
    private void handleProceed(ActionEvent event) 
    {
        if (customerLabel.getText().equalsIgnoreCase("Customer Not Found"))
        {
            isCustomerFound = false;
            isProceedClicked = true;
            dialogStage.close();
        }
        else
        {
            isCustomerFound = true;
            isProceedClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) 
    {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
       this.dialogStage = dialogStage;
    }

    public boolean isProceedClicked() {
        return isProceedClicked;
    }
    
    public static Customer returnCustomer()
    {
        return customer;
    }
    
    public static boolean returnIsCustomerFound()
    {
        return isCustomerFound;
    }
    
}
