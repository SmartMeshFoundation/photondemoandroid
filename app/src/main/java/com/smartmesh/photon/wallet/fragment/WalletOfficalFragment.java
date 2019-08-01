package com.smartmesh.photon.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseFragment;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.wallet.WalletThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created  on 2017/8/21.
 * The official import wallet KeyStore
 */

public class WalletOfficalFragment extends BaseFragment implements View.OnClickListener {

    /**
     * root view
     */
    private View view = null;

    /*KeyStore password text content*/
    @BindView(R.id.wallet_keystore_pwd)
    EditText keyStorePwd;
    /*keyStore text content*/
    @BindView(R.id.wallet_keystore_info)
    EditText keyStoreInfo;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wallet_offical_layout,container,false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @OnClick(R.id.wallet_start_import)
    public void onClick(View v){
        switch (v.getId()){
            case R.id.wallet_start_import:
                String password = keyStorePwd.getText().toString().trim();
                String source = keyStoreInfo.getText().toString().trim();
                if (TextUtils.isEmpty(source)){
                    MyToast.showToast(getActivity(),getString(R.string.wallet_keystore_empty));
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    MyToast.showToast(getActivity(),getString(R.string.wallet_pwd_empty));
                    return;
                }
                LoadingDialog.show(getActivity(),getString(R.string.wallet_import_ing));
                new WalletThread(PhotonApplication.mContext,null,password,null,source,2,false).start();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
