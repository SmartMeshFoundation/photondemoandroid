package com.smartmesh.photon.channel.entity;

public enum TxTypeStr {
    //deposit
    ChannelDeposit,
    //close
    ChannelClose,
    //settle
    ChannelSettle,
    //settle
    CooperateSettle,
    UpdateBalanceProof,
    Unlock,
    Punish,
    //withdraw
    Withdraw,
    //approve deposit
    ApproveDeposit,
    RegisterSecret;
}
