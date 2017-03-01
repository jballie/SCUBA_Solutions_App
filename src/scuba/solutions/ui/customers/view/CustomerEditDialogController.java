/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    public void initialize(URL url, ResourceBundle rb) {
    	
    	statesData = FXCollections.observableArrayList();
    	statesData.addAll(STATES);
        stateComboBox.setItems(statesData);
        
        
        stateComboBox.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String s = jumpTo(event.getText(), stateComboBox.getValue(), stateComboBox.getItems());
                if (s != null) {
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
     * 
     * @param person
     */
    public void setCustomer(Customer customer) 
    {
        this.customer = customer;
        // sets all the text fields to this customer's attributes.
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        streetField.setText(customer.getStreet());
        postalCodeField.setText(Integer.toString(customer.getPostalCode()));
        cityField.setText(customer.getCity());
        stateComboBox.setValue(customer.getState());         
        phoneNumField.setText(customer.getPhoneNumber());
        emailAddressField.setText(customer.getEmailAddress());
        dobField.setValue(customer.getDateOfBirth());
        certAgencyField.setText(customer.getCertAgency());
        certDiveNoField.setText(Integer.toString(customer.getCertDiveNo()));       
    }
    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks save. Saves the changes after the confirmation is made.
     */
    @FXML
    private void handleSave() {
        if (isInputValid()) 
        {
        	customer.setFirstName(firstNameField.getText());
            customer.setLastName(lastNameField.getText());
            customer.setStreet(streetField.getText());
            customer.setPostalCode(Integer.parseInt(postalCodeField.getText()));
            customer.setCity(cityField.getText());
            customer.setState(stateComboBox.getValue());
            customer.setDateOfBirth(dobField.getValue());
            customer.setEmailAddress(emailAddressField.getText());
            customer.setCertAgency(certAgencyField.getText());
            customer.setCertDiveNo(Integer.parseInt(certDiveNoField.getText()));
            
            // Confirms the save changes before putting them into effect.
            boolean confirm = confirmation();
            if(confirm)
            {
            	okClicked = true;
            	dialogStage.close();
            }
            else
            {
            	okClicked = false;
            	dialogStage.close();
            }
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() 
    {
        String errorMessage = "";

        if (firstNameField.getText() == null || firstNameField.getText().length() == 0) {
            errorMessage += "No valid first name!\n"; 
        }
        if (lastNameField.getText() == null || lastNameField.getText().length() == 0) {
            errorMessage += "No valid last name!\n"; 
        }
        if (streetField.getText() == null || streetField.getText().length() == 0) {
            errorMessage += "No valid street!\n"; 
        }

        if (postalCodeField.getText() == null || postalCodeField.getText().length() == 0) {
            errorMessage += "No valid postal code!\n"; 
        } else {
            // try to parse the postal code into an int.
            try {
                Integer.parseInt(postalCodeField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid postal code (must be an integer)!\n"; 
            }
        }

        if (cityField.getText() == null || cityField.getText().length() == 0) {
            errorMessage += "No valid city!\n"; 
        }
        
        if (stateComboBox.getValue() == null) {
            errorMessage += "No valid state!\n"; 
        }

        if (phoneNumField.getText() == null || phoneNumField.getText().length() == 0) {
            errorMessage += "No valid phone number!\n"; 
        }
        
        if (emailAddressField.getText() == null || emailAddressField.getText().length() == 0) {
            errorMessage += "No valid email address!\n"; 
        }
    
        if (dobField.getValue() == null ) {
            errorMessage += "No valid date of birth!\n";
        }
            else if(!isAdult()) 
            {
            	errorMessage += "Customer is not an Adult!\n";
            
            } 
        
        
        try {
            Integer.parseInt(certDiveNoField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "No valid certification dive number (must be an integer)!\n"; 
        }        

        if (errorMessage.length() == 0) {
            return true;
        } else {
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
    private boolean isAdult()
    {
        int currentYear = LocalDate.now().getYear();
        int birthYear = dobField.getValue().getYear();
        
        long years = currentYear - birthYear;
        
        if(years >= 18) {
            return true;
        }
        
        return false;
    }
    
    
    // Determines whether value inputed is an email address
    private boolean isEmailAddress()
    {
    	// uses regular expressions to determine this.
    	return false;
    }
    
    // Determines whether value inputes is a postal code
    private boolean isPostalCode()
    {
    	
    	// uses regular expression to determine this.
    	return false;
    	
    }
    
    
    

    // Goes to the matching letter value in the states comboBox - based on the key user presses.
    private static String jumpTo(String keyPressed, String currentlySelected, List<String> items) 
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
    
    // Confirms the customer addition or changes before they are put into effect.
    // If the user clicks OK - returns true and update is put into effect.
    // Otherwise, returns false and not put into effect.
    private boolean confirmation()
    {
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Confirmation Dialog");
    	alert.setHeaderText("Please confirm the update");
    	alert.setContentText("Press OK to confirm the update!");

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    	    // ... user chose OK
    		return true;
    	} else {
    	    // ... user chose CANCEL or closed the dialog
    		return false;
    	}
    	
    }
    
}