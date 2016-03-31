package cn.yoho.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.log.Log;

/**
 * 正常Servlet抽象类
 * 
 * @author Hbomb
 * 
 */
public abstract class NormalImServlet extends GenericServlet {

    /**
   * 
   */
    private static final long serialVersionUID = -6090997130817639013L;
    private PrintWriter _out;
    protected String _contentType = "text/json; charset=utf-8";

    protected String _start = "[";
    protected String _end = "]\r\n";

    protected String _start_p = "([";
    protected String _end_p = "])\r\n";

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        paser2Obj(req);
        rout(req, res);
    }

    /**
     * 方法跳转控制
     * 
     * @param req
     * @param res
     * @throws IOException
     */
    protected abstract void rout(ServletRequest req, ServletResponse res)
            throws IOException;

    /**
     * 判断是否是jsonp跨域调用
     * 
     * @param req
     */
    private void isJsonp(ServletRequest req) {
        String jsonpParam = req.getParameter("jsonp");
        if (null != jsonpParam)// 判断jsonp函数名是否为空
        {
            _start = jsonpParam + _start_p;
            _end = _end_p;
        }
    }

    /**
     * 设置返回信息（成功或失败）
     * 
     * @param flag
     * @param str
     * @param res
     * @throws IOException
     */
    protected void makeInfo(boolean flag, String str, ServletRequest req,
            ServletResponse res) throws IOException {
        HttpServletResponse response = (HttpServletResponse) res;
        isJsonp(req);
        response.setStatus(200);
        _contentType = "text/javascript; charset=utf-8";
        response.setContentType(_contentType);
        _out = response.getWriter();
        _out.write(_start);
        _out.write("{\"is\":" + flag + ",\"msg\":\"" + str + "\"}");
        _out.write(_end);
        _out.close();
    }

    /**
     * 转客户端json到Java对象
     * 
     * @param req
     * @throws IOException
     */
    private void paser2Obj(ServletRequest req) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String str = (String) request.getParameter("json");
        if (null != str) {
            Object reqs = JSON.parse(str);
            req.setAttribute("jsonBean", reqs);
        }
    }

    /**
     * Java对象转化到客户端json
     * 
     * @param res
     * @throws IOException
     */
    protected void paser2Json(ServletRequest req, ServletResponse res,
            Object obj) throws IOException {
        String ret = JSON.getDefault().toJSON(obj);
        Log.info(ret);
        HttpServletResponse response = (HttpServletResponse) res;
        isJsonp(req);
        response.setStatus(200);
        _contentType = "text/javascript; charset=utf-8";
        response.setContentType(_contentType);
        _out = response.getWriter();
        _out.write(_start);
        _out.write(ret);
        _out.write(_end);
        _out.close();
    }
}
