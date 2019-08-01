package com.smartmesh.photon.wallet.presenter;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.base.BasePresenterImpl;
import com.smartmesh.photon.wallet.contract.QuickMarkShowContract;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.util.WalletStorage;

import java.util.ArrayList;
import java.util.Hashtable;

public class QuickMarkShowPresenter extends BasePresenterImpl<QuickMarkShowContract.View> implements QuickMarkShowContract.Presenter{

    public QuickMarkShowPresenter(QuickMarkShowContract.View view){
        super(view);
    }

    @Override
    public Bitmap createQRCodeBitmap(String content, int widthAndHeight) {
        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<>();
        qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        qrParam.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, qrParam);
            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            int[] data = new int[w * h];

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y)){
                        data[y * w + x] = 0xff000000;
                    }else{
                        data[y * w + x] = -1;
                    }
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(data, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public StorableWallet getWallet() {
        StorableWallet storableWallet = null;
        int index = -1;//Which one is selected
        ArrayList<StorableWallet> storableWallets = WalletStorage.getInstance(PhotonApplication.mContext).get();
        for (int i = 0 ; i < storableWallets.size(); i++){
            if (storableWallets.get(i).isSelect() ){
                WalletStorage.getInstance(PhotonApplication.mContext).updateWalletToList(PhotonApplication.mContext,storableWallets.get(i).getPublicKey(),false);
                index = i;
                storableWallet = storableWallets.get(i);
                break;
            }
        }
        if (index == -1 && storableWallets.size() > 0){
            storableWallet = storableWallets.get(0);
        }
        return storableWallet;
    }

}
