package com.smartmesh.photon.wallet.util;

import android.content.Context;
import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.wallet.entity.StorableWallet;

import java.util.ArrayList;

public class WalletInfoUtils {

    private static WalletInfoUtils instance;

    public static WalletInfoUtils getInstance() {
        if (instance == null) {
            synchronized (WalletStorage.class) {
                if (instance == null) {
                    instance = new WalletInfoUtils();
                }
            }
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }

    /**
     * Name to get the wallet
     * @ param context context Wallet name name is empty
     * */
    public static String getWalletName(Context context){
        int index= 1;
        String walletName;
        while (true){
            walletName  = context.getString(R.string.account)	+index;
            boolean foundSameName =false;
            for(StorableWallet storableWallet : WalletStorage.getInstance(context).get()){
                if(walletName.equals(storableWallet.getWalletName())){
                    foundSameName = true;
                    break;
                }
            }
            if(foundSameName){
                index++;
            }else{
                break;
            }
        }
        return walletName;
    }

    /**
     * Load or refresh the wallet information
     */
    public StorableWallet getStorableWallet() {
        int index = -1;//Which one is selected
        ArrayList<StorableWallet> storableWallets = WalletStorage.getInstance(PhotonApplication.mContext).get();
        StorableWallet storableWallet = null;
        for (int i = 0; i < storableWallets.size(); i++) {
            if (storableWallets.get(i).isSelect()) {
                WalletStorage.getInstance(PhotonApplication.mContext).updateWalletToList(PhotonApplication.mContext, storableWallets.get(i).getPublicKey(), false);
                index = i;
                storableWallet = storableWallets.get(i);
                break;
            }
        }
        if (index == -1 && storableWallets.size() > 0) {
            storableWallet = storableWallets.get(0);
        }
        return storableWallet;
    }


    public String getSelectAddress(){
        try {
            ArrayList<StorableWallet> storableWallets = WalletStorage.getInstance(PhotonApplication.mContext).get();
            if (storableWallets != null && storableWallets.size() > 0) {
                String selectAddress = "";
                for (int i = 0; i < storableWallets.size(); i++) {
                    if (storableWallets.get(i).isSelect()) {
                        selectAddress = storableWallets.get(i).getPublicKey();
                    }
                }
                if (TextUtils.isEmpty(selectAddress)) {
                    selectAddress = storableWallets.get(0).getPublicKey();
                }
                if (!selectAddress.startsWith("0x")) {
                    selectAddress = "0x" + selectAddress;
                }
                return selectAddress.trim();
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }

}
