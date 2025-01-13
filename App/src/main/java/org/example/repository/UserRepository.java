package org.example.repository;

import java.sql.*;

public class UserRepository {

    private static final String SQL_INSERT = "INSERT INTO users (name, email) VALUES (?, ?)";

    private Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveUser(String name, String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(1, name);
            statement.setString(2, email);
            statement.executeUpdate();
        }
    }
}
