package com.labelprintertest.android.Utils;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.labelprintertest.android.Common.Common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@SuppressLint("SdCardPath")
public class ImageUtils {

    /**
     * イメージを圧縮する
     *
     * @param imageUri
     * @param height
     * @param width
     * @return
     */
    public static Bitmap compressImage(String imageUri, float height, float width) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float maxHeight = height;
        float maxWidth = width;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);

            return scaledBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * コンテンツURIからRealPathを取得する
     *
     * @param contentURI
     * @return
     */
    private static String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = Common.currentActivity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    /**
     * 画像サイズの計算
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /**
     * イメージを作成する
     *
     * @param bitmap
     * @param file_name
     * @param filepath
     */


    public static void createImage(Bitmap bitmap, String file_name, String filepath)
    {

        File path = new File(filepath);

        if(!path.exists())
        {
            path.mkdirs();
        }

        File file = new File(path,file_name);

        if (file.exists ())
        {
            file.delete ();
            file = new File(path,file_name);
            store_image(file,bitmap);
        }
        else
        {
            store_image(file,bitmap);
        }

    }

    /**
     * 画像を保存する
     *
     * @param file
     * @param bmp
     */
    public static void store_image(File file, Bitmap bmp)
    {
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ビューからイメージを取得する
     *
     * @param v 画像を取得するためのビュー
     * @return Bitmap data
     * */
    public static Bitmap getImageFromView(View v) {
        v.setDrawingCacheEnabled(true);
        if (v.getDrawingCache() != null) {
            Bitmap b1 = v.getDrawingCache();
            Bitmap b = Bitmap.createBitmap(b1.copy(Bitmap.Config.ARGB_8888, false));
            v.setDrawingCacheEnabled(false); // clear drawing cache
            return b;
        }else {
            v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false); // clear drawing cache
            return b;
        }
    }

    /**
     * 撮ったキャプチャを保存
     *
     * @param view
     * @param file 書き込み先ファイルfile
     */
    public static void saveCapture(View view, File file) {
        // キャプチャを撮る
        Bitmap capture = getImageFromView(view);
        store_image(file, capture);
    }

}
