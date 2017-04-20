
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
 * FXML Controller class
 *
 * @author Jon
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
        String status = trip.getWeatherStatus();
       
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
          if(cancelledRadio.isSelected() && !trip.getWeatherStatus().equalsIgnoreCase("Cancelled"))
          {
              confirm = AlertUtil.confirmCancelAlert();
              isCancelled = true;
          }
          else
          {
               confirm = AlertUtil.confirmChangesAlert();
          }
            // Confirms the save changes before putting them into effect.
            if(confirm)
            {
               if(okRadio.isSelected())
               {
                   trip.setWeatherStatus("OK");
               }
               else if(cancelledRadio.isSelected())
               {
                   
                //ExecutorService executor = Executors.newSingleThreadExecutor();
                
                //executor.
                 //   trip.setWeatherStatus("CANCELLED");
                    Executors.newSingleThreadExecutor().execute(() -> 
                    {
                        try 
                        {
                            sendCancellationEmails();
                        } 
                        catch (SQLException | IOException e)
                        {

                        }
                    });
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
    
    public static boolean isCancelled()
    {
        return isCancelled;
    }
    
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
