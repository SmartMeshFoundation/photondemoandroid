package com.smartmesh.photon.channel.presenter;

import android.text.Editable;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.base.BasePresenterImpl;
import com.smartmesh.photon.channel.contract.PhotonCreateContract;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.util.ThreadPoolUtils;

import org.web3j.utils.Convert;

import java.math.BigDecimal;

/**
 * photon 创建通道实现类
 * {@link com.smartmesh.photon.channel.PhotonCreateChannel}
 * */
public class PhotonCreatePresenterImpl extends BasePresenterImpl<PhotonCreateContract.View> implements PhotonCreateContract.Presenter{

    private PhotonCreateContract.View mView;

    public PhotonCreatePresenterImpl(PhotonCreateContract.View view){
        super(view);
        this.mView = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void checkDepositValue(Editable s) {
        try {
            String temp = s.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) {
                if (temp.length() <= 8) {
                    return;
                } else {
                    s.delete(8, 9);
                    return;
                }
            }
            if (temp.length() - posDot - 1 > 2) {
                s.delete(posDot + 3, posDot + 4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建通道  create channel
     * @param photonTokenAddress 光子币种token地址      Photon currency token address
     * @param partnerAddress 目标地址                   target address
     * @param depositBalance 存款金额                   Deposit amount
     *                       异步调用 需要确实是否要废弃线程
     * */
    @Override
    public void createChannelMethod(String photonTokenAddress,String partnerAddress, String depositBalance) {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                try {
                    if (PhotonApplication.api != null) {
                        mView.createChannelStart();
                        /*
                         * partnerAddress 	string 	partner_address 	通道对方地址
                         * tokenAddress 	string 	token_address 	哪种token
                         * settleTimeout 	string 	settle_timeout 	通道结算时间 存款为0  主网创建为40000以上  Channel settlement time deposit is 0, the main network is created above 40,000
                         * balanceStr 	big.Int 	balance 	存入金额，一定大于0     Deposit amount must be greater than 0
                         * newChannel 	bool 	new_channel 	判断通道是否存在，决定此次行为是创建通道并存款还是只存款  false为存钱
                         * Determine whether the channel exists, decide whether the behavior is to create a channel and deposit or only deposit false for saving money
                         * */
                        String channelBalance = new BigDecimal(depositBalance).multiply(Convert.Unit.ETHER.getWeiFactor()).stripTrailingZeros().toPlainString();
                        String jsonString = PhotonApplication.api.deposit(partnerAddress,photonTokenAddress, PhotonUrl.PHOTON_SETTLE_TIMEOUT,channelBalance ,true);
                        mView.createChannelSuccess(jsonString);
                    } else {
                        mView.photonNotStart();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mView.createChannelError();
                }
            });
        } catch (Exception e) {
            mView.createChannelError();
        }
    }

    /**
     * get balance from photon
     * */
    @Override
    public void getBalanceFromPhoton() {
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
    }
}
