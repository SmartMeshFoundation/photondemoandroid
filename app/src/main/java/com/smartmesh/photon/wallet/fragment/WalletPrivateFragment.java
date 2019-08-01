package com.smartmesh.photon.wallet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.wallet.WalletThread;
import com.smartmesh.photon.wallet.util.CustomWalletUtils;
import com.smartmesh.photon.wallet.util.PrivateKeyCheckUtils;

import org.web3j.utils.Numeric;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created on 2017/8/21.
 * The private key import the purse
 */

public class WalletPrivateFragment extends Fragment implements View.OnClickListener {

    /**
     * root view
     */
    private View view = null;

    private Unbinder unbinder;

    @BindView(R.id.walletPwd)
    EditText walletPwd;//The wallet password
    @BindView(R.id.walletAgainPwd)
    EditText walletAgainPwd;//The wallet password again
    @BindView(R.id.walletPwdInfo)
    EditText walletPwdInfo;//Password prompt information
    @BindView(R.id.wallet_private_info)
    EditText privateKeyInfo;
    //Remove the wallet name Show the password
    @BindView(R.id.isShowPass)
    ImageView isShowPass;

    //是否是10进制
    private boolean isNotPrivateHex;

    /**
     * Show the password
     */
    boolean isShowPassWorld = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wallet_private_layout,container,false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    //Import the purse, how to import the wallet
    @OnClick({R.id.isShowPass,R.id.wallet_start_import})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.isShowPass:
                isShowPassWorld = !isShowPassWorld;
                if (isShowPassWorld) { /*Set the EditText content is visible */
                    walletPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPass.setImageResource(R.mipmap.eye_open);
                } else {/* The content of the EditText set as hidden*/
                    walletPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPass.setImageResource(R.mipmap.eye_close);
                }
                break;
            case R.id.wallet_start_import:
                String password = walletPwd.getText().toString().trim();
                String pwdInfo = walletPwdInfo.getText().toString().trim();
                String source = privateKeyInfo.getText().toString().trim();

                if (TextUtils.isEmpty(source)){
                    MyToast.showToast(getActivity(),getString(R.string.wallet_private_key_empty));
                    return;
                }

                if (password.length() > 16 || password.length() < 6){
                    MyToast.showToast(getActivity(),getString(R.string.wallet_pwd_warning));
                    return;
                }

                if (!isNotPrivateHex){//16进制判断
                    if (!CustomWalletUtils.isValidPrivateKey(source)){
                        MyToast.showToast(getActivity(),getString(R.string.wallet_private_key_error));
                        return;
                    }

                    String chatAt = PrivateKeyCheckUtils.checkPrivateKey(source);
                    if (!TextUtils.isEmpty(chatAt)){
                        MyToast.showToast(getActivity(),getString(R.string.wallet_private_key_error_1,chatAt));
                        return;
                    }
                }

                if (TextUtils.equals(password,walletAgainPwd.getText().toString().trim())){
                    LoadingDialog.show(getActivity(),getString(R.string.wallet_import_ing));
                    new WalletThread(PhotonApplication.mContext,null,password,pwdInfo,Numeric.cleanHexPrefix(source),1,isNotPrivateHex).start();
                }else{
                    MyToast.showToast(getActivity(),getString(R.string.account_pwd_again_warning));
                }
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
