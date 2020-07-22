package com.labelprintertest.android.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.citizen.sdk.labelprint.LabelPrinter;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.Common.DownTimer;
import com.labelprintertest.android.Common.LocalStorageManager;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.Dialogs.DeviceSettingDialog;
import com.labelprintertest.android.Dialogs.ReportingDialog;
import com.labelprintertest.android.Dialogs.TicketNumberInputDlg;
import com.labelprintertest.android.Dialogs.TicketingDlg;
import com.labelprintertest.android.Models.TicketInfo;
import com.labelprintertest.android.Models.TicketModel;
import com.labelprintertest.android.Models.TicketType;
import com.labelprintertest.android.PrinterManager.PrinterManager;
import com.labelprintertest.android.R;
import com.labelprintertest.android.Utils.DialogManager;
import com.labelprintertest.android.Views.TabItemView;
import com.labelprintertest.android.Views.TicketListItemView;

import java.io.File;
import java.util.ArrayList;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

/**
 *
 * 発券画面
 *
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TicketNumberInputDlg.TicketNumClickListener, TicketListItemView.TicketItemClickListener {

    private LinearLayout ticketListView;
    private LinearLayout tabItemLayout;
    private ArrayList<ArrayList> ticketViewArr;
    private ArrayList<TabItemView> tabViewArr;
    private TextView sumPriceTxt;
    private ScrollView ticketScrollView;
    private RelativeLayout loadingLayout;

    private ArrayList<TicketInfo> ticketingList;
    private ArrayList<TicketType> tabList;

    private int selectedIndex;
    private int selectedTabIndex = 0;
    private int selectedPayType;
    private boolean isLoading = false;
    static public boolean checkingpring = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        currentActivity = this;
        super.onResume();

        initUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentActivity = this;

        DbHelper dbHelper = new DbHelper(this);
        Queries query = new Queries(null, dbHelper);
        //query.setTestData();

        if (!cm.checkDeviceName()) {
            cm.showAlertDlg(getString(R.string.device_err_title), getString(R.string.device_err_msg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DeviceSettingDialog deviceSettingDialog = new DeviceSettingDialog(currentActivity, new DeviceSettingDialog.DeviceChangeListner() {
                        @Override
                        public void OnChangedDevice() {
                            TextView userInfo = findViewById(R.id.userInfo);
                            userInfo.setText(cm.getUserInfo());
                        }
                    });
                    deviceSettingDialog.show();
                }
            }, null);
        }

        ticketViewArr = new ArrayList<>();
        tabViewArr = new ArrayList<>();
        ticketingList = new ArrayList<>();

        ArrayList<TextView> items1 = new ArrayList<>();
        items1.add((TextView) findViewById(R.id.ticket11));
        items1.add((TextView) findViewById(R.id.ticket12));
        items1.add((TextView) findViewById(R.id.ticket13));
        items1.add((TextView) findViewById(R.id.ticket14));
        items1.add((TextView) findViewById(R.id.ticket15));
        ticketViewArr.add(items1);
        ArrayList<TextView> items2 = new ArrayList<>();
        items2.add((TextView) findViewById(R.id.ticket21));
        items2.add((TextView) findViewById(R.id.ticket22));
        items2.add((TextView) findViewById(R.id.ticket23));
        items2.add((TextView) findViewById(R.id.ticket24));
        items2.add((TextView) findViewById(R.id.ticket25));
        ticketViewArr.add(items2);
        ArrayList<TextView> items3 = new ArrayList<>();
        items3.add((TextView) findViewById(R.id.ticket31));
        items3.add((TextView) findViewById(R.id.ticket32));
        items3.add((TextView) findViewById(R.id.ticket33));
        items3.add((TextView) findViewById(R.id.ticket34));
        items3.add((TextView) findViewById(R.id.ticket35));
        ticketViewArr.add(items3);
        ArrayList<TextView> items4 = new ArrayList<>();
        items4.add((TextView) findViewById(R.id.ticket41));
        items4.add((TextView) findViewById(R.id.ticket42));
        items4.add((TextView) findViewById(R.id.ticket43));
        items4.add((TextView) findViewById(R.id.ticket44));
        items4.add((TextView) findViewById(R.id.ticket45));
        ticketViewArr.add(items4);

        tabItemLayout = findViewById(R.id.tabItemLayout);

        Button systemBtn = findViewById(R.id.btnSystem);
        systemBtn.setOnClickListener(this);

        Button reportBtn = findViewById(R.id.btnReport);
        reportBtn.setOnClickListener(this);

        Button clearBtn = findViewById(R.id.btnClear);
        clearBtn.setOnClickListener(this);

        Button refundBtn = findViewById(R.id.btnRefund);
        refundBtn.setOnClickListener(this);

        Button reticketingBtn = findViewById(R.id.btnReticketing);
        reticketingBtn.setOnClickListener(this);

//        Button consignBtn = findViewById(R.id.btnConsign);
//        consignBtn.setOnClickListener(this);

//        Button receivableBtn = findViewById(R.id.btnReceivable);
//        receivableBtn.setOnClickListener(this);

        Button cashBtn = findViewById(R.id.btnCash);
        cashBtn.setOnClickListener(this);

        Button selectXmlBtn = findViewById(R.id.selectXML);
        selectXmlBtn.setOnClickListener(this);

        ticketListView = findViewById(R.id.ticketListLayout);
        sumPriceTxt = findViewById(R.id.sumPriceTxt);

        ticketScrollView = findViewById(R.id.ticketScroll);

        loadingLayout = findViewById(R.id.loadingLayout);
        loadingLayout.setVisibility(View.INVISIBLE);
    }

    /**
     *
     * 画面UIコンポーネントの初期化関数
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initUI() {

        showTabs();
        setTicketList();
    }

    private void updateTicketList(TicketType type) {
        for (int k=0; k<ticketViewArr.size(); k++) {
            ArrayList list = ticketViewArr.get(k);
            for (int i=0; i<list.size(); i++) {
                TextView view = (TextView) list.get(i);
                view.setTag(String.valueOf(k) + "," + String.valueOf(i));
                final TicketModel model = cm.getTicketModelFormPos(type.getType(), k, i);
                if (model !=null) {
                    GradientDrawable bgShape = (GradientDrawable) view.getBackground();
                    bgShape.mutate();
                    bgShape.setColor(Color.parseColor(model.getBgColor()));
                    view.setTextColor(Color.parseColor(model.getFgColor()));
                    view.setText("");
//                    if (model.getPrice() > 0)
                    view.setText(model.getName() + "\n" + "\n" + getResources().getString(R.string.lb_yen2) + model.getPrice());
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (!isLoading) {
//                                if (model.getPrice() != 0) {
                                    final TicketInfo info = new TicketInfo();
                                    info.setModel(model);
                                    info.setNum(0);
                                    info.setType(tabList.get(selectedTabIndex));
                                    final TicketNumberInputDlg dlg = new TicketNumberInputDlg(currentActivity, info, new TicketNumberInputDlg.TicketNumClickListener() {
                                        @Override
                                        public void OnDeleteBtnClicked() {
                                        }

                                        @Override
                                        public void OnConfirmBtnClicked(int num) {
                                            addTicketInfoWithFilter(num, info);
                                        }
                                    });
                                    dlg.show();
//                                }
                            }
                            return false;
                        }
                    });
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (model.getPrice() != 0) {
                                TicketInfo info = new TicketInfo();
                                info.setModel(model);
                                info.setType(tabList.get(selectedTabIndex));
                                info.setNum(1);
                                addTicketInfoWithFilter(1, info);
//                            }
                        }
                    });
                }else {
                    GradientDrawable bgShape = (GradientDrawable) view.getBackground();
                    bgShape.mutate();
                    bgShape.setColor(Color.parseColor("#E0E0E0"));
                    view.setText("");
                    view.setOnClickListener(null);
                    view.setOnLongClickListener(null);
                }
            }
        }
    }

    private void addTicketInfoWithFilter(int num, TicketInfo newInfo) {
        Boolean isExsist = false;
        if (ticketingList.size() > 0) {
            for (int kk=0; kk<ticketingList.size(); kk++) {
                TicketInfo oldInfo = ticketingList.get(kk);
                if (oldInfo.getModel().getId().equals(newInfo.getModel().getId())) {
                    int oldNum = oldInfo.getNum();
                    oldInfo.setNum(oldNum + num);
                    ticketingList.set(kk, oldInfo);
                    isExsist = true;
                    break;
                }
            }
        }
        if (!isExsist) {
            newInfo.setNum(num);
            ticketingList.add(newInfo);
        }
        setTicketList();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showTabs() {
        TextView userInfo = findViewById(R.id.userInfo);
        userInfo.setText(cm.getUserInfo());

        if (cm.ticketTypes.size() > 0) {
            LocalStorageManager localStorageManager = new LocalStorageManager();
            String result = localStorageManager.getHideTicketType();
            ArrayList<String> hideTypes = new ArrayList<>();
            tabList = new ArrayList<>();
            if (result != null) {
                if (!result.equals("")) {
                    hideTypes = cm.convertToArrayListFromString(result);
                    for (TicketType type : cm.ticketTypes) {
                        boolean isHidden = false;
                        for (String name : hideTypes) {
                            if (type.getName().equals(name)) {
                                isHidden = true;
                            }
                        }
                        if (!isHidden)
                            tabList.add(type);
                    }
                }else {
                    tabList = (ArrayList<TicketType>) cm.ticketTypes.clone();
                }
            }else {
                tabList = (ArrayList<TicketType>) cm.ticketTypes.clone();
            }
            tabItemLayout.removeAllViews();
            for (int i=0; i<tabList.size(); i++) {
                TicketType type = tabList.get(i);
                TabItemView itemView = new TabItemView(this, type, i, new TabItemView.TabItemClickListener() {
                    @Override
                    public void OnTabItemClicked(int ind) {
                        tabChanged(ind);
                    }
                });
                tabItemLayout.addView(itemView);
            }
            TabItemView itemView = (TabItemView) tabItemLayout.getChildAt(selectedTabIndex);
            itemView.changeBackground(getDrawable(R.drawable.border_main));
            updateTicketList(tabList.get(selectedTabIndex));
        }
    }

    private void setTicketList() {
        ticketListView.removeAllViews();
        int price = 0;
        for (int i=0; i<ticketingList.size(); i++) {
            TicketInfo info = ticketingList.get(i);
            TicketListItemView itemView = new TicketListItemView(currentActivity, i, info, this);
            ticketListView.addView(itemView);
            TicketModel model = info.getModel();
            price += model.getPrice() * info.getNum();
        }

        ticketScrollView.post(new Runnable() {
            @Override
            public void run() {
                ticketScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        if (price > 0)
            {checkingpring = true;
            sumPriceTxt.setText(cm.numberFormat(price));}
        else
            sumPriceTxt.setText("");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void tabChanged(int ind) {
        TabItemView oldItemView = (TabItemView) tabItemLayout.getChildAt(selectedTabIndex);
        oldItemView.changeBackground(getDrawable(R.drawable.border_white));
        selectedTabIndex = ind;
        TabItemView newItemView = (TabItemView) tabItemLayout.getChildAt(selectedTabIndex);
        newItemView.changeBackground(getDrawable(R.drawable.border_main));
        updateTicketList(tabList.get(ind));
//        ArrayList<TicketInfo> tempList = (ArrayList<TicketInfo>) ticketingList.clone();
//        for (int i=0; i<ticketingList.size(); i++) {
//            TicketInfo info = tempList.get(i);
//            info.setType(tabList.get(ind));
//            ticketingList.set(i, info);
//        }
    }

    private void showTicketingDlg(int ind) {
        if (ticketingList.size() > 0) {
            TicketingDlg dlg = new TicketingDlg(currentActivity, ind, ticketingList, new TicketingDlg.TicketingLinstener() {
                @Override
                public void OnTicketingBtnClicked(LabelPrinter printer) { //to print
                    //test
//                    DbHelper dbHelper = new DbHelper(currentActivity);
//                    Queries query = new Queries(null, dbHelper);
//                    query.addSellInfoWithData(ticketingList, selectedPayType, 0);
//                    ticketingList.clear();
//                    setTicketList();

                    checkingPintState(printer, 0, 0, "");
                }

                @Override
                public void OnTicketingReceiptBtnClicked(LabelPrinter printer, int value, String only) { //to print

//                    DbHelper dbHelper = new DbHelper(currentActivity);
//                    Queries query = new Queries(null, dbHelper);
//                    query.addSellInfoWithData(ticketingList, selectedPayType, 0);
//                    query.addReceiptInfoWithData(value, only, selectedPayType);
//                    ticketingList.clear();
//                    setTicketList();

                    checkingPintState(printer, 1, value, only);
                }

                @Override
                public void OnRefundTicketingBtnClicked(int refund_type) {
                    DbHelper dbHelper = new DbHelper(currentActivity);
                    Queries query = new Queries(null, dbHelper);
                    query.addSellInfoWithData(ticketingList, selectedPayType, refund_type);
                    ticketingList.clear();
                    setTicketList();
                }
            });
            dlg.setCanceledOnTouchOutside(false);
            dlg.show();
        }else {
            // error alert
        }
    }

    private void checkingPintState(final LabelPrinter printer, final int receiptOption, final int receiptValue, final String receiptOnlyStr) {
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
                            Common.cm.showAlertDlg(currentActivity.getResources().getString(R.string.printing_err_title),
                                    currentActivity.getResources().getString(R.string.printing_err_msg), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }, null);
                            return;
                        }
                    }else { // completed print
                        if (receiptOption != 2) {
                            DbHelper dbHelper = new DbHelper(currentActivity);
                            Queries query = new Queries(null, dbHelper);
                            query.addSellInfoWithData(ticketingList, selectedPayType, 0);
                            if (receiptOption == 1)
                                query.addReceiptInfoWithData(receiptValue, receiptOnlyStr, selectedPayType);
                        }
                        ticketingList.clear();
                        setTicketList();
                        cm.hasPrintingErr = false;
                        loadingLayout.setVisibility(View.INVISIBLE);
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

    private void selectXML() {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/LabelPrinter");
        File[] files = dir.listFiles();
        if (files.length > 0) {
            final String[] fileList = new String[files.length];
            int ind = 0;

            for (int i = 0; i < files.length; i++)
            {
                fileList[i] = files[i].getName();
            }
            DialogManager.showRadioDialog(
                    this,
                    getResources().getString(R.string.select_xml_title),
                    fileList,
                    null,
                    new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LocalStorageManager localStorageManager = new LocalStorageManager();
                            localStorageManager.saveXMLFile(fileList[which]);
                            cm.getTicketInfoFromXml();

                            localStorageManager.saveHideTicketType(null);
                            selectedTabIndex = 0;
                            initUI();
                            tabChanged(0);
                        }
                    });
        }else {
            Common.cm.showAlertDlg(currentActivity.getResources().getString(R.string.no_xml_title),
                    currentActivity.getResources().getString(R.string.no_xml_msg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }, null);
            return;
        }

    }

    @Override
    public void onClick(View v) {
        if (isLoading) return;
        if (!cm.checkDeviceName()) {
            cm.showAlertDlg(getString(R.string.device_err_title), getString(R.string.device_err_msg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DeviceSettingDialog deviceSettingDialog = new DeviceSettingDialog(currentActivity, new DeviceSettingDialog.DeviceChangeListner() {
                        @Override
                        public void OnChangedDevice() {
                            TextView userInfo = findViewById(R.id.userInfo);
                            userInfo.setText(cm.getUserInfo());
                        }
                    });
                    deviceSettingDialog.show();
                }
            }, null);
            return;
        }
        switch (v.getId()) {
            case R.id.btnSystem:
                Intent intent = new Intent(currentActivity, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.btnReport:
                ReportingDialog dlg = new ReportingDialog(this);
                dlg.show();
                break;
            case R.id.btnClear:
                ticketingList.clear();
                setTicketList();
                break;
            case R.id.btnRefund:
                showTicketingDlg(0);
                selectedPayType = 5;
                break;
            case R.id.btnReticketing:
                if (ticketingList.size() > 0) {
                    PrinterManager manager = new PrinterManager();
                    LabelPrinter printer = manager.printerStart(ticketingList, 0, "", 0);
                    checkingPintState(printer, 2, 0, "");
                    selectedPayType = 0;
                }
                break;
//            case R.id.btnConsign:
//                showTicketingDlg(2);
//                selectedPayType = 4;
//                break;
//            case R.id.btnReceivable:
//                showTicketingDlg(3);
//                selectedPayType = 2;
//                break;
            case R.id.btnCash:
                showTicketingDlg(4);
                selectedPayType = 1;
                break;
            case R.id.selectXML:
                selectXML();
                break;
        }
    }

    @Override
    public void OnDeleteBtnClicked() {
        ticketingList.remove(selectedIndex);
        setTicketList();
    }

    @Override
    public void OnConfirmBtnClicked(int num) {
        TicketInfo info = ticketingList.get(selectedIndex);
        info.setNum(num);
        ticketingList.set(selectedIndex, info);
        setTicketList();
    }

    @Override
    public void OnTicketItemClicked(int ind) {
        selectedIndex = ind;
        TicketNumberInputDlg dlg = new TicketNumberInputDlg(currentActivity, ticketingList.get(ind), this);
        dlg.show();
    }
}
