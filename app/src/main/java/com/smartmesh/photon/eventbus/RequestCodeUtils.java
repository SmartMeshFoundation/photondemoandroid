package com.smartmesh.photon.eventbus;

/**
 * 请求code码
 * */
public class RequestCodeUtils {

    /**
     * 上网支付创建通道 code码
     * Internet payment creation channel code code
     * */
    public static final int PHOTON_CREATE_CODE = 10001;

    /**
     * 扫面二维码 从相册选择 code码
     * Scanning QR code Select the code code from the album
     * */
    public static final int PHOTON_CREATE_IMAGE_CODE = PHOTON_CREATE_CODE + 1;

    /**
     * 检测钱包回调
     * Detect wallet callback
     * */
    public static final int PHOTON_EVENT_CHECK_WALLET = PHOTON_CREATE_IMAGE_CODE + 1;

    /**
     * 检测钱包失败回调
     * Detecting wallet failure callback
     * */
    public static final int PHOTON_EVENT_CHECK_WALLET_ERROR = PHOTON_EVENT_CHECK_WALLET + 1;


    /**
     * 提现失败回调
     * Cash withdrawal callback
     * 结算失败回调
     * Settlement failure callback
     * */
    public static final int PHOTON_EVENT_ERROR = PHOTON_EVENT_CHECK_WALLET_ERROR + 1;

    /**
     * 提现成功回调
     * Cash withdrawal success
     * */
    public static final int PHOTON_EVENT_WITHDRAW_SUCCESS = PHOTON_EVENT_ERROR + 1;

    /**
     * 结算成功回调
     * Settlement success callback
     * */
    public static final int PHOTON_EVENT_SETTLE_SUCCESS = PHOTON_EVENT_WITHDRAW_SUCCESS + 1;

    /**
     * 关闭成功回调
     * Close successful callback
     * */
    public static final int PHOTON_EVENT_CLOSE_SUCCESS = PHOTON_EVENT_SETTLE_SUCCESS + 1;

    /**
     * 获取余额成功回调
     * Get a successful balance callback
     * */
    public static final int PHOTON_EVENT_BALANCE_SUCCESS = PHOTON_EVENT_CLOSE_SUCCESS + 1;

    /**
     * 获取通道列表成功
     * Get the channel list successfully
     * */
    public static final int PHOTON_EVENT_CHANNEL_LIST_SUCCESS = PHOTON_EVENT_BALANCE_SUCCESS + 1;

    /**
     * 获取通道列表失败
     * Failed to get channel list
     * */
    public static final int PHOTON_EVENT_CHANNEL_LIST_ERROR = PHOTON_EVENT_CHANNEL_LIST_SUCCESS + 1;

    /**
     * 转账api调用检测
     * Transfer api call detection
     * */
    public static final int PHOTON_EVENT_CHANNEL_TRANSFER_START = PHOTON_EVENT_CHANNEL_LIST_ERROR + 1;


    /**
     * 转账api调用成功
     * Transfer api call succeeded
     * */
    public static final int PHOTON_EVENT_CHANNEL_TRANSFER_SUCCESS = PHOTON_EVENT_CHANNEL_TRANSFER_START + 1;

    /**
     * 转账api调用失败
     * Transfer api call failed
     * */
    public static final int PHOTON_EVENT_CHANNEL_TRANSFER_ERROR = PHOTON_EVENT_CHANNEL_TRANSFER_SUCCESS + 1;

    /**
     * 查询路由信息api调用成功 并且有费用
     * The query routing information api is successfully invoked and has a fee
     * */
    public static final int PHOTON_EVENT_CHANNEL_FIND_PATH_SUCCESS = PHOTON_EVENT_CHANNEL_TRANSFER_ERROR + 1;

    /**
     * 查询路由信息api调用失败
     * Query routing information api call failed
     * */
    public static final int PHOTON_EVENT_CHANNEL_FIND_PATH_ERROR = PHOTON_EVENT_CHANNEL_FIND_PATH_SUCCESS + 1;


    /**
     * 创建通道api调用成功
     * Create channel api call succeeded
     * */
    public static final int PHOTON_EVENT_CHANNEL_CREATE_SUCCESS = PHOTON_EVENT_CHANNEL_FIND_PATH_ERROR + 1;

    /**
     * 创建通道api调用失败
     * Create channel api call failed
     * */
    public static final int PHOTON_EVENT_CHANNEL_CREATE_ERROR = PHOTON_EVENT_CHANNEL_CREATE_SUCCESS + 1;


    /**
     * 创建通道api调用开始
     * Create a channel api call to start
     * */
    public static final int PHOTON_EVENT_CHANNEL_CREATE_ING = PHOTON_EVENT_CHANNEL_CREATE_ERROR + 1;

    /**
     * 通道存款页面获取余额成功回调
     * Channel deposit page to get the balance successfully callback
     * */
    public static final int PHOTON_EVENT_BALANCE_ON_CHAIN_SUCCESS = PHOTON_EVENT_CHANNEL_CREATE_ING + 1;

    /**
     * 通道存款api调用成功
     * Channel deposit api call succeeded
     * */
    public static final int PHOTON_EVENT_CHANNEL_DEPOSIT_SUCCESS = PHOTON_EVENT_BALANCE_ON_CHAIN_SUCCESS + 1;

    /**
     * 通道存款api调用失败
     * Channel deposit api call failed
     * */
    public static final int PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR = PHOTON_EVENT_CHANNEL_DEPOSIT_SUCCESS + 1;


    /**
     * 通道存款api调用开始
     * Channel deposit api call starts
     * */
    public static final int PHOTON_EVENT_CHANNEL_DEPOSIT_ING = PHOTON_EVENT_CHANNEL_DEPOSIT_ERROR + 1;


    /**
     * 获取发出的交易列表失败
     * Failed to get the list of issued transactions
     * */
    public static final int PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_ERROR = PHOTON_EVENT_CHANNEL_DEPOSIT_ING + 1;

    /**
     * 获取发出的交易列表成功
     * Get the list of issued transactions successfully
     * */
    public static final int PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_SUCCESS = PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_ERROR + 1;

    /**
     * 获取收到的交易列表成功
     * Get the list of transactions received successfully
     * */
    public static final int PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_SUCCESS = PHOTON_EVENT_CHANNEL_SEND_TRANSFER_LIST_SUCCESS + 1;

    /**
     * 获取收到的交易列表失败
     * Failed to get the list of transactions received
     * */
    public static final int PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_ERROR = PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_SUCCESS + 1;

    /**
     * 获取合约交易列表成功
     * Successfully get a list of contract transactions
     * */
    public static final int PHOTON_EVENT_CHANNEL_CONTRACT_LIST_SUCCESS = PHOTON_EVENT_CHANNEL_RECEIVED_TRANSFER_LIST_ERROR + 1;


    /**
     * 创建钱包密码错误
     * Create wallet password error
     * */
    public static final int WALLET_CREATE_PWD_ERROR = PHOTON_EVENT_CHANNEL_CONTRACT_LIST_SUCCESS + 1;

    /**
     * 创建钱包错误
     * Create a wallet error
     * */
    public static final int WALLET_CREATE_OTHER_ERROR = WALLET_CREATE_PWD_ERROR + 1;

    /**
     * 创建钱包重复错误
     * Create wallet repeat error
     * */
    public static final int WALLET_CREATE_REPEAT_ERROR = WALLET_CREATE_OTHER_ERROR + 1;


    /**
     * 创建钱包成功
     * Create a wallet successfully
     * */
    public static final int WALLET_CREATE_SUCCESS = WALLET_CREATE_REPEAT_ERROR + 1;

    /**
     * 校验钱包错误
     * Verify wallet error
     * */
    public static final int WALLET_VERIFY_OTHER_ERROR = WALLET_CREATE_SUCCESS + 1;

    /**
     * 校验钱包密码错误
     * Verify wallet password error
     * */
    public static final int WALLET_VERIFY_PWD_ERROR = WALLET_VERIFY_OTHER_ERROR + 1;

    /**
     * 校验钱包成功
     * Verify wallet success
     * */
    public static final int WALLET_VERIFY_SUCCESS = WALLET_VERIFY_PWD_ERROR + 1;


    /**
     * 删除钱包成功
     * Delete wallet successfully
     * */
    public static final int WALLET_DELETE_SUCCESS = WALLET_VERIFY_SUCCESS + 1;


    /**
     * 导入钱包成功
     * Imported wallet successfully
     * */
    public static final int WALLET_IMPORT_SUCCESS = WALLET_DELETE_SUCCESS + 1;

}
