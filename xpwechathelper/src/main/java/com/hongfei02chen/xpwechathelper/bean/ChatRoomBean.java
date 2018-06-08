package com.hongfei02chen.xpwechathelper.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * created by chenhongfei on 2018/6/6
 */
@Entity
public class ChatRoomBean {
    @Id(autoincrement = true)
    private Long id;
    @NotNull
    @Unique
    @Property(nameInDb = "chat_room")
    private String chatRoom;
    @Property(nameInDb = "room_name")
    private String roomName;
    @Property(nameInDb = "state")
    private int state;
    @Generated(hash = 2003406892)
    public ChatRoomBean(Long id, @NotNull String chatRoom, String roomName,
            int state) {
        this.id = id;
        this.chatRoom = chatRoom;
        this.roomName = roomName;
        this.state = state;
    }
    @Generated(hash = 2079157416)
    public ChatRoomBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getChatRoom() {
        return this.chatRoom;
    }
    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }
    public String getRoomName() {
        return this.roomName;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public int getState() {
        return this.state;
    }
    public void setState(int state) {
        this.state = state;
    }

}
