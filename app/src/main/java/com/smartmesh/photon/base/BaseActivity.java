package com.smartmesh.photon.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.language.MultiLanguageUtil;
import com.smartmesh.photon.util.MyToast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView, View.OnClickListener {

    protected P mPresenter;
    private Unbinder mUnBinder;
    protected TextView mTitle;
    protected ImageView mBack;
    protected TextView mBottomTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnBinder = ButterKnife.bind(this);
        initView();
        mPresenter = createPresenter();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_back:
                finish();
                break;
        }
    }

    private void initView() {
        mBack = (ImageView) findViewById(R.id.app_back);
        mTitle = (TextView) findViewById(R.id.app_title);
        mBottomTitle = (TextView) findViewById(R.id.app_title_bottom_left);
        if (mBack != null){
            mBack.setOnClickListener(this);
        }
    }

    protected void setTitle(String title) {
        if(mTitle != null){
            mTitle.setText(title);
        }
    }

    protected void setBottomTitle(String title) {
        if(mBottomTitle != null){
            mBottomTitle.setVisibility(View.VISIBLE);
            mBottomTitle.setText(title);
        }
    }

    protected void showToast(String msg){
        MyToast.showToast(this, msg);
    }

    public abstract int getLayoutId();

    public abstract P createPresenter();

    protected abstract void initData();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }
}