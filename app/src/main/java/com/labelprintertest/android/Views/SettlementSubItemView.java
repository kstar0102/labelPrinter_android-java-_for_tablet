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

public class SettlementSubItemView extends LinearLayout {

    private Context context;
    private View viewInflate;

    public SettlementSubItemView(Context context) {
        super(context);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.settlement_sub_item, this, true);
    }

    public SettlementSubItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.settlement_sub_item, this, true);
    }

    public SettlementSubItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.settlement_sub_item, this, true);
    }

    public void initUI(HashMap map) {
        TextView titleTxt = viewInflate.findViewById(R.id.titleTxt);
        titleTxt.setText(String.valueOf(map.get("title")));

        TextView numTxt = viewInflate.findViewById(R.id.numTxt);
        if (String.valueOf(map.get("num")).equals("消費税内税"))
            numTxt.setText("0件");
        else
            numTxt.setText(String.valueOf(map.get("num"))+"件");

        TextView priceTxt = viewInflate.findViewById(R.id.priceTxt);
        priceTxt.setText(cm.numberFormat(Integer.valueOf(String.valueOf(map.get("price")))));
    }
}
