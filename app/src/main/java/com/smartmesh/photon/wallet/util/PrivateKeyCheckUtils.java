package com.smartmesh.photon.wallet.util;

import android.text.TextUtils;

import org.web3j.utils.Numeric;

/**
 * 检测私钥是否合法
 * */
public class PrivateKeyCheckUtils {

    public static String checkPrivateKey(String privateKey){
        if (TextUtils.isEmpty(privateKey)){
            return null;
        }

        if (Numeric.containsHexPrefix(privateKey)){
            privateKey = privateKey.substring(2);
        }

        for (int i = 0 ; i < privateKey.length() ; i++){
            String charAt = String.valueOf(privateKey.charAt(i));
            String regex="^[A-Fa-f0-9]+$";
            if(!charAt.matches(regex)){
                return charAt;
            }
        }
        return null;
    }
}
