package com.smartmesh.photon.ui.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BasePresenterImpl;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.ui.MainActivity;
import com.smartmesh.photon.ui.contract.MainContract;
import com.smartmesh.photon.util.SDCardCtrl;
import com.smartmesh.photon.wallet.WalletCreateActivity;
import com.smartmesh.photon.wallet.contract.WalletCreateContract;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.util.WalletInfoUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

import mobile.Mobile;
import mobile.SimpleAPI;

public class MainPresenter extends BasePresenterImpl<MainContract.View> implements MainContract.Presenter {

    private MainContract.View mView;

    public MainPresenter(MainContract.View view) {
        super(view);
        this.mView = view;
    }


    /**
     * get balance from photon
     * photon has stop          use   balanceAvailabelOnPhoton
     * photon has stated        use   getAssetsOnToken
     * */
    @Override
    public void getBalanceFromPhoton() {
        try {
            if (PhotonApplication.api != null) {
                String photonStr = PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS;
                String jsonString = PhotonApplication.api.getAssetsOnToken(photonStr);
                JSONObject jsoObject = new JSONObject(jsonString);
                int errorCode = jsoObject.optInt("error_code");
                if (errorCode == 0){
                    JSONArray array = jsoObject.optJSONArray("data");
                    if (array != null){
                        for (int i = 0 ; i < array.length() ; i++){
                            String tokenAddress = array.optJSONObject(i).optString("token_address");
                            String balanceInPhoton = array.optJSONObject(i).optString("balance_in_photon");
                            if (!TextUtils.isEmpty(balanceInPhoton)){
                                String photonBalance = new BigDecimal(balanceInPhoton)
                                        .divide(Convert.Unit.ETHER.getWeiFactor(), 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                                if (PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS.equalsIgnoreCase(tokenAddress)){
                                    mView.getBalanceSuccess(photonBalance);
                                }
                            }
                        }
                    }
                }
            }else{
                SimpleAPI simpleAPI = Mobile.newSimpleAPI(PhotonApplication.mContext.getFilesDir().getAbsolutePath() + SDCardCtrl.PHOTON_DATA, WalletInfoUtils.getInstance().getSelectAddress());
                String smtTokenBalance = simpleAPI.balanceAvailabelOnPhoton(PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS);
                simpleAPI.stop();
                if (!TextUtils.isEmpty(smtTokenBalance)){
                    JSONObject smtObject = new JSONObject(smtTokenBalance);
                    int errorCode = smtObject.optInt("error_code",-1);
                    if (errorCode == 0){
                        String photonSmtBalance = smtObject.optString("data");
                        if (!TextUtils.isEmpty(photonSmtBalance)){
                            String smtBalance = new BigDecimal(photonSmtBalance).divide(Convert.Unit.ETHER.getWeiFactor(), 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                            mView.getBalanceSuccess(smtBalance);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * verify goto photon method
     * */
    @Override
    public void intoPhotonTransfer() {
        StorableWallet storableWallet = WalletInfoUtils.getInstance().getStorableWallet();
        if (storableWallet == null){
            mView.walletIsEmpty();
            return;
        }

        if (storableWallet.getWalletType() == 1){
            mView.walletIsObserve();
            return;
        }

//        if (!storableWallet.isBackup()) {
//            mView.walletHasNotBackUp();
//            return;
//        }

        if (PhotonApplication.photonStatus && PhotonApplication.api == null){
            return;
        }

        if (PhotonApplication.api != null){
            mView.canIntoPhotonPay();
        }else{
            mView.inputPwdForPhoton();
        }
    }
}
