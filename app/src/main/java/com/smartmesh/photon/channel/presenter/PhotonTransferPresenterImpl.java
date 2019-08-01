package com.smartmesh.photon.channel.presenter;

import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.base.BasePresenterImpl;
import com.smartmesh.photon.channel.contract.PhotonTransferContract;
import com.smartmesh.photon.channel.util.ChannelNoteUtils;
import com.smartmesh.photon.util.ThreadPoolUtils;

import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

/**
 * photon 转账实现类
 * {@link  com.smartmesh.photon.channel.PhotonTransferUI}
 * */
public class PhotonTransferPresenterImpl extends BasePresenterImpl<PhotonTransferContract.View> implements PhotonTransferContract.Presenter{

    private PhotonTransferContract.View mView;

    public PhotonTransferPresenterImpl(PhotonTransferContract.View view){
        super(view);
        this.mView = view;
    }

    /**
     * load channel list
     */
    @Override
    public void loadChannelList(boolean showToast) {
        try {
            if (PhotonApplication.api != null) {
                String str = PhotonApplication.api.getChannelList();
                if (!TextUtils.isEmpty(str)) {
                    mView.loadChannelSuccess(str,showToast);
                }else{
                    mView.loadChannelError(false);
                }
            } else {
                mView.loadChannelError(showToast);
            }
        } catch (Exception e) {
            mView.loadChannelError(showToast);
        }
    }

    @Override
    public void photonTransferMethod(String token,String amount,String walletAddress,boolean showDialog,boolean isDirect,String filePath) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(walletAddress) || TextUtils.isEmpty(token)) {
            mView.transferError(isDirect,amount);
            return;
        }
        if (showDialog){
            mView.transferCheck();
        }
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                if (PhotonApplication.api != null) {
                    String balance = new BigDecimal(amount).multiply(Convert.Unit.ETHER.getWeiFactor()).stripTrailingZeros().toPlainString();
                    /**
                     * tokenAddress string– 交易token
                     * targetAddress string – 收款方地址
                     * amountstr string – 金额
                     * feestr string – 手续费金额
                     * secretStr string – 交易密码,可为""
                     * isDirect string – 是否直接通道交易
                     * data - 发送交易附带的消息
                     */
                    String jsonString = PhotonApplication.api.transfers(token,walletAddress, balance,"", isDirect,"",filePath);
                    mView.transferSuccess(jsonString,isDirect,amount);
                } else {
                    mView.transferError(isDirect,amount);
                }
            });
        } catch (Exception e) {
            mView.transferError(isDirect,amount);
        }
    }

    /**
     * 获取通道费用
     * targetStr 目标节点地址
     * tokenStr 转账token地址
     * amountstr l转账的金额
     * */
    @Override
    public void getFeeFindPath(String token,String amount, String walletAddress) {
        if (TextUtils.isEmpty(amount) || TextUtils.isEmpty(walletAddress) || TextUtils.isEmpty(token)) {
            mView.loadFindPathError(amount);
            return;
        }
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                if (PhotonApplication.api != null) {
                    String balance = new BigDecimal(amount).multiply(Convert.Unit.ETHER.getWeiFactor()).stripTrailingZeros().toPlainString();
                    String str = PhotonApplication.api.findPath(ChannelNoteUtils.checkAddress(walletAddress),token,balance);
                    if (!TextUtils.isEmpty(str)) {
                        mView.loadFindPathSuccess(str,amount);
                    }else{
                        mView.loadFindPathError(amount);
                    }
                } else {
                    mView.loadFindPathError(amount);
                }
            });
        } catch (Exception e) {
            mView.loadFindPathError(amount);
        }
    }

    /**
     * 获取光子版本号
     * */
    @Override
    public void getPhotonVersionCode() {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                if (PhotonApplication.api != null) {
                    String jsonString = PhotonApplication.api.version();
                    if (!TextUtils.isEmpty(jsonString)) {
                        try {
                            JSONObject object = new JSONObject(jsonString);
                            int errorCode = object.optInt("error_code");
                            if (errorCode == 0){
                                JSONObject dataObject = object.optJSONObject("data");
                                if (dataObject != null){
                                    String photonVersion = dataObject.optString("version");
                                    mView.getVersionCodeSuccess(photonVersion);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public void start() {

    }


}
