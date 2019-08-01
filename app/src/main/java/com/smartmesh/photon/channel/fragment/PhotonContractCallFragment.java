package com.smartmesh.photon.channel.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseFragment;
import com.smartmesh.photon.channel.adapter.PhotonContractListAdapter;
import com.smartmesh.photon.channel.contract.PhotonContractListContract;
import com.smartmesh.photon.channel.entity.PhotonContractEntity;
import com.smartmesh.photon.channel.entity.TxStatus;
import com.smartmesh.photon.channel.entity.TxTypeStr;
import com.smartmesh.photon.channel.presenter.PhotonContractListPresenterImpl;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yczbj.ycrefreshviewlib.view.YCRefreshView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 合约调用记录页面
 * {@link com.smartmesh.photon.channel.PhotonTransferQueryUI}
 * */
public class PhotonContractCallFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PhotonContractListContract.View {

    /**
     * root view
     */
    private View view = null;
    private Unbinder unbinder;

    @BindView(R.id.recyclerView)
    YCRefreshView refreshView;

    private PhotonContractListAdapter mAdapter = null;
    private List<PhotonContractEntity> source = null;
    private PhotonContractListContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.photon_contract_list_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);
        initData();
        return view;
    }

    private void initData() {
        new PhotonContractListPresenterImpl(this);
        EventBus.getDefault().register(this);
        source = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        refreshView.setLayoutManager(linearLayoutManager);
        refreshView.setRefreshListener(this);
        refreshView.setRefreshingColorResources(R.color.colorPrimary);
        mAdapter = new PhotonContractListAdapter(getActivity());
        refreshView.setAdapter(mAdapter);
        refreshView.setRefreshing(true);
        refreshView.setRefreshListener(this);
        new Handler().postDelayed(() -> {
            mPresenter.getContractCallTxQuery();
        }, 500);
    }

    @Override
    public void onRefresh() {
        if (mPresenter != null){
            mPresenter.getContractCallTxQuery();
        }
    }

    @Override
    public void getContractCallTxQuerySuccess(String jsonString) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_CONTRACT_LIST_SUCCESS);
        messageEvent.setMessage(jsonString);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void getContractCallTxQueryError(String errorMessage) {

    }

    /**
     * 解析合约数据
     * */
    private void parseContractJson(String jsonString) {
        try {
            source.clear();
            if (TextUtils.isEmpty(jsonString) || "null".equals(jsonString)) {
                if (mAdapter != null){
                    mAdapter.clear();
                    mAdapter.addAll(source);
                }
                return;
            }
            JSONObject object = new JSONObject(jsonString);
            int errorCode = object.optInt("error_code");
            if (errorCode == 0){
                JSONArray array = object.optJSONArray("data");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject dataObject = array.optJSONObject(i);
                        PhotonContractEntity contractEntity = new PhotonContractEntity().parse(dataObject);
                        if (TextUtils.equals(TxTypeStr.ApproveDeposit.name(),contractEntity.getType()) && TextUtils.equals(TxStatus.success.name(),contractEntity.getTxStatus())){
                            continue;
                        }
                        source.add(contractEntity);
                    }
                }
            }
            Collections.sort(source);
            if (mAdapter != null){
                mAdapter.clear();
                mAdapter.addAll(source);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPresenter(PhotonContractListContract.Presenter presenter) {
            this.mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    /**
     * code    PHOTON_EVENT_CHANNEL_CONTRACT_LIST_SUCCESS              获取合约交易列表api调用成功
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        if (messageEvent != null){
            if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_CONTRACT_LIST_SUCCESS == messageEvent.getCode()){
                parseContractJson(messageEvent.getMessage());
            }
        }
    }

}
