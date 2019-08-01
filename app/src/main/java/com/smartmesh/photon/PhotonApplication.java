package com.smartmesh.photon;

import android.app.Application;
import android.content.Context;

import com.smartmesh.photon.channel.entity.PhotonStatusVo;
import com.smartmesh.photon.util.CrashHandler;
import com.smartmesh.photon.util.SDCardCtrl;

import mobile.API;
import mobile.Subscription;

public class PhotonApplication extends Application {

    public static Context mContext;
    public static API api;
    public static Subscription mPhotonSubscribe;
    public static PhotonStatusVo mPhotonStatusVo;

    //光子网络状态  可能正在启动 或者启动成功
    public static boolean photonStatus = false;
    public static boolean photonStatusStatus = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        SDCardCtrl.initPath(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
