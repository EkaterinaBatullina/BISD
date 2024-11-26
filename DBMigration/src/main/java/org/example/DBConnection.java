package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

        private static Connection connection;

//        private static HikariDataSource dataSource;

        private DBConnection() {}

        public static Connection getConnection() throws SQLException {
            if (connection == null || connection.isClosed()) {
                try {
                    connection = DriverManager.getConnection(
                            "jdbc:postgresql://localhost:5432/lab06", "postgres", "password");
                } catch (SQLException e) {
                    throw new SQLException("Ошибка подключения", e);
                }
            }

            return connection;
        }

        public static void releaseConnection() {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

//    public static void initialize() {
//        if (dataSource == null) {
//            // Настройки для HikariCP
//            HikariConfig config = new HikariConfig();
//            config.setJdbcUrl("jdbc:postgresql://localhost:5432/lab06");
//            config.setUsername("postgres");
//            config.setPassword("password");
//            config.setMaximumPoolSize(10);
//
//            dataSource = new HikariDataSource(config);
//        }
//    }
//
//    public static Connection getConnection() throws SQLException {
//        if (dataSource == null) {
//            throw new IllegalStateException("Соединение не установлено");
//        }
//        return dataSource.getConnection();
//    }
//
//    public static void close() {
//        if (dataSource != null) {
//            dataSource.close();
//        }
//    }

    }


