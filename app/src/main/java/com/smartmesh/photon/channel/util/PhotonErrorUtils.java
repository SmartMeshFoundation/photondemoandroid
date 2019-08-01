package com.smartmesh.photon.channel.util;
/**
 * ## 特殊接口错误处理(交易接口错误处理建议是按照上诉逻辑实现来给出的建议)
 * 接口名|手机接口|http接口|错误码|错误说明|建议处理方式
 * ---|---|---|---|---|---|
 * FindPath|√|√|1022|photon节点没有启用pfs或pfs模块不可用|不应该出现,目前手机启动photon会使用默认的pfs,可以不进行处理
 * FindPath|√|√|3003|无可用路由|
 * FindPath|√|√|4000|调用pfs失败|报告用户查询路由失败
 * Transfer|√|√|1002|通道余额不足|不应该出现
 * Transfer|√|√|1014|当前无网,不允许发起MediatedTransfer|不应该出现
 * Transfer|√|√|1015|密码相同的重复交易|报告用户
 * Transfer|√|√|1020|交易超时|报告用户
 * Transfer|√|√|1023|无网时间过长,不允许发起交易|报告用户
 * Transfer|√|√|3001|token不存在|不应该出现
 * Transfer|√|√|3002|无直接通道|不应该出现
 * Transfer|√|√|3003|无可用路由|不应该出现
 * Transfer|√|√|5001|通道状态不为open|不应该出现
 * Deposit(创建通道)|√|√|2012|公链节点状态不正常|报告用户
 * Deposit(创建通道)|√|√|3005|通道已经存在|报告用户
 * Deposit(创建通道)|√|√|2000|合约调用gas费用不足|报告用户
 * Deposit(创建通道)|√|√|2999|公链节点rpc调用失败|报告用户
 * Deposit(存款)|√|√|2012|公链节点状态不正常|报告用户
 * Deposit(存款)|√|√|3002|通道不存在|不应该出现
 * Deposit(存款)|√|√|5001|通道状态不为open|不应该出现,如果出现可以报告用户
 * Withdraw|√|√|1016|对方不在线|报告用户
 * Withdraw|√|√|3002|通道不存在|不应该出现
 * Withdraw|√|√|5001|通道状态不允许提现|编码规避或报告用户,这里有两种可能,第一种是通道状态不为open,可以通过调用前校验通道状态来规避,第二种是通道内存在尚未完成的Deposit操作,直接建议用户等待Deposit完成之后再试
 * Withdraw|√|√|5018|提现金额大于可用金额|编码规避或报告用户,规避方式:调用前校验用户收入
 * Withdraw|√|√|5024|通道中仍然存在锁,不允许提现|报告用户
 * 合作关闭|√|√|1016|对方不在线|报告用户
 * 合作关闭|√|√|3002|通道不存在|不应该出现
 * 合作关闭|√|√|5025|通道中仍存在锁|报告用户
 * Close(强制关闭)|√|√|5001|通道状态不允许关闭(已关闭/已结算)|编码规避或报告用户
 * Close(强制关闭)|√|√|2000|合约调用gas费用不足|报告用户
 * Close(强制关闭)|√|√|2999|公链节点rpc调用失败|报告用户
 * Settle(结算)|√|√|5001|通道状态不允许关闭(已关闭/已结算)|编码规避或报告用户
 * Settle(结算)|√|√|5002|尚未到可结算时间|报告用户
 * Settle(结算)|√|√|2000|合约调用gas费用不足|报告用户
 * Settle(结算)|√|√|2999|公链节点rpc调用失败|报告用户
 * */

import android.content.Context;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.util.MyToast;

/**
 * photon api 错误码处理
 */
public class PhotonErrorUtils {

    private static final int PHOTON_CODE_1002 = 1002;
    private static final int PHOTON_CODE_1014 = 1014;
    private static final int PHOTON_CODE_1015 = 1015;
    private static final int PHOTON_CODE_1016 = 1016;
    private static final int PHOTON_CODE_1020 = 1020;
    private static final int PHOTON_CODE_1022 = 1022;
    private static final int PHOTON_CODE_1023 = 1023;
    private static final int PHOTON_CODE_2000 = 2000;
    private static final int PHOTON_CODE_2012 = 2012;
    private static final int PHOTON_CODE_2999 = 2999;
    private static final int PHOTON_CODE_3005 = 3005;
    private static final int PHOTON_CODE_4000 = 4000;
    private static final int PHOTON_CODE_5001 = 5001;
    private static final int PHOTON_CODE_5018 = 5018;
    private static final int PHOTON_CODE_5024 = 5024;
    private static final int PHOTON_CODE_5025 = 5025;
    private static final int PHOTON_CODE_5027 = 5027;

    /**
     * 通道相关错误码处理
     * @param errorCode      错误码
     * @param errorMessage   错误信息
     * */
    public static void handlerPhotonError(int errorCode,String errorMessage){
        switch (errorCode){
            case PHOTON_CODE_1002:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_1002));
                break;
            case PHOTON_CODE_1014:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_1014));
                break;
            case PHOTON_CODE_1015:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_1015));
                break;
            case PHOTON_CODE_1016:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_1016));
                break;
            case PHOTON_CODE_1020:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_1020));
                break;
            case PHOTON_CODE_1022:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_1022));
                break;
            case PHOTON_CODE_1023:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_1023));
                break;
            case PHOTON_CODE_2000:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_2000));
                break;
            case PHOTON_CODE_2012:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_2012));
                break;
            case PHOTON_CODE_2999:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_2999));
                break;
            case PHOTON_CODE_3005:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_3005));
                break;
            case PHOTON_CODE_4000:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_4000));
                break;
            case PHOTON_CODE_5001:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_5001));
                break;
            case PHOTON_CODE_5018:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_5018));
                break;
            case PHOTON_CODE_5024:
            case PHOTON_CODE_5025:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_5025));
                break;
            case PHOTON_CODE_5027:
                MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_error_5027));
                break;
            default:
                MyToast.showToast(PhotonApplication.mContext,errorMessage);
                break;
        }
    }

    /**
     * 通道相关错误码处理
     * @param errorCode      错误码
     * */
    public static String handlerPhotonErrorString(Context context,int errorCode, String errorMessage){
        if (context == null){
            return null;
        }
        switch (errorCode){
            case PHOTON_CODE_1002:
                return context.getResources().getString(R.string.photon_error_1002);
            case PHOTON_CODE_1014:
                return context.getResources().getString(R.string.photon_error_1014);
            case PHOTON_CODE_1015:
                return context.getResources().getString(R.string.photon_error_1015);
            case PHOTON_CODE_1016:
                return context.getResources().getString(R.string.photon_error_1016);
            case PHOTON_CODE_1020:
                return context.getResources().getString(R.string.photon_error_1020);
            case PHOTON_CODE_1022:
                return context.getResources().getString(R.string.photon_error_1022);
            case PHOTON_CODE_1023:
                return context.getResources().getString(R.string.photon_error_1023);
            case PHOTON_CODE_2000:
                return context.getResources().getString(R.string.photon_error_2000);
            case PHOTON_CODE_2012:
                return context.getResources().getString(R.string.photon_error_2012);
            case PHOTON_CODE_2999:
                return context.getResources().getString(R.string.photon_error_2999);
            case PHOTON_CODE_3005:
                return context.getResources().getString(R.string.photon_error_3005);
            case PHOTON_CODE_4000:
                return context.getResources().getString(R.string.photon_error_4000);
            case PHOTON_CODE_5001:
                return context.getResources().getString(R.string.photon_error_5001);
            case PHOTON_CODE_5018:
                return context.getResources().getString(R.string.photon_error_5018);
            case PHOTON_CODE_5024:
            case PHOTON_CODE_5025:
                return context.getResources().getString(R.string.photon_error_5025);
            case PHOTON_CODE_5027:
                return context.getResources().getString(R.string.photon_error_5027);
            default:
                return errorMessage;
        }
    }

}
