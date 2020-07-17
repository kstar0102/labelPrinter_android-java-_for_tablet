package com.labelprintertest.android.PrinterManager;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.hardware.usb.UsbDevice;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.citizen.sdk.labelprint.LabelConst;
import com.citizen.sdk.labelprint.LabelDesign;
import com.citizen.sdk.labelprint.LabelPrinter;
import com.labelprintertest.android.Common.Common;
import com.labelprintertest.android.DBManager.DbHelper;
import com.labelprintertest.android.DBManager.Queries;
import com.labelprintertest.android.Models.PrinterInfo;
import com.labelprintertest.android.Models.TicketInfo;
import com.labelprintertest.android.Models.TicketModel;
import com.labelprintertest.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.labelprintertest.android.Common.Common.cm;
import static com.labelprintertest.android.Common.Common.currentActivity;

public class PrinterManager {

    private ArrayList<TicketInfo> ticketInfos;
    private ArrayList<TicketModel> selectticketInfos;

    public PrinterManager () {
        //
        // Runtime Permission
        //
        getPermissions(permissions);
    }

    public LabelPrinter printerStart(ArrayList<TicketInfo> infos, long receiptMoney, String receiptName, int payType) {
        this.ticketInfos = infos;
        // Constructor
        LabelPrinter printer = new LabelPrinter();

        // Set context
        printer.setContext(currentActivity);

        // Get Address
        UsbDevice usbDevice = null;                                               // null (Automatic detection)

        // Connect
        int result = printer.connect(LabelConst.CLS_PORT_USB, usbDevice);       // Android 3.1 ( API Level 12 ) or later
        if (LabelConst.CLS_SUCCESS == result) {
            // Set Property (Measurement unit)
            printer.setMeasurementUnit(LabelConst.CLS_UNIT_MILLI);

            for (int infoNum=0; infoNum<ticketInfos.size(); infoNum++) {
                TicketInfo info = ticketInfos.get(infoNum);
                TicketModel model = info.getModel();
                for (int modelNum=0; modelNum<info.getNum(); modelNum++) {
                    // Create an instance (LabelDesign Class)
                    LabelDesign design = new LabelDesign();

                    // Design label
                    getDesignFromTicketInfo(printer, design, model, payType);
                    cm.hasPrintingErr = false;

                    // Set Property (Tear Off)
                    printer.setMediaHandling(LabelConst.CLS_MEDIAHANDLING_CUT);

                    // Print design
                    int printResult = printer.print(design, 1);
                    if (LabelConst.CLS_SUCCESS != printResult) {
                        cm.hasPrintingErr = true;
                    }
                }
            }
            if (receiptMoney > 0) {
                // Set Property (Cut on last page)
                printer.setMediaHandling(LabelConst.CLS_MEDIAHANDLING_CUT);

                // Print label

                LabelDesign design1 = new LabelDesign();
                getDesignFromReceiptInfo(printer, design1, receiptMoney, receiptName);
                int printResult = printer.print(design1, 1);
                if (LabelConst.CLS_SUCCESS != printResult) {
                    cm.hasPrintingErr = true;
                }
            }

            // Disconnect
            printer.disconnect();
        } else {
            // Connect Error
            Toast.makeText(currentActivity, "Connect or Printer Error : " + Integer.toString(result), Toast.LENGTH_LONG).show();
            return null;
        }

        return printer;
    }

    public LabelPrinter selectprinterStart(ArrayList<TicketModel> infos, long receiptMoney, String receiptName, int payType) {
        this.selectticketInfos = infos;
        // Constructor
        LabelPrinter printer = new LabelPrinter();

        // Set context
        printer.setContext(currentActivity);

        // Get Address
        UsbDevice usbDevice = null;                                               // null (Automatic detection)

        // Connect
        int result = printer.connect(LabelConst.CLS_PORT_USB, usbDevice);       // Android 3.1 ( API Level 12 ) or later
        if (LabelConst.CLS_SUCCESS == result) {
            // Set Property (Measurement unit)
            printer.setMeasurementUnit(LabelConst.CLS_UNIT_MILLI);

            for (int infoNum=0; infoNum<ticketInfos.size(); infoNum++) {
                TicketInfo info = ticketInfos.get(infoNum);
                TicketModel model = info.getModel();
                for (int modelNum=0; modelNum<info.getNum(); modelNum++) {
                    // Create an instance (LabelDesign Class)
                    LabelDesign design = new LabelDesign();

                    // Design label
                    getDesignFromTicketInfo(printer, design, model, payType);
                    cm.hasPrintingErr = false;

                    // Set Property (Tear Off)
                    printer.setMediaHandling(LabelConst.CLS_MEDIAHANDLING_CUT);

                    // Print design
                    int printResult = printer.print(design, 1);
                    if (LabelConst.CLS_SUCCESS != printResult) {
                        cm.hasPrintingErr = true;
                    }
                }
            }
            if (receiptMoney > 0) {
                // Set Property (Cut on last page)
                printer.setMediaHandling(LabelConst.CLS_MEDIAHANDLING_CUT);

                // Print label

                LabelDesign design1 = new LabelDesign();
                getDesignFromReceiptInfo(printer, design1, receiptMoney, receiptName);
                int printResult = printer.print(design1, 1);
                if (LabelConst.CLS_SUCCESS != printResult) {
                    cm.hasPrintingErr = true;
                }
            }

            // Disconnect
            printer.disconnect();
        } else {
            // Connect Error
            Toast.makeText(currentActivity, "Connect or Printer Error : " + Integer.toString(result), Toast.LENGTH_LONG).show();
            return null;
        }

        return printer;
    }

    public LabelPrinter receiptOnlyPrintStart(long receiptMoney, String receiptName) {
        // Constructor
        LabelPrinter printer = new LabelPrinter();

        // Set context
        printer.setContext(currentActivity);

        // Get Address
        UsbDevice usbDevice = null;                                               // null (Automatic detection)

        // Connect
        int result = printer.connect(LabelConst.CLS_PORT_USB, usbDevice);       // Android 3.1 ( API Level 12 ) or later
        if (LabelConst.CLS_SUCCESS == result) {
            LabelDesign design = new LabelDesign();

            // Set Property (Measurement unit)
            printer.setMeasurementUnit(LabelConst.CLS_UNIT_MILLI);

            // Set Property (Cut on last page)
            printer.setMediaHandling(LabelConst.CLS_MEDIAHANDLING_CUT);

            // Print label

            getDesignFromReceiptInfo(printer, design, receiptMoney, receiptName);
            cm.hasPrintingErr = false;
            int printResult = printer.print(design, 1);
            if (LabelConst.CLS_SUCCESS != printResult) {
                cm.hasPrintingErr = true;
            }

            // Disconnect
            printer.disconnect();
        } else {
            // Connect Error
            Toast.makeText(currentActivity, "Connect or Printer Error : " + Integer.toString(result), Toast.LENGTH_LONG).show();
            return null;
        }

        return printer;
    }

    public LabelPrinter settlementPrinterStart(String title, ArrayList<HashMap> mainList, ArrayList<HashMap> subList) {
        // Constructor
        LabelPrinter printer = new LabelPrinter();

        // Set context
        printer.setContext(currentActivity);

        // Get Address
        UsbDevice usbDevice = null;                                               // null (Automatic detection)

        // Connect
        int result = printer.connect(LabelConst.CLS_PORT_USB, usbDevice);       // Android 3.1 ( API Level 12 ) or later
        if (LabelConst.CLS_SUCCESS == result) {
            // Set Property (Measurement unit)
            printer.setMeasurementUnit(LabelConst.CLS_UNIT_MILLI);
            LabelDesign design = new LabelDesign();

            getDesignFromMainList(design, mainList, subList, title);

            printer.setMediaHandling(LabelConst.CLS_MEDIAHANDLING_CUT);
            int printResult = printer.print(design, 1);
            if (LabelConst.CLS_SUCCESS != printResult) {
                cm.hasPrintingErr = true;
            }

            // Disconnect
            printer.disconnect();
        } else {
            // Connect Error
            Toast.makeText(currentActivity, "Connect or Printer Error : " + Integer.toString(result), Toast.LENGTH_LONG).show();
            return null;
        }

        return printer;
    }

    private void getDesignFromTicketInfo (LabelPrinter printer, LabelDesign design, TicketModel model, int payType) {

        if (cm.printerInfos.size() >0) {
            for (PrinterInfo info : cm.printerInfos) {
                if (info.getType().equals("RYOSHUSHO") || info.getIsShown().equals("0") || info.getProfileNo() != model.getProfileNo())
                    continue;
                String content = "";
                Calendar nowDate = Calendar.getInstance();

                String fontName = info.getFont();
                int fontSize = (int) info.getFontSize();
                int kanjiSize = LabelConst.CLS_PRT_FNT_KANJI_SIZE_16;
                if (fontSize <= 16) {
                    kanjiSize = LabelConst.CLS_PRT_FNT_KANJI_SIZE_16;
                }else if (fontSize > 16 && fontSize <= 24) {
                    kanjiSize = LabelConst.CLS_PRT_FNT_KANJI_SIZE_24;
                }else if (fontSize > 24 && fontSize <= 32) {
                    kanjiSize = LabelConst.CLS_PRT_FNT_KANJI_SIZE_32;
                }else if (fontSize > 32 && fontSize <= 48) {
                    kanjiSize = LabelConst.CLS_PRT_FNT_KANJI_SIZE_48;
                }

                int style = LabelConst.CLS_FNT_DEFAULT;
                if (info.getIsBold() > 0) {
                    if (info.getIsItalic() > 0) {
                        style = (LabelConst.CLS_FNT_BOLD | LabelConst.CLS_FNT_ITALIC);
                    }else {
                        style = LabelConst.CLS_FNT_BOLD;
                    }
                }else {
                    if (info.getIsItalic() > 0) {
                        style = LabelConst.CLS_FNT_ITALIC;
                    }
                }

                int startX = (int) (info.getStartX()); // mm
                int startY = (int) (info.getEndY()); // mm
                int endX = (int) (info.getEndX()); // mm
                int endY = (int) (info.getStartY()); // mm

                if (info.getPrinterType().equals("TEXT")) {
                    content = fillterPrintItem(info.getFormat(), model, payType);
                    if (info.getWhiteFlag() == 1) {
                        if (content.length() > 0) {
                            design.fillRect(startX, startY, (int) (fontSize * content.length() * 3.78f), endY - startY, LabelConst.CLS_SHADED_PTN_1);
                        }
                    }
                    design.drawTextLocalFont(content, Typeface.create(fontName, Typeface.NORMAL),
                            LabelConst.CLS_RT_NORMAL, 100, 100, fontSize,
                            style, startX, startY,
                            LabelConst.CLS_PRT_RES_203, LabelConst.CLS_UNIT_MILLI);


                }else if (info.getPrinterType().equals("IMAGE")) {
                    if (info.getImgData() != null) {
                        String fname = info.getFileName();
                        design.drawBitmap (fname, LabelConst.CLS_RT_NORMAL, endX - startX, endY - startY, startX, startY);
//                    }else {
//                        File root = android.os.Environment.getExternalStorageDirectory();
//                        String fname = root.getAbsolutePath() + "/LabelPrinter/Images/ticket_img.png";
//                        design.drawBitmap (fname, LabelConst.CLS_RT_NORMAL, endX - startX, endY - startY, startX, startY);
                    }
                }else if (info.getPrinterType().equals("BARCODE")) {
                    content = info.getBarcode();
                    design.drawBarCode(content, info.getBarcodeType(),
                            LabelConst.CLS_RT_NORMAL, 5, 2, info.getBarcodeHeight(), startX, startY,
                            LabelConst.CLS_BCS_TEXT_SHOW);
                }else if (info.getPrinterType().equals("LINE")) {
                    design.drawLine (startX, startY, endX, endY, 1);
                }
            }
        }else {
            Common.cm.showAlertDlg(currentActivity.getResources().getString(R.string.printer_connect_err_title),
                    currentActivity.getResources().getString(R.string.printer_connect_err_msg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, null);
            return;
        }
    }

    private void getDesignFromReceiptInfo (LabelPrinter printer, LabelDesign design, long receiptMoney, String receiptName) {
        if (cm.printerInfos.size() >0) {
            for (PrinterInfo info : cm.printerInfos) {
                if (info.getType().equals("TICKET") || info.getIsShown().equals("0"))
                    continue;
                String content = "";

                String fontName = info.getFont();
                int fontSize = (int) info.getFontSize();

                int style = LabelConst.CLS_FNT_DEFAULT;
                if (info.getIsBold() > 0) {
                    if (info.getIsItalic() > 0) {
                        style = (LabelConst.CLS_FNT_BOLD | LabelConst.CLS_FNT_ITALIC);
                    }else {
                        style = LabelConst.CLS_FNT_BOLD;
                    }
                }else {
                    if (info.getIsItalic() > 0) {
                        style = LabelConst.CLS_FNT_ITALIC;
                    }
                }

                int startX = (int) (info.getStartX()); // mm
                int startY = (int) (info.getEndY()); // mm
                int endX = (int) (info.getEndX()); // mm
                int endY = (int) (info.getStartY()); // mm

                if (info.getPrinterType().equals("TEXT")) {
                    content = info.getFormat();
                    if (content.contains("{KINGAKU}")) {
                        content = content.replace("{KINGAKU}", "¥" + cm.numberFormat((int) receiptMoney) + "");
                    }
                    if (content.contains("{TADASHIGAKI}")) {
                        content = content.replace("{TADASHIGAKI}", receiptName);
                    }
                    if (content.contains("{RYOSHUSHONO}")) {
                        DbHelper dbHelper = new DbHelper(currentActivity);
                        Queries query = new Queries(null, dbHelper);
                        int receiptNum = query.getEndNumberWithSection("RYOSHUSHO");
                        content = content.replace("{RYOSHUSHONO}", receiptNum + "");
                    }
                    Calendar nowDate = Calendar.getInstance();
                    nowDate.setTime(new Date());

                    if (content.contains("{GENZAINICHIJI}")) {
                        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        content = content.replace("{GENZAINICHIJI}", f.format(nowDate.getTime()));
                    }
                    if (info.getWhiteFlag() == 1) {
                        if (content.length() > 0) {
                            design.fillRect(startX, startY, (int) (fontSize * content.length() * 3.78f), endY - startY, LabelConst.CLS_SHADED_PTN_1);
                        }
                    }
                    design.drawTextLocalFont(content, Typeface.create(fontName, Typeface.NORMAL),
                            LabelConst.CLS_RT_NORMAL, 100, 100, fontSize,
                            style, startX, startY,
                            LabelConst.CLS_PRT_RES_203, LabelConst.CLS_UNIT_MILLI);
                }else if (info.getPrinterType().equals("IMAGE")) {
                    if (info.getImgData() != null) {
                        String fname = info.getFileName();
                        design.drawBitmap (fname, LabelConst.CLS_RT_NORMAL, endX - startX, endY - startY, startX, startY);
//                    }else {
//                    File root = android.os.Environment.getExternalStorageDirectory();
//                    String fname = root.getAbsolutePath() + "/LabelPrinter/Images/receipt_img.png";
//                    design.drawBitmap (fname, LabelConst.CLS_RT_NORMAL, endX - startX, endY - startY, startX, startY);
                    }
                }else if (info.getPrinterType().equals("LINE")) {
                    design.drawLine (startX, startY, endX, endY, 1);
                }
            }
        }else {
            Common.cm.showAlertDlg(currentActivity.getResources().getString(R.string.printer_connect_err_title),
                    currentActivity.getResources().getString(R.string.printer_connect_err_msg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, null);
            return;
        }
    }

    private void getDesignFromMainList(LabelDesign design, ArrayList<HashMap> mainList, ArrayList<HashMap> subList, String title) {

        HashMap header = new HashMap();
        header.put("code", currentActivity.getResources().getString(R.string.lb_code));
        header.put("type", currentActivity.getResources().getString(R.string.lb_type));
        header.put("unit", currentActivity.getResources().getString(R.string.lb_unit));
        header.put("sell", currentActivity.getResources().getString(R.string.lb_sell));
        header.put("refund", currentActivity.getResources().getString(R.string.lb_refund));
        header.put("total", currentActivity.getResources().getString(R.string.lb_total));
        header.put("price", currentActivity.getResources().getString(R.string.lb_price));
        mainList.add(0, header);

        int startY = 0;
        for (int i=mainList.size()-1; i>=0; i--) {
            design.drawLine (0, startY, 1000, startY, 2);
            startY += 20;
            HashMap map = mainList.get(i);
            if (i == 0) {
                design.drawTextLocalFont(String.valueOf(map.get("code")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 10, startY);
                design.drawTextLocalFont(String.valueOf(map.get("type")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 130, startY);
                design.drawTextLocalFont(String.valueOf(map.get("unit")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 330, startY);
                design.drawTextLocalFont(String.valueOf(map.get("sell")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 480, startY);
                design.drawTextLocalFont(String.valueOf(map.get("refund")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 560, startY);
                design.drawTextLocalFont(String.valueOf(map.get("total")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 640, startY);
                design.drawTextLocalFont(String.valueOf(map.get("price")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 740, startY);
            }else {
                if (!String.valueOf(map.get("code")).equals(""))
                    design.drawTextLocalFont(String.valueOf(map.get("code")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 10, startY);
                design.drawTextLocalFont(String.valueOf(map.get("type")), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 130, startY);
                if (!String.valueOf(map.get("unit")).equals(""))
                    design.drawTextLocalFont(String.valueOf(cm.numberFormat((int) map.get("unit"))), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 330, startY);
                design.drawTextLocalFont(String.valueOf(cm.numberFormat((int) map.get("sell"))), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 480, startY);
                design.drawTextLocalFont(String.valueOf(cm.numberFormat((int) map.get("refund"))), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 560, startY);
                design.drawTextLocalFont(String.valueOf(cm.numberFormat((int) map.get("total"))), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 640, startY);
                design.drawTextLocalFont(String.valueOf(cm.numberFormat((int) map.get("price"))), Typeface.SERIF,
                        LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                        LabelConst.CLS_FNT_BOLD, 740, startY);
            }
            startY += 60;
        }
        design.drawLine (0, startY, 1000, startY, 3);
        startY += 50;

        for (int i=subList.size()-1; i>=0; i--) {
            design.drawLine (0, startY, 1000, startY, 2);
            startY += 20;
            HashMap map = subList.get(i);
            design.drawTextLocalFont(String.valueOf(map.get("title")), Typeface.SERIF,
                    LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                    LabelConst.CLS_FNT_BOLD, 10, startY);
            design.drawTextLocalFont(String.valueOf(map.get("num")), Typeface.SERIF,
                    LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                    LabelConst.CLS_FNT_BOLD, 210, startY);
            design.drawTextLocalFont(String.valueOf(cm.numberFormat((int) map.get("price"))), Typeface.SERIF,
                    LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                    LabelConst.CLS_FNT_BOLD, 310, startY);
            startY += 60;
        }
        design.drawLine (0, startY, 1000, startY, 3);
        startY += 50;
        design.drawTextLocalFont(title, Typeface.SERIF,
                LabelConst.CLS_RT_NORMAL, 100, 100, 10,
                LabelConst.CLS_FNT_BOLD, 50, startY);
    }

    private String fillterPrintItem (String item, TicketModel model, int payType) {
        String fillterStr = item;
        Calendar nowDate = Calendar.getInstance();
        if (fillterStr.contains("{HAKKENBI}")) {
            fillterStr = fillterStr.replace("{HAKKENBI}", nowDate.get(Calendar.YEAR) + "/" + (nowDate.get(Calendar.MONTH)+1) + "/" + nowDate.get(Calendar.DATE) + "");
        }
        if (fillterStr.contains("(HAKKENBI2}")) {
            fillterStr = fillterStr.replace("{HAKKENBI2}", nowDate.get(Calendar.YEAR) + " " + (nowDate.get(Calendar.MONTH)+1) + " " + nowDate.get(Calendar.DATE) + "");
        }
        if (fillterStr.contains("{HAKKENBI-YEA}")) {
            fillterStr = fillterStr.replace("{HAKKENBI-YEA}", nowDate.get(Calendar.YEAR) + "");
        }
        if (fillterStr.contains("{HAKKENBI-MON}")) {
            fillterStr = fillterStr.replace("{HAKKENBI-MON}", (nowDate.get(Calendar.MONTH)+1) + "");
        }
        if (fillterStr.contains("{HAKKENBI-DAY}")) {
            fillterStr = fillterStr.replace("{HAKKENBI-DAY}", nowDate.get(Calendar.DATE) + "");
        }

        nowDate.setTime(new Date());
        int end = model.getEndDays();
        if (end > 0)
            end = end - 1;
        long month = 3600 * 24 * end * 1000L;
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(nowDate.getTimeInMillis() + month);

        if (fillterStr.contains("{YUKOKIGEN}")) {
            fillterStr = fillterStr.replace("{YUKOKIGEN}", endDate.get(Calendar.YEAR) + "/" + (endDate.get(Calendar.MONTH)+1) + "/" + endDate.get(Calendar.DATE) + "");
        }
        if (fillterStr.contains("{YUKOKIGEN2}")) {
            fillterStr = fillterStr.replace("{YUKOKIGEN2}", endDate.get(Calendar.YEAR) + " " + (endDate.get(Calendar.MONTH)+1) + " " + endDate.get(Calendar.DATE) + "");
        }
        if (fillterStr.contains("{YUKOKIGEN-YEA}")) {
            fillterStr = fillterStr.replace("{YUKOKIGEN-YEA}", endDate.get(Calendar.YEAR) + "");
        }
        if (fillterStr.contains("{YUKOKIGEN-MON}")) {
            fillterStr = fillterStr.replace("{YUKOKIGEN-MON}", (endDate.get(Calendar.MONTH)+1) + "");
        }
        if (fillterStr.contains("{YUKOKIGEN-DAY}")) {
            fillterStr = fillterStr.replace("{YUKOKIGEN-DAY}", endDate.get(Calendar.DATE) + "");
        }
        if (fillterStr.contains("{YUKOKIGEN-AMPM}")) {
            if (model.getHalfDay() == 1) {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    Date limitTime = f.parse(nowDate.get(Calendar.YEAR) + "-" + (nowDate.get(Calendar.MONTH) + 1) + "-" + nowDate.get(Calendar.DATE) + " " + "09:50");
                    Calendar limitDate = Calendar.getInstance();
                    limitDate.setTime(limitTime);
                    long nowSec = nowDate.getTimeInMillis();
                    long limitSec = limitDate.getTimeInMillis();
                    if (nowSec > limitSec) {
                        fillterStr = fillterStr.replace("{YUKOKIGEN-AMPM}", "午前");
                    }else {
                        fillterStr = fillterStr.replace("{YUKOKIGEN-AMPM}", "午後");
                    }
                } catch (ParseException e) {
                    fillterStr = fillterStr.replace("{YUKOKIGEN-AMPM}", "午前");
                }
            }else {
                fillterStr = fillterStr.replace("{YUKOKIGEN-AMPM}", "");
            }
        }
        if (fillterStr.contains("{KENSHUMEI}")) {
            String ticketName = model.getNamePr();
            if (ticketName.equals(""))
                ticketName = model.getName();
            fillterStr = fillterStr.replace("{KENSHUMEI}", ticketName);
        }
        if (fillterStr.contains("{TANKA}")) {
            fillterStr = fillterStr.replace("{TANKA}", "¥" + cm.numberFormat(model.getPrice()) + "");
        }
        if (fillterStr.contains("{NENREISO}")) {
            fillterStr = fillterStr.replace("{NENREISO}", model.getNameAge());
        }
        if (fillterStr.contains("{KAKAKU}")) {
            fillterStr = fillterStr.replace("{KAKAKU}", "¥" + cm.numberFormat(model.getPrice()) + "");
        }
        if (fillterStr.contains("{GENZAINICHIJI}")) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            fillterStr = fillterStr.replace("{GENZAINICHIJI}", f.format(nowDate.getTime()));
        }

        if (fillterStr.contains("{TANMATSUNO}")) {
            DbHelper dbHelper = new DbHelper(currentActivity);
            Queries query = new Queries(null, dbHelper);
            HashMap initInfo = query.getDeviceInfo();

            if (initInfo == null) {
                fillterStr = fillterStr.replace("{TANMATSUNO}", "0");
            }else {
                fillterStr = fillterStr.replace("{TANMATSUNO}", initInfo.get("tanmatsuno").toString());
            }
        }

        if (payType == 2) {
            if (fillterStr.contains("{URIKAKEMARK}"))
                fillterStr = fillterStr.replace("{URIKAKEMARK}", "●");
        }

        return fillterStr;
    }


            /**
             * Permissions
             * @see <a href="https://developer.android.com/guide/topics/security/permissions#perm-groups">Dangerous permissions and permission groups.</a>
             */
    final String[] permissions = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    /** requestCode for {@link ActivityCompat.OnRequestPermissionsResultCallback} @Override#onRequestPermissionsResult*/
    private static final int PERMISSION_REQUEST_CODE = 1234;

    /**
     * Get the permissions.
     * @param permissions ex. {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}
     */
    private void getPermissions(@NonNull final String[] permissions) {

        if (!checkPermissions(permissions)) {
            requestPermissions(permissions);
        }
    }
    /**
     * Checks whether this app has all permissions.<br>
     * @param permissions ex. {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}
     * @return true:All permissions granted, false:Permission denied
     */
    private boolean checkPermissions(@NonNull final String[] permissions) {

        for (String permission : permissions) {
            int permissionResult = ContextCompat.checkSelfPermission(currentActivity, permission);
            if (permissionResult != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return false;				// non-permission
            }
        }
        return true;
    }
    /**
     * Requests permissions to be granted to this app.<br>
     * @param permissions ex. {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}
     */
    private void requestPermissions(@NonNull final String[] permissions) {
        ActivityCompat.requestPermissions(currentActivity, permissions, PERMISSION_REQUEST_CODE);
    }
}
