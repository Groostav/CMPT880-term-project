/**
 * 
 */
package cn.yoho.im.bean;

import java.util.HashMap;

/**
 * @author Hbomb <h1>用户列表对象</h1>
 */
public class User extends HashMap<String, Object> {
    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    private String id = "id";

    private String name = "name";

    private String state = "state";

    private String groupUser = "groupUser";
    
    public Boolean getGroupUser() {
        return (Boolean) this.get(groupUser);
    }

    public void setGroupUser(Boolean groupUser) {
        this.put(this.groupUser, groupUser);
    }

    public User(String d, String n) {
        this.put(id, d);
        this.put(name, n);
    }

    public void setId(String d) {
        this.put(id, d);
    }

    public String getId() {
        return (String) this.get(id);
    }

    public void setName(String n) {
        this.put(name, n);
    }

    public String getName() {
        return (String) this.get(name);
    }

    public void setState(String s) {
        this.put(state, s);
    }

    public String getState() {
        return (String) this.get(state);
    }
}
