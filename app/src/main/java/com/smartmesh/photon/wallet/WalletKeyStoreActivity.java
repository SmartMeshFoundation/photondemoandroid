package com.smartmesh.photon.wallet;

import android.view.View;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.util.Utils;
import com.smartmesh.photon.wallet.util.WalletConstants;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created on 2017/8/22.
 * According to the private key plaintext
 * {@link WalletCopyActivity}
 */

public class WalletKeyStoreActivity extends BaseActivity {

    @BindView(R.id.wallet_show_keystore)
    TextView walletKeyStore;
    @BindView(R.id.wallet_copy_keystore)
    TextView walletCopyKeyStore;

    private String keystore;

    private void getPassData() {
        keystore = getIntent().getStringExtra(WalletConstants.KEYSTORE);
    }

    @Override
    protected void initData() {
        setBottomTitle(getString(R.string.wallet_show_keystore));
        walletKeyStore.setText(keystore);
    }

    @OnClick(R.id.wallet_copy_keystore)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.wallet_copy_keystore:
                Utils.copyText(WalletKeyStoreActivity.this,keystore);
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    public int getLayoutId() {
        getPassData();
        return R.layout.wallet_show_keystore_layout;
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
