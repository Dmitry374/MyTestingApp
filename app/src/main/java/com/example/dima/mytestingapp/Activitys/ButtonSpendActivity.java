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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Adapters.SpinnerAdapterWithDB;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.ItemDataWithDB;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import java.text.DateFormat;
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

import static java.lang.Thread.currentThread;

public class ButtonSpendActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvBtnSpendName;

    EditText editTextMySpend;

    Spinner spinnerWhatPaySpend;

    Button btnSpendApply;
    Button btnSpendCancel;
    Button btnSpendStatistics;
//    Button btnSpendChart;

    String btnName;

    final int REQUEST_CODE_BTN_RELOAD_SPEND = 2;

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;
    String login;

    String selectSpinnerItem;  //  Выбранный элемент Spinner

    AlertDialog.Builder ad;   //  Диалог
    AlertDialog.Builder adSave;   //  Диалог на сохранение

    Retrofit.Builder builder;
    Retrofit retrofit;
    ServerApi serverApi;

    long id;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_spend);
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ButtonGetActivity.this, FragmentMain.class);
//                startActivity(intent);

                Intent intent = new Intent(ButtonSpendActivity.this, UserActivity.class);
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


        editTextMySpend = (EditText) findViewById(R.id.editTextMySpend);

        spinnerWhatPaySpend = (Spinner) findViewById(R.id.spinnerWhatPaySpend);

        btnSpendApply = (Button) findViewById(R.id.btnSpendApply);
        btnSpendApply.setOnClickListener(this);
        btnSpendCancel = (Button) findViewById(R.id.btnSpendCancel);
        btnSpendCancel.setOnClickListener(this);
        btnSpendStatistics = (Button) findViewById(R.id.btnSpendStatistics);
        btnSpendStatistics.setOnClickListener(this);
//        btnSpendChart = (Button) findViewById(R.id.btnSpendChart);
//        btnSpendChart.setOnClickListener(this);

        tvBtnSpendName = (TextView) findViewById(R.id.tvBtnSpendName);

        final Intent intent = getIntent();
        btnName = intent.getStringExtra("btnNameSpend");

        tvBtnSpendName.setText("Название раздела - " +  btnName);

//        btnSpendChart.setText("График по " + btnName);

        btnSpendStatistics.setText("Статистика по " + btnName);




        //        --------------------------- Всплывающее окно Alert Dialog -----------------------
//        Context context = ButtonSpendActivity.this;
//        final String title = "Удаление элемента " + btnName;
//        String message = "Вы действительно хотите удалить данный элемент? Все данные будут утеряны!";
//        String buttonYes = "Да";
//        String buttonNo = "Нет";
//
//        ad = new AlertDialog.Builder(context);
//        ad.setTitle(title);
//        ad.setMessage(message);
//        ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String sql = "delete from " + DBHelper.TABLE_SPEND + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = '"
//                        + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "'";
//                db.execSQL(sql);
//
//                Intent intent = new Intent();
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });
//
//        ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });


        cratedActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();

        cratedActivity();
    }

    public void cratedActivity(){

        Context context = ButtonSpendActivity.this;
        String title = "Удаление элемента " + btnName;
        String message = "Вы действительно хотите удалить данный элемент? Все данные будут утеряны!";
        String buttonYes = "Да";
        String buttonNo = "Нет";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Call<Void> callDeleteGet = serverApi.deleteFromTableGet("spend", btnName, login);

                callDeleteGet.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "delete from " + DBHelper.TABLE_SPEND + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = '"
                                + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'deleted' where "
                                + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "'";
                        db.execSQL(sql);

                    }
                });


                Intent intent = new Intent(ButtonSpendActivity.this, UserActivity.class);
                intent.putExtra("name_fragment", "fmain");
                startActivity(intent);
            }
        });

        ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });









//        -----------------------------------------------------------------------------------------



//        --------------------------- Всплывающее окно Alert Dialog SAVE !!! -----------------------
        context = ButtonSpendActivity.this;
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

                String onSpendCount = null;


//                ------------------ Извлечение значения onCount из таблицы Get --------------------
                String onGetCount = null;

                String sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                        + " where " + DBHelper.KEY_REF_LOGIN + " =? AND " + DBHelper.KEY_GET_NAME + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, selectSpinnerItem});

                if (cursor.moveToFirst()) {
                    do {
                        onGetCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                        Log.d("myLogs", "onGetCount = " + onGetCount);
                    } while (cursor.moveToNext());
                }
                cursor.close();
//                ----------------------------------------------------------------------------------


//        ---------- Извлечение переменной которая показывает текущее значение на счету ---------
                sql = "select " + DBHelper.KEY_KOL_SPEND + " from " + DBHelper.TABLE_SPEND
                        + " where " + DBHelper.KEY_REF_LOGIN_SPEND + "=? AND " + DBHelper.KEY_SPEND_NAME + " =?";
                cursor = db.rawQuery(sql, new String[]{login, btnName});

                if (cursor.moveToFirst()) {
                    do {
                        onSpendCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                        Log.d("myLogs", "onGetCount = " + onSpendCount);
                    } while (cursor.moveToNext());
                }
                cursor.close();
//        ------------------------------------------------------------------------------------------

//                Подсказка сколько в данный момент на счету
                editTextMySpend.setHint(onSpendCount + " руб.");

                Log.d("myLogs", "login = " + login + " btnName = " + btnName + " editTextMyGet = " + editTextMySpend.getText().toString());

                double num = Double.parseDouble(editTextMySpend.getText().toString());

//                Добавление к предыдущему значению
                double count = Double.parseDouble(onSpendCount) + Double.parseDouble(editTextMySpend.getText().toString());

                Log.d("myLogs", "count = " + count + " onGetCount = " + Double.parseDouble(onGetCount));

                if (Double.parseDouble(editTextMySpend.getText().toString()) > Double.parseDouble(onGetCount)){
                    Toast.makeText(ButtonSpendActivity.this, "В разделе " + selectSpinnerItem + " недостаточно средств", Toast.LENGTH_SHORT).show();
                } else {
                    //  Обновление таблиц
                    sql = "UPDATE " + DBHelper.TABLE_SPEND + " SET " + DBHelper.KEY_KOL_SPEND + " = '"
                            + Double.toString(count) + "' WHERE "
                            + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' AND "
                            + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "'";
                    db.execSQL(sql);

                    sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_KOL_GET + " = '"
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetCount) - Double.parseDouble(editTextMySpend.getText().toString()))) / 100.0) + "' where "
                            + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                            + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "'";
                    db.execSQL(sql);



//                      Обновление количества средств на сервере Spend
                    Call<Void> call = serverApi.setKolBtn("table_spend", Double.toString(count), login,
                            btnName, "",
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetCount) - Double.parseDouble(editTextMySpend.getText().toString()))) / 100.0),
                            selectSpinnerItem);

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                    + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_SPEND_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                    + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_SPEND_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }
                    });






//                    ---- Добавление в таблицу SpendStatistic каждой операции с датой и времением и чем оплачено ----

//                    Текущая дата
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String dateNow = formatDate.format(date);
                    DateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String timeNow = formatTime.format(date);
                    Log.d("myLogs", "dateNow = " + dateNow);


//                    --------------------------- TABLE_SPEND_STATISTIC -------------------------
//                    ContentValues contentValues = new ContentValues();
//
////                        UPDATE table_get SET kol_get = datetime(strftime('%s','now'), 'unixepoch') WHERE get_id = 1
//
////                        SELECT get_id FROM table_get ORDER BY get_id DESC LIMIT 1
//                    contentValues.put(DBHelper.KEY_SPEND_STATISTIC_NAME, btnName);
//                    contentValues.put(DBHelper.KEY_SPEND_STATISTIC_KOL, editTextMySpend.getText().toString());
//                    contentValues.put(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY, selectSpinnerItem);
//                    contentValues.put(DBHelper.KEY_SPEND_STATISTIC_DATE, dateNow);
//                    contentValues.put(DBHelper.KEY_SPEND_STATISTIC_TIME, timeNow);
//                    contentValues.put(DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN, login);
//
//                    db.insert(DBHelper.TABLE_SPEND_STATISTICS, null, contentValues);
//
//                    contentValues.clear();

//                    ------------------------ TABLE_SPEND_STATISTIC ----------------------------
                    ContentValues contentValues = new ContentValues();

                    contentValues.put(DBHelper.KEY_STATISTIC_TYPE, "spend");
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, btnName);
                    contentValues.put(DBHelper.KEY_STATISTIC_KOL, editTextMySpend.getText().toString());
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, selectSpinnerItem);
                    contentValues.put(DBHelper.KEY_STATISTIC_DATE, dateNow);
                    contentValues.put(DBHelper.KEY_STATISTIC_TIME, timeNow);
                    contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                    contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "ins");

                    id = db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);

//                      Запись статистики на сервер
                    call = serverApi.addStatistic("spend", selectSpinnerItem, editTextMySpend.getText().toString(), btnName,
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

                            String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'ins' where "
                                    + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_STATISTIC_ID + " = '" + id + "' and "
                                    + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }
                    });
//                    ------------------------------------------------------------------------


//                    cursor = db.query(DBHelper.TABLE_SPEND_STATISTICS, null, null, null, null, null, null);
//
//
//                    if (cursor.moveToFirst()) {
//                        int spendIndexId = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_ID);
//                        int spendIndexName = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_NAME);
//                        int spendIndexKol = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_KOL);
//                        int spendIndexType = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY);
//                        int spendIndexDate = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_DATE);
//                        int spendIndexRefLogin = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN);
//                        do {
//                            int spendId = cursor.getInt(spendIndexId);
//                            String spendName = cursor.getString(spendIndexName);
//                            String spendKol = cursor.getString(spendIndexKol);
//                            String spendType = cursor.getString(spendIndexType);
//                            String spendDate = cursor.getString(spendIndexDate);
//                            String spendRefLogin = cursor.getString(spendIndexRefLogin);
//
//                            Log.d("myLogs", "spendId = " + spendId + " spendName = " + spendName +
//                                    " spendKol = " + spendKol + " spendType = "
//                                    + " spendType = " + spendType + " spendDate = " + spendDate + " spendRefLogin = " + spendRefLogin);
//                        } while (cursor.moveToNext());
//                    }
//                    cursor.close();

                    cursor = db.query(DBHelper.TABLE_STATISTIC, null, null, null, null, null, null);


                    if (cursor.moveToFirst()) {
                        int getIndexId = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID);
                        int getIndexType = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE);
                        int getIndexName = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND);
                        int getIndexKol = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL);
                        int getIndexTypePay = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET);
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


                    editTextMySpend.setText("");
                    Intent intent = new Intent(ButtonSpendActivity.this, ItemStatisticActivity.class);
                    intent.putExtra("btnName", btnName);
                    intent.putExtra("action_key", "spend");
                    startActivity(intent);
                }
//                    setResult(RESULT_OK, intent);
//                    finish();
            }
        });

        adSave.setNegativeButton(buttonSaveNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editTextMySpend.setText("");
                Intent intent = new Intent(ButtonSpendActivity.this, ItemStatisticActivity.class);
                intent.putExtra("btnName", btnName);
                intent.putExtra("action_key", "spend");
                startActivity(intent);
            }
        });

//        -----------------------------------------------------------------------------------------





//        Отображение кастомного Spinner
        final ArrayList<ItemDataWithDB> list = new ArrayList<>();

        String sql = "select " + DBHelper.KEY_GET_NAME + ", " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
        final Cursor cursor = db.rawQuery(sql, new String[]{login, "deleted"});

        if (cursor.moveToFirst()) {
            do {
                list.add(new ItemDataWithDB(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_NAME)), cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET)) + " руб."));
            } while (cursor.moveToNext());
        }
        cursor.close();

        SpinnerAdapterWithDB spinnerAdapterWithDB = new SpinnerAdapterWithDB(this, R.layout.spinner_layout_with_db, R.id.tvGetName, R.id.tvHowMach, list);

        spinnerWhatPaySpend.setAdapter(spinnerAdapterWithDB);





////        Создание адаптера для Spinner
//        String sql = "SELECT " + DBHelper.KEY_GET_ID + ", " + DBHelper.KEY_GET_NAME + " FROM " + DBHelper.TABLE_GET + " WHERE " +
//                DBHelper.KEY_REF_LOGIN + " =?";
//        final Cursor cursor = db.rawQuery(sql, new String[]{login});
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, cursor, new String[] {DBHelper.KEY_GET_NAME}, new int[] {android.R.id.text1});
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerWhatPaySpend.setAdapter(adapter);


//        context = getApplicationContext();   //   !!!!!!!!!!!
//        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(editTextMySpend.getWindowToken(), 0);

        spinnerWhatPaySpend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(this, getText(position), Toast.LENGTH_SHORT).show();

//                onGetCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
//                Log.d("myLogs", String.valueOf(parent.getItemAtPosition(position)));
//                spinnerWhatPaySpend.getSelectedItemPosition();
//                Log.d("myLogs", String.valueOf(spinnerWhatPaySpend.getSelectedItemPosition()));    // number

//                selectSpinnerItem = cursor.getString(1);
//                Log.d("myLogs", cursor.getString(1));  //  Имя выбранного элемента

                selectSpinnerItem = list.get((int) id).getGetName();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String onSpendCount = null;
        Cursor getSpendOnCountCursor;
        //        ---------- Извлечение переменной которая показывает текущее значение на счету ---------
        sql = "select " + DBHelper.KEY_KOL_SPEND + " from " + DBHelper.TABLE_SPEND
                + " where " + DBHelper.KEY_REF_LOGIN_SPEND + "=? AND " + DBHelper.KEY_SPEND_NAME + " =?";
        getSpendOnCountCursor = db.rawQuery(sql, new String[]{login, btnName});

        if (getSpendOnCountCursor.moveToFirst()) {
            do {
                onSpendCount = getSpendOnCountCursor.getString(getSpendOnCountCursor.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                Log.d("myLogs", "onGetCount = " + onSpendCount);
            } while (getSpendOnCountCursor.moveToNext());
        }
        getSpendOnCountCursor.close();
//        ------------------------------------------------------------------------------------------

//        Подсказка сколько в даннйй момент на счету
        editTextMySpend.setHint(onSpendCount + " руб.");

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case  R.id.btnSpendApply:

                if (editTextMySpend.getText().toString().equals("") || editTextMySpend.getText().toString().equals(".")){
                    editTextMySpend.setText("");
                    Toast.makeText(this, "Заполните поле ввода !!!", Toast.LENGTH_SHORT).show();
                }
                else {
                    sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                    login = sPrefLogin.getString("save_login", "");

                    String onSpendCount = null;


//                ------------------ Извлечение значения onCount из таблицы Get --------------------
                    String onGetCount = null;

                    String sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                            + " where " + DBHelper.KEY_REF_LOGIN + " =? AND " + DBHelper.KEY_GET_NAME + " =?";
                    Cursor cursor = db.rawQuery(sql, new String[]{login, selectSpinnerItem});

                    if (cursor.moveToFirst()) {
                        do {
                            onGetCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                            Log.d("myLogs", "onGetCount = " + onGetCount);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
//                ----------------------------------------------------------------------------------


//        ---------- Извлечение переменной которая показывает текущее значение на счету ---------
                    sql = "select " + DBHelper.KEY_KOL_SPEND + " from " + DBHelper.TABLE_SPEND
                            + " where " + DBHelper.KEY_REF_LOGIN_SPEND + "=? AND " + DBHelper.KEY_SPEND_NAME + " =?";
                    cursor = db.rawQuery(sql, new String[]{login, btnName});

                    if (cursor.moveToFirst()) {
                        do {
                            onSpendCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                            Log.d("myLogs", "onGetCount = " + onSpendCount);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
//        ------------------------------------------------------------------------------------------

//                Подсказка сколько в данный момент на счету
                    editTextMySpend.setHint(onSpendCount + " руб.");

                    Log.d("myLogs", "login = " + login + " btnName = " + btnName + " editTextMyGet = " + editTextMySpend.getText().toString());

//                Добавление к предыдущему значению
                    double count = Double.parseDouble(onSpendCount) + Double.parseDouble(editTextMySpend.getText().toString());

                    Log.d("myLogs", "count = " + count + " onGetCount = " + Double.parseDouble(onGetCount));

                    if (Double.parseDouble(editTextMySpend.getText().toString()) > Double.parseDouble(onGetCount)){
                        Toast.makeText(this, "В разделе " + selectSpinnerItem + " недостаточно средств", Toast.LENGTH_SHORT).show();
                    } else {
                        //  Обновление таблиц
                        sql = "UPDATE " + DBHelper.TABLE_SPEND + " SET " + DBHelper.KEY_KOL_SPEND + " = "
                                + Double.toString(count) + " WHERE "
                                + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' AND "
                                + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "'";
                        db.execSQL(sql);

                        sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_KOL_GET + " = '"
                                + Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetCount) - Double.parseDouble(editTextMySpend.getText().toString()))) / 100.0) + "' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "'";
                        db.execSQL(sql);


//                          Обновление количества средств на сервере Spend
                        Call<Void> call = serverApi.setKolBtn("table_spend", Double.toString(count), login,
                                btnName, "",
                                Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetCount) - Double.parseDouble(editTextMySpend.getText().toString()))) / 100.0),
                                selectSpinnerItem);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                        + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "' and "
                                        + DBHelper.KEY_SPEND_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);

                                sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                        + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'no' where "
                                        + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                        + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "' and "
                                        + DBHelper.KEY_SPEND_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);

                                sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                        + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                        + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);

                            }
                        });






//                    ---- Добавление в таблицу SpendStatistic каждой операции с датой и времением и чем оплачено ----

//                    Текущая дата
                        Date date = new Date();
                        Calendar calendar = Calendar.getInstance();
                        DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        String dateNow = formatDate.format(date);
                        DateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String timeNow = formatTime.format(date);
                        Log.d("myLogs", "dateNow = " + dateNow);

//                        Calendar dating = Calendar.getInstance();
//                        SimpleDateFormat formating = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//                        Log.d("myLogs", "formating.format(dating.getTime()) = " + formating.format(dating.getTime()));

//                        ---------------------- TABLE_SPEND_STATISTIC ------------------------
//                        ContentValues contentValues = new ContentValues();
//
////                        UPDATE table_get SET kol_get = datetime(strftime('%s','now'), 'unixepoch') WHERE get_id = 1
//
////                        SELECT get_id FROM table_get ORDER BY get_id DESC LIMIT 1
//                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_NAME, btnName);
//                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_KOL, editTextMySpend.getText().toString());
//                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY, selectSpinnerItem);
//                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_DATE, dateNow);
//                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_TIME, timeNow);
////                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_DATE,
////                                db.execSQL("UPDATE " + DBHelper.TABLE_GET_STATISTICS + " SET " + DBHelper.KEY_GET_STATISTIC_DATE + " = "
////                                        + " datetime(strftime('%s','now'), 'unixepoch') WHERE " + );
////                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_DATE,
//
//
//                        contentValues.put(DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN, login);
//
//                        db.insert(DBHelper.TABLE_SPEND_STATISTICS, null, contentValues);
//
//                        contentValues.clear();

//                    ------------------------ TABLE_STATISTIC ----------------------------
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(DBHelper.KEY_STATISTIC_TYPE, "spend");
                        contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, btnName);
                        contentValues.put(DBHelper.KEY_STATISTIC_KOL, editTextMySpend.getText().toString());
                        contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, selectSpinnerItem);
                        contentValues.put(DBHelper.KEY_STATISTIC_DATE, dateNow);
                        contentValues.put(DBHelper.KEY_STATISTIC_TIME, timeNow);
                        contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                        contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "ins");

                        id = db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);


//                          Запись статистики на сервер
                        call = serverApi.addStatistic("spend", selectSpinnerItem, editTextMySpend.getText().toString(), btnName,
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

                                String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'ins' where "
                                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_STATISTIC_ID + " = '" + id + "' and "
                                        + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);

                            }
                        });
//                    ------------------------------------------------------------------------

//                        UPDATE spend_statistics SET date_stat_spend =  datetime(strftime('%s','now'), 'localtime') WHERE _id = (SELECT _id FROM spend_statistics ORDER BY _id DESC LIMIT 1)
//                        db.execSQL("UPDATE " + DBHelper.TABLE_SPEND_STATISTICS + " SET " + DBHelper.KEY_SPEND_STATISTIC_DATE + " = "
//                                + " datetime(strftime('%s','now'), 'unixepoch') WHERE " + DBHelper.KEY_SPEND_STATISTIC_ID + " = ("
//                                + "SELECT " + DBHelper.KEY_SPEND_STATISTIC_ID + " FROM " + DBHelper.TABLE_SPEND_STATISTICS + " ORDER BY "
//                                + DBHelper.KEY_SPEND_STATISTIC_ID + " DESC LIMIT 1)");


//                        cursor = db.query(DBHelper.TABLE_SPEND_STATISTICS, null, null, null, null, null, null);
//
//
//                        if (cursor.moveToFirst()) {
//                            int spendIndexId = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_ID);
//                            int spendIndexName = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_NAME);
//                            int spendIndexKol = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_KOL);
//                            int spendIndexType = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY);
//                            int spendIndexDate = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_DATE);
//                            int spendIndexRefLogin = cursor.getColumnIndex(DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN);
//                            do {
//                                int spendId = cursor.getInt(spendIndexId);
//                                String spendName = cursor.getString(spendIndexName);
//                                String spendKol = cursor.getString(spendIndexKol);
//                                String spendType = cursor.getString(spendIndexType);
//                                String spendDate = cursor.getString(spendIndexDate);
//                                String spendRefLogin = cursor.getString(spendIndexRefLogin);
//
//                                Log.d("myLogs", "spendId = " + spendId + " spendName = " + spendName +
//                                        " spendKol = " + spendKol + " spendType = "
//                                        + " spendType = " + spendType + " spendDate = " + spendDate + " spendRefLogin = " + spendRefLogin);
//                            } while (cursor.moveToNext());
//                        }
//                        cursor.close();

                        cursor = db.query(DBHelper.TABLE_STATISTIC, null, null, null, null, null, null);


                        if (cursor.moveToFirst()) {
                            int getIndexId = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID);
                            int getIndexType = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE);
                            int getIndexName = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND);
                            int getIndexKol = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL);
                            int getIndexTypePay = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET);
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



//                        db.execSQL("INSERT INTO " + DBHelper.TABLE_SPEND_STATISTICS
//                                + " (" + DBHelper.KEY_SPEND_STATISTIC_DATE + ")"
//                                + " VALUES (datetime(strftime('%s','now'), 'unixepoch'))");

//                        ------------------------------------------------------------------------------------------



//                        Calendar calendar = Calendar.getInstance();
//                        Log.d("myLogs", "calendar.getTimeInMillis() = " + calendar.getTimeInMillis()/1000);
//                        int mYear = calendar.get(Calendar.YEAR);
//                        int mMonth = calendar.get(Calendar.MONTH);
//                        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
//                        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
//                        int mMinute = calendar.get(Calendar.MINUTE);
//                        int mSecond = calendar.get(Calendar.SECOND);
//                        Log.d("myLogs", "mYear = " + mYear + " mMonth = " + mMonth + " mDay = " + mDay + " mHour = " + mHour + " mMinute = " + mMinute + " mSecond = " + mSecond);
//
//                        Log.d("myLogs", "System.currentTimeMillis() = " + System.currentTimeMillis()/1000);
//
//                        date = new Date(System.currentTimeMillis()/1000);
//                        Log.d("myLogs", "format.format(date) = " + format.format(date));
//
//
//                        calendar.set(0, 0, 0, 0, 0, (int) (calendar.getTimeInMillis()/1000));
//                        Log.d("myLogs", "format.format(calendar.getTime()) = " + format.format(calendar.getTime()));

//                        Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();


                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
                break;
            case R.id.btnSpendCancel:
                finish();
                break;

            case R.id.btnSpendStatistics:
                if (editTextMySpend.getText().toString().equals(".")){
                    editTextMySpend.setText("");
                }

                if (!editTextMySpend.getText().toString().equals("")){
                    adSave.show();
                } else {
                    intent = new Intent(ButtonSpendActivity.this, ItemStatisticActivity.class);
                    intent.putExtra("btnName", btnName);
                    intent.putExtra("action_key", "spend");
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
            intent.putExtra("action_key", "action_spend");
            intent.putExtra("btnName", btnName);
            startActivityForResult(intent, REQUEST_CODE_BTN_RELOAD_SPEND);
//            finish();
        } else if (id == R.id.action_editing_last_record){
            Intent intent = new Intent(this, EditingLastRecordSpend.class);
            intent.putExtra("btnName", btnName);
            startActivityForResult(intent, REQUEST_CODE_BTN_RELOAD_SPEND);
        } else if (id == R.id.action_delete){
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();

            ad.show();  //  Показ диалога на удаление
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(ButtonSpendActivity.this, UserActivity.class);
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
                case REQUEST_CODE_BTN_RELOAD_SPEND:
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
    }
}
