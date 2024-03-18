package com.example.ramadan1;

import java.util.ArrayList;
import java.util.List;

public class Sehri_iftari_class {

    public String date;
   static List<Sehri_iftari_class> data = new ArrayList<>();
    public String Sehri;
    public String Iftari;
    public  Sehri_iftari_class(String date, String Sehri, String Iftari, String Day, int id) {
        this.date = date;
        this.Sehri = Sehri;
        this.Iftari = Iftari;
        this.Day = Day;
        this.id = id;
    }
    public String getDate() {
        return date;
    }

    public String getSehri() {
        return Sehri;
    }

    public String getIftari() {
        return Iftari;
    }

    public String getDay() {
        return Day;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static List<Sehri_iftari_class> getData() {
        return data;
    }

    public static void setData(List<Sehri_iftari_class> data) {
        Sehri_iftari_class.data = data;
    }

    public void setSehri(String sehri) {
        Sehri = sehri;
    }

    public void setIftari(String iftari) {
        Iftari = iftari;
    }

    public void setDay(String day) {
        Day = day;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String Day;
    public int id;

}
