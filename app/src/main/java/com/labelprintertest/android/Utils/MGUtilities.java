package com.labelprintertest.android.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.labelprintertest.android.R;

public class MGUtilities {
    public static void showAlertView(Activity act, int resIdTitle, int resIdMessage) {
        AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setTitle(resIdTitle);
        alert.setMessage(resIdMessage);
        alert.setPositiveButton(act.getResources().getString(R.string.OK),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
        alert.create();
        alert.show();
    }
}
