package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.User;
import org.example.service.UserService;

import freemarker.template.TemplateException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/register")
public class RegisterController extends BaseController {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doGet method");
        Map<String, Object> model = createModel(request);
        try {
            renderTemplate("register.ftl", model, response);
        } catch (TemplateException e) {
            logger.error("Template Exception", e);
        }
        logger.debug("Exiting doGet method");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doPost method");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        logger.info("Received registration data: username={}, email={}", username, email);
        User user = new User(username, password, email);
        try {
            boolean success = userService.register(user);
            logger.debug("Registration success status: {}", success);
            if (success) {
                logger.info("User registered successfully.");
                request.getSession().setAttribute("user", user);
                response.sendRedirect("projects");
            } else {
                logger.warn("User registration failed for username: {}", username);
                Map<String, Object> model = createModel(request);
                model.put("errorMessage", "Username already exists or registration failed");
                renderTemplate("register.ftl", model, response);
            }
        } catch (Exception e) {
            logger.error("Error during registration", e);
            Map<String, Object> model = createModel(request);
            model.put("errorMessage", "Произошла ошибка при регистрации. Попробуйте позже.");
            try {
                renderTemplate("register.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        }
        logger.debug("Exiting doPost method");
    }
}



