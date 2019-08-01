package com.smartmesh.photon.ui.contract;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.base.BaseView;

public interface MainContract {

    public interface View extends BaseView {

        void getBalanceSuccess(String balance);

        void getBalanceError();

        //钱包不存在
        void walletIsEmpty();

        //观察模式
        void walletIsObserve();

        //没有备份
        void walletHasNotBackUp();

        //可以直接进入光子
        void canIntoPhotonPay();

        //输入密码进入
        void inputPwdForPhoton();
    }

    public interface Presenter extends BasePresenter {

        /**
         * get smt balance from photon api
         * */
        void getBalanceFromPhoton();

        /**
         * into photon transfer method
         * */
        void intoPhotonTransfer();
    }
}
