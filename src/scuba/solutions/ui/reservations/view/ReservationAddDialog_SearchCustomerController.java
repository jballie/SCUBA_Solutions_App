package scuba.solutions.ui.reservations.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.customers.view.CustomerPaneController;
import scuba.solutions.util.AlertUtil;

/**
 * Controller class for searching for a customer in the new reservation process.
 * This class inherits from its parent CustomerPane.
 * @author Jonathan Balliet, Samuel Brock
 */
public class ReservationAddDialog_SearchCustomerController extends CustomerPaneController  implements Initializable 
{
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
            AlertUtil.showErrorAlert("Error with loading customers!\n", e);
        }
        catch (IOException | SQLException e)
        {
            AlertUtil.showErrorAlert("Error with loading customers!\n", e);
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
            ReservationAddDialog_SearchCustomerController.customer = customer;
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

    public void setDialogStage(Stage dialogStage) 
    {
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
