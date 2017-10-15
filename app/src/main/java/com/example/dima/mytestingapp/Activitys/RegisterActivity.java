package com.example.dima.mytestingapp.Activitys;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dima.mytestingapp.Activitys.HelloUserSplashActivity;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.ItemServerData;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edName, edSurname, edPatronymic, edMobile, edEmail, edPassword, edLogin;
    EditText edDateOfBirth;

    Spinner edGender;

    Button btnRegister;

    String gender;

    int countEmail = 0;  //  Колисество строк в таблице "user" гле одинаковый email (если > 0, то выводим Toast)
    int countLogin = 0;  //  Колисество строк в таблице "user" гле одинаковый login (если > 0, то выводим Toast)

    SQLiteDatabase database;

    ContentValues contentValues;


//    Для запоминания вхождения в аккаунт
    SharedPreferences sPref;
    Boolean loginPref;
    final String SAVED_TEXT = "saved_text";

    SharedPreferences sPrefLogin, sPrefUserName, sPrefEmail, sPrefPassword;

    String sLogin;  // SharedPreferences для login
    String sUserName;  // SharedPreferences для userName
    String sEmail;  // SharedPreferences для email


    int DIALOG_DATE = 1;
//    int myYear = 2011;
//    int myMonth = 02;
//    int myDay = 03;

//    Значение даты по умолчанию
    Calendar calendar = Calendar.getInstance();
    int myYear = calendar.get(Calendar.YEAR) - 20;
    int myMonth = calendar.get(Calendar.MONTH);
    int myDay = calendar.get(Calendar.DAY_OF_MONTH);

    DBHelper dbHelper;  // Объект базы данных

    String email;
    String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edName = (EditText) findViewById(R.id.edName);
        edSurname = (EditText) findViewById(R.id.edSurname);
        edPatronymic = (EditText) findViewById(R.id.edPatronymic);

        edDateOfBirth = (EditText) findViewById(R.id.edDateOfBirth);
        edDateOfBirth.setOnClickListener(this);
//        Отключение редактирования
        edDateOfBirth.setInputType(InputType.TYPE_NULL);

        edMobile = (EditText) findViewById(R.id.edMobile);
//        Формат ввода номера
        edMobile.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        edMobile.setMaxLines(16); // ?????????

        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edLogin = (EditText) findViewById(R.id.edLogin);

        edGender = (Spinner) findViewById(R.id.edGender);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        dbHelper = new DBHelper(this);

//        При открытии Activity поле Name - в фокусе
//        edName.requestFocus();





//        Отображается диалог при нажатии первый раз на поле Даты
//        edDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                showDialog(DIALOG_DATE);
//            }
//        });

//        Отображается диалог при нажатии на поле Дата
        edDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edDateOfBirth.hasFocus()){
//                    DateDialog dialog = new DateDialog(v);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    dialog.show(ft, "DialogPicker");

                    showDialog(DIALOG_DATE);
                }
            }
        });

//        Обработка поля Мобильный телефон
        edMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edMobile.hasFocus()){
                    edMobile.setText("+7");

                    Selection.setSelection(edMobile.getText(), edMobile.getText().length());
                    edMobile.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!s.toString().startsWith("+7")) {
                                edMobile.setText("+7");
                                Selection.setSelection(edMobile.getText(), edMobile
                                        .getText().length());

                            }
                        }
                    });
                }
            }
        });

//        Обработка Spinner Пол
        edGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("myLogs", String.valueOf(parent.getItemAtPosition(position)));
                gender = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Закрытие onCreate
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
//            myYear = year;
//            myMonth = month;
//            myDay = dayOfMonth;
//            edDateOfBirth.setText(myDay + "." + (myMonth + 1) + "." + myYear);

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

            edDateOfBirth.setText(date);
        }
    };
//    ---------------------------------------------------------------------------------

    @Override
    public void onClick(View v) {

        String name = edName.getText().toString().trim();
        String surname = edSurname.getText().toString().trim();
        String patronymic = edPatronymic.getText().toString().trim();
//        Поле gender глобальная переменная

        String dateOfBirth = edDateOfBirth.getText().toString().trim();
//        String day = dateOfBirth.substring(0,2);
//        String month = dateOfBirth.substring(3,5);
//        String year = dateOfBirth.substring(6,10);

        String mobile = edMobile.getText().toString().replaceAll("^(\\+7)|\\D+","").trim();
        email = edEmail.getText().toString().trim();
        login = edLogin.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

//        -----------------------------------------------------------------------------------

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

//        --------------------------------------------------------------------------------------------------
//        Проверка на Email
        Call<String> callEmail = serverApi.countEmail(email);

        callEmail.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                countEmail = Integer.parseInt(response.body());
                if (countEmail == 1){
                    edEmail.setText("");
                    email = "";
                    edEmail.requestFocus();
                    Toast toast = Toast.makeText(RegisterActivity.this, "Пользователь с такой почтой уже существует !!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 320);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error countEmail", Toast.LENGTH_SHORT).show();
            }
        });

//        Проверка на Login
        Call<String> callLogin = serverApi.countLogin(login);

        callLogin.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                countLogin = Integer.parseInt(response.body());
                if (countLogin == 1){
                    edLogin.setText("");
                    login = "";
                    edLogin.requestFocus();

                    Toast toast = Toast.makeText(RegisterActivity.this, "Пользователь с таким логином уже существует !!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 320);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error countLogin", Toast.LENGTH_SHORT).show();
            }
        });

//        ------------------------------------------------------------------------------------------------------------------




//        Открытие БД для чтения и записи
        database = dbHelper.getWritableDatabase();

        contentValues = new ContentValues();


        switch (v.getId()){
            case R.id.edDateOfBirth:
                if (edDateOfBirth.hasFocus()){
//                    DateDialog dialog = new DateDialog(v);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    dialog.show(ft, "DialogPicker");

                    showDialog(DIALOG_DATE);
                }
                break;
            case R.id.btnRegister:

//                Log.d("myLogs", "name = " + name + " surname = " + surname + " ochestvo = " + patronymic
//                    + " day = " + Integer.valueOf(day) + " month = " + Integer.valueOf(month) + " year = " + Integer.valueOf(year)
//                    + " gender = " + gender + " mobile = " + mobile + " email = " + email
//                    + " login = " + login + " password = " + password);

                Log.d("myLogs", "name = " + name + " surname = " + surname + " ochestvo = " + patronymic
                        + " dateOfBirth = " + dateOfBirth
                        + " gender = " + gender + " mobile = " + mobile + " email = " + email
                        + " login = " + login + " password = " + password);

                if ((name.equals("")) || (surname.equals("")) || (patronymic.equals("")) || (gender.equals(""))
                        || mobile.equals("") || (email.equals("")) || (password.equals(""))
                        || (login.equals(""))){
                    Toast toast = Toast.makeText(this, "Заполние все поля !!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 320);
                    toast.show();

//                    Проверка на корректность почты
                } else if (!isValidEmailAddress(email)){
                    Toast toast = Toast.makeText(this, "Введите корректный E-mail адрес !!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 320);
                    toast.show();

                    edEmail.setText("");

//                    Проверка на уникальность почты
                } /*else if (countEmail == 1){

                    Toast toast = Toast.makeText(this, "Пользователь с такой почтой уже существует !!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 320);
                    toast.show();

                    edEmail.setText("");

//                    Проверка на уникальность логина
                } else if (countLogin == 1){

                    Toast toast = Toast.makeText(this, "Пользователь с таким логином уже существует !!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 320);
                    toast.show();

                    edLogin.setText("");
                } */

                else {

//                    Запись данных на сервер
                    Call<List<ItemServerData>> callData = serverApi.saveDataUser(name, surname, patronymic, gender, dateOfBirth,
                            mobile, email, login, password, "0");

                    callData.enqueue(new Callback<List<ItemServerData>>() {
                        @Override
                        public void onResponse(Call<List<ItemServerData>> call, Response<List<ItemServerData>> response) {

                            Toast.makeText(RegisterActivity.this, "All is OK !!!", Toast.LENGTH_SHORT).show();

                            String sName = null, sSurname = null, sPatronymic = null, sGender = null,
                                    sDateOfBirth = null, sMobilePhone = null,
                                    sEmail = null, sLogin = null, sPassword = null, sKeySign = null;

//                            Прием данных обратно с сервера
                            for (int i = 0; i < response.body().size(); i++){
                                sName = response.body().get(i).getName();
                                sSurname = response.body().get(i).getSurname();
                                sPatronymic = response.body().get(i).getPatronymic();
                                sGender = response.body().get(i).getGender();
                                sDateOfBirth = response.body().get(i).getDateOfBirth();
                                sMobilePhone = response.body().get(i).getMobilePhone();
                                sEmail = response.body().get(i).getEmail();
                                sLogin = response.body().get(i).getLogin();
                                sPassword = response.body().get(i).getPassword();
                                sKeySign = response.body().get(i).getKeySign();
                            }

                            contentValues.put(DBHelper.KEY_NAME, sName);
                            contentValues.put(DBHelper.KEY_SURNAME, sSurname);
                            contentValues.put(DBHelper.KEY_PATRONYMIC, sPatronymic);
                            contentValues.put(DBHelper.KEY_GENDER, sGender);
                            contentValues.put(DBHelper.KEY_DATE_OF_BIRTH, sDateOfBirth);
                            contentValues.put(DBHelper.KEY_MOBILE, sMobilePhone);
                            contentValues.put(DBHelper.KEY_EMAIL, sEmail);
                            contentValues.put(DBHelper.KEY_LOGIN, sLogin);
                            contentValues.put(DBHelper.KEY_PASSWORD, sPassword);
                            contentValues.put(DBHelper.KEY_SIGN_IN, sKeySign);
                            contentValues.put(DBHelper.KEY_SYNCHRONISE, "yes");

                            database.insert(DBHelper.TABLE_USER, null, contentValues);


//                    ********************** Запоминаеи вхождение в аккаунт *********************
                            sPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sPref.edit();
                            ed.putBoolean(SAVED_TEXT, true);
                            ed.commit();
//                            ----------- Сохраняем данные о пользователе -------------------------------
                            sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                            SharedPreferences.Editor edPrefLogin = sPrefLogin.edit();
                            edPrefLogin.putString("save_login", sLogin);
                            edPrefLogin.commit();

                            sPrefUserName = getSharedPreferences("SharedPrefUserName",MODE_PRIVATE);
                            SharedPreferences.Editor edPrefUserName = sPrefUserName.edit();
                            edPrefUserName.putString("save_user_name", sName);
                            edPrefUserName.commit();

                            sPrefEmail = getSharedPreferences("SharedPrefEmail",MODE_PRIVATE);
                            SharedPreferences.Editor edPrefEmail = sPrefEmail.edit();
                            edPrefEmail.putString("save_email", sEmail);
                            edPrefEmail.commit();
//                    ----------------------------------------------------------------------------------

//                    Переход на Activity с заставкой
                            Intent intent = new Intent(RegisterActivity.this, HelloUserSplashActivity.class);
                            intent.putExtra("login", sLogin);
                            intent.putExtra("userName", sName);
                            intent.putExtra("email", sEmail);
                            startActivity(intent);
                            finish();  //  Завершает активность


                        }

                        @Override
                        public void onFailure(Call<List<ItemServerData>> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, "Нет подключения к сети !!!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
        }
        //dbHelper.close();
    }

//    Функция проверки почты
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//    }
}
