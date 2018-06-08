package com.hongfei02chen.xpwechathelper.eventbus;

/**
 * created by chenhongfei on 2018/5/21
 */
public class MessageEvent {

    private String message;
    private boolean hook;

    public MessageEvent(String message, boolean h){
        this.message = message;
        this.hook = h;
    }

    public String getMessage(){
        return message;
    }

    public boolean isHook() {
        return this.hook;
    }
}

