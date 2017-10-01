package com.example.dima.mytestingapp.Activitys;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

/**
 * Created by Dima on 15.08.2017.
 */

public class ReminderEditActivity extends AppCompatActivity {

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
    private String mRepeatInterval;
    private String mRepeatType;
    private String mActive;
    private int mReceivedID;
    String iconName = "Нет мркера";
    String remindImageIcon;
//    int imgIcon = R.mipmap.ic_no_icon_select;
    int imgIcon;
    int remindImgIcon;
    String chosenRingtone = "Стандартный";   //  File Uri
    String soundName;  //  Имя мелодии

    private AlarmReceiver mAlarmReceiver;

    // Constant Intent String
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";
    private static final String KEY_IMAGE_ICON = "image_icon";
    private static final String KEY_NAME_ICON = "name_icon";

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private static final long milYear = 31536000000L;

    int DIALOG_DATE = 1;
    int DIALOG_TIME = 2;

    int REQUEST_CODE_SOUND = 1;

    Calendar calendar = Calendar.getInstance();
    int myYear = calendar.get(Calendar.YEAR);
    int myMonth = calendar.get(Calendar.MONTH);
    int myDay = calendar.get(Calendar.DAY_OF_MONTH);

    int myHour = calendar.get(Calendar.HOUR_OF_DAY);
    int myMinute = calendar.get(Calendar.MINUTE);

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    Boolean btnIsApply;  // При входе в ReminderActivity если сразу нажать кнопку Ok, то - "отм. как вып.", иначе - редактирование

    String saveText;   //  Для проверки на изменения в EditText

    GridView gvIcons;
    SelectionGridAdapter adapter;

    int secondIcon = R.drawable.ic_second_icon_24dp;

    ArrayList<ItemIcons> listIcons;

    String action;

    Retrofit.Builder builder;
    Retrofit retrofit;
    ServerApi serverApi;

    String fileString = "";
    String[] s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);


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

        btnIsApply = true;

        mAlarmReceiver = new AlarmReceiver();

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

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
        getSupportActionBar().setTitle(R.string.title_activity_edit_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    //   Стрелка
//        toolbar.setNavigationIcon(R.drawable.ic_action_close);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderEditActivity.this, UserActivity.class);
                intent.putExtra("name_fragment", "freminder");
                startActivity(intent);
            }
        });



        // Initialize default values
        mActive = "true";
//        mRepeat = "true";
        mRepeatInterval = Integer.toString(1);
        mRepeatType = "Ежечасно";

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

        if (mMonth+1 < 10){
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

//        mDate = mDay + "." + mMonth + "." + mYear;
//        mTime = mHour + ":" + mMinute;


        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("myLogs", "beforeTextChanged count = " + count + " start = " + start + " after = " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
                mTitleText.setSelection(mTitleText.getText().length());  //  Курсор в конец строки

                Log.d("myLogs", "onTextChanged count = " + count + " start = " + start + " before = " + before);

//                Log.d("myLogs", "Text Changed");
//                btnIsApply = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("myLogs", "Editable s = " + s);
            }
        });

        // Get reminder id from intent
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));
        Log.d("myLogs", "mReceivedID Edit = " + mReceivedID);

//        String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
//                + "Просроченные" + "'" + " WHERE "
//                + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID;
//        db.execSQL(sql);


        String sql = "select " + DBHelper.KEY_REMINDER_ID + ", " + DBHelper.KEY_REMINDER_TYPE + ", "
                + DBHelper.KEY_REMINDER_NAME + ", " + DBHelper.KEY_REMINDER_DATE + ", "
                + DBHelper.KEY_REMINDER_TIME + ", " + DBHelper.KEY_REMINDER_REPEAT + ", "
                + DBHelper.KEY_REMINDER_IMG_MARKER + ", " + DBHelper.KEY_REMINDER_MARKER_NAME + ", "
                + DBHelper.KEY_REMINDER_SOUND
                + " from " + DBHelper.TABLE_REMINDER
                + " where " + DBHelper.KEY_REMINDER_ID + "=? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(mReceivedID), login});

        if (cursor.moveToFirst()) {
            int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
            int typeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE);
            int timeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME);
            int repeatIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT);
            int iconImageIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER);
            int soundUriIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_SOUND);
            int nameIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME);
            do {

                mTitle = cursor.getString(nameIndex);
                mDate = cursor.getString(dateIndex);
                mTime = cursor.getString(timeIndex);
//                mRepeat = cursor.getString(repeatIndex);
                mRepeatType = cursor.getString(repeatIndex);
                imgIcon = cursor.getInt(iconImageIndex);
                remindImgIcon = imgIcon;
                iconName = cursor.getString(nameIconIndex);
                remindImageIcon = iconName;
                chosenRingtone = cursor.getString(soundUriIndex);

            } while (cursor.moveToNext());
        }
        cursor.close();


        // Setup TextViews using reminder values
        mTitleText.setText(mTitle);
        saveText = mTitle;  //  Сохраняется заголовок для сравнения
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
//        mRepeatIntervalText.setText(mRepeatInterval);
        mRepeatTypeText.setText(mRepeatType);
        mIconNameText.setText(iconName);

        if (chosenRingtone.equals("Стандартный")){
            soundName = "Стандартный";
            chosenRingtone = "Стандартный";
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(chosenRingtone));
            soundName = ringtone.getTitle(this);
            Log.d("myLogs", "soundName = " + soundName);
        }

        mSoundNameText.setText(soundName);
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

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatIntervalText.setText(savedRepeatNo);
            mRepeatInterval = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            String savedNameIcon = savedInstanceState.getString(KEY_NAME_ICON);
            mIconNameText.setText(savedNameIcon);
            iconName = savedNameIcon;

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

        btnIsApply = false;
    }

    // On clicking Time picker
    public void setTime(View v){
        showDialog(DIALOG_TIME);

        btnIsApply = false;
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
        btnIsApply = false;

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

        btnIsApply = false;

        adapter = new SelectionGridAdapter(this, secondIcon, listIcons);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Выберите иконку");
        // Create EditText box to input repeat number
        gvIcons = new GridView(this);
        gvIcons.setAdapter(adapter);
        adjustGridView();

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
                    Toast.makeText(ReminderEditActivity.this, listIcons.get(position).getNameIcon(), Toast.LENGTH_SHORT).show();
                    iconName = listIcons.get(position).getNameIcon();
                    imgIcon = R.drawable.ic_transparent_round_24dp;
                    mIconNameText.setText(iconName);

                } else {
                    Toast.makeText(ReminderEditActivity.this, listIcons.get(position).getNameIcon(), Toast.LENGTH_SHORT).show();
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

                for (int i = 0; i < listIcons.size(); i++){
                    listIcons.get(i).setVisibility(View.INVISIBLE);
                }
                gvIcons.setAdapter(adapter);

                iconName = remindImageIcon;
                imgIcon = remindImgIcon;
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

        btnIsApply = false;

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, REQUEST_CODE_SOUND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        Log.d("myLogs", "resultCode = " + resultCode + " requestCode = " + requestCode + " intent = " + intent);
        Log.d("myLogs", "resultCode == RESULT_OK && requestCode == REQUEST_CODE_SOUND && intent!=null = " + (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SOUND && intent!=null));
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SOUND && intent!=null)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
            }
            else
            {
                this.chosenRingtone = null;
            }
        }

        if (chosenRingtone == null){
            soundName = "Стандартный";
            chosenRingtone = "Стандартный";
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(this, Uri.parse(chosenRingtone));
            soundName = ringtone.getTitle(this);
            Log.d("myLogs", "soundName = " + soundName);
        }

        mSoundNameText.setText(soundName);
    }





    // On clicking the update button
    public void updateReminder(){
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("name_fragment", "freminder");

        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();


        String type = null;

        Date date = new Date();  //  Текущая дата
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateNow = format.format(date);

        Log.d("myLogsN", "chosenRingtone = " + chosenRingtone);



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



            if (mDate.equals(dateNow)){
                type = "Сегодня";
            } else if (mDate.equals(tomorrowString)){
                type = "Завтра";
            } else if (dateReminder.compareTo(tomorrowDate) == 1){
                type = "Будущие";
            }


            Call<Void> callEditReminder = serverApi.editReminder(type, mTitle, mDate, mTime, mRepeatType,
                    String.valueOf(imgIcon), iconName, chosenRingtone, login, String.valueOf(mReceivedID));

            String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_NAME + " = '"
                    + mTitle + "', " + DBHelper.KEY_REMINDER_TYPE + " = '" + type + "', " + DBHelper.KEY_REMINDER_DATE + " = '"
                    + mDate + "', " + DBHelper.KEY_REMINDER_TIME + " = '"
                    + mTime + "', " + DBHelper.KEY_REMINDER_REPEAT + " = '"
                    + mRepeatType + "', " + DBHelper.KEY_REMINDER_IMG_MARKER + " = '"
                    + imgIcon + "', " + DBHelper.KEY_REMINDER_MARKER_NAME + " = '"
                    + iconName + "', " + DBHelper.KEY_REMINDER_SOUND + " = '" + chosenRingtone + "' WHERE "
                    + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                    + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
            db.execSQL(sql);

            callEditReminder.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                            + "yes" + "' WHERE " + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                            + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                    db.execSQL(sql);

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                            + "no" + "' WHERE " + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                            + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                    db.execSQL(sql);

//                    --------------------------- Запись ID в файл --------------------------------

                    try{
//            Сткрываем поток для чтения
                        BufferedReader br  = new BufferedReader(new InputStreamReader(
                                openFileInput("reminder_update.txt")));
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
                                openFileOutput("reminder_update.txt", MODE_PRIVATE)));
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





            String dateFromDb = mDate + " " + mTime;   //  23.08.2017 03:14

            mDay = Integer.parseInt(dateFromDb.substring(0,2));
            mMonth = Integer.parseInt(dateFromDb.substring(3,5));
            mYear = Integer.parseInt(dateFromDb.substring(6,10));

            mHour = Integer.parseInt(dateFromDb.substring(11,13));
            mMinute = Integer.parseInt(dateFromDb.substring(14));
            Log.d("myLogsN", "mDay = " + mDay + " mMonth = " + mMonth + " mYear = " + mYear
                    + " mHour = " + mHour + " mMinute = " + mMinute);


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
            } else if (mRepeatType.equals("Ежегодно")){
                mRepeatTime = milYear;
            }

            // Create a new notification
            if (mRepeatType.equals("Один раз")){
                Log.d("myLogs", "setAlarm");
                new AlarmReceiver().setAlarm(this, mCalendar, mReceivedID);
            } else {
                Log.d("myLogs", "setRepeatAlarm");
                new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
            }

            // Create toast to confirm update
            Toast.makeText(getApplicationContext(), "Изменено",
                    Toast.LENGTH_SHORT).show();

            intent = new Intent(ReminderEditActivity.this, UserActivity.class);
            intent.putExtra("name_fragment", "freminder");
            startActivity(intent);

            backPressedNew();

        } else {
            Toast.makeText(this, "Установите будущее время !!!", Toast.LENGTH_SHORT).show();
        }



    }

    public void backPressedNew(){
        super.onBackPressed();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ReminderEditActivity.this, UserActivity.class);
        intent.putExtra("name_fragment", "freminder");
        startActivity(intent);
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
                backPressedNew();
                return true;

            // On clicking save reminder button
            // Update reminder
            case R.id.save_reminder:
                Log.d("myLogsN", "saveText = " + saveText);
                mTitleText.setText(mTitle);
                Log.d("myLogsN", "mTitleText.getText() = " + mTitleText.getText());

//                Проверка на изменения в EditText
                if (!saveText.equals(mTitleText.getText().toString())){
                    btnIsApply = false;
                }



                if (mTitleText.getText().toString().length() == 0)
                    mTitleText.setError("Заполните поле ввода!!!");

                else {


//                        -*-*- Если тип повторения "Один раз", то удаляем сигнал повторения, если нет то сигнал будет повторяться -*-*-
                    String sql = "select " + DBHelper.KEY_REMINDER_REPEAT
                            + " from " + DBHelper.TABLE_REMINDER
                            + " where " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? and "
                            + DBHelper.KEY_REMINDER_ID + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, String.valueOf(mReceivedID)});

                    String reminderRepeatType = "";  //  Получаем тип повторения
                    if (cursor.moveToFirst()) {
                        do {
                            reminderRepeatType = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT));

                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    if (reminderRepeatType.equals("Один раз")){

                        Call<Void> editReminderType = serverApi.editReminderType("Выполненные", String.valueOf(mReceivedID), login, "Один раз");

                        mAlarmReceiver.cancelAlarm(getApplicationContext(), mReceivedID);

                        sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
                                + "Выполненные" + "'" + " WHERE "
                                + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                        db.execSQL(sql);

                        editReminderType.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                        + "yes" + "'" + " WHERE "
                                        + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                        + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                        + "no" + "'" + " WHERE "
                                        + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                        + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);




//                    --------------------------- Запись ID в файл --------------------------------

                                try{
//            Сткрываем поток для чтения
                                    BufferedReader br  = new BufferedReader(new InputStreamReader(
                                            openFileInput("reminder_update.txt")));
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
                                            openFileOutput("reminder_update.txt", MODE_PRIVATE)));
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
                    }

//                        ----------------------------------------------------------------------------



                    if (btnIsApply){

//                        --- Если тип повторения "Один раз", то удаляем сигнал повторения, если нет то сигнал будет повторяться ---
                        sql = "select " + DBHelper.KEY_REMINDER_REPEAT
                                + " from " + DBHelper.TABLE_REMINDER
                                + " where " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? and "
                                + DBHelper.KEY_REMINDER_ID + " =?";
                        cursor = db.rawQuery(sql, new String[]{login, String.valueOf(mReceivedID)});

                        reminderRepeatType = "";  //  Получаем тип повторения
                        if (cursor.moveToFirst()) {
                            do {
                                reminderRepeatType = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT));

                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        if (reminderRepeatType.equals("Один раз")){

                            Call<Void> editReminderType = serverApi.editReminderType("Выполненные", String.valueOf(mReceivedID), login, "Один раз");

                            mAlarmReceiver.cancelAlarm(getApplicationContext(), mReceivedID);

                            sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
                                    + "Выполненные" + "'" + " WHERE "
                                    + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                                    + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                            db.execSQL(sql);

                            editReminderType.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                    String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                            + "yes" + "'" + " WHERE "
                                            + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                            + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                                    db.execSQL(sql);

                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                    String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                            + "no" + "'" + " WHERE "
                                            + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID + " AND "
                                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                            + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                                    db.execSQL(sql);



//                    --------------------------- Запись ID в файл --------------------------------

                                    try{
//            Сткрываем поток для чтения
                                        BufferedReader br  = new BufferedReader(new InputStreamReader(
                                                openFileInput("reminder_update.txt")));
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
                                                openFileOutput("reminder_update.txt", MODE_PRIVATE)));
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
                        }

//                        ----------------------------------------------------------------------------


                        Toast.makeText(ReminderEditActivity.this, "Напоминание отмечено как выполнееное", Toast.LENGTH_SHORT).show();

//                        Intent intent = new Intent(ReminderEditActivity.this, UserActivity.class);
//                        intent.putExtra("name_fragment", "freminder");
//                        startActivity(intent);

                        Intent intent = new Intent(ReminderEditActivity.this, UserActivity.class);
                        intent.putExtra("name_fragment", "freminder");
                        startActivity(intent);

                        backPressedNew();
                    } else {
                        updateReminder();
                    }
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), "Отмена",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ReminderEditActivity.this, UserActivity.class);
                intent.putExtra("name_fragment", "freminder");
                startActivity(intent);

                backPressedNew();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
