/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scuba.solutions.ui.customers.view;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Loader for the Customer Pane in the Scuba Solutions App.
 * @author Jonathan Balliet, Samuel Brock
 */


public class CustomerPane extends Application {
	
    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
     Parent root = FXMLLoader.load(getClass().getResource("CustomerPane.fxml"));
     Scene scene = new Scene(root);
    
     primaryStage.setScene(scene);
     primaryStage.show();  
     //primaryStage.setTitle("Customers");
        
    }

    /**
     * Returns the primary stage.
     * @return
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }


    public static void main(String[] args) {
        launch(args);
    }
    
}
