package com.smartmesh.photon.channel.entity;

public enum TxStatus {
    //查询pending状态的tx    Query the tx of the pending state
    pending,
    //查询执行成功的tx       Query successful tx
    success,
    //查询执行失败的tx       Query failed tx
    failed;
}
