package com.smartmesh.photon.channel.entity;

public enum TxStatus {
    //查询pending状态的tx
    pending,
    //查询执行成功的tx
    success,
    //查询执行失败的tx
    failed;
}
