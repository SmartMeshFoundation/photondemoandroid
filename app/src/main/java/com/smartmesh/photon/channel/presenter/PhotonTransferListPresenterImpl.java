package com.smartmesh.photon.channel.presenter;

import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.channel.contract.PhotonTransferListContract;
import com.smartmesh.photon.channel.fragment.PhotonTransferListFragment;
import com.smartmesh.photon.util.ThreadPoolUtils;

/**
 * photon 通道内转账列表实现类
 * {@link PhotonTransferListFragment}
 * */
public class PhotonTransferListPresenterImpl implements PhotonTransferListContract.Presenter {


    private PhotonTransferListContract.View mView;

    public PhotonTransferListPresenterImpl(PhotonTransferListFragment view){
        this.mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    /**
     * 简单查询数据库 不会阻塞  ps:如果数据太多需要加上线程
     * */
    @Override
    public void getSentTransfers() {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                if (PhotonApplication.api != null) {
                    String str = PhotonApplication.api.getSentTransfers("",-1,-1);
                    if (!TextUtils.isEmpty(str)) {
                        mView.getSentTransfersSuccess(str);
                    }else{
                        mView.getSentTransfersError(null);
                    }
                } else {
                    mView.getSentTransfersError(null);
                }
            });
        } catch (Exception e) {
            mView.getSentTransfersError(null);
        }
    }

    /**
     * 简单查询数据库 不会阻塞  ps:如果数据太多需要加上线程
     * */
    @Override
    public void getReceivedTransfers() {
        try {
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                if (PhotonApplication.api != null) {
                    String str = PhotonApplication.api.getReceivedTransfers("",-1,-1);
                    if (!TextUtils.isEmpty(str)) {
                        mView.getReceivedTransfersSuccess(str);
                    }else{
                        mView.getReceivedTransfersError(null);
                    }
                } else {
                    mView.getReceivedTransfersError(null);
                }
            });
        } catch (Exception e) {
            mView.getReceivedTransfersError(null);
        }
    }
}
