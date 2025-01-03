package org.example.dao;

import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.service.ProjectService;

import java.sql.*;

public class UserDAO {
    private static final String SQL_CHECK_USERNAME_EXISTENCE = "SELECT COUNT(*) FROM \"user\" WHERE username = ?";
    private static final String SQL_INSERT_USER = "INSERT INTO \"user\" (username, password, email) VALUES (?, ?, ?)";
    private static final String SQL_GET_USER_BY_ID = "SELECT * FROM \"user\" WHERE id = ?";
    private static final String SQL_GET_USER_BY_USERNAME = "SELECT * FROM \"user\" WHERE username = ?";
    private final Connection connection;
    private final UserMapper userMapper;
    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    public UserDAO(ServletContext context) {
        this.connection = (Connection) context.getAttribute("dbConnection");
        this.userMapper = new UserMapper();
    }

    public User getById(int userId) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_GET_USER_BY_ID)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return userMapper.mapRow(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting user by id = " + userId, e);
        }
        return null;
    }

    public User getByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_GET_USER_BY_USERNAME)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return userMapper.mapRow(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting user by username = " + username, e);
        }
        return null;
    }
    public boolean save(User user) {
        Connection connection = null;
        PreparedStatement psCheckUsername = null;
        PreparedStatement psInsertUser = null;
        try {
            connection = this.connection;
            connection.setAutoCommit(false);
            psCheckUsername = connection.prepareStatement(SQL_CHECK_USERNAME_EXISTENCE);
            psCheckUsername.setString(1, user.getUsername());
            try (ResultSet rs = psCheckUsername.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    connection.rollback();
                    return false;
                }
            }
            psInsertUser = connection.prepareStatement(SQL_INSERT_USER);
            psInsertUser.setString(1, user.getUsername());
            psInsertUser.setString(2, user.getPassword());
            psInsertUser.setString(3, user.getEmail());
            int rowsAffected = psInsertUser.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error saving user", e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error rollback", rollbackEx);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                if (psCheckUsername != null) psCheckUsername.close();
                if (psInsertUser != null) psInsertUser.close();
            } catch (SQLException ex) {
                logger.error("Error saving user", ex);
            }
        }
        return false;
    }

}
