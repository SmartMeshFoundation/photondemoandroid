package com.smartmesh.photon.channel.contract;


import com.smartmesh.photon.base.BaseFragmentPresenter;
import com.smartmesh.photon.base.BaseFragmentView;

/**
 * photon 转账列表契约类
 * {@link com.smartmesh.photon.channel.fragment.PhotonTransferListFragment}
 * */
public interface PhotonTransferListContract {

    interface Presenter extends BaseFragmentPresenter {

        /**
         * 获取发出的交易记录
         * */
        void getSentTransfers();

        /**
         * 获取收到的交易记录
         * */
        void getReceivedTransfers();

    }

    interface View extends BaseFragmentView<Presenter> {

        void getSentTransfersSuccess(String jsonString);

        void getSentTransfersError(String errorMessage);

        void getReceivedTransfersSuccess(String jsonString);

        void getReceivedTransfersError(String errorMessage);
    }
}
