package cn.yoho.im.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.print.attribute.standard.Finishings;

import org.eclipse.jetty.util.ajax.JSONPojoConvertor;
import org.eclipse.jetty.util.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import tokyotyrant.RDB;

import cn.yoho.im.bean.Message;
import cn.yoho.im.data.TTServer;
import cn.yoho.im.data.TTServerPool;

public class MessageOperator implements Runnable {
    
    public static final String RECEIVE_UNREAD_MSG = "chat_unread";
    public static final String CHAT_TRACK = "chat_track";
    
    public MessageOperator() {
     // 初始化内存数据库
        TTServerPool.setHostname("yoho.xicp.cn");
        TTServerPool.setPort(11211);
        TTServerPool.initialize();
    }
    
    /**
     * 将一条消息保存进内存数据库中。所有消息均被保存进<code>CHAT_TRACK</code>前缀的key值下
     * <br/>
     * 如果<code>type</code>是<code>RECEIVE_UNREAD_MSG</code>类型，则同时将数据保存进<code>RECEIVE_UNREAD_MSG</code>
     * 前缀的key值下。并且将此key作为value加入<code>RECEIVE_UNREAD_MSG</code>_<code>sender</code>_<code>receiver</code>的key值下
     * 
     * @param type
     * @param sender
     * @param receiver
     * @param data
     */
    public void saveToDB(String type, String sender, String receiver, Message data) {

        String jsonValue = JSONValue.toJSONString(data);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar date = Calendar.getInstance();
        String dateSuffix = df.format(date.getTime());
        String key = CHAT_TRACK + "_" + sender + "_" + receiver + "_" + dateSuffix;

        System.out.println("**********key is: " + key);
        addValueToKey(jsonValue, key);
        
        // 如果是未读类型的消息，则将消息保存在以chat_unread_receiver_sender为前缀的key值下，并将key值保存在chat_unread_receiver的key值下
        if (RECEIVE_UNREAD_MSG.equals(type)) {
            String unreadKey = RECEIVE_UNREAD_MSG + "_" + receiver + "_" + sender;
            addValueToKey(jsonValue, unreadKey);
            
            String keyKey = RECEIVE_UNREAD_MSG + "_" + receiver;
            
            Log.info("add key: " + unreadKey + " to keyKey: " + keyKey);
            addUnreadKeyToKeyset(keyKey, unreadKey);
        }
    }

    private void addValueToKey(String jsonValue, String key) {
        
        String value = "";
        JSONParser parser = new JSONParser();
        TTServer db = null;
        try {

            db = TTServerPool.getConnection();
            String existedValue = db.get(key);
            if (existedValue == null) {
                value = "[" + jsonValue + "]";
            } else {
                Object jsonObj = parser.parse(existedValue);
                if (jsonObj != null) {
                    JSONArray array = (JSONArray) jsonObj;
                    array.add(parser.parse(jsonValue));
                    value = array.toJSONString();
                } else {
                    value = "[" + jsonValue + "]";
                }
            }

            db.put(key, value);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * 将来自相同的接收者和发送者的未读消息的key加入到一个统一的key值下以方便一次性更新
     * 
     * @param keyKey 未读消息的key的key。所有相同接收者和发送者的未读消息都被保存进这个key下
     * @param key 未读消息的key
     */
    private void addUnreadKeyToKeyset(String keyKey, String key) {
    	
    	if (keyKey == null || key == null || 
    			keyKey.trim().length() == 0 || key.trim().length() == 0) {
    		
    		Log.info("invalid key");
    		return;
    	}
    	
    	JSONParser parser = new JSONParser();
    	TTServer db = null;
    	try {
    		Set<String> keySet = new HashSet<String>();
    		
    		db = TTServerPool.getConnection();
			String keyJSONString = db.get(keyKey);
			
			if (keyJSONString == null || keyJSONString.trim().length() == 0) {
				keySet.add(key);
			} else {
				Object jsonObj = parser.parse(keyJSONString);
				
				if (jsonObj != null) {
					ArrayList<String> keyArray = (ArrayList<String>) jsonObj;
					keySet.addAll(keyArray);
					keySet.add(key);
				} else {
					Log.info("parse error! clean old and add new key");
					keySet.add(key);
				}
			}
			
			String jsonValue = JSONValue.toJSONString(new ArrayList(keySet));
			db.put(keyKey, jsonValue);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

    /**
     * 找到所有指定发送者和接收者的未读消息的key
     * @param sender
     * @param receiver
     * @return
     */
    private ArrayList<String> findAllUnreadKeys(String receiver) {
    	
    	if (receiver == null || receiver.trim().length() == 0) {
    		
    		Log.info("invalid sender or receiver");
    		return new ArrayList<String>();
    	}
    	
    	String keyKey = RECEIVE_UNREAD_MSG + "_" + receiver;
    	Log.info("keykey is: " + keyKey);
    	ArrayList<String> keyArray = new ArrayList<String>();
    	JSONParser parser = new JSONParser();
    	TTServer db = null;
    	try {
    		
    	    db = TTServerPool.getConnection();
    		String keyJSONString = db.get(keyKey);
    		Log.info("receiver is " + receiver + "keyJSONString is : " + keyJSONString + " keykey is " + keyKey);
    		if (keyJSONString == null || keyJSONString.trim().length() == 0) {
				// do nothing return empty array
			} else {
				Object jsonObj = parser.parse(keyJSONString);
				
				if (jsonObj != null) {
					keyArray = (ArrayList<String>) jsonObj;
					Log.info("receiver is " + receiver + "keyArray is : " + keyArray);
				} else {
					Log.info("parse error! no key found! return empty array");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return keyArray;
    }
    
    
    /**
     * 更新未读消息。首先将未读消息的key值从receiver的未读消息key列表中删除。然后再将消息删除
     * 
     * @param receiver
     * @param sender
     */
    public void updateUnreadMSG(String receiver, String sender) {

        if (receiver == null || receiver.trim().length() == 0 ||
        		sender == null || sender.trim().length() == 0) {

            Log.info("receiver or sender is invalid");
            return;
        }

        ArrayList<String> keyArray = findAllUnreadKeys(receiver);
        String key = RECEIVE_UNREAD_MSG + "_" + receiver + "_" + sender;
        String keyKey = RECEIVE_UNREAD_MSG + "_" + receiver;
        keyArray.remove(key);
        String jsonValue = JSONValue.toJSONString(keyArray);
        TTServer db = null;
        try {
            db = TTServerPool.getConnection();
        	// 删除key
			db.put(keyKey, jsonValue);
			// 删除消息
			db.remove(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * 根据接收者和（或）发送者获取所有未读消息。
     * 如果没有发送者则获取所有接收者接受到的消息。如果有发送者则只返回该发送者与接收者之间的未读消息
     * 
     * @param receiver
     * @param sender
     * @return
     */
    public ArrayList<Message> getAllUnreadMessage(String receiver, String sender, int maxMSG) {
    	
    	ArrayList<Message> messages = new ArrayList<Message>();
    	
    	if (maxMSG == 0) {
            // 如果将最大返回消息数设置为0， 则最大返回2147483646条
            maxMSG = Integer.MAX_VALUE - 1;
        }
    	
    	if (receiver == null || receiver.trim().length() == 0) {
    		Log.info("invalid receiver");
    		return messages;
    	}
    	
    	if (sender == null || sender.trim().length() == 0) {
    		// 如果发送者为null或者为空，则返回该接收者接受到的所有未读消息
    		ArrayList<String> keys = findAllUnreadKeys(receiver);
    		
    		Iterator<String> it = keys.iterator();
    		
    		Log.info("keys are " + keys);
    		while (it.hasNext()) {

    			String key = it.next();
    			
    			ArrayList<Message> msgs = getAllMessagesByKey(key);
    			Log.info("receiver " + receiver + "msgs:" + msgs + " key is " + key);
    			if (msgs != null && msgs.size() > 0) {
    				messages.addAll(msgs);
    			}
    			
    			if (messages.size() > maxMSG) {
    				break;
    			}

    		}
        } else {
        	String key = RECEIVE_UNREAD_MSG + "_" + receiver + "_" + sender;
        	if (getAllMessagesByKey(key) != null) {
        		messages = getAllMessagesByKey(key);
        	}
        }
    	
    	return messages;
    }
    
    /**
     * 获得指定时间段内的某种类型的所有消息。
     * 
     * <strong>TODO: </strong> 以后改为可分页信息并从关系数据库中查找
     * 
     * @param sender
     * @param receiver
     * @param beginDate 如果为null则设置为当天
     * @param endDate 如果为null则设置为当天
     * @param maxMSG 最大允许加的消息数量
     * @return
     */
    public ArrayList<Message> getMessages(String sender, String receiver, 
            Calendar beginDate, Calendar endDate, int maxMSG){ 
        
        ArrayList<Message> messages = new ArrayList<Message>();
        
        if (maxMSG == 0) {
            // 如果将最大返回消息数设置为0， 则最大返回2147483646条
            maxMSG = Integer.MAX_VALUE - 1;
        }
        
        if (beginDate == null) {
            beginDate = Calendar.getInstance();
        }
        
        if (endDate == null) {
            endDate = Calendar.getInstance();
        }
        
        if (beginDate.after(endDate)) {
            Log.info("date range error");
            return null;
        }
        
        long dateRange = (endDate.getTimeInMillis() - beginDate.getTimeInMillis())/(1000L * 60L * 60L * 24L);
        
        int endDateDay = endDate.get(Calendar.DATE);
        
        for (int i = 0; i <= (int)dateRange; i++) {
            
            if (messages.size() > maxMSG) {
                break;
            }
            
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            Calendar date = (Calendar)endDate.clone();
            date.set(Calendar.DATE, endDateDay - i);
            String key = CHAT_TRACK + "_" + sender + "_" + receiver + "_" + df.format(date.getTime());
            
            ArrayList<Message> msgs = getAllMessagesByKey(key);
            
            if (msgs != null && msgs.size() > 0) {
				messages.addAll(msgs);
			}
            
            if (messages.size() > maxMSG) {
                break;
            }
            
        }
        
        return messages;
    }

    private ArrayList<Message> getAllMessagesByKey(String key) {
    	
    	ArrayList<Message> messages = new ArrayList<Message>();
    	
		JSONParser parser = new JSONParser();
		
		TTServer db = null;
        
		try {
		    db = TTServerPool.getConnection();
			String value = db.get(key);
			
			if (value == null || value.trim().length() == 0) {
	            return messages;
	        }
			
            Object messageObj = parser.parse(value);
            
            if (messageObj == null) {
                Log.info("parse error");
                return messages;
            }
            
            JSONArray msgArray = (JSONArray)messageObj;
            JSONPojoConvertor joc = new JSONPojoConvertor(Message.class);
            
            for (int j = 0; j < msgArray.size(); j++) {

                Message msg = new Message();
                // TODO 加非null和非空判断
                JSONObject jo = (JSONObject)msgArray.get(j);
                
                joc.setProps(msg, jo);
                messages.add(msg);
                
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return messages;
    }
    
    public void run() {
        // TODO Auto-generated method stub
        
    }
    
    public static void main(String[] args) throws Exception {

        MessageOperator msgOperator = new MessageOperator();
        
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar date = Calendar.getInstance();
        String key1= RECEIVE_UNREAD_MSG +"_" + "danny" +"_" + "dirk" + "_" + df.format(date.getTime());
        String key= CHAT_TRACK +"_" + "danny" +"_" + "dirk" + "_" + df.format(date.getTime());
        String key2= RECEIVE_UNREAD_MSG +"_" + "dirk" +"_" + "danny" + "_" + df.format(date.getTime());
        String key3= CHAT_TRACK +"_" + "dirk" +"_" + "danny" + "_" + df.format(date.getTime());
        
        TTServer db = TTServerPool.getConnection();
        db.close();
        db.open();
        db.remove("chat_track_b_a_20090617");
        db.remove("chat_track_a_b_20090617");
//        Message data = new Message();
//        data.setChat("chat");
//        data.setScope("private");
//        data.setSender("dirk");
//        data.setTime("time");
//        data.setUser("user");
//        
//        Message data1 = new Message();
//        data1.setChat("chat1");
//        data1.setScope("private1");
//        data1.setSender("danny");
//        data1.setTime("time");
//        data1.setUser("user1");
//        
//        msgOperator.saveToDB(RECEIVE_UNREAD_MSG, "dirk", "danny", data);
//        msgOperator.saveToDB(RECEIVE_UNREAD_MSG, "danny", "dirk", data);
//        msgOperator.saveToDB(RECEIVE_UNREAD_MSG, "dirk1", "danny", data1);
//        msgOperator.saveToDB(RECEIVE_UNREAD_MSG, "danny", "dirk", data1);
//        msgOperator.saveToDB(CHAT_TRACK, "danny","dirk", data1);
//        
//        System.err.println(key3 + " " + db.get(key3));
//        System.err.println(key2 + " " + db.get(key2));
//        System.err.println(key1 + " " + db.get(key1));
//        System.err.println(key + " " + db.get(key));
//        
//        System.err.println(msgOperator.getMessages("dirk", "danny", null, null, 0));
//        System.err.println(msgOperator.getMessages("danny", "dirk", null, null, 0));
//        
//        System.err.println(msgOperator.findAllUnreadKeys("dirk"));
//        System.err.println(msgOperator.findAllUnreadKeys("danny"));
//        
//        
//        msgOperator.updateUnreadMSG("dirk", "danny");
//        
//        System.err.println(msgOperator.findAllUnreadKeys("dirk"));
//        System.err.println(msgOperator.findAllUnreadKeys("danny"));
//        
//        System.err.println(msgOperator.getAllUnreadMessage("danny", null, 0));
//        System.err.println(msgOperator.getAllUnreadMessage("danny", "dirk1", 1));
//        msgOperator.updateUnreadMSG("danny", "dirk");
//        
//        System.err.println(msgOperator.getAllUnreadMessage("danny", null, 0));
//        msgOperator.updateUnreadMSG("danny", "dirk1");
//        System.err.println(msgOperator.getAllUnreadMessage("danny", null, 0));
//        db.remove(RECEIVE_UNREAD_MSG+"_a");
//        System.err.println(msgOperator.findAllUnreadKeys("a"));
        
//        saveToDB(SEND_MSG, "dirk", data);
//
//        Message data1 = new Message();
//        data1.setChat("chat");
//        data1.setScope("private");
//        data1.setSender("dany");
//        data1.setTime("time");
//        data1.setUser("user");
//
//        saveToDB(RECEIVE_UNREAD_MSG, "dirk", data1);
//        saveToDB(RECEIVE_UNREAD_MSG, "dirk", data1);
//        saveToDB(RECEIVE_UNREAD_MSG, "dirk", data1);
//        saveToDB(RECEIVE_UNREAD_MSG, "dirk", data1);
//        
//        System.out.println(db.get(key1));
//        System.out.println(db.get(key2));
//        
//        updateUnreadMSG(key2);
//        
//        System.out.println(db.get(key2));
//        System.out.println(db.get(key3));
        
    }

}
