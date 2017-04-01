/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.home.view;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scuba.solutions.Main;

/**
 * FXML Controller class
 *
 * @author Jon
 */
public class HomePaneController implements Initializable {
    Stage primaryStage;
    
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
    private Label todayDivelabel1;
    @FXML
    private Label todayDivelabel2;
    @FXML
    private Label todayDivelabel3;
    @FXML
    private Label nextDiveLabel;
    @FXML
    private JFXButton helpButton;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label currentDateLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        homeButton.setDisable(true);
    }



    

    @FXML
    public void transitionToCustomers(ActionEvent event) throws IOException
    {
              Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/customers/view/CustomerPane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    @FXML
    public void transitionToDiveSchedule(ActionEvent event) throws IOException 
    {
                Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(getClass().getResource("/scuba/solutions/ui/dive_schedule/view/DiveSchedule.fxml"));
	    Parent root = loader.load();
        //Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    @FXML
    public void exitProgram(ActionEvent event)
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
    
}
