package com.example.dima.mytestingapp.Activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoneyTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTransaction;
    EditText edTransaction;
    Spinner spinnerTransactTo;
    Button btnTransactionApply;
    Button btnTransactionCancel;

    String btnName;

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;
    String login;

    String selectSpinnerItem;  //  Выбранный элемент Spinner

    String onGetCount;  //   В данный момент на счету

    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transaction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");

        tvTransaction = (TextView) findViewById(R.id.tvTransaction);
        edTransaction = (EditText) findViewById(R.id.edTransaction);
        spinnerTransactTo = (Spinner) findViewById(R.id.spinnerTransactTo);
        btnTransactionApply = (Button) findViewById(R.id.btnTransactionApply);
        btnTransactionApply.setOnClickListener(this);
        btnTransactionCancel = (Button) findViewById(R.id.btnTransactionCancel);
        btnTransactionCancel.setOnClickListener(this);

        Intent intent = getIntent();
        btnName = intent.getStringExtra("btnName");

        tvTransaction.setText("Перевод с " +  btnName);

        //        Отображение кастомного Spinner
        final ArrayList<ItemDataWithDB> list = new ArrayList<>();

        String sql = "select " + DBHelper.KEY_GET_NAME + ", " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " != ? and "
                + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
        final Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "deleted"});

        if (cursor.moveToFirst()) {
            do {
                list.add(new ItemDataWithDB(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_NAME)), cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET)) + " руб."));
            } while (cursor.moveToNext());
        }
        cursor.close();

        SpinnerAdapterWithDB spinnerAdapterWithDB = new SpinnerAdapterWithDB(this, R.layout.spinner_layout_with_db, R.id.tvGetName, R.id.tvHowMach, list);

        spinnerTransactTo.setAdapter(spinnerAdapterWithDB);

        spinnerTransactTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectSpinnerItem = list.get((int) id).getGetName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        onGetCount = null;
        Cursor getGetOnCountCursor;
        //        ---------- Извлечение переменной которая показывает текущее значение на счету ---------
        sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + "=? AND " + DBHelper.KEY_GET_NAME + " =? and "
                + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
        getGetOnCountCursor = db.rawQuery(sql, new String[]{login, btnName, "deleted"});

        if (getGetOnCountCursor.moveToFirst()) {
            do {
                onGetCount = getGetOnCountCursor.getString(getGetOnCountCursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                Log.d("myLogs", "onGetCount = " + onGetCount);
            } while (getGetOnCountCursor.moveToNext());
        }
        getGetOnCountCursor.close();
//        ------------------------------------------------------------------------------------------

//        Подсказка сколько в даннйй момент на счету
        edTransaction.setHint(onGetCount + " руб.");

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.btnTransactionApply:

                String onGetAnotherCount = null;
                Cursor getGetOnCountCursor;
//        ---------- Извлечение переменной которая показывает текущее значение на счету элемента выбранного в Spinner ---------
                String sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                        + " where " + DBHelper.KEY_REF_LOGIN + "=? AND " + DBHelper.KEY_GET_NAME + " =? AND "
                        + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
                getGetOnCountCursor = db.rawQuery(sql, new String[]{login, selectSpinnerItem, "deleted"});

                if (getGetOnCountCursor.moveToFirst()) {
                    do {
                        onGetAnotherCount = getGetOnCountCursor.getString(getGetOnCountCursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                        Log.d("myLogs", "onGetCount = " + onGetAnotherCount);
                    } while (getGetOnCountCursor.moveToNext());
                }
                getGetOnCountCursor.close();
//        ------------------------------------------------------------------------------------------



                if (edTransaction.getText().toString().equals("") || edTransaction.getText().toString().equals(".")){
                    edTransaction.setText("");
                    Toast.makeText(this, "Заполните поле ввода !!!", Toast.LENGTH_SHORT).show();
                } else if (Double.parseDouble(onGetCount) < Double.parseDouble(edTransaction.getText().toString())){
                    Toast.makeText(this, "На счету недостаточно средств !!!", Toast.LENGTH_SHORT).show();
                } else {

//                    Запись на сервер
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl("https://myinfdb.000webhostapp.com")
                            .addConverterFactory(GsonConverterFactory.create());

                    Retrofit retrofit = builder.build();

                    ServerApi serverApi = retrofit.create(ServerApi.class);


//                    Добавление введнной суммы в ВЫБРАННЫЙ раздел из Spinner
                    sql = "UPDATE " + DBHelper.TABLE_GET + " SET " + DBHelper.KEY_KOL_GET + " = "
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetAnotherCount) + Double.parseDouble(edTransaction.getText().toString()))) / 100.0) + " WHERE "
                            + DBHelper.KEY_REF_LOGIN + " = '" + login + "' AND "
                            + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "'";
                    db.execSQL(sql);

                    sql = "UPDATE " + DBHelper.TABLE_GET + " SET " + DBHelper.KEY_KOL_GET + " = "
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetCount) - Double.parseDouble(edTransaction.getText().toString()))) / 100.0) + " WHERE "
                            + DBHelper.KEY_REF_LOGIN + " = '" + login + "' AND "
                            + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
                    db.execSQL(sql);






                    Call<Void> call = serverApi.editTransaction(
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetAnotherCount) + Double.parseDouble(edTransaction.getText().toString()))) / 100.0),
                            login, selectSpinnerItem,
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onGetCount) - Double.parseDouble(edTransaction.getText().toString()))) / 100.0),
                            btnName
                    );

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);


                        }
                    });
//                    --------------------------------------------------



//                    Запись перевода в таблицу Статистики
                    ContentValues contentValues = new ContentValues();

//                    Текущая дата
                    Date date = new Date();
                    DateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String dateNow = formatDate.format(date);
                    DateFormat formatTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    String timeNow = formatTime.format(date);

                    contentValues.put(DBHelper.KEY_STATISTIC_TYPE, "transaction");
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, selectSpinnerItem);
                    contentValues.put(DBHelper.KEY_STATISTIC_KOL, edTransaction.getText().toString());
                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, btnName);
                    contentValues.put(DBHelper.KEY_STATISTIC_DATE, dateNow);
                    contentValues.put(DBHelper.KEY_STATISTIC_TIME, timeNow);
                    contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                    contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "ins");

                    id = db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);

//                    Запись перевода на сервер
                    call = serverApi.addStatistic("transaction", selectSpinnerItem, edTransaction.getText().toString(),
                            btnName, dateNow, timeNow, login, String.valueOf(id));

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



                    Toast.makeText(this, "Перевод проведен", Toast.LENGTH_SHORT).show();


                    setResult(RESULT_OK, intent);
                    finish();
                }

                break;

            case R.id.btnTransactionCancel:
                finish();
                break;

        }

    }
}
