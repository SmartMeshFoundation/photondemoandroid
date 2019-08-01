package com.smartmesh.photon.wallet.util;

import android.text.TextUtils;


import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.util.SDCardCtrl;

import java.io.File;
import java.util.ArrayList;

public class KeyStoreFileUtils {

    /**
     * keystore 文件名称前缀。。。。。
     * */
    public static String keystorePath = "UTC--2018-01-19T07-39-12.496246684Z--";

    /**
     * 获取keystore文件存储名字
     * */
    public static String getKeyStoreFileName(String walletAddress){
        if (walletAddress.startsWith("0x")){
            walletAddress = walletAddress.substring(2);
        }
        String keyStoreName = walletAddress.toLowerCase();
        try {
            File file = new File(PhotonApplication.mContext.getFilesDir(), SDCardCtrl.WALLET_PATH);
            File[] files = file.listFiles();
            if (files != null && files.length > 0){
                for (File file1 : files) {
                    String name = file1.getName();
                    if (name.toLowerCase().contains(walletAddress.toLowerCase())) {
                        keyStoreName = name;
                        break;
                    }
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
            keyStoreName = initLocalKeyStore(walletAddress);
        }
        return keyStoreName;
    }

    /**
     * 获取keystore文件存储名字
     * 可能存在多个相同地址的keystore,所以删除时候要全部删除
     * */
    public static ArrayList<String> getKeyStoreFileNameArray(String walletAddress){

        if (walletAddress.startsWith("0x")){
            walletAddress = walletAddress.substring(2);
        }

        ArrayList<String> arrayList = new ArrayList<>();

        try {
            File file = new File(PhotonApplication.mContext.getFilesDir(), SDCardCtrl.WALLET_PATH);
            File[] files = file.listFiles();
            if (files != null && files.length > 0){
                for (File file1 : files) {
                    String name = file1.getName();
                    if (name.toLowerCase().contains(walletAddress.toLowerCase())) {
                        arrayList.add(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            arrayList.add(initLocalKeyStore(walletAddress));
        }
        return arrayList;
    }

    private static String initLocalKeyStore(String str) {
        String address = "";
        if (!TextUtils.isEmpty(str)) {
            if (str.startsWith(keystorePath)) {
                String tempStr = str.substring(keystorePath.length());
                if (!tempStr.startsWith("0x")) {
                    tempStr = "0x" + tempStr;
                }
                address = keystorePath + tempStr;
            } else {
                if (!str.startsWith("0x")) {
                    str = "0x" + str;
                }
                address = keystorePath + str;
            }
        }
        return address;
    }
}
