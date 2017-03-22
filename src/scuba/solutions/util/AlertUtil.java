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

    public static void showDbSavedAlert(String content)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Saved!");
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void noSelectionSAlert(String header, String content)
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.initOwner(DiveSchedulePane.getPrimaryStage());
        alert.setTitle("No Selection");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

     // Confirms the customer addition or changes before they are put into effect.
     // If the user clicks OK - returns true and update is put into effect.
     // Otherwise, returns false and not put into effect.
    public static boolean confirmChangesAlert()
    {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Please confirm the update");
        alert.setContentText("Press OK to confirm the update!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
        // ... user chose OK
            return true;
        } else 
        {
        // ... user chose CANCEL or closed the dialog
            return false;
        }
    }
}
