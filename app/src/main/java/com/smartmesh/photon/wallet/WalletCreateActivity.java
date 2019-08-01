package com.smartmesh.photon.wallet;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartmesh.photon.ui.MainActivity;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.channel.util.PhotonNetUtil;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.wallet.contract.WalletCreateContract;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.presenter.WalletCreatePresenter;
import com.smartmesh.photon.wallet.util.WalletConstants;
import com.smartmesh.photon.wallet.util.WalletStorage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * create wallet page
 * */
public class WalletCreateActivity extends BaseActivity<WalletCreateContract.Presenter> implements WalletCreateContract.View{

    @BindView(R.id.wallet_create_name)
    EditText walletName;//Name of the wallet
    @BindView(R.id.walletPwd)
    EditText walletPwd;//The wallet password
    @BindView(R.id.walletAgainPwd)
    EditText walletAgainPwd;//Input the purse close again
    @BindView(R.id.walletPwdInfo)
    EditText walletPwdInfo;//Password prompt information
    @BindView(R.id.isShowPass)
    ImageView isShowPass;

    /**
     * Show the password
     */
    boolean isShowPassWord = false;

    @Override
    public int getLayoutId() {
        return R.layout.wallet_create_layout;
    }

    @Override
    public WalletCreateContract.Presenter createPresenter() {
        return new WalletCreatePresenter(this);
    }

    @Override
    protected void initData() {
        setBottomTitle(getString(R.string.wallet_create));
        EventBus.getDefault().register(this);
    }

    //Create a wallet 、import the purse
    @OnClick({R.id.clearWalletName,R.id.isShowPass,R.id.create_wallet})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.clearWalletName://Remove the name
                walletName.setText("");
                break;
            case R.id.isShowPass://Show or hide the password
                isShowPassWord = !isShowPassWord;
                mPresenter.showPass(isShowPassWord,walletPwd,isShowPass);
                break;
            case R.id.create_wallet:
                createWallet();
                break;
            default:
                super.onClick(v);
        }
    }

    /**
     * create wallet
     * */
    private void createWallet(){
        if (walletName.length() > 12 || walletName.length() <= 0){
            showToast(getString(R.string.wallet_name_warning));
            return;
        }
        if (walletPwd.length() > 16 || walletPwd.length() < 6){
            showToast(getString(R.string.wallet_pwd_warning));
            return;
        }
        String password = walletPwd.getText().toString().trim();
        String name = walletName.getText().toString().trim();
        String pwdInfo = walletPwdInfo.getText().toString().trim();
        if (TextUtils.equals(password,walletAgainPwd.getText().toString().trim())){
            for(StorableWallet storableWallet : WalletStorage.getInstance(getApplicationContext()).get()){
                if(name.equals(storableWallet.getWalletName())){
                    showToast(getString(R.string.account_name_exist));
                    return;
                }
            }
            LoadingDialog.show(WalletCreateActivity.this,getString(R.string.wallet_create_ing));
            new WalletThread(getApplicationContext(),name,password,pwdInfo,null,0,false).start();
        }else{
            showToast(getString(R.string.account_pwd_again_warning));
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
        EventBus.getDefault().unregister(this);
    }

    /**
     * code    WALLET_CREATE_SUCCESS             创建钱包成功               create wallet success
     * code    WALLET_CREATE_OTHER_ERROR         创建钱包其他错误           create wallet error
     * code    WALLET_CREATE_REPEAT_ERROR        创建钱包名称重复           wallet name has exists
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        try {
            if (messageEvent != null) {
                switch (messageEvent.getCode()) {
                    case RequestCodeUtils.WALLET_CREATE_SUCCESS:
                        LoadingDialog.close();
                        String walletAddress = messageEvent.getMessage();
                        String mnemonic = messageEvent.getMessage2();
                        createSuccess(mnemonic,walletAddress);
                        break;
                    case RequestCodeUtils.WALLET_CREATE_OTHER_ERROR:
                        LoadingDialog.close();
                        showToast(getString(R.string.wallet_create_failure));
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
            showToast(getString(R.string.wallet_create_failure));
        }
    }

    /**
     * create wallet success
     * */
    private void createSuccess(String mnemonic,String walletAddress){
        PhotonNetUtil.getInstance().stopPhoton();
        LoadingDialog.close();
        Intent intent = new Intent(WalletCreateActivity.this, MainActivity.class);
        intent.putExtra(WalletConstants.MNEMONIC,mnemonic);
        intent.putExtra(WalletConstants.WALLET_ADDRESS,walletAddress);
        startActivity(intent);
        showToast(getString(R.string.notification_wallgen_finished));
        setResult(RESULT_OK);
        finish();
    }
}
