package com.example.dima.mytestingapp.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditingLastRecord extends AppCompatActivity implements View.OnClickListener {

//    Get
    TextView tvNameEdLast;
    EditText edNewSum;
    Button btnNewSumApply;
    Button btnNewSumCancel;


    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;
    String btnName;

    int onCountStatisticId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_last_record);

        Intent intent = getIntent();
        btnName = intent.getStringExtra("btnName");

        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

//        Извлечение Login
        sPrefLogin = getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");



        tvNameEdLast = (TextView) findViewById(R.id.tvNameEdLast);
        edNewSum = (EditText) findViewById(R.id.edNewSum);
        btnNewSumApply = (Button) findViewById(R.id.btnNewSumApply);
        btnNewSumApply.setOnClickListener(this);
        btnNewSumCancel = (Button) findViewById(R.id.btnNewSumCancel);
        btnNewSumCancel.setOnClickListener(this);

        tvNameEdLast = (TextView) findViewById(R.id.tvNameEdLast);
        tvNameEdLast.setText("Редактирование последней введенной суммы раздела " + btnName);

        Log.d("myLogs", "btnName = " + btnName + " login = " + login);

        String onCount = null;  //  Последнее добавление

//        String sql = "select " + DBHelper.KEY_GET_STATISTIC_KOL + " from " + DBHelper.TABLE_GET_STATISTICS
//                + " where " + DBHelper.KEY_GET_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_GET_STATISTIC_NAME + " =?";
//        Cursor cursor = db.rawQuery(sql, new String[]{login, btnName});

        String sql = "select " + DBHelper.KEY_STATISTIC_KOL + " from " + DBHelper.TABLE_STATISTIC
                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_STATISTIC_NAME_GET + " =? and "
                + DBHelper.KEY_STATISTIC_TYPE + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{login, btnName, "get"});

        if (cursor.moveToLast()) {
            do {
                onCount = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL));
                Log.d("myLogs", "onCount = " + onCount);
            } while (cursor.moveToNext());
        }
        cursor.close();


        edNewSum.setText(onCount);
        edNewSum.setSelection(edNewSum.getText().length());  //  Курсор в конец строки


    }

    @Override
    public void onClick(View v) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ServerApi serverApi = retrofit.create(ServerApi.class);


        dbHelper = new DBHelper(this);

        db = dbHelper.getWritableDatabase();

        Cursor cursor;

//        Извлечение Login
        sPrefLogin = getSharedPreferences("SharedPrefLogin", MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");

        Intent intent = getIntent();
        btnName = intent.getStringExtra("btnName");

        switch (v.getId()){
            case R.id.btnNewSumApply:

//                Получаем сумму последнего измениения
                String onCountStatistic = null;
                onCountStatisticId = 0;
//                String sql = "select "+ DBHelper.KEY_GET_STATISTIC_ID + ", " + DBHelper.KEY_GET_STATISTIC_KOL
//                        + " from " + DBHelper.TABLE_GET_STATISTICS
//                        + " where " + DBHelper.KEY_GET_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_GET_STATISTIC_NAME + " =?";
//                cursor = db.rawQuery(sql, new String[]{login, btnName});

                String sql = "select "+ DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_KOL
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? and " + DBHelper.KEY_STATISTIC_NAME_GET + " =? and "
                        + DBHelper.KEY_STATISTIC_TYPE + " =?" ;
                cursor = db.rawQuery(sql, new String[]{login, btnName, "get"});

                if (cursor.moveToLast()){
                    do {
                        onCountStatisticId = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID));
                        onCountStatistic = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL));
                        Log.d("myLogs","onCountStatisticId = " + onCountStatisticId + " onCountStatistic = " + onCountStatistic);
                    } while (cursor.moveToNext());
                }
                cursor.close();


//                    Получаем сумму из TableGet
                String onCountTableGet = null;
                sql = "select " + DBHelper.KEY_KOL_GET + " from " + DBHelper.TABLE_GET
                        + " where " + DBHelper.KEY_REF_LOGIN + "=? and " + DBHelper.KEY_GET_NAME + " =?";
                cursor = db.rawQuery(sql, new String[]{login, btnName});

                if (cursor.moveToFirst()){
                    do {
                        onCountTableGet = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                        Log.d("myLogs", "onCountTableGet = " + onCountTableGet);
                    } while (cursor.moveToNext());
                }
                cursor.close();


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



                if (edNewSum.getText().toString().equals("") || edNewSum.getText().toString().equals(".")){
                    Toast.makeText(this, "Заполгните поля ввода !!!", Toast.LENGTH_SHORT).show();
                    edNewSum.setText("");
                } else if (Double.parseDouble(onCountTableGet) == 0 || Double.parseDouble(onCountTableTotalGet) == 0){
                    Toast.makeText(this, "Не достаточно данных !!!", Toast.LENGTH_SHORT).show();
                } else {

//                            Обновление таблицы статистики Statistic
                    sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_KOL + " = '"
                            + edNewSum.getText().toString()
                            + "' where " + DBHelper.KEY_STATISTIC_ID + " = " + onCountStatisticId + " and "
                            + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "'";
                    db.execSQL(sql);

//                            Обновление таблицы отображения
                    sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_KOL_GET + " = '"
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGet) - Double.parseDouble(onCountStatistic) + Double.parseDouble(edNewSum.getText().toString()))) / 100.0) + "' where "
                            + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                            + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
                    db.execSQL(sql);

//                            Обновление таблицы итогов за месяц
                    sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_KOL_TOTAL_GET + " = '"
                            + Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableTotalGet) - Double.parseDouble(onCountStatistic) + Double.parseDouble(edNewSum.getText().toString()))) / 100.0) + "' where "
                            + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                    db.execSQL(sql);

                    Call<Void> call = serverApi.editLastRecord("get", edNewSum.getText().toString(),
                            login,
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableGet) - Double.parseDouble(onCountStatistic) + Double.parseDouble(edNewSum.getText().toString()))) / 100.0),
                            btnName,
                            Double.toString(Math.rint(100.0 * (Double.parseDouble(onCountTableTotalGet) - Double.parseDouble(onCountStatistic) + Double.parseDouble(edNewSum.getText().toString()))) / 100.0));

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_STATISTIC_ID + " = " + onCountStatisticId + " and "
                                    + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                            db.execSQL(sql);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_STATISTIC_ID + " = " + onCountStatisticId + " and "
                                    + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_STATISTIC_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                    + DBHelper.KEY_GET_NAME + " = '" + btnName + "' and "
                                    + DBHelper.KEY_GET_SYNCHRONISE + " != 'ins'";
                            db.execSQL(sql);

                            sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'no' where "
                                    + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                            db.execSQL(sql);

                        }
                    });

                    setResult(RESULT_OK, intent);
                    finish();
                }



                break;

            case R.id.btnNewSumCancel:
                finish();
                break;
        }

    }
}
