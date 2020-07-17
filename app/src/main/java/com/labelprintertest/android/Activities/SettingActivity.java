package com.labelprintertest.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citizen.sdk.labelprint.LabelPrinter;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.Common.Config;
import com.labelprintertest.android.Common.DownTimer;
import com.labelprintertest.android.Common.LocalStorageManager;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.Dialogs.CsvExportDlg;
import com.labelprintertest.android.Dialogs.DeviceSettingDialog;
import com.labelprintertest.android.Dialogs.ReceiptDialog;
import com.labelprintertest.android.Dialogs.StartModeDialog;
import com.labelprintertest.android.Dialogs.TicketTypeSettingDialog;
import com.labelprintertest.android.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView serverIPTxt, lastSyncTxt;
    private RelativeLayout loadingLayout;
    private boolean isLoading = false;

    @Override
    public void onResume() {
        currentActivity = this;
        super.onResume();
        TextView userInfo = findViewById(R.id.userInfo);
        userInfo.setText(cm.getUserInfo());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        currentActivity = this;

        initUI();
    }

    private void initUI () {
        findViewById(R.id.startModeBtn).setOnClickListener(this);
        findViewById(R.id.deviceSettingBtn).setOnClickListener(this);
        findViewById(R.id.ticketTypeBtn).setOnClickListener(this);
        findViewById(R.id.receiptBtn).setOnClickListener(this);
        findViewById(R.id.backBtn).setOnClickListener(this);
        findViewById(R.id.exportBtn).setOnClickListener(this);

        serverIPTxt = findViewById(R.id.serverIPTxt);
        serverIPTxt.setText(Config.SERVER_IP_ADDRESS);

        lastSyncTxt = findViewById(R.id.lastSyncTxt);
        LocalStorageManager localStorageManager = new LocalStorageManager();
        String syncDate = localStorageManager.getLastSyncDate();
        if (syncDate != null) {
            lastSyncTxt.setText(syncDate);
        }

        loadingLayout = findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(View.INVISIBLE);

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM_dd(E) hh:mm", Locale.JAPANESE);

        String dateStr = sdf.format(date);

        TextView versionInfoTxt = findViewById(R.id.versionTxt);
        versionInfoTxt.setText(getResources().getString(R.string.build_date) + " " + dateStr);
    }

    private void checkingPrintState(final LabelPrinter printer, final int value, final String only) {
        isLoading = true;
        loadingLayout.setVisibility(View.VISIBLE);
        final DownTimer myTimer = new DownTimer(1, 500);
        myTimer.setOnFinishListener(new DownTimer.OnFinishListener() {

            @Override
            public void onFinish() {
                if (printer != null) {
                    if (printer.getPrinting() == 1) { // printing now...
                        if (cm.hasPrintingErr) {
                            loadingLayout.setVisibility(View.INVISIBLE);
                            myTimer.initialize();
                            isLoading = false;
                            Common.cm.showAlertDlg(currentActivity.getResources().getString(R.string.printing_err_title),
                                    currentActivity.getResources().getString(R.string.printing_err_msg), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }, null);
                            return;
                        }
                    }else { // completed print
                        cm.hasPrintingErr = false;
                        loadingLayout.setVisibility(View.INVISIBLE);
                        DbHelper dbHelper = new DbHelper(currentActivity);
                        Queries query = new Queries(null, dbHelper);
                        query.addReceiptInfoWithData(value, only, 1);
                        myTimer.initialize();
                        isLoading = false;
                    }
                }else {
                    cm.hasPrintingErr = false;
                    loadingLayout.setVisibility(View.INVISIBLE);
                    myTimer.initialize();
                    isLoading = false;
                }
            }

            @Override
            public void onTick(int progressValue) {

            }
        });
        myTimer.start();
    }

    @Override
    public void onClick(View v) {
        if (isLoading) return;
        switch (v.getId()) {
            case R.id.startModeBtn:
                StartModeDialog startModeDialog = new StartModeDialog(currentActivity);
                startModeDialog.show();
                break;
            case R.id.deviceSettingBtn:
                DeviceSettingDialog deviceSettingDialog = new DeviceSettingDialog(currentActivity, new DeviceSettingDialog.DeviceChangeListner() {
                    @Override
                    public void OnChangedDevice() {
                        TextView userInfo = findViewById(R.id.userInfo);
                        userInfo.setText(cm.getUserInfo());
                    }
                });
                deviceSettingDialog.show();
                break;
            case R.id.ticketTypeBtn:
                TicketTypeSettingDialog ticketTypeSettingDialog = new TicketTypeSettingDialog(currentActivity);
                ticketTypeSettingDialog.show();
                break;
            case R.id.receiptBtn:
                ReceiptDialog receiptDialog = new ReceiptDialog(currentActivity, new ReceiptDialog.ReceiptPrinterListner() {
                    @Override
                    public void OnReceiptBtnClicked(LabelPrinter printer, int value, String only) {
                        //test
//                        DbHelper dbHelper = new DbHelper(currentActivity);
//                        Queries query = new Queries(null, dbHelper);
//                        query.addReceiptInfoWithData(value, only, 1);

                        checkingPrintState(printer, value, only);
                    }
                });
                receiptDialog.show();
                break;
            case R.id.backBtn:
                finish();
                break;
            case R.id.exportBtn:
                CsvExportDlg csvExportDlg = new CsvExportDlg(currentActivity);
                csvExportDlg.show();
        }
    }
}
