package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private int id;
    private String name;
    private String description;
    private User owner;
    private boolean isPrivate;

    public Project(String name, String description, User owner, boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.isPrivate = isPrivate;
    }

    public boolean checkPrivate() {
        return isPrivate;
    }
}

