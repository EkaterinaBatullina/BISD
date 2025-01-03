package org.example.mapper;

import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.model.Project;
import org.example.model.User;
import org.example.service.ProjectService;
import org.example.service.UserService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskMapper implements RowMapper<Task> {
    private final UserService userService;
    private final ProjectService projectService;

    public TaskMapper(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }
    @Override
    public Task mapRow(ResultSet resultSet) throws SQLException {
        int taskId = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        TaskStatus status = TaskStatus.valueOf(resultSet.getString("status"));
        int projectId = resultSet.getInt("project_id");
        Project project = projectService.getById(projectId);
        int assignedId = resultSet.getInt("assigned_to");
        User assignedTo = userService.getById(assignedId);
        return new Task(taskId, title, description, status, assignedTo, project);
    }
}
