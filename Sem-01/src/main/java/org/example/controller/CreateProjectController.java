package org.example.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.model.User;
import org.example.service.ProjectService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/createProject")
public class CreateProjectController extends BaseController {
    private ProjectService projectService;

    @Override
    public void init() throws ServletException {
        super.init();
        projectService = (ProjectService) getServletContext().getAttribute("projectService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doGet method");
        Map<String, Object> model = createModel(request);
        try {
            renderTemplate("createProject.ftl", model, response);
        } catch (TemplateException e) {
            logger.error("Template Exception", e);
        }
        logger.debug("Exiting doGet method");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Entering doPost method");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String isPrivateParam = request.getParameter("isPrivate");
        Map<String, Object> model = createModel(request);
        if (name == null || name.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            logger.warn("Name and description are required.");
            model.put("errorMessage", "Name and Description are required.");
            try {
                renderTemplate("createProject.ftl", model, response);
            } catch (TemplateException e) {
                logger.error("Template Exception", e);
            }
            return;
        }
        User user = (User) request.getSession().getAttribute("user");
        Project project = new Project(name, description, user, Boolean.parseBoolean(isPrivateParam));
        if (projectService.save(project)) {
            logger.info("Project {} created successfully", name);
            response.sendRedirect("projects");
        } else {
            logger.error("Failed to create project {}", name);
            model.put("errorMessage", "Failed to create project");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("createProject.ftl", model, response);
            } catch (TemplateException e) {
                logger.error("Template Exception", e);
            }
        }
        logger.debug("Exiting doPost method");
    }
}





