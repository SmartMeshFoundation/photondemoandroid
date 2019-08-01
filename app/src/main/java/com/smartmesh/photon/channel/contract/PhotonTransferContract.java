package com.smartmesh.photon.channel.contract;


import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.base.BaseView;

/**
 * photon 转账款契约类
 * {@link com.smartmesh.photon.channel.PhotonTransferUI}
 * */
public interface PhotonTransferContract {

    interface Presenter extends BasePresenter {

        /**
         * 获取通道列表
         * */
        void loadChannelList(boolean showToast);

        /**
         * 光子网络转账
         * @param token  转账的token类型
         * @param amount 转账金额
         * @param walletAddress 转账地址
         * @param showDialog 是否弹框提示
         * @param isDirect 是否是直接通道转账
         * @param filePath 转账路由信息
         * */
        void photonTransferMethod(String token, String amount, String walletAddress, boolean showDialog, boolean isDirect, String filePath);

        /**
         * 获取转账费用
         * @param token  转账的token类型
         * @param amount 转账金额
         * @param walletAddress 转账地址
         * */
        void getFeeFindPath(String token, String amount, String walletAddress);

        /**
         * 获取光子版本号
         * */
        void getPhotonVersionCode();
    }

    interface View extends BaseView{

        /**
         * 通道列表获取成功
         * */
        void loadChannelSuccess(String jsonString, boolean showToast);

        /**
         * 通道列表获取失败
         * */
        void loadChannelError(boolean showToast);

        /**
         * 转账检测
         * */
        void transferCheck();

        /**
         * 转账api调用成功 这个是photon transfer接口成功返回 并不代表一定成功
         * */
        void transferSuccess(String jsonString, boolean isDirect, String amount);

        /**
         * 转账api调用失败
         * */
        void transferError(boolean isDirect, String amount);

        /**
         * 获取路由信息成功  这个是photon findPath接口成功返回 并不代表一定成功
         * */
        void loadFindPathSuccess(String jsonString, String amount);

        /**
         * 获取路由信息失败
         * */
        void loadFindPathError(String amount);

        /**
         * 获取光子版本号
         * */
        void getVersionCodeSuccess(String version);

    }
}
