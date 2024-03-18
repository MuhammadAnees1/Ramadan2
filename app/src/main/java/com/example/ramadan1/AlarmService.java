package com.example.ramadan1;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.Calendar;
public class AlarmService extends Service {
//    final String TAG = "AlarmMe";
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i(TAG, "AlarmService.onStartCommand");
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.YEAR, 2024);
//        calendar.set(Calendar.MONTH, Calendar.MARCH);
//        calendar.set(Calendar.DAY_OF_MONTH, 7);
//        calendar.set(Calendar.HOUR_OF_DAY, 16);
//        calendar.set(Calendar.MINUTE, 46);
//        AlarmHelper.setupAlarmWithVibration(getApplicationContext(), calendar);
//        return Service.START_NOT_STICKY;
//    }
//
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
