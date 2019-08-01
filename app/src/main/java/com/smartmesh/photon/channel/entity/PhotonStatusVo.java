package com.smartmesh.photon.channel.entity;

import org.json.JSONObject;

public class PhotonStatusVo {

    /**
     * XMPPStatus : 0
     * EthStatus : 3
     * LastBlockTime : 09-05|07:49:11.878
     */

    private int XmppStatus;
    private int EthStatus;
    private String LastBlockTime;

    public int getXmppStatus() {
        return XmppStatus;
    }

    public void setXmppStatus(int xmppStatus) {
        XmppStatus = xmppStatus;
    }

    public int getEthStatus() {
        return EthStatus;
    }

    public void setEthStatus(int EthStatus) {
        this.EthStatus = EthStatus;
    }

    public String getLastBlockTime() {
        return LastBlockTime;
    }

    public void setLastBlockTime(String LastBlockTime) {
        this.LastBlockTime = LastBlockTime;
    }

    public PhotonStatusVo parse(JSONObject obj) {
        setEthStatus(obj.optInt("eth_status"));
        setXmppStatus(obj.optInt("xmpp_status"));
        setLastBlockTime(obj.optString("last_block_time"));
        return this;
    }
}
