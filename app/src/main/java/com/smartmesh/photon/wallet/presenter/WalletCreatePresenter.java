package com.smartmesh.photon.wallet.presenter;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BasePresenterImpl;
import com.smartmesh.photon.wallet.contract.WalletCreateContract;

public class WalletCreatePresenter extends BasePresenterImpl<WalletCreateContract.View> implements WalletCreateContract.Presenter {

    public WalletCreatePresenter(WalletCreateContract.View view) {
        super(view);
    }

    @Override
    public void showPass(boolean isShowPassWord, EditText walletPwd,ImageView isShowPass) {
        if (isShowPassWord) { /* Set the EditText content is visible */
            walletPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isShowPass.setImageResource(R.mipmap.eye_open);
        } else {/* The content of the EditText set as hidden*/
            walletPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isShowPass.setImageResource(R.mipmap.eye_close);
        }
    }
}
