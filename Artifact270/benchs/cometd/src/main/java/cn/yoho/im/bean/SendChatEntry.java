package cn.yoho.im.bean;

import java.util.ArrayList;
import java.util.HashMap;

public class SendChatEntry extends HashMap<String, Object> {

    private static final long serialVersionUID = -8583862688778060170L;

    String sender = "sender";
    String channel = "channel";
    String receiverList = "receiverList";
    String data = "data";
    // 对发送者来说没用
    String isRead = "isRead";

    public Object getSender() {
        return this.get(sender);
    }

    public void setSender(Object sender) {
        this.put(this.sender, sender);
    }

    public String getChannel() {
        return (String) this.get(channel);
    }

    public void setChannel(Object channel) {
        this.put(this.channel, channel);
    }

    public ArrayList<String> getReceiverList() {
        return (ArrayList<String>) this.get(receiverList);
    }

    public void setReceiverList(Object receiverList) {
        this.put(this.receiverList, receiverList);
    }

    public String getData() {
        return (String) this.get(data);
    }

    public void setData(Object data) {
        this.put(this.data, data);
    }

    public boolean isRead() {
        return (Boolean) this.get(isRead);
    }

    public void setRead(Object isRead) {
        this.put(this.isRead, isRead);
    }

}
