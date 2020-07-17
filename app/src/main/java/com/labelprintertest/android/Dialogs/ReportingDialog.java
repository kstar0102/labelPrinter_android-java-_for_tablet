package com.labelprintertest.android.Dialogs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.labelprintertest.android.Activities.SumByDayActivity;
import com.labelprintertest.android.Activities.SumByTableAvtivity;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.Common.LocalStorageManager;
import com.labelprintertest.android.DBManager.APIManager;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

public class ReportingDialog extends Dialog implements View.OnClickListener {

    private TextView dateTxt, infoTxt;
    private Button endCancelBtn, endByDayBtn, daySumSendBtn, daySumCancelBtn, daySumPrintBtn, SumPrintBtn;
    private Calendar nowDate;
    private boolean isByEndSum = false;
    private boolean isDaySumSend = false;
    boolean ischeckdialog = false;
    private LinearLayout ReportLayout;
    public RelativeLayout mainloading;

    public ReportingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.reporting_dialog);
        ReportLayout = findViewById(R.id.maindialog);

        mainloading = findViewById(R.id.mainloadingLayout);

        if(ischeckdialog == true){
            dismiss();
        }
        initUI();
    }

    private void initUI() {
        endByDayBtn = findViewById(R.id.endByDayBtn);
        endByDayBtn.setOnClickListener(this);
        endCancelBtn = findViewById(R.id.endCancelBtn);
        endCancelBtn.setOnClickListener(this);
        daySumSendBtn = findViewById(R.id.daySumSendBtn);
        daySumSendBtn.setOnClickListener(this);
        daySumCancelBtn = findViewById(R.id.daySumCancelBtn);
        daySumCancelBtn.setOnClickListener(this);
        daySumPrintBtn = findViewById(R.id.daySumPrintBtn);
        daySumPrintBtn.setOnClickListener(this);
        SumPrintBtn = findViewById(R.id.SumPrintBtn);
        SumPrintBtn.setOnClickListener(this);


        nowDate = Calendar.getInstance();
        dateTxt = findViewById(R.id.dateTxt);
        dateTxt.setOnClickListener(this);
        dateTxt.setText(nowDate.get(Calendar.YEAR) + "年 " + (nowDate.get(Calendar.MONTH)+1) + "月 " + nowDate.get(Calendar.DATE) + "日");

        infoTxt = findViewById(R.id.infoTxt);

        TextView userInfo = findViewById(R.id.userInfo);
        userInfo.setText(cm.getUserInfo());

        infoTxt.setText(Common.endInfoStr);
        if(SumByDayActivity.checkingpring == true){
            infoTxt.setText("締めデータ転送済");
        }
        SimpleDateFormat formatter= new SimpleDateFormat("HH");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));
        if(Integer.parseInt(formatter.format(date)) > 23){
            SumByDayActivity.checkingpring = false;
        }
    }

    private void checkSettlementState(Calendar calendar) {

    }

    /**
     *
     * 日時を設定するデートピッカーを表示する
     *
     */
    private void showDatePicker(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dlg = new DatePickerDialog(cm.currentActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(y, m, d);
                nowDate = calendar;
                dateTxt.setText(nowDate.get(Calendar.YEAR) + "年 " + (nowDate.get(Calendar.MONTH)+1) + "月 " + nowDate.get(Calendar.DATE) + "日");
            }
        }, year, month, day);
        dlg.show();
    }

    private void showInfo() {
        Date date = nowDate.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.JAPANESE);
        String dateStr = sdf.format(date);

        String byEndStr = " " + currentActivity.getResources().getString(R.string.btn_by_end_day) + currentActivity.getResources().getString(R.string.lb_completed);

        if (!isByEndSum) byEndStr = "";
        String daySendStr = " " + currentActivity.getResources().getString(R.string.btn_day_sum_send) + currentActivity.getResources().getString(R.string.lb_completed);
        if (!isDaySumSend) daySendStr = "";

        if (byEndStr.equals("") && daySendStr.equals("")) {
            Common.endInfoStr = "";
        }else {
            Common.endInfoStr = dateStr + byEndStr + daySendStr;
        }
        infoTxt.setText(Common.endInfoStr);
    }

    private boolean saveByDayEndData () {
        boolean isDone = true;
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        isDone = query.saveByDayEndData(nowDate);
        return isDone;
    }

    private boolean sendDayEndSumData (){
        boolean isDone = true;
        mainloading.setVisibility(VISIBLE);
        APIManager apiManager = new APIManager();
        if (cm.hasInternetConnection() && apiManager.connectionclass() != null) {
            APIManager manager = new APIManager();
            isDone = manager.syncToServer(nowDate);
            if (isDone) {
                LocalStorageManager localStorageManager = new LocalStorageManager();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.JAPANESE);
                localStorageManager.saveLastSyncDate(sdf.format(Calendar.getInstance().getTime()));
            }
       }
        else {
            isDone = false;
        }
        mainloading.setVisibility(GONE);
        return isDone;
    }

    private boolean cancelDayEndSumData () {
        boolean isDone = true;
        mainloading.setVisibility(VISIBLE);
        APIManager manager = new APIManager();
        if (cm.hasInternetConnection() && manager.connectionclass() != null) {
            isDone = manager.unSyncToServer(nowDate);
        }else {
            isDone = false;
        }
        mainloading.setVisibility(GONE);
        return isDone;
    }

    private boolean deleteByDayEndData () {
        boolean isDone = true;
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        isDone = query.deleteByDayEndData(nowDate);
        return isDone;
    }

    @Override
    public void onClick(final View v) {
        v.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                v.setEnabled(true);
            }
        }, 1000L);

        switch (v.getId()) {
            case R.id.endCancelBtn:
                isByEndSum = !deleteByDayEndData();
                showInfo();
                break;
            case R.id.endByDayBtn:
                isByEndSum = saveByDayEndData();
                showInfo();
                break;
            case R.id.daySumSendBtn:
                isDaySumSend = sendDayEndSumData();
                showInfo();
                break;
            case R.id.daySumCancelBtn:
                isDaySumSend = !cancelDayEndSumData();
                showInfo();
                infoTxt.setText("日次締済み締データ取消転送済み!");
                break;
            case R.id.daySumPrintBtn:
                Intent intent = new Intent(currentActivity, SumByDayActivity.class);
                nowDate = Calendar.getInstance();
                intent.putExtra("date", nowDate);
                currentActivity.startActivity(intent);
                dismiss();
                break;
            case R.id.dateTxt:
                showDatePicker(nowDate);
                break;
//            case R.id.daySumPrintBtn:
//                Intent sumintent = new Intent(currentActivity, SumByTableAvtivity.class);
//                sumintent.putExtra("date", nowDate);
//                currentActivity.startActivity(sumintent);
//                dismiss();
//                break;
        }
    }
}
