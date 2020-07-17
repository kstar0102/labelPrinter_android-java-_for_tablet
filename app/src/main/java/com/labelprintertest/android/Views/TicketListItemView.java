package com.labelprintertest.android.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.labelprintertest.android.Models.TicketInfo;
import com.labelprintertest.android.Models.TicketModel;
import com.labelprintertest.android.R;

import static com.labelprintertest.android.Common.Common.cm;

public class TicketListItemView extends LinearLayout {

    private Context context;
    private TicketInfo info;
    private View viewInflate;
    private TicketItemClickListener listener;
    private int ind;

    public TicketListItemView(Context context, int ind, TicketInfo info, TicketItemClickListener listener) {
        super(context);

        this.context = context;
        this.info = info;
        this.listener = listener;
        this.ind = ind;

        init();
    }

    public TicketListItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.ticket_item, this, true);

        TicketModel model = info.getModel();
        TextView ticketName = viewInflate.findViewById(R.id.ticketName);
        ticketName.setText(model.getName());
        TextView ticketNum = viewInflate.findViewById(R.id.ticketNum);
        ticketNum.setText(cm.numberFormat(info.getNum()) + context.getResources().getString(R.string.lb_num));
        TextView ticketPrice = viewInflate.findViewById(R.id.ticketPrice);
        ticketPrice.setText(context.getResources().getString(R.string.lb_yen2) + cm.numberFormat(model.getPrice() * info.getNum()));

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnTicketItemClicked(ind);
                }
            }
        });
    }

    public interface TicketItemClickListener {
        public abstract void OnTicketItemClicked(int ind);
    }
}
