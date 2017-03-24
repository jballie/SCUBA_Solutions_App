/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.reservations.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import scuba.solutions.ui.reservations.model.Payment;
import scuba.solutions.ui.reservations.model.Waiver;

/**
 * FXML Controller class
 *
 */
public class ReservationEditDialogController implements Initializable {

    private Stage dialogStage;
    private boolean okClicked = false;
    private Waiver waiver;
    private Payment payment; 
    
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXTextField amountTextField;
    @FXML
    private JFXTextField ccConfirmTextField;
    @FXML
    private JFXDatePicker dateProcDatePicker;
    @FXML
    private JFXDatePicker dateSignedDatePicker;
    @FXML
    private JFXTextField erFirstTextField;
    @FXML
    private JFXTextField erLastTextField;
    @FXML
    private JFXTextField erPhoneTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage;
    }
    
    @FXML
    private void handleSave(ActionEvent event) {
        if(isInputValid()) {
            waiver.setDateSigned(dateSignedDatePicker.getValue());
            waiver.setERFirst(erFirstTextField.getText());
            waiver.setERLast(erLastTextField.getText());
            waiver.setERPhone(erPhoneTextField.getText());
            
            payment.setCCConfirmNo(Integer.parseInt(ccConfirmTextField.getText()));
            payment.setDateProcessed(dateProcDatePicker.getValue());
            payment.setAmount(Integer.parseInt(amountTextField.getText()));
            
            
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
     * Allows user to confirm entry
     * 
     * @return 
     */
    private boolean confirmation()
    {
    	
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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
    
    /**
     * Closes dialog stage if user cancels
     * @param event 
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }
    
    /**
     * Sets Waiver data in dialog box
     * 
     * @param waiver 
     */
    public void setWaiver(Waiver waiver) {
        this.waiver = waiver;
        dateSignedDatePicker.setValue(waiver.getDateSigned());
        erFirstTextField.setText(waiver.getERFirst());
        erLastTextField.setText(waiver.getERLast());
        erPhoneTextField.setText(waiver.getERPhone());
    }
    
    /**
     * Return true if okClicked
     * 
     * @return 
     */
    public boolean isOkClicked()
    {
        return okClicked;
    }
    
    /**
     * Sets Payment data in dialog box
     * 
     * @param payment 
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
        amountTextField.setText(Integer.toString(payment.getAmount()));
        dateProcDatePicker.setValue(payment.getDateProcessed());
        ccConfirmTextField.setText(Integer.toString(payment.getCCConfirmNo()));
    }
    
    /**
     * Validates user entries
     * 
     * @return 
     */
    public boolean isInputValid()
    {
        String errorMessage = " ";
        
        try {
            Integer.parseInt(amountTextField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "No valid amount (must be an integer)!\n"; 
        }
        
        try {
            Integer.parseInt(ccConfirmTextField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "No valid credit card conformation (must be an integer)!\n"; 
        }
        
        if(errorMessage.equals(" ")) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
            return false;          
        }
          
    }
    
}
