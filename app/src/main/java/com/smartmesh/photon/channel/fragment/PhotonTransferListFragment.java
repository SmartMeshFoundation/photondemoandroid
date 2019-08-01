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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseFragment;
import com.smartmesh.photon.channel.adapter.PhotonTransferListAdapter;
import com.smartmesh.photon.channel.contract.PhotonTransferListContract;
import com.smartmesh.photon.channel.entity.PhotonTransferEntity;
import com.smartmesh.photon.channel.presenter.PhotonTransferListPresenterImpl;
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
 * 通道内交易记录页面
 * {@link com.smartmesh.photon.channel.PhotonTransferQueryUI}
 * */
public class PhotonTransferListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PhotonTransferListContract.View{
    /**
     * root view
     */
    private View view = null;
    private Unbinder unbinder;

    @BindView(R.id.empty_text)
    TextView emptyTextView;
    @BindView(R.id.empty_like_icon)
    ImageView emptyIcon;
    @BindView(R.id.empty_like_rela)
    RelativeLayout emptyRela;

    @BindView(R.id.recyclerView)
    YCRefreshView refreshView;

    private PhotonTransferListAdapter mAdapter = null;
    private List<PhotonTransferEntity> source = null;

    private PhotonTransferListContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.photon_transfer_list_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);
        initData();
        return view;
    }

    private void initData() {
        new PhotonTransferListPresenterImpl(this);
        EventBus.getDefault().register(this);
        source = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        refreshView.setLayoutManager(linearLayoutManager);
        refreshView.setRefreshListener(this);
        refreshView.setRefreshingColorResources(R.color.colorPrimary);
        mAdapter = new PhotonTransferListAdapter(getActivity());
        refreshView.setAdapter(mAdapter);
        refreshView.setRefreshing(true);
        refreshView.setRefreshListener(this);
        new Handler().postDelayed(() -> {
            mPresenter.getSentTransfers();
        }, 500);
    }

    @Override
    public void onRefresh() {
        if (mPresenter != null){
            mPresenter.getSentTransfers();
        }
    }

    @Override
    public void getSentTransfersSuccess(String jsonString) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_SUCCESS);
        messageEvent.setMessage(jsonString);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void getSentTransfersError(String errorMessage) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_ERROR);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void getReceivedTransfersSuccess(String jsonString) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_SUCCESS);
        messageEvent.setMessage(jsonString);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void getReceivedTransfersError(String errorMessage) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_ERROR);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void setPresenter(PhotonTransferListContract.Presenter presenter) {
        this.mPresenter = presenter;
    }


    /**
     * To test whether the current list is empty
     */
    private void checkListEmpty() {
        try {
            if (source == null || source.size() == 0) {
                if (emptyRela != null){
                    emptyRela.setVisibility(View.VISIBLE);
                }

                if (emptyTextView != null){
                    emptyTextView.setText(R.string.photon_transfer_list_empty);
                }

                if (emptyIcon != null){
                    emptyIcon.setVisibility(View.VISIBLE);
                    emptyIcon.setImageResource(R.mipmap.icon_empty_contract);
                }
            } else {
                if (emptyRela != null){
                    emptyRela.setVisibility(View.GONE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 解析发出的交易记录
     * */
    private void parseSendTransferJson(String jsonString){
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
                        PhotonTransferEntity transferEntity = new PhotonTransferEntity().parse(dataObject);
                        transferEntity.setType(0);
                        source.add(transferEntity);
                    }
                }
            }
            mPresenter.getReceivedTransfers();
        } catch (JSONException e) {
            e.printStackTrace();
            mPresenter.getReceivedTransfers();
        }
    }

    /**
     * 接收的交易记录
     * */
    private void parseReceivedTransferJson(String jsonString){
        try {
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
                        PhotonTransferEntity transferEntity = new PhotonTransferEntity().parse(dataObject);
                        transferEntity.setType(1);
                        transferEntity.setStatus(3);
                        source.add(transferEntity);
                    }
                }
            }
            Collections.sort(source);
            if (mAdapter != null){
                mAdapter.clear();
                mAdapter.addAll(source);
            }
            checkListEmpty();
        } catch (JSONException e) {
            e.printStackTrace();
            Collections.sort(source);
            if (mAdapter != null){
                mAdapter.clear();
                mAdapter.addAll(source);
            }
            checkListEmpty();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    /**
     * code    PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_ERROR              获取发出的交易列表api调用失败
     * code    PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_SUCCESS            获取发出的交易列表api调用成功
     * code    PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_SUCCESS        获取收到的交易列表api调用失败
     * code    PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_ERROR          获取收到的交易列表api调用失败
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        if (messageEvent != null){
            if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_ERROR == messageEvent.getCode()){
                mPresenter.getReceivedTransfers();
            }else if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_SUCCESS == messageEvent.getCode()){
                parseSendTransferJson(messageEvent.getMessage());
            }else if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_SUCCESS == messageEvent.getCode()){
                parseReceivedTransferJson(messageEvent.getMessage());
            }else if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_ERROR == messageEvent.getCode()){
                checkListEmpty();
            }
        }
    }
}
