package com.smartmesh.photon.wallet;

import android.content.Intent;
import android.view.View;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.channel.util.PhotonStartUtils;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.util.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;

/**
 * create or import wallet page
 * */
public class NewWalletActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.wallet_new_activity;
    }


    @OnClick({R.id.wallet_create,R.id.wallet_import})
    public void onClickView(View v){
        switch (v.getId()){
            case R.id.wallet_create://goto create wallet page
                startActivity(new Intent(NewWalletActivity.this, WalletCreateActivity.class));
                break;
            case R.id.wallet_import://goto import wallet page
                startActivity(new Intent(NewWalletActivity.this, WalletImportActivity.class));
                break;
        }
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
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
     * code    WALLET_IMPORT_SUCCESS             导入钱包成功    import wallet success
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        try {
            if (messageEvent != null) {
                if (messageEvent.getCode() == RequestCodeUtils.WALLET_IMPORT_SUCCESS) {
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoadingDialog.close();
        }
    }
}
