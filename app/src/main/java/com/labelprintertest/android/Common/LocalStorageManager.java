package com.labelprintertest.android.Common;

import android.content.SharedPreferences;

import com.labelprintertest.android.DBManager.APIManager;
import com.labelprintertest.android.R;

import java.util.Calendar;

/**
 *
 * アプリ内のデータの保存を管理するクラス
 *
 */

public class LocalStorageManager {

    /**
     *
     * LoginStatusを保存する
     *
     * @param info Status
     *
     */
    public void saveLoginInfo(String info) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login_info", info);
        editor.commit();
    }

    /**
     *
     * LoginStatusを返す
     *
     * @return NSString Status
     *
     */
    public String getLoginStatus() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("login_info", null);
        return syncState;
    }

    public void saveStartMode(String date) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("start_mode", date);
        editor.commit();
    }

    public String getStartMode() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("start_mode", null);
        return syncState;
    }

    public void saveLastSyncDate(String date) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_sync", date);
        editor.commit();
    }

    public String getLastSyncDate() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("last_sync", null);
        return syncState;
    }

    public void saveHideTicketType(String date) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ticket_names", date);
        editor.commit();
    }

    public String getHideTicketType() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("ticket_names", null);
        return syncState;
    }

    public void saveXMLFile(String fname) {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("xml_name", fname);
        editor.commit();
    }

    public String getXMLFile() {
        SharedPreferences sharedPreferences = Common.myApp.getSharedPreferences(String.valueOf(R.string.app_name), Common.myApp.MODE_PRIVATE);
        String syncState = sharedPreferences.getString("xml_name", null);
        return syncState;
    }
}

