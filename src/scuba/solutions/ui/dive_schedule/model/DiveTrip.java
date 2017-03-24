package scuba.solutions.ui.dive_schedule.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scuba.solutions.util.DateUtil;

/**
 * Represents a Dive Trip for the Scuba Solutions app.
 * @author Jon
 */
public class DiveTrip {
    private final IntegerProperty tripId;
    private final ObjectProperty<LocalDate> tripDate;
    private final IntegerProperty availSeats;
    private final ObjectProperty<LocalTime> departTime;
    private final StringProperty weatherStatus;
    private final StringProperty dayOfWeek;

    public DiveTrip()
    {
    	this(0);
    }
    
    
    public DiveTrip(int tripId)
    {
    	this.tripId = new SimpleIntegerProperty(tripId);
        this.tripDate = new SimpleObjectProperty<LocalDate>();
        this.availSeats = new SimpleIntegerProperty(8);
        this.departTime = new SimpleObjectProperty<LocalTime>();
        this.weatherStatus = new SimpleStringProperty("");
        this.dayOfWeek = new SimpleStringProperty("");
    }

    public int getTripId() {
        return tripId.get();
    }

    public LocalDate getTripDate() {
        return tripDate.get();
    }
    
    public void setTripDate(LocalDate tripDate){
        this.tripDate.set(tripDate);
    }
    
    public StringProperty tripDateProperty() {
        String dateTemp = DateUtil.format(getTripDate());
    	StringProperty dateProp = new SimpleStringProperty(dateTemp);
    	return dateProp;
    }

      
    public int getAvailSeats() {
        return availSeats.get();
    }
    
    public void setAvailSeats(int availSeats){
        this.availSeats.set(availSeats);
    }
    
    public StringProperty availSeatsProperty() {
      String seatsTemp = Integer.toString(getAvailSeats());
      StringProperty seatsProp = new  SimpleStringProperty(seatsTemp);
      return seatsProp;
      
    }
    
    public LocalTime getDepartTime() {
        return departTime.get();
    }
    
     public void setDepartTime(LocalTime departTime){
        this.departTime.set(departTime);
    }
     
    public ObjectProperty<LocalTime> departTimeProperty() {
    	return departTime;
    }

    public String getWeatherStatus() {
        return weatherStatus.get();
    }
    
     public void setWeatherStatus(String weatherStatus){
        this.weatherStatus.set(weatherStatus);
    }
    
    public StringProperty weatherStatusProperty() {
      return weatherStatus;
      
    }
    
    public String determineDayOfWeek()
    {
       LocalDate date = tripDate.get();
       
       // util method for capitalizing words (also needed for adding/updating so it matches DB format)
       // or Apache Common lang library can be used
       String day = date.getDayOfWeek().toString();
      
       String let = day.substring(0, 1).toUpperCase();
     
       String rest = day.substring(1, day.length()).toLowerCase();
       
       String newDay = let + rest;
       return newDay;
    }

    
    public String getDayOfWeek()
    {
        return dayOfWeek.get();
    }
    
        
    public void setDayOfWeek(String day)
    {
        dayOfWeek.set(day);
    }
    
     public StringProperty dayOfWeekProperty() {
      return dayOfWeek;      
    }    
     
    public String toString()
    {
    	return  "Trip Date: " + tripDate.get().toString() + "\nDeaprt Time: " + departTime.get().toString();
    }
    
}
