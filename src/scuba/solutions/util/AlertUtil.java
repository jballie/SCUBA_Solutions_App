package scuba.solutions.util;

import java.sql.SQLException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import scuba.solutions.ui.dive_schedule.view.DiveSchedulePane;

public class AlertUtil 
{

    public static void showDbErrorAlert(String content, SQLException e)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error!");
        alert.setContentText(content +
            "\n\nSQL Error Info:\n" + e.toString());
        alert.showAndWait(); 	
    }
    
    public static void showCustomerAlreadyThere()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Customer already signed up for this event");
        // alert.setContentText(content);
        alert.showAndWait();
    }
    
    
    public static void showEmailSent(String content)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Email Sent!");
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showDbSavedAlert(String content)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Saved!");
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void noSelectionAlert(String header, String content)
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    
    public static void invalidInputAlert(String errorMessage)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Fields");
        alert.setHeaderText("Please correct invalid fields");
        alert.setContentText(errorMessage);
        alert.showAndWait();
        
    }
    
    public static void showErrorAlert(String content, Exception e)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(DiveSchedulePane.getPrimaryStage());
        alert.setHeaderText("Error!");
        alert.setContentText(content +
            "\n\nError Info:\n" + e.toString());
        alert.showAndWait(); 	
    }

     // Confirms the customer addition or changes before they are put into effect.
     // If the user clicks OK - returns true and update is put into effect.
     // Otherwise, returns false and not put into effect.
    public static boolean confirmChangesAlert()
    {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation!");
        alert.setHeaderText("Please confirm the update or addition");
        alert.setContentText("Press OK to confirm!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
            return true;
        } 
        else 
        {
            return false;
        }
    }
    
    public static boolean confirmCancelAlert()
    {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Cancellation!");
        alert.setHeaderText("Please confirm the cancellation for this dive trip.");
        alert.setContentText("Press OK to confirm this cancellation!");
            Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
            return true;
        } 
        else 
        {

            return false;
        }
    }
}
