package scuba.solutions.ui.dive_schedule.model;

import java.time.LocalDate;
import java.time.LocalTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scuba.solutions.util.DateUtil;

/**
 * Represents a Dive Trip that customers can make a reservation for.
 * @author Jonathan Balliet, Samuel Brock
 */
public class DiveTrip implements Comparable<DiveTrip> {
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
        this.tripDate = new SimpleObjectProperty();
        this.availSeats = new SimpleIntegerProperty(6);
        this.departTime = new SimpleObjectProperty();
        this.weatherStatus = new SimpleStringProperty("OK");
        this.dayOfWeek = new SimpleStringProperty("");
    }
    
    public DiveTrip(int tripId, LocalDate tripDate, LocalTime departTime)
    {
    	this.tripId = new SimpleIntegerProperty(tripId);
        this.tripDate = new SimpleObjectProperty(tripDate);
        this.availSeats = new SimpleIntegerProperty(6);
        this.departTime = new SimpleObjectProperty(departTime);
        this.weatherStatus = new SimpleStringProperty("OK");
        String day = this.determineDayOfWeek();
        this.dayOfWeek = new SimpleStringProperty(day);
    }

    public int getTripId() 
    {
        return tripId.get();
    }

    public LocalDate getTripDate() 
    {
        return tripDate.get();
    }
    
    public void setTripDate(LocalDate tripDate)
    {
        this.tripDate.set(tripDate);
    }
    
    public StringProperty tripDateProperty()
    {
        String dateTemp = DateUtil.format(getTripDate());
    	StringProperty dateProp = new SimpleStringProperty(dateTemp);
    	return dateProp;
    }

    public int getAvailSeats() 
    {
        return availSeats.get();
    }
    
    public void setAvailSeats(int availSeats)
    {
        this.availSeats.set(availSeats);
    }
    
    public ObjectProperty availSeatsProperty() 
    {
        return availSeats.asObject();
    }
    
    public LocalTime getDepartTime() 
    {
        return departTime.get();
    }
    
    public void setDepartTime(LocalTime departTime)
    {
        this.departTime.set(departTime);
    }
     
    public ObjectProperty<LocalTime> departTimeProperty() 
    {
    	return departTime;
    }

    public String getWeatherStatus() 
    {
        return weatherStatus.get();
    }
    
    public void setWeatherStatus(String weatherStatus)
    {
        this.weatherStatus.set(weatherStatus);
    }
    
    public StringProperty weatherStatusProperty() 
    {
        return weatherStatus; 
    }
    
    // Determines the day of week that the dive trip takes place on.
    public String determineDayOfWeek()
    {
       LocalDate date = tripDate.get();

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
    
    // Compares the Dive Trips by their date and time.
    @Override
    public int compareTo(DiveTrip o) 
    {
        if(this.getTripDate().compareTo(o.getTripDate()) > 0)
        {
            return 1;
        }
        else if(this.getTripDate().compareTo(o.getTripDate()) < 0)
        {
            return -1;
        }
        else if(this.getDepartTime().compareTo(o.getDepartTime()) > 0)
        {
            return 1;
        }
         else if(this.getDepartTime().compareTo(o.getDepartTime()) < 0)
        {
            return -1;
        }
        
        return 0;
    }
    
}
