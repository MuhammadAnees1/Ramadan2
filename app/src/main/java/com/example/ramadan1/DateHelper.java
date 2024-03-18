package com.example.ramadan1;

import com.ibm.icu.util.IslamicCalendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public static String getCurrentDateEnglish() {
        return new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(new Date());
    }

    public static String getCurrentDateIslamic() {
        IslamicCalendar islamicCalendar = new IslamicCalendar();
        String[] islamicMonths = {"Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani", "Jumada al-awwal", "Jumada al-thani",
                "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"};
        int islamicYear = islamicCalendar.get(IslamicCalendar.YEAR);

        return String.format(Locale.getDefault(), "%s %d, %d",
                islamicMonths[islamicCalendar.get(IslamicCalendar.MONTH)],
                islamicCalendar.get(IslamicCalendar.DAY_OF_MONTH),
                islamicYear);
    }
}
