package com.smartmesh.photon.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.base.BasePresenter;
import com.smartmesh.photon.dialog.CustomDialogFragment;
import com.smartmesh.photon.wallet.NewWalletActivity;
import com.smartmesh.photon.wallet.util.WalletStorage;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;

/**
 * Created on 2017/8/23.
 * splash page
 */

public class SplashActivity extends BaseActivity implements Animation.AnimationListener, View.OnClickListener {

    @BindView(R.id.splash_bg)
    ImageView splash_bg;


    @Override
    protected void initData() {
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(1000);
        splash_bg.startAnimation(aa);
        aa.setAnimationListener(this);

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @SuppressLint("CheckResult")
    @Override
    public void onAnimationEnd(Animation animation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //get permissions
            final RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .request(Manifest.permission.CAMERA
                            ,Manifest.permission.READ_PHONE_STATE
                            ,Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ,Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean){
                            intoNextMethod();
                        }else{
                            openPermission();
                        }
                    });
        }else{
            intoNextMethod();
        }
    }

    /**
     * set permissions yourself
     * */
    private void openPermission(){
        CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        customDialogFragment.setTitle(getString(R.string.open_permission_about_smartmesh));
        customDialogFragment.setContent(getString(R.string.open_permission));
        customDialogFragment.setCancelable(false);
        customDialogFragment.setCancelListener(() -> {
            finish();
        });
        customDialogFragment.setSubmitListener(() -> {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(localIntent);
            finish();
        });
        customDialogFragment.show(getSupportFragmentManager(), "mdf");
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    /**
     * goto next page
     * have a wallet     goto  MainActivity
     * else   goto   create wallet page
     * */
    private void intoNextMethod(){
        if (WalletStorage.getInstance(getApplicationContext()).get().size() > 0) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, NewWalletActivity.class));
            finish();
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.splash_layout;
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
