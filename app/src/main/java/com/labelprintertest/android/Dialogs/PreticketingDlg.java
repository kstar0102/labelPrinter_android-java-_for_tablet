package com.labelprintertest.android.Dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.citizen.sdk.labelprint.LabelPrinter;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.Common.DownTimer;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.Models.TicketInfo;
import com.labelprintertest.android.Models.TicketModel;
import com.labelprintertest.android.Models.TicketType;
import com.labelprintertest.android.PrinterManager.PrinterManager;
import com.labelprintertest.android.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;
import static io.fabric.sdk.android.Fabric.TAG;

public class PreticketingDlg extends Dialog{
    DatePickerDialog picker;
    EditText eText;
    Button btnGet;
    private Calendar selectDate;
    private ArrayList<TicketModel> selectList;
    private int paymentType = 1;
    private ArrayList<TicketInfo> ticketingList;
    private ArrayList<TicketType> tabList;
    private int selectedPayType;
    public PreticketingDlg(@NonNull Context context) {
        super(context);
        setContentView(R.layout.preticketing_dialog);
        eText=findViewById(R.id.preDate);
        eText.setInputType(InputType.TYPE_NULL);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE);
        String dateStr = sdf.format(new Date());
        eText.setHint(dateStr);
        eText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                selectDate = cldr;
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        btnGet= findViewById(R.id.btn_preticket);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectDate == null){
                    Toast.makeText(getContext(), "日付を選択してください。", Toast.LENGTH_LONG).show();
                    dismiss();
                    return;
                }
                DbHelper dbHelper = new DbHelper(getOwnerActivity());
                Queries query = new Queries(null, dbHelper);
                selectList = query.getPreTicketModels(selectDate);
                if(selectList == null){
                    Common.cm.showAlertDlg(currentActivity.getResources().getString(R.string.no_xml_title),
                            currentActivity.getResources().getString(R.string.select_print_msg), new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }, null);
                    return;
                }else {
                    PrinterManager manager = new PrinterManager();
                    manager.selectprinterStart(selectList, 0, "", paymentType);
                }

            }
        });
    }
}
