
package scuba.solutions.ui.home.view;

import com.jfoenix.controls.JFXButton;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import scuba.solutions.database.DbConnection;
import scuba.solutions.ui.dive_schedule.model.DiveTrip;
import scuba.solutions.util.AlertUtil;
import scuba.solutions.util.SQLUtil;

/**
 * Controller class for the Home Pane
 *
 * @author Jonathan Balliet, Samuel Brock
 */
public class HomePaneController implements Initializable 
{
    Stage primaryStage;
    private final DateTimeFormatter currentTime =  DateTimeFormatter.ofPattern("h:mm:ss a");
    private final DateTimeFormatter currentDate = DateTimeFormatter.ofPattern("MMM dd, yyyy");
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
    private Label todayDivelabel1;
    @FXML
    private Label todayDivelabel2;
    @FXML
    private Label todayDivelabel3;
    @FXML
    private Label nextDiveLabel;
    @FXML
    private JFXButton helpButton;
    @FXML
    private Label currentTimeLabel;
    @FXML
    private Label currentDateLabel;
    @FXML
    private JFXButton recordsButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        homeButton.setDisable(true);   // disables the Home Button
        setTimeDisplay();              // sets the current time
        setDateDisplay();              // sets the current date
        setTodaysDives();             // sets todays dives
        setNextDive();                // sets the next dive scheduled beginning from a future date
    }


    // Sets the current time on Home Pane display. The time displays the seconds amount
    // and constantly updates every second.
    private void setTimeDisplay()
    {

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0),
                                        event -> currentTimeLabel.setText(LocalTime.now().format(currentTime))),
                                        new KeyFrame(Duration.seconds(1)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    
    // Sets the current date on the Home Pane display.
    private void setDateDisplay()
    {
        LocalDate date = LocalDate.now();
        currentDateLabel.setText(date.format(currentDate));
        
    }
    
   // Sets the current dives schedules scheduled for the day.
   // A maxmimum of three dives can be displayed.
   private void setTodaysDives()
   {
       
        List<DiveTrip> trips = new LinkedList<>();   // list to store the trips
        LocalDate date = LocalDate.now();
        
        Connection connection = null;
        PreparedStatement preSt = null;
        ResultSet result = null;
        
        try 
        {
            connection = DbConnection.accessDbConnection().getConnection();
            preSt = connection.prepareStatement("Select * from Dive_Trip Where Trip_Date = ?");

            preSt.setDate(1, Date.valueOf(date));
            
            result = preSt.executeQuery();
            
            while(result.next())
            {
                int tripID = result.getInt(1);
                DiveTrip trip = new DiveTrip(tripID);
                
                trip.setTripDate((result.getDate(2)).toLocalDate());
                String strTime = result.getString(4);
                LocalTime time = SQLUtil.intervalToLocalTime(strTime);
                trip.setDepartTime(time);
                
                trips.add(trip);
 
            }
                 
        } 
        catch (SQLException | IOException e ) 
        {
                    AlertUtil.showErrorAlert("Error occured", e);
        }
        finally
        {
            try
            {
                if(preSt != null)
                    preSt.close();
                
                if(result != null)
                    result.close();
            }
            catch (SQLException e)
            {
                AlertUtil.showErrorAlert("Error occured", e);
            } 
        }
        
        if(trips.isEmpty())
        {
            
            todayDivelabel1.setText("No Dives Today");   // displays No Dives Today is list is empty
         
        }
        else
        {
            Collections.sort(trips);          // sorts the dive trips to be displayed 
            
            for(int i = 0; i < trips.size(); i++)
            {
                String diveText = "Dive at ";
                diveText += trips.get(i).getDepartTime().format(DateTimeFormatter.ofPattern("h:mm a"));
     
                switch (i) 
                {
                    case 0:
                        todayDivelabel1.setText(diveText);
                        break;
                    case 1:
                        todayDivelabel2.setText(diveText);
                        break;
                    case 2:
                        todayDivelabel3.setText(diveText);
                        break;
                    default:
                        break;
                }
            }   
        }
   }
   
   // Sets the date and time for the next scheduled dive. This dive 
   // starts from the next day (must be a future date).
   private void setNextDive()
   {
       
        DiveTrip trip  = null;
        boolean found = false;
        LocalDate date = LocalDate.now();
        date = date.plusDays(1);
        String dateStr = date.format((DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        
        
        Connection connection = null;
        PreparedStatement preSt = null;
        ResultSet result = null;
        try 
        {
            connection = DbConnection.accessDbConnection().getConnection();
            preSt = connection.prepareStatement("Select * from Dive_Trip Where Trip_Date >= to_date(?, 'DD-MON-YYYY') ORDER BY trip_date ASC");

            preSt.setString(1, dateStr);
            
            result = preSt.executeQuery();
            
            if(result.next())
            {
                found = true;
                
                int tripID = result.getInt(1);
                trip = new DiveTrip(tripID);
                
                trip.setTripDate((result.getDate(2)).toLocalDate());
                String strTime = result.getString(4);
                LocalTime time = SQLUtil.intervalToLocalTime(strTime);
                trip.setDepartTime(time);
            }
            else
            {
                found = false;
            }
                 
        } 
        catch (SQLException | IOException e ) 
        {
                    AlertUtil.showErrorAlert("Error occured", e);
        }
        finally
        {
            try
            {
                if(preSt != null)
                    preSt.close();
                
                if(result != null)
                    result.close();
            }
            catch (SQLException e)
            {
                AlertUtil.showErrorAlert("Error occured", e);
            } 
        }
        
        if (found == false)
        {
             nextDiveLabel.setText("No Dives Scheduled");
        }
        else
        {

            DiveTrip nextDive;
            nextDive = trip;
            String nextDiveText = "";

            nextDiveText += nextDive.getTripDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
            nextDiveText += " at ";
            nextDiveText += nextDive.getDepartTime().format(DateTimeFormatter.ofPattern("h:mm a"));

            nextDiveLabel.setText(nextDiveText);
       }
        
   }
   
   // Displays the user guide for the Scuba Solution application when Help Button clicked.
   @FXML
   public void displayUserGuide() throws IOException
   {
        File file = new File("resources/UserGuide.pdf");
        Desktop.getDesktop().open(file);
       
   }
   
    // Transitions to the Customer Pane when Customer button clicked.
    @FXML
    public void transitionToCustomers(ActionEvent event) throws IOException
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/customers/view/CustomerPane.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    // Transitions to the Dive Schedule Pane when Dive Schedule button clicked.
    @FXML
    public void transitionToDiveSchedule(ActionEvent event) throws IOException 
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/scuba/solutions/ui/dive_schedule/view/DiveSchedule.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show( );
    }
    
    // Transitions to the Records Pane when Records button clicked.
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
    
    // Exits the application when the Exit button clicked.
    @FXML
    public void exitProgram(ActionEvent event)
    {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


   
   
}
