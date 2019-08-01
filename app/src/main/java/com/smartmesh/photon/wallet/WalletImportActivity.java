package com.smartmesh.photon.wallet;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.smartmesh.photon.R;
import com.smartmesh.photon.adapter.SlidePagerAdapter;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.channel.util.PhotonNetUtil;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.ui.MainActivity;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.wallet.fragment.WalletOfficalFragment;
import com.smartmesh.photon.wallet.fragment.WalletPrivateFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created on 2017/8/21.
 * Import the wallet
 * {@link WalletCreateActivity}
 */

public class WalletImportActivity extends BaseActivity {

    @BindView(R.id.wallet_tabs)
    TabLayout walletTabs;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    WalletOfficalFragment walletOfficalFragment;
    WalletPrivateFragment walletPrivateFragment;
    private ArrayList<Fragment> frameList = new ArrayList<>();
    private List<String> frameTitle = new ArrayList<>();
    private SlidePagerAdapter mPagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.wallet_import_layout;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        setBottomTitle(getString(R.string.wallet_import));
        EventBus.getDefault().register(this);
        setupViewPager();
        walletTabs.addTab(walletTabs.newTab().setText(getString(R.string.wallet_official)));
        walletTabs.addTab(walletTabs.newTab().setText(getString(R.string.wallet_private_key)));
        walletTabs.setupWithViewPager(mViewPager);
    }

    /**
     * Set the ViewPager content
     * */
    private void setupViewPager() {
        walletOfficalFragment = new WalletOfficalFragment();
        walletPrivateFragment = new WalletPrivateFragment();
        frameList.add(walletOfficalFragment);
        frameList.add(walletPrivateFragment);
        frameTitle.add(getString(R.string.wallet_official));
        frameTitle.add(getString(R.string.wallet_private_key));
        mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(),frameList,frameTitle);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(0);
    }


    /**
     * import wallet success
     * */
    private void createWalletSuccess(){
        PhotonNetUtil.getInstance().stopPhoton();
        LoadingDialog.close();
        showToast(getString(R.string.notification_wallimp_finished));
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_IMPORT_SUCCESS);
        EventBus.getDefault().post(messageEvent);
        Intent intent1 = new Intent(WalletImportActivity.this, MainActivity.class);
        startActivity(intent1);
        finish();
    }


    /**
     * code    WALLET_CREATE_SUCCESS             创建钱包成功
     * code    WALLET_CREATE_OTHER_ERROR         创建钱包其他错误
     * code    WALLET_CREATE_REPEAT_ERROR        创建钱包名称重复
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        try {
            if (messageEvent != null) {
                switch (messageEvent.getCode()) {
                    case RequestCodeUtils.WALLET_CREATE_SUCCESS:
                        LoadingDialog.close();
                        createWalletSuccess();
                        break;
                    case RequestCodeUtils.WALLET_CREATE_OTHER_ERROR:
                        LoadingDialog.close();
                        showToast(getString(R.string.notification_wallimp_failure));
                        break;
                    case RequestCodeUtils.WALLET_CREATE_PWD_ERROR:
                        LoadingDialog.close();
                        showToast(getString(R.string.wallet_pwd_error));
                        break;
                    case RequestCodeUtils.WALLET_CREATE_REPEAT_ERROR:
                        LoadingDialog.close();
                        showToast(getString(R.string.account_name_exist));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoadingDialog.close();
            showToast(getString(R.string.notification_wallimp_failure));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }
}
