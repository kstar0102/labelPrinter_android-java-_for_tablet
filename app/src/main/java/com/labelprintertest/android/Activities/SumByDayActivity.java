package com.labelprintertest.android.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citizen.sdk.labelprint.LabelPrinter;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.Common.DownTimer;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.PrinterManager.PrinterManager;
import com.labelprintertest.android.R;
import com.labelprintertest.android.Views.SettlementMainItemView;
import com.labelprintertest.android.Views.SettlementSubItemView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

public class SumByDayActivity extends AppCompatActivity {

    private LinearLayout mainListLayout;
    private LinearLayout subListLayout;
        private RelativeLayout loadingLayout;

    private ArrayList<HashMap> mainList;
    private ArrayList<HashMap> subList;
    private ArrayList<HashMap> list;
    private Calendar nowDate;
    private String titleStr;
    private boolean isLoading = false;
    private boolean datechecking = false;
    static public boolean checkingpring = false;

    @Override
    public void onResume() {
        currentActivity = this;
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = this;
        setContentView(R.layout.activity_sum_by_day);

        nowDate = (Calendar) getIntent().getExtras().getSerializable("date");

        SettlementMainItemView mainHeaderView = findViewById(R.id.mainHeaderView);
        HashMap map = new HashMap();
        map.put("code", getResources().getString(R.string.lb_code));
        map.put("type", getResources().getString(R.string.lb_type));
        map.put("unit", getResources().getString(R.string.lb_unit));
        map.put("sell", getResources().getString(R.string.lb_sell));
        map.put("refund", getResources().getString(R.string.lb_refund));
        map.put("total", getResources().getString(R.string.lb_total));
        map.put("price", getResources().getString(R.string.lb_price));
        mainHeaderView.initHeaderUI(map);

//        SettlementSubItemView subHeaderView = findViewById(R.id.subHeaderView);
//        map.clear();
//        map.put("title", getResources().getString(R.string.lb_code));
//        map.put("num", getResources().getString(R.string.lb_type));
//        map.put("price", getResources().getString(R.string.lb_unit));

        mainListLayout = findViewById(R.id.mainListLayout);
        subListLayout = findViewById(R.id.subListLayout);

        TextView settlementTitle = findViewById(R.id.settlementTitle);
        String title = getResources().getString(R.string.lb_settlement_title);

        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        String deviceInfo = "";
        HashMap data = query.getDeviceInfo();
        if (data != null) {
            final String[] items = currentActivity.getResources().getStringArray(R.array.device_place);
            deviceInfo = items[cm.parseInteger((String) data.get("hanbaibasho")) - 1];
        }
        Date date = nowDate.getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd(E) hh:mm", Locale.JAPANESE);
        String dateStr = sdf.format(date);

        titleStr = title + " " + dateStr + " " + deviceInfo;
        settlementTitle.setText(titleStr);

        TextView userInfo = findViewById(R.id.userInfo);
        userInfo.setText(cm.getUserInfo());

        mainList = new ArrayList<>();
        subList = new ArrayList<>();

        findViewById(R.id.printBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainList.size() > 0 || subList.size() > 0) {
                    PrinterManager manager = new PrinterManager();
                    checkingPrintState(manager.settlementPrinterStart(titleStr, mainList, subList));
                    checkingpring = true;
                }else {
                    finish();
                }
            }
        });

        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        showMainList();
        showSubList();

        loadingLayout = findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(View.INVISIBLE);
    }

    private void showMainList() {
        DbHelper dbHelper = new DbHelper(this);
        Queries query = new Queries(null, dbHelper);

        mainList = filterByRefund(query.gettableDataByGroup(nowDate, nowDate));

        if (mainList.size() > 0) {
            mainListLayout.removeAllViews();
            int totalSellNum = 0;
            int totalRefundNum = 0;
            int totalSum = 0;
            for (HashMap data : mainList) {
                SettlementMainItemView view = new SettlementMainItemView(currentActivity);
                int unit = (int) data.get("unit");
                int sellNum = (int) data.get("sell");
                int refundNum = (int) data.get("refund");
                int sum = unit * (sellNum + refundNum);
                totalRefundNum += refundNum;
                totalSellNum += sellNum;
                totalSum += sum;
                view.initUI(data);
                mainListLayout.addView(view);
            }
            SettlementMainItemView sumView = new SettlementMainItemView(currentActivity);
            HashMap map = new HashMap();
            map.put("code", "");
            map.put("type", getResources().getString(R.string.lb_total));
            map.put("unit", "");
            map.put("sell", totalSellNum);
            map.put("refund", totalRefundNum);
            map.put("total", totalSellNum + totalRefundNum);
            map.put("price", totalSum);
            sumView.initUI(map);
            mainListLayout.addView(sumView);
            mainList.add(map);
        }
    }
    private ArrayList<HashMap> filterByRefund(ArrayList<HashMap> preList) {
        ArrayList<HashMap> list = new ArrayList<>();
        int ind = 0;
        for (HashMap data : preList) {
            if (ind == 0) {
                HashMap map = new HashMap();
                map.put("code", data.get("tickettypename"));
                map.put("type", data.get("meisho"));
                map.put("uriagekb", data.get("uriagekb"));
                map.put("haraimodoshikb", data.get("haraimodoshikb"));
                map.put("suryo", data.get("suryo"));
                map.put("kingaku", data.get("kingaku"));
                map.put("shohizei", data.get("shohizei"));
                int unit = (int) data.get("hanbaitanka");
                int num = (int) data.get("uriagesuryo");
                map.put("unit", unit);
                if (data.get("haraimodoshikb").equals("0")) {
                    map.put("sell", num);
                    map.put("refund", 0);
                    map.put("total", num);
                    map.put("price", unit * num);
                }else {
                    map.put("sell", 0);
                    map.put("refund", -num);
                    map.put("total", -num);
                    map.put("price", -unit * num);
                }
                list.add(map);
            }else {
                HashMap preMap = preList.get(ind-1);
                HashMap data2 = list.get(list.size()-1);
                if (data.get("meisho").equals(preMap.get("meisho")) && data.get("ticketid").equals(preMap.get("ticketid"))) {
                    int unit = (int) data.get("hanbaitanka");
                    int num = (int) data.get("uriagesuryo");
                    if (data.get("haraimodoshikb").equals("0")) {
                        data2.put("sell", num + (int) data2.get("sell"));
                        data2.put("total", num + (int) data2.get("total"));
                    }else {
                        data2.put("refund", -num + (int) data2.get("refund"));
                        data2.put("total", -num + (int) data2.get("total"));
                    }
                    data2.put("price", unit * (int) data2.get("total"));
                    list.set(list.size()-1, data2);
                }else {
                    HashMap map = new HashMap();
                    map.put("code", data.get("tickettypename"));
                    map.put("type", data.get("meisho"));
                    map.put("uriagekb", data.get("uriagekb"));
                    map.put("haraimodoshikb", data.get("haraimodoshikb"));
                    map.put("suryo", data.get("suryo"));
                    map.put("kingaku", data.get("kingaku"));
                    map.put("shohizei", data.get("shohizei"));
                    int unit = (int) data.get("hanbaitanka");
                    int num = (int) data.get("uriagesuryo");
                    map.put("unit", unit);
                    if (data.get("haraimodoshikb").equals("0")) {
                        map.put("sell", num);
                        map.put("refund", 0);
                        map.put("total", num);
                        map.put("price", unit * num);
                    }else {
                        map.put("sell", 0);
                        map.put("refund", -num);
                        map.put("total", -num);
                        map.put("price", -unit * num);
                    }
                    list.add(map);
                }
            }
            ind ++;
        }
        return list;
    }

    private void showSubList() {
        DbHelper dbHelper = new DbHelper(this);
        Queries query = new Queries(null, dbHelper);

        ArrayList<HashMap> list = query.getSettabletlementData(nowDate, nowDate);
        ArrayList<HashMap> addsublist = filterByRefund(query.gettableDataByGroup(nowDate, nowDate));
        ArrayList<String> groupkeyList = new ArrayList<String>();
        ArrayList<ArrayList<HashMap>> grouplist = new ArrayList<ArrayList<HashMap>>();

        try {
            for(int i = 0; i < addsublist.size(); i++){
                String keydate = (String) addsublist.get(i).get("code");
                if(!groupkeyList.contains(keydate)){
                    groupkeyList.add(keydate);
                }else{
                    continue;
                }
            }
        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(SumByDayActivity.this).create();
            alertDialog.setTitle("Alert groupKeyList");
            alertDialog.setMessage("The groupkey list is error:" + e.getMessage() );
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
//            return null;
        }
        try {
            if(groupkeyList != null) {
                for (int i = 0; i < groupkeyList.size(); i++) {
                    ArrayList<HashMap> BinData = new ArrayList<HashMap>();
                    for (int j = 0; j < addsublist.size(); j++) {
                        if (groupkeyList.get(i).equals(addsublist.get(j).get("code"))) {
                            BinData.add(addsublist.get(j));
                        }
                    }
                    grouplist.add(BinData);
                }
            }
        } catch (Exception e) {
            AlertDialog alertDialog = new AlertDialog.Builder(SumByDayActivity.this).create();
            alertDialog.setTitle("Alert groupList");
            alertDialog.setMessage("The group by is error:" + e.getMessage());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }




        if (list.size() > 0) {
            subList.clear();
            subListLayout.removeAllViews();
            int num1 = 0, num2 = 0, num3 = 0, num4 = 0, num5 = 0, num6 = 0;
            int price1 = 0, price2 = 0, price3 = 0, price4 = 0, price5 = 0, price6 = 0;
            int tax1 = 0, tax2 = 0, tax3 = 0, tax4 = 0, tax5 = 0, tax6 = 0;
            for (HashMap data : list) {
                int payType = (int) data.get("uriagekb");
                if (payType == 1) {//
                    num1 = (int) data.get("suryo");
                    price1 = (int) data.get("kingaku");
                    tax1 = (int) data.get("shohizei");
                }else if (payType == 2) {// 売掛売上
                    num2 = (int) data.get("suryo");
                    price2 = (int) data.get("kingaku");
                    tax2 = (int) data.get("shohizei");
                }else if (payType == 3) {// 雑収入
                    num3 = (int) data.get("suryo");
                    price3 = (int) data.get("kingaku");
                    tax3 = (int) data.get("shohizei");
                }else if (payType == 4) {// 委託
                    num4 = (int) data.get("suryo");
                    price4 = (int) data.get("kingaku");
                    tax4 = (int) data.get("shohizei");
                }else if (payType == 5) {// 払戻
                    String refundType = (String) data.get("haraimodoshikb");
                    if (refundType.equals("1")) {
                        num5 = (int) data.get("suryo");
                        price5 = (int) data.get("kingaku");
                        tax5 = (int) data.get("shohizei");
                    }else if (refundType.equals("2")){
                        num6 = (int) data.get("suryo");
                        price6 = (int) data.get("kingaku");
                        tax6 = (int) data.get("shohizei");
                    }
                }
            }

            SettlementSubItemView view_2 = new SettlementSubItemView(currentActivity);
            HashMap map_2 = new HashMap();
            map_2.put("title", "総売上");
            map_2.put("num", num2+num6+num1+num5);
            map_2.put("price", price2+price6+price1+price5);
            view_2.initUI(map_2);
            subListLayout.addView(view_2);
            subList.add(map_2);

            try {
                for(int i = 0; i < grouplist.size(); i++){
                    int tapnum = 0;
                    int tapprice = 0;
                    SettlementSubItemView view = new SettlementSubItemView(currentActivity);
                    HashMap map = new HashMap();
                    map.put("title", grouplist.get(i).get(0).get("code"));
                    for(int j = 0; j < grouplist.get(i).size(); j++){
                        tapnum += (int)grouplist.get(i).get(j).get("total");
                        tapprice += (int)grouplist.get(i).get(j).get("price");
                    }
                    map.put("num", tapnum);
                    map.put("price", tapprice);

                    view.initUI(map);
                    subListLayout.addView(view);
                    subList.add(map);
                }
            } catch (Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(SumByDayActivity.this).create();
                alertDialog.setTitle("Alert groupView");
                alertDialog.setMessage("The view error:" + e.getMessage());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }



            SettlementSubItemView view_3 = new SettlementSubItemView(currentActivity);
            HashMap map_3 = new HashMap();
            map_3.put("title", "雑収入");
            map_3.put("num", num3);
            map_3.put("price", price3);
            view_3.initUI(map_3);
            subListLayout.addView(view_3);
            subList.add(map_3);


            SettlementSubItemView view0 = new SettlementSubItemView(currentActivity);
            HashMap map0 = new HashMap();
            map0.put("title", "消費税内税");
            map0.put("num", 0);
            map0.put("price", tax1+tax2+tax3+tax4+tax5+tax6);

            view0.initUI(map0);
            subListLayout.addView(view0);
            subList.add(map0);

            SettlementSubItemView view1 = new SettlementSubItemView(currentActivity);
            HashMap map1 = new HashMap();
            map1.put("title", "売上合計");
            map1.put("num", num1+num5+num2+num6);
            map1.put("price", price1+price5+price2+price6);

            view1.initUI(map1);
            subListLayout.addView(view1);
            subList.add(map1);

            SettlementSubItemView view3 = new SettlementSubItemView(currentActivity);
            HashMap map3 = new HashMap();
            map3.put("title", "税金合計");
            map3.put("num", "0");
            map3.put("price", price1+price2+price3+price4+price5+price6);

            view3.initUI(map3);
            subListLayout.addView(view3);
            subList.add(map3);

            SettlementSubItemView view4 = new SettlementSubItemView(currentActivity);
            HashMap map4 = new HashMap();
            map4.put("title", "税抜き合計");
            map4.put("num", "0");
            map4.put("price", price1+price2+price3+price4+price5+price6-(tax1+tax2+tax3+tax4+tax5+tax6));

            view4.initUI(map4);
            subListLayout.addView(view4);
            subList.add(map4);
        }
    }


    private void checkingPrintState(final LabelPrinter printer) {
        isLoading = true;
        loadingLayout.setVisibility(View.VISIBLE);
        final DownTimer myTimer = new DownTimer(1, 500);
        myTimer.setOnFinishListener(new DownTimer.OnFinishListener() {

            @Override
            public void onFinish() {
                if (printer != null){
                    if (printer.getPrinting() == 1) { // printing now...
                        if (cm.hasPrintingErr) {
                            loadingLayout.setVisibility(View.INVISIBLE);
                            myTimer.initialize();
                            isLoading = false;
                            finish();
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
                        myTimer.initialize();
                        isLoading = false;
                        finish();
                    }
                }else {
                    cm.hasPrintingErr = false;
                    loadingLayout.setVisibility(View.INVISIBLE);
                    myTimer.initialize();
                    isLoading = false;
                    finish();
                }
            }

            @Override
            public void onTick(int progressValue) {

            }
        });
        myTimer.start();
    }
}
