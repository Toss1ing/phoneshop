package com.es.core.listener;

import com.es.core.service.cart.HttpSessionCartService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        WebApplicationContext ctx = WebApplicationContextUtils
                .getWebApplicationContext(se.getSession().getServletContext());
        if (ctx != null) {
            HttpSessionCartService httpSessionCartService = ctx.getBean(HttpSessionCartService.class);
            httpSessionCartService.removeLockForSession(se.getSession().getId());
        }
    }

}
