package com.smartmesh.photon.channel;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.channel.contract.PhotonDepositContract;
import com.smartmesh.photon.channel.presenter.PhotonDepositPresenterImpl;
import com.smartmesh.photon.channel.util.PhotonErrorUtils;
import com.smartmesh.photon.channel.util.PhotonIntentDataUtils;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.channel.util.PhotonUtils;
import com.smartmesh.photon.dialog.CustomDialogFragment;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.MySharedPrefs;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created  on 2018/1/26.
 * photon channel add ui
 * 光子通道内补充余额页面
 */

public class PhotonChannelDepositUI extends BaseActivity<PhotonDepositContract.Presenter> implements PhotonDepositContract.View {

    @BindView(R.id.partner)
    TextView partner;//partner
    @BindView(R.id.balance)
    TextView balance;//pay
    @BindView(R.id.channel_token_name)
    TextView channelTokenName;
    @BindView(R.id.channel_add_number)
    EditText channelAddNumber;//add number
    @BindView(R.id.photon_channel_add)
    TextView channelAdd;//to add
    @BindView(R.id.close_key_word)
    View closeKeyWord;

    private String tokenAddress;
    private String partnerAddress;

    private String photonBalanceOnChain = "0";

    @Override
    public int getLayoutId() {
        getPassData();
        return R.layout.photon_channel_deposit_layout;
    }

    @Override
    public PhotonDepositPresenterImpl createPresenter() {
        return new PhotonDepositPresenterImpl(this);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        setBottomTitle(getString(R.string.photon_channel_list_deposit));
        mPresenter.getBalanceFromPhoton();
        channelTokenName.setText(PhotonUtils.getPhotonTokenSymbol(tokenAddress));
        partner.setText(partnerAddress);
    }
    
    @OnClick({R.id.photon_channel_add,R.id.close_key_word})
    public void onClickView(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.photon_channel_add:
                channelAddBalanceMethod();
                break;
            case R.id.close_key_word:
                Utils.hiddenKeyBoard(PhotonChannelDepositUI.this);
                break;
        }
    }

    private void getPassData() {
        tokenAddress = getIntent().getStringExtra(PhotonIntentDataUtils.PHOTON_TOKEN_ADDRESS);
        partnerAddress = getIntent().getStringExtra(PhotonIntentDataUtils.PHOTON_PARTNER_ADDRESS);
    }


    @OnTextChanged(value = R.id.channel_add_number,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s){
        try {
            if (TextUtils.isEmpty(s.toString())){
                channelAdd.setEnabled(false);
                return;
            }
            String temp = s.toString();
            double tempInt = Double.parseDouble(temp);
            if (tempInt > 0){
                channelAdd.setEnabled(true);
            }else{
                channelAdd.setEnabled(false);
            }
            mPresenter.checkDepositValue(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * partnerAddress 	string 	partner_address 	通道对方地址
     * tokenAddress 	string 	token_address 	哪种token
     * settleTimeout 	string 	settle_timeout 	通道结算时间 存款为0
     * balanceStr 	big.Int 	balance 	存入金额，一定大于0
     * newChannel 	bool 	new_channel 	判断通道是否存在，决定此次行为是创建通道并存款还是只存款  false为存钱
     *
     * partnerAddress string partner_address channel address
     * tokenAddress string token_address which token
     * settleTimeout string settle_timeout Channel settlement time Deposit is 0
     * balanceStr big.Int balance deposit amount, must be greater than 0
     * newChannel bool new_channel Determines whether the channel exists or not, and decides whether the behavior is to create a channel and deposit or only deposit false to save money.
     * */
    private void channelAddBalanceMethod() {
        try {

            if (PhotonUtils.offLineOrSourceIsEmpty()){
                return;
            }

            final String channelNumber = channelAddNumber.getText().toString().trim();
            float tempValue;
            try {
                tempValue = Float.parseFloat(channelNumber);
            }catch (Exception e){
                e.printStackTrace();
                tempValue = 0;
            }
            if (TextUtils.isEmpty(channelNumber) || tempValue <= 0) {
                MyToast.showToast(PhotonChannelDepositUI.this, getResources().getString(R.string.photon_channel_error_balance));
                return;
            }

            try {
                if(tempValue > Float.parseFloat(photonBalanceOnChain)){
                    MyToast.showToast(PhotonChannelDepositUI.this,getString(R.string.balance_not_enough));
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
            customDialogFragment.setHindCancelButton(true);
            customDialogFragment.setTitle(getString(R.string.dialog_prompt));
            customDialogFragment.setConfirmButton(getString(R.string.ok));
            customDialogFragment.setContent(getString(R.string.dialog_deposit_content));
            customDialogFragment.setSubmitListener(() -> mPresenter.depositChannelMethod(tokenAddress,partnerAddress,channelNumber));
            customDialogFragment.show(getSupportFragmentManager(),"mdf");
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
        intent.putExtra("fromType",2);
        startActivity(intent);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * start deposit
     * */
    @Override
    public void depositChannelStart() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_ING);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * deposit success
     * */
    @Override
    public void depositChannelSuccess(String response) {
        try {
            JSONObject object = new JSONObject(response);
            int errorCode = object.optInt("error_code");
            if (errorCode == 0){
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_SUCCESS);
                EventBus.getDefault().post(messageEvent);
            }else{
                String errorMessage = object.optString("error_message");
                MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR);
                if (TextUtils.isEmpty(errorMessage)){
                    errorMessage = getResources().getString(R.string.photon_channel_create_enter_add_error);
                    errorCode = -1;
                }
                messageEvent.setMessage(errorMessage);
                messageEvent.setErrorCode(errorCode);
                EventBus.getDefault().post(messageEvent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR);
            messageEvent.setMessage(getResources().getString(R.string.photon_channel_create_enter_add_error));
            messageEvent.setErrorCode(-1);
            EventBus.getDefault().post(messageEvent);
        }
    }

    /**
     * deposit error
     * */
    @Override
    public void depositChannelError() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR);
        messageEvent.setMessage(getResources().getString(R.string.photon_channel_create_enter_add_error));
        messageEvent.setErrorCode(-1);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * photon does not start
     * */
    @Override
    public void photonNotStart() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR);
        messageEvent.setMessage(getResources().getString(R.string.photon_restart));
        messageEvent.setErrorCode(-1);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * get photon balance success
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
                                    photonBalanceOnChain = new BigDecimal(chainBalance).divide(Convert.Unit.ETHER.getWeiFactor(), 6, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                                    MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_BALANCE_ON_CHAIN_SUCCESS);
                                    EventBus.getDefault().post(messageEvent);
                                }
                            }
                        }
                    }
                }else{
                    if (!TextUtils.isEmpty(photonBalanceOnChain)){
                        String chainBalance = MySharedPrefs.readString(PhotonChannelDepositUI.this,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_ON_CHAIN_BALANCE);
                        if (!TextUtils.isEmpty(chainBalance)){
                            photonBalanceOnChain = chainBalance;
                            MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_BALANCE_ON_CHAIN_SUCCESS);
                            EventBus.getDefault().post(messageEvent);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * code    PHOTON_EVENT_CHANNEL_DEPOSIT_ING              通道存款api调用开始                     Channel deposit api call starts
     * code    PHOTON_EVENT_CHANNEL_DEPOSIT_SUCCESS          通道存款api调用成功                     Channel deposit api call succeeded
     * code    PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR            通道存款api调用失败                     Channel deposit api call failed
     * code    PHOTON_EVENT_BALANCE_ON_CHAIN_SUCCESS         链上余额api调用成功                     The chain balance api is successfully called.
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        if (messageEvent != null){
            if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_ING == messageEvent.getCode()){
                LoadingDialog.show(PhotonChannelDepositUI.this, getString(R.string.photon_channel_create_enter_add));
            }else if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_SUCCESS == messageEvent.getCode()){
                LoadingDialog.close();
                intoContractQueryUI();
            }else if (RequestCodeUtils.PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR == messageEvent.getCode()){
                LoadingDialog.close();
                PhotonErrorUtils.handlerPhotonError(messageEvent.getErrorCode(),messageEvent.getMessage());
            }else if (RequestCodeUtils.PHOTON_EVENT_BALANCE_ON_CHAIN_SUCCESS == messageEvent.getCode()){
                if (balance != null){
                    balance.setText(photonBalanceOnChain);
                }
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
