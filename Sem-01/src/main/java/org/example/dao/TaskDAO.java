package org.example.dao;

import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.mapper.TaskMapper;
import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.service.ProjectService;
import org.example.service.UserService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    private static final String SQL_CHECK_TASK_EXISTENCE = "SELECT COUNT(*) FROM task WHERE title = ? AND project_id = ?";
    private static final String SQL_INSERT_TASK = "INSERT INTO task (title, description, status, project_id, assigned_to) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_GET_TASKS_BY_PROJECT_ID = "SELECT * FROM task WHERE project_id = ? ORDER BY id ASC";
    private static final String SQL_GET_TASK_BY_ID = "SELECT * FROM task WHERE id = ?";
    private static final String SQL_UPDATE_TASK_STATUS = "UPDATE task SET status = ? WHERE id = ?";
    private static final String SQL_DELETE_TASK_BY_ID = "DELETE FROM task WHERE id = ?";
    private static final String SQL_DELETE_TASK_BY_PROJECT_ID = "DELETE FROM task WHERE project_id = ?";
    private static final String SQL_INSERT_PROJECT_PARTICIPANT = """
                INSERT INTO project_participant (project_id, user_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
            """;
    private final TaskMapper taskMapper;
    private final Connection connection;
    private static final Logger logger = LogManager.getLogger(TaskDAO.class);

    public TaskDAO(ServletContext context) {
        this.connection = (Connection) context.getAttribute("dbConnection");
        UserService userService = (UserService) context.getAttribute("userService");
        ProjectService projectService = (ProjectService) context.getAttribute("projectService");
        this.taskMapper = new TaskMapper(userService, projectService);
    }

    public boolean save(Task task) {
        Connection connection = null;
        PreparedStatement psCheckExistence = null;
        PreparedStatement psInsert = null;
        PreparedStatement psInsertProjectParticipant = null;
        try {
            connection = this.connection;
            connection.setAutoCommit(false);
            psCheckExistence = connection.prepareStatement(SQL_CHECK_TASK_EXISTENCE);
            psCheckExistence.setString(1, task.getTitle());
            psCheckExistence.setInt(2, task.getProject().getId());
            try (ResultSet rsCheck = psCheckExistence.executeQuery()) {
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    connection.rollback();
                    return false;
                }
            }
            psInsert = connection.prepareStatement(SQL_INSERT_TASK);
            psInsert.setString(1, task.getTitle());
            psInsert.setString(2, task.getDescription());
            psInsert.setString(3, task.getStatus().toString());
            psInsert.setInt(4, task.getProject().getId());
            psInsert.setInt(5, task.getAssignedTo().getId());
            int rowsAffected = psInsert.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = psInsert.getGeneratedKeys();
                psInsertProjectParticipant = connection.prepareStatement(SQL_INSERT_PROJECT_PARTICIPANT);
                psInsertProjectParticipant.setInt(1, task.getProject().getId());
                psInsertProjectParticipant.setInt(2, task.getAssignedTo().getId());
                psInsertProjectParticipant.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error saving task ", e);
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

    public List<Task> getByProjectID(int projectID) {
        List<Task> tasks = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SQL_GET_TASKS_BY_PROJECT_ID)) {
            ps.setInt(1, projectID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task task = taskMapper.mapRow(rs);
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            logger.error("Error get By Project ID ", e);
        }
        return tasks;
    }

    public Task getById(int taskId) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_GET_TASK_BY_ID)) {
            ps.setInt(1, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return taskMapper.mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error get By ID ", e);
        }
        return null;
    }

    public void updateStatus(int taskId, TaskStatus newStatus) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_UPDATE_TASK_STATUS)) {
            ps.setString(1, newStatus.toString());
            ps.setInt(2, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error update Status ", e);
        }
    }

    public void deleteById(int taskId) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE_TASK_BY_ID)) {
            ps.setInt(1, taskId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error delete By Id ", e);
        }
    }

    public void deleteByProjectId(int projectId, Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(SQL_DELETE_TASK_BY_PROJECT_ID)) {
            ps.setInt(1, projectId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error delete By Project Id ", e);
        }
    }

}
