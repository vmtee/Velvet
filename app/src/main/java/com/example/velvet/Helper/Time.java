package com.example.velvet.Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Time {
    /**
     * returns current Time of device
     * **/
    public String getCurrentTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentTime = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss a");
        String time = dateFormat.format(currentTime);
        return time;
    }
    /**
     * returns current Date of device
     * **/
    public String getCurrentDate(){
        Date time = Calendar.getInstance().getTime();
        String formatTime = DateFormat.getDateInstance().format(time);
        return formatTime;
    }
}
