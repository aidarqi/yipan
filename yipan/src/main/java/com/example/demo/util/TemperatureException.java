package com.example.demo.util;

public class TemperatureException extends Exception{
    public String msgContent;
    public int msgCode;

    public TemperatureException(){
        super();
    }

    public TemperatureException(Msg msg){
        this.msgContent = msg.getMsgContent();
        this.msgCode = msg.getMsgCode();
    }

}
