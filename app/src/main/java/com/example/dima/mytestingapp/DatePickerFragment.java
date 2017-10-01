package com.example.dima.mytestingapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Dima on 05.08.2017.
 */

public class DatePickerFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {

    //Interface created for communicating this dialog fragment events to called fragment
    public interface DatePickerDialogFragmentEvents{
        void onDateSelected(String date);
    }

    DatePickerDialogFragmentEvents dpdfe;

    public void setDatePickerDialogFragmentEvents(DatePickerDialogFragmentEvents dpdfe){
        this.dpdfe = dpdfe;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, dayOfMonth);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = sdf.format(c.getTime());
        dpdfe.onDateSelected(formattedDate); //Changed

    }
}
