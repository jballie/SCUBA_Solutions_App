
package scuba.solutions.ui.reservations.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import scuba.solutions.ui.reservations.model.Payment;
import scuba.solutions.ui.reservations.model.Waiver;
import scuba.solutions.util.AlertUtil;

/**
 * Controller class for updating a reservation. The payment and waiver information for
 * a reservation can be updated, once these are both complete - the reservation's status
 * is changed to Booked.
 * @author Samuel Brock, Jonathan Balliet
 */
public class ReservationEditDialogController implements Initializable 
{

    private Stage dialogStage;
    private boolean saveClicked = false;
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
    private void handleSave(ActionEvent event) 
    {
        if(isInputValid()) 
        {
            waiver.setDateSigned(dateSignedDatePicker.getValue());
            waiver.setERFirst(erFirstTextField.getText());
            waiver.setERLast(erLastTextField.getText());
            waiver.setERPhone(erPhoneTextField.getText());
            
            payment.setCCConfirmNo(Long.parseLong(ccConfirmTextField.getText()));
            payment.setDateProcessed(dateProcDatePicker.getValue());
            payment.setAmount(Integer.parseInt(amountTextField.getText()));
            
            
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
     * Closes dialog stage if user cancels
     * @param event 
     */
    @FXML
    private void handleCancel(ActionEvent event) 
    {
        dialogStage.close();
    }
    
    /**
     * Sets Waiver data in dialog box
     * 
     * @param waiver 
     */
    public void setWaiver(Waiver waiver) 
    {
        this.waiver = waiver;
        dateSignedDatePicker.setValue(waiver.getDateSigned());
        erFirstTextField.setText(waiver.getERFirst());
        erLastTextField.setText(waiver.getERLast());
        erPhoneTextField.setText(waiver.getERPhone());
    }
    
    /**
     * Return true if saveClicked
     * 
     * @return 
     */
    public boolean isSaveClicked()
    {
        return saveClicked;
    }
    
    /**
     * Sets Payment data in dialog box
     * 
     * @param payment 
     */
    public void setPayment(Payment payment)
    {
        this.payment = payment;
        amountTextField.setText(Integer.toString(payment.getAmount()));
        dateProcDatePicker.setValue(payment.getDateProcessed());
        ccConfirmTextField.setText(Long.toString(payment.getCCConfirmNo()));
    }
    
    /**
     * Validates user entries
     * 
     * @return 
     */
    public boolean isInputValid()
    {
        String errorMessage = " ";
     
        try 
        {
            Integer.parseInt(amountTextField.getText());
        } 
        catch (NumberFormatException e) 
        {
            errorMessage += "The amount is not valid. Please enter a number value for the amount.\n"; 
        }
        
        try
        {
           if ( Integer.parseInt(amountTextField.getText()) > 0 && 
                   Integer.parseInt(amountTextField.getText()) < 150 )
            {
                errorMessage += "The amount is not valid. All dive trips currently cost 150. Please enter atleast 150 for the amount.";
            }
           
           if ( Integer.parseInt(amountTextField.getText()) < 0  )
            {
                errorMessage += "The amount is not valid. All dive trips currently cost 150. Please enter atleast 150 for the amount\n";
            }
           
           
        }
        catch (NumberFormatException e) 
        {
            errorMessage += "The amount is not valid. Please enter a number value for the amount.\n"; 
        }
        
        try 
        {
            Long.parseLong(ccConfirmTextField.getText());
        } 
        catch (NumberFormatException e) 
        {
            errorMessage += "The credit card confirmation number is not valid. Please enter a "
                    + "number value.!\n"; 
        }
        
       if(dateProcDatePicker.getValue() != null)
        {
            if(dateProcDatePicker.getValue().isAfter(LocalDate.now()))
            {
                errorMessage += "The date processed is not valid. Please enter a current or past date. \n"; 
            }
        }
        if(dateSignedDatePicker.getValue() != null)
        {
            if(dateSignedDatePicker.getValue().isAfter(LocalDate.now()))
            {
                errorMessage += "The date signed is not valid. Please enter a current or past date. \n"; 
            }
        }
        
        if(errorMessage.equals(" ")) 
        {
            return true;
        } 
        else 
        {
            AlertUtil.invalidInputAlert(errorMessage);
            return false;          
        }
          
    }
    
}
