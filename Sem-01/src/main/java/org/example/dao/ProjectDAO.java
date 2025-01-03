package org.example.dao;

import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.mapper.ProjectMapper;
import org.example.mapper.RowMapper;
import org.example.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDAO {
    private static final String SQL_FIND_BY_ID = "SELECT * FROM project WHERE id = ?";
    private static final String SQL_GET_PROJECTS_FOR_USER = """
        SELECT DISTINCT p.*
        FROM project p
        LEFT JOIN task t ON p.id = t.project_id AND (t.assigned_to = ?)
        WHERE p.owner_id = ? OR t.project_id IS NOT NULL
        ORDER BY id ASC
        """;
    private static final String SQL_INSERT = "INSERT INTO project (name, description, owner_id, is_private) VALUES (?, ?, ?, ?)";
    private static final String SQL_DELETE = "DELETE FROM project WHERE id = ?";
    private static final String SQL_CHECK_EXISTENCE_BY_NAME = "SELECT COUNT(*) FROM project WHERE name = ?";
    private static final String SQL_UPDATE = "UPDATE project SET name = ?, description = ?, is_private = ? WHERE id = ?";
    private static final String SQL_INSERT_PROJECT_PARTICIPANT = """
        INSERT INTO project_participant (project_id, user_id)
        VALUES (?, ?) ON CONFLICT DO NOTHING
    """;
    private final RowMapper<Project> projectMapper;
    private final Connection connection;
    private static final Logger logger = LogManager.getLogger(ProjectDAO.class);

    public ProjectDAO(ServletContext context) {
        this.connection = (Connection) context.getAttribute("dbConnection");
        UserService userService = (UserService) context.getAttribute("userService");
        this.projectMapper = new ProjectMapper(userService);
    }

    public Project getById(int projectId) {
        try (PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, projectId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return projectMapper.mapRow(resultSet);
            }
        } catch (SQLException e) {
            logger.error("Error Project get By Id ", e);
        }
        return null;
    }

    public List<Project> getByUserId(int userId) {
        List<Project> projects = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_GET_PROJECTS_FOR_USER)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                projects.add(projectMapper.mapRow(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error get Project By User Id ", e);
        }
        return projects;
    }

    public boolean save(Project project) {
        Connection connection = null;
        PreparedStatement psCheckExistence = null;
        PreparedStatement psInsert = null;
        PreparedStatement psInsertParticipant = null;
        try {
            connection = this.connection;
            connection.setAutoCommit(false);
            try (PreparedStatement psCheck = connection.prepareStatement(SQL_CHECK_EXISTENCE_BY_NAME)) {
                psCheck.setString(1, project.getName());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        connection.rollback();
                        return false;
                    }
                }
            }
            psInsert = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, project.getName());
            psInsert.setString(2, project.getDescription());
            psInsert.setInt(3, project.getOwner().getId());
            psInsert.setBoolean(4, project.isPrivate());
            int rowsAffected = psInsert.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = psInsert.getGeneratedKeys();
                int projectId = -1;
                if (generatedKeys.next()) {
                    projectId = generatedKeys.getInt(1);
                }
                psInsertParticipant = connection.prepareStatement(SQL_INSERT_PROJECT_PARTICIPANT);
                psInsertParticipant.setInt(1, projectId);
                psInsertParticipant.setInt(2, project.getOwner().getId());
                psInsertParticipant.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error saving project ", e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error rollback ", rollbackEx);
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
                if (psCheckExistence != null) psCheckExistence.close();
                if (psInsert != null) psInsert.close();
            } catch (SQLException ex) {
                logger.error("Error setAutoCommit ", ex);
            }
        }
        return false;
    }


    public void delete(int projectId) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, projectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error delete project ", e);
        }
    }

    public void update(Project project) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setBoolean(3, project.isPrivate());
            ps.setInt(4, project.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error update project ", e);
        }
    }

}
