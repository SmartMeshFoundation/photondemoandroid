package com.smartmesh.photon.wallet;

import android.view.View;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.presenter.WalletCopyPresenter;
import com.smartmesh.photon.wallet.util.WalletConstants;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created on 2017/8/22.
 * Backup account
 */

public class WalletCopyActivity extends BaseActivity {

    @BindView(R.id.app_btn_right)
    TextView appBtnRight;
    @BindView(R.id.success)
    TextView success;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.walletCopyPwdInfo)
    TextView walletCopyPwdInfo;
    @BindView(R.id.walletCopyPwdInfoLine)
    View walletCopyPwdInfoLine;
    @BindView(R.id.walletCopyKey)
    TextView walletCopyKey;
    @BindView(R.id.walletCopyKeyStore)
    TextView walletCopyKeyStore;
    @BindView(R.id.walletDelete)
    TextView walletDelete;

    private WalletCopyPresenter walletCopyPresenter;

    private StorableWallet storableWallet;
    private String iconId;
    private int type;//0 the newly created, 1 backup wallet


    @Override
    protected void initData() {
        storableWallet = (StorableWallet) getIntent().getSerializableExtra(WalletConstants.WALLET_INFO);
        type = getIntent().getIntExtra(WalletConstants.WALLET_TYPE, 0);
        walletCopyPresenter = new WalletCopyPresenter(WalletCopyActivity.this);
        walletCopyPresenter.getPassData(storableWallet,iconId,type);
        walletCopyPresenter.initView(address);
        setBottomTitle(getString(R.string.wallet_copy));
        walletCopyPresenter.initData(appBtnRight,walletCopyPwdInfo,walletCopyPwdInfoLine,
                success,walletCopyKey,walletCopyKeyStore);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        walletCopyPresenter.onDestroy();
    }

    /**
     * to export the private key
     * export KeyStore
     * to delete the wallet
     */
    @OnClick({R.id.walletCopyKey,R.id.walletCopyKeyStore,R.id.walletDelete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.walletCopyKey:
                if (storableWallet != null){
                    if (storableWallet.getWalletType() == 1){
                        showToast(getString(R.string.wallet_scan_cant_click));
                        return;
                    }
                    walletCopyPresenter.showPwdDialog(0);
                }
                break;
            case R.id.walletCopyKeyStore:
                if (storableWallet != null){
                    if (storableWallet.getWalletType() == 1){
                        showToast(getString(R.string.wallet_scan_cant_click));
                        return;
                    }
                    walletCopyPresenter.showPwdDialog(1);
                }
                break;
            case R.id.walletDelete:
                if (storableWallet.getWalletType() == 1) {
                    walletCopyPresenter.showDelSacnWalletDialog();
                } else {
                    walletCopyPresenter.showPwdDialog(2);
                }
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.wallet_copy_layout;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }
}
