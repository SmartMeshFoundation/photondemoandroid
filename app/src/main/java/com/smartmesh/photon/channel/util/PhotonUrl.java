package com.smartmesh.photon.channel.util;

/**
 * Created by H on 2018/5/11.
 * Description:
 */

public class PhotonUrl {

    /**
     * 公链节点host,http协议
     * */
    public static String PHOTON_URL_WS =  "http://transport01.smartmesh.cn:44444";

    /**
     * 光子部署的 SMT token
     * */
    public static String PHOTON_SMT_TOKEN_ADDRESS = "0x6601F810eaF2fa749EEa10533Fd4CC23B8C791dc";

    /**
     * Photon 超时时间
     * */
    public static int PHOTON_SETTLE_TIMEOUT = 40001;

    /**
     * 目前来看是光子网络启动状态的广播
     * */
    public static final String ACTION_RAIDEN_CONNECTION_STATE = "com.smartmesh.photon.connection.state";

    /**
     * 光子网络启动失败的广播
     * */
    public static final String ACTION_PHOTON_CONNECTION_STATE_ERROR = "com.smartmesh.photon.photon.connection.state_error";

    /**
     * 收到光子转账
     * */
    public static final String ACTION_PHOTON_RECEIVER_TRANSFER = "com.smartmesh.photon.photon_receiver_transfer";

    /**
     * 转出光子
     * */
    public static final String ACTION_PHOTON_SENT_TRANSFER = "com.smartmesh.photon.photon_sent_transfer";

    /**
     * 关闭通道，提现通知
     * */
    public static final String ACTION_PHOTON_NOTIFY_CALL_ID = "com.smartmesh.photon.photon_notify_call_id";

    /**
     * 通道状态变化
     * */
    public static final String ACTION_PHOTON_NOTIFY_CALL_CHANNEL_INFO = "com.smartmesh.photon.photon_notify_call_channel_info";

    /**
     * 合约调用变化
     * */
    public static final String ACTION_PHOTON_NOTIFY_CALL_CONTRACT_INFO = "com.smartmesh.photon.photon_notify_call_contract_info";


    /**
     * 转账成功
     * */
    public static final String ACTION_PHOTON_NOTIFY_CALL_CHANNEL_TRANSFER_SUCCESS = "com.smartmesh.photon.photon_notify_call_channel_transfer_success";

    /**
     * 转账失败
     * */
    public static final String ACTION_PHOTON_NOTIFY_CALL_CHANNEL_TRANSFER_ERROR = "com.smartmesh.photon.photon_notify_call_channel_transfer_error";

    /**
     * 上传光子日志
     * */
    public static final String ACTION_PHOTON_RECEIVER_UPLOAD_LOG = "com.smartmesh.photon.photon_receiver_upload_log";
    
    private static volatile PhotonUrl S_INST;
    
    public static PhotonUrl getInatance() {
        if (S_INST == null) {
            synchronized (PhotonUrl.class) {
                if (S_INST == null) {
                    S_INST = new PhotonUrl();
                }
            }
        }
        return S_INST;
    }

}
