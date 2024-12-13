package org.example.web.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DBListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            sce.getServletContext().setAttribute("dbConnection", connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.close();
        System.out.println("Соединение закрыто, пул соединений освобожден");
    }

}
