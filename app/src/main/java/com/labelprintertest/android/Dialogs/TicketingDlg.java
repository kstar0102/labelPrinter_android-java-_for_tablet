package com.labelprintertest.android.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.citizen.sdk.labelprint.LabelPrinter;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.Models.TicketInfo;
import com.labelprintertest.android.Models.TicketModel;
import com.labelprintertest.android.PrinterManager.PrinterManager;
import com.labelprintertest.android.R;
import com.labelprintertest.android.Views.TicketListItemView;

import java.util.ArrayList;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

import com.labelprintertest.android.Dialogs.PreticketingDlg;

public class TicketingDlg extends Dialog {

    private LinearLayout ticketListView;
    private ArrayList<TicketInfo> models;
    private TicketingLinstener linstener;
    private int paymentType;
    private int ticketingMoney, remainMoney;
    private long preMoney = 0;
    private TextView ticketingMoneyTxt, remainMoneyTxt;
    private EditText preMoneyTxt, receiptTxt;
    private Button ticketingBtn, ticketingReceiptBtn, ticketingPreBtn;
    private LinearLayout preMoneyLayout, remainMoneyLayout, refundLayout;
    private RadioButton cashRd, receivableRd;


    public TicketingDlg(@NonNull Context context, final int type, ArrayList<TicketInfo> datas, final TicketingLinstener linstener) {
        super(context);
        setContentView(R.layout.ticketing_dialog);

        this.models = datas;
        this.linstener = linstener;
        this.paymentType = type;
        ticketListView = findViewById(R.id.ticketListLayout);
        TextView paymentTypeTxt = findViewById(R.id.cashLb);
        paymentTypeTxt.setText(context.getResources().getStringArray(R.array.TicketingType)[paymentType]);

        preMoneyLayout = findViewById(R.id.preMoneyLayout);
        remainMoneyLayout = findViewById(R.id.remainMoneyLayout);
        refundLayout = findViewById(R.id.refundLayout);

        TextView ticketingMoneyLb = findViewById(R.id.ticketingMoneyLb);
        if (paymentType == 0) {
            preMoneyLayout.setVisibility(View.INVISIBLE);
            remainMoneyLayout.setVisibility(View.INVISIBLE);
            refundLayout.setVisibility(View.VISIBLE);
            ticketingMoneyLb.setText(R.string.refund_money);
        }else {
            preMoneyLayout.setVisibility(View.VISIBLE);
            remainMoneyLayout.setVisibility(View.VISIBLE);
            refundLayout.setVisibility(View.INVISIBLE);
            ticketingMoneyLb.setText(R.string.ticketing_money);
        }

        cashRd = findViewById(R.id.cashRd);
        receivableRd = findViewById(R.id.receivableRd);

        preMoneyTxt = findViewById(R.id.preMoney);
        receiptTxt = findViewById(R.id.receiptTxt);
        if (type == 4) {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(10);
            preMoneyTxt.setFilters(FilterArray);
            preMoneyTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    preMoneyTxt.setSelection(count);
                    String val = String.valueOf(s).replace(",", "");
                    if (val.equals("")) val = "0";
                    if (!String.valueOf(preMoney).equals(val)) {
                        preMoney = Long.valueOf(String.valueOf(val));
                        preMoneyTxt.setText(cm.numberFormat(cm.parseInteger(String.valueOf(preMoney))));
                        remainMoney = (int) (preMoney - ticketingMoney);
                        remainMoneyTxt.setText(cm.numberFormat(remainMoney) + currentActivity.getResources().getString(R.string.lb_yen));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }else {
            preMoneyTxt.setEnabled(false);
//            receiptTxt.setEnabled(false);
        }

        ticketingBtn = findViewById(R.id.ticketingBtn);
        ticketingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("確認");
                alert.setMessage("実行しますよろしいですか");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (linstener != null) {
                            if (paymentType == 0) {
                                if (cashRd.isChecked()) {//現金払戻し
                                    linstener.OnRefundTicketingBtnClicked(1);
                                }else {//売掛払戻し
                                    linstener.OnRefundTicketingBtnClicked(2);
                                }
                                DisableButton();
//                        dismiss();
                            }else {
                                if (type == 4) {
                                    if (preMoney != 0) {
                                        if (preMoney < ticketingMoney) {
                                            Common.cm.showAlertDlg(currentActivity.getResources().getString(R.string.input_err_title),
                                                    currentActivity.getResources().getString(R.string.price_err_msg), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }, null);
                                            return;
                                        }
                                    }
                                }
                                PrinterManager manager = new PrinterManager();
                                LabelPrinter printer = manager.printerStart(models, 0, "", paymentType);

                                //test
//                        if(printer != null) {
                                linstener.OnTicketingBtnClicked(printer);
                                DisableButton();
//                        }
                            }
                        }
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                alert.show();
            }
        });

        ticketingReceiptBtn = findViewById(R.id.ticketingReceiptBtn);
        if (type == 2) {
            ticketingReceiptBtn.setEnabled(false);
        }else {
            ticketingReceiptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (paymentType == 0) {
                        if (cashRd.isChecked()) {//現金払戻し
                            linstener.OnRefundTicketingBtnClicked(1);
                        }else {//売掛払戻し
                            linstener.OnRefundTicketingBtnClicked(2);
                        }
                        DisableButton();
//                        dismiss();
                    }else {
                        if (linstener != null) {
                            PrinterManager manager = new PrinterManager();
                            LabelPrinter printer = manager.printerStart(models, ticketingMoney, receiptTxt.getText().toString(), paymentType);

                            //test
//                            if(printer != null) {
                            linstener.OnTicketingReceiptBtnClicked(printer, Integer.valueOf((int) preMoney), receiptTxt.getText().toString());
                            DisableButton();
//                            }
                        }
                    }
                }
            });
        }


        ticketingPreBtn = findViewById(R.id.ticketPrebtn);
        ticketingPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreticketingDlg preticketingDlg = new PreticketingDlg(getContext());
                preticketingDlg.show();
            }
        });

        Button closeBtn = findViewById(R.id.ticketingCloseBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ticketingMoneyTxt = findViewById(R.id.ticketingMoney);
        remainMoneyTxt = findViewById(R.id.remainMoney);

        TextView userInfo = findViewById(R.id.userInfo);
        userInfo.setText(cm.getUserInfo());

        setTicketList();
    }

    private void setTicketList() {
        ticketListView.removeAllViews();
        int price = 0;
        for (int i=0; i<models.size(); i++) {
            TicketInfo info = models.get(i);
            TicketListItemView itemView = new TicketListItemView(cm.currentActivity, i, info, new TicketListItemView.TicketItemClickListener() {
                @Override
                public void OnTicketItemClicked(int ind) {

                }
            });
            TicketModel model = info.getModel();
            price += model.getPrice() * info.getNum();
            ticketListView.addView(itemView);
        }
        ticketingMoney = price;
        if (price > 0) ticketingMoneyTxt.setText(cm.numberFormat(price) + currentActivity.getResources().getString(R.string.lb_yen));
    }

    private void DisableButton(){
        ticketingBtn.setEnabled(false);
        ticketingReceiptBtn.setEnabled(false);
    }

    public interface TicketingLinstener {
        public abstract void OnTicketingBtnClicked(LabelPrinter printer);
        public abstract void OnTicketingReceiptBtnClicked(LabelPrinter printer, int value, String only);
        public abstract void OnRefundTicketingBtnClicked(int refund_type);
    }
}
