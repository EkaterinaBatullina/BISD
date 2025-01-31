package org.example.client;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class DBHelper {
    private static final String URL = "jdbc:postgresql://localhost:5432/maze_game"; // URL вашей БД
    private static final String USER = "Ekaterina"; // Имя пользователя
    private static final String PASSWORD = "eka_rina16"; // Пароль пользователя

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Метод для проверки существования пользователя
    public static boolean checkUserCredentials(String username, String password) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs =  stmt.executeQuery();
            rs.next();
            return BCrypt.checkpw(password, rs.getString("password"));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для регистрации нового пользователя
    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2,  BCrypt.hashpw(password, BCrypt.gensalt()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Если добавлена хотя бы одна строка, значит регистрация успешна
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
