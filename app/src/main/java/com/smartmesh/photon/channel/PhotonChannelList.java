package com.smartmesh.photon.channel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.base.BaseActivity;
import com.smartmesh.photon.channel.adapter.PhotonChannelListAdapter;
import com.smartmesh.photon.channel.contract.PhotonChannelListContract;
import com.smartmesh.photon.channel.entity.PhotonChannelVo;
import com.smartmesh.photon.channel.entity.PhotonStatusType;
import com.smartmesh.photon.channel.entity.TxStatus;
import com.smartmesh.photon.channel.entity.TxTypeStr;
import com.smartmesh.photon.channel.presenter.PhotonChannelListPresenterImpl;
import com.smartmesh.photon.channel.util.ChannelNoteUtils;
import com.smartmesh.photon.channel.util.PhotonConnectStatus;
import com.smartmesh.photon.channel.util.PhotonErrorUtils;
import com.smartmesh.photon.channel.util.PhotonIntentDataUtils;
import com.smartmesh.photon.channel.util.PhotonStartUtils;
import com.smartmesh.photon.channel.util.PhotonUrl;
import com.smartmesh.photon.channel.util.PhotonUtils;
import com.smartmesh.photon.dialog.CustomDialogFragment;
import com.smartmesh.photon.eventbus.MessageEvent;
import com.smartmesh.photon.eventbus.RequestCodeUtils;
import com.smartmesh.photon.ui.AlertActivity;
import com.smartmesh.photon.util.LoadingDialog;
import com.smartmesh.photon.util.MySharedPrefs;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.util.SDCardCtrl;
import com.smartmesh.photon.wallet.util.WalletInfoUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.utils.Convert;
import org.yczbj.ycrefreshviewlib.inter.InterItemView;
import org.yczbj.ycrefreshviewlib.view.YCRefreshView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created on 2018/1/24.
 * photon channel list ui
 * 光子通道列表页面
 * {@link PhotonTransferUI}
 */

public class PhotonChannelList extends BaseActivity<PhotonChannelListContract.Presenter> implements PhotonChannelListContract.View , SwipeRefreshLayout.OnRefreshListener, ChangeChannelStateListener{

    private static int PHOTON_CHANNEL_CREATE = 100;

    @BindView(R.id.empty_text)
    TextView emptyTextView;
    @BindView(R.id.empty_like_rela)
    RelativeLayout emptyRela;

    @BindView(R.id.recyclerView)
    YCRefreshView refreshView;

    private PhotonChannelListAdapter mAdapter = null;
    private List<PhotonChannelVo> source = new ArrayList<>();
    ;
    private int type;//0 通道列表页面 1 转账页面 2 启动光子页面

    private Timer mTimer;

    private Unbinder mUnBinder;
    private HeaderViewHolder mHeaderHolder;

    private int clickNumber = 0;

    private String photonBalanceOnChain = "0";

    private boolean isLoadingData = false;


    @Override
    public int getLayoutId() {
        getPassData();
        return R.layout.photon_channel_list;
    }

    @Override
    public PhotonChannelListContract.Presenter createPresenter() {
        return new PhotonChannelListPresenterImpl(this);
    }

    private void getPassData() {
        type = getIntent().getIntExtra("type", -1);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && (PhotonUrl.ACTION_RAIDEN_CONNECTION_STATE.equals(intent.getAction()))) {
                mPresenter.setPhotonStatus(mHeaderHolder.photonState, PhotonChannelList.this);
            } else if (intent != null && (PhotonUrl.ACTION_PHOTON_RECEIVER_TRANSFER.equals(intent.getAction()))) {
                if (!isLoadingData) {
                    isLoadingData = true;
                    mPresenter.loadChannelList(true);
                    mPresenter.getBalanceFromPhoton();
                }
            } else if (intent != null && (PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CHANNEL_INFO.equals(intent.getAction()))) {
                if (!isLoadingData) {
                    isLoadingData = true;
                    mPresenter.loadChannelList(true);
                    mPresenter.getBalanceFromPhoton();
                }
            } else if (intent != null && (PhotonUrl.ACTION_PHOTON_RECEIVER_UPLOAD_LOG.equals(intent.getAction()))) {
                int errorCode = intent.getIntExtra("error_code", -1);
                if (errorCode == 0) {
                    MyToast.showNewToast(PhotonChannelList.this, getString(R.string.photon_upload_log_success));
                } else {
                    showUploadLogErrorDialog();
                }
            } else if (intent != null && (PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CONTRACT_INFO.equals(intent.getAction()))) {
                notifyInfoMethod(intent);
            }
        }
    };

    /**
     * 光子通知消息
     */
    private void notifyInfoMethod(Intent intent) {
        String txType = intent.getStringExtra("type");
        String txStatus = intent.getStringExtra("txStatus");
        int settleTimeOut = intent.getIntExtra("settleTimeOut", 0);
        if (!TextUtils.isEmpty(txType)) {
            new Handler().postDelayed(() -> {
                if (TextUtils.equals(txType, TxTypeStr.ChannelDeposit.name())) {
                    if (settleTimeOut > 0) {
                        if (!TextUtils.isEmpty(txStatus) && TextUtils.equals(TxStatus.failed.name(), txStatus)) {
                            showToast(getString(R.string.photon_tx_create_2));
                        } else {
                            showToast(getString(R.string.photon_tx_create_1));
                        }
                    } else {
                        if (!TextUtils.isEmpty(txStatus) && TextUtils.equals(TxStatus.failed.name(), txStatus)) {
                            showToast(getString(R.string.photon_tx_deposit_2));
                        } else {
                            showToast(getString(R.string.photon_tx_deposit_1));
                        }
                    }
                } else if (TextUtils.equals(txType, TxTypeStr.Withdraw.name())) {
                    if (!TextUtils.isEmpty(txStatus) && TextUtils.equals(TxStatus.failed.name(), txStatus)) {
                        showToast(getString(R.string.photon_tx_withdraw_2));
                    } else {
                        showToast(getString(R.string.photon_tx_withdraw_1));
                    }
                } else if (TextUtils.equals(txType, TxTypeStr.ChannelSettle.name()) || TextUtils.equals(txType, TxTypeStr.CooperateSettle.name())) {
                    showToast(getString(R.string.photon_tx_close_1));
                }
            }, 500);
        }
        if (mPresenter != null) {
            mPresenter.getBalanceFromPhoton();
            if (!isLoadingData) {
                isLoadingData = true;
                mPresenter.loadChannelList(true);
            }
        }
    }

    @Override
    protected void initData() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(PhotonUrl.ACTION_RAIDEN_CONNECTION_STATE);
            filter.addAction(PhotonUrl.ACTION_PHOTON_RECEIVER_TRANSFER);
            filter.addAction(PhotonUrl.ACTION_PHOTON_RECEIVER_UPLOAD_LOG);
            filter.addAction(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_ID);
            filter.addAction(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CHANNEL_INFO);
            filter.addAction(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CONTRACT_INFO);
            registerReceiver(receiver, filter);
            EventBus.getDefault().register(this);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            refreshView.setLayoutManager(linearLayoutManager);
            refreshView.setRefreshListener(this);
            refreshView.setRefreshingColorResources(R.color.colorPrimary);
            mAdapter = new PhotonChannelListAdapter(this, this);
            refreshView.setAdapter(mAdapter);
            initHeader();
            mPresenter.getBalanceFromPhoton();
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(mTimerTask, 0, 14000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加头部内容
     */
    private void initHeader() {
        mAdapter.removeAllHeader();
        mAdapter.addHeader(new InterItemView() {
            @Override
            public View onCreateView(ViewGroup parent) {
                return LayoutInflater.from(PhotonChannelList.this).inflate(R.layout.photon_channel_list_header, null);
            }

            @Override
            public void onBindView(View headerView) {
                mHeaderHolder = new HeaderViewHolder();
                mUnBinder = ButterKnife.bind(mHeaderHolder, headerView);
                mPresenter.setPhotonStatus(mHeaderHolder.photonState, PhotonChannelList.this);
                showTempBalance();
            }
        });
    }


    @OnClick({R.id.photon_trans_query, R.id.photon_wallet_address, R.id.photon_create
            , R.id.photon_pay, R.id.app_upload_photon_log})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.photon_pay:
                if (type == 1) {
                    finish();
                } else {
                    Intent photonIntent = new Intent(PhotonChannelList.this, PhotonTransferUI.class);
                    startActivity(photonIntent);
                }
                break;
            case R.id.photon_create:
                Intent photonCreate = new Intent(this, PhotonCreateChannel.class);
                photonCreate.putExtra("fromType", 1);
                startActivityForResult(photonCreate, PHOTON_CHANNEL_CREATE);
                break;
            case R.id.photon_trans_query:
                Intent intent = new Intent(this, PhotonTransferQueryUI.class);
                intent.putExtra("fromType", 1);
                startActivity(intent);
                break;
            case R.id.photon_wallet_address:
                showWalletAddress();
                break;
            case R.id.app_upload_photon_log:
                CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
                customDialogFragment.setTitle(getString(R.string.photon_upload_log_title));
                customDialogFragment.setContent(getString(R.string.photon_upload_log_content));
                customDialogFragment.setSubmitListener(() -> {
                    showToast(getString(R.string.photon_upload_log_ing));
                    PhotonStartUtils.getInstance().uploadPhotonLog();
                });
                customDialogFragment.show(getSupportFragmentManager(), "mdf");
                break;
        }
    }

    /**
     * 显示地址弹框
     */
    private void showWalletAddress() {
        String selectAddress = WalletInfoUtils.getInstance().getSelectAddress();
        Intent intentAlert = new Intent(this, AlertActivity.class);
        intentAlert.putExtra("type", 9);
        intentAlert.putExtra(PhotonIntentDataUtils.WALLET_ADDRESS, selectAddress);
        startActivity(intentAlert);
        overridePendingTransition(0, 0);
    }

    private void showUploadLogErrorDialog() {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        customDialogFragment.setTitle(getString(R.string.photon_upload_log_error));
        customDialogFragment.setContent(getString(R.string.photon_upload_log_error_content, SDCardCtrl.getPhotonErrorLogPath()));
        customDialogFragment.show(getSupportFragmentManager(), "mdf");
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            if (!isLoadingData) {
                isLoadingData = true;
                mPresenter.loadChannelList(true);
            }
            mPresenter.getBalanceFromPhoton();
        }, 500);
    }

    public TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (!isLoadingData) {
                isLoadingData = true;
                mPresenter.loadChannelList(true);
            }
            mPresenter.getBalanceFromPhoton();
        }
    };

    @Override
    public void onRefresh() {
        if (!isLoadingData) {
            isLoadingData = true;
            mPresenter.loadChannelList(true);
        }
        mPresenter.getBalanceFromPhoton();
    }


    /**
     * parse json
     * 更新通道列表
     *
     * @param jsonString response string
     */
    private void parseJson(String jsonString) {
        try {
            if (TextUtils.isEmpty(jsonString) || "null".equals(jsonString)) {
                source.clear();
                if (mAdapter != null) {
                    mAdapter.clear();
                    mAdapter.addAll(source);
                }
                return;
            }
            try {
                source.clear();
                JSONObject object = new JSONObject(jsonString);
                int errorCode = object.optInt("error_code");
                if (errorCode == 0) {
                    JSONArray array = object.optJSONArray("data");
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject dataObject = array.optJSONObject(i);
                            PhotonChannelVo channelVo = new PhotonChannelVo().parse(dataObject);
                            source.add(channelVo);
                        }
                    }
                }
                if (mAdapter != null) {
                    mAdapter.clear();
                    mAdapter.addAll(source);
                }
                checkListEmpty();
            } catch (Exception e) {
                e.printStackTrace();
                if (mAdapter != null) {
                    mAdapter.clear();
                    mAdapter.addAll(source);
                }
                checkListEmpty();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To test whether the current list is empty
     */
    private void checkListEmpty() {
        if (source == null || source.size() == 0) {
            if (emptyRela != null) {
                emptyRela.setVisibility(View.VISIBLE);
            }
            if (emptyTextView != null) {
                emptyTextView.setText(R.string.photon_list_empty);
            }
        } else {
            if (emptyRela != null) {
                emptyRela.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void changeChannel(final int position, final boolean isForced) {
        closeChannelMethod(position, isForced);
    }

    /**
     * 关闭channel方法
     *
     * @param position 通道index
     * @param isForced 是否是强制关闭
     */
    private void closeChannelMethod(final int position, final boolean isForced) {
        if (source.size() <= 0) {
            return;
        }
        String title = isForced ? getString(R.string.photon_channel_forced_close_hint) : getString(R.string.photon_channel_close_hint);
        String content = isForced ? getString(R.string.photon_channel_forced_close_content) : getString(R.string.photon_channel_close_content);
        CustomDialogFragment mdf = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        mdf.setTitle(title);
        mdf.setContent(content);
        mdf.setSubmitListener(() -> channelCloseMethod(position, isForced));
        mdf.show(getSupportFragmentManager(), "mdf");
    }

    /**
     * 关闭channel方法
     *
     * @param position   通道index
     * @param isWithdraw 是否是提现，提现失败强制关闭通道 和 关闭通道失败强制关闭通道不一样
     *                   普通关闭通道出错时候弹出
     */
    private void closeChannelMethodOnError(int errorCode, String errorMessage, int position, boolean isWithdraw) {
        if (source == null || source.size() <= 0) {
            return;
        }
        String content1 = isWithdraw ? getString(R.string.photon_channel_forced_close_hint_2) : getString(R.string.photon_channel_forced_close_hint_1);
        String content2 = getString(R.string.photon_channel_forced_close_content);
        String errorInfo = PhotonErrorUtils.handlerPhotonErrorString(PhotonChannelList.this, errorCode, errorMessage);
        String content3 = "";
        if (!TextUtils.isEmpty(errorInfo)) {
            content3 = getString(R.string.photon_error_info, errorInfo);
        }
        CustomDialogFragment mdf = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        mdf.setContent(content1);
        mdf.setContent2(content2);
        mdf.setContent3(content3);
        mdf.setSubmitListener(() -> channelCloseMethod(position, true));
        mdf.show(getSupportFragmentManager(), "mdf");
    }

    /**
     * 存款
     */
    @Override
    public void depositChannel(int position) {
        if (source == null || source.size() <= 0) {
            return;
        }
        Intent intent = new Intent(PhotonChannelList.this, PhotonChannelDepositUI.class);
        intent.putExtra(PhotonIntentDataUtils.PHOTON_PARTNER_ADDRESS, source.get(position).getPartnerAddress());
        intent.putExtra(PhotonIntentDataUtils.PHOTON_TOKEN_ADDRESS, source.get(position).getTokenAddress());
        startActivityForResult(intent, PHOTON_CHANNEL_CREATE);
    }

    /**
     * 提现提示框
     */
    @Override
    public void withdrawChannel(int position) {
        if (source == null || source.size() <= 0) {
            return;
        }
        String tokenSymbol = PhotonUtils.getPhotonTokenSymbol(source.get(position).getTokenAddress());
        CustomDialogFragment mdf = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        mdf.setTitle(getString(R.string.photon_channel_withdraw_hint));
        mdf.setContent(getString(R.string.photon_channel_withdraw_content, source.get(position).getBalance(), tokenSymbol));
        mdf.setSubmitListener(() -> channelWithdrawMethod(position));
        mdf.show(getSupportFragmentManager(), "mdf");
    }

    /**
     * 结算提示框
     */
    @Override
    public void settleChannel(int position) {
        if (source == null || source.size() <= 0) {
            return;
        }
        CustomDialogFragment mdf = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        mdf.setTitle(getString(R.string.photon_channel_forced_settle_hint));
        mdf.setContent(getString(R.string.photon_channel_settle_content));
        mdf.setSubmitListener(() -> settleChannelMethod(position));
        mdf.show(getSupportFragmentManager(), "mdf");
    }

    /**
     * pms提示相关 提示            pms prompt related tips
     *
     * @param delegateState list position
     *                      pms   表示委托状态的数字          pms indicates the number of delegate status
     *                      0 不需要委托                     0 no commission required
     *                      1.正在等待委托到pms              1. Waiting for delegate to pms
     *                      2.委托成功                       2. Successful commission
     *                      3.委托失败                       3. Delegate failed
     *                      4.委托失败并无有效公链           4. Failure of the commission does not have an effective public chain
     */
    @Override
    public void pmsChannel(int delegateState) {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment(CustomDialogFragment.CUSTOM_DIALOG);
        customDialogFragment.setTitle(getString(R.string.dialog_prompt));
        customDialogFragment.setContent(delegateState == 1 ? getString(R.string.photon_pms_1) : getString(R.string.photon_pms_2));
        customDialogFragment.show(getSupportFragmentManager(), "mdf");
    }

    /**
     * 隐藏底部 暂时不用
     */
    @Override
    public void hiddenBottom(int position) {
        if (source != null && source.size() > position) {
            boolean isHiddenBottom = source.get(position).isHidden();
            source.get(position).setHidden(!isHiddenBottom);
            mAdapter.clear();
            mAdapter.addAll(source);
        }
    }

    /**
     * 结算方法
     * Settlement method
     */
    private void settleChannelMethod(int position) {

        if (offLineOrSourceIsEmpty(position)) {
            return;
        }

        PhotonChannelVo channelVoClose = source.get(position);
        LoadingDialog.show(this, "");
        try {
            if (PhotonApplication.api != null) {
                mPresenter.photonSettleChannel(position, channelVoClose.getChannelIdentifier());
            } else {
                LoadingDialog.close();
            }
        } catch (Exception e) {
            showToast(e.getMessage());
            LoadingDialog.close();
        }
    }

    /**
     * 取钱
     * channelIdentifierHashStr 通道地址
     * amountstr 取钱的金额
     * op 选项
     * preparewithdraw 当你准备withdraw的时候，可以把通道转态切换到'prepareForWithdraw'状态，此时通道不再发起或接受任何交易
     * cancelprepare 取消withdraw,把通道转态从prepareForWithdraw 切回到opened
     * 当然，当amount大于0的时候，op参数是没有意义的，会直接取钱。
     *
     * withdraw money
     * channelIdentifierHashStr channel address
     * amountstr The amount of money withdrawn
     * op option
     * preparewithdraw When you are ready to withdraw, you can switch the channel to the 'prepareForWithdraw' state, at which point the channel no longer initiates or accepts any transactions.
     * cancelprepare cancels withdraw, cuts the channel from prepareForWithdraw back to opened
     * Of course, when amount is greater than 0, the op parameter is meaningless and will take money directly.
     */
    private void channelWithdrawMethod(int position) {

        if (offLineOrSourceIsEmpty(position)) {
            return;
        }

        try {
            if (!TextUtils.isEmpty(photonBalanceOnChain)) {
                BigDecimal bigDecimal = new BigDecimal(photonBalanceOnChain);
                if (bigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                    showToast(getString(R.string.photon_error_2000));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PhotonChannelVo withdrawChannel = source.get(position);
        if (PhotonConnectStatus.StateOpened == withdrawChannel.getState()) {
            LoadingDialog.show(this, "");
            try {
                if (PhotonApplication.api != null) {
                    BigDecimal bigDecimal = new BigDecimal(source.get(position).getBalance()).multiply(Convert.Unit.ETHER.getWeiFactor());
                    if (bigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                        showToast(getString(R.string.photon_channel_withdraw_zero));
                        LoadingDialog.close();
                        return;
                    }
                    String balance = bigDecimal.stripTrailingZeros().toPlainString();
                    mPresenter.photonWithDraw(position, withdrawChannel.getChannelIdentifier(), balance, "");
                } else {
                    LoadingDialog.close();
                }
            } catch (Exception e) {
                showToast(e.getMessage());
                LoadingDialog.close();
            }
        }
    }

    /**
     * close channel
     * channelIdentifierHashStr 通道地址
     * force 为false,则会寻求和对方协商关闭通道,在协商一致的情况下可以立即(等待一两个块的时间)将Token返回到双方账户;
     * force 为true,则不会与对方协商,意味着会强制关闭通道,等待settleTimeout结算窗口期,然后才可以进行SettleChannel,最终Token才会返回双方的账户
     * close channel
     * channelIdentifierHashStr channel address
     * force is false, it will seek to close the channel with the other party. In the case of consensus,
     * you can immediately return the Token to both accounts if you wait for one or two blocks.
     * force is true, it will not negotiate with the other party, which means that the channel will be forcibly closed, waiting for the settleTimeout settlement window period,
     * and then the SettleChannel can be performed, and finally the Token will return the accounts of both parties.
     */
    private void channelCloseMethod(int position, boolean isForced) {

        if (offLineOrSourceIsEmpty(position)) {
            return;
        }

        try {
            if (!TextUtils.isEmpty(photonBalanceOnChain)) {
                BigDecimal bigDecimal = new BigDecimal(photonBalanceOnChain);
                if (bigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                    showToast(getString(R.string.photon_error_2000));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        PhotonChannelVo channelVoClose = source.get(position);
        LoadingDialog.show(this, "");
        try {
            if (PhotonApplication.api != null) {
                mPresenter.photonCloseChannel(position, channelVoClose.getChannelIdentifier(), isForced);
            } else {
                LoadingDialog.close();
            }
        } catch (Exception e) {
            showToast(e.getMessage());
            LoadingDialog.close();
        }
    }

    /**
     * 监测通道是否可以进行 关闭 提现操作
     * 无网状态不允许转账
     * Whether the monitoring channel can be closed, cash withdrawal operation
     * No network status is not allowed to transfer money
     */
    private boolean offLineOrSourceIsEmpty(int position) {
        if (PhotonApplication.mPhotonStatusVo != null) {
            if (PhotonStatusType.Connected != PhotonApplication.mPhotonStatusVo.getEthStatus()) {
                showToast(getString(R.string.photon_channel_mesh_pay_4));
                return true;
            }
        }

        if (source == null || source.size() <= 0 || position < 0 || position >= source.size()) {
            showToast(getString(R.string.error));
            return true;
        }
        return false;
    }

    /**
     * 合约交易列表
     * goto contract transaction list page
     */
    private void intoContractQueryUI() {
        Intent intent = new Intent(this, PhotonTransferQueryUI.class);
        intent.putExtra("showContract", true);
        intent.putExtra("fromType", 1);
        startActivity(intent);
    }

    /**
     * 更新通道列表状态
     * Update channel list status
     * @param position   更新的项目  Updated item
     * @param jsonString 具体数据    specific data
     * @param needForced 当接口调用失败时候 是否需要强制关闭通道
     *                   Whether to forcefully close the channel when the interface call fails
     * @param isWithdraw 是否是提现，提现失败强制关闭通道 和 关闭通道失败强制关闭通道不一样
     *                   Whether it is cash withdrawal, withdrawal failure forced to close the channel and closing the channel failed to force the channel to be closed is not the same
     */
    private void updateChannelList(int position, String jsonString, boolean needForced, boolean isWithdraw) {
        try {
            LoadingDialog.close();
            JSONObject object = new JSONObject(jsonString);
            int errorCode = object.optInt("error_code");
            if (errorCode == 0) {
                intoContractQueryUI();
                JSONObject dataObject = object.optJSONObject("data");
                int state = dataObject.optInt("state");
                source.get(position).setState(state);
                if (mAdapter != null) {
                    mAdapter.clear();
                    mAdapter.addAll(source);
                }
            } else if (errorCode == 2000) {
                showToast(getString(R.string.photon_error_2000));
            } else {
                String errorMessage = object.optString("error_message");
                if (needForced) {
                    closeChannelMethodOnError(errorCode, errorMessage, position, isWithdraw);
                } else {
                    PhotonErrorUtils.handlerPhotonError(errorCode, errorMessage);
                }
            }
        } catch (Exception e) {
            LoadingDialog.close();
            showToast(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PHOTON_CHANNEL_CREATE) {
            if (!isLoadingData) {
                isLoadingData = true;
                mPresenter.loadChannelList(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        unregisterReceiver(receiver);
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取通道列表成功
     * Get the channel list successfully
     */
    @Override
    public void loadChannelSuccess(String jsonString) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_LIST_SUCCESS);
        messageEvent.setMessage(jsonString);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 获取通道列表失败
     * Failed to get channel list
     */
    @Override
    public void loadChannelError(boolean showToast) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHANNEL_LIST_ERROR);
        if (showToast) {
            if (PhotonApplication.api == null) {
                messageEvent.setMessage(getString(R.string.photon_restart));
            } else {
                messageEvent.setMessage(getString(R.string.error_get_photon_list));
            }
        }
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 检测钱包密码成功
     * Check the wallet password successfully
     */
    @Override
    public void checkWalletExistSuccess(String walletPwd) {
        MessageEvent event = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHECK_WALLET);
        event.setMessage(walletPwd);
        EventBus.getDefault().post(event);
    }

    /**
     * 检测钱包密码失败
     * Failed to detect wallet password
     */
    @Override
    public void checkWalletExistError() {
        MessageEvent event = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CHECK_WALLET_ERROR);
        EventBus.getDefault().post(event);
    }

    /**
     * 获取链上余额  通道内总余额成功
     * Get the balance on the chain. The total balance in the channel is successful.
     */
    @Override
    public void getPhotonBalanceFromApiSuccess(String jsonString) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_BALANCE_SUCCESS);
        messageEvent.setMessage(jsonString);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 提现api调用成功
     * The api call succeeded
     */
    @Override
    public void photonWithdrawSuccess(int position, String jsonString) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_WITHDRAW_SUCCESS);
        messageEvent.setMessage(jsonString);
        messageEvent.setPosition(position);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 结算api调用成功
     * Settle api call succeeded
     */
    @Override
    public void photonSettleSuccess(int position, String jsonString) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_SETTLE_SUCCESS);
        messageEvent.setMessage(jsonString);
        messageEvent.setPosition(position);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * 关闭api调用成功
     * Close api call succeeded
     */
    @Override
    public void photonCloseChannelSuccess(int position, String jsonString, boolean isForced) {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_CLOSE_SUCCESS);
        messageEvent.setMessage(jsonString);
        messageEvent.setPosition(position);
        messageEvent.setBooleanType(isForced);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * api调用失败
     * Api call failed
     */
    @Override
    public void photonError() {
        MessageEvent messageEvent = new MessageEvent(RequestCodeUtils.PHOTON_EVENT_ERROR);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void onResult(Object result, String message) {

    }

    @Override
    public void onError(Throwable throwable, String message) {

    }

    /**
     * 头部相关
     */
    class HeaderViewHolder {
        @BindView(R.id.smt_balance)
        TextView walletSmtBalance;
        @BindView(R.id.photon_smt_balance)
        TextView photonSmtBalance;
        @BindView(R.id.photon_state)
        TextView photonState;
    }

    /**
     * 解析通道余额
     * parse channel balance
     * @param jsonString 返回信息
     */
    private void parseOnChainBalance(String jsonString) {
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                JSONObject jsoObject = new JSONObject(jsonString);
                int errorCode = jsoObject.optInt("error_code");
                if (errorCode == 0) {
                    JSONArray array = jsoObject.optJSONArray("data");
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            String tokenAddress = array.optJSONObject(i).optString("token_address");
                            String balanceInPhoton = array.optJSONObject(i).optString("balance_in_photon");
                            photonBalanceOnChain = array.optJSONObject(i).optString("balance_on_chain");
                            if (PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS.equalsIgnoreCase(tokenAddress)) {
                                if (!TextUtils.isEmpty(balanceInPhoton)) {
                                    String photonBalance = new BigDecimal(balanceInPhoton)
                                            .divide(Convert.Unit.ETHER.getWeiFactor(), 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                                    MySharedPrefs.write(PhotonChannelList.this, MySharedPrefs.FILE_USER, MySharedPrefs.KEY_PHOTON_IN_PHOTON_BALANCE, photonBalance);
                                    if (mHeaderHolder.photonSmtBalance != null) {
                                        mHeaderHolder.photonSmtBalance.setText(photonBalance);
                                    }
                                }

                                if (!TextUtils.isEmpty(photonBalanceOnChain)) {
                                    String chainBalance = new BigDecimal(photonBalanceOnChain)
                                            .divide(Convert.Unit.ETHER.getWeiFactor(), 4, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                                    MySharedPrefs.write(PhotonChannelList.this, MySharedPrefs.FILE_USER, MySharedPrefs.KEY_PHOTON_ON_CHAIN_BALANCE, chainBalance);
                                    if (mHeaderHolder.walletSmtBalance != null) {
                                        mHeaderHolder.walletSmtBalance.setText(chainBalance);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    showTempBalance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示缓存余额
     */
    private void showTempBalance() {
        try {
            String photonBalance = MySharedPrefs.readString(PhotonChannelList.this, MySharedPrefs.FILE_USER, MySharedPrefs.KEY_PHOTON_IN_PHOTON_BALANCE);
            if (!TextUtils.isEmpty(photonBalance) && mHeaderHolder.photonSmtBalance != null) {
                mHeaderHolder.photonSmtBalance.setText(photonBalance);
            }

            if (!TextUtils.isEmpty(photonBalanceOnChain)) {
                String chainBalance = MySharedPrefs.readString(PhotonChannelList.this, MySharedPrefs.FILE_USER, MySharedPrefs.KEY_PHOTON_ON_CHAIN_BALANCE);
                if (!TextUtils.isEmpty(chainBalance) && mHeaderHolder.walletSmtBalance != null) {
                    mHeaderHolder.walletSmtBalance.setText(chainBalance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * code    PHOTON_EVENT_CHECK_WALLET            检测钱包密码成功
     * code    PHOTON_EVENT_CHECK_WALLET_ERROR      检测钱包密码失败
     * code    PHOTON_EVENT_ERROR                   api调用失败
     * code    PHOTON_EVENT_WITHDRAW_SUCCESS        提现api调用成功
     * code    PHOTON_EVENT_SETTLE_SUCCESS          结算api调用成功
     * code    PHOTON_EVENT_CLOSE_SUCCESS           关闭api调用成功
     * code    PHOTON_EVENT_BALANCE_SUCCESS         获取余额api调用成功
     * code    PHOTON_EVENT_CHANNEL_LIST_SUCCESS    获取通道列表api调用成功
     * code    PHOTON_EVENT_CHANNEL_LIST_ERROR      获取通道列表api调用失败
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEvent messageEvent) {
        try {
            if (messageEvent != null) {
                switch (messageEvent.getCode()) {
                    case RequestCodeUtils.PHOTON_EVENT_CHECK_WALLET:
                        LoadingDialog.close();
                        PhotonStartUtils.getInstance().startPhotonServer(messageEvent.getMessage(), "");
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_CHECK_WALLET_ERROR:
                        LoadingDialog.close();
                        showToast(getString(R.string.wallet_pwd_error));
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_ERROR:
                        LoadingDialog.close();
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_WITHDRAW_SUCCESS:
                        updateChannelList(messageEvent.getPosition(), messageEvent.getMessage(), true, true);
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_SETTLE_SUCCESS:
                        updateChannelList(messageEvent.getPosition(), messageEvent.getMessage(), false, false);
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_CLOSE_SUCCESS:
                        if (source != null && source.size() > messageEvent.getPosition()) {
                            PhotonChannelVo channelVo = source.get(messageEvent.getPosition());
                            ChannelNoteUtils.deleteChannelNote(channelVo.getTokenAddress(), channelVo.getPartnerAddress());
                        }
                        updateChannelList(messageEvent.getPosition(), messageEvent.getMessage(), !messageEvent.isBooleanType(), false);
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_BALANCE_SUCCESS:
                        parseOnChainBalance(messageEvent.getMessage());
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_CHANNEL_LIST_SUCCESS:
                        isLoadingData = false;
                        parseJson(messageEvent.getMessage());
                        break;
                    case RequestCodeUtils.PHOTON_EVENT_CHANNEL_LIST_ERROR:
                        isLoadingData = false;
                        if (!TextUtils.isEmpty(messageEvent.getMessage())) {
                            showToast(messageEvent.getMessage());
                        }
                        checkListEmpty();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
