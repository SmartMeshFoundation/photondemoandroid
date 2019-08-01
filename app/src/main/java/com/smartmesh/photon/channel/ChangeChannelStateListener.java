package com.smartmesh.photon.channel;

/**
 * Created on 2018/2/15.
 * change channel state listener
 */

public interface ChangeChannelStateListener {

    /**
     * change channel
     * @param position  list position
     * @param isForced    true 强制关闭 、false 合同关闭
     */
    void changeChannel(int position, boolean isForced);

    /**
     * deposit channel
     * @param position list position
     * */
    void depositChannel(int position);

    /**
     * transfer channel
     * @param position list position
     * */
    void withdrawChannel(int position);

    /**
     * settle channel
     * @param position list position
     * */
    void settleChannel(int position);

    /**
     * pms提示相关
     * @param delegateState list position
     * pms   表示委托状态的数字
     * 0 不需要委托
     * 1.正在等待委托到pms
     * 2.委托成功
     * 3.委托失败
     * 4.委托失败并无有效公链
     * */
    void pmsChannel(int delegateState);


    /**
     * 隐藏或者取消
     * @param position list position
     * */
    void hiddenBottom(int position);

}
