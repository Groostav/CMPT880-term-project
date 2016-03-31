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

public class NormalImServlet_bak extends GenericServlet {

    /**
   * 
   */
    private static final long serialVersionUID = -6090997130817639013L;
    private PrintWriter _out;
    protected String _contentType = "text/json; charset=utf-8";
    protected String _start = "[";
    protected String _end = "]\r\n";

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        rout(req, res);
    }

    private void rout(ServletRequest req, ServletResponse res)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String m = request.getParameter("m");
        int methodCode = 0;
        if (m != null)
            methodCode = Integer.parseInt(m);
        switch (methodCode) {
        case 1:
            login(req, res);
            break;
        case 2:
            logout(req, res);
            break;
        case 3:
            changeState(req, res);
            break;
        default:
            makeMsg(false, "非法请求!", res);
            break;
        }
    }

    /**
     * 设置返回信息
     * 
     * @param flag
     * @param str
     * @param res
     * @throws IOException
     */
    private void makeMsg(boolean flag, String str, ServletResponse res)
            throws IOException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setStatus(200);
        response.setContentType(_contentType);
        _out = response.getWriter();
        _out.write(_start);
        _out.write("{\"is\":" + flag + ",\"msg\":\"" + str + "\"}");
        _out.write(_end);
        _out.close();
    }

    /**
     * 是否通过
     * 
     * @param hm
     * @return
     * @throws IOException
     */
    private boolean isPass(ServletRequest req, ServletResponse res)
            throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String userId = request.getParameter("userId");
        String password = request.getParameter("psw");
        String validate = request.getParameter("vn");
        if (null == userId || null == password || null == validate) {
            makeMsg(false, "登录信息填写不全！", res);
            return false;
        }
        return false;
    }

    /**
     * 登录
     */
    private void login(ServletRequest req, ServletResponse res)
            throws IOException {
        isPass(req, res);
    }

    /**
     * 注销
     */
    private void logout(ServletRequest req, ServletResponse res)
            throws IOException {

    }

    /**
     * 更改状态
     */
    private void changeState(ServletRequest req, ServletResponse res)
            throws IOException {
        ServletContext application = getServletConfig().getServletContext();
        application.setAttribute("haha", "lala");

    }

    /**
     * 添加cookie
     * 
     * @param res
     */
    private void addCookie(ServletResponse res) {

    }

}
