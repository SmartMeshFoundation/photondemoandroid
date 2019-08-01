package com.smartmesh.photon.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.smartmesh.photon.util.MyToast;

public abstract class BaseFragment<P extends BaseFragmentPresenter> extends Fragment{

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void showToast(String msg){
        MyToast.showToast(getActivity(), msg);
    }
}