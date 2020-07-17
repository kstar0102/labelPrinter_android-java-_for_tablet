package com.labelprintertest.android.Utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Calendar initCalendar;
    private OnDateSetHandler handler;

    public DatePickerFragment(Calendar calendar, OnDateSetHandler handler) {
        this.initCalendar = calendar;
        this.handler = handler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = initCalendar.get(Calendar.YEAR);
        int month = initCalendar.get(Calendar.MONTH);
        int day = initCalendar.get(Calendar.DATE);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(handler != null){
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            handler.onSetDate(calendar.getTime());
        }
    }

    public static interface OnDateSetHandler{
        public void onSetDate(Date newDate);
    }
}
