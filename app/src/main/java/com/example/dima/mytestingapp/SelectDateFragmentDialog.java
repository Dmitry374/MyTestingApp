package com.example.dima.mytestingapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Dima on 18.07.2017.
 */

@SuppressLint("ValidFragment")
public class SelectDateFragmentDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText edText;

    public SelectDateFragmentDialog(View view) {
        edText = (EditText) view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }

    public void populateSetDate(int year, int month, int day) {

        String strDay = "", strMonth = "";

        if (day < 10){
//            String date = "0" + dayOfMonth + "." + (month+1) + "." + year;
            strDay = "0" + day;
        } else {
            strDay = day + "";
        }

        if (month < 10){
//            strMonth = "0" + (month+1);
            strMonth = "0" + month;
        } else {
            strMonth = month + "";
        }


        String date = strDay + "." + strMonth + "." + year;
        Log.d("myLogs", "day = " + day + " month = " + (month+1) + " year = " + year);

//        edPayBegin.setText(month+"/"+day+"/"+year);
        edText.setText(date);
    }

}
