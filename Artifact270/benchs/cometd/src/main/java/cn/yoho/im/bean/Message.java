package cn.yoho.im.bean;

import java.util.HashMap;

public class Message extends HashMap<String, Object> {

    /**
   * 
   */
    private static final long serialVersionUID = -1776789725209822511L;

    private String chat = "chat";

    private String user = "user";

    private String scope = "scope";

    private String group = "group";

    private String time = "time";

    public String getChat() {
        return (String) this.get(chat);
    }

    public void setChat(Object c) {
        this.put(chat, c);
    }

    public Object getUser() {
        return this.get(user);
    }

    public void setUser(Object u) {
        this.put(user, u);
    }

    public Object getScope() {
        return this.get(scope);
    }

    public void setScope(Object s) {
        this.put(scope, s);
    }

    public String getGroup() {
        return (String) this.get(group);
    }

    public void setGroup(Object s) {
        this.put(group, s);
    }

    public String getTime() {
        return (String) this.get(time);
    }

    public void setTime(String t) {
        this.put(time, t);
    }

}
