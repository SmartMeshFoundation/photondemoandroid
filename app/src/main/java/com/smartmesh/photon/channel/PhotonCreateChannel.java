package com.smartmesh.photon.channel;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.channel.contract.PhotonCreateContract;
import com.smartmesh.photon.channel.entity.PhotonStatusType;
import com.smartmesh.photon.channel.presenter.PhotonCreatePresenterImpl;
import com.smartmesh.photon.channel.util.ChannelNoteUtils;
import com.smartmesh.photon.channel.util.PhotonErrorUtils;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.channel.util.PhotonUtils;
import com.smartmesh.photon.custom.CustomCaptureActivity;
import com.smartmesh.photon.dialog.CustomDialogFragment;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.spinner.CustomSpinner;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.MySharedPrefs;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.util.Utils;
import com.smartmesh.photon.wallet.util.CustomWalletUtils;
import com.smartmesh.photon.wallet.util.WalletInfoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created on 2018/1/24.
 * cheate raiden channel ui
 * {@link PhotonTransferUI}
 * {@link PhotonChannelList}
 * 创建通道页面
 */

public class PhotonCreateChannel extends BaseActivity<PhotonCreateContract.Presenter> implements PhotonCreateContract.View, AdapterView.OnItemClickListener {

    @BindView(R.id.partner)
    EditText partner;//partner
    @BindView(R.id.deposit)
    EditText deposit;//deposit
    @BindView(R.id.photon_token_type)
    TextView mTokenType;
    @BindView(R.id.photon_channel_note)
    EditText channelNote;
    @BindView(R.id.wallet_create_scan)
    ImageView walletCreateScan;
    @BindView(R.id.photon_channel_enter)
    TextView channelEnter;
    @BindView(R.id.custom_spinner)
    CustomSpinner customSpinner;


    private int fromType;//0转账页面 1通道列表页面
    private String photonTokenAddress;
    private String photonBalanceOnChain = "0";

    @Override
    public int getLayoutId() {
        getPassData();
        return R.layout.photon_channel_create;
    }

    @Override
    public PhotonCreateContract.Presenter createPresenter() {
        return new PhotonCreatePresenterImpl(this);
    }

    private void getPassData() {
        fromType = getIntent().getIntExtra("fromType",-1);
    }


    @OnFocusChange({R.id.deposit})
    public void onFocusChange(View v, boolean hasFocus) {
        final String depositBalance = deposit.getText().toString();
        if (hasFocus) {
            if (TextUtils.isEmpty(depositBalance)) {
                if (!TextUtils.isEmpty(photonBalanceOnChain)){
                    deposit.setHint(getString(R.string.photon_create_balance, photonBalanceOnChain));
                }else{
                    deposit.setHint(getString(R.string.set_amount));
                }
            }
        } else {
            if (TextUtils.isEmpty(depositBalance)) {
                deposit.setHint(getString(R.string.set_amount));
            }
        }
    }

    @OnTextChanged(value = R.id.deposit,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s){
        try {
            mPresenter.checkDepositValue(s);
            checkEnableCreate();
        } catch (Exception e) {
            e.printStackTrace();
            checkEnableCreate();
        }
    }

    @OnTextChanged(value = R.id.partner,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterPartnerTextChanged(Editable s){
        checkEnableCreate();
    }

    private void checkEnableCreate(){
        try {
            String depositValue = deposit.getText().toString();
            String partnerValue = partner.getText().toString();
            if (TextUtils.isEmpty(depositValue) || TextUtils.isEmpty(partnerValue)){
                channelEnter.setEnabled(false);
            }else{
                channelEnter.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {
        setBottomTitle(getString(R.string.photon_channel_create_2));
        EventBus.getDefault().register(this);
        photonTokenAddress = PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS;

        loadSpinnerData();
        customSpinner.addOnItemClickListener(this);
        mPresenter.getBalanceFromPhoton();
        mTokenType.setText(PhotonUtils.getPhotonTokenSymbol(photonTokenAddress));
        walletCreateScan.setImageResource(R.mipmap.icon_scan);
    }

    /**
     * 加载显示的币种
     * 也就两个 SMT  MESH
     * */
    private void loadSpinnerData() {
        List<String> dataSet = new LinkedList<>(Collections.singletonList(getString(R.string.smt)));
        customSpinner.setLeftTextView(getString(R.string.token));
        customSpinner.attachDataSource(dataSet);
    }

    @OnClick({R.id.photon_channel_enter,R.id.wallet_create_scan,R.id.close_key_word})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photon_channel_enter://create photon channel
                createChannel();
                break;
            case R.id.wallet_create_scan://goto scan qe code page
                Intent i = new Intent(PhotonCreateChannel.this, CustomCaptureActivity.class);
                i.putExtra("type", 1);
                startActivityForResult(i, 100);
                break;
            case R.id.close_key_word://hidden key board
                Utils.hiddenKeyBoard(PhotonCreateChannel.this);
                break;
            default:
                super.onClick(v);
                break;

        }
    }

    /**
     * 创建通道
     * create channel method
     * */
    private void createChannel(){
        try {

            if (PhotonUtils.offLineOrSourceIsEmpty()){
                return;
            }

            String partnerAddress = partner.getText().toString();
            String depositBalance = deposit.getText().toString();
            if(!CustomWalletUtils.isValidAddress(partnerAddress)){
                MyToast.showToast(PhotonCreateChannel.this, getResources().getString(R.string.error_address));
                return;
            }
            if (TextUtils.isEmpty(depositBalance) || TextUtils.isEmpty(partnerAddress)) {
                MyToast.showToast(PhotonCreateChannel.this, getResources().getString(R.string.photon_open_channel_error));
                return;
            }
            float tempValue;
            try {
                tempValue = Float.parseFloat(depositBalance);
            } catch (Exception e) {
                e.printStackTrace();
                tempValue = 0;
            }
            if (tempValue <= 0) {
                MyToast.showToast(PhotonCreateChannel.this, getResources().getString(R.string.photon_channel_error_balance));
                return;
            }

            try {
                if(tempValue > Float.parseFloat(photonBalanceOnChain)){
                    MyToast.showToast(PhotonCreateChannel.this,getString(R.string.balance_not_enough));
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
            customDialogFragment.setHindCancelButton(true);
            customDialogFragment.setTitle(getString(R.string.dialog_prompt));
            customDialogFragment.setConfirmButton(getString(R.string.ok));
            customDialogFragment.setContent(getString(R.string.dialog_create_content));
            customDialogFragment.setSubmitListener(() -> mPresenter.createChannelMethod(photonTokenAddress,partnerAddress,depositBalance));
            customDialogFragment.show(getSupportFragmentManager(),"mdf");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String address = data.getStringExtra("address");
            partner.setText(address);
            partner.setSelection(partner.getText().length());
        }
    }

    /**
     * 开始创建通道
     * start create channel
     * */
    @Override
    public void createChannelStart() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_ING);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 创建通道api调用成功
     * Create channel api call succeeded
     * */
    @Override
    public void createChannelSuccess(String response) {
        try {
            JSONObject object = new JSONObject(response);
            int errorCode = object.optInt("error_code");
            if (errorCode == 0){
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_SUCCESS);
                EventBus.getDefault().post(messageEvent);
            }else{
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_ERROR);
                String errorMessage = object.optString("error_message");
                if (TextUtils.isEmpty(errorMessage)){
                    errorMessage = getResources().getString(R.string.photon_open_channel_error);
                    errorCode = -1;
                }
                messageEvent.setMessage(errorMessage);
                messageEvent.setErrorCode(errorCode);
                EventBus.getDefault().post(messageEvent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_ERROR);
            messageEvent.setMessage(getResources().getString(R.string.photon_open_channel_error));
            messageEvent.setErrorCode(-1);
            EventBus.getDefault().post(messageEvent);
        }
    }

    /**
     * 创建通道api调用失败
     * Create channel api call failed
     * */
    @Override
    public void createChannelError() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_ERROR);
        messageEvent.setMessage(getResources().getString(R.string.photon_open_channel_error));
        messageEvent.setErrorCode(-1);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 光子未启动
     * Photon is not activated
     * */
    @Override
    public void photonNotStart() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_ERROR);
        messageEvent.setMessage(getResources().getString(R.string.photon_restart));
        messageEvent.setErrorCode(-1);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 获取链上余额和通道内余额成功
     * Successfully obtained the balance on the chain and the balance in the channel
     * @param jsonString  返回结果
     * */
    @Override
    public void getPhotonBalanceFromApiSuccess(String jsonString) {
        try {
            if (!TextUtils.isEmpty(jsonString)){
                JSONObject jsoObject = new JSONObject(jsonString);
                int errorCode = jsoObject.optInt("error_code");
                if (errorCode == 0){
                    JSONArray array = jsoObject.optJSONArray("data");
                    if (array != null){
                        for (int i = 0 ; i < array.length() ; i++){
                            String tokenAddress = array.optJSONObject(i).optString("token_address");
                            String chainBalance = array.optJSONObject(i).optString("balance_on_chain");
                            if (PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS.equalsIgnoreCase(tokenAddress)){
                                if (!TextUtils.isEmpty(chainBalance)){
                                    photonBalanceOnChain = new BigDecimal(chainBalance)
                                            .divide(Convert.Unit.ETHER.getWeiFactor(), 6, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                                }
                            }
                        }
                    }
                }else{
                    if (!TextUtils.isEmpty(photonBalanceOnChain)){
                        String chainBalance = MySharedPrefs.readString(PhotonCreateChannel.this,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_ON_CHAIN_BALANCE);
                        if (!TextUtils.isEmpty(chainBalance)){
                            photonBalanceOnChain = chainBalance;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 合约交易列表
     * goto contract transaction list page
     * */
    private void intoContractQueryUI(){
        Intent intent = new Intent(this,PhotonTransferQueryUI.class);
        intent.putExtra("showContract",true);
        intent.putExtra("fromType",fromType);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        photonTokenAddress = PhotonUtils.getPhotonTokenAddress(item);
        mPresenter.getBalanceFromPhoton();
        mTokenType.setText(PhotonUtils.getPhotonTokenSymbol(photonTokenAddress));
    }

    /**
     * code    PHOTON_EVENT_CHANNEL_CREATE_ING              创建通道api调用开始                      Create a channel api call to start
     * code    PHOTON_EVENT_CHANNEL_CREATE_SUCCESS          创建通道api调用成功                      Create channel api call succeeded
     * code    PHOTON_EVENT_CHANNEL_CREATE_ERROR            创建通道api调用失败                      Create channel api call failed
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        if (messageEvent != null){
            if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_ING == messageEvent.getCode()){
                LoadingDialog.show(PhotonCreateChannel.this, getString(R.string.photon_channel_create_enter_ing));
            }else if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_SUCCESS == messageEvent.getCode()){
                LoadingDialog.close();
                ChannelNoteUtils.insertChannelNote(photonTokenAddress, WalletInfoUtils.getInstance().getSelectAddress(),partner.getText().toString(),channelNote.getText().toString());
                MyToast.showToast(PhotonCreateChannel.this, getResources().getString(R.string.photon_open_channel_success));
                intoContractQueryUI();
            }else if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_CREATE_ERROR == messageEvent.getCode()){
                LoadingDialog.close();
                PhotonErrorUtils.handlerPhotonError(messageEvent.getErrorCode(),messageEvent.getMessage());
            }
        }
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }
}
