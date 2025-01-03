package org.example.controller;

import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.User;
import org.example.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginController extends BaseController {
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
            renderTemplate("login.ftl", model, response);
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
        Map<String, Object> model = createModel(request);
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                logger.info("User {} logged in successfully.", username);
                response.sendRedirect("projects");
            } else {
                logger.warn("Invalid login attempt for user {}", username);
                model.put("errorMessage", "Неверный логин или пароль");
                renderTemplate("login.ftl", model, response);
            }
        } catch (Exception e) {
            logger.error("Error during authentication", e);
            model.put("errorMessage", "Произошла ошибка при аутентификации. Попробуйте позже.");
            try {
                renderTemplate("login.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        } finally {
            logger.debug("Exiting doPost method");
        }
    }
}


