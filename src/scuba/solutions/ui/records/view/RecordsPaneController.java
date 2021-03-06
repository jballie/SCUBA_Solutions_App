
package scuba.solutions.ui.records.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.ui.reservations.model.Reservation;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.DateUtil;
import scuba.solutions.util.SQLUtil;

/**
 * 
 * Controller class for the Records Pane.
 *
 * @author Jonathan Balliet, Samuel Brock
 */
public class RecordsPaneController implements Initializable {
    private static Connection connection;    
    private final ObservableList<Reservation>  recordsData = FXCollections.observableArrayList();
    private FilteredList<Reservation> filteredRecords; 
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
    private TableColumn<Reservation, Integer> reservationIdColumn;
    @FXML
    private TableColumn<Reservation, String> reservationStatusColumn;
    @FXML
    private TableColumn<Reservation, String> customerNameColumn;
    @FXML
    private TableColumn<Reservation, String> tripDateColumn;
    @FXML
    private TableColumn<Reservation, String> availabilityColumn;
    @FXML
    private TableColumn<Reservation, String> tripStatusColumn;
    @FXML
    private TableView<Reservation> recordsTable;
    @FXML
    private JFXButton recordsButton;
    @FXML
    private TableColumn<Reservation, LocalTime> departTimeColumn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
       recordsButton.setDisable(true);
       initalizeRecordColumns();
        try 
        {
            loadRecords();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(RecordsPaneController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(RecordsPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        initializeSearchField();
        searchTextField.setFocusTraversable(false);
    }   
    
    // Loads all of the historical data for the company into the Records Table.
    private void loadRecords() throws IOException, FileNotFoundException, SQLException
    {
        StringBuilder recordsQuery = new StringBuilder();
        recordsQuery.append("SELECT R.RESERVATION_ID, R.RESERVATION_STATUS, ")
                    .append("C.CUST_ID, C.FIRST_NAME, C.LAST_NAME, D.TRIP_ID, D.TRIP_DATE, ")
                    .append("D.DEPARTURE_TIME, D.AVAILABLE_SEATS, D.DIVE_STATUS ")
                    .append("FROM RESERVATION R INNER JOIN CUSTOMER C ON R.CUST_ID = C.CUST_ID ")
                    .append("INNER JOIN DIVE_TRIP D ON R.TRIP_ID = D.TRIP_ID");
      
        String query = recordsQuery.toString();
        
        recordsData.clear();
    	
    	connection = DbConnection.accessDbConnection().getConnection();
		
		
    	Statement statement = null;
    	ResultSet result = null;
    
    	try
    	{
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            
            while(result.next())
            {
                int resId = result.getInt(1);
                Reservation reserv = new Reservation(resId);
                reserv.setStatus(result.getString(2));
                
                int cusId = result.getInt(3);
                Customer customer = new Customer(cusId);
                customer.setFirstName(result.getString(4));
                customer.setLastName(result.getString(5));
                
                reserv.setCustomer(customer);
                
                int tripId = result.getInt(6);
                DiveTrip trip = new DiveTrip(tripId);
                
                trip.setTripDate((result.getDate(7)).toLocalDate());
                trip.setAvailSeats(result.getInt(9));
                String strTime = result.getString(8);
                LocalTime time = SQLUtil.intervalToLocalTime(strTime);
                trip.setDepartTime(time);
                trip.setAvailSeats(result.getInt(9));
                trip.setTripStatus(result.getString(10));
                
                reserv.setDriveTrip(trip);
				
                recordsData.add(reserv);
            }
    	}
    	catch (SQLException e)
    	{
            AlertUtil.showDbErrorAlert("Error with selecting the reservations to the records table", e);
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
        
        recordsTable.setItems(recordsData);
      //  initializeSearchField();
    }
        
    // Initializes the columns for the Records Table.
    private void initalizeRecordColumns()
    {
        reservationIdColumn.setCellValueFactory(cellData -> cellData.getValue().reservationIdProperty());
        reservationStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().getCustomer().fullNameProperty());
        tripDateColumn.setCellValueFactory(cellData -> cellData.getValue().getDiveTrip().tripDateProperty());
  
        departTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getDiveTrip().departTimeProperty());
        availabilityColumn.setCellValueFactory(cellData -> cellData.getValue().getDiveTrip().availSeatsProperty());
        tripStatusColumn.setCellValueFactory(cellData -> cellData.getValue().getDiveTrip().tripStatusProperty()); 
       
        tripDateColumn.setComparator(new DateComparator());
        
    }
    
    // Compares the string dates stored in the Records Table according to their date value.
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
    
    // Clears the search bar. All stored data will be shown in the Records table.
    @FXML
    private void clearSearch(ActionEvent event) 
    {
        searchTextField.clear();
    }
    
    
    public void initializeSearchField()
    {
        
        filteredRecords= new FilteredList<>(recordsData, p -> true);

        // Sets the search filter Predicate whenever the search values change.
        searchTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> 
        {
            filteredRecords.setPredicate( reservation -> 
            {
                // If the search field text is empty, display all customers in the table
                if (newValue == null || newValue.isEmpty()) 
                {
                    return true;
                }

                // Compares the name, id, and date of birth of every customer with the search text
        
                String lowerCaseFilter = newValue.toLowerCase();


                if (reservation.getCustomer().getFullName().toLowerCase().contains(lowerCaseFilter)) 
                {
                    return true; // Search matches full name
                } 
                else if (DateUtil.format(reservation.getDiveTrip().getTripDate()).contains(lowerCaseFilter)) 
                {
                    return true;// Search matches trip date
                } 
                else if (reservation.getDiveTrip().getDepartTime().toString().contains(lowerCaseFilter)) 
                {
                    return true; // Search matches depart time
                } 
                else if (reservation.getDiveTrip().getTripStatus().toLowerCase().contains(lowerCaseFilter))
                {
                    return true; // Search matches trip status
                } 
                else if (reservation.getStatus().toLowerCase().contains(lowerCaseFilter)) 
                {
                    return true;
                } 
                else if (Integer.toString(reservation.getReservationId()).contains(lowerCaseFilter))
                {
                    return true; // Search matches eservation id.
                }
                
                return false; // Search does not match any data.
        
            });
        });
     
     
        SortedList<Reservation> sortedData = new SortedList<>(filteredRecords);

        //  Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(recordsTable.comparatorProperty());

        //  Add sorted (and filtered) data to the table.
        recordsTable.setItems(sortedData);
       
    }

    @FXML
    private void transitionToHome(ActionEvent event) throws IOException
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/home/view/HomePane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    

    @FXML
    private void transitionToDiveSchedule(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/dive_schedule/view/DiveSchedule.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }

    @FXML
    private void transitionToCustomers(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/customers/view/CustomerPane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }

    @FXML
    private void exitProgram(ActionEvent event) 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


    
 
    
}
