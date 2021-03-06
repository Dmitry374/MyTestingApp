package com.example.dima.mytestingapp.Activitys;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.AlarmReceiver;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;
import com.example.dima.mytestingapp.fragments.FragmentKreditCalc;
import com.example.dima.mytestingapp.fragments.FragmentMain;
import com.example.dima.mytestingapp.fragments.FragmentReminder;
import com.example.dima.mytestingapp.fragments.FragmentStatistic;
import com.example.dima.mytestingapp.fragments.FragmentExchangeRates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import br.liveo.interfaces.OnPrepareOptionsMenuLiveo;
import br.liveo.model.HelpLiveo;
import br.liveo.model.Navigation;
import br.liveo.navigationliveo.NavigationLiveo;
import br.liveo.interfaces.OnItemClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserActivity1 extends NavigationLiveo implements OnItemClickListener
        /*implements NavigationView.OnNavigationItemSelectedListener*/ {

    FragmentMain fmain;
    FragmentKreditCalc fkrditcalk;
    FragmentStatistic fstatistic;
    FragmentReminder freminder;
    FragmentExchangeRates fexhangerate;

    TextView tvUserName;
    TextView emailUser;

//    String login;
    public String login;
    String userNameRes;
    String email;

    DBHelper dbHelper;
    SQLiteDatabase db;

//    Для проверки вхождения в аккаунт
    SharedPreferences sPref;
    Boolean loginPref;
    final String SAVED_TEXT = "saved_text";


    SharedPreferences sPrefLogin, sPrefUserName, sPrefEmail, sPrefSelection;

    String sLogin;  // SharedPreferences для login
    String sUserName;  // SharedPreferences для userNameRes
    String sEmail;  // SharedPreferences для email

    AlertDialog.Builder ad;
    AlertDialog.Builder adNoSynchroniseData;

    final int REQUEST_CODE_LIST = 1;

    FloatingActionButton fab;

    Intent intent;
    String fragmentName;

    ContentValues contentValues;

    int userId;
    String localId;
    String getName;
    String spendName;

    Timer mTimer;
    MyTimerTask mMyTimerTask;

    int num;

    String reminderType, reminderName, reminderDate, reminderTime, reminderSynchronise, reminderImgMarker,
            reminderImgMarkerName, reminderRepeat, reminderSound, reminderRefLogin;

    int k;

    String fileReminderInsert = "";
    String fileReminderUpdate = "";
    String fileReminderDelete = "";

    String[] arrayReminderInsert, arrayReminderUpdate, arrayReminderDelete;

    private HelpLiveo mHelpLiveo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

//        Проверяем подключение к Интернету, если есть подключение - загружаем данные на сервер
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        try{

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork.isConnectedOrConnecting();

            Log.d("muLogsCon", "isConnected = " + isConnected);

            if (isConnected){
                uploadToServer();
                Toast.makeText(this, "Uploading", Toast.LENGTH_SHORT);
            }

        } catch (java.lang.NullPointerException e){
            Log.d("muLogsCon", "Null");
        }



        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();


//        Загрузка данных на сервер через промежуток времени
        // delay 1000ms, repeat in 5000ms
        mTimer.schedule(mMyTimerTask, 1000, 10000);
//        mTimer.schedule(mMyTimerTask, 1000, 3600000);   // Час




//        Вставка иконки в Action Bar
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

        /*NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        fmain = new FragmentMain();
        fkrditcalk = new FragmentKreditCalc();
        fstatistic = new FragmentStatistic();
        freminder = new FragmentReminder();
        fexhangerate = new FragmentExchangeRates();

//        Определение Header для его измениния
        /*View header = navigationView.getHeaderView(0);

        tvUserName = (TextView)header.findViewById(R.id.tvUserName);
        emailUser = (TextView)header.findViewById(R.id.emailUser);*/

//        Intent intent = getIntent();
//        login = intent.getStringExtra("login");
//        userNameRes = intent.getStringExtra("userNameRes");
//        email = intent.getStringExtra("email");

//        Извлечение полей из SharedPreferences !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");

        sPrefUserName = getSharedPreferences("SharedPrefUserName", MODE_PRIVATE);
        userNameRes = sPrefUserName.getString("save_user_name", "");

        sPrefEmail = getSharedPreferences("SharedPrefEmail", MODE_PRIVATE);
        email = sPrefEmail.getString("save_email", "");

//        Toast.makeText(this, login + " " + userNameRes + " " + email, Toast.LENGTH_SHORT).show();


        contentValues = new ContentValues();


//        --------------------------- Всплывающее окно Alert Dialog -----------------------
        Context context = UserActivity1.this;

        String title = "Выход из аккаунта";
        String message = "Вы действительно хотите выйти из своего аккаунта?";
        String buttonYes = "Да";
        String buttonNo = "Нет";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //            При выходе, пременная запоминающая было ли вхождение аккаунт принимает значение FALSE
                sPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(SAVED_TEXT, false);
                ed.commit();

//            ------------- Обнуляются данные пользователя, что бы не отображались в LoginActivity --------------------
                sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                SharedPreferences.Editor edPrefLogin = sPrefLogin.edit();
                edPrefLogin.putString("save_login", "");
                edPrefLogin.commit();

                sPrefUserName = getSharedPreferences("SharedPrefUserName",MODE_PRIVATE);
                SharedPreferences.Editor edPrefUserName = sPrefUserName.edit();
                edPrefUserName.putString("save_user_name", "");
                edPrefUserName.commit();

                sPrefEmail = getSharedPreferences("SharedPrefEmail",MODE_PRIVATE);
                SharedPreferences.Editor edPrefEmail = sPrefEmail.edit();
                edPrefEmail.putString("save_email", "");
                edPrefEmail.commit();

                sPrefSelection = getSharedPreferences("SharedPrefSelection",MODE_PRIVATE);
                SharedPreferences.Editor edPrefSelection = sPrefSelection.edit();
                edPrefSelection.putInt("save_selection", 1);
                edPrefSelection.commit();
//            -----------------------------------------------------------------------


//                --------------------------- Закрываем все уведомления ----------------------
                AlarmReceiver mAlarmReceiver = new AlarmReceiver();

                ArrayList<Integer> itemIds = new ArrayList<>();

                String sql = "SELECT " + DBHelper.KEY_REMINDER_ID + " FROM " + DBHelper.TABLE_REMINDER
                        + " WHERE " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.moveToFirst()) {
                    int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                    do {
                        itemIds.add(cursor.getInt(idKeyIndex));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                mAlarmReceiver.cancelAlarm(UserActivity1.this, itemIds);
//                ----------------------------------------------------------------------------

//                Очистка базы данных
                dbHelper.dropTables(db);

                deleteFile("reminder_insert.txt");
                deleteFile("reminder_update.txt");
                deleteFile("reminder_delete.txt");


                Intent intent = new Intent(UserActivity1.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

//        -----------------------------------------------------------------------------------------




        //        --------------------------- Всплывающее окно Alert Dialog -----------------------
        context = UserActivity1.this;

        title = "Внимание !!!";
        message = "Есть несинхронизированные данные !!!\nЕсли Вы сейчас выйдете несинхронизированные данные будут утеряны !!!\n"
                + "Подключитесь к Интернету для синхронизайии !!!";
        buttonYes = "Выйти";
        buttonNo = "Вернуться";

        adNoSynchroniseData = new AlertDialog.Builder(context);
        adNoSynchroniseData.setTitle(title);
        adNoSynchroniseData.setMessage(message);
        adNoSynchroniseData.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //            При выходе, пременная запоминающая было ли вхождение аккаунт принимает значение FALSE
                sPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(SAVED_TEXT, false);
                ed.commit();

//            ------------- Обнуляются данные пользователя, что бы не отображались в LoginActivity --------------------
                sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                SharedPreferences.Editor edPrefLogin = sPrefLogin.edit();
                edPrefLogin.putString("save_login", "");
                edPrefLogin.commit();

                sPrefUserName = getSharedPreferences("SharedPrefUserName",MODE_PRIVATE);
                SharedPreferences.Editor edPrefUserName = sPrefUserName.edit();
                edPrefUserName.putString("save_user_name", "");
                edPrefUserName.commit();

                sPrefEmail = getSharedPreferences("SharedPrefEmail",MODE_PRIVATE);
                SharedPreferences.Editor edPrefEmail = sPrefEmail.edit();
                edPrefEmail.putString("save_email", "");
                edPrefEmail.commit();
//            -----------------------------------------------------------------------


//                --------------------------- Закрываем все уведомления ----------------------
                AlarmReceiver mAlarmReceiver = new AlarmReceiver();

                ArrayList<Integer> itemIds = new ArrayList<>();

                String sql = "SELECT " + DBHelper.KEY_REMINDER_ID + " FROM " + DBHelper.TABLE_REMINDER
                        + " WHERE " + DBHelper.KEY_REMINDER_REF_LOGIN + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.moveToFirst()) {
                    int idKeyIndex = cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID);
                    do {
                        itemIds.add(cursor.getInt(idKeyIndex));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                mAlarmReceiver.cancelAlarm(UserActivity1.this, itemIds);
//                ----------------------------------------------------------------------------

//                Очистка базы данных
                dbHelper.dropTables(db);

                deleteFile("reminder_insert.txt");
                deleteFile("reminder_update.txt");
                deleteFile("reminder_delete.txt");


                Intent intent = new Intent(UserActivity1.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        adNoSynchroniseData.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

//        -----------------------------------------------------------------------------------------

        /*tvUserName.setText(userNameRes);
        emailUser.setText(email);*/


        intent = getIntent();
        fragmentName = intent.getStringExtra("name_fragment");


//        При вхождении в приложение первый раз открывает FragmentMain
        SharedPreferences sp = getSharedPreferences("SignFirst", MODE_PRIVATE);
        boolean signFirst = sp.getBoolean("signFirst", true);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.content_user, fmain);
        fragmentTransaction.commit();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Log.d("myLogs", "fragmentName = " + fragmentName);

//        По умолчанию открывается Главная, в остальных случаях принимается Intent и открывается нужная страница
//        getSupportActionBar().setTitle("Главная");
        try {
            if (fragmentName.equals("fmain")){
//                fragmentTransaction.replace(R.id.content_user, fmain);
            }

            if (fragmentName.equals("fkrditcalk")){
//                fragmentTransaction.replace(R.id.content_user, fkrditcalk);
            }

            if (fragmentName.equals("freminder")){
//                fragmentTransaction.replace(R.id.content_user, freminder);
            }

            if (fragmentName.equals("frates")){
//                fragmentTransaction.replace(R.id.content_user, fexhangerate);
            }
            fragmentTransaction.commit();
        } catch (java.lang.NullPointerException e){
//            fragmentTransaction.replace(R.id.content_user, fmain);
            fragmentTransaction.commit();
        }

    }

//    ------------------------------- Custom NavigationDrawer ----------------------------------------

//    private void initializeNavigationDrawer(Toolbar toolbar) {
//
//        AccountHeader.Result accHeaderResult = createAccountHeader();
//
//        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//
//        result = new Drawer()
//                .withActivity(this)
//                .withToolbar(toolbar)  //  Кнопка разворочивания на toolbar
//                .withAccountHeader(accHeaderResult)  //  добавление header -а
//                .withDisplayBelowToolbar(true)  //  открывается под кнопкой разворачивания
//                .withActionBarDrawerToggleAnimated(true)  //  Анимация кнопки из трех палочек в стрелку
//                .withSliderBackgroundColor(Color.LTGRAY)  // Цвет фона
//                .addDrawerItems(
//                        initializeDrawerItems())
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
//                        switch (position){
//                            case 0:
//                                fragmentTransaction.replace(R.id.content_user, fmain);
////                                fragmentTransaction.commit();
//                                break;
//                            case 1:
//                                fragmentTransaction.replace(R.id.content_user, fkrditcalk);
////                                fragmentTransaction.commit();
//                                break;
//                            case 2:
//                                fragmentTransaction.replace(R.id.content_user, fstatistic);
////                                fragmentTransaction.commit();
//                                break;
//                            case 3:
//                                fragmentTransaction.replace(R.id.content_user, freminder);
////                                fragmentTransaction.commit();
//                                break;
//                            case 4:
//                                fragmentTransaction.replace(R.id.content_user, fexhangerate);
////                                fragmentTransaction.commit();
//                                break;
//                        }
//                        fragmentTransaction.commit();
//                    }
//                })
//                .build();
//    }
//
//    @NonNull
//    private IDrawerItem[] initializeDrawerItems() {
//        return new IDrawerItem[]{new PrimaryDrawerItem()  //  AДобавляем вкладку
//                .withName(R.string.nav_menu_item_main)  //  Имя вклаждки
//                .withIdentifier(1)
//                .withIcon(R.drawable.ic_main_24dp),      //  Позиция, если несколько, то так их можно группировать
//                //new DividerDrawerItem(),  //  Подчеркивание
//
//                new PrimaryDrawerItem()
//                    .withName(R.string.nav_menu_item_credit_calc)
//                    .withIdentifier(1)
//                    .withIcon(R.drawable.ic_kred_kalc_24dp),
//
//                new PrimaryDrawerItem()
//                    .withName(R.string.nav_menu_item_statistic)
//                    .withIdentifier(1)
//                    .withIcon(R.drawable.ic_statistic_black_24dp),
//
//                new PrimaryDrawerItem()
//                        .withName(R.string.nav_menu_item_reminder)
//                        .withIdentifier(1)
//                        .withIcon(R.drawable.ic_reminder_black_24dp),
//
//                new PrimaryDrawerItem()
//                        .withName(R.string.nav_menu_item_exchange_rates)
//                        .withIdentifier(1)
//                        .withIcon(R.drawable.ic_exchange_rate_24dp)
//        };
//    }
//
//    private AccountHeader.Result createAccountHeader() {
//
//        IProfile profile = new ProfileDrawerItem()
//                .withName("Dima")
//                .withEmail("dima@gmail.com").withTextColor(Color.WHITE)
//                .withIcon(getResources().getDrawable(R.drawable.ic_account_circle_24dp));
//
//        return new AccountHeader()
//                .withActivity(this)
//                .withHeaderBackground(R.drawable.bcg1)
//                .addProfiles(profile)
//                .build();
//    }


    @Override
    public void onInt(Bundle savedInstanceState) {
        // User Information
        this.userName.setText("Rudson Lima");
        this.userEmail.setText("rudsonlive@gmail.com");
        this.userPhoto.setImageResource(R.drawable.ic_rudsonlive);
        this.userBackground.setImageResource(R.drawable.ic_user_background_first);

        // Creating items navigation
        mHelpLiveo = new HelpLiveo();
        mHelpLiveo.add(getString(R.string.nav_menu_item_main), R.drawable.ic_main_24dp);
//        mHelpLiveo.addSubHeader(getString(R.string.categories)); //Item subHeader
        mHelpLiveo.add(getString(R.string.nav_menu_item_credit_calc), R.drawable.ic_kred_kalc_24dp);
        mHelpLiveo.add(getString(R.string.nav_menu_item_statistic), R.drawable.ic_statistic_black_24dp);
        mHelpLiveo.add(getString(R.string.nav_menu_item_reminder), R.drawable.ic_reminder_black_24dp);
        mHelpLiveo.add(getString(R.string.nav_menu_item_exchange_rates), R.drawable.ic_exchange_rate_24dp);
//        mHelpLiveo.addSeparator(); // Item separator

        with(this, Navigation.THEME_DARK);   //. add theme dark
        //with(this, Navigation.THEME_LIGHT). add theme light

        with(this) // default theme is dark
                .startingPosition(2) //Starting position in the list
                .addAllHelpItem(mHelpLiveo.getHelp())
                .colorItemSelected(R.color.nliveo_blue_colorPrimary)
                .footerItem(R.string.action_settings, R.drawable.ic_main_24dp)
                .setOnClickUser(onClickPhoto)
                .setOnPrepareOptionsMenu(onPrepare)
                .setOnClickFooter(onClickFooter)
                .build();

        int position = this.getCurrentPosition();
        this.setElevationToolBar(position != 2 ? 15 : 0);
    }

    @Override //The "R.id.container" should be used in "beginTransaction (). Replace"
    public void onItemClick(int position) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment mFragment;

        switch (position){
            default:
                mFragment = FragmentMain.newInstance(mHelpLiveo.get(position).getName());
                break;
//            case 1:
//                fragmentTransaction.replace(R.id.content_user, fkrditcalk);
//                                fragmentTransaction.commit();
//                break;
//            case 2:
//                fragmentTransaction.replace(R.id.content_user, fstatistic);
//                                fragmentTransaction.commit();
//                break;
//            case 3:
//                fragmentTransaction.replace(R.id.content_user, freminder);
//                                fragmentTransaction.commit();
//                break;
//            case 4:
//                fragmentTransaction.replace(R.id.content_user, fexhangerate);
//                                fragmentTransaction.commit();
//                break;
        }

        if (mFragment != null){
//            fragmentManager.beginTransaction().replace(R.id.content_user, mFragment).commit();
        }

        setElevationToolBar(position != 2 ? 15 : 0);
    }

    private OnPrepareOptionsMenuLiveo onPrepare = new OnPrepareOptionsMenuLiveo() {
        @Override
        public void onPrepareOptionsMenu(Menu menu, int position, boolean visible) {
        }
    };

    private View.OnClickListener onClickPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), "onClickPhoto :D", Toast.LENGTH_SHORT).show();
            closeDrawer();
        }
    };

    private View.OnClickListener onClickFooter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeDrawer();
        }
    };



//    ------------------------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

//            Ставится в настройки для диалога валют выбор индекса с EUR
            sPrefSelection = getSharedPreferences("SharedPrefSelection",MODE_PRIVATE);
            SharedPreferences.Editor edPrefSelection = sPrefSelection.edit();
            edPrefSelection.putInt("save_selection", 1);
            edPrefSelection.commit();

//              Выход из всего приложения
//          finishAffinity();
            ActivityCompat.finishAffinity (this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_site){
            Toast.makeText(this, "Site", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_support){
            Toast.makeText(this, "Support", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_exit) {


            ad.show();

//            if (isSynchroniseData()){
//                ad.show();
//            } else {
//                adNoSynchroniseData.show();
//            }
        }

        return super.onOptionsItemSelected(item);
    }

//    Метод проверяющий синхронизацию данных с локальной бд
    private boolean isSynchroniseData() {
        boolean isSynchronise = true;

        String sql = "select * from " + DBHelper.TABLE_USER
                + " where " + DBHelper.KEY_SYNCHRONISE + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{"no"});


        if (cursor.getCount() > 0){
            isSynchronise = false;
        }

        sql = "select * from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + " =? AND " + DBHelper.KEY_GET_SYNCHRONISE + " =? or "
                + DBHelper.KEY_GET_SYNCHRONISE + " = ? or "
                + DBHelper.KEY_GET_SYNCHRONISE + " = ?";
        cursor = db.rawQuery(sql, new String[]{login, "no", "ins", "deleted"});


        if (cursor.getCount() > 0){
            isSynchronise = false;
        }

        sql = "select * from " + DBHelper.TABLE_SPEND
                + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " =? AND " + DBHelper.KEY_SPEND_SYNCHRONISE + " =? or "
                + DBHelper.KEY_SPEND_SYNCHRONISE + " = ? or "
                + DBHelper.KEY_SPEND_SYNCHRONISE + " = ?";
        cursor = db.rawQuery(sql, new String[]{login, "no", "ins", "deleted"});


        if (cursor.getCount() > 0){
            isSynchronise = false;
        }

        sql = "select * from " + DBHelper.TABLE_GET_FOR_TOTAL
                + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " =? AND " + DBHelper.KEY_TOTAL_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "no"});


        if (cursor.getCount() > 0){
            isSynchronise = false;
        }

        sql = "select * from " + DBHelper.TABLE_STATISTIC
                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + " =? AND " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " =? or "
                + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = ? or "
                + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = ?";
        cursor = db.rawQuery(sql, new String[]{login, "no", "ins", "deleted"});


        if (cursor.getCount() > 0){
            isSynchronise = false;
        }

        cursor.close();


        if (checkFile("reminder_insert.txt")){
            isSynchronise = false;
        }

        if (checkFile("reminder_update.txt")){
            isSynchronise = false;
        }

        if (checkFile("reminder_delete.txt")){
            isSynchronise = false;
        }

        return isSynchronise;
    }

// ------------------------------------------------------------------------------------------------------------------------------
    private boolean checkFile(String filename) {
        boolean isExists;
        File file = new File(filename);
        if (!file.exists()) {
            isExists = true;
        } else {
            isExists = false;
        }

        return isExists;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
/*
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_main) {
            //Toast.makeText(this, "Главная", Toast.LENGTH_SHORT).show();
            fragmentTransaction.replace(R.id.content_user, fmain);
        }
        else if (id == R.id.nav_kredit_kalc) {
            fragmentTransaction.replace(R.id.content_user, fkrditcalk);
        } else if (id == R.id.nav_statistic) {
            fragmentTransaction.replace(R.id.content_user, fstatistic);
        } else if (id == R.id.nav_reminder) {
            fragmentTransaction.replace(R.id.content_user, freminder, "FragmentReminder");
        } else if (id == R.id.nav_exchange_rate) {
            fragmentTransaction.replace(R.id.content_user, fexhangerate);
        }
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            uploadToServer();

        }
    }

    public void uploadToServer() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        ServerApi serverApi = retrofit.create(ServerApi.class);


//            ------------------------------ User ----------------------------------

        sPrefLogin = getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");

        String sql = "select * from " + DBHelper.TABLE_USER
                + " where " + DBHelper.KEY_LOGIN + " =? AND " + DBHelper.KEY_SYNCHRONISE + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{login, "no"});

        userId = 0; String nameUser = "", dateOfBirthUser = "", mobileUser = "", emailUser = "", loginUser = "",
                passwordUser = "";
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_ID));
                nameUser = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME));
                dateOfBirthUser = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DATE_OF_BIRTH));
                mobileUser = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MOBILE));
                emailUser = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_EMAIL));
                loginUser = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LOGIN));
                passwordUser = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PASSWORD));

                Call<Void> userUpd = serverApi.updateUserTable(nameUser,
                        dateOfBirthUser, mobileUser, emailUser, loginUser, passwordUser);

                userUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_USER + " set " + DBHelper.KEY_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_LOGIN + " = '" + login + "' and " + DBHelper.KEY_ID + " = " + userId;
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();

//            ---------------------------------- TABLE GET ------------------------------------------

        sql = "select * from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + " =? AND " + DBHelper.KEY_GET_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "no"});

        String getImage = "", getKol = "", getRefLogin = "", getSynchronise = "";
        getName = "";

        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_ID));
                getName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_NAME));
                getImage = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_IMAGE));
                getKol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                getRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REF_LOGIN));

                Call<Void> getUpd = serverApi.updateGetTable("update", getName, getImage, getKol,
                        getRefLogin, localId);

                getUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and " + DBHelper.KEY_GET_ID + " = '" + localId + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();






        sql = "select * from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + " =? AND " + DBHelper.KEY_GET_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "deleted"});

        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_ID));
                getName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_NAME));
                getImage = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_IMAGE));
                getKol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                getRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REF_LOGIN));

                Call<Void> getUpd = serverApi.updateGetTable("delete", getName, getImage, getKol,
                        getRefLogin, localId);

                getUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "delete from " + DBHelper.TABLE_GET + " where " + DBHelper.KEY_REF_LOGIN + " = '"
                                + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + getName + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();




        sql = "select * from " + DBHelper.TABLE_GET
                + " where " + DBHelper.KEY_REF_LOGIN + " =? AND " + DBHelper.KEY_GET_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "ins"});

        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_ID));
                getName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_NAME));
                getImage = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_IMAGE));
                getKol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_GET));
                getRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REF_LOGIN));

                getSynchronise = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GET_SYNCHRONISE));


//                    Call<Void> getUpd = serverApi.updateGetTable("insert", getName, getImage, getKol,
//                            getRefLogin, String.valueOf(localId));

                Call<Void> getUpd = serverApi.addNewButton("table_get", getName,
                        getImage, getKol, getRefLogin, localId);

                getUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + getName + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();





//            ------------------------------------ TABLE SPEND -----------------------------------------

        sql = "select * from " + DBHelper.TABLE_SPEND
                + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " =? AND " + DBHelper.KEY_SPEND_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "no"});

        String spendImage = "", spendKol = "", spendRefLogin = "", spendSynchronise = "";
        spendName = "";

        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_ID));
                spendName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_NAME));
                spendImage = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_IMAGE));
                spendKol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                spendRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REF_LOGIN_SPEND));

                Call<Void> spendUpd = serverApi.updateSpendTable("update", spendName, spendImage, spendKol,
                        spendRefLogin, localId);

                spendUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and " + DBHelper.KEY_SPEND_ID + " = '" + localId + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();


        sql = "select * from " + DBHelper.TABLE_SPEND
                + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " =? AND " + DBHelper.KEY_SPEND_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "deleted"});

        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_ID));
                spendName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_NAME));
                spendImage = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_IMAGE));
                spendKol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                spendRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REF_LOGIN_SPEND));

                Call<Void> spendUpd = serverApi.updateSpendTable("delete", spendName, spendImage, spendKol,
                        spendRefLogin, localId);

                spendUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "delete from " + DBHelper.TABLE_SPEND + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = '"
                                + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + spendName + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();




        sql = "select * from " + DBHelper.TABLE_SPEND
                + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " =? AND " + DBHelper.KEY_SPEND_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "ins"});

        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_ID));
                spendName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_NAME));
                spendImage = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_IMAGE));
                spendKol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                spendRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REF_LOGIN_SPEND));

                spendSynchronise = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SPEND_SYNCHRONISE));

                if (spendSynchronise.equals("ins")){

                    Call<Void> spendUpd = serverApi.addNewButton("table_spend", spendName,
                            spendImage, spendKol, spendRefLogin, localId);

                    spendUpd.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                    + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + spendName + "'";
                            db.execSQL(sql);

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });

                }


            } while (cursor.moveToNext());
        }
        cursor.close();







//            ---------------------------------------- TABLE TOTAL ----------------------------------------------

        sql = "select * from " + DBHelper.TABLE_GET_FOR_TOTAL
                + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " =? AND " + DBHelper.KEY_TOTAL_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "no"});

        String kolTotal = "", loginTotal = "";

        if (cursor.moveToFirst()) {
            do {
                kolTotal = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_KOL_TOTAL_GET));
                loginTotal = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REF_LOGIN_TOTAL_GET));

                Call<Void> totalUpd = serverApi.updateTotalTable(kolTotal, loginTotal);

                totalUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();

//            -------------------------------------- TABLE SPEND -------------------------------------------

        sql = "select * from " + DBHelper.TABLE_STATISTIC
                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + " =? AND " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "no"});

        String type = "", nameGet = "", kol = "", nameSpend = "", date = "", time = "", refLogin = "";

        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID));
                type = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                nameGet = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET));
                kol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL));
                nameSpend = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND));
                date = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                time = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME));
                refLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_REF_LOGIN));

                Call<Void> statisticUpd = serverApi.updateStatisticTable("update", type, nameGet, kol,
                        nameSpend, date, time, refLogin, localId);

                statisticUpd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_STATISTIC_ID + " = '" + localId + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();



        sql = "select * from " + DBHelper.TABLE_STATISTIC
                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + " =? AND " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " =?";
        cursor = db.rawQuery(sql, new String[]{login, "ins"});

        if (cursor.moveToFirst()) {
            do {

                localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID));
                type = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                nameGet = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET));
                kol = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL));
                nameSpend = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND));
                date = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                time = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME));
                refLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_REF_LOGIN));

                Log.d("myLogsSw", "localId = " + localId + " type = " + type + " nameGet = " + nameGet
                        + " kol = " + kol + " nameSpend = " + nameSpend + " date = " + date + " time = " + time
                        + " refLogin = " + refLogin + "\n");

                Call<Void> addStatistic = serverApi.addStatistic(type, nameGet, kol, nameSpend,
                        date, time, refLogin, localId);

                addStatistic.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_STATISTIC + " set " + DBHelper.KEY_STATISTIC_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_STATISTIC_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_STATISTIC_ID + " = '" + localId + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

            } while (cursor.moveToNext());
        }
        cursor.close();


//            ---------------------------------- TABLE REMINDER -----------------------------------

//                    --------------------------- Удаление ID из файла --------------------------------

//        ----------- Считываем из файлов номера ID на добавление, обновление и удаление ---------------------

        try{
//            Сткрываем поток для чтения
            BufferedReader br  = new BufferedReader(new InputStreamReader(
                    openFileInput("reminder_insert.txt")));
            String str = "";
//            Читаем содержимое
            while ((str = br.readLine()) != null){
                fileReminderInsert = str;
            }


            String[] str1 = fileReminderInsert.split(" ");
            Set<String> set = new HashSet<String>(Arrays.asList(str1));
            arrayReminderInsert = set.toArray(new String[set.size()]);

            fileReminderInsert = (arrayReminderInsert[0] + " ").trim();
            for (int i = 1; i < arrayReminderInsert.length; i++){
                fileReminderInsert = fileReminderInsert + arrayReminderInsert[i] + " ";
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


        try{
//            Сткрываем поток для чтения
            BufferedReader br  = new BufferedReader(new InputStreamReader(
                    openFileInput("reminder_update.txt")));
            String str = "";
//            Читаем содержимое
            while ((str = br.readLine()) != null){
                fileReminderUpdate = str;
            }


            String[] str1 = fileReminderUpdate.split(" ");
            Set<String> set = new HashSet<String>(Arrays.asList(str1));
            arrayReminderUpdate = set.toArray(new String[set.size()]);

            fileReminderUpdate = (arrayReminderUpdate[0] + " ").trim();
            for (int i = 1; i < arrayReminderUpdate.length; i++){
                fileReminderUpdate = fileReminderUpdate + arrayReminderUpdate[i] + " ";
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }



        try{
//            Сткрываем поток для чтения
            BufferedReader br  = new BufferedReader(new InputStreamReader(
                    openFileInput("reminder_delete.txt")));
            String str = "";
//            Читаем содержимое
            while ((str = br.readLine()) != null){
                fileReminderDelete = str;
            }


            String[] str1 = fileReminderDelete.split(" ");
            Set<String> set = new HashSet<String>(Arrays.asList(str1));
            arrayReminderDelete = set.toArray(new String[set.size()]);

            fileReminderDelete = (arrayReminderDelete[0] + " ").trim();
            for (int i = 1; i < arrayReminderDelete.length; i++){
                fileReminderDelete = fileReminderDelete + arrayReminderDelete[i] + " ";
            }


        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

//        ---------------------------------------------------------------------




        try{


            arrayReminderUpdate = minus(arrayReminderUpdate, arrayReminderDelete);


        } catch (java.lang.NullPointerException e){
            e.printStackTrace();
        } catch (java.lang.ArrayIndexOutOfBoundsException e){
            arrayReminderUpdate = new String[0];
        }





        try{

            arrayReminderInsert = minus(arrayReminderInsert, arrayReminderDelete);

//            fileReminderInsert = arrayReminderInsert[0];
//            for (int i = 1; i < arrayReminderInsert.length; i++){
//                fileReminderInsert = fileReminderInsert + " " + arrayReminderInsert[i];
//            }

        } catch (java.lang.NullPointerException e){
            e.printStackTrace();
        } catch (java.lang.ArrayIndexOutOfBoundsException e){
            arrayReminderInsert = new String[0];
        }



        Log.d("myLogsTesting", "fileReminderInsert = " + fileReminderInsert + " fileReminderUpdate = " + fileReminderUpdate
                + " fileReminderDelete = " + fileReminderDelete);




//                    -----------------------------------------------------------------------------




//            ------------------------------- REMINDER INSERT -------------------------------------

        try{

            for (int i = 0; i < arrayReminderInsert.length; i++){
                //Log.d("myLogsStringArr", "s[" + i + "] = " + s[i]);

                k = i;

                if (!arrayReminderInsert[i].equals("")){

                    sql = "select * from " + DBHelper.TABLE_REMINDER
                            + " where " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND " + DBHelper.KEY_REMINDER_ID + " =?";
                    cursor = db.rawQuery(sql, new String[]{login, arrayReminderInsert[i]});

                    if (cursor.moveToFirst()) {
                        do {
                            localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID));
                            reminderType = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE));
                            reminderName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME));
                            reminderDate = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE));
                            reminderTime = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME));
                            reminderImgMarker = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER));
                            reminderImgMarkerName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME));
                            reminderRepeat = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT));
                            reminderSound = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SOUND));
                            reminderRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REF_LOGIN));
                            reminderSynchronise = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SYNCHRONISE));


                            Call<Void> addReminder = serverApi.addReminder(reminderType, reminderName,
                                    reminderDate, reminderTime, reminderRepeat, reminderImgMarker, reminderImgMarkerName,
                                    reminderSound, reminderRefLogin, localId);

                            addReminder.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                    String sql = "update " + DBHelper.TABLE_REMINDER + " set " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = 'yes' where "
                                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "' and "
                                            + DBHelper.KEY_REMINDER_ID + " = '" + localId + "'";
                                    db.execSQL(sql);

//                                        if (k == arrayReminderInsert.length-1){
                                    deleteFile("reminder_insert.txt");
//                                        }

                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });

                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                }

            }

        } catch (java.lang.NullPointerException e){

        }


//            ---------------------------------- REMINDER UPDATE ----------------------------------

        try{

            for (int i = 0; i < arrayReminderUpdate.length; i++){
                //Log.d("myLogsStringArr", "s[" + i + "] = " + s[i]);

                k = i;

                if (!arrayReminderUpdate[i].equals("")){

                    sql = "select * from " + DBHelper.TABLE_REMINDER
                            + " where " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND " + DBHelper.KEY_REMINDER_ID + " =?";
                    cursor = db.rawQuery(sql, new String[]{login, arrayReminderUpdate[i]});

                    if (cursor.moveToFirst()) {
                        do {
                            localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID));
                            reminderType = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE));
                            reminderName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME));
                            reminderDate = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE));
                            reminderTime = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME));
                            reminderImgMarker = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER));
                            reminderImgMarkerName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME));
                            reminderRepeat = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT));
                            reminderSound = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SOUND));
                            reminderRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REF_LOGIN));
                            reminderSynchronise = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SYNCHRONISE));


                            Call<Void> reminderUpd = serverApi.editReminder(reminderType, reminderName, reminderDate, reminderTime,
                                    reminderRepeat, reminderImgMarker, reminderImgMarkerName, reminderSound, reminderRefLogin,
                                    localId);

                            reminderUpd.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                    String sql = "UPDATE " + DBHelper.TABLE_REMINDER + " SET " + DBHelper.KEY_REMINDER_SYNCHRONISE + " = '"
                                            + "yes" + "' WHERE " + DBHelper.KEY_REMINDER_ID + " = '" + localId + "' AND "
                                            + DBHelper.KEY_REMINDER_REF_LOGIN + " = '" + login + "'";
                                    db.execSQL(sql);

                                    if (k == arrayReminderUpdate.length-1){
                                        deleteFile("reminder_update.txt");
                                    }

                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });

                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                }

            }

        } catch (java.lang.NullPointerException e){

        }

//            ----------------------------------------------------------------------------------------------


//            -------------------------------------------------------------------------------------------------------------------------


//            ---------------------------------- REMINDER DELETE ----------------------------------

        try{

            for (int i = 0; i < arrayReminderDelete.length; i++){
                //Log.d("myLogsStringArr", "s[" + i + "] = " + s[i]);

                k = i;

                if (!arrayReminderDelete[i].equals("")){

                    sql = "select * from " + DBHelper.TABLE_REMINDER
                            + " where " + DBHelper.KEY_REMINDER_REF_LOGIN + " =? AND " + DBHelper.KEY_REMINDER_ID + " =?";
                    cursor = db.rawQuery(sql, new String[]{login, arrayReminderDelete[i]});

                    if (cursor.moveToFirst()) {
                        do {
                            localId = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_ID));
                            reminderType = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TYPE));
                            reminderName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_NAME));
                            reminderDate = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_DATE));
                            reminderTime = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_TIME));
                            reminderImgMarker = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_IMG_MARKER));
                            reminderImgMarkerName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_MARKER_NAME));
                            reminderRepeat = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REPEAT));
                            reminderSound = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SOUND));
                            reminderRefLogin = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_REF_LOGIN));
                            reminderSynchronise = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_REMINDER_SYNCHRONISE));


                            Call<Void> delReminder = serverApi.deleteFromReminders("one", reminderType,
                                    login, localId);

                            delReminder.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                    Log.d("myLogsTest", "num = " + num + " localId = " + localId + " reminderType = " + reminderType
                                            + " reminderName = " + reminderName + " reminderDate = " + reminderDate
                                            + " reminderTime = " + reminderTime
                                            + " reminderSynchronise = " + reminderSynchronise);

                                    String sql = "delete from " + DBHelper.TABLE_REMINDER + " where " + DBHelper.KEY_REMINDER_REF_LOGIN + " = '"
                                            + login + "' and " + DBHelper.KEY_REMINDER_ID + " = '"
                                            + localId + "'";
                                    db.execSQL(sql);

                                    if (k == arrayReminderDelete.length-1){
                                        deleteFile("reminder_delete.txt");
                                    }

                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });

                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                }

            }

        } catch (java.lang.NullPointerException e){

        }



//            -------------------------------------------------------------------------------------------------------------------------

    }

    public static String[] minus(String[] arr1, String[] arr2){

        List<String> list1 = new ArrayList<String>(Arrays.asList(arr1));
        List<String> list2 = new ArrayList<String>(Arrays.asList(arr2));
        list1.removeAll(list2);

        HashSet<String> set = new HashSet<>(list1);
        String[] array = set.toArray(new String[set.size()]);

//        String[] array = list1.toArray(new String[list1.size()]);
//
//        Set<String> set = new HashSet<String>(Arrays.asList(array));
//        array = set.toArray(new String[set.size()]);

        return array;
    }


}
