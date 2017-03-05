/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.customers.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Jon
 */
public class DiveSchedulePaneController implements Initializable {
    private Stage primaryStage;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXButton homeButton;
    @FXML
    private JFXButton diveScheduleButton;
    @FXML
    private JFXButton customersButton;
    @FXML
    private JFXButton exitButton;
    @FXML
    private JFXTextField searchTextField;
    @FXML
    private JFXButton clearSearchButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         primaryStage = DiveSchedulePane.getPrimaryStage();
         diveScheduleButton.setDisable(true);
    }    

    @FXML
    private void clearSearch(ActionEvent event) {
    }
    
    
    @FXML
    public void transitionToCustomers() throws IOException
    {
         Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
    	    loader.setLocation(getClass().getResource("CustomerPane.fxml"));
    	    Parent root = loader.load();
            //Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show( );
    }
}
