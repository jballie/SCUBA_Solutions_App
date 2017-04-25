
package scuba.solutions.ui.customers.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.DateUtil;

/**
 * Controller class for the edit dialog for a Customer. This class handles the additions and changes for 
 * a Customer profile for SCUBA SCUBA Now.
 * @author Jonathan Balliet, Samuel Brock
 */
public class CustomerEditDialogController implements Initializable {
    
    protected final static String[] STATES = {"AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL","GA","GU",
                                     "HI","IA","ID", "IL","IN","KS","KY","LA","MA","MD","ME","MH",
                                     "MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY", 
                                     "OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA",
                                     "VI","VT","WA","WI","WV","WY"};
    
    protected ObservableList<String> statesData;
    
    protected Stage dialogStage;
    
    protected Customer customer;
    
    protected boolean saveClicked = false;
    
    @FXML
    protected TextField streetField;
    @FXML
    protected TextField lastNameField;
    @FXML
    protected TextField firstNameField;
    @FXML
    protected TextField cityField;
    @FXML
    protected ComboBox<String> stateComboBox;
    @FXML
    protected TextField postalCodeField;
    @FXML
    protected TextField phoneNumField;
    @FXML
    protected TextField emailAddressField;
    @FXML
    protected JFXDatePicker dobField;
    @FXML
    protected TextField certAgencyField;
    @FXML
    protected TextField certDiveNoField;
    @FXML
    protected JFXButton saveButton;
    @FXML
    protected JFXButton cancelButton;
    
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
     * Returns true if the user clicked Save, false otherwise.
     */
    public boolean isSaveClicked()
    {
        return saveClicked;
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
            	saveClicked = true;
            	dialogStage.close();
            }
            else
            {
            	saveClicked = false;
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
            errorMessage += "First name is not valid.\n"; 
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) 
        {
            errorMessage += "Last name is not valid.\n"; 
        }
        if (streetField.getText() == null || streetField.getText().length() == 0)
        {
            errorMessage += "Street is not valid.\n"; 
        }

        if (postalCodeField.getText() == null || (postalCodeField.getText().length() !=5)) {
            errorMessage += "Postal code is not valid. Please enter a postal code that is "
                    + "5 digits long. \n"; 
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
                errorMessage += "Postal code is not valid. Please enter digits for the "
                        + "postal code.\n"; 
            }
        }

        if (cityField.getText() == null || cityField.getText().length() == 0) 
        {
            errorMessage += "City is not valid.\n"; 
        }
        
        if (stateComboBox.getValue() == null || stateComboBox.getValue().length() == 0) 
        {
            errorMessage += "State is not valid.\n"; 
        }

        if (phoneNumField.getText() == null || phoneNumField.getText().length() == 0) 
        {
            errorMessage += "Phone number is not valid.\n"; 
        }
            else if(!isPhoneNumber())
            {
                errorMessage += "Phone Number format is not valid. Please enter a phone number"
                        + "matching the format: 000-000-0000";
            }
        
        if (emailAddressField.getText() == null || emailAddressField.getText().length() == 0) 
        {
            errorMessage += "Email address is not valid.\n"; 
        }
            else if(!isEmailAddress())
            {
                errorMessage += "Please enter a valid email address that contain a @.com\n";
            }
    
        if (dobField.getValue() == null || DateUtil.validDate(dobField.getValue().toString())) 
        {
            errorMessage += "Date of Birth is not valid.\n";
        }
            else if(!isAdult()) 
            {
            	errorMessage += "Customer is not an Adult. A customer must be 18 years or older. \n";
            
            } 
        
        if (certAgencyField.getText() == null || certAgencyField.getText().length() == 0)
        {
            errorMessage += "Certification Agency is not valid.\n";
        }
        
        if (certDiveNoField.getText() == null || certDiveNoField.getText().length() == 0 )
        {
            errorMessage += "Certification Dive No. is not valid.\n";
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
                errorMessage += "Certification dive no. is not valid. Please enter a"
                        + "number\n"; 
            }
        }
       
        if (errorMessage.length() == 0) 
        {
            return true;
        } 
        else 
        {
            AlertUtil.invalidInputAlert(errorMessage);
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
    
    
    // Determines whether value inputed matches an email adress format.
    public boolean isEmailAddress()
    {
    	return emailAddressField.getText().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
                
    }
    
    // Determines whether the phone number entered matached the ###-###-#### format.
    public boolean isPhoneNumber()
    {
        return phoneNumField.getText().matches("\\d{3}[-]\\d{3}[-]\\d{4}");
    }
    
    
    // Goes to the matching letter value in the states comboBox - based on the key user presses.
    public static String jumpTo(String keyPressed, String currentlySelected, List<String> items) 
    {
        String key = keyPressed.toUpperCase();
        if (key.matches("^[A-Z]$")) {
     
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