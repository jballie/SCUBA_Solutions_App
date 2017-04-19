/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.concurrent.Executors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
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

    private boolean okClicked = false;
    
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
     * Sets the customer to be edited in the dialog.
     * @param trip
     */
    public void setDive(DiveTrip trip) 
    {
        this.trip = trip;
        // sets all the text fields to this customer's attributes.
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
    public  boolean isOkClicked() 
    {
        return okClicked;
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
             //  trip.setTripDate(tripDatePicker.getValue());
             //  trip.setDepartTime(departTimePicker.getTime());
               
               
               if(okRadio.isSelected())
               {
                   trip.setWeatherStatus("OK");
               }
               else if(cancelledRadio.isSelected())
               {
                  trip.setWeatherStatus("CANCELLED");
                   Executors.newSingleThreadExecutor().execute(() -> {
                    try {
                    sendCancellationEmails();
                  } catch (SQLException e)
                  {
                      System.out.println(e);
                  } catch (IOException e)
                  {
                      System.out.println(e);
                  }});
               }
            	okClicked = true;
            	dialogStage.close();
            }
            else
            {
            	okClicked = false;
            	//dialogStage.close();
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

       
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
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
        connection = DbConnection.accessDbConnection().getConnection();
        Statement statement = connection.createStatement();
        String query = "SELECT RESERVATION_ID, CUST_ID FROM RESERVATION WHERE TRIP_ID=" + trip.getTripId();
        ResultSet results = statement.executeQuery(query);
        
        while(results.next())
        {
            int resId = results.getInt(1);
            int custId = results.getInt(2);
            Statement st = connection.createStatement();
            String custQuery = "SELECT FIRST_NAME, LAST_NAME, EMAIL FROM CUSTOMER WHERE CUST_ID=" + custId;
            ResultSet result = st.executeQuery(custQuery);
            
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
    
    
}
