package org.example.dao;

import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private static final String GET_USER_BY_LOGIN_SQL = "SELECT * FROM users WHERE username = ?";
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User getUserByLogin(String login) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(GET_USER_BY_LOGIN_SQL)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String secretKey = rs.getString("secret_key");
                return new User(username, password, secretKey);
            }
        }
        return null;
    }

}
