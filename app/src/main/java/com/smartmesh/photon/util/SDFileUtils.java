package com.smartmesh.photon.util;

import android.os.Environment;

import java.io.File;

/**
 * SD card auxiliary class
 *
 * @author ck 1124
 */
public class SDFileUtils {
    private static SDFileUtils sdFile = null;

    public static synchronized SDFileUtils getInstance() {
        if (sdFile == null) {
            sdFile = new SDFileUtils();
        }
        return sdFile;
    }

    private SDFileUtils() {
        String basePath = "/.photon";
        createDir(getSDPath() + basePath);
    }

    /**
     * Check whether the SD card is inserted, and returns the path to the SD card
     */
    public static boolean SDCardIsOk() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * The SD path
     *
     * @return /sdcard
     */
    public String getSDPath() {
        // To determine whether a sd card
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdDir = Environment.getExternalStorageDirectory();// Access to the root directory
            return sdDir.getPath();
        }
        return "/mnt/sdcard";
    }

    /**
     * Create a folder
     *
     * @param dirName
     */
    public void createDir(String dirName) {
        File destDir = new File(dirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

}
