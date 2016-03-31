package cn.yoho.im.data;

import java.net.InetSocketAddress;

import tokyotyrant.RDB;

/**
 * ����TTServer����
 * 
 * @author ice.deng
 * 
 */
public class TTServer {
    private String hostname;
    private int port;
    private RDB db;

    /**
     * @param hostname
     *            the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    public TTServer() {
    }

    public TTServer(String _hostname, int _port) throws Exception {
        hostname = _hostname;
        port = _port;
        open();
    }

    /**
     * ����l��
     * 
     * @throws Exception
     */
    public void open() throws Exception {
        db = new RDB();
        db.open(new InetSocketAddress(hostname, port));
    }

    /**
     * �ر�l��
     */
    public void close() {
        db.close();
    }

    /**
     * д����ݣ������ھ���д
     * 
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public boolean put(String key, String value) throws Exception {
        return db.put(key, value);
    }

    /**
     * ������
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public boolean remove(String key) throws Exception {
        return db.out(key);
    }

    /**
     * ��ȡ���
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public String get(String key) throws Exception {
        Object value = db.get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }
}
