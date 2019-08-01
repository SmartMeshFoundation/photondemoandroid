package com.smartmesh.photon.channel.contract;

import com.smartmesh.photon.base.BaseFragmentPresenter;
import com.smartmesh.photon.base.BaseFragmentView;

/**
 * photon 合约调用契约类
 * {@link com.smartmesh.photon.channel.fragment.PhotonContractCallFragment}
 * */
public interface PhotonContractListContract {

    interface Presenter extends BaseFragmentPresenter {

        /**
         * 获取合约交易
         * Obtain contract transaction
         * */
        void getContractCallTxQuery();

    }

    interface View extends BaseFragmentView<Presenter> {

        void getContractCallTxQuerySuccess(String jsonString);

        void getContractCallTxQueryError(String errorMessage);

    }
}
