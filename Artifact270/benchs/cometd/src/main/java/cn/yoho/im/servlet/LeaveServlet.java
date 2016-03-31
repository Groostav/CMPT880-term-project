package cn.yoho.im.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.util.log.Log;

public class LeaveServlet extends GenericServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 373787597089739290L;

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {

        Log.info("this is leave servlet");
        HttpSession session = ((HttpServletRequest)request).getSession();
        session.removeAttribute("userName");
        ((HttpServletResponse)response).sendRedirect("/im/examples/chat-jquery/login.html");

    }

}
