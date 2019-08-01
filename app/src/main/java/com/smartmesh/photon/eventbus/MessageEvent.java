package com.smartmesh.photon.eventbus;

public class MessageEvent {

    private Object object;

    /**
     * 传递信息
     * */
    private String message;


    /**
     * 传递信息
     * */
    private String message2;

    /**
     * 金额
     * */
    private String amount;

    /**
     * 类型
     * */
    private int code;

    /**
     * 位置
     * */
    private int position;

    /**
     * 错误码
     * */
    private int errorCode;

    /**
     * 是否是强制关闭
     * 是否是直接转账
     * */
    private boolean booleanType;

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * 初始化code码
     * */
    public MessageEvent(int code){
        this.code = code;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isBooleanType() {
        return booleanType;
    }

    public void setBooleanType(boolean booleanType) {
        this.booleanType = booleanType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
