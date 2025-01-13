package org.example.service;

import org.example.repository.UserRepository;

import java.sql.SQLException;

public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUserData(String name, String email) {
        try {
            userRepository.saveUser(name, email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
