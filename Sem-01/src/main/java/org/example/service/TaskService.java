package org.example.service;

import jakarta.servlet.ServletContext;
import org.example.dao.TaskDAO;
import org.example.dao.UserDAO;
import org.example.model.Task;
import org.example.model.TaskStatus;

import java.sql.Connection;
import java.util.List;

public class TaskService {
    private TaskDAO taskDAO;
    public TaskService() {}

    public void initialize(ServletContext context) {
        this.taskDAO = new TaskDAO(context);
    }
    public boolean save(Task task) {
        return taskDAO.save(task);
    }

    public Task getTById(int taskId) {
        return taskDAO.getById(taskId);
    }

    public void updateStatus(int taskId, TaskStatus newStatus) {
        taskDAO.updateStatus(taskId, newStatus);
    }

    public List<Task> getByProject(int projectID) {
        return taskDAO.getByProjectID(projectID);
    }

    public void deleteById(int taskId) {
        taskDAO.deleteById(taskId);
    }

    public void deleteByProjectId(int projectId, Connection connection) {
        taskDAO.deleteByProjectId(projectId, connection);
    }
}

