package com.smartmesh.photon.channel.entity;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.math.BigDecimal;

public class PhotonContractTxEntity implements Comparable<PhotonContractTxEntity>{

    /**
     *      // DepositTXParams :
     *      // 1. 保存在ApproveTX的TXParams中,给崩溃恢复后继续deposit使用
     *      // 2. 保存在DepositTX的TXParams中
     *      type DepositTXParams struct {
     *      TokenAddress       common.Address `json:"token_address"` // token地址
     *      ParticipantAddress common.Address `json:"participant_address"` // 自己的地址
     *      PartnerAddress     common.Address `json:"partner_address"` // 通道对方的地址
     *      Amount             *big.Int       `json:"amount"` // 存款金额
     *      SettleTimeout      uint64         `json:"settle_timeout"` //
     *      settle_timeout"` // 等于0时表示Deposit, 大于0时表示OpenAndDeposit
     *      }
     *
     *      // ChannelCloseOrChannelUpdateBalanceProofTXParams 关闭通道或者UpdateBalanceProof的参数,两种操作复用,根据上层TXInfo中的Type区分
     *      type ChannelCloseOrChannelUpdateBalanceProofTXParams struct {
     *      TokenAddress       common.Address `json:"token_address"`
     *      ParticipantAddress common.Address `json:"participant_address"`  // 自己的地址
     *      PartnerAddress     common.Address `json:"partner_address"`  // 通道对方的地址
     *      TransferAmount     *big.Int       `json:"transfer_amount"` // 对方的TransferAmount
     *      LocksRoot          common.Hash    `json:"locks_root"` // 对方的locksroot
     *      Nonce              uint64         `json:"nonce"`  // 对方的locksroot
     *      ExtraHash          common.Hash    `json:"extra_hash"`
     *      Signature          []byte         `json:"signature"`  // 对方的BalanceProof签名
     *      }
     *
     *      // ChannelSettleTXParams 通道结算的参数, p1为自己,p2为对方
     *      type ChannelSettleTXParams struct {
     *      TokenAddress     common.Address `json:"token_address"`
     *      P1Address        common.Address `json:"p1_address"`
     *      P1TransferAmount *big.Int       `json:"p1_transfer_amount"`
     *      P1LocksRoot      common.Hash    `json:"p1_locks_root"`
     *      P2Address        common.Address `json:"p2_address"`
     *      P2TransferAmount *big.Int       `json:"p2_transfer_amount"`
     *      P2LocksRoot      common.Hash    `json:"p2_locks_root"`
     *      }
     *
     *      // ChannelWithDrawTXParams 通道取现的参数, p1为自己,p2为对方
     *      type ChannelWithDrawTXParams struct {
     *      TokenAddress common.Address `json:"token_address"`
     *      P1Address    common.Address `json:"p1_address"`
     *      P2Address    common.Address `json:"p2_address"`
     *      P1Balance    *big.Int       `json:"p1_balance"`
     *      P1Withdraw   *big.Int       `json:"p1_withdraw"`
     *      P1Signature  []byte         `json:"p1_signature"`
     *      P2Signature  []byte         `json:"p2_signature"`
     *      }
     *
     *      // ChannelCooperativeSettleTXParams 通道合作关闭的参数, p1为自己,p2为对方
     *      type ChannelCooperativeSettleTXParams struct {
     *      TokenAddress common.Address `json:"token_address"`
     *      P1Address    common.Address `json:"p1_address"`
     *      P1Balance    *big.Int       `json:"p1_balance"`
     *      P2Address    common.Address `json:"p2_address"`
     *      P2Balance    *big.Int       `json:"p2_balance"`
     *      P1Signature  []byte         `json:"p1_signature"`
     *      P2Signature  []byte         `json:"p2_signature"`
     *      }
     * */

    /**
     * token_address : 0xf0123c3267af5cbbfab985d39171f5f5758c0900
     * p1_address : 0x9a8130b5daaf11f48637d10172fc427dfcc44ebb
     * p1_balance : 48999400000000000000
     * p2_address : 0xbbad695e60d8c3b50bafd78bd9400522ff14c95d
     * p2_balance : 51000600000000000000
     */

    // 通道取现的参数 通道结算的参数, p1为自己,p2为对方
    private String token_address;// token地址
    private String p1_address;//自己的地址
    private String p1_balance;//自己的金额
    private String p2_address;//对方的地址
    private String p2_balance;//对方的金额
    private String p1_withdraw;//自己提现金额
    private String p1_transfer_amount;//自己结算金额
    private String participant_address;// 自己的地址
    private String partner_address;// 通道对方的地址
    private String amount;// 存款金额
    private String transfer_amount;// 对方的TransferAmount
    private String settle_timeout;//等于0时表示Deposit, 大于0时表示OpenAndDeposit

    public String getSettle_timeout() {
        return settle_timeout;
    }

    public void setSettle_timeout(String settle_timeout) {
        this.settle_timeout = settle_timeout;
    }

    public String getP1_transfer_amount() {
        return p1_transfer_amount;
    }

    public void setP1_transfer_amount(String p1_transfer_amount) {
        this.p1_transfer_amount = p1_transfer_amount;
    }

    public String getToken_address() {
        return token_address;
    }

    public void setToken_address(String token_address) {
        this.token_address = token_address;
    }

    public String getP1_address() {
        return p1_address;
    }

    public void setP1_address(String p1_address) {
        this.p1_address = p1_address;
    }

    public String getP1_balance() {
        return p1_balance;
    }

    public void setP1_balance(String p1_balance) {
        this.p1_balance = p1_balance;
    }

    public String getP2_address() {
        return p2_address;
    }

    public void setP2_address(String p2_address) {
        this.p2_address = p2_address;
    }

    public String getP2_balance() {
        return p2_balance;
    }

    public void setP2_balance(String p2_balance) {
        this.p2_balance = p2_balance;
    }

    public String getP1_withdraw() {
        return p1_withdraw;
    }

    public void setP1_withdraw(String p1_withdraw) {
        this.p1_withdraw = p1_withdraw;
    }

    public String getParticipant_address() {
        return participant_address;
    }

    public void setParticipant_address(String participant_address) {
        this.participant_address = participant_address;
    }

    public String getPartner_address() {
        return partner_address;
    }

    public void setPartner_address(String partner_address) {
        this.partner_address = partner_address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransfer_amount() {
        return transfer_amount;
    }

    public void setTransfer_amount(String transfer_amount) {
        this.transfer_amount = transfer_amount;
    }

    public PhotonContractTxEntity parse(JSONObject object){
        if (object == null){
            return null;
        }
        setToken_address(object.optString("token_address"));
        setP1_address(object.optString("p1_address"));
        setP1_withdraw(object.optString("p1_withdraw"));
        setP2_address(object.optString("p2_address"));
        setParticipant_address(object.optString("participant_address"));
        setPartner_address(object.optString("partner_address"));
        setSettle_timeout(object.optString("settle_timeout"));
        BigDecimal ether = Convert.Unit.ETHER.getWeiFactor();
        try {
            String parseP1Balance = object.optString("p1_balance");
            if (!TextUtils.isEmpty(parseP1Balance)){
                String p1Balance = new BigDecimal(object.optString("p1_balance"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setP1_balance(p1Balance);
            }else{
                setP1_balance("0");
            }
        }catch (Exception e){
            e.printStackTrace();
            setP1_balance("0");
        }
        try {
            String parseP2Balance = object.optString("p2_balance");
            if (!TextUtils.isEmpty(parseP2Balance)){
                String p2Balance = new BigDecimal(object.optString("p2_balance"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).toPlainString();
                setP2_balance(p2Balance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            String parseAmount = object.optString("amount");
            if (!TextUtils.isEmpty(parseAmount)){
                String amount = new BigDecimal(object.optString("amount"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setAmount(amount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            String parseTransferAmount = object.optString("transfer_amount");
            if (!TextUtils.isEmpty(parseTransferAmount)){
                String transferAmount = new BigDecimal(object.optString("transfer_amount"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setTransfer_amount(transferAmount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            String parseP1TransferAmount = object.optString("p1_transfer_amount");
            if (!TextUtils.isEmpty(parseP1TransferAmount)){
                String p1TransferAmount = new BigDecimal(object.optString("p1_transfer_amount"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setP1_transfer_amount(p1TransferAmount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public int compareTo(@NonNull PhotonContractTxEntity o) {
        return 0;
    }
}
