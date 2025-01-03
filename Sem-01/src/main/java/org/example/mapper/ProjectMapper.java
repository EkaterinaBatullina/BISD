package org.example.mapper;

import org.example.model.Project;
import org.example.model.User;
import org.example.service.UserService;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectMapper implements RowMapper<Project> {
    private final UserService userService;
    public ProjectMapper(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Project mapRow(ResultSet resultSet) throws SQLException {
        int projectId = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        int id = resultSet.getInt("owner_id");
        User owner = userService.getById(id);
        boolean isPrivate = resultSet.getBoolean("is_private");
        return new Project(projectId, name, description, owner, isPrivate);
    }

}
