package com.labelprintertest.android.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.labelprintertest.android.Application.MyApplication;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.Models.PrinterInfo;
import com.labelprintertest.android.Models.TicketModel;
import com.labelprintertest.android.Models.TicketType;
import com.labelprintertest.android.Models.User;
import com.labelprintertest.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Commonクラス
 */

public class Common {
    //　共通

    /** Down Timer */
    public static DownTimer myTimer;

    /** Commonクラスのオブジェクト */
    public static Common cm = new Common();

    /** 現在のActivityを保存するオブジェクト */
    public static Activity currentActivity = null;

    /** 起動時パターン */
    public static int StartPattern = 1;//1~8

    public static User me = null;

    /** MyApplicationを保存するオブジェクト */
    public static MyApplication myApp;

    /** 日本の祝日を保存するオブジェクト */
    public static JSONArray holidayList;

    public static boolean hasPrintingErr = false;

    public static String endInfoStr = "";

    public static ArrayList<TicketType> ticketTypes = new ArrayList<>();
    public static ArrayList<PrinterInfo> printerInfos = new ArrayList<>();
    public static ArrayList<TicketModel> ticketModels = new ArrayList<>();

    public boolean checkLocalDBState() {
        boolean exist = false;
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        exist = query.getDBInfo();
        return exist;
    }

    public void getConfigInfoFromXml() {
        String sampleXml = readFromXml(Config.CONFIG_NAME);
        if (!sampleXml.equals("")) {
            JSONObject jsonObject = null;
            try {
                jsonObject = XML.toJSONObject(sampleXml);
                JSONObject object = jsonObject.getJSONObject("Item");
                Config.SERVER_IP_ADDRESS = object.getString("SERVER_IP");
                Config.SERVER_PORT = object.getString("SERVER_PORT");
                Config.DB_NAME = object.getString("DB_NAME");
                Config.USER_NAME = object.getString("USER_NAME");
                Config.PASSWORD = object.getString("PASSWORD");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void getTicketInfoFromXml () {
        LocalStorageManager localStorageManager = new LocalStorageManager();
        String fname =  localStorageManager.getXMLFile();
        if (fname == null) {
            fname = Config.XML_NAME;
        }
        String sampleXml = readFromXml(fname);
        if (!sampleXml.equals("")) {

//            XmlToJson xmlToJson = new XmlToJson.Builder(String.valueOf(sampleXml)).build();
//            JSONObject jsonObject = xmlToJson.toJson();
            JSONObject jsonObject = null;
            try {
                jsonObject = XML.toJSONObject(sampleXml);
                JSONObject ticketObject = jsonObject.getJSONObject("Items");
                JSONObject ticketTypeObj = ticketObject.optJSONObject("tickettype");

                Object json = ticketTypeObj.get("ticket");

                ticketTypes.clear();
                if (json instanceof JSONObject) {
                    JSONObject ticket = ticketTypeObj.getJSONObject("ticket");
                    TicketType type = new TicketType();
                    type.setType(parseJsonString(ticket,"チケット種類"));
                    type.setName(parseJsonString(ticket,"名称"));
                    type.setOrder(parseInteger(parseJsonString(ticket,"表示順")));
                    ticketTypes.add(type);
                } else {
                    JSONArray ticketType = ticketTypeObj.getJSONArray("ticket");
                    for (int i=0; i<ticketType.length(); i++) {
                        JSONObject ticket = ticketType.getJSONObject(i);
                        TicketType type = new TicketType();
                        type.setType(parseJsonString(ticket,"チケット種類"));
                        type.setName(parseJsonString(ticket,"名称"));
                        type.setOrder(parseInteger(parseJsonString(ticket,"表示順")));
                        ticketTypes.add(type);
                    }
                }

                // for sort
                if (ticketTypes.size() >= 2) {
                    for (int i = 0; i < ticketTypes.size(); i++) {
                        for (int j = i + 1; j < ticketTypes.size(); j++) {
                            TicketType type1 = ticketTypes.get(i);
                            TicketType type2 = ticketTypes.get(j);
                            if (type1.getOrder() > type2.getOrder()) {
                                ticketTypes.set(i, type2);
                                ticketTypes.set(j, type1);
                            }
                        }
                    }
                }

                printerInfos.clear();
                JSONObject printInfoObj = ticketObject.getJSONObject("printinfo");
                json = printInfoObj.get("印字設定");
                if (json instanceof JSONObject) {
                    JSONObject object = printInfoObj.getJSONObject("印字設定");
                    PrinterInfo info = new PrinterInfo();
                    info.setProfileNo(parseInteger(parseJsonString(object,"プロファイルNo")));
                    info.setType(parseJsonString(object,"帳票区分"));
                    info.setPrinterNum(parseInteger(parseJsonString(object,"印字番号")));
                    info.setPrinterType(parseJsonString(object,"印字種別"));
                    info.setIsShown(parseJsonString(object,"表示区分"));
                    info.setFont(parseJsonString(object,"フォント"));
                    info.setFontSize(parseJsongDouble(parseJsonString(object,"フォントサイズ")));
                    info.setIsItalic(parseInteger(parseJsonString(object,"斜体")));
                    info.setIsBold(parseInteger(parseJsonString(object,"太字")));
                    info.setFormat(parseJsonString(object,"書式"));
                    info.setStartX(parseInteger(parseJsonString(object,"X始点")));
                    info.setStartY(parseInteger(parseJsonString(object,"Y始点")));
                    info.setEndX(parseInteger(parseJsonString(object,"X終点")));
                    info.setEndY(parseInteger(parseJsonString(object,"Y終点")));
                    info.setImgData(null, parseJsonString(object,"ファイル名"));
                    info.setBarcodeType(parseInteger(parseJsonString(object,"バーコードタイプ")));
                    info.setBarcodeHeight(parseInteger(parseJsonString(object,"バーコード高さ")));
                    info.setBarcode(parseJsonString(object,"コード"));
                    info.setWhiteFlag(parseInteger(parseJsonString(object,"白抜")));
                    printerInfos.add(info);
                }else if (json instanceof JSONArray) {
                    JSONArray printInfo = printInfoObj.getJSONArray("印字設定");
                    for (int i=0; i<printInfo.length(); i++) {
                        JSONObject object = printInfo.getJSONObject(i);
                        PrinterInfo info = new PrinterInfo();
                        info.setProfileNo(parseInteger(parseJsonString(object,"プロファイルNo")));
                        info.setType(parseJsonString(object,"帳票区分"));
                        info.setPrinterNum(parseInteger(parseJsonString(object,"印字番号")));
                        info.setPrinterType(parseJsonString(object,"印字種別"));
                        info.setIsShown(parseJsonString(object,"表示区分"));
                        info.setFont(parseJsonString(object,"フォント"));
                        info.setFontSize(parseJsongDouble(parseJsonString(object,"フォントサイズ")));
                        info.setIsItalic(parseInteger(parseJsonString(object,"斜体")));
                        info.setIsBold(parseInteger(parseJsonString(object,"太字")));
                        info.setFormat(parseJsonString(object,"書式"));
                        info.setStartX(parseInteger(parseJsonString(object,"X始点")));
                        info.setStartY(parseInteger(parseJsonString(object,"Y始点")));
                        info.setEndX(parseInteger(parseJsonString(object,"X終点")));
                        info.setEndY(parseInteger(parseJsonString(object,"Y終点")));
                        info.setImgData(null, parseJsonString(object,"ファイル名"));
                        info.setBarcodeType(parseInteger(parseJsonString(object,"バーコードタイプ")));
                        info.setBarcodeHeight(parseInteger(parseJsonString(object,"バーコード高さ")));
                        info.setBarcode(parseJsonString(object,"コード"));
                        info.setWhiteFlag(parseInteger(parseJsonString(object,"白抜")));
                        printerInfos.add(info);
                    }
                }

                ticketModels.clear();
                if (ticketObject.has("Item")) {
                    json = ticketObject.get("Item");
                    if (json instanceof JSONObject) {
                        JSONObject object = ticketObject.getJSONObject("Item");
                        TicketModel model = new TicketModel();
                        model.setId(parseJsonString(object,"ID"));
                        model.setType(parseJsonString(object,"チケット種類"));
                        model.setName(parseJsonString(object,"券種名"));
                        model.setNamePr(parseJsonString(object,"券種名印字"));
                        model.setNameAge(parseJsonString(object,"年齢層"));
                        model.setPrice(parseInteger(parseJsonString(object,"価格")));
                        model.setTaxRatio(parseFloat(parseJsonString(object,"消費税率")));
                        model.setTax(parseInteger(parseJsonString(object,"消費税額")));
                        JSONObject posObj = object.getJSONObject("位置");
                        model.setRowPos(parseInteger(parseJsonString(posObj, "行")));
                        model.setColPos(parseInteger(parseJsonString(posObj, "列")));
                        JSONObject colorObj = object.getJSONObject("配色");
                        model.setFgColor(parseJsonString(colorObj, "文字"));
                        model.setBgColor(parseJsonString(colorObj, "背景"));
                        model.setOrder(parseInteger(parseJsonString(object,"清算表示順")));
                        model.setEndDays(parseInteger(parseJsonString(object,"有効期間日")));
                        model.setHalfDay(parseInteger(parseJsonString(object,"半日加算")));
                        model.setProfileNo(parseInteger(parseJsonString(object,"プロファイルNo")));
                        ticketModels.add(model);
                    }else if (json instanceof JSONArray) {
                        JSONArray ticketList = ticketObject.getJSONArray("Item");
                        for (int i=0; i<ticketList.length(); i++) {
                            JSONObject object = ticketList.getJSONObject(i);
                            TicketModel model = new TicketModel();
                            model.setId(parseJsonString(object,"ID"));
                            model.setType(parseJsonString(object,"チケット種類"));
                            model.setName(parseJsonString(object,"券種名"));
                            model.setNamePr(parseJsonString(object,"券種名印字"));
                            model.setNameAge(parseJsonString(object,"年齢層"));
                            model.setPrice(parseInteger(parseJsonString(object,"価格")));
                            model.setTaxRatio(parseFloat(parseJsonString(object,"消費税率")));
                            model.setTax(parseInteger(parseJsonString(object,"消費税額")));
                            JSONObject posObj = object.getJSONObject("位置");
                            model.setRowPos(parseInteger(parseJsonString(posObj, "行")));
                            model.setColPos(parseInteger(parseJsonString(posObj, "列")));
                            JSONObject colorObj = object.getJSONObject("配色");
                            model.setFgColor(parseJsonString(colorObj, "文字"));
                            model.setBgColor(parseJsonString(colorObj, "背景"));
                            model.setOrder(parseInteger(parseJsonString(object,"清算表示順")));
                            model.setEndDays(parseInteger(parseJsonString(object,"有効期間日")));
                            model.setHalfDay(parseInteger(parseJsonString(object,"半日加算")));
                            model.setProfileNo(parseInteger(parseJsonString(object,"プロファイルNo")));
                            ticketModels.add(model);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            cm.getTicketInfoFromLocal();
        }
    }

    public void getTicketInfoFromLocal () {
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        ticketTypes = query.getTicketTypes();
        if (ticketTypes.size() >= 2) {
            for (int i = 0; i < ticketTypes.size(); i++) {
                for (int j = i + 1; j < ticketTypes.size(); j++) {
                    TicketType type1 = ticketTypes.get(i);
                    TicketType type2 = ticketTypes.get(j);
                    if (type1.getOrder() > type2.getOrder()) {
                        ticketTypes.set(i, type2);
                        ticketTypes.set(j, type1);
                    }
                }
            }
        }
        printerInfos = query.getPrinterInfos();
        ticketModels = query.getTicketModels();
    }

    public boolean checkExistXML() {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/LabelPrinter");
        if(dir.exists()) {
            File file = new File(dir, Config.XML_NAME);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }

    private String readFromXml(String fname) {
        String aBuffer = "";
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/LabelPrinter");
        if(dir.exists()) {
            File file = new File(dir, fname);
            FileInputStream fIn = null;
            try {
                fIn = new FileInputStream(file);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                String aDataRow = "";
                while ((aDataRow = myReader.readLine()) != null) {
                    if(!aDataRow.equals("")) {
                        aDataRow = aDataRow.replace("\t", "");
                        aDataRow = aDataRow.replace("  ", "");
                        aBuffer += aDataRow;
                    }
                }
                myReader.close();
                if (cm.me != null) {
                    DbHelper dbHelper = new DbHelper(currentActivity);
                    Queries query = new Queries(null, dbHelper);
                    query.addReadXMLdata(fname, aBuffer);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return aBuffer;
    }

    public TicketModel getTicketModelFormPos (String ind, int row, int col) {
        for (TicketModel model : ticketModels) {
            if (model.getType().equals(ind) && model.getRowPos() == row && model.getColPos() == col)
                return model;
        }
        return null;
    }

    public String getUserInfo () {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd(E) hh:mm", Locale.JAPANESE);

        String dateStr = sdf.format(date);
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        HashMap map = query.getDeviceInfo();
        String deviceInfo = "";
        if (map != null) {
            final String[] items = currentActivity.getResources().getStringArray(R.array.device_place);
            deviceInfo = map.get("tanmatsumei") + " " + items[parseInteger((String) map.get("hanbaibasho")) - 1];
        }
        String info = cm.me!=null?cm.me.getName():"";
        return deviceInfo + " " + info + " " + dateStr;
    }

    public boolean checkDeviceName() {
        String deviceInfo = "";
        DbHelper dbHelper = new DbHelper(currentActivity);
        Queries query = new Queries(null, dbHelper);
        HashMap map = query.getDeviceInfo();
        if (map != null)
            deviceInfo = map.get("tanmatsumei") + "";
        return deviceInfo == "" ? false : true;
    }

    /**
     *
     * アラートを表示する。
     * */
    public void showAlertDlg(String title, String msg, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(currentActivity);
        alert.setTitle(title);
        alert.setMessage(msg);
        alert.setPositiveButton(currentActivity.getResources().getString(R.string.OK), positiveListener);
        if (negativeListener != null) alert.setNegativeButton(currentActivity.getResources().getString(R.string.Cancel), negativeListener);
        alert.create();
        alert.show();
    }

    public String numberFormat (int val) {
        String convert = String.valueOf(NumberFormat.getNumberInstance(Locale.getDefault()).format(val));
        return convert;
    }

    /**
    *
    * インターネット接続状態を確認する
    * */
    public boolean checkNetworkConnected () {
        /* ConnectivityManagerの取得 */
        ConnectivityManager connectivityManager = (ConnectivityManager) currentActivity.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = connectivityManager.getActiveNetworkInfo();

        if (nInfo == null) {
            return false;
        }

        if (nInfo.isConnected()) {
            /* NetWork接続可 */
//            if (nInfo.getTypeName().equals("WIFI")) {
//                return true;
//            } else if (nInfo.getTypeName().equals("mobile")) {
//                return true;
//            }
            return true;
        } else {
            /* NetWork接続不可 */
            return false;
        }
    }

    /**
     *
     * 必要な許可を確認する
     *
     * @param permissionsToRequest 必要な許可のリスト
     *
     * @return ArrayList 可能な許容のリスト
     * */
    public ArrayList checkPermissions(ArrayList<String> permissionsToRequest) {
        ArrayList<String> permissionsRejected = new ArrayList();
        for (String perms : permissionsToRequest) {
            if (!hasPermission(perms)) {
                permissionsRejected.add(perms);
            }
        }
        return permissionsRejected;
    }

    /**
     *
     * 許可の状態をを判断する
     *
     * @return boolean 許可の状態 true / false
     * */
    public boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (currentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    /**
     *
     * Androidのバージョンを判断する
     *
     * @return boolean Android6.0より低いバージョンの場合 false / true
     * */
    public boolean canAskPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return true;
        else return false;
    }

    /**
     * 日本の祝日を読み込みます
     *
     * @param context
     * */
    public void loadJSONFromAsset(Context context) {
        try {
            InputStream is = context.getAssets().open("public_holiday.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            String json = new String(buffer, "UTF-8");

            JSONObject object = new JSONObject(json);
            this.holidayList = object.getJSONArray("holidays");

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
    *
    * 月の第一週の日付を計算する
    *
    * @param calendar 月のCalendar
    *
    * @return int 日付の数
    * */
    public int getFirstDaysOfMonth(Calendar calendar) {
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        int blankies;

        if (firstDayOfMonth == Calendar.MONDAY) {
            blankies = 0;
        } else if (firstDayOfMonth < Calendar.MONDAY) {
            blankies = Calendar.SATURDAY - (Calendar.MONDAY - 1);
        } else {
            blankies = firstDayOfMonth - Calendar.MONDAY;
        }
        return blankies;
    }

    /**
     * 文字列配列から文字列を取得する
     *
     * @param arr 文字列配列
     * @return String 文字列
     * */
    public String stringFromStringArray(ArrayList<String> arr) {
        String str = "";
        int ii = 0;
        for (String item : arr) {
            if (ii == 0) {
                str = item;
            }else {
                str = str + ", " + item;
            }
            ii ++;
        }
        return str;
    }

    /**
     * HashMapから文字列を取得する
     *
     * @param jsonObj HashMap data
     * @return String 文字列
     * */
    public String convertToStringFromHashMap(HashMap<String, String> jsonObj) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : jsonObj.keySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            String value = jsonObj.get(key);
            try {
                try {
                    stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                stringBuilder.append("=");
                stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("error", e);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 文字列からHashMapを取得する
     *
     * @param str String 文字列
     * @return HashMap data
     * */
    public HashMap convertToHashMapFromString(String str) {
        HashMap<String, String> map = new HashMap<String, String>();

        String[] nameValuePairs = str.split("&");
        for (String nameValuePair : nameValuePairs) {
            String[] nameValue = nameValuePair.split("=");
            try {
                map.put(URLDecoder.decode(nameValue[0], "UTF-8"), nameValue.length > 1 ? URLDecoder.decode(
                        nameValue[1], "UTF-8") : "");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("This method requires UTF-8 encoding support", e);
            }
        }

        return map;
    }

    /**
     * 文字列からArrayListを取得する
     *
     * @param str String 文字列
     * @return ArrayList data
     * */
    public ArrayList convertToArrayListFromString(String str) {
        ArrayList<String> arr = new ArrayList<>();
        if (str == null || str.equals("")) return arr;
        String[] strArr = str.split(", ");
        for (String item : strArr) {
            arr.add(item);
        }
        return arr;
    }

    public double convertToMilisecondsFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public Date convertToDateFromMiliseconds(long sec) {
        Date date = new Date();
        date.setTime(sec);
        return date;
    }

    public String converToDateTimeFormatFromTime(long sec) {
        Date date = new Date();
        date.setTime(sec);
        return DateFormat.getDateTimeInstance().format(date).toString();
    }

    public int parseInteger(String str) {
        str = str.replace(" ", "");
        return str.equals("")?0:Integer.parseInt(str);
    }

    public float parseFloat(String str) {
        str = str.replace(" ", "");
        return str.equals("")?0:Float.parseFloat(str);
    }

    public String parseJsonString(JSONObject object, String key) {
        String str = "";
        try {
            str = object.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return str;
    }

    public float parseJsongDouble(String val) {
        float value = 8.0f;
        if (!val.equals("")) {
            value = Float.valueOf(val);
        }
        return value;
    }

    public String getHashCodeFromPass(String pass) {
        MessageDigest digest=null;
        String hash = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(pass.getBytes());

            hash = bytesToHexString(digest.digest());

        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return hash;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public String writeToFile(byte[] array, String fname) {
        String fPath = "";
        try {
            FileOutputStream stream = new FileOutputStream(fname);
            stream.write(array);
            stream.flush();
            stream.close();
            fPath = fname;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return fPath;
    }
}
