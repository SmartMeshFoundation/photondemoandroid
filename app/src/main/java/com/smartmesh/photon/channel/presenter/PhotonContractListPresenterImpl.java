package com.smartmesh.photon.channel.presenter;

import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.channel.contract.PhotonContractListContract;
import com.smartmesh.photon.channel.entity.TxTypeStr;
import com.smartmesh.photon.util.ThreadPoolUtils;

/**
 * photon 合约调用列表实现类
 * {@link com.smartmesh.photon.channel.fragment.PhotonContractCallFragment}
 * */
public class PhotonContractListPresenterImpl implements PhotonContractListContract.Presenter {


    private PhotonContractListContract.View mView;

    public PhotonContractListPresenterImpl(PhotonContractListContract.View view){
        this.mView = view;
        mView.setPresenter(this);
    }

    /**
     * 读取数据库数据 不会阻塞
     * */
    @Override
    public void getContractCallTxQuery() {
        try {
             ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                 if (PhotonApplication.api != null) {
                     String builder = TxTypeStr.Withdraw.name() + "," +
                             TxTypeStr.ChannelDeposit.name() + "," +
                             TxTypeStr.ApproveDeposit.name() + "," +
                             TxTypeStr.CooperateSettle.name() + "," +
                             TxTypeStr.ChannelSettle.name() + "," +
                             TxTypeStr.ChannelClose;
                     String str = PhotonApplication.api.contractCallTXQuery("",0,"", builder,"");
                     if (!TextUtils.isEmpty(str)) {
                         mView.getContractCallTxQuerySuccess(str);
                     }else{
                         mView.getContractCallTxQueryError(null);
                     }
                 } else {
                     mView.getContractCallTxQueryError(null);
                 }
             });
        } catch (Exception e) {
            mView.getContractCallTxQueryError(null);
        }
    }

    @Override
    public void start() {

    }
}
