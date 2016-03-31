/**
 * 
 */
package cn.yoho.im.bean;

import java.util.HashMap;

/**
 * @author Hbomb <h1>参数设置</h1>
 */
public class Config extends HashMap<String, Object> {
    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    private String save = "save";

    private String face = "face";

    private String sound = "sound";

    private String add = "add";

    public Config(String s, String f, String so, String a) {
        this.put(save, s);
        this.put(face, f);
        this.put(sound, so);
        this.put(add, a);
    }

    public void setSave(String d) {
        this.put(save, d);
    }

    public String getSave() {
        return (String) this.get(save);
    }

    public void setFace(String n) {
        this.put(face, n);
    }

    public String getFace() {
        return (String) this.get(face);
    }

    public void setSound(String s) {
        this.put(sound, s);
    }

    public String getSound() {
        return (String) this.get(sound);
    }

    public void setAdd(String s) {
        this.put(add, s);
    }

    public String getadd() {
        return (String) this.get(add);
    }
}
