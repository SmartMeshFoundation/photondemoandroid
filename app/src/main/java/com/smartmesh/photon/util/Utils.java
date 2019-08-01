package com.smartmesh.photon.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.smartmesh.photon.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    /**
     * send broadcast
     * @ param action filters
     * @ param bundle parameters
     */
    public static void intentAction(Context mContext, String action, Bundle bundle) {
        if (!TextUtils.isEmpty(action)) {
            Intent intent = new Intent(action);
            if (bundle != null)
                intent.putExtra(action, bundle);
            if (mContext != null)
                mContext.sendBroadcast(intent);
        }
    }

    public static void intentAction(Context mContext, Intent intent) {
        mContext.sendBroadcast(intent);
    }

    /**
     * According to the resolution of the mobile phone from dp units become px (pixels)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * copy the text to the clipboard
     * @ param context
     * @ param text text content
     * */
    public static  void copyText(Context context,String text) {
        // since API11 android is recommended to use android. The content. ClipboardManager
        // in order to compatible with low version. Here we use the old version of the android text. ClipboardManager, although deprecated, but not influence use.
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // The text content on the system clipboard。
        cm.setPrimaryClip(ClipData.newPlainText(null, text));
        MyToast.showToast(context,context.getString(R.string.copy_end));
    }

    public static void notifySystemUpdateFolder(Context context, File file) {
        //Comrade system update photo album
        int version = Build.VERSION.SDK_INT;
        if (version < 19) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + file.getParentFile().getAbsolutePath())));
        } else {
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
        }
    }


    public static String formatTxTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time * 1000);
    }


    /**
     * transfer record time
     * */
    public static String formatTransMsgTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm MM/dd");
        return format.format(time * 1000);
    }

    /**
     * System time is a wonderful work of 10 digits and 10 digits seconds value < / font >
     */
    public static String eventDetailTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return format.format(time * 1000);
    }

    /**
     * Record application error log when used
     */
    public static final String getSimpDate() {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        currentDate = Calendar.getInstance().getTime();
        return formatter.format(currentDate);
    }

    /**
     * Hide the soft keyboard
     */
    public static void hiddenKeyBoard(Activity activity) {

        try {
            if (activity == null) return;
            // Cancel the pop-up dialog box
            InputMethodManager manager = (InputMethodManager) activity.getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager.isActive()) { //Only when the keyboard is in the state of the pop-up to hide by: KNothing
                if (activity.getCurrentFocus() == null) {
                    return;
                }
                manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Access to program version number
     * */
    public static int getVersionCode(Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()Is your current class package name. 0 is represented for version information
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 110;
    }

    /**
     * Access to program version name
     * */
    public static String getVersionName(Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }
}