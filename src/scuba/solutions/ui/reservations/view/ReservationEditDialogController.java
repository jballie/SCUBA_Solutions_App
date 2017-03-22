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
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Jon
 */
public class ReservationEditDialogController implements Initializable {

    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private GridPane amountTextField;
    @FXML
    private JFXDatePicker ccConfirmDatePicker;
    @FXML
    private JFXDatePicker dateProcTimePicker;
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

    @FXML
    private void handleSave(ActionEvent event) {
    }

    @FXML
    private void handleCancel(ActionEvent event) {
    }
    
}
