package com.smartmesh.photon.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Folder control class
 */
public class SDCardCtrl {

    /**
     * The log cat Tag
     */
    public static final String TAG = "SDCheck";

    /**
     * 隐藏文件
     */
    public static String HIDDEN_ROOT_PATH = "/.photon";

    /**
     * 普通文件路径
     * */
    public static String ROOT_PATH = "/photon";


    /**
     * QRCODEPATH
     */
    public static String QR_CODE_PATH = "/qrcode";


    /**
     * ERRORLOGPATH
     */
    public static String ERROR_LOG_PATH = "/ErrorLog";



    /**
     * Wallet path
     * */
    public static String WALLET_PATH = "/spectrum/keystore";

    /**
     * photon db data
     * */
    public static String PHOTON_DATA = "/spectrum/photonData";


    /**
     * @return ROOTPATH
     */
    public static String getCtrlCPath() {
        return HIDDEN_ROOT_PATH;
    }

    public static String getQrCodePath() {
        return QR_CODE_PATH;
    }

    public static String getErrorLogPath() {
        return ERROR_LOG_PATH;
    }

    /**
     * 光子 错误日志
     */
    public static String PHOTON_ERROR_LOG = "/PhotonErrorLog";

    public static String getPhotonErrorLogPath() {
        return PHOTON_ERROR_LOG;
    }

    /**
     * @return Is or not exist SD card
     */
    public static boolean sdCardIsExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * <Build data file for this application >
     */
    public static void initPath(Context context) {
        String ROOT;
        if (sdCardIsExist()) {
            ROOT = Environment.getExternalStorageDirectory().getPath();
        } else {
            ROOT = "/mnt/sdcard";
        }
        if (HIDDEN_ROOT_PATH.equals("/.photon")) {
            //根目录
            HIDDEN_ROOT_PATH = ROOT + HIDDEN_ROOT_PATH;
            ROOT_PATH = ROOT + ROOT_PATH;
            PHOTON_ERROR_LOG = ROOT + PHOTON_ERROR_LOG;
            //显示文件目录
            QR_CODE_PATH = ROOT_PATH + QR_CODE_PATH;
            ERROR_LOG_PATH = ROOT_PATH + ERROR_LOG_PATH;
        }
        SDFileUtils.getInstance().createDir(HIDDEN_ROOT_PATH);
        SDFileUtils.getInstance().createDir(ROOT_PATH);
        SDFileUtils.getInstance().createDir(PHOTON_ERROR_LOG);
        SDFileUtils.getInstance().createDir(QR_CODE_PATH);
        SDFileUtils.getInstance().createDir(ERROR_LOG_PATH);
        SDFileUtils.getInstance().createDir(context.getFilesDir().getAbsolutePath() + WALLET_PATH);
        SDFileUtils.getInstance().createDir(context.getFilesDir().getAbsolutePath() + PHOTON_DATA);
    }

    /**
     * 启动光子之前 检测相关文件夹是否存在
     * */
    public static void checkPathExist(){
        SDFileUtils.getInstance().createDir(getCtrlCPath());
        SDFileUtils.getInstance().createDir(getPhotonErrorLogPath());
        SDFileUtils.getInstance().createDir(PhotonApplication.mContext.getFilesDir().getAbsolutePath() + WALLET_PATH);
        SDFileUtils.getInstance().createDir(PhotonApplication.mContext.getFilesDir().getAbsolutePath() + PHOTON_DATA);
    }

    /**
     * <Save the App crash info to sdcard>
     */
    public static String saveCrashInfoToFile(String excepMsg) {
        if (TextUtils.isEmpty(excepMsg)) {
            return "";
        }
        String errorlog = getErrorLogPath();
        FileWriter fw = null;
        PrintWriter pw = null;
        File logFile = null;
        try {
            StringBuilder logSb = new StringBuilder();
            logSb.append("crashlog");
            logSb.append("(");
            logSb.append(Utils.getSimpDate());
            logSb.append(")");
            logSb.append(".txt");
            logFile = new File(errorlog, logSb.toString());
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            fw = new FileWriter(logFile, true);
            pw = new PrintWriter(fw);
            pw.write(excepMsg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.flush();
                pw.close();
            }
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                }
            }
        }
        return logFile == null ? "" : logFile.getAbsolutePath();
    }

}
