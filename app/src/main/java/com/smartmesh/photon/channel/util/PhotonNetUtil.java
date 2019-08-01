package com.smartmesh.photon.channel.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.util.ThreadPoolUtils;

/**
 * Created by H on 2018/5/11.
 * Description:
 */

public class PhotonNetUtil {

    private static volatile PhotonNetUtil S_INST;
    
    public static PhotonNetUtil getInstance() {
        if (S_INST == null) {
            synchronized (PhotonNetUtil.class) {
                if (S_INST == null) {
                    S_INST = new PhotonNetUtil();
                }
            }
        }
        return S_INST;

    }

    /**
     * 停止运行光子网络
     * */
    public void stopPhoton() {
        if (PhotonApplication.api == null && PhotonApplication.photonStatus){
            return;
        }
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                if (PhotonApplication.api != null) {
                    if(PhotonApplication.mPhotonSubscribe !=null){
                        PhotonApplication.mPhotonSubscribe.unsubscribe();
                    }
                    PhotonApplication.api.stop();
                    PhotonApplication.api = null;
                }
                PhotonApplication.photonStatusStatus = false;
                PhotonApplication.photonStatus = false;
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取当前wifi ip
     * */
    public String getCurWifiIp(){
        try {
            WifiManager wm = (WifiManager) PhotonApplication.mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String clientIP = null;
            if (wm != null) {
                clientIP = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            }
            return clientIP;
        }catch (Exception e){
            return null;
        }
    }
}
