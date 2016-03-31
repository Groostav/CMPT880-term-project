package cn.yoho.im.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import cn.yoho.im.bean.SendChatEntry;
import cn.yoho.im.bean.Message;
import cn.yoho.im.data.Utility;

/**
 * �����û����ߵ����
 * 
 * @author ice.deng
 * 
 */
public class UserOnline {
    /**
     * 
     * @param userId
     *            �û�ID
     */
    public void update(int userId) throws Exception {
        TTServer db = TTServerPool.getConnection();
        try {
            removeUserInTimestamp(userId, db);
            String key = addUserInTimestamp(userId, db);
            System.out.println(key);
            updateUserInfo(userId, key, db);
        } catch (Exception e) {
            throw e;
        } finally {
            TTServerPool.setConnection(db);
        }
    }

    /**
     * �ѵ�ǰ�û�����һ��ʱ������Ƴ�
     * 
     * @param userId
     * @param db
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void removeUserInTimestamp(int userId, TTServer db)
            throws Exception {
        JSONParser parser = new JSONParser();
        // ɾ��ǰ�û�����һ��ʱ��w����µ����
        String userInfo = db.get("chat_info_" + userId);
        if (userInfo == null) {
            return;
        }
        try {
            Object jsonObj = parser.parse(userInfo);
            if (jsonObj == null) {
                return;
            }
            HashMap<String, String> mInfo = (HashMap<String, String>) jsonObj;
            String timestamp = mInfo.get("Timestamp").toString();
            if (timestamp == null) {
                return;
            }
            // �Ӹ�ʱ������Ƴ��ID
            String ts = db.get(timestamp);
            if (ts == null) {
                return;
            }
            jsonObj = parser.parse(ts);
            if (jsonObj != null) {
                JSONArray array = (JSONArray) jsonObj;
                array.remove(userId);
                db.put(timestamp, array.toJSONString());
            }

        } catch (Exception ex) {
        }
    }

    /**
     * ��¼��ǰ�û��ڵ�ǰʱ�����
     * 
     * @param userId
     * @param db
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String addUserInTimestamp(int userId, TTServer db) throws Exception {
        JSONParser parser = new JSONParser();
        // ��ǰʱ���
        String timestampNow = Utility.getTimestamp();
        // ��ǰ�û��ķ���
        int region = Utility.getUserRegion(userId);
        String key = "Chat_onlineUsers_" + timestampNow + region;
        String value = "";
        String nowTSInfo = db.get(key);
        if (nowTSInfo != null) {
            try {
                Object jsonObj = parser.parse(nowTSInfo);
                if (jsonObj != null) {
                    JSONArray array = (JSONArray) jsonObj;
                    if (array.indexOf(userId) == -1) {
                        array.add(userId);
                    }
                    value = array.toJSONString();
                } else {
                    value = "[" + userId + "]";
                }
            } catch (Exception ex) {
            }
        } else {
            value = "[" + userId + "]";
        }
        db.put(key, value);
        return key;
    }

    /**
     * �����û�������ʱ��z�����״̬
     * 
     * @param userId
     * @param tsKey
     * @param db
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void updateUserInfo(int userId, String tsKey, TTServer db)
            throws Exception {
        JSONParser parser = new JSONParser();
        String key = "chat_info_" + userId;
        String value = "{\"Online\":\"1\",\"Timestamp\":\"" + tsKey
                + "\",\"Info\":[]}";
        String userInfo = db.get(key);
        if (userInfo != null) {
            Object jsonObj = parser.parse(userInfo);
            if (jsonObj != null) {
                HashMap<String, String> mInfo = (HashMap<String, String>) jsonObj;
                mInfo.put("Timestamp", tsKey);
                mInfo.put("Online", "1");
                value = JSONValue.toJSONString(mInfo);
            }
        }
        db.put(key, value);
    }

    /**
     * ��ȡ�û��Ƿ�����
     * 
     * @param userId
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean get(int userId) {
        boolean online = false;
        JSONParser parser = new JSONParser();
        TTServer db = TTServerPool.getConnection();
        try {
            String key = "chat_info_" + userId;
            String userInfo = db.get(key);
            if (userInfo != null) {
                Object jsonObj = parser.parse(userInfo);
                if (jsonObj != null) {
                    HashMap<String, String> mInfo = (HashMap<String, String>) jsonObj;
                    String str = mInfo.get("Online").toString();
                    if (str.equals("1")) {
                        online = true;
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            TTServerPool.setConnection(db);
        }
        return online;
    }

    /**
     * �����û�����
     * 
     * @param userId
     */
    @SuppressWarnings("unchecked")
    public void offline(int userId) {
        JSONParser parser = new JSONParser();
        TTServer db = TTServerPool.getConnection();
        try {
            String key = "chat_info_" + userId;
            String userInfo = db.get(key);
            if (userInfo != null) {
                Object jsonObj = parser.parse(userInfo);
                if (jsonObj != null) {
                    HashMap<String, String> mInfo = (HashMap<String, String>) jsonObj;
                    mInfo.put("Online", "0");
                    db.put(key, JSONValue.toJSONString(mInfo));
                }
            }
        } catch (Exception e) {
        } finally {
            TTServerPool.setConnection(db);
        }
    }

    public static void main(String[] args) throws Exception {
        TTServerPool.setHostname("yoho.xicp.cn");
        TTServerPool.setPort(11211);
        TTServerPool.initialize();

        UserOnline uo = new UserOnline();
        uo.update(123);
        System.out.println(uo.get(123));
        System.out.println(uo.get(1234));
        uo.offline(123);

        System.out.println("End");
        ArrayList array = new ArrayList<String>();
        array.add("gy");
        array.add("sdsda");
        SendChatEntry chatEntry = new SendChatEntry();
        chatEntry.setChannel("channel");
        chatEntry.setRead(Boolean.TRUE);
        chatEntry.setReceiverList(array);
        String value = JSONValue.toJSONString(chatEntry);

        System.out.println(value);

        JSONParser parser = new JSONParser();
        HashMap jsonObj = (HashMap) parser.parse(value);
        System.out.println(jsonObj.get("channel"));
        System.out.println(jsonObj.get("receiverList"));
        System.out.println(jsonObj.get("isRead"));

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar date = Calendar.getInstance();
        String key = "chat_send_" + df.format(date.getTime());
        System.out.println(key);
        /*
         * String json = "{\"online\":1,\"timestamp\":\"ddd\"}"; JSONParser
         * parser = new JSONParser(); Map j = (Map) parser.parse(json);
         * System.out.println(j.get("online"));
         * System.out.println(j.get("timestamp"));
         * 
         * System.out.println(JSONValue.toJSONString(j));
         * 
         * Calendar date = Calendar.getInstance();
         * System.out.println(date.get(Calendar.SECOND));
         * 
         * System.out.println(Utility.getTimestamp());
         */
        /*
         * TTServerPool.setHostname("yoho.xicp.cn");
         * TTServerPool.setPort(11211); TTServerPool.initialize(); TTServer db =
         * TTServerPool.getConnection(); db.put("test__", "ɾ�����...");
         * 
         * 
         * System.out.println("���������س�"); System.in.read(); db.put("test__",
         * "�Ҳ��ԵĶ������"); System.out.println(db.get("test__"));
         * 
         * 
         * System.out.println(TTServerPool.getConnectionTotal());
         * 
         * System.out.println(TTServerPool.getFreeConnectionTotal());
         * 
         * TTServerPool.setConnection(db);
         * 
         * System.out.println(TTServerPool.getConnectionTotal());
         * 
         * System.out.println(TTServerPool.getFreeConnectionTotal());
         * 
         * System.out.println("end");
         */
    }
}
