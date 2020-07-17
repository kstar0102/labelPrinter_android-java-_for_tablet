package com.labelprintertest.android.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.labelprintertest.android.Common.LocalStorageManager;
import com.labelprintertest.android.Models.TicketType;
import com.labelprintertest.android.R;

import java.util.ArrayList;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

public class TicketTypeSettingDialog extends Dialog {

    LinearLayout ticketTypeListLayout;

    public TicketTypeSettingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.ticket_type_setting);

        ticketTypeListLayout = findViewById(R.id.ticketTypeListLayout);
        if (cm.ticketTypes.size() > 0) {
            LocalStorageManager localStorageManager = new LocalStorageManager();
            String result = localStorageManager.getHideTicketType();
            ArrayList<String> hideTypes = new ArrayList<>();
            if (result != null) {
                if (!result.equals(""))
                    hideTypes = cm.convertToArrayListFromString(result);
            }
            int ind = 0;
            for (final TicketType ticketType : cm.ticketTypes) {
                final CheckBox checkBox = new CheckBox(currentActivity);
                checkBox.setHeight(70);
                checkBox.setTextSize(20);
                checkBox.setTag(ind);
                checkBox.setText(ticketType.getName());
                if (hideTypes.size() > 0) {
                    boolean isHidden = false;
                    for (String name : hideTypes) {
                        if (name.equals(ticketType.getName())) {
                            isHidden = true;
                            break;
                        }
                    }
                    checkBox.setChecked(!isHidden);
                }else {
                    checkBox.setChecked(true);
                }
                ticketTypeListLayout.addView(checkBox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (checkEnableState() <= 0)
                            checkBox.setChecked(true);
                    }
                });
                ind ++;
            }
        }

        findViewById(R.id.registryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> hideTypes = new ArrayList<>();
                for (int i=0; i<ticketTypeListLayout.getChildCount(); i++) {
                    CheckBox view = (CheckBox) ticketTypeListLayout.getChildAt(i);
                    if (!view.isChecked()) {
                        int ind = (int) view.getTag();
                        TicketType type = cm.ticketTypes.get(ind);
                        String name = type.getName();
                        hideTypes.add(name);
                    }
                }
                LocalStorageManager localStorageManager = new LocalStorageManager();
                localStorageManager.saveHideTicketType(cm.stringFromStringArray(hideTypes));
                dismiss();
            }
        });

        TextView userInfo = findViewById(R.id.userInfo);
        userInfo.setText(cm.getUserInfo());
    }

    private int checkEnableState() {
        int showNum = 0;
        for (int i=0; i<ticketTypeListLayout.getChildCount(); i++) {
            CheckBox view = (CheckBox) ticketTypeListLayout.getChildAt(i);
            if (view.isChecked()) {
                showNum ++;
            }
        }
        return showNum;
    }
}
