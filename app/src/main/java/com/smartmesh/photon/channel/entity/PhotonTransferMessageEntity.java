package com.smartmesh.photon.channel.entity;

public class PhotonTransferMessageEntity {

    /**
     * lock_secret_hash : Transfers接口返回中的lockSecretHash
     * token_address : 交易token
     * 0 - Transfer init
     * 1 - transfer can cancel
     * 2 - transfer can not cancel
     * 3 - transfer already success
     * 4 - transfer cancel by user request
     * 5 - transfer already failed
     * status_message : UnLock 发送成功,交易成功.
     */

    /**
     * lock_secret_hash : 0xc7631e4763d9b112b2ee88754ed9a63cfd9e7cc7e6ff0e560385c06f7a27fbd4
     * token_address : 0xf0123c3267af5cbbfab985d39171f5f5758c0900
     * status : 2
     * status_message : RevealSecret 正在发送 target=70ae
     */

    private String lock_secret_hash;
    private String token_address;
    private int status;
    private String status_message;

    public String getLock_secret_hash() {
        return lock_secret_hash;
    }

    public void setLock_secret_hash(String lock_secret_hash) {
        this.lock_secret_hash = lock_secret_hash;
    }

    public String getToken_address() {
        return token_address;
    }

    public void setToken_address(String token_address) {
        this.token_address = token_address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

}
