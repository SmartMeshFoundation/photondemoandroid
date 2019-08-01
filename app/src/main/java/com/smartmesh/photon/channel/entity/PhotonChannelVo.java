package com.smartmesh.photon.channel.entity;

import android.text.TextUtils;

import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/1/24.
 * channel_identifier: 通道地址
 * open_block_number : 打开通道的时间
 * partner_address: 通道对方
 * balance: 自身通道可用余额
 * partner_balance : 通道对方可用余额
 * locked_amount: 自身通道锁定的金额
 * partner_locked_amount: 通道对方锁定的金额
 * token_address: token 地址
 * state :表示通道状态的数字
 * StateString : 通道状态
 * settle_timeout: 确定交易结算的时间段
 * reveal_timeout: 注册secret的时间段
 *
 * state :表示通道状态的数字
 * 0 	inValid 	无效的通道或者通道不存在
 * 1 	opened 	    通道打开状态，可以正常转账交易
 * 2 	closed 	    通道关闭状态，不能再发起交易，还可以接受交易
 * 3 	settled 	通道结算状态，和inValid状态意义相同
 * 4 	closing 	用户发起了关闭通道的请求,正在处理，正在进行交易,可以继续,不再新开交易
 * 5 	settling 	用户发起了结算请求,正在处理，正常情况下此时不应该还有未完成交易,不能新开交易,正在进行的交易也没必要继续了.因为已经提交到链上了
 * 6 	withdrawing 	用户收到或者发出了 withdraw 请求,这时候正在进行的交易只能立即放弃,因为没有任何意义了
 * 7 	cooperativeSettling 	用户收到或者发出了 cooperative settle 请求,这时候正在进行的交易只能立即放弃,因为没有任何意义了
 * 8 	prepareForWithdraw 	收到用户请求,要发起 withdraw, 但是目前还持有锁,不再发起或者接受任何交易,可以等待一段时间进行 withdraw,已经开始交易,可以继续
 * 9 	prepareForCooperativeSettle 	收到了用户 cooperative 请求,但是有正在处理的交易,这时候不再接受新的交易了,可以等待一段时间,然后settle，已经开始交易,可以继续
 * 10 	unkown 	StateError
 * 11 	StatePartnerCooperativeSettling 	用户收到对方发来的CooperativeSettle请求并同意后,将通道置为该状态,效果同cooperativeSetting状态
 * 12 	StatePartnerWithdrawing 	用户收到的了对方发来的withdraw请求并同意后,将通道置为该状态,效果同withdrawing状态
 */

public class PhotonChannelVo implements Serializable {

    /**
     * 通道地址
     * */
    private String channelIdentifier;

    /**
     * 打开通道的时间
     * */
    private String openBlockNumber;

    /**
     * 通道对方
     * */
    private String partnerAddress;

    /**
     * 通道内可转账的token 地址
     * */
    private String tokenAddress;

    /**
     * 通道内自身节点可用余额
     * */
    private String balance;

    /**
     * 通道内对方节点可用余额
     * */
    private String partnerBalance ;

    /**
     * 通道内自身节点锁定的金额
     * */
    private String lockedAmount;

    /**
     * 通道内对方节点锁定的金额
     * */
    private String partnerLockedAmount;

    /**
     * 确定交易结算的时间段（区块高度），必须大于reveal_timeout时间。
     * */
    private int settleTimeout;

    /**
     * 注册secret的时间段（区块高度），默认值为30，如果修改，可以在节点启动时加载--reveal-timeout 进行设置
     */
    private int revealTimeout;

    /**
     * 表示通道状态的数字
     * */
    private int state;

    /**
     * 通道状态字符表示
     * */
    private String stateString;

    /**
     * 关闭时所在的块
     * */
    private long closedBlock;

    /**
     * 可以进行settle的块
     * 大于此块时候可以进行settle 操作
     **/
    private long settledBlock;

    /**
     * 当前链上最新区块
     * */
    private long currentBlock;

    private boolean isHidden;

    /**
     * pms   表示委托状态的数字
     * 0 不需要委托
     * 1.正在等待委托到pms
     * 2.委托成功
     * 3.委托失败
     * 4.委托失败并无有效公链
     * */
    private int delegateState;

    public long getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(long currentBlock) {
        this.currentBlock = currentBlock;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getOpenBlockNumber() {
        return openBlockNumber;
    }

    public void setOpenBlockNumber(String openBlockNumber) {
        this.openBlockNumber = openBlockNumber;
    }

    public String getPartnerBalance() {
        return partnerBalance;
    }

    public void setPartnerBalance(String partnerBalance) {
        this.partnerBalance = partnerBalance;
    }

    public String getLockedAmount() {
        return lockedAmount;
    }

    public void setLockedAmount(String lockedAmount) {
        this.lockedAmount = lockedAmount;
    }

    public String getPartnerLockedAmount() {
        return partnerLockedAmount;
    }

    public void setPartnerLockedAmount(String partnerLockedAmount) {
        this.partnerLockedAmount = partnerLockedAmount;
    }

    public int getSettleTimeout() {
        return settleTimeout;
    }

    public void setSettleTimeout(int settleTimeout) {
        this.settleTimeout = settleTimeout;
    }

    public int getRevealTimeout() {
        return revealTimeout;
    }

    public void setRevealTimeout(int revealTimeout) {
        this.revealTimeout = revealTimeout;
    }

    public String getChannelIdentifier() {
        return channelIdentifier;
    }

    public void setChannelIdentifier(String channelIdentifier) {
        this.channelIdentifier = channelIdentifier;
    }

    public String getPartnerAddress() {
        return partnerAddress;
    }

    public void setPartnerAddress(String partnerAddress) {
        this.partnerAddress = partnerAddress;
    }

    public String getTokenAddress() {
        return tokenAddress;
    }

    public void setTokenAddress(String tokenAddress) {
        this.tokenAddress = tokenAddress;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateString() {
        return stateString;
    }

    public void setStateString(String stateString) {
        this.stateString = stateString;
    }

    public long getClosedBlock() {
        return closedBlock;
    }

    public void setClosedBlock(long closedBlock) {
        this.closedBlock = closedBlock;
    }

    public long getSettledBlock() {
        return settledBlock;
    }

    public void setSettledBlock(long settledBlock) {
        this.settledBlock = settledBlock;
    }

    public int getDelegateState() {
        return delegateState;
    }

    public void setDelegateState(int delegateState) {
        this.delegateState = delegateState;
    }

    public PhotonChannelVo parse(JSONObject object) {
        if (object == null) {
            return null;
        }
        setChannelIdentifier(object.optString("channel_identifier"));
        setPartnerAddress(object.optString("partner_address"));
        setOpenBlockNumber(object.optString("open_block_number"));
        setTokenAddress(object.optString("token_address"));
        setState(object.optInt("state"));
        setStateString(object.optString("state_string"));
        setSettleTimeout(object.optInt("settle_timeout"));
        setRevealTimeout(object.optInt("reveal_timeout"));
        setClosedBlock(object.optLong("closed_block"));
        setDelegateState(object.optInt("delegate_state"));
        setSettledBlock(object.optLong("block_number_channel_can_settle",-1));
        setCurrentBlock(object.optLong("block_number_now",-1));
        BigDecimal ether = Convert.Unit.ETHER.getWeiFactor();
        try {
            if (!TextUtils.isEmpty(object.optString("balance"))){
                String balance = new BigDecimal(object.optString("balance"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setBalance(balance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(object.optString("partner_balance"))){
                String partnerBalance = new BigDecimal(object.optString("partner_balance"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setPartnerBalance(partnerBalance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(object.optString("locked_amount"))){
                String lockedAmount = new BigDecimal(object.optString("locked_amount"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setLockedAmount(lockedAmount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (!TextUtils.isEmpty(object.optString("partner_locked_amount"))){
                String partnerLockedAmount = new BigDecimal(object.optString("partner_locked_amount"))
                        .divide(ether, 5, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
                setPartnerLockedAmount(partnerLockedAmount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

}
