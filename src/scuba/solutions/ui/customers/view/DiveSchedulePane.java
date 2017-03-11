
package scuba.solutions.ui.customers.view;


import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Jon
 */
public class DiveSchedulePane extends Application {
    
    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
     Parent root = FXMLLoader.load(getClass().getResource("DiveSchedule.fxml"));
     Scene scene = new Scene(root);
    
     primaryStage.setScene(scene);
     primaryStage.show();  
     primaryStage.setTitle("Dive Schedule");
        
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
