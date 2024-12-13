package org.example.util;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private static HikariDataSource dataSource;
    private DBConnection() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/db_bisd");
        config.setUsername("Ekaterina");
        config.setPassword("eka_rina16");
        config.setDriverClassName("org.postgresql.Driver");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);

        dataSource = new HikariDataSource(config);

    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection(); // Создание объекта
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

}