package com.smartmesh.photon.channel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.smartmesh.photon.R;
import com.smartmesh.photon.adapter.SlidePagerAdapter;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.channel.entity.TxTypeStr;
import com.smartmesh.photon.channel.fragment.PhotonContractCallFragment;
import com.smartmesh.photon.channel.fragment.PhotonTransferListFragment;
import com.smartmesh.photon.channel.util.PhotonUrl;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 光子转账记录页面
 * 包括 通道内交易记录   合约调用记录 两种
 * Photon Transfer Record Page
 * Includes in-channel transaction record contract call record
 * {@link PhotonChannelList}
 * */
public class PhotonTransferQueryUI extends BaseActivity {

    @BindView(R.id.photonTransferTabs)
    TabLayout photonTransferTabs;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    PhotonTransferListFragment transferListFragment;
    PhotonContractCallFragment contractCallFragment;

    private ArrayList<Fragment> frameList = new ArrayList<>();
    private List<String> frameTitle = new ArrayList<>();
    private SlidePagerAdapter mPagerAdapter;

    private boolean showContract = false;
    private int fromType;//0 转账页面 1 通道列表 2 存款

    private void getPassData() {
        showContract = getIntent().getBooleanExtra("showContract",false);
        fromType = getIntent().getIntExtra("fromType",-1);
    }


    @Override
    public int getLayoutId() {
        getPassData();
        return R.layout.photon_transfer_query_layout;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CONTRACT_INFO);
        filter.addAction(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CHANNEL_TRANSFER_ERROR);
        registerReceiver(receiver, filter);
        setBottomTitle(getString(R.string.photon_transfer_title));
        setupViewPager();
        photonTransferTabs.addTab(photonTransferTabs.newTab().setText(getString(R.string.photon)));
        photonTransferTabs.addTab(photonTransferTabs.newTab().setText(getString(R.string.spectrum)));
        photonTransferTabs.setupWithViewPager(mViewPager);
        if (showContract){
            mViewPager.setCurrentItem(1);
        }
    }

    /**
     * Set the ViewPager content
     * */
    private void setupViewPager() {
        transferListFragment = new PhotonTransferListFragment();
        contractCallFragment = new PhotonContractCallFragment();
        frameList.add(transferListFragment);
        frameList.add(contractCallFragment);
        frameTitle.add(getString(R.string.photon));
        frameTitle.add(getString(R.string.spectrum));
        mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(),frameList,frameTitle);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && (PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CONTRACT_INFO.equals(intent.getAction()))) {
                String txType = intent.getStringExtra("type");
                if (contractCallFragment != null && !TextUtils.equals(txType, TxTypeStr.ApproveDeposit.name())){
                    contractCallFragment.onRefresh();
                    if (fromType == 0){
                        Intent photonIntent = new Intent(PhotonTransferQueryUI.this, PhotonChannelList.class);
                        photonIntent.putExtra("type", 1);
                        startActivity(photonIntent);
                    }else{
                        finish();
                    }
                }
            }else if (intent != null && (PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CHANNEL_TRANSFER_ERROR.equals(intent.getAction()))) {
                if (transferListFragment != null){
                    transferListFragment.onRefresh();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }
}
