package com.smartmesh.photon.channel.util;

public class PhotonConnectStatus {

    /**
     * 0 	inValid 	通道不存在
     * 1 	opened 	通道打开状态，可以正常转账交易,可以CloseChannel,Withdraw和Deposit,不能SettleChannel
     * 2 	closed 	通道关闭状态，不能再发起交易，还可以接受交易; 等到当前块数(获取当前块数)大于SettledBlock后可以SettleChannel,不能CloseChannel,Withdraw和Deposit
     * 3 	settled 	通道结算状态，结算后通道内资金将返还节点链上帐户，通道将不存在,从ChannelList中消失
     * 4 	closing 	用户发起了关闭通道的请求,正在处理过程中，此时正在进行交易的可以继续,不能新开交易;不能 SettleChannel,CloseChannel,Withdraw和Deposit
     * 5 	settling 	用户发起了结算请求,正在处理，正常情况下此时不应该还有未完成交易。在这种状态下不能新开交易,正在提交到链上，还没被成功打包;不能 SettleChannel,CloseChannel,Withdraw和Deposit
     * 6 	withdrawing 	用户收到或者发出了 withdraw 请求后,如恰在此时收到的交易请求，则该交易请求只能立即放弃。不能 SettleChannel,Withdraw和Deposit,可以强制关闭通道(CloseChannel)
     * 7 	cooperativeSettling 	本地或者通道另一方收到来自用户关闭通道请求(CloseChannel 中force为false),如恰在此刻收到对方节点的交易请求，则该交易只能立即放弃; 不能SettleChannel,Withdraw和Deposit,可以强制关闭通道(CloseChannel,force为true)
     * 8 	prepareForCooperativeSettle 	用户向photon发起合作关闭通道请求,因为仍存在交易，不能合作关闭通道。但用户如果仍想合作关闭通道，可以等待交易彻底结束后再调用合作关闭通道。为防止在等待期间产生新交易，设置'prepareForCooperativeSettle'作为标记，不再接受新的交易,等待当前交易完成,再使用合作关闭通道方式结算通道。不能SettleChannel,Withdraw和Deposit,可以强制关闭通道(CloseChannel,force为true)
     * 9 	prepareForWithdraw 	用户向photon发起 withdraw请求, 但是目前还持有锁,不能合作取钱。但用户仍想取钱，可以等待交易解锁后再调用合作取钱功能。为防止在等待期间产生新交易,设置'prepareForWithdraw'作为标记，不再接受新的交易。等待当前交易解锁后再进行 withdraw,进行合作取钱。不能SettleChannel,Withdraw和Deposit,但是可以强制关闭通道(CloseChannel,force为true)
     * 10 	unkown 	StateError
     * */

    /**
     * 0 inValid 通道不存在
     * */
    public static int StateInValid = 0;

    /**
     * 1 opened 通道打开状态
     * 可以正常转账交易,可以CloseChannel,Withdraw和Deposit
     * 不能SettleChannel
     * */
    public static int StateOpened = 1;

    /**
     * 2 closed 通道关闭状态，不能再发起交易，还可以接受交易;
     * 等到当前块数(获取当前块数)大于SettledBlock后可以SettleChannel
     * 不能CloseChannel,Withdraw和Deposit
     * */
    public static int StateClosed = 2;

    /**
     * 3 settled 通道结算状态
     * 结算后通道内资金将返还节点链上帐户，通道将不存在,从ChannelList中消失
     * */
    public static int StateSettled = 3;

    /**
     * 4 closing 用户发起了关闭通道的请求,正在处理过程中
     * 此时正在进行交易的可以继续,不能新开交易;
     * 不能 SettleChannel,CloseChannel,Withdraw和Deposit
     * */
    public static int StateClosing = 4;

    /**
     * 5 settling 用户发起了结算请求,正在处理，
     * 正常情况下此时不应该还有未完成交易。
     * 在这种状态下不能新开交易,正在提交到链上，还没被成功打包;
     * 不能 SettleChannel,CloseChannel,Withdraw和Deposit
     * */
    public static int StateSettling = 5;

    /**
     *  6 withdrawing 用户收到或者发出了 withdraw 请求后
     *  如恰在此时收到的交易请求，则该交易请求只能立即放弃。
     *  不能 SettleChannel,Withdraw和Deposit,可以强制关闭通道(CloseChannel)
     * */
    public static int StateWithdraw = 6;

    /**
     * 7 cooperativeSettling 本地或者通道另一方收到来自用户关闭通道请求(CloseChannel 中force为false)
     * 如恰在此刻收到对方节点的交易请求，则该交易只能立即放弃;
     * 不能SettleChannel,Withdraw和Deposit,可以强制关闭通道(CloseChannel,force为true)
     * */
    public static int StateCooperativeSettle = 7;

    /**
     * 8 prepareForCooperativeSettle 用户向photon发起合作关闭通道请求
     * 因为仍存在交易，不能合作关闭通道。
     * 用户如果仍想合作关闭通道，可以等待交易彻底结束后再调用合作关闭通道。
     * 为防止在等待期间产生新交易，设置'prepareForCooperativeSettle'作为标记，不再接受新的交易,等待当前交易完成,再使用合作关闭通道方式结算通道。
     * 不能SettleChannel,Withdraw和Deposit,可以强制关闭通道(CloseChannel,force为true)
     * */
    public static int StatePrepareForCooperativeSettle = 8;

    /**
     * 9 prepareForWithdraw 用户向photon发起 withdraw请求
     * 但是目前还持有锁,不能合作取钱。
     * 用户如果仍想取钱，可以等待交易解锁后再调用合作取钱功能。
     * 为防止在等待期间产生新交易,设置'prepareForWithdraw'作为标记，不再接受新的交易。等待当前交易解锁后再进行 withdraw,进行合作取钱。
     * 不能SettleChannel,Withdraw和Deposit,但是可以强制关闭通道(CloseChannel,force为true)
     * */
    public static int StatePrepareForWithdraw = 9;

    /**
     * 10 unkown StateError
     * */
    public static int StateError = 10;

    /**
     * 11 StatePartnerCooperativeSettling 用户收到对方发来的CooperativeSettle请求并同意后,将通道置为该状态,效果同cooperativeSetting状态
     * */
    public static int StatePartnerCooperativeSettling = 11;

    /**
     * 12 StatePartnerWithdrawing 用户收到的了对方发来的withdraw请求并同意后,将通道置为该状态,效果同withdrawing状态
     * */
    public static int StatePartnerWithdrawing = 12;
}
