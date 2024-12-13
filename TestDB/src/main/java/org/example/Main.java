package org.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;

import java.sql.Connection;

import java.sql.SQLException;

    public class Main {
        public static void main(String[] args) {
            // Конфигурация HikariCP для подключения к базе данных
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/db_sem01");
            config.setUsername("Ekaterina");
            config.setPassword("eka_rina16");
            config.setDriverClassName("org.postgresql.Driver");

            config.setMaximumPoolSize(10); // Максимальное количество соединений в пуле
            config.setMinimumIdle(5); // Минимальное количество неактивных соединений в пуле
            config.setIdleTimeout(30000); // Время простоя соединения до его закрытия
            config.setConnectionTimeout(30000); // Время ожидания на подключение

            // Пул соединений
            HikariDataSource dataSource = new HikariDataSource(config);

            try (Connection connection = dataSource.getConnection()) {
                // Вставка нового пользователя
                String sql = "INSERT INTO \"user\" (username, password, email, role) VALUES ('john_doe', 'secure_password_123', 'john_doe@example.com', 'user')";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.executeUpdate(); // Выполнение команды
                    System.out.println("User successfully inserted.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            } finally {
                dataSource.close(); // Закрытие соединений
            }
        }
    }
