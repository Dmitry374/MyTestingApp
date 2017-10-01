package com.example.dima.mytestingapp.Activitys;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Adapters.StatisticAdapterGetGen;
import com.example.dima.mytestingapp.Adapters.StatisticAdapterSpendGen;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.ItemServerStatistic;
import com.example.dima.mytestingapp.Items.ItemStatisticGetGen;
import com.example.dima.mytestingapp.Items.ItemStatisticSpendGen;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *  Одиночная статистика
 */

public class ItemStatisticActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    ListView lvItemStatistic;

    Button btnStatisticDay;
    Button btnStatisticWeek;
    Button btnStatisticMonth;
    Button btnStatisticQuarter;
    Button btnStatisticYear;
    Button btnStatisticAnyDay;

    Date date;  //  Текущая дата
    String dateNow;

    String getAction;
    String btnName;

    String typePay;

    int DIALOG_DATE = 1;

    TextView noDataView;

    //    Значение даты по умолчанию
    Calendar calendar = Calendar.getInstance();
    int myYear = calendar.get(Calendar.YEAR);
    int myMonth = calendar.get(Calendar.MONTH);
    int myDay = calendar.get(Calendar.DAY_OF_MONTH);

    String getNewDateDialog;

    StatisticAdapterSpendGen statisticAdapterSpendGeneral;
    StatisticAdapterGetGen statisticAdapterGetGeneral;

    ArrayList<ItemStatisticGetGen> listGetGeneral = new ArrayList<ItemStatisticGetGen>();
    ArrayList<ItemStatisticSpendGen> listSpendGeneral = new ArrayList<ItemStatisticSpendGen>();

    ServerApi serverApi;
    ContentValues contentValues;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_statictic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        Intent intent = getIntent();
        btnName = intent.getStringExtra("btnName");
        getAction = intent.getStringExtra("action_key");

//        Имя Toolbar
        Activity activity = this;
        if (toolbar != null) {
            activity.setTitle("Статистика по " + btnName);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    //   Стрелка
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnStatisticDay = (Button) findViewById(R.id.btnStatisticDay);
        btnStatisticDay.setOnClickListener(this);
        btnStatisticWeek = (Button) findViewById(R.id.btnStatisticWeek);
        btnStatisticWeek.setOnClickListener(this);
        btnStatisticMonth = (Button) findViewById(R.id.btnStatisticMonth);
        btnStatisticMonth.setOnClickListener(this);
        btnStatisticQuarter = (Button) findViewById(R.id.btnStatisticQuarter);
        btnStatisticQuarter.setOnClickListener(this);
        btnStatisticYear = (Button) findViewById(R.id.btnStatisticYear);
        btnStatisticYear.setOnClickListener(this);
        btnStatisticAnyDay = (Button) findViewById(R.id.btnStatisticAnyDay);
        btnStatisticAnyDay.setOnClickListener(this);

        noDataView = (TextView) findViewById(R.id.no_statistic_item_text);


        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");


        date = new Date();
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateNow = format.format(date);
//        DateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        String timeNow = formatTime.format(date);
        Log.d("myLogs", "dateNow = " + dateNow);


        if (getAction.equals("get")){
            String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                    + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                    + " from " + DBHelper.TABLE_STATISTIC
                    + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                    + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, dateNow, "get"});

            if (cursor.getCount() == 0){
                noDataView.setVisibility(View.VISIBLE);
            } else {
                noDataView.setVisibility(View.GONE);
            }

            if (cursor.moveToFirst()) {
                do {
                    listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                            "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));
                } while (cursor.moveToNext());
            }
            cursor.close();

            statisticAdapterGetGeneral = new StatisticAdapterGetGen(this, listGetGeneral, R.color.colorTextGreen);


            lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
            lvItemStatistic.setAdapter(statisticAdapterGetGeneral);

        } else {

            String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                    + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                    + DBHelper.KEY_STATISTIC_TIME
                    + " from " + DBHelper.TABLE_STATISTIC
                    + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                    + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
            Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, dateNow, "spend"});

            if (cursor.getCount() == 0){
                noDataView.setVisibility(View.VISIBLE);
            } else {
                noDataView.setVisibility(View.GONE);
            }

            if (cursor.moveToFirst()) {
                do {
//                    typePay = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY));

                    listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                            "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));
                } while (cursor.moveToNext());
            }
            cursor.close();

            statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(this, listSpendGeneral, Color.RED);


            lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
            lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);

        }



        //        Обновление Activity
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.statistic_swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getAction.equals("get")){
                            loadStatisticGet();
                        } else {
                            loadStatisticSpend();
                        }

//                        new LoginActivity().loadDataFromServer();

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.red);





    }

    private void loadStatisticGet() {

        String sql = "delete from " + DBHelper.TABLE_STATISTIC;
        db.execSQL(sql);

        contentValues = new ContentValues();

//                Загрузка данных из сервера
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);

//                        --------------------------- Statistic ------------------------------------

        Call<List<ItemServerStatistic>> callStatistic = serverApi.loadTableStatistic(login);

        callStatistic.enqueue(new Callback<List<ItemServerStatistic>>() {
            @Override
            public void onResponse(Call<List<ItemServerStatistic>> call, Response<List<ItemServerStatistic>> response) {

                String type = "", nameGet = "", kol = "", nameSpend = "",
                        date = "", time = "", refLogin = "";

//                            Прием данных обратно с сервера
                for (int i = 0; i < response.body().size(); i++) {
                    type = response.body().get(i).getType();
                    nameGet = response.body().get(i).getNameGet();
                    kol = response.body().get(i).getKol();
                    nameSpend = response.body().get(i).getNameSpend();
                    date = response.body().get(i).getDate();
                    time = response.body().get(i).getTime();


                    contentValues.put(DBHelper.KEY_STATISTIC_TYPE, type);
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, nameGet);
                    contentValues.put(DBHelper.KEY_STATISTIC_KOL, kol);
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, nameSpend);
                    contentValues.put(DBHelper.KEY_STATISTIC_DATE, date);
                    contentValues.put(DBHelper.KEY_STATISTIC_TIME, time);
                    contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                    contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "yes");

                    db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);
                }

                String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                        + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, dateNow, "get"});

                listGetGeneral.removeAll(listGetGeneral);

                if (cursor.moveToFirst()) {
                    do {
                        listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                statisticAdapterGetGeneral = new StatisticAdapterGetGen(ItemStatisticActivity.this, listGetGeneral, R.color.colorTextGreen);


                lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                lvItemStatistic.setAdapter(statisticAdapterGetGeneral);

            }

            @Override
            public void onFailure(Call<List<ItemServerStatistic>> call, Throwable t) {

            }
        });

    }



    private void loadStatisticSpend(){

        String sql = "delete from " + DBHelper.TABLE_STATISTIC;
        db.execSQL(sql);

        contentValues = new ContentValues();

//                Загрузка данных из сервера
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);

//                        --------------------------- Statistic ------------------------------------

        Call<List<ItemServerStatistic>> callStatistic = serverApi.loadTableStatistic(login);

        callStatistic.enqueue(new Callback<List<ItemServerStatistic>>() {
            @Override
            public void onResponse(Call<List<ItemServerStatistic>> call, Response<List<ItemServerStatistic>> response) {

                String type = "", nameGet = "", kol = "", nameSpend = "",
                        date = "", time = "", refLogin = "";

//                            Прием данных обратно с сервера
                for (int i = 0; i < response.body().size(); i++) {
                    type = response.body().get(i).getType();
                    nameGet = response.body().get(i).getNameGet();
                    kol = response.body().get(i).getKol();
                    nameSpend = response.body().get(i).getNameSpend();
                    date = response.body().get(i).getDate();
                    time = response.body().get(i).getTime();


                    contentValues.put(DBHelper.KEY_STATISTIC_TYPE, type);
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, nameGet);
                    contentValues.put(DBHelper.KEY_STATISTIC_KOL, kol);
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, nameSpend);
                    contentValues.put(DBHelper.KEY_STATISTIC_DATE, date);
                    contentValues.put(DBHelper.KEY_STATISTIC_TIME, time);
                    contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                    contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "yes");

                    db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);

                }

                String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                        + DBHelper.KEY_STATISTIC_TIME
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                        + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, dateNow, "spend"});

                listSpendGeneral.removeAll(listSpendGeneral);

                if (cursor.moveToFirst()) {
                    do {
//                    typePay = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY));

                        listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(ItemStatisticActivity.this, listSpendGeneral, Color.RED);


                lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);


            }

            @Override
            public void onFailure(Call<List<ItemServerStatistic>> call, Throwable t) {

            }
        });

    }




    //    --------------- Методы для диалога даты -----------------------------------------
    protected Dialog onCreateDialog(int id){
        if (id == DIALOG_DATE){
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack,
                    myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
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


            String date = strDay + "." + strMonth + "." + year;
            Log.d("myLogs", "day = " + dayOfMonth + " month = " + (month+1) + " year = " + year);

            getNewDateDialog = date;  //  Дата выбранная из диалога

            if (getAction.equals("get")){
//                -------------------------------- GET -----------------------------------------------------
                String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                        + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, getNewDateDialog, "get"});

                if (cursor.getCount() == 0){
                    noDataView.setVisibility(View.VISIBLE);
                } else {
                    noDataView.setVisibility(View.GONE);
                }

                listGetGeneral.removeAll(listGetGeneral);   //  Удаляем все дааные из List что бы небыло повторения

                if (cursor.moveToFirst()) {
                    do {
                        listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                statisticAdapterGetGeneral = new StatisticAdapterGetGen(ItemStatisticActivity.this, listGetGeneral, R.color.colorTextGreen);


                lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                lvItemStatistic.setAdapter(statisticAdapterGetGeneral);
//                ----------------------------------------------------------------------------------------------
            } else {
//                --------------------------- SPEND ------------------------------------------
                String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                        + DBHelper.KEY_STATISTIC_TIME
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                        + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, getNewDateDialog, "spend"});

                if (cursor.getCount() == 0){
                    noDataView.setVisibility(View.VISIBLE);
                } else {
                    noDataView.setVisibility(View.GONE);
                }

                listSpendGeneral.removeAll(listSpendGeneral);   //  Удаляем все дааные из List что бы небыло повторения

                if (cursor.moveToFirst()) {
                    do {
//                    typePay = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY));

                        listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(ItemStatisticActivity.this, listSpendGeneral, Color.RED);


                lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);


//                ----------------------------------------------------------------------------
            }


        }
    };
//    ---------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {

        date = new Date();
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateNow = format.format(date);

        switch (v.getId()){
            case R.id.btnStatisticDay:
                Toast.makeText(this, "Текущая дата", Toast.LENGTH_SHORT).show();
                listGetGeneral.removeAll(listGetGeneral);
                listSpendGeneral.removeAll(listSpendGeneral);


                if (getAction.equals("get")){

                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                            + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, dateNow, "get"});

                    if (cursor.getCount() == 0){
                        noDataView.setVisibility(View.VISIBLE);
                    } else {
                        noDataView.setVisibility(View.GONE);
                    }

                    listGetGeneral.removeAll(listGetGeneral);   //  Удаляем все дааные из List что бы небыло повторения

                    if (cursor.moveToFirst()) {
                        do {
                            listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                    "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                    cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterGetGeneral = new StatisticAdapterGetGen(ItemStatisticActivity.this, listGetGeneral, R.color.colorTextGreen);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterGetGeneral);

                } else {


//                --------------------------- SPEND ------------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                            + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                            + DBHelper.KEY_STATISTIC_DATE + " =? AND " + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, dateNow, "spend"});

                    if (cursor.getCount() == 0){
                        noDataView.setVisibility(View.VISIBLE);
                    } else {
                        noDataView.setVisibility(View.GONE);
                    }

                    listSpendGeneral.removeAll(listSpendGeneral);   //  Удаляем все дааные из List что бы небыло повторения

                    if (cursor.moveToFirst()) {
                        do {
//                    typePay = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY));

                            listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                    "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                    cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                    cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(ItemStatisticActivity.this, listSpendGeneral, Color.RED);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);

//                ----------------------------------------------------------------------------

                }
                break;

            case R.id.btnStatisticWeek:
                Toast.makeText(this, "Текущая неделя", Toast.LENGTH_SHORT).show();

                format = new SimpleDateFormat("dd.MM.yyyy",  new Locale("ru", "RU"));
//                Calendar calendar = new GregorianCalendar();

                Calendar calendar = Calendar.getInstance();

//                String testDate = "08.08.2017";  //  !!!!! Можно задать любую дату и определить даты недели !!!!!
//                Date docDate = null;
//                try {
//                    docDate= format.parse(testDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                calendar.setTime(docDate);

//                Calendar calendar = Calendar.getInstance(new Locale("ru", "RU"));

//                --------------------------------------- Рабочий ----------------------------------
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date weekStartDate = calendar.getTime();
                String weekStart = format.format(weekStartDate);

                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date weekEndDate = calendar.getTime();
                String weekEnd = format.format(weekEndDate);

                Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
//                ----------------------------------------------------------------------------------

//                Определение текущего дня
                calendar.setTime(date);
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                Log.d("myLogs", "day = " + day + " dateNow.substring(0,2) = " + dateNow.substring(0,2));
                calendar.setTime(date);

                if (day == Calendar.SUNDAY){
                    Log.d("myLogs", "Here is day 1");
                    Toast.makeText(this, "Day " + day, Toast.LENGTH_SHORT).show();
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    weekStartDate = calendar.getTime();
                    weekStart = format.format(weekStartDate);

                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    weekEndDate = calendar.getTime();
                    weekEnd = format.format(weekEndDate);

                    Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
                }

//                Если day = номеру дня месяца
                if (day == Integer.parseInt(dateNow.substring(0,2))){
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    weekStartDate = calendar.getTime();
                    weekStart = format.format(weekStartDate);

                    calendar.setTime(weekStartDate);
                    calendar.add(Calendar.DAY_OF_WEEK, +6);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    weekEndDate = calendar.getTime();  // это будет конец недели
                    weekEnd = format.format(weekEndDate);
                }

                Log.d("myLogs", "Final !!! weekStart = " + weekStart + " weekEnd = " + weekEnd + " dateNow = " + dateNow);

////                Определение Милисекунд Начала недели
//                String sql = "select " + DBHelper.KEY_GET_STATISTIC_DATE_MILLISECONDS
//                        + " from " + DBHelper.TABLE_GET_STATISTICS
//                        + " where " + DBHelper.KEY_GET_STATISTIC_REF_LOGIN + "=? AND "
//                        + DBHelper.KEY_GET_STATISTIC_DATE + " =?";
//                Cursor cursor = db.rawQuery(sql, new String[]{login, weekStart});
//
//                cursor.moveToFirst();
//                int weekMillisecondsStart = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_DATE_MILLISECONDS));
//                Log.d("myLogs", "weekMillisecondsStart = " + weekMillisecondsStart);
//                cursor.close();
//
////                Определение Милисекунд Конца недели
//                sql = "select " + DBHelper.KEY_GET_STATISTIC_DATE_MILLISECONDS
//                        + " from " + DBHelper.TABLE_GET_STATISTICS
//                        + " where " + DBHelper.KEY_GET_STATISTIC_REF_LOGIN + "=? AND "
//                        + DBHelper.KEY_GET_STATISTIC_DATE + " =?";
//                cursor = db.rawQuery(sql, new String[]{login, weekEnd});
//
//                cursor.moveToLast();
//                int weekMillisecondsEnd = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_DATE_MILLISECONDS));
//                Log.d("myLogs", "weekMillisecondsStart = " + weekMillisecondsEnd);
//                cursor.close();

                if (getAction.equals("get")){
//                    -------------------------- WEEK GET --------------------------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "get"});

                    listGetGeneral.removeAll(listGetGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if ((gettingDate.compareTo(weekStartDate) == 1 || gettingDateString.equals(weekStart)) &&
                                    (gettingDate.compareTo(weekEndDate) == -1 || gettingDateString.equals(weekEnd))){
                                listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterGetGeneral = new StatisticAdapterGetGen(this, listGetGeneral, R.color.colorTextGreen);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterGetGeneral);
                } else {
//                    --------------------------- WEEK SPEND ---------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                            + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "spend"});

                    listSpendGeneral.removeAll(listSpendGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if ((gettingDate.compareTo(weekStartDate) == 1 || gettingDateString.equals(weekStart)) &&
                                    (gettingDate.compareTo(weekEndDate) == -1 || gettingDateString.equals(weekEnd))){
                                listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(this, listSpendGeneral, Color.RED);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);

                }

                break;

            case R.id.btnStatisticMonth:
                Toast.makeText(this, "Текущий месяц", Toast.LENGTH_SHORT).show();

                format = new SimpleDateFormat("dd.MM.yyyy",  new Locale("ru", "RU"));

                calendar = Calendar.getInstance();

//                --------------------------------------- Рабочий ----------------------------------
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)); // это будет начало месяца
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date monthStartDate = calendar.getTime();
                String monthStart = format.format(monthStartDate);

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец месяца
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date monthEndDate = calendar.getTime();
                String monthEnd = format.format(monthEndDate);

                Log.d("myLogs", "monthStart = " + monthStart + " monthEnd = " + monthEnd);
//                ----------------------------------------------------------------------------------

                Log.d("myLogs", "date.compareTo(monthEndDate) = " + date.compareTo(monthEndDate));

//                ----------------------------------------------------------------------------------------------------------------------
                if (getAction.equals("get")){
//                    -------------------------- MONTH GET --------------------------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "get"});

                    listGetGeneral.removeAll(listGetGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Log.d("myLogs", "------------------------------------------------------------------------------------------------------------");
                            Log.d("myLogs", "gettingDateString = " + gettingDateString + " monthStart = " + monthStart + " monthEnd = " + monthEnd);

//                            Log.d("myLogs", "gettingDateString.compareTo(monthStart) = " + gettingDateString.compareTo(monthStart));

                            Log.d("myLogs", "gettingDate.compareTo(monthStartDate)==1 = " + (gettingDate.compareTo(monthStartDate) == 1));
                            Log.d("myLogs", "gettingDateString.equals(monthStart) = " + (gettingDateString.equals(monthStart)));
                            Log.d("myLogs", "gettingDate.compareTo(monthEndDate)==-1 = " + (gettingDate.compareTo(monthEndDate) == -1));
                            Log.d("myLogs", "gettingDateString.equals(monthEnd) = " + (gettingDateString.equals(monthEnd)));

                            Log.d("myLogs", "gettingDate.compareTo(monthStartDate) == 1 || gettingDateString.equals(monthStart) &&\n" +
                                    " gettingDate.compareTo(monthEndDate) == -1 || gettingDateString.equals(monthEnd) = "
                                    + (gettingDate.compareTo(monthStartDate) == 1 || gettingDateString.equals(monthStart) &&
                                    gettingDate.compareTo(monthEndDate) == -1 || gettingDateString.equals(monthEnd)));
                            Log.d("myLogs", "------------------------------------------------------------------------------------------------------------");


                            if ((gettingDate.compareTo(monthStartDate) == 1 || gettingDateString.equals(monthStart)) &&
                                    (gettingDate.compareTo(monthEndDate) == -1 || gettingDateString.equals(monthEnd))){
                                listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterGetGeneral = new StatisticAdapterGetGen(this, listGetGeneral, R.color.colorTextGreen);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterGetGeneral);
                } else {
//                    --------------------------- MONTH SPEND ---------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                            + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "spend"});

                    listSpendGeneral.removeAll(listSpendGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if ((gettingDate.compareTo(monthStartDate) == 1 || gettingDateString.equals(monthStart)) &&
                                    (gettingDate.compareTo(monthEndDate) == -1 || gettingDateString.equals(monthEnd))){
                                listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(this, listSpendGeneral, Color.RED);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);

                }
//                ----------------------------------------------------------------------------------------------------------------------


//                Определение текущего месяца
                calendar.setTime(date);
                int month = calendar.get(Calendar.MONTH);

                Log.d("myLogs", "month = " + month);
                break;

            case R.id.btnStatisticYear:
                Toast.makeText(this, "Текущий год", Toast.LENGTH_SHORT).show();

                format = new SimpleDateFormat("dd.MM.yyyy",  new Locale("ru", "RU"));

                calendar = Calendar.getInstance();

//                --------------------------------------- Рабочий ----------------------------------
                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR)); // это будет начало года
                Date yearStartDate = calendar.getTime();
                String yearStart = format.format(yearStartDate);

                calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); // это будет конец года
                Date yearEndDate = calendar.getTime();
                String yearEnd = format.format(yearEndDate);

                Log.d("myLogs", "yearStart = " + yearStart + " yearEnd = " + yearEnd);
//                ----------------------------------------------------------------------------------

//                ----------------------------------------------------------------------------------------------------------------------
                if (getAction.equals("get")){
//                    -------------------------- YEAR GET --------------------------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "get"});

                    listGetGeneral.removeAll(listGetGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }



                            if ((gettingDate.compareTo(yearStartDate) == 1 || gettingDateString.equals(yearStart)) &&
                                    (gettingDate.compareTo(yearEndDate) == -1 || gettingDateString.equals(yearEnd))){
                                listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterGetGeneral = new StatisticAdapterGetGen(this, listGetGeneral, R.color.colorTextGreen);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterGetGeneral);
                } else {
//                    --------------------------- YEAR SPEND ---------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND
                            + ", " + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "spend"});

                    listSpendGeneral.removeAll(listSpendGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if ((gettingDate.compareTo(yearStartDate) == 1 || gettingDateString.equals(yearStart)) &&
                                    (gettingDate.compareTo(yearEndDate) == -1 || gettingDateString.equals(yearEnd))){
                                listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(this, listSpendGeneral, Color.RED);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);

                }
//                ----------------------------------------------------------------------------------------------------------------------
                break;

            case R.id.btnStatisticQuarter:

                Toast.makeText(this, "Текущий квартал", Toast.LENGTH_SHORT).show();

                format = new SimpleDateFormat("dd.MM.yyyy",  new Locale("ru", "RU"));

                calendar = Calendar.getInstance();

                Log.d("myLogs", "q = " + getQuarter(date));


//                String testDate = "08.08.2017";  //  !!!!! Можно задать любую дату и определить даты недели !!!!!
//                Date docDate = null;
//                try {
//                    docDate= format.parse(testDate);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                calendar.setTime(docDate);


                String beginOfMonth = null;
                switch (getQuarter(date)){
                    case 1:
                        beginOfMonth = "01.01";  //  Январь
                        break;
                    case 2:
                        beginOfMonth = "01.04";  //  Апрель
                        break;
                    case 3:
                        beginOfMonth = "01.07";  //  Июль
                        break;
                    case 4:
                        beginOfMonth = "01.10";  //  Октябрь
                }

                int year = calendar.get(Calendar.YEAR);
                String beginQuarter = beginOfMonth + "." + year;   //  Определение даты начала квартала
                Log.d("myLogs", "beginQuarter = " + beginQuarter);


                Date beginQuarterDate = null;
                try {
                    beginQuarterDate = format.parse(beginQuarter);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                calendar.setTime(beginQuarterDate);
                calendar.add(Calendar.MONTH, +2);
                Date endBeginQuarterDate = calendar.getTime();  //  Дата начала последнего месяца квартала

                calendar.setTime(endBeginQuarterDate);


////                --------------------------------------- Рабочий ----------------------------------
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец квартала
                Date quarterEndDate = calendar.getTime();
                String endQuarter = format.format(quarterEndDate);

                Log.d("myLogs", "beginQuarter = " + beginQuarter + " endQuarter = " + endQuarter);
////                ----------------------------------------------------------------------------------

                if (getAction.equals("get")){
//                    -------------------------- QUARTER GET --------------------------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_GET + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + "=?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "get"});

                    listGetGeneral.removeAll(listGetGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }



                            if ((gettingDate.compareTo(beginQuarterDate) == 1 || gettingDateString.equals(beginQuarter)) &&
                                    (gettingDate.compareTo(quarterEndDate) == -1 || gettingDateString.equals(endQuarter))){
                                listGetGeneral.add(new ItemStatisticGetGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "+ " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterGetGeneral = new StatisticAdapterGetGen(this, listGetGeneral, R.color.colorTextGreen);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterGetGeneral);
                } else {
//                    --------------------------- QUARTER SPEND ---------------------------------------
                    String sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                            + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                            + DBHelper.KEY_STATISTIC_TIME
                            + " from " + DBHelper.TABLE_STATISTIC
                            + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? AND "
                            + DBHelper.KEY_STATISTIC_TYPE + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "spend"});

                    listSpendGeneral.removeAll(listSpendGeneral);

                    if (cursor.moveToFirst()) {
                        do {
                            String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                            Date gettingDate = null;
                            try {
                                gettingDate = format.parse(gettingDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if ((gettingDate.compareTo(beginQuarterDate) == 1 || gettingDateString.equals(beginQuarter)) &&
                                    (gettingDate.compareTo(quarterEndDate) == -1 || gettingDateString.equals(endQuarter))){
                                listSpendGeneral.add(new ItemStatisticSpendGen(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
                                        "- " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND))));

                                noDataView.setVisibility(View.GONE);
                            } else {
                                noDataView.setVisibility(View.VISIBLE);
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    statisticAdapterSpendGeneral = new StatisticAdapterSpendGen(this, listSpendGeneral, Color.RED);


                    lvItemStatistic = (ListView) findViewById(R.id.lvItemStatistic);
                    lvItemStatistic.setAdapter(statisticAdapterSpendGeneral);


//                    lvItemStatistic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            String[] sql = {"select " + DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY
//                                    + " from " + DBHelper.TABLE_SPEND_STATISTICS
//                                    + " where " + DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN + "=? AND " + DBHelper.KEY_SPEND_STATISTIC_NAME + " =?"
//                                    + " AND " + DBHelper.KEY_SPEND_STATISTIC_DATE + " =?"
//                                    + " AND " + DBHelper.KEY_SPEND_STATISTIC_TIME + " =?"};
//                            Cursor[] cursor = {db.rawQuery(sql[0], new String[]{login, btnName, list.get(position).getDate(), list.get(position).getTime()})};
//
//                            typePay = null;
//                            if (cursor[0].moveToFirst()) {
//                                do {
//                                    typePay = cursor[0].getString(cursor[0].getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY));
//                                } while (cursor[0].moveToNext());
//                            }
//                            cursor[0].close();
//
//
//                            Toast.makeText(ItemStatisticActivity.this, "Расплатился: " + typePay, Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }




                break;

            case R.id.btnStatisticAnyDay:
                showDialog(DIALOG_DATE);

                Log.d("myLogs", "getNewDateDialog = " + getNewDateDialog);
                break;
        }
    }

//    Функция определяющая номер квартала
    public static int getQuarter(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        int month = c.get(Calendar.MONTH) + 1;
        int quarter;
        if (month < 4) quarter = 1;
        else if (month >= 4 && month < 7) quarter = 2;
        else if (month >= 7 && month < 10) quarter = 3;
        else quarter = 4;
        return quarter;
    }
}
