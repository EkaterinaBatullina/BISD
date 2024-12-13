package org.example.web.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.dao.UserDAO;
import org.example.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebListener
public class DBListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            sce.getServletContext().setAttribute("dbConnection", connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection dbConnection = DBConnection.getInstance();
        dbConnection.close();
    }

}

