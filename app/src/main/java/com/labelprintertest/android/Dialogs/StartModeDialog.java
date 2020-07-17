package com.labelprintertest.android.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.labelprintertest.android.Common.LocalStorageManager;
import com.labelprintertest.android.R;

import static com.labelprintertest.android.Common.Common.cm;

public class StartModeDialog extends Dialog {
    public StartModeDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.start_mode_dialog);

        TextView userInfo = findViewById(R.id.userInfo);
        userInfo.setText(cm.getUserInfo());

        LocalStorageManager localStorageManager = new LocalStorageManager();
        final CheckBox checkBox = findViewById(R.id.startModeCheck);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        if (localStorageManager.getStartMode() != null) {
            if (localStorageManager.getStartMode().equals("online"))
                checkBox.setChecked(false);
            else
                checkBox.setChecked(true);
        }

        findViewById(R.id.registryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalStorageManager localStorageManager = new LocalStorageManager();
                if (checkBox.isChecked()) {
                    localStorageManager.saveStartMode("standalone");
                }else {
                    localStorageManager.saveStartMode("online");
                }
                dismiss();
            }
        });
    }
}
