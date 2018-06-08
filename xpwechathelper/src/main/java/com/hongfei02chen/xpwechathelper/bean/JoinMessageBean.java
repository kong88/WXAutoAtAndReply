package com.hongfei02chen.xpwechathelper.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/6
 */
@Entity
public class JoinMessageBean {
    @Id
    private Long id;
    @Unique
    @Property(nameInDb = "msg_id")
    private int msgId;
    @Property(nameInDb = "template")
    private String template;
    @NotNull
    @Property(nameInDb = "nickname")
    private String nickname;
    @Property(nameInDb = "username")
    private String username;
    @Property(nameInDb = "invite_nickname")
    private String inviteNickname;
    @Property(nameInDb = "invite_username")
    private String inviteUsername;
    @Property(nameInDb = "chatRoom")
    private String chatRoom;
    @Property(nameInDb = "create_time")
    private Long createTime;
    @Property(nameInDb = "state")
    private int state;
    private int flag;

    @Generated(hash = 1621428079)
    public JoinMessageBean(Long id, int msgId, String template, @NotNull String nickname, String username,
            String inviteNickname, String inviteUsername, String chatRoom, Long createTime, int state, int flag) {
        this.id = id;
        this.msgId = msgId;
        this.template = template;
        this.nickname = nickname;
        this.username = username;
        this.inviteNickname = inviteNickname;
        this.inviteUsername = inviteUsername;
        this.chatRoom = chatRoom;
        this.createTime = createTime;
        this.state = state;
        this.flag = flag;
    }

    @Generated(hash = 1816993513)
    public JoinMessageBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getInviteNickname() {
        return this.inviteNickname;
    }

    public void setInviteNickname(String inviteNickname) {
        this.inviteNickname = inviteNickname;
    }

    public String getChatRoom() {
        return this.chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInviteUsername() {
        return this.inviteUsername;
    }

    public void setInviteUsername(String inviteUsername) {
        this.inviteUsername = inviteUsername;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("nickname:").append(nickname).append(" chatRoom:").append(chatRoom).append(" state:").append(state);

        return sb.toString();
    }

    @Transient
    private Gson mGson = new Gson();

    public List<String> getNicknameList() {
        if (TextUtils.isEmpty(this.nickname)) {
            return null;
        }
        List<String> list = null;
        try {
            list = mGson.fromJson(this.nickname, List.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getMsgId() {
        return this.msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
