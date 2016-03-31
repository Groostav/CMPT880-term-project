package cn.yoho.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.yoho.im.bean.Group;
import cn.yoho.im.bean.Message;
import cn.yoho.im.service.DBOperator;

/**
 * 群组服务
 * 
 * @author Hbomb
 * 
 */
public class GroupImServlet extends NormalImServlet {

    /**
   * 
   */
    private static final long serialVersionUID = -6090997130817639013L;
    
    private DBOperator msgOperator;
    
    public GroupImServlet() {
        msgOperator = new DBOperator();
    }

    /**
     * 调用转向
     */
    protected void rout(ServletRequest req, ServletResponse res)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String m = request.getParameter("groupOperation");
        int methodCode = 0;
        if (m != null)
            methodCode = Integer.parseInt(m);
        switch (methodCode) {
        case 1:
            createGroup(req, res);
            break;
        case 2:
            delGroup(req, res);
            break;
        case 3:
            delGroupMember(req, res);
            break;
        case 4:
            addGroupMember(req, res);
            break;
        case 5:
            findGroup(req, res);
        case 6:
            GroupDetail(req, res);
            break;
        default:
            makeInfo(false, "非法请求!", req, res);
            break;
        }
    }

    /**
     * 创建群
     * 
     * @param req
     * @param res
     */
    private void createGroup(ServletRequest req, ServletResponse res) {

        String creator = req.getParameter("creator");
        
        Group group = new Group("0",null);
        
        if (creator == null || creator.trim().length() == 0) {
            group.setState("fail");
            try {
                paser2Json(req, res, group);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        } 
        
        String users = req.getParameter("users");
        if (users != null || users.length() != 0) {
            users += "," + creator;
        } else {
            users = creator;
        }
        
        String[] userArr = users.split(",");
        String name = req.getParameter("name");
        
        if (name == null) {
            name = creator + Calendar.getInstance().getTimeInMillis();
        }
        int groupId = msgOperator.createGroup(creator, name, req.getParameter("description"));
                
        msgOperator.addGroupMembers(groupId,userArr,null);
        
        group.setId(String.valueOf(groupId));
        group.setName(name);
        group.setState("success");
        
        try {
            paser2Json(req, res, group);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    /**
     * 删除群
     * 
     * @param req
     * @param res
     */
    private void delGroup(ServletRequest req, ServletResponse res) {

    }

    /**
     * 删除群成员
     * 
     * @param req
     * @param res
     */
    private void delGroupMember(ServletRequest req, ServletResponse res) {

    }

    /**
     * 添加群成员
     * 
     * @param req
     * @param res
     */
    private void addGroupMember(ServletRequest req, ServletResponse res) {
      //  "groupId=" + groupId + "&user=" + userName +"&groupOperation=4",
        String groupId = req.getParameter("groupId");
        Group group = new Group("0",null);
        String user = req.getParameter("user");
        if (groupId == null || groupId.trim().length() == 0 || 
                user == null || user.trim().length() == 0) {
            group.setState("fail");
            try {
                paser2Json(req, res, group);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        
        int gid = Integer.valueOf(groupId);
        
        int addNum = msgOperator.addGroupMembers(gid, new String[]{user}, null);
        
        if (addNum > 0) {
            group.setState("success");
            group.setId(groupId);
            try {
                paser2Json(req, res, group);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 查找群
     * 
     * @param req
     * @param res
     */
    private void findGroup(ServletRequest req, ServletResponse res) {

    }

    /**
     * 群详情
     * 
     * @param req
     * @param res
     */
    private void GroupDetail(ServletRequest req, ServletResponse res) {

    }
}
