
package scuba.solutions.ui.dive_schedule.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.emails.EmailAlert;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.ui.reservations.model.Waiver;
import scuba.solutions.ui.reservations.model.Payment;
import scuba.solutions.ui.reservations.model.Reservation;
import scuba.solutions.ui.reservations.view.ReservationAddDialog_ExistingCustomerController;
import scuba.solutions.ui.reservations.view.ReservationAddDialog_NewCustomerController;
import scuba.solutions.ui.reservations.view.ReservationAddDialog_SearchCustomerController;
import scuba.solutions.ui.reservations.view.ReservationEditDialogController;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.DateUtil;
import scuba.solutions.util.SQLUtil;


/**
 * Controller class for the Dive Schedule Interface.
 * @author Jonathan Balliet, Samuel Brock
 */
public class DiveSchedulePaneController implements Initializable 
{
    private static Connection connection;    
    private final ObservableList<Reservation>  reservationData = FXCollections.observableArrayList();
    private final ObservableList<DiveTrip>  tripData = FXCollections.observableArrayList();    
    private FilteredList<DiveTrip> filteredTripData; 
    private Stage primaryStage;
    private Stage dialogStage;
    private String currentTab;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXButton homeButton;
    @FXML
    private JFXButton diveScheduleButton;
    @FXML
    private JFXButton customersButton;
    @FXML
    private JFXButton exitButton;
    @FXML
    private JFXTextField searchTextField;
    @FXML
    private JFXButton clearSearchButton;
    @FXML
    private TableView<Reservation> reservationsTable;
    @FXML
    private TableView<DiveTrip> tripTable;
    @FXML
    private TableColumn<DiveTrip, String> tripDateColumn;
    @FXML
    private TableColumn<DiveTrip, String> dayOfWeekColumn;
    @FXML
    private TableColumn<DiveTrip, LocalTime> departTimeColumn;
    @FXML
    private TableColumn<DiveTrip, String> availSeatsColumn;
    @FXML
    private TableColumn<DiveTrip, String> tripStatusColumn;
    @FXML
    private TableColumn<Reservation, String> fullNameColumn;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label waiverLabel;
    @FXML
    private Label paymentLabel;
    @FXML
    private JFXButton newReservationButton;
    @FXML
    private JFXButton newDiveButton;
    @FXML
    private JFXButton updateDiveButton;
    @FXML
    private JFXButton viewWeatherButton;
    @FXML
    private TableColumn<Reservation, String> statusColumn;
    @FXML
    private JFXButton updateReservationButton;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        primaryStage = DiveSchedulePane.getPrimaryStage();
        diveScheduleButton.setDisable(true);
        
        initializeTrips();
        initializeReservations();
        
        try
        {
            loadDiveTrips();
        }
        catch (FileNotFoundException e)
        {
            AlertUtil.showErrorAlert("File not Found!", e);
        }
        catch (IOException e)
        {
            AlertUtil.showErrorAlert("Error!", e);
        }
        catch (SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with loading dive trips to the table", e);
        }
        
   
        initializeSearchField();

        tripTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> loadTripReservations(newValue));
        
        reservationsTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showReservationStatusDetails(newValue));
        
        
        tripTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends DiveTrip> observable, DiveTrip oldValue, DiveTrip newValue) -> 
        {
            if(newValue == null || newValue.getTripStatus().equalsIgnoreCase("Cancelled"))
            {
                newReservationButton.setDisable(true);
                updateReservationButton.setDisable(true);
            }
            else
            {
                newReservationButton.setDisable(false);
                updateReservationButton.setDisable(false);
            }

            if( newValue == null || (newValue.getAvailSeats() == 0 || newValue.getTripStatus().equalsIgnoreCase("Cancelled")) )
            {
                newReservationButton.setDisable(true);
                //updateReservationButton.setDisable(true);
            }
            else
            {
                newReservationButton.setDisable(false);
            }
        });  
    }
    
    // Loads all of the dive trips in the DB to the Dive Trips Table
    // Trips are set in descending order in the table by default - so the most recent trips are at the top.
    public void loadDiveTrips() throws FileNotFoundException, IOException, SQLException
    {
    	tripData.clear();
    	
    	connection = DbConnection.accessDbConnection().getConnection();
		
    	String query = "SELECT * FROM dive_trip";
		
    	Statement statement = null;
    	ResultSet result = null;
    
    	try
    	{
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            
            while(result.next())
            {
                int tripID = result.getInt(1);
                DiveTrip trip = new DiveTrip(tripID);
                
                trip.setTripDate((result.getDate(2)).toLocalDate());
                trip.setAvailSeats(result.getInt(3));
                String strTime = result.getString(4);
                LocalTime time = SQLUtil.intervalToLocalTime(strTime);
                
                trip.setDepartTime(time);
                trip.setTripStatus(result.getString(5));
                trip.setDayOfWeek(trip.determineDayOfWeek());
				
                tripData.add(trip);
                
            }
    	}
    	catch (SQLException e)
    	{
            AlertUtil.showDbErrorAlert("Error with selecting and adding Dive Trips", e);
    	}
        
    	finally
    	{
            try
            {
            	if (statement != null)
            	{
                    statement.close();
            	}
                if (result != null)
                {
                    result.close();
                }
            }
            catch (SQLException e)
            {
            	AlertUtil.showDbErrorAlert("Error with DB Connection", e);
            }
            
    	}
        // sorts the trips in descending (reverse) order
        Collections.sort(tripData, Collections.reverseOrder());
        
        tripTable.setItems(tripData);
        
        initializeSearchField();
    }
    
    // Loads all of the reservations for selected dive trip into the Reservations table.    
    private void loadTripReservations(DiveTrip selectedTrip)
    { 
        int tripId = 0;
        
        if(!reservationData.isEmpty())
        {
            reservationData.clear();
        }

        if (selectedTrip != null) 
        {
            tripId = selectedTrip.getTripId();

            PreparedStatement preSt = null;           
            ResultSet result = null;          
            PreparedStatement statement = null;
            ResultSet resultSet = null; 

            try
            {
                preSt = connection.prepareStatement("SELECT * FROM RESERVATION WHERE TRIP_ID=?");

                preSt.setInt(1, tripId);

                result = preSt.executeQuery();

                while(result.next())
                {
                    int restID = result.getInt(1);
                    int custID = result.getInt(2);
                    int diveID = result.getInt(3);
                    String status = result.getString(4);

                    statement = connection.prepareStatement("SELECT FIRST_NAME, "
                            + "LAST_NAME FROM CUSTOMER WHERE CUST_ID=?");

                    statement.setInt(1, custID);

                    resultSet = statement.executeQuery();
                    resultSet.next();

                    String firstName = resultSet.getString(1);
                    String lastName = resultSet.getString(2);

                    LocalDate diveDate = null;

                    statement = connection.prepareStatement("SELECT TRIP_DATE, DEPARTURE_TIME, "
                            + "DIVE_STATUS FROM DIVE_TRIP WHERE TRIP_ID=?");

                    statement.setInt(1, diveID);

                    resultSet = statement.executeQuery();
                    resultSet.next();

                    if(resultSet.getDate(1) != null)
                        diveDate = resultSet.getDate(1).toLocalDate();

                    String strTime = resultSet.getString(2);
                    LocalTime time = SQLUtil.intervalToLocalTime(strTime);
                    String tripStatus = resultSet.getString(3);

                    Customer customer = new Customer(custID, firstName, lastName);
                    DiveTrip diveTrip = new DiveTrip(diveID, diveDate, time);
                    diveTrip.setTripStatus(tripStatus);
                    Reservation res = new Reservation(restID);
                    
                    res.setCustomer(customer);
                    res.setDiveTripId(diveID);
                    res.setDriveTrip(diveTrip);
                    res.setStatus(status);

                    reservationData.add(res);
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

                    if(preSt != null)
                        preSt.close();

                    if(resultSet != null)
                        resultSet.close();

                    if(result != null)
                        result.close();

                   } 
                catch (SQLException e) 
                {
                    AlertUtil.showDbErrorAlert("Error with Database", e);
                }
            }

            reservationsTable.setItems(reservationData);   
        }
    }

    
    // Retrieves the details of the selected Reservation to display
    // in the Reservation details sub-pane.
    private void showReservationStatusDetails(Reservation reservation)
    {
        int reservationId = 0;
        Waiver waiver = null;
        Payment payment = null;

        if(reservation != null) 
        {
            reservationId = reservation.getReservationId();        
            waiver = reservation.getWaiver();
            payment = reservation.getPayment();
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null; 
        PreparedStatement st = null;
        ResultSet results = null;

        try 
        {
            statement = connection.prepareStatement("SELECT * FROM WAIVER WHERE RESERVATION_ID=?");            
            statement.setInt(1, reservationId);            
            resultSet = statement.executeQuery();

            if(resultSet.next()) 
            {
                waiver.setWaiverStatus(resultSet.getString(2));

                if(resultSet.getDate(3) != null)
                    waiver.setDateSigned(resultSet.getDate(3).toLocalDate());

                waiver.setERFirst(resultSet.getString(4));
                waiver.setERLast(resultSet.getString(5));
                waiver.setERPhone(resultSet.getString(6));
            }

            st = connection.prepareStatement("SELECT * FROM PAYMENT WHERE RESERVATION_ID=?");            
            st.setInt(1, reservationId);            
            results = st.executeQuery();            

            if(results.next()) 
            {            
                payment.setReservationId(reservationId);
                payment.setPaymentStatus(results.getString(2));
                payment.setCCConfirmNo(results.getInt(3));   // getLong

                if(results.getDate(4) != null)
                    payment.setDateProcessed(results.getDate(4).toLocalDate());

                payment.setAmount(results.getInt(5));
            }

        } 
        catch (SQLException | NullPointerException e)
        {
            AlertUtil.showErrorAlert("Error with Database", e);
        }
        finally
        {
            try
            {
                if (statement != null)
                    statement.close();

                if(resultSet != null)
                    resultSet.close();
                
                if(st != null)
                    st.close();
                
                if(results != null)
                    results.close();
            }
            catch (SQLException | NullPointerException e)
            {
                AlertUtil.showErrorAlert("Error with Database", e);        
            }
        }

        if (reservation != null) 	
        {
            firstNameLabel.setText(reservation.getCustomer().getFirstName());
            lastNameLabel.setText(reservation.getCustomer().getLastName());
            waiverLabel.setText(waiver.getWaiverStatus());
            paymentLabel.setText(payment.getPaymentStatus());
        } 
        else 
        {
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            waiverLabel.setText("");
            paymentLabel.setText("");
        }
    }
    
    // Initializes the trip columns in the Dive Trips table.
    public void initializeTrips()
    {
        tripDateColumn.setCellValueFactory(cellData -> cellData.getValue().tripDateProperty());
        dayOfWeekColumn.setCellValueFactory(cellData -> cellData.getValue().dayOfWeekProperty());
        departTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departTimeProperty());
        availSeatsColumn.setCellValueFactory(cellData -> cellData.getValue().availSeatsProperty());
        tripStatusColumn.setCellValueFactory(cellData -> cellData.getValue().tripStatusProperty()); 
        tripDateColumn.setComparator(new DateComparator());
    }
    
    // Compares the stored string values by their actual date values
    public class DateComparator implements Comparator<String>
    {
        @Override
        public int compare(String str1, String str2)
        {
            LocalDate dateStr1 = DateUtil.parse(str1);
            LocalDate dateStr2 = DateUtil.parse(str2);
            
            return dateStr1.compareTo(dateStr2);
        }
    }
   
    // Initiazlies the reservation columns in the Reservations table.
   public void initializeReservations()
   {
       fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().fullNameProperty());
       statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
   }
   
   // Displays the the Update Dive dialog.
   public boolean showDiveEditDialog(DiveTrip trip) 
   {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DiveSchedulePaneController.class.getResource("DiveEditDialog.fxml"));
            Parent root = loader.load();

            dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            DiveEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDive(trip);

            dialogStage.showAndWait();
            return controller.isSaveClicked(); 
        }
        catch (IOException e) 
    	{
    	    return false;
    	}	
   }
   
   // Handles updating the DiveTrip when the Update Dive button is clicked. 
   @FXML
   public void updateTrip() throws FileNotFoundException, IOException, SQLException
   {
        updateDiveButton.setDisable(true);
        DiveTrip selectedTrip = (DiveTrip) tripTable.getSelectionModel().getSelectedItem();
        TableViewSelectionModel<DiveTrip> selection = tripTable.getSelectionModel();
   
        if (selectedTrip != null)
        {
            boolean saveClicked = false;
    	    saveClicked = showDiveEditDialog(selectedTrip);
            
            if (saveClicked) 
            {
                int trId = selectedTrip.getTripId();

                LocalDate tripDate = selectedTrip.getTripDate();
                int availSeat = selectedTrip.getAvailSeats();
                LocalTime departTime = selectedTrip.getDepartTime();
                String time = SQLUtil.localTimeToInterval(departTime);
                String tripStatus = selectedTrip.getTripStatus();
               

                PreparedStatement preSt = null;

                try 
                {
                    preSt = connection.prepareStatement("UPDATE DIVE_TRIP SET trip_date=?," +
                                    "available_seats=?, departure_time=?, dive_status=?" +
                                    "WHERE TRIP_ID=?");

                    preSt.setDate(1, Date.valueOf(tripDate));
                    preSt.setInt(2, availSeat);
                    preSt.setString(3, time);
                    preSt.setString(4, tripStatus);
                    preSt.setInt(5, trId);

                    if (preSt.executeUpdate() >= 0)  
                    {   
                        boolean isCancelled = DiveEditDialogController.isCancelled();
                        
                        if(isCancelled)
                        {
                            AlertUtil.showDbSavedAlert("Dive Trip has successfully "
                           + "been cancelled in the database.");
                        }
                        else
                        {
                            AlertUtil.showDbSavedAlert("Dive Trip has successfully "
                           + "been updated in the database.");
                        }
                    }  
                } 
                catch (SQLException e) 
                {
                    AlertUtil.showDbErrorAlert("Error occured with the update "
                            + "of the dive trip", e);
                }
                finally
                {
                    updateDiveButton.setDisable(false);
                
        
                    try
                    {
                        if (preSt != null)
                            preSt.close();

                    }
                    catch (SQLException | NullPointerException e)
                    {
                        AlertUtil.showErrorAlert("Error with Database", e);        
                    }
                }
                
                tripTable.setSelectionModel(selection);
            }
        } 
        else 
        {
            // If nothing is selected a warning message will pop-up
            AlertUtil.noSelectionAlert("No Dive Trip Selected", "Please select a dive trip in the dive trips table to update.");
        }
        updateDiveButton.setDisable(false);
        searchTextField.setFocusTraversable(false);
   }
   
   // Displays the New Dive dialog.
   public boolean showDiveAddDialog(DiveTrip trip) 
   {
        try
        {	
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DiveSchedulePaneController.class.getResource("DiveAddDialog.fxml"));
            Parent root = loader.load();

            dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            DiveAddDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDive(trip);

            dialogStage.showAndWait();
            return controller.isSaveClicked();
        }
	catch (IOException e) 
	{
            return false;
	}
   }
   
   // Handles adding a single or recurring dive trips when the New Dive button is clicked.
   @FXML
   public void addTrip() throws FileNotFoundException, IOException, SQLException
   {
       newDiveButton.setDisable(true);
       DiveTrip trip = new DiveTrip();
       
       boolean saveClicked = showDiveAddDialog(trip);
       currentTab = DiveAddDialogController.getTab();
       
       PreparedStatement preSt = null;
       try 
       {
            if (saveClicked && currentTab.equalsIgnoreCase("singleDiveTab"))
            {
                LocalDate tripDate = trip.getTripDate();
                LocalTime departTime = trip.getDepartTime();
                String time = SQLUtil.localTimeToInterval(departTime);
         
                preSt = connection.prepareStatement("INSERT INTO DIVE_TRIP "
                + "(trip_date, departure_time)"
                + " values(?, ?)");

                preSt.setDate(1, Date.valueOf(tripDate));
                preSt.setString(2, time);

                if (preSt.executeUpdate() >= 0)  
                {
                    AlertUtil.showDbSavedAlert("Dive Trip has successfully been "
                            + "added to the database.");
                    loadDiveTrips();
                          
                    
                }
            }
        }
        catch (SQLException e) 
        {
                AlertUtil.showDbErrorAlert("Error occured with the addition "
                        + "of the dive trip.", e);
        }

        finally
        {
            newDiveButton.setDisable(false);
        }
            try
            {       
                if (preSt != null)
                    preSt.close();

            }
            catch (SQLException | NullPointerException e)
            {
               AlertUtil.showErrorAlert("Error with Database", e);        
            }
          
       try
       {
            if (saveClicked && currentTab.equalsIgnoreCase("recurringDiveTab"))
            {
                LinkedList<DiveTrip> trips  = DiveAddDialogController.getRecurringTrips();

                preSt = connection.prepareStatement("INSERT INTO DIVE_TRIP "
                                 + "(trip_date, departure_time)"
                                 + " values(?, ?)");

                for(DiveTrip diveTrip: trips)
                {
                    LocalDate tripDate = diveTrip.getTripDate();
                    LocalTime departTime = diveTrip.getDepartTime();
                    String time = SQLUtil.localTimeToInterval(departTime);

                    preSt.setDate(1, Date.valueOf(tripDate));
                    preSt.setString(2, time);

                    preSt.addBatch(); 
                }

                preSt.executeBatch();
                AlertUtil.showDbSavedAlert("Recurring Dive Trips have successfully been added to the database.");
                loadDiveTrips();
                
            }
        }        
        catch (SQLException e) 
        {
            AlertUtil.showDbErrorAlert("Error occured with the additon of the recurring dive trips.", e);
        }
        finally
        {
            newDiveButton.setDisable(false);
            try
            {       
                if (preSt != null)
                    preSt.close();

            }
            catch (SQLException | NullPointerException e)
            {
                AlertUtil.showErrorAlert("Error with Database", e);        
            }
        }
   }
   
   // Displays the Update Reservation dialog.
    public boolean showReservationEditDialog(Reservation reservation)
    {
       try
    	{
    	   FXMLLoader loader = new FXMLLoader();
    	   loader.setLocation(DiveSchedulePaneController.class.getResource("/scuba/solutions/ui/reservations/view/ReservationEditDialog.fxml"));
    	   Parent root = loader.load();

    	   dialogStage = new Stage();
    	   dialogStage.initModality(Modality.WINDOW_MODAL);
    	   dialogStage.initOwner(primaryStage);
    	   Scene scene = new Scene(root);
    	   dialogStage.setScene(scene);

    	   ReservationEditDialogController controller = loader.getController();
    	   controller.setDialogStage(dialogStage);
    	   controller.setWaiver(reservation.getWaiver());
           controller.setPayment(reservation.getPayment());

    	   dialogStage.showAndWait();

    	   return controller.isSaveClicked();
    	   
        }
       catch (IOException e) 
       {
    	   return false;
       }
    }
   
     /**
    * Handles updates for a customer reservation in WAIVER and PAYMENT tables based on
    * user entry when the Update Reservation is clicked.  
    * Calls bookReservation() method
    */
    @FXML
    public void updateReservation() throws FileNotFoundException, IOException, SQLException 
    {
        updateReservationButton.setDisable(true);
        Reservation selectedReservation = (Reservation) reservationsTable.getSelectionModel().getSelectedItem();
        Waiver waiver = null;
        Payment payment = null;
        DiveTrip selectedTrip = (DiveTrip) tripTable.getSelectionModel().getSelectedItem();
        
        
        if (selectedReservation != null && (selectedTrip.getAvailSeats() > 0 || 
                selectedReservation.getStatus().equalsIgnoreCase("Booked") ))
        {
            
            
            if(selectedReservation.getWaiver() != null)           
                waiver = selectedReservation.getWaiver();
         
            if(selectedReservation.getPayment() != null)
                payment = selectedReservation.getPayment();
             
            boolean saveClicked = showReservationEditDialog(selectedReservation);
        
            PreparedStatement preSt1 = null;
            PreparedStatement preSt2 = null;
            
            try
            {
                if (saveClicked) 
                {            	 
                    int reservationId = selectedReservation.getReservationId();
                    int preSt1Result = 0;
                    int preSt2Result = 0;

                    if(waiver != null &&  waiver.isComplete()) 
                    {
                        LocalDate dateSigned = waiver.getDateSigned();
                        String erLast = waiver.getERLast();
                        String erFirst = waiver.getERFirst();
                        String erPhone = waiver.getERPhone();

                        preSt1 = connection.prepareStatement("UPDATE WAIVER SET date_signed=?, "
                                + "er_first=?, er_last=?, er_phone=? WHERE reservation_id=?");

                        preSt1.setDate(1, Date.valueOf(dateSigned));
                        preSt1.setString(2, erFirst);
                        preSt1.setString(3, erLast);
                        preSt1.setString(4, erPhone);
                        preSt1.setInt(5, reservationId);

                        preSt1Result = preSt1.executeUpdate();

                    }

                    if(payment != null && payment.isComplete()) 
                    {
                        long ccConfirmNo = payment.getCCConfirmNo();
                        LocalDate dateProc = payment.getDateProcessed();
                        int amount = payment.getAmount();

                        preSt2 = connection.prepareStatement("UPDATE PAYMENT SET cc_confirm_no=?,"
                                + " date_processed=?, amount=? WHERE reservation_id=?");

                        preSt2.setLong(1, ccConfirmNo);
                        preSt2.setDate(2, Date.valueOf(dateProc));
                        preSt2.setInt(3, amount);
                        preSt2.setInt(4, reservationId);

                        preSt2Result = preSt2.executeUpdate();
                    }

                    if (preSt1Result ==  1 || preSt2Result == 1) 
                    {
                        AlertUtil.showDbSavedAlert("Reservation has successfully been updated in the database.");
                    } 
                    else 
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("Error occured with the update of reservation.");
                        alert.showAndWait();
                    }

                    bookReservation(selectedReservation);
                    
                    showReservationStatusDetails(selectedReservation);
                }
            }
            catch (SQLException e)
            {
                AlertUtil.showDbErrorAlert("Error occured with the update of"
                        + "the reservation.", e);
            }
            finally
            {
                try
                {       
                    if (preSt1 != null)
                        preSt1.close();
                    if(preSt2 != null)
                        preSt2.close();
                }
                catch (SQLException | NullPointerException e)
                {
                   AlertUtil.showErrorAlert("Error with Database", e);        
                }
            }
        } 
        else 
        {
            if(selectedReservation == null)
            {
                AlertUtil.noSelectionAlert("No Reservation Selected", "Please select "
                     + "a reservation in the reservations table to update.");
            }
            else
            {
                AlertUtil.showMaxCapacity();
            }
            
        }
        
        
        updateReservationButton.setDisable(false);
    }
    
     /**
     * Updates RESERVATION, WAIVER, and PAYMENT tables to indicate completeness
     * of data entries. Once complete, the Reservation's status is changed to booked
     * and the confirmation email is sent to the customer.
     */
    public void bookReservation(Reservation selectedReservation) throws FileNotFoundException, IOException, SQLException 
    {
        int reservationId = selectedReservation.getReservationId();
        Waiver waiver = selectedReservation.getWaiver();
        Payment payment = selectedReservation.getPayment();
        
        String update = null;
        Statement statement1 = null;
        Statement statement2 = null;
        Statement statement3 = null;
        Statement statement4 = null;
        Statement statement5 = null;
        PreparedStatement preSt = null;
        ResultSet resultSet = null;
        try
        {
            statement1 = connection.createStatement();

            if(waiver.isComplete()) 
            {
                statement1.executeUpdate("UPDATE WAIVER SET waiver_status='COMPLETE' WHERE reservation_id=" + reservationId);
            } 
            else 
            {
               statement1.executeUpdate("UPDATE WAIVER SET waiver_status='INCOMPLETE' WHERE reservation_id=" + reservationId);
            }
            
            statement2 = connection.createStatement();
            if(payment.isComplete()) 
            {
                statement2.executeUpdate("UPDATE PAYMENT SET payment_status='PAID' WHERE reservation_id=" +  + reservationId);
            } 
            else 
            {
                statement2.executeUpdate("UPDATE PAYMENT SET payment_status='UNPAID' WHERE reservation_id=" +  + reservationId);
            }     

            int availSeats;
            int tripId = selectedReservation.getDiveTripId();
            
            statement3 = connection.createStatement();
            
            resultSet = statement3.executeQuery("SELECT available_seats FROM DIVE_TRIP WHERE trip_id=" + tripId);
            resultSet.next();
            
            availSeats = resultSet.getInt(1);

            if(waiver.isComplete() && payment.isComplete()) 
            {
                if (selectedReservation.getStatus() == null || selectedReservation.getStatus().equalsIgnoreCase("Pending"))
                {
                    statement4 = connection.createStatement();
                    update = "UPDATE RESERVATION SET reservation_status='BOOKED' WHERE reservation_id=" + reservationId;            
                    statement4.executeUpdate(update);
                    selectedReservation.setStatus("BOOKED");

                    Runnable emailTask = () -> 
                    { 
                        try 
                        {
                           EmailAlert.sendConfirmationEmail(selectedReservation);

                           Platform.runLater(()->
                           AlertUtil.showEmailSent("Customer has been booked for the dive trip."
                             + " Email confirmation sent to " + selectedReservation.getCustomer().getFullName() + "."));

                        }
                       catch (IOException | IllegalStateException  e)
                       {
                            Platform.runLater(() -> 
                            {
                                try 
                                {
                                    EmailAlert.sendConfirmationEmail(selectedReservation);
                                    Platform.runLater(()-> AlertUtil.showEmailSent("Customer has been booked for the dive trip."
                                     + " Email confirmation sent to " + selectedReservation.getCustomer().getFullName() + "."));
                                }
                                catch (IOException | IllegalStateException  ex)
                                {
                                    Platform.runLater(()-> AlertUtil.showErrorAlert("Email Error!", ex));
                                    Thread.currentThread().interrupt();
                                }
                            });
                        }
                    };
                 
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(emailTask);
                    executor.shutdown();
                   
                    availSeats--;
                }

            } 
            else 
            {
            
                String reservationStatus = selectedReservation.getStatus();
                
                if(reservationStatus == null || reservationStatus.equalsIgnoreCase("Booked")) 
                {
                    statement5 = connection.createStatement();
                    statement5.executeUpdate("UPDATE RESERVATION SET reservation_status='PENDING' WHERE reservation_id=" + reservationId);
                    selectedReservation.setStatus("PENDING");
                    if(reservationStatus != null && reservationStatus.equalsIgnoreCase("Booked"))
                    {
                        availSeats++;
                    }
                    
                }
            }

            preSt = connection.prepareStatement("UPDATE DIVE_TRIP SET available_seats=? WHERE trip_id=?");
            preSt.setInt(1, availSeats);
            preSt.setInt(2, tripId);
            preSt.executeUpdate();

            tripTable.requestFocus();
            DiveTrip trip = tripTable.getSelectionModel().getSelectedItem();
            trip.setAvailSeats(availSeats);
        }
        catch (SQLException e)
        {
            AlertUtil.showErrorAlert("Error with database", e);
        }
        finally
        {
            try
            {
                if (statement1 != null)
                    statement1.close();
                
                if (statement2 != null)
                    statement2.close();
                
                if (statement3 != null)
                    statement3.close();
                
                if (statement4 != null)
                    statement4.close();
                
                if (statement5 != null)
                    statement5.close();
                
                if (preSt != null)
                    preSt.close();
                
                if(resultSet != null)
                   resultSet.close();
            }
            catch (SQLException e)
            {
                AlertUtil.showErrorAlert("Error with database", e);
            }
        }
    }
   
   // Clears the search text bar. All data in the Dive Trips table is shown again.
    @FXML
    private void clearSearch(ActionEvent event) 
    {
        searchTextField.clear();
    }
    
    // Initializes the search bar for the Dive Trips table.
    public void initializeSearchField()
    {
        filteredTripData = new FilteredList<>(tripData, p -> true);

        searchTextField.textProperty().addListener((ObservableValue<? extends String>
                observable, String oldValue, String newValue) -> 
        {
            filteredTripData.setPredicate(trip -> 
            {
                // If the search field text is empty, display all customers in the table
                if (newValue == null || newValue.isEmpty()) 
                {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (DateUtil.format(trip.getTripDate()).contains(lowerCaseFilter))
                {
                    return true; // Search matches date of trip
                } 
                else if (trip.getDepartTime().toString().contains(lowerCaseFilter)) 
                {
                    return true; // Search matches depart time
                } 
                else if (trip.getDayOfWeek().toLowerCase().contains(lowerCaseFilter)) 
                {
                    return true;  // Search matches day of week.
                } 
                else if (trip.getTripStatus().toLowerCase().contains(lowerCaseFilter))
                {
                    return true;
                }

                return false; // Search does not match any data.
            });
        });
        SortedList<DiveTrip> sortedData = new SortedList<>(filteredTripData);

        sortedData.comparatorProperty().bind(tripTable.comparatorProperty());

        tripTable.setItems(sortedData);
    }
    
    // Shows the New Reservation Search Customer dialog.
    public boolean showReservationAddDialog_Search() 
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DiveSchedulePaneController.class.getResource("/scuba/solutions/ui/reservations/view/ReservationAddDialog_SearchCustomer.fxml"));
            Parent root = loader.load();

            dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ReservationAddDialog_SearchCustomerController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            return controller.isProceedClicked(); 
        }
        catch (IOException e) 
    	{
    	    return false;
    	}	
   }
    
    // Shows the New Reservation New Customer dialog. 
    public boolean showReservationAddDialog_NewCustomer(Customer customer) 
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DiveSchedulePaneController.class.getResource("/scuba/solutions/ui/reservations/view/ReservationAddDialog_NewCustomer.fxml"));
            Parent root = loader.load();

            dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ReservationAddDialog_NewCustomerController controller = loader.getController();
            controller.setDialogStage(dialogStage);
    	    controller.setCustomer(customer);

            dialogStage.showAndWait();

            return controller.isSaveClicked(); 
        }
        catch (IOException e) 
    	{
            AlertUtil.showErrorAlert("Error!", e);
    	    return false;
    	}	
   }
    
    // Shows the New Reservation Exisiting Customer dialog.
    public boolean showReservationAddDialog_ExistingCustomer(Customer customer) 
    {
        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DiveSchedulePaneController.class.getResource("/scuba/solutions/ui/reservations/view/ReservationAddDialog_ExistingCustomer.fxml"));
            Parent root = loader.load();

            dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ReservationAddDialog_ExistingCustomerController controller = loader.getController();
            controller.setDialogStage(dialogStage);
    	    controller.setCustomer(customer);

            dialogStage.showAndWait();

            return controller.isSaveClicked(); 
        }
        catch (IOException e) 
    	{
            AlertUtil.showErrorAlert("Error!", e);
    	    return false;
    	}	
    }
    
    // Handles adding a new reservation for a selected dive trip. Once a new reservation
    // has successfully been added the reserved customer will be sent an email for
    // their reservation request with the attached waiver and their reservation status
    // will be set to PENDING.
    @FXML
    private void addReservation(ActionEvent event) throws IOException, SQLException, CloneNotSupportedException 
    {
        DiveTrip selectedTrip = (DiveTrip) tripTable.getSelectionModel().getSelectedItem();
        System.out.println(selectedTrip);
        newReservationButton.setDisable(true);
        if (selectedTrip != null)
        {
            try
            {
                boolean isProceedClicked = showReservationAddDialog_Search();
                boolean isCustomerFound = ReservationAddDialog_SearchCustomerController.returnIsCustomerFound();
                if(isProceedClicked && isCustomerFound)
                {
                    Customer customer = ReservationAddDialog_SearchCustomerController.returnCustomer();
                    Customer selectedCustomer = new Customer(customer);
                   
                    boolean confirmClicked = showReservationAddDialog_ExistingCustomer(selectedCustomer);
                    if(confirmClicked)
                    {
  
                        if(!selectedCustomer.equals(customer))
                        {
                             Customer.updateCustomer(selectedCustomer);
                        }

                        int custId = Customer.getCustId(selectedCustomer);
                        int diveId = selectedTrip.getTripId();
                        
                        boolean alreadyReserved = Reservation.isCustomerAlreadyReserved(custId, diveId);
        
                        if(alreadyReserved)
                        {
                            AlertUtil.showCustomerAlreadyThere();
                        }
                        else 
                        {
                            if(Reservation.addReservation(custId, diveId) > 0)
                            {

                                AlertUtil.showDbSavedAlert("New Reservation for " + selectedCustomer.getFullName() + " succesfully saved.");
                                
                                int reservationId = Reservation.getReservationId(custId, selectedTrip.getTripId());
                                Reservation resev = new Reservation(reservationId);
                                resev.setCustomer(selectedCustomer);
                                resev.setDriveTrip(selectedTrip);
                                Waiver.addWaiver(reservationId);
                                Payment.addPayment(reservationId);
                                resev.setCustomerId(custId);
                                resev.setDiveTripId(selectedTrip.getTripId());
                                resev.setStatus("PENDING");
                                reservationData.add(resev);
                                
                                Runnable emailTask = () -> 
                                { 
                                    try 
                                    {
                                        EmailAlert.sendRequestEmail(reservationId, customer, selectedTrip);

                                        Platform.runLater(()-> AlertUtil.showEmailSent("Reservation email request "
                                                + "sent to " + customer.getFullName() + "."));

                                    }
                                    catch (IOException | IllegalStateException | SQLException | InterruptedException  e)
                                    {
                                        Platform.runLater(() -> 
                                        {
                                            try 
                                            {
                                                EmailAlert.sendRequestEmail(reservationId, customer, selectedTrip);
                                                Platform.runLater(()-> AlertUtil.showEmailSent("Reservation email request "
                                                + "sent to " + customer.getFullName() + "."));
                                            }
                                            catch (IOException | IllegalStateException | SQLException | InterruptedException  ex)
                                            {
                                               Platform.runLater(()-> AlertUtil.showErrorAlert("Email Error!", ex));
                                                 Thread.currentThread().interrupt();
                                            }
                                        });
                                    }
                                };
                            
                                ExecutorService executor = Executors.newSingleThreadExecutor();
                                executor.execute(emailTask);
                                executor.shutdown();

                            }
                        }
                    }
                } 
                else if(isProceedClicked && !isCustomerFound)
                {
                    Customer customer = new Customer();
                    boolean confirmClicked = showReservationAddDialog_NewCustomer(customer);
                    if(confirmClicked)
                    {

                        Customer.addCustomer(customer);

                        int custId = Customer.getCustId(customer);
                       

                        if(Reservation.addReservation(custId, selectedTrip.getTripId()) > 0)
                        {
                            AlertUtil.showDbSavedAlert("New Reservation for " + customer.getFullName() + " succesfully saved.");
                            int reservationId = Reservation.getReservationId(custId, selectedTrip.getTripId());
                            Reservation resev = new Reservation(reservationId);
                            resev.setCustomer(customer);
                            resev.setDriveTrip(selectedTrip);
                            Waiver.addWaiver(reservationId);
                            Payment.addPayment(reservationId);
                            resev.setCustomerId(custId);
                            resev.setDiveTripId(selectedTrip.getTripId());
                           
                            resev.setStatus("PENDING");
                            reservationData.add(resev);
                     
                            Runnable emailTask = () -> 
                            { 
                                try 
                                {
                                    EmailAlert.sendRequestEmail(reservationId, customer, selectedTrip);

                                    Platform.runLater(()-> AlertUtil.showEmailSent("Reservation email "
                                            + "request sent to " + customer.getFullName() + "."));

                                }
                                catch (IOException | IllegalStateException | SQLException | InterruptedException  e)
                                {
                                    Platform.runLater(() -> 
                                    {
                                        try 
                                        {
                                            EmailAlert.sendRequestEmail(reservationId, customer, selectedTrip);
                                            Platform.runLater(()-> AlertUtil.showEmailSent("Reservation email "
                                                    + "request sent to " + customer.getFullName() + "."));
                                        }
                                        catch (IOException | IllegalStateException | SQLException | InterruptedException  ex)
                                        {
                                           Platform.runLater(()-> AlertUtil.showErrorAlert("Email Error!", ex));
                                             Thread.currentThread().interrupt();
                                        }
                                    });
                                }
                            };
                            
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.execute(emailTask);
                            executor.shutdown();                           
                        }
                    }
                }
            }
            catch(SQLException e)
            {
                AlertUtil.showDbErrorAlert("Error with adding new Reservation", e);
            }
        }
        else
        {
           AlertUtil.noSelectionAlert("No Dive Trip Selected", "Please select a dive trip in the dive trips table for the new reservation.");
        }
        
        newReservationButton.setDisable(false);
    }
    
    @FXML
    public void transitionToCustomers() throws IOException
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/customers/view/CustomerPane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    @FXML
    public void transitionToHome(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/home/view/HomePane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    @FXML
    public void transitionToRecords(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/records/view/RecordsPane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    @FXML
    public void exitProgram(ActionEvent event)
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}