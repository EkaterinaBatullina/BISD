
package org.example.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Project;
import org.example.model.Task;
import org.example.model.User;
import org.example.service.ProjectService;
import org.example.service.TaskService;
import org.example.service.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/createTask")
public class CreateTaskController extends BaseController {
    private UserService userService;
    private ProjectService projectService;
    private TaskService taskService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        userService = (UserService) context.getAttribute("userService");
        projectService = (ProjectService) context.getAttribute("projectService");
        taskService = (TaskService) context.getAttribute("taskService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Entering doGet method");
        Map<String, Object> model = createModel(request);
        try {
            String projectIdStr = request.getParameter("projectId");
            if (projectIdStr == null || projectIdStr.trim().isEmpty()) {
                logger.error("Project ID parameter is missing");
                model.put("errorMessage", "Project ID is missing");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("createTask.ftl", model, response);
                return;
            }
            int projectId = Integer.parseInt(projectIdStr.trim().replaceAll("\\u00A0+", ""));
            Project project = projectService.getById(projectId);
            if (project == null) {
                logger.error("Project with ID {} not found", projectId);
                model.put("projectId", projectId);
                model.put("projectName", "");
                model.put("errorMessage", "Project not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderTemplate("createTask.ftl", model, response);
                return;
            }
            String projectName = project.getName();
            model.put("projectId", projectId);
            model.put("projectName", projectName);
            response.setContentType("text/html; charset=UTF-8");
            renderTemplate("createTask.ftl", model, response);
        } catch (Exception e) {
            logger.error("Error displaying create task page", e);
            model.put("errorMessage", "An unexpected error occurred while displaying the page. Please try again later.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("createTask.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        } finally {
            logger.debug("Exiting doGet method");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Entering doPost method");
        Map<String, Object> model = createModel(request);
        try {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String assignedToUsername = request.getParameter("assignedTo");
            String projectIdStr = request.getParameter("projectId");
            logger.debug("Received parameters: title={}, description={}, assignedTo={}, projectId={}", title, description, assignedToUsername, projectIdStr);
            if (title == null || title.isEmpty() || description == null || description.isEmpty() || assignedToUsername == null || assignedToUsername.isEmpty() || projectIdStr == null || projectIdStr.isEmpty()) {
                logger.error("Missing required parameters");
                model.put("errorMessage", "Missing required parameters");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("createTask.ftl", model, response);
                return;
            }
            int projectId = Integer.parseInt(projectIdStr.replaceAll("\\u00A0+", ""));
            Project project = projectService.getById(projectId);
            if (project == null) {
                logger.error("Project with ID {} not found", projectId);
                model.put("errorMessage", "Project not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                renderTemplate("createTask.ftl", model, response);
                return;
            }

            String projectName = project.getName();
            model.put("projectId", projectId);
            if (projectName != null) {
                model.put("projectName", projectName);
            } else {
                model.put("projectName", "Неизвестный проект");
            }
            User assignedTo = userService.findByUsername(assignedToUsername);
            if (assignedTo == null) {
                logger.error("User {} not found", assignedToUsername);
                model.put("errorMessage", "Assigned user not found");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                renderTemplate("createTask.ftl", model, response);
                return;
            }
            Task task = new Task(title, description, project, assignedTo);
            if (taskService.save(task)) {
                logger.info("Task {} created successfully", title);
                response.sendRedirect("tasks?projectId=" + projectId);
            } else {
                logger.error("Failed to create task {}", title);
                model.put("errorMessage", "Failed to create task");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                renderTemplate("createTask.ftl", model, response);
            }
        } catch (Exception e) {
            logger.error("Error creating task", e);
            model.put("errorMessage", "Error creating task");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                renderTemplate("createTask.ftl", model, response);
            } catch (TemplateException ex) {
                logger.error("Template Exception", ex);
            }
        } finally {
            logger.debug("Exiting doPost method");
        }
    }
}
