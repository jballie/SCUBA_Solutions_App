
package scuba.solutions.ui.dive_schedule.view;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.emails.EmailAlert;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.ui.reservations.model.Reservation;
import scuba.solutions.util.AlertUtil;

/**
 * Controller class for the Update Dive dialog. The user can change the status
 * of a dive trip. If a dive trip is changed to CANCELLED and confirmed by the user 
 * - cancellation emails will be sent to all reserved customers.
 * @author Jonathan Balliet, Samuel Brock
 */
public class DiveEditDialogController implements Initializable {
	
    private Stage dialogStage;
    private static Connection connection; 
    private DiveTrip trip;
    private boolean saveClicked = false;
    private static boolean isCancelled = false;
    private final DateTimeFormatter tripTime =  DateTimeFormatter.ofPattern("h:mm a");
    private final DateTimeFormatter tripDate = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    @FXML
    private JFXButton saveButton;
    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXRadioButton okRadio;
    @FXML
    private ToggleGroup cancelRadios;
    @FXML
    private JFXRadioButton cancelledRadio;
    @FXML
    private Label tripDateLabel;
    @FXML
    private Label departTimeLabel;

    /**
     * Initializes the controller class.
     */
	
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    } 
    
    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) 
    {
        this.dialogStage = dialogStage; 
    }
    
    /**
     * Sets the dive trip to be edited in the dialog.
     * @param trip
     */
    public void setDive(DiveTrip trip) 
    {
        this.trip = trip;
        
        tripDateLabel.setText(trip.getTripDate().format(tripDate));
        departTimeLabel.setText(trip.getDepartTime().format(tripTime)); 
        String status = trip.getTripStatus();
       
        if(status.equalsIgnoreCase("OK"))
        {
            okRadio.setSelected(true);
        }
        else if(status.equalsIgnoreCase("Cancelled"))
        {
            cancelledRadio.setSelected(true);
        } 
    }
    
    /**
     * Returns true if the user clicked OK, false otherwise.
     * @return 
     */
    public  boolean isSaveClicked() 
    {
        return saveClicked;
    }

    /**
     * Called when the user clicks save. Saves the changes after the confirmation is made.
     */
    @FXML
    private void handleSave() 
    {
        boolean confirm = false;
        if (isInputValid()) 
        {
            if(cancelledRadio.isSelected() && !trip.getTripStatus().equalsIgnoreCase("Cancelled"))
            {
                confirm = AlertUtil.confirmCancelAlert();
                isCancelled = true;
            }
            else
            {
                confirm = AlertUtil.confirmChangesAlert();
                isCancelled = false;
            }
            // Confirms the save changes before putting them into effect.
            if(confirm)
            {
               if(okRadio.isSelected())
               {
                   trip.setTripStatus("OK");
               }
               else if(cancelledRadio.isSelected())
               {
       
                    trip.setTripStatus("CANCELLED");
                    Runnable emailTask = () -> 
                    { 
                        try 
                        {
                           sendCancellationEmails();

                           Platform.runLater(()-> AlertUtil.showEmailSent("Cancellation emails for the cancelled dive trip sent to all reserved customers."));

                        }
                        catch (IOException | IllegalStateException | SQLException e)
                        {
                            Platform.runLater(()-> AlertUtil.showErrorAlert("Email Error!", e));
                            Thread.currentThread().interrupt();

                        }
                    };

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(emailTask);
                    executor.shutdown();
         
                }
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
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() 
    {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() 
    {
        String errorMessage = "";

        if (!cancelledRadio.isSelected() && !okRadio.isSelected())
        {
            errorMessage += "No radio button selected for trip status!\n"; 
        }

        if (errorMessage.length() == 0) 
        {
            return true;
        } 
        else
        {
            AlertUtil.invalidInputAlert(errorMessage);
            return false;            
        }    
    }
    
    // Returns whether the trip was cancelled or not
    public static boolean isCancelled()
    {
        return isCancelled;
    }
    
    // Sends cancellation emails for all the reserved customers
    public void sendCancellationEmails() throws SQLException, IOException
    {
        Customer customer = new Customer();
        Reservation reservation = new Reservation();
        
        Statement statement = null;
        ResultSet results = null;
        Statement st = null;
        ResultSet result = null;
        try
        {
            connection = DbConnection.accessDbConnection().getConnection();
            statement = connection.createStatement();
            String query = "SELECT RESERVATION_ID, CUST_ID FROM RESERVATION WHERE TRIP_ID=" + trip.getTripId();
            results = statement.executeQuery(query);

            while(results.next())
            {
                int resId = results.getInt(1);
                int custId = results.getInt(2);
                st = connection.createStatement();
                String custQuery = "SELECT FIRST_NAME, LAST_NAME, EMAIL FROM CUSTOMER WHERE CUST_ID=" + custId;
                result = st.executeQuery(custQuery);

                result.next();

                customer.setFirstName(result.getString(1));           
                customer.setLastName(result.getString(2));
                customer.setEmailAddress(result.getString(3));

                reservation.setReservationId(resId);
                reservation.setCustomerId(custId);
                reservation.setCustomer(customer);
                reservation.setDriveTrip(trip);

                EmailAlert.sendCancellationEmail(reservation);
            }
        }
        catch (SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with Database", e);
        }
        finally
        {
            try
            {
               if (statement != null)
                   statement.close();

               if(results != null)
                   results.close();

               if(result != null)
                   result.close();

               if(st != null)
                   st.close();

            } 
            catch (SQLException e) 
            {
                 AlertUtil.showDbErrorAlert("Error with Database", e);
            }
        }
    }
}
