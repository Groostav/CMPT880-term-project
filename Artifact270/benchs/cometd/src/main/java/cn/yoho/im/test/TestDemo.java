// ========================================================================
// Copyright 2007 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at 
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//========================================================================

package cn.yoho.im.test;


import org.cometd.oort.Oort;
import org.cometd.oort.OortServlet;
import org.cometd.oort.Seti;
import org.cometd.oort.SetiServlet;
import org.cometd.server.continuation.ContinuationCometdServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.handler.ContextHandler.Context;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import cn.yoho.im.servlet.CometdImServlet;
import cn.yoho.im.servlet.LeaveServlet;
import cn.yoho.im.servlet.LoginServlet;



/* ------------------------------------------------------------ */
/** Main class for cometd demo.
 * 
 * This is of use when running demo in a terracotta cluster
 * 
 * @author gregw
 *
 */
public class TestDemo
{
    private static int _testHandshakeFailure;
    private Oort _oort;
    
    /* ------------------------------------------------------------ */
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        TestDemo d8080=new TestDemo(8080);
        TestDemo d8081=new TestDemo(8081);
        // OortDemo d8082=new OortDemo(8082);
    }

    /* ------------------------------------------------------------ */
    public TestDemo(int port) throws Exception
    {
        String base=".";
        
        // Manually contruct context to avoid hassles with webapp classloaders for now.
        Server server = new Server();
        QueuedThreadPool qtp = new QueuedThreadPool();
        qtp.setMinThreads(5);
        qtp.setMaxThreads(200);
        server.setThreadPool(qtp);
        
        SelectChannelConnector connector=new SelectChannelConnector();
        // SocketConnector connector=new SocketConnector();
        connector.setPort(port);
        server.addConnector(connector);
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        server.setHandler(contexts);
        
        ServletContextHandler context = new ServletContextHandler(contexts,"/",ServletContextHandler.SESSIONS);
        
        context.setBaseResource(new ResourceCollection(new Resource[]
        {
            Resource.newResource(base+"/../../cometd-demo/src/main/webapp/"),
            Resource.newResource(base+"/../../cometd-demo/target/cometd-demo-1.0.beta9-SNAPSHOT/"),
        }));
        
        // Cometd servlet
        ServletHolder cometd_holder = new ServletHolder(ContinuationCometdServlet.class);
        cometd_holder.setInitParameter("timeout","200000");
        cometd_holder.setInitParameter("interval","100");
        cometd_holder.setInitParameter("maxInterval","100000");
        cometd_holder.setInitParameter("multiFrameInterval","1500");
        cometd_holder.setInitParameter("directDeliver","true");
        cometd_holder.setInitParameter("logLevel","1");
        cometd_holder.setInitOrder(1);
        context.addServlet(cometd_holder, "/im/cometd/*");
        
        ServletHolder oort_holder = new ServletHolder(OortServlet.class);
        oort_holder.setInitParameter(Oort.OORT_URL,"http://localhost:"+port+"/im/cometd");
        oort_holder.setInitParameter(Oort.OORT_CHANNELS,"/chat/**");
        oort_holder.setInitParameter(Oort.OORT_CLOUD,(port==8080)?"http://localhost:"+8081+"/im/cometd":"http://localhost:"+8080+"/im/cometd");
        oort_holder.setInitOrder(2);
        context.addServlet(oort_holder, "/oort/*");

        ServletHolder seti_holder = new ServletHolder(SetiServlet.class);
        seti_holder.setInitParameter(Seti.SETI_SHARD,"S"+(port%2));
        seti_holder.setInitOrder(2);
        context.addServlet(seti_holder, "/seti/*");
        
        ServletHolder demo_holder = new ServletHolder(CometdImServlet.class);
        demo_holder.setInitOrder(3);
        context.getServletHandler().addServlet(demo_holder);
        
        ServletHolder login_holder = new ServletHolder(LoginServlet.class);
        demo_holder.setInitOrder(4);
        context.addServlet(login_holder,"/im/login/*");
        
        ServletHolder leave_holder = new ServletHolder(LeaveServlet.class);
        demo_holder.setInitOrder(5);
        context.addServlet(leave_holder,"/im/leave/*");
        
        context.addServlet("org.eclipse.jetty.servlet.DefaultServlet", "/");
        
        server.start();
        Context cont= context.getServletContext();
        _oort = (Oort)cont.getAttribute(Oort.OORT_ATTRIBUTE);
        assert(_oort!=null);
        
    }
}
