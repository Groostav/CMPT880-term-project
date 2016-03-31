package cn.yoho.im.bean;

import java.util.HashMap;

/**
 * 用户信息详情
 * 
 * @author Hbomb
 * 
 */
public class UserDetail extends User {

    /**
   * 
   */
    private static final long serialVersionUID = 8658710563268501075L;

    private String email = "email";

    private String friendList = "friendList";

    private String leaveMsgs = "leaveMsg";

    private String config = "config";

    public UserDetail(String d, String n) {
        super(d, n);
    }

    /**
     * 设置邮箱地址
     * 
     * @param n
     */
    public void setEmail(String n) {
        this.put(email, n);
    }

    /**
     * 获取邮箱地址
     * 
     * @return
     */
    public String getEmail() {
        return (String) this.get(email);
    }

    /**
     * 设置用户列表
     * 
     * @param f
     */
    public void setFriendList(HashMap<String, Object> f) {
        this.put(friendList, f);
    }

    /**
     * 获取用户列表
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getFriendList() {
        return (HashMap<String, Object>) this.get(friendList);
    }

    /**
     * 设置离线信息
     * 
     * @param m
     */
    public void setLeaveMsgs(HashMap<String, Object> m) {
        this.put(leaveMsgs, m);
    }

    /**
     * 获取离线信息
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getLeaveMsgs() {
        return (HashMap<String, Object>) this.get(leaveMsgs);
    }

    /**
     * 设置参数
     * 
     * @param m
     */
    public void setConfig(HashMap<String, String> m) {
        this.put(config, m);
    }

    /**
     * 获取参数
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, String> getConfig() {
        return (HashMap<String, String>) this.get(config);
    }
}
