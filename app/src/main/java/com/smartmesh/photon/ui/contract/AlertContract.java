package com.smartmesh.photon.ui.contract;

import android.content.Context;
import android.content.DialogInterface;

import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.base.BaseView;
import com.smartmesh.photon.wallet.entity.StorableWallet;

public interface AlertContract {

    interface Presenter extends BasePresenter {

        /**
         * 光子地址
         * */
        void photonWalletAddressDialog(Context context, String address);

    }

    interface View extends BaseView{

        /**
         * alertFinish
         * */
        void alertFinish();


    }
}
