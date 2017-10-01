package com.example.dima.mytestingapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dima.mytestingapp.Activitys.ReminderEditActivity;
import com.example.dima.mytestingapp.api.ServerApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dima on 15.08.2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    String mSound;

    int mReceivedID;

    String fileString = "";

    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;

        mReceivedID = Integer.parseInt(intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID));

//        --------------------------------------------------------------
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ServerApi serverApi = retrofit.create(ServerApi.class);
//        --------------------------------------------------------------


        dbHelper = new DBHelper(context);

        db = dbHelper.getWritableDatabase();

//        Извлечение Login
        sPrefLogin = context.getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");



        String sql = "select " + DBHelper.KEY_REMINDER_NAME + ", " + DBHelper.KEY_REMINDER_SOUND
                + " from " + DBHelper.TABLE_REMINDER
                + " where " + DBHelper.KEY_REMINDER_ID + "=? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(mReceivedID), login});

        String mTitle = null;
        mSound = null;
        if (cursor.moveToFirst()) {
            do {
                mTitle = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME));
                mSound = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SOUND));
                Log.d("myLogs", "mTitle = " + mTitle + " mSound = " + mSound);
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d("myLogs", "mTitle = " + mTitle);
        Log.d("myLogs", "mSound = " + mSound);
        Log.d("myLogs", "mReceivedID = " + mReceivedID);

        try{
            if (mSound.equals("Стандартный")){
                // Create intent to open ReminderEditActivity on notification click
                Intent editIntent = new Intent(context, ReminderEditActivity.class);
                editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Create Notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo))
                        .setSmallIcon(R.mipmap.ic_small_time_icon)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setTicker(mTitle)
                        .setContentText(mTitle)
                        .setContentIntent(mPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS).setAutoCancel(true)
                        .setOnlyAlertOnce(true);

                NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nManager.notify(mReceivedID, mBuilder.build());
            } else {
                // Create intent to open ReminderEditActivity on notification click
                Intent editIntent = new Intent(context, ReminderEditActivity.class);
                editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // Create Notification
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo))
                        .setSmallIcon(R.mipmap.ic_small_time_icon)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setTicker(mTitle)
                        .setContentText(mTitle)
                        .setSound(Uri.parse(mSound))
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(mPendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS).setAutoCancel(true)
                        .setOnlyAlertOnce(true);

                NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nManager.notify(mReceivedID, mBuilder.build());
            }
        } catch (java.lang.NullPointerException e){

            // Create intent to open ReminderEditActivity on notification click
            Intent editIntent = new Intent(context, ReminderEditActivity.class);
            editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Create Notification
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo))
                    .setSmallIcon(R.mipmap.ic_small_time_icon)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setTicker(mTitle)
                    .setContentText(mTitle)
                    .setContentIntent(mPendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS).setAutoCancel(true)
                    .setOnlyAlertOnce(true);

            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify(mReceivedID, mBuilder.build());

        }


        Call<Void> editReminderType = serverApi.editReminderType("Просроченные", String.valueOf(mReceivedID), login, "Один раз");

        sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
                + "Просроченные" + "'" + " WHERE "
                + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND " + DBHelper.KEY_REMINDER_REPEAT + " = '"
                + "Один раз" + "'";
        db.execSQL(sql);

        editReminderType.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                        + "yes" + "'" + " WHERE "
                        + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND " + DBHelper.KEY_REMINDER_REPEAT + " = '"
                        + "Один раз" + "' AND " + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                db.execSQL(sql);

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                        + "no" + "'" + " WHERE "
                        + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND " + DBHelper.KEY_REMINDER_REPEAT + " = '"
                        + "Один раз" + "' AND " + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                db.execSQL(sql);


//                    --------------------------- Запись ID в файл --------------------------------

                try{
//            Сткрываем поток для чтения
                    BufferedReader br  = new BufferedReader(new InputStreamReader(
                            ctx.openFileInput("reminder_update.txt")));
                    String str = "";
//            Читаем содержимое
                    while ((str = br.readLine()) != null){
                        fileString = str;
                    }
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }




                try{
//            Открываем поток для записи
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                            ctx.openFileOutput("reminder_update.txt", MODE_PRIVATE)));
//            Пишем данные
                    bw.write(fileString + " " + String.valueOf(mReceivedID));
//            Закрываем поток
                    bw.close();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


//                    -----------------------------------------------------------------------------


            }
        });


//        --------------------------------------------------------------------
//        Если у напоминания частота повторения не равна "Один раз", то оно не отмечается не в "Выполненные", не
//        в "Просроченые". Дата и время обновляются и напоминание реализуется повторно

//        Выбираем дату, время и частоту повторения
        sql = "select " + DBHelper.KEY_REMINDER_DATE + ", " + DBHelper.KEY_REMINDER_TIME + ", " + DBHelper.KEY_REMINDER_REPEAT + ", "
                + DBHelper.KEY_REMINDER_TYPE + ", " + DBHelper.KEY_REMINDER_IMG_MARKER + ", " + DBHelper.KEY_REMINDER_MARKER_NAME + ", "
                + DBHelper.KEY_REMINDER_SOUND
                + " from " + DBHelper.TABLE_REMINDER
                + " where " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? and "
                + DBHelper.KEY_REMINDER_ID + " =?";
        cursor = db.rawQuery(sql, new String[]{login, String.valueOf(mReceivedID)});

        String reminderDate = "";   //  Дата
        String reminderTime = "";  //  Время
        String reminderRepeat = "";  //  Тип повторения
        String reminderType = "";
        String reminderImgMarker = "";
        String reminderMarkerName = "";
        String reminderSound = "";
        if (cursor.moveToFirst()) {
            do {
                reminderDate = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE));
                reminderTime = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME));

                reminderRepeat = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT));


                reminderType = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE));
                reminderImgMarker = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER));
                reminderMarkerName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME));
                reminderSound = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SOUND));
            } while (cursor.moveToNext());
        }
        cursor.close();

        String reminderAllDateStr = reminderDate + " " + reminderTime;  //  Дата "полностью" полученная из БД
        Log.d("myLogsN", "Date = " + reminderAllDateStr);
        DateFormat formatDateAll = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        Date date = new Date();  //  Текущая дата
        String dateNow = formatDate.format(date);




        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_MONTH, +1);
        Date tomorrowDate = calendar.getTime();

        String tomorrowString = formatDate.format(tomorrowDate);   //  Завтрашняя дата





        Date reminderAllDate = null;

        try {
            reminderAllDate = formatDateAll.parse(reminderAllDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar = Calendar.getInstance();
        calendar.setTime(reminderAllDate);

        switch (reminderRepeat) {
            case "Ежечасно":
                calendar.add(Calendar.HOUR_OF_DAY, +1);
                break;
            case "Ежедневно":
                calendar.add(Calendar.DAY_OF_MONTH, +1);
                break;
            case "Еженедельно":
                calendar.add(Calendar.WEEK_OF_YEAR, +1);
                break;
            case "Ежемесячно":
                calendar.add(Calendar.MONTH, +1);
                break;
            case "Ежегодно":
                calendar.add(Calendar.YEAR, +1);
                break;
        }

        Date newDateDate = calendar.getTime();

        String newDateString = formatDateAll.format(newDateDate);

        String newDate = newDateString.substring(0, 10);
        String newTime = newDateString.substring(11);
        String type = "";

        if (newDate.equals(dateNow)){
            type = "Сегодня";
        } else if (newDate.equals(tomorrowString)){
            type = "Завтра";
        } else if (newDateDate.compareTo(tomorrowDate) == 1){
            type = "Будущие";
        }


        Call<Void> reminderRepeatNotOnes = serverApi.editReminderRepeatNotOnes(type, newDate, newTime, reminderImgMarker,
                reminderMarkerName, reminderSound, String.valueOf(mReceivedID), login, "Один раз");

        sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '" + type + "', "
                + DBHelper.KEY_REMINDER_DATE + " = '"
                + newDate + "', " + DBHelper.KEY_REMINDER_TIME + " = '"
                + newTime + "', " + DBHelper.KEY_REMINDER_TYPE + " ='" + reminderType + "', "
                + DBHelper.KEY_REMINDER_IMG_MARKER + " ='" + reminderImgMarker + "', "
                + DBHelper.KEY_REMINDER_MARKER_NAME + " ='" + reminderMarkerName + "', "
                + DBHelper.KEY_REMINDER_SOUND + " ='" + reminderSound + "'"
                + " WHERE "
                + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND " + DBHelper.KEY_REMINDER_REPEAT + " != '"
                + "Один раз" + "'";
        db.execSQL(sql);

        reminderRepeatNotOnes.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'yes'"
                        + " WHERE "
                        + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND " + DBHelper.KEY_REMINDER_REPEAT + " != '"
                        + "Один раз" + "' AND " + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                db.execSQL(sql);

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'no'"
                        + " WHERE "
                        + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND " + DBHelper.KEY_REMINDER_REPEAT + " != '"
                        + "Один раз" + "' AND " + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                db.execSQL(sql);


//                    --------------------------- Запись ID в файл --------------------------------

                try{
//            Сткрываем поток для чтения
                    BufferedReader br  = new BufferedReader(new InputStreamReader(
                            ctx.openFileInput("reminder_update.txt")));
                    String str = "";
//            Читаем содержимое
                    while ((str = br.readLine()) != null){
                        fileString = str;
                    }
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }




                try{
//            Открываем поток для записи
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                            ctx.openFileOutput("reminder_update.txt", MODE_PRIVATE)));
//            Пишем данные
                    bw.write(fileString + " " + String.valueOf(mReceivedID));
//            Закрываем поток
                    bw.close();
                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


//                    -----------------------------------------------------------------------------


            }
        });

//        sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '" + type + "', "
//                + DBHelper.KEY_REMINDER_DATE + " = '"
//                + newDate + "', " + DBHelper.KEY_REMINDER_TIME + " = '"
//                + newTime + "' WHERE "
//                + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
//                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND " + DBHelper.KEY_REMINDER_REPEAT + " != '"
//                + "Один раз" + "'";
//        db.execSQL(sql);


//        -------------------------------------------------------------------

    }

    public void setAlarm(Context context, Calendar calendar, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification time
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Start alarm using notification time
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                mPendingIntent);
//
        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void setRepeatAlarm(Context context, Calendar calendar, int ID, long RepeatTime) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d("myLogs", "RepeatTime = " + RepeatTime);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Calculate notification timein
        Calendar c = Calendar.getInstance();
        long currentTime = c.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Start alarm using initial notification time and repeat interval time
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime,
                RepeatTime, mPendingIntent);   //  60000L

//         Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    //    Для закрытия одного элемента
    public void cancelAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(ID);

        // Disable alarm
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    //    Для закрытия нескольких элементов
    public void cancelAlarm(Context context, ArrayList<Integer> ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < ID.size(); i++){
            // Cancel Alarm using Reminder ID
            mPendingIntent = PendingIntent.getBroadcast(context, ID.get(i), new Intent(context, AlarmReceiver.class), 0);
            mAlarmManager.cancel(mPendingIntent);

            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.cancel(ID.get(i));

            // Disable alarm
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        }
    }


}
