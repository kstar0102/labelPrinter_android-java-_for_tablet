package com.labelprintertest.android.DBManager;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.StrictMode;
import android.widget.Toast;

import com.labelprintertest.android.Common.Config;
import com.labelprintertest.android.Models.TicketInfo;
import com.labelprintertest.android.Models.User;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

public class APIManager {

    public APIManager() {

    }

    @SuppressLint("NewApi")
    public Connection connectionclass()
    {
        if (!cm.checkNetworkConnected())
            return null;
        Connection connection;

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try
        {
            String driverName = "net.sourceforge.jtds.jdbc.Driver";
            Class.forName(driverName).newInstance();;
            String url = "jdbc:jtds:sqlserver://"
                    + Config.SERVER_IP_ADDRESS
                    + ":" + Config.SERVER_PORT
                    + ";DatabaseName="
                    + Config.DB_NAME;
            String name = Config.USER_NAME;
            String pass = Config.PASSWORD;
            connection = DriverManager.getConnection(url, name, pass);

        }
        catch (SQLException se)
        {
            Toast.makeText(currentActivity, "Database Error : " + se.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        catch (ClassNotFoundException e)
        {
            Toast.makeText(currentActivity, "Database Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        catch (Exception e)
        {
            Toast.makeText(currentActivity, "Database Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        return connection;
    }

    public User loginToServer(String username, String pass) {
        User user = new User();
        Connection con = connectionclass();
        if (con != null) {
            Statement st = null;
            try {
                st = con.createStatement();
                ResultSet rs = st.executeQuery("select * from mst_user where userid='" + username + "' and password='" + pass + "'");
                ResultSetMetaData rsmd = rs.getMetaData();

                while (rs.next()) {
                    user.setId(rs.getString(1));
                    user.setPassword(rs.getString(2));
                    user.setName(rs.getString(3));
                }
                return user;
            } catch (SQLException e) {
                Toast.makeText(currentActivity, "Database Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                return null;
            }
        }

        return null;
    }

    public void syncFromServer() {//mst_ticket, mst_ticketstyle, mst_user, mst_kbn, mst_ui_tabticket, mst_ui_tab
        Connection con = connectionclass();
        if (con != null) {
            DbHelper dbHelper = new DbHelper(currentActivity);
            Queries query = new Queries(null, dbHelper);
            Statement st = null;
            try {
                st = con.createStatement();

                //mst_ticket
                ResultSet rs = st.executeQuery("select * from mst_ticket");
                rs.getMetaData();

                ArrayList<ContentValues> ticketModels = new ArrayList<>();
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("ticketid", rs.getInt(1));
                    values.put("kenshumei", rs.getString(2));
                    values.put("kenshumeiinji", rs.getString(3));
                    values.put("nenreiso", rs.getString(4));
                    values.put("kakaku", rs.getInt(5));
                    values.put("shohizeiritsu", rs.getString(6));
                    values.put("shohizeigaku", rs.getInt(7));
                    values.put("seisansortno", rs.getInt(8));
                    values.put("haikeishoku", rs.getString(9));
                    values.put("mojishoku", rs.getString(10));
                    values.put("yukokikanbi", rs.getInt(11));
                    values.put("hannichikasan", rs.getInt(12));
                    values.put("profileno", rs.getInt(13));
                    values.put("kikan_fr", cm.convertToMilisecondsFromDate(rs.getDate(14)));
                    values.put("kikan_to", cm.convertToMilisecondsFromDate(rs.getDate(15)));
                    values.put("sakuseiuserid", rs.getString(16));
                    values.put("sakuseinichiji", cm.convertToMilisecondsFromDate(rs.getDate(17)));
                    values.put("koshinuserid", rs.getString(18));
                    values.put("koshinnichiji", cm.convertToMilisecondsFromDate(rs.getDate(19)));

                    ticketModels.add(values);
                }
                query.addTableFromServer("mst_ticket", ticketModels);
//                Toast.makeText(currentActivity, "mst_ticket Database Rows = " + ticketModels.size(), Toast.LENGTH_LONG).show();

                //mst_ticketstyle
                rs = st.executeQuery("select * from mst_ticketstyle");
                rs.getMetaData();

                ArrayList<ContentValues> printerInfos = new ArrayList<>();
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("profileno", rs.getInt(1));
                    values.put("chohyokb", rs.getString(2));
                    values.put("areano", rs.getInt(3));
                    values.put("hyojikb", rs.getString(4));
                    values.put("injishubetsu", rs.getString(5));
                    values.put("fontmei", rs.getString(6));
                    values.put("fontsize", rs.getInt(7));
                    values.put("fontstyle_shatai", rs.getInt(8));
                    values.put("fontstyle_bold", rs.getInt(9));
                    values.put("shoshiki", rs.getString(10));
                    values.put("xposfrom", rs.getInt(11));
                    values.put("yposfrom", rs.getInt(12));
                    values.put("xposto", rs.getInt(13));
                    values.put("yposto", rs.getInt(14));
                    values.put("filemei", rs.getString(15));
                    Blob blob = rs.getBlob(16);
                    if (blob != null) {
                        int blobLength = (int) blob.length();
                        byte[] blobAsBytes = blob.getBytes(1, blobLength);
                        values.put("img", blobAsBytes);
//                        blob.free();
                    }
                    values.put("barcodetype", rs.getInt(17));
                    values.put("barcodeheight", rs.getInt(18));
                    values.put("kikan_fr", cm.convertToMilisecondsFromDate(rs.getDate(19)));
                    values.put("kikan_to", cm.convertToMilisecondsFromDate(rs.getDate(20)));
                    values.put("sakuseiuserid", rs.getString(21));
                    values.put("sakuseinichiji", cm.convertToMilisecondsFromDate(rs.getDate(22)));
                    values.put("koshinuserid", rs.getString(23));
                    values.put("koshinnichiji", cm.convertToMilisecondsFromDate(rs.getDate(24)));
                    values.put("shironuki", rs.getInt(25));

                    printerInfos.add(values);
                }
                query.addTableFromServer("mst_ticketstyle", printerInfos);

                //mst_user
                rs = st.executeQuery("select * from mst_user");
                rs.getMetaData();

                ArrayList<ContentValues> users = new ArrayList<>();
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("userid", rs.getString(1));
                    values.put("password", rs.getString(2));
                    values.put("username", rs.getString(3));
                    values.put("kengengrp", rs.getString(4));
                    values.put("sakuseiuserid", rs.getString(5));
                    values.put("sakuseinichiji", cm.convertToMilisecondsFromDate(rs.getDate(6)));
                    values.put("koshinuserid", rs.getString(7));
                    values.put("koshinnichiji", cm.convertToMilisecondsFromDate(rs.getDate(8)));

                    users.add(values);
                }
                query.addTableFromServer("mst_user", users);

                //mst_kbn
                rs = st.executeQuery("select * from mst_kbn");
                rs.getMetaData();

                ArrayList<ContentValues> kbns = new ArrayList<>();
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("kbtype", rs.getString(1));
                    values.put("kbcd", rs.getString(2));
                    values.put("kbmei", rs.getString(3));
                    values.put("biko", rs.getString(4));
                    values.put("sakuseiuserid", rs.getString(5));
                    values.put("sakuseinichiji", cm.convertToMilisecondsFromDate(rs.getDate(6)));
                    values.put("koshinuserid", rs.getString(7));
                    values.put("koshinnichiji", cm.convertToMilisecondsFromDate(rs.getDate(8)));

                    kbns.add(values);
                }
                query.addTableFromServer("mst_kbn", kbns);

                //mst_ui_tabticket
                rs = st.executeQuery("select * from mst_ui_tabticket");
                rs.getMetaData();

                ArrayList<ContentValues> tabTickets = new ArrayList<>();
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("tickettypecd", rs.getString(1));
                    values.put("xpos", rs.getInt(2));
                    values.put("ypos", rs.getInt(3));
                    values.put("ticketid", rs.getInt(4));
                    values.put("kikan_fr", cm.convertToMilisecondsFromDate(rs.getDate(5)));
                    values.put("kikan_to", cm.convertToMilisecondsFromDate(rs.getDate(6)));
                    values.put("sakuseiuserid", rs.getString(7));
                    values.put("sakuseinichiji", cm.convertToMilisecondsFromDate(rs.getDate(8)));
                    values.put("koshinuserid", rs.getString(9));
                    values.put("koshinnichiji", cm.convertToMilisecondsFromDate(rs.getDate(10)));

                    tabTickets.add(values);
                }
                query.addTableFromServer("mst_ui_tabticket", tabTickets);
//                Toast.makeText(currentActivity, "mst_ui_tabticket Database Rows = " + ticketModels.size(), Toast.LENGTH_LONG).show();

                //mst_ui_tab
                rs = st.executeQuery("select * from mst_ui_tab");
                rs.getMetaData();

                ArrayList<ContentValues> tabs = new ArrayList<>();
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("tickettypecd", rs.getString(1));
                    values.put("hyojikb", rs.getInt(2));
                    values.put("sortno", rs.getInt(3));
                    values.put("kikan_fr", cm.convertToMilisecondsFromDate(rs.getDate(4)));
                    values.put("kikan_to", cm.convertToMilisecondsFromDate(rs.getDate(5)));
                    values.put("sakuseiuserid", rs.getString(6));
                    values.put("sakuseinichiji", cm.convertToMilisecondsFromDate(rs.getDate(7)));
                    values.put("koshinuserid", rs.getString(8));
                    values.put("koshinnichiji", cm.convertToMilisecondsFromDate(rs.getDate(9)));

                    tabs.add(values);
                }
                query.addTableFromServer("mst_ui_tab", tabs);
            } catch (SQLException e) {
            }
        }
    }

    public void getCounterFromServer(String device_name) {
        Connection con = connectionclass();
        if (con != null) {
            DbHelper dbHelper = new DbHelper(currentActivity);
            Queries query = new Queries(null, dbHelper);
            Statement st = null;
            try {
                st = con.createStatement();

                //mst_counter
                ResultSet rs = st.executeQuery("select * from mst_counter where saibancd = '" + device_name +"'");
                rs.getMetaData();

                while (rs.next()) {
                    int receiptNum = query.getEndNumberWithSection(rs.getString(1));
                    if (receiptNum < rs.getInt(3)) {
                        ContentValues values = new ContentValues();
                        values.put("saibankb", rs.getString(1));
                        values.put("saibancd", device_name);
                        values.put("genzaino", rs.getInt(3));
                        values.put("sakuseiuserid", cm.me.getId());
                        values.put("sakuseinichiji", cm.convertToMilisecondsFromDate(new Date()));
                        values.put("koshinuserid", cm.me.getId());
                        values.put("koshinnichiji", cm.convertToMilisecondsFromDate(new Date()));
                        query.updateNumberMax(values);
                    }
                }
            } catch (SQLException e) {
            }
        }
    }

    public boolean syncToServer(Calendar calendar) {//mst_tanmatsu, mst_counter, dat_record, dat_ryoshu, dat_expense, dat_history
        Connection con = connectionclass();
        if (con != null) {
            unSyncToServer(calendar);
            DbHelper dbHelper = new DbHelper(currentActivity);
            Queries query = new Queries(null, dbHelper);
            Statement st = null;
            try {
                st = con.createStatement();

                //mst_tanmatsu
                HashMap map = query.getDeviceInfo();
                if (map != null) {
                    st.executeUpdate("DELETE FROM mst_tanmatsu where tanmatsumei = '" + map.get("tanmatsumei") +"'");
                    st.executeUpdate("INSERT INTO mst_tanmatsu VALUES(" +
                            "'"+ map.get("tanmatsumei") +"', " +
                            map.get("tanmatsuno") + ", " +
                            "'"+ map.get("hanbaibasho") +"', " +
                            "'"+ map.get("sakuseiuserid") +"', " +
                            "'"+ map.get("sakuseinichiji") +"', " +
                            "'"+ map.get("koshinuserid") +"', " +
                            "'"+ map.get("koshinnichiji") + "')");
                }

                //mst_counter
                ArrayList<HashMap> list = query.getNumberData(calendar);
                if (list.size() > 0) {
                    for (HashMap data : list) {
                        if (data != null) {
                            st.executeUpdate("DELETE FROM mst_counter where saibancd = '" + data.get("saibancd") +"'");
                            st.executeUpdate("INSERT INTO mst_counter VALUES(" +
                                    "'"+ data.get("saibankb") +"', " +
                                    "'"+ data.get("saibancd") +"', " +
                                    data.get("genzaino") + ", " +
                                    "'"+ data.get("sakuseiuserid") +"', " +
                                    "'"+ data.get("sakuseinichiji") +"', " +
                                    "'"+ data.get("koshinuserid") +"', " +
                                    "'"+ data.get("koshinnichiji") + "')");
                        }
                    }
                }

                //dat_record
                list = query.getSellData(calendar);
                if (list.size() > 0) {
                    for (HashMap data : list) {
                        if (data != null) {
                            st.executeUpdate("INSERT INTO dat_record VALUES(" +
                                    data.get("uriageno") + ", " +
                                    data.get("gyono") + ", " +
                                    "'"+ data.get("tanmatsumei") +"', " +
                                    "'"+ data.get("hanbaibasho") +"', " +
                                    "'"+ data.get("uriagenichiji") + "', " +
                                    data.get("ticketid") + ", " +
                                    data.get("uriagesuryo") + ", " +
                                    data.get("hanbaitanka") + ", " +
                                    data.get("hanbaikingaku") + ", " +
                                    "'"+ data.get("shohizeiritsu") +"', " +
                                    data.get("shohizeigaku") + ", " +
                                    "'"+ data.get("tickettypecd") +"', " +
                                    "'"+ data.get("meisho") +"', " +
                                    "'"+ data.get("haraimodoshikb") +"', " +
                                    "'"+ data.get("uriagekb") +"', " +
                                    data.get("shimeno") + ", " +
                                    "'"+ data.get("sakuseiuserid") +"', " +
                                    "'"+ data.get("sakuseinichiji") +"', " +
                                    "'"+ data.get("koshinuserid") +"', " +
                                    "'"+ data.get("koshinnichiji") + "')");
                        }
                    }
                }

                //dat_ryoshu
                list = query.getReceiptData(calendar);
                if (list.size() > 0) {
                    for (HashMap data : list) {
                        if (data != null) {
                            st.executeUpdate("INSERT INTO dat_ryoshu VALUES(" +
                                    data.get("ryoshuno") + ", " +
                                    "'"+ data.get("tanmatsumei") +"', " +
                                    "'"+ data.get("hanbaibasho") +"', " +
                                    "'"+ data.get("hakkonichiji") +"', " +
                                    data.get("ryoshukingaku") + ", " +
                                    "'"+ data.get("tadashigaki") +"', " +
                                    data.get("uriageno") + ", " +
                                    "'"+ data.get("sakuseiuserid") +"', " +
                                    "'"+ data.get("sakuseinichiji") +"', " +
                                    "'"+ data.get("koshinuserid") +"', " +
                                    "'"+ data.get("koshinnichiji") + "')");
                        }
                    }
                }

                //dat_expense
                list = query.getSettlementData(calendar);
                if (list.size() > 0) {
                    for (HashMap data : list) {
                        if (data != null) {
                            st.executeUpdate("INSERT INTO dat_expense VALUES(" +
                                    data.get("shimeno") + ", " +
                                    "'"+ data.get("tanmatsumei") +"', " +
                                    "'"+ data.get("hanbaibasho") +"', " +
                                    "'"+ data.get("shimebi") +"', " +
                                    data.get("uriagekb") + ", " +
                                    "'"+ data.get("haraimodoshikb") +"', " +
                                    data.get("suryo") + ", " +
                                    data.get("kingaku") + ", " +
                                    data.get("shohizei") + ", " +
                                    "'"+ data.get("sakuseiuserid") +"', " +
                                    "'"+ data.get("sakuseinichiji") +"', " +
                                    "'"+ data.get("koshinuserid") +"', " +
                                    "'"+ data.get("koshinnichiji") + "')");
                        }
                    }
                }

                //dat_history
                list = query.getReadXMLData(calendar);
                if (list.size() > 0) {
                    for (HashMap data : list) {
                        if (data != null) {
                            st.executeUpdate("INSERT INTO dat_history (tanmatsumei, filemei, filenaiyo, sakuseiuserid, sakuseinichiji, koshinuserid, koshinnichiji) VALUES(" +
                                    "'"+ data.get("tanmatsumei") +"', " +
                                    "'"+ data.get("filemei") +"', " +
                                    "'"+ data.get("filenaiyo") +"', " +
                                    "'"+ data.get("sakuseiuserid") +"', " +
                                    "'"+ data.get("sakuseinichiji") +"', " +
                                    "'"+ data.get("koshinuserid") +"', " +
                                    "'"+ data.get("koshinnichiji") + "')");
                        }
                    }
                }
            } catch (SQLException e) {
                Toast.makeText(currentActivity, "Database Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public boolean unSyncToServer(Calendar calendar) {//mst_tanmatsu, mst_counter, dat_record, dat_ryoshu, dat_expense, dat_history
        Connection con = connectionclass();
        if (con != null) {
            Statement st = null;
            try {
                DbHelper dbHelper = new DbHelper(currentActivity);
                Queries query = new Queries(null, dbHelper);
                st = con.createStatement();

                DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                try {
                    Date c_date = dateFormatter.parse(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE)+" 00:00");
                    calendar.setTime(c_date);
                    long startTime = calendar.getTimeInMillis();
                    c_date = dateFormatter.parse(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE)+" 23:59");
                    calendar.setTime(c_date);
                    long endTime = calendar.getTimeInMillis();

                    //mst_tanmatsu
                    st.executeUpdate("DELETE FROM dat_record WHERE tanmatsumei = '"+String.valueOf(query.getDeviceInfo().get("tanmatsumei")) +"' and koshinuserid = '" + cm.me.getId()
                            + "' and koshinnichiji > '" + cm.converToDateTimeFormatFromTime(startTime)
                            + "' and koshinnichiji <= '" + cm.converToDateTimeFormatFromTime(endTime) + "'");
                    String sss = "DELETE FROM dat_record WHERE tanmatsumei = '"+String.valueOf(query.getDeviceInfo().get("tanmatsumei")) +"' and koshinuserid = '" + cm.me.getId()
                            + "' and koshinnichiji > '" + cm.converToDateTimeFormatFromTime(startTime)
                            + "' and koshinnichiji <= '" + cm.converToDateTimeFormatFromTime(endTime) + "'";
                    //dat_ryoshu
                    st.executeUpdate("DELETE FROM dat_ryoshu WHERE tanmatsumei = '"+String.valueOf(query.getDeviceInfo().get("tanmatsumei")) +"' and koshinuserid = '" + cm.me.getId()
                            + "' and koshinnichiji > '" + cm.converToDateTimeFormatFromTime(startTime)
                            + "' and koshinnichiji <= '" + cm.converToDateTimeFormatFromTime(endTime) + "'");
                    //dat_expense
                    st.executeUpdate("DELETE FROM dat_expense WHERE tanmatsumei = '"+String.valueOf(query.getDeviceInfo().get("tanmatsumei")) +"' and koshinuserid = '" + cm.me.getId()
                            + "' and koshinnichiji > '" + cm.converToDateTimeFormatFromTime(startTime)
                            + "' and koshinnichiji <= '" + cm.converToDateTimeFormatFromTime(endTime) + "'");
                    //dat_history
                    st.executeUpdate("DELETE FROM dat_history WHERE tanmatsumei = '"+String.valueOf(query.getDeviceInfo().get("tanmatsumei")) +"' and koshinuserid = '" + cm.me.getId()
                            + "' and koshinnichiji > '" + cm.converToDateTimeFormatFromTime(startTime)
                            + "' and koshinnichiji <= '" + cm.converToDateTimeFormatFromTime(endTime) + "'");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                Toast.makeText(currentActivity, "Database Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public boolean syncHistoryToServer(ContentValues data) {
        Connection con = connectionclass();
        if (con != null) {
            Statement st = null;
            try {
                st = con.createStatement();
                st.executeUpdate("DELETE FROM dat_history where tanmatsumei = '" + data.get("tanmatsumei") +"' and filemei = '" + data.get("filemei") + "'");
                st.executeUpdate("INSERT INTO dat_history (tanmatsumei, filemei, filenaiyo, sakuseiuserid, sakuseinichiji, koshinuserid, koshinnichiji) VALUES(" +
                        "'"+ data.get("tanmatsumei") +"', " +
                        "'"+ data.get("filemei") +"', " +
                        "'"+ data.get("filenaiyo") +"', " +
                        "'"+ data.get("sakuseiuserid") +"', " +
                        "'"+ cm.converToDateTimeFormatFromTime(Long.parseLong(String.format( "%.0f",data.get("sakuseinichiji")))) +"', " +
                        "'"+ data.get("koshinuserid") +"', " +
                        "'"+ cm.converToDateTimeFormatFromTime(Long.parseLong(String.format( "%.0f",data.get("koshinnichiji")))) + "')");
            } catch (SQLException e) {
                return false;
            }
        }
        return true;
    }

    public boolean sendToServerDayEndSumData (ArrayList<TicketInfo> infos) {
        boolean isSuccess = false;
        Connection con = connectionclass();
        if (con != null) {
            Statement st = null;
            try {
                st = con.createStatement();
//                ResultSet rs = st.executeQuery("select * from mst_user where userid=" + username + " and password=" + pass);
//                ResultSetMetaData rsmd = rs.getMetaData();

                isSuccess = true;
            } catch (SQLException e) {
                return false;
            }
        }
        return isSuccess;
    }

    public boolean cancelToServerDayEndSumData (ArrayList<TicketInfo> infos) {
        boolean isSuccess = false;
        Connection con = connectionclass();
        if (con != null) {
            Statement st = null;
            try {
                st = con.createStatement();
//                ResultSet rs = st.executeQuery("select * from mst_user where userid=" + username + " and password=" + pass);
//                ResultSetMetaData rsmd = rs.getMetaData();

                isSuccess = true;
            } catch (SQLException e) {
                return false;
            }
        }
        return isSuccess;
    }

}
