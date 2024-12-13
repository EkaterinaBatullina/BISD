package org.example.web.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebListener
public class UserSessionListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Map<UUID, Long> userSessions = new HashMap<>();
        sce.getServletContext().setAttribute("USER_SESSIONS", userSessions);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Map<UUID, Long> userSessions = (Map<UUID, Long>) sce.getServletContext()
                .getAttribute("USER_SESSIONS");
        if (userSessions != null) {
            userSessions.clear();
        }
    }

}

