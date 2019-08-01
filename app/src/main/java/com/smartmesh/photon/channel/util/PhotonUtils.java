package com.smartmesh.photon.channel.util;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.R;
import com.smartmesh.photon.channel.entity.PhotonStatusType;
import com.smartmesh.photon.util.ContractUtils;
import com.smartmesh.photon.util.MyToast;

public class PhotonUtils {

    /**
     * 获取当前光子通道的币种
     * @param photonTokenAddress 合约地址
     * */
    public static String getPhotonTokenSymbol(String photonTokenAddress){
        String tokenSymbol = "SMT";
        if (PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS.equalsIgnoreCase(photonTokenAddress)){
            tokenSymbol = "SMT";
        }
        return tokenSymbol;
    }

    /**
     * 获取当前光子通道币种的合约地址
     * @param tokenSymbol token昵称
     * */
    public static String getPhotonTokenAddress(String tokenSymbol){
        String photonTokenAddress = PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS;
        if ("SMT".equalsIgnoreCase(tokenSymbol)){
            photonTokenAddress = PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS;
        }
        return photonTokenAddress;
    }


    /**
     * 获取当前光子通道的币种的合约地址
     * @param photonTokenAddress 光子合约地址
     * */
    public static String getTokenAddress(String photonTokenAddress){
        String tokenAddress = ContractUtils.SMT_CONTACT;
        if (PhotonUrl.PHOTON_SMT_TOKEN_ADDRESS.equalsIgnoreCase(photonTokenAddress)){
            tokenAddress = ContractUtils.SMT_CONTACT;
        }
        return tokenAddress;
    }

    /**
     * 监测通道是否可以进行 关闭 提现操作
     * */
    public static boolean offLineOrSourceIsEmpty(int position){
        try {
            if (PhotonApplication.mPhotonStatusVo != null){
                if (PhotonStatusType.Connected != PhotonApplication.mPhotonStatusVo.getEthStatus()){
                    MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_channel_mesh_pay_4));
                    return true;
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 监测通道是否可以进行 关闭 提现操作
     * 无网状态不允许转账
     * */
    public static boolean offLineOrSourceIsEmpty(){
        try {
            if (PhotonApplication.mPhotonStatusVo != null){
                if (PhotonStatusType.Connected != PhotonApplication.mPhotonStatusVo.getEthStatus()){
                    MyToast.showToast(PhotonApplication.mContext,PhotonApplication.mContext.getString(R.string.photon_channel_mesh_pay_4));
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
