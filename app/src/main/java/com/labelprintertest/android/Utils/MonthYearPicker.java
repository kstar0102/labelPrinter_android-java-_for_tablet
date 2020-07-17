package com.labelprintertest.android.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.NumberPicker;

import com.labelprintertest.android.R;

import java.util.Calendar;

public class MonthYearPicker {

    private static final int MIN_YEAR = 1970;

    private static final int MAX_YEAR = 2099;

    private static final String[] PICKER_DISPLAY_MONTHS_NAMES = new String[] { "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月",
            "11月", "12月" };

    private static final String[] MONTHS = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };

    private View view;
    private Activity activity;
    private AlertDialog.Builder builder;
    private AlertDialog pickerDialog;
    private boolean build = false;
    private NumberPicker monthNumberPicker;
    private NumberPicker yearNumberPicker;

    /**
     * Instantiates a new month year picker.
     *
     * @param activity
     *            the activity
     */
    public MonthYearPicker(Activity activity) {
        this.activity = activity;
        this.view = activity.getLayoutInflater().inflate(R.layout.month_year_picker_view, null);
    }

    /**
     * Builds the month year alert dialog.
     *
     * @param positiveButtonListener
     *            the positive listener
     * @param negativeButtonListener
     *            the negative listener
     */
    public void build(DialogInterface.OnClickListener positiveButtonListener, DialogInterface.OnClickListener negativeButtonListener) {
        this.build(-1, -1, positiveButtonListener, negativeButtonListener);
    }

    private int currentYear;

    private int currentMonth;

    /**
     * Builds the month year alert dialog.
     *
     * @param selectedMonth
     *            the selected month 0 to 11 (sets current moth if invalid
     *            value)
     * @param selectedYear
     *            the selected year 1970 to 2099 (sets current year if invalid
     *            value)
     * @param positiveButtonListener
     *            the positive listener
     * @param negativeButtonListener
     *            the negative listener
     */
    public void build(int selectedMonth, int selectedYear, DialogInterface.OnClickListener positiveButtonListener,
                      DialogInterface.OnClickListener negativeButtonListener) {

        final Calendar instance = Calendar.getInstance();
        currentMonth = instance.get(Calendar.MONTH);
        currentYear = instance.get(Calendar.YEAR);

        if (selectedMonth > 11 || selectedMonth < -1) {
            selectedMonth = currentMonth;
        }

        if (selectedYear < MIN_YEAR || selectedYear > MAX_YEAR) {
            selectedYear = currentYear;
        }

        if (selectedMonth == -1) {
            selectedMonth = currentMonth;
        }

        if (selectedYear == -1) {
            selectedYear = currentYear;
        }

        builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        monthNumberPicker = (NumberPicker) view.findViewById(R.id.monthNumberPicker);
        monthNumberPicker.setDisplayedValues(PICKER_DISPLAY_MONTHS_NAMES);

        monthNumberPicker.setMinValue(0);
        monthNumberPicker.setMaxValue(MONTHS.length - 1);

        yearNumberPicker = (NumberPicker) view.findViewById(R.id.yearNumberPicker);
        yearNumberPicker.setMinValue(MIN_YEAR);
        yearNumberPicker.setMaxValue(MAX_YEAR);

        monthNumberPicker.setValue(selectedMonth);
        yearNumberPicker.setValue(selectedYear);

        monthNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        builder.setPositiveButton(activity.getString(R.string.Select), positiveButtonListener);
        builder.setNegativeButton(activity.getString(R.string.Cancel), negativeButtonListener);
        build = true;
        pickerDialog = builder.create();

    }

    /**
     * Show month year picker dialog.
     */
    public void show() {
        if (build) {
            pickerDialog.show();
        } else {
            throw new IllegalStateException("Build picker before use");
        }
    }

    /**
     * Gets the selected month.
     *
     * @return the selected month
     */
    public int getSelectedMonth() {
        return monthNumberPicker.getValue();
    }

    /**
     * Gets the selected year.
     *
     * @return the selected year
     */
    public int getSelectedYear() {
        return yearNumberPicker.getValue();
    }
}

