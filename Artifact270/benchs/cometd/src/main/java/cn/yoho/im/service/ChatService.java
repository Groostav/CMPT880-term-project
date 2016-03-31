/**
 * 
 */
package cn.yoho.im.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletContext;

import org.cometd.Bayeux;
import org.cometd.Channel;
import org.cometd.Client;
import org.cometd.MessageListener;
import org.cometd.RemoveListener;
import org.cometd.oort.Oort;
import org.cometd.oort.Seti;
import org.cometd.server.BayeuxService;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.eclipse.jetty.util.ajax.JSONPojoConvertor;
import org.eclipse.jetty.util.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;

import cn.yoho.im.bean.Group;
import cn.yoho.im.bean.SendChatEntry;
import cn.yoho.im.bean.Message;
import cn.yoho.im.bean.User;
import cn.yoho.im.data.TTServer;
import cn.yoho.im.data.TTServerPool;

/**
 * 聊天服务（主要是自定义channel的业务注入）
 * 
 * @author Hbomb
 * 
 */
public class ChatService extends BayeuxService {
    
    private Oort _oort;
    private Seti _seti;
    
    public ChatService(ServletContext context) {
    	
        super((Bayeux)context.getAttribute(Bayeux.ATTRIBUTE), "chat");

        _oort = (Oort)context.getAttribute(Oort.OORT_ATTRIBUTE);
        if (_oort==null)
            throw new RuntimeException("!"+Oort.OORT_ATTRIBUTE);
        _seti = (Seti)context.getAttribute(Seti.SETI_ATTRIBUTE);
        if (_seti==null)
            throw new RuntimeException("!"+Seti.SETI_ATTRIBUTE);

        _oort.observeChannel("/chat/**");
        
        subscribe("/chat/**", "trackMembers");
        subscribe("/service/readMessage", "updateUnreadMessage");
        subscribe("/service/privatechat", "privateChat");
        subscribe("/service/allchat", "allChat");
        subscribe("/service/groupchat", "groupChat");
        
//        dbParam = new DBParam();
//        dbOperator = new DBOperator();
//        System.out.println("************************Thread is: " + dbOperator.getId());
//        dbOperator.start();
    }

    public void login(final Client joiner, final String channelName,
            Map<String, Object> data, final String messageId) {
        Log.info("Hello!");
        boolean isLogin = true;

        if (!isLogin) {
            Channel channel = getBayeux().getChannel(channelName, false);
            if (channel != null)
                channel.unsubscribe(joiner);
            joiner.disconnect();
        }
    }

    /**
     * 获取用户列表
     * 
     * @param joiner
     * @param channelName
     * @param data
     * @param messageId
     */
    public void trackMembers(final Client joiner, final String channelName,
            Object data, final String messageId) {
        
        final DBOperator dbOperator = new DBOperator();
        
        if (data instanceof Object[]) {
            return;
        } else if (data instanceof Map) {
            
            Map<String, Object> map = (Map<String, Object>) data;
            
            if (Boolean.TRUE.equals(map.get("join"))) {

                final String userName = (String) map.get("user");
                
                
                
                if (!_oort.isOort(joiner)) {
                    if (dbOperator.isUserOnline(userName)) {
                        // 如果用户已登录了，则强迫上个登录的下线
                        
                        Message sysMessage = new Message();
                        sysMessage.setChat(userName + "已在其他地方登陆，请重新登陆！");
                        sysMessage.setUser("系统信息");
                        sysMessage.setScope("denied");
                        sysMessage.setTime(getSystemTime());
                        
                        _seti.sendMessage(userName, channelName, sysMessage);
                    }
                    Log.info("&&&&&&&&&&&&&&&&&&&seti is associating joiner!  " + userName);
                    _seti.associate(userName,joiner);
                    dbOperator.updateUserStat(userName, joiner.getId(), channelName, true);
                }
                
                final ArrayList<User> members = dbOperator.getUsers(channelName,false);
                
                /**
                 * 添加监听下线者
                 */
                joiner.addListener(new RemoveListener() {
                    public void removed(String clientId, boolean timeout) {
                        Log.info("remove begin after timeout " + clientId + ". current id is " + dbOperator.getClientIdByUser(userName));
                        
                        if (timeout)
                        {
                            if (clientId != null && clientId.equals(dbOperator.getClientIdByUser(userName))) {
                                if (!_oort.isOort(joiner)) {
                                    Log.info("client " + clientId + "is leaving since time out!");
                                    _seti.disassociate(userName);
                                }
                                
                                Log.info("user " + userName + "is leaving!");
                                
                                dbOperator.removeUserByClientId(clientId);
                                                    
                                final ArrayList<User> members = dbOperator.getUsers(channelName,false);
                                
                                Log.info("members: " + members);
                                // Broadcast the members to all existing members
                                Channel channel = getBayeux()
                                        .getChannel(channelName, false);
                                if (channel != null){
                                    channel.publish(getClient(), members, messageId);
                                }
                            }
                        }
                        
                    }
                });

                /**
                 * 添加监听加入者的信息
                 */
                joiner.addListener(new MessageListener() {

                    public void deliver(Client fromClient, Client toClient,
                            org.cometd.Message msg) {
                        Log.info("Client " + toClient.getId() + " received " + msg);

                    }
                });

                Log.info("Members: " + members);
                // Broadcast the members to all existing members
                getBayeux().getChannel(channelName, false).publish(getClient(),
                        members, messageId);
                
                ArrayList<User> groups = dbOperator.getGroupsByUser(userName);
                joiner.deliver(getClient(), channelName, groups, messageId);
                
                // 发送离线消息
                ArrayList<Message> messages = dbOperator.getAllUnreadMessage(userName, null, 0);
                Iterator<Message> it = messages.iterator();
                
                while (it.hasNext()) {
                    Message msg = it.next();
                    joiner.deliver(getClient(), channelName, msg, messageId);
                }
            } else  if (Boolean.TRUE.equals(map.get("leave"))){
                                
                String userName = (String)map.get("user");
                
                String clientId = dbOperator.getClientIdByUser(userName);
                
                if (!_oort.isOort(joiner)) {
                    _seti.disassociate(userName);
                }
                
                // 如果当前在线用户的clientId与请求的相等，则下线当前 否则保留
                if (joiner != null && joiner.getId().equals(clientId)) {
                    Log.info("user " + userName + "is leaving!");
                    
                    dbOperator.removeUserByClientId(clientId);
                                        
                    final ArrayList<User> members = dbOperator.getUsers(channelName,false);
                    
                    Log.info("members: " + members);
                    // Broadcast the members to all existing members
                    Channel channel = getBayeux()
                            .getChannel(channelName, false);
                    if (channel != null){
                        channel.publish(getClient(), members, messageId);
                    }
                }
            }
        }
        
    }
    
    public void updateUnreadMessage(Client source, String channel,
            Map<String, Object> data, String messageId) {
        
        DBOperator dbOperator = new DBOperator();
        String sender = (String)data.get("sender");
        String receiver = (String)data.get("receiver");
        
        Log.info("update unread message from " + sender + " for receiver: " + receiver);
        dbOperator.updateUnreadMSG(receiver, sender);
    }

    /**
     * 点对点发送信息
     * 
     * @param source
     * @param channel
     * @param data
     * @param messageId
     */
    public void privateChat(Client source, String channel,
            Map<String, Object> data, String messageId) {
        
        DBOperator dbOperator = new DBOperator();
        
        String nowTime = getSystemTime();

        String roomName = (String) data.get("room");

        String peerName = (String) data.get("peerName");
        

        Message message = new Message();
        message.setChat(data.get("chat"));
        message.setUser(data.get("user"));
        message.setScope("private");
        message.setTime(nowTime);
        
        dbOperator.saveToDB(dbOperator.CHAT_TRACK, (String)data.get("user"), 
                peerName, message, false);
        
        // 在线消息
        if (dbOperator.isUserOnline(peerName)) {
            
            source.deliver(getClient(), roomName, message, messageId);
            _seti.sendMessage(peerName,roomName,message);
        } 
        // 离线消息
        else {
            
            dbOperator.saveToDB(dbOperator.RECEIVE_UNREAD_MSG, (String)data.get("user"), 
                    peerName, message, false);
            Message sysMessage = new Message();
            sysMessage.setChat(peerName + "已下线！");
            sysMessage.setUser("系统信息");
            sysMessage.setScope("error");
            sysMessage.setTime(nowTime);

            source.deliver(source, roomName, sysMessage, messageId);
            // 继续记录消息
            source.deliver(source, roomName, message, messageId);
        }

        
    }

    /**
     * 点对多聊天
     * 
     * @param source
     * @param channel
     * @param data
     * @param messageId
     */
    public void allChat(Client source, String channel,
            Map<String, Object> data, String messageId) {
        
        DBOperator dbOperator = new DBOperator();
        
        String roomName = (String) data.get("room");
        ArrayList<User> membersArr = dbOperator.getUsers(roomName,true);
        for (User u : membersArr) {
            Client peer = getBayeux().getClient(u.getId());
            Message message = new Message();
            message.setChat(data.get("chat"));
            message.setUser(data.get("user"));
            message.setTime(getSystemTime());
            dbOperator.saveToDB(dbOperator.CHAT_TRACK, (String)data.get("user"), 
                    u.getName(), message, false);
            _seti.sendMessage(u.getName(), roomName, message);
        }
    }

    public void groupChat(Client source, String channel,
            Map<String, Object> data, String messageId) {
        
        DBOperator dbOperator = new DBOperator();
        
        String gId = (String)data.get("gId");
        String room = (String)data.get("room");
        
        Message message = new Message();
        message.setChat(data.get("chat"));
        message.setUser(data.get("user"));
        message.setScope("group");
        // 当群组发送时，sender为group id
        message.setGroup(gId);
        message.setTime(getSystemTime());
        
        dbOperator.saveToDB(DBOperator.CHAT_TRACK, (String)data.get("user"), 
                gId, message, true);
        
        ArrayList<String> users = dbOperator.getUsersByGroupId(Integer.valueOf(gId));
        
        for (String user : users) {

            if (dbOperator.isUserOnline(user)) {
                
                _seti.sendMessage(user, room, message);
                
            } else {
                // 离线消息
                dbOperator.saveToDB(DBOperator.RECEIVE_UNREAD_MSG, gId, user, message, true);
            }
            
        }
    }

    /**
     * 获取当前时间
     * 
     * @return
     */
    String getSystemTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar date = Calendar.getInstance();
        return df.format(date.getTime());
    }


    public static void main(String[] args) throws Exception {

                
    }
    
}
