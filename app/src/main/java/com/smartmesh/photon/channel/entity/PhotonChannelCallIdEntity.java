package com.smartmesh.photon.channel.entity;

import org.json.JSONObject;

import java.io.Serializable;

public class PhotonChannelCallIdEntity implements Serializable {
    private String callId;
    //status 1 表示成功,ErrorMessage一定为空,2 表示失败,如果Status为2,那么ErrorMessage一定不空
    private int status;
    private String errorMessage;
    //其中channel字段就是通道结构体,除了settle调用成功的时候channel为空,其他时候channel一定有有效的值
    private PhotonChannelVo photonChannelVo;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public PhotonChannelVo getPhotonChannelVo() {
        return photonChannelVo;
    }

    public void setPhotonChannelVo(PhotonChannelVo photonChannelVo) {
        this.photonChannelVo = photonChannelVo;
    }

    public PhotonChannelCallIdEntity parse(JSONObject object) {
        try {
            if (object == null) {
                return null;
            }
            setCallId(object.optString("call_id"));
            setStatus(object.optInt("status"));
            setErrorMessage(object.optString("error_message"));
            JSONObject channelObject = object.optJSONObject("channel");
            if (channelObject != null) {
                PhotonChannelVo photonChannelVo = new PhotonChannelVo();
                setPhotonChannelVo(photonChannelVo.parse(channelObject));
            }
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
