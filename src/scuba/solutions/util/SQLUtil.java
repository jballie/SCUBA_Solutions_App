package scuba.solutions.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * (Insert a comment that briefly describes the purpose of this class definition.)
 *
 * <p/> Bugs: (List any known issues or unimplemented features here)
 * 
 * @author (Insert your first and last name)
 *
 */
public class SQLUtil
{
    public static LocalTime intervalToLocalTime(String str)
    {
        String strTime = str.substring(2, 7).trim();

        if(strTime.charAt(4) == ':')
        {
            strTime = strTime.substring(0, 4);
        }
        else if(strTime.charAt(3) == ':')
        {
            strTime = strTime.substring(0, 3);
            if(strTime.length() <= 4)
            {
                strTime = strTime + "0";
            }
        }
        if(strTime.charAt(2) == ':' && strTime.length() <= 4)
        {
            char temp = strTime.charAt(3);
            strTime = strTime.substring(0, 3);
            strTime = strTime + "0" + temp;
        }
      
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("H:mm");
        LocalTime time = LocalTime.parse(strTime, dtf);
        return time;
    }
	
    public static String localTimeToInterval(LocalTime time)
    {
        String interval = "0 " + time.toString() + ":0.0";
        return interval;
    }
	
}
