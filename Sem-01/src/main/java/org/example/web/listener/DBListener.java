package org.example.web.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.service.ProjectService;
import org.example.service.TaskService;
import org.example.service.UserService;
import org.example.util.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DBListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(DBListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            sce.getServletContext().setAttribute("dbConnection", connection);
        } catch (SQLException e) {
            logger.error("Connection initializing error");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.close();
    }
}

