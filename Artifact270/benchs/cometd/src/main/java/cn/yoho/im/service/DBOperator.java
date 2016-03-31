package cn.yoho.im.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jetty.util.ajax.JSONPojoConvertor;
import org.eclipse.jetty.util.log.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import cn.yoho.im.bean.Group;
import cn.yoho.im.bean.Message;
import cn.yoho.im.bean.User;
import cn.yoho.im.data.MySQLPool;
import cn.yoho.im.data.TTServer;
import cn.yoho.im.data.TTServerPool;

/*
 * this is a operator class for mysql
 */
public class DBOperator {

    public static final String RECEIVE_UNREAD_MSG = "chat_unread";
    public static final String CHAT_TRACK = "chat_track";
    private MySQLPool mySQLPool;
    
    public DBOperator() {
        mySQLPool = MySQLPool.getInstance();
    }
    
    public void saveToDB(String type, String sender, String receiver, Message data, boolean isGroupChat) {

        if (sender == null || sender.trim().length() == 0) {
            Log.info("invalid sender");
            return;
        }

        Calendar date = Calendar.getInstance();

        Connection conn = mySQLPool.getConnection();
        
        String dataValue = JSONValue.toJSONString(data);
        try {
            
            if (CHAT_TRACK.equals(type)) {
                String sql = "insert into chat_history(receiver_id, sender_id, send_time, chat_content, group_chat) values(?,?,?,?,?);";
                PreparedStatement psm = conn.prepareStatement(sql);
                psm.setString(1, receiver);
                psm.setString(2, sender);
                psm.setTimestamp(3, new Timestamp(date.getTimeInMillis()));
                psm.setString(4, dataValue);
                psm.setBoolean(5, isGroupChat);
                psm.executeUpdate();
            }
            if (RECEIVE_UNREAD_MSG.equals(type)) {
                String unreadSql = "insert into chat_unread(receiver_id, sender_id, send_time, chat_content, group_chat) values(?,?,?,?,?);";
                PreparedStatement unreadPsm = conn.prepareStatement(unreadSql);
                unreadPsm.setString(1, receiver);
                unreadPsm.setString(2, sender);
                unreadPsm.setTimestamp(3, new Timestamp(date.getTimeInMillis()));
                unreadPsm.setString(4, dataValue);
                unreadPsm.setBoolean(5, isGroupChat);
                unreadPsm.executeUpdate();

            }
        }catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    
    /**
     * 更新未读消息。
     * 
     * @param receiver
     * @param sender
     */
    public void updateUnreadMSG(String receiver, String sender) {

        if (receiver == null || receiver.trim().length() == 0) {
            Log.info("invalid receiver");
            return;
        }
        
        Connection conn = mySQLPool.getConnection();
                
        try {
            
            String sql = "delete from chat_unread where receiver_id=? ";
            if (sender != null && sender.trim().length() != 0) {
                sql += "and sender_id=?";
                PreparedStatement psm = conn.prepareStatement(sql);
                psm.setString(1, receiver);
                psm.setString(2, sender);
                psm.executeUpdate();
            } else {
                PreparedStatement psm = conn.prepareStatement(sql);
                psm.setString(1, receiver);
                psm.executeUpdate();
            }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
            Log.info("invalid sender");
            return new ArrayList<Message>();
        }
        
        Connection conn = mySQLPool.getConnection();
        ResultSet rs = null;
        try {
            String sql = "select chat_content from chat_unread where receiver_id=? ";
            if (sender != null && sender.length() != 0) {
                sql += "and sender_id=? order by send_time";
                PreparedStatement psm = conn.prepareStatement(sql);
                psm.setString(1, receiver);
                psm.setString(2, sender);
                rs = psm.executeQuery();
            } else {
                sql += " order by send_time";
                PreparedStatement psm = conn.prepareStatement(sql);
                psm.setString(1, receiver);
                rs = psm.executeQuery();
            }
            
            while (rs.next()) {
                String data = rs.getString("chat_content");
                JSONParser parser = new JSONParser();
                Message message = new Message();
                
                try {
                    JSONObject obj = (JSONObject)parser.parse(data);
                    
                    if(obj == null) {
                        continue;
                    }
                    
                    JSONPojoConvertor joc = new JSONPojoConvertor(Message.class);
                    joc.setProps(message, obj);
                    
                    if (messages.size() >= maxMSG) {
                        break;
                    }
                    
                    messages.add(message);
                    
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        
        return messages;
    }
    
    /**
     * 获得指定时间段内的所有消息。
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
        
        Connection conn = mySQLPool.getConnection();
        ResultSet rs = null;
        
        try {
            String sql = "select chat_content from chat_history where receiver_id = ? " +
            		"and sender_id = ? and send_time <= ? and send_time >= ? order by send_time";
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setString(1, receiver);
            psm.setString(2, sender);
            psm.setDate(3, new java.sql.Date(endDate.getTimeInMillis() + (24 * 60 * 60 * 1000)));
            psm.setDate(4, new java.sql.Date(beginDate.getTimeInMillis()));
            
            rs = psm.executeQuery();
            
            while (rs.next()) {
                String data = rs.getString("chat_content");
                JSONParser parser = new JSONParser();
                Message message = new Message();
                
                try {
                    JSONObject obj = (JSONObject)parser.parse(data);
                    
                    if(obj == null) {
                        continue;
                    }
                    
                    JSONPojoConvertor joc = new JSONPojoConvertor(Message.class);
                    joc.setProps(message, obj);
                    
                    if (messages.size() >= maxMSG) {
                        break;
                    }
                    
                    messages.add(message);
                    
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        } catch (SQLException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        
        return messages;
    }
    
    public String getUserInSession(String sessionId) {
        if (sessionId == null || sessionId.trim().length() == 0) {
            Log.info("invalid session");
            return null;
        }
        
        Connection conn = mySQLPool.getConnection();
        ResultSet rs = null;
        
        try {
            String sql = "select user_id from chat_user where session_id=?";
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setString(1, sessionId);
            
            rs = psm.executeQuery();
            if (rs.next()) {
                String user = rs.getString("user_id");
                return user;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        return null;
        
    }
    
    public boolean validateUser(String userName, String password) {
        
        if (userName == null || userName.trim().length() == 0) {
            Log.info("invalid userName");
            return false;
        }
        
        Connection conn = mySQLPool.getConnection();
        ResultSet rs = null;
        
        try {
            String sql = "select count(*) from im_user where name=? and password=?";
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setString(1, userName);
            psm.setString(2, password);
            
            rs = psm.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                
                if (count > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        return false;
    }
    
    public ArrayList<User> getUsers(String channel, boolean getOnline) {
        
        ArrayList<User> users = new ArrayList<User>();
        
        if (channel == null || channel.trim().length() == 0) {
            return users;
        }
        
        Connection conn = mySQLPool.getConnection();
        ResultSet rs = null;
        
        try {
            
            String sql = "select user_id, client_id, is_online from chat_user where channel_name=?";
            
            if (getOnline) {
                sql += "and is_online=true";
            }
             
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setString(1, channel);
            
            rs = psm.executeQuery();

            while (rs.next()) {
                
                String userName = rs.getString("user_id");
                String clientId = rs.getString("client_id");
                boolean isOnline = rs.getBoolean("is_online");
                User user = new User(clientId, userName);
                if (isOnline) {
                    user.setState("online");
                }
                user.setGroupUser(Boolean.FALSE);
                users.add(user);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        return users;
    }
    
    public void updateUserStat(String userName, String clientId, String channelName, boolean isOnline) {
        if (userName == null || userName.trim().length() == 0) {
            Log.info("invalid user name");
            return;
        }
        
        Connection conn = mySQLPool.getConnection();
        
        try {
            conn.setAutoCommit(false);
            String updateSql = "update chat_user set channel_name=?, client_id=?, is_online=? where user_id=?";
            PreparedStatement psm = conn.prepareStatement(updateSql);
            psm.setString(1, channelName);
            psm.setString(2, clientId);
            psm.setBoolean(3, isOnline);
            psm.setString(4, userName);
            
            int updated = psm.executeUpdate();
            
            // 有且仅有一行被更新
            if (updated != 1) {
                String deleteSql = "delete from chat_user where user_id=?";
                psm = conn.prepareStatement(deleteSql);
                psm.setString(1, userName);
                psm.executeUpdate();
                
                String insertSql = "insert into chat_user (user_id, channel_name, client_id, is_online) values (?,?,?,?)";
                psm = conn.prepareStatement(insertSql);
                psm.setString(1, userName);
                psm.setString(2, channelName);
                psm.setString(3, clientId);
                psm.setBoolean(4, isOnline);
                psm.executeUpdate();
            }
            conn.commit();
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
            try {
                conn.rollback();
            } catch (SQLException e1) {
                Log.info("fail to roll back");
                e1.printStackTrace();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean isUserOnline(String userName) {
        if (userName == null || userName.trim().length() == 0) {
            Log.info("invalid userName");
            return false;
        }
        
        Connection conn = mySQLPool.getConnection();
        ResultSet rs = null;
        
        try {
            
            String sql = "select is_online from chat_user where user_id=?";
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setString(1, userName);
            
            rs = psm.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("is_online");
            }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        return false;
    }
    
    public void removeUserByClientId(String clientId) {
        if (clientId == null || clientId.trim().length() == 0) {
            Log.info("invalid clientId");
            return;
        }
        
        Connection conn = mySQLPool.getConnection();
        
        try {
            
            String updateSql = "update chat_user set is_online=false where client_id=?";
            PreparedStatement psm = conn.prepareStatement(updateSql);
            psm.setString(1, clientId);
            
            psm.executeUpdate();
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }
    
    public String getClientIdByUser (String user) {
        if (user == null || user.trim().length() == 0) {
            Log.info("invalid clientId");
            return null;
        }
        
        Connection conn = mySQLPool.getConnection();
        ResultSet rs = null;
        
        try {
            
            String updateSql = "select client_id from chat_user where user_id=?";
            PreparedStatement psm = conn.prepareStatement(updateSql);
            psm.setString(1, user);
            
            rs = psm.executeQuery();
            
            if (rs.next()) {
                return rs.getString("client_id");
            }
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
        
    }
    
    public int createGroup(String creator, String name, String description) {
        
        Calendar date = Calendar.getInstance();

        Connection conn = mySQLPool.getConnection();
        
        try {
            String sql = "insert into chat_group(creator_id, name, create_time, description) values(?,?,?,?);";
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setString(1, creator);
            psm.setString(2, name);
            psm.setTimestamp(3, new Timestamp(date.getTimeInMillis()));
            psm.setString(4, description);
            psm.executeUpdate();
            String idSql = "select @@IDENTITY";
            Statement sm = conn.createStatement();
            ResultSet rs = sm.executeQuery(idSql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
    
    public int addGroupMembers(int groupId, String[] users, String desc) {
        Calendar date = Calendar.getInstance();

        Connection conn = mySQLPool.getConnection();
        int count = 0;
        
        try {
            String sql = "insert into chat_group_member(group_id, member_id, join_time, description) values(?,?,?,?);";
            PreparedStatement psm = conn.prepareStatement(sql);
            
            psm.setInt(1,groupId);
            psm.setTimestamp(3, new Timestamp(date.getTimeInMillis()));
            psm.setString(4, desc);
            
            String querySql = "select count(*) from chat_group_member where group_id = ? and member_id = ?";
            PreparedStatement sm = conn.prepareStatement(querySql);
            
            sm.setInt(1,groupId);
            
            for (String user : users) {
                sm.setString(2, user);
                
                ResultSet rs = sm.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) == 0) {
                        psm.setString(2, user);
                        int rowNum = psm.executeUpdate();
                        count += rowNum;
                    }
                }
                
            }
            
        } catch (SQLException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        return count;
    }
    
    public ArrayList<String> getUsersByGroupId(int groupId) {
        
        ArrayList<String> users = new ArrayList<String>();
        if (groupId <= 0) {
            return users;
        }
        
        Connection conn = mySQLPool.getConnection();
        
        try {
            String sql = "select member_id from chat_group_member where group_id=?";
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setInt(1,groupId);
            
            ResultSet rs = psm.executeQuery();
            
            while (rs.next()) {
                String user = rs.getString(1);
                users.add(user);
            }
            
        } catch (SQLException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return users;
        
    }
    
    public ArrayList<User> getGroupsByUser(String user) {
        
        ArrayList<User> groups = new ArrayList<User>();
        
        if (user == null || user.trim().length() == 0) {
            return groups;
        }
        
        Connection conn = mySQLPool.getConnection();
        
        try {
            String sql = "select cg.id, cg.name from " +
            		"chat_group as cg where cg.id in(select cgm.group_id from chat_group_member as cgm where cgm.member_id=?)";
            PreparedStatement psm = conn.prepareStatement(sql);
            psm.setString(1,user);
            
            ResultSet rs = psm.executeQuery();
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                User group = new User(String.valueOf(id),name);
                group.setGroupUser(Boolean.TRUE);
                
                groups.add(group);
                
            }
            
        } catch (SQLException e) {
         // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        return groups;
        
    }
        
    public static void main(String[] args) {
        DBOperator mo = new DBOperator();
//        Message message = new Message();
//        message.setChat("asdfasdfasd");
//        message.setUser("danny");
//        message.setScope("private");
//        message.setSender("dirk");
//        message.setTime("2009-08-09 12:32:23");
//        
//        mo.saveToDB(RECEIVE_UNREAD_MSG, "dirk0", "danny1", message);
//        mo.saveToDB(CHAT_TRACK, "dirk1", "danny", message);
//        mo.saveToDB(RECEIVE_UNREAD_MSG, "dirk2", "danny1", message);
//        mo.saveToDB(CHAT_TRACK, "dirk3", "danny", message);
//        mo.saveToDB(RECEIVE_UNREAD_MSG, "dirk4", "danny1", message);
//        mo.updateUnreadMSG("danny", "dirk2");
//        mo.updateUnreadMSG("danny", null);
//        Calendar begin = Calendar.getInstance();
//        begin.set(Calendar.DAY_OF_MONTH, 20);
//        Calendar end = Calendar.getInstance();
//        end.set(Calendar.DAY_OF_MONTH, 25);
//        System.out.println(mo.getMessages("dirk1", "danny", null, end, 3));
        
       
//       mo.updateUserStat("dirk", "123", "channel", true);
//       mo.updateUserStat("danny", "client", "channel", false);
//       mo.updateUserStat("danny", null, null, false);
       System.out.println(mo.getGroupsByUser("5"));
       
    }
}
