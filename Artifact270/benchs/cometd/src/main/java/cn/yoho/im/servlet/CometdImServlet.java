// ========================================================================
// Copyright 2007-2008 Mort Bay Consulting Pty. Ltd.
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

package cn.yoho.im.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.cometd.Bayeux;
import org.cometd.server.ext.TimesyncExtension;

import cn.yoho.im.service.ChatService;

/**
 * 长连接服务
 * 
 * @author Hbomb
 * 
 */
public class CometdImServlet extends GenericServlet {
    /**
   * 
   */
    private static final long serialVersionUID = -7357495399909087447L;

    ServletConfig _config;
    ServletContext _context;


    public String getServletInfo()
    {
        return this.getClass().toString();
    }
    
    public ServletConfig getServletConfig()
    {
        return _config;
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        
        _config=config;
        _context=config.getServletContext();

        new ChatService(_context);

        Bayeux bayeux = (Bayeux)_context.getAttribute(Bayeux.ATTRIBUTE);
        bayeux.addExtension(new TimesyncExtension());
                
    }

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException {
        ((HttpServletResponse) res).sendError(503);
    }
    
    public void destroy()
    {
    }
}
