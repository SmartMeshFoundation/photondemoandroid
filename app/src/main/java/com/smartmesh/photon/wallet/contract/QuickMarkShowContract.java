package com.smartmesh.photon.wallet.contract;

import android.graphics.Bitmap;

import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.base.BaseView;
import com.smartmesh.photon.wallet.entity.StorableWallet;

public interface QuickMarkShowContract {

    interface Presenter extends BasePresenter {

        Bitmap createQRCodeBitmap(String content, int widthAndHeight);

        StorableWallet getWallet();
    }

    interface View extends BaseView{

    }
}
