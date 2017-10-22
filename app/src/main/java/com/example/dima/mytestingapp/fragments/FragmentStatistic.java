package com.example.dima.mytestingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.Adapters.AdapterSpinnerStatistic;
import com.example.dima.mytestingapp.Adapters.StatisticAdapterGeneral;
import com.example.dima.mytestingapp.DBHelper;
import com.example.dima.mytestingapp.DatePickerFragment;
import com.example.dima.mytestingapp.Items.ItemStatisticGeneral;
import com.example.dima.mytestingapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentStatistic.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentStatistic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStatistic extends Fragment implements View.OnClickListener, DatePickerFragment.DatePickerDialogFragmentEvents {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;

    Button btnStatisticDayGen;
    Button btnStatisticWeekGen;
    Button btnStatisticMonthGen;
    Button btnStatisticQuarterGen;
    Button btnStatisticYearGen;
    Button btnStatisticAnyDayGen;

    TextView noData;

    Spinner spinStatistic;

    String selectSpinnerItem;  //  Выбранный элемент Spinner

    ListView lvItemStatisticGeneral;

    DBHelper dbHelper;
    SQLiteDatabase db;

    SharedPreferences sPrefLogin;

    String login;

    Date date;  //  Текущая дата
    String dateNow;

    String typePay;

    int DIALOG_DATE = 1;

    //    Значение даты по умолчанию
    Calendar calendar = Calendar.getInstance();
    int myYear = calendar.get(Calendar.YEAR);
    int myMonth = calendar.get(Calendar.MONTH);
    int myDay = calendar.get(Calendar.DAY_OF_MONTH);

    String getNewDateDialog;

    StatisticAdapterGeneral statisticAdapterGeneral;   //  Адаптер для списка

    ArrayList<ItemStatisticGeneral> listStatisticGeneral = new ArrayList<ItemStatisticGeneral>();   //  ArrayList для списка


    //    ArrayList<ItemSpinnerStatistic> listSpinner = new ArrayList<>();
    ArrayList<String> listSpinner = new ArrayList<>();  //  ArrayList для Spinner

    AdapterSpinnerStatistic spinnerAdapter;  //  Адаптер для Spinner

    HashSet<String> dateBegin;  //  HashSet для хранения уникальных данных (используется для хранения дат начала недели)

    ArrayList<String> listData = new ArrayList<>();
    ArrayList<String> listDataWeek = new ArrayList<>();
    ArrayList<String> listMonth = new ArrayList<>();
    ArrayList<String> listDataYear = new ArrayList<>();


    private OnFragmentInteractionListener mListener;

    public FragmentStatistic() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentStatistic.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentStatistic newInstance(String param1, String param2) {
        FragmentStatistic fragment = new FragmentStatistic();
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
            activity.setTitle("Статистика");
        }

        view = inflater.inflate(R.layout.fragment_statistic, container, false);


//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
//        tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
//        tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
//        tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        btnStatisticDayGen = (Button) view.findViewById(R.id.btnStatisticDayGen);
        btnStatisticDayGen.setOnClickListener(this);
        btnStatisticWeekGen = (Button) view.findViewById(R.id.btnStatisticWeekGen);
        btnStatisticWeekGen.setOnClickListener(this);
        btnStatisticMonthGen = (Button) view.findViewById(R.id.btnStatisticMonthGen);
        btnStatisticMonthGen.setOnClickListener(this);
        btnStatisticQuarterGen = (Button) view.findViewById(R.id.btnStatisticQuarterGen);
        btnStatisticQuarterGen.setOnClickListener(this);
        btnStatisticYearGen = (Button) view.findViewById(R.id.btnStatisticYearGen);
        btnStatisticYearGen.setOnClickListener(this);
        btnStatisticAnyDayGen = (Button) view.findViewById(R.id.btnStatisticAnyDayGen);
        btnStatisticAnyDayGen.setOnClickListener(this);

        spinStatistic = (Spinner) view.findViewById(R.id.spinStatistic);

        spinStatistic.setEnabled(false);
        spinStatistic.setVisibility(View.GONE);

        lvItemStatisticGeneral = (ListView) view.findViewById(R.id.lvItemStatisticGeneral);

        noData = (TextView) view.findViewById(R.id.no_stat_text);


        dbHelper = new DBHelper(getActivity());

        db = dbHelper.getWritableDatabase();

        sPrefLogin = getActivity().getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
        login = sPrefLogin.getString("save_login", "");


        date = new Date();
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateNow = format.format(date);
        Log.d("myLogs", "dateNow = " + dateNow);


        String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", " + DBHelper.KEY_STATISTIC_DATE + ", "
                + DBHelper.KEY_STATISTIC_KOL + ", "
                + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", " + DBHelper.KEY_STATISTIC_TIME
                + " from " + DBHelper.TABLE_STATISTIC
                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND "
                + DBHelper.KEY_STATISTIC_DATE + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{login, dateNow});

        if (cursor.getCount() == 0){
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }

        int imgId = 0;
        String sign = null;
//        int color = 0;
        int color = 0;

        if (cursor.moveToFirst()) {
            do {
                String typeItem = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                if (typeItem.equals("get")){
                    imgId = R.mipmap.ic_get;
                    sign = "+ ";
//                    color = Color.GREEN;
                    color = R.color.colorTextGreen;
                }
                if (typeItem.equals("spend")){
                    imgId = R.mipmap.ic_spend;
                    sign = "- ";
//                    color = Color.RED;
                    color = R.color.colorTextRed;
                }
                if (typeItem.equals("transaction")){
                    imgId = R.mipmap.ic_transaction;
                    sign = "- ";
                    color = R.color.colorTextBlack;
                }

                listStatisticGeneral.add(new ItemStatisticGeneral(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
//                        imgId,
                        sign + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)), color,
                        cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID))));
            } while (cursor.moveToNext());
        }
        cursor.close();

        statisticAdapterGeneral = new StatisticAdapterGeneral(getActivity(), listStatisticGeneral);

        lvItemStatisticGeneral.setAdapter(statisticAdapterGeneral);




        lvItemStatisticGeneral.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                sPrefLogin = getActivity().getSharedPreferences("SharedPrefLogin",MODE_PRIVATE);
                login = sPrefLogin.getString("save_login", "");

                int itemId = listStatisticGeneral.get((int) id).getId();


                String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", " + DBHelper.KEY_STATISTIC_DATE + ", "
                        + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", " + DBHelper.KEY_STATISTIC_TIME + ", "
                        + DBHelper.KEY_STATISTIC_REF_LOGIN
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_ID + " =? AND "
                        + DBHelper.KEY_STATISTIC_REF_LOGIN + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(itemId), login});


                if (cursor.moveToFirst()) {
                    do {
                        String type = null;
                        String sign = null;

                        String message = null;
                        switch (cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE))){
                            case "get":
                                type = "Приход";
                                sign = "+";
                                message = "Тип: " + type + "\n"
                                        + "Сумма: " + sign + " " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб." + "\n"
                                        + "Получил на: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)) + "\n"
                                        + "Дата: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)) + "\n"
                                        + "Время: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME));
                                break;
                            case "spend":
                                type = "Расход";
                                sign = "-";
                                message = "Тип: " + type + "\n"
                                        + "Сумма: " + sign + " " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб." + "\n"
                                        + "Потратил с: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)) + "\n"
                                        + "Потратил на: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)) + "\n"
                                        + "Дата: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)) + "\n"
                                        + "Время: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME));
                                break;
                            case "transaction":
                                type = "Перевод";
                                sign = "-";
                                message = "Тип: " + type + "\n"
                                        + "Сумма: " + sign + " " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб." + "\n"
                                        + "Снял с: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)) + "\n"
                                        + "Перевел на: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)) + "\n"
                                        + "Дата: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)) + "\n"
                                        + "Время: " + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TIME));
                                break;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Инфомация")
                                .setMessage(message)
                                .setCancelable(false)
                                .setNegativeButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    } while (cursor.moveToNext());
                }

                cursor.close();

                return true;
            }
        });




        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStatisticDayGen:   //   Отображение данных за текущий день

                spinStatistic.setEnabled(false);
                listSpinner.removeAll(listSpinner);
                spinStatistic.setVisibility(View.GONE);

                Toast.makeText(getActivity(), "Текущий день", Toast.LENGTH_SHORT).show();

                date = new Date();
                DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String dateNow = format.format(date);
                Log.d("myLogs", "dateNow = " + dateNow);


                String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", " + DBHelper.KEY_STATISTIC_DATE + ", "
                        + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", " + DBHelper.KEY_STATISTIC_TIME
                        + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND "
                        + DBHelper.KEY_STATISTIC_DATE + " =?";
                Cursor cursor = db.rawQuery(sql, new String[]{login, dateNow});

                if (cursor.getCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                } else {
                    noData.setVisibility(View.GONE);
                }

                int imgId = 0;
                String sign = null;
                int color = 0;

                listStatisticGeneral.removeAll(listStatisticGeneral);

                if (cursor.moveToFirst()) {
                    do {
                        String typeItem = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                        if (typeItem.equals("get")){
                            imgId = R.mipmap.ic_get;
                            sign = "+ ";
                            color = R.color.colorTextGreen;
                        }
                        if (typeItem.equals("spend")){
                            imgId = R.mipmap.ic_spend;
                            sign = "- ";
                            color = R.color.colorTextRed;
                        }
                        if (typeItem.equals("transaction")){
                            imgId = R.mipmap.ic_transaction;
                            sign = "- ";
                            color = R.color.colorTextBlack;
                        }

                        listStatisticGeneral.add(new ItemStatisticGeneral(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
//                                imgId,
                                sign + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)), color,
                                cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID))));
                    } while (cursor.moveToNext());
                }
                cursor.close();

                statisticAdapterGeneral = new StatisticAdapterGeneral(getActivity(), listStatisticGeneral);

                lvItemStatisticGeneral.setAdapter(statisticAdapterGeneral);
                break;

            case R.id.btnStatisticWeekGen:    //   Отображение данных по неделям

////                Создается переменная SharedPreferences для запоминания первого нажатия на кнопку "Неделя", для
////                избежания дублирования в Spinner
//                SharedPreferences sPrefSpin = getActivity().getSharedPreferences("SharedPrefSpinner", MODE_PRIVATE);
//                Boolean firstClickSpinner = sPrefSpin.getBoolean("first_click", true);
//
//                if (!firstClickSpinner){
//                    listSpinner.removeAll(listSpinner);
//                } else {
//                    SharedPreferences.Editor e = sPrefSpin.edit();
//                    e.putBoolean("first_click", false);
//                    e.apply();
//                }


                date = new Date();  //  Текущая дата
                format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                dateNow = format.format(date);

                spinStatistic.setEnabled(true);
                spinStatistic.setVisibility(View.VISIBLE);

                dateBegin = new HashSet<>();

//                Формитование данныхдля Spinner
                sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                        + DBHelper.KEY_STATISTIC_TIME + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                cursor = db.rawQuery(sql, new String[]{login});

                listSpinner.removeAll(listSpinner);

                if (cursor.moveToFirst()) {
                    do {
                        String dateWeek = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));

                        Date dateWeekDate = null;
                        try {
                            dateWeekDate = format.parse(dateWeek);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.setTime(dateWeekDate);


//                --------------------------------------- Рабочий ----------------------------------
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                        Date weekStartDate = calendar.getTime();
                        String weekStart = format.format(weekStartDate);

                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                        Date weekEndDate = calendar.getTime();
                        String weekEnd = format.format(weekEndDate);

                        Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
//                ----------------------------------------------------------------------------------

//                Определение текущего дня
                        calendar.setTime(dateWeekDate);
                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        int day = calendar.get(Calendar.DAY_OF_WEEK);

                        Log.d("myLogs", "day = " + day + " dateNow.substring(0,2) = " + dateWeek.substring(0,2));
                        calendar.setTime(dateWeekDate);

                        if (day == Calendar.SUNDAY){
                            Log.d("myLogs", "Here is day 1");
//                            Toast.makeText(getActivity(), "Day " + day, Toast.LENGTH_SHORT).show();
                            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                            weekStartDate = calendar.getTime();
                            weekStart = format.format(weekStartDate);

                            calendar.setFirstDayOfWeek(Calendar.MONDAY);
                            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                            weekEndDate = calendar.getTime();
                            weekEnd = format.format(weekEndDate);

                            Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
                        }

//                Если day = номеру дня месяца
                        if (day == Integer.parseInt(dateWeek.substring(0,2))){
                            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                            weekStartDate = calendar.getTime();
                            weekStart = format.format(weekStartDate);

                            calendar.setTime(weekStartDate);
                            calendar.add(Calendar.DAY_OF_WEEK, +6);
                            weekEndDate = calendar.getTime();  // это будет конец недели
                            weekEnd = format.format(weekEndDate);
                        }

                        Log.d("myLogs", "Final !!! weekStart = " + weekStart + " weekEnd = " + weekEnd + " dateNow = " + dateNow);

//                        dateWeekBegin.add(weekStart + " - " + weekEnd);
                        dateBegin.add(weekStart);

                    } while (cursor.moveToNext());
                }
                cursor.close();




//                Из коллекции Set записываем данные в массив
                Date[] sortedArray = new Date[dateBegin.size()];

                Iterator<String> iter = dateBegin.iterator();
                while (iter.hasNext()) {
                    for (int i = 0; i < dateBegin.size(); i++){
                        Date dateArr = null;
                        try {
                            dateArr= format.parse(iter.next());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        sortedArray[i] = dateArr;
                    }
                }

                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "Array: " + format.format(sortedArray[i]));
                }

//                Сортируем массив по датам по убыванию
                Date r;
                for (int j = 0; j < sortedArray.length; j++){
                    for (int i = 0; i < sortedArray.length-1; i++){
                        if (sortedArray[i].compareTo(sortedArray[i+1]) == -1){
                            r = sortedArray[i];
                            sortedArray[i] = sortedArray[i+1];
                            sortedArray[i+1] = r;
                        }
                    }
                }


//                Заполнение listSpinner из массива
                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "ArraySort: " + format.format(sortedArray[i]));



                    calendar.setTime(sortedArray[i]);


//                --------------------------------------- Рабочий ----------------------------------
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                    Date weekStartDate = calendar.getTime();
                    String weekStart = format.format(weekStartDate);

                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                    Date weekEndDate = calendar.getTime();
                    String weekEnd = format.format(weekEndDate);

                    Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
//                ----------------------------------------------------------------------------------

//                Определение выбранного дня
                    calendar.setTime(sortedArray[i]);
                    calendar.setFirstDayOfWeek(Calendar.MONDAY);
                    int day = calendar.get(Calendar.DAY_OF_WEEK);

                    calendar.setTime(sortedArray[i]);

                    if (day == Calendar.SUNDAY){
                        Log.d("myLogs", "Here is day 1");
//                        Toast.makeText(getActivity(), "Day " + day, Toast.LENGTH_SHORT).show();
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                        weekStartDate = calendar.getTime();
                        weekStart = format.format(weekStartDate);

                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                        weekEndDate = calendar.getTime();
                        weekEnd = format.format(weekEndDate);

                        Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
                    }

//                Если day = номеру дня месяца
                    if (day == Integer.parseInt(format.format(sortedArray[i]).substring(0,2))){
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)+1); // это будет начало недели
                        weekStartDate = calendar.getTime();
                        weekStart = format.format(weekStartDate);

                        calendar.setTime(weekStartDate);
                        calendar.add(Calendar.DAY_OF_WEEK, +6);
                        weekEndDate = calendar.getTime();  // это будет конец недели
                        weekEnd = format.format(weekEndDate);
                    }

                    Log.d("myLogs", "Final !!! weekStart = " + weekStart + " weekEnd = " + weekEnd + " dateNow = " + dateNow);


                    listSpinner.add(weekStart + " - " + weekEnd);

                    listDataWeek.add(weekStart + " - " + weekEnd);  //  Держит данные именно дат !!!
                }



                //        Отображение кастомного Spinner

                spinnerAdapter = new AdapterSpinnerStatistic(getActivity(),listSpinner);

                spinStatistic.setAdapter(spinnerAdapter);

//                Отображение при выботе определенного пункта Spinner
                spinStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

//                        selectSpinnerItem = listSpinner.get(position);
                        selectSpinnerItem = listDataWeek.get(position);

                        String weekStart = selectSpinnerItem.substring(0, 10);   //  07.08.2017 - 13.08.17




                        Log.d("myLogsN", "weekStart = " + weekStart);

                        Date dateWeekStart = null;
                        try {
                            dateWeekStart= format.parse(weekStart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.setTime(dateWeekStart);


//                --------------------------------------- Рабочий ----------------------------------
                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                        Date weekEndDate = calendar.getTime();
                        String weekEnd = format.format(weekEndDate);

                        Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
//                ----------------------------------------------------------------------------------

//                Определение текущего дня
                        calendar.setTime(dateWeekStart);
                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        int day = calendar.get(Calendar.DAY_OF_WEEK);

                        calendar.setTime(dateWeekStart);

                        if (day == Calendar.SUNDAY){
                            calendar.setFirstDayOfWeek(Calendar.MONDAY);
                            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1); // это будет конец недели
                            weekEndDate = calendar.getTime();
                            weekEnd = format.format(weekEndDate);

                            Log.d("myLogs", "weekStart = " + weekStart + " weekEnd = " + weekEnd);
                        }

//                Если day = номеру дня месяца
                        if (day == Integer.parseInt(weekStart.substring(0,2))){
                            calendar.setTime(dateWeekStart);
                            calendar.add(Calendar.DAY_OF_WEEK, +6);
                            weekEndDate = calendar.getTime();  // это будет конец недели
                            weekEnd = format.format(weekEndDate);
                        }

                        Log.d("myLogs", "Final !!! weekStart = " + weekStart + " weekEnd = " + weekEnd);










//                        String weekEnd = selectSpinnerItem.substring(13);        //  0        10  13     позиции
//                        Log.d("myLogs", "Final !!! weekStart = " + weekStart + " weekEnd = " + weekEnd);
//
//                        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
//
//                        Date weekStartDate = null;
//                        Date weekEndDate = null;
//                        try {
//                            weekStartDate = format.parse(weekStart);
//                            weekEndDate = format.parse(weekEnd);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }


                        String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", "
                                + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                                + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                                + DBHelper.KEY_STATISTIC_TIME
                                + " from " + DBHelper.TABLE_STATISTIC
                                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                        Cursor cursor = db.rawQuery(sql, new String[]{login});

                        if (cursor.getCount() == 0){
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            noData.setVisibility(View.GONE);
                        }

                        int imgId = 0;
                        String sign = null;
                        int color = 0;

                        listStatisticGeneral.removeAll(listStatisticGeneral);

                        if (cursor.moveToFirst()) {
                            do {
                                String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                                Date gettingDate = null;
                                try {
                                    gettingDate = format.parse(gettingDateString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String typeItem = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                                if (typeItem.equals("get")){
                                    imgId = R.mipmap.ic_get;
                                    sign = "+ ";
                                    color = R.color.colorTextGreen;
                                }
                                if (typeItem.equals("spend")){
                                    imgId = R.mipmap.ic_spend;
                                    sign = "- ";
                                    color = R.color.colorTextRed;
                                }
                                if (typeItem.equals("transaction")){
                                    imgId = R.mipmap.ic_transaction;
                                    sign = "- ";
                                    color = R.color.colorTextBlack;
                                }

                                if ((gettingDate.compareTo(dateWeekStart) == 1 || gettingDateString.equals(weekStart)) &&
                                        (gettingDate.compareTo(weekEndDate) == -1 || gettingDateString.equals(weekEnd))){


                                    listStatisticGeneral.add(new ItemStatisticGeneral(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
//                                imgId,
                                            sign + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)), color,
                                            cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID))));

                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        statisticAdapterGeneral = new StatisticAdapterGeneral(getActivity(), listStatisticGeneral);

                        lvItemStatisticGeneral.setAdapter(statisticAdapterGeneral);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                break;

            case R.id.btnStatisticMonthGen:    //   Отображение данных по месяцам


                date = new Date();  //  Текущая дата
                format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                spinStatistic.setEnabled(true);
                spinStatistic.setVisibility(View.VISIBLE);

                dateBegin = new HashSet<>();

                String monthStart;
                String monthEnd;

//                Формитование данныхдля Spinner
                sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                        + DBHelper.KEY_STATISTIC_TIME + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.getCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                } else {
                    noData.setVisibility(View.GONE);
                }

                listSpinner.removeAll(listSpinner);

                if (cursor.moveToFirst()) {
                    do {
                        String dateMonth = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));

                        Date dateMonthDate = null;
                        try {
                            dateMonthDate= format.parse(dateMonth);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.setTime(dateMonthDate);


//                --------------------------------------- Рабочий ----------------------------------
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)); // это будет начало месяца
                        Date monthStartDate = calendar.getTime();
                        monthStart = format.format(monthStartDate);

                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец месяца
                        Date monthEndDate = calendar.getTime();
                        monthEnd = format.format(monthEndDate);

                        Log.d("myLogs", "monthStart = " + monthStart + " monthEnd = " + monthEnd);
//                ----------------------------------------------------------------------------------

                        dateBegin.add(monthStart);

                    } while (cursor.moveToNext());
                }
                cursor.close();


//                Из коллекции Set записываем данные в массив
                sortedArray = new Date[dateBegin.size()];

                iter = dateBegin.iterator();
                while (iter.hasNext()) {
                    for (int i = 0; i < dateBegin.size(); i++){
                        Date dateArr = null;
                        try {
                            dateArr = format.parse(iter.next());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        sortedArray[i] = dateArr;
                    }
                }

                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "Array: " + format.format(sortedArray[i]));
                }

//                Сортируем массив по датам по убыванию
                for (int j = 0; j < sortedArray.length; j++){
                    for (int i = 0; i < sortedArray.length-1; i++){
                        if (sortedArray[i].compareTo(sortedArray[i+1]) == -1){
                            r = sortedArray[i];
                            sortedArray[i] = sortedArray[i+1];
                            sortedArray[i+1] = r;
                        }
                    }
                }

                //                Заполнение listSpinner из массива
                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "ArraySort: " + format.format(sortedArray[i]));



                    calendar.setTime(sortedArray[i]);


//                --------------------------------------- Рабочий ----------------------------------
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_WEEK)); // это будет начало месяца
                    Date monthStartDate = calendar.getTime();
                    monthStart = format.format(monthStartDate);

                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец месяца
                    Date monthEndDate = calendar.getTime();
                    monthEnd = format.format(monthEndDate);

                    Log.d("myLogs", "monthStart = " + monthStart + " monthEnd = " + monthEnd);
//                ----------------------------------------------------------------------------------

                    String monthName = null;  //  Название месяца

                    int monthNumber = Integer.parseInt(monthStart.substring(3,5));   //  08

                    switch (monthNumber){
                        case 1:
                            monthName = "Январь";
                            break;
                        case 2:
                            monthName = "Февраль";
                            break;
                        case 3:
                            monthName = "Март";
                            break;
                        case 4:
                            monthName = "Апрель";
                            break;
                        case 5:
                            monthName = "Май";
                            break;
                        case 6:
                            monthName = "Июнь";
                            break;
                        case 7:
                            monthName = "Июль";
                            break;
                        case 8:
                            monthName = "Август";
                            break;
                        case 9:
                            monthName = "Сентябрь";
                            break;
                        case 10:
                            monthName = "Октябрь";
                            break;
                        case 11:
                            monthName = "Ноябрь";
                            break;
                        case 12:
                            monthName = "Декабрь";
                            break;
                    }

                    String year = monthStart.substring(6,10);  // 2017

                    Log.d("myLogsN", monthName + " " + year);

//                    listSpinner.add(monthStart + " - " + monthEnd);
                    listSpinner.add(monthName + " " + year);

                    listMonth.add(monthStart + " - " + monthEnd);
                }


                //        Отображение кастомного Spinner

                spinnerAdapter = new AdapterSpinnerStatistic(getActivity(), listSpinner);

                spinStatistic.setAdapter(spinnerAdapter);






                //                Отображение при выботе определенного пункта Spinner
                spinStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

//                        selectSpinnerItem = listSpinner.get(position);
                        selectSpinnerItem = listMonth.get(position);

                        String monthStart = selectSpinnerItem.substring(0, 10);   //  07.08.2017 - 13.08.17


                        Log.d("myLogs", "monthStart = " + monthStart);

                        Date dateMonthStart = null;
                        try {
                            dateMonthStart = format.parse(monthStart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.setTime(dateMonthStart);



////                --------------------------------------- Рабочий ----------------------------------
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец месяца
                        Date monthEndDate = calendar.getTime();
                        String endMonth = format.format(monthEndDate);

                        Log.d("myLogs", "monthStart = " + monthStart + " endMonth = " + endMonth);
////                ----------------------------------------------------------------------------------


                        String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", "
                                + DBHelper.KEY_STATISTIC_DATE + ", "
                                + DBHelper.KEY_STATISTIC_KOL + ", " + DBHelper.KEY_STATISTIC_NAME_GET + ", "
                                + DBHelper.KEY_STATISTIC_NAME_SPEND + ", " + DBHelper.KEY_STATISTIC_TIME
                                + " from " + DBHelper.TABLE_STATISTIC
                                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                        Cursor cursor = db.rawQuery(sql, new String[]{login});

                        int imgId = 0;
                        String sign = null;
                        int color = 0;

                        listStatisticGeneral.removeAll(listStatisticGeneral);

                        if (cursor.moveToFirst()) {
                            do {
                                String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                                Date gettingDate = null;
                                try {
                                    gettingDate = format.parse(gettingDateString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String typeItem = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                                if (typeItem.equals("get")){
                                    imgId = R.mipmap.ic_get;
                                    sign = "+ ";
                                    color = R.color.colorTextGreen;
                                }
                                if (typeItem.equals("spend")){
                                    imgId = R.mipmap.ic_spend;
                                    sign = "- ";
                                    color = R.color.colorTextRed;
                                }
                                if (typeItem.equals("transaction")){
                                    imgId = R.mipmap.ic_transaction;
                                    sign = "- ";
                                    color = R.color.colorTextBlack;
                                }

                                if ((gettingDate.compareTo(dateMonthStart) == 1 || gettingDateString.equals(dateMonthStart)) &&
                                        (gettingDate.compareTo(monthEndDate) == -1 || gettingDateString.equals(monthEndDate))){


                                    listStatisticGeneral.add(new ItemStatisticGeneral(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
//                                imgId,
                                            sign + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)), color,
                                            cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID))));

                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        statisticAdapterGeneral = new StatisticAdapterGeneral(getActivity(), listStatisticGeneral);

                        lvItemStatisticGeneral.setAdapter(statisticAdapterGeneral);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });








                break;

            case R.id.btnStatisticQuarterGen:    //   Отображение данных за квартал

                format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                spinStatistic.setEnabled(true);
                spinStatistic.setVisibility(View.VISIBLE);

                dateBegin = new HashSet<>();

                //                Формитование данныхдля Spinner
                sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                        + DBHelper.KEY_STATISTIC_TIME + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.getCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                } else {
                    noData.setVisibility(View.GONE);
                }

                listSpinner.removeAll(listSpinner);

                if (cursor.moveToFirst()) {
                    do {
                        String dateQuarter = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));

                        Date dateQuarterDate = null;
                        try {
                            dateQuarterDate = format.parse(dateQuarter);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String beginOfMonth = null;
                        switch (getQuarter(dateQuarterDate)){
                            case 1:
                                beginOfMonth = "01.01";  //  Январь
                                break;
                            case 2:
                                beginOfMonth = "01.04";  //  Апрель
                                break;
                            case 3:
                                beginOfMonth = "01.07";  //  Июль
                                break;
                            case 4:
                                beginOfMonth = "01.10";  //  Октябрь
                        }

                        int year = Integer.parseInt(dateQuarter.substring(6, 10)); //calendar.get(Calendar.YEAR);
                        String beginQuarter = beginOfMonth + "." + year;   //  Определение даты начала квартала
                        Log.d("myLogs", "beginQuarter = " + beginQuarter);


                        Date beginQuarterDate = null;
                        try {
                            beginQuarterDate = format.parse(beginQuarter);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        calendar.setTime(beginQuarterDate);
                        calendar.add(Calendar.MONTH, +2);
                        Date endBeginQuarterDate = calendar.getTime();  //  Дата начала последнего месяца квартала

                        calendar.setTime(endBeginQuarterDate);


////                --------------------------------------- Рабочий ----------------------------------
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец квартала
                        Date quarterEndDate = calendar.getTime();
                        String endQuarter = format.format(quarterEndDate);

                        Log.d("myLogs", "beginQuarter = " + beginQuarter + " endQuarter = " + endQuarter);
////                ----------------------------------------------------------------------------------

                        dateBegin.add(beginQuarter);

                    } while (cursor.moveToNext());
                }
                cursor.close();

                //                Из коллекции Set записываем данные в массив
                sortedArray = new Date[dateBegin.size()];

                iter = dateBegin.iterator();
                while (iter.hasNext()) {
                    for (int i = 0; i < dateBegin.size(); i++){
                        Date dateArr = null;
                        try {
                            dateArr = format.parse(iter.next());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        sortedArray[i] = dateArr;
                    }
                }

                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "Array: " + format.format(sortedArray[i]));
                }

//                Сортируем массив по датам по убыванию
                for (int j = 0; j < sortedArray.length; j++){
                    for (int i = 0; i < sortedArray.length-1; i++){
                        if (sortedArray[i].compareTo(sortedArray[i+1]) == -1){
                            r = sortedArray[i];
                            sortedArray[i] = sortedArray[i+1];
                            sortedArray[i+1] = r;
                        }
                    }
                }

                //                Заполнение listSpinner из массива
                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "ArraySort: " + format.format(sortedArray[i]));



                    calendar.setTime(sortedArray[i]);

                    String beginQuarter = format.format(sortedArray[i]);


                    calendar.add(Calendar.MONTH, +2);
                    Date endBeginQuarterDate = calendar.getTime();  //  Дата начала последнего месяца квартала

                    calendar.setTime(endBeginQuarterDate);


////                --------------------------------------- Рабочий ----------------------------------
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец квартала
                    Date quarterEndDate = calendar.getTime();
                    String endQuarter = format.format(quarterEndDate);

                    Log.d("myLogs", "beginQuarter = " + beginQuarter + " endQuarter = " + endQuarter);
////                ----------------------------------------------------------------------------------

                    String nameQuarter = null;

                    Date dateQuarter = null;
                    try {
                        dateQuarter = format.parse(beginQuarter);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    switch (getQuarter(dateQuarter)){
                        case 1:
                            nameQuarter = "Январь - Март";
                            break;
                        case 2:
                            nameQuarter = "Апрель - Июнь";
                            break;
                        case 3:
                            nameQuarter = "Июль - Сентябрь";;
                            break;
                        case 4:
                            nameQuarter = "Октябрь - Декабрь";
                            break;
                    }

                    String yearQuarter = beginQuarter.substring(6,10);  // 2017


//                    listSpinner.add(beginQuarter + " " + endQuarter);
                    listSpinner.add(nameQuarter + " " + yearQuarter);

                    listData.add(beginQuarter + " - " + endQuarter);
                }


                //        Отображение кастомного Spinner

                spinnerAdapter = new AdapterSpinnerStatistic(getActivity(), listSpinner);

                spinStatistic.setAdapter(spinnerAdapter);



                //                Отображение при выботе определенного пункта Spinner
                spinStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

//                        selectSpinnerItem = listSpinner.get(position);
                        selectSpinnerItem = listData.get(position);

                        String quarterStart = selectSpinnerItem.substring(0, 10);   //  07.08.2017 - 13.08.17


                        Log.d("myLogs", "quarterStart = " + quarterStart);

                        Date dateQuarterStart = null;
                        try {
                            dateQuarterStart = format.parse(quarterStart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.setTime(dateQuarterStart);


                        calendar.add(Calendar.MONTH, +2);
                        Date endBeginQuarterDate = calendar.getTime();  //  Дата начала последнего месяца квартала

                        calendar.setTime(endBeginQuarterDate);


////                --------------------------------------- Рабочий ----------------------------------
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // это будет конец квартала
                        Date quarterEndDate = calendar.getTime();
                        String endQuarter = format.format(quarterEndDate);

                        Log.d("myLogs", "beginQuarter = " + quarterStart + " endQuarter = " + endQuarter);
////                ----------------------------------------------------------------------------------


                        String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", "
                                + DBHelper.KEY_STATISTIC_DATE + ", "
                                + DBHelper.KEY_STATISTIC_KOL + ", " + DBHelper.KEY_STATISTIC_NAME_GET + ", "
                                + DBHelper.KEY_STATISTIC_NAME_SPEND + ", " + DBHelper.KEY_STATISTIC_TIME
                                + " from " + DBHelper.TABLE_STATISTIC
                                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                        Cursor cursor = db.rawQuery(sql, new String[]{login});

                        int imgId = 0;
                        String sign = null;
                        int color = 0;

                        listStatisticGeneral.removeAll(listStatisticGeneral);

                        if (cursor.moveToFirst()) {
                            do {
                                String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                                Date gettingDate = null;
                                try {
                                    gettingDate = format.parse(gettingDateString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String typeItem = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                                if (typeItem.equals("get")){
                                    imgId = R.mipmap.ic_get;
                                    sign = "+ ";
                                    color = R.color.colorTextGreen;
                                }
                                if (typeItem.equals("spend")){
                                    imgId = R.mipmap.ic_spend;
                                    sign = "- ";
                                    color = R.color.colorTextRed;
                                }
                                if (typeItem.equals("transaction")){
                                    imgId = R.mipmap.ic_transaction;
                                    sign = "- ";
                                    color = R.color.colorTextBlack;
                                }

                                if ((gettingDate.compareTo(dateQuarterStart) == 1 || gettingDateString.equals(dateQuarterStart)) &&
                                        (gettingDate.compareTo(quarterEndDate) == -1 || gettingDateString.equals(quarterEndDate))){


                                    listStatisticGeneral.add(new ItemStatisticGeneral(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
//                                imgId,
                                            sign + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)), color,
                                            cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID))));

                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        statisticAdapterGeneral = new StatisticAdapterGeneral(getActivity(), listStatisticGeneral);

                        lvItemStatisticGeneral.setAdapter(statisticAdapterGeneral);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                break;

            case R.id.btnStatisticYearGen:   //   Отображение данных по годам

                format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

                spinStatistic.setEnabled(true);
                spinStatistic.setVisibility(View.VISIBLE);

                dateBegin = new HashSet<>();

                //                Формитование данныхдля Spinner
                sql = "select " + DBHelper.KEY_STATISTIC_DATE + ", " + DBHelper.KEY_STATISTIC_KOL + ", "
                        + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", "
                        + DBHelper.KEY_STATISTIC_TIME + " from " + DBHelper.TABLE_STATISTIC
                        + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                cursor = db.rawQuery(sql, new String[]{login});

                if (cursor.getCount() == 0){
                    noData.setVisibility(View.VISIBLE);
                } else {
                    noData.setVisibility(View.GONE);
                }

                listSpinner.removeAll(listSpinner);

                if (cursor.moveToFirst()) {
                    do {
                        String dateYear = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));

                        Date dateYearDate = null;
                        try {
                            dateYearDate = format.parse(dateYear);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.setTime(dateYearDate);

//                --------------------------------------- Рабочий ----------------------------------
                        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMinimum(Calendar.DAY_OF_YEAR)); // это будет начало года
                        Date yearStartDate = calendar.getTime();
                        String yearStart = format.format(yearStartDate);

                        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); // это будет конец года
                        Date yearEndDate = calendar.getTime();
                        String yearEnd = format.format(yearEndDate);

                        Log.d("myLogs", "yearStart = " + yearStart + " yearEnd = " + yearEnd);
//                ----------------------------------------------------------------------------------

                        dateBegin.add(yearStart);

                    } while (cursor.moveToNext());
                }
                cursor.close();

                //                Из коллекции Set записываем данные в массив
                sortedArray = new Date[dateBegin.size()];

                iter = dateBegin.iterator();
                while (iter.hasNext()) {
                    for (int i = 0; i < dateBegin.size(); i++){
                        Date dateArr = null;
                        try {
                            dateArr = format.parse(iter.next());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        sortedArray[i] = dateArr;
                    }
                }

                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "Array: " + format.format(sortedArray[i]));
                }

//                Сортируем массив по датам по убыванию
                for (int j = 0; j < sortedArray.length; j++){
                    for (int i = 0; i < sortedArray.length-1; i++){
                        if (sortedArray[i].compareTo(sortedArray[i+1]) == -1){
                            r = sortedArray[i];
                            sortedArray[i] = sortedArray[i+1];
                            sortedArray[i+1] = r;
                        }
                    }
                }

                //                Заполнение listSpinner из массива
                for (int i = 0; i < sortedArray.length; i++){
                    Log.d("myLogs", "ArraySort: " + format.format(sortedArray[i]));

                    String yearStart = format.format(sortedArray[i]);

                    calendar.setTime(sortedArray[i]);

//                --------------------------------------- Рабочий ----------------------------------
                    calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); // это будет конец года
                    Date yearEndDate = calendar.getTime();
                    String yearEnd = format.format(yearEndDate);

                    Log.d("myLogs", "yearStart = " + yearStart + " yearEnd = " + yearEnd);
//                ----------------------------------------------------------------------------------


                    String year = yearStart.substring(6);

//                    String yearQuarter = beginQuarter.substring(6,10);  // 2017


//                    listSpinner.add(beginQuarter + " " + endQuarter);
                    listSpinner.add(year);

                    listDataYear.add(yearStart + " - " + yearEnd);
                }


                //        Отображение кастомного Spinner

                spinnerAdapter = new AdapterSpinnerStatistic(getActivity(), listSpinner);

                spinStatistic.setAdapter(spinnerAdapter);


                //                Отображение при выботе определенного пункта Spinner
                spinStatistic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

//                        selectSpinnerItem = listSpinner.get(position);
                        selectSpinnerItem = listDataYear.get(position);

                        String yearStart = selectSpinnerItem.substring(0, 10);   //  07.08.2017 - 13.08.17


                        Date dateYearStart = null;
                        try {
                            dateYearStart = format.parse(yearStart);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.setTime(dateYearStart);

//                --------------------------------------- Рабочий ----------------------------------
                        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR)); // это будет конец года
                        Date yearEndDate = calendar.getTime();
                        String yearEnd = format.format(yearEndDate);

                        Log.d("myLogs", "yearStart = " + yearStart + " yearEnd = " + yearEnd);
//                ----------------------------------------------------------------------------------


                        String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", "
                                + DBHelper.KEY_STATISTIC_DATE + ", "
                                + DBHelper.KEY_STATISTIC_KOL + ", " + DBHelper.KEY_STATISTIC_NAME_GET + ", "
                                + DBHelper.KEY_STATISTIC_NAME_SPEND + ", " + DBHelper.KEY_STATISTIC_TIME
                                + " from " + DBHelper.TABLE_STATISTIC
                                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=?";
                        Cursor cursor = db.rawQuery(sql, new String[]{login});

                        int imgId = 0;
                        String sign = null;
                        int color = 0;

                        listStatisticGeneral.removeAll(listStatisticGeneral);

                        if (cursor.moveToFirst()) {
                            do {
                                String gettingDateString = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE));
                                Date gettingDate = null;
                                try {
                                    gettingDate = format.parse(gettingDateString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String typeItem = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                                if (typeItem.equals("get")){
                                    imgId = R.mipmap.ic_get;
                                    sign = "+ ";
                                    color = R.color.colorTextGreen;
                                }
                                if (typeItem.equals("spend")){
                                    imgId = R.mipmap.ic_spend;
                                    sign = "- ";
                                    color = R.color.colorTextRed;
                                }
                                if (typeItem.equals("transaction")){
                                    imgId = R.mipmap.ic_transaction;
                                    sign = "- ";
                                    color = R.color.colorTextBlack;
                                }

                                if ((gettingDate.compareTo(dateYearStart) == 1 || gettingDateString.equals(dateYearStart)) &&
                                        (gettingDate.compareTo(yearEndDate) == -1 || gettingDateString.equals(yearEndDate))){


                                    listStatisticGeneral.add(new ItemStatisticGeneral(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
//                                imgId,
                                            sign + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                                            cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)), color,
                                            cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID))));

                                }
                            } while (cursor.moveToNext());
                        }
                        cursor.close();

                        statisticAdapterGeneral = new StatisticAdapterGeneral(getActivity(), listStatisticGeneral);

                        lvItemStatisticGeneral.setAdapter(statisticAdapterGeneral);


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


                break;

            case R.id.btnStatisticAnyDayGen:
//                getActivity().showDialog(DIALOG_DATE);
                Toast.makeText(getActivity(), "Any Day", Toast.LENGTH_SHORT).show();

                DatePickerFragment picker = new DatePickerFragment();
                picker.setDatePickerDialogFragmentEvents(FragmentStatistic.this); //Changed
                picker.show(getFragmentManager(), "Date Picker");

                break;
        }
    }


    //    Функция определяющая номер квартала
    public static int getQuarter(Date date) {
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        int month = c.get(Calendar.MONTH) + 1;
        int quarter;
        if (month < 4) quarter = 1;
        else if (month >= 4 && month < 7) quarter = 2;
        else if (month >= 7 && month < 10) quarter = 3;
        else quarter = 4;
        return quarter;
    }


//    Возвращается после DatePickerFragment
    @Override
    public void onDateSelected(String date) {

        String selectedDate = date;

        spinStatistic.setEnabled(false);
        listSpinner.removeAll(listSpinner);
        spinStatistic.setVisibility(View.GONE);

        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());


        String sql = "select " + DBHelper.KEY_STATISTIC_ID + ", " + DBHelper.KEY_STATISTIC_TYPE + ", "
                + DBHelper.KEY_STATISTIC_DATE + ", "
                + DBHelper.KEY_STATISTIC_KOL + ", "
                + DBHelper.KEY_STATISTIC_NAME_GET + ", " + DBHelper.KEY_STATISTIC_NAME_SPEND + ", " + DBHelper.KEY_STATISTIC_TIME
                + " from " + DBHelper.TABLE_STATISTIC
                + " where " + DBHelper.KEY_STATISTIC_REF_LOGIN + "=? AND "
                + DBHelper.KEY_STATISTIC_DATE + " =?";
        Cursor cursor = db.rawQuery(sql, new String[]{login, selectedDate});

        if (cursor.getCount() == 0){
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
        }

        int imgId = 0;
        String sign = null;
        int color = 0;

        listStatisticGeneral.removeAll(listStatisticGeneral);

        if (cursor.moveToFirst()) {
            do {
                String typeItem = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_TYPE));
                if (typeItem.equals("get")){
                    imgId = R.mipmap.ic_get;
                    sign = "+ ";
                    color = R.color.colorTextGreen;
                }
                if (typeItem.equals("spend")){
                    imgId = R.mipmap.ic_spend;
                    sign = "- ";
                    color = R.color.colorTextRed;
                }
                if (typeItem.equals("transaction")){
                    imgId = R.mipmap.ic_transaction;
                    sign = "- ";
                    color = R.color.colorTextBlack;
                }

                listStatisticGeneral.add(new ItemStatisticGeneral(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_DATE)),
//                                imgId,
                        sign + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_KOL)) + " руб.",
                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_GET)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_NAME_SPEND)), color,
                        cursor.getInt(cursor.getColumnIndex(DBHelper.KEY_STATISTIC_ID))));
            } while (cursor.moveToNext());
        }
        cursor.close();

        statisticAdapterGeneral = new StatisticAdapterGeneral(getActivity(), listStatisticGeneral);

        lvItemStatisticGeneral.setAdapter(statisticAdapterGeneral);


        Log.d("myLogs", "selectedDate = " + selectedDate);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listStatisticGeneral.removeAll(listStatisticGeneral);
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
}
