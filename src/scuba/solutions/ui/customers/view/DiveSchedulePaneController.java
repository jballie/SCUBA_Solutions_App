
package scuba.solutions.ui.customers.view;

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
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.customers.model.Customer;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.util.DateUtil;

/**
 * Controller for the Dive Schedule Pane.
 *
 * @author Jon
 */
public class DiveSchedulePaneController implements Initializable {
    
    private ObservableList<DiveTrip>  tripsData = FXCollections.observableArrayList();
    private FilteredList<DiveTrip> filteredData; 
    private static Connection connection;
    
    private Stage primaryStage;
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
    private JFXButton newReservButton;
    @FXML
    private JFXButton newDiveButton;
    @FXML
    private JFXButton updateDiveButton;
    @FXML
    private JFXButton viewWeatherButton;
    @FXML
    private TableView<DiveTrip> diveTripsTable;
    @FXML
    private TableColumn<DiveTrip, LocalDate> dateColumn;
    @FXML
    private TableColumn<DiveTrip, String> dayColumn;
    @FXML
    private TableColumn<DiveTrip, LocalTime> startTimeColumn;
    @FXML
    private TableColumn<DiveTrip, String> availColumn;
    @FXML
    private TableColumn<DiveTrip, String> tripStatusColumn;
    @FXML
    private TableColumn<?, ?> customerColumn;
    @FXML
    private TableColumn<?, ?> reservStatusColumn;
    @FXML
    private JFXButton updateReservButton;
    @FXML
    private TableView<?> reservationsTable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         primaryStage = DiveSchedulePane.getPrimaryStage();
         diveScheduleButton.setDisable(true);
         initalizeDiveTrips();
        try {
            loadDiveTrips();
        } catch (IOException ex) {
            Logger.getLogger(DiveSchedulePaneController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DiveSchedulePaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    
      // Initializes the columns for the TableView
    public void initalizeDiveTrips()
    {
    	dateColumn.setCellValueFactory(cellData -> cellData.getValue().tripDateProperty());
        dayColumn.setCellValueFactory(cellData -> cellData.getValue().dayOfWeekProperty());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().departTimeProperty());
        availColumn.setCellValueFactory(cellData -> cellData.getValue().availSeatsProperty());
        tripStatusColumn.setCellValueFactory(cellData -> cellData.getValue().weatherStatusProperty());
    }
    
    // Loads all the Dive Trips from that database into the tableView
    public void loadDiveTrips() throws FileNotFoundException, IOException, SQLException
    {
        
    	tripsData.clear();
            
        // queries DB to get all data for each column
        // stores the result set into an equivalent variable
        // creates a Dive Trip object and adds to the list
    	
    	connection = DbConnection.accessDbConnection().getConnection();
		
	String query = "SELECT * FROM DIVE_TRIP";
		
	Statement statement = null;
	ResultSet result = null;
    
	try
	{
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            while(result.next())
            {
                int tripId = result.getInt(1);
		DiveTrip trip = new DiveTrip(tripId);
		trip.setTripDate(result.getDate(2).toLocalDate());
                String day = trip.determineDayOfWeek();
                trip.setDayOfWeek(day);
		trip.setAvailSeats(result.getInt(3));
                
                // Issue with Interval to Day conversion to JDBC
                //will need to add a util method for conversion or PL/SQL function
		String time = result.getString(4).trim();
                
                String newTime = time.substring(2, 6);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

                LocalTime dateTime = LocalTime.parse(newTime, formatter);
                System.out.println(newTime);
                
                
                trip.setDepartTime(dateTime);
		trip.setWeatherStatus(result.getString(5));
		
				
		tripsData.add(trip);
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
		{
                    statement.close();
		}		
                    result.close();
		}
		catch (SQLException e)
		{
                    Logger.getLogger(CustomerPaneController.class.getName()).log(Level.SEVERE, null, e);
		}
	}
        diveTripsTable.getItems().setAll(tripsData);
    }
    
   

    @FXML
    private void clearSearch(ActionEvent event) {
    }
    
    
    @FXML
    public void transitionToCustomers() throws IOException
    {
         Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
    	    loader.setLocation(getClass().getResource("CustomerPane.fxml"));
    	    Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show( );
    }
}
