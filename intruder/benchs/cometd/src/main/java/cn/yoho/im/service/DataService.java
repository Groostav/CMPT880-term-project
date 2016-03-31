package cn.yoho.im.service;

import java.util.Map;

import org.cometd.Bayeux;
import org.cometd.Client;
import org.cometd.server.BayeuxService;

/**
 * 持久数据处理服务
 * 
 * @author Hbomb
 */
public class DataService extends BayeuxService {

    public DataService(Bayeux bayeux) {
        super(bayeux, "data");
        subscribe("/meta/handshake", "login");// 登录
        subscribe("/meta/connect", "update");// 更新用户状态
        subscribe("/service/*", "save");// 保存发送信息
        subscribe("/meta/unsubscribe", "logout");// 下线
    }

    /**
     * 获取登录信息
     * 
     * @param source
     * @param channel
     * @param data
     * @param messageId
     */
    public void login(Client source, String channel, Map<String, Object> data,
            String messageId) {

    }

    /**
     * 更新用户状态，并获取最新好友列表（包含群列表）
     * 
     * @param source
     * @param channel
     * @param data
     * @param messageId
     */
    public void update(Client source, String channel, Map<String, Object> data,
            String messageId) {

    }

    /**
     * 保存聊天信息
     * 
     * @param source
     * @param channel
     * @param data
     * @param messageId
     */
    public void save(Client source, String channel, Map<String, Object> data,
            String messageId) {

    }

    /**
     * 登出（设置下线状态）
     * 
     * @param source
     * @param channel
     * @param data
     * @param messageId
     */
    public void logout(Client source, String channel, Map<String, Object> data,
            String messageId) {

    }
}
