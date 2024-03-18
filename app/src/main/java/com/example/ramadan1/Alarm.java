package com.example.ramadan1;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Alarm implements Comparable<Alarm> {
    Context mContext;
    private long mId;
    private String mTitle;
    private long mDate;
    private boolean mEnabled;
    private int mOccurence;
    private int mDays;
    private long mNextOccurence;
    public static final int ONCE = 0;
    public Alarm(Context context) {
        mContext = context;
        mId = 0;
        mTitle = "Default Title";
        mDate = System.currentTimeMillis();
        mEnabled = true;
        mOccurence = ONCE;


    }
    public long getId()
    {
        return mId;
    }

    public void setId(long id)
    {
        mId = id;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }
    public long getDate()
    {
        return mDate;
    }
    public long getNextOccurence()
    {
        return mNextOccurence;
    }

    public int compareTo(Alarm aThat)
    {
        final long thisNext = getNextOccurence();
        final long thatNext = aThat.getNextOccurence();
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == aThat)
            return EQUAL;

        if (thisNext > thatNext)
            return AFTER;
        else if (thisNext < thatNext)
            return BEFORE;
        else
            return EQUAL;
    }


    public void toIntent(Intent intent)
    {
        intent.putExtra("com.example.ramadan1.id", mId);
        intent.putExtra("com.example.ramadan1.title", mTitle);
        intent.putExtra("com.example.ramadan1.date", mDate);
        intent.putExtra("com.example.ramadan1.alarm", mEnabled);
        intent.putExtra("com.example.ramadan1.occurence", mOccurence);
        intent.putExtra("com.example.ramadan1.days", mDays);
    }

    public void fromIntent(Intent intent) {
        mId = intent.getLongExtra("com.example.ramadan1.id", 0);
        mTitle = intent.getStringExtra("com.example.ramadan1.title");

        // Set a default title if the title is null
        if (mTitle == null) {
            mTitle = "Default Title";
        }

        mDate = intent.getLongExtra("com.example.ramadan1.date", 0);
        mEnabled = intent.getBooleanExtra("com.example.ramadan1.alarm", true);
        mOccurence = intent.getIntExtra("com.example.ramadan1.occurence", 0);
        mDays = intent.getIntExtra("com.example.ramadan1.days", 0);
    }
}