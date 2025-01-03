package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status;
    private User assignedTo;
    private Project project;

    public Task(String title, String description, Project project, User assignedTo) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.assignedTo = assignedTo;
        this.status = TaskStatus.TODO;
    }

}
