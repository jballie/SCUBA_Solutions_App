package scuba.solutions;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starts the application from the Home Pane.
 * 
 * @author Jonathan Balliet, Samuel Brock
 */
public class Main extends Application {
    
    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException 
    {
        Parent root = FXMLLoader.load(getClass().getResource("/scuba/solutions/ui/home/view/HomePane.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();   
    }

    /**
     * Returns the primary stage.
     * @return
     */
    public static Stage getPrimaryStage() 
    {
        return primaryStage;
    }

    // Launches the app
    public static void main(String[] args)
    {
        launch(args);
    }
}