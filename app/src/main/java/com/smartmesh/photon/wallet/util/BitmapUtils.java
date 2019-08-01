package com.smartmesh.photon.wallet.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.smartmesh.photon.util.SDCardCtrl;
import com.smartmesh.photon.util.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Image tools
 */
public class BitmapUtils {

    public static Bitmap loadBitmap(String imagePath, Bitmap bitmap) {
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            // Reads the images in the camera direction information
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            // To calculate rotation Angle
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        if (digree != 0) {
            // Rotating images
            Matrix m = new Matrix();
            m.postRotate(digree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        return bitmap;
    }
    
    /**
     * @param image
     * @return The compressed image
     */
    public static Bitmap compressImage(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;
        if (image.hasAlpha()) {
            image.compress(Bitmap.CompressFormat.PNG, options, baos);// Quality compression method, 100 said here without compression, the compressed data stored in the baos
        } else {
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// Quality compression method, 100 said here without compression, the compressed data stored in the baos
        }
        while (baos.toByteArray().length / 1024 >= size) { // Cycle to judge if the compressed image is larger than the size KB, greater than continue to compress
            if (options == 30) {//Has been unable to compress again compression distortion
                break;
            }
            options -= 10;// Reduce 10 every time
            baos.reset();// Reset the baos namely empty baos
            if (image.hasAlpha()) {
                image.compress(Bitmap.CompressFormat.PNG, options, baos);// Compression options here %, the compressed data stored in the baos
            } else {
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// Compression options here %, the compressed data stored in the baos
            }
            
        }
        
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// The compressed data baos deposit into a ByteArrayInputStream
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// The ByteArrayInputStream data generated images
        try {
            if (baos != null) {
                baos.close();
            }
            if (isBm != null) {
                isBm.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (image != null) {
            image.recycle();
            image = null;
        }
        return bitmap;
    }
    

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {
        
        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bm.hasAlpha()) {
            bm.compress(Bitmap.CompressFormat.PNG, 90, baos);
        } else {
            bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        }
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    
    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        
        return BitmapFactory.decodeFile(filePath, options);
    }
    
    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    
    public static Uri saveBitmap2SD(Bitmap bitmap, String filePath, String fileName) {
        
        if (!new File(filePath).exists()) {
            new File(filePath).mkdir();
        }
        File file = new File(filePath, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    // With the current time to obtain the image name
    @SuppressLint("SimpleDateFormat")
    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmssSSS");
        return dateFormat.format(date) + ".jpg";
        // return "1.jpg";
    }
    
    // When first time to take pictures of the name
    @SuppressLint("SimpleDateFormat")
    public static String getPhotoFileName(int size) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmssSSS" + size + "_" + size);
        return dateFormat.format(date) + ".jpg";
        // return "1.jpg";
    }
    
    /**
     * Get photo address list
     *
     * @return list
     */
    private ArrayList<String> getImgPathList(Context c) {
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor = c.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data"}, null, null, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(1));// Add image path to the list
        }
        cursor.close();
        return list;
    }
    
    // Cut images
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    
    public static String BitmapToBase64String(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bmp.hasAlpha()) {
            bmp.compress(Bitmap.CompressFormat.PNG, 80, baos);
        } else {
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        }
        String result = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        try {
            if (baos != null) {
                baos.close();
            }
        } catch (Exception e) {
        }
        return result;
    }

    
    public static String uploadZxing(Context context, Bitmap bitmap, boolean isQrcode, boolean isNeedRecycle) throws Exception {
        String filePath = SDCardCtrl.getQrCodePath();
        String fileName = getPhotoFileName();
        if (!new File(filePath).exists()) {
            new File(filePath).mkdir();
        }
        File file = new File(filePath, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            if (bitmap.hasAlpha()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            bos.flush();
            bos.close();
            if (isNeedRecycle && bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Notification system update photo album
        Utils.notifySystemUpdateFolder(context, file);
        return file.getAbsolutePath();
    }
    
    public static String saveZxing2SD(Context context, Bitmap bitmap, boolean isNeedRecycle) throws Exception {
        String filePath = SDCardCtrl.getQrCodePath();
        String fileName = getPhotoFileName();
        if (!new File(filePath).exists()) {
            new File(filePath).mkdir();
        }
        File file = new File(filePath, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            if (bitmap.hasAlpha()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            }
            bos.flush();
            bos.close();
            if (isNeedRecycle && bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Notification system update photo album
        Utils.notifySystemUpdateFolder(context, file);
        return file.getAbsolutePath();
    }
    

}
