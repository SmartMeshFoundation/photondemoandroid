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

public class WalletPrivateKeyActivity extends BaseActivity {

    @BindView(R.id.walletPrivateKey)
    TextView walletPrivateKey;
    @BindView(R.id.walletCopyPrivateKey)
    TextView walletCopyPrivateKey;

    private String privateKey;
    private boolean hasCopy;//Is the copy

    private void getPassData() {
        privateKey = getIntent().getStringExtra(WalletConstants.PRIVATE_KEY);
    }

    @Override
    protected void initData() {
        setBottomTitle(getString(R.string.wallet_show_private_key));
        walletPrivateKey.setText(privateKey);
    }

    @OnClick(R.id.walletCopyPrivateKey)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.walletCopyPrivateKey:
                Utils.copyText(WalletPrivateKeyActivity.this,privateKey);
                walletCopyPrivateKey.setText(getString(R.string.wallet_show_private_key_3));
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    public int getLayoutId() {
        getPassData();
        return R.layout.wallet_show_privatekey_layout;
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
