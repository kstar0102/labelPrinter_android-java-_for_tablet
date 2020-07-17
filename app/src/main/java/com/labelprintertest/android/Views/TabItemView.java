package com.labelprintertest.android.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.labelprintertest.android.Models.TicketType;
import com.labelprintertest.android.R;


public class TabItemView extends LinearLayout {

    private Context context;
    private View viewInflate;
    private TicketType tabInfo;
    private TabItemClickListener listener;
    private LinearLayout tabItemView;
    private int ind;

    public TabItemView(Context context, TicketType tabInfo, int ind, TabItemClickListener listener) {
        super(context);

        this.context = context;
        this.tabInfo = tabInfo;
        this.listener = listener;
        this.ind = ind;

        init();
    }

    public TabItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewInflate = inflater.inflate(R.layout.tab_item_view, this, true);

        tabItemView = viewInflate.findViewById(R.id.tabItemView);
        TextView tabText = viewInflate.findViewById(R.id.tabTitleTxt);
        tabText.setText(this.tabInfo.getName());

        this.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnTabItemClicked(ind);
                }
            }
        });
    }

    public  void changeBackground(Drawable drawable) {
        tabItemView.setBackground(drawable);
    }

    public interface TabItemClickListener {
        public abstract void OnTabItemClicked(int ind);
    }
}
