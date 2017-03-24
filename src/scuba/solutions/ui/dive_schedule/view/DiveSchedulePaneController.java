/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.customers.view.CustomerPaneController;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.ui.reservations.model.Waiver;
import scuba.solutions.ui.reservations.model.Payment;
import scuba.solutions.ui.reservations.model.Reservation;
import scuba.solutions.ui.reservations.view.ReservationEditDialogController;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.SQLUtil;

/**
 * FXML Controller class
 *
 * @author Jonathan Balliet, Samuel Brock
 * Bugs/TODO:
 * Error when selection of Dive Event that has no Reservations.
 * 
 */
public class DiveSchedulePaneController implements Initializable 
{
    private static Connection connection;    
    private final ObservableList<Reservation>  reservationData = FXCollections.observableArrayList();
    private final ObservableList<DiveTrip>  tripData = FXCollections.observableArrayList();    
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
    private TableColumn<DiveTrip, String> weatherStatusColumn;
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
    
   private Waiver waiver = new Waiver();
    private Payment payment = new Payment();
    private int tripId;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        primaryStage = DiveSchedulePane.getPrimaryStage();
        diveScheduleButton.setDisable(true);
        
        initializeTrip();
        initializeReservation();
        
        try
        {
            loadDiveTrips();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with loading Dive Trips to the Table", e);
        }
        
        // Clear customer details.
        

        tripTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> loadTripReservations(newValue));
        
        reservationsTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showReservationStatusDetails(newValue));
    }    
    
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
                trip.setWeatherStatus(result.getString(5));
                trip.setDayOfWeek(trip.determineDayOfWeek());
				
                tripData.add(trip);
            }
    	}
    	catch (SQLException e)
    	{
            AlertUtil.showDbErrorAlert("Error with selecting and adding Dive Trips", e);
    	}
        /*
    	finally
    	{
            try
            {
            	if (statement != null)
            	{
                    statement.close();
            	}		
                    result.close();
            }
            catch (SQLException e)
            {
            	AlertUtil.showDbErrorAlert("Error with DB Connection", e);
            }
            
    	}
        */
        tripTable.setItems(tripData);
    }
    
    // Retrieves customers in dive trip    
    private void loadTripReservations(DiveTrip selectedTrip)
    { 
       
         reservationData.clear();
 
       
       if (selectedTrip != null) 
       {
           tripId = selectedTrip.getTripId();
       }
               
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
                
                statement = connection.prepareStatement("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMER WHERE CUST_ID=?");
          
                statement.setInt(1, custID);
           
                resultSet = statement.executeQuery();
                resultSet.next();
                
                String firstName = resultSet.getString(1);
                String lastName = resultSet.getString(2);
                
                Customer customer = new Customer(custID, firstName, lastName);
                Reservation res = new Reservation(restID);
                res.setCustomer(customer);
                res.setDiveTrip(diveID);
                res.setStatus(status);

                reservationData.add(res);
            }
         }
         catch (SQLException e)
         {
            e.printStackTrace();
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
                    
                } catch (SQLException e) {
                    Logger.getLogger(DiveSchedulePaneController.class.getName()).log(Level.SEVERE, null, e);
                }
         	}
         
        reservationsTable.setItems(reservationData);   
   }

   private void showReservationStatusDetails(Reservation reservation)
    {
   int reservationId = 0;
        if(reservation != null) {
        	 reservationId = reservation.getReservationId();
        }
        PreparedStatement statement = null;
        ResultSet resultSet = null; 
       
        try {
            /* 
            statement = connection.prepareStatement("SELECT RESERVATION_ID FROM RESERVATION WHERE CUST_ID=? AND TRIP_ID=?");          
            statement.setInt(1, customerId);
            statement.setInt(2, tripId);
            
            System.out.println(tripId);
            resultSet = statement.executeQuery(); 
            if(resultSet.next())
            {
                reservationId = resultSet.getInt(1);
            }
            */
        	
            statement = connection.prepareStatement("SELECT * FROM WAIVER WHERE RESERVATION_ID=?");            
            statement.setInt(1, reservationId);            
            resultSet = statement.executeQuery();            
            
            
            waiver.setReservationId(reservationId);
            
            if(resultSet.next()) {
                waiver.setWaiverStatus(resultSet.getString(2));
                waiver.setDateSigned(resultSet.getDate(3).toLocalDate());
                waiver.setERFirst(resultSet.getString(4));
                waiver.setERLast(resultSet.getString(5));
                waiver.setERPhone(resultSet.getString(6));
            }
            
            statement = connection.prepareStatement("SELECT * FROM PAYMENT WHERE RESERVATION_ID=?");            
            statement.setInt(1, reservationId);            
            resultSet = statement.executeQuery();            
            
            if(resultSet.next()) {            
                payment.setReservationId(reservationId);
                payment.setPaymentStatus(resultSet.getString(2));
                payment.setCCConfirmNo(resultSet.getInt(3));
                payment.setDateProcessed(resultSet.getDate(4).toLocalDate());
                payment.setAmount(resultSet.getInt(5));
            }
            
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch(NullPointerException e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	try
            {
        		if (statement != null)
        		{
        			statement.close();
        		}		
                resultSet.close();
        	}
    		catch (SQLException e)
    		{
                Logger.getLogger(DiveSchedulePaneController.class.getName()).log(Level.SEVERE, null, e);
    		}
            catch(NullPointerException e)
            {
            	e.printStackTrace();
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
   
   public void initializeTrip()
   {
    	tripDateColumn.setCellValueFactory(cellData -> cellData.getValue().tripDateProperty());
        dayOfWeekColumn.setCellValueFactory(cellData -> cellData.getValue().dayOfWeekProperty());
        departTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departTimeProperty());
        availSeatsColumn.setCellValueFactory(cellData -> cellData.getValue().availSeatsProperty());
        weatherStatusColumn.setCellValueFactory(cellData -> cellData.getValue().weatherStatusProperty());
    }
   
   public void initializeReservation()
   {
       fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().fullNameProperty());
       statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
   }
   
   public boolean showDiveEditDialog(DiveTrip trip) 
   {
        try
        {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DiveSchedulePaneController.class.getResource("DiveEditDialog.fxml"));
            Parent root = loader.load();

            // Create the dialog Stage.
            dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Set the customer into the controller.
            DiveEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDive(trip);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked(); 
        }
        catch (IOException e) 
    	{
            e.printStackTrace();
    	    return false;
    	}	
   }
   
   
   @FXML
   public void updateTrip() throws FileNotFoundException, IOException, SQLException
   {
        updateDiveButton.setDisable(true);
        DiveTrip selectedTrip = (DiveTrip) tripTable.getSelectionModel().getSelectedItem();

        if (selectedTrip != null)
        {
    	    boolean okClicked = showDiveEditDialog(selectedTrip);
            
            if (okClicked) 
            {
                int tripId = selectedTrip.getTripId();

                LocalDate tripDate = selectedTrip.getTripDate();
                int availSeat = selectedTrip.getAvailSeats();
                LocalTime departTime = selectedTrip.getDepartTime();
                String time = SQLUtil.localTimeToInterval(departTime);
                String tripStatus = selectedTrip.getWeatherStatus();
                System.out.println(departTime);

                PreparedStatement preSt;

                try 
                {
                    preSt = connection.prepareStatement("UPDATE DIVE_TRIP SET trip_date=?," +
                                    "available_seats=?, departure_time=?, weather_status=?" +
                                    "WHERE TRIP_ID=?");

                    preSt.setDate(1, Date.valueOf(tripDate));
                    preSt.setInt(2, availSeat);
                    preSt.setString(3, time);
                    preSt.setString(4, tripStatus);
                    preSt.setInt(5, tripId);

                    if (preSt.executeUpdate() >= 0)  
                    {
                        AlertUtil.showDbSavedAlert("Dive Trip has successfully been Updated in the database.");
                    }
                } 
                catch (SQLException e) 
                {
                    AlertUtil.showDbErrorAlert("Error Occured with the Update of the Dive Trip", e);
                }
                finally
                {
                    updateDiveButton.setDisable(false);
                }
               
                loadDiveTrips();
            
            }
        } 
        else 
        {
            // If nothing is selected a warning message will pop-up
            AlertUtil.noSelectionSAlert("No Dive Trip Selected", "Please Select A Dive Trip in the Table to Update.");
           
        }
        updateDiveButton.setDisable(false);
   }
   
   
   public boolean showDiveAddDialog(DiveTrip trip) 
   {
        try
        {	
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DiveSchedulePaneController.class.getResource("DiveAddDialog.fxml"));
            Parent root = loader.load();

            // Create the dialog Stage.
            dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Set the dive trip into the controller.
            DiveAddDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDive(trip);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        }
	catch (IOException e) 
	{
            e.printStackTrace();
            return false;
	}
   }
   
   @FXML
   public void addTrip() throws FileNotFoundException, IOException, SQLException
   {
       newDiveButton.setDisable(true);
       DiveTrip trip = new DiveTrip();
       boolean okClicked = showDiveAddDialog(trip);
       currentTab = DiveAddDialogController.getTab();
       try 
       {
            if (okClicked && currentTab.equalsIgnoreCase("singleDiveTab"))
            {
            LocalDate tripDate = trip.getTripDate();
            LocalTime departTime = trip.getDepartTime();
            String time = SQLUtil.localTimeToInterval(departTime);
            PreparedStatement preSt;

            
                preSt = connection.prepareStatement("INSERT INTO DIVE_TRIP "
                + "(trip_date, departure_time)"
                + " values(?, ?)");

                preSt.setDate(1, Date.valueOf(tripDate));
                preSt.setString(2, time);

                if (preSt.executeUpdate() >= 0)  
                {
                    AlertUtil.showDbSavedAlert("Dive Trip has successfully been added to the database.");
                }
            }
        }
        catch (SQLException e) 
        {
                AlertUtil.showDbErrorAlert("Error Occured with the Addition of the Dive Trip.", e);
                
            }
            catch (Exception e)
            {
                
            }
            finally
            {
                newDiveButton.setDisable(false);
            }
       		
            
            loadDiveTrips();
                  
       try
       {
          
       
       
       if (okClicked && currentTab.equalsIgnoreCase("recurringDiveTab"))
       {
            LinkedList<DiveTrip> trips = DiveAddDialogController.getRecurringTrips();

            PreparedStatement preSt = connection.prepareStatement("INSERT INTO DIVE_TRIP "
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
	  			
                int result[] = preSt.executeBatch();

                //if (result[0] == -2)
                //{
                    AlertUtil.showDbSavedAlert("Recurring Dive Trips have successfully been added to the database.");
                //}
       		}
       }        
                catch (SQLException e) 
       		{
                    AlertUtil.showDbErrorAlert("Error Occured with the Additon of the Recurring Dive Trips.", e);
       		}
                catch (Exception e)
                {
                    
                }
                finally
                {
                    newDiveButton.setDisable(false);
                }
            
            loadDiveTrips();
       
      newDiveButton.setDisable(false);
   }
   
  public boolean showReservationEditDialog() {
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
    	   controller.setWaiver(waiver);
           controller.setPayment(payment);

    	   dialogStage.showAndWait();

    	   return controller.isOkClicked();
    	   
        }
       catch (IOException e) 
       {
           e.printStackTrace();
    	   return false;
       }
   }
   
   /**
    * Updates customer reservation in WAIVER and PAYMENT tables based on
    * user entry.  Calls book() method
    * 
    * @throws FileNotFoundException
    * @throws IOException
    * @throws SQLException 
    */
    @FXML
    public void updateReservation() throws FileNotFoundException, IOException, SQLException {
        
    	Reservation selectedReservation = (Reservation) reservationsTable.getSelectionModel().getSelectedItem();
         if (selectedReservation != null)
         {
            boolean okClicked = showReservationEditDialog();
            
            if (okClicked) 
            {            	 
            	int reservationId = waiver.getReservationId();
                LocalDate dateSigned = waiver.getDateSigned();
            	String erLast = waiver.getERLast();
            	String erFirst = waiver.getERFirst();
            	String erPhone = waiver.getERPhone();
                 
            	PreparedStatement preSt1 = 
            			connection.prepareStatement("UPDATE WAIVER SET date_signed=?, er_first=?, er_last=?,"+
            			 "er_phone=? WHERE reservation_id=?");
          
            	preSt1.setDate(1, Date.valueOf(dateSigned));
        		preSt1.setString(2, erFirst);
        		preSt1.setString(3, erLast);
        		preSt1.setString(4, erPhone);
        		preSt1.setInt(5, reservationId);
                
                int ccConfirmNo = payment.getCCConfirmNo();
                LocalDate dateProc = payment.getDateProcessed();
                int amount = payment.getAmount();
          
               PreparedStatement preSt2 = 
            			connection.prepareStatement("UPDATE PAYMENT SET cc_confirm_no=?, date_processed=?, amount=?"+
            			 "WHERE reservation_id=?");
          
            	preSt2.setInt(1, ccConfirmNo);
        		preSt2.setDate(2, Date.valueOf(dateProc));
        		preSt2.setInt(3, amount);
        		preSt2.setInt(4, reservationId);
                
                if (preSt1.executeUpdate() == 1 && preSt2.executeUpdate() == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Saved!");
                    alert.setContentText("Reservation has successfully been updated in the database.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Error Occured with update of reservation.");
                    alert.showAndWait();
                }
                book();
                showReservationStatusDetails(selectedReservation);
             }
         } 
         else 
         {
             Alert alert = new Alert(Alert.AlertType.WARNING);
             alert.initOwner(DiveSchedulePane.getPrimaryStage());
             alert.setTitle("No Selection");
             alert.setHeaderText("No Customer Selected");
             alert.setContentText("Please select a customer in the table to update.");

             alert.showAndWait();
         }
    }
    
     /**
     * Updates RESERVATION, WAIVER, and PAYMENT tables to indicate completeness
     * of data entries
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SQLException 
     */
    public void book() throws FileNotFoundException, IOException, SQLException {
        
        int reservationId = waiver.getReservationId();

        String update = null;		
        Statement statement = connection.createStatement();
        
        if(waiver.isComplete()) 
        {
            statement.executeUpdate("UPDATE WAIVER SET waiver_status='Complete' WHERE reservation_id=" + reservationId);
        } else 
        {
           statement.executeUpdate("UPDATE WAIVER SET waiver_status='Incomplete' WHERE reservation_id=" + reservationId);
        }
        
        if(payment.isComplete()) 
        {
            statement.executeUpdate("UPDATE PAYMENT SET payment_status='Paid' WHERE reservation_id=" +  + reservationId);
        } else 
        {
            statement.executeUpdate("UPDATE PAYMENT SET payment_status='Unpaid' WHERE reservation_id=" +  + reservationId);
        }     
        
        ResultSet resultSet = null;
        int availSeats;
        
        resultSet = statement.executeQuery("SELECT trip_id FROM RESERVATION WHERE reservation_id=" + reservationId);
        resultSet.next();
            
        tripId = resultSet.getInt(1);
            
        resultSet = statement.executeQuery("SELECT available_seats FROM DIVE_TRIP WHERE trip_id=" + tripId);
        resultSet.next();
        availSeats = resultSet.getInt(1);
        if(waiver.isComplete() && payment.isComplete()) 
        {
            update = "UPDATE RESERVATION SET reservation_status='Booked' WHERE reservation_id=" + reservationId;            
            statement.executeUpdate(update);
            
            availSeats--;
            
        } else 
        {
            resultSet = statement.executeQuery("SELECT reservation_status FROM RESERVATION WHERE reservation_id=" + reservationId);
            resultSet.next();
            String reservationStatus = resultSet.getString(1);
            
            if(reservationStatus != null && reservationStatus.equalsIgnoreCase("Booked"))
            {
                statement.executeUpdate("UPDATE RESERVATION SET reservation_status='Pending' WHERE reservation_id=" + reservationId);
                availSeats++;
            }
        }
        
        PreparedStatement preSt = 
               connection.prepareStatement("UPDATE DIVE_TRIP SET available_seats=? WHERE trip_id=?");
        preSt.setInt(1, availSeats);
        preSt.setInt(2, tripId);
        preSt.executeUpdate();
        loadDiveTrips();
        
        statement.close();
        preSt.close();
        resultSet.close();
    }
   
   
    @FXML
    private void clearSearch(ActionEvent event) 
    {
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

   
}    
