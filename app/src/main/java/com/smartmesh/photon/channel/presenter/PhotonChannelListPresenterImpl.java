package com.smartmesh.photon.channel.presenter;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BasePresenterImpl;
import com.smartmesh.photon.channel.contract.PhotonChannelListContract;
import com.smartmesh.photon.channel.entity.PhotonStatusType;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.util.ThreadPoolUtils;
import com.smartmesh.photon.wallet.util.WalletStorage;

import org.json.JSONException;
import org.web3j.crypto.CipherException;

import java.io.IOException;

/**
 * photon 通道列表实现类
 * {@link com.smartmesh.photon.channel.PhotonChannelList}
 * */
public class PhotonChannelListPresenterImpl extends BasePresenterImpl<PhotonChannelListContract.View> implements PhotonChannelListContract.Presenter{

    private PhotonChannelListContract.View mView;

    public PhotonChannelListPresenterImpl(PhotonChannelListContract.View view){
        super(view);
        this.mView = view;
    }

    /**
     * set photo status
     * */
    @Override
    public void setPhotonStatus(TextView photonStatus,Context context) {
        if (photonStatus != null){
            if (PhotonApplication.mPhotonStatusVo != null) {
                if (PhotonStatusType.Connected == PhotonApplication.mPhotonStatusVo.getEthStatus()){
                    photonStatus.setText(context.getString(R.string.on_line));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        photonStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.shape_oval_45ffbf),null,null,null);
                    }else{
                        photonStatus.setCompoundDrawables(context.getResources().getDrawable(R.drawable.shape_oval_45ffbf),null,null,null);
                    }
                }else {
                    photonStatus.setText(context.getString(R.string.off_line));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        photonStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.shape_oval_cccccc),null,null,null);
                    }else{
                        photonStatus.setCompoundDrawables(context.getResources().getDrawable(R.drawable.shape_oval_cccccc),null,null,null);
                    }
                }
            }
        }
    }

    @Override
    public void checkWalletExist(Context context, String walletPwd, String walletAddress) {
        if (TextUtils.isEmpty(walletPwd)){
            MyToast.showToast(context,context.getString(R.string.wallet_pwd_error));
            return;
        }
        LoadingDialog.show(context,"");
        ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
            try {
                String address = walletAddress;
                if (!TextUtils.isEmpty(address) && !address.contains("0x")){
                    address = "0x" + address;
                }
                WalletStorage.getInstance(PhotonApplication.mContext).getFullWallet(context,walletPwd,address);
                mView.checkWalletExistSuccess(walletPwd);
            } catch (IOException e) {
                e.printStackTrace();
                mView.checkWalletExistError();
            } catch (JSONException e) {
                e.printStackTrace();
                mView.checkWalletExistError();
            } catch (CipherException e) {
                e.printStackTrace();
                mView.checkWalletExistError();
            }
        });
    }

    /**
     * load channel list
     * 读取数据库 不可能发生阻塞  需要统一确认是否要废弃线程池
     */
    @Override
    public void loadChannelList(boolean showToast) {
        ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
            try {
                if (PhotonApplication.api != null) {
                    String str = PhotonApplication.api.getChannelList();
                    if (!TextUtils.isEmpty(str)) {
                        mView.loadChannelSuccess(str);
                    }else{
                        mView.loadChannelError(false);
                    }
                } else {
                    mView.loadChannelError(showToast);
                }
            } catch (Exception e) {
                mView.loadChannelError(showToast);
            }
        });
    }

    /**
     * withdraw
     * @param channelIdentifierHashStr    channel identifier
     * @param amountstr                   Withdrawal Amount
     * */
    @Override
    public void photonWithDraw(int position,String channelIdentifierHashStr, String amountstr, String op) {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                try {
                    if (PhotonApplication.api != null) {
                        String jsonString = PhotonApplication.api.withdraw(channelIdentifierHashStr,amountstr ,op);
                        mView.photonWithdrawSuccess(position,jsonString);
                    } else {
                        mView.photonError();
                    }
                } catch (Exception e) {
                    mView.photonError();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            mView.photonError();
        }
    }

    /**
     * settle channel
     * @param channelIdentifierHashStr    channel identifier
     * */
    @Override
    public void photonSettleChannel(int position, String channelIdentifierHashStr) {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                try {
                    if (PhotonApplication.api != null) {
                        String jsonString = PhotonApplication.api.settleChannel(channelIdentifierHashStr);
                        mView.photonSettleSuccess(position,jsonString);
                    } else {
                        mView.photonError();
                    }
                } catch (Exception e) {
                    mView.photonError();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            mView.photonError();
        }
    }

    /**
     * close channel
     * @param channelIdentifierHashStr    channel identifier
     * @param isForced                    is forced close the channel  true or false
     * */
    @Override
    public void photonCloseChannel(int position, String channelIdentifierHashStr, boolean isForced) {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                try {
                    if (PhotonApplication.api != null) {
                        String jsonString = PhotonApplication.api.closeChannel(channelIdentifierHashStr, isForced);
                        mView.photonCloseChannelSuccess(position,jsonString,isForced);
                    } else {
                        mView.photonError();
                    }
                } catch (Exception e) {
                    mView.photonError();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            mView.photonError();
        }
    }

    /**
     * 通过photon获取链上、通道内余额
     * Get the balance on the chain and channel through photon
     * 内部实现是去公网查询
     * Internal implementation is to go to the public network query
     * */
    @Override
    public void getBalanceFromPhoton() {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                try {
                    if (PhotonApplication.api != null) {
                        String jsonString = PhotonApplication.api.getAssetsOnToken(PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS);
                        mView.getPhotonBalanceFromApiSuccess(jsonString);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void start() {

    }


}
