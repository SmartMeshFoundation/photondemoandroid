package com.smartmesh.photon.channel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.channel.adapter.ChannelListAdapter;
import com.smartmesh.photon.channel.contract.PhotonTransferContract;
import com.smartmesh.photon.channel.entity.PhotonChannelVo;
import com.smartmesh.photon.channel.entity.PhotonFeeEntity;
import com.smartmesh.photon.channel.presenter.PhotonTransferPresenterImpl;
import com.smartmesh.photon.channel.util.ChannelNoteUtils;
import com.smartmesh.photon.channel.util.PhotonErrorUtils;
import com.smartmesh.photon.channel.util.PhotonNetUtil;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.channel.util.PhotonUtils;
import com.smartmesh.photon.custom.CustomCaptureActivity;
import com.smartmesh.photon.dialog.CustomDialogFragment;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.spinner.CustomSpinner;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

/**
 * Created on 2018/1/26.
 * photon transfer ui
 * 光子转账页面
 * {@link com.smartmesh.photon.ui.MainActivity}
 * {@link PhotonChannelList}
 */

public class PhotonTransferUI extends BaseActivity<PhotonTransferContract.Presenter> implements PhotonTransferContract.View, AdapterView.OnItemClickListener {

    @BindView(R.id.photon_channel_pay)
    TextView channelPay;
    @BindView(R.id.photon_to_value)
    EditText toValue;
    @BindView(R.id.address_spinner)
    Spinner mSpinner;
    @BindView(R.id.photon_show_text)
    TextView mShowText;
    @BindView(R.id.photon_show_text_address)
    TextView showTextAddress;
    @BindView(R.id.photon_show_note_body)
    LinearLayout showNoteBody;
    @BindView(R.id.photon_scan_qr)
    ImageView channelQrImg;
    @BindView(R.id.photon_create_channel)
    ImageView photonCreate;
    @BindView(R.id.photon_channel_list)
    ImageView photonList;
    @BindView(R.id.photon_get_channel)
    TextView getChannel;
    @BindView(R.id.down_flg)
    ImageView down_flg;
    @BindView(R.id.photon_wallet_address_delete)
    ImageView walletAddressDelete;
    @BindView(R.id.photon_version_code)
    TextView photonVersionCode;
    @BindView(R.id.custom_spinner)
    CustomSpinner customSpinner;

    private String photonTokenAddress;

    private String toAddress;

    private String scanResultAddress = "";

    private static int RAIDEN_CHANNEL_CREATE = 1001;

    private ChannelListAdapter mAdapter;
    private ArrayList<PhotonChannelVo> channelList = new ArrayList<>();
    private PhotonChannelVo tempChannelVo;

    private boolean notShowSpinner;
    private String sendAmount = "-1";

    @Override
    public int getLayoutId() {
        return R.layout.photon_transfer_layout;
    }

    @Override
    public PhotonTransferContract.Presenter createPresenter() {
        return new PhotonTransferPresenterImpl(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toValue != null){
            toValue.setText("");
            toValue.setHint(getString(R.string.set_amount));
        }
        mPresenter.loadChannelList(false);
        mPresenter.getPhotonVersionCode();
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(PhotonUrl.ACTION_PHOTON_RECEIVER_TRANSFER);
        registerReceiver(receiver, filter);
        loadSpinnerData();
        customSpinner.addOnItemClickListener(this);
        photonTokenAddress = PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS;
        mAdapter = new ChannelListAdapter(this, channelList);
        mSpinner.setAdapter(mAdapter);
        checkChannelEmpty();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && (PhotonUrl.ACTION_PHOTON_RECEIVER_TRANSFER.equals(intent.getAction()))) {
                mPresenter.loadChannelList(false);
            }
        }
    };

    /**
     * 解析通道列表
     * parse channel list
     * @param jsonString response string
     */
    private void parseJson(String jsonString,int showToast) {
        LoadingDialog.close();
        if (TextUtils.isEmpty(jsonString) || "null".equals(jsonString)) {
            channelList.clear();
            mAdapter.resetSource(channelList);
            if (mSpinner != null && mSpinner.isActivated()){
                mSpinner.performClick();
            }
            checkChannelEmpty();
            if (showToast == 1){
                showToast(getString(R.string.photon_channel_no_use));
            }
            return;
        }
        try {
            channelList.clear();
            JSONObject object = new JSONObject(jsonString);
            int errorCode = object.optInt("error_code");
            if (errorCode == 0){
                JSONArray array = object.optJSONArray("data");
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject dataObject = array.optJSONObject(i);
                        PhotonChannelVo channelVo = new PhotonChannelVo().parse(dataObject);
                        if (channelVo.getState() == 1){
                            if (photonTokenAddress.equalsIgnoreCase(channelVo.getTokenAddress())){
                                channelList.add(channelVo);
                            }
                        }
                    }
                }
            }
            if (channelList.size() <= 0 && showToast == 1){
                showToast(getString(R.string.photon_channel_no_use));
            }
            mAdapter.resetSource(channelList);
            checkCurrentAmount();
            checkChannelEmpty();
            if (mSpinner != null && mSpinner.isActivated()){
                mSpinner.performClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前选中通道中的抵押余额
     * Get the mortgage balance in the currently selected channel
     * */
    private void checkCurrentAmount(){
        if (!TextUtils.isEmpty(toAddress) && channelList != null && channelList.size() > 0){
            for (int i = 0 ; i < channelList.size() ; i++){
                if (TextUtils.equals(channelList.get(i).getPartnerAddress().toLowerCase(),toAddress.toLowerCase())){
                    toValue.setText("");
                    toValue.setHint(channelList.get(i).getBalance());
                    break;
                }
            }
        }
    }

    /**
     * 检测通道显示状态
     * Detect channel display status
     * */
    private void checkChannelEmpty(){
        if (TextUtils.isEmpty(scanResultAddress)){
            normalAddress();
        }else{
            isScanAddress();
        }
        scanResultAddress = "";
    }

    /**
     * 正常显示UI
     * Display UI normally
     * */
    private void normalAddress(){
        if (channelList == null || channelList.size() <= 0){
            if (getChannel != null){
                getChannel.setVisibility(View.VISIBLE);
            }
            if (down_flg != null){
                down_flg.setVisibility(View.GONE);
            }

            if (walletAddressDelete != null){
                walletAddressDelete.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(toAddress)){
                toAddress = "";
                if (mShowText != null){
                    mShowText.setText("");
                }
                if (showNoteBody != null){
                    showNoteBody.setVisibility(View.GONE);
                }
            }
        }else{
            if (walletAddressDelete != null){
                walletAddressDelete.setVisibility(View.GONE);
            }

            if (getChannel != null){
                getChannel.setVisibility(View.GONE);
            }

            if (down_flg != null){
                down_flg.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(toAddress)){
                boolean hasExist = false;
                for (int i = 0 ; i < channelList.size() ; i++){
                    if (TextUtils.equals(toAddress.toLowerCase(),channelList.get(i).getPartnerAddress().toLowerCase())){
                        mSpinner.setSelection(i,true);
                        hasExist = true;
                        break;
                    }
                }

                if (!hasExist){
                    toAddress = "";
                    if (mShowText != null){
                        mShowText.setText("");
                    }
                    if (showNoteBody != null){
                        showNoteBody.setVisibility(View.GONE);
                    }
                    if (channelList != null && channelList.size() >0){
                        mSpinner.setSelection(0,true);
                        setChannelNote(channelList.get(0).getTokenAddress(),channelList.get(0).getPartnerAddress(),true);
                    }
                }
            }
        }
    }

    /**
     * 是扫描二维码回来
     * Is scanning the QR code back
     * */
    private void isScanAddress(){
        if (channelList == null || channelList.size() <= 0){
            if (getChannel != null){
                getChannel.setVisibility(View.GONE);
            }
            if (down_flg != null){
                down_flg.setVisibility(View.GONE);
            }

            if (walletAddressDelete != null){
                walletAddressDelete.setVisibility(View.VISIBLE);
            }
        }else{
            boolean hasExist = false;
            for (int i = 0 ; i < channelList.size() ; i++){
                if (TextUtils.equals(scanResultAddress.toLowerCase(),channelList.get(i).getPartnerAddress().toLowerCase())){
                    mSpinner.setSelection(i,true);
                    hasExist = true;
                    break;
                }
            }
            if (hasExist){
                if (walletAddressDelete != null){
                    walletAddressDelete.setVisibility(View.GONE);
                }

                if (getChannel != null){
                    getChannel.setVisibility(View.GONE);
                }
                if (down_flg != null){
                    down_flg.setVisibility(View.VISIBLE);
                }
            }else{
                if (walletAddressDelete != null){
                    walletAddressDelete.setVisibility(View.VISIBLE);
                }

                if (getChannel != null){
                    getChannel.setVisibility(View.GONE);
                }
                if (down_flg != null){
                    down_flg.setVisibility(View.GONE);
                }
            }
        }
    }

    @OnItemSelected(value = R.id.address_spinner , callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        if (channelList != null && channelList.size() > 0){
            String address = channelList.get(position).getPartnerAddress();
            String tokenAddress = channelList.get(position).getTokenAddress();
            tempChannelVo = channelList.get(position);
            if (toValue != null){
                toValue.setText("");
                if (tempChannelVo != null && !TextUtils.isEmpty(tempChannelVo.getBalance())){
                    toValue.setHint(tempChannelVo.getBalance());
                }else {
                    toValue.setHint(getString(R.string.set_amount));
                }
            }
            setChannelNote(tokenAddress,address,true);
            notShowSpinner = true;
        }
    }

    @OnTextChanged(value = R.id.photon_to_value,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s){
        try {
            String temp = s.toString();
            int posDot = temp.indexOf(".");
            if (posDot <= 0) {
                if (temp.length() <= 8) {
                    return;
                } else {
                    s.delete(8, 9);
                    return;
                }
            }
            if (temp.length() - posDot - 1 > 2) {
                s.delete(posDot + 3, posDot + 4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @OnClick({R.id.photon_channel_pay,R.id.down_flg,R.id.photon_wallet_address_delete,R.id.photon_get_channel
            ,R.id.photon_scan_qr,R.id.photon_create_channel,R.id.photon_channel_list,
            R.id.close_key_word,R.id.photon_app_back})
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.photon_channel_pay://photon transfer
                sendTransferMethod();
                break;
            case R.id.photon_app_back://exist photon
                showClosePhotonDialog();
                break;
            case R.id.down_flg:// get channel list
                loadChannelList();
                break;
            case R.id.photon_get_channel:// get channel list
                LoadingDialog.show(this,"");
                mPresenter.loadChannelList(true);
                break;
            case R.id.photon_wallet_address_delete:// clear wallet address
                deleteWalletAddress();
                break;
            case R.id.photon_scan_qr: // scan wallet from qr
                Intent photonScan = new Intent(this, CustomCaptureActivity.class);
                photonScan.putExtra("type", 1);
                startActivityForResult(photonScan, 100);
                break;
            case R.id.photon_create_channel://goto create channel page
                Intent intent = new Intent(this, PhotonCreateChannel.class);
                intent.putExtra("fromType", 0);
                startActivityForResult(intent, RAIDEN_CHANNEL_CREATE);
                break;
            case R.id.photon_channel_list://goto channel list page
                intoPhotonChannelList();
                break;
            case R.id.close_key_word://hidden key board
                Utils.hiddenKeyBoard(PhotonTransferUI.this);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        showClosePhotonDialog();
    }

    /**
     * 显示退出光子弹框
     * Display exit photo dialog
     * */
    private void showClosePhotonDialog(){
        CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        customDialogFragment.setTitle(getString(R.string.dialog_prompt));
        customDialogFragment.setContent(getString(R.string.photon_stop));
        customDialogFragment.setSubmitListener(() -> {
            Utils.hiddenKeyBoard(PhotonTransferUI.this);
            finish();
        });
        customDialogFragment.show(getSupportFragmentManager(),"mdf");
    }

    /**
     * 加载通道列表
     * load channel list
     * */
    private void loadChannelList(){
        if (channelList != null && channelList.size() > 0){
            if (mSpinner != null){
                mSpinner.performClick();
            }
        }else{
            LoadingDialog.show(this,"");
            mPresenter.loadChannelList(true);
        }
    }

    /**
     * 删除转账地址
     * Delete transfer address
     * */
    private void deleteWalletAddress(){
        toAddress = "";
        if (mShowText != null){
            mShowText.setText("");
        }
        if (showNoteBody != null){
            showNoteBody.setVisibility(View.GONE);
        }
        if (channelList != null && channelList.size() >0){
            mSpinner.setSelection(0,true);
            setChannelNote(channelList.get(0).getTokenAddress(),channelList.get(0).getPartnerAddress(),true);
        }
        checkChannelEmpty();
    }

    /**
     * 转账相关
     * about transfer
     * */
    private void sendTransferMethod(){

        if (TextUtils.isEmpty(toAddress)){
            showToast(getString(R.string.input_address));
            return;
        }

        String amount = toValue.getText().toString().trim();

        float tempValue;
        try {
            tempValue = Float.parseFloat(amount);
        } catch (Exception e) {
            e.printStackTrace();
            tempValue = 0;
        }
        if (tempValue <= 0) {
            showToast(getString(R.string.input_value));
            return;
        }

        if (TextUtils.isEmpty(amount)){
            showToast(getString(R.string.input_value));
            return;
        }

        if (channelList == null || channelList.size() <= 0){
            showToast(getString(R.string.photon_channel_no));
            return;
        }

        boolean canTransfer = checkTransferAmount(amount);
        if (!canTransfer){
            showToast(getString(R.string.balance_not_enough));
            return;
        }

        /**
         * 直接调用DirectTransfer
         *     成功 -> 结束
         *     失败 -> 解析错误码:
         *                 3002 通道不存在 -> 调用FindPath,成功调用MediatedTransfer,失败结束
         *                 1002 直接通道余额不足 -> 调用FindPath,成功调用MediatedTransfer,失败结束
         *                 1023 无网时间过长 -> 失败结束
         *                 1016 对方不在线 -> 失败结束
         *                 其他错误 -> 失败结束
         * Directly call DirectTransfer
         *   Success -> End
         *   Failed -> Parse error code:
         *          3002 channel does not exist -> Call FindPath, successfully call MediatedTransfer, the end of failure
         *          1002 Insufficient direct channel balance -> Call FindPath, successfully call MediatedTransfer, end of failure
         *          1023 No network time is too long -> End of failure
         *          1016 The other party is not online -> End of failure
         *          Other errors -> End of failure
         * */
        photonTransfer(amount,true,true,"");
    }

    /**
     * 检测转账金额是否超出可用余额
     * Check if the transfer amount exceeds the available balance
     * @param amount  转账金额  transfer amount
     * */
    private boolean checkTransferAmount(String amount){
        try {
            if (channelList != null){
                boolean canTransfer = false;
                for (int i = 0 ; i < channelList.size();i++){
                    String balance = channelList.get(i).getBalance();
                    String lockedAmount = channelList.get(i).getLockedAmount();
                    if (Float.parseFloat(balance) - Float.parseFloat(lockedAmount) - Float.parseFloat(amount) >= 0){
                        canTransfer = true;
                        break;
                    }
                }
                return canTransfer;
            }else{
                return true;
            }
        }catch (Exception e){
            return true;
        }
    }

    /**
     * 进入通道列表页面
     * goto photon channel list
     * */
    private void intoPhotonChannelList(){
        Intent photonIntent = new Intent(PhotonTransferUI.this, PhotonChannelList.class);
        photonIntent.putExtra("type", 1);
        startActivity(photonIntent);
    }

    /**
     * 扫码回归
     * scan call back
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RAIDEN_CHANNEL_CREATE){
                mPresenter.loadChannelList(true);
            }else{
                String address = data.getStringExtra("address");
                scanResultAddress = address;
                toAddress = address;
                if (showNoteBody != null){
                    showNoteBody.setVisibility(View.GONE);
                }
                if (mShowText != null){
                    mShowText.setText(address);
                }
                if (toValue != null){
                    toValue.setHint(getString(R.string.set_amount));
                }
            }
        }
    }

    /**
     * 转账正常接收到数据
     * 此时可能成功也可能失败
     * 不再等待，直接进入channel list 页面
     * @param jsonString     转账api返回数据
     * @param isDirect       是否是直接通道转账
     * @param amount         转账金额
     * Transfers receive data normally
     * It may or may not fail at this time
     * No longer waiting, go directly to the channel list page
     * @param jsonString transfer api returns data
     * @param isDirect is direct channel transfer
     * @param amount Transfer amount
     * */
    private void inquiryTransferStatus(final String jsonString,boolean isDirect,String amount) {
        try {
            JSONObject object = new JSONObject(jsonString);
            int errorCode = object.optInt("error_code");
            if (errorCode == 0) {
                LoadingDialog.close();
                showToast(getResources().getString(R.string.photon_transfer_success));
                intoPhotonChannelList();
            }else if ((errorCode == 1002 || errorCode == 3002) && isDirect){
                mPresenter.getFeeFindPath(photonTokenAddress,amount,toAddress);
            }else{
                LoadingDialog.close();
                PhotonErrorUtils.handlerPhotonError(errorCode,object.optString("error_message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoadingDialog.close();
        }
    }

    /**
     * get channel list success
     * */
    @Override
    public void loadChannelSuccess(String jsonString,boolean showToast) {
        parseJson(jsonString,showToast ? 1 : 0);
    }

    /**
     * Failed to get channel list
     * */
    @Override
    public void loadChannelError(boolean showToast) {
        if (showToast){
            showToast(getString(R.string.error_get_photon_list_1));
        }
        LoadingDialog.close();
    }

    /**
     * 转账之前检测 显示loading页面
     * Check before transfer Show loading page
     * */
    @Override
    public void transferCheck() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_TRANSFER_START);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 转账api调用成功 实际结果
     * Transfer api call succeeded Actual result
     * */
    @Override
    public void transferSuccess(String jsonString,boolean isDirect,String amount) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_TRANSFER_SUCCESS);
        messageEvent.setMessage(jsonString);
        messageEvent.setBooleanType(isDirect);
        messageEvent.setAmount(amount);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 转账api调用失败
     * Transfer api call failed
     * */
    @Override
    public void transferError(boolean isDirect,String amount) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_TRANSFER_ERROR);
        messageEvent.setBooleanType(isDirect);
        messageEvent.setAmount(amount);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 获取路由信息api调用成功
     * Get the routing information api call succeeded
     * */
    @Override
    public void loadFindPathSuccess(String jsonString,String amount) {
        if (TextUtils.isEmpty(jsonString)){
            photonTransfer(amount,false,false,"");
        }else{
            try {
                String fee =  "0";
                JSONObject object = new JSONObject(jsonString);
                int errorCode = object.optInt("error_code",-1);
                String errorMessage = object.optString("error_message");
                if (errorCode == 0){
                    JSONArray array = object.optJSONArray("data");
                    if (array != null && array.length() > 0){
                        JSONObject objectFee = array.optJSONObject(0);
                        fee = objectFee.optString("fee");
                    }
                    if (TextUtils.equals(fee,"0")){
                        photonTransfer(amount,false,false,array == null ? "" : array.toString());
                    }else{
                        PhotonFeeEntity feeEntity = new PhotonFeeEntity();
                        feeEntity.setFee(fee);
                        feeEntity.setAmount(amount);
                        feeEntity.setFilePath(array == null ? "" : array.toString());
                        EventBus.getDefault().post(feeEntity);
                    }
                }else{
                    MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_FIND_PATH_ERROR);
                    messageEvent.setMessage(errorMessage);
                    EventBus.getDefault().post(messageEvent);
                }
            }catch (Exception e){
                e.printStackTrace();
                photonTransfer(amount,false,false,"");
            }
        }
    }

    /**
     * 获取路由信息api调用失败
     * Get routing information api call failed
     * */
    @Override
    public void loadFindPathError(String amount) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_FIND_PATH_ERROR);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 转账付费弹框
     * Transfer payment dialog
     * */
    private void photonTransferDialog(PhotonFeeEntity feeEntity ){
        try {
            if (feeEntity == null){
                photonTransfer(sendAmount,false,false,"");
            }else{
                LoadingDialog.close();
                CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
                customDialogFragment.setTitle(getString(R.string.dialog_prompt));
                customDialogFragment.setConfirmButton(getString(R.string.photon_channel_transfer_type_2));
                String tokenSymbol = "";
                tokenSymbol = TextUtils.equals(photonTokenAddress,PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS) ? "SMT" : "MESH";
                String tempFee = Convert.fromWei(feeEntity.getFee(),Convert.Unit.ETHER).stripTrailingZeros().toPlainString();
                customDialogFragment.setContent(getString(R.string.photon_channel_transfer_type_1,tempFee,tokenSymbol));
                customDialogFragment.setSubmitListener(() -> photonTransfer(feeEntity.getAmount(),true,false,feeEntity.getFilePath()));
                customDialogFragment.show(getSupportFragmentManager(),"mdf");
            }
        }catch (Exception e){
            e.printStackTrace();
            LoadingDialog.close();
        }
    }

    /**
     * 光子转账方法       Photon transfer method
     * @param amount     转账金额                 Transfer amount
     * @param showDialog 是否显示提示             whether to display prompts
     * @param filePath   路径                     path
     * @param isDirect   是否是直接转账           is direct transfer
     *
     * */
    private void photonTransfer(String amount,boolean showDialog,boolean isDirect,String filePath){
        mPresenter.photonTransferMethod(photonTokenAddress,amount,toAddress,showDialog,isDirect,filePath);
    }


    /**
     * 获取photon 版本号api调用成功
     * Get photon version number api call succeeded
     * */
    @Override
    public void getVersionCodeSuccess(String version) {
        if (!TextUtils.isEmpty(version) && photonVersionCode != null){
            if (version.startsWith("v")){
                photonVersionCode.setText(version);
            }else{
                photonVersionCode.setText(getString(R.string.photon_version,version));
            }
        }
    }


    /**
     * 设置通道地址备注  Set channel address notes
     * @param address 通道地址                             channel address
     * @param onSpinnerSelect 是否检测显示spinner          Whether to detect the display of spinner
     * */
    private void setChannelNote(String tokenAddress,String address,boolean onSpinnerSelect){
        String channelNote = "";
        if (!TextUtils.isEmpty(address)){
            channelNote = ChannelNoteUtils.getChannelNote(tokenAddress,address);
        }
        toAddress = address;
        if (TextUtils.isEmpty(channelNote)){
            if (showNoteBody != null){
                showNoteBody.setVisibility(View.GONE);
            }
            if (mShowText != null){
                mShowText.setText(address);
            }
        }else{
            if (showNoteBody != null){
                showNoteBody.setVisibility(View.VISIBLE);
                showTextAddress.setText(address);
            }
            if (mShowText != null){
                mShowText.setText(channelNote);
            }
        }
        checkCurrentAmount();
        if (onSpinnerSelect && !notShowSpinner){
            new Handler().postDelayed(() -> {
                if (mSpinner != null){
                    mSpinner.performClick();
                }
            },100);
        }
    }

    /**
     * 加载显示的币种  Load the displayed currency
     * 也就两个 SMT  MESH
     * */
    private void loadSpinnerData() {
        List<String> dataSet = new LinkedList<>(Collections.singletonList(getString(R.string.smt)));
        customSpinner.setLeftTextView(getString(R.string.token));
        customSpinner.attachDataSource(dataSet);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        photonTokenAddress = PhotonUtils.getPhotonTokenAddress(item);
        mPresenter.loadChannelList(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null){
            unregisterReceiver(receiver);
        }
        EventBus.getDefault().unregister(this);
        /**
         * 关闭光子
         * stop photon
         * */
        PhotonNetUtil.getInstance().stopPhoton();
    }

    /**
     * code    PHOTON_EVENT_CHANNEL_TRANSFER_SUCCESS            转账api调用成功             Transfer api call succeeded
     * code    PHOTON_EVENT_CHANNEL_TRANSFER_ERROR              转账api调用失败             Transfer api call failed
     * code    PHOTON_EVENT_CHANNEL_FIND_PATH_ERROR             查询路由api调用失败          Query route api call failed
     * code    PHOTON_EVENT_CHANNEL_TRANSFER_START              转账loading页面             Transfer loading page
     * other   查询路由信息成功                                                              Query routing information successfully
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(Object event) {
        try {
            if (event != null){
                if (event instanceof  MessageEvent){
                    MessageEvent messageEvent = (MessageEvent) event;
                    switch (messageEvent.getCode()){
                        case RequestCodeUtils.PHOTON_EVENT_CHANNEL_TRANSFER_SUCCESS:
                            inquiryTransferStatus(messageEvent.getMessage(),messageEvent.isBooleanType(),messageEvent.getAmount());
                            break;
                        case RequestCodeUtils.PHOTON_EVENT_CHANNEL_TRANSFER_ERROR:
                            if (messageEvent.isBooleanType()){
                                mPresenter.getFeeFindPath(photonTokenAddress,messageEvent.getAmount(),toAddress);
                            }else{
                                LoadingDialog.close();
                                if (PhotonApplication.api == null){
                                    PhotonApplication.photonStatus = false;
                                    showToast(getString(R.string.photon_restart));
                                }else{
                                    showToast(getString(R.string.photon_transfer_error));
                                }
                            }
                            break;
                        case RequestCodeUtils.PHOTON_EVENT_CHANNEL_FIND_PATH_ERROR:
                            LoadingDialog.close();
                            String errorMessage = messageEvent.getMessage();
                            if (!TextUtils.isEmpty(errorMessage)){
                                showToast(errorMessage);
                            }else{
                                if (PhotonApplication.api == null){
                                    PhotonApplication.photonStatus = false;
                                    showToast(getString(R.string.photon_restart));
                                }else{
                                    showToast(getString(R.string.photon_transfer_no_route));
                                }
                            }
                            break;
                        case RequestCodeUtils.PHOTON_EVENT_CHANNEL_TRANSFER_START:
                            LoadingDialog.show(PhotonTransferUI.this, getString(R.string.photon_trans_ing));
                            break;
                    }
                }else if (event instanceof PhotonFeeEntity){
                    PhotonFeeEntity feeEntity = (PhotonFeeEntity) event;
                    photonTransferDialog(feeEntity);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }
}
