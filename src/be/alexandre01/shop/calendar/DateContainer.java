package be.alexandre01.shop.calendar;

import com.mysql.fabric.xmlrpc.base.Array;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateContainer {
    private String day;
    private int hour;
    private int minute;

    public DateContainer(String day, int hour, int minute) {
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        TimersManager.index.add(this);
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
