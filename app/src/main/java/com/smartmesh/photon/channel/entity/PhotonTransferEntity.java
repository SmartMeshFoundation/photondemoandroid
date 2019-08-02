package com.smartmesh.photon.channel.entity;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

public class PhotonTransferEntity implements Comparable<PhotonTransferEntity>{
    /**
     * initiator_address : 0x70aEfe8d97EF5984B91b5169418f3db283F65a29
     * target_address : 0x63611545f5d3D6c8e194cA92c3a4BBD6b49bA74b
     * token_address : 0xF0123C3267Af5CbBFAB985d39171f5F5758C0900
     * amount : 199000000000000000000
     * lockSecretHash : 0x3945b8d047ce6a38f9338817621f5eb68ff46f2b0c340e64e63f7fe6443640c5
     * fee : 0
     * data :
     */

    //交易hash  Trading hash
    private String lockSecretHash;

    //目标地址 target address
    private String targetAddress;
    //发起人地址   Sponsor address
    private String fromAddress;
    //转账金额     transfer amount
    private String amount;
    //转账区块     Transfer block
    private long blockNumber;
    //转账时间     Transfer time
    private long time;
    //转账费用     Transfer fee
    private String fee;
    //token 合约地址   Token contract address
    private String tokenAddress;
    //转账信息    Transfer information
    private String data;
    /**
     *   0 - Transfer init
     *   1 - transfer can cancel
     *   2 - transfer can not cancel
     *   3 - transfer already success
     *   4 - transfer cancel by user request
     *   5 - transfer already failed
     * */
    private int status;
    //发出还是接受  0 发送 1 接收记录     Issue or accept 0 send 1 receive record
    private int type;

    public String getLockSecretHash() {
        return lockSecretHash;
    }

    public void setLockSecretHash(String lockSecretHash) {
        this.lockSecretHash = lockSecretHash;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public PhotonTransferEntity parse(JSONObject object){
        if (object == null){
            return null;
        }
        if (TextUtils.isEmpty(object.optString("target_address"))){
            setTargetAddress(object.optString("initiator_address"));
        }else{
            setTargetAddress(object.optString("target_address"));
        }
        if (TextUtils.isEmpty(object.optString("lockSecretHash"))){
            setLockSecretHash(object.optString("Key"));
        }else{
            setLockSecretHash(object.optString("lockSecretHash"));
        }
        setTokenAddress(object.optString("token_address"));
        setAmount(object.optString("amount"));
        setBlockNumber(object.optLong("block_number"));
        setFee(object.optString("fee"));
        setData(object.optString("data"));
        if (TextUtils.isEmpty(object.optString("time_stamp"))){
            setTime(object.optLong("sending_time"));
        }else{
            setTime(object.optLong("time_stamp"));
        }

        setStatus(object.optInt("status",-1));
        try {
            String balance = new BigDecimal(object.optString("amount"))
                    .divide(Convert.Unit.ETHER.getWeiFactor(), 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            setAmount(balance);
        }catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public int compareTo(@NonNull PhotonTransferEntity o) {
        return (int) (o.getTime() - this.getTime());
    }
}
