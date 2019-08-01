package com.smartmesh.photon.channel.util;

import android.os.Build;
import android.text.TextUtils;

import com.smartmesh.photon.PhotonApplication;
import com.smartmesh.photon.util.MySharedPrefs;
import com.smartmesh.photon.wallet.util.WalletInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Numeric;

public class ChannelNoteUtils {

    //通道备注 自己地址
    private static final String CHANNEL_NOTE_FROM_ADDRESS = "channel_note_from_address";
    //通道备注 对方地址
    private static final String CHANNEL_NOTE_TO_ADDRESS = "channel_note_to_address";
    //通道备注
    private static final String CHANNEL_NOTE_NOTE = "channel_note_note";
    //通道 token地址
    private static final String CHANNEL_TOKEN_ADDRESS = "channel_token_address";

    /**
     * 获取通道昵称
     * @param address 通道地址 这里做key获取通道昵称
     * */
    public static String getChannelNote(String tokenAddress,String address){
        if (TextUtils.isEmpty(address) || TextUtils.isEmpty(tokenAddress)){
            return null;
        }
        String fromAddress = WalletInfoUtils.getInstance().getSelectAddress();
        if (TextUtils.isEmpty(fromAddress)){
            return null;
        }
        String channelNote = "";
        try {
            String jsonString = MySharedPrefs.readString(PhotonApplication.mContext,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_CHANNEL_NOTE_LIST);
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0 ; i < array.length() ; i++){
                JSONObject object = array.optJSONObject(i);
                String tempFromAddress = object.optString(CHANNEL_NOTE_FROM_ADDRESS);
                String tempToAddress = object.optString(CHANNEL_NOTE_TO_ADDRESS);
                String tempPhotonTokenAddress = object.optString(CHANNEL_TOKEN_ADDRESS);
                if (!TextUtils.isEmpty(tempFromAddress) && !TextUtils.isEmpty(tempToAddress) && !TextUtils.isEmpty(tempPhotonTokenAddress)
                        && TextUtils.equals(checkAddress(fromAddress),tempFromAddress.toLowerCase())
                        && TextUtils.equals(tokenAddress.toLowerCase(),tempPhotonTokenAddress.toLowerCase())
                        && TextUtils.equals(checkAddress(address),tempToAddress.toLowerCase())){
                    channelNote = object.optString(CHANNEL_NOTE_NOTE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MySharedPrefs.write(PhotonApplication.mContext,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_CHANNEL_NOTE_LIST,"");
        }
        return channelNote;
    }

    /**
     * 增加通道昵称
     * @param fromAddress    自己的地址
     * @param address        通道对方地址，这里做key
     * @param channelNote   通道昵称 value
     * */
    public static void insertChannelNote(String tokenAddress,String fromAddress,String address,String channelNote){
        try {
            if (TextUtils.isEmpty(address) || TextUtils.isEmpty(fromAddress) || TextUtils.isEmpty(tokenAddress)){
                return;
            }
            try {
                JSONArray array;
                String jsonString = MySharedPrefs.readString(PhotonApplication.mContext,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_CHANNEL_NOTE_LIST);
                if (TextUtils.isEmpty(jsonString)){
                    array = new JSONArray();
                }else{
                    array = new JSONArray(jsonString);
                }
                boolean hasExist = false;
                for (int i = 0 ; i < array.length() ; i++){
                    JSONObject object = array.optJSONObject(i);
                    String tempFromAddress = object.optString(CHANNEL_NOTE_FROM_ADDRESS);
                    String tempToAddress = object.optString(CHANNEL_NOTE_TO_ADDRESS);
                    String tempPhotonTokenAddress = object.optString(CHANNEL_TOKEN_ADDRESS);
                    if (!TextUtils.isEmpty(tempFromAddress) && !TextUtils.isEmpty(tempToAddress) && !TextUtils.isEmpty(tempPhotonTokenAddress)
                            && TextUtils.equals(checkAddress(fromAddress),tempFromAddress.toLowerCase())
                            && TextUtils.equals(tokenAddress.toLowerCase(),tempPhotonTokenAddress.toLowerCase())
                            && TextUtils.equals(checkAddress(address),tempToAddress.toLowerCase())){
                        hasExist = true;
                        array.optJSONObject(i).put(CHANNEL_NOTE_NOTE,channelNote);
                    }
                }
                if (!hasExist){
                    JSONObject object = new JSONObject();
                    object.put(CHANNEL_NOTE_FROM_ADDRESS,checkAddress(fromAddress));
                    object.put(CHANNEL_NOTE_TO_ADDRESS,checkAddress(address));
                    object.put(CHANNEL_NOTE_NOTE,channelNote);
                    object.put(CHANNEL_TOKEN_ADDRESS,tokenAddress.toLowerCase());
                    array.put(object);
                }
                MySharedPrefs.write(PhotonApplication.mContext,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_CHANNEL_NOTE_LIST,array.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                MySharedPrefs.write(PhotonApplication.mContext,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_CHANNEL_NOTE_LIST,"");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除通道昵称，通常是关闭通道后（这里有问题，不知道什么时候删除）
     * 关闭通道不是立刻执行的
     * @param address 通道地址
     * */
    public static void deleteChannelNote(String tokenAddress,String address){
        try {
            if (TextUtils.isEmpty(address) || TextUtils.isEmpty(tokenAddress)){
                return;
            }
            String fromAddress = WalletInfoUtils.getInstance().getSelectAddress();
            if (TextUtils.isEmpty(fromAddress)){
                return;
            }
            JSONArray array;
            String jsonString = MySharedPrefs.readString(PhotonApplication.mContext,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_CHANNEL_NOTE_LIST);
            if (TextUtils.isEmpty(jsonString)){
                array = new JSONArray();
            }else{
                array = new JSONArray(jsonString);
            }
            for (int i = 0 ; i < array.length() ; i++){
                JSONObject object = array.optJSONObject(i);
                String tempFromAddress = object.optString(CHANNEL_NOTE_FROM_ADDRESS);
                String tempToAddress = object.optString(CHANNEL_NOTE_TO_ADDRESS);
                String tempPhotonTokenAddress = object.optString(CHANNEL_TOKEN_ADDRESS);
                if (!TextUtils.isEmpty(tempFromAddress) && !TextUtils.isEmpty(tempToAddress) && !TextUtils.isEmpty(tempPhotonTokenAddress)
                        && TextUtils.equals(checkAddress(fromAddress),tempFromAddress.toLowerCase())
                        && TextUtils.equals(tokenAddress.toLowerCase(),tempPhotonTokenAddress.toLowerCase())
                        && TextUtils.equals(checkAddress(address),tempToAddress.toLowerCase())){
                    if (Build.VERSION.SDK_INT < 19){
                        array = removeFromJsonArray(array,i);
                    }else {
                        array.remove(i);
                    }
                    break;
                }
            }
            MySharedPrefs.write(PhotonApplication.mContext,MySharedPrefs.FILE_USER,MySharedPrefs.KEY_PHOTON_CHANNEL_NOTE_LIST,array.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray removeFromJsonArray(JSONArray array,int position){
        JSONArray tempArray = new JSONArray();
        for (int i = 0 ; i < array.length() ; i++){
            if (i != position){
                tempArray.put(array.optJSONObject(i));
            }
        }
        return tempArray;
    }

    /**
     * 检测通道地址，需加0x
     * */
    public static String checkAddress(String address){
        if (TextUtils.isEmpty(address)){
            return address;
        }
        return Numeric.prependHexPrefix(address);
    }

}
