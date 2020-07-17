package com.labelprintertest.android.Models;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static com.labelprintertest.android.Common.Common.cm;

public class PrinterInfo {
    private int profileNo;
    private String type; //帳票区分
    private int printerNum; //印字番号
    private String printerType; //印字種別
    private String isShown; //表示区分
    private String font; //フォント
    private float fontSize; //フォントサイズ
    private int isItalic; //斜体
    private int isBold; //太字
    private String format; //書式
    private int startX; //X始点
    private int startY; //Y始点
    private int endX; //X終点
    private int endY; //Y終点
    private String fileName; //ファイル名
    private byte[] imgData; //画像ファイルのバイナリデータをセット
    private int barcodeType; //バーコードタイプ
    private int barcodeHeight; //バーコード高さ
    private String barcode; //コード
    private int whiteFlag; //文字を白抜き

    public PrinterInfo () {

    }

    public void setProfileNo(int val) {
        profileNo = val;
    }
    public int getProfileNo() {
        return profileNo;
    }

    public void setType(String val) {
        type = val;
    }
    public String getType() {
        return type;
    }

    public void setPrinterNum(int val) {
        printerNum = val;
    }
    public int getPrinterNum() {
        return printerNum;
    }

    public void setPrinterType(String val) {
        printerType = val;
    }
    public String getPrinterType() {
        return printerType;
    }

    public void setIsShown(String val) {
        isShown = val;
    }
    public String getIsShown() {
        return isShown;
    }

    public void setFont(String val) {
        font = val;
    }
    public String getFont() {
        return font;
    }

    public void setFontSize(float val) {
        fontSize = val;
    }
    public float getFontSize() {
        return fontSize;
    }

    public void setIsItalic(int val) {
        isItalic = val;
    }
    public int getIsItalic() {
        return isItalic;
    }

    public void setIsBold(int val) {
        isBold = val;
    }
    public int getIsBold() {
        return isBold;
    }

    public void setFormat(String val) {
        format = val;
    }
    public String getFormat() {
        return format;
    }

    public void setStartX(int val) {
        startX = val;
    }
    public int getStartX() {
        return startX;
    }

    public void setStartY(int val) {
        startY = val;
    }
    public int getStartY() {
        return startY;
    }

    public void setEndX(int val) {
        endX = val;
    }
    public int getEndX() {
        return endX;
    }

    public void setEndY(int val) {
        endY = val;
    }
    public int getEndY() {
        return endY;
    }

    public void setFileName(String val) {
        fileName = val;
    }
    public String getFileName() {
        return fileName;
    }

    public void setWhiteFlag(int val) {
        whiteFlag = val;
    }
    public int getWhiteFlag() {
        return whiteFlag;
    }

    public void setImgData(byte[] val, String fileName) {
        String originName = "";
        if (fileName != null && !fileName.equals("")) {
            String[] arr = fileName.split("\\\\");
            originName = arr[arr.length-1];
        }else {
            originName = "";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/LabelPrinter/Images");
        if(!dir.exists()) {
            dir.mkdirs();
        }
//        String imgName = "";
//        if (type.equals("TICKET"))
//            imgName = "ticket_img.png";
//        else
//            imgName = "receipt_img.png";
        File file = new File(dir, originName);
        if (val != null) {
            String fname = cm.writeToFile(val, file.getAbsolutePath());
            if (!fname.equals("")) {
                setFileName(fname);
            }
            imgData = val;
        }else {
            String fname = file.getAbsolutePath();
            setFileName(fname);
        }
    }
    public byte[] getImgData() {
        return imgData;
    }

    public void setBarcodeType(int val) {
        barcodeType = val;
    }
    public int getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeHeight(int val) {
        barcodeHeight = val;
    }
    public int getBarcodeHeight() {
        return barcodeHeight;
    }

    public void setBarcode(String val) {
        barcode = val;
    }
    public String getBarcode() {
        return barcode;
    }
}
