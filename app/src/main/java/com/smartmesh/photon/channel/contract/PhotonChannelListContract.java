package com.smartmesh.photon.channel.contract;

import android.content.Context;
import android.widget.TextView;

import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.base.BaseView;


/**
 * photon 通道列表契约类
 * {@link com.smartmesh.photon.channel.PhotonChannelList}
 * */
public interface PhotonChannelListContract {

    interface Presenter extends BasePresenter {

        /**
         * 设置光子网络状态
         * Set photonic network status
         * @param photonState 光子状态
         * */
        void setPhotonStatus(TextView photonState, Context context);

        /**
         * 检测地址钱包是否存在
         * @param context 上下文
         * @param walletPwd 钱包密码
         * @param walletAddress 钱包地址
         * */
        void checkWalletExist(Context context, final String walletPwd, String walletAddress);

        /**
         * 获取通道列表
         * Get channel list
         * */
        void loadChannelList(boolean showToast);

        /**
         * 提现
         * withdraw
         * */
        void photonWithDraw(int position, String channelIdentifierHashStr, String amountstr, String op);

        /**
         * 结算
         * settle
         * */
        void photonSettleChannel(int position, String channelIdentifierHashStr);

        /**
         * 关闭
         * close channel
         * */
        void photonCloseChannel(int position, String channelIdentifierHashStr, boolean isForced);

        /**
         * 获取链上 链下余额
         * get balance from photon
         * */
        void getBalanceFromPhoton();


    }

    interface View extends BaseView {

        /**
         * 获取通道列表成功
         * Get the channel list successfully
         * */
        void loadChannelSuccess(String jsonString);

        /**
         * 获取通道列表失败
         * Failed to get channel list
         * */
        void loadChannelError(boolean showToast);

        /**
         * 检测钱包密码成功
         * Check the wallet password successfully
         * */
        void checkWalletExistSuccess(String walletPwd);

        /**
         * 检测钱包密码失败
         * Failed to detect wallet password
         * */
        void checkWalletExistError();

        /**
         * 获取通道内总余额 链上余额
         * Get the total balance in the channel
         * @param jsonString  smt mesh总余额
         * */
        void getPhotonBalanceFromApiSuccess(String jsonString);

        /**
         * 提现请求成功
         * The withdrawal request is successful
         * */
        void photonWithdrawSuccess(int position, String jsonString);


        /**
         * 结算请求成功
         * Successful settle request
         * */
        void photonSettleSuccess(int position, String jsonString);

        /**
         * 关闭请求成功
         * Close request successful
         * */
        void photonCloseChannelSuccess(int position, String jsonString, boolean isForced);

        /**
         * 提现请求失败
         * 结算请求失败
         * api error
         * */
        void photonError();
    }
}
