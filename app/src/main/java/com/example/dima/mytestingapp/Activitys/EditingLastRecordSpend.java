package com.example.dima.mytestingapp.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditingLastRecordSpend extends AppCompatActivity implements View.OnClickListener {

    TextView tvNameEdLastSpend;
    EditText edNewSumSpend;
    Spinner spinLastTypeSpend;
    Button btnNewSumApplySpend;
    Button btnNewSumCancelSpend;

    String selectSpinnerItem;  //  Выбранный элемент Spinner

    ArrayList<ItemDataWithDB> list;


    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    Intent intent;
    String btnName;

    String typePay;
    int onCountStatisticSpendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_last_record_spend);

        intent = getIntent();
        btnName = intent.getStringExtra("btnName");

        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

//        Извлечение Login
        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");

        tvNameEdLastSpend = (TextView) findViewById(R.id.tvNameEdLastSpend);
        edNewSumSpend = (EditText) findViewById(R.id.edNewSumSpend);
        spinLastTypeSpend = (Spinner) findViewById(R.id.spinLastTypeSpend);
        btnNewSumApplySpend = (Button) findViewById(R.id.btnNewSumApplySpend);
        btnNewSumApplySpend.setOnClickListener(this);
        btnNewSumCancelSpend = (Button) findViewById(R.id.btnNewSumCancelSpend);
        btnNewSumCancelSpend.setOnClickListener(this);

        tvNameEdLastSpend.setText("Редактирование последней введенной суммы раздела " + btnName);

        //        Отображение кастомного Spinner
        list = new ArrayList<>();

        String sql = "select " + DBHelper.KEY_GET_NAME + ", " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
        Cursor cursor = db.rawQuery(sql, new String[]{login, "deleted"});

        if (cursor.moveToFirst()) {
            do {
                list.add(new ItemDataWithDB(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_NAME)), cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET)) + " руб."));
            } while (cursor.moveToNext());
        }
        cursor.close();


        SpinnerAdapterWithDB spinnerAdapterWithDB = new SpinnerAdapterWithDB(this, R.layout.spinner_layout_with_db, R.id.tvGetName, R.id.tvHowMach, list);


//            ---------------------------------------------------------------------------------------------------
//                Получаем сумму последнего измениения и Spinner
        int onCountStatisticSpendId = 0;
        String onCountStatisticSpend = null;
        String typeOfPay = null;
//        sql = "select "+ DBHelper.KEY_SPEND_STATISTIC_ID + ", " + DBHelper.KEY_SPEND_STATISTIC_KOL + ", " + DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY
//                + " from " + DBHelper.TABLE_SPEND_STATISTICS
//                + " where " + DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_SPEND_STATISTIC_NAME + " =?";
//        cursor = db.rawQuery(sql, new String[]{login, btnName});

        sql = "select "+ DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_KOL + ", " + DBHelper.KEY_STATISTIC_NAME_GET
                + " from " + DBHelper.TABLE_STATISTIC
                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? and "
                + DBHelper.KEY_STATISTIC_TYPE + " =?" ;
        cursor = db.rawQuery(sql, new String[]{login, btnName, "spend"});

        if (cursor.moveToLast()){
            do {
                onCountStatisticSpendId = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID));
                onCountStatisticSpend = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL));
                typeOfPay = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET));
                Log.d("myLogs","onCountStatisticSpendId = " + onCountStatisticSpendId + " onCountStatisticSpend = " + onCountStatisticSpend + " typeOfPay = " + typeOfPay);
            } while (cursor.moveToNext());
        }
        cursor.close();

//            Извлекаем из TableGet ID по названию

//        int posId = 0;
//        try{
//
//            sql = "select "+ DBHelper.KEY_GET_ID + " from " + DBHelper.TABLE_GET
//                    + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " =? and "
//                    + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
//            cursor = db.rawQuery(sql, new String[]{login, typeOfPay, "deleted"});
//
//            Toast.makeText(this, "cursor.getCount() = " + cursor.getCount(), Toast.LENGTH_SHORT).show();
//
//            if (cursor.moveToLast()){
//                do {
//                    posId = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_GET_ID));
//                    Log.d("myLogs","posId = " + posId);
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//
//        } catch (java.lang.IllegalArgumentException e){
//            Toast.makeText(this, "Изменений не было !!!", Toast.LENGTH_SHORT).show();
//        }

        int posId = 0;
        sql = "select "+ DBHelper.KEY_GET_ID + " from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " =? and "
                + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
        cursor = db.rawQuery(sql, new String[]{login, typeOfPay, "deleted"});

        if (cursor.moveToLast()){
            do {
                posId = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_GET_ID));
                Log.d("myLogs","posId = " + posId);
            } while (cursor.moveToNext());
        }
        cursor.close();

//            -----------------------------------------------------------------------------------------------------

        spinLastTypeSpend.setAdapter(spinnerAdapterWithDB);

        spinLastTypeSpend.setSelection(posId -1);   //   Spinner по умолчанию

        spinLastTypeSpend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSpinnerItem = list.get((int) id).getGetName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edNewSumSpend.setText(onCountStatisticSpend);
        edNewSumSpend.setSelection(edNewSumSpend.getText().length());  //  Курсор в конец строки

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNewSumApplySpend:

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://myinfdb.000webhostapp.com")
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.build();

                ServerApi serverApi = retrofit.create(ServerApi.class);



//                Получаем сумму последнего измениения TableStatistic и Spinner
                String onCountStatisticSpend = null;
                onCountStatisticSpendId = 0;
                typePay = null;
//                String sql = "select "+ DBHelper.KEY_SPEND_STATISTIC_ID + ", " + DBHelper.KEY_SPEND_STATISTIC_KOL + ", " + DBHelper.KEY_SPEND_STATISTIC_TYPE_PAY
//                        + " from " + DBHelper.TABLE_SPEND_STATISTICS
//                        + " where " + DBHelper.KEY_SPEND_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_SPEND_STATISTIC_NAME + " =?";
//                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName});

                String sql = "select "+ DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_KOL + ", " + DBHelper.KEY_STATISTIC_NAME_GET
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_STATISTIC_NAME_SPEND + " =? and "
                        + DBHelper.KEY_STATISTIC_TYPE + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "spend"});

                if (cursor.moveToLast()){
                    do {
                        onCountStatisticSpendId = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID));
                        onCountStatisticSpend = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL));
                        typePay = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET));
                        Log.d("myLogs","onCountStatisticSpendId = " + onCountStatisticSpendId + " onCountStatisticSpend = " + onCountStatisticSpend + " typePay = " + typePay);
                    } while (cursor.moveToNext());
                }
                cursor.close();

//                Получаем сумму из TableSpend
                String onCountTableSpend = null;
                sql = "select " + DBHelper.KEY_KOL_SPEND + " from " + DBHelper.TABLE_SPEND
                        + " where " + DBHelper.KEY_REF_LOGIN_SPEND + "=? and " + DBHelper.KEY_SPEND_NAME + " =?";
                cursor = db.rawQuery(sql, new String[]{login, btnName});

                if (cursor.moveToFirst()){
                    do {
                        onCountTableSpend = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                        Log.d("myLogs", "onCountTableSpend = " + onCountTableSpend);
                    } while (cursor.moveToNext());
                }
                cursor.close();



//                    Получаем сумму из TableGet
                String onCountTableGet = null;
                sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                        + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " =?";
                cursor = db.rawQuery(sql, new String[]{login, typePay});

                if (cursor.moveToFirst()){
                    do {
                        onCountTableGet = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                        Log.d("myLogs", "onCountTableGet = " + onCountTableGet);
                    } while (cursor.moveToNext());
                }
                cursor.close();


//                    Получаем сумму из TableGet
                String onCountTableGetSpinner = null;
                sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                        + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " =?";
                cursor = db.rawQuery(sql, new String[]{login, selectSpinnerItem});

                if (cursor.moveToFirst()){
                    do {
                        onCountTableGetSpinner = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                        Log.d("myLogs", "onCountTableGetSpinner = " + onCountTableGetSpinner);
                    } while (cursor.moveToNext());
                }
                cursor.close();

////                    Получаем сумму из TableGet 2
//                String onCountTableGetAnother = null;
//                sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
//                        + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " =?";
//                cursor = db.rawQuery(sql, new String[]{login, selectSpinnerItem});
//
//                if (cursor.moveToFirst()){
//                    do {
//                        onCountTableGetAnother = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
//                        Log.d("myLogs", "onCountTableGet2 (onCountTableGetAnother) = " + onCountTableGetAnother);
//                    } while (cursor.moveToNext());
//                }
//                cursor.close();


//                    Получаем сумму из TotalGet
                String onCountTableTotalGet = null;
                sql = "select " + DBHelper.KEY_KOL_TOTAL_GET + " from " + DBHelper.TABLE_GET_FOR_TOTAL
                        + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + "=?";
                cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.moveToFirst()){
                    do {
                        onCountTableTotalGet = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_TOTAL_GET));
                        Log.d("myLogs", "onCountTableTotalGet = " + onCountTableTotalGet);
                    } while (cursor.moveToNext());
                }
                cursor.close();

                double onCount = Double.parseDouble(onCountTableGetSpinner);
                double edSum = Double.parseDouble(edNewSumSpend.getText().toString());
                if (edNewSumSpend.getText().toString().equals("") || edNewSumSpend.getText().toString().equals(".")){
                    Toast.makeText(this, "Заполгните поля ввода !!!", Toast.LENGTH_SHORT).show();
                    edNewSumSpend.setText("");
                } else if (Double.parseDouble(onCountTableSpend) == 0){
                    Toast.makeText(this, "Не достаточно данных !!!", Toast.LENGTH_SHORT).show();
                } else if (onCount < edSum){
                    Toast.makeText(this, "На счету недостаточно средств !!!", Toast.LENGTH_SHORT).show();
                } else {

//                            Обновление таблицы TableGet
                    sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_KOL_GET + " = '"
//                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGet) - Double.parseDouble(onCountStatisticSpend) + Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0) + "' where "
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGet) + Double.parseDouble(onCountStatisticSpend))) / 100.0) + "' where "
                            + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                            + DBHelper.KEY_GET_NAME + " = '" + typePay + "'";
                    db.execSQL(sql);
                    Log.d("myLogsNew", "Обновление таблицы TableGet");
                    Log.d("myLogsNew", "onCountTableGet (" + onCountTableGet + ") + onCountStatisticSpend (" + onCountStatisticSpend + ") = " + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGet) + Double.parseDouble(onCountStatisticSpend))) / 100.0));

//                    -----------------------------------------------------------------
                    //                    Получаем сумму из TableGet 2
                    String onCountTableGetAnother = null;
                    sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                            + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " =?";
                    cursor = db.rawQuery(sql, new String[]{login, selectSpinnerItem});

                    if (cursor.moveToFirst()){
                        do {
                            onCountTableGetAnother = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                            Log.d("myLogs", "onCountTableGet2 (onCountTableGetAnother) = " + onCountTableGetAnother);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
//                    -----------------------------------------------------------------

//                            Обновление таблицы TableGet 2
                    sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_KOL_GET + " = '"
//                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGet) - Double.parseDouble(onCountStatisticSpend) + Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0) + "' where "
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGetAnother) - Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0) + "' where "
                            + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                            + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "'";
                    db.execSQL(sql);
                    Log.d("myLogsNew", "Обновление таблицы TableGet 2");
                    Log.d("myLogsNew", "onCountTableGetAnother (" + onCountTableGetAnother + ") - edNewSumSpend (" + edNewSumSpend.getText().toString() + ") = " + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGetAnother) - Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0));



////                            Обновление таблицы итогов за месяц TotalGet
//                    sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_KOL_TOTAL_GET + " = '"
//                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableTotalGet) - Double.parseDouble(onCountStatisticSpend) + Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0) + "' where "
//                            + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
//                    db.execSQL(sql);
//                    Log.d("myLogsNew", "Обновление таблицы итогов за месяц TotalGet");
//                    Log.d("myLogsNew", "onCountTableTotalGet (" + onCountTableTotalGet + ") - onCountStatisticSpend (" + onCountStatisticSpend + ") + edNewSumSpend (" + edNewSumSpend + ") = " + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableTotalGet) - Double.parseDouble(onCountStatisticSpend) + Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0));



//                            Обновление таблицы статистики Statistic
                    sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_KOL + " = '"
                            + edNewSumSpend.getText().toString() + "'" + " where "
                            + DBHelper.KEY_STATISTIC_ID + " = " + onCountStatisticSpendId + " and "
                            + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "'";
                    db.execSQL(sql);

                    sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_NAME_GET + " = '"
                            + selectSpinnerItem + "' where " + DBHelper.KEY_STATISTIC_ID + " = " + onCountStatisticSpendId + " and "
                            + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "'";
                    db.execSQL(sql);

//                    ----------------------------------------------------------------------------------------
                    cursor = db.query(DBHelper.TABLE_STATISTIC, null, null, null, null, null, null);

                    if (cursor.moveToFirst()){
                        int idIndex = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID);
                        int nameIndex = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND);
                        int kolIndex = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL);
                        int typeIndex = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET);
                        int refIndex = cursor.getColumnIndex(DBHelper.KEY_STATISTIC_REF_LOGIN);
                        do {
                            Log.d("myLogsNew", "ID = " + cursor.getInt(idIndex) +
                                    ", name = " + cursor.getString(nameIndex) +
                                    ", kol = " + cursor.getString(kolIndex) +
                                    ", typePay = " + cursor.getString(typeIndex) +
                                    ", refLogin = " + cursor.getString(refIndex));
                        } while (cursor.moveToNext());
                    } else
                        Log.d("mLog", "0 rows");

                    cursor.close();
//                    ----------------------------------------------------------------------------------------

                    Log.d("myLogsNew", "Обновление таблицы статистики SpendStatistic");
                    Log.d("myLogsNew", "KEY_SPEND_STATISTIC_KOL = " + edNewSumSpend.getText().toString() + " KEY_SPEND_STATISTIC_TYPE_PAY (selectSpinnerItem) = " + selectSpinnerItem);


//                            Обновление таблицы отображения TableSpend
                    sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_KOL_SPEND + " = '"
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableSpend) - Double.parseDouble(onCountStatisticSpend) + Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0) + "' where "
                            + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                            + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "'";
                    db.execSQL(sql);
                    Log.d("myLogsNew", "Обновление таблицы отображения TableSpend");
                    Log.d("myLogsNew", "onCountTableSpend (" + onCountTableSpend + ") - onCountStatisticSpend (" + onCountStatisticSpend + ") + edNewSumSpend (" + edNewSumSpend.getText().toString() + ") = " + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableSpend) - Double.parseDouble(onCountStatisticSpend) + Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0));


                    Call<Void> call = serverApi.editLastRecordSpend(btnName,
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGet) + Double.parseDouble(onCountStatisticSpend))) / 100.0),
                            login, typePay,
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGetAnother) - Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0),
                            selectSpinnerItem, edNewSumSpend.getText().toString(),
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableSpend) - Double.parseDouble(onCountStatisticSpend) + Double.parseDouble(edNewSumSpend.getText().toString()))) / 100.0));

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

//                            Обновление таблицы TableGet
                            String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + typePay + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

//                            Обновление таблицы TableGet 2
                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

//                            Обновление таблицы статистики Statistic
                            sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_STATISTIC_ID + " = " + onCountStatisticSpendId + " and "
                                    + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

//                            Обновление таблицы отображения TableSpend
                            sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                    + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_SPEND_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

//                            Обновление таблицы TableGet
                            String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + typePay + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

//                            Обновление таблицы TableGet 2
                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + selectSpinnerItem + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

//                            Обновление таблицы статистики Statistic
                            sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_STATISTIC_ID + " = " + onCountStatisticSpendId + " and "
                                    + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

//                            Обновление таблицы отображения TableSpend
                            sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                    + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_SPEND_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                        }
                    });


                    setResult(RESULT_OK, intent);
                    finish();
                }

                break;

            case R.id.btnNewSumCancelSpend:
                finish();
                break;
        }

    }
}
