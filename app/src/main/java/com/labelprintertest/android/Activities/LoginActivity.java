package com.labelprintertest.android.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.Common.DownTimer;
import com.labelprintertest.android.Common.LocalStorageManager;
import com.labelprintertest.android.DBManager.APIManager;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.Models.User;
import com.labelprintertest.android.R;

import java.io.File;
import java.util.ArrayList;

import static com.labelprintertest.android.Common.Common.StartPattern;
import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    private EditText userid, password;
    private boolean standalon = false;
    private RelativeLayout loading;

    /** すべての許可のリクエストID */
    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    public void onResume() {
        currentActivity = this;
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        currentActivity = this;

//        Fabric.with(this, new Crashlytics());

        ArrayList<String> permissions = new ArrayList();
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissions.add(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE);

        ArrayList requirePermissions = cm.checkPermissions(permissions);
        if (!requirePermissions.isEmpty()) {
            requestPermissions((String[]) requirePermissions.toArray(new String[requirePermissions.size()]),
                    ALL_PERMISSIONS_RESULT);
        }else {
            cm.getConfigInfoFromXml();
        }

        userid = findViewById(R.id.userId);
        password = findViewById(R.id.password);

        final LocalStorageManager localStorageManager = new LocalStorageManager();
        String userId = localStorageManager.getLoginStatus();
        if (userId != null) {
            userid.setText(userId);
        }else {
            userid.setText("");
        }
        password.setText("");

        final Button login = findViewById(R.id.buttonSign);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userid.getText().toString().equals("") || password.getText().toString().equals("")) {
                    Common.cm.showAlertDlg(getResources().getString(R.string.input_err_title),
                            getResources().getString(R.string.input_err_msg), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }, null);
                    return;
                }else {
                    login(userid.getText().toString(), password.getText().toString());
//                    loginFromLocal(userid.getText().toString(), password.getText().toString());
                }
                login.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        login.setEnabled(true);
                    }
                }, 4000L);
            }
        });

        loading = findViewById(R.id.loadingLayout);
        final DownTimer myTimer = new DownTimer(500, 100);
        myTimer.setOnFinishListener(new DownTimer.OnFinishListener() {

            @Override
            public void onFinish() {
                getStartPattern();
                if (StartPattern == 7) {
                    cm.showAlertDlg(getResources().getString(R.string.login_err_title),
                            getResources().getString(R.string.login_err_msg2),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }, null);
                }else if (StartPattern == 8) {
                    cm.showAlertDlg(getResources().getString(R.string.login_err_title),
                            getResources().getString(R.string.login_err_msg3),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }, null);
                }
                String val = localStorageManager.getStartMode();
                if (val != null) {
                    if (val.equals("online")) {
                        checkServerState();
                    }else {
                        standalon = true;
                        changeStartPattern();
                    }
                }else {
                    checkServerState();
                }
                myTimer.initialize();
            }

            @Override
            public void onTick(int progressValue) {

            }
        });
        myTimer.start();
    }

    private void getStartPattern() {
        APIManager apiManager = new APIManager();
        if (cm.checkExistXML()) {//XMLファイル有
            if (cm.checkLocalDBState()) {//ローカルデータベース接続
                if (apiManager.connectionclass() != null) {//リモートデータベース接続
                    StartPattern = 1;
                    loading.setVisibility(View.GONE);
                    return;
                }else {//リモートデータベース接続不可
                    StartPattern = 2;
                    loading.setVisibility(View.GONE);
                    return;
                }
            }else {//ローカルデータベース接続不可
                if (apiManager.connectionclass() != null) {//リモートデータベース接続
                    StartPattern = 3;
                    loading.setVisibility(View.GONE);
                    return;
                }else {//リモートデータベース接続不可
                    StartPattern = 4;
                    loading.setVisibility(View.GONE);
                    return;
                }
            }
        }else {//XMLファイル無
            if (cm.checkLocalDBState()) {//ローカルデータベース接続
                if (apiManager.connectionclass() != null) {//リモートデータベース接続
                    StartPattern = 5;
                    loading.setVisibility(View.GONE);
                    return;
                }else {//リモートデータベース接続不可
                    StartPattern = 6;
                    loading.setVisibility(View.GONE);
                    return;
                }
            }else {//ローカルデータベース接続不可
                if (apiManager.connectionclass() != null) {//リモートデータベース接続
                    StartPattern = 7;
                    loading.setVisibility(View.GONE);
                    return;
                }else {//リモートデータベース接続不可
                    StartPattern = 8;
                    loading.setVisibility(View.GONE);
                    return;
                }
            }
        }
    }

    private void checkServerState() {
        APIManager apiManager = new APIManager();
        if (apiManager.connectionclass() != null) {//リモートデータベース接続
            standalon = false;
            changeStartPattern();
        }else {//リモートデータベース接続不可
            cm.showAlertDlg(getResources().getString(R.string.login),
                    getResources().getString(R.string.server_connect_error_msg),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            standalon = true;
                            changeStartPattern();
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    private void changeStartPattern() {
        if (standalon) {
            if (StartPattern == 1 ||
                    StartPattern == 3 ||
                    StartPattern == 5) {
                StartPattern ++;
            }
        }else {
            if (StartPattern == 2 ||
                    StartPattern == 4 ||
                    StartPattern == 6) {
                StartPattern --;
            }
        }
    }

    private void login(String id, String pass) {
        loading.setVisibility(View.VISIBLE);
        APIManager apiManager = new APIManager();
        LocalStorageManager localStorageManager = new LocalStorageManager();
        String fname =  localStorageManager.getXMLFile();
        switch (StartPattern) {
            case 1:
                apiManager.syncFromServer();
                loginFromLocal(id, pass);
                cm.getTicketInfoFromXml();
                break;
            case 2:
                loginFromLocal(id, pass);
                cm.getTicketInfoFromXml();
                break;
            case 3:
                apiManager.syncFromServer();
                loginFromServer(id, pass);
                cm.getTicketInfoFromXml();
                break;
            case 4:
                loading.setVisibility(View.GONE);
                cm.showAlertDlg(getResources().getString(R.string.login_err_title),
                        getResources().getString(R.string.login_err_msg1),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, null);
                //test
//                loginFromLocal(id, pass);
//                cm.getTicketInfoFromXml();
                break;
            case 5:
                apiManager.syncFromServer();
                loginFromLocal(id, pass);
                if (fname != null) {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File dir = new File(root.getAbsolutePath() + "/LabelPrinter/" + fname);
                    if(dir.exists()) {
                        cm.getTicketInfoFromXml();
                        return;
                    }
                }
                cm.getTicketInfoFromLocal();
                break;
            case 6:
                loginFromLocal(id, pass);
                if (fname != null) {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File dir = new File(root.getAbsolutePath() + "/LabelPrinter/" + fname);
                    if(dir.exists()) {
                        cm.getTicketInfoFromXml();
                        return;
                    }
                }
                cm.getTicketInfoFromLocal();
                break;
            default:
                break;
        }
    }

    private void loginFromLocal(String id, String pass) {
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        LocalStorageManager manager = new LocalStorageManager();
        String hashPass = cm.getHashCodeFromPass(pass);
        System.out.println(hashPass);
        User user = query.getUserInfo(id, hashPass);
        // test
//        user = new User();
//        user.setId("1");
//        user.setName("テスター");
//        user.setPassword("111111");
        if (user != null) {
            manager.saveLoginInfo(user.getId());
            cm.me = user;
            loading.setVisibility(View.GONE);
            Intent intent = new Intent(currentActivity, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            loading.setVisibility(View.GONE);
            cm.showAlertDlg(getResources().getString(R.string.login_err_title),
                    getResources().getString(R.string.login_err_msg),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, null);
        }
    }

    private void loginFromServer(String id, String pass) {
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        LocalStorageManager manager = new LocalStorageManager();
        APIManager apiManager = new APIManager();
        String hashPass = cm.getHashCodeFromPass(pass);

        User user = apiManager.loginToServer(id, hashPass);
        // test
//        User user = new User();
//        user.setId("1");
//        user.setName("テスター");
//        user.setPassword("111111");
//        cm.me = user;
        if (user != null) {
            query.addUserInfo(user);
            manager.saveLoginInfo(user.getId());
            cm.me = user;
            loading.setVisibility(View.GONE);
            Intent intent = new Intent(cm.currentActivity, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            loading.setVisibility(View.GONE);
            cm.showAlertDlg(getResources().getString(R.string.login_err_title),
                    getResources().getString(R.string.login_err_msg),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                cm.getConfigInfoFromXml();
        }
    }
}
