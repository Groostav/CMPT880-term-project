package cn.yoho.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
 * 系统设置服务
 * 
 * @author Hbomb
 * 
 */
public class SystemImServlet extends NormalImServlet {

    /**
   * 
   */
    private static final long serialVersionUID = -6090997130817639013L;

    /**
     * 调用
     */
    protected void rout(ServletRequest req, ServletResponse res)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String m = request.getParameter("m");
        int methodCode = 0;
        if (m != null)
            methodCode = Integer.parseInt(m);
        switch (methodCode) {
        case 1:
            setConfig(req, res);
            break;
        case 2:
            getConfig(req, res);
            break;
        case 3:
            setMessageRead(req, res);
            break;
        default:
            makeInfo(false, "非法请求!", req, res);
            break;
        }
    }

    /**
     * 设置参数
     * 
     * @param req
     * @param res
     */
    private void setConfig(ServletRequest req, ServletResponse res) {

    }

    /**
     * 获取参数
     * 
     * @param req
     * @param res
     */
    private void getConfig(ServletRequest req, ServletResponse res) {

    }

    /**
     * 设置信息已读
     * 
     * @param req
     * @param res
     */
    private void setMessageRead(ServletRequest req, ServletResponse res) {

    }

}
