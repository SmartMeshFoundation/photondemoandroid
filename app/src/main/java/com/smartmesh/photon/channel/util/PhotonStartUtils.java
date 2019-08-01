package com.smartmesh.photon.channel.util;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.channel.entity.PhotonStatusType;
import com.smartmesh.photon.channel.entity.PhotonStatusVo;
import com.smartmesh.photon.util.ContractUtils;
import com.smartmesh.photon.util.MyToast;
import com.smartmesh.photon.util.SDCardCtrl;
import com.smartmesh.photon.util.ThreadPoolUtils;
import com.smartmesh.photon.util.Utils;
import com.smartmesh.photon.wallet.util.WalletInfoUtils;

import org.json.JSONObject;

import java.io.File;

import mobile.Mobile;
import mobile.NotifyHandler;
import mobile.Strings;

/**
 * Photon All api are called asynchronously, except for the balance
 * Returning success does not mean actual success, just sending successfully
 * 返回成功并不代表实际成功，仅仅是发送成功
 * */
public class PhotonStartUtils {

    private static volatile PhotonStartUtils S_INST;

    public static PhotonStartUtils getInstance() {
        if (S_INST == null) {
            synchronized (PhotonStartUtils.class) {
                if (S_INST == null) {
                    S_INST = new PhotonStartUtils();
                }
            }
        }
        return S_INST;
    }

    /**
     * Start photon network
     * @param password 钱包密码  wallet password
     * @param ethRPCEndPoint   Public chain node host, http protocol
     * */
    public void startPhotonServer(String password,String ethRPCEndPoint) {
        try {
            if (PhotonApplication.photonStatus && PhotonApplication.api == null ){
                return;
            }
            final String tempAddress = WalletInfoUtils.getInstance().getSelectAddress();
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                try {
                    if (PhotonApplication.api != null && PhotonApplication.photonStatus) {
                        if(PhotonApplication.mPhotonSubscribe !=null){
                            PhotonApplication.mPhotonSubscribe.unsubscribe();
                        }
                        PhotonApplication.api.stop();
                        PhotonApplication.api = null;
                        PhotonApplication.photonStatus = false;
                        PhotonApplication.photonStatusStatus = false;
                    }
                    String clientIP = PhotonNetUtil.getInstance().getCurWifiIp();
                    if (TextUtils.isEmpty(clientIP)){
                        clientIP = "0.0.0.0";
                    }
                    String fileName = "photon_log" + "(" + tempAddress + ")" + ".txt";
                    SDCardCtrl.checkPathExist();
                    File logFile = new File(SDCardCtrl.getPhotonErrorLogPath(), fileName);
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    PhotonApplication.mPhotonStatusVo = new PhotonStatusVo();
                    PhotonApplication.mPhotonStatusVo.setEthStatus(PhotonStatusType.Default);
                    PhotonApplication.mPhotonStatusVo.setXmppStatus(PhotonStatusType.Default);
                    Bundle bundle = new Bundle();
                    Utils.intentAction(PhotonApplication.mContext, PhotonUrl.ACTION_RAIDEN_CONNECTION_STATE, bundle);
                    PhotonApplication.photonStatus = true;
                    /**
                     * 参数: parameter
                     * address string– 光子节点所使用的账户地址         Account address used by the photon node
                     * keystorePath string – 账户私钥保存路径          Account private key save path
                     * ethRPCEndPoint string – 公链节点host,http协议   Public chain node host, http protocol
                     * dataDir string – Photon db路径  测试环境和正式环境数据不一致，所以路径也不一样，
                     * passwordfile string – 账户密码文件路径            Account password file path
                     * apiAddr string – http api 监听端口               Http api listening port
                     * listenAddr string – udp 监听端口                 Udp listening port
                     * ogFile string – 日志文件路径                     Log file path
                     * registryAddress string – TokenNetworkRegistry合约地址         Contract address
                     * otherArgs mobile.Strings – 其他参数,参考photon -h              Other parameters
                     * */
                    Strings otherArg= Mobile.newStrings(1);
                    String arg = "--xmpp";
//                    String arg = "--xmpp" : "--matrix";
                    otherArg.set(0,arg);
                    String dataDir = PhotonApplication.mContext.getFilesDir().getAbsolutePath() + SDCardCtrl.PHOTON_DATA;
                    PhotonApplication.api = Mobile.startUp(//start up photon
                            tempAddress,
                            PhotonApplication.mContext.getFilesDir().getAbsolutePath() + SDCardCtrl.WALLET_PATH,
                            TextUtils.isEmpty(ethRPCEndPoint) ? PhotonUrl.PHOTON_URL_WS : ethRPCEndPoint,
                            dataDir,
                            password,
                            "127.0.0.1:5001",
                            clientIP + ":40001",
                            logFile.getAbsolutePath(),
                            ContractUtils.CONTACT_PHOTON_ADDRESS,
                            otherArg);
                    PhotonApplication.mPhotonSubscribe = PhotonApplication.api.subscribe(new NotifyHandler() {

                        @Override
                        public void onError(long l, String s) {
                            PhotonApplication.photonStatus = false;
                            onStartPhotonError(true);
                        }

                        @Override
                        public void onNotify(long l, String s) {
                            onPhotonNotify(l,s);
                        }

                        @Override
                        public void onReceivedTransfer(String s) {
                            updateChannelBalance(s);
                        }

                        @Override
                        public void onStatusChange(String s) {
                            updateStatusChanged(s);
                        }
                    });
                } catch (Exception e) {
                    startUpError(e);
                }
            });
        } catch (Exception e) {
            startUpError(e);
        }
    }

    private void startUpError(Exception e){
        try {
            PhotonApplication.photonStatus = false;
            if (e != null && (!TextUtils.isEmpty(e.getMessage()) || !TextUtils.equals(e.getMessage(),"connection not established"))){
                JSONObject object = new JSONObject(e.getMessage());
                int errorCode = object.optInt("error_code",-1);
                if (errorCode == 3){
                    Intent intent = new Intent(PhotonUrl.ACTION_PHOTON_CONNECTION_STATE_ERROR);
                    intent.putExtra(PhotonIntentDataUtils.ERROR_CODE,errorCode);
                    Utils.intentAction(PhotonApplication.mContext,intent);
                }
                onStartPhotonError(true);
            }else{
                onStartPhotonError(false);
            }
        }catch (Exception ce){
            ce.printStackTrace();
            PhotonApplication.photonStatus = false;
            onStartPhotonError(true);
        }

    }

    /**
     * 光子网络发送的通知
     * level 0 光子发出的通道变化信息
     * level 1 光子发出的警告信息
     * level 2 光子发出的错误信息
     *
     *
     * Info 	InfoTypeString 	0 	简单字符串通知,格式不固定,已经弃用
     * Info 	InfoTypeSentTransferDetail 	1 	发起方发起的交易状态发生了变化,格式固定
     * Info 	InfoTypeChannelCallID 	2 	关于通道的操作有了结果,格式不固定,调用方根据CallID决定是什么操作
     * Info 	InfoTypeChannelStatus 	3 	通道状态发生了变化,包括balance,patner_balance,locked_amount,partner_locked_amount,state等
     * Info 	InfoTypeContractCallTXInfo 	4 	用户发起的Tx执行结果通知,格式相对固定
     * Info 	InfoTypeInconsistentDatabase 	5 	交易时,发现接收双方数据库不一致的情况.注意此消息只能作为参考,对方可能恶意造假
     * Error 	InfoTypeBalanceNotEnoughError 	6 	账户SMT不足,不能保证进行链上交易的底线,必须尽快充值
     * Error 	InfoTypeCooperateSettleRefused 	7 	后台执行合作关闭通道失败,对方拒绝
     * Error 	InfoTypeCooperateSettleFailed 	8 	后台执行合作关闭通道 Tx失败
     * Error 	InfoTypeWithdrawRefused 	9 	后台执行withdraw,对方拒绝
     * Error 	InfoTypeWithdrawFailed 	10 	后台执行withdraw,Tx失败
     * Info 	InfoTypeReceivedMediatedTransfer 	11 	接收方收到MediatedTransfer,不代表交易成功,只是代表收到消息,如果处理成功收到的交易,请使用OnReceivedTransfer
     * */
    private void onPhotonNotify(long level ,String message){
        try {
            if (level == 0){
                photonNotifyInfo(message);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 光子通知 通道状态变化信息
     * */
    private void photonNotifyInfo(String message){
        try {
            if (TextUtils.isEmpty(message)){
                return;
            }
            JSONObject object = new JSONObject(message);
            int messageType = object.optInt("type");
            if (messageType == 1){
                JSONObject messageObject = object.optJSONObject("message");
                if (messageObject != null){
                    int status = messageObject.optInt("status");
                    if (status == 5){
                        Intent intent = new Intent(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CHANNEL_TRANSFER_ERROR);
                        Utils.intentAction(PhotonApplication.mContext,intent);
                    }
                }
            }else if (messageType == 3){
                Intent intent = new Intent(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CHANNEL_INFO);
                Utils.intentAction(PhotonApplication.mContext,intent);
            }else if (messageType == 4){
                JSONObject messageObject = object.optJSONObject("message");
                if (messageObject != null){
                    String txType = messageObject.optString("type");
                    int settleTimeOut = 0;
                    if (!TextUtils.isEmpty(txType)){
                        String txString = messageObject.optString("tx_params");
                        String txStatus = messageObject.optString("tx_status");
                        if (!TextUtils.isEmpty(txString)){
                            JSONObject txObject = new JSONObject(txString);
                            try {
                                settleTimeOut = txObject.optInt("settle_timeout",0);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        Intent intent = new Intent(PhotonUrl.ACTION_PHOTON_NOTIFY_CALL_CONTRACT_INFO);
                        intent.putExtra("type",txType);
                        intent.putExtra("txStatus",txStatus);
                        intent.putExtra("settleTimeOut",settleTimeOut);
                        Utils.intentAction(PhotonApplication.mContext,intent);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 启动光子网络失败
     * @param needStopPhoton 是否需要停止光子
     * */
    private void onStartPhotonError(boolean needStopPhoton){
        if (needStopPhoton){
            PhotonNetUtil.getInstance().stopPhoton();
        }
        if (PhotonApplication.mPhotonStatusVo != null) {
            PhotonApplication.mPhotonStatusVo.setXmppStatus(PhotonStatusType.Closed);
            PhotonApplication.mPhotonStatusVo.setEthStatus(PhotonStatusType.Closed);
        }
        Bundle bundle = new Bundle();
        Utils.intentAction(PhotonApplication.mContext, PhotonUrl.ACTION_RAIDEN_CONNECTION_STATE, bundle);
    }

    /**
     * 更新光子连接状态
     * */
    private void updateStatusChanged(String s){
        try {
            JSONObject obj = new JSONObject(s);
            PhotonApplication.mPhotonStatusVo = new PhotonStatusVo().parse(obj);
            Bundle bundle = new Bundle();
            Utils.intentAction(PhotonApplication.mContext, PhotonUrl.ACTION_RAIDEN_CONNECTION_STATE, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新转账后金额
     * */
    private void updateChannelBalance(String jsonString){
        try {
            if (TextUtils.isEmpty(jsonString)){
                return;
            }
            JSONObject object = new JSONObject(jsonString);
            String channel_identifier = object.optString("channel_identifier");
            Intent intent = new Intent(PhotonUrl.ACTION_PHOTON_RECEIVER_TRANSFER);
            intent.putExtra("channel_identifier",channel_identifier);
            Utils.intentAction(PhotonApplication.mContext,intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void uploadPhotonLog(){
        try {
            if (PhotonApplication.api == null){
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_restart));
                return;
            }
            ThreadPoolUtils.getInstance().getCachedThreadPool().execute(() -> {
                try {
                    String result = PhotonApplication.api.debugUploadLogfile();
                    if (result != null){
                        JSONObject object = new JSONObject(result);
                        int errorCode = object.optInt("error_code");
                        Intent intent = new Intent(PhotonUrl.ACTION_PHOTON_RECEIVER_UPLOAD_LOG);
                        intent.putExtra("error_code",errorCode);
                        Utils.intentAction(PhotonApplication.mContext,intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
