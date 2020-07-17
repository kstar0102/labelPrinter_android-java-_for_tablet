package com.labelprintertest.android.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.labelprintertest.android.R;

import java.util.HashMap;

import static com.labelprintertest.android.Common.Common.cm;

public class SettlementMainItemView extends LinearLayout{

    private Context context;
    private View viewInflate;

    public SettlementMainItemView(Context context) {
        super(context);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.settlement_main_item, this, true);
    }

    public SettlementMainItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.settlement_main_item, this, true);
    }

    public SettlementMainItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.settlement_main_item, this, true);
    }

    public void initHeaderUI(HashMap map) {
        TextView codeTxt = viewInflate.findViewById(R.id.codeTxt);
        codeTxt.setText(String.valueOf(map.get("code")));

        TextView ticketTypeTxt = viewInflate.findViewById(R.id.ticketTypeTxt);
        ticketTypeTxt.setText(String.valueOf(map.get("type")));

        TextView unitTxt = viewInflate.findViewById(R.id.unitTxt);
        unitTxt.setText(String.valueOf(map.get("unit")));

        TextView sellTxt = viewInflate.findViewById(R.id.sellTxt);
        sellTxt.setText(String.valueOf(map.get("sell")));

        TextView refundTxt = viewInflate.findViewById(R.id.refundTxt);
        refundTxt.setText(String.valueOf(map.get("refund")));

        TextView totalTxt = viewInflate.findViewById(R.id.totalTxt);
        totalTxt.setText(String.valueOf(map.get("total")));

        TextView priceTxt = viewInflate.findViewById(R.id.priceTxt);
        priceTxt.setText(String.valueOf(map.get("price")));
    }

    public void initUI(HashMap map) {
        TextView codeTxt = viewInflate.findViewById(R.id.codeTxt);
        if(!String.valueOf(map.get("code")).equals(""))
            codeTxt.setText(String.valueOf(map.get("code")));

        TextView ticketTypeTxt = viewInflate.findViewById(R.id.ticketTypeTxt);
        ticketTypeTxt.setText(String.valueOf(map.get("type")));

        TextView unitTxt = viewInflate.findViewById(R.id.unitTxt);
        if(!String.valueOf(map.get("unit")).equals(""))
            unitTxt.setText(cm.numberFormat(Integer.valueOf(String.valueOf(map.get("unit")))));

        TextView sellTxt = viewInflate.findViewById(R.id.sellTxt);
        sellTxt.setText(cm.numberFormat(Integer.valueOf(String.valueOf(map.get("sell")))));

        TextView refundTxt = viewInflate.findViewById(R.id.refundTxt);
        refundTxt.setText(cm.numberFormat(Integer.valueOf(String.valueOf(map.get("refund")))));

        TextView totalTxt = viewInflate.findViewById(R.id.totalTxt);
        totalTxt.setText(cm.numberFormat(Integer.valueOf(String.valueOf(map.get("total")))));

        TextView priceTxt = viewInflate.findViewById(R.id.priceTxt);
        priceTxt.setText(cm.numberFormat(Integer.valueOf(String.valueOf(map.get("price")))));
    }
}
