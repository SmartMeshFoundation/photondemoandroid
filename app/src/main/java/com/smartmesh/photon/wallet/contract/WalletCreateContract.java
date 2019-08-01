package com.smartmesh.photon.wallet.contract;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.base.BaseView;

public interface WalletCreateContract {

    public interface View extends BaseView {

    }

    public interface Presenter extends BasePresenter {
        void showPass(boolean isShowPassWord, EditText walletPwd, ImageView isShowPass);
    }
}
