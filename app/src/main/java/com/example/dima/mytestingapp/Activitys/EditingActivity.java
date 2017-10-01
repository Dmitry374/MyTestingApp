package com.example.dima.mytestingapp.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Adapters.SpinnerAdapter;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.ItemData;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvNameText;

    Button buttonApplyEditing;
    EditText edNewBtnName;

    Spinner spinnerButtonImage;
    int imageId;  //  ID изображения

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    String getAction;
    String btnName;

    ArrayList<ItemData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        Intent intent = getIntent();
        btnName = intent.getStringExtra("btnName");

        tvNameText = (TextView) findViewById(R.id.tvNameText);
        tvNameText.setText("Редактирование раздела " + btnName);

        buttonApplyEditing = (Button) findViewById(R.id.buttonApplyEditing);
        buttonApplyEditing.setOnClickListener(this);
        edNewBtnName = (EditText) findViewById(R.id.edNewBtnName);
        spinnerButtonImage = (Spinner) findViewById(R.id.spinnerButtonImage);

        intent = getIntent();
        getAction = intent.getStringExtra("action_key");

        if (getAction.equals("action_spend")){
            list.add(new ItemData(R.drawable.spend1));
            list.add(new ItemData(R.drawable.spend2));
            list.add(new ItemData(R.drawable.spend3));

            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.img, list);

            spinnerButtonImage.setAdapter(spinnerAdapter);
        } else {
            list.add(new ItemData(R.drawable.get1));
            list.add(new ItemData(R.drawable.get2));
            list.add(new ItemData(R.drawable.get3));
            list.add(new ItemData(R.drawable.get4));
            list.add(new ItemData(R.drawable.get5));
            list.add(new ItemData(R.drawable.get6));
            list.add(new ItemData(R.drawable.get7));
            list.add(new ItemData(R.drawable.get8));
            list.add(new ItemData(R.drawable.get9));
            list.add(new ItemData(R.drawable.get10));
            list.add(new ItemData(R.drawable.get11));
            list.add(new ItemData(R.drawable.get12));
            list.add(new ItemData(R.drawable.get13));
            list.add(new ItemData(R.drawable.get14));

            SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.img, list);

            spinnerButtonImage.setAdapter(spinnerAdapter);
        }

        spinnerButtonImage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageId = list.get((int) id).getImageId();
//                Toast.makeText(EditingActivity.this, "imageId = " + imageId, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonApplyEditing:

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://myinfdb.000webhostapp.com")
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.build();

                ServerApi serverApi = retrofit.create(ServerApi.class);


                dbHelper = new DBHelper(this);

                db = dbHelper.getWritableDatabase();

                Cursor cursor;

//                ContentValues contentValues = new ContentValues();

//                Извлечение Login
                sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                login = sPrefLogin.getString("save_login", "");

                Intent intent = getIntent();
                getAction = intent.getStringExtra("action_key");
                btnName = intent.getStringExtra("btnName");

//                Редактирование Spend
                if (getAction.equals("action_spend")) {
                    cursor = db.query(DBHelper.TABLE_SPEND, null, null, null, null, null, null);


                    Boolean nameInTable = null;  //  Есть ли в таблице такое имя
//
//                Здесь идёт перебор всех записей из БД и сравнивание с введенными данными,
//                если введённые данные существуют, переходим на FragmentMain,
//                если НЕТ - выводим предупреждение(Toast)
                    if (cursor.moveToFirst()) {
                        int btnNameIndex = cursor.getColumnIndex(DBHelper.KEY_SPEND_NAME);
                        do {
                            if (edNewBtnName.getText().toString().equals(cursor.getString(btnNameIndex))) {


                                Toast.makeText(this, "Кнопка с таким именем уже есть", Toast.LENGTH_SHORT).show();

                                nameInTable = true;

                                break;


                            } else if (cursor.isLast()) {
                                Toast toast = Toast.makeText(this, "Кнопка обновлена", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 330);
                                toast.show();
                                nameInTable = false;
                                break;
                            }
                        } while (cursor.moveToNext());
                    }

                    if (!nameInTable){
//                        Обновление Имени
                        String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_NAME + " = '"
                                + edNewBtnName.getText().toString()
                                + "' where " + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                + DBHelper.KEY_SPEND_NAME + " = '" + btnName + "'";
                        db.execSQL(sql);

//                        Обновление изображения
                        sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_IMAGE + " = "
                                + imageId + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                + DBHelper.KEY_SPEND_NAME + " = '" + edNewBtnName.getText().toString() + "'";
                        db.execSQL(sql);


//                        Обновление на сервере
                        Call<Void> call = serverApi.editButton("table_spend", edNewBtnName.getText().toString(),
                                String.valueOf(imageId), btnName, login);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                        + DBHelper.KEY_SPEND_NAME + " = '" + edNewBtnName.getText().toString() + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'no' where "
                                        + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                        + DBHelper.KEY_SPEND_NAME + " = '" + edNewBtnName.getText().toString() + "' and "
                                        + DBHelper.KEY_SPEND_SYNCHRONISE + " != 'ins'";
                                db.execSQL(sql);

                            }
                        });





//                        Обновление таблицы Table Statistic
                        sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '"
                                + edNewBtnName.getText().toString()
                                + "' where " + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '" + btnName + "' and "
                                + DBHelper.KEY_STATISTIC_TYPE + " = '" + "spend" + "'";
                        db.execSQL(sql);


                        call = serverApi.editStatistic(edNewBtnName.getText().toString(), login,
                                btnName, "spend");

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '" + edNewBtnName.getText().toString() + "' and "
                                        + DBHelper.KEY_STATISTIC_TYPE + " = '" + "spend" + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'no' where "
                                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '" + edNewBtnName.getText().toString() + "' and "
                                        + DBHelper.KEY_STATISTIC_TYPE + " = '" + "spend" + "'";
                                db.execSQL(sql);

                            }
                        });



                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    cursor.close();


//                    Редактирование Get
                } else {

                    cursor = db.query(DBHelper.TABLE_GET, null, null, null, null, null, null);


                    Boolean nameInTable = null;  //  Есть ли в таблице такое имя
//
//                Здесь идёт перебор всех записей из БД и сравнивание с введенными данными,
//                если введённые данные существуют, переходим на FragmentMain,
//                если НЕТ - выводим предупреждение(Toast)
                    if (cursor.moveToFirst()) {
                        int btnNameIndex = cursor.getColumnIndex(DBHelper.KEY_GET_NAME);
                        do {
                            if (edNewBtnName.getText().toString().equals(cursor.getString(btnNameIndex))) {


                                Toast.makeText(this, "Кнопка с таким именем уже есть", Toast.LENGTH_SHORT).show();

                                nameInTable = true;

                                break;


                            } else if (cursor.isLast()) {
                                Toast toast = Toast.makeText(this, "Кнопка обновлена", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 330);
                                toast.show();
                                nameInTable = false;
                                break;
                            }
                        } while (cursor.moveToNext());
                    }

                    if (!nameInTable){
//                        Обновление Имени
                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_NAME + " = '"
                                + edNewBtnName.getText().toString()
                                + "' where " + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_GET_NAME + " = '" + btnName + "'";
                        db.execSQL(sql);

//                        Обновление изображения
                        sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_IMAGE + " = "
                                + imageId + " where " + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_GET_NAME + " = '" + edNewBtnName.getText().toString() + "'";
                        db.execSQL(sql);

//                        Обновление на сервере
                        Call<Void> call = serverApi.editButton("table_get", edNewBtnName.getText().toString(),
                                String.valueOf(imageId), btnName, login);

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '" + edNewBtnName.getText().toString() + "' and "
                                        + DBHelper.KEY_STATISTIC_TYPE + " = '" + "get" + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'no' where "
                                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '" + edNewBtnName.getText().toString() + "' and "
                                        + DBHelper.KEY_STATISTIC_TYPE + " = '" + "get" + "'";
                                db.execSQL(sql);

                            }
                        });








//                        Обновление таблицы Table Get
                        sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_NAME_GET + " = '"
                                + edNewBtnName.getText().toString()
                                + "' where " + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_STATISTIC_NAME_GET + " = '" + btnName + "' and "
                                + DBHelper.KEY_STATISTIC_TYPE + " = '" + "get" + "'";
                        db.execSQL(sql);

                        call = serverApi.editStatistic(edNewBtnName.getText().toString(), login,
                                btnName, "get");

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '" + edNewBtnName.getText().toString() + "' and "
                                        + DBHelper.KEY_STATISTIC_TYPE + " = '" + "get" + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'no' where "
                                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_STATISTIC_NAME_SPEND + " = '" + edNewBtnName.getText().toString() + "' and "
                                        + DBHelper.KEY_STATISTIC_TYPE + " = '" + "get" + "'";
                                db.execSQL(sql);

                            }
                        });





                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    cursor.close();
                }



                break;
        }
    }
}
