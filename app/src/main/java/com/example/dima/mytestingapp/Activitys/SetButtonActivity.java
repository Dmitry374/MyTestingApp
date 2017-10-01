package com.example.dima.mytestingapp.Activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class SetButtonActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnOk;
    EditText edBtnName;

    Spinner spinner;
    int imageId;  //  ID изображения

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    String typeGrid;

    long id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_button);
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

                finish();
            }
        });

        Intent intent = getIntent();
        typeGrid = intent.getStringExtra("set_button_type");


        btnOk = (Button) findViewById(R.id.btnApply);
        edBtnName = (EditText) findViewById(R.id.edBtnName);
        btnOk.setOnClickListener(this);

        final ArrayList<ItemData> list = new ArrayList<>();

        if (typeGrid.equals("set_btn_get")){
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
            list.add(new ItemData(R.drawable.get15));
            list.add(new ItemData(R.drawable.get16));
        } else {
            list.add(new ItemData(R.drawable.spend1));
            list.add(new ItemData(R.drawable.spend2));
            list.add(new ItemData(R.drawable.spend3));
            list.add(new ItemData(R.drawable.spend4));
            list.add(new ItemData(R.drawable.spend5));
            list.add(new ItemData(R.drawable.spend6));
            list.add(new ItemData(R.drawable.spend7));
            list.add(new ItemData(R.drawable.spend8));
            list.add(new ItemData(R.drawable.spend9));
            list.add(new ItemData(R.drawable.spend10));
            list.add(new ItemData(R.drawable.spend11));
            list.add(new ItemData(R.drawable.spend12));
            list.add(new ItemData(R.drawable.spend13));
            list.add(new ItemData(R.drawable.spend14));
            list.add(new ItemData(R.drawable.spend15));
            list.add(new ItemData(R.drawable.spend16));
        }

        spinner = (Spinner) findViewById(R.id.spinnerImages);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.spinner_layout, R.id.img, list);

        spinner.setAdapter(spinnerAdapter);

//        Нажатие на пункт списка Spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("myLogs", "spinner.getResources() = " + list.get((int) id).getImageId());

//                Принимаем Id для вставки в getResources()
                imageId = list.get((int) id).getImageId();


//                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), list.get((int) id).getImageId())); !!!!

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnApply:

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://myinfdb.000webhostapp.com")
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.build();

                ServerApi serverApi = retrofit.create(ServerApi.class);



                dbHelper = new DBHelper(this);

                db = dbHelper.getWritableDatabase();

                Intent intent = getIntent();
                typeGrid = intent.getStringExtra("set_button_type");

//                Извлечение Login
                sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                login = sPrefLogin.getString("save_login", "");

//                ---------------------------------------------------------------------------------------------------------------------

                if (typeGrid.equals("set_btn_get")){
                    Cursor cursor = db.query(DBHelper.TABLE_GET, null, null, null, null, null, null);

                    Boolean nameInTable = false;  //  Есть ли в таблице такое имя
//
//                Здесь идёт перебор всех записей из БД и сравнивание с введенными данными,
//                если введённые данные существуют, переходим на FragmentMain,
//                если НЕТ - выводим предупреждение(Toast)
                    if (cursor.moveToFirst()) {
                        int btnNameIndex = cursor.getColumnIndex(DBHelper.KEY_GET_NAME);
                        do {
                            if (edBtnName.getText().toString().equals(cursor.getString(btnNameIndex))) {


                                Toast.makeText(this, "Кнопка с таким именем уже есть", Toast.LENGTH_SHORT).show();

                                nameInTable = true;

                                break;


                            } else if (cursor.isLast()) {
                                Toast toast = Toast.makeText(this, "Новая кнопка создана", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 330);
                                toast.show();
                                nameInTable = false;
                                break;
                            }
                        } while (cursor.moveToNext());
                    }

                    if (!nameInTable){

//                        Запись в локальную бд
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(DBHelper.KEY_GET_NAME, edBtnName.getText().toString());
                        contentValues.put(DBHelper.KEY_GET_IMAGE, imageId);
                        contentValues.put(DBHelper.KEY_KOL_GET, 0);
                        contentValues.put(DBHelper.KEY_REF_LOGIN, login);
                        contentValues.put(DBHelper.KEY_GET_SYNCHRONISE, "");

                        id = db.insert(DBHelper.TABLE_GET, null, contentValues);


//                        Запись на сервер
                        Call<Void> call = serverApi.addNewButton("table_get", edBtnName.getText().toString(),
                                String.valueOf(imageId), "0", login, String.valueOf(id));

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_GET_NAME + " = '" + edBtnName.getText().toString() + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'ins' where "
                                        + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                        + DBHelper.KEY_GET_NAME + " = '" + edBtnName.getText().toString() + "'";
                                db.execSQL(sql);

                            }
                        });

//                        intent.putExtra("btnName", edBtnName.getText().toString());
//                        intent.putExtra("imageId", imageId);

                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    cursor.close();
                    break;
                } else {
                    Cursor cursor = db.query(DBHelper.TABLE_SPEND, null, null, null, null, null, null);

                    Boolean nameInTable = false;  //  Есть ли в таблице такое имя
//
//                Здесь идёт перебор всех записей из БД и сравнивание с введенными данными,
//                если введённые данные существуют, переходим на FragmentMain,
//                если НЕТ - выводим предупреждение(Toast)
                    if (cursor.moveToFirst()) {
                        int btnNameIndex = cursor.getColumnIndex(DBHelper.KEY_SPEND_NAME);
                        do {
                            if (edBtnName.getText().toString().equals(cursor.getString(btnNameIndex))) {


                                Toast.makeText(this, "Кнопка с таким именем уже есть", Toast.LENGTH_SHORT).show();

                                nameInTable = true;

                                break;


                            } else if (cursor.isLast()) {
                                Toast toast = Toast.makeText(this, "Новая кнопка создана", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 330);
                                toast.show();
                                nameInTable = false;
                                break;
                            }
                        } while (cursor.moveToNext());
                    }

                    if (!nameInTable){

//                        Запись в локальную бд
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(DBHelper.KEY_SPEND_NAME, edBtnName.getText().toString());
                        contentValues.put(DBHelper.KEY_SPEND_IMAGE, imageId);
                        contentValues.put(DBHelper.KEY_KOL_SPEND, 0);
                        contentValues.put(DBHelper.KEY_REF_LOGIN_SPEND, login);
                        contentValues.put(DBHelper.KEY_SPEND_SYNCHRONISE, "");

                        id = db.insert(DBHelper.TABLE_SPEND, null, contentValues);


//                        Запись на сервер
                        Call<Void> call = serverApi.addNewButton("table_spend", edBtnName.getText().toString(),
                                String.valueOf(imageId), "0", login, String.valueOf(id));

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                        + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                        + DBHelper.KEY_SPEND_NAME + " = '" + edBtnName.getText().toString() + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'ins' where "
                                        + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                        + DBHelper.KEY_SPEND_NAME + " = '" + edBtnName.getText().toString() + "'";
                                db.execSQL(sql);

                            }
                        });

//                        intent.putExtra("btnName", edBtnName.getText().toString());
//                        intent.putExtra("imageId", imageId);

                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    cursor.close();
                    break;
                }




//                ---------------------------------------------------------------------------------------------------------------------

//                ContentValues contentValues = new ContentValues();
//
//                contentValues.put(DBHelper.KEY_GET_NAME, edBtnName.getText().toString());
//                contentValues.put(DBHelper.KEY_GET_IMAGE, imageId);
//                contentValues.put(DBHelper.KEY_KOL_GET, 0);
//                contentValues.put(DBHelper.KEY_REF_LOGIN, login);
//
//                db.insert(DBHelper.TABLE_GET, null, contentValues);
//
//
//
//                intent.putExtra("btnName", edBtnName.getText().toString());
//                intent.putExtra("imageId", imageId);


        }
//        setResult(RESULT_OK, intent);
//        finish();
    }
}
