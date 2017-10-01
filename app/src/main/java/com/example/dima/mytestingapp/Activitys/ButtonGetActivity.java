package com.example.dima.mytestingapp.Activitys;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ButtonGetActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvBtnGetName;

    EditText editTextMyGet;

    Button btnGetApply;
    Button btnGetCancel;
    Button btnGetStatistics;
    Button btnGetChart;

    String btnName;

    final int REQUEST_CODE_BTN_RELOAD = 1;

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;
    String login;

    AlertDialog.Builder ad;   //  Диалог
    AlertDialog.Builder adSave;   //  Диалог на сохранение

    Retrofit.Builder builder;
    Retrofit retrofit;
    ServerApi serverApi;

    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_get);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    //   Стрелка
//        toolbar.setNavigationIcon(R.drawable.ic_action_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ButtonGetActivity.this, FragmentMain.class);
//                startActivity(intent);

                Intent intent = new Intent(ButtonGetActivity.this, UserActivity.class);
                intent.putExtra("name_fragment", "fmain");
                startActivity(intent);

                finish();
            }
        });



        builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);



        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");


        editTextMyGet = (EditText) findViewById(R.id.editTextMyGet);

        btnGetApply = (Button) findViewById(R.id.btnGetApply);
        btnGetApply.setOnClickListener(this);
        btnGetCancel = (Button) findViewById(R.id.btnGetCancel);
        btnGetCancel.setOnClickListener(this);
        btnGetStatistics = (Button) findViewById(R.id.btnGetStatistics);
        btnGetStatistics.setOnClickListener(this);
        btnGetChart = (Button) findViewById(R.id.btnGetChart);
        btnGetChart.setOnClickListener(this);

        tvBtnGetName = (TextView) findViewById(R.id.tvBtnGetName);

        final Intent intent = getIntent();
        btnName = intent.getStringExtra("btnNameGet");

        tvBtnGetName.setText("Название раздела - " +  btnName);

        btnGetChart.setText("График по " + btnName);

        btnGetStatistics.setText("Статистика по " + btnName);


        cratedActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();

        cratedActivity();
    }


    public void cratedActivity(){

        String onGetCount = null;
        String onGetTotalCount = null;

//        ---------- Извлечение переменной которая показывает текущее значение на счету ---------
        String sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + "=? AND " + DBHelper.KEY_GET_NAME + " =? AND "
                + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
        Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "deleted"});

        if (cursor.moveToFirst()) {
            do {
                onGetCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                Log.d("myLogs", "onGetCount = " + onGetCount);
            } while (cursor.moveToNext());
        }
        cursor.close();


        sql = "select " + DBHelper.KEY_KOL_TOTAL_GET + " from " + DBHelper.TABLE_GET_FOR_TOTAL
                + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + "=?";
        cursor = db.rawQuery(sql, new String[]{login});

        if (cursor.moveToFirst()) {
            do {
                onGetTotalCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_TOTAL_GET));
                Log.d("myLogs", "onGetTotalCount = " + onGetTotalCount);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        ------------------------------------------------------------------------------------------

        editTextMyGet.setHint(onGetCount + " руб.");




//        --------------------------- Всплывающее окно Alert Dialog -----------------------
//        Context context = ButtonGetActivity.this;
//        String title = "Удаление элемента " + btnName;
//        String message = "Вы действительно хотите удалить данный элемент? Все данные будут утеряны!\n " +
//                "Либо Вы можите перевести средства на другой счет!!!";
//        String buttonYes = "Да";
//        String buttonNo = "Нет";
//        String buttonTransact = "Перевести";
//
//        ad = new AlertDialog.Builder(context);
//        ad.setTitle(title);
//        ad.setMessage(message);
//        ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String sql = "delete from " + DBHelper.TABLE_GET + " where " + DBHelper.KEY_REF_LOGIN + " = '"
//                        + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
//                db.execSQL(sql);
//
//                Intent intent = new Intent();
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
//
//        ad.setNeutralButton(buttonTransact, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(ButtonGetActivity.this, MoneyTransactionActivity.class);
//                intent.putExtra("btnName", btnName);
//                startActivityForResult(intent, REQUEST_CODE_BTN_RELOAD);
//            }
//        });
//
//        ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });




        Context context = ButtonGetActivity.this;
        String title = "Удаление элемента " + btnName;
        String message = "Вы действительно хотите удалить данный элемент? Все данные будут утеряны!\n" +
                "Вы также можите перевести средства на другой счет!!!";
        String buttonYes = "Да";
        String buttonNo = "Нет";
        String buttonTransact = "Перевести";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Call<Void> callDeleteGet = serverApi.deleteFromTableGet("get", btnName, login);

                callDeleteGet.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "delete from " + DBHelper.TABLE_GET + " where " + DBHelper.KEY_REF_LOGIN + " = '"
                                + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'deleted' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
                        db.execSQL(sql);

                    }
                });

//                        --------------- Уменьшение значения из таблицы TotalGet
//                    на ту сумму которая была получена в текущем месяце ---------------------------

                Calendar calendar = Calendar.getInstance();

                int monthNow = calendar.get(Calendar.MONTH) + 1;

//                        Выбираем сумму из TotalGet которая в данный момент на счету
                String sql = "select " + DBHelper.KEY_KOL_TOTAL_GET + " from " + DBHelper.TABLE_GET_FOR_TOTAL
                        + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = ?";
                Cursor cursor = db.rawQuery(sql, new String[]{login});

                String kolTotalGet = "0";  //  Сумму из TotalGet которая в данный момент на счету
                if (cursor.moveToFirst()) {
                    do {
                        kolTotalGet = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_TOTAL_GET));
                    } while (cursor.moveToNext());
                }
                cursor.close();

//                        Выбираем сумму из TableStatistic по данному элементу за текущий месяц по "get"
                Double kolGetMonth = 0.0;  //  Сумму из TableStatistic по данному элементу за текущий месяц
                String selectDate;  //  Месяц даты из БД   (Например: 08)
                sql = "select " + DBHelper.KEY_STATISTIC_KOL + ", " + DBHelper.KEY_STATISTIC_DATE
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + " =? and "
                        + DBHelper.KEY_STATISTIC_TYPE + " =? and " + DBHelper.KEY_STATISTIC_NAME_GET + " =?";
                cursor = db.rawQuery(sql, new String[]{login, "get", btnName});

                if (cursor.moveToFirst()) {
                    do {
                        selectDate = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                        selectDate = selectDate.substring(3, 5);
                        Log.d("myLogsN", "selectDate = " + selectDate + " monthNow = " + monthNow);

                        if (Integer.parseInt(selectDate) == monthNow){
                            kolGetMonth = kolGetMonth + Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)));
                        }

                        Log.d("myLogsN", "kolGetMonth = " + kolGetMonth);

                    } while (cursor.moveToNext());
                }
                cursor.close();

//                        Log.d("myLogsN", "Double.parseDouble(kolTotalGet) = " + Double.parseDouble(kolTotalGet) + " kolGetMonth = " + kolGetMonth);


//                        Выбираем сумму из TableStatistic по данному элементу за текущий месяц по "transaction"
                Double kolTransactionMonth = 0.0;  //  Сумма по "transaction" из TableStatistic по данному элементу за текущий месяц
                sql = "select " + DBHelper.KEY_STATISTIC_KOL + ", " + DBHelper.KEY_STATISTIC_DATE
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + " =? and "
                        + DBHelper.KEY_STATISTIC_TYPE + " =? and " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =?";
                cursor = db.rawQuery(sql, new String[]{login, "transaction", btnName});

                if (cursor.moveToFirst()) {
                    do {
                        selectDate = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                        selectDate = selectDate.substring(3, 5);
                        Log.d("myLogsN", "selectDate = " + selectDate + " monthNow = " + monthNow);

                        if (Integer.parseInt(selectDate) == monthNow){
                            kolTransactionMonth = kolTransactionMonth + Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)));
                        }

                        Log.d("myLogsN", "kolTransactionMonth = " + kolTransactionMonth);

                    } while (cursor.moveToNext());
                }
                cursor.close();

//                        Log.d("myLogsN", "Double.parseDouble(kolTotalGet) = " + Double.parseDouble(kolTotalGet) + " kolTransactionMonth = " + kolTransactionMonth);



//                        Обновляем TotalGet вычитаем из общей суммы TotalGet сумму элемента за текущий месяц
                sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_KOL_TOTAL_GET + " = "
                        + String.valueOf(Double.parseDouble(kolTotalGet) - (kolGetMonth - kolTransactionMonth)) + " where "
                        + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                db.execSQL(sql);

                Call<Void> callLessening = serverApi.lesseningTotal(
                        String.valueOf(Double.parseDouble(kolTotalGet) - (kolGetMonth - kolTransactionMonth)),
                        login);

                callLessening.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'no' where "
                                + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                        db.execSQL(sql);

                    }
                });

//                        ------------------------------------------------------------------------------


                Intent intent = new Intent(ButtonGetActivity.this, UserActivity.class);
                intent.putExtra("name_fragment", "fmain");
                startActivity(intent);
            }
        });

        ad.setNeutralButton(buttonTransact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ButtonGetActivity.this, MoneyTransactionActivity.class);
                intent.putExtra("btnName", btnName);
                startActivityForResult(intent, REQUEST_CODE_BTN_RELOAD);
            }
        });

        ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });












//        -----------------------------------------------------------------------------------------

//        --------------------------- Всплывающее окно Alert Dialog SAVE !!! -----------------------
        context = ButtonGetActivity.this;
        String titleSave = "Сохранить изменения в " + btnName;
        String messageSave = "Сохранить введенные данные?";
        String buttonSaveYes = "Да";
        String buttonSaveNo = "Нет";

        adSave = new AlertDialog.Builder(context);
        adSave.setTitle(titleSave);
        adSave.setMessage(messageSave);
        adSave.setPositiveButton(buttonSaveYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sPrefLogin = getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
                login = sPrefLogin.getString("save_login", "");

                String onGetCount = "0";
                String onGetTotalCount = "0";

//        ---------- Извлечение переменной которая показывает текущее значение на счету ---------
                String sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                        + " where " + DBHelper.KEY_REF_LOGIN + "=? AND " + DBHelper.KEY_GET_NAME + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName});

                if (cursor.moveToFirst()) {
                    do {
                        onGetCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                        Log.d("myLogs", "onGetCount = " + onGetCount);
                    } while (cursor.moveToNext());
                }
                cursor.close();



                sql = "select " + DBHelper.KEY_KOL_TOTAL_GET + " from " + DBHelper.TABLE_GET_FOR_TOTAL
                        + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + "=?";
                cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.moveToFirst()) {
                    do {
                        onGetTotalCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_TOTAL_GET));
                        Log.d("myLogs", "onGetTotalCount = " + onGetTotalCount);
                    } while (cursor.moveToNext());
                }
                cursor.close();
//        ------------------------------------------------------------------------------------------

                Log.d("myLogs", "login = " + login + " btnName = " + btnName + " editTextMyGet = " + editTextMyGet.getText().toString());

                double count = 0;
                double countTotalGet = 0;

//                    Добавление к предыдущему значению
                count = Double.parseDouble(onGetCount) + Double.parseDouble(editTextMyGet.getText().toString());
                countTotalGet = Double.parseDouble(onGetTotalCount) + Double.parseDouble(editTextMyGet.getText().toString());

//                    Обновление таблицы GET
                sql = "UPDATE " + DBHelper.TABLE_GET + " SET " + DBHelper.KEY_KOL_GET + " = "
                        + Double.toString(count) + " WHERE "
                        + DBHelper.KEY_REF_LOGIN + " = '" + login + "' AND "
                        + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
                db.execSQL(sql);

//                Обновление количества средств на сервере Get
                Call<Void> call = serverApi.setKolBtn("table_get", Double.toString(count), login,
                        btnName, "", "", "");

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                        db.execSQL(sql);

                    }
                });

//                Обновление таблицы GetTotal
                sql = "UPDATE " + DBHelper.TABLE_GET_FOR_TOTAL + " SET " + DBHelper.KEY_KOL_TOTAL_GET + " = "
                        + Double.toString(countTotalGet) + " WHERE "
                        + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                db.execSQL(sql);

//                Обновление количества средств на сервере Total
                call = serverApi.setKolBtn("total_get", "", login,
                        "", Double.toString(countTotalGet), "", "");

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'no' where "
                                + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                        db.execSQL(sql);

                    }
                });




//                    ---- Добавление в таблицу GetStatistic каждой операции с датой и времением ----

//                    Текущая дата
                Date date = new Date();
                Calendar calendar = Calendar.getInstance();
//                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String dateNow = formatDate.format(date);
                DateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String timeNow = formatTime.format(date);

//                -------------------- TABLE_GET_STATISTIC -------------------------
//                ContentValues contentValues = new ContentValues();
//
//                contentValues.put(DBHelper.KEY_GET_STATISTIC_NAME, btnName);
//                contentValues.put(DBHelper.KEY_GET_STATISTIC_KOL, editTextMyGet.getText().toString());
//                contentValues.put(DBHelper.KEY_GET_STATISTIC_DATE, dateNow);
//                contentValues.put(DBHelper.KEY_GET_STATISTIC_TIME, timeNow);
//                contentValues.put(DBHelper.KEY_GET_STATISTIC_REF_LOGIN, login);
//
//                db.insert(DBHelper.TABLE_GET_STATISTICS, null, contentValues);
//
//                contentValues.clear();

//                ------------------ TABLE_STATISTIC --------------------------------
                ContentValues contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_STATISTIC_TYPE, "get");
                contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, btnName);
                contentValues.put(DBHelper.KEY_STATISTIC_KOL, editTextMyGet.getText().toString());
                contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, "");
                contentValues.put(DBHelper.KEY_STATISTIC_DATE, dateNow);
                contentValues.put(DBHelper.KEY_STATISTIC_TIME, timeNow);
                contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "ins");

                id = db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);


//                Запись статистики на сервер
                call = serverApi.addStatistic("get", btnName, editTextMyGet.getText().toString(), "",
                        dateNow, timeNow, login, String.valueOf(id));

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_STATISTIC_ID + " = '" + id + "' and "
                                + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'no' where "
                                + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_STATISTIC_ID + " = '" + id + "' and "
                                + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                        db.execSQL(sql);

                    }
                });
//                    -------------------------------------------------------------------------------

//                cursor = db.query(DBHelper.TABLE_GET_STATISTICS, null, null, null, null, null, null);
//
//
//                if (cursor.moveToFirst()) {
//                    int getIndexId = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_ID);
//                    int getIndexName = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_NAME);
//                    int getIndexKol = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_KOL);
//                    int getIndexDate = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_DATE);
//                    int getIndexTime = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_TIME);
//                    int getIndexRefLogin = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_REF_LOGIN);
//                    do {
//                        int getId = cursor.getInt(getIndexId);
//                        String getName = cursor.getString(getIndexName);
//                        String getKol = cursor.getString(getIndexKol);
//                        String getDate = cursor.getString(getIndexDate);
//                        String getTime = cursor.getString(getIndexTime);
//                        String getRefLogin = cursor.getString(getIndexRefLogin);
//
//                        Log.d("myLogs", "getId = " + getId + " getName = " + getName
//                                + " getKol = " + getKol
//                                + " getDate = " + getDate + " getTime = " + getTime
//                                + " getRefLogin = " + getRefLogin);
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();

                cursor = db.query(DBHelper.TABLE_STATISTIC, null, null, null, null, null, null);


                if (cursor.moveToFirst()) {
                    int getIndexId = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID);
                    int getIndexType = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE);
                    int getIndexName = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET);
                    int getIndexKol = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL);
                    int getIndexTypePay = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND);
                    int getIndexDate = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE);
                    int getIndexTime = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME);
                    int getIndexRefLogin = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_REF_LOGIN);
                    do {
                        int getId = cursor.getInt(getIndexId);
                        String getType = cursor.getString(getIndexType);
                        String getName = cursor.getString(getIndexName);
                        String getKol = cursor.getString(getIndexKol);
                        String getTypePay = cursor.getString(getIndexTypePay);
                        String getDate = cursor.getString(getIndexDate);
                        String getTime = cursor.getString(getIndexTime);
                        String getRefLogin = cursor.getString(getIndexRefLogin);

                        Log.d("myLogs", "getIdStatistic = " + getId + " getTypeStatistic = " + getType
                                + " getNameStatistic = " + getName
                                + " getKolStatistic = " + getKol
                                + " getTypePayStatistic = " + getTypePay
                                + " getDateStatistic = " + getDate + " getTimeStatistic = " + getTime
                                + " getRefLoginStatistic = " + getRefLogin);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                editTextMyGet.setText("");
                Intent intent = new Intent(ButtonGetActivity.this, ItemStatisticActivity.class);
                intent.putExtra("btnName", btnName);
                intent.putExtra("action_key", "get");
                startActivity(intent);

//                    setResult(RESULT_OK, intent);
//                    finish();
            }
        });

        adSave.setNegativeButton(buttonSaveNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editTextMyGet.setText("");
                Intent intent = new Intent(ButtonGetActivity.this, ItemStatisticActivity.class);
                intent.putExtra("btnName", btnName);
                intent.putExtra("action_key", "get");
                startActivity(intent);
            }
        });

//        -----------------------------------------------------------------------------------------


    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnGetApply:
                sPrefLogin = getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
                login = sPrefLogin.getString("save_login", "");

                String onGetCount = "0";
                String onGetTotalCount = "0";

//        ---------- Извлечение переменной которая показывает текущее значение на счету ---------
                String sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                        + " where " + DBHelper.KEY_REF_LOGIN + "=? AND " + DBHelper.KEY_GET_NAME + " =? and "
                        + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "deleted"});

                if (cursor.moveToFirst()) {
                    do {
                        onGetCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                        Log.d("myLogs", "onGetCount = " + onGetCount);
                    } while (cursor.moveToNext());
                }
                cursor.close();



                sql = "select " + DBHelper.KEY_KOL_TOTAL_GET + " from " + DBHelper.TABLE_GET_FOR_TOTAL
                        + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + "=?";
                cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.moveToFirst()) {
                    do {
                        onGetTotalCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_TOTAL_GET));
                        Log.d("myLogs", "onGetTotalCount = " + onGetTotalCount);
                    } while (cursor.moveToNext());
                }
                cursor.close();
//        ------------------------------------------------------------------------------------------

                Log.d("myLogs", "login = " + login + " btnName = " + btnName + " editTextMyGet = " + editTextMyGet.getText().toString());

                double count = 0;
                double countTotalGet = 0;

                try{
//                    Добавление к предыдущему значению
                    count = Double.parseDouble(onGetCount) + Double.parseDouble(editTextMyGet.getText().toString());
                    countTotalGet = Double.parseDouble(onGetTotalCount) + Double.parseDouble(editTextMyGet.getText().toString());

//                    Обновление таблицы GET
                    sql = "UPDATE " + DBHelper.TABLE_GET + " SET " + DBHelper.KEY_KOL_GET + " = "
                            + Double.toString(count) + " WHERE "
                            + DBHelper.KEY_REF_LOGIN + " = '" + login + "' AND "
                            + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
                    db.execSQL(sql);


//                Обновление количества средств на сервере Get
                    Call<Void> call = serverApi.setKolBtn("table_get", Double.toString(count), login,
                            btnName, "", "", "");

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }
                    });


//                Обновление таблицы GetTotal
                    sql = "UPDATE " + DBHelper.TABLE_GET_FOR_TOTAL + " SET " + DBHelper.KEY_KOL_TOTAL_GET + " = "
                            + Double.toString(countTotalGet) + " WHERE "
                            + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                    db.execSQL(sql);


//                Обновление количества средств на сервере Total
                    call = serverApi.setKolBtn("total_get", "", login,
                            "", Double.toString(countTotalGet), "", "");

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            String sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                            db.execSQL(sql);

                        }
                    });

//                    ---- Добавление в таблицу GetStatistic каждой операции с датой и времением ----

//                    Текущая дата
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
//                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String dateNow = formatDate.format(date);
                    DateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String timeNow = formatTime.format(date);

//                    ---------------------- TABLE_GET_STATISTIC ---------------------
//                    ContentValues contentValues = new ContentValues();
//
//                    contentValues.put(DBHelper.KEY_GET_STATISTIC_NAME, btnName);
//                    contentValues.put(DBHelper.KEY_GET_STATISTIC_KOL, editTextMyGet.getText().toString());
//                    contentValues.put(DBHelper.KEY_GET_STATISTIC_DATE, dateNow);
//                    contentValues.put(DBHelper.KEY_GET_STATISTIC_TIME, timeNow);
//                    contentValues.put(DBHelper.KEY_GET_STATISTIC_REF_LOGIN, login);
//
//                    db.insert(DBHelper.TABLE_GET_STATISTICS, null, contentValues);
//
//                    contentValues.clear();


//                ------------------ TABLE_STATISTIC --------------------------------
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_STATISTIC_TYPE, "get");
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, btnName);
                    contentValues.put(DBHelper.KEY_STATISTIC_KOL, editTextMyGet.getText().toString());
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, "");
                    contentValues.put(DBHelper.KEY_STATISTIC_DATE, dateNow);
                    contentValues.put(DBHelper.KEY_STATISTIC_TIME, timeNow);
                    contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                    contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "ins");

                    id = db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);

                    contentValues.clear();

//                      Запись статистики на сервер
                    call = serverApi.addStatistic("get", btnName, editTextMyGet.getText().toString(), "",
                            dateNow, timeNow, login, String.valueOf(id));

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_STATISTIC_ID + " = '" + id + "' and "
                                    + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_STATISTIC_ID + " = '" + id + "' and "
                                    + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
//                                    + DBHelper.KEY_STATISTIC_NAME_GET + " = '" + btnName + "'";
                            db.execSQL(sql);

                        }
                    });
//                    -------------------------------------------------------------------------------


////                    ------------------- TABLE_STATISTIC -------------------------
//                    contentValues = new ContentValues();
//
//                    contentValues.put(DBHelper.KEY_STATISTIC_TYPE, "get");
//                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, btnName);
//                    contentValues.put(DBHelper.KEY_STATISTIC_KOL, editTextMyGet.getText().toString());
//                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, "");
//                    contentValues.put(DBHelper.KEY_STATISTIC_DATE, dateNow);
//                    contentValues.put(DBHelper.KEY_STATISTIC_TIME, timeNow);
//                    contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
//
//                    db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);

//                    Пример обновления
//                    UPDATE spend_statistics SET date_stat_spend =  datetime(strftime('%s','now'), 'unixepoch') WHERE _id = (SELECT _id FROM spend_statistics ORDER BY _id DESC LIMIT 1)
//                    db.execSQL("UPDATE " + DBHelper.TABLE_GET_STATISTICS + " SET " + DBHelper.KEY_GET_STATISTIC_DATE + " = "
//                            + " datetime(strftime('%s','now'), 'unixepoch') WHERE " + DBHelper.KEY_GET_STATISTIC_ID + " = ("
//                            + "SELECT " + DBHelper.KEY_GET_STATISTIC_ID + " FROM " + DBHelper.TABLE_GET_STATISTICS + " ORDER BY "
//                            + DBHelper.KEY_GET_STATISTIC_ID + " DESC LIMIT 1)");
//                    -------------------------------------------------------------------------------

//                    cursor = db.query(DBHelper.TABLE_GET_STATISTICS, null, null, null, null, null, null);
//
//
//                    if (cursor.moveToFirst()) {
//                        int getIndexId = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_ID);
//                        int getIndexName = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_NAME);
//                        int getIndexKol = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_KOL);
//                        int getIndexDate = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_DATE);
//                        int getIndexTime = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_TIME);
//                        int getIndexRefLogin = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_REF_LOGIN);
//                        do {
//                            int getId = cursor.getInt(getIndexId);
//                            String getName = cursor.getString(getIndexName);
//                            String getKol = cursor.getString(getIndexKol);
//                            String getDate = cursor.getString(getIndexDate);
//                            String getTime = cursor.getString(getIndexTime);
//                            String getRefLogin = cursor.getString(getIndexRefLogin);
//
//                            Log.d("myLogs", "getId = " + getId + " getName = " + getName
//                                    + " getKol = " + getKol
//                                    + " getDate = " + getDate + " getTime = " + getTime
//                                    + " getRefLogin = " + getRefLogin);
//                        } while (cursor.moveToNext());
//                    }
//                    cursor.close();

//                    cursor = db.query(DBHelper.TABLE_STATISTIC, null, null, null, null, null, null);
//
//
//                    if (cursor.moveToFirst()) {
//                        int getIndexId = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_ID);
//                        int getIndexType = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE);
//                        int getIndexName = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET);
//                        int getIndexKol = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL);
//                        int getIndexTypePay = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND);
//                        int getIndexDate = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE);
//                        int getIndexTime = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME);
//                        int getIndexRefLogin = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_REF_LOGIN);
//                        do {
//                            int getId = cursor.getInt(getIndexId);
//                            String getType = cursor.getString(getIndexType);
//                            String getName = cursor.getString(getIndexName);
//                            String getKol = cursor.getString(getIndexKol);
//                            String getTypePay = cursor.getString(getIndexTypePay);
//                            String getDate = cursor.getString(getIndexDate);
//                            String getTime = cursor.getString(getIndexTime);
//                            String getRefLogin = cursor.getString(getIndexRefLogin);
//
//                            Log.d("myLogs", "getIdStatistic = " + getId + " getTypeStatistic = " + getType
//                                    + " getNameStatistic = " + getName
//                                    + " getKolStatistic = " + getKol
//                                    + " getTypePayStatistic = " + getTypePay
//                                    + " getDateStatistic = " + getDate + " getTimeStatistic = " + getTime
//                                    + " getRefLoginStatistic = " + getRefLogin);
//                        } while (cursor.moveToNext());
//                    }
//                    cursor.close();


                    cursor = db.query(DBHelper.TABLE_STATISTIC, null, null, null, null, null, null);


                    if (cursor.moveToFirst()) {
                        int getIndexId = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID);
                        int getIndexType = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE);
                        int getIndexName = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET);
                        int getIndexKol = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL);
                        int getIndexTypePay = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND);
                        int getIndexDate = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE);
                        int getIndexTime = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME);
                        int getIndexRefLogin = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_REF_LOGIN);
                        do {
                            int getId = cursor.getInt(getIndexId);
                            String getType = cursor.getString(getIndexType);
                            String getName = cursor.getString(getIndexName);
                            String getKol = cursor.getString(getIndexKol);
                            String getTypePay = cursor.getString(getIndexTypePay);
                            String getDate = cursor.getString(getIndexDate);
                            String getTime = cursor.getString(getIndexTime);
                            String getRefLogin = cursor.getString(getIndexRefLogin);

                            Log.d("myLogs", "getIdStatistic = " + getId + " getTypeStatistic = " + getType
                                    + " getNameStatistic = " + getName
                                    + " getKolStatistic = " + getKol
                                    + " getTypePayStatistic = " + getTypePay
                                    + " getDateStatistic = " + getDate + " getTimeStatistic = " + getTime
                                    + " getRefLoginStatistic = " + getRefLogin);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();


//                    ArrayList<Integer> list = new ArrayList<>();
//
//                    cursor = db.query(DBHelper.TABLE_GET_STATISTICS, null, null, null, null, null, null);
//                    if (cursor.moveToFirst()) {
//                        int getIndexDateMilliseconds = cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_DATE_MILLISECONDS);
//                        do {
//                            int getDateMilliseconds = cursor.getInt(getIndexDateMilliseconds);
//                            list.add(getDateMilliseconds);
//                        } while (cursor.moveToNext());
//                    }
//                    cursor.close();
//
//                    cursor = db.query(DBHelper.TABLE_SPEND_STATISTICS, null, null, null, null, null, null);
//                    if (cursor.moveToFirst()) {
//                        int getIndexDateMilliseconds = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_DATE_MILLISECONDS);
//                        do {
//                            int getDateMilliseconds = cursor.getInt(getIndexDateMilliseconds);
//                            list.add(getDateMilliseconds);
//                        } while (cursor.moveToNext());
//                    }
//                    cursor.close();
//
//                    for (int i = 0; i < list.size(); i++){
//                        Log.d("myLogs", "i = " + i + " item = " + list.get(i));
//                    }
//                    Collections.sort(list);  //  Сортировка по возростанию
//
//                    Log.d("myLogs", "Collections Sort");
//                    for (int i = 0; i < list.size(); i++){
//                        Log.d("myLogs", "i = " + i + " item = " + list.get(i));
//                    }
//
//
//                    String getName;
//                    sql = "select " + DBHelper.KEY_GET_STATISTIC_NAME + " OR " + DBHelper.KEY_SPEND_STATISTIC_NAME
//                            + " from " + DBHelper.TABLE_GET_STATISTICS + ", " + DBHelper.TABLE_SPEND_STATISTICS
//                            + " where " + DBHelper.KEY_GET_STATISTIC_REF_LOGIN + "=? OR "
//                            + DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN + "=?";
//                    cursor = db.rawQuery(sql, new String[]{login});
//
//                    if (cursor.moveToFirst()) {
//                        do {
//                            getName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_STATISTIC_NAME));
//                            Log.d("myLogs", "Name = " + getName);
//                        } while (cursor.moveToNext());
//                    }
//                    cursor.close();




                    setResult(RESULT_OK, intent);
                    finish();

                } catch (java.lang.NumberFormatException e){
                    Toast.makeText(this, "Заполните поля ввода !!!", Toast.LENGTH_SHORT).show();
                    editTextMyGet.setText("");
                }

                break;

            case R.id.btnGetCancel:
                finish();
                break;

            case R.id.btnGetStatistics:
                if (editTextMyGet.getText().toString().equals(".")){
                    editTextMyGet.setText("");
                }

                if (!editTextMyGet.getText().toString().equals("")){
                    adSave.show();
                } else {
                    intent = new Intent(ButtonGetActivity.this, ItemStatisticActivity.class);
                    intent.putExtra("btnName", btnName);
                    intent.putExtra("action_key", "get");
                    startActivity(intent);
                }
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.button_editing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_editing){
            Toast.makeText(this, "Editing", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, EditingActivity.class);
            intent.putExtra("action_key", "action_get");
            intent.putExtra("btnName", btnName);
//            startActivity(intent);
            startActivityForResult(intent, REQUEST_CODE_BTN_RELOAD);
//            finish();
        } else if (id == R.id.action_editing_last_record){
            Intent intent = new Intent(this, EditingLastRecord.class);
            intent.putExtra("btnName", btnName);
            startActivityForResult(intent, REQUEST_CODE_BTN_RELOAD);
        } else if (id == R.id.action_money_transaction){
            Intent intent = new Intent(this, MoneyTransactionActivity.class);
            intent.putExtra("btnName", btnName);
            startActivityForResult(intent, REQUEST_CODE_BTN_RELOAD);
        } else if (id == R.id.action_delete){
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();

            ad.show();  //  Показ диалога на удаление
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ButtonGetActivity.this, UserActivity.class);
        intent.putExtra("name_fragment", "fmain");
        startActivity(intent);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent intent = new Intent();

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_BTN_RELOAD:
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }
}
