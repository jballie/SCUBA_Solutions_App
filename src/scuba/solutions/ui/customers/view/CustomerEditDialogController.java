
package scuba.solutions.ui.customers.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.DateUtil;

/**
 * Controller class for the edit dialog for a Customer. This class handles the additions and changes for 
 * a Customer profile in Scuba Solutions's databases.
 * @author Jonathan Balliet, Samuel Brock
 * 
 */
public class CustomerEditDialogController implements Initializable {
    
    private final static String[] STATES = {"AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL","GA","GU",
                                     "HI","IA","ID", "IL","IN","KS","KY","LA","MA","MD","ME","MH",
                                     "MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY", 
                                     "OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA",
                                     "VI","VT","WA","WI","WV","WY"};
    
    private ObservableList<String> statesData;
    
    private Stage dialogStage;
    
    private Customer customer;
    
    private boolean okClicked = false;
    
    @FXML
    private TextField streetField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField firstNameField;
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
    	
    	statesData = FXCollections.observableArrayList();
    	statesData.addAll(STATES);
        stateComboBox.setItems(statesData);
        
        
        stateComboBox.setOnKeyReleased(new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) 
        {
            String s = jumpTo(event.getText(), stateComboBox.getValue(), stateComboBox.getItems());
            if (s != null) 
            {
                stateComboBox.setValue(s);
            }
        }
        });  
    }    
    
    
    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage;
    }
    
    /**
     * Sets the customer to be edited in the dialog.
     */
    public void setCustomer(Customer customer) 
    {
        this.customer = customer;
        // sets all the text fields to this customer's attributes.
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        streetField.setText(customer.getStreet());
        postalCodeField.setText(customer.getPostalCode());
        cityField.setText(customer.getCity());
        stateComboBox.setValue(customer.getState());         
        phoneNumField.setText(customer.getPhoneNumber());
        emailAddressField.setText(customer.getEmailAddress());
        dobField.setValue(customer.getDateOfBirth());
        certAgencyField.setText(customer.getCertAgency());
        certDiveNoField.setText(customer.getCertDiveNo());       
    }
    /**
     * Returns true if the user clicked OK, false otherwise.
     */
    public boolean isOkClicked()
    {
        return okClicked;
    }

    /**
     * Called when the user clicks save. Saves the changes after the confirmation is made.
     */
    @FXML
    public void handleSave() 
    {
        if (isInputValid()) 
        {
            customer.setFirstName(firstNameField.getText());
            customer.setLastName(lastNameField.getText());
            customer.setStreet(streetField.getText());
            customer.setPostalCode(postalCodeField.getText());
            customer.setCity(cityField.getText());
            customer.setState(stateComboBox.getValue());
            customer.setDateOfBirth(dobField.getValue());
            customer.setPhoneNumber(phoneNumField.getText());
            customer.setEmailAddress(emailAddressField.getText());
            customer.setCertAgency(certAgencyField.getText());
            customer.setCertDiveNo(certDiveNoField.getText());
            
            // Confirms the save changes before putting them into effect.
            boolean confirm = AlertUtil.confirmChangesAlert();
            if(confirm)
            {
            	okClicked = true;
            	dialogStage.close();
            }
            else
            {
            	okClicked = false;
            	//dialogStage.close();
            }
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    public void handleCancel()
    {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    public boolean isInputValid() 
    {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) 
        {
            errorMessage += "No valid first name!\n"; 
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) 
        {
            errorMessage += "No valid last name!\n"; 
        }
        if (streetField.getText() == null || streetField.getText().length() == 0)
        {
            errorMessage += "No valid street!\n"; 
        }

        if (postalCodeField.getText() == null || (postalCodeField.getText().length() !=5)) {
            errorMessage += "No valid postal code! Postal code must be 5 characters long! \n"; 
        } 
        else 
        {
            // try to parse the postal code into an int.
            try 
            {
                Integer.parseInt(postalCodeField.getText());
            } 
            catch (NumberFormatException e) 
            {
                errorMessage += "No valid postal code (must be a number value)!\n"; 
            }
        }

        if (cityField.getText() == null || cityField.getText().length() == 0) 
        {
            errorMessage += "No valid city!\n"; 
        }
        
        if (stateComboBox.getValue() == null || stateComboBox.getValue().length() == 0) 
        {
            errorMessage += "No valid state!\n"; 
        }

        if (phoneNumField.getText() == null || phoneNumField.getText().length() == 0) 
        {
            errorMessage += "No valid phone number!\n"; 
        }
            else if(!isPhoneNumber())
            {
                errorMessage += "Does not match Phone Number format! Must be formated as: 000-000-0000";
            }
        
        if (emailAddressField.getText() == null || emailAddressField.getText().length() == 0) 
        {
            errorMessage += "No valid email address!\n"; 
        }
            else if(!isEmailAddress())
            {
                errorMessage += "Does not match a valid email format! Please enter a valid Email Address!\n";
            }
    
        if (dobField.getValue() == null || DateUtil.validDate(dobField.getValue().toString())) 
        {
            errorMessage += "No valid date of birth!\n";
        }
            else if(!isAdult()) 
            {
            	errorMessage += "Customer is not an Adult! Customer must be 18 or older! \n";
            
            } 
        
        if (certAgencyField.getText() == null || certAgencyField.getText().length() == 0)
        {
            errorMessage += "No valid Certification Agency!\n";
        }
        
        if (certDiveNoField.getText() == null || certDiveNoField.getText().length() == 0 )
        {
            errorMessage += "No valid Certification Dive No.!\n";
        } 
        else 
        {
            // try to parse the cert dive no. into an int.
            try 
            {
                Integer.parseInt(postalCodeField.getText());
            } 
            catch (NumberFormatException e) 
            {
                errorMessage += "No valid certification dive no. (must be a number value)!\n"; 
            }
        }
       
        if (errorMessage.length() == 0) 
        {
            return true;
        } 
        else 
        {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
            return false;            
        }    
    }
    
    // Determines whether the age value inputed is 18 or older.
    public boolean isAdult()
    {
        int currentYear = LocalDate.now().getYear();
        int birthYear = dobField.getValue().getYear();
        
        long years = currentYear - birthYear;
        
        return years >= 18;
    }
    
    
    // Determines whether value inputed is an email address
    public boolean isEmailAddress()
    {
    	return emailAddressField.getText().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
                
    }
    
    public boolean isPhoneNumber()
    {
        return phoneNumField.getText().matches("\\d{3}[-]\\d{3}[-]\\d{4}");
    }
    
    
    // Goes to the matching letter value in the states comboBox - based on the key user presses.
    public static String jumpTo(String keyPressed, String currentlySelected, List<String> items) 
    {
        String key = keyPressed.toUpperCase();
        if (key.matches("^[A-Z]$")) {
            // Only act on letters so that navigating with cursor keys does not
            // try to jump somewhere.
            boolean letterFound = false;
            boolean foundCurrent = currentlySelected == null;
            for (String s : items) {
                if (s.toUpperCase().startsWith(key)) {
                    letterFound = true;
                    if (foundCurrent) {
                        return s;
                    }
                    foundCurrent = s.equals(currentlySelected);
                }
            }
            if (letterFound) {
                return jumpTo(keyPressed, null, items);
            }
        }
        return null;
    }

    
}