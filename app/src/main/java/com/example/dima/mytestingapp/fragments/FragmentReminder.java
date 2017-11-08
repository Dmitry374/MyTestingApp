package com.example.dima.mytestingapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Activitys.AddReminderActivity;
import com.example.dima.mytestingapp.Activitys.LoginActivity;
import com.example.dima.mytestingapp.Activitys.ReminderEditActivity;
import com.example.dima.mytestingapp.Activitys.UserActivity;
import com.example.dima.mytestingapp.Adapters.ExpandableListAdapterList;
import com.example.dima.mytestingapp.AlarmReceiver;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.ItemChildReminder;
import com.example.dima.mytestingapp.Items.ItemServerReminder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentReminder.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentReminder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentReminder extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String ARG_ACTION = "action";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String action;

    String[] titles = new String[]{"Выполненные", "Просроченные", "Сегодня", "Завтра", "Будущие"};


    //    Заголовки групп
    ArrayList<String> groupList;
    //    Содержит категорию
    ArrayList<ItemChildReminder> childList;
    //    Коллекция элементов подгрупп
    Map<String, ArrayList<ItemChildReminder>> childCollection;

    //    Коллекции для накопления
    ArrayList<ItemChildReminder> listExecuted;
    ArrayList<ItemChildReminder> listOverdue;
    ArrayList<ItemChildReminder> listToday;
    ArrayList<ItemChildReminder> listTomorrow;
    ArrayList<ItemChildReminder> listFuture;


    ExpandableListView elvReminders;

    TextView noReminderView;

    ContentValues contentValues;

    final int REQUEST_CODE_LIST = 1;


//    Cursor cursor;

    DBHelper dbHelper;
    SQLiteDatabase db;

    final int MENU_REMINDER_EDIT = 1;
    final int MENU_REMINDER_DELETE = 2;
    final int MENU_REMINDER_DONE = 3;

    final int MENU_REMINDER_DELETE_ALL = 4;
    final int MENU_REMINDER_DONE_ALL = 5;

    String groupName;  //   Название группы выбранног id
    String itemTitle;  //  Название элемента
    String itemType;  //  Тип элемента - просроченные, выполнеено и т.д.
    int itemId;  //  id дочернего элемента списка

    private AlarmReceiver mAlarmReceiver;  //   Класс уведомлений

    AlertDialog.Builder ad;   //  Диалог

    FloatingActionButton fab;

    SharedPreferences sPrefLogin;

    String login;

    View view;

    Retrofit.Builder builder;
    Retrofit retrofit;
    ServerApi serverApi;

    int id;

    String fileString = "";

    SwipeRefreshLayout mSwipeRefreshLayout;

    private long mRepeatTime;

    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private static final long milYear = 31536000000L;



    private OnFragmentInteractionListener mListener;

    public FragmentReminder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentReminder.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentReminder newInstance(String text) {
        FragmentReminder fragment = new FragmentReminder();
        Bundle mBundle = new Bundle();
        mBundle.putString("FragmentReminder", text);
        fragment.setArguments(mBundle);
        return fragment;
    }

//    public static FragmentReminder newInstance(String myAction) {
//        FragmentReminder fragment = new FragmentReminder();
//        Bundle args = new Bundle();
//
//        args.putString(ARG_ACTION, myAction);
//
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            action = getArguments().getString(ARG_ACTION);
            Log.d("myLogs", "actionFragment = " + action);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Имя Toolbar
        Activity activity = this.getActivity();
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setTitle("Напоминалка");
        }

        view = inflater.inflate(R.layout.fragment_reminder, null);

        mAlarmReceiver = new AlarmReceiver();

        dbHelper = new DBHelper(getActivity());

        db = dbHelper.getWritableDatabase();

        noReminderView = (TextView) view.findViewById(R.id.no_reminder_text);

        fab = (FloatingActionButton) view.findViewById(R.id.fab_reminder);

//        Извлечение Login
        sPrefLogin = getActivity().getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");




        builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);


        createThisFragm();


        //        Обновление Activity
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.reminder_swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadReminderFromServer();

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.red);


        // Inflate the layout for this fragment
        return view;
    }


    private void loadReminderFromServer() {

        contentValues = new ContentValues();

//                Загрузка данных из сервера
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);

//                --------------------------- Закрываем все уведомления ----------------------
        AlarmReceiver mAlarmReceiver = new AlarmReceiver();

        ArrayList<Integer> itemIds = new ArrayList<>();

        String sql = "SELECT " + DBHelper.KEY_REMINDER_ID + " FROM " + DBHelper.TABLE_REMINDER
                + " WHERE " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{login});

        if (cursor.moveToFirst()) {
            int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
            do {
                itemIds.add(cursor.getInt(idKeyIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();

        mAlarmReceiver.cancelAlarm(getActivity(), itemIds);
//                ----------------------------------------------------------------------------


        sql = "delete from " + DBHelper.TABLE_REMINDER;
        db.execSQL(sql);


//                        --------------------------- Reminders ------------------------------------

        Call<List<ItemServerReminder>> callReminders = serverApi.loadReminders(login);

        callReminders.enqueue(new Callback<List<ItemServerReminder>>() {
            @Override
            public void onResponse(Call<List<ItemServerReminder>> call, Response<List<ItemServerReminder>> response) {

                String type = "", name = "", date = "", time = "", repeat = "",
                        imgMarker = "", imgMarkerName = "", sound = "", refLogin = "", localId = "";

//                            Прием данных обратно с сервера
                for (int i = 0; i < response.body().size(); i++){
                    localId = response.body().get(i).getLocalId();
                    type = response.body().get(i).getType();
                    name = response.body().get(i).getName();
                    date = response.body().get(i).getDate();
                    time = response.body().get(i).getTime();
                    repeat = response.body().get(i).getRepeat();
                    imgMarker = response.body().get(i).getImgMarker();
                    imgMarkerName = response.body().get(i).getImgMarkerName();
                    sound = response.body().get(i).getSound();

                    contentValues.put(DBHelper.KEY_REMINDER_ID, localId);
                    contentValues.put(DBHelper.KEY_REMINDER_TYPE, type);
                    contentValues.put(DBHelper.KEY_REMINDER_NAME, name);
                    contentValues.put(DBHelper.KEY_REMINDER_DATE, date);
                    contentValues.put(DBHelper.KEY_REMINDER_TIME, time);
                    contentValues.put(DBHelper.KEY_REMINDER_REPEAT, repeat);
                    contentValues.put(DBHelper.KEY_REMINDER_IMG_MARKER, imgMarker);
                    contentValues.put(DBHelper.KEY_REMINDER_MARKER_NAME, imgMarkerName);
                    contentValues.put(DBHelper.KEY_REMINDER_SOUND, sound);
                    contentValues.put(DBHelper.KEY_REMINDER_REF_LOGIN, login);
                    contentValues.put(DBHelper.KEY_REMINDER_SYNCHRONISE, "yes");

                    long id = db.insert(DBHelper.TABLE_REMINDER, null, contentValues);

//                                    ------------------- Восстановление сигнала -------------

                    Date dateDt = new Date();  //  Текущая дата
                    String strDate = date + " " + time;
                    DateFormat formatDateAll = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

                    Date dateDbDate = null;

                    try {
                        dateDbDate = formatDateAll.parse(strDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

//                                    Восстанавливаются только будуще сигналы

                    if (dateDbDate.compareTo(dateDt) == 1) {
                        Log.d("myLogs", "Date is After");


                        int mDay = Integer.parseInt(date.substring(0,2));
                        int mMonth = Integer.parseInt(date.substring(3,5));
                        int mYear = Integer.parseInt(date.substring(6,10));

                        int mHour = Integer.parseInt(time.substring(0,2));
                        int mMinute = Integer.parseInt(time.substring(3,5));

                        Calendar mCalendar = Calendar.getInstance();

                        // Set up calender for creating the notification
                        mCalendar.set(Calendar.MONTH, --mMonth);
                        mCalendar.set(Calendar.YEAR, mYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                        mCalendar.set(Calendar.MINUTE, mMinute);
                        mCalendar.set(Calendar.SECOND, 0);


                        // Check repeat type
                        if (repeat.equals("Ежечасно")) {
                            mRepeatTime = milHour;
                        } else if (repeat.equals("Ежедневно")) {
                            mRepeatTime = milDay;
                        } else if (repeat.equals("Еженедельно")) {
                            mRepeatTime = milWeek;
                        } else if (repeat.equals("Ежемесячно")) {
                            mRepeatTime = milMonth;
                        } else if (repeat.equals("Ежегодно")){
                            mRepeatTime = milYear;
                        }

                        // Create notifications
                        if (repeat.equals("Один раз")){
                            Log.d("myLogs", "setAlarm");
                            new AlarmReceiver().setAlarm(getActivity(), mCalendar, (int) id);
                        } else {
                            Log.d("myLogs", "setRepeatAlarm");
                            new AlarmReceiver().setRepeatAlarm(getActivity(), mCalendar, (int) id, mRepeatTime);
                        }


                    }
//                                    ---------------------------------------------

                    contentValues.clear();
                }

                createThisFragm();

            }

            @Override
            public void onFailure(Call<List<ItemServerReminder>> call, Throwable t) {

            }
        });

//                        --------------------- Завершение загрузки с сервера --------------------------------------------------


    }


    public void createThisFragm() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent intent = new Intent(getActivity(), AddReminderActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LIST);

//                -----------------------------------------------------------------------------------------
//                // Create new fragment and transaction
//                Fragment fkredit = new FragmentKreditCalc();
//                // consider using Java coding conventions (upper first char class names!!!)
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//                // Replace whatever is in the fragment_container view with this fragment,
//                // and add the transaction to the back stack
//                transaction.replace(R.id.content_user, fkredit);
//                transaction.addToBackStack(null);
//
//                Bundle bundle = new Bundle();
//                bundle.putString("key", "Hello");
//                fkredit.setArguments(bundle);
//
//                // Commit the transaction
//                transaction.commit();
//                -------------------------------------------------------------------------------------------

            }
        });

        String sql = "SELECT * FROM " + DBHelper.TABLE_REMINDER + " WHERE " + DBHelper.KEY_REMINDER_REF_LOGIN
                + " =? AND " + DBHelper.KEY_REMINDER_SYNCHRONISE + " != ?";
        Cursor cursor = db.rawQuery(sql, new String[]{login, "deleted"});

        Log.d("myLogs", "cursor.getCount() = " + cursor.getCount());

        if (cursor.getCount() == 0) {
            noReminderView.setVisibility(View.VISIBLE);
        } else {
            noReminderView.setVisibility(View.INVISIBLE);
        }
        cursor.close();

//        ------------------- Если был перевод часов вперед даты сместятся  ------------------------

//        String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
//                + "Выполненные" + "'" + " WHERE "
//                + DBHelper.KEY_REMINDER_ID + " = " + mReceivedID;
//        db.execSQL(sql);

        String type = null;
        Date date = new Date();  //  Текущая дата
        DateFormat formatDateAll = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateNow = formatDateAll.format(date);

//        sql = "select " + DBHelper.KEY_REMINDER_ID + ", " + DBHelper.KEY_REMINDER_TYPE + ", "
//                + DBHelper.KEY_REMINDER_NAME + ", " + DBHelper.KEY_REMINDER_DATE + ", "
//                + DBHelper.KEY_REMINDER_TIME + ", " + DBHelper.KEY_REMINDER_REPEAT
//                + " from " + DBHelper.TABLE_REMINDER + " WHERE " + DBHelper.KEY_REMINDER_TYPE + " != ? AND "
//                + DBHelper.KEY_REMINDER_TYPE + " != ? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
//        cursor = db.rawQuery(sql, new String[]{"Выполненные", "Просроченные", login});

        sql = "SELECT * FROM " + DBHelper.TABLE_REMINDER + " WHERE " + DBHelper.KEY_REMINDER_TYPE + " != ? AND "
                + DBHelper.KEY_REMINDER_TYPE + " != ? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND "
                + DBHelper.KEY_REMINDER_SYNCHRONISE + " != ?";
        cursor = db.rawQuery(sql, new String[]{"Выполненные", "Просроченные", login, "deleted"});


        String sDate;
        String sRepeat;
//        String sTime;
        if (cursor.moveToFirst()) {
            int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
            int typeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME);
            int dateIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE);
            int timeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME);
            int repeatIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT);
            do {
                id = cursor.getInt(idKeyIndex);
                sDate = cursor.getString(dateIndex);
                sRepeat = cursor.getString(repeatIndex);
//                sTime = cursor.getString(timeIndex);

//                String dateTimeString = sDate + " " + sTime;
                String dateTimeString = sDate;
                Date dateTimeDate = null;

                try {
                    dateTimeDate = formatDateAll.parse(dateTimeString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, +1);
                Date tomorrowDate = calendar.getTime();

                String tomorrowString = formatDateAll.format(tomorrowDate);

                if (dateTimeString.equals(dateNow)) {
                    type = "Сегодня";
                    Log.d("myLogs", type + " dateTimeString = " + dateTimeString + " dateNow = " + dateNow);
                } else if (dateTimeString.equals(tomorrowString)) {
                    type = "Завтра";
                    Log.d("myLogs", type + " dateTimeString = " + dateTimeString + " tomorrowString = " + tomorrowString);
                } else if (dateTimeDate.compareTo(tomorrowDate) == 1) {
                    type = "Будущие";
                    Log.d("myLogs", type + " dateTimeString = " + dateTimeString + " tomorrowString = " + tomorrowString);
                } else {
                    type = "Просроченные";
                    mAlarmReceiver.cancelAlarm(getActivity(), id);
                }
//                SELECT * FROM reminders WHERE reminder_type != 'Выполненные' AND reminder_type != 'Просроченные'


                String sqlUpd = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
                        + type + "'" + " WHERE " + DBHelper.KEY_REMINDER_ID + " = " + id + " AND "
                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                db.execSQL(sqlUpd);


                Call<Void> updType = serverApi.editReminderType(type, String.valueOf(id), login, sRepeat);

                updType.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sqlUpd = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                + "yes' WHERE " + DBHelper.KEY_REMINDER_ID + " = " + id + " AND "
                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                        db.execSQL(sqlUpd);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sqlUpd = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                + "no' WHERE " + DBHelper.KEY_REMINDER_ID + " = " + id + " AND "
                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                        db.execSQL(sqlUpd);

                    }
                });



            } while (cursor.moveToNext());
        }

        cursor.close();


//        --------------------------------------------------------------------------------


//        Выбор Сегодня, завтра, будущие Update в соответствии с текущим временем


//        Создаём коллекцию для коллекции элементов
        groupList = new ArrayList<>();

//        Создаем коллекции для отдельных групп
        listExecuted = new ArrayList<>();
        listOverdue = new ArrayList<>();
        listToday = new ArrayList<>();
        listTomorrow = new ArrayList<>();
        listFuture = new ArrayList<>();

        for (String title : titles) {

//            sql = "select " + DBHelper.KEY_REMINDER_ID + ", " + DBHelper.KEY_REMINDER_TYPE + ", "
//                    + DBHelper.KEY_REMINDER_NAME + ", " + DBHelper.KEY_REMINDER_DATE + ", "
//                    + DBHelper.KEY_REMINDER_TIME + ", " + DBHelper.KEY_REMINDER_REPEAT + ", "
//                    + DBHelper.KEY_REMINDER_IMG_MARKER + ", " + DBHelper.KEY_REMINDER_MARKER_NAME
//                    + " from " + DBHelper.TABLE_REMINDER
//                    + " where " + DBHelper.KEY_REMINDER_TYPE + "=? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
//
//            cursor = db.rawQuery(sql, new String[]{title, login});

            sql = "SELECT * FROM " + DBHelper.TABLE_REMINDER + " WHERE "
                    + DBHelper.KEY_REMINDER_TYPE + "=? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND "
                    + DBHelper.KEY_REMINDER_SYNCHRONISE + " != ?";

            cursor = db.rawQuery(sql, new String[]{title, login, "deleted"});


            if (cursor.getCount() != 0) {
//            Заполняем список атрибутов для каждой группы
                groupList.add(title);
            }


//        Заполняем список атрибутов для каждого элемента
            if (cursor.moveToFirst()) {
                int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                int typeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE);
                int nameIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME);
                int dateIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE);
                int timeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME);
                int repeatIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT);
                int imgIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER);
                int nameIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME);
                do {

                    if (cursor.getString(typeIndex).equals("Выполненные")) {
                        listExecuted.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Просроченные")) {
                        listOverdue.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Сегодня")) {
                        listToday.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Завтра")) {
                        listTomorrow.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Будущие")) {
                        listFuture.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }


                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        childCollection = new HashMap<String, ArrayList<ItemChildReminder>>();

        for (String group : groupList) {
            switch (group) {
                case "Выполненные":
                    childList = listExecuted;
                    break;
                case "Просроченные":
                    childList = listOverdue;
                    break;
                case "Сегодня":
                    childList = listToday;
                    break;
                case "Завтра":
                    childList = listTomorrow;
                    break;
                case "Будущие":
                    childList = listFuture;
                    break;
            }

            childCollection.put(group, childList);
        }

        updateFromDb();


        elvReminders = (ExpandableListView) view.findViewById(R.id.elvReminders);
        ExpandableListAdapterList adapter = new ExpandableListAdapterList(getActivity(), groupList, childCollection);
        elvReminders.setAdapter(adapter);

//        elvReminders.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                LinearLayout groupLayout = (LinearLayout) view.findViewById(R.id.groupLayout);
//
//                groupList.get(groupPosition)
//
//                groupLayout.setBackgroundColor(Color.GREEN);
//            }
//        });

        elvReminders.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                String title = groupList.get(groupPosition);
                Log.d("myLogs", "title = " + title);

                String sql = "SELECT * FROM " + DBHelper.TABLE_REMINDER + " WHERE "
                        + DBHelper.KEY_REMINDER_NAME + " =? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND "
                        + DBHelper.KEY_REMINDER_SYNCHRONISE + " != ?";
                Cursor cursor = db.rawQuery(sql, new String[]{title, login, "deleted"});

                //        Заполняем список атрибутов для каждого элемента
                if (cursor.moveToFirst()) {
                    int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                    int typeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME);
                    int dateIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE);
                    int timeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME);
                    int repeatIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT);
                    int imgIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER);
                    int nameIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME);
                    do {

                        if (cursor.getString(typeIndex).equals("Выполненные")) {
                            listExecuted.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                    cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                    cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                        }

                        if (cursor.getString(typeIndex).equals("Просроченные")) {
                            listOverdue.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                    cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                    cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                        }

                        if (cursor.getString(typeIndex).equals("Сегодня")) {
                            listToday.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                    cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                    cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                        }

                        if (cursor.getString(typeIndex).equals("Завтра")) {
                            listTomorrow.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                    cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                    cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                        }

                        if (cursor.getString(typeIndex).equals("Будущие")) {
                            listFuture.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                    cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                    cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                        }


                    } while (cursor.moveToNext());
                }
                cursor.close();

                childCollection = new HashMap<String, ArrayList<ItemChildReminder>>();

                for (String group : groupList) {
                    switch (group) {
                        case "Выполненные":
                            childList = listExecuted;
                            break;
                        case "Просроченные":
                            childList = listOverdue;
                            break;
                        case "Сегодня":
                            childList = listToday;
                            break;
                        case "Завтра":
                            childList = listTomorrow;
                            break;
                        case "Будущие":
                            childList = listFuture;
                            break;
                    }

                    childCollection.put(group, childList);
                }

            }
        });


        elvReminders.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long position) {

                String groupName = groupList.get(groupPosition);

                int itemId = childCollection.get(groupName).get(childPosition).getId();

                Intent intent = new Intent(getActivity(), ReminderEditActivity.class);
                intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(itemId));
                startActivityForResult(intent, REQUEST_CODE_LIST);

                getActivity().finish();

                return true;
            }
        });


        registerForContextMenu(elvReminders);
    }

    private void updateFromDb(){

//        Создаём коллекцию для коллекции элементов
        groupList = new ArrayList<>();

//        Создаем коллекции для отдельных групп
        listExecuted = new ArrayList<>();
        listOverdue = new ArrayList<>();
        listToday = new ArrayList<>();
        listTomorrow = new ArrayList<>();
        listFuture = new ArrayList<>();

        for (String title : titles) {

//            sql = "select " + DBHelper.KEY_REMINDER_ID + ", " + DBHelper.KEY_REMINDER_TYPE + ", "
//                    + DBHelper.KEY_REMINDER_NAME + ", " + DBHelper.KEY_REMINDER_DATE + ", "
//                    + DBHelper.KEY_REMINDER_TIME + ", " + DBHelper.KEY_REMINDER_REPEAT + ", "
//                    + DBHelper.KEY_REMINDER_IMG_MARKER + ", " + DBHelper.KEY_REMINDER_MARKER_NAME
//                    + " from " + DBHelper.TABLE_REMINDER
//                    + " where " + DBHelper.KEY_REMINDER_TYPE + "=? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
//
//            cursor = db.rawQuery(sql, new String[]{title, login});

            String sql = "SELECT * FROM " + DBHelper.TABLE_REMINDER + " WHERE "
                    + DBHelper.KEY_REMINDER_TYPE + "=? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND "
                    + DBHelper.KEY_REMINDER_SYNCHRONISE + " != ?";

            Cursor cursor = db.rawQuery(sql, new String[]{title, login, "deleted"});


            if (cursor.getCount() != 0) {
//            Заполняем список атрибутов для каждой группы
                groupList.add(title);
            }


//        Заполняем список атрибутов для каждого элемента
            if (cursor.moveToFirst()) {
                int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                int typeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE);
                int nameIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME);
                int dateIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE);
                int timeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME);
                int repeatIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT);
                int imgIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER);
                int nameIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME);
                do {

                    if (cursor.getString(typeIndex).equals("Выполненные")) {
                        listExecuted.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Просроченные")) {
                        listOverdue.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Сегодня")) {
                        listToday.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Завтра")) {
                        listTomorrow.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }

                    if (cursor.getString(typeIndex).equals("Будущие")) {
                        listFuture.add(new ItemChildReminder(cursor.getString(nameIndex), cursor.getString(typeIndex),
                                cursor.getString(timeIndex), cursor.getString(dateIndex), cursor.getString(repeatIndex),
                                cursor.getInt(idKeyIndex), cursor.getInt(imgIconIndex), cursor.getString(nameIconIndex)));
                    }


                } while (cursor.moveToNext());
            }
            cursor.close();
        }


        childCollection = new HashMap<String, ArrayList<ItemChildReminder>>();

        for (String group : groupList) {
            switch (group) {
                case "Выполненные":
                    childList = listExecuted;
                    break;
                case "Просроченные":
                    childList = listOverdue;
                    break;
                case "Сегодня":
                    childList = listToday;
                    break;
                case "Завтра":
                    childList = listTomorrow;
                    break;
                case "Будущие":
                    childList = listFuture;
                    break;
            }

            childCollection.put(group, childList);
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type =
                ExpandableListView.getPackedPositionType(info.packedPosition);
        int group =
                ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child =
                ExpandableListView.getPackedPositionChild(info.packedPosition);

        groupName = groupList.get(group);

        //Only create a context menu for child items
        if (type == 0) {
            if (!groupName.equals("Выполненные")) {
                menu.add(0, MENU_REMINDER_DONE_ALL, 0, "Отметить все как выполненные");
                menu.add(0, MENU_REMINDER_DELETE_ALL, 0, "Удалить все");
            } else {
                menu.add(0, MENU_REMINDER_DELETE_ALL, 0, "Удалить все");
            }
        }

        if (type == 1) {

            itemId = childCollection.get(groupName).get(child).getId();
            itemTitle = childCollection.get(groupName).get(child).getTitle();
            itemType = childCollection.get(groupName).get(child).getType();
            //Array created earlier when we built the expandable list
//            menu.setHeaderTitle("Title");

            if (!itemType.equals("Выполненные")){
                menu.add(0, MENU_REMINDER_EDIT, 0, "Редактировать");
                menu.add(0, MENU_REMINDER_DELETE, 0, "Удалить");
                menu.add(0, MENU_REMINDER_DONE, 0, "Отметить как выполненное");
            } else {
                menu.add(0, MENU_REMINDER_EDIT, 0, "Редактировать");
                menu.add(0, MENU_REMINDER_DELETE, 0, "Удалить");
            }
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
//            -------------------- ДЛЯ ДОЧЕРНИХ ЭЛЕМЕНТОВ ------------------------------
            case MENU_REMINDER_EDIT:  // Редактировать
                Intent intent = new Intent(getActivity(), ReminderEditActivity.class);
                intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(itemId));
                startActivityForResult(intent, REQUEST_CODE_LIST);

                getActivity().finish();
                break;
            case MENU_REMINDER_DELETE:  //  Удалить


//        --------------------------- Всплывающее окно Alert Dialog -----------------------
                Context context = getActivity();
                String title = "Удаление " + itemTitle;
                String message = "Вы уверены что хотите удалить выбранное задание?";
                String buttonYes = "Да";
                String buttonNo = "Нет";

                ad = new AlertDialog.Builder(context);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Call<Void> callDeleteReminderOne = serverApi.deleteFromReminders("one", itemType,
                                login, String.valueOf(itemId));

//                        String sql = "update " + DBHelper.TABLE_REMINDER + " set " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'deleted' where "
//                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' and "
//                                + DBHelper.KEY_REMINDER_ID + " = " + itemId;
//                        db.execSQL(sql);

                        callDeleteReminderOne.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "delete from " + DBHelper.TABLE_REMINDER + " where " + DBHelper.KEY_REMINDER_ID + " = "
                                        + itemId;
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

//                    --------------------------- Запись ID в файл --------------------------------

                                try{
//            Сткрываем поток для чтения
                                    BufferedReader br  = new BufferedReader(new InputStreamReader(
                                            getActivity().openFileInput("reminder_delete.txt")));
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
                                            getActivity().openFileOutput("reminder_delete.txt", MODE_PRIVATE)));
//            Пишем данные
                                    bw.write(fileString + " " + String.valueOf(itemId));
//            Закрываем поток
                                    bw.close();
                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


//                    -----------------------------------------------------------------------------

                                String sql = "update " + DBHelper.TABLE_REMINDER + " set " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'deleted' where "
                                        + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_REMINDER_ID + " = " + itemId;
                                db.execSQL(sql);


                            }
                        });

                        getActivity().finish();

                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        intent.putExtra("name_fragment", "freminder");
                        startActivity(intent);

//                        refreshFragment();

                        mAlarmReceiver.cancelAlarm(getActivity(), itemId);
                    }
                });

                ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

//        -----------------------------------------------------------------------------------------

                ad.show();


                break;

            case MENU_REMINDER_DONE:  //  Отметить как выполненное

                String sql = "SELECT * FROM " + DBHelper.TABLE_REMINDER + " WHERE "
                        + DBHelper.KEY_REMINDER_ID + " =? AND "
                        + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";

                Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(itemId), login});

                String reminderId = "", reminderType = "", reminderName = "", reminderDate = "", reminderTime = "",
                        reminderRepeat = "", reminderImgIcon = "", reminderNameIcon = "", reminderSound = "";

                if (cursor.moveToFirst()){
                    int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                    int typeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME);
                    int dateIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE);
                    int timeIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME);
                    int repeatIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT);
                    int imgIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER);
                    int nameIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME);
                    int soundIconIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_SOUND);
                    do {

                        reminderId = cursor.getString(idKeyIndex);
                        reminderType = cursor.getString(typeIndex);
                        reminderName = cursor.getString(nameIndex);
                        reminderDate = cursor.getString(dateIndex);
                        reminderTime = cursor.getString(timeIndex);
                        reminderRepeat = cursor.getString(repeatIndex);
                        reminderImgIcon = cursor.getString(imgIconIndex);
                        reminderNameIcon = cursor.getString(nameIconIndex);
                        reminderSound = cursor.getString(soundIconIndex);

                    }while (cursor.moveToNext());
                }
                cursor.close();

                if (reminderRepeat.equals("Один раз")){


                    Call<Void> callEditReminder = serverApi.editReminder("Выполненные", reminderName, reminderDate,
                            reminderTime, reminderRepeat, reminderImgIcon, reminderNameIcon, reminderSound, login, reminderId);

                    sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
                            + "Выполненные" + "'" + " WHERE "
                            + DBHelper.KEY_REMINDER_ID + " = " + itemId + " AND "
                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                    db.execSQL(sql);

                    callEditReminder.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                    + "yes" + "'" + " WHERE "
                                    + DBHelper.KEY_REMINDER_ID + " = " + itemId + " AND "
                                    + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                    + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                    + "no" + "'" + " WHERE "
                                    + DBHelper.KEY_REMINDER_ID + " = " + itemId + " AND "
                                    + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                    + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);



//                    --------------------------- Запись ID в файл --------------------------------

                            try{
//            Сткрываем поток для чтения
                                BufferedReader br  = new BufferedReader(new InputStreamReader(
                                        getActivity().openFileInput("reminder_update.txt")));
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
                                        getActivity().openFileOutput("reminder_update.txt", MODE_PRIVATE)));
//            Пишем данные
                                bw.write(fileString + " " + String.valueOf(itemId));
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


//                refreshFragment();

                    mAlarmReceiver.cancelAlarm(getActivity(), itemId);

                    getActivity().finish();

                    intent = new Intent(getActivity(), UserActivity.class);
                    intent.putExtra("name_fragment", "freminder");
                    startActivity(intent);

                    Toast.makeText(getActivity(), "Напоминание отмечено как выполненое", Toast.LENGTH_SHORT).show();


                }

                break;

//            ------------------------  ДЛЯ ГРУПП -------------------------------
            case MENU_REMINDER_DONE_ALL:  //  Отметить все как выполнееные

//                --------------------------- Закрываем несколько уведомлений ----------------------
                ArrayList<Integer> itemIds = new ArrayList<>();

                sql = "SELECT " + DBHelper.KEY_REMINDER_ID + " FROM " + DBHelper.TABLE_REMINDER
                        + " WHERE " + DBHelper.KEY_REMINDER_TYPE + " =? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND "
                        + DBHelper.KEY_REMINDER_SYNCHRONISE + " != ?";
                cursor = db.rawQuery(sql, new String[]{groupName, login, "deleted"});

                if (cursor.moveToFirst()) {
                    int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                    do {
                        itemIds.add(cursor.getInt(idKeyIndex));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                mAlarmReceiver.cancelAlarm(getActivity(), itemIds);
//                ----------------------------------------------------------------------------

                Call<Void> callEditRemindDoneAll = serverApi.editReminderDoneAll(groupName, login);

//                update reminders set reminder_type = 'Выполненные', reminder_synchronise = 'no' where reminder_type = 'Сегодня'

                callEditRemindDoneAll.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
                                + "Выполненные" + "', " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'no'" + " WHERE "
                                + DBHelper.KEY_REMINDER_TYPE + " = '" + groupName + "' AND "
                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                + DBHelper.KEY_REMINDER_REPEAT + " = 'Один раз'";
                        db.execSQL(sql);

                        sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                + "yes" + "'" + " WHERE "
                                + DBHelper.KEY_REMINDER_TYPE + " = '" + groupName + "' AND "
                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                + DBHelper.KEY_REMINDER_REPEAT + " = 'Один раз'";
                        db.execSQL(sql);

//                        String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
//                                + "yes" + "'" + " WHERE "
//                                + DBHelper.KEY_REMINDER_TYPE + " = '" + groupName + "' AND "
//                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
//                                + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
//                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "SELECT " + DBHelper.KEY_REMINDER_ID + " FROM " + DBHelper.TABLE_REMINDER
                                + " WHERE " + DBHelper.KEY_REMINDER_TYPE + " =? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND "
                                + DBHelper.KEY_REMINDER_SYNCHRONISE + " != ?";
                        Cursor cursor = db.rawQuery(sql, new String[]{groupName, login, "deleted"});

                        if (cursor.moveToFirst()) {
                            int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                            do {


//                    --------------------------- Запись ID в файл --------------------------------

                                try{
//            Сткрываем поток для чтения
                                    BufferedReader br  = new BufferedReader(new InputStreamReader(
                                            getActivity().openFileInput("reminder_update.txt")));
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
                                            getActivity().openFileOutput("reminder_update.txt", MODE_PRIVATE)));
//            Пишем данные
                                    bw.write(fileString + " " + String.valueOf(cursor.getInt(idKeyIndex)));
//            Закрываем поток
                                    bw.close();
                                } catch (FileNotFoundException e){
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


//                    -----------------------------------------------------------------------------


                            } while (cursor.moveToNext());
                        }
                        cursor.close();


                        sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_TYPE + " = '"
                                + "Выполненные" + "', " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'no'" + " WHERE "
                                + DBHelper.KEY_REMINDER_TYPE + " = '" + groupName + "' AND "
                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
                                + DBHelper.KEY_REMINDER_REPEAT + " = 'Один раз'";
                        db.execSQL(sql);

//                        String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
//                                + "no" + "'" + " WHERE "
//                                + DBHelper.KEY_REMINDER_TYPE + " = '" + groupName + "' AND "
//                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
//                                + DBHelper.KEY_REMINDER_REPEAT + " = 'Один раз'";
//                        db.execSQL(sql);

//                        String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
//                                + "no" + "'" + " WHERE "
//                                + DBHelper.KEY_REMINDER_TYPE + " = '" + groupName + "' AND "
//                                + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' AND "
//                                + DBHelper.KEY_REMINDER_SYNCHRONISE + " != 'ins'";
//                        db.execSQL(sql);

                    }
                });


                intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("name_fragment", "freminder");
                startActivity(intent);

                getActivity().finish();

                Toast.makeText(getActivity(), "Напоминания отмеченны как выполненные", Toast.LENGTH_SHORT).show();

                break;

            case MENU_REMINDER_DELETE_ALL:   //   Удалить все


//        --------------------------- Всплывающее окно Alert Dialog -----------------------
                context = getActivity();
                title = "Удаление " + groupName;
                message = "Вы уверены что хотите удалить выбранную группу?";
                buttonYes = "Да";
                buttonNo = "Нет";

                ad = new AlertDialog.Builder(context);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Call<Void> callDeleteReminderAll = serverApi.deleteFromReminders("all", groupName,
                                login, "");

                        String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                + "deleted" + "'" + " WHERE "
                                + DBHelper.KEY_REMINDER_TYPE + " = '"
                                + groupName + "' AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                        db.execSQL(sql);

                        callDeleteReminderAll.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "delete from " + DBHelper.TABLE_REMINDER + " where " + DBHelper.KEY_REMINDER_TYPE + " = '"
                                        + groupName + "' AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "SELECT " + DBHelper.KEY_REMINDER_ID + " FROM " + DBHelper.TABLE_REMINDER
                                        + " WHERE " + DBHelper.KEY_REMINDER_TYPE + " =? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
                                Cursor cursor = db.rawQuery(sql, new String[]{groupName, login});

                                if (cursor.moveToFirst()) {
                                    int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                                    do {

//                    --------------------------- Запись ID в файл --------------------------------

                                        try{
//            Сткрываем поток для чтения
                                            BufferedReader br  = new BufferedReader(new InputStreamReader(
                                                    getActivity().openFileInput("reminder_delete.txt")));
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
                                                    getActivity().openFileOutput("reminder_delete.txt", MODE_PRIVATE)));
//            Пишем данные
                                            bw.write(fileString + " " + String.valueOf(cursor.getInt(idKeyIndex)));
//            Закрываем поток
                                            bw.close();
                                        } catch (FileNotFoundException e){
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


//                    -----------------------------------------------------------------------------



                                    } while (cursor.moveToNext());
                                }
                                cursor.close();



                                sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                        + "deleted" + "'" + " WHERE "
                                        + DBHelper.KEY_REMINDER_TYPE + " = '"
                                        + groupName + "' AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                                db.execSQL(sql);

                            }
                        });

//                --------------------------- Закрываем несколько уведомлений ----------------------
                        ArrayList<Integer> itemIds = new ArrayList<>();

                        sql = "SELECT " + DBHelper.KEY_REMINDER_ID + " FROM " + DBHelper.TABLE_REMINDER
                                + " WHERE " + DBHelper.KEY_REMINDER_TYPE + " =? AND " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
                        Cursor cursor = db.rawQuery(sql, new String[]{groupName, login});

                        if (cursor.moveToFirst()) {
                            int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                            do {
                                itemIds.add(cursor.getInt(idKeyIndex));
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        mAlarmReceiver.cancelAlarm(getActivity(), itemIds);
//                ----------------------------------------------------------------------------

                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        intent.putExtra("name_fragment", "freminder");
                        startActivity(intent);

                        getActivity().finish();
//                        refreshFragment();


                    }
                });

                ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

//        -----------------------------------------------------------------------------------------

                ad.show();



                break;

            default:
                return super.onContextItemSelected (item);
        }
        return true;

    }

//    private void refreshFragment() {
//        Fragment frg = null;
//        frg = getFragmentManager().findFragmentByTag("FragmentReminder");
//        final FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(frg);
//        ft.attach(frg);
//        ft.commit();
//    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_LIST:

                    createThisFragm();

                    break;
            }
        }



    }


    @Override
    public void onPause() {
        super.onPause();

        groupList.removeAll(groupList);
        childCollection.remove(childCollection);
    }


    @Override
    public void onResume() {
        super.onResume();

        createThisFragm();

//        refreshFragment();
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
