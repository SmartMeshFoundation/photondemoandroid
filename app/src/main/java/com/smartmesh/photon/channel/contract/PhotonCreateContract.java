package com.smartmesh.photon.channel.contract;

import android.text.Editable;

import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.base.BaseView;


/**
 * photon 创建通道契约类
 * {@link com.smartmesh.photon.channel.PhotonCreateChannel}
 * */
public interface PhotonCreateContract {

    interface Presenter extends BasePresenter {

        void checkDepositValue(Editable s);

        void createChannelMethod(String tokenAddress, String partnerAddress, String depositBalance);

        /**
         * 获取链上 链下余额
         * */
        void getBalanceFromPhoton();
    }

    interface View extends BaseView{

        void createChannelStart();

        void createChannelSuccess(String response);

        void createChannelError();

        void photonNotStart();

        /**
         * 获取通道内总余额
         * @param jsonString  smt mesh总余额
         * */
        void getPhotonBalanceFromApiSuccess(String jsonString);

    }
}
