package com.example.dima.mytestingapp.Activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dima.mytestingapp.Adapters.SelectionGridAdapter;
import com.example.dima.mytestingapp.AlarmReceiver;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.ItemIcons;
import com.example.dima.mytestingapp.R;
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

public class AddReminderActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mTitleText;
    private TextView mDateText, mTimeText, mRepeatIntervalText, mRepeatTypeText, mIconNameText, mSoundNameText;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private String mTitle;
    private String mTime;
    private String mDate;
    //    private String mRepeat;
//    private String mRepeatInterval;
    private String mRepeatType;
    private String mActive;
    String chosenRingtone = "Стандартный";   //  File Uri

    String soundName;  //  Имя звука

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    //    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";
    private static final String KEY_IMAGE_ICON = "image_icon";
    private static final String KEY_NAME_ICON = "name_icon";

    // Constant values in milliseconds
//    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private static final long milYear = 31536000000L;

    int DIALOG_DATE = 1;
    int DIALOG_TIME = 2;

    Calendar calendar = Calendar.getInstance();
    int myYear = calendar.get(Calendar.YEAR);
    int myMonth = calendar.get(Calendar.MONTH);
    int myDay = calendar.get(Calendar.DAY_OF_MONTH);

    int myHour = calendar.get(Calendar.HOUR_OF_DAY);
    int myMinute = calendar.get(Calendar.MINUTE);

    GridView gvIcons;
    SelectionGridAdapter adapter;

    String iconName = "Нет мркера";
//    int imgIcon = R.mipmap.ic_no_icon_select;
//    int imgIcon = 123;
    int imgIcon = R.drawable.ic_transparent_round_24dp;

    int secondIcon = R.drawable.ic_second_icon_24dp;

//    String[] names = new String[]{"Нет мркера", "Маркер 1", "Маркер 2", "Маркер 3", "Маркер 4", "Маркер 5", "Маркер 6"};

    ArrayList<ItemIcons> listIcons;

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    long ID;

    String fileString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        listIcons = new ArrayList<>();
        listIcons.add(new ItemIcons("Нет мркера", R.mipmap.ic_empty_icon, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 1", R.mipmap.ic_marker_red, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 2", R.mipmap.ic_marker_green, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 3", R.mipmap.ic_marker_blue, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 4", R.mipmap.ic_marker_yellow, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 5", R.mipmap.ic_marker_brown, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 6", R.mipmap.ic_marker_orange, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 7", R.mipmap.ic_marker_purple, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 8", R.mipmap.ic_marker_black, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 9", R.mipmap.ic_marker_bl_blue, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 10", R.mipmap.ic_marker_pinc, secondIcon, View.INVISIBLE));
        listIcons.add(new ItemIcons("Маркер 11", R.mipmap.ic_marker_light_green, secondIcon, View.INVISIBLE));


//        Извлечение Login
        sPrefLogin = getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");


        // Initialize Views
//        mToolbar = (Toolbar) findViewById(R.id.toolbar_add_reminder);
        mTitleText = (EditText) findViewById(R.id.edTitle);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
//        mRepeatText = (TextView) findViewById(R.id.set_repeat);
//        mRepeatIntervalText = (TextView) findViewById(R.id.tvRepeatInterval);
        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        mIconNameText = (TextView) findViewById(R.id.set_repeat_icon);
        mSoundNameText = (TextView) findViewById(R.id.set_sound);

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialize default values
        mActive = "true";
//        mRepeat = "true";
//        mRepeatInterval = Integer.toString(1);
        mRepeatType = "Один раз";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

//        -----------------------------------------------
        String strDay = "", strMonth = "";  String strHour = ""; String strMinute = "";

        if (mDay < 10){
//            String date = "0" + dayOfMonth + "." + (month+1) + "." + year;
            strDay = "0" + mDay;
        } else {
            strDay = mDay + "";
        }

        if (mMonth < 10){
//            strMonth = "0" + (month+1);
            strMonth = "0" + mMonth;
        } else {
            strMonth = mMonth + "";
        }

        if (mHour < 10){
            strHour = "0" + mHour;
        } else {
            strHour = mHour + "";
        }

        if (mMinute < 10){
            strMinute = "0" + mMinute;
        } else {
            strMinute = mMinute + "";
        }

//        --------------------------------------------------

//        mDate = mDay + "." + mMonth + "." + mYear;
        mDate = strDay + "." + strMonth + "." + mYear;
//        mTime = mHour + ":" + mMinute;
        mTime = strHour + ":" + strMinute;

        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup TextViews using reminder values
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
//        mRepeatIntervalText.setText(mRepeatInterval);
        mRepeatTypeText.setText(mRepeatType);
        mIconNameText.setText("Нет маркера");
//        mRepeatText.setText("Каждый(е) " + mRepeatInterval + " " + mRepeatType);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

//            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
//            mRepeatText.setText(saveRepeat);
//            mRepeat = saveRepeat;

//            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
//            mRepeatIntervalText.setText(savedRepeatNo);
//            mRepeatInterval = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            String savedNameIcon = savedInstanceState.getString(KEY_NAME_ICON);
            mIconNameText.setText(savedNameIcon);
            iconName = savedNameIcon;
            mSoundNameText.setText("Стандартный");

            int imgIcon = savedInstanceState.getInt(KEY_IMAGE_ICON);

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }


    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
//        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
//        outState.putCharSequence(KEY_REPEAT_NO, mRepeatIntervalText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
        outState.putCharSequence(KEY_NAME_ICON, mIconNameText.getText());
    }


    // On clicking Date picker
    public void setDate(View v){
        showDialog(DIALOG_DATE);
    }

    // On clicking Time picker
    public void setTime(View v){
        showDialog(DIALOG_TIME);
    }

    //    --------------- Методы для диалога даты -----------------------------------------
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_DATE){
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBackDate,
                    myYear, myMonth, myDay);
            return tpd;
        }

        if (id == DIALOG_TIME){
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBackTime,
                    myHour, myMinute, true);
            return tpd;
        }

        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBackDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month,
                              int dayOfMonth) {

            String strDay = "", strMonth = "";

            if (dayOfMonth < 10){
//            String date = "0" + dayOfMonth + "." + (month+1) + "." + year;
                strDay = "0" + dayOfMonth;
            } else {
                strDay = dayOfMonth + "";
            }

            if (month+1 < 10){
//            strMonth = "0" + (month+1);
                strMonth = "0" + (month+1);
            } else {
                strMonth = (month+1) + "";
            }

            month ++;
            mDay = dayOfMonth;
            mMonth = month;
            mYear = year;


//            String date = strDay + "." + strMonth + "." + year;
            mDate = strDay + "." + strMonth + "." + year;
            Log.d("myLogs", "day = " + dayOfMonth + " month = " + (month+1) + " year = " + year);

            mDateText.setText(mDate);
        }
    };
//    ---------------------------------------------------------------------------------

//    -------------------------- Методы для диалога времени ---------------------------

    TimePickerDialog.OnTimeSetListener myCallBackTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            myHour = hourOfDay;
//            myMinute = minute;
//            if (hourOfDay < 10 && minute < 10) {
//                mTime = "0" + hourOfDay + ":" + "0" + minute;
//            } else {
//                mTime = hourOfDay + ":" + minute;
//            }

            mHour = hourOfDay;
            mMinute = minute;



            String strHour = "";   String strMinute = "";

            if (mHour < 10){
                strHour = "0" + mHour;
            } else {
                strHour = mHour + "";
            }

            if (mMinute < 10){
                strMinute = "0" + mMinute;
            } else {
                strMinute = mMinute + "";
            }

            mTime = strHour + ":" + strMinute;

            mTimeText.setText(mTime);
        }
    };

//    ---------------------------------------------------------------------------------

    // On clicking repeat type button
    public void selectRepeatType(View v){
//    Название типов повторения
        final String[] typesRepeating = new String[]{"Один раз", "Ежечасно", "Ежедневно", "Еженедельно", "Ежемесячно", "Ежегодно"};

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите тип повторения");
        builder.setItems(typesRepeating, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = typesRepeating[item];
                mRepeatTypeText.setText(mRepeatType);
//                mRepeatText.setText("Каждый(е) " + mRepeatNo + " " + mRepeatType);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectIcon(View v){

        adapter = new SelectionGridAdapter(this, secondIcon, listIcons);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Выберите иконку");
        // Create EditText box to input repeat number
        gvIcons = new GridView(this);
        gvIcons.setAdapter(adapter);
        adjustGridView();

//        Отметка галочки по умолчанию
        for (int i = 0; i < listIcons.size(); i++){
            if (listIcons.get(i).getNameIcon().equals(iconName)){
                listIcons.get(i).setVisibility(View.VISIBLE);
            }
        }
        gvIcons.setAdapter(adapter);

        gvIcons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i < listIcons.size(); i++){
                    if (i == position){
                        listIcons.get(i).setVisibility(View.VISIBLE);
                    } else {
                        listIcons.get(i).setVisibility(View.INVISIBLE);
                    }
                }
                gvIcons.setAdapter(adapter);



                if (position == 0){
                    Toast.makeText(AddReminderActivity.this, listIcons.get(position).getNameIcon(), Toast.LENGTH_SHORT).show();
                    iconName = listIcons.get(position).getNameIcon();
                    imgIcon = R.drawable.ic_transparent_round_24dp;
                    mIconNameText.setText(iconName);

                } else {
                    Toast.makeText(AddReminderActivity.this, listIcons.get(position).getNameIcon(), Toast.LENGTH_SHORT).show();
                    iconName = listIcons.get(position).getNameIcon();
                    imgIcon = listIcons.get(position).getImgIcon();
                    mIconNameText.setText(iconName);
                }

            }
        });

        alert.setView(gvIcons);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {


                    }
                });
        alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                iconName = "Нет мркера";
                imgIcon = R.drawable.ic_transparent_round_24dp;
                mIconNameText.setText(iconName);
                gvIcons.setAdapter(adapter);

            }
        });
        alert.show();
    }


    private void adjustGridView(){
        gvIcons.setNumColumns(4);
//        gvIcons.setNumColumns(GridView.AUTO_FIT);
        gvIcons.setColumnWidth(60);
//        gvIcons.setVerticalSpacing(5);
//        gvIcons.setHorizontalSpacing(5);
//        gvMain.setStretchMode(GridView.NO_STRETCH);

        gvIcons.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);

//        gvMain.setStretchMode(GridView.STRETCH_SPACING);

//        gvIcons.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
    }


    public void selectSound(View v){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Log.d("myLogs", "Uri = " + uri);

            if (uri != null)
            {
                chosenRingtone = uri.toString();
            }
            else
            {
                chosenRingtone = null;
            }
        }

        Log.d("myLogs", "chosenRingtone = " + chosenRingtone + " soundName = " + soundName);


        if (chosenRingtone == null){
            soundName = "Стандартный";
            chosenRingtone = "Стандартный";
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(chosenRingtone));
            soundName = ringtone.getTitle(this);
            Log.d("myLogs", "soundName = " + soundName);
        }

        Log.d("myLogs", "soundName = " + soundName);


        mSoundNameText.setText(soundName);
    }


    // On clicking the save button
    public void saveReminder() {


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ServerApi serverApi = retrofit.create(ServerApi.class);




        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("name_fragment", "freminder");

        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

        Log.d("myLogsN", "chosenRingtone = " + chosenRingtone);


        String type = null;

        Date date = new Date();  //  Текущая дата
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateNow = format.format(date);


//        ---------------------------------------------------------------
        String mySelectedDateString = mDate + " " + mTime;
        Log.d("myLogs", "Date = " + mySelectedDateString);
        DateFormat formatDateAll = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String dtNow = formatDateAll.format(date);

        Date mySelectedDate = null;

        try {
            mySelectedDate = formatDateAll.parse(mySelectedDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        if (mySelectedDate.compareTo(date) == 1){
//            Log.d("myLogs", "Date is After");
//        } else if (mySelectedDateString.equals(dtNow)){
//            Log.d("myLogs", "Date are Equals");
//        } else if (mySelectedDate.compareTo(date) == -1){
//            Log.d("myLogs", "Date is Before");
//        }

        if (mySelectedDate.compareTo(date) == 1) {
            Log.d("myLogs", "Date is After");

            Date dateReminder = null;
            try {
                dateReminder = format.parse(mDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

//        calendar.setTime(date);
            calendar = Calendar.getInstance();


            calendar.add(Calendar.DAY_OF_MONTH, +1);
            Date tomorrowDate = calendar.getTime();

            String tomorrowString = format.format(tomorrowDate);

            Log.d("myLogs", "dateNow = " + dateNow + " mDate = " + mDate + " tomorrowString = " + tomorrowString);


            if (mDate.equals(dateNow)) {
                type = "Сегодня";
            } else if (mDate.equals(tomorrowString)) {
                type = "Завтра";
            } else if (dateReminder.compareTo(tomorrowDate) == 1) {
                type = "Будущие";
            }


            ContentValues contentValues = new ContentValues();


            contentValues.put(DBHelper.KEY_REMINDER_TYPE, type);
            contentValues.put(DBHelper.KEY_REMINDER_NAME, mTitle);
            contentValues.put(DBHelper.KEY_REMINDER_DATE, mDate);
            contentValues.put(DBHelper.KEY_REMINDER_TIME, mTime);
            contentValues.put(DBHelper.KEY_REMINDER_REPEAT, mRepeatType);
            contentValues.put(DBHelper.KEY_REMINDER_IMG_MARKER, imgIcon);
            contentValues.put(DBHelper.KEY_REMINDER_MARKER_NAME, iconName);
            contentValues.put(DBHelper.KEY_REMINDER_SOUND, chosenRingtone);
            contentValues.put(DBHelper.KEY_REMINDER_REF_LOGIN, login);
            contentValues.put(DBHelper.KEY_REMINDER_SYNCHRONISE, "ins");

            ID = db.insert(DBHelper.TABLE_REMINDER, null, contentValues);




            Call<Void> callAddReminder = serverApi.addReminder(type, mTitle, mDate, mTime, mRepeatType,
                    String.valueOf(imgIcon), iconName, chosenRingtone, login, String.valueOf(ID));

            callAddReminder.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    String sql = "update " + DBHelper.TABLE_REMINDER + " set " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'yes' where "
                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' and "
                            + DBHelper.KEY_REMINDER_ID + " = '" + ID + "' and "
                            + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                    db.execSQL(sql);

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    String sql = "update " + DBHelper.TABLE_REMINDER + " set " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'ins' where "
                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' and "
                            + DBHelper.KEY_REMINDER_ID + " = '" + ID + "' and "
                            + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                    db.execSQL(sql);



//                    ------------------ Запись ID в файл ----------------------

                    try{
//            Сткрываем поток для чтения
                        BufferedReader br  = new BufferedReader(new InputStreamReader(
                                openFileInput("reminder_insert.txt")));
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
                                openFileOutput("reminder_insert.txt", MODE_PRIVATE)));
//            Пишем данные
                        bw.write(fileString + " " + String.valueOf(ID));
//            Закрываем поток
                        bw.close();
                    } catch (FileNotFoundException e){
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    ----------------------------------------------------------------



                }
            });



            // Set up calender for creating the notification
            mCalendar.set(Calendar.MONTH, --mMonth);
            mCalendar.set(Calendar.YEAR, mYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
            mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            mCalendar.set(Calendar.MINUTE, mMinute);
            mCalendar.set(Calendar.SECOND, 0);

            Log.d("myLogs", "mRepeatType = " + mRepeatType);
            Log.d("myLogs", "mRepeatTime = " + mRepeatTime);

//        // Check repeat type
            if (mRepeatType.equals("Ежечасно")) {
                mRepeatTime = milHour;
            } else if (mRepeatType.equals("Ежедневно")) {
                mRepeatTime = milDay;
            } else if (mRepeatType.equals("Еженедельно")) {
                mRepeatTime = milWeek;
            } else if (mRepeatType.equals("Ежемесячно")) {
                mRepeatTime = milMonth;
            } else if (mRepeatType.equals("Ежегодно")) {
                mRepeatTime = milYear;
            }

            // Create a new notification
            if (mRepeatType.equals("Один раз")) {
                Log.d("myLogs", "setAlarm");
                new AlarmReceiver().setAlarm(this, mCalendar, (int) ID);
            } else {
                Log.d("myLogs", "setRepeatAlarm");
                new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendar, (int) ID, mRepeatTime);
            }

            // Create toast to confirm new reminder
            Toast.makeText(getApplicationContext(), "Сохранено",
                    Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK, intent);

            onBackPressed();
        } else {
            Toast.makeText(this, "Установите будущее время !!!", Toast.LENGTH_SHORT).show();
        }


//        ------------------------------------------------------------------

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // On clicking the back arrow
            // Discard any changes
            case android.R.id.home:
                onBackPressed();
                return true;

            // On clicking save reminder button
            // Update reminder
            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if (mTitleText.getText().toString().length() == 0)
//                    mTitleText.setError("Reminder Title cannot be blank!");
                    mTitleText.setError("Напоминание не задано!!!");

                else {
                    saveReminder();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), "Отменено",
                        Toast.LENGTH_SHORT).show();

                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
