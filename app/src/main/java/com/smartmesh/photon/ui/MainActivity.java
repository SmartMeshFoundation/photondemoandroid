package com.smartmesh.photon.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.channel.PhotonTransferUI;
import com.smartmesh.photon.channel.util.PhotonIntentDataUtils;
import com.smartmesh.photon.channel.util.PhotonStartUtils;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.dialog.CustomDialogFragment;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.ui.contract.MainContract;
import com.smartmesh.photon.ui.presenter.MainPresenter;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.wallet.WalletCopyActivity;
import com.smartmesh.photon.wallet.WalletCreateActivity;
import com.smartmesh.photon.wallet.WalletQrCodeActivity;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.util.CustomWalletUtils;
import com.smartmesh.photon.wallet.util.WalletConstants;
import com.smartmesh.photon.wallet.util.WalletInfoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.utils.Numeric;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainContract.Presenter> implements MainContract.View, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.main_wallet_balance)
    TextView mainWalletBalance;
    @BindView(R.id.main_wallet_address)
    TextView mainWalletAddress;
    @BindView(R.id.ecology_photon_body)
    RelativeLayout ecologyPhotonBody;
    @BindView(R.id.wallet_name)
    TextView walletName;
    @BindView(R.id.wallet_backup)
    TextView walletBackUp;
    @BindView(R.id.app_back)
    ImageView appBack;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private StorableWallet storableWallet;
    private CustomDialogFragment customDialogFragment;

    private int errorCode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void initData() {
        setBottomTitle(getString(R.string.app_name));
        appBack.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(this);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PhotonUrl.ACTION_PHOTON_CONNECTION_STATE_ERROR);//第一次无网启动
        registerReceiver(mBroadcastReceiver, filter);
        initWalletInfo();
        mPresenter.getBalanceFromPhoton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initWalletInfo();
    }

    /**
     * 初始化钱包信息
     * init wallet info
     */
    private void initWalletInfo() {
        try {
            storableWallet = WalletInfoUtils.getInstance().getStorableWallet();
            if (storableWallet != null) {
                walletName.setText(storableWallet.getWalletName());
//                if (storableWallet.isBackup()){
//                    walletBackUp.setVisibility(View.GONE);
//                }else{
                    walletBackUp.setVisibility(View.VISIBLE);
//                }
                String address = Numeric.prependHexPrefix(storableWallet.getPublicKey());
                if (mainWalletAddress != null) {
                    mainWalletAddress.setText(address);
                    mPresenter.getBalanceFromPhoton();
                }
            }else{
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.main_wallet_balance,R.id.main_wallet_address,R.id.ecology_photon_body,R.id.wallet_backup})
    public void onClickView(View v){
        switch (v.getId()){
            case R.id.main_wallet_balance:
            case R.id.main_wallet_address:// goto wallet QR code page
                Intent qrEthIntent = new Intent(this, WalletQrCodeActivity.class);
                qrEthIntent.putExtra(WalletConstants.WALLET_ADDRESS, storableWallet.getPublicKey());
                startActivity(qrEthIntent);
                break;
            case R.id.ecology_photon_body:// goto photon transfer page
                mPresenter.intoPhotonTransfer();
                break;
            case R.id.wallet_backup://goto copy wallet keystore or private key page
                Intent copyIntent = new Intent(this,WalletCopyActivity.class);
                copyIntent.putExtra(WalletConstants.WALLET_INFO, storableWallet);
                copyIntent.putExtra(WalletConstants.WALLET_IMAGE, storableWallet.getWalletImageId());
                copyIntent.putExtra(WalletConstants.WALLET_TYPE, 1);
                startActivity(copyIntent);
                break;
        }
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().register(this);
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                if (PhotonUrl.ACTION_PHOTON_CONNECTION_STATE_ERROR.equals(intent.getAction())){
                    errorCode = intent.getIntExtra(PhotonIntentDataUtils.ERROR_CODE,-1);
                }
            }
        }
    };

    /**
     * get balance success
     * */
    @Override
    public void getBalanceSuccess(String balance) {
        if (!TextUtils.isEmpty(balance)){
            mainWalletBalance.setText(getString(R.string.photon_in_balance,balance));
        }
    }

    /**
     * get balance error
     * */
    @Override
    public void getBalanceError() {

    }

    /**
     * no wallet
     * go to create wallet
     * */
    @Override
    public void walletIsEmpty() {
        showToast(getString(R.string.wallet_empty));
        startActivity(new Intent(MainActivity.this, WalletCreateActivity.class));
    }

    /**
     * observation mode
     * */
    @Override
    public void walletIsObserve() {
        showToast(getString(R.string.wallet_scan_cant_photon));
    }

    /**
     * please back up
     * */
    @Override
    public void walletHasNotBackUp() {
        showToast(getString(R.string.wallet_copy));
    }

    /**
     * go to photon
     * */
    @Override
    public void canIntoPhotonPay() {
        intoPhotonPay();
    }

    /**
     * verify wallet password
     * */
    @Override
    public void inputPwdForPhoton() {
        CustomDialogFragment mdf = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG_INPUT_PWD);
        mdf.setEditCallbackListener(content -> {
            LoadingDialog.show(MainActivity.this,"");
            CustomWalletUtils.verifyWalletPwd(MainActivity.this,content);
        });
        mdf.show(getSupportFragmentManager(), "mdf");
    }

    /**
     * go to photon
     * */
    private void intoPhotonPay(){
        if(PhotonApplication.api == null){
            if (PhotonApplication.photonStatusStatus){
                if (customDialogFragment == null){
                    customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG_NO_BUTTON);
                    customDialogFragment.setTitle(getString(R.string.photon_notify));
                    customDialogFragment.setContent(getString(R.string.photon_certification));
                    customDialogFragment.setContent2(getString(R.string.photon_sync_block));
                    customDialogFragment.setContent3(getString(R.string.photon_start));
                    customDialogFragment.setCancelable(false);
                    if (getFragmentManager() != null) {
                        customDialogFragment.show(getSupportFragmentManager(),"mdf");
                    }
                }else {
                    customDialogFragment.resetContentViewDrawable();
                }
                new Handler().postDelayed(this::intoPhotonPay,500);
            }else{
                if (customDialogFragment !=null){
                    customDialogFragment.dismiss();
                    customDialogFragment = null;
                }
                if (errorCode == 3){
                    showToast(getString(R.string.photon_error_first_start));
                }else{
                    showToast(getString(R.string.photon_error_start));
                }
            }
        }else{
            if (customDialogFragment != null){
                customDialogFragment.resetContentView3Drawable();
            }
            new Handler().postDelayed(() -> {
                if (customDialogFragment !=null){
                    customDialogFragment.dismiss();
                    customDialogFragment = null;
                }
                Intent photonIntent = new Intent(MainActivity.this, PhotonTransferUI.class);
                startActivity(photonIntent);
            },1000);
        }
    }

    /**
     * code    WALLET_VERIFY_SUCCESS             创建钱包成功               create wallet success
     * code    WALLET_VERIFY_OTHER_ERROR         创建钱包其他错误           create wallet other errors
     * code    WALLET_VERIFY_PWD_ERROR           密码错误                   password error
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        try {
            if (messageEvent != null) {
                switch (messageEvent.getCode()) {
                    case RequestCodeUtils.WALLET_VERIFY_SUCCESS:
                        LoadingDialog.close();
                        PhotonStartUtils.getInstance().startPhotonServer(messageEvent.getMessage(),"");
                        PhotonApplication.photonStatusStatus = true;
                        intoPhotonPay();
                        break;
                    case RequestCodeUtils.WALLET_VERIFY_OTHER_ERROR:
                        LoadingDialog.close();
                        break;
                    case RequestCodeUtils.WALLET_VERIFY_PWD_ERROR:
                        LoadingDialog.close();
                        showToast(getString(R.string.wallet_pwd_error));
                        break;
                    case RequestCodeUtils.WALLET_DELETE_SUCCESS:
                        finish();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoadingDialog.close();
        }
    }

    @Override
    public void onRefresh() {
        initWalletInfo();
        mPresenter.getBalanceFromPhoton();
        swipeRefreshLayout.setRefreshing(false);
    }
}
