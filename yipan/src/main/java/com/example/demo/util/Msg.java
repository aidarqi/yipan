package com.example.demo.util;

public enum Msg {
    NO_PROVINCE("province does not exist", -1),
    NO_CITY("city does not exist", -2),
    NO_COUNTY("county does not exist", -3);

    private String msgContent;
    private int msgCode;

    private Msg(String msgContent, int msgCode) {
        this.msgContent = msgContent;
        this.msgCode = msgCode;
    }

    public int getMsgCode() {
        return msgCode;
    }
    public String getMsgContent() {
        return msgContent;
    }
}
