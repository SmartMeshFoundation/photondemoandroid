package com.smartmesh.photon.channel.entity;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONObject;

public class PhotonContractEntity implements Comparable<PhotonContractEntity>{

    /**
     * tx_hash : 0xda42edb7198bac71f8479b2439822a80a70ffc04c6fec4783cca72a9aa043d4b
     * channel_identifier : 0x63e0446d5c9d910485098e19ed2dafd4d1727214203a4ea9eb55977a32adb094
     * open_block_number : 1914544
     * token_address : 0xf0123c3267af5cbbfab985d39171f5f5758c0900
     * type : CooperateSettle
     * is_self_call : true
     * tx_params : {"token_address":"0xf0123c3267af5cbbfab985d39171f5f5758c0900","p1_address":"0x9a8130b5daaf11f48637d10172fc427dfcc44ebb","p1_balance":48999400000000000000,"p2_address":"0xbbad695e60d8c3b50bafd78bd9400522ff14c95d","p2_balance":51000600000000000000,"p1_signature":"XMSSG+MmL+nEg8JaUYgYQHVdy+Ez+MMP+cA7j6PUjXUBn3vSaG5w7m0Gb+7Ss65L93pNviAgOHupOgjINJkz4Rw=","p2_signature":"IHGKDbdDpRTrYQIFdiGC82VwUM+sM4gi0QRaIpvvBkwNu2duqLC7RyTvLnnwQQhfupnZRHYQ4582/XwK6WaFlxw="}
     * tx_status : success
     * events : null
     * pack_block_number : 1914552
     * call_time : 1550816050
     * pack_time : 1550816067
     * gas_price : 0
     * gas_used : 0
     */

    private String txHash;
    private String channelIdentifier;
    private long openBlockNumber;
    private String tokenAddress;
    private String type;
    private boolean isSelfCall;
    private String txParams;
    private String txStatus;
    private long packBlockNumber;
    private long callTime;
    private long packTime;
    private String gasPrice;
    private String gasUsed;
    private PhotonContractTxEntity contractTxEntity;

    public PhotonContractTxEntity getContractTxEntity() {
        return contractTxEntity;
    }

    public void setContractTxEntity(PhotonContractTxEntity contractTxEntity) {
        this.contractTxEntity = contractTxEntity;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getChannelIdentifier() {
        return channelIdentifier;
    }

    public void setChannelIdentifier(String channelIdentifier) {
        this.channelIdentifier = channelIdentifier;
    }

    public long getOpenBlockNumber() {
        return openBlockNumber;
    }

    public void setOpenBlockNumber(long openBlockNumber) {
        this.openBlockNumber = openBlockNumber;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelfCall() {
        return isSelfCall;
    }

    public void setSelfCall(boolean selfCall) {
        isSelfCall = selfCall;
    }

    public String getTxParams() {
        return txParams;
    }

    public void setTxParams(String txParams) {
        this.txParams = txParams;
    }

    public String getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(String txStatus) {
        this.txStatus = txStatus;
    }

    public long getPackBlockNumber() {
        return packBlockNumber;
    }

    public void setPackBlockNumber(long packBlockNumber) {
        this.packBlockNumber = packBlockNumber;
    }

    public long getCallTime() {
        return callTime;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }

    public long getPackTime() {
        return packTime;
    }

    public void setPackTime(long packTime) {
        this.packTime = packTime;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public PhotonContractEntity parse(JSONObject object){
        if (object == null){
            return null;
        }
        setTxHash(object.optString("tx_hash"));
        setChannelIdentifier(object.optString("channel_identifier"));
        setOpenBlockNumber(object.optLong("open_block_number"));
        setTokenAddress(object.optString("token_address"));
        setType(object.optString("type"));
        setSelfCall(object.optBoolean("is_self_call"));
        setPackBlockNumber(object.optLong("pack_block_number"));
        setCallTime(object.optLong("call_time"));
        setPackTime(object.optLong("pack_time"));
        setGasPrice(object.optString("gas_price"));
        setGasUsed(object.optString("gas_used"));
        setTxStatus(object.optString("tx_status"));
        String jsonString = object.optString("tx_params");
        if (!TextUtils.isEmpty(jsonString)){
            try {
                JSONObject txObject = new JSONObject(jsonString);
                PhotonContractTxEntity txEntity = new PhotonContractTxEntity().parse(txObject);
                setContractTxEntity(txEntity);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public int compareTo(@NonNull PhotonContractEntity o) {
        return (int) (o.getCallTime() - this.getCallTime());
    }
}
