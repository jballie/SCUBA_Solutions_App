package scuba.solutions.util;

import java.sql.SQLException;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import scuba.solutions.ui.dive_schedule.view.DiveSchedulePane;

/**
 * Alert Utility Class. Contains static method to call the various alert dialogs
 * that pop-up for data validation, confirmation, invalid selection, and various
 * errors.
 * @author Jonathan Balliet, Samuel Brock
 */
public class AlertUtil 
{
    // Displays an alert for Database and SQL errors with the error message.
    public static void showDbErrorAlert(String content, SQLException e)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error!");
        alert.setContentText(content +
            "\n\nSQL Error Info:\n" + e.toString());
        alert.showAndWait(); 	
    }
    
    // Displays an alert that the customer has already been reserved for a dive trip.
    public static void showCustomerAlreadyThere()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Customer has already signed up for this dive trip");
        alert.showAndWait();
    }
    
    // Displays an alert when the user tries to update a trip that is fully booked.
    public static void showMaxCapacity()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("The trip's availability is at max capacity. No more reservations"
                + " can be booked for this dive.");
        alert.showAndWait();
    }
    
    // Displays an alert that the email successfully sent.
    public static void showEmailSent(String content)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Email Sent!");
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Displays an alert that the changes saves successfully in the database.
    public static void showDbSavedAlert(String content)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Saved!");
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Displays an alert that no selection has been made for the specific button function.
    public static void noSelectionAlert(String header, String content)
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Displays an alert that invalid data has been entered with the needed corrections.
    public static void invalidInputAlert(String errorMessage)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Fields");
        alert.setHeaderText("Please correct invalid fields");
        alert.setContentText(errorMessage);
        alert.showAndWait();
        
    }
    
    // Displays an error alert with the error message.
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
        alert.setHeaderText("Please confirm the update");
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
    
    // Confirms the cancellation of a dive trip.
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
