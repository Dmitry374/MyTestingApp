package com.example.dima.mytestingapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Activitys.ButtonGetActivity;
import com.example.dima.mytestingapp.Activitys.ButtonSpendActivity;
import com.example.dima.mytestingapp.Activitys.EditingActivity;
import com.example.dima.mytestingapp.Activitys.EditingLastRecord;
import com.example.dima.mytestingapp.Activitys.EditingLastRecordSpend;
import com.example.dima.mytestingapp.Activitys.LoginActivity;
import com.example.dima.mytestingapp.Activitys.MoneyTransactionActivity;
import com.example.dima.mytestingapp.Activitys.SetButtonActivity;
import com.example.dima.mytestingapp.Activitys.UserActivity;
import com.example.dima.mytestingapp.Adapters.GridAdapter;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.Items.Item;
import com.example.dima.mytestingapp.Items.ItemServerData;
import com.example.dima.mytestingapp.Items.ItemServerGet;
import com.example.dima.mytestingapp.Items.ItemServerSpend;
import com.example.dima.mytestingapp.Items.ItemServerStatistic;
import com.example.dima.mytestingapp.Items.ItemServerTotal;
import com.example.dima.mytestingapp.MyGridView;
import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.api.ServerApi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMain.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMain extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    RelativeLayout getRelLayout;
    Button btnAdd;

    View viewItem;

    int itemWidth;  //  Длина нового элемента

    ImageView imgAdd;
    ImageView imgAddSpend;

    View viewLayoutGet;
//    GridView gridViewGet;
//    GridView gridViewSpend;

    TextView tvGetSpend;
    double countSpendTable;

    MyGridView gridViewGet;
    MyGridView gridViewSpend;

    ArrayList<Item> listGet = new ArrayList<>();
    ArrayList<Item> listSpend = new ArrayList<>();

    GridAdapter gridAdapterGet;
    GridAdapter gridAdapterSpend;


    final int REQUEST_CODE_NEW_BTN = 1;

    String btnName;

    int imageId;

    String btnNameSpend;
    int imageIdSpend;

    DBHelper dbHelper;
    SQLiteDatabase db;

//    SharedPreferences sPrefLogin;
    SharedPreferences sPrefLogin;

    AlertDialog.Builder ad;   //  Диалог

    String nameOfButton;

    String login;

    ContentValues contentValues;

//        Данные по умолчанию для таблицы Get
    String[] btnNames;
    int[] imgIds;
    String[] getPay;

//        Данные по умолчанию для таблицы Spend
    String[] btnNamesSpend;
    int[] imgIdsSpend;
    String[] spendPay;


    Retrofit.Builder builder;
    Retrofit retrofit;
    ServerApi serverApi;

    long id;

    SwipeRefreshLayout mSwipeRefreshLayout;




    Date date;
    DateFormat format;
    String dateNow;
    SharedPreferences sPrefDate;
    String dateNowString;
    Date dateFromPref;




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMain_11.
     */
    // TODO: Rename and change types and number of parameters
    public FragmentMain newInstance(String param1, String param2) {
        FragmentMain fragment = new FragmentMain();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Имя Toolbar
        Activity activity = this.getActivity();
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setTitle("Главная");
        }

        // Inflate the layout for this fragment
        viewLayoutGet = inflater.inflate(R.layout.fragment_main, null);

        tvGetSpend = (TextView) viewLayoutGet.findViewById(R.id.tvGetSpend);

        tvGetSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Сумма считается за месяц", Toast.LENGTH_SHORT).show();
            }
        });


//        -------------- Работа с БД ---------------------

//        Данные по умолчанию для таблицы Get
        btnNames = new String[]{"Бизнес", "Карьера"};
        imgIds = new int[]{R.drawable.get1, R.drawable.get6};
        getPay = new String[]{"0", "0"};

//        Данные по умолчанию для таблицы Spend
        btnNamesSpend = new String[]{"Перерыв"};
        imgIdsSpend = new int[]{R.drawable.spend7};
        spendPay = new String[]{"0"};

        dbHelper = new DBHelper(this.getActivity());

        db = dbHelper.getWritableDatabase();

        final ContentValues contentValues = new ContentValues();

//        Извлечение Login
        sPrefLogin = getActivity().getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");



        builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);



//        ---------- Извлечение переменной которя определяет первое вхождение пользователя с Сервера ---------

        String sql = "select " + DBHelper.KEY_SIGN_IN + " from " + DBHelper.TABLE_USER
                + " where " + DBHelper.KEY_LOGIN + "=?";
        final Cursor cursor = db.rawQuery(sql, new String[]{login});

        int isSignIn = 0;
        if (cursor.moveToFirst()) {
            do {
                isSignIn = cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_SIGN_IN));
                Log.d("myLogs", "isSignIn = " + isSignIn);
            } while (cursor.moveToNext());
        }
        cursor.close();

//        ---------------- Обнуление данных на начало месяца ------------------------------------------
//        Текущая дата
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        String dateNow = format.format(date);

        String dayNow = dateNow.substring(0,2);

        SharedPreferences sNewMonth = getActivity().getSharedPreferences("SharedPrefNewMonth", MODE_PRIVATE);
        boolean isNewMonth = sNewMonth.getBoolean("isNewMonth", true);
        Log.d("myLogs", "isNewMonth First = " + isNewMonth);


        if (dayNow.equals("01") && isNewMonth){

            sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_KOL_SPEND + " = '0.0' where "
                    + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "'";
            db.execSQL(sql);

            sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_KOL_TOTAL_GET + " = '0.0' where "
                    + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
            db.execSQL(sql);

            Call<Void> callZeroing = serverApi.editZeroing(login);

            callZeroing.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                            + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "'";
                    db.execSQL(sql);

                    sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'yes' where "
                            + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                    db.execSQL(sql);

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'no' where "
                            + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "'";
                    db.execSQL(sql);

                    sql = "update " + DBHelper.TABLE_GET_FOR_TOTAL + " set " + DBHelper.KEY_TOTAL_SYNCHRONISE + " = 'no' where "
                            + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = '" + login + "'";
                    db.execSQL(sql);

                }
            });

            SharedPreferences.Editor e = sNewMonth.edit();
            e.putBoolean("isNewMonth", false);
            e.commit(); // не забудьте подтвердить изменения
        }


        if (!dayNow.equals("01")){
            SharedPreferences.Editor e = sNewMonth.edit();
            e.putBoolean("isNewMonth", true);
            e.apply(); // не забудьте подтвердить изменения
        }
//        isNewMonth = sNewMonth.getBoolean("isNewMonth", false);
//        Log.d("myLogs", "isNewMonth Second = " + isNewMonth);

//        ------------------------------------------------------------------------------------------------
        // Заполнение данных из массива БД
        if (isSignIn == 0){

            for (int i = 0; i < btnNames.length; i++) {
                Log.d("myLogs", "Writing ...");

                final String buttonName = btnNames[i];

//                          Запись в локальную бд
                contentValues.put(DBHelper.KEY_GET_NAME, btnNames[i]);
                contentValues.put(DBHelper.KEY_GET_IMAGE, imgIds[i]);
                contentValues.put(DBHelper.KEY_KOL_GET, getPay[i]);
                contentValues.put(DBHelper.KEY_REF_LOGIN, login);
                contentValues.put(DBHelper.KEY_GET_SYNCHRONISE, "");

                id = db.insert(DBHelper.TABLE_GET, null, contentValues);

                contentValues.clear();


//                Запись на сервер

                Call<Void> writeTable = serverApi.writeGeneralTable("table_get", btnNames[i], String.valueOf(imgIds[i]),
                        getPay[i], login, String.valueOf(id));

                writeTable.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_GET_NAME + " = '" + buttonName + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'ins' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and "
                                + DBHelper.KEY_GET_NAME + " = '" + buttonName + "'";
                        db.execSQL(sql);

                    }
                });

            }

            contentValues.clear();

            for (int i = 0; i < btnNamesSpend.length; i++){

                final String buttonNameSpend = btnNamesSpend[i];

//                Запись в локальную бд
                contentValues.put(DBHelper.KEY_SPEND_NAME, btnNamesSpend[i]);
                contentValues.put(DBHelper.KEY_SPEND_IMAGE, imgIdsSpend[i]);
                contentValues.put(DBHelper.KEY_KOL_SPEND, spendPay[i]);
                contentValues.put(DBHelper.KEY_REF_LOGIN_SPEND, login);
                contentValues.put(DBHelper.KEY_SPEND_SYNCHRONISE, "");

                id = db.insert(DBHelper.TABLE_SPEND, null, contentValues);

                contentValues.clear();


//                Запись на сервер
                Call<Void> writeTable = serverApi.writeGeneralTable("table_spend", btnNamesSpend[i], String.valueOf(imgIdsSpend[i]),
                        spendPay[i], login, String.valueOf(id));

                writeTable.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'yes' where "
                                + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                + DBHelper.KEY_SPEND_NAME + " = '" + buttonNameSpend + "'";
                        db.execSQL(sql);

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'ins' where "
                                + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and "
                                + DBHelper.KEY_SPEND_NAME + " = '" + buttonNameSpend + "'";
                        db.execSQL(sql);
                    }
                });

            }

            contentValues.clear();


//            Запись в локальную бд
            contentValues.put(DBHelper.KEY_KOL_TOTAL_GET, "0.0");
            contentValues.put(DBHelper.KEY_REF_LOGIN_TOTAL_GET, login);
            contentValues.put(DBHelper.KEY_TOTAL_SYNCHRONISE, "");

            db.insert(DBHelper.TABLE_GET_FOR_TOTAL, null, contentValues);


//            Запись на сервер таблицу Total
            Call<Void> writeTotalGet = serverApi.writeTotalGetTable("0.0", login);

            writeTotalGet.enqueue(new Callback<Void>() {
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

            contentValues.clear();



//            Запись (обновление) в TableUser перменной которая сообщает о первом вхождении пользователя в аккаунт на сервере
            Call<Void> callUpdIsSign = serverApi.updateIsSign(login, "1");

            callUpdIsSign.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    Log.d("myLogsN", "Запись обновлена");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
                }
            });

//            Запись (обновление) в TableUser перменной которая сообщает о первом вхождении пользователя в аккаунт
            contentValues.put(DBHelper.KEY_SIGN_IN, 1);
            int updCount = db.update(DBHelper.TABLE_USER, contentValues, DBHelper.KEY_LOGIN + " = ?",
                    new String[] {login});
            Log.d("myLogs", "updated rows count = " + updCount);

        }



//        ------------------------------------------------


            Cursor cursorGridViewGet;
            String btnName;
            int imgBtn;
            String btnOnCount;

//        ------------------------- Извлечение данных из таблицы Get в БД --------------------------
            sql = "select " + DBHelper.KEY_GET_NAME + ", " + DBHelper.KEY_GET_IMAGE + ", " + DBHelper.KEY_KOL_GET
                    + " from " + DBHelper.TABLE_GET + " where " + DBHelper.KEY_REF_LOGIN + " = ? and "
                    + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
            cursorGridViewGet = db.rawQuery(sql, new String[]{login, "deleted"});

            if (cursorGridViewGet.moveToFirst()) {
                do {
                    btnName = cursorGridViewGet.getString(cursorGridViewGet.getColumnIndex(DBHelper.KEY_GET_NAME));
                    imgBtn = cursorGridViewGet.getInt(cursorGridViewGet.getColumnIndex(DBHelper.KEY_GET_IMAGE));
                    btnOnCount = cursorGridViewGet.getString(cursorGridViewGet.getColumnIndex(DBHelper.KEY_KOL_GET));

                    Log.d("myLogs", "btnName = " + btnName + " imgBtn = " + imgBtn + " btnOnCount = " + btnOnCount);

                    listGet.add(new Item(btnName, imgBtn, btnOnCount));
                } while (cursorGridViewGet.moveToNext());
            }
            cursorGridViewGet.close();
//        ------------------------------------------------------------------------------------------

//            listGet.add(new Item("", R.drawable.ic_control_point_black_24dp, ""));
            listGet.add(new Item("", R.drawable.button_add, ""));

            gridViewGet = (MyGridView) viewLayoutGet.findViewById(R.id.gridViewGet);

            gridAdapterGet = new GridAdapter(getActivity(), R.id.tvBtnName, R.id.imgBtn, R.id.tvBtnOnCount, listGet);

            gridViewGet.setAdapter(gridAdapterGet);

//        Регистрация контекстного меню для gridViewGet
            registerForContextMenu(gridViewGet);

            gridViewGet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == gridAdapterGet.getCount() - 1) {
                        Toast.makeText(getActivity(), "Go Next", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), SetButtonActivity.class);
                        intent.putExtra("set_button_type", "set_btn_get");
                        startActivityForResult(intent, REQUEST_CODE_NEW_BTN);

                    } else {
                        Toast.makeText(getActivity(), listGet.get(position).getBtnName(), Toast.LENGTH_SHORT).show();


                        Intent intent = new Intent(getActivity(), ButtonGetActivity.class);
                        intent.putExtra("btnNameGet", listGet.get(position).getBtnName());
//                        startActivityForResult(intent, REQUEST_CODE_BTN_NAME_GET);
                        startActivityForResult(intent, REQUEST_CODE_NEW_BTN);

                    }
                }
            });


            Cursor cursorGridViewSpend;
            String btnNameSpend;
            int imgBtnSpend;
            String btnOnCountSpend;

//        ------------------------- Извлечение данных из таблицы Spend в БД --------------------------
            sql = "select " + DBHelper.KEY_SPEND_NAME + ", " + DBHelper.KEY_SPEND_IMAGE + ", " + DBHelper.KEY_KOL_SPEND
                    + " from " + DBHelper.TABLE_SPEND + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = ? and "
                    + DBHelper.KEY_SPEND_SYNCHRONISE + " != ?";
            cursorGridViewSpend = db.rawQuery(sql, new String[]{login, "deleted"});

            if (cursorGridViewSpend.moveToFirst()) {
                do {
                    btnNameSpend = cursorGridViewSpend.getString(cursorGridViewSpend.getColumnIndex(DBHelper.KEY_SPEND_NAME));
                    imgBtnSpend = cursorGridViewSpend.getInt(cursorGridViewSpend.getColumnIndex(DBHelper.KEY_SPEND_IMAGE));
                    btnOnCountSpend = cursorGridViewSpend.getString(cursorGridViewSpend.getColumnIndex(DBHelper.KEY_KOL_SPEND));

                    Log.d("myLogs", "btnName = " + btnNameSpend + " imgBtn = " + imgBtnSpend + " btnOnCount = " + btnOnCountSpend);

                    listSpend.add(new Item(btnNameSpend, imgBtnSpend, btnOnCountSpend));
                } while (cursorGridViewSpend.moveToNext());
            }
            cursorGridViewSpend.close();
//        ------------------------------------------------------------------------------------------

//            listSpend.add(new Item("", R.drawable.ic_control_point_black_24dp, ""));
            listSpend.add(new Item("", R.drawable.button_add, ""));

            gridViewSpend = (MyGridView) viewLayoutGet.findViewById(R.id.gridViewSpend);

            gridAdapterSpend = new GridAdapter(getActivity(), R.id.tvBtnName, R.id.imgBtn, R.id.tvBtnOnCount, listSpend);

            gridViewSpend.setAdapter(gridAdapterSpend);

//        Double.parseDouble();

//        Регистрация контекстного меню для gridViewSpend
        registerForContextMenu(gridViewSpend);

            gridViewSpend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == gridAdapterSpend.getCount() - 1) {
                        Toast.makeText(getActivity(), "Go Next", Toast.LENGTH_SHORT).show();

//                        Intent intent = new Intent(getActivity(), SetButtonActivitySpend.class);
                        Intent intent = new Intent(getActivity(), SetButtonActivity.class);
                        intent.putExtra("set_button_type", "set_btn_spend");
                        startActivityForResult(intent, REQUEST_CODE_NEW_BTN);
                    } else {
//                        Toast.makeText(getActivity(), listSpend.get(position).getBtnName(), Toast.LENGTH_SHORT).show();

//                        btnNameForContextMenu = listSpend.get(position).getBtnName();

                        Intent intent = new Intent(getActivity(), ButtonSpendActivity.class);
                        intent.putExtra("btnNameSpend", listSpend.get(position).getBtnName());
                        startActivityForResult(intent, REQUEST_CODE_NEW_BTN);

                    }
                }
            });



//                    --------------------------- TextView TotalGet ------------------------------------------------
        Cursor cursorGetTotalCount;
        String getCount;
//                    double countGetTable = 0;
        double countGetTotalTable = 0;
//        ------------------------- Извлечение данных БД и отображение итоговой суммы --------------------------
        sql = "select " + DBHelper.KEY_KOL_TOTAL_GET + " from " + DBHelper.TABLE_GET_FOR_TOTAL
                + " where " + DBHelper.KEY_REF_LOGIN_TOTAL_GET + " = ?";
        cursorGetTotalCount = db.rawQuery(sql, new String[]{login});

        if (cursorGetTotalCount.moveToFirst()) {
            do {
                getCount = cursorGetTotalCount.getString(cursorGetTotalCount.getColumnIndex(DBHelper.KEY_KOL_TOTAL_GET));
                countGetTotalTable = countGetTotalTable + Double.parseDouble(getCount);
            } while (cursorGetTotalCount.moveToNext());
        }
        cursorGetTotalCount.close();
//        ------------------------------------------------------------------------------------------

//                    --------------------------- TextView Spend ------------------------------------------------
        Cursor cursorSpendCount;
        String spendCount;
        countSpendTable = 0;
//        ------------------------- Извлечение данных БД и отображение итоговой суммы --------------------------
        sql = "select " + DBHelper.KEY_KOL_SPEND + " from " + DBHelper.TABLE_SPEND
                + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = ? and " + DBHelper.KEY_SPEND_SYNCHRONISE + " != ?";
        cursorSpendCount = db.rawQuery(sql, new String[]{login, "deleted"});

        if (cursorSpendCount.moveToFirst()) {
            do {
                spendCount = cursorSpendCount.getString(cursorSpendCount.getColumnIndex(DBHelper.KEY_KOL_SPEND));
                countSpendTable = countSpendTable + Double.parseDouble(spendCount);
            } while (cursorSpendCount.moveToNext());
        }
        cursorSpendCount.close();
//        ------------------------------------------------------------------------------------------

        countGetTotalTable = Math.rint(100.0 * (countGetTotalTable)) / 100.0;
        countSpendTable = Math.rint(100.0 * (countSpendTable)) / 100.0;

        tvGetSpend.setText("Получил " + countGetTotalTable + " руб. Потратил " + countSpendTable + " руб.");

//        Обновление Activity
        mSwipeRefreshLayout = (SwipeRefreshLayout) viewLayoutGet.findViewById(R.id.main_swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadFromServer();

//                        new LoginActivity().loadDataFromServer();

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.red);


//        -------------------------------------------------------------------------------------------------------------------------


//        Возвращает ширину экраан
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        final int width = display.getWidth();

        return viewLayoutGet;
    }


    private void loadFromServer() {

        contentValues = new ContentValues();

//                Загрузка данных из сервера
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://myinfdb.000webhostapp.com")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        serverApi = retrofit.create(ServerApi.class);

        String sql = "delete from " + DBHelper.TABLE_GET;
        db.execSQL(sql);

        sql = "delete from " + DBHelper.TABLE_SPEND;
        db.execSQL(sql);

        sql = "delete from " + DBHelper.TABLE_GET_FOR_TOTAL;
        db.execSQL(sql);

        sql = "delete from " + DBHelper.TABLE_STATISTIC;
        db.execSQL(sql);

        //        ******************************* Загрузка данных с сервера ***********************************

//        --------------------------------------- Get -------------------------------------------------

        Call<List<ItemServerGet>> callGet = serverApi.loadTableGet(login);

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


//                        ------------------------- Извлечение данных из таблицы Get в БД --------------------------
                String sql = "select " + DBHelper.KEY_GET_NAME + ", " + DBHelper.KEY_GET_IMAGE + ", " + DBHelper.KEY_KOL_GET
                        + " from " + DBHelper.TABLE_GET + " where " + DBHelper.KEY_REF_LOGIN + " = ? and "
                        + DBHelper.KEY_GET_SYNCHRONISE + " != ?";
                Cursor cursorGridViewGet = db.rawQuery(sql, new String[]{login, "deleted"});

                listGet.removeAll(listGet);

                String btnName, btnOnCount;
                int imgBtn;

                if (cursorGridViewGet.moveToFirst()) {
                    do {
                        btnName = cursorGridViewGet.getString(cursorGridViewGet.getColumnIndex(DBHelper.KEY_GET_NAME));
                        imgBtn = cursorGridViewGet.getInt(cursorGridViewGet.getColumnIndex(DBHelper.KEY_GET_IMAGE));
                        btnOnCount = cursorGridViewGet.getString(cursorGridViewGet.getColumnIndex(DBHelper.KEY_KOL_GET));

                        Log.d("myLogs", "btnName = " + btnName + " imgBtn = " + imgBtn + " btnOnCount = " + btnOnCount);

                        listGet.add(new Item(btnName, imgBtn, btnOnCount));
                    } while (cursorGridViewGet.moveToNext());
                }
                cursorGridViewGet.close();
//        ------------------------------------------------------------------------------------------

//            listGet.add(new Item("", R.drawable.ic_control_point_black_24dp, ""));
                listGet.add(new Item("", R.drawable.button_add, ""));

                gridViewGet = (MyGridView) viewLayoutGet.findViewById(R.id.gridViewGet);

                gridAdapterGet = new GridAdapter(getActivity(), R.id.tvBtnName, R.id.imgBtn, R.id.tvBtnOnCount, listGet);

                gridViewGet.setAdapter(gridAdapterGet);


            }

            @Override
            public void onFailure(Call<List<ItemServerGet>> call, Throwable t) {
                Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });




//                        ------------------------------- Spend ---------------------------------

        Call<List<ItemServerSpend>> callSpend = serverApi.loadTableSpend(login);

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


                Cursor cursorGridViewSpend;
                String btnNameSpend;
                int imgBtnSpend;
                String btnOnCountSpend;

//        ------------------------- Извлечение данных из таблицы Spend в БД --------------------------
                String sql = "select " + DBHelper.KEY_SPEND_NAME + ", " + DBHelper.KEY_SPEND_IMAGE + ", " + DBHelper.KEY_KOL_SPEND
                        + " from " + DBHelper.TABLE_SPEND + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = ? and "
                        + DBHelper.KEY_SPEND_SYNCHRONISE + " != ?";
                cursorGridViewSpend = db.rawQuery(sql, new String[]{login, "deleted"});

                listSpend.removeAll(listSpend);

                if (cursorGridViewSpend.moveToFirst()) {
                    do {
                        btnNameSpend = cursorGridViewSpend.getString(cursorGridViewSpend.getColumnIndex(DBHelper.KEY_SPEND_NAME));
                        imgBtnSpend = cursorGridViewSpend.getInt(cursorGridViewSpend.getColumnIndex(DBHelper.KEY_SPEND_IMAGE));
                        btnOnCountSpend = cursorGridViewSpend.getString(cursorGridViewSpend.getColumnIndex(DBHelper.KEY_KOL_SPEND));

                        Log.d("myLogs", "btnName = " + btnNameSpend + " imgBtn = " + imgBtnSpend + " btnOnCount = " + btnOnCountSpend);

                        listSpend.add(new Item(btnNameSpend, imgBtnSpend, btnOnCountSpend));
                    } while (cursorGridViewSpend.moveToNext());
                }
                cursorGridViewSpend.close();
//        ------------------------------------------------------------------------------------------

//            listSpend.add(new Item("", R.drawable.ic_control_point_black_24dp, ""));
                listSpend.add(new Item("", R.drawable.button_add, ""));

                gridViewSpend = (MyGridView) viewLayoutGet.findViewById(R.id.gridViewSpend);

                gridAdapterSpend = new GridAdapter(getActivity(), R.id.tvBtnName, R.id.imgBtn, R.id.tvBtnOnCount, listSpend);

                gridViewSpend.setAdapter(gridAdapterSpend);



            }

            @Override
            public void onFailure(Call<List<ItemServerSpend>> call, Throwable t) {
                Toast.makeText(getActivity(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });



//                        ------------------------ Total ----------------------------

        Call<List<ItemServerTotal>> callTotal = serverApi.loadTableTotal(login);

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

                tvGetSpend.setText("Получил " + kol + " руб. Потратил " + countSpendTable + " руб.");
            }

            @Override
            public void onFailure(Call<List<ItemServerTotal>> call, Throwable t) {

            }
        });


        //                        --------------------------- Statistic ------------------------------------

        Call<List<ItemServerStatistic>> callStatistic = serverApi.loadTableStatistic(login);

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

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


//    Принимаем результат
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_NEW_BTN:
//                    Intent intent = new Intent(getActivity(), UserActivity.class);
//                    intent.putExtra("name_fragment", "fmain");
//                    startActivity(intent);

//                    Перезагрузка Activity
                    Log.d("myLogs", "Reload");
                    Intent i = new Intent(getActivity(), getActivity().getClass());
                    i.putExtra("name_fragment", "fmain");
                    getActivity().startActivity(i);

//                    Оригинал
//                    Intent i = new Intent( this , this.getClass() );
//                    finish();
//                    this.startActivity(i);

                    break;
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MyGridView gv;
        int position;
        AdapterView.AdapterContextMenuInfo info;

        switch (v.getId()){
            case  R.id.gridViewGet:
                getActivity().getMenuInflater().inflate(R.menu.context_menu_get, menu);
                gv = (MyGridView) v;
                info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                position = info.position;
                if (!(position == gv.getCount() - 1)) {

                    Toast.makeText(getActivity(), listGet.get(position).getBtnName(), Toast.LENGTH_SHORT).show();
                    nameOfButton = listGet.get(position).getBtnName();
                } else {
                    menu.clear();
                    menu.close();
                }
                break;

            case R.id.gridViewSpend:
                getActivity().getMenuInflater().inflate(R.menu.context_menu_spend, menu);
                gv = (MyGridView) v;
                info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                position = info.position;
                if (!(position == gv.getCount() - 1)) {

                    Toast.makeText(getActivity(), listSpend.get(position).getBtnName(), Toast.LENGTH_SHORT).show();
                    nameOfButton = listSpend.get(position).getBtnName();
                } else {
                    menu.clear();
                    menu.close();
                }

        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        CharSequence messageMenu;
        switch (item.getItemId()) {
            case R.id.context_edit_get:
//                messageMenu = "Выбран пункт Редактировать Get";
                Intent intent = new Intent(this.getActivity(), EditingActivity.class);
                intent.putExtra("action_key", "action_get");
                intent.putExtra("btnName", nameOfButton);
//                Toast.makeText(getActivity(), "nameOfButton = " + nameOfButton, Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUEST_CODE_NEW_BTN);
                break;

            case R.id.context_edit_last_get:
                intent = new Intent(getActivity(), EditingLastRecord.class);
                intent.putExtra("btnName", nameOfButton);
                startActivityForResult(intent, REQUEST_CODE_NEW_BTN);
                break;

            case R.id.context_money_transaction:
                intent = new Intent(getActivity(), MoneyTransactionActivity.class);
                intent.putExtra("btnName", nameOfButton);
                startActivityForResult(intent, REQUEST_CODE_NEW_BTN);
                break;

            case R.id.context_delete_get:
//                messageMenu = "Выбран пункт Удалить Get";


                //        --------------------------- Всплывающее окно Alert Dialog -----------------------
                Context context = getActivity();
                String title = "Удаление элемента " + nameOfButton;
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

                        String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'deleted' where "
                                + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + nameOfButton + "'";
                        db.execSQL(sql);

                        Call<Void> callDeleteGet = serverApi.deleteFromTableGet("get", nameOfButton, login);

                        callDeleteGet.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "delete from " + DBHelper.TABLE_GET + " where " + DBHelper.KEY_REF_LOGIN + " = '"
                                        + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + nameOfButton + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_GET + " set " + DBHelper.KEY_GET_SYNCHRONISE + " = 'deleted' where "
                                        + DBHelper.KEY_REF_LOGIN + " = '" + login + "' and " + DBHelper.KEY_GET_NAME + " = '" + nameOfButton + "'";
                                db.execSQL(sql);

                            }
                        });

//                        --------------- Уменьшение значения из таблицы TotalGet
//                    на ту сумму которая была получена в текущем месяце ---------------------------

                        Calendar calendar = Calendar.getInstance();

                        int monthNow = calendar.get(Calendar.MONTH) + 1;

//                        Выбираем сумму из TotalGet которая в данный момент на счету
                        sql = "select " + DBHelper.KEY_KOL_TOTAL_GET + " from " + DBHelper.TABLE_GET_FOR_TOTAL
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
                        cursor = db.rawQuery(sql, new String[]{login, "get", nameOfButton});

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
                        cursor = db.rawQuery(sql, new String[]{login, "transaction", nameOfButton});

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


                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        intent.putExtra("name_fragment", "fmain");
                        startActivity(intent);
                    }
                });

                ad.setNeutralButton(buttonTransact, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), MoneyTransactionActivity.class);
                        intent.putExtra("btnName", nameOfButton);
                        startActivityForResult(intent, REQUEST_CODE_NEW_BTN);
                    }
                });

                ad.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

//        -----------------------------------------------------------------------------------------

                ad.show();
                break;
            case R.id.context_edit_spend:
//                messageMenu = "Выбран пункт Редактировать Spend";

                intent = new Intent(this.getActivity(), EditingActivity.class);
                intent.putExtra("action_key", "action_spend");
                intent.putExtra("btnName", nameOfButton);
                Toast.makeText(getActivity(), "nameOfButton = " + nameOfButton, Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUEST_CODE_NEW_BTN);
                break;

            case R.id.context_edit_last_spend:
                intent = new Intent(getActivity(), EditingLastRecordSpend.class);
                intent.putExtra("btnName", nameOfButton);
                startActivityForResult(intent, REQUEST_CODE_NEW_BTN);
                break;

            case R.id.context_delete_spend:
//                messageMenu = "Выбран пункт Удалить Spend";

                //        --------------------------- Всплывающее окно Alert Dialog -----------------------
                context = getActivity();
                title = "Удаление элемента " + nameOfButton;
                message = "Вы действительно хотите удалить данный элемент? Все данные будут утеряны!";
                buttonYes = "Да";
                buttonNo = "Нет";

                ad = new AlertDialog.Builder(context);
                ad.setTitle(title);
                ad.setMessage(message);
                ad.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'deleted' where "
                                + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + nameOfButton + "'";
                        db.execSQL(sql);


                        Call<Void> callDeleteGet = serverApi.deleteFromTableGet("spend", nameOfButton, login);

                        callDeleteGet.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                                String sql = "delete from " + DBHelper.TABLE_SPEND + " where " + DBHelper.KEY_REF_LOGIN_SPEND + " = '"
                                        + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + nameOfButton + "'";
                                db.execSQL(sql);

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {

                                String sql = "update " + DBHelper.TABLE_SPEND + " set " + DBHelper.KEY_SPEND_SYNCHRONISE + " = 'deleted' where "
                                        + DBHelper.KEY_REF_LOGIN_SPEND + " = '" + login + "' and " + DBHelper.KEY_SPEND_NAME + " = '" + nameOfButton + "'";
                                db.execSQL(sql);

                            }
                        });


                        Intent intent = new Intent(getActivity(), UserActivity.class);
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

                ad.show();
                break;

            default:
                return super.onContextItemSelected(item);
        }
//        Toast toast = Toast.makeText(getActivity(), messageMenu, Toast.LENGTH_SHORT);
//        toast.show();
        return true;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
//    Припеключении на длугую вкладку данные из List удаляются, т.к. при повторном создании будут созданы повторно
        listGet.removeAll(listGet);
        listSpend.removeAll(listSpend);
    }
}
