package org.example.service;

import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dao.ProjectDAO;
import org.example.model.Project;
import org.example.model.User;
import org.example.web.listener.DBListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProjectService {
    private ProjectDAO projectDAO;
    private Connection connection;
    private static final Logger logger = LogManager.getLogger(ProjectService.class);

    public ProjectService() {}

    public void initialize(ServletContext context) {
        this.projectDAO = new ProjectDAO(context);
        this.connection = (Connection) context.getAttribute("dbConnection");
    }
    public boolean save(Project project) {
        return projectDAO.save(project);
    }

    public Project getById(int projectId) {
        return projectDAO.getById(projectId);
    }

    public void delete(int projectId) {
        projectDAO.delete(projectId);
    }

    public List<Project> findByUser(User user) {
        return projectDAO.getByUserId(user.getId());
    }

    public void update(Project project) {
        projectDAO.update(project);
    }

    public void destroy(){
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Error closing connection", e);
        }
    }

}
