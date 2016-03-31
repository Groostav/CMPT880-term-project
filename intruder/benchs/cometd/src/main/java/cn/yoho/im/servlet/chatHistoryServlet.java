package cn.yoho.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import cn.yoho.im.bean.Message;
import cn.yoho.im.service.MessageOperator;
import cn.yoho.im.service.DBOperator;

public class chatHistoryServlet extends GenericServlet {

    private static final long serialVersionUID = -7187971526553821378L;
   
    private DBOperator msgOperator;
    
    public chatHistoryServlet() {

        msgOperator = new DBOperator();
    }
    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {

        String sender = request.getParameter("sender");
        String receiver = request.getParameter("receiver");
        
        ArrayList<Message> messages = msgOperator.getMessages(sender, receiver, Calendar.getInstance(), Calendar.getInstance(), 100);
        ArrayList<Message> receiveMessages = msgOperator.getMessages(receiver, sender, Calendar.getInstance(), Calendar.getInstance(), 100);
        messages.addAll(receiveMessages);
        Collections.sort(messages, new Comparator<Message>() {

            public int compare(Message o1, Message o2) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date1;
                try {
                    date1 = df.parse(o1.getTime());
                    Date date2 = df.parse(o2.getTime());
                    if(date1.getTime() > date2.getTime()) {
                        return 1;
                    } else if(date1.getTime() == date2.getTime()) {
                        return 0;
                    } else {
                        return -1;
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return -1;
            }});
        StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            String fromUser = (String)message.getUser();
            String time = message.getTime();
            String chat = message.getChat();
            
            String msg = fromUser + "&nbsp;<span style='font-size:12px;color:#aaa'>" + time + "</span>&nbsp;<br/>";
            msg += "<span class=\"membership\"><span class=\"text\" style=\"font-size:12px\">" + chat + "</span></span><br/>";
            
            sb.append(msg);
        }
        response.setCharacterEncoding("utf-8");
        PrintWriter pw = response.getWriter();
        pw.write(sb.toString());
    }

}
