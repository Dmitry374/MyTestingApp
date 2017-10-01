package com.example.dima.mytestingapp.Activitys;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dima.mytestingapp.AlarmReceiver;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.ItemServerData;
import com.example.dima.mytestingapp.Items.ItemServerGet;
import com.example.dima.mytestingapp.Items.ItemServerReminder;
import com.example.dima.mytestingapp.Items.ItemServerSpend;
import com.example.dima.mytestingapp.Items.ItemServerStatistic;
import com.example.dima.mytestingapp.Items.ItemServerTotal;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;  // Кнпка Login для входа
    Button btnRegister;  // Переход на регистрацию

    EditText edLogin, edPassword;

    String login; // Переменная для принятия имени из EditText
    String password; // Переменная для принятия пароля из EditText

    DBHelper dbHelper;  // Объект базы данных
    SQLiteDatabase db;

    ContentValues contentValues;

    //    Для запоминания вхождения в аккаунт
    SharedPreferences sPref;
    Boolean loginPref;
    final  String SAVED_TEXT = "saved_text";

    SharedPreferences sPrefLogin, sPrefUserName, sPrefEmail, sPrefPassword;

    String sLogin;  // SharedPreferences для login
    String sUserName;  // SharedPreferences для userName
    String sEmail;  // SharedPreferences для email

    private long mRepeatTime;

    ServerApi serverApi;

    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    private static final long milYear = 31536000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

//        btnRegister = (Button) findViewById(R.id.btnRegister);
//        btnRegister.setOnClickListener(this);

        btnLogin = (Button) findViewById(R.id.btnLoginLogin);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btnLoginRegister);
        btnRegister.setOnClickListener(this);

        edLogin = (EditText) findViewById(R.id.edLoginLogin);
        edPassword = (EditText) findViewById(R.id.edLoginPassword);

        dbHelper = new DBHelper(this);

        edLogin.requestFocus();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        sPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
        loginPref = sPref.getBoolean(SAVED_TEXT, false);

//        --------------------- Извлекаем данные о пользователе --------------------------
        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        sLogin = sPrefLogin.getString("save_login", "");

        sPrefUserName = getSharedPreferences("SharedPrefUserName",MODE_PRIVATE);
        sUserName = sPrefUserName.getString("save_user_name", "");

        sPrefEmail = getSharedPreferences("SharedPrefEmail",MODE_PRIVATE);
        sEmail = sPrefEmail.getString("save_email", "");

//        sPrefPassword = getSharedPreferences("SharedPrefPassword", MODE_PRIVATE);
//        sPassword = sPrefPassword.getString("save_password", "");

        edLogin.setText(sLogin);


//        Если пользователь не вышел из аккаунта
        if (loginPref){
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra("userName", sUserName);
            intent.putExtra("login", sLogin);
            intent.putExtra("email", sEmail);
            startActivity(intent);
        }

//        edLogin.setText("");
//        edPassword.setText("");

        edLogin.requestFocus();
    }

    @Override
    public void onClick(View v) {

        login = edLogin.getText().toString();
        password = edPassword.getText().toString();

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        switch (v.getId()){
            case R.id.btnLoginLogin:

//                loadDataFromServer();

                contentValues = new ContentValues();

//                Загрузка данных из сервера
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("https://myinfdb.000webhostapp.com")
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.build();

                final ServerApi serverApi = retrofit.create(ServerApi.class);

                Call<List<ItemServerData>> loginUser = serverApi.loginUser(login, password);

                loginUser.enqueue(new Callback<List<ItemServerData>>() {
                    @Override
                    public void onResponse(Call<List<ItemServerData>> call, Response<List<ItemServerData>> response) {

                        String nameFromServ = null, surnameFromServ = null, patronymicFromServ = null, genderFromServ = null,
                                dateOfBirthFromServ = null, mobilePhoneFromServ = null,
                                emailFromServ = null, loginFromServ = null, passwordFromServ = null, keySignFromServ = null;

//                            Прием данных обратно с сервера
                        for (int i = 0; i < response.body().size(); i++){
                            nameFromServ = response.body().get(i).getName();
                            surnameFromServ = response.body().get(i).getSurname();
                            patronymicFromServ = response.body().get(i).getPatronymic();
                            genderFromServ = response.body().get(i).getGender();
                            dateOfBirthFromServ = response.body().get(i).getDateOfBirth();
                            mobilePhoneFromServ = response.body().get(i).getMobilePhone();
                            emailFromServ = response.body().get(i).getEmail();
                            loginFromServ = response.body().get(i).getLogin();
                            passwordFromServ = response.body().get(i).getPassword();
                            keySignFromServ = response.body().get(i).getKeySign();
                        }

                        contentValues.put(DBHelper.KEY_NAME, nameFromServ);
                        contentValues.put(DBHelper.KEY_SURNAME, surnameFromServ);
                        contentValues.put(DBHelper.KEY_PATRONYMIC, patronymicFromServ);
                        contentValues.put(DBHelper.KEY_GENDER, genderFromServ);
                        contentValues.put(DBHelper.KEY_DATE_OF_BIRTH, dateOfBirthFromServ);
                        contentValues.put(DBHelper.KEY_MOBILE, mobilePhoneFromServ);
                        contentValues.put(DBHelper.KEY_EMAIL, emailFromServ);
                        contentValues.put(DBHelper.KEY_LOGIN, loginFromServ);
                        contentValues.put(DBHelper.KEY_PASSWORD, passwordFromServ);
                        contentValues.put(DBHelper.KEY_SIGN_IN, keySignFromServ);
                        contentValues.put(DBHelper.KEY_SYNCHRONISE, "yes");

                        db.insert(DBHelper.TABLE_USER, null, contentValues);




                        contentValues.clear();

//        ******************************* Загрузка данных с сервера ***********************************

//        --------------------------------------- Get -------------------------------------------------

                        Call<List<ItemServerGet>> callGet = serverApi.loadTableGet(loginFromServ);

                        callGet.enqueue(new Callback<List<ItemServerGet>>() {
                            @Override
                            public void onResponse(Call<List<ItemServerGet>> call, Response<List<ItemServerGet>> response) {
                                String name = "", image = "", kol = "", refLogin = "", localId = "";

//                            Прием данных обратно с сервера
                                for (int i = 0; i < response.body().size(); i++) {
                                    localId = response.body().get(i).getLocalId();
                                    name = response.body().get(i).getName();
                                    image = response.body().get(i).getImage();
                                    kol = response.body().get(i).getKol();
                                    refLogin = response.body().get(i).getRefLogin();


                                    contentValues.put(DBHelper.KEY_GET_ID, localId);
                                    contentValues.put(DBHelper.KEY_GET_NAME, name);
                                    contentValues.put(DBHelper.KEY_GET_IMAGE, image);
                                    contentValues.put(DBHelper.KEY_KOL_GET, kol);
                                    contentValues.put(DBHelper.KEY_REF_LOGIN, refLogin);
                                    contentValues.put(DBHelper.KEY_GET_SYNCHRONISE, "yes");

                                    db.insert(DBHelper.TABLE_GET, null, contentValues);

                                    contentValues.clear();

                                }
                            }

                            @Override
                            public void onFailure(Call<List<ItemServerGet>> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            }
                        });

//                        ------------------------------- Spend ---------------------------------

                        Call<List<ItemServerSpend>> callSpend = serverApi.loadTableSpend(loginFromServ);

                        callSpend.enqueue(new Callback<List<ItemServerSpend>>() {
                            @Override
                            public void onResponse(Call<List<ItemServerSpend>> call, Response<List<ItemServerSpend>> response) {

                                String name = "", image = "", kol = "", refLogin = "", localId = "";

//                            Прием данных обратно с сервера
                                for (int i = 0; i < response.body().size(); i++) {
                                    localId = response.body().get(i).getLocalId();
                                    name = response.body().get(i).getName();
                                    image = response.body().get(i).getImage();
                                    kol = response.body().get(i).getKol();
                                    refLogin = response.body().get(i).getRefLogin();


                                    contentValues.put(DBHelper.KEY_SPEND_ID, localId);
                                    contentValues.put(DBHelper.KEY_SPEND_NAME, name);
                                    contentValues.put(DBHelper.KEY_SPEND_IMAGE, image);
                                    contentValues.put(DBHelper.KEY_KOL_SPEND, kol);
                                    contentValues.put(DBHelper.KEY_REF_LOGIN_SPEND, refLogin);
                                    contentValues.put(DBHelper.KEY_SPEND_SYNCHRONISE, "yes");

                                    db.insert(DBHelper.TABLE_SPEND, null, contentValues);

                                    contentValues.clear();
                                }

                            }

                            @Override
                            public void onFailure(Call<List<ItemServerSpend>> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            }
                        });

//                        --------------------------- Statistic ------------------------------------

                        Call<List<ItemServerStatistic>> callStatistic = serverApi.loadTableStatistic(loginFromServ);

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
                                    refLogin = response.body().get(i).getRefLogin();


                                    contentValues.put(DBHelper.KEY_STATISTIC_TYPE, type);
                                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_GET, nameGet);
                                    contentValues.put(DBHelper.KEY_STATISTIC_KOL, kol);
                                    contentValues.put(DBHelper.KEY_STATISTIC_NAME_SPEND, nameSpend);
                                    contentValues.put(DBHelper.KEY_STATISTIC_DATE, date);
                                    contentValues.put(DBHelper.KEY_STATISTIC_TIME, time);
                                    contentValues.put(DBHelper.KEY_STATISTIC_REF_LOGIN, login);
                                    contentValues.put(DBHelper.KEY_STATISTIC_SYNCHRONISE, "yes");

                                    db.insert(DBHelper.TABLE_STATISTIC, null, contentValues);

                                    contentValues.clear();
                                }

                            }

                            @Override
                            public void onFailure(Call<List<ItemServerStatistic>> call, Throwable t) {

                            }
                        });

//                        ------------------------ Total ----------------------------

                        Call<List<ItemServerTotal>> callTotal = serverApi.loadTableTotal(loginFromServ);

                        callTotal.enqueue(new Callback<List<ItemServerTotal>>() {
                            @Override
                            public void onResponse(Call<List<ItemServerTotal>> call, Response<List<ItemServerTotal>> response) {
                                String kol = "", refLogin = "";

                                //                            Прием данных обратно с сервера
                                for (int i = 0; i < response.body().size(); i++) {
                                    kol = response.body().get(i).getKol();
                                    refLogin = response.body().get(i).getRefLogin();


                                    contentValues.put(DBHelper.KEY_KOL_TOTAL_GET, kol);
                                    contentValues.put(DBHelper.KEY_REF_LOGIN_TOTAL_GET, refLogin);
                                    contentValues.put(DBHelper.KEY_TOTAL_SYNCHRONISE, "yes");

                                    db.insert(DBHelper.TABLE_GET_FOR_TOTAL, null, contentValues);

                                    contentValues.clear();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<ItemServerTotal>> call, Throwable t) {

                            }
                        });



//                        --------------------------- Reminders ------------------------------------

                        Call<List<ItemServerReminder>> callReminders = serverApi.loadReminders(loginFromServ);

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
                                    refLogin = response.body().get(i).getRefLogin();

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
                                            new AlarmReceiver().setAlarm(LoginActivity.this, mCalendar, (int) id);
                                        } else {
                                            Log.d("myLogs", "setRepeatAlarm");
                                            new AlarmReceiver().setRepeatAlarm(LoginActivity.this, mCalendar, (int) id, mRepeatTime);
                                        }


                                    }
//                                    ---------------------------------------------

                                    contentValues.clear();
                                }

                            }

                            @Override
                            public void onFailure(Call<List<ItemServerReminder>> call, Throwable t) {

                            }
                        });

//                        --------------------- Завершение загрузки с сервера --------------------------------------------------









                        Intent intent = new Intent(LoginActivity.this, HelloUserSplashActivity.class);
                        intent.putExtra("login", loginFromServ);
                        intent.putExtra("userName", nameFromServ);
                        intent.putExtra("email", emailFromServ);
                        startActivity(intent);

//                            ********************** Запоминаеи вхождение в аккаунт *********************
                        sPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putBoolean(SAVED_TEXT, true);
                        ed.commit();
//                            ----------- Сохраняем данные о пользователе -------------------------------
                        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                        SharedPreferences.Editor edPrefLogin = sPrefLogin.edit();
                        edPrefLogin.putString("save_login", loginFromServ);
                        edPrefLogin.commit();

                        sPrefUserName = getSharedPreferences("SharedPrefUserName",MODE_PRIVATE);
                        SharedPreferences.Editor edPrefUserName = sPrefUserName.edit();
                        edPrefUserName.putString("save_user_name", nameFromServ);
                        edPrefUserName.commit();

                        sPrefEmail = getSharedPreferences("SharedPrefEmail",MODE_PRIVATE);
                        SharedPreferences.Editor edPrefEmail = sPrefEmail.edit();
                        edPrefEmail.putString("save_email", emailFromServ);
                        edPrefEmail.commit();

                        finish();

                    }

                    @Override
                    public void onFailure(Call<List<ItemServerData>> call, Throwable t) {

                        Toast toast = Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 330);
                        toast.show();
                        edLogin.setText("");
                        edPassword.setText("");
                        edLogin.requestFocus();

                    }
                });


                break;

            case R.id.btnLoginRegister:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Завершает приложение
//        finishAffinity();
        ActivityCompat.finishAffinity(this);
    }
}
