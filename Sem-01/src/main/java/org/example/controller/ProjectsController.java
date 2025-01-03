package org.example.controller;

import jakarta.servlet.ServletContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.model.User;
import org.example.service.ProjectService;
import org.example.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/projects")
public class ProjectsController extends BaseController {
    private ProjectService projectService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        projectService = (ProjectService) context.getAttribute("projectService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doGet method");
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");
        logger.info("Current user: {}", currentUser.getUsername());
        Map<String, Object> model = createModel(request);
        model.put("currentUser", currentUser);
        try {
            List<Project> projects = projectService.findByUser(currentUser);
            logger.info("Number of projects for user {}: {}", currentUser.getUsername(), projects.size());
            if (projects == null || projects.isEmpty()) {
                logger.info("No projects available to display.");
                model.put("message", "You have no projects.");
            } else {
                model.put("projects", projects);
            }
            renderTemplate("projects.ftl", model, response);
        } catch (Exception e) {
            logger.error("Error retrieving projects for user", e);
            model.put("errorMessage", "An unexpected error occurred while retrieving your projects.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("projects.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        } finally {
            logger.debug("Exiting doGet method");
        }
    }
}
