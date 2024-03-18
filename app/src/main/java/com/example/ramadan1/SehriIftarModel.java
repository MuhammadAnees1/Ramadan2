package com.example.ramadan1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SehriIftarModel {
    private int id;
    private String sehriTime;
    private String iftarTime;
    private String date;
    private String day;

    public SehriIftarModel(int id, String sehriTime, String iftarTime, String date, String day) {
        this.id = id;
        this.sehriTime = sehriTime;
        this.iftarTime = iftarTime;
        this.date = date;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSehriTime() {
        return sehriTime;
    }

    public void setSehriTime(String sehriTime) {
        this.sehriTime = sehriTime;
    }

    public String getIftarTime() {
        return iftarTime;
    }

    public void setIftarTime(String iftarTime) {
        this.iftarTime = iftarTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    public Calendar getSehriCalendar() {
        return getCalendarFromTimeString(sehriTime);
    }

    public Calendar getIftarCalendar() {
        return getCalendarFromTimeString(iftarTime);
    }

    private Calendar getCalendarFromTimeString(String timeString) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date date = sdf.parse(timeString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
