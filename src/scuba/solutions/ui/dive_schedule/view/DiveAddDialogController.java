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
        //if (isInputValid()) 
       // {
    		currentTab = "singleDiveTab";
        	trip.setTripDate(tripDatePicker.getValue());
        	trip.setDepartTime(departTimePicker.getTime());
        	
           
            // Confirms the save changes before putting them into effect.
            boolean confirm = AlertUtil.confirmChangesAlert();
            if(confirm)
            {
            	okClicked = true;
            	dialogStage.close();
            }
            else
            {
            	okClicked = false;
            	dialogStage.close();
            }
        //}
    }
    
    /**
     * Called when the user clicks save on the Recurring Dive Trips Tab. 
     * Saves the changes after the confirmation is made.
     */
    
    @FXML
    private void handleRecurringSave()
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
    			DiveTrip trip = new DiveTrip();
    			
    			trip.setDayOfWeek(dayOfWeek);
    			trip.setDepartTime(departTime);
    			trip.setTripDate(date);
    			
    			trips.add(trip);
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
         	dialogStage.close();
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
     * Confirms the Dive Trip addition/s before they are put into effect.
     */

    
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

}
