package com.smartmesh.photon.ui.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.smartmesh.photon.base.BasePresenterImpl;
import com.smartmesh.photon.custom.SubmitDialog;
import com.smartmesh.photon.ui.contract.AlertContract;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.util.Utils;
import com.smartmesh.photon.wallet.util.BitmapUtils;

public class AlertPresenterImpl extends BasePresenterImpl<AlertContract.View> implements AlertContract.Presenter {

    private AlertContract.View mView;

    public AlertPresenterImpl(AlertContract.View view){
        super(view);
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void photonWalletAddressDialog(Context context,String address) {
        SubmitDialog.Builder builder = new SubmitDialog.Builder(context);
        builder.showPhotonWalletAddressDialog(address);
        builder.setPhotonWalletListener(new SubmitDialog.Builder.photonWalletListener() {
            @Override
            public void photonClose() {
                mView.alertFinish();
            }

            @Override
            public void photonCopyAddress(String address) {
                Utils.copyText(context,address);
            }

            @Override
            public void photonSavePhoton(Bitmap qrBitMap) {
                try {
                    String qrPath = BitmapUtils.uploadZxing(context,qrBitMap,true,false);
                    MyToast.showToast(context,qrPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setCancelable(false);
    }
}
