package com.smartmesh.photon.wallet.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.channel.util.PhotonNetUtil;
import com.smartmesh.photon.dialog.CustomDialogFragment;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.ui.SplashActivity;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.wallet.WalletKeyStoreActivity;
import com.smartmesh.photon.wallet.WalletPrivateKeyActivity;
import com.smartmesh.photon.wallet.entity.StorableWallet;
import com.smartmesh.photon.wallet.util.WalletConstants;
import com.smartmesh.photon.wallet.util.WalletStorage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class WalletCopyPresenter {

    private Context context;
    private StorableWallet storableWallet;
    private String iconId;
    private int type;//0 the newly created, 1 backup wallet

    private TextView address;

    public WalletCopyPresenter(Context context){
        this.context = context;
    }

    public void initView(TextView address){
        this.address = address;
    }


    /**
     * get pass data
     * 获取传递的数据
     * */
    public void getPassData(StorableWallet storableWallet,String iconId,int type) {
        this.storableWallet = storableWallet;
        this.iconId = iconId;
        this.type = type;
    }

    /**
     * init data
     * 初始化数据
     * */
    public void initData(TextView appBtnRight, TextView walletCopyPwdInfo,View walletCopyPwdInfoLine,
                         TextView success,TextView walletCopyKey,TextView walletCopyKeyStore) {
        try {
            if (storableWallet != null) {
                if (TextUtils.isEmpty(storableWallet.getPwdInfo())) {
                    walletCopyPwdInfo.setVisibility(View.GONE);
                    walletCopyPwdInfoLine.setVisibility(View.GONE);
                } else {
                    walletCopyPwdInfo.setText(context.getString(R.string.wallet_copy_pwd_info, storableWallet.getPwdInfo()));
                    walletCopyPwdInfoLine.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(storableWallet.getWalletImageId()) || !storableWallet.getWalletImageId().startsWith("icon_static_")) {
                    storableWallet.setWalletImageId(iconId);
                }

                String key = storableWallet.getPublicKey();
                if (!key.startsWith("0x")) {
                    key = "0x" + key;
                }
                address.setText(key);

                //Observe the purse
                if (storableWallet.getWalletType() == 1 && walletCopyKey != null && walletCopyKeyStore != null) {
                    walletCopyKey.setSelected(true);
                    walletCopyKeyStore.setSelected(true);
                    walletCopyKey.setTextColor(PhotonApplication.mContext.getResources().getColor(R.color.color_7fe2edea));
                    walletCopyKeyStore.setTextColor(PhotonApplication.mContext.getResources().getColor(R.color.color_7fe2edea));
                }
            }

            if (type == 1) {
                success.setVisibility(View.GONE);
                address.setVisibility(View.VISIBLE);
            } else {
                success.setVisibility(View.VISIBLE);
                address.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * password authentication
     * @ param type 0 for the private key, 1 for keyStore, 2 to delete the wallet
     */
    public void showPwdDialog(final int type) {
        CustomDialogFragment mdf = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG_INPUT_PWD);
        mdf.setEditCallbackListener(content -> {
            switch (type) {
                case 0:
                    getWalletPrivateKey(content, 0);
                    break;
                case 1:
                    getWalletKeyStore(content);
                    break;
                case 2:
                    getWalletPrivateKey(content, 2);
                    break;
            }
        });
        mdf.show(((AppCompatActivity)context).getSupportFragmentManager(), "mdf");
    }

    /**
     * Observe purse to delete
     */
    public void showDelSacnWalletDialog() {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        customDialogFragment.setTitle(context.getString(R.string.wallet_delete));
        customDialogFragment.setContent(context.getString(R.string.wallet_scan_del));
        customDialogFragment.setSubmitListener(() -> delWallet());
        customDialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "mdf");
    }

    /**
     * access to the private key
     * Password @ param walletPwd purse
     */
    private void getWalletPrivateKey(final String walletPwd, final int type) {
        LoadingDialog.show(context, "");
        new Thread(() -> {
            try {
                Credentials keys = WalletStorage.getInstance(context.getApplicationContext()).getFullWallet(context, walletPwd, storableWallet.getPublicKey());
                BigInteger privateKey = keys.getEcKeyPair().getPrivateKey();
                String privateKeyString = Numeric.toHexStringNoPrefixZeroPadded(privateKey,64);
                Message message = Message.obtain();
                message.what = type;
                message.obj = privateKeyString;
                mHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(4);
            } catch (JSONException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(4);
            } catch (CipherException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(3);
            } catch (RuntimeException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(5);
            }catch (Exception e){
                e.printStackTrace();
                mHandler.sendEmptyMessage(4);
            }
        }).start();
    }

    /**
     * get the keyStore
     * Password @ param walletPwd purse
     */
    private void getWalletKeyStore(final String walletPwd) {
        LoadingDialog.show(context, "");
        new Thread(() -> {
            try {
                String keyStore = WalletStorage.getInstance(context.getApplicationContext()).getWalletKeyStore(context, walletPwd, storableWallet.getPublicKey());
                Message message = Message.obtain();
                message.what = 1;
                message.obj = keyStore;
                mHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(4);
            } catch (JSONException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(4);
            } catch (CipherException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(3);
            } catch (RuntimeException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(5);
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://The private key
                    LoadingDialog.close();
                    updateCopyState(true);
                    String privateKey = (String) msg.obj;
                    Intent showPrivateKey = new Intent(context, WalletPrivateKeyActivity.class);
                    showPrivateKey.putExtra(WalletConstants.PRIVATE_KEY, privateKey);
                    context.startActivity(showPrivateKey);
                    break;
                case 1://keystore
                    LoadingDialog.close();
                    updateCopyState(false);
                    String keyStore = (String) msg.obj;
                    Intent showKeyStore = new Intent(context, WalletKeyStoreActivity.class);
                    showKeyStore.putExtra(WalletConstants.KEYSTORE, keyStore);
                    context.startActivity(showKeyStore);
                    break;
                case 2://To delete the wallet
                    delWallet();
                    break;
                case 3://Password mistake
                    LoadingDialog.close();
                    MyToast.showToast(context,context.getString(R.string.wallet_pwd_error));
                    break;
                case 4://The operation failure
                    LoadingDialog.close();
                    MyToast.showToast(context,context.getString(R.string.error));
                    break;
                case 5://Out of memory
                    LoadingDialog.close();
                    MyToast.showToast(context,context.getString(R.string.notification_wallgen_no_memory));
                    break;
            }
        }
    };

    /**
     * Delete the wallet data
     */
    private void delWallet() {
        String tempAddress = storableWallet.getPublicKey();
        if (!tempAddress.startsWith("0x")){
            tempAddress = "0x" + tempAddress;
        }
        WalletStorage.getInstance(context.getApplicationContext()).removeWallet(tempAddress, storableWallet.getWalletType(), context);
        PhotonNetUtil.getInstance().stopPhoton();
        WalletStorage.getInstance(context.getApplicationContext()).get().remove(storableWallet);
        if (WalletStorage.getInstance(context.getApplicationContext()).get().size() > 0) {
            WalletStorage.getInstance(context.getApplicationContext()).get().get(0).setSelect(true);
        } else {
            WalletStorage.destroy();
        }
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.WALLET_DELETE_SUCCESS);
        EventBus.getDefault().post(messageEvent);
        context.startActivity(new Intent(context, SplashActivity.class));
        ((Activity)context).finish();
    }


    public void updateCopyState(boolean isCopyPrivateKey){
        storableWallet.setBackup(true);
        if (isCopyPrivateKey){
            storableWallet.setCanExportPrivateKey(0);
        }
        ArrayList<StorableWallet> walletList = WalletStorage.getInstance(context.getApplicationContext()).get();
        for (int i = 0; i < walletList.size(); i++) {
            if (walletList.get(i).getPublicKey().equals(storableWallet.getPublicKey())) {
                walletList.get(i).setBackup(true);
                if (isCopyPrivateKey){
                    walletList.get(i).setCanExportPrivateKey(0);
                }
                break;
            }
        }
        WalletStorage.getInstance(context.getApplicationContext()).updateWalletToList(context, storableWallet.getPublicKey(), true);
    }

    public void onDestroy(){
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

}
