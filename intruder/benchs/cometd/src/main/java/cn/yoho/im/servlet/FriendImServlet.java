package cn.yoho.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 朋友列表服务
 * 
 * @author Hbomb
 * 
 */
public class FriendImServlet extends NormalImServlet {

    /**
   * 
   */
    private static final long serialVersionUID = -6090997130817639013L;

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        rout(req, res);
    }

    protected void rout(ServletRequest req, ServletResponse res)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String m = request.getParameter("m");
        int methodCode = 0;
        if (m != null)
            methodCode = Integer.parseInt(m);
        switch (methodCode) {
        case 1:
            findFriend(req, res);
            break;
        case 2:
            addFriend(req, res);
            break;
        case 3:
            delFriend(req, res);
            break;
        case 4:
            addFriendGroup(req, res);
            break;
        case 5:
            delFriendGroup(req, res);
        case 6:
            friendDetail(req, res);
            break;
        default:
            makeInfo(false, "非法请求!", req, res);
            break;
        }
    }

    /**
     * 查找好友
     * 
     * @param req
     * @param res
     */
    private void findFriend(ServletRequest req, ServletResponse res) {

    }

    /**
     * 添加好友
     * 
     * @param req
     * @param res
     */
    private void addFriend(ServletRequest req, ServletResponse res) {

    }

    /**
     * 删除好友
     * 
     * @param req
     * @param res
     */
    private void delFriend(ServletRequest req, ServletResponse res) {

    }

    /**
     * 添加好友分组
     * 
     * @param req
     * @param res
     */
    private void addFriendGroup(ServletRequest req, ServletResponse res) {

    }

    /**
     * 删除好友分组
     * 
     * @param req
     * @param res
     */
    private void delFriendGroup(ServletRequest req, ServletResponse res) {

    }

    /**
     * 好友详情
     * 
     * @param req
     * @param res
     */
    private void friendDetail(ServletRequest req, ServletResponse res) {

    }
}
