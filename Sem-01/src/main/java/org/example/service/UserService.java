package org.example.service;

import jakarta.servlet.ServletContext;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private UserDAO userDAO;

    public UserService() {}

    public void initialize(ServletContext context) {
        this.userDAO = new UserDAO(context);
    }

    public User findByUsername(String username) {
        return userDAO.getByUsername(username);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User getById(int id) {
        return userDAO.getById(id);
    }

    public boolean register(User user) {
        user.setPassword(hashPassword(user.getPassword()));
        return userDAO.save(user);
    }

    public User authenticate(String username, String password) {
        User user = userDAO.getByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}

