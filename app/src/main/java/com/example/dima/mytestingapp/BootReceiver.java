package com.example.dima.mytestingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dima on 17.08.2017.
 *
 * Restart alarm if device is rebooted
 */

public class BootReceiver extends BroadcastReceiver {

    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private String mTitle;
    private String mTime;
    private String mDate;

    private String mRepeatType;
    int mId;
    String mType;
    int imgIcon;
    String iconName;

    private AlarmReceiver mAlarmReceiver;

    // Constant values in milliseconds
//    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private static final long milYear = 31536000000L;

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;



    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();

//        Извлечение Login
            sPrefLogin = context.getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
            login = sPrefLogin.getString("save_login", "");


            mCalendar = Calendar.getInstance();
            mAlarmReceiver = new AlarmReceiver();



            String sql = "SELECT * FROM " + DBHelper.TABLE_REMINDER + " WHERE " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";

            Cursor cursor = db.rawQuery(sql, new String[]{login});

            if (cursor.moveToFirst()) {
                int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                int typeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE);
                int nameIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME);
                int dateIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE);
                int timeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME);
                int repeatIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT);
                int iconImageIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER);
                int nameIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME);
                do {

                    mId = cursor.getInt(idKeyIndex);
                    mType = cursor.getString(typeIndex);
                    mTitle = cursor.getString(nameIndex);
                    mDate = cursor.getString(dateIndex);
                    mTime = cursor.getString(timeIndex);
                    mRepeatType = cursor.getString(repeatIndex);
                    imgIcon = cursor.getInt(iconImageIndex);
                    iconName = cursor.getString(nameIconIndex);

                    String dateFromDb = mDate + " " + mTime;

                    mDay = Integer.parseInt(dateFromDb.substring(0,2));
                    mMonth = Integer.parseInt(dateFromDb.substring(3,5));
                    mYear = Integer.parseInt(dateFromDb.substring(6,10));

                    mHour = Integer.parseInt(dateFromDb.substring(11,13));
                    mMinute = Integer.parseInt(dateFromDb.substring(14));

                    // Set up calender for creating the notification
                    mCalendar.set(Calendar.MONTH, --mMonth);
                    mCalendar.set(Calendar.YEAR, mYear);
                    mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                    mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                    mCalendar.set(Calendar.MINUTE, mMinute);
                    mCalendar.set(Calendar.SECOND, 0);

                    // Check repeat type
                    if (mRepeatType.equals("Ежечасно")) {
                        mRepeatTime = milHour;
                    } else if (mRepeatType.equals("Ежедневно")) {
                        mRepeatTime = milDay;
                    } else if (mRepeatType.equals("Еженедельно")) {
                        mRepeatTime = milWeek;
                    } else if (mRepeatType.equals("Ежемесячно")) {
                        mRepeatTime = milMonth;
                    } else if (mRepeatType.equals("Ежегодно")){
                        mRepeatTime = milYear;
                    }

                    // Create a new notification
                    if (mRepeatType.equals("Один раз")){
                        Log.d("myLogs", "setAlarm");
                        new AlarmReceiver().setAlarm(context, mCalendar, mId);
                    } else {
                        Log.d("myLogs", "setRepeatAlarm");
                        new AlarmReceiver().setRepeatAlarm(context, mCalendar, mId, mRepeatTime);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

}
