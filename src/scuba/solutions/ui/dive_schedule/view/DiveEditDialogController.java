/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.dive_schedule.view;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.util.AlertUtil;

/**
 * FXML Controller class
 *
 * @author Jon
 */
public class DiveEditDialogController implements Initializable {
	
    private Stage dialogStage;

    private DiveTrip trip;

    private boolean okClicked = false;
	
    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXDatePicker tripDatePicker;

    @FXML
    private JFXDatePicker departTimePicker;

    @FXML
    private JFXRadioButton yesCancelRadio;

    @FXML
    private ToggleGroup cancelRadios;

    @FXML
    private JFXRadioButton noCancelRadio;

    /**
     * Initializes the controller class.
     */
	
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {

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
     * @param trip
     */
    public void setDive(DiveTrip trip) 
    {
        this.trip = trip;
        // sets all the text fields to this customer's attributes.
        tripDatePicker.setValue(trip.getTripDate());
        departTimePicker.setTime(trip.getDepartTime());    
    }
    /**
     * Returns true if the user clicked OK, false otherwise.
     * @return 
     */
    public boolean isOkClicked() 
    {
        return okClicked;
    }

    /**
     * Called when the user clicks save. Saves the changes after the confirmation is made.
     */
    @FXML
    private void handleSave() 
    {
        if (isInputValid()) 
        {
            trip.setTripDate(tripDatePicker.getValue());
            trip.setDepartTime(departTimePicker.getTime());
            if(yesCancelRadio.isSelected())
            {
            	trip.setWeatherStatus("Cancelled");
            }
            else 
            {
            	trip.setWeatherStatus("OK");
            }
          
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
            	dialogStage.close();
            }
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() 
    {
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

        if (tripDatePicker.getValue() == null)
        {
            errorMessage += "No valid date of dive trip!\n"; 
        }
     //   else if(!isValidDate()) 
       // {
        //	errorMessage += "Dive trip date is not valid. Is not a current or future date!\n";
        //} 
        if (departTimePicker.getTime() == null) 
        {
            errorMessage += "No valid depart time!\n"; 
        }
        if (!noCancelRadio.isSelected() && !yesCancelRadio.isSelected())
        {
            errorMessage += "No radio button selected for trip status!\n"; 
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
    
    private boolean isValidDate()
    {
    	LocalDate date = tripDatePicker.getValue();
    	
    	return date.compareTo(LocalDate.now()) >= 0;
    }
    
    
}
