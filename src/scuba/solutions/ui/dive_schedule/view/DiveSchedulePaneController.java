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
import scuba.solutions.ui.dive_schedule.model.Waiver;
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
    private ObservableList<Customer>  customerData = FXCollections.observableArrayList();
    private ObservableList<DiveTrip>  tripData = FXCollections.observableArrayList();    
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
    private TableView<Customer> customersTable;
    @FXML
    private TableView<DiveTrip> tripTable;
    @FXML
    private TableColumn<DiveTrip, LocalDate> tripDateColumn;
    @FXML
    private TableColumn<DiveTrip, String> dayOfWeekColumn;
    @FXML
    private TableColumn<DiveTrip, LocalTime> departTimeColumn;
    @FXML
    private TableColumn<DiveTrip, String> availSeatsColumn;
    @FXML
    private TableColumn<DiveTrip, String> weatherStatusColumn;
    @FXML
    private TableColumn<Customer, String> fullNameColumn;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label waiverLabel;
    @FXML
    private Label paymentLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        primaryStage = DiveSchedulePane.getPrimaryStage();
        diveScheduleButton.setDisable(true);
        
        initializeTrip();
        initializeCustomer();
        
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

        tripTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> loadTripCustomers(newValue));
        
        customersTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showCustomerStatusDetails(newValue));
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
        
        tripTable.setItems(tripData);
    }
    
    // Retrieves customers in dive trip    
    private void loadTripCustomers(DiveTrip selectedTrip)
    { 
       
       customerData.clear();
       
       int tripID = 0;
 
       if (selectedTrip != null) 
       {
           tripID = selectedTrip.getTripId();
       }
               
       PreparedStatement preSt = null;           
       ResultSet result = null;          
       PreparedStatement statement = null;
       ResultSet resultSet = null; 
         
       try
       {
            preSt = connection.prepareStatement("SELECT CUST_ID FROM RESERVATION WHERE TRIP_ID=?");

            preSt.setInt(1, tripID);

            result = preSt.executeQuery();

            while(result.next())
            {
                int custID = result.getInt(1);
                statement = connection.prepareStatement("SELECT FIRST_NAME, LAST_NAME FROM CUSTOMER WHERE CUST_ID=?");

                statement.setInt(1, custID);

                resultSet = statement.executeQuery();
                resultSet.next();

                String firstName = resultSet.getString(1);
                String lastName = resultSet.getString(2);

                Customer customer = new Customer(custID, firstName, lastName);

                customerData.add(customer);
            }
        }
        catch (SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with loading Reserved Customers into Table", e);
        }
            finally
            {
                try
                {
                    if (statement != null || preSt != null)
                    {
                        statement.close();
                        preSt.close();
                    }

                    resultSet.close();
                    result.close();
                }
                catch (SQLException e)
                {
                    AlertUtil.showDbErrorAlert("Error with DB Connection", e);
                }
        }

        customersTable.setItems(customerData);   
   }

   private void showCustomerStatusDetails(Customer customer)
    {
        int customerId = customer.getCustomerID();
        Waiver waiver = new Waiver();
        PreparedStatement statement = null;
        ResultSet resultSet = null; 
         
        try 
        {
            statement = connection.prepareStatement("SELECT RESERVATION_ID FROM RESERVATION WHERE CUST_ID=?");          
            statement.setInt(1, customerId);           
            resultSet = statement.executeQuery();            
            resultSet.next();
            
            int reservationId = resultSet.getInt(1);
            
            statement = connection.prepareStatement("SELECT * FROM WAIVER WHERE RESERVATION_ID=?");            
            statement.setInt(1, reservationId);            
            resultSet = statement.executeQuery();            
            resultSet.next();
            
            waiver.setReservationId(reservationId);            
            waiver.setDateSigned(resultSet.getDate(2).toLocalDate());
            waiver.setERFirst(resultSet.getString(3));
            waiver.setERLast(resultSet.getString(4));
            waiver.setERPhone(resultSet.getString(5));   
            
        } 
        catch (SQLException e)
        {
            AlertUtil.showDbErrorAlert("Error with loading details for a Reserved Customer", e);
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
        }
        
        if (customer != null) 
        {
            firstNameLabel.setText(customer.getFirstName());
            lastNameLabel.setText(customer.getLastName());
            waiverLabel.setText(waiver.isComplete());
            //paymentLabel.setText();
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
   
   public void initializeCustomer()
   {
       fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
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

                loadDiveTrips();
            
            }
        } 
        else 
        {
            // If nothing is selected a warning message will pop-up
            AlertUtil.noSelectionSAlert("No Dive Trip Selected", "Please Select A Dive Trip in the Table to Update.");
        }
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
       DiveTrip trip = new DiveTrip();
       boolean okClicked = showDiveAddDialog(trip);
       currentTab = DiveAddDialogController.getTab();
       
       if (okClicked && currentTab.equalsIgnoreCase("singleDiveTab"))
       {
            LocalDate tripDate = trip.getTripDate();
            LocalTime departTime = trip.getDepartTime();
            String time = SQLUtil.localTimeToInterval(departTime);
            PreparedStatement preSt;

            try 
            {
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
            catch (SQLException e) 
            {
                AlertUtil.showDbErrorAlert("Error Occured with the Addition of the Dive Trip.", e);
            }
       		
            loadDiveTrips();
                  
       }
       
       if (okClicked && currentTab.equalsIgnoreCase("recurringDiveTab"))
       {
            LinkedList<DiveTrip> trips = DiveAddDialogController.getRecurringTrips();

            PreparedStatement preSt = connection.prepareStatement("INSERT INTO DIVE_TRIP "
                            + "(trip_date, departure_time)"
                            + " values(?, ?)");
  			
            try 
            {
       			
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
                catch (SQLException e) 
       		{
                    AlertUtil.showDbErrorAlert("Error Occured with the Additon of the Recurring Dive Trips.", e);
       		}
  			
            loadDiveTrips();
       }
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
