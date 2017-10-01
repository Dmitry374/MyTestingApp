package com.example.dima.mytestingapp.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dima.mytestingapp.R;
import com.example.dima.mytestingapp.SelectDateFragmentDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentKreditCalc.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentKreditCalc#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentKreditCalc extends Fragment /*android.app.Fragment*/ implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Spinner spinnerKred;  // Валюта
    Spinner spinnerTypePay;  //  Тип кредита

    TextView textView5, textView11, textView13;

    Button btnCalk;

    TextView tvKredRes;
    TextView tvTotal;  //  Всего будет выплачено
    TextView tvOverpayment;  //  Переплата по кредиту
    TextView tvDate;  //  Дата выплаты кредита

    EditText edCredSize;  //  Размер кредита
    EditText edTimeKred;  //  Срок кредита
    EditText edInterestRate;  //  Процентная ставка по кредиту
    EditText edPayBegin;  //  Начало выплаты кредита

    String typePay;
    double sizeCredit;  //  Размер кредита (значение)
    double interestRate;  //  Процентная ставка по кредиту (значение)
    int timeCredit; // Срок кредита (значение)
    double total;  //  Всего будет выплачено (значение)
    double overpayment;  //  Переплата по кредиту (значение)

    double monthPay;  //  Сумма ежемесячног платежа
    int monthBegin;  //  Начало платежа - месяц
    int yearBegin;  //  Начало платежа - год
    int year, month;  // Окончание выплаты (значения)
    String nameMonth;  //  Название месяца

    String typeOfValuta;  //  Тип валюты

    int DIALOG_DATE = 1;
    int myYear = 2011;
    int myMonth = 02;
    int myDay = 03;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentKreditCalc() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentKreditCalc.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentKreditCalc newInstance(String param1, String param2) {
        FragmentKreditCalc fragment = new FragmentKreditCalc();
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

//        textView5 = (TextView) textView5.findViewById(R.id.textView5);
//        textView11 = (TextView) textView11.findViewById(R.id.textView11);
//        textView13 = (TextView) textView13.findViewById(R.id.textView13);
//
//        tvKredRes = (TextView) tvKredRes.findViewById(R.id.tvKredRes);
//        tvTotal = (TextView) tvTotal.findViewById(R.id.tvTotal);
//        tvOverpayment = (TextView) tvOverpayment.findViewById(R.id.tvOverpayment);
//        tvDate = (TextView) tvDate.findViewById(R.id.tvDate);
//
//        edCredSize = (EditText) edCredSize.findViewById(R.id.edCredSize);
//        edTimeKred = (EditText) edTimeKred.findViewById(R.id.edTimeKred);
//        edInterestRate = (EditText) edInterestRate.findViewById(R.id.edInterestRate);
//        edPayBegin = (EditText) edPayBegin.findViewById(R.id.edPayBegin);
//        edPayBegin.setOnClickListener(this);
//
//        spinnerKred = (Spinner) spinnerKred.findViewById(R.id.spinnerKred);
//        spinnerTypePay = (Spinner) spinnerTypePay.findViewById(R.id.spinnerTypePay);
//
//
//        btnCalk = (Button) btnCalk.findViewById(R.id.btnCalk);
//        btnCalk.setOnClickListener(this);

//        //        ------------- Обработчики Spinner -----------------------------------------------
//        spinnerKred.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("myLogs", String.valueOf(parent.getItemAtPosition(position)));
//                typeOfValuta = String.valueOf(parent.getItemAtPosition(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        spinnerTypePay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Log.d("myLogs", String.valueOf(parent.getItemAtPosition(position)));
//                typePay = String.valueOf(parent.getItemAtPosition(position));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
////        -----------------------------------------------------------------------------------------


//        //        Отображается диалог при нажатии на поле Дата
//        edPayBegin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (edPayBegin.hasFocus()){
//                    DateDialog dialog = new DateDialog(v);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    dialog.show(ft, "DialogPicker");
//                }
//            }
//        });


    }

//    --------------- Методы для диалога даты -----------------------------------------

//    Ввод даты начала платежа как в RegisterActivity и расчет !!!

//    protected Dialog onCreateDialog(int id){
//        if (id == DIALOG_DATE){
//            DatePickerDialog tpd = new DatePickerDialog(getActivity(), myCallBack,
//                    myYear, myMonth, myDay);
//            return tpd;
//        }
//        return this.onCreateDialog(id);
//    }
//
//    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker view, int year, int month,
//                              int dayOfMonth) {
//            myYear = year;
//            myMonth = month;
//            myDay = dayOfMonth;
//            edPayBegin.setText(myDay + "." + (myMonth + 1) + "." + myYear);
//        }
//    };
//    ---------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        Имя Toolbar
        Activity activity = this.getActivity();
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        if (toolbar != null) {
            activity.setTitle("Кредитный калькулятор");
        }

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_kredit_calc, container, false);

        View v = inflater.inflate(R.layout.fragment_kredit_calc, null);

        textView5 = (TextView) v.findViewById(R.id.textView5);
        textView11 = (TextView) v.findViewById(R.id.textView11);
        textView13 = (TextView) v.findViewById(R.id.textView13);

        tvKredRes = (TextView) v.findViewById(R.id.tvKredRes);
        tvTotal = (TextView) v.findViewById(R.id.tvTotal);
        tvOverpayment = (TextView) v.findViewById(R.id.tvOverpayment);
        tvDate = (TextView) v.findViewById(R.id.tvDate);

        edCredSize = (EditText) v.findViewById(R.id.edCredSize);
        edTimeKred = (EditText) v.findViewById(R.id.edTimeKred);
        edInterestRate = (EditText) v.findViewById(R.id.edInterestRate);

        edPayBegin = (EditText) v.findViewById(R.id.edPayBegin);
        edPayBegin.setOnClickListener(this);

//        Отключение редактирования поля edPayBegin
        edPayBegin.setInputType(InputType.TYPE_NULL);

        spinnerKred = (Spinner) v.findViewById(R.id.spinnerKred);
        spinnerTypePay = (Spinner) v.findViewById(R.id.spinnerTypePay);

        btnCalk = (Button) v.findViewById(R.id.btnCalk);
        btnCalk.setOnClickListener(this);

//        edPayBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (edPayBegin.hasFocus()){
//                    DateDialog dialog = new DateDialog(v);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    dialog.show(ft, "DialogPicker");
//                }
//            }
//        });

        edPayBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().showDialog(DIALOG_DATE);

                if (edPayBegin.hasFocus()){
//                    DialogFragment newFragment = new SelectDateFragmentDialog(v);
//                    newFragment.show(getFragmentManager(), "DatePicker");

                    DialogFragment newFragment = new SelectDateFragmentDialog(v);
                    newFragment.show(getFragmentManager(), "DatePicker");

                }
            }
        });





//        ------------- Обработчики Spinner -----------------------------------------------
        spinnerKred.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("myLogs", String.valueOf(parent.getItemAtPosition(position)));
                typeOfValuta = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTypePay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("myLogs", String.valueOf(parent.getItemAtPosition(position)));
                typePay = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        -----------------------------------------------------------------------------------------

        //        Отображается диалог при нажатии на поле Дата
//        edPayBegin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (edPayBegin.hasFocus()){
//                    DateDialog dialog = new DateDialog(v);
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    dialog.show(ft, "DialogPicker");
//                }
//            }
//        });

        edPayBegin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (edPayBegin.hasFocus()){
                    DialogFragment newFragment = new SelectDateFragmentDialog(v);
                    newFragment.show(getFragmentManager(), "DatePicker");
                }
            }
        });

        return v;
    }

//    protected Dialog onCreateDialog(int id){
//        if (id == DIALOG_DATE){
//            DatePickerDialog tpd = new DatePickerDialog(getActivity(), myCallBack,
//                    myYear, myMonth, myDay);
//            return tpd;
//        }
//        return super.onCreateDialog(id);
//    }
//
//    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker view, int year, int month,
//                              int dayOfMonth) {
//            myYear = year;
//            myMonth = month;
//            myDay = dayOfMonth;
//            tvDate.setText("Today is " + myDay + "/" + (myMonth + 1) + "/" + myYear);
//        }
//    };

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
        switch (v.getId()) {
            case R.id.btnCalk:

//                Intent intent = new Intent(getActivity(), UserActivity.class);   //  Тестировал
//                intent.putExtra("name_fragment", "fkrditcalk");
//                startActivity(intent);

                if (edCredSize.getText().toString().equals("") || edTimeKred.getText().toString().equals("")
                        || edInterestRate.getText().toString().equals("") || edPayBegin.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getActivity(), "Заполние все поля !!!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 320);
                    toast.show();
                } else {
                    tvKredRes.setVisibility(View.VISIBLE);
                    tvTotal.setVisibility(View.VISIBLE);
                    tvOverpayment.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.VISIBLE);

                    textView5.setVisibility(View.VISIBLE);
                    textView11.setVisibility(View.VISIBLE);
                    textView13.setVisibility(View.VISIBLE);


                    try {
                        sizeCredit = Double.parseDouble(edCredSize.getText().toString());
                        interestRate = Double.parseDouble(edInterestRate.getText().toString());
                        timeCredit = Integer.parseInt(edTimeKred.getText().toString());

                    } catch (NumberFormatException e) {
                        Toast toast = Toast.makeText(getActivity(), "Заполние все поля !!!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 320);
                        toast.show();
                    }

//                 Расчет месячных выплат
//                double a = 1 + (interestRate/100)/12;
//                double b = (timeCredit/12)*12;
//                double mp = Math.pow(a,b);
//                Log.d("myLogs", "Math.pow() = " + mp + " a = " + a + " b = " + b + " timeCredit = " + (double) timeCredit/12);

                    monthPay = (sizeCredit * (interestRate / 100) / 12) / (1 - 1 / Math.pow((1 + (interestRate / 100) / 12), ((double) timeCredit / 12) * 12));
//                Log.d("myLogs", "1 - " + String.valueOf((sizeCredit * (interestRate/100)/12)));
//                Log.d("myLogs", "2 - " + String.valueOf(( Math.pow((1 + (interestRate/100)/12), (timeCredit/12)*12))));
                    monthPay = Math.rint(100.0 * (monthPay)) / 100.0;

//                 Всего будет выплачено
                    total = monthPay * timeCredit;
                    total = Math.rint(100.0 * (total)) / 100.0;

//                Переплата по кредиту
                    overpayment = total - sizeCredit;
                    overpayment = Math.rint(100.0 * (overpayment)) / 100.0;


                    if (typePay.equals("Аннуитетный")) {
//                    Расчет месячных выплат
                        tvKredRes.setText(Double.toString(monthPay) + "  " + typeOfValuta);

//                    Всего будет выплачено
                        tvTotal.setText(Double.toString(total) + "  " + typeOfValuta);

//                    Переплата по кредиту
                        tvOverpayment.setText(Double.toString(overpayment) + "  " + typeOfValuta);
                    }

                    if (typePay.equals("Дифференцированный")) {

                        sizeCredit = Double.parseDouble(edCredSize.getText().toString());
                        interestRate = Double.parseDouble(edInterestRate.getText().toString());
                        timeCredit = Integer.parseInt(edTimeKred.getText().toString());

//                    Ежемесячная сумма погашения основного долга
                        double sumPogDebt = sizeCredit / (((double) timeCredit / 12) * 12);
                        sumPogDebt = Math.rint(100.0 * (sumPogDebt)) / 100.0;

//                    Процентный платёж для первого месяца
                        double platFirstMonth = (sizeCredit * (interestRate / 100)) / 12;
                        platFirstMonth = Math.rint(100.0 * (platFirstMonth)) / 100.0;

//                    Сумма платежа к погашению за первый месяц
                        double sumPlatFirst = sumPogDebt + platFirstMonth;
                        sumPlatFirst = Math.rint(100.0 * (sumPlatFirst)) / 100.0;

//                    Процентный платёж для последнего месяца
                        double sumPlatLast = (sumPogDebt - (double) timeCredit / 12 * sumPogDebt / (((double) timeCredit / 12) * 12)) * (interestRate / 100) / 12;
                        sumPlatLast = Math.rint(100.0 * (sumPlatLast)) / 100.0;

                        double sumPlatLastKred = sumPogDebt + sumPlatLast;
                        sumPlatLastKred = Math.rint(100.0 * (sumPlatLastKred)) / 100.0;


                        tvKredRes.setText(Double.toString(sumPlatFirst) + " ... " + Double.toString(sumPlatLastKred) + " " + typeOfValuta);

                        tvTotal.setText(Double.toString(total) + " " + typeOfValuta);

                        tvOverpayment.setText(Double.toString(overpayment) + " " + typeOfValuta);
                    }

//                Извлечение начала даты платежа
                    String payBegin = edPayBegin.getText().toString();

                    int day = Integer.parseInt(payBegin.substring(0, 2));
                    int month = Integer.parseInt(payBegin.substring(3, 5));
                    month--;
                    int year = Integer.parseInt(payBegin.substring(6, 10));


                    Calendar c = new GregorianCalendar(year, month, day);

                    c.add(Calendar.MONTH, timeCredit);

                    int nYear = c.get(Calendar.YEAR);
                    int nMonth = c.get(Calendar.MONTH);
                    int nDay = c.get(Calendar.DAY_OF_MONTH);


                    String tDay = "", tMnth = "";
                    if (nDay < 10) {
                        tDay = "0" + nDay;
                    } else {
                        tDay = nDay + "";
                    }

                    if (nMonth + 1 < 10) {
                        tMnth = "0" + (nMonth + 1);
                    } else {
                        tMnth = (nMonth + 1) + "";
                    }


//                tvDate.setText("Дата выплаты кредита:   " + nDay + "." + (nMonth+1) + "." + nYear);
                    tvDate.setText("Дата выплаты кредита:   " + tDay + "." + tMnth + "." + nYear);

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
}
