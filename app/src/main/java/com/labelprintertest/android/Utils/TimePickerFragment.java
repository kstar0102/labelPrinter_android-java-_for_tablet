package com.labelprintertest.android.Utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private Calendar initCalendar;
    private TimePickerFragment.OnTimeSetHandler handler;

    public TimePickerFragment(Calendar calendar, OnTimeSetHandler handler) {
        this.initCalendar = calendar;
        this.handler = handler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour = initCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = initCalendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(handler != null){
            initCalendar.set(initCalendar.get(Calendar.YEAR),
                    initCalendar.get(Calendar.MONTH),
                    initCalendar.get(Calendar.DATE),
                    hourOfDay,
                    minute);
            handler.onSetTime(initCalendar.getTime());
        }
    }

    public static interface OnTimeSetHandler{
        public void onSetTime(Date newDate);
    }
}