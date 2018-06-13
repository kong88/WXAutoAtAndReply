package com.hongfei02chen.xpwechathelper;

/**
 * 发送消息流程的状态机
 *
 * created by chenhongfei on 2018/6/12
 */
public  class SendState {

    public enum  S {
        IDLE,// 空闲状态
        PASTE_AT,// 粘贴@
        SELECT_AT_NICKNAME,// 弹出nickname列表，选择nickname
        CLICK_SEND, // 点击发送
        PASTE_CONTENT,//粘贴内容
        CLICK_SEND_2,// 再次点击发送
        END,// 一次发送流程结束
    }
}
