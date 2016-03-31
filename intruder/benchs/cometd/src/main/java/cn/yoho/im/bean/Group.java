/**
 * 
 */
package cn.yoho.im.bean;

import java.util.HashMap;

/**
 * @author Hbomb <h1>群组</h1>
 */
public class Group extends HashMap<String, Object> {
    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    private String id = "id";

    private String name = "name";
    
    private String description = "description";

    private String state = "state";

    public Group(String d, String n) {
        this.put(id, d);
        this.put(name, n);
    }

    public void setId(String d) {
        this.put(id, d);
    }

    public String getId() {
        return (String) this.get(id);
    }
    
    public void setDescription(String d) {
        this.put(description, d);
    }

    public String getDescription() {
        return (String) this.get(description);
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
