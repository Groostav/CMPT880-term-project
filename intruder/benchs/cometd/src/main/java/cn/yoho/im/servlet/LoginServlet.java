package cn.yoho.im.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;

import cn.yoho.im.service.DBOperator;

public class LoginServlet extends GenericServlet {

    private DBOperator dbOperator = new DBOperator();
    
    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {

        String userName = (String) request.getParameter("userName");
        String password = (String) request.getParameter("password");

        if (userName == null || userName.trim().length() == 0) {
            ((HttpServletResponse) response).sendError(503);
        } else {
            // TODO： 在此判断用户名和密码是否正确
            if (dbOperator.validateUser(userName, password)) {
                HttpSession session = ((HttpServletRequest)request).getSession();
                session.setAttribute("userName", userName);
                
                ((HttpServletResponse) response).addCookie(new Cookie("userName",
                        userName));
                ((HttpServletResponse) response)
                        .sendRedirect("/im/examples/chat-jquery/im.html");
            } else {
                ((HttpServletResponse) response).sendError(503);
            }
            
        }

    }

}
