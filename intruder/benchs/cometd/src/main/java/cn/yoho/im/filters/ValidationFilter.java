package cn.yoho.im.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.util.log.Log;

import cn.yoho.im.service.DBOperator;

public class ValidationFilter implements Filter{

    private DBOperator dbOperator;
    
    public void destroy() {
        // TODO Auto-generated method stub
        
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        HttpSession session = ((HttpServletRequest)request).getSession();
        String userName = (String)session.getAttribute("userName");
        
        if (userName != null) {
            Log.info("user " + userName +"logged in");
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendRedirect("login.html");
        }
        
    }

    public void init(FilterConfig config) throws ServletException {
        // TODO Auto-generated method stub
        Log.info("filter actived");
        dbOperator = new DBOperator();
    }

}
