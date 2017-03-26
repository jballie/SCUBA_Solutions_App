/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.reservations.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.customers.view.CustomerEditDialogController;

/**
 * FXML Controller class
 *
 * @author Jon
 */
public class ReservationAddDialog_ExistingCustomerController extends CustomerEditDialogController implements Initializable {
    
     private final static String[] STATES = {"AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL","GA","GU",
                                     "HI","IA","ID", "IL","IN","KS","KY","LA","MA","MD","ME","MH",
                                     "MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY", 
                                     "OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA",
                                     "VI","VT","WA","WI","WV","WY"};
    
    private ObservableList<String> statesData;
    
    private Stage dialogStage;
    
    public static Customer customer;
    
    private boolean okClicked = false;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField streetField;
    @FXML
    private TextField cityField;
    @FXML
    private ComboBox<String> stateComboBox;
    @FXML
    private TextField postalCodeField;
    @FXML
    private TextField phoneNumField;
    @FXML
    private TextField emailAddressField;
    @FXML
    private JFXDatePicker dobField;
    @FXML
    private TextField certAgencyField;
    @FXML
    private TextField certDiveNoField;
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        super.initialize(url, rb);
    }    
    
}
