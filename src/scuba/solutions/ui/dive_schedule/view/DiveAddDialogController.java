package scuba.solutions.ui.dive_schedule.view;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.DateUtil;

/**
 * Controller class for adding Dive Trips. Contains two tabs - allowing the user to
 * add a single Diven Trip or recurring Dive Trips.
 *
 * @author Jonathan Balliet
 */
public class DiveAddDialogController implements Initializable {
	
    private final static String[] DAYS_OF_WEEK = {"Monday","Tuesday","Wednesday", "Thursday",
                                                                             "Friday", "Saturday", "Sunday"};

    private ObservableList<String> daysOfWeekData;

    private Stage dialogStage;
	
    private DiveTrip trip;
    
    private boolean okClicked = false;
    
    private static LinkedList<DiveTrip> trips = new LinkedList<>();
    
    private static String currentTab;

    @FXML
    private JFXTabPane tabPane;

    @FXML
    private Tab singleDiveTab;

    @FXML
    private JFXButton saveButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private JFXDatePicker tripDatePicker;

    @FXML
    private JFXDatePicker departTimePicker;

    @FXML
    private Tab recurringDiveTab;

    @FXML
    private JFXDatePicker recurringStartDatePicker;

    @FXML
    private JFXDatePicker recurringEndDatePicker;

    @FXML
    private JFXComboBox<String> dayOfWeekCombo;

    @FXML
    private JFXDatePicker recurringDepartTimePicker;

    @FXML
    private JFXButton recurringSaveButton;

    @FXML
    private JFXButton recurringCancelButton;

	
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    	daysOfWeekData = FXCollections.observableArrayList();
    	daysOfWeekData.addAll(DAYS_OF_WEEK);
        dayOfWeekCombo.setItems(daysOfWeekData);
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
     * Returns true if the user clicked OK, false otherwise.
     */
    public boolean isOkClicked() 
    {
        return okClicked;
    }
    
    /**
     * Sets the Dive Trip to be added.
     * @param trip
     */
    public void setDive(DiveTrip trip) 
    {
        this.trip = trip;
        tripDatePicker.setValue(trip.getTripDate());
        departTimePicker.setTime(trip.getDepartTime());    
    }
    

    /**
     * Called when the user clicks save on the Single Dive Trip Tab. 
     * Saves the changes after the confirmation is made.
     */
    @FXML
    private void handleSingleSave(){
        if (isSingleDiveInputValid()) 
        {
            currentTab = "singleDiveTab";
            trip.setTripDate(tripDatePicker.getValue());
            trip.setDepartTime(departTimePicker.getTime());
            trip.setDayOfWeek(trip.determineDayOfWeek());

            boolean confirm = AlertUtil.confirmChangesAlert();
            if(confirm)
            {
            	okClicked = true;
            	dialogStage.close();
            }
            else
            {
            	okClicked = false;
            
            }
        }
    }
    
    /**
     * Called when the user clicks save on the Recurring Dive Trips Tab. 
     * Saves the changes after the confirmation is made.
     */
    @FXML
    private void handleRecurringSave()
    {
        if (isRecurringDiveInputValid()) 
        {
            currentTab = "recurringDiveTab";
            trips.clear();

            LocalDate startDate = recurringStartDatePicker.getValue();
            LocalDate endDate = recurringEndDatePicker.getValue();
            LocalTime departTime = recurringDepartTimePicker.getTime();
            String dayOfWeek = dayOfWeekCombo.getValue();

            LocalDate date = startDate;

            while(date.compareTo(endDate) <= 0)
            {

                if(date.getDayOfWeek().toString().equalsIgnoreCase(dayOfWeek))
                {
                    DiveTrip recurTrip = new DiveTrip();

                    recurTrip.setDayOfWeek(dayOfWeek);
                    recurTrip.setDepartTime(departTime);
                    recurTrip.setTripDate(date);

                    trips.add(recurTrip);
                }

                date = date.plusDays(1);
            }

             boolean confirm =  AlertUtil.confirmChangesAlert();

             if(confirm)
             {
                okClicked = true;
                dialogStage.close();
             }
             else
             {
                okClicked = false;
              //  dialogStage.close();
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
     * Gets the current Tab of the user.
     */
    public static String getTab()
    {
    	return currentTab;
    }
    
    /**
     * Gets the list of recurring dive trips.
     */
    public static LinkedList<DiveTrip> getRecurringTrips()
    {
    	return trips;
    }
    
    private boolean isSingleDiveInputValid()
    {
        String errorMessage = "";

        if (tripDatePicker.getValue() == null || DateUtil.validDate(tripDatePicker.getValue().toString()))
        {
            errorMessage += "Dive trip date is not valid. Please select a date value from the date picker.\n"; 
        }
        else if(!isValidDate()) 
        {
            errorMessage += "Dive trip date is not valid. Please select a current or future date.\n";
        } 
        if (departTimePicker.getTime() == null ) 
        {
            errorMessage += "Depart time is not valid. Please select a depart time from the time picker.\n"; 
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
    
    
    private boolean isRecurringDiveInputValid()
    {
        LocalDate startDate = recurringStartDatePicker.getValue();
    	LocalDate endDate = recurringEndDatePicker.getValue();
    	LocalTime departTime = recurringDepartTimePicker.getTime();
    	String dayOfWeek = dayOfWeekCombo.getValue();
        String errorMessage = "";

        if (recurringStartDatePicker.getValue() == null || recurringEndDatePicker.getValue() == null 
                || DateUtil.validDate(recurringStartDatePicker.getValue().toString()) || DateUtil.validDate(recurringEndDatePicker.getValue().toString()) )
        {
            errorMessage += "Dive trip date/s are not valid. Please select dates value from the date pickers.\n"; 
        }
        else if(recurringStartDatePicker.getValue().compareTo(LocalDate.now()) < 0 || recurringEndDatePicker.getValue().compareTo(LocalDate.now()) < 0)
        {
            errorMessage += "Dive trip date/s are not valid. Please select current or future dates.\n";
        }
        else if(!isEndAfterStart())
        {
            errorMessage += "Dive trip date/s are not valid. Please select an ending date that comes after the start date.\n ";
        }
        else if(!isValidStartToEndDates())
        {
            errorMessage += "Start and end dates are not valid. Please select atleast a week between the start date and end date for recurring trips.\n";
        }
        if (recurringDepartTimePicker.getTime() == null) 
        {
            errorMessage += "Depart time is not valid. Please select a depart time from the time picker.\n"; 
        }
        if(dayOfWeekCombo.getValue() == null)
        {
            errorMessage += "Day of week is not valid. Please select a day of week from the drop-down menu.\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            AlertUtil.invalidInputAlert(errorMessage);
            return false;            
        }    
        
    }
    
    
    private boolean isValidDate()
    {
    	LocalDate date = tripDatePicker.getValue();
    	
    	return date.compareTo(LocalDate.now()) >= 0;
    }
    
    private boolean isValidStartToEndDates()
    {
        LocalDate startDate = recurringStartDatePicker.getValue();
        LocalDate endDate = recurringEndDatePicker.getValue();
        
        

        if((endDate.minusDays(7)).compareTo(startDate) < 0)
        {
            return false;
        }
        
        return true;
    }
    
    private boolean isEndAfterStart()
    {
        LocalDate startDate = recurringStartDatePicker.getValue();
        LocalDate endDate = recurringEndDatePicker.getValue();
        
        if(startDate.isAfter(endDate))
        {
            return false;
        }
        
        return true;
    }

}
