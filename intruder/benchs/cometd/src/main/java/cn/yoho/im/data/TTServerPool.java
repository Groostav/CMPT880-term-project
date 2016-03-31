/**
 * 
 */
package cn.yoho.im.data;

import java.util.LinkedList;

/**
 * TTServerl�ӳ�
 * 
 * @author ice.deng
 * 
 */
public class TTServerPool {
    private static LinkedList<TTServer> freeConnections = new LinkedList<TTServer>();
    private static LinkedList<TTServer> connections = new LinkedList<TTServer>();
    private static int initConnections = 5;
    private static int maxConnections = 0;
    private static String hostname;
    private static int port;
    private static boolean isInit = false;

    /**
     * ���ó�ʼ����b��l����
     * 
     * @param initConnections
     *            the initConnections to set
     */
    public static void setInitConnections(int _initConnections) {
        initConnections = _initConnections;
    }

    /**
     * �������l����0Ϊ������
     * 
     * @param maxConnections
     *            the maxConnections to set
     */
    public static void setMaxConnections(int _maxConnections) {
        maxConnections = _maxConnections;
    }

    /**
     * ����TTServer�������ַ
     * 
     * @param hostname
     *            the hostname to set
     */
    public static void setHostname(String _hostname) {
        hostname = _hostname;
    }

    /**
     * ����TTServer�˿�
     * 
     * @param port
     *            the port to set
     */
    public static void setPort(int _port) {
        port = _port;
    }

    /**
     * ��ʼ��l�ӳ�
     * 
     * @return
     */
    public static boolean initialize() {
        if (isInit) {
            return true;
        }
        if (hostname == null || port < 1) {
            return false;
        }
        try {
            for (int i = 0; i < initConnections; i++) {
                create();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * ����TTServer��l��
     * 
     * @throws Exception
     */
    private synchronized static void create() throws Exception {
        TTServer db = new TTServer(hostname, port);
        db.open();
        freeConnections.add(db);
        connections.add(db);
    }

    /**
     * ��ȡһ����õ�l��
     * 
     * @return
     */
    public synchronized static TTServer getConnection() {
        if (freeConnections.size() < 1) {
            if (connections.size() > maxConnections && maxConnections != 0) {
                return null;
            } else {
                try {
                    create();
                } catch (Exception e) {
                    return null;
                }
                return getConnection();
            }
        } else {
            return freeConnections.getFirst();
        }
    }

    /**
     * ����һ���õ�l��
     * 
     * @param tts
     */
    public synchronized static void setConnection(TTServer tts) {
        freeConnections.add(tts);
    }

    /**
     * ��ȡl�ӳ��е�l����
     * 
     * @return
     */
    public static int getConnectionTotal() {
        return connections.size();
    }

    /**
     * ��ȡl�ӳ��п��е�l����
     * 
     * @return
     */
    public static int getFreeConnectionTotal() {
        return freeConnections.size();
    }

    /**
     * �ͷ�ȫ��t��
     */
    public synchronized static void release() {
        while (connections.size() > 0) {
            connections.poll().close();
        }
        isInit = false;
    }
}
