package com.smartmesh.photon.channel.entity;

import java.io.Serializable;

public class PhotonPayEntity implements Serializable {

    //接收方地址
    private String toAddress;
    //转账金额
    private String total;
    //转账携带参数
    private String data;

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
